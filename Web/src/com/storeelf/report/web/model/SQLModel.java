package com.storeelf.report.web.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.hazelcast.core.Hazelcast;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.StringUtils;
import com.storeelf.util.exception.StoreElfException;

public class SQLModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger											logger					= Logger.getLogger(SQLModel.class);
	private Date												lastexecutetimestamp	= null;
	private Date												lastresulttimestamp		= null;
	private ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap				= new ConcurrentHashMap<Integer, HashMap<String, Object>>() ; 
	private String												env						= null;
	private String												jsonResult				= null;
	private String												tableName				= null;
	private String												id						= null;
	private transient Connection								con						= null;
	
	
	public String getJsonResult() {
		return jsonResult;
	}

	/*public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
		
		String tableName = this.getTableName();
		Calendar now = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("mm");
		String nowTime = df.format(now.getTime());
		
		if((nowTime.equals("00"))||(nowTime.equals("15"))||(nowTime.equals("30"))||(nowTime.equals("45")))
		writeToMySQL(jsonResult, tableName);
	}*/
	
	public void setJsonResult(String jsonResult) throws Exception {
		this.jsonResult = jsonResult;

		String tableName = this.getTableName();
		Calendar now = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("mm");
		String nowTime = df.format(now.getTime());

		if((nowTime.equals("00"))||(nowTime.equals("15"))||(nowTime.equals("30"))||(nowTime.equals("45"))){
			if(tableName.equals("cancelglance")&&(!jsonResult.equals(Constants.INVALID_CANCELGLANCE)))
				SQLUtils.writeToMySQL(jsonResult, tableName);
			else if(tableName.equals("top_10_cancelled_SKU")&&(!jsonResult.equals(Constants.INVALID_TOP_10_CANCELLED_SKU)))
				SQLUtils.writeToMySQL(jsonResult, tableName);
			else if(tableName.equals("14_day_cancel_stats")&&(!jsonResult.equals(Constants.INVALID_14_DAY_CANCEL_STATS)))
				SQLUtils.writeToMySQL(jsonResult, tableName);
			else if(tableName.equals("5_day_auto_cancel_stats")&&(!jsonResult.equals(Constants.INVALID_5_DAY_AUTO_CANCEL_STATS)))
				SQLUtils.writeToMySQL(jsonResult, tableName);
			else if(tableName.equals("14_day_cust_cancels")&&(!jsonResult.equals(Constants.INVALID_14_DAY_CUST_CANCELS)))
				SQLUtils.writeToMySQL(jsonResult, tableName);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

 

	public SQLModel() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getEnv() {
		return this.env;
	}
  
	/**
	 * @param id
	 */
	public SQLModel(String id) {
		this.id = id;
	}

	/**
	 * @return the con
	 */
	public Connection getCon() {
		return con;
	}

	/**
	 * @param con
	 *            the con to set
	 */
	public void setCon(Connection con) {
		this.con = con;
	}

	/**
	 * @return the lastexecutetimestamp
	 */
	public Date getLastexecutetimestamp() {
		return lastexecutetimestamp;
	}

	/**
	 * @param lastexecutetimestamp
	 *            the lastexecutetimestamp to set
	 */
	public void setLastexecutetimestamp(Date lastexecutetimestamp) {
		this.lastexecutetimestamp = lastexecutetimestamp;
	}
	
	public ConcurrentHashMap<Integer, HashMap<String, Object>> refreshResultSet() {
		return refreshResultSet(false);
	}
	
	/*public ConcurrentHashMap<Integer, HashMap<String, Object>> refreshResultSet(boolean forceSQLRefresh) {
		return refreshResultSet(forceSQLRefresh);
	}*/
	
	/*public ConcurrentHashMap<Integer, HashMap<String, Object>> refreshResultSet(Connection connectionMap) {
		return refreshResultSet(false,connectionMap);
	}*/

	public ConcurrentHashMap<Integer, HashMap<String, Object>> refreshResultSet(boolean forceSQLRefresh) {
		//HazelcastInstance		hazelcastInstance	= Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME);
		//IMap<String, SQLModel>	MODEL_MAP			= hazelcastInstance.getMap("STOREELF_SQL_MODELMAP");
		Connection CONNECTION = null;
		String inst = null;
		String efc = null;
		HashMap<String, Object> row = null;
		Object val = null;
		Iterator<String> efcit = null;
		ConcurrentHashMap<Integer, HashMap<String, Object>> map = null;
		ConcurrentHashMap<Integer, HashMap<String, Object>> tempmap = null;
		try {			
			inst = Constants.SQL_INST.get(this.getId());
			
			if(Constants.SQL_INST.get(this.getId())==null) throw new Exception("CRITICAL: SQL_ID '"+this.getId()+"' does NOT have a Database instance listed.");
				
			if (
					getLastexecutetimestamp() == null || 
					(getLastresulttimestamp() != null && getLastexecutetimestamp().getTime() < getLastresulttimestamp().getTime()) ||
					forceSQLRefresh == true
			) {
				setLastexecutetimestamp(new Date());
				
				

				if (inst.equals(Constants.EFC)) {
					efcit = Constants.EFC_LIST.iterator();
					map = new ConcurrentHashMap<Integer, HashMap<String, Object>>();
					map.put(1, new HashMap<String, Object>());

					while (efcit.hasNext()) {
						efc = efcit.next();
						tempmap = null;
						
						CONNECTION = ReportActivator.getInstance().getConnection(efc);
						
						if (StringUtils.isVoid(this.env)) {							
							CONNECTION = (ReportActivator.getInstance().getConnection(efc));
							
							if(CONNECTION==null) throw new Exception("DB Connection null for efc: "+efc);
							//con = ReportActivator.getInstance().getConnection(efc);
						} else {
							CONNECTION = ReportActivator.getInstance().getConnection(efc, env);
							if(CONNECTION==null) throw new Exception("DB Connection null for efc: "+efc);
							//con = ReportActivator.getInstance().getConnection(efc, env);
						}
						tempmap = SQLUtils.getSQLResult(getId(),Constants.SQL_MAP.get(this.getId()), CONNECTION);
						row = tempmap.get(1);
						val = null;
						if (row != null) val = row.get(Constants.EFC);
						map.get(1).put(efc, val);
					}
					this.resultmap = (map==null || map.size()==0) ? this.resultmap : map;
				} else {
					if (StringUtils.isVoid(this.env)) {
						
						 
							 CONNECTION = ReportActivator.getInstance().getConnection(inst);
						 
						
						if(CONNECTION==null) throw new Exception("DB Connection null for instance: "+inst);
						//con = ReportActivator.getInstance().getConnection(inst);
					} else {
						//BENS
						
						
							CONNECTION = ReportActivator.getInstance().getConnection(inst);
						
						
						//CONNECTION = ReportActivator.getInstance().getConnection(inst,env);
						if(CONNECTION==null) throw new Exception("DB Connection null for instance:"+inst);
						con = ReportActivator.getInstance().getConnection(inst, env);
					}
					tempmap = SQLUtils.getSQLResult(getId(),Constants.SQL_MAP.get(id), CONNECTION);
					this.resultmap = (tempmap==null || tempmap.size()==0) ? this.resultmap : tempmap;
				}
				setLastresulttimestamp(new Date());
				
				logger.trace("Thread with SQL ID {"+getId()+"} is currently DONE: StoreElfRefreshSQLAgent complete");
				Constants.STOREELF_SQL_REFRESH_JOBS.put(getId(), "DONE");
				Constants.STOREELF_SQL_JOBS_SUCCESSLOG.put(getId(), resultmap.toString());
				
				//put local copy in cached map
				SQLUtils.getCachedModelMap().set(id, this);
			}
		} catch (FileNotFoundException e) {
			logger.error("error processing refreshResultSet : FileNotFoundException", e);
		}catch(SQLTimeoutException e){
			logger.error("-->"+id);
			logger.error("-->\n\r"+Constants.SQL_MAP.get(id)+"\n\r", e);
			logger.error("error processing refreshResultSet : SQLTimeoutException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(id, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(id, ExceptionUtils.getStackTrace(e));			
			logger.error("Thread with SQL ID {"+id+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		} catch (SQLException e) {
			logger.error("-->"+id);
			logger.error("-->\n\r"+Constants.SQL_MAP.get(id)+"\n\r", e);
			logger.error("error processing refreshResultSet : SQLException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(id, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(id, ExceptionUtils.getStackTrace(e));			
			logger.error("Thread with SQL ID {"+id+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		} catch (ClassNotFoundException e) {
			logger.error("error processing refreshResultSet : ClassNotFoundException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(id, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(id, ExceptionUtils.getStackTrace(e));			
			logger.error("Thread with SQL ID {"+id+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		} catch (IOException e) {
			logger.error("error processing refreshResultSet : IOException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(id, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(id, ExceptionUtils.getStackTrace(e));			
			logger.error("Thread with SQL ID {"+id+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		} catch (Exception e){
			Constants.STOREELF_SQL_REFRESH_JOBS.put(id, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(id, ExceptionUtils.getStackTrace(e));			
			logger.error("Thread with SQL ID {"+id+"} threw ERROR: StoreElfRefreshSQLAgent FAILED");
		} finally {
			try { 
				if(CONNECTION != null){
					CONNECTION.close();	
				}
			}
			catch (SQLException e) { logger.error("error processing refreshResultSet ... again : SQLException", e);}
			catch (Exception e) {e.printStackTrace();}
			
			//cleanup for GC
			inst = null;
			efc = null;
			row = null;
			val = null;
			efcit = null;
			map = null;
			tempmap = null;
			CONNECTION = null;
		}
		
		
		
		this.afterRefreshModel();		
		return this.resultmap;
	}
	
	//get connection from thread's map
	public Connection getConnectionFromThreadMap(ConcurrentHashMap<String, Connection> CONNECTION_MAP, Connection replacementConnection){
		if(CONNECTION_MAP.containsKey(getId())){
			return CONNECTION_MAP.get(getId());
		}else{
			if(replacementConnection !=null)	CONNECTION_MAP.put(getId(), replacementConnection);
			return CONNECTION_MAP.get(getId());
		}
	}


	public boolean isRefreshRequired() {
		
		if ( getLastexecutetimestamp() == null) {
			return true;
		}
		if ((new Date().getTime()
				- getLastexecutetimestamp().getTime()) > Constants.SQL_TIME_MAP.get(id)) {
			return true;
		}
		return false;
	}


	/**
	 * @return the resultmap
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws StoreElfException
	 */
	public ConcurrentHashMap<Integer, HashMap<String, Object>> getResultmap() {
		//boolean isWhitelistFiltered = ReportActivator.ProcessSQLAgentWhitelistFilter(getId());
		
		if(Constants.STOREELF_SQL_REFRESH_JOBS.containsKey(getId())){
			//if currently flagged as running AND thread exists
			if( StringUtils.equals(Constants.STOREELF_SQL_REFRESH_JOBS.get(getId()),"RUNNING") && SQLUtils.getThreadStatus(getId())!=null){
				//ignore and leave the thread alone, 
			}else{
				//if the SQLID is in ERROR status, LEAVE it in error status to be inspected and triggered again later
				if(StringUtils.equals(Constants.STOREELF_SQL_REFRESH_JOBS.get(getId()),"DISABLED")){
					//ignore and leave it at ERROR
				}else{
					//if(isWhitelistFiltered) 
						Constants.STOREELF_SQL_REFRESH_JOBS.put(getId(), "SCHEDULED");
				}
			}
		}else{
			//if(isWhitelistFiltered) 
				Constants.STOREELF_SQL_REFRESH_JOBS.put(getId(), "SCHEDULED");
		}
		return resultmap;
	}



	/**
	 * @param resultmap
	 *            the resultmap to set
	 */
	public void setResultmap(ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap) {
		this.resultmap = resultmap;
		Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME).getMap("STOREELF_SQL_MODELMAP")
				.put(id, this);
	}

	/**
	 * @return the lastresulttimestamp
	 */
	public Date getLastresulttimestamp() {
		return lastresulttimestamp;
	}

	/**
	 * @param lastresulttimestamp
	 *            the lastresulttimestamp to set
	 */
	public void setLastresulttimestamp(Date lastresulttimestamp) {
		this.lastresulttimestamp = lastresulttimestamp;
	}

	public void afterRefreshModel(){
		// HOOK
	}
}
