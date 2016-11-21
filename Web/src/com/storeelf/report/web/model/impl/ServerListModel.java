/**
 *
 */
package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.exception.StoreElfException;

/**
 * <B>Class Name:</B>This class is used to <BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Nov 18, 2011 4:45:10 PM<BR/>
 */
public class ServerListModel extends SQLModel implements Serializable {

	private static final long serialVersionUID = 929071902617329299L;
	private boolean processed;
	private List<ServerModel> serverlist;

	public ServerListModel(String id) {
		super(id);
		this.serverlist = new ArrayList<ServerModel>();
	}

	public ServerListModel() {
		super();
		this.serverlist = new ArrayList<ServerModel>();
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * @param processed
	 *            the processed to set
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	/**
	 *
	 * @return The list of servers
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getServerlist() throws StoreElfException {
		ConcurrentHashMap<Integer, HashMap<String, Object>> resultmap = this
				.getResultmap();
		if (resultmap == null || resultmap.size()<1)
			setProcessed(false);
		else {
			try {
				this.serverlist.clear();
				Integer rowcount = resultmap.size();

				HashMap<String, Object> row = null;
				String servername = null;
				String hostname = null;
				String servertype = null;
				BigDecimal activecount = null;
				ServerModel server = null;

				for (int i = 0; i < rowcount; i++) {
					row = resultmap.get(i + 1);
					servername = (String) row.get("server_name".toUpperCase());
					hostname = (String) row.get("host_name".toUpperCase());
					servertype = (String) row.get("server_type".toUpperCase());
					activecount = (BigDecimal) row.get("active_count".toUpperCase());
					server = new ServerModel(servername, hostname, servertype, activecount.intValue());
					server.setTotalcount(activecount.intValue());
					server.setExceptioncount(0);
					server.setInactivecount(0);
					this.serverlist.add(server);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				this.serverlist = new ArrayList<ServerModel>();
			}
		}
		return serverlist;
	}

	/**
	 *
	 * @return The List of App Servers
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getServerListByType(String type)
			throws FileNotFoundException, ClassNotFoundException, SQLException,
			IOException, SAXException, ParserConfigurationException, StoreElfException {
		Map<String, Map<String, List<ServerModel>>> conmap = this
				.getConsolidatedServerMap();
		if (conmap == null || conmap.size() == 0) {
			return this.getServerlist();
		}
		List<ServerModel> appServerList = new ArrayList<ServerModel>();
		Iterator<String> it = conmap.keySet().iterator();

		List<ServerModel> applist = null;

		while (it.hasNext()) {
			applist = conmap.get(it.next()).get(type);
			if (applist != null)
				appServerList.addAll(applist);
		}
		return appServerList;
	}

	/**
	 *
	 * @return The List of App Servers
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getAppServerList() throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException, SAXException,
			ParserConfigurationException, StoreElfException {
		return this.getServerListByType(Constants.APPSERVER);
	}

	/**
	 *
	 * @return A List of Agent Servers
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getAgentServerList() throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException, SAXException,
			ParserConfigurationException, StoreElfException {
		return this.getServerListByType(Constants.AGENTSERVER);
	}

	/**
	 *
	 * @return A List of Int Servers
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getIntServerList() throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException, SAXException,
			ParserConfigurationException, StoreElfException {
		return this.getServerListByType(Constants.INTSERVER);
	}

	/**
	 *
	 * @return A List of Health Monitor Servers
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws StoreElfException
	 */
	public List<ServerModel> getHealthMonitorList()
			throws FileNotFoundException, ClassNotFoundException, SQLException,
			IOException, SAXException, ParserConfigurationException, StoreElfException {
		return this.getServerListByType(Constants.HEALTHMONITOR);
	}

	public HashMap<String, List<ServerModel>> mapServerTypeByHost(
			String servertype) throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException, SAXException,
			ParserConfigurationException, StoreElfException {
		HashMap<String, List<ServerModel>> map = new HashMap<String, List<ServerModel>>();
		List<ServerModel> baseserverlist = null;
		if (servertype.equals(Constants.APPSERVER)) {
			baseserverlist = this.getAppServerList();
		}
		if (servertype.equals(Constants.AGENTSERVER)) {
			baseserverlist = this.getAgentServerList();
		}
		if (servertype.equals(Constants.INTSERVER)) {
			baseserverlist = this.getIntServerList();
		}
		if (servertype.equals(Constants.HEALTHMONITOR)) {
			baseserverlist = this.getHealthMonitorList();
		}
		Iterator<ServerModel> it = baseserverlist.iterator();
		while (it.hasNext()) {
			ServerModel server = it.next();
			String hostname = server.getHostname();
			List<ServerModel> serverlist = map.get(hostname);
			if (serverlist == null) {
				serverlist = new ArrayList<ServerModel>();
			}
			serverlist.add(server);
			map.put(hostname, serverlist);
		}
		return map;
	}

	/**
	 *
	 * @return A map of all Servers associated with the Environment
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 * @throws StoreElfException
	 */
	public Map<String, Map<String, List<ServerModel>>> getConsolidatedServerMap()
			throws FileNotFoundException, ClassNotFoundException, SAXException,
			IOException, ParserConfigurationException, SQLException, StoreElfException {
		Map<String, Map<String, List<ServerModel>>> hostmap = ReportActivator
				.getInstance().getConfiguredServerList(this.getEnv());

		List<ServerModel> serverlist = this.getServerlist();
		Iterator<ServerModel> it = serverlist.iterator();

		ServerModel server = null;
		String hostname = null;
		String servertype = null;
		Integer totalcount = null;
		Integer inactivecount = null;
		Map<String, List<ServerModel>> typemap = null;

		while (it.hasNext()) {
			boolean check = false;
			server = it.next();
			hostname = server.getHostname();
			servertype = server.getServertype();
			totalcount = server.getTotalcount();
			inactivecount = server.getInactivecount();
			typemap = hostmap.get(hostname);
			if (typemap == null) {
				typemap = new TreeMap<String, List<ServerModel>>();
			}
			List<ServerModel> srvlist = typemap.get(servertype);
			if (srvlist == null) {
				srvlist = new ArrayList<ServerModel>();
			}
			Iterator<ServerModel> itmod = srvlist.iterator();
			ServerModel srvmod = null;
			while (itmod.hasNext()) {
				srvmod = itmod.next();
				if (srvmod.equals(server)) {
					int tot = srvmod.getTotalcount();
					int inact = srvmod.getInactivecount();
					int expcnt = totalcount - (tot - inact) - inactivecount;
					if (expcnt < 0) {
						expcnt = -1 * expcnt;
					} else {
						expcnt = 0;
					}
					server.setExceptioncount(expcnt);
					server.setInactivecount(inact);
					check = true;
					break;
				}
			}
			if (check) {
				srvlist.remove(srvmod);
			}
			srvlist.add(server);
			typemap.put(servertype, srvlist);
			hostmap.put(hostname, typemap);
		}
		return hostmap;
	}

	/**
	 *
	 * @return: List of Hosts for INT and AGENT servers.
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 * @throws StoreElfException
	 */
	public List<String> getAgentIntServerHostList()
			throws FileNotFoundException, ClassNotFoundException, SAXException,
			IOException, ParserConfigurationException, SQLException, StoreElfException {

		List<String> hostList	= new ArrayList<String>();
		Set<String> hostset		= new HashSet<String>();
		Map<String, Map<String, List<ServerModel>>> map = this.getConsolidatedServerMap();
		Iterator<String> ithost = map.keySet().iterator();

		String host = null;
		Map<String, List<ServerModel>> typemap = null;
		List<ServerModel> intlist = null;
		List<ServerModel> agentlist = null;

		while (ithost.hasNext()) {
			host = ithost.next();
			typemap = map.get(host);
			intlist = typemap.get(Constants.INTSERVER);

			if (intlist != null && intlist.size() > 0) {
				hostset.add(host);
			}
		}
		ithost = map.keySet().iterator();
		while (ithost.hasNext()) {
			host = ithost.next();
			typemap = map.get(host);
			agentlist = typemap.get(Constants.AGENTSERVER);
			if (agentlist != null && agentlist.size() > 0) {
				hostset.add(host);
			}
		}
		hostList = new ArrayList<String>(hostset);
		Collections.sort(hostList);
		return hostList;
	}

	/**
	 * This method is used to return a list of hosts for a type of server
	 *
	 * @param type
	 *            : AGENT/INT/APPSERVER etc
	 * @return List of server host names for the Server Type
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 * @throws StoreElfException
	 */
	public List<String> getHostListByType(String type)
			throws FileNotFoundException, ClassNotFoundException, SAXException,
			IOException, ParserConfigurationException, SQLException, StoreElfException {
		List<String> hostList = new ArrayList<String>();
		Map<String, Map<String, List<ServerModel>>> map = this
				.getConsolidatedServerMap();
		Iterator<String> ithost = map.keySet().iterator();
		while (ithost.hasNext()) {
			String host = ithost.next();
			Map<String, List<ServerModel>> typemap = map.get(host);
			List<ServerModel> intlist = typemap.get(type);

			if (intlist != null && intlist.size() > 0) {
				hostList.add(host);
			}
		}
		return hostList;
	}

	/**
	 * 0 - Server Up
	 * 1 - Server has issues
	 * 2 - Server has down
	 *
	 * @return status of a Host (Agent or Integration only)
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws StoreElfException
	 */
	public HashMap<String, Integer> getServerStatusList(String type)
			throws FileNotFoundException, ClassNotFoundException, SQLException,
			IOException, SAXException, ParserConfigurationException, StoreElfException {
		HashMap<String, Integer> servermap = new HashMap<String, Integer>();
		List<String> hostlist = this.getHostListByType(type);
		Iterator<String> ithostlist = hostlist.iterator();
		while (ithostlist.hasNext()) {
			String hostname = ithostlist.next();
			Integer status = this.getHostStatus(hostname, type);
			servermap.put(hostname, status);
		}
		return servermap;
	}

	/**
	 *
	 * @param hostname
	 * @param type
	 *            : AGENT/INT
	 * @return
	 *         0 - Host is up
	 *         1 - Host has some issues
	 *         2 - Host is down
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 * @throws StoreElfException
	 */
	public Integer getHostStatus(String hostname, String type)
			throws FileNotFoundException, ClassNotFoundException, SAXException,
			IOException, ParserConfigurationException, SQLException, StoreElfException {
		Integer status = 0;
		Map<String, Map<String, List<ServerModel>>> hostmap = this
				.getConsolidatedServerMap();
		if (hostmap == null)
			return 2;
		Map<String, List<ServerModel>> typemap = hostmap.get(hostname);
		if (typemap == null)
			return 0;
		List<ServerModel> servers = typemap.get(type);
		if (servers == null)
			return 0;
		Iterator<ServerModel> itserver = servers.iterator();
		int expcount = 0;
		int totcount = servers.size();
		while (itserver.hasNext()) {
			ServerModel server = itserver.next();
			if (server.getExceptioncount() > 0) {
				expcount++;
			}
		}
		if (expcount == 0)
			return 0;
		if (expcount < totcount)
			return 1;
		if (expcount == totcount)
			return 2;
		return status;
	}
}
