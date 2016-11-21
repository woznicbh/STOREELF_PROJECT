package com.storeelf.report.web.servlets._internal_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
import com.storeelf.report.web.model.StoreElfSearchHistoryEntityModel;
import com.storeelf.report.web.security.StoreElfCache;
import com.storeelf.report.web.security.StoreElfCacheManager;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.report.web.servlets.dashboard.StoreElfComMetricsServlet;

public class UserSessionServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(StoreElfComMetricsServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/dashboard_includes/dashboard.jsp";

	public UserSessionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void search_history(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			request_uri			= null;
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
//		String			jsp_include_page		= "/utility_includes/order_management/order.jsp";
		Gson 			gson	         		= new GsonBuilder().create();
		JsonArray		rootReturn  			= new JsonArray();
		JsonObject		searchEntityJsonObject	= new JsonObject();
		JsonObject		searchEntityParamJsonObject	= new JsonObject();

		StoreElfSearchHistoryEntityModel searchHistoryEntityModel = null; 

		//logger.log(Level.INFO, "OrderManagementServlet:orderResponse | " + requestedPage + "|" + request.getParameter("orderNumber"));

		try{
			if( "POST".equals(request.getMethod()) ){
				request_uri	= request.getParameter("request_uri");

				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(request_uri)) {
					ConcurrentHashMap<String, StoreElfSearchHistoryEntityModel> search_history = (ConcurrentHashMap<String, StoreElfSearchHistoryEntityModel>) getSessionAttribute("search_history");

					searchHistoryEntityModel = search_history.get(request_uri);

					//iterate search entry map
					for(Entry<String, HashMap<String, String>> e : searchHistoryEntityModel.getSearchMap().entrySet()){
						searchEntityJsonObject.addProperty("SEARCH_DATE", e.getKey().toString());

						//iterate search parameters
						for(Entry<String, String> kv : e.getValue().entrySet()){
							searchEntityParamJsonObject.addProperty(kv.getKey(), kv.getValue());
						}
						searchEntityJsonObject.add("SEARCH_PARAMETERS", searchEntityParamJsonObject);
						rootReturn.add(searchEntityJsonObject);

						searchEntityJsonObject = new JsonObject();
						searchEntityParamJsonObject = new JsonObject();
					}

					//finally prep the rootReturn object to be sent to the page
					content = gson.toJson(rootReturn);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				responseWriter	= response.getWriter();
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				responseWriter.write("error, use POST instead");
				//request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				responseWriter.flush();
				responseWriter.close();
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
	
	public void get_session(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/_internal_api_includes/UserSession/get_session.jsp";
		String			session_id				= "";
		
		try{
			if( "GET".equals(request.getMethod()) ){
				session_id			= request.getParameter("session_id");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				try{
					
					CachingSecurityManager	cachingSecurityManager	= (CachingSecurityManager)org.apache.shiro.SecurityUtils.getSecurityManager();
					StoreElfCacheManager<?, ?>		storeelfCacheManager		= (StoreElfCacheManager<?, ?>) cachingSecurityManager.getCacheManager();
					StoreElfCache				storeelfCache				= storeelfCacheManager.getCache("SHIRO_STOREELF_SESSION_MAP");
					
					SimpleSession			session					= (SimpleSession) storeelfCache.get(session_id);
					
					if(session!=null){
						content = session.getHost()
								+"\n getTimeout:"+session.getTimeout()
								+"\n getAttributeKeys:"+session.getAttributeKeys().size()								
								;
						for(Entry<Object, Object> e :session.getAttributes().entrySet()){
							content += "\n\t getAttribute[K/V]:["+e.getKey()+"/"+e.getValue()+"]";
						}
					content		+="\n getId:"+session.getId()
								+"\n getLastAccessTime:"+session.getLastAccessTime().toString()
								+"\n getStartTimestamp:"+session.getStartTimestamp()
								+"\n getStopTimestamp:"+session.getStopTimestamp()
								;
					}
					
				}catch(Exception e){
					logger.error("error encrypting value provided",e);
				}

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}



}
