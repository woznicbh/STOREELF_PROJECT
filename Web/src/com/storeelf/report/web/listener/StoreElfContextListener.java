package com.storeelf.report.web.listener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.WanTargetClusterConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.agents.StoreElfRefreshSQLAgent;
import com.storeelf.report.web.agents.StoreElfHZKeepAliveAgent;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.XProperties;

/**
 * Bootstrap all continuous threads here
 *
 **/
public class StoreElfContextListener implements ServletContextListener {
	static final Logger						logger						= Logger.getLogger(StoreElfContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//destroy ALL services
		/***
		 * TODO:
		 * 	to gracefully stop all of the agents clear their working-maps first
		 * 	null the executor service
		 *
		 ***/
		/**
		 * Deleting data in MySQl on server startup
		 * 
		 */
		//Commenting out due to storeelf not starting when this is run
		//SQLModel.deleteAgentIntegInfo();
		
		for(Entry<String, PreparedStatement> entry :Constants.STOREELF_SQL_STMT_MAP.entrySet()){
			try{
				
				PreparedStatement ps = entry.getValue();
				
				//cancel the query if it's still running
				if(ps.isClosed()==false) ps.cancel();
			
			}catch(Exception e){ e.printStackTrace();}
		}
		
		
		
		if(Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE!=null)		Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE.shutdownNow();
		if(Constants.STOREELF_UTILITY_EXECUTOR_SERVICE!=null)			Constants.STOREELF_UTILITY_EXECUTOR_SERVICE.shutdownNow();
		if(Constants.STOREELF_HZ_KEEP_ALIVE_EXECUTOR_SERVICE!=null)	Constants.STOREELF_HZ_KEEP_ALIVE_EXECUTOR_SERVICE.shutdownNow();
		
		HazelcastInstance	hazelcastInstance	= Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME);
		 
		if(hazelcastInstance!=null) hazelcastInstance.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		logger.error("STOREELF.HZ.WAN_REPLICATION:"+getStoreElfProperty("STOREELF.HZ.WAN_REPLICATION"));
		
		Constants.STOREELF_HZ_WAN_REPLICATION	= (getStoreElfProperty("STOREELF.HZ.WAN_REPLICATION").equals("Y")) ? true:false;
		Constants.STOREELF_SERVLET_CONTEXT	= event.getServletContext();
		Constants.STOREELF_HOST				= Constants.STOREELF_SERVLET_CONTEXT.getVirtualServerName();
		String			contextPath			= Constants.STOREELF_SERVLET_CONTEXT.getContextPath();
		
		
		
		logger.debug("Starting STOREELF context:"+contextPath);
		
		Constants.STOREELF_HZ_KEEP_ALIVE_EXECUTOR_SERVICE	= Executors.newSingleThreadScheduledExecutor();		
				
		if(StringUtils.equals(Constants.STOREELF_HOST,"localhost")){
			Constants.STOREELF_HOST = Constants.STOREELF_HOST+":8080";
		}
		
		Hazelcast.getOrCreateHazelcastInstance(getHZConfig());
				
		for(HazelcastInstance instance :Hazelcast.getAllHazelcastInstances()){
			logger.info("HazelcastInstance started: "+instance.getName());
		}
		System.setProperty( "hazelcast.logging.type", "error" );			
		
		//start the Hazelcast keep alive thread, this isn't out-of-box functionality; This is just my way of keeping the instance 
		//from idling and being removed from the cluster
		Constants.STOREELF_HZ_KEEP_ALIVE_EXECUTOR_SERVICE.scheduleAtFixedRate(new StoreElfHZKeepAliveAgent(), 0, 30, TimeUnit.SECONDS);
		
		
		if(contextPath.startsWith("/Dashboard")){
			Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE		= Executors.newFixedThreadPool(Constants.STOREELF_DASHBOARD_SQL_THREAD_MAX);
			
			//initialize SQL refresh agents
			logger.debug("initialize SQL refresh agents for :"+contextPath);
			
			for(int i=0;i<getStoreElfDashboardThreadMax();i++){
				logger.debug("initialize SQL refresh agents #:"+(i+1));								 
				Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE.execute(new StoreElfRefreshSQLAgent(  (i+1)+""));
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		}else if(contextPath.startsWith("/Utility")){
			Constants.STOREELF_UTILITY_EXECUTOR_SERVICE		= Executors.newSingleThreadExecutor();
			logger.debug("initialize SQL refresh agents for :"+contextPath);
			for(int i=0;i<1;i++){
				logger.debug("initialize SQL refresh agents #:"+(i+1));
				Constants.STOREELF_UTILITY_EXECUTOR_SERVICE.execute( new StoreElfRefreshSQLAgent(  (i+1)+"", "RP_"));
			}
		}
		
		//set global security flag
		Constants.STOREELF_SECURITY_ENABLED = isAuthorizationEnabled();
	}
	
	public boolean isAuthorizationEnabled(){
		try{
			XProperties systemProperties = null;		
						systemProperties = ReportActivator.getXProperties();		
			return (systemProperties.getProperty("STOREELF.IS_AUTHORIZATION_ENABLED").equalsIgnoreCase("Y") || systemProperties.getProperty("STOREELF.IS_AUTHORIZATION_ENABLED") == "y");
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public Config getHZConfig(){
		try{
			String	instance_suffix	= "";
			String	ip_address		= Inet4Address.getLocalHost().getHostAddress();
			String	contextPath		= Constants.STOREELF_SERVLET_CONTEXT.getContextPath();
			
			//label the MF and GT datacenter IP prefixes
			boolean isMFDatacenter = ip_address.startsWith("10.1");
			boolean isGTDatacenter = ip_address.startsWith("10.7");
			
			GroupConfig	groupConfig = null;
			
			//create a group for EACH datacenter
			if(isMFDatacenter){
				groupConfig = new GroupConfig("mf-storeelf-session-group", "MF_STOREELF_SESSION_GROUP_PASSWORD");
				instance_suffix = "MF";
			}else if(isGTDatacenter){
				groupConfig = new GroupConfig("gt-storeelf-session-group", "GT_STOREELF_SESSION_GROUP_PASSWORD");
				instance_suffix = "GT";
			}else{
				groupConfig = new GroupConfig("local-storeelf-session-group", "LOCAL_STOREELF_SESSION_GROUP_PASSWORD");
				instance_suffix = "LOCAL";
			}
			
			Constants.STOREELF_HAZELCAST_INSTANCE_NAME = "STOREELF_HAZELCAST_INSTANCE_"+instance_suffix+"_"+contextPath.substring(1).toUpperCase();
			
			Config	config = new Config(Constants.STOREELF_HAZELCAST_INSTANCE_NAME);
			 
			switch (contextPath) {
				case "/Dashboard":		config.getNetworkConfig().setPort( 5701 ).setPortAutoIncrement( true );	logger.info("Starting HZ Dashboard		on 5701");	break;
				case "/Utility":		config.getNetworkConfig().setPort( 5703 ).setPortAutoIncrement( true );	logger.info("Starting HZ Utility		on 5702");	break;
				case "/Security":		config.getNetworkConfig().setPort( 5705 ).setPortAutoIncrement( true );	logger.info("Starting HZ Security		on 5703");	break;
				case "/Help":			config.getNetworkConfig().setPort( 5706 ).setPortAutoIncrement( true );	logger.info("Starting HZ Help			on 5704");	break;
			default:					config.getNetworkConfig().setPortAutoIncrement(true);												break;
			}
			JoinConfig	join = config.getNetworkConfig().getJoin();
			NetworkConfig networkConfig = config.getNetworkConfig();
			networkConfig.setReuseAddress( true );
						join.getMulticastConfig().setEnabled( true );
			
			//config.setNetworkConfig(network);
			
			WanReplicationRef wanReplicationRef  = new WanReplicationRef("storeelf-session-cluster", "com.hazelcast.map.merge.PassThroughMergePolicy", null, true);
			
			MapConfig	mapConfig = new MapConfig();
						mapConfig.setName( "STOREELF_SESSIONS" );
						mapConfig.setBackupCount( 2 );
						mapConfig.getMaxSizeConfig().setSize( 10000 );
						mapConfig.getMaxSizeConfig().setMaxSizePolicy(MaxSizePolicy.PER_NODE);
						mapConfig.setTimeToLiveSeconds( 0 );
						mapConfig.setWanReplicationRef(wanReplicationRef);
						
						
			MapConfig	mapConfig_auth = new MapConfig();
						mapConfig_auth.setName( "STOREELF_AUTH_INFO" );
						mapConfig_auth.setBackupCount( 5 );
						mapConfig_auth.getMaxSizeConfig().setSize( 10000 );
						mapConfig_auth.getMaxSizeConfig().setMaxSizePolicy(MaxSizePolicy.PER_NODE);
						mapConfig_auth.setTimeToLiveSeconds( 0 );
						mapConfig_auth.setWanReplicationRef(wanReplicationRef);
						
			EntryListenerConfig			listenerConfig = new EntryListenerConfig();
										//listenerConfig.setClassName("com.storeelf.report.web.listener.StoreElfModelMapListener");
										listenerConfig.setImplementation(Constants.STOREELF_MODELMAP_LISTENER);
						
			List<EntryListenerConfig>	listenerConfigs = new ArrayList<EntryListenerConfig>();
										listenerConfigs.add(listenerConfig);

			MapConfig	mapConfig_modelmap = new MapConfig();
						mapConfig_modelmap.setName( "STOREELF_SQL_MODELMAP" );
						mapConfig_modelmap.setBackupCount( 2 );
						mapConfig_modelmap.getMaxSizeConfig().setSize( 500 );
						mapConfig_modelmap.getMaxSizeConfig().setMaxSizePolicy(MaxSizePolicy.PER_NODE);
						mapConfig_modelmap.setTimeToLiveSeconds( 0 );
						mapConfig_modelmap.setWanReplicationRef(wanReplicationRef);
						mapConfig_modelmap.setEntryListenerConfigs(listenerConfigs);
						
			WanReplicationConfig	wanReplicationConfig = new WanReplicationConfig();
			wanReplicationConfig.setName("storeelf-session-cluster");
			
			WanTargetClusterConfig	wanTargetClusterConfig = new WanTargetClusterConfig();
			WanTargetClusterConfig	mf_wanTargetClusterConfig = null; //new WanTargetClusterConfig();
			WanTargetClusterConfig	gt_wanTargetClusterConfig = null; //new WanTargetClusterConfig();
						
						
			if(isMFDatacenter){
				//if(ping("10.7.32.22",2000)){
					wanTargetClusterConfig.setGroupName("gt-storeelf-session-group");
					wanTargetClusterConfig.setGroupPassword("GT_STOREELF_SESSION_GROUP_PASSWORD");
					wanTargetClusterConfig.setReplicationImpl("com.hazelcast.wan.impl.WanNoDelayReplication");
					wanTargetClusterConfig.setEndpoints(new LinkedList<String>());
					wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5701");
					wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5702");
					wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5703");
					wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5704");
					wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5705");
			}else if(isGTDatacenter){
				//if(ping("10.1.31.81",2000)){	
					wanTargetClusterConfig.setGroupName("mf-storeelf-session-group");
					wanTargetClusterConfig.setGroupPassword("MF_STOREELF_SESSION_GROUP_PASSWORD");
					wanTargetClusterConfig.setReplicationImpl("com.hazelcast.wan.impl.WanNoDelayReplication");
					wanTargetClusterConfig.setEndpoints(new LinkedList<String>());
					wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5701");
					wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5702");
					wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5703");
					wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5704");
					wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5705");
					//wanReplicationConfig.addTargetClusterConfig(wanTargetClusterConfig);
//				}else{
//					logger.error("could not reach GT host 10.7.32.22");
//				}
			}else{
				//int pingCount = 0;
				//if(ping("10.7.32.22",2000)){
					gt_wanTargetClusterConfig = new WanTargetClusterConfig();
					gt_wanTargetClusterConfig.setGroupName("gt-storeelf-session-group");
					gt_wanTargetClusterConfig.setGroupPassword("GT_STOREELF_SESSION_GROUP_PASSWORD");
					gt_wanTargetClusterConfig.setReplicationImpl("com.hazelcast.wan.impl.WanNoDelayReplication");
					gt_wanTargetClusterConfig.setEndpoints(new LinkedList<String>());
					gt_wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5701");
					gt_wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5702");
					gt_wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5703");
					gt_wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5704");
					gt_wanTargetClusterConfig.getEndpoints().add("10.7.32.22:5705");
					//wanReplicationConfig.addTargetClusterConfig(gt_wanTargetClusterConfig);
//				}else{
//					logger.error("could not reach GT host 10.7.32.22");
//				}
				
				//if(ping("10.1.31.81",2000)){
					mf_wanTargetClusterConfig = new WanTargetClusterConfig();
					mf_wanTargetClusterConfig.setGroupName("mf-storeelf-session-group");
					mf_wanTargetClusterConfig.setGroupPassword("MF_STOREELF_SESSION_GROUP_PASSWORD");
					mf_wanTargetClusterConfig.setReplicationImpl("com.hazelcast.wan.impl.WanNoDelayReplication");
					mf_wanTargetClusterConfig.setEndpoints(new LinkedList<String>());
					mf_wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5701");
					mf_wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5702");
					mf_wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5703");
					mf_wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5704");
					mf_wanTargetClusterConfig.getEndpoints().add("10.1.31.81:5705");
					//wanReplicationConfig.addTargetClusterConfig(mf_wanTargetClusterConfig);
					
//				}else{
//					logger.error("could not reach MF host 10.1.31.81");
//				}
				
			} 
			
			if(Constants.STOREELF_HZ_WAN_REPLICATION==true){									
				if(isMFDatacenter || isGTDatacenter){
					wanReplicationConfig.addTargetClusterConfig(wanTargetClusterConfig);
				}else{
					wanReplicationConfig.addTargetClusterConfig(gt_wanTargetClusterConfig);
					wanReplicationConfig.addTargetClusterConfig(mf_wanTargetClusterConfig);
				}
				config.addWanReplicationConfig(wanReplicationConfig);
			}
			
			config.setGroupConfig(groupConfig);
			config.addMapConfig(mapConfig);
			config.addMapConfig(mapConfig_auth);
			config.addMapConfig(mapConfig_modelmap);
			
			config.setProperty( "hazelcast.logging.type", "log4j" );
			return config;
		}catch(Exception e){
			logger.error("could not instantiate HZ config", e);
		}
		return null;
	}
	
	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 */
	public static boolean ping(String url, int timeout) {
	    // Otherwise an exception may be thrown on invalid SSL certificates:
	    url = url.replaceFirst("^https", "http");

	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    } catch (IOException exception) {
	        return false;
	    }
	}
	
	public int getStoreElfDashboardThreadMax(){
		String STOREELF_DASHBOARD_SQL_THREAD_MAX = getStoreElfProperty("STOREELF.STOREELF_DASHBOARD_SQL_THREAD_MAX");
		if (STOREELF_DASHBOARD_SQL_THREAD_MAX==null){ return Constants.STOREELF_DASHBOARD_SQL_THREAD_MAX;}
		else{ return Integer.parseInt(STOREELF_DASHBOARD_SQL_THREAD_MAX); }
	}
	
	public String getStoreElfProperty(String propKey){
		XProperties systemProperties = null; 
		try{
			systemProperties = ReportActivator.getXProperties();
			return systemProperties.getProperty(propKey);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
