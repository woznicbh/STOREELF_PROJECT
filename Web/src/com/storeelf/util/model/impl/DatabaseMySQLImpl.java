package com.storeelf.util.model.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.storeelf.report.web.Constants;
import com.storeelf.util.StringUtils;
import com.storeelf.util.model.Database;

import groovy.sql.Sql;

/**
 *
 * Class Name: com.storeelf.tools.model.impl.DatabaseMySQLImpl Purpose: This class
 * is used to create connections to a Oracle Database. Since MySQL treats the
 * database as the schema the schema parameter has no meaning here and is pretty
 * much ignored. Creation Date: Sep 7,
 * 2011 9:43:12 AM
 */
public class DatabaseMySQLImpl implements Database {
	static final Logger						logger						= Logger.getLogger(DatabaseMySQLImpl.class);
	private String username;
	private String password;
	private String host;
	private String port;
	private String db;
	private String url;
	private String driver;
	private String schema;
	private int	   failureCount = 0;

	/**
	 *
	 * @param username
	 *            : Database User Name
	 * @param password
	 *            : Password for the provided user
	 * @param host
	 *            : Host where the Database is running
	 * @param port
	 *            : Oracle DB Listener Port
	 * @param sid
	 *            : SID for the Database
	 * @param serviceName
	 *            : Service Name for the Database
	 * @param driver
	 *            : Driver to be used to connect to the database.
	 * @throws ClassNotFoundException
	 *             : If the Driver class is not found
	 * @throws SQLException
	 *             : If connection is not successful
	 */
	public DatabaseMySQLImpl(String username, String password, String host,
			String port, String db, String url) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.driver = "com.mysql.jdbc.Driver";
		this.db = db;
		this.setUrl("jdbc:mysql://" + this.getHost() + ":" + this.getPort() + "/" + this.getDb());
		if (!StringUtils.isVoid(url)) {
			this.setUrl(url);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	
	public Connection getNewConnection() throws SQLException, ClassNotFoundException {
		Class.forName(this.getDriver());
		Connection con = null;
		String key = this.getUsername() + "__" + this.getPassword() + "__" + this.getUrl();

		if (Constants.STOREELF_SQL_CONNECTIONS.containsKey(key) == false) {
			con = DriverManager.getConnection(this.getUrl(), this.getUsername(), this.getPassword());
			con.setAutoCommit(false);
			Constants.STOREELF_SQL_CONNECTIONS.put(key, con);
		} else {
			
			if(Constants.STOREELF_SQL_CONNECTIONS.get(key).isClosed()){
				con = DriverManager.getConnection(this.getUrl(), this.getUsername(), this.getPassword());
				con.setAutoCommit(false);
				Constants.STOREELF_SQL_CONNECTIONS.put(key, con);
			} 
			
			con = Constants.STOREELF_SQL_CONNECTIONS.get(key);
		}

		return con;
	}
	
	@Override
	public Sql getNewSQLInstance() throws SQLException, ClassNotFoundException {
		Sql sql = Sql.newInstance(this.getUrl(), this.getUsername(),
				this.getPassword(), this.getDriver());
		sql.getConnection().setAutoCommit(false);
		sql.execute("SET autocommit = 0");
		return sql;
	}

	@Override
	public String getSchema() {
		return schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public int getFailureCount() {
		return this.failureCount;		
	}

	@Override
	public void incrementFailureCount() {
		this.failureCount++;		
	}

	@Override
	public void resetFailureCount() {
		this.failureCount = 0;
	}
}
