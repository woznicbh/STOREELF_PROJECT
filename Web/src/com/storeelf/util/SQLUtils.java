/**
 *
 */
package com.storeelf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.model.Database;

/**
 * <B>Purpose:</B> This is utility class with utilities for SQL<BR/>
 * <B>Creation Date:</B> Sep 26, 2011 2:30:17 PM<BR/>
 */
public class SQLUtils {
	static final Logger logger							= Logger.getLogger(SQLUtils.class);

	public static void sqlToXML(Database db, Document xpathDoc, String sql,
			String dirpath, String prefix, String filetype)
			throws SQLException, XPathExpressionException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, IOException, ClassNotFoundException {
		
		Connection con = db.getNewConnection();
		Statement stmt = null;
		ResultSet rset = null;
		try {
			HashMap<String, Boolean> colmap = new HashMap<String, Boolean>();
			stmt = con.createStatement();
			rset = stmt.executeQuery(sql);
			XpathParser parser = new XpathParser();
			int iFileCount = 0;

			Document finalDoc = null;
			Document doc = XMLUtils.createEmptyDocument();

			NodeList nl = null;

			Element ele = null;
			String col = null;
			Boolean chk = null;

			String val = null;

			while (rset.next()) {
				finalDoc = null;
				doc = XMLUtils.createEmptyDocument();
				doc.appendChild(doc.importNode(xpathDoc.getDocumentElement().cloneNode(true), true));

				nl = doc.getElementsByTagName("XPath");

				for (int iCount = 0; iCount < nl.getLength(); iCount++) {
					ele = (Element) nl.item(iCount);
					col = ele.getAttribute("Value");
					chk = colmap.get(col);
					try {
						if (chk == null || chk.booleanValue()) {
							val = rset.getObject(col).toString().trim();
							ele.setAttribute("Value", val);
							colmap.put(col, true);
						} else {
							ele.setAttribute("Value", col);
						}
					} catch (java.sql.SQLException e) {
						System.err.print("Invalid Column:" + col);
						ele.setAttribute("Value", col);
						colmap.put(col, false);
					}
				}
				iFileCount++;
				finalDoc = parser.createDocFromXPath(doc);
				File outfile = new File(dirpath + File.separator + prefix + "_"
						+ iFileCount + "." + filetype);
				XMLUtils.writeXmlFile(finalDoc, outfile);
			}

		} finally {
			if(stmt != null){stmt.close();}
			if(rset != null){rset.close();}
		}
	}

	public static Document sqlToXML(Database db, Document xpathDoc, String sql,
			String rootElement) throws SQLException, XPathExpressionException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, IOException, ClassNotFoundException {
		HashMap<String, Boolean> colmap = new HashMap<String, Boolean>();
		Connection con = db.getNewConnection();
		Document outDoc = XMLUtils.createEmptyDocument();
		Element eleRoot = outDoc.createElement(rootElement);
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.createStatement();
			rset = stmt.executeQuery(sql);
			XpathParser parser = new XpathParser();
			while (rset.next()) {
				Document finalDoc = null;
				Document doc = XMLUtils.createEmptyDocument();
				doc.appendChild(doc.importNode(xpathDoc.getDocumentElement()
						.cloneNode(true), true));
				NodeList nl = doc.getElementsByTagName("XPath");
				for (int iCount = 0; iCount < nl.getLength(); iCount++) {
					Element ele = (Element) nl.item(iCount);
					String col = ele.getAttribute("Value");
					Boolean chk = colmap.get(col);
					try {
						if (chk == null || chk.booleanValue()) {
							Object colval = rset.getObject(col);
							if(colval==null){
								colval="";
							}
							String val = colval.toString().trim();
							ele.setAttribute("Value", val);
							colmap.put(col, true);
						} else {
							ele.setAttribute("Value", col);
						}
					} catch (java.sql.SQLException e) {
						logger.error("Invalid Column:" + col);
						ele.setAttribute("Value", col);
						colmap.put(col, false);
					}
				}
				finalDoc = parser.createDocFromXPath(doc);
				eleRoot.appendChild(outDoc.importNode(
						finalDoc.getDocumentElement(), true));
			}
			outDoc.appendChild(eleRoot);
		} finally {
			if(stmt != null){stmt.close();}
			if(rset != null){rset.close();}
			try {
				if (!con.getMetaData().getURL().contains("k2ms2055")) {
					if (con != null)
						con.close();
				}
			} catch (SQLException e) {
				logger.error("error closing SQL  connection: IOException", e);
			} catch (Exception e) {
				logger.error("error closing SQL  connection : Exception", e);
			}
		}
		return outDoc;
	}
	
	public static ConcurrentHashMap<Integer, HashMap<String, Object>> getSQLResult(String sql, Connection con) throws SQLException{
		return getSQLResult(null, sql, con);
	}

	/**
	 *
	 * <B>Purpose:</B>Create a Map of sql result rows. RowNum,RowMap
	 *
	 * @param sql
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static ConcurrentHashMap<Integer, HashMap<String, Object>> getSQLResult(String sqlId, String sql, Connection con) throws SQLException {
		ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap = new ConcurrentHashMap<Integer, HashMap<String, Object>>();
		PreparedStatement	stmt    = null;
		ResultSet			rset	= null;
		ResultSetMetaData	meta	= null;
		int colcount = 0;
		int rowcount = 0;
		HashMap<String, Object> row = null;
		ArrayList<String>	column_names	= new ArrayList<String>();
		try{
			
			
			if(StringUtils.isNotBlank(sqlId)){
				Constants.STOREELF_SQL_STMT_MAP.put(sqlId,con.prepareStatement(sql));
				stmt	= Constants.STOREELF_SQL_STMT_MAP.get(sqlId);
				if(stmt.isClosed()){
					Constants.STOREELF_SQL_STMT_MAP.put(sqlId,con.prepareStatement(sql));
					stmt	= Constants.STOREELF_SQL_STMT_MAP.get(sqlId);
				}
			}else{
				stmt	= con.prepareStatement(sql);
			}
			
			con.setAutoCommit(true);
			
			
			if (!con.getMetaData().getURL().contains("k2ms2055")) {
				con.setReadOnly(true);
			}
			//limit all queries to 15 minutes
			stmt.setQueryTimeout(60 * Constants.STOREELF_SQL_TIMEOUT_MINUTES);

			logger.debug("Executing Query: " + sql);
			rset		= stmt.executeQuery();
			meta		= rset.getMetaData();
			colcount	= meta.getColumnCount();

			while (rset.isClosed()==false && rset.next()) {
				rowcount++;
	
				row = new HashMap<String, Object>();
	
				//if column names list is empty, populate with column names
				if(column_names.size()==0){
						//set initial node '0' as empty - REQUIRED
						column_names.add("");
					for (int i = 1; i <= colcount; i++){
						column_names.add(meta.getColumnName(i));
					}
				}
	
				for (int i = 1; i <= colcount; i++) {
					if(rset.isClosed()){
						throw new Exception("ResultSet closed, potential corrupted resultset... rerun query");						
					}
	
					row.put(column_names.get(i).toUpperCase(), rset.getObject(i));
					logger.debug("Column: " + (column_names.get(i).toUpperCase()+"").toUpperCase() + "\nValue: " + rset.getObject(i));
				}
				resultmap.put(rowcount, row);
			}
		} catch(CommunicationsException e){
			logger.error("Closed the Connection due to : CommunicationsException");
			if(con!=null){con.close();}
		} catch(SQLTimeoutException e){
			logger.error("SQL query '"+sqlId+"' cancelled due to Timeout or User cancel: SQLTimeoutException");
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} catch(SQLException e){
			logger.error("error '"+sqlId+"' executing SQL query: IOException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} catch(Exception e) {
			logger.error("error '"+sqlId+"' executing SQL query, connection could be null: Exception", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} finally {
			if(stmt != null){stmt.close();}
			if(rset != null){rset.close();}
			
		}
		
		return resultmap;
	}
	
	/**
	 *
	 * <B>Purpose:</B>Create a Map of sql result rows. RowNum,RowMap
	 *
	 * @param sql
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static ConcurrentHashMap<Integer, HashMap<String, Object>> getPreparedSQLResult(PreparedStatement statement,
			String sqlId, Connection con) throws SQLException {
		ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap = new ConcurrentHashMap<Integer, HashMap<String, Object>>();

		ResultSet rset = null;
		ResultSetMetaData meta = null;
		int colcount = 0;
		int rowcount = 0;
		HashMap<String, Object> row = null;
		ArrayList<String> column_names = new ArrayList<String>();
		try {

			con.setAutoCommit(true);

			if (!con.getMetaData().getURL().contains("k2ms2055")) {
				con.setReadOnly(true);
			}
			// limit all queries to 15 minutes
			statement.setQueryTimeout(60 * Constants.STOREELF_SQL_TIMEOUT_MINUTES);

			logger.debug("Executing Query: " + Constants.SQL_MAP.get(sqlId));
			rset = statement.executeQuery();
			meta = rset.getMetaData();
			colcount = meta.getColumnCount();

			while (rset.isClosed() == false && rset.next()) {
				rowcount++;

				row = new HashMap<String, Object>();

				// if column names list is empty, populate with column names
				if (column_names.size() == 0) {
					// set initial node '0' as empty - REQUIRED
					column_names.add("");
					for (int i = 1; i <= colcount; i++) {
						column_names.add(meta.getColumnName(i));
					}
				}

				for (int i = 1; i <= colcount; i++) {
					if (rset.isClosed()) {
						throw new Exception("ResultSet closed, potential corrupted resultset... rerun query");
					}
					row.put(column_names.get(i).toUpperCase(), rset.getObject(i));
					logger.trace("Column: " + (column_names.get(i).toUpperCase() + "").toUpperCase() + "\nValue: " + rset.getObject(i));
				}
				resultmap.put(rowcount, row);
			}
		} catch(CommunicationsException e){
			logger.error("Closed the Connection due to : CommunicationsException");
			if(con!=null){con.close();}
		} catch (SQLTimeoutException e) {
			logger.error("SQL query '" + sqlId + "' cancelled due to Timeout or User cancel: SQLTimeoutException");
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			logger.error("error '" + sqlId + "' executing SQL query: IOException", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} catch (Exception e) {
			logger.error("error '" + sqlId + "' executing SQL query, connection could be null: Exception", e);
			Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlId, "ERROR");
			Constants.STOREELF_SQL_JOBS_ERRORLOG.put(sqlId, ExceptionUtils.getStackTrace(e));
		} finally {
			if(statement != null){statement.close();}
			if(rset != null){rset.close();}
						
		}
		return resultmap;
	}

	/**
	 *
	 * <B>Purpose:</B>Create a Map of sql result rows. RowNum,RowMap
	 *
	 * @param sql
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static boolean setSQL(String sql, Connection con) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement(); // (sql);
			boolean inserted = false;

			logger.debug("Executing Query: " + sql);
			inserted = stmt.execute(sql);
			logger.debug("Rows INSERTED/UPDATED: " + inserted);
			return inserted;
		} catch (SQLException e) {
			logger.debug("error writing session: SQLException", e);
		} catch (Exception e) {
			logger.debug("error writing session: Exception", e);
		}finally{
			if(stmt != null){stmt.close();}
		}
		return false;
	}

	public static String requestIDSQL(Connection con, String tableName) throws SQLException {
		Statement stmt = null;
		ResultSet rs   =  null;
		try {
			String requestId = null;
			// ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap =
			// new ConcurrentHashMap<Integer, HashMap<String, Object>>();
			String sql = "SELECT ifnull(MAX(REQUEST_ID),0) FROM " + tableName;
			stmt = con.createStatement(); // (sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				requestId = rs.getString(1).trim();
				if (requestId.equals("0") || requestId.isEmpty()) {
					requestId = "10000000";
				}
			}
			logger.debug("Executing Query: " + sql);
			logger.debug("REQUEST ID is : " + requestId);
			return requestId;
		} catch (SQLException e) {
			logger.debug("error writing session: SQLException", e);
		} catch (Exception e) {
			logger.debug("error writing session: Exception", e);
		} finally {
			if(stmt != null){stmt.close();}
			if(rs != null){rs.close();}
		}
		return null;
	}
	
	public static boolean isRefreshRequired(SQLModel model) {
		if (model.getLastexecutetimestamp() == null)
			return true;

		return (model.getLastexecutetimestamp() == null || (new Date()).getTime()
				- model.getLastexecutetimestamp().getTime() > Constants.SQL_TIME_MAP.get(model.getId()));
	}

	public static Thread getThreadStatus(String SQLID){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for(Thread t:threadSet.toArray(new Thread[threadSet.size()])){
			if(t.getName().contains(SQLID)){
				threadSet = null;
				return t;
			}
		}
		threadSet = null;
		return null;
	}
	
	public static ArrayList<Thread> getThreadStoreElfThreads(){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		ArrayList<Thread> storeelfThreads = new ArrayList<>(); 
		for(Thread t:threadSet.toArray(new Thread[threadSet.size()])){
			if(t.getName().contains("STOREELF")){
				storeelfThreads.add(t);
			}
		}
		threadSet = null;
		return storeelfThreads;
	}
	
	public static void ProcessSQLAgentWhitelist(){
		try{
			List<String> whitelist = new ArrayList<String>();
			File whitelistFile = new File(SQLUtils.class.getResource("SQL_AGENT_WHITELIST").toURI()); 
			whitelist = FileUtils.readLines(whitelistFile);
			
			String entry_sql = "";
			
			//only apply logic if whitelist contains values.
			if(whitelist.size()>0){
				Constants.STOREELF_SQL_AGENT_WHITELIST.addAll(whitelist);
				
				logger.debug("whitelist located, only enabling SQL_ID's within the file 'SQL_AGENT_WHITELIST'");
				//disable all entries
				for(Entry<String, String> entry:Constants.STOREELF_SQL_REFRESH_JOBS.entrySet()){
					entry_sql = entry.getKey();
					Constants.STOREELF_SQL_REFRESH_JOBS.put(entry_sql,"DISABLED");
				}

				//enabled only whitelisted items
				for(Object sql_id:whitelist.toArray()) Constants.STOREELF_SQL_REFRESH_JOBS.put(sql_id+"","SCHEDULED");				
			}
		}catch(Exception e){
			
		}
	}
	
	public static SQLModel getModelObject(String id, String env) {
		SQLModel model = null;
		String classname = null;
		Class<?> classDefinition = null;
		try {
			model = Constants.STOREELF_SQLMODEL_MAP.get(id);

			// if local copy is null, pull from the cached map instead
			if (model == null) {
				logger.error("Unable to locate local SQL_ID: '" + id + "', searching in cached map");
				model = getCachedModelMap().get(id);
			} else {
				return model;
			}

			// if cached copy is null, create a new one
			if (model == null) {
				logger.error("Unable to locate cached or local SQL_ID: '" + id + "', creating new SQLModel");
				classname = Constants.SQL_MODEL_MAP.get(id);
				classDefinition = Class.forName(classname);
				model = (SQLModel) classDefinition.newInstance();
				model.setId(id);
				model.setEnv(env);
			}

			if (!com.storeelf.util.StringUtils.isVoid(env) && !model.getEnv().equals(env)) {
				model.setEnv(env);
				model.setLastexecutetimestamp(null);
				model.setLastresulttimestamp(null);
			}

			Constants.STOREELF_SQLMODEL_MAP.put(id, model);
			getCachedModelMap().set(id, model);
			// SQLModel.MODEL_MAP.put(id, model);
		} catch (IllegalAccessException e) {
			logger.error("StoreElf unable to locate SQLModel - IllegalAccessException", e);
		} catch (InstantiationException e) {
			logger.error("StoreElf unable to locate SQLModel - InstantiationException", e);
		} catch (Exception e) {
			logger.error("StoreElf unable to locate SQLModel - Exception", e);
			return null;
		} finally {
			classname = null;
			classDefinition = null;
			model = null;
		}

		if (getCachedModelMap().containsKey(id)) {
			logger.error("using model from cache");		
			return getCachedModelMap().get(id);
		} else {
			logger.error("using model from local map");
			return Constants.STOREELF_SQLMODEL_MAP.get(id);
		}
	}
	
	public static SQLModel getModelObject(String id) {
		return getModelObject(id, null);
	}
	
	public static IMap<String, SQLModel> getCachedModelMap(){
		return Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME).getMap("STOREELF_SQL_MODELMAP");
	}

	public static void writeToMySQL(String jsonResult, String tableName) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		// logger.debug("Inside writeToMySQL method" + jsonResult);
		try {
			con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
			stmt = con.createStatement();

			stmt.executeUpdate(
					"delete from storeelf.storeelf_top10_cancelled  where reference_table = '" + tableName + "'");
			con.setAutoCommit(true);

			stmt.executeUpdate("\n Insert into storeelf.storeelf_top10_cancelled (jsondata,reference_table,createts) "
					+ " \n VALUES ('" + jsonResult + "','" + tableName + "'" + ", NOW())");

			con.setAutoCommit(true);
			logger.debug("My Sql updated !!!!");

		} catch(CommunicationsException e){
			logger.error("Closed the Connection due to : CommunicationsException");
			if(con!=null){con.close();}
		} catch (FileNotFoundException e) {
			logger.error("error processing request : FileNotFoundException", e);
		} catch (SQLException e) {
			logger.error("error processing request : SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (IOException e) {
			logger.error("error processing request : IOException", e);
		} finally {
			if (stmt != null){stmt.close();}
		}

	}
	
	public static void insertUpdateMySql(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		try {
			con = ReportActivator.getInstance().getConnection(Constants.STOREELF_WR);
			con.setAutoCommit(true);
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			
			logger.debug("My Sql updated !!!!");

		} catch(CommunicationsException e){
			logger.error("Closed the Connection due to : CommunicationsException");
			if(con!=null){con.close();}
		} catch (FileNotFoundException e) {
			logger.error("error processing request : FileNotFoundException", e);
		} catch (SQLException e) {
			logger.error("error processing request : SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (IOException e) {
			logger.error("error processing request : IOException", e);
		} finally {
			if (stmt != null){stmt.close();}
		}

	}
}
