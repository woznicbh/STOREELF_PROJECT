package com.storeelf.report.web.servlets.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.session.mgt.SimpleSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.security.StoreElfCache;
import com.storeelf.report.web.security.StoreElfCacheManager;

public class SessionManagementServlet {
	static final Logger logger = Logger.getLogger(SessionManagementServlet.class);
	private String defaultPage = "/security_includes/security.jsp";
	
	public SessionManagementServlet() {
		super();
	}

	public void sessionmanagement(String requestedPage, HttpServletRequest request, HttpServletResponse response) {
		String session_id = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/security_includes/session_management/sessionmanagement.jsp";

		logger.log(Level.INFO, "UserManagementServlet:userSerachResponse | "
				+ requestedPage + "|" + request.getParameter("userName") + "["
				+ "]" + "|" + request.getParameter("userID") + "["
				+ session_id + "]");

		try {
			if ("POST".equals(request.getMethod())) {
				session_id = request.getParameter("sessionID");
				
				responseWriter = response.getWriter();
				response.setContentType("application/json");

				//if (com.storeelf.util.StringUtils.isVoid(session_id)) {
					
					CachingSecurityManager	cachingSecurityManager	= (CachingSecurityManager)org.apache.shiro.SecurityUtils.getSecurityManager();
					StoreElfCacheManager<?, ?>		storeelfCacheManager		= (StoreElfCacheManager<?, ?>) cachingSecurityManager.getCacheManager();
					StoreElfCache				storeelfCache				= storeelfCacheManager.getCache("SHIRO_STOREELF_SESSION_MAP");
					JsonArray				session_list			= new JsonArray();					
					JsonObject				key_object				= new JsonObject();
					
					SimpleSession			session = null;
							
					for(Object key:storeelfCache.keys()){
						session					= (SimpleSession) storeelfCache.get(key);
						
						Calendar	session_expire = Calendar.getInstance();
									session_expire.setTime(session.getStartTimestamp());
									session_expire.add(24, Calendar.HOUR);
						
						key_object.addProperty("SESSION_ID", session.getId()+""); 
						key_object.addProperty("LAST_ACCESS", session.getLastAccessTime().toString());
						key_object.addProperty("START_DATE", session.getStartTimestamp().toString());
						key_object.addProperty("HOST", session.getHost());
						key_object.addProperty("SESSION_USER", session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY")+"");
						key_object.addProperty("EXPIRES", session_expire.getTime().toString());											
						session_list.add(key_object);
						key_object = new JsonObject();
					}
					
					Gson gson = new GsonBuilder().create();
					content = gson.toJson(session_list);
				//}
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
		catch (IOException e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);
		//}catch (ClassNotFoundException e)	{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);
		//}catch (SQLException e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException",e);
		}catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);
		}
	}
}
