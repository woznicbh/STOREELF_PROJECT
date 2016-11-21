package com.storeelf.report.web.init;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.comparator.MapStringStringComparator;
import com.storeelf.report.web.model.impl.ServerModel;
import com.storeelf.util.SecurityUtils;
import com.storeelf.util.StringUtils;
import com.storeelf.util.XMLUtils;
import com.storeelf.util.XProperties;
import com.storeelf.util.model.Database;
import com.storeelf.util.model.SCEnv;
import com.storeelf.util.model.impl.DatabaseMySQLImpl;
import com.storeelf.util.model.impl.SCWSEnv;

/**
 * <B>Class Name:</B> com.storeelf.report.web.init.ReportActivator<BR/>
 * <B>Purpose:</B> Initiates the environment for Report Generation<BR/>
 * <B>Creation Date:</B> Sep 8, 2011 5:03:43 PM
 */
public class ReportActivator {
	//private static final Logger logger = Logger.getLogger(ReportActivator.class.getPackage().getName());
	static final Logger						logger						= Logger.getLogger(ReportActivator.class);


	private static ReportActivator instance;
	public static String server_instance = "OMS" ;
	public static XProperties systemProperties = null;
	private HashMap<String, Database> reportDBMap = new HashMap<String, Database>();
	private HashMap<String, SCEnv> envMap = new HashMap<String, SCEnv>();

	public HashMap<String, Database> getReportDBMap() {
		return this.reportDBMap;
	}

	public static XProperties getXProperties(){
		try {
			if(systemProperties==null){
				ReportActivator.systemProperties = new XProperties();
				ReportActivator.systemProperties.load(ReportActivator.class.getResourceAsStream("storeelf.properties"));
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
		ReportActivator.systemProperties.load(this.getClass().getResourceAsStream("storeelf.properties"));
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

	public SCEnv getEnv(String instance) throws ClassNotFoundException,
	SQLException {
		return this.getEnv(instance, systemProperties.getProperty(instance
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

			return this.reportDBMap.get(instance).getNewConnection();
		
		}
		catch(SQLException e)			{logger.error("Could not establish connection to instance:"+instance, e);}
		catch(ClassNotFoundException e)	{logger.error("Could not establish connection to instance:"+instance, e);}
		catch(Exception e)				{logger.error("Could not establish connection to instance:"+instance, e);}
		return null;
	}

	public HashMap<String, HashMap<Date, String>> getFileMap(String cprefix,
			File dir, final String filter) throws ParseException {
		TreeMap<String, String> reportNameMap = Constants.REPORT_MAP;
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dthrformat = new SimpleDateFormat("yyyy/MM/dd HH");

		List<Map.Entry<String, String>> rlist = new LinkedList<Map.Entry<String, String>>(
				reportNameMap.entrySet());
		Collections.sort(rlist, new MapStringStringComparator());
		HashMap<String, HashMap<Date, String>> filemap = new HashMap<String, HashMap<Date, String>>();
		ArrayList<String> reportList = new ArrayList<String>();
		Iterator<String> rnameit = reportNameMap.keySet().iterator();
		while (rnameit.hasNext()) {
			String name = rnameit.next();
			reportList.add(name);
			filemap.put(name, new HashMap<Date, String>());
		}
		Iterator<String> itrlist = reportList.iterator();
		while (itrlist.hasNext()) {
			final String name = itrlist.next();
			HashMap<Date, String> map = filemap.get(name);
			File[] files = null;
			FilenameFilter filefilter = new FilenameFilter() {
				@Override
				public boolean accept(File directory, String filename) {
					boolean fileOK = true;
					fileOK &= filename.startsWith(name + "_")
							&& filename.endsWith(filter);
					return fileOK;
				}
			};
			files = dir.listFiles(filefilter);

			if(files!=null){
				for (int i = 0; i < files.length; i++) {
					String filename = files[i].getName();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					String crdt = sdf.format(new Date(files[i].lastModified()));
					String sdate = filename.substring(filename.indexOf('_') + 1,
							filename.lastIndexOf(filter));
					Date date = null;
					if (sdate.length() == 8) {
						date = dtformat.parse(sdate);
					}
					if (sdate.length() == 2) {
						date = dthrformat.parse(crdt + " " + sdate);
					}
					if (sdate.length() == 11) {
						SimpleDateFormat dtandhrformat = new SimpleDateFormat(
								"yyyyMMdd_HH");
						date = dtandhrformat.parse(sdate);
					}
					map.put(date, cprefix + "/" + filename);
				}
			}
		}
		return filemap;
	}

	public ArrayList<String> getReportList() {
		TreeMap<String, String> reportNameMap = Constants.REPORT_MAP;
		List<Map.Entry<String, String>> rlist = new LinkedList<Map.Entry<String, String>>(
				reportNameMap.entrySet());
		Collections.sort(rlist, new MapStringStringComparator());
		ArrayList<String> reportList = new ArrayList<String>();
		Iterator<String> rnameit = reportNameMap.keySet().iterator();
		while (rnameit.hasNext()) {
			String name = rnameit.next();
			reportList.add(name);
		}
		Collections.sort(reportList);
		return reportList;
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

	/**
	 * Expected XML document structure:
	 * <Hosts>
	 * <Host name="ag000038.storeelf.com">
	 * <Types>
	 * <Type type="AGENT|INT|APPSERVER|HEALTH">
	 * <Servers>
	 * <Server name="StoreElfPurgeAgent" totalNumberOfInstances="10"
	 * activeNumberOfInstances="1" />
	 * </Servers>
	 * </Type>
	 * </Types>
	 * </Host>
	 * </Hosts>
	 *
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public Map<String, Map<String, List<ServerModel>>> getConfiguredServerList(
			String env) throws SAXException, IOException,
	ParserConfigurationException {

		InputStream in = null;

		/* Nijee's change to remove hard code*/
		try{
			in = this.getClass().getResourceAsStream("storeelfserver_"+server_instance+".xml");
		}catch(Exception e){
			logger.error("can't find storeelfserver.xml, put it back!!!", e);
		}

		Document serverdoc = XMLUtils.fileToXml(in);
		Map<String, Map<String, List<ServerModel>>> servermap = new TreeMap<String, Map<String, List<ServerModel>>>();
		String defaultenv = this.getDefaultEnv( );
		if (StringUtils.isVoid(env)) {
			env = defaultenv;
		}


		NodeList nlEnv = serverdoc.getElementsByTagName("Environment");
		for (int ienv = 0; ienv < nlEnv.getLength(); ienv++) {
			Element eEnv = (Element) nlEnv.item(ienv);
			String sEnv = eEnv.getAttribute("name");
			if (!StringUtils.isVoid(sEnv) && sEnv.equals(env)) {
				NodeList nlHosts = eEnv.getElementsByTagName("Host");
				for (int host = 0; host < nlHosts.getLength(); host++) {
					Element hostele = (Element) nlHosts.item(host);
					String hostname = hostele.getAttribute("name");
					NodeList nlTypes = hostele.getElementsByTagName("Type");
					Map<String, List<ServerModel>> typemap = new HashMap<String, List<ServerModel>>();
					for (int type = 0; type < nlTypes.getLength(); type++) {
						Element typeele = (Element) nlTypes.item(type);
						String typename = typeele.getAttribute("name");
						NodeList nlServers = typeele
								.getElementsByTagName("Server");
						List<ServerModel> serverlist = new ArrayList<ServerModel>();
						for (int server = 0; server < nlServers.getLength(); server++) {
							Element serverele = (Element) nlServers
									.item(server);
							String servername = serverele.getAttribute("name");
							String totalInstance = serverele
									.getAttribute("totalNumberOfInstances");
							String activeInstance = serverele
									.getAttribute("activeNumberOfInstances");
							int active = 0;
							int total = 0;
							int inactive = 0;
							if (totalInstance != null) {
								total = Integer.parseInt(totalInstance);
							}
							if (activeInstance != null) {
								active = Integer.parseInt(activeInstance);
							}
							inactive = (total - active);
							ServerModel mdl = new ServerModel();
							mdl.setActivecount(0);
							if (!typename.equals(Constants.APPSERVER)
									&& !typename.equals(Constants.AGENTSERVER)
									&& !typename.equals(Constants.INTSERVER))
								mdl.setActivecount(active);
							mdl.setInactivecount(inactive);
							mdl.setExceptioncount(total - inactive);
							mdl.setTotalcount(total);
							mdl.setHostname(hostname);
							mdl.setServername(servername);
							mdl.setServertype(typename);
							serverlist.add(mdl);
						}
						typemap.put(typename, serverlist);
					}
					servermap.put(hostname, typemap);
				}
			}
		}
		return servermap;
	}

	public SCEnv getEnv(String instance, String env) throws ClassNotFoundException, SQLException {
		try{
			SCEnv scenv = null;
			String spropprefix	= instance + Constants.PROP_SEPERATOR + env + Constants.PROP_SEPERATOR;

			switch (Constants.ENV_TYPE_MAP.get(systemProperties.getProperty(spropprefix + Constants.PROP_ENV_TYPE))) {
			// WS
			case SCWSEnv.TYPE:
				scenv = new SCWSEnv();
				scenv.setProgId(systemProperties.getProperty(spropprefix + Constants.PROP_PROG));
				scenv.setUserId(systemProperties.getProperty(spropprefix + Constants.PROP_USER));
				scenv.setUrl(systemProperties.getProperty(spropprefix + Constants.PROP_URL));
				break;
			}
			return scenv;
		}catch(Exception e){
			logger.error("CHECK STOREELF.PROPERTIES FILE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",e);
		}
		return null;
	}

	public Database getDB(String instance, String env)
			throws ClassNotFoundException, SQLException {
		Database db = null;
		String spropprefix = instance + Constants.PROP_SEPERATOR + env
				+ Constants.PROP_SEPERATOR;
		String sLunarDBUsr			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_USR);
		String sLunarDBPwd			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_PWD);
		String sLunarDBHost			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_HOST);
		String sLunarDBPort			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_PORT);
		String sLunarDBSID			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SID);
		String sLunarDBServiceName	= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SERVICE_NAME);
		String sLunarDBUrl			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_URL);
		String sLunarDBType			= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_TYPE);
		String schema				= systemProperties.getProperty(spropprefix	+ Constants.PROP_DB_SCHEMA);

		logger.debug("System Properties:\n" + spropprefix
				+ "DB_USER:"+ sLunarDBUsr + "\n" + spropprefix
				+ "DB_PWD:" + sLunarDBPwd + "\n"	+ spropprefix
				+ "DB_HOST:" + sLunarDBHost		+ "\n" + spropprefix
				+ "DB_SID:" + sLunarDBSID + "\n" + spropprefix
				+ "DB_SERVICE_NAME:" + sLunarDBServiceName);

		loadCertKey();

		logger.debug("Creating connection to: ["+instance+"] using Key: "+Constants.STOREELF_CERT_KEY);

		try{
			sLunarDBPwd = (sLunarDBPwd!=null) ? SecurityUtils.symmetricDecrypt(sLunarDBPwd, Constants.STOREELF_CERT_KEY) : "STOREELF_DECRYPTION_ERROR";
		}catch(Exception e){
			logger.error("error decrypting data for instance '"+instance+"' on '"+env+"'");
			sLunarDBPwd = "STOREELF_DECRYPTION_ERROR";
		}

		if(StringUtils.equals("STOREELF_ENCRYPTION_ERROR", sLunarDBPwd ) ||  StringUtils.equals("STOREELF_DECRYPTION_ERROR", sLunarDBPwd ) ){
			//quit
			logger.error(sLunarDBPwd+": Error connecting to database, please verify password is correct for instance '"+instance+"' on '"+env+"'");
			return null;
		}

		db = new DatabaseMySQLImpl(sLunarDBUsr, sLunarDBPwd, sLunarDBHost, sLunarDBPort, sLunarDBSID, sLunarDBUrl);

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
