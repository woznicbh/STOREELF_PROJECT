package com.storeelf.report.web.servlets.inventory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

/**
 * Servlet implementation class OrderManagementServlet
 *
 * each *Response method MUST have the following types as arguments in this
 * order: ExampleResponse(String page, HttpServletRequest rq,
 * HttpServletResponse rs)
 *
 * @author tkmagh4
 * @web.servlet name=OrderManagementServlet
 */
public class InventoryServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(InventoryServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/dashboard_includes/dashboard.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InventoryServlet() {
		super();
		
	}

    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config); //added this line then it worked
    	
    	
    }

    

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page the POST must return
	 * json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void get_inventory(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/inventory_includes/inventory/inventory.jsp";
		
		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
				
				String username 				= request.getParameter("username");
				JsonObject	inventory_object 	= new JsonObject();
				Gson 		gson				= new GsonBuilder().create();
				Connection  conRO 				= ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);
				
				ConcurrentHashMap<Integer, HashMap<String, Object>> inventoryResults = SQLUtils.getSQLResult(
						"",
						conRO);

				if (!inventoryResults.isEmpty()) {
					for (HashMap<String, Object> map : inventoryResults.values()) {
						
						//TODO add queried pieces to a jasonObject **Need to discuss proposed JSON format for what needs to be returned.**
						
						//inventory_object.addProperty("ORDER_LINE_KEY", String.valueOf(map.get("ORDER_LINE_KEY")).trim());
						
						
					}
				} else {
					
					//No results found for username **Should assume user is new and has not entered any items/inventory**
				
				}
				response_content = gson.toJson(inventory_object);
				responseWriter = response.getWriter();
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();

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

	public void set_inventory(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/inventory_includes/inventory/inventory.jsp";

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				//these are all guesses as to what the inputs would be
				String username        = request.getParameter("username");
				String category        = request.getParameter("category");
				String class1          = request.getParameter("Classification1");
				String class2          = request.getParameter("Classification2");
				String class3          = request.getParameter("Classification3");
				String class4          = request.getParameter("Classification4");
				String class5     	   = request.getParameter("Classification5");
				String classAlias1     = request.getParameter("ClassAlias1");
				String classAlias2     = request.getParameter("ClassAlias2");
				String classAlias3     = request.getParameter("ClassAlias3");
				String classAlias4     = request.getParameter("ClassAlias4");
				String classAlias5     = request.getParameter("ClassAlias5");
				
				//Not sure if we would be passing in the photo file or if the JS would take care of it and just pass a URL to the backend. 
				//May need to add code to upload it to external site
				//
				//String photoUrl1      = request.getParameter("photoUrl1");
				//String photoUrl2      = request.getParameter("photoUrl2");
				//String photoUrl3      = request.getParameter("photoUrl3");
				
				//TODO insert statement(s) to set all necessary pieces for the item
				SQLUtils.insertUpdateMySql("");
				
				
				
				responseWriter = response.getWriter();
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();

				

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
