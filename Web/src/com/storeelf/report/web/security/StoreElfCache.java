package com.storeelf.report.web.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.WanTargetClusterConfig;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.listener.StoreElfSessionListener;

/**
 * @author tkmagh4
 *
 * StoreElf users, roles and permissions Cache
 * 
 */
public class StoreElfCache implements Cache{
	private static final Logger logger = LoggerFactory.getLogger(StoreElfCache.class);
	//private String name = "";
	private String				name	= "";
	private  com.google.common.cache.Cache<Object,Object>				map		= null;
	private Connection			con		= null;
	private ArrayList<String>	externalHosts = null;	
	private int dbConnectionMaxRetryCount		= 10;
	private int dbConnectionRetryAttemptCount	= 0;
	private HazelcastInstance	hazelcastInstance			= Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME);
	private IMap<Object, Object> STOREELF_SESSIONS			= hazelcastInstance.getMap("STOREELF_SESSIONS");
	private IMap<Object, Object> STOREELF_SESSIONS_AUTH_INFO	= hazelcastInstance.getMap("STOREELF_SESSIONS_AUTH_INFO");
 
	public StoreElfCache() {
		this.map = CacheBuilder.newBuilder()
			    .maximumSize(Constants.STOREELF_MAX_CONCURRENT_SESSIONS_CACHED)
			    .expireAfterWrite(24, TimeUnit.HOURS)
			    .build();
		this.name = "SHIRO_STOREELF_SESSION_MAP";
		
		//create session and session auth object maps if they don't already exist 
		if(STOREELF_SESSIONS==null){
        	STOREELF_SESSIONS = hazelcastInstance.getMap( "STOREELF_SESSIONS" );
        	STOREELF_SESSIONS.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
        }
		if(STOREELF_SESSIONS_AUTH_INFO==null){
			STOREELF_SESSIONS_AUTH_INFO = hazelcastInstance.getMap( "STOREELF_AUTH_INFO" );
			STOREELF_SESSIONS_AUTH_INFO.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
		}
			
		logger.debug("StoreElfCache Initializing without name, generic map");
		try {
			this.setConnection(ReportActivator.getInstance().getConnection(Constants.STOREELF_RO));
		}
			catch (FileNotFoundException e) {e.printStackTrace();}
			catch (ClassNotFoundException e) {e.printStackTrace();}
			catch (SQLException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();
		}
	}
	
	public StoreElfCache(String pName) {
		logger.debug("StoreElfCache Initializing name '"+pName+"', generic map");
		if (pName == null) {
            throw new IllegalArgumentException("Cache name cannot be null.");
        }
		this.name = pName;
		
		//create session and session auth object maps if they don't already exist
		if(STOREELF_SESSIONS==null){
        	STOREELF_SESSIONS = hazelcastInstance.getMap( "STOREELF_SESSIONS" );
        	STOREELF_SESSIONS.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
        }
		if(STOREELF_SESSIONS_AUTH_INFO==null){
			STOREELF_SESSIONS_AUTH_INFO = hazelcastInstance.getMap( "STOREELF_AUTH_INFO" );
			STOREELF_SESSIONS_AUTH_INFO.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
		}
	}

	public StoreElfCache(String pName, com.google.common.cache.Cache<Object,Object> pBackingMap) {
		logger.debug("StoreElfCache Initializing name '"+pName+"', backing map supplied");
		if (pName == null)			throw new IllegalArgumentException("Cache name cannot be null.");
        if (pBackingMap == null)	throw new IllegalArgumentException("Backing map cannot be null.");
        
      //create session and session auth object maps if they don't already exist
    	if(STOREELF_SESSIONS==null){
        	STOREELF_SESSIONS = hazelcastInstance.getMap( "STOREELF_SESSIONS" );
        	STOREELF_SESSIONS.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
        }
		if(STOREELF_SESSIONS_AUTH_INFO==null){
			STOREELF_SESSIONS_AUTH_INFO = hazelcastInstance.getMap( "STOREELF_AUTH_INFO" );
			STOREELF_SESSIONS_AUTH_INFO.addEntryListener(Constants.STOREELF_SESSION_LISTENER, true);
		}
		
         this.name = pName;
         this.map = pBackingMap;
         
	}

	@Override
	public void clear() throws CacheException {
		STOREELF_SESSIONS.clear();
		map.cleanUp();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#get(java.lang.Object)
	 *  
	 *  Cycle through all available session storage mediums. Check Hazelcast distributed memory map, then local, then file
	 */
	@Override
	public Object get(Object paramK) throws CacheException {
		// TODO Auto-generated method stub
		Object cacheObject = null;

		if(StringUtils.equals(getCacheName(), "SHIRO_STOREELF_SESSION_MAP")){
			if(cacheObject==null){
				logger.debug("StoreElfCache: getting HZ session:"+paramK);
				cacheObject = getSessionFromHZ(String.valueOf(paramK));
			}if(cacheObject==null){
				logger.debug("StoreElfCache: getting LOCAL session:"+paramK);
				cacheObject = map.getIfPresent(paramK);
			}/*if(cacheObject==null){
				logger.debug("StoreElfCache: getting DB session:"+paramK);
				cacheObject = getSessionFromDB(String.valueOf(paramK));
			}*/if(cacheObject==null){
				logger.debug("StoreElfCache: getting File session:"+paramK);
				cacheObject = getSessionFromFile(paramK);
			}
			/* TODO fix connection error
			 * if(cacheObject==null){
				logger.debug("StoreElfCache: getting web service session:"+paramK);
				cacheObject = getSessionFromWebService(String.valueOf(paramK));
			}*/
		}else{
			cacheObject = map.getIfPresent(paramK);
			if(cacheObject==null){
				logger.debug("StoreElfCache: getting HZ session:"+paramK);
				cacheObject = getSessionFromHZ(String.valueOf(paramK));
			}if(cacheObject==null){
				logger.debug("StoreElfCache: getting LOCAL session:"+paramK);
				cacheObject = map.getIfPresent(paramK);
			}
		}

		logger.debug("StoreElfCache:"+getCacheName()+":get("+paramK+")::"+(cacheObject!=null));
		return cacheObject;
	}
	
	public Object getSessionFromHZ(Object paramK){
		try{
			//STOREELF_HAZELCAST_INSTANCE
			return	STOREELF_SESSIONS.get(paramK); 
		}catch(Exception e){
			logger.debug("session not found in HZ");
			return null;
		}
	}
	
	public Object getSessionFromFile(Object paramK){
		byte[] sessionFile;
		try {
			sessionFile = FileUtils.readFileToByteArray(new File(Constants.STOREELF_SERVLET_CONTEXT.getRealPath("/")+"/../Logistics/Sessions/"+paramK));
			Object output_session_object = SerializationUtils.deserialize(sessionFile);
			if(output_session_object instanceof SimpleSession) return output_session_object;
		}catch (IOException e) {logger.trace("session file not found");}
		catch (Exception e) {logger.trace("session file not found");}
		return null;		
	}
	
	@Override
	public Set<Object> keys() {
		return map.asMap().keySet();
	}

	@Override
	public Object put(Object paramK, Object paramV) throws CacheException {
		
		//Confirm the object type before inserting into map
		if(paramV instanceof SimpleAuthorizationInfo){
			logger.debug("0) StoreElfCache:"+name+":put("+paramK+", "+paramV+")");

			if(logger.isTraceEnabled()){
				for(String r :((SimpleAuthorizationInfo)paramV).getRoles()) logger.trace("StoreElfCache: roleName = "+r);
			}
			
			
			try{
				STOREELF_SESSIONS_AUTH_INFO.put(paramK, (SimpleAuthorizationInfo) paramV);				
			}catch(Exception e){
				logger.debug("StoreElfCache: Putting auth info in HZ failed, storing in local cache instead");
				putLocal(paramK, paramV);
			}
			return paramV;
		}else if(paramV instanceof org.apache.shiro.session.mgt.SimpleSession){
			String session_id = ((SimpleSession)paramV).getId().toString();
			try{
				STOREELF_SESSIONS.put(session_id, ObjectUtils.cloneIfPossible(((SimpleSession) paramV)));				
			}catch(Exception e){
				logger.debug("StoreElfCache: Putting session in HZ failed, storing in local cache instead");
				putLocal(paramK, paramV);
			}
			//Constants.STOREELF_SESSION_REFRESH_JOBS.put(session_id+"__PUT", (SimpleSession) paramV);
		}
		return paramV;
	}
	
	public Object putLocal(Object paramK, Object paramV){
		if(map==null){
			map = CacheBuilder.newBuilder()
			.maximumSize(Constants.STOREELF_MAX_CONCURRENT_SESSIONS_CACHED)
			.expireAfterWrite(24, TimeUnit.HOURS)
		    .build();
		}		
		return  paramV;
	}
	
	private Object getSessionFromDB(String session_id) throws SQLException {
		Connection conn = null;
		Statement stmnt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			stmnt = conn.createStatement();
			result = stmnt.executeQuery(
					"SELECT session, MAX(user_session_key) FROM storeelf.se_user_session WHERE session_id = '"
							+ session_id + "' AND sysdate() < expirets");
			Object output_session_object = null;

			while (result.next())
				output_session_object = SerializationUtils.deserialize((byte[]) result.getObject("SESSION"));
			return output_session_object;
		} catch (SQLException e) {
			logger.debug("error processing authentication : SQLException", e);
		} catch (Exception e) {
			logger.debug("error processing authentication : Exception", e);
		} finally{
			if(stmnt!=null){stmnt.close();}
			if(result!=null){result.close();}
		}
		// finally {try {if(conn!=null){conn.close();}} catch (Exception e2) {}}
		return null;
	}

	@Override
	public Object remove(Object paramK) throws CacheException {
		Object paramV = map.getIfPresent(paramK);
		
		//Confirm the object type before removing from map
		if(paramV instanceof org.apache.shiro.session.mgt.SimpleSession){
			String session_id = ((SimpleSession)paramV).getId().toString();
			STOREELF_SESSIONS.remove(session_id); 
		}else if(paramV instanceof SimpleAuthorizationInfo){
			String session_id = ((SimpleSession)paramV).getId().toString();
			STOREELF_SESSIONS_AUTH_INFO.remove(session_id);
		}
		
		if(map!=null){map.invalidate(paramK);}
		return paramK;
	}

	@Override
	public int size() {
		try{
			if(STOREELF_SESSIONS!=null) return STOREELF_SESSIONS.size();
		}catch(Exception e){
			if(map!=null) return NumberUtils.toInt(map.size()+"");
		}
		return -1;
	}

	@Override
	public Collection values() {
		if(STOREELF_SESSIONS!=null){
			return STOREELF_SESSIONS.values();
		}
		return map.asMap().values();
	}

	public String getCacheName(){
		return this.name;
	}

	public Connection getConnection() {
		try {
			if((con==null || con.isClosed()) && dbConnectionRetryAttemptCount<dbConnectionMaxRetryCount){
				logger.debug("previous connection stale, creating new one");
				this.setConnection(ReportActivator.getInstance().getConnection(Constants.STOREELF_RO));
			}else{
				logger.debug("previous connection stale, reached max retry attempts; Going to the timeout corner, I've been a naughty app");
				Thread.sleep(250);
				dbConnectionRetryAttemptCount=0;
			}
		}	
		catch (SQLException e) {			logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;} 
		catch (FileNotFoundException e) {	logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;}
		catch (ClassNotFoundException e) {	logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;}
		catch (IOException e) {				logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;} 
		catch (InterruptedException e) {	logger.debug("error interrupting thread, now's a good time to PANIC!! ... or ... just reload servlet in tomcat ^_^"); dbConnectionRetryAttemptCount=0;}
		
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}

}
