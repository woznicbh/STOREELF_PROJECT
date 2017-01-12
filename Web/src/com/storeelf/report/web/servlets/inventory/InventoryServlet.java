package com.storeelf.report.web.servlets.inventory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

    
    public void get_categories(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/inventory_includes/inventory/inventory.jsp";

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				String username  = request.getParameter("username");
				Connection conRO = ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);
				
				ConcurrentHashMap<Integer, HashMap<String, Object>> categoryResults = SQLUtils.getSQLResult(
						"Select distinct(ic.category_name) from se_user_item_category uic, se_item_category ic "
						+" where uic.item_category_key = ic.item_category_key and uic.username = '"+username+"'",
						conRO);
								
				JsonArray category_object = new JsonArray();
				for (HashMap<String, Object> map : categoryResults.values()) {
					category_object.add(new JsonPrimitive(String.valueOf(map.get("ACTIVE")).trim()));					
				}
				
				Gson gson = new GsonBuilder().create();
				response_content = gson.toJson(category_object);
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			logger.error("error processing request : Exception", e);
		}
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
				
				String username 				= request.getParameter("USERNAME").trim();
				String category					= request.getParameter("CATEGORY").trim();
				JsonArray	inventory_array		= new JsonArray();
				JsonObject	inventory_object 	= new JsonObject();
				JsonArray	item_array			= new JsonArray();
				JsonObject  item_object			= new JsonObject();
				JsonObject  position_object		= new JsonObject();
				Gson 		gson				= new GsonBuilder().create();
				Connection  conRO 				= ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);
				
				ConcurrentHashMap<Integer, HashMap<String, Object>> inventoryResults = SQLUtils.getSQLResult(
						"Select i.item_name, i.item_desc, ic.alias_1, i.class_1, ic.alias_2, "
						+" i.class_2,ic.alias_3, i.class_3, ic.alias_4, i.class_4, ic.alias_5, "
						+" i.class_5, ii.available_qty, ii.sold_qty from se_item i, se_item_inventory ii, se_item_category ic, "
						+" se_user_item_category uic where i.username = uic.username and i.item_category_key = uic.item_category_key "
						+" and i.item_key = ii.item_key and i.item_category_key = ic.item_category_key "
						+" and i.username = '"+username+"' "
						+" and ic.category_name = '"+category+"' ",
						conRO);

				if (!inventoryResults.isEmpty()) {
					for (HashMap<String, Object> map : inventoryResults.values()) {
						
						item_object = new JsonObject();
						item_object.addProperty("ITEM_NAME", String.valueOf(map.get("ITEM_NAME")).trim());
						item_object.addProperty("ITEM_DESC", String.valueOf(map.get("ITEM_DESC")).trim());
						item_object.addProperty("AVAILABLE_QTY", String.valueOf(map.get("AVAILABLE_QTY")).trim());
						item_object.addProperty("SOLD_QTY", String.valueOf(map.get("SOLD_QTY")).trim());
						item_object.addProperty(String.valueOf(map.get("ALIAS_1")).trim().toUpperCase(), String.valueOf(map.get("CLASS_1")).trim());
						item_object.addProperty(String.valueOf(map.get("ALIAS_2")).trim().toUpperCase(), String.valueOf(map.get("CLASS_2")).trim());
						item_object.addProperty(String.valueOf(map.get("ALIAS_3")).trim().toUpperCase(), String.valueOf(map.get("CLASS_3")).trim());
						item_object.addProperty(String.valueOf(map.get("ALIAS_4")).trim().toUpperCase(), String.valueOf(map.get("CLASS_4")).trim());
						item_object.addProperty(String.valueOf(map.get("ALIAS_5")).trim().toUpperCase(), String.valueOf(map.get("CLASS_5")).trim());
						
						item_array.add(item_object);
						
					}
					
					ConcurrentHashMap<Integer, HashMap<String, Object>> positionResults = SQLUtils.getSQLResult(
							"Select ic.alias_1, uic.class_1_position, ic.alias_2, uic.class_2_position, ic.alias_3, "
							+" uic.class_3_position, ic.alias_4, uic.class_4_position, ic.alias_5, uic.class_5_position "
							+" from se_user_item_category uic, se_item_category ic "
							+" where uic.item_category_key = ic.item_category_key "
							+" and uic.username='"+username+"' "
							+" and ic.category_name='"+category+"'",
							conRO);
					
					for (HashMap<String, Object> map : positionResults.values()) {
						
						position_object = new JsonObject();
						position_object.addProperty(String.valueOf(map.get("ALIAS_1")).trim().toUpperCase(), String.valueOf(map.get("CLASS_1_POSITION")).trim());
						position_object.addProperty(String.valueOf(map.get("ALIAS_2")).trim().toUpperCase(), String.valueOf(map.get("CLASS_2_POSITION")).trim());
						position_object.addProperty(String.valueOf(map.get("ALIAS_3")).trim().toUpperCase(), String.valueOf(map.get("CLASS_3_POSITION")).trim());
						position_object.addProperty(String.valueOf(map.get("ALIAS_4")).trim().toUpperCase(), String.valueOf(map.get("CLASS_4_POSITION")).trim());
						position_object.addProperty(String.valueOf(map.get("ALIAS_5")).trim().toUpperCase(), String.valueOf(map.get("CLASS_5_POSITION")).trim());
						inventory_object.add("POSITION", position_object);
					}
					
					inventory_object.add("ITEM_ARRAY", item_array);
					
				} else {
					
					//No results found for username **Should assume user is new and has not entered any items/inventory**
				
				}
				
				response_content = gson.toJson(inventory_array);
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
