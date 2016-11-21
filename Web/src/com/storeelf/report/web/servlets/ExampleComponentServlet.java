package com.storeelf.report.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.SQLUtils;

/**
 * Servlet implementation class ExampleComponentServlet
 * 
 * each method MUST have the following types as arguments in this order:
 * example_module(String page, HttpServletRequest rq, HttpServletResponse rs)
 * 
 * @author tkmagh4
 * @web.servlet 
 *   name=ExampleComponentServlet
 */
public class ExampleComponentServlet extends StoreElfHttpServlet {
	static final Logger			logger				= Logger.getLogger(ExampleComponentServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				error				= "error-response";
	private String				defaultPage			= "/example_includes/example.jsp";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExampleComponentServlet() {
        super();
    }
    	
	/**
	 * Each method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 * 
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void example_module(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			search_value		= null;
		Connection		connection			= null;
		String			sql_query			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/example_includes/example_component/"+requestedPage+".jsp";
		
		logger.log(Level.INFO, "ExampleComponentServlet:example_moduleResponse | " + requestedPage + "|" + request.getParameter("parameterValue"));
		
		try{				
			if(StringUtils.equals(request.getMethod(),"POST")){
				
				//handle request parameters here
				search_value	= request.getParameter("parameterName");
				responseWriter	= response.getWriter();
				
				//content type MUST be "application/json"
				response.setContentType("application/json");
				
				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					search_value	= search_value.trim();
					connection		= ReportActivator.getInstance().getConnection(Constants.OMS);
					
					sql_query =  "SELECT * FROM user.table_name WHERE column = 'value'";
					
					//place resultset into HashMap
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql_query, connection);
					
					//Convert HashMap into json object
					Gson 		gson				= new GsonBuilder().create();
								response_content	= gson.toJson(result);
				}
				
				//write content to response writer, flush before closing ... trust me on this one ... 
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();	
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
}
