package com.storeelf.report.web.agents;

import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.SQLUtils;

//TODO this class is dirty, clean it up
public class StoreElfRefreshSQLAgent extends Thread {
	final Logger							logger							= Logger.getLogger(StoreElfRefreshSQLAgent.class);
	Date									currentDate						= new Date();
	String									THREAD_ID						= null;
	String									SQLID_PREFIX_FILTER				= null;
	//SQLModel 								model							= null;
	int										failureCount					= 0;
	Long									datediff						= 0l;
	String									THREAD_NAME						= "";
	boolean forceSQLRefresh;
	Thread sql_thread;
	String name 															= "";
	String SQLID															= "";
	String SQLID_STATUS														= "";
	SQLModel 	model														= null;
	boolean SQL_ID_MATCHES 													= false;
	boolean useFilterflag;
	
	public StoreElfRefreshSQLAgent(String thread_id) {
		THREAD_ID = thread_id;
		SQLID_PREFIX_FILTER = null;
		THREAD_NAME = THREAD_ID+"-STOREELF_REFRESH_SQL_AGENT[]::UNUSED THREAD ...";
	}

	public StoreElfRefreshSQLAgent(String thread_id, String sqlid_prefix_filter) {
		THREAD_ID = thread_id;
		SQLID_PREFIX_FILTER = sqlid_prefix_filter;
		THREAD_NAME = THREAD_ID+"-STOREELF_REFRESH_SQL_AGENT["+sqlid_prefix_filter+"]::UNUSED THREAD ...";
	}
	
	public void setThreadStatus(String newStatus){
		
		if(THREAD_NAME.contains("::")){
			name	= THREAD_NAME.split("::")[0];
			THREAD_NAME = name+"::"+newStatus;
		}
		Thread.currentThread().setName(THREAD_NAME);
	}

	@Override
	public void run() {
		try{
			useFilterflag = (SQLID_PREFIX_FILTER != null);
			//set the current thread's name to avoid confusion later
			setThreadStatus("UNUSED THREAD ...");

			//turn this thread into a message consumer
			while(
					(Constants.STOREELF_SQL_REFRESH_JOBS.containsValue("SCHEDULED") ||
					Constants.STOREELF_SQL_REFRESH_JOBS.containsValue("WAITING") ||
					Constants.STOREELF_SQL_REFRESH_JOBS.containsValue("DONE") ||
					Constants.STOREELF_SQL_REFRESH_JOBS.containsValue("FORCE") ||
					Constants.STOREELF_SQL_REFRESH_JOBS.containsValue("ERROR") ||
					Constants.STOREELF_SQL_REFRESH_JOBS.size()==0)
					&& 
					currentThread().isInterrupted()==false
				){
				//if the job concurrent hashmap is null, create a new one
				if(Constants.STOREELF_SQL_REFRESH_JOBS == null){
					Constants.STOREELF_SQL_REFRESH_JOBS					= new ConcurrentHashMap<String, String>();
					logger.debug("STOREELF_SQL_REFRESH_JOBS reset");
				}
				
				SQL_ID_MATCHES = false;

				//loop through each refresh job and execute
				for(Entry<String, String> job: Constants.STOREELF_SQL_REFRESH_JOBS.entrySet()){
					SQLID			= job.getKey();
					SQLID_STATUS	= job.getValue();

					//ONLY refresh if the job is marked as Scheduled, Waiting or Done !
					if(useFilterflag){
						SQL_ID_MATCHES = (SQLID.startsWith("SE_") || SQLID.startsWith(SQLID_PREFIX_FILTER)) && (SQLID_STATUS.equals("SCHEDULED") || SQLID_STATUS.equals("WAITING") || SQLID_STATUS.equals("DONE") || SQLID_STATUS.equals("FORCE") || SQLID_STATUS.equals("ERROR") ); 
					}else{
						SQL_ID_MATCHES = (SQLID_STATUS.equals("SCHEDULED") || SQLID_STATUS.equals("WAITING") || SQLID_STATUS.equals("DONE") || SQLID_STATUS.equals("FORCE") ||SQLID_STATUS.equals("ERROR") ); 
					}
					
					//apply whitelist logic
					//if(SQL_ID_MATCHES){
					//	SQL_ID_MATCHES = ReportActivator.ProcessSQLAgentWhitelistFilter(SQLID);//(Constants.STOREELF_SQL_AGENT_WHITELIST.size()>0 && Constants.STOREELF_SQL_AGENT_WHITELIST.contains(SQLID));
					//}
					
					if(SQL_ID_MATCHES){
						//handle any exception and log into map
						try{
							model	= SQLUtils.getModelObject(SQLID);						

							
							//ok, this is an aggressive approach but will allow us to force the refresh of a SQL query
							//TODO wrap the original implementation into permissions handler to allow groups to force refresh
							if((isRefreshRequired(model) || SQLID_STATUS.equals("FORCE")) && SQLUtils.getThreadStatus(SQLID) == null){
								
								triggerRefresh(SQLID, model);
							}else{
								//if no refresh required, mark job as Waiting; the next available thread will pick it up
								//Constants.STOREELF_SQL_REFRESH_JOBS.put(SQLID,"WAITING");
							}
						}catch(Exception e){
							if(failureCount<1){
								//DO NOT write to logger unless absolutely required
								logger.error("Thread with SQL ID {"+SQLID+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
								setThreadStatus("ERROR ...");
								Constants.STOREELF_SQL_JOBS_ERRORLOG.put(SQLID, ExceptionUtils.getStackTrace(e));
								failureCount++;
							}
						}
					}
				}
				//sleep for 5 seconds ALWAYS do this or you'll run out of heap faster than you can say it.
				//wait();
				Thread.sleep(Constants.STOREELF_MAX_SLEEP_MILLISECONDS);
			}
		}catch (Exception e)				{
			logger.trace("error processing StoreElfRefreshSQLJob : Exception", e);
			//Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE.execute(new StoreElfRefreshSQLAgent(THREAD_ID));
			return;
		}
		//}catch (Exception e)				{}
	}
//org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(Throwable)
	protected boolean triggerRefresh(String SQLID, SQLModel model){		
		
		forceSQLRefresh = StringUtils.equals(Constants.STOREELF_SQL_REFRESH_JOBS.get(SQLID), "FORCE");
		
		try {
			sql_thread = SQLUtils.getThreadStatus(SQLID);
			
			if(sql_thread == null){
				setThreadStatus("RUNNING "+SQLID);
				Constants.STOREELF_SQL_REFRESH_JOBS.put(SQLID,"RUNNING");
			
				//refresh the data here, WOO!! ^_^
				if(forceSQLRefresh){
					model.refreshResultSet(true);
				}else{
					model.refreshResultSet();
				}
				setThreadStatus("DONE ...feed me");
				//Constants.STOREELF_SQL_REFRESH_JOBS.put(SQLID, "DONE");
			}else{
				if(StringUtils.equals(Constants.STOREELF_SQL_REFRESH_JOBS.get(SQLID), "RUNNING")){
					//it's marked as RUNNING, good. Leave it that way 
				}else{
					//thread exists so we need to mark it as such
					//Constants.STOREELF_SQL_REFRESH_JOBS.put(SQLID,"RUNNING");
				}
				//############################################################################################
				//TODO: interrupt existing thread and create a new one EXPERIMENTAL !!!!!!!!!!!!!!!!
				//	if(forceSQLRefresh){
				//		try{
				//			sql_thread.interrupt();
				//			//interrupt existing thread and create a new one
				//			Thread	storeelfRefreshSQLAgent = new StoreElfRefreshSQLAgent(  "", "SRV_");
				//					storeelfRefreshSQLAgent.setDaemon(true);				
				//			Constants.STOREELF_ENVIRONMENTS_EXECUTOR_SERVICE.execute(storeelfRefreshSQLAgent);
				//		}
				//		catch(Exception e){logger.trace("Existing Thread with SQL ID {"+SQLID+"} Could NOT be interrupted ");}
				//		
				//	}
				//############################################################################################
				
				//It's obviously already running so let's leave the SQLID status at 'RUNNING'
				//DO NOT mark it as 'DONE' yet
				logger.trace("Thread with SQL ID {"+SQLID+"} is already running: StoreElfRefreshSQLAgent aborted");
				return false;
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace(); 
			Constants.STOREELF_SQL_REFRESH_JOBS.put(SQLID, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(SQLID, ExceptionUtils.getStackTrace(e));
			setThreadStatus("ERROR ...check me");
			logger.error("Thread with SQL ID {"+SQLID+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		}
		return false;
	}

	protected boolean isRefreshRequired(SQLModel model){
		try{
			if(model == null) return false;
			if(model.getLastexecutetimestamp() == null) return true;

			datediff = (new Date()).getTime() - model.getLastexecutetimestamp().getTime();
			return (model.getLastexecutetimestamp() == null || datediff > Constants.SQL_TIME_MAP.get(model.getId()));
		}catch (Exception e)				{
			e.printStackTrace();
			logger.debug("error processing StoreElfRefreshSQLJob.isRefreshRequired() : Exception", e);
			return false;
		}
	}
}
