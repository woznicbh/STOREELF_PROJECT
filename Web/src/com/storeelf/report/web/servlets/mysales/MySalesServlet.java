package com.storeelf.report.web.servlets.mysales;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

public class MySalesServlet extends StoreElfHttpServlet<Object> {

	static final Logger logger = Logger.getLogger(MySalesServlet.class);
	private String defaultPage = "/mysales_includes/mysales.jsp";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void scrape_facebook(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/mysales_includes/mysales/mysales.jsp";

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
				Gson 		gson				= new GsonBuilder().create();
				String username = request.getParameter("username");
				String fbUrl = request.getParameter("fbUrl");
				String fbToken = "";
				
				Connection conRO = ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);
				ConcurrentHashMap<Integer, HashMap<String, Object>> usernameHM = null;
				
				//Need to validate that user has access to scrape FB feature
				usernameHM = SQLUtils.getSQLResult("",conRO);

				if (!usernameHM.isEmpty()) {
					logger.error("User "+username+" does not have access to this feature");
					
					responseWriter = response.getWriter();
					responseWriter.write("Error");
					responseWriter.flush();
					responseWriter.close();
					
				} else {

					for (HashMap<String, Object> map : usernameHM.values()) {
						
						fbToken =  String.valueOf(map.get("FACEBOOK_TOKEN"));
						
					}
					
					//this is where we scrape the data
					
					//Next we would format this data the way we want
					
					//return the data to the front end.
					response_content = gson.toJson(null);
					responseWriter = response.getWriter();
					responseWriter.write(response_content);
					responseWriter.flush();
					responseWriter.close();

				}

			} else {
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}

}
