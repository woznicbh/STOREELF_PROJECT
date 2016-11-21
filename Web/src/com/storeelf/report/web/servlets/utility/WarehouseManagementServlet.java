package com.storeelf.report.web.servlets.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

/**
 * Servlet implementation class WarehouseManagementServlet
 */
public class WarehouseManagementServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(WarehouseManagementServlet.class);
	private static final long	serialVersionUID	= 1L;
	private Connection		con		= null;
	private String				defaultPage			= "/utility_includes/utility.jsp";
	private int dbConnectionRetryAttemptCount	= 0;

    /**
     * @see StoreElfHttpServlet#StoreElfHttpServlet()
     */
    public WarehouseManagementServlet() {
        super();
    }

    public void proship_container_lookup(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			search_value		= null;
		Connection		connection			= null;
		String			sql_query			= null;
		JsonObject		root_return			= new JsonObject();
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/"+requestedPage+".jsp";
        
		logger.debug("WarehouseManagementServlet:proship_container_lookup | " + requestedPage + "|" + request.getParameter("parameterName"));

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				search_value	= request.getParameter("paramOne");
				responseWriter	= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");
				
				

				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					search_value	= search_value.trim();
					connection		= ReportActivator.getInstance().getConnection(Constants.PROSHIP);

					sql_query =  " select "  
							+ " shipping_container_id as CONTAINER_ID, Shipper_name as SHIPPER_NAME, SHIPVIA, TRACKING_NUMBER, DIVERT_LANE, "
							+ " SERVER_NAME, PNA_AUTOBagger_Station as BAGGER,  " 
							+ " TO_CHAR(creation_datetime,'MM/DD/YYYY HH:MI:SS AM') as CREATETS, " 
							+ " TO_CHAR(confirmation_scan_datetime,'MM/DD/YYYY HH:MI:SS AM') as CONFIRMTS, "
							+ " TO_CHAR(shipvia_sent_datetime,'MM/DD/YYYY HH:MI:SS AM') as SHIPVIATS, " 
							+ " sent_8001_message as SENT_8001 " 
							+ " from proship_container_history ch where ch.shipping_container_id = '"
							+ search_value +"' "
							+ " order by ch.creation_datetime";
					
					//place resultset into HashMap
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql_query, connection);
					
					//iterate through our result set and build our JSON
					for(HashMap<String, Object> map : result.values()){
						root_return.addProperty("CONTAINER_ID", String.valueOf(map.get("CONTAINER_ID")).trim());
						root_return.addProperty("SHIPPER_NAME", String.valueOf(map.get("SHIPPER_NAME")).trim());
						root_return.addProperty("SHIPVIA", String.valueOf(String.valueOf(map.get("SHIPVIA")).trim()));
						root_return.addProperty("TRACKING_NUMBER", String.valueOf(map.get("TRACKING_NUMBER")).trim());
						root_return.addProperty("DIVERT_LANE", String.valueOf(map.get("DIVERT_LANE")).trim());
						root_return.addProperty("SERVER_NAME", String.valueOf(map.get("SERVER_NAME")).trim());
						root_return.addProperty("BAGGER", String.valueOf(map.get("BAGGER")).trim());
						root_return.addProperty("CREATETS", String.valueOf(map.get("CREATETS")).trim());
						root_return.addProperty("CONFIRMTS", String.valueOf(map.get("CONFIRMTS")).trim());
						root_return.addProperty("SHIPVIATS", String.valueOf(map.get("SHIPVIATS")).trim());
						root_return.addProperty("SENT_8001", String.valueOf(map.get("SENT_8001")).trim());
					
					}
					
					//Convert HashMap into json object
					Gson 		gson				= new GsonBuilder().create();
								response_content	= gson.toJson(root_return);
				}

				//write content to response writer, flush before closing ... trust me on this one ...
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page)
						.forward(request, response);
			}
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
    
    public void ship_via(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			return_type			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/ship_via.jsp";


		logger.debug("WarehouseManagementServlet:ship_via | " + requestedPage);

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){
				Gson 		gson				= new GsonBuilder().create();

				/*String ship_via_code	= request.getParameter("ship_via_code");
				String ship_via_desc	= request.getParameter("ship_via_desc");
				String CSRT				= request.getParameter("CSRT");*/

				//handle request parameters here
				return_type		= request.getParameter("return_type");
				responseWriter	= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(return_type)) {
					return_type	= return_type.trim();

					if("auto_complete".equals(return_type)){
						HashMap<String, Object> result = new HashMap<String, Object>();
						Set<String> codes = new HashSet<String>();
						Set<String> descs = new HashSet<String>();
						HashMap<String, String> list = new HashMap<String, String>();

						Connection con_efc1 = ReportActivator.getInstance().getConnection(Constants.EFC1);
						Connection con_efc2 = ReportActivator.getInstance().getConnection(Constants.EFC2);
						Connection con_efc3 = ReportActivator.getInstance().getConnection(Constants.EFC3);
						Connection con_efc4 = ReportActivator.getInstance().getConnection(Constants.EFC4);
						
						String sql = "SELECT UNIQUE DESCRIPTION, SHIP_VIA FROM WMOS_EFC.SHIP_VIA ORDER BY DESCRIPTION";
						ConcurrentHashMap<Integer, HashMap<String, Object>> efc1Results = SQLUtils.getSQLResult(sql, con_efc1);
						ConcurrentHashMap<Integer, HashMap<String, Object>> efc2Results = SQLUtils.getSQLResult(sql, con_efc2);
						ConcurrentHashMap<Integer, HashMap<String, Object>> efc3Results = SQLUtils.getSQLResult(sql, con_efc3);
						ConcurrentHashMap<Integer, HashMap<String, Object>> efc4Results = SQLUtils.getSQLResult(sql, con_efc4);
						String code = "";
						String desc = "";

						for(HashMap<String, Object> map : efc1Results.values()){
							code = (String)map.get("SHIP_VIA");
							desc = (String)map.get("DESCRIPTION");
							codes.add(code);
							descs.add(desc);
							list.put(desc, code);
						}
						
						for(HashMap<String, Object> map : efc2Results.values()){
							code = (String)map.get("SHIP_VIA");
							desc = (String)map.get("DESCRIPTION");
							codes.add(code);
							descs.add(desc);
							list.put(desc, code);
						}
						
						for(HashMap<String, Object> map : efc3Results.values()){
							code = (String)map.get("SHIP_VIA");
							desc = (String)map.get("DESCRIPTION");
							codes.add(code);
							descs.add(desc);
							list.put(desc, code);
						}
						
						for(HashMap<String, Object> map : efc4Results.values()){
							code = (String)map.get("SHIP_VIA");
							desc = (String)map.get("DESCRIPTION");
							codes.add(code);
							descs.add(desc);
							list.put(desc, code);
						}

						result.put("codes", codes);
						result.put("list", list);
						result.put("descs", descs);

						//Convert HashMap into json object
						response_content	= gson.toJson(result);
					}else{

						//#####################################################################################
						JsonObject EFC_Object = new JsonObject();
						JsonObject rootObject = new JsonObject();

						Connection con_efc1 = null, con_efc2 = null, con_efc3 = null, con_efc4 = null;
						try{
							String shipvia = request.getParameter("ship_via_code").toUpperCase();
							String sql = null;

							if (!com.storeelf.util.StringUtils.isVoid(shipvia) && shipvia.matches("[A-Za-z0-9\\s]+")){
								con_efc1 = ReportActivator.getInstance().getConnection(Constants.EFC1);
								con_efc2 = ReportActivator.getInstance().getConnection(Constants.EFC2);
								con_efc3 = ReportActivator.getInstance().getConnection(Constants.EFC3);
								con_efc4 = ReportActivator.getInstance().getConnection(Constants.EFC4);

								Connection[] efc_list = new Connection[]{con_efc1, con_efc2, con_efc3, con_efc4}; 

								sql = "select ship_via, description, carrier_id, service_level_indicator, label_type"
										+ "\n from wmos_efc.ship_via"
										+ "\n where ship_via = '" + shipvia + "'";
								HashMap<String, Object> row = null;

								for(int i = 0; i < 4; i++){ //for each efc
									try{
										//can do .get(1) since SHIP_VIA is a database index and will just return one row
										row = SQLUtils.getSQLResult(sql, efc_list[i]).get(1);
										if(row == null){
											logger.error("No data available for EFC"+ (i + 1) +" and ship via \""+shipvia+"\"");
										}else{
											EFC_Object.addProperty("EFC"				, "EFC"+ ( i + 1 ));
											EFC_Object.addProperty("DESCRIPTION"		,row.get("DESCRIPTION")+"");
											EFC_Object.addProperty("SHIP_VIA"           ,row.get("SHIP_VIA")+"");
											EFC_Object.addProperty("CARRIER_ID"           ,row.get("CARRIER_ID")+"");
											//EFC_Object.addProperty("MOD_DATE_TIME"     ,row.get("MOD_DATE_TIME")+"");
											//EFC_Object.addProperty("USER_ID"           ,row.get("USER_ID")+"");
											//EFC_Object.addProperty("CREATE_DATE_TIME"  ,row.get("CREATE_DATE_TIME")+"");
											EFC_Object.addProperty("LABEL_TYPE"          ,row.get("LABEL_TYPE")+"");
											EFC_Object.addProperty("SERVICE_LEVEL_INDICATOR"         ,row.get("SERVICE_LEVEL_INDICATOR")+"");

											rootObject.add(i+"", EFC_Object);
										}
										EFC_Object = new JsonObject();
									}catch(Exception ex){
										logger.error("Error while pulling data for EFC"+ (i + 1) +" and ship via \""+shipvia+"\"");
									}
								}
							}else{
								logger.error("Please enter a valid ship via code with alphanumeric characters");
							}
						}catch(Exception ex){
							logger.error("error", ex);
						}finally{
							if(con_efc1!=null) con_efc1.close();
							if(con_efc2!=null) con_efc2.close();
							if(con_efc3!=null) con_efc3.close();
							if(con_efc4!=null) con_efc4.close();
						}
						response_content = gson.toJson(rootObject);
					}
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
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.error("error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
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
	public void distribution_order_detail(String requestedPage, HttpServletRequest request, HttpServletResponse response){
/*		String			search_value		= null;
		Connection		connection			= null;
		String			sql_query			= null;*/
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/distribution_order_detail.jsp";

		Gson 			gson				= new GsonBuilder().create();

		logger.debug("WarehouseManagementServlet:pickticket_detail | " + requestedPage);

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				responseWriter	= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");

					Connection con = null;
					try{
						String pktCntrl = request.getParameter("pickticket_control_number").toUpperCase().trim();
						String efcNo    = request.getParameter("efc_number").toUpperCase().trim();

						/* CONSTANTS SECTION */
						/* In order to organize data returned by these 13 queries, I have created
						a small constants section to define specialized headers and css classes */

						//sqlids for each of the queries needed to gather information
						String[] queries = new String[] {
							Constants.UTIL_FRM_PKTHDR,
							Constants.UTIL_FRM_PKTDTL,
							Constants.UTIL_FRM_CRTNHDR,
							Constants.UTIL_FRM_CRTNDTL,
							Constants.UTIL_FRM_CRTNTYP,
							Constants.UTIL_FRM_MANHDR,
							Constants.UTIL_FRM_MANDTL,
							Constants.UTIL_FRM_OPKTHDR,
							Constants.UTIL_FRM_OPKTDTL,
							Constants.UTIL_FRM_OCRTNHDR,
							Constants.UTIL_FRM_OCRTNDTL,
							Constants.UTIL_FRM_CNCLS,
							Constants.UTIL_FRM_INV_WM };

						//Used during iteration to be able to logically group information
						HashMap<String, String> cssClassMap = new HashMap<String, String>();
						cssClassMap.put(Constants.UTIL_FRM_PKTHDR, "pkt");
						cssClassMap.put(Constants.UTIL_FRM_PKTDTL, "pkt");
						cssClassMap.put(Constants.UTIL_FRM_CRTNHDR, "carton");
						cssClassMap.put(Constants.UTIL_FRM_CRTNDTL, "carton");
						cssClassMap.put(Constants.UTIL_FRM_CRTNTYP, "carton");
						cssClassMap.put(Constants.UTIL_FRM_MANHDR, "manifest");
						cssClassMap.put(Constants.UTIL_FRM_MANDTL, "manifest");
						cssClassMap.put(Constants.UTIL_FRM_OPKTHDR, "outputpkt");
						cssClassMap.put(Constants.UTIL_FRM_OPKTDTL, "outputpkt");
						cssClassMap.put(Constants.UTIL_FRM_OCRTNHDR, "outputcrtn");
						cssClassMap.put(Constants.UTIL_FRM_OCRTNDTL, "outputcrtn");
						cssClassMap.put(Constants.UTIL_FRM_CNCLS, "cancels");
						cssClassMap.put(Constants.UTIL_FRM_INV_WM, "invoices");

						//A map to check if a header has already been written for a section
						HashMap<String, Boolean> headerMap = new HashMap<String, Boolean>();
						headerMap.put("pkt", false);
						headerMap.put("carton", false);
						headerMap.put("manifest", false);
						headerMap.put("outputpkt", false);
						headerMap.put("outputcrtn", false);
						headerMap.put("cancels", false);
						headerMap.put("invoices", false);

						//The actual header data that is needed to create the accordion
						HashMap<String, String> headerDataMap = new HashMap<String, String>();
						headerDataMap.put("pkt", "Pickticket Header and Detail Information");
						headerDataMap.put("carton", "Carton Header, Detail, and Type Information");
						headerDataMap.put("manifest", "Manifest Information");
						headerDataMap.put("outputpkt", "Output Pickticket Header and Detail Information");
						headerDataMap.put("outputcrtn", "Output Carton Header and Detail Information");
						headerDataMap.put("cancels", "Cancelations Information");
						headerDataMap.put("invoices", "Invoice Information");

						/* END CONSTANTS SECTION */

						JsonObject root					= new JsonObject();
						//JsonArray json_table_root		= new JsonArray();
						JsonObject json_cell_data		= new JsonObject();
						JsonObject json_row_data		= new JsonObject();

						//Data Validation
						if (!com.storeelf.util.StringUtils.isVoid(pktCntrl) && pktCntrl.matches("[A-Za-z0-9\\s]+")
							 && !com.storeelf.util.StringUtils.isVoid(efcNo) && efcNo.matches("EFC[1-4]")){
							if(efcNo.equals("EFC1")){
								con = ReportActivator.getInstance().getConnection(Constants.EFC1);
							}else if(efcNo.equals("EFC2")){
								con = ReportActivator.getInstance().getConnection(Constants.EFC2);
							}else if(efcNo.equals("EFC3")){
								con = ReportActivator.getInstance().getConnection(Constants.EFC3);
							}else if(efcNo.equals("EFC4")){
								con = ReportActivator.getInstance().getConnection(Constants.EFC4);
							}

							logger.debug("Results for Pickticket "+pktCntrl +" at " + efcNo);

							HashMap<Integer, String> columns = null;

							for(String sqlid : queries){
								try{

									//Writing the headers
									if(!headerMap.get(cssClassMap.get(sqlid))){
										//if the header is not yet writen
										headerMap.put(cssClassMap.get(sqlid), true); //header written

									}//if

									//write the column header data from SQL_COL_TO_DESC
									columns = Constants.FRM_RES_FLDS.get(sqlid);

									// Add the search parameter to the query and poll the db
									ConcurrentHashMap<Integer, HashMap<String, Object>> rows = SQLUtils.getSQLResult(String.format(Constants.SQL_MAP.get(sqlid), pktCntrl), con);

									logger.debug("### row size "+rows.size());

									String columnName = "";

									if(rows != null && rows.keySet().size() > 0){
										logger.log(Level.DEBUG, " rows.keySet().size():: " + rows.keySet().size()  + " | " + sqlid);

										for(int i = 0; i < rows.keySet().size(); i++){
											logger.log(Level.DEBUG, " row: " + i + " of "+ rows.keySet().size()  + " | " + sqlid);

											for(int j = 0; j < columns.keySet().size(); j++){
											//index the ith row with each of the j column names to pull the query data
												columnName = columns.get(j + 1);


												//remove DB function from name
												if(StringUtils.equals(columnName, "SUM(OCD.UNITS_PAKD)")){
													columnName = "UNITS_PAKD";
												}

												json_cell_data.addProperty(columnName , rows.get(i + 1).get(columns.get(j + 1))+"");
											}//for columns

											json_row_data.add(i+"", json_cell_data);

											json_cell_data	= new JsonObject();
										}//for rows
									}else{
										logger.debug("No data available for "+pktCntrl +" at " + efcNo);
									}
									root.add(sqlid, json_row_data);
									json_row_data	= new JsonObject();
								}catch(Exception ex){
									logger.error("The folling sqlid errored out " + sqlid, ex);
								}
							}//for sqlid

							response_content	= gson.toJson(root);
						}else{
							logger.debug("Please Enter a valid PickTicket Ctrl number");
						}
					}catch(Exception ex){
						logger.error("Connection Error", ex);
					}finally{
						if(con != null) con.close();
					}

				//write content to response writer, flush before closing ... trust me on this one ...
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}

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
	public void collate_print_times(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/collate_print_times.jsp";

		logger.debug("WarehouseManagementServlet:collate_print_times | " + requestedPage + "|" + request.getParameter("parameterValue"));

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				String			req_efc_number		= request.getParameter("efc_number");
				String			req_print_area		= request.getParameter("print_area");
				String			req_print_server	= request.getParameter("print_server");
				String			req_print_requester	= request.getParameter("print_requester");
				String			req_time_breakdown	= request.getParameter("time_breakdown");
				String			req_from_date		= request.getParameter("from_date");
				String			req_until_date		= request.getParameter("until_date");

				responseWriter	= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");

				if (
						com.storeelf.util.StringUtils.isNotBlank(req_efc_number)		&&
						com.storeelf.util.StringUtils.isNotBlank(req_print_area)		&&
						com.storeelf.util.StringUtils.isNotBlank(req_time_breakdown)	&&
						com.storeelf.util.StringUtils.isNotBlank(req_from_date)		&&
						com.storeelf.util.StringUtils.isNotBlank(req_until_date)
					) {
					req_efc_number	= req_efc_number.trim();

					//Convert HashMap into json object
					Gson 		gson				= new GsonBuilder().create();

					Connection con = null;
					try{
						JsonObject root 		= new JsonObject();
						JsonArray  data_root	= new JsonArray();
						JsonObject row	 		= new JsonObject();

						//validate print area
						if (
								(com.storeelf.util.StringUtils.isBlank(req_print_area) || req_print_area.matches("(PACKOUT|WAVE|PEAK|SORTER|PUTWALL|ALL)"))

								&& com.storeelf.util.StringUtils.isNotBlank(req_efc_number) && req_efc_number.matches("EFC[1-4]")

								// Next regex matches dates ie: 07/05/2013
								&& com.storeelf.util.StringUtils.isNotBlank(req_from_date)	&& req_from_date.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d")
								&& com.storeelf.util.StringUtils.isNotBlank(req_until_date)	&& req_until_date.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d")
								&& com.storeelf.util.StringUtils.isNotBlank(req_time_breakdown) && req_time_breakdown.matches("(DAILY|HOURLY)"
								)
							){

								req_print_area		= req_print_area		== null ? "" : req_print_area.toUpperCase().trim();
								req_efc_number		= req_efc_number		== null ? "" : req_efc_number.toUpperCase().trim();
								req_print_server	= req_print_server		== null ? "" : req_print_server.toUpperCase().trim();
								req_print_requester = req_print_requester   == null ? "" : req_print_requester.toUpperCase().trim();
								req_from_date		= req_from_date			== null ? "" : req_from_date.trim();
								req_until_date		= req_until_date		== null ? "" : req_until_date.trim();

								if(req_efc_number.equals("EFC1")){
									con = ReportActivator.getInstance().getConnection(Constants.EFC1);
								}else if(req_efc_number.equals("EFC2")){
									con = ReportActivator.getInstance().getConnection(Constants.EFC2);								
								}else if(req_efc_number.equals("EFC3")){
									con = ReportActivator.getInstance().getConnection(Constants.EFC3);
								}else if(req_efc_number.equals("EFC4")){
									con = ReportActivator.getInstance().getConnection(Constants.EFC4);
								}

								String sql = buildQuery(
										req_efc_number,
										req_time_breakdown,
										req_print_area,
										(req_print_requester != null && req_print_requester.equals("TRUE")),
										req_from_date,
										req_until_date,
										(req_print_server != null && req_print_server.equals("TRUE")));

								ConcurrentHashMap<Integer, HashMap<String, Object>> rows = SQLUtils.getSQLResult(sql, con);

							root.addProperty("user_selection_timebreak", ( req_time_breakdown.equals("HOURLY") ? "Hour (CST)" : "Date" ));

							root.addProperty("user_requested_print_server",		com.storeelf.util.StringUtils.equals(req_print_server, "TRUE"));
							root.addProperty("user_requested_print_requester",	com.storeelf.util.StringUtils.equals(req_print_requester, "TRUE"));
							root.addProperty("response_size",					rows.keySet().size());

							if(rows != null && rows.keySet().size() > 0){
								HashMap<String, Object> rowData = null;

								for(int i = 0; i < rows.keySet().size(); i++){
									rowData = rows.get((i + 1));

									row.addProperty("DTE", rowData.get("DTE")+"");
									if(com.storeelf.util.StringUtils.equals(req_print_server, "TRUE")){
										row.addProperty("PRINT_SRV", rowData.get("PRINT_SRV")+"");
									}
									if(com.storeelf.util.StringUtils.equals(req_print_requester, "TRUE")){
										row.addProperty("PRNT_AREA", rowData.get("PRNT_AREA")+"");
									}
									row.addProperty("1_5", rowData.get("1_5")+"");
									row.addProperty("6_10", rowData.get("6_10")+"");
									row.addProperty("11_15", rowData.get("11_15")+"");
									row.addProperty("16_20", rowData.get("16_20")+"");
									row.addProperty("21_30", rowData.get("21_30")+"");
									row.addProperty("31_40", rowData.get("31_40")+"");
									row.addProperty("41_50", rowData.get("41_50")+"");
									row.addProperty("51_60", rowData.get("51_60")+"");
									row.addProperty("61_120", rowData.get("61_120")+"");
									row.addProperty("121_300", rowData.get("121_300")+"");
									row.addProperty("301_1800", rowData.get("301_1800")+"");
									row.addProperty("1800", rowData.get("1800")+"");
									row.addProperty("TOTAL", rowData.get("TOTAL")+"");

									data_root.add(row);
									row = new JsonObject();
								}//for

								root.add("data", data_root);
							}else{
								//No data need to dynamically calculate col span
								//int colspan = 16 + (com.storeelf.util.StringUtils.isVoid(req_print_server) ? 0 : 1) + (com.storeelf.util.StringUtils.isVoid(req_print_requester) ? 0 : 1);
								root.addProperty("ERROR", "No data available for " + req_efc_number);
							}//else null data
						}else{
							root.addProperty("ERROR", "Please enter valid inputs");
						}//else bad inputs

						response_content = gson.toJson(root);

					}catch(Exception ex){
						logger.error("failed", ex);
						ex.printStackTrace();
					}finally{
						if(con != null) con.close();
					}
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
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
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
	public void lpn_detail(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/"+requestedPage+".jsp";
		Gson 			gson				= new GsonBuilder().create();

		logger.debug("WarehouseManagementServlet:carton_detail | " + requestedPage + "|" + request.getParameter("parameterValue"));

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				responseWriter	= response.getWriter();

				JsonObject root 							= new JsonObject();
				JsonObject row	 							= new JsonObject();
				JsonObject cells	 						= new JsonObject();

				//content type MUST be "application/json"
				response.setContentType("application/json");

				Connection con = null;

				try{
					String cartonNo = request.getParameter("carton_no").toUpperCase().trim();
					String efcNo    = request.getParameter("efc_no_carton").toUpperCase().trim();
					
					//EFC2 and EFC4 2012 upgrade - start	
					//sqlids for each of the queries needed to gather information
					String[] queries = new String[] {
						Constants.UTIL_FRM_CARTON_HEADER,
						Constants.UTIL_FRM_CARTON_DETAIL,
						Constants.UTIL_FRM_CARTON_PKTHDR
					};

					//Data Validation
					if (!com.storeelf.util.StringUtils.isVoid(cartonNo) && cartonNo.matches("[A-Za-z0-9\\s]+")
						 && !com.storeelf.util.StringUtils.isVoid(efcNo) && efcNo.matches("EFC[1-4]")){
						if(efcNo.equals("EFC1")){
							con = ReportActivator.getInstance().getConnection(Constants.EFC1);
						}else if(efcNo.equals("EFC2")){						
							con = ReportActivator.getInstance().getConnection(Constants.EFC2);
						}else if(efcNo.equals("EFC3")){						
							con = ReportActivator.getInstance().getConnection(Constants.EFC3);
						}else if(efcNo.equals("EFC4")){						
							con = ReportActivator.getInstance().getConnection(Constants.EFC4);
						}
						//EFC2 and EFC4 2012 upgrade - end		
						String sqlid = "";
						HashMap<Integer, String> columns = null;
						ConcurrentHashMap<Integer, HashMap<String, Object>> rows = null;

						for(int k = 0; k < queries.length; k++){
							sqlid = queries[k];
							try{
								//write the column header data from SQL_COL_TO_DESC
								columns = Constants.FRM_RES_FLDS.get(sqlid);

								// Add the search parameter to the query and poll the db
								rows = SQLUtils.getSQLResult(String.format(Constants.SQL_MAP.get(sqlid), cartonNo), con);

								if(rows != null && rows.keySet().size() > 0){
									for(int i = 0; i < rows.keySet().size(); i++){

										for(int j = 0; j < columns.keySet().size(); j++){
										//index the ith row with each of the j column names to pull the query data
											cells.addProperty(columns.get(j + 1), rows.get(i + 1).get(columns.get(j + 1))+"");
										}//for columns
										row.add(i+"", cells);
										cells = new JsonObject();
									}//for rows
									root.add(sqlid.replace(";", ""), row);
									row = new JsonObject();
								}else{
									root.addProperty("ERROR", "No data available for "+cartonNo+" at "+efcNo);
								}
							}catch(Exception ex){
								root.addProperty("ERROR", "The folling sqlid errored out " + sqlid);
								logger.error("The folling sqlid errored out " + sqlid, ex);
							}
						}//for sqlid

					}else{
						root.addProperty("ERROR", "Please Enter a valid Carton number");
					}
				}catch(Exception ex){
					root.addProperty("ERROR", "Connection Error");
					logger.error("Connection Error", ex);
				}finally{
					if(con != null) con.close();
				}

				//Convert HashMap into json object
				response_content	= gson.toJson(root);

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
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
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
	public void task_detail(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_management/"+requestedPage+".jsp";
		Gson 			gson				= new GsonBuilder().create();

		logger.debug("WarehouseManagementServlet:task_detail | " + requestedPage + "|" + request.getParameter("parameterValue"));

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				responseWriter	= response.getWriter();

				JsonObject root 							= new JsonObject();
				JsonArray row	 							= new JsonArray();
				/*JsonArray  rows	 							= new JsonArray();*/
				JsonObject cells	 						= new JsonObject();

				//content type MUST be "application/json"
				response.setContentType("application/json");

				Connection con = null;
				try{
					String taskId = request.getParameter("task_id").toUpperCase().trim();
					String efcNo    = request.getParameter("efc_no_task").toUpperCase().trim();

					//sqlids for each of the queries needed to gather information
					//EFC2 and EFC4 2012 upgrade - start
					String[] queries = new String[] {
						Constants.UTIL_FRM_TASK_HDR,
						Constants.UTIL_FRM_TASK_DTL,
						Constants.UTIL_FRM_TASK_ALLOCATION,
						Constants.UTIL_FRM_TASK_CASE
					};

					//Data Validation
					if (!com.storeelf.util.StringUtils.isVoid(taskId) && taskId.matches("[0-9]+")
						 && !com.storeelf.util.StringUtils.isVoid(efcNo) && efcNo.matches("EFC[1-4]")){
						if(efcNo.equals("EFC1")){
							con = ReportActivator.getInstance().getConnection(Constants.EFC1);
						}else if(efcNo.equals("EFC2")){
							con = ReportActivator.getInstance().getConnection(Constants.EFC2);
						}else if(efcNo.equals("EFC3")){
							con = ReportActivator.getInstance().getConnection(Constants.EFC3);
						}else if(efcNo.equals("EFC4")){
							con = ReportActivator.getInstance().getConnection(Constants.EFC4);							
						}
						//EFC2 and EFC4 2012 upgrade - end
						HashMap<Integer, String> columns = null;
						ConcurrentHashMap<Integer, HashMap<String, Object>> db_rows = null;

						for(String sqlid : queries){
							try{

								//write the column header data from SQL_COL_TO_DESC
								columns = Constants.FRM_RES_FLDS.get(sqlid);

								// Add the search parameter to the query and poll the db
								db_rows = SQLUtils.getSQLResult(String.format(Constants.SQL_MAP.get(sqlid), taskId), con);
								if(db_rows  != null && db_rows .keySet().size() > 0){
									for(int i = 0; i < db_rows .keySet().size(); i++){

										for(int j = 0; j < columns.keySet().size(); j++){
										//index the ith row with each of the j column names to pull the query data
											cells.addProperty(columns.get(j + 1), db_rows .get(i + 1).get(columns.get(j + 1))+"");
										}//for columns

										row.add(cells);
										cells	= new JsonObject();
									}//for rows
								}else{
									root.addProperty("ERROR", "No data available for "+taskId+" at "+efcNo);
									logger.debug("No data available for "+taskId+" at "+efcNo);
								}

								root.add(sqlid, row);
								row = new JsonArray();
							}catch(Exception ex){
								root.addProperty("ERROR", "The folling sqlid errored out " + sqlid);
								logger.error("The folling sqlid errored out " + sqlid, ex);
							}
						}//for sqlid

					}else{
						root.addProperty("ERROR", "Please Enter a valid Task Id");
					}
				}catch(Exception ex){
					root.addProperty("ERROR", "Connection Error");
					logger.error("Connection Error", ex);
				}finally{
					if(con != null) con.close();
				}

				//Convert HashMap into json object
				response_content	= gson.toJson(root);

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
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
	}


	public String buildQuery(String efcNo, String timeBreak, String prntArea, boolean dispReqstr,
			 String printFromDate, String printToDate, boolean groupPrntServer){
		String sql_return = null;
		String sql1 = null;
		String sql2 = null;
		String sql3 = null;
		//String sql  = null;

		if(efcNo.equals("EFC1") || efcNo.equals("EFC2") || efcNo.equals("EFC3") || efcNo.equals("EFC4")){
			sql1 = "SELECT DISTINCT"
			+ "\n TO_CHAR(lrfr.CREATED_DTTM, '%s') AS DTE, "
			+ "\n %s" //PRNTAREA SELECT SECTION    #1
			+ "\n %s"; //PRNTSERVER SELECT SECTION  #2
		sql2=
			 "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 0 AND 5 THEN lrfr.REPORT_ID END)\"1_5\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 6 AND 10 THEN lrfr.REPORT_ID END)\"6_10\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 11 AND 15 THEN lrfr.REPORT_ID END)\"11_15\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 16 AND 20 THEN lrfr.REPORT_ID END)\"16_20\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 21 AND 30 THEN lrfr.REPORT_ID END)\"21_30\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 31 AND 40 THEN lrfr.REPORT_ID END)\"31_40\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 41 AND 50 THEN lrfr.REPORT_ID END)\"41_50\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 51 AND 60 THEN lrfr.REPORT_ID END)\"51_60\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 61 AND 120 THEN lrfr.REPORT_ID END)\"61_120\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 121 AND 300 THEN lrfr.REPORT_ID END)\"121_300\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) BETWEEN 301 AND 1800 THEN lrfr.REPORT_ID END)\"301_1800\", "
			+ "\n count(case when ROUND(TO_NUMBER(((TO_DATE(TO_CHAR(lrfra.PRINT_END_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') - TO_DATE(TO_CHAR(lrfra.PRINT_START_DTTM, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60))) > 1801 THEN lrfr.REPORT_ID END)\"1800\", "
			+ "\n count(lrfr.REPORT_ID)\"TOTAL\" "
			+ "\n FROM WMOS_EFC.LRF_REPORT_AUDIT lrfra  "
			+ "\n JOIN WMOS_EFC.LRF_REPORT lrfr "
			+ "\n ON lrfr.REPORT_ID = lrfra.REPORT_ID "	
			+ "\n WHERE lrfr.REPORT_NAME LIKE 'KH_LBColl%'";
		sql3=
			 "\n AND lrfr.PRINT_STATUS = 90 "
			+ "\n AND lrfr.CREATED_DTTM >= TO_DATE('%s', 'MM/DD/YYYY')"  //FROM DATE #3
			+ "\n AND lrfr.CREATED_DTTM <= TO_DATE('%s', 'MM/DD/YYYY') + 1"  //TO DATE #4
			+ "\n AND lrfr.RETRIES = 0 "
			+ "\n %s" //where clause for filtering on print area #5
			+ "\n GROUP BY TO_CHAR(lrfr.CREATED_DTTM, '%s') %s" //Section group by and (PrintServer)#6
			+ "\n %s" //where clause for filtering on print area #
			+ "\n ORDER BY TO_DATE(TO_CHAR(lrfr.CREATED_DTTM, '%s'), '%s') DESC"; //Sections order by (PrintServer)#7

			// Used for where clause with Print Area Filtering
						HashMap<String, String> printAreaRules = new HashMap<String, String>();
						printAreaRules.put("PACKOUT", "LIKE '%PACKOUT%'");
						printAreaRules.put("WAVE", "LIKE 'W%'");
						printAreaRules.put("PEAK", "LIKE 'PEAK%'");
						printAreaRules.put("SORTER", "LIKE '%870_X%'");
						printAreaRules.put("PUTWALL", "LIKE 'PUTWALL%'");

						//Column to parse and select print servers
						String prntServerCol = "lrfra.PRINT_START_DTTM";

						//How to format dates based on the time breakdown field
						String timeBreakSQL = "";
						if(timeBreak.equals("HOURLY")){
							timeBreakSQL = "MM/DD/YYYY HH24\":00:00\"";
						}else{
							timeBreakSQL = "MM/DD/YYYY";
						}

						//String to pass in to select / group by for PrintArea
						String printAreaSql = "";
						if(!prntArea.equals("ALL")){
							printAreaSql = "lrfr.PRT_REQSTR as PRNT_AREA,";
						}

						/*  Build Query using a format string EFC[1-3]*/
						//  Eight Cases:
						//  1: Neither prntArea or prntServer given: select neither, group by all (dont include group statements), order by time desc
						//  2: PrntArea given and prntServer not given: select printArea, group by print area, and order by time, print area
						//  3: PrntArea not given and prntServer given: select printServer, group by prntServer, and order by time, print Server
						//  4: PrntArea given and prntServer given: select printArea, printServer,  prntServer and
						//      order by time, prntArea, prntServer
						//  5,6,7,8: Each of the above cases displayed Hourly / Daily

						// Parameter summary:
						// 1 Daily / Hourly
						// 2 print area select
						// 3 print server select
						// 4 from Date
						// 5 to Date
						// 6 where clause for print areas
						// 7 Daily / Hourly
						// 8 group by print server
						// 9 order by print server
						// 10 Daily / Hourly
						// 11 Daily / Hourly


						if(prntArea.equals("ALL") && !groupPrntServer){//Case 1
							sql_return = String.format(sql1, timeBreakSQL, "","") +sql2+ String.format(sql3, printFromDate, printToDate,"",timeBreakSQL,"","", timeBreakSQL, timeBreakSQL);
						}else if(prntArea.equals("ALL") &&  groupPrntServer){//Case 2
							sql_return = String.format(sql1, timeBreakSQL, "", prntServerCol + "AS PRINT_SRV, ") +sql2+String.format(sql3, printFromDate, printToDate, "", timeBreakSQL, ", " + prntServerCol, prntServerCol + ", ", timeBreakSQL, timeBreakSQL);
						}else if(!prntArea.equals("ALL") && !groupPrntServer){//Case 3
							if(dispReqstr){
								sql_return = String.format(sql1, timeBreakSQL, printAreaSql, "" ) +sql2+String.format(sql3, printFromDate, printToDate, "AND lrfr.PRT_REQSTR " + printAreaRules.get(prntArea), timeBreakSQL,", lrfr.PRT_REQSTR", "lrfr.PRT_REQSTR, ", timeBreakSQL, timeBreakSQL);
							}else{
								sql_return = String.format(sql1,  timeBreakSQL, "", "" ) +sql2+String.format(sql3, printFromDate, printToDate, " AND lrfr.PRT_REQSTR " + printAreaRules.get(prntArea), timeBreakSQL, "" , "", timeBreakSQL, timeBreakSQL);
							}
						}else if(!prntArea.equals("ALL") && groupPrntServer){//Case 4
							if(dispReqstr){
								sql_return = String.format(sql1,  timeBreakSQL, printAreaSql, prntServerCol + "AS PRINT_SRV," ) +sql2+String.format(sql3, printFromDate, printToDate, "AND lrfr.PRT_REQSTR " + printAreaRules.get(prntArea),timeBreakSQL, ", " + prntServerCol + ", lrfr.PRT_REQSTR ", prntServerCol + ", lrfr.PRT_REQSTR,", timeBreakSQL, timeBreakSQL);
							}else{
								sql_return = String.format(sql1,  timeBreakSQL, "", prntServerCol + "AS PRINT_SRV," ) +sql2+String.format(sql3, printFromDate, printToDate, "AND lrfr.PRT_REQSTR " + printAreaRules.get(prntArea),timeBreakSQL, ", " + prntServerCol , prntServerCol + ",", timeBreakSQL, timeBreakSQL);
							}
						}

		}
		return sql_return;
	}	
	
	public Connection getConnection(String instance) {
		try {			
			if(con==null || con.isClosed()){
				logger.debug("previous connection stale, creating new one");
				
				if(instance.equals("873")){
					this.setConnection(ReportActivator.getInstance().getConnection(Constants.EFC1));
				}else if(instance.equals("809")){						
					this.setConnection(ReportActivator.getInstance().getConnection(Constants.EFC2));
				}else if(instance.equals("819")){						
					this.setConnection(ReportActivator.getInstance().getConnection(Constants.EFC3));
				}else if(instance.equals("829")){						
					this.setConnection(ReportActivator.getInstance().getConnection(Constants.EFC4));
				}
			}
		}	
		catch (SQLException e) {			logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;} 
		catch (FileNotFoundException e) {	logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;}
		catch (ClassNotFoundException e) {	logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;}
		catch (IOException e) {				logger.debug("error retrieving new connection, returning NULL; Attempt: "+dbConnectionRetryAttemptCount); dbConnectionRetryAttemptCount++;} 		
		return con;
	}
	
	public void setConnection(Connection con) {
		this.con = con;
	}
}

