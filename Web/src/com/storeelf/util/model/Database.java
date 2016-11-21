package com.storeelf.util.model;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * Interface Name: com.storeelf.tools.model.Database <BR/>
 * Purpose: This interface is used as a template to create connections to
 * different databases <BR/>
 * Creation Date: Sep 7, 2011 9:41:47 AM
 */
public interface Database {

	/**
	 * 
	 * Purpose: This method returns a valid connection to the database. Must be
	 * closed after use.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getNewConnection() throws SQLException,
			ClassNotFoundException;

	/**
	 * 
	 * <B>Purpose:</B> This method returns a valid SQL Instance for the
	 * database.
	 * 
	 * @return SQL Instance
	 */
	public groovy.sql.Sql getNewSQLInstance() throws SQLException,
			ClassNotFoundException;

	public String getUsername();

	public void setUsername(String username);

	public String getPassword();

	public void setPassword(String password);

	public String getHost();

	public void setHost(String host);

	public String getPort();

	public void setPort(String port);

	public String getDb();

	public void setDb(String db);

	public void setUrl(String url);

	public String getUrl();

	public String getDriver();

	public void setDriver(String driver);

	public String getSchema();

	public void setSchema(String schema);
	
	public int getFailureCount();
	
	public void incrementFailureCount();
	
	public void resetFailureCount();
}
