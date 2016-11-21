package com.storeelf.report.web.servlets.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.report.web.servlets.security.UserManagementServlet;
import com.storeelf.util.SQLUtils;

public class UserManagementServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(UserManagementServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/security_includes/security.jsp";
	public UserManagementServlet() {
		super();
	}

public void usermanagement(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String user_name = null;
		String user_id = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/user_management/usermanagement.jsp";

		logger.log(Level.INFO, "UserManagementServlet:userSerachResponse | "
				+ requestedPage + "|" + request.getParameter("userName") + "["
				+ user_name + "]" + "|" + request.getParameter("userID") + "["
				+ user_id + "]");

		try {
			if ("POST".equals(request.getMethod())) {
				user_id = request.getParameter("userID");
				user_name = request.getParameter("userName");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(user_id)
						|| !com.storeelf.util.StringUtils.isVoid(user_name)) {
					user_name = user_name.trim();
					user_id = user_id.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					sql = " SELECT USERNAME AS USERID, FIRST_NAME, LAST_NAME, IS_ADMIN "
							+ "\n FROM STOREELF.LH_USER ";

					if (!com.storeelf.util.StringUtils.isVoid(user_id))
						sql += "WHERE USERNAME  LIKE '%" + user_id + "%'";
					else if (!com.storeelf.util.StringUtils.isVoid(user_name))
						sql += "WHERE FIRST_NAME  LIKE '%" + user_name + "%'";
					System.out.println("SQL ==> " + sql);
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils
							.getSQLResult(sql, con);
					Gson gson = new GsonBuilder().create();
					content = gson.toJson(result);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			} else {
				// assume it's GET request, load JSP
				// request.getRequestDispatcher(jsp_page).include(request,
				// response);
				request.getRequestDispatcher(
						defaultPage + "?include=" + jsp_include_page).forward(
						request, response);
			}
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.ERROR,
					"error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : SQLException",
					e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		}
	}

	public void addUser_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String firstName = null;
		String lastName = null;
		String userID = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement stmt = null;
		try {
			if ("POST".equals(request.getMethod())) {
				logger.debug("In the post Method");
				firstName = request.getParameter("firstName");
				lastName = request.getParameter("lastName");
				userID = request.getParameter("userID");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(userID) && !com.storeelf.util.StringUtils.isVoid(firstName)
						&& !com.storeelf.util.StringUtils.isVoid(lastName)) {
					userID = userID.trim();
					firstName = firstName.trim();
					lastName = lastName.trim();

					con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "\n INSERT INTO storeelf.lh_user (USERNAME,FIRST_NAME,LAST_NAME, IS_ADMIN, ACTIVE, CREATETS, MODIFYTS) "
							+ " \n VALUES ('" + userID + "','" + firstName + "','" + lastName
							+ "', 'N', 'N', NOW(), NOW())";

					logger.debug("sql query for user update is: " + sql);
					System.out.println("update sql   --> " + sql);

					stmt = con.createStatement();
					con.setAutoCommit(true);
					int count = stmt.executeUpdate(sql);
					content = String.valueOf(count);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			content = "{\"error\":\"" + e.getMessage() + "\"};";
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
			logger.log(Level.ERROR, "error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}

	public void editUserGroup_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String firstName = null;
		String lastName = null;
		String userID = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement stmt = null;
		try {
			if ("POST".equals(request.getMethod())) {
				logger.debug("In the post Method");
				firstName = request.getParameter("firstName");
				lastName = request.getParameter("lastName");
				userID = request.getParameter("userID");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(userID) && !com.storeelf.util.StringUtils.isVoid(firstName)
						&& !com.storeelf.util.StringUtils.isVoid(lastName)) {
					userID = userID.trim();
					firstName = firstName.trim();
					lastName = lastName.trim();

					con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "\n INSERT INTO storeelf.lh_user (USERNAME,FIRST_NAME,LAST_NAME, IS_ADMIN, ACTIVE, CREATETS, MODIFYTS) "
							+ " \n VALUES ('" + userID + "','" + firstName + "','" + lastName
							+ "', 'N', 'N', NOW(), NOW())";

					logger.debug("sql query for user update is: " + sql);
					System.out.println("update sql   --> " + sql);

					stmt = con.createStatement();
					con.setAutoCommit(true);
					int count = stmt.executeUpdate(sql);
					content = String.valueOf(count);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			content = "{\"error\":\"" + e.getMessage() + "\"};";
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
			logger.log(Level.ERROR, "error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}

public void groupDetails_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response){
	String 			userID		= null;
	Connection		con			= null;
	String			sql			= null;
	String			content 	= "-error-";
	PrintWriter		responseWriter		= null;
	
	try{
		if( "POST".equals(request.getMethod()) ){
			logger.debug("In the post Method");
			userID		=  request.getParameter("userID");
			responseWriter	= response.getWriter();
			response.setContentType("application/json");

			if (!com.storeelf.util.StringUtils.isVoid(userID)) {
				userID			= userID.trim();
				con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "SELECT '"+ userID +"' as username, ug.name, ug.user_group_key, ug.description, ug.default_landing_page_uri, (select count(*) from storeelf.lh_user_group_list gl where gl.user_group_key = ug.user_group_key and gl.username='"+ userID +"' ) as IS_ASSIGNED"
						  + " \n	FROM storeelf.lh_user_group ug";
					
				logger.debug("sql query is: " + sql);
				ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils
						.getSQLResult(sql, con);
				Gson gson = new GsonBuilder().create();
				content = gson.toJson(result);
			}
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
		}
	}
	catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
	catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
	catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
	catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
}

public void assignGroups(String requestedPage,
		HttpServletRequest request, HttpServletResponse response) throws SQLException {
	String username		= null;
	String user_group_key	= null;
	
	Connection con = null;
	String sql = null;
	String content = "-error-";
	PrintWriter responseWriter = null;
	Statement	stmt	= null;
	try{
		if( "POST".equals(request.getMethod()) ){
			logger.debug("In the post Method");
			
			username		=  request.getParameter("username");
			user_group_key	=  request.getParameter("user_group_key");
			
			responseWriter	= response.getWriter();
			response.setContentType("application/json");

			if( !com.storeelf.util.StringUtils.isVoid(username) && !com.storeelf.util.StringUtils.isVoid(user_group_key) ) {
				
				username		= username.trim();
				user_group_key	= user_group_key.trim();
				String currentuser	= org.apache.shiro.SecurityUtils.getSubject().getPrincipal().toString();
				
				con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
				
				sql="INSERT INTO `storeelf`.`lh_user_group_list` (`user_group_key`,`username`,`createts`,`modifyts`,`createusername`,`modifyusername`)"
						+ "VALUES ('"+user_group_key+"', '"+username+"', NOW(), NOW(), '"+currentuser+"', '"+currentuser+"')";
					
				logger.debug("sql query is: " + sql); 
				stmt	= con.createStatement();
				con.setAutoCommit(true);
				int count = stmt.executeUpdate(sql);
				
				System.out.println("Count = " + count);
				content = String.valueOf(count);
			}
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
		}
	}
	// handle EVERY exception!
	catch (IOException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : IOException", e);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
		logger.log(Level.ERROR,
				"error processing request : ClassNotFoundException", e);
	} catch (SQLException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : SQLException",
				e);
	} catch (Exception e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : Exception", e);
	} finally {
		if(stmt!=null){stmt.close();}
	}
}

public void unassignGroups(String requestedPage,
		HttpServletRequest request, HttpServletResponse response) throws SQLException {
	String username		= null;
	String user_group_key	= null;
	
	Connection con = null;
	String sql = null;
	String content = "-error-";
	PrintWriter responseWriter = null;
	Statement	stmt	= null;
	try{
		if( "POST".equals(request.getMethod()) ){
			logger.debug("In the post Method");
			
			username		=  request.getParameter("username");
			user_group_key	=  request.getParameter("user_group_key");
			
			responseWriter	= response.getWriter();
			response.setContentType("application/json");

			if( !com.storeelf.util.StringUtils.isVoid(username) && !com.storeelf.util.StringUtils.isVoid(user_group_key) ) {
				
				username		= username.trim();
				user_group_key	= user_group_key.trim();
				con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

				sql = "DELETE from storeelf.lh_user_group_list where username = '"+username+"' AND user_group_key = '"+user_group_key+"'";
					
				logger.debug("sql query is: " + sql); 
				stmt	= con.createStatement();
				con.setAutoCommit(true);
				int count = stmt.executeUpdate(sql);
				
				System.out.println("Count = " + count);
				content = String.valueOf(count);
			}
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
		}
	}
	// handle EVERY exception!
	catch (IOException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : IOException", e);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
		logger.log(Level.ERROR,
				"error processing request : ClassNotFoundException", e);
	} catch (SQLException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : SQLException",
				e);
	} catch (Exception e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : Exception", e);
	} finally {
		if(stmt!=null){stmt.close();}
	}
}


	public void deleteUser_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {

		String userID = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement stmt = null;
		try {
			if ("POST".equals(request.getMethod())) {
				logger.debug("In the post Method");
				userID = request.getParameter("userID");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(userID)) {
					userID = userID.trim();
					con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "\n DELETE FROM storeelf.lh_user WHERE username = '" + userID + "'";

					logger.debug("sql query is: " + sql);
					stmt = con.createStatement();
					con.setAutoCommit(true);
					int count = stmt.executeUpdate(sql);
					content = String.valueOf(count);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}

	}

public void getUserDropdown(String requestedPage,HttpServletRequest request, HttpServletResponse response) {
	Connection con = null;
	String sql = null;
	String content = "-error-";
	PrintWriter responseWriter = null;
	String jsp_include_page = "/security_includes/user_management/usermanagement.jsp";

	logger.log(Level.INFO, "UserManagementServlet:getUserGroupDropdownResponse | ");

	try {
		if ("POST".equals(request.getMethod())) {
			
			responseWriter = response.getWriter();
			response.setContentType("application/json");

				con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
				sql = "SELECT NAME FROM STOREELF.LH_USER_GROUP";
				ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils
						.getSQLResult(sql, con);
				Gson gson = new GsonBuilder().create();
				content = gson.toJson(result);
			
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
		} else {
			// assume it's GET request, load JSP
			// request.getRequestDispatcher(jsp_page).include(request,
			// response);
			request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
		}
	}
	// handle EVERY exception!
	catch (IOException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : IOException", e);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
		logger.log(Level.ERROR,
				"error processing request : ClassNotFoundException", e);
	} catch (SQLException e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : SQLException",
				e);
	} catch (Exception e) {
		e.printStackTrace();
		logger.log(Level.ERROR, "error processing request : Exception", e);
	}
}
}

