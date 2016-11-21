package com.storeelf.report.web.servlets.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.SQLConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.ExampleComponentServlet;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;

public class SourcingDetailServlet<T> extends StoreElfHttpServlet<T> {
	static final Logger			logger				= Logger.getLogger(ExampleComponentServlet.class);
	private static final long	serialVersionUID	= 1L;
	//private String				error				= "error-response";
	private String				defaultPage			= "/utility_includes/utility.jsp";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SourcingDetailServlet() {
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
	 * @throws SQLException 
	 */
	public void sourcing_unit_capacity(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/sourcing_detail/"+requestedPage+".jsp";
		
		String exportResult		= request.getParameter("export_result_csv");
		String ajax_request		= request.getParameter("ajax_request");
		
		boolean isPost = StringUtils.equals(request.getMethod(),"POST");
		boolean	isGet = StringUtils.equals(request.getMethod(),"GET");
		boolean isExport = org.apache.commons.lang.StringUtils.equals(exportResult, "true");
		boolean isAjax	= org.apache.commons.lang.StringUtils.equals(ajax_request, "true");
		ResultSet result = null;
		Connection	connGIV	= null;
		try{ 
			if(		isPost ||  (isGet && (isExport||isAjax))		){
				boolean triggerDownload			= (org.apache.commons.lang.StringUtils.equals(exportResult, "true")) ? true : false;

				//handle request parameters here
				String strStoreNo	   = request.getParameter("store_number");
				String strMultiStoreNo = request.getParameter("field_value");
				String strNodeType	   = request.getParameter("node_type");
				String strZeroCap	   = request.getParameter("zero_cap");
				
				responseWriter		   = response.getWriter();

				response.setContentType("application/json");


					strStoreNo		= strStoreNo.trim();
					strMultiStoreNo = strMultiStoreNo.replaceAll("\\s+","");
					strMultiStoreNo = strMultiStoreNo.replaceAll(",","','");
					System.out.println(strMultiStoreNo);
					JsonObject 			rootObject						= new JsonObject();

					JsonArray			storeCapacityRootArray			= new JsonArray();
					JsonObject 			storeCapacityObject				= new JsonObject();

					String exportResultDataHeaders	= "";
					String exportResultData			= "";
					//-----------------------------------------------

					
					/*if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
						response.addHeader("Content-Type", "text/csv");
						response.addHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
						response.setHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");

						response_content = (String) getSessionAttribute("store_capacity_details_csv");
					}
					else {*/

						
						String				sqlCapacity 	= null;
						
							sqlCapacity = " SELECT ";
								
							if("true".equals(strZeroCap)){
										sqlCapacity +=	 "distinct(pool.NODE_KEY) as STORE, ";
									}
							else{
								sqlCapacity += "pool.NODE_KEY as STORE, ";
								}
									
							sqlCapacity += SQLConstants.SQL_MAP.get(SQLConstants.SD_UNIT_CAPACITY_GIV_SQL);
									if(!("".equals(strMultiStoreNo)) && !("true".equals(strZeroCap))){
										sqlCapacity+="AND pool.NODE_KEY in ('"+strMultiStoreNo+"')";
									};
									if(!("ALL - ALL".equals(strStoreNo)) && ("".equals(strMultiStoreNo)) && !("undefined".equals(strStoreNo)) && !("true".equals(strZeroCap))){
										sqlCapacity+= " AND pool.NODE_KEY = '"+strStoreNo+"'";
									}
									if("SFS".equals(strNodeType)){
										sqlCapacity+= " AND pool.resource_pool_id not like '%-PICK%'";
									}
									if("BOPUS".equals(strNodeType)){
										sqlCapacity+= " AND pool.resource_pool_id like '%-PICK%'";
									}
									if("true".equals(strZeroCap)){
										sqlCapacity+= " AND (c.sunday_capacity = '0' "
												+ "OR c.monday_capacity 	  = '0' "
												+ "OR c.tuesday_capacity 	  = '0' "
												+ "OR c.wednesday_capacity 	  = '0' "
												+ "OR c.thursday_capacity 	  = '0' "
												+ "OR c.friday_capacity 	  = '0' "
												+ "OR c.saturday_capacity 	  = '0') ";
									}

									System.out.println(sqlCapacity);
									

						connGIV          = ReportActivator.getInstance().getConnection(Constants.GIV);

						result = connGIV.createStatement().executeQuery(sqlCapacity);						

						try {
							while(result.next()) {
								
								 
								if(exportResultDataHeaders.length()==0){

									//TODO write csv headers
									for(int i=0;i<result.getMetaData().getColumnCount();i++){
										exportResultDataHeaders += result.getMetaData().getColumnName(i+1)+",";
										if((i+1)==result.getMetaData().getColumnCount()-1) exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length()-1);
									}
								}
								
								exportResultData += result.getString("STORE").trim() + ",";
								exportResultData += result.getString("SOURCING_TYPE") + ",";
								exportResultData += result.getString("SUNDAY_CAPACITY") + ",";
								exportResultData += result.getString("MONDAY_CAPACITY") + ",";
								exportResultData += result.getString("TUESDAY_CAPACITY") + ",";
								exportResultData += result.getString("WEDNESDAY_CAPACITY") + ",";
								exportResultData += result.getString("THURSDAY_CAPACITY") + ",";
								exportResultData += result.getString("FRIDAY_CAPACITY") + ",";
								exportResultData += result.getString("SATURDAY_CAPACITY") + ",";
								
								exportResultData += "\n";
								
								//add the object to an array and refresh object
								storeCapacityRootArray.add(storeCapacityObject);
								storeCapacityObject = new JsonObject();
								 
								
								 if(triggerDownload==false){
										storeCapacityObject.addProperty("STORE", 				Integer.parseInt(result.getString("STORE").trim()));
										storeCapacityObject.addProperty("SOURCING_TYPE", 		result.getString("SOURCING_TYPE"));
										storeCapacityObject.addProperty("MONDAY_CAPACITY", 		Integer.parseInt(result.getString("MONDAY_CAPACITY")));
										storeCapacityObject.addProperty("TUESDAY_CAPACITY", 	Integer.parseInt(result.getString("TUESDAY_CAPACITY")));
										storeCapacityObject.addProperty("WEDNESDAY_CAPACITY", 	Integer.parseInt(result.getString("WEDNESDAY_CAPACITY")));
										storeCapacityObject.addProperty("THURSDAY_CAPACITY", 	Integer.parseInt(result.getString("THURSDAY_CAPACITY")));
										storeCapacityObject.addProperty("FRIDAY_CAPACITY", 		Integer.parseInt(result.getString("FRIDAY_CAPACITY")));
										storeCapacityObject.addProperty("SATURDAY_CAPACITY", 	Integer.parseInt(result.getString("SATURDAY_CAPACITY")));
										storeCapacityObject.addProperty("SUNDAY_CAPACITY", 		Integer.parseInt(result.getString("SUNDAY_CAPACITY")));

										//add the object to an array and refresh object
										storeCapacityRootArray.add(storeCapacityObject);
										storeCapacityObject = new JsonObject();
									}else{
										if(triggerDownload==false){
											response.setContentType("text/csv");
											response.addHeader("Content-Type", "text/csv");
											response.addHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
											response.setHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
											triggerDownload=true;
										}
										responseWriter.write(exportResultData);
										responseWriter.flush();
										exportResultData = "";
									}	   
								
							}

							rootObject.add("store_capacity_results", storeCapacityRootArray);
							
							
						} catch (Exception e) {
							e.printStackTrace();
						} 

					//setSessionAttribute("store_capacity_details_csv",  exportResultDataHeaders+"\n"+exportResultData);
					
					//Convert HashMap into json object
					Gson 		gson				= new GsonBuilder().create();
					
					 if(triggerDownload==false){
						 	response_content	= gson.toJson(rootObject);
						 	responseWriter.write(response_content);
						}else{
							response.setContentType("text/csv");
							response.addHeader("Content-Type", "text/csv");
							response.addHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
							response.setHeader("Content-Disposition", "attachment;filename=\"StoreCapacityDetails_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
						}
				//}

				//write content to response writer, flush before closing ... trust me on this one ...
				
				responseWriter.flush();
				responseWriter.close();
				
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
			
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.error("error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally {
			if(result!=null){result.close();}
			if(connGIV!=null){connGIV.close();}
		}
	}

	/**
	 * Each method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public void get_list_of_stores(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray stores = new JsonArray();
			JsonObject store = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.OMS);
			resultset = (connection.prepareStatement(
					"SELECT SHIPNODE_KEY, DESCRIPTION FROM yfs_ship_node WHERE node_type = 'STORE' order by shipnode_key"))
							.executeQuery();

			// stores.add("Store No");

			while (resultset.next()) {
				store.addProperty("store_number", Integer.parseInt(resultset.getString(1).trim()));
				store.addProperty("store_description", resultset.getString(2).trim());

				stores.add(store);
				store = new JsonObject();
			}

			// store.addProperty("store_number", "ALL");
			// store.addProperty("store_description", "ALL");
			stores.add(store);

			store = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(stores);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();

			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void sourcing_rule_details(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/sourcing_detail/" + requestedPage + ".jsp";
		String sql = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				String fulfillment = request.getParameter("fulfillment_type");
				String region = request.getParameter("region_name");
				String item1 = request.getParameter("item_class_1");
				String item2 = request.getParameter("item_class_2");
				String item3 = request.getParameter("item_class_3");
				String item4 = request.getParameter("item_class_4");
				String item5 = request.getParameter("item_class_5");
				// String distribution =
				// request.getParameter("distribution_rule_id");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(fulfillment) || !com.storeelf.util.StringUtils.isVoid(region)
						|| !com.storeelf.util.StringUtils.isVoid(item1) || !com.storeelf.util.StringUtils.isVoid(item3)
						|| !com.storeelf.util.StringUtils.isVoid(item3)) {
					fulfillment = fulfillment.trim();
					region = region.trim();
					item1 = item1.trim();
					item2 = item2.trim();
					item3 = item3.trim();

					if (fulfillment.equals("undefined") || fulfillment.equals("ALL")) {
						fulfillment = "";
					}
					if (region.equals("undefined") || region.equals("ALL")) {
						region = "";
					}
					if (item1.equals("undefined")) {
						item1 = "";
					}
					if (item2.equals("undefined")) {
						item2 = "";
					}
					if (item3.equals("undefined")) {
						item3 = "";
					}
					if (item4.equals("undefined")) {
						item4 = "";
					}
					if (item5.equals("undefined")) {
						item5 = "";
					}

					logger.log(Level.INFO, "fulfillment | " + fulfillment);
					logger.log(Level.INFO, "region | " + region);

					conn = ReportActivator.getInstance().getConnection(Constants.GIV);

					sql = SQLConstants.SQL_MAP.get(SQLConstants.SD_RULE_DETAIL_GIV_SQL);
					// only fulfillment selected
					if (StringUtils.isNotBlank(fulfillment)) {
						sql = sql + "\n  AND hdr.fulfillment_type = '" + fulfillment + "'";
						// fulfillment && item1
					}
					if (StringUtils.isNotBlank(region)) {
						sql = sql

								+ "\n  AND region.region_name = '" + region + "'";
						// only region selected
					}
					if (StringUtils.isNotBlank(item1)) {
						if (!item1.equals("ALL")) {
							sql = sql + "\n  AND hdr.item_classification = '" + item1 + "'";
						}
					} else if (StringUtils.isBlank(item1)) {
						sql = sql + "\n  AND hdr.item_classification LIKE ' '";
					}
					if (StringUtils.isNotBlank(item2)) {
						if (!item2.equals("ALL")) {
							sql = sql + "\n  AND hdr.item_classification2 = '" + item2 + "'";
						}
					} else if (StringUtils.isBlank(item2)) {
						sql = sql + "\n  AND hdr.item_classification2 LIKE ' '";

					}
					if (StringUtils.isNotBlank(item3)) {

						if (!item3.equals("ALL")) {
							sql = sql + "\n  AND hdr.item_classification3 = '" + item3 + "'";
						}
					} else if (StringUtils.isBlank(item3)) {
						sql = sql + "\n  AND hdr.item_classification3 LIKE ' '";
					}
					if (StringUtils.isNotBlank(item4)) {

						if (!item4.equals("ALL")) {
							sql = sql + "\n  AND hdr.item_classification4 = '" + item4 + "'";
						}
					} else if (StringUtils.isBlank(item4)) {
						sql = sql + "\n  AND hdr.item_classification4 LIKE ' '";
					}
					if (StringUtils.isNotBlank(item5)) {

						if (!item5.equals("ALL")) {
							sql = sql + "\n  AND hdr.item_classification5 = '" + item5 + "'";
						}
					} else if (StringUtils.isBlank(item5)) {
						sql = sql + "\n  AND hdr.item_classification5 LIKE ' '";
					}
					if (StringUtils.isBlank(region)) {

						sql += SQLConstants.SQL_MAP.get(SQLConstants.SD_RULE_DETAIL_GIV_SQL2);

						if (StringUtils.isNotBlank(fulfillment)) {
							sql = sql + "\n AND hdr.fulfillment_type = '" + fulfillment + "'";
						}
						if (StringUtils.isNotBlank(item1)) {
							if (!item1.equals("ALL")) {
								sql = sql + "\n  AND hdr.item_classification = '" + item1 + "'";
							}
						} else if (StringUtils.isBlank(item1)) {
							sql = sql + "\n  AND hdr.item_classification LIKE ' '";
						}
						if (StringUtils.isNotBlank(item2)) {
							if (!item2.equals("ALL")) {
								sql = sql + "\n  AND hdr.item_classification2 = '" + item2 + "'";
							}
						} else if (StringUtils.isBlank(item2)) {
							sql = sql + "\n  AND hdr.item_classification2 LIKE ' '";

						}
						if (StringUtils.isNotBlank(item3)) {

							if (!item3.equals("ALL")) {
								sql = sql + "\n  AND hdr.item_classification3 = '" + item3 + "'";
							}
						} else if (StringUtils.isBlank(item3)) {
							sql = sql + "\n  AND hdr.item_classification3 LIKE ' '";
						}
						if (StringUtils.isNotBlank(item4)) {

							if (!item4.equals("ALL")) {
								sql = sql + "\n  AND hdr.item_classification4 = '" + item4 + "'";
							}
						} else if (StringUtils.isBlank(item4)) {
							sql = sql + "\n  AND hdr.item_classification4 LIKE ' '";
						}
						if (StringUtils.isNotBlank(item5)) {

							if (!item5.equals("ALL")) {
								sql = sql + "\n  AND hdr.item_classification5 = '" + item5 + "'";
							}
						} else if (StringUtils.isBlank(item5)) {
							sql = sql + "\n  AND hdr.item_classification5 LIKE ' '";
						}
					}

					// System.out.println(sql);

					result = conn.createStatement().executeQuery(sql);
					logger.log(Level.INFO, "SourcingDetailServlet | RuleResult" + sql);

					JsonArray root = new JsonArray();
					JsonObject line = new JsonObject();

					while (result.next()) {
						String FULFILLMENT_TYPE = result.getString("FULFILLMENT_TYPE");
						String REGION_NAME = result.getString("REGION_NAME");
						String ITEM_CLASSIFICATION = result.getString("ITEM_CLASSIFICATION");
						String ITEM_CLASSIFICATION2 = result.getString("ITEM_CLASSIFICATION2");
						String ITEM_CLASSIFICATION3 = result.getString("ITEM_CLASSIFICATION3");
						String ITEM_CLASSIFICATION4 = result.getString("ITEM_CLASSIFICATION4");
						String ITEM_CLASSIFICATION5 = result.getString("ITEM_CLASSIFICATION5");

						line.addProperty("FULFILLMENT_TYPE", FULFILLMENT_TYPE);
						line.addProperty("REGION_NAME", REGION_NAME);
						line.addProperty("ITEM_CLASSIFICATION", ITEM_CLASSIFICATION);
						line.addProperty("ITEM_CLASSIFICATION2", ITEM_CLASSIFICATION2);
						line.addProperty("ITEM_CLASSIFICATION3", ITEM_CLASSIFICATION3);
						line.addProperty("ITEM_CLASSIFICATION4", ITEM_CLASSIFICATION4);
						line.addProperty("ITEM_CLASSIFICATION5", ITEM_CLASSIFICATION5);

						root.add(line);
						line = new JsonObject();
					}

					// HashMap<Integer, HashMap<String, Object>> result =
					// SQLUtils.getSQLResult(sql, conn);

					Gson gson = new GsonBuilder().create();
					response_content = gson.toJson(root);
				}

				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(conn!=null){conn.close();}
		}
	}

	public void get_fulfillment(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray fil_list = new JsonArray();
			JsonObject fulfillment = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT FULFILLMENT_TYPE FROM yfs_sourcing_rule_hdr ORDER BY FULFILLMENT_TYPE"))
							.executeQuery();

			while (resultset.next()) {
				fulfillment.addProperty("FULFILLMENT_TYPE", resultset.getString(1).trim());

				fil_list.add(fulfillment);
				fulfillment = new JsonObject();
			}

			fulfillment = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(fil_list);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void get_region(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;
		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray reg_list = new JsonArray();
			JsonObject region = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"select reg.region_name from yfs_region reg join yfs_region_schema sch on reg.region_schema_key = sch.region_schema_key where reg.region_schema_key = 'ALL_US' and reg.region_level_name = 'Region'"))
							.executeQuery();

			while (resultset.next()) {
				region.addProperty("REGION_NAME", resultset.getString(1).trim());

				reg_list.add(region);
				region = new JsonObject();
			}

			region = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(reg_list);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void item_class_1(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray item_class_1 = new JsonArray();
			JsonObject item = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT item_classification from yfs_sourcing_rule_hdr ORDER BY item_classification"))
							.executeQuery();

			while (resultset.next()) {
				item.addProperty("ITEM_CLASSIFICATION", resultset.getString(1).trim());

				item_class_1.add(item);
				item = new JsonObject();
			}

			item = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(item_class_1);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
			
	}

	public void item_class_2(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray item_class_2 = new JsonArray();
			JsonObject item = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.OMS);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT item_classification2 from yfs_sourcing_rule_hdr ORDER BY item_classification2"))
							.executeQuery();

			while (resultset.next()) {
				item.addProperty("ITEM_CLASSIFICATION2", resultset.getString(1).trim());

				item_class_2.add(item);
				item = new JsonObject();
			}
			item = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(item_class_2);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally{
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void item_class_3(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;
		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray item_list_3 = new JsonArray();
			JsonObject item = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT item_classification3 from yfs_sourcing_rule_hdr ORDER BY item_classification3"))
							.executeQuery();

			while (resultset.next()) {
				item.addProperty("ITEM_CLASSIFICATION3", resultset.getString(1).trim());

				item_list_3.add(item);
				item = new JsonObject();
			}
			item = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(item_list_3);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void item_class_4(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray item_list_4 = new JsonArray();
			JsonObject item = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT item_classification4 from yfs_sourcing_rule_hdr ORDER BY item_classification4"))
							.executeQuery();

			while (resultset.next()) {
				item.addProperty("ITEM_CLASSIFICATION4", resultset.getString(1).trim());

				item_list_4.add(item);
				item = new JsonObject();
			}
			item = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(item_list_4);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void item_class_5(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray item_list_5 = new JsonArray();
			JsonObject item = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT item_classification5 from yfs_sourcing_rule_hdr ORDER BY item_classification5"))
							.executeQuery();

			while (resultset.next()) {
				item.addProperty("ITEM_CLASSIFICATION5", resultset.getString(1).trim());

				item_list_5.add(item);
				item = new JsonObject();
			}
			item = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(item_list_5);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void get_rule(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray rule_list = new JsonArray();
			JsonObject rule = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT DISTINCT DISTRIBUTION_RULE_ID FROM yfs_sourcing_rule_dtl ORDER BY DISTRIBUTION_RULE_ID"))
							.executeQuery();

			while (resultset.next()) {
				rule.addProperty("DISTRIBUTION_RULE_ID", resultset.getString(1).trim());

				rule_list.add(rule);
				rule = new JsonObject();
			}

			rule.addProperty("DISTRIBUTION_RULE_ID", "ALL");
			rule_list.add(rule);

			rule = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(rule_list);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void get_region_details(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String sql = null;
		String dist_string = null;
		Connection conn = null;
		ResultSet result = null;
		String distribution_name = request.getParameter("distribution_name");

		try {

			// handle request parameters here
			responseWriter = response.getWriter();
			JsonObject root = new JsonObject();
			JsonArray detail_list = new JsonArray();
			JsonObject detail = new JsonObject();

			JsonArray node_list = new JsonArray();
			JsonObject node = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			conn = ReportActivator.getInstance().getConnection(Constants.GIV);

			sql = " SELECT dr.distribution_rule_id, dr.priority, dr.shipnode_key, "
					+ " TO_CHAR(dr.effective_start_date, 'DD-MON-YYYY') as EFFECTIVE_START_DATE, "
					+ " TO_CHAR(dr.effective_end_date, 'DD-MON-YYYY') as EFFECTIVE_END_DATE, "
					+ " sn.LATITUDE, sn.LONGITUDE,pi.city, pi.state "
					+ " FROM yfs_item_ship_node dr, yfs_ship_node sn, YFS_PERSON_INFO pi "
					+ " WHERE dr.distribution_rule_id ='" + distribution_name + "'"
					+ " and dr.shipnode_key = sn.shipnode_key " + " and sn.SHIP_NODE_ADDRESS_KEY = pi.PERSON_INFO_KEY "
					+ " ORDER BY dr.distribution_rule_id, dr.priority, dr.shipnode_key";

			result = conn.createStatement().executeQuery(sql);
			ArrayList<String> dist_nodes = new ArrayList<String>();

			while (result.next()) {

				Integer PRIORITY = Integer.parseInt(result.getString("PRIORITY").trim());
				Integer SHIPNODE_KEY = Integer.parseInt(result.getString("SHIPNODE_KEY").trim());
				String EFFECTIVE_START_DATE = result.getString("EFFECTIVE_START_DATE");
				String EFFECTIVE_END_DATE = result.getString("EFFECTIVE_END_DATE");

				detail.addProperty("PRIORITY", PRIORITY);
				detail.addProperty("SHIPNODE_KEY", SHIPNODE_KEY);
				detail.addProperty("EFFECTIVE_START_DATE", EFFECTIVE_START_DATE);
				detail.addProperty("EFFECTIVE_END_DATE", EFFECTIVE_END_DATE);

				String CITY = result.getString("CITY");
				String STATE = result.getString("STATE");

				detail.addProperty("CITY", CITY);
				detail.addProperty("STATE", STATE);

				dist_nodes.add("'" + SHIPNODE_KEY + "'");
				dist_string = dist_nodes.toString();
				dist_string = dist_string.substring(1, dist_string.length() - 1);
				detail_list.add(detail);
				detail = new JsonObject();

				String SHIP_NODE = result.getString("SHIPNODE_KEY");
				String LATITUDE = result.getString("LATITUDE");
				String LONGITUDE = result.getString("LONGITUDE");

				if (StringUtils.isNotBlank(LATITUDE) || StringUtils.isNotBlank(LONGITUDE)) {

					node.addProperty("SHIP_NODE", SHIP_NODE);
					node.addProperty("LATITUDE", Double.parseDouble(LATITUDE));
					node.addProperty("LONGITUDE", Double.parseDouble(LONGITUDE));

					node_list.add(node);
					node = new JsonObject();
				}

			}

			detail = null;
			node = null;

			// detail_list.add(node_list);
			root.add("detail_list", detail_list);
			root.add("node_list", node_list);

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(root);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(conn!=null){conn.close();}
		}
	}

	public void rule_order(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {

		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/sourcing_detail/" + requestedPage + ".jsp";
		String sql = null;
		Connection conn = null;
		ResultSet result = null;
		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				String fulfillment = request.getParameter("fulfillment_type");
				String region = request.getParameter("region_name");
				String item1 = request.getParameter("item_classification");
				String item2 = request.getParameter("item_classification2");
				String item3 = request.getParameter("item_classification3");
				String item4 = request.getParameter("item_classification4");
				String item5 = request.getParameter("item_classification5");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (fulfillment.equals("undefined")) {
					fulfillment = " ";
				}
				if (region.equals("undefined")) {
					region = " ";
				}
				if (item1.equals("undefined")) {
					item1 = " ";
				}
				if (item2.equals("undefined")) {
					item2 = " ";
				}
				if (item3.equals("undefined")) {
					item3 = " ";
				}
				if (item4.equals("undefined")) {
					item4 = " ";
				}
				if (item5.equals("undefined")) {
					item5 = " ";
				}

				// System.out.println("here");

				logger.log(Level.INFO, "fulfillment | " + fulfillment);
				logger.log(Level.INFO, "region | " + region);
				logger.log(Level.INFO, "item_class1 | " + item3);
				logger.log(Level.INFO, "item_class2 | " + item2);
				logger.log(Level.INFO, "item_class3 | " + item3);
				logger.log(Level.INFO, "item_class4 | " + item4);
				logger.log(Level.INFO, "item_class5 | " + item5);

				conn = ReportActivator.getInstance().getConnection(Constants.GIV);

				sql =

						"\n  SELECT  case when dtl.DISTRIBUTION_RULE_ID = ' ' then dtl.TEMPLATE_TYPE"
								+ "\n	  else dtl.DISTRIBUTION_RULE_ID end as DISTRIBUTION_RULE_ID," + "\n  dtl.SEQ_NO"
								+ "\n  FROM yfs_sourcing_rule_hdr hdr," + "\n  yfs_sourcing_rule_dtl dtl,"
								+ "\n  yfs_region region" + "\n  WHERE hdr.region_key = region.region_key"
								+ "\n  AND hdr.sourcing_rule_hdr_key = dtl.sourcing_rule_hdr_key"
								+ "\n  AND hdr.FULFILLMENT_TYPE = '" + fulfillment + "'"
								+ "\n  AND region.REGION_NAME = '" + region + "'"
								+ "\n  AND hdr.ITEM_CLASSIFICATION = '" + item1 + "'"
								+ "\n  AND hdr.ITEM_CLASSIFICATION2 = '" + item2 + "'"
								+ "\n  AND hdr.ITEM_CLASSIFICATION3 = '" + item3 + "'"
								+ "\n  AND hdr.ITEM_CLASSIFICATION4 = '" + item4 + "'"
								+ "\n  AND hdr.ITEM_CLASSIFICATION5 = '" + item5 + "'";

				if (StringUtils.isBlank(region)) {
					sql = sql

							+ "\n  UNION "
							+ "\n  SELECT  case when dtl.DISTRIBUTION_RULE_ID = ' ' then dtl.TEMPLATE_TYPE"
							+ "\n	  else dtl.DISTRIBUTION_RULE_ID end as DISTRIBUTION_RULE_ID," + "\n  dtl.SEQ_NO"
							+ "\n  FROM yfs_sourcing_rule_hdr hdr," + "\n  yfs_sourcing_rule_dtl dtl"
							+ "\n  WHERE hdr.sourcing_rule_hdr_key = dtl.sourcing_rule_hdr_key"
							+ "\n  AND hdr.FULFILLMENT_TYPE = '" + fulfillment + "'"
							+ "\n  AND hdr.ITEM_CLASSIFICATION = '" + item1 + "'"
							+ "\n  AND hdr.ITEM_CLASSIFICATION2 = '" + item2 + "'"
							+ "\n  AND hdr.ITEM_CLASSIFICATION3 = '" + item3 + "'"
							+ "\n  AND hdr.ITEM_CLASSIFICATION4 = '" + item4 + "'"
							+ "\n  AND hdr.ITEM_CLASSIFICATION5 = '" + item5 + "'";
				}

				// System.out.println(sql);

				result = conn.createStatement().executeQuery(sql);
				// logger.log(Level.INFO, "SourcingDetailServlet | RuleResult" +
				// sql);

				JsonArray root = new JsonArray();
				JsonObject line = new JsonObject();

				while (result.next()) {
					String DISTRIBUTION_RULE_ID = result.getString("DISTRIBUTION_RULE_ID");
					String SEQ_NO = result.getString("SEQ_NO");

					line.addProperty("DISTRIBUTION_RULE_ID", DISTRIBUTION_RULE_ID);
					line.addProperty("SEQ_NO", Integer.parseInt(SEQ_NO));

					root.add(line);
					line = new JsonObject();
				}

				// HashMap<Integer, HashMap<String, Object>> result =
				// SQLUtils.getSQLResult(sql, conn);

				Gson gson = new GsonBuilder().create();
				response_content = gson.toJson(root);

				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(conn!=null){conn.close();}
		}
	}

	public void get_region_by_state(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray reg_list = new JsonArray();
			JsonObject region = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"with Region_Details as (select reg.* " + "\n from yfs_region reg, yfs_region_schema sch"
							+ "\n where reg.region_schema_key = sch.region_schema_key"
							+ "\n and sch.region_schema_name = 'ALL_US'" + "\n and reg.region_level_name = 'Region')"
							+ "\n select region.region_name,state.region_name as state "
							+ "\n from Region_Details region, yfs_region state"
							+ "\n where region.region_key = state.parent_region_key "
							+ "\n and state.region_name not in ('AA', 'AE', 'AF', 'AP', 'DC')"
							+ "\n order by region.region_name")).executeQuery();

			int i = 0;

			while (resultset.next()) {

				region.addProperty("REGION_NAME", resultset.getString(1).trim());
				String reg_name = resultset.getString(1).trim();

				if (reg_name.equals("MidAtlantic")) {
					i = 5;
				} else if (reg_name.equals("Midwest")) {
					i = 10;
				} else if (reg_name.equals("Mountain")) {
					i = 15;
				} else if (reg_name.equals("NewEngland")) {
					i = 20;
				} else if (reg_name.equals("Northeast")) {
					i = 25;
				} else if (reg_name.equals("Northwest")) {
					i = 30;
				} else if (reg_name.equals("Plains")) {
					i = 35;
				} else if (reg_name.equals("South")) {
					i = 40;
				} else if (reg_name.equals("Southeast")) {
					i = 45;
				} else if (reg_name.equals("Southwest")) {
					i = 50;
				} else if (reg_name.equals("Wisconsin")) {
					i = 55;
				}

				region.addProperty("VALUE", i);
				region.addProperty("STATE", resultset.getString(2).trim());

				reg_list.add(region);
				region = new JsonObject();
			}

			region = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(reg_list);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void get_node_locations(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Connection connection = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		ResultSet resultset = null;

		try {

			// handle request parameters here
			responseWriter = response.getWriter();

			JsonArray loc_list = new JsonArray();
			JsonObject loc = new JsonObject();

			// content type MUST be "application/json"
			response.setContentType("application/json");

			connection = ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset = (connection.prepareStatement(
					"SELECT ship_node, latitude, longitude from yfs_ship_node where node_type in ('DC','Store')"))
							.executeQuery();

			while (resultset.next()) {

				String SHIP_NODE = resultset.getString("SHIP_NODE");
				String LATITUDE = resultset.getString("LATITUDE");
				String LONGITUDE = resultset.getString("LONGITUDE");

				if (StringUtils.isNotBlank(LATITUDE) || StringUtils.isNotBlank(LONGITUDE)) {

					loc.addProperty("SHIP_NODE", SHIP_NODE);
					loc.addProperty("LATITUDE", Double.parseDouble(LATITUDE));
					loc.addProperty("LONGITUDE", Double.parseDouble(LONGITUDE));

					loc_list.add(loc);
					loc = new JsonObject();
				}
			}

			loc = null;

			// Convert HashMap into json object
			Gson gson = new GsonBuilder().create();
			response_content = gson.toJson(loc_list);

			// write content to response writer, flush before closing ... trust
			// me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();
			
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(resultset!=null){resultset.close();}
			if (connection!=null){connection.close();}
		}
	}

	public void distribution_group_details(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String sql = null;
		String jsp_include_page = "/utility_includes/sourcing_detail/" + requestedPage + ".jsp";
		Connection conn = null;
		ResultSet result = null;
		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
				String search_value_dist_group = request.getParameter("dist_region");
				String search_value_ship_node = request.getParameter("ship_node");

				if (search_value_ship_node.equals("undefined")) {
					search_value_ship_node = "";
				}
				if (search_value_dist_group.equals("undefined") || search_value_dist_group.equals("ALL")) {
					search_value_dist_group = "";

				}

				// handle request parameters here
				responseWriter = response.getWriter();

				JsonArray detail_list = new JsonArray();
				JsonObject detail = new JsonObject();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				conn = ReportActivator.getInstance().getConnection(Constants.GIV);

				sql = SQLConstants.SQL_MAP.get(SQLConstants.SD_DIST_GROUP_GIV_SQL);
				if (StringUtils.isNotBlank(search_value_dist_group)) {
					sql = sql + " 	AND dtl.distribution_rule_id = '" + search_value_dist_group.trim() + "'";

				}
				if (StringUtils.isNotBlank(search_value_ship_node)) {
					sql = sql + " 	AND sn.ship_node = '" + search_value_ship_node.trim() + "'";
				}

				sql = sql + " 	ORDER BY dtl.distribution_rule_id";

				result = conn.createStatement().executeQuery(sql);
				logger.info(sql);

				while (result.next()) {
					String DISTRIBUTION_RULE_ID = result.getString("DISTRIBUTION_RULE_ID");

					detail.addProperty("DISTRIBUTION_RULE_ID", DISTRIBUTION_RULE_ID);

					detail_list.add(detail);
					detail = new JsonObject();
				}

				detail = null;

				// Convert HashMap into json object
				Gson gson = new GsonBuilder().create();
				response_content = gson.toJson(detail_list);

				// write content to response writer, flush before closing ...
				// trust me on this one ...
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(conn!=null){conn.close();}
		}
	}

	public void get_group_details(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String sql = null;
		String jsp_include_page = "/utility_includes/sourcing_detail/" + requestedPage + ".jsp";
		Connection conn = null;
		ResultSet result = null;

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				responseWriter = response.getWriter();

				JsonArray detail_list = new JsonArray();
				JsonObject detail = new JsonObject();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				conn = ReportActivator.getInstance().getConnection(Constants.GIV);

				sql = SQLConstants.SQL_MAP.get(SQLConstants.SD_GROUP_DETAIL_GIV_SQL);

				result = conn.createStatement().executeQuery(sql);

				while (result.next()) {
					String DISTRIBUTION_RULE_ID = result.getString("DISTRIBUTION_RULE_ID");

					detail.addProperty("DISTRIBUTION_RULE_ID", DISTRIBUTION_RULE_ID);

					detail_list.add(detail);
					detail = new JsonObject();
				}

				detail = null;

				// Convert HashMap into json object
				Gson gson = new GsonBuilder().create();
				response_content = gson.toJson(detail_list);

				// write content to response writer, flush before closing ...
				// trust me on this one ...
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(conn!=null){conn.close();}
		}
	}

}
