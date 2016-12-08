package com.storeelf.report.web.init;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.storeelf.report.web.Constants;
import com.storeelf.util.SecurityUtils;
import com.storeelf.util.StringUtils;
import com.storeelf.util.XMLUtils;
import com.storeelf.util.XProperties;
import com.storeelf.util.model.Database;
import com.storeelf.util.model.impl.DatabaseMySQLImpl;

/**
 * <B>Class Name:</B> com.storeelf.report.web.init.ReportActivator<BR/>
 * <B>Purpose:</B> Initiates the environment for Report Generation<BR/>
 * <B>Creation Date:</B> Sep 8, 2011 5:03:43 PM
 */
public class ReportActivator {
	static final Logger						logger						= Logger.getLogger(ReportActivator.class);


	private static ReportActivator instance;
	public static String server_instance = "OMS" ;
	public static XProperties systemProperties = null;
	private HashMap<String, Database> reportDBMap = new HashMap<String, Database>();

	public HashMap<String, Database> getReportDBMap() {
		return this.reportDBMap;
	}

	public static XProperties getXProperties(){
		try {
			if(systemProperties==null){
				ReportActivator.systemProperties = new XProperties();
				ReportActivator.systemProperties.load(ReportActivator.class.getResourceAsStream("StoreElf.properties"));
			}
		} catch (IOException e) { e.printStackTrace(); }		
		return systemProperties;
	}

	//@SuppressWarnings("unchecked")
	public static boolean ProcessSQLAgentWhitelistFilter(String SQL_ID){
		return true;
	}

	/**
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ReportActivator() throws FileNotFoundException, IOException,
	ClassNotFoundException, SQLException {

		ReportActivator.systemProperties = new XProperties();
		ReportActivator.systemProperties.load(this.getClass().getResourceAsStream("StoreElf.properties"));
		this.reportDBMap.put(Constants.STOREELF_RO, this.getDB(Constants.STOREELF_RO));
		
	}



	public void restartMap(String db) throws ClassNotFoundException, SQLException{
		
		this.reportDBMap.put(db, this.getDB(db));
	
	}

	public Database getDB(String instance) throws ClassNotFoundException,
	SQLException {
		return this.getDB(instance, systemProperties.getProperty(instance
				+ Constants.PROP_SEPERATOR + Constants.PROP_DB_INSTANCE));
	}

	/**
	 *
	 * <B>Purpose:Retrieves all the system properties.</B>
	 *
	 * @return: The System properties
	 */
	public XProperties getSystemProperties() {
		return ReportActivator.systemProperties;
	}

	/**
	 * @return the instance
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public static ReportActivator getInstance() throws FileNotFoundException,
	ClassNotFoundException, IOException, SQLException {

		synchronized (ReportActivator.class) {
			if (instance == null) {
				instance = new ReportActivator();
			}

			return instance;
		}
	}

	public static void reloadPropertiesFile() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
		instance = null;
	}


	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(ReportActivator instance) {
		ReportActivator.instance = instance;
	}

	public Connection getConnection(String instance){
		logger.debug("com.storeelf.report.web.init.ReportActivator::getConnection");
		try{

			//return this.reportDBMap.get(instance).getNewConnection();
			return null;
		
		}
		//catch(SQLException e)			{logger.error("Could not establish connection to instance:"+instance, e);}
		//catch(ClassNotFoundException e)	{logger.error("Could not establish connection to instance:"+instance, e);}
		catch(Exception e)				{logger.error("Could not establish connection to instance:"+instance, e);}
		return null;
	}

	public String getDefaultEnv() throws SAXException, IOException,
	ParserConfigurationException {
		InputStream in = null;


		/* Nijee's change to remove hard code*/
		try{
			in = this.getClass().getResourceAsStream("storeelfserver_"+server_instance+".xml");
		}catch(Exception e){
			logger.error("can't find storeelfserver.xml, put it back!!!",e);
		}

		Document serverdoc = XMLUtils.fileToXml(in);
		Element eEnvs = (Element) serverdoc
				.getElementsByTagName("Environments").item(0);
		String defaultenv = eEnvs.getAttribute("default");
		logger.info("DEFAULT ENVI:" + defaultenv);
		return defaultenv;
	}

	public Database getDB(String instance, String env)
			throws ClassNotFoundException, SQLException {
		Database db = null;
		String spropprefix = instance + Constants.PROP_SEPERATOR + env
				+ Constants.PROP_SEPERATOR;
		String storeElfDBUsr			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_USR);
		String storeElfDBPwd			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_PWD);
		String storeElfDBHost			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_HOST);
		String storeElfDBPort			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_PORT);
		String storeElfDBSID			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SID);
		String storeElfDBServiceName	= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SERVICE_NAME);
		String storeElfDBUrl			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_URL);
		String storeElfDBType			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_TYPE);
		String storeELfSchema			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SCHEMA);

		logger.debug("System Properties:\n" + spropprefix
				+ "DB_USER:"+ storeElfDBUsr + "\n" + spropprefix
				+ "DB_PWD:" + storeElfDBPwd + "\n"	+ spropprefix
				+ "DB_HOST:" + storeElfDBHost		+ "\n" + spropprefix
				+ "DB_SID:" + storeElfDBSID + "\n" + spropprefix
				+ "DB_SERVICE_NAME:" + storeElfDBServiceName);

		loadCertKey();

		logger.debug("Creating connection to: ["+instance+"] using Key: "+Constants.STOREELF_CERT_KEY);

		try{
			storeElfDBPwd = (storeElfDBPwd!=null) ? SecurityUtils.symmetricDecrypt(storeElfDBPwd, Constants.STOREELF_CERT_KEY) : "STOREELF_DECRYPTION_ERROR";
		}catch(Exception e){
			logger.error("error decrypting data for instance '"+instance+"' on '"+env+"'");
			storeElfDBPwd = "STOREELF_DECRYPTION_ERROR";
		}

		if(StringUtils.equals("STOREELF_ENCRYPTION_ERROR", storeElfDBPwd ) ||  StringUtils.equals("STOREELF_DECRYPTION_ERROR", storeElfDBPwd ) ){
			//quit
			logger.error(storeElfDBPwd+": Error connecting to database, please verify password is correct for instance '"+instance+"' on '"+env+"'");
			return null;
		}

		db = new DatabaseMySQLImpl(storeElfDBUsr, storeElfDBPwd, storeElfDBHost, storeElfDBPort, storeElfDBSID, storeElfDBUrl);

		return db;
	}

	public Connection getConnection(String inst, String env)
			throws ClassNotFoundException, SQLException {
		Database db = this.getDB(inst, env);

		try{
			if(db!=null){
				if(db.getFailureCount()<2)	 return db.getNewConnection();
			}
		}
		catch(ClassNotFoundException e)	{e.printStackTrace(); if(db!=null){ db.incrementFailureCount(); } }
		catch(SQLException e)			{e.printStackTrace(); if(db!=null){ db.incrementFailureCount(); } }
		catch(Exception e)				{e.printStackTrace(); if(db!=null){ db.incrementFailureCount(); } }
		return null;
	}

	/**
	 * Returns the property value from the storeelf.properties file
	 *
	 * @param prop
	 *            : Property key
	 * @return
	 */
	public String getSystemProperty(String prop) {
		return this.getSystemProperties().getProperty(prop);
	}

	public void loadCertKey(){
		if(Constants.STOREELF_CERT_KEY==null){
			try{
				logger.debug("loading cert Key...");
				String key = org.apache.commons.io.IOUtils.toString(this.getClass().getResourceAsStream("StoreElf.cert"));
				Constants.STOREELF_CERT_KEY = key;
				logger.debug("Cert Key successfully loaded...");
			}
			catch(Exception e){
				logger.error("Error loading cert Key");
			}			
		}
	}

}
