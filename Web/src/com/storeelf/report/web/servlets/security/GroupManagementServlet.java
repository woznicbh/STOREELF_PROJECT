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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

public class GroupManagementServlet extends StoreElfHttpServlet<Object>{

	static final Logger logger = Logger.getLogger(GroupManagementServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/security_includes/security.jsp";
	public GroupManagementServlet() {
		super();
	}
	
	public void groupmanagement(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String group_name = null;
		String groupDesc = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/group_management/groupmanagement.jsp";

		
		
		try {
			if ("POST".equals(request.getMethod())) {
				groupDesc = request.getParameter("groupDesc");
				group_name = request.getParameter("groupName");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupDesc)
						|| !com.storeelf.util.StringUtils.isVoid(group_name)) {
					group_name = group_name.trim();
					groupDesc = groupDesc.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					sql = " SELECT user_group_key AS GROUPKEY, NAME, DESCRIPTION, IS_ACTIVE, DEFAULT_LANDING_PAGE_URI, CREATETS, MODIFYTS, CREATEUSERNAME, MODIFYUSERNAME "
							+ "\n FROM STOREELF.lh_user_group ";

					if (!com.storeelf.util.StringUtils.isVoid(groupDesc))
						sql += "WHERE DESCRIPTION  LIKE '%" + groupDesc + "%'";
					else if (!com.storeelf.util.StringUtils.isVoid(group_name))
						sql += "WHERE NAME  LIKE '%" + group_name + "%'";

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
	
	public void addGroup_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			groupName	= null;
		String 			groupDesc = null;
		
		Connection		con			= null;
		String			sql			= null;
		String			content 	= "-error-";
		PrintWriter		responseWriter		= null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				groupName	=  request.getParameter("groupName");
				//description	=  request.getParameter("description");
				groupDesc	=  request.getParameter("groupDesc");
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupDesc) && !com.storeelf.util.StringUtils.isVoid(groupName) ) {
					groupDesc	= groupDesc.trim();
					groupName	= groupName.trim();
					//description	= description.trim();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

						sql = "\n INSERT INTO storeelf.lh_user_group ( name, description, is_active, default_landing_page_uri, CREATETS, MODIFYTS) "
								+ " \n VALUES ('"
								
								+ groupName
								+ "','"
								+ groupDesc
								+ "', 'N', '/Logistics/DEMO', NOW(), NOW())";
						
						logger.debug("sql query is: " + sql);

//					ConcurrentHashMap<Integer, HashMap<String, Object>>; 
					stmt	= con.createStatement();
					con.setAutoCommit(true);
					//Statement	stmt2	= con.createStatement();
					int count = stmt.executeUpdate(sql);
					
					
					System.out.println("Count = " + count);
					//Gson 		gson	= new GsonBuilder().create();
							//content = gson.toJson(result);
					content = String.valueOf(count);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}
			//try {if(con!=null){con.close();}} catch (Exception e2) {logger.error("error closing DB connection");}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(stmt!=null){stmt.close();}
		}
	}
	
	
	
	
	public void delGroup_popup(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		
		String groupkey = null;
		Connection con = null;
		String sqlDel = null;
		
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/group_management/groupmanagement.jsp";
		Statement	stmt	= null;
		

		try {
			if ("POST".equals(request.getMethod())) {
				groupkey = request.getParameter("groupKey");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupkey)) {
					System.out.println("groupkey = " + groupkey);

					groupkey = groupkey.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					sqlDel = " DELETE FROM  STOREELF.LH_USER_GROUP WHERE USER_GROUP_KEY  = '" + groupkey + "'";
					 logger.debug("sql query is: " + sqlDel);
//				ConcurrentHashMap<Integer, HashMap<String, Object>>; 
				
				con.setAutoCommit(true);
				
				stmt	= con.createStatement();
				int delcount = stmt.executeUpdate(sqlDel);
				
				
				System.out.println("Count = " + delcount);
					
					
					//Gson gson = new GsonBuilder().create();
					//content = gson.toJson(count);
					content = String.valueOf(delcount);
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
			if(e.getMessage().contains("Cannot delete or update a parent row"))
			content = "{\"error\":\"" + "Cannot delete row because of dependency" + "\"};";
			else
				content = "{\"error\":\"" + e.getMessage() + "\"};";
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
			logger.log(Level.ERROR, "error processing request : SQLException",
					e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}
	
	public void editGroup_popup(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String groupkey = null;
		String groupname = null;
		String groupdesc = null;
		Connection con = null;
		String sqlUpdate = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/group_management/groupmanagement.jsp";
		Statement	stmt	= null;
		
		try {
			if ("POST".equals(request.getMethod())) {
				groupkey = request.getParameter("editGroupKey");
				groupname = request.getParameter("groupname");
				groupdesc = request.getParameter("groupdesc");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupkey)) {
					groupkey = groupkey.trim();
					
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					 sqlUpdate = " UPDATE  STOREELF.lh_user_group SET NAME='" + groupname + "', DESCRIPTION='" + groupdesc + "' WHERE USER_GROUP_KEY  = '" + groupkey + "'";
					 logger.debug("sql query is: " + sqlUpdate);
//				ConcurrentHashMap<Integer, HashMap<String, Object>>; 
				
				con.setAutoCommit(true);
				
				stmt	= con.createStatement();
				int updatecount = stmt.executeUpdate(sqlUpdate);
				
				
				System.out.println("Count = " + updatecount);
					
					
					//Gson gson = new GsonBuilder().create();
					//content = gson.toJson(count);
					content = String.valueOf(updatecount);
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
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}
	
	
	public void assignRoles(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role_key		= null;
		String user_group_key	= null;
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				user_role_key	=  request.getParameter("user_role_key");
				user_group_key	=  request.getParameter("user_group_key");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if( !com.storeelf.util.StringUtils.isVoid(user_role_key) && !com.storeelf.util.StringUtils.isVoid(user_group_key) ) {
					
					user_role_key		= user_role_key.trim();
					user_group_key		= user_group_key.trim();
					String currentuser	= org.apache.shiro.SecurityUtils.getSubject().getPrincipal().toString();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
					
					sql="INSERT INTO `storeelf`.`lh_user_group_role_list` (`user_group_key`,`user_role_key`,`createts`,`modifyts`,`createusername`,`modifyusername`)"
							+ "VALUES ('"+user_group_key+"', '"+user_role_key+"', NOW(), NOW(), '"+currentuser+"', '"+currentuser+"')";
					//sql = "UPDATE `storeelf`.`lh_user_roles` SET `user_group_key` = '123456789', `modifyts` = '2015-01-07 15:55:40', `modifyusername` = 'TKMAGH4' WHERE `user_role_key` = '987654321';";
						
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

	public void unassignRoles(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role_key		= null;
		String user_group_key	= null;
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				user_role_key	=  request.getParameter("user_role_key");
				user_group_key	=  request.getParameter("user_group_key");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if( !com.storeelf.util.StringUtils.isVoid(user_role_key) && !com.storeelf.util.StringUtils.isVoid(user_group_key) ) {
					
					user_role_key	= user_role_key.trim();
					user_group_key	= user_group_key.trim();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "DELETE from storeelf.lh_user_group_role_list where user_role_key = '"+user_role_key+"' AND user_group_key = '"+user_group_key+"'";
						
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
			//try {if(con!=null){con.close();}} catch (Exception e2) {logger.error("error closing DB connection");}
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

	
	
	public void userDetails_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String 			groupKey		= null;
		Connection		con			= null;
		String			sql			= null;
		String			content 	= "-error-";
		PrintWriter		responseWriter		= null;

		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				groupKey		=  request.getParameter("groupKey");
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupKey)) {
					groupKey			= groupKey.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

						sql = "select first_name, last_name, username from storeelf.lh_user where username in (SELECT  ugl.username"
							  + " \n	FROM storeelf.lh_user_group_list ugl, storeelf.lh_user_group ug		  "
							  + " \n	WHERE ugl.user_group_key = ug.user_group_key and ug.user_group_key = "+ groupKey +")" ;

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
	
	
	
	
	public void roleDetails_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String 			groupKey		= null;
		Connection		con			= null;
		String			sql			= null;
		String			content 	= "-error-";
		PrintWriter		responseWriter		= null;

		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				groupKey		=  request.getParameter("roleGroupKey");
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(groupKey)) {
					groupKey			= groupKey.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
					
					sql = "SELECT '"+ groupKey +"' as user_group_key, r.user_role_key, r.name, r.description as role_description, "
							+ "(select count(*) from storeelf.lh_user_group_role_list grl where grl.user_role_key = r.user_role_key and grl.user_group_key = '"+ groupKey +"') as IS_ASSIGNED"
							+ " FROM storeelf.lh_user_roles r";
						
						logger.debug("sql query is: " + sql);
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils
							.getSQLResult(sql, con);
					
					JsonArray ja=new JsonArray();
					JsonObject jo =new JsonObject();
					for (HashMap<String, Object> map : result.values()) {
						
						int key =  Integer.parseInt(String.valueOf(map.get("USER_GROUP_KEY")));
						
						String name = (String) map.get("NAME");
						String desc = (String) map.get("DESCRIPTION");
						
						
						jo.addProperty("USER_GROUP_KEY", key);
						jo.addProperty("NAME", name);
						jo.addProperty("DESCRIPTION", desc);
						
						
						ja.add(jo);
						jo =new JsonObject();
					}
					
					
					
					Gson gson = new GsonBuilder().create();
					content = gson.toJson(ja);
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
	
}
