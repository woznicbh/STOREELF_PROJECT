package com.storeelf.report.web.servlets.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.CachingSecurityManager;

import com.google.common.reflect.ClassPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.security.StoreElfCacheManager;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

public class RoleManagementServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(UserManagementServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/security_includes/security.jsp";
	public RoleManagementServlet() {
		super();
	}

	public void rolemanagement(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String user_role = null;
		String user_id = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";

		logger.log(Level.INFO, "RoleManagementServlet:userRoleSerachResponse | "
				+ requestedPage + "|"  + "["
				+ user_role + "]" + "|" + request.getParameter("userRole") + "["
				+ user_id + "]");

		try {
			if ("POST".equals(request.getMethod())) {
				
				user_role = request.getParameter("userRole");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if ( !com.storeelf.util.StringUtils.isVoid(user_role)) {
					user_role = user_role.trim();
					
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					sql = " SELECT USER_ROLE_KEY,NAME, DESCRIPTION, CREATEUSERNAME "
							+ "\n FROM STOREELF.SE_USER_ROLES ";

					
					 if (!com.storeelf.util.StringUtils.isVoid(user_role))
						sql += "WHERE NAME  = '" + user_role + "'";

					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils
							.getSQLResult(sql, con);
					//logger.debug("result==>"+result.get(0).get("NAME").toString());
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
	
	
	public void editUserRole(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role = null;
		String role_id = null;
		Connection con = null;
		String sqlUpdate = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";
		Statement	stmt	= null;
		
		logger.log(Level.INFO, "RoleManagementServleteditUserRoleResponse | "
				+ requestedPage + "|" + request.getParameter("userRoleID") + "["
				+ user_role + "]" + "|" + request.getParameter("userRole") + "["
				+ role_id + "]");

		try {
			if ("POST".equals(request.getMethod())) {
				role_id = request.getParameter("userRoleID");
				user_role = request.getParameter("userRole");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(role_id)
						|| !com.storeelf.util.StringUtils.isVoid(user_role)) {
					user_role = user_role.trim();
					role_id = role_id.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					 sqlUpdate = " UPDATE  STOREELF.SE_USER_ROLES SET NAME='" + user_role + "'WHERE USER_ROLE_KEY  = '" + role_id + "'";
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
			//clear permissions cache after modifying permissions
			CachingSecurityManager securityManager = (org.apache.shiro.mgt.CachingSecurityManager)SecurityUtils.getSecurityManager();
			((StoreElfCacheManager<Object, Object>)securityManager.getCacheManager()).clearPermissionsCache();
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
	
	
	public void getRoleDropdown(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";

		logger.log(Level.INFO, "RoleManagementServlet:getRoleDropdownResponse | ");

		try {
			if ("POST".equals(request.getMethod())) {
				
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				
					
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					sql = "SELECT USER_ROLE_KEY,NAME FROM STOREELF.SE_USER_ROLES ";
							
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
	public void addUserRole(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String userRole = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		logger.log(Level.INFO, "RoleManagementServleteditUserRoleResponse | "
				+ requestedPage + "|" + request.getParameter("userRoleDesc") + "["
				+ userRole + "]" + "|" + request.getParameter("userRoleId") );
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				userRole	=  request.getParameter("userRole");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(userRole) && !com.storeelf.util.StringUtils.isVoid(userRole) ) {
					
					userRole	= userRole.trim();
					
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

						sql = "\n INSERT INTO storeelf.se_user_roles (name,description,servlet_uri, is_active, is_admin, permission_1, createts,modifyts,createusername,modifyusername) "
								+ " \n VALUES ('"
								
								+ userRole
								+ "','"
								+ "' '"
								+ "', '/*', 'Y','N','', NOW(), NOW(),'TKMAAXO','TKMAAXO')";
						
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
	public void deleteRole(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role = null;
		String role_id = null;
		Connection con = null;
		String sqlUpdate = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";
		Statement	stmt	= null;
		logger.log(Level.INFO, "RoleManagementServleteditUserRoleResponse | "
				+ requestedPage + "|" + request.getParameter("userRoleID") + "["
				+ user_role + "]" + "|" + request.getParameter("userRole") + "["
				+ role_id + "]");

		try {
			if ("POST".equals(request.getMethod())) {
				role_id = request.getParameter("userRoleID");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(role_id)) {
					
					role_id = role_id.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					 sqlUpdate = " DELETE FROM  STOREELF.SE_USER_ROLES WHERE USER_ROLE_KEY  = '" + role_id + "'";
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
	
	public void viewPermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";

		

		try {
			if ("POST".equals(request.getMethod())) {
				
				
				responseWriter = response.getWriter();
				response.setContentType("application/json");
				
				String user_role_key = request.getParameter("user_role_key");
					
				con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
				
				if(StringUtils.isNotBlank(user_role_key) && !StringUtils.equals(user_role_key, "null")){
					sql = "SELECT USER_PERMISSION_KEY,SECTION,FUNCTION, SERVLET_URI, (select count(*) from storeelf.se_user_roles r join storeelf.se_user_role_list rl on rl.user_role_key = r.user_role_key join storeelf.se_user_permissions up on rl.user_permission_key = up.user_permission_key where up.user_permission_key = lhup.user_permission_key  AND r.user_role_key = '"+user_role_key+"') as has_permission FROM STOREELF.SE_USER_PERMISSIONS lhup";
				}else{
					sql = " SELECT USER_PERMISSION_KEY,SECTION, FUNCTION, SERVLET_URI FROM STOREELF.SE_USER_PERMISSIONS ";	
				}				
				
				ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);
					
				JsonArray ja=new JsonArray();
				JsonObject jo =new JsonObject();
				for (HashMap<String, Object> map : result.values()) {
					
					String func = (String) map.get("FUNCTION");
					String sect = (String) map.get("SECTION");
					String serv = (String) map.get("SERVLET_URI");
					int key = (int) map.get("USER_PERMISSION_KEY");
					
					jo.addProperty("FUNCTION", func);
					jo.addProperty("SECTION", sect);
					jo.addProperty("SERVLET_URI", serv);
					jo.addProperty("USER_PERMISSION_KEY", key);
					
					ja.add(jo);
					jo =new JsonObject();
				}
				
				
				Gson gson = new GsonBuilder().create();
				content = gson.toJson(ja);
				
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
	
	public void listPermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";

		try {
			logger.trace("listPermissions ...");
			
			if ("POST".equals(request.getMethod())) {			
				
				responseWriter = response.getWriter();
				response.setContentType("application/json");
				
				//con = ReportActivator.getInstance().getConnection(Constants.STOREELF);
				
				HashMap<String, HashMap<String, ArrayList<String>>>	sections	= new HashMap<String, HashMap<String, ArrayList<String>>>();

				for(Class<?> clazz: getClasses("com.storeelf.report.web.servlets")){															
					if(StringUtils.equals(clazz.getPackage().getName(), "com.storeelf.report.web.servlets") == false){
						logger.debug("class:"+clazz.getCanonicalName());
						String section_name = StringUtils.lowerCase(clazz.getCanonicalName().split("\\.")[5]);
						String component_name = StringUtils.lowerCase(clazz.getCanonicalName().split("\\.")[6]);
							   component_name = component_name.replace("servlet", "");
							   
						if(sections.containsKey(section_name)){
							if(sections.get(section_name).containsKey(component_name)){
								for(Method responseMethod: clazz.getMethods()){
									sections.get(section_name).get(component_name).add(StringUtils.lowerCase(responseMethod.getName()));
								}
							}else{
								sections.get(section_name).put(component_name, new ArrayList<String>());
								for(Method responseMethod: clazz.getMethods()){
									sections.get(section_name).get(component_name).add(StringUtils.lowerCase(responseMethod.getName()));
								}
							}	
						}else{
							sections.put(section_name, new HashMap<String, ArrayList<String>>());
							sections.get(section_name).put(component_name, new ArrayList<String>());
							
							for(Method responseMethod: clazz.getMethods()){
								sections.get(section_name).get(component_name).add(StringUtils.lowerCase(responseMethod.getName()));
							}							
						}
					}
				}
						
				Gson gson = new GsonBuilder().create();
				content = gson.toJson(sections);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			} else {
				request.getRequestDispatcher(
						defaultPage + "?include=" + jsp_include_page).forward(
						request, response);
			}
			//try {if(con!=null){con.close();}} catch (Exception e2) {logger.error("error closing DB connection");}
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		}
	}
	

	private static Class<?>[] getClasses(String packageName) {
		logger.debug("getClasses");
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    try {
	        for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if(info.getName().startsWith(packageName)){
                	classes.add( Class.forName(info.getName()) );
                }
	        }
	    }
	    catch (IOException e) {e.printStackTrace();}
	    catch (ClassNotFoundException e) { e.printStackTrace();}
	    return classes.toArray(new Class[classes.size()]);
	}
	
	public void addPermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String section = null;
		String function = null;
		String servletUri = null;
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				section	=  request.getParameter("section");
				function	=  request.getParameter("function");
				servletUri	=  request.getParameter("servleturi");
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(section) && !com.storeelf.util.StringUtils.isVoid(function)&&!com.storeelf.util.StringUtils.isVoid(servletUri) ) {
					
					section	= section.trim();
					function=function.trim();
					servletUri=servletUri.trim();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

						sql = "\n INSERT INTO storeelf.se_user_permissions (section,function,servlet_uri,createts,modifyts) "
								+ " \n VALUES ('"
								
								+ StringUtils.lowerCase(section)
								+ "','"
								+ StringUtils.lowerCase(function)
								+ "','"
								+ servletUri
								
								+ "',NOW(), NOW())";
						
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
			
			//clear permissions cache after adding new permission
			CachingSecurityManager securityManager = (org.apache.shiro.mgt.CachingSecurityManager)SecurityUtils.getSecurityManager();
			((StoreElfCacheManager<Object, Object>)securityManager.getCacheManager()).clearPermissionsCache();
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
	
	public void assignPermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role_key		= null;
		String user_permission_key	= null;
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				user_role_key		=  request.getParameter("user_role_key");
				user_permission_key	=  request.getParameter("user_permission_key");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if( !com.storeelf.util.StringUtils.isVoid(user_role_key) && !com.storeelf.util.StringUtils.isVoid(user_permission_key) ) {
					
					user_role_key		= user_role_key.trim();
					user_permission_key	= user_permission_key.trim();
					String currentuser	= org.apache.shiro.SecurityUtils.getSubject().getPrincipal().toString();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "\n INSERT INTO storeelf.se_user_role_list (`user_role_key`, `user_permission_key`, `createts`,`modifyts`,`createusername`,`modifyusername`) "
						+ " \n VALUES ('"+ user_role_key+ "','"+ user_permission_key+ "',NOW(), NOW(),'"+currentuser+"','"+currentuser+"')";
						
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
			
			//clear permissions cache after adding new permission
			CachingSecurityManager securityManager = (org.apache.shiro.mgt.CachingSecurityManager)SecurityUtils.getSecurityManager();
			((StoreElfCacheManager<Object, Object>)securityManager.getCacheManager()).clearPermissionsCache();
		} catch (IOException e) {
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
	
	public void removePermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String user_role_key		= null;
		String user_permission_key	= null;
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		Statement	stmt	= null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				user_role_key		=  request.getParameter("user_role_key");
				user_permission_key	=  request.getParameter("user_permission_key");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if( !com.storeelf.util.StringUtils.isVoid(user_role_key) && !com.storeelf.util.StringUtils.isVoid(user_permission_key) ) {
					
					user_role_key		= user_role_key.trim();
					user_permission_key	= user_permission_key.trim();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "DELETE from storeelf.se_user_role_list where user_role_key = '"+user_role_key+"' AND user_permission_key = '"+user_permission_key+"'";
						//+ " \n VALUES ('"+ user_role_key+ "','"+ user_permission_key+ "',NOW(), NOW(),'"+currentuser+"',"+currentuser+")";
						
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
			
			//clear permissions cache after modifying permissions
			CachingSecurityManager securityManager = (org.apache.shiro.mgt.CachingSecurityManager)SecurityUtils.getSecurityManager();
			((StoreElfCacheManager<Object, Object>)securityManager.getCacheManager()).clearPermissionsCache();
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
	
	public void hasPermissions(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String user_role_key		= null;
		String user_permission_key	= null;
		
		Connection con = null;
		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				
				user_role_key		=  request.getParameter("user_role_key");
				user_permission_key	=  request.getParameter("user_permission_key");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if( !com.storeelf.util.StringUtils.isVoid(user_role_key) && !com.storeelf.util.StringUtils.isVoid(user_permission_key) ) {
					
					user_role_key		= user_role_key.trim();
					user_permission_key	=user_permission_key.trim();
					//String currentuser	= org.apache.shiro.SecurityUtils.getSubject().getPrincipal().toString();
					
					con				= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);

					sql = "select user_role_key,user_permission_key from storeelf.se_user_role_list where user_role_key = '"+user_role_key+"' AND user_permission_key = '"+user_permission_key+"'";
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

					logger.debug("sql query is: " + sql);

					Gson 		gson	= new GsonBuilder().create();
					content = gson.toJson(result);
					//content = String.valueOf(count);
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
		}
	}
	
	public void deletePermission(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) throws SQLException {
		
		String permissionkey = null;
		Connection con = null;
		String sqlUpdate = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/role_management/rolemanagement.jsp";
		Statement	stmt	= null;
		

		try {
			if ("POST".equals(request.getMethod())) {
				permissionkey = request.getParameter("permissionkey");
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(permissionkey)) {
					
					permissionkey = permissionkey.trim();
					con = ReportActivator.getInstance().getConnection(
							Constants.STOREELF_RO);

					 sqlUpdate = " DELETE FROM  STOREELF.SE_USER_PERMISSIONS WHERE USER_PERMISSION_KEY  = '" + permissionkey + "'";
					 logger.debug("sql query is: " + sqlUpdate);
//				ConcurrentHashMap<Integer, HashMap<String, Object>>; 
				
				con.setAutoCommit(true);
				
				stmt	= con.createStatement();
				int updatecount = 0;
				try{
					updatecount = stmt.executeUpdate(sqlUpdate);
				}catch(Exception e){}
				
				
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
			
			//clear permissions cache after modifying permissions
			CachingSecurityManager securityManager = (org.apache.shiro.mgt.CachingSecurityManager)SecurityUtils.getSecurityManager();
			((StoreElfCacheManager<Object, Object>)securityManager.getCacheManager()).clearPermissionsCache();
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
	
}
