package com.storeelf.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;

public class StoreElfAudit {
	static final Logger logger = Logger.getLogger(StoreElfAudit.class);

	/**
	 * Writes and audit record the the StoreElf DB
	 * 
	 * @param request
	 *            the httpServletRequest containing the response
	 * @param location
	 *            contains whether its OMS or WMOS
	 * @return the status of the WS response
	 *
	 * @throws SQLException
	 *             when there is an issue with the sql
	 * @throws IOException
	 *             when there is an I/O issue with the DB
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static void WriteAuditRecord(HttpServletRequest request, String location,
			JsonArray releases) throws SQLException {

		String tk = request.getParameter("tk");
		String order_no = request.getParameter("order_no");
		String reason_name = request.getParameter("reason_name");
		String reason_description = request.getParameter("reason_description");
		Connection con = null;
		Statement stmt = null;
		JsonElement releaseEle, orderLineEle = null;
		JsonObject releaseObj, orderLineObj = null;
		JsonArray jArray = null;
		String item_id = null;
		String shipnode_key = null;

		/*
		 * Write audit record
		 */
		logger.log(Level.INFO, tk + " is attempting to cancel order " + order_no + " in "
				+ location);
		for (int i = 0; i < releases.size(); i++) {
			releaseEle = releases.get(i);
			releaseObj = releaseEle.getAsJsonObject();
			if (location == "wmos") {
				shipnode_key = releaseObj.get("shipnode_key").toString();
			}
			jArray = releaseObj.getAsJsonArray("order_array");
			String mysql = "INSERT INTO lh_order_cancel_audit ( username, order_no, ";
			if (location == "wmos") {		
				mysql += "ship_node,";
			}
			mysql += " target_api, reason_name, reason_description, createts, items) "
					+ " VALUES (upper('" + tk + "'), '" + order_no;
			if (location == "wmos") {
				mysql += "', '" + shipnode_key;
			}
			mysql += "', " + "upper('" + location + "'), '" + reason_name + "', '"
					+ reason_description + "'" + ", NOW(), '";

			if (jArray != null) {
				for (int j = 0; j < jArray.size(); j++) {
					orderLineEle = jArray.get(j);
					orderLineObj = orderLineEle.getAsJsonObject();
					item_id = orderLineObj.get("ITEM_ID").toString();
					item_id = item_id.replace("\"", "");

					mysql += "" + item_id + "";
					if (j != (jArray.size() - 1)) {
						mysql += ", ";
					}
				}
			}

			mysql += "')";

			try {
				con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
				stmt = con.createStatement();
				con.setAutoCommit(true);
				stmt.executeUpdate(mysql);
			} catch (SQLException e) {
				logger.log(Level.ERROR,
						"SQLException Calling Insert to StoreElf Audit : SQLException", e);
			} catch (Exception e) {
				logger.log(Level.ERROR, "Exception Calling Insert to StoreElf Audit : Exception", e);
			} finally {
				if(stmt!=null){stmt.close();}
			}
		}
	}
	
	public static void createJVMRecord(String strMachine, String strPort, String strName) throws SQLException{
		Connection con = null;
		Statement stmt = null;
		String mysql = "INSERT INTO lh_jvm_conn_list ( host_name, port_num, jvm_instance) VALUES ('"+strMachine+"','"+strPort+"','"+strName+"')";
	
		try {
			con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
			stmt = con.createStatement();
			con.setAutoCommit(true);
			stmt.executeUpdate(mysql);
		} catch (SQLException e) {
			logger.log(Level.ERROR,
					"SQLException Calling createJVMRecord : SQLException", e);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Exception Calling createJVMRecord : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	
	}
	
	public static void deleteOldJVMList() throws SQLException {
		Connection con = null;
		Statement stmt = null;
		String mysql = "TRUNCATE TABLE lh_jvm_conn_list";
		
		try {
			con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
			stmt = con.createStatement();
			con.setAutoCommit(true);
			stmt.executeUpdate(mysql);
		} catch (SQLException e) {
			logger.log(Level.ERROR,
					"SQLException Calling updateJVMRecord : SQLException", e);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Exception Calling updateJVMRecord : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}
}
