package com.storeelf.report.web.servlets.dashboard;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.impl.GenericCountModel;
import com.storeelf.report.web.model.impl.GenericTabularModel;
import com.storeelf.report.web.model.impl.MultiColumnModal;
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
public class StoreElfComMetricsServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(StoreElfComMetricsServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/dashboard_includes/dashboard.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StoreElfComMetricsServlet() {
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
	public void quick_metrics(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/quick_metrics.jsp";

		logger.debug("DashboardServlet : quickMetricsResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				chart_type = request.getParameter("chart");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if ("glance".equals(chart_type)) {
						response_content = generateGlanceChart();
					} else if ("inventory_snapshot".equals(chart_type)) {
						response_content = generateAllNodeInventorySnapshotChart();
					} else if ("redundancy_statistics".equals(chart_type)) {
						response_content = redundancyStatistics();
					} else if ("14_day_fullfillment_performance"
							.equals(chart_type)) {
						Gson gson = new GsonBuilder().create();

						JsonObject all = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_5.keySet().iterator());
						JsonObject dsv = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_7.keySet().iterator());
						JsonObject ntw = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_8.keySet().iterator());

						JsonObject root = new JsonObject();
						root.add("all", all);
						root.add("direct_ship_vendor", dsv);
						root.add("network", ntw);

						response_content = gson.toJson(root);
					} else if ("fullfillment_performance".equals(chart_type)) {
						response_content = fullfillmentPerformance();
					}
				}

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}

	public void quick_metrics_visuals(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String chart_type = "";
		String csv = "";
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/quick_metrics_visuals.jsp";

		logger.debug("DashboardServlet : graph_testResponse | " + requestedPage
				+ "|" + request.getParameter("chart"));

		try {
			
			// handle request parameters here
			chart_type = request.getParameter("chart");
			csv = request.getParameter("csv");
			
			
			if (StringUtils.equals(request.getMethod(), "POST")) {
				
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if ("glance".equals(chart_type)) {
						response_content = generateGlanceChart();
					} else if ("inventory_snapshot".equals(chart_type)) {
						response_content = generateAllNodeInventorySnapshotChart();
					} else if ("redundancy_statistics".equals(chart_type)) {
						response_content = redundancyStatistics();
					} else if ("7_day_financial".equals(chart_type)){
						response_content = SevenDayFinancial(csv); 
					} else if ("14_day_fullfillment_performance"
							.equals(chart_type)) {
						Gson gson = new GsonBuilder().create();

						JsonObject all = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_5.keySet().iterator());
						JsonObject dsv = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_7.keySet().iterator());
						JsonObject ntw = StoreElf14DayFullfillmentPerformance(Constants.SQL_FRM_8.keySet().iterator());

						JsonObject root = new JsonObject();
						root.add("all", all);
						root.add("direct_ship_vendor", dsv);
						root.add("network", ntw);

						response_content = gson.toJson(root);
					} else if ("fullfillment_performance".equals(chart_type)) {
						response_content = fullfillmentPerformance_vq();
					}
				}

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
			} else if(csv !=null && csv.equals("true")){
			//check if CSV is being returned instead of JSON
				
				response_content = SevenDayFinancial(csv); 
			
				// send the bytes to file
				byte requestBytes[] = response_content.toString().getBytes();

				ByteArrayInputStream bis = new ByteArrayInputStream(
						requestBytes);

				response.reset();

				
				response.setHeader("Content-disposition",
						"attachment; filename=" + chart_type + ".csv");

				byte[] buf = new byte[1024 * 64];

				int len;

				BufferedOutputStream outs = new BufferedOutputStream(
						response.getOutputStream());
				while ((len = bis.read(buf)) > 0) {

					outs.write(buf, 0, len);

				}

				bis.close();

				outs.close();
			}
			
			else {
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}
	
	public String SevenDayFinancial(String csv){
		//	ID_7_DAY_FNCL_OVRVW		

 
		String commonTime = "";
		String csvOutput  = "";
		String sqlid = Constants.ID_7_DAY_FNCL_OVRVW;
		Gson gson = new GsonBuilder().create();

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
		
		
 
			ConcurrentHashMap<Integer, HashMap<String, Object>> result_map = null;
			try {
				result_map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getResultmap();
				runts = (SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
				tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
				
				//If desired export is csv instead build string to return
				if(csv.equals("true")){
					//Add column headers to output, loop through result_map and add rows of info
					csvOutput += "ORD_DATE,SHIPNODEKEY,SFS_BPS,STATUS,SUM(UNITS),SUM(DOLLARS) \n";
					
					//first result is always the timestamp
					boolean first = true;
								
					for(HashMap<String,Object> row : result_map.values()){
						if(first){
							first = false;
						} else {
						csvOutput += String.valueOf(row.get("ORD_DATE")).trim() + ",";
						csvOutput += String.valueOf(row.get("SHIPNODEKEY")).trim() + ",";
						csvOutput += String.valueOf(row.get("SFS_BPS")).trim() + ",";
						csvOutput += String.valueOf(row.get("STATUS")).trim() + ",";
						csvOutput += String.valueOf(row.get("SUM(UNITS)")).trim() + ",";
						csvOutput += String.valueOf(row.get("SUM(DOLLARS)")).trim() + " \n"; //this is the last in the row so do a newline
						}
					}
					
					return csvOutput;					
				}

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();

			}
		
		HashMap<String, Object>	timestamp = new HashMap<String, Object>();
		timestamp.put("timestamp", commonTime);
		
		result_map.put(0, timestamp);
			
		return gson.toJson(result_map);
	
	}

	/*public void cancel_dashboard(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/cancel_dashboard.jsp";

		logger.debug("Dashb oardServlet : cancelDashoardResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				chart_type = request.getParameter("chart");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if ("cancelglance".equals(chart_type)) {
						Gson gson = new GsonBuilder().create();

						JsonObject today = generateCancelGlanceChart(
								Constants.SQL_CNCL_FRM_1.keySet().iterator(), Constants.ID_CNCL_SPARK, Constants.ID_CNCL_BREAKDOWN,"Today");
						JsonObject prevDay = generateCancelGlanceChart(
								Constants.SQL_CNCL_FRM_1_1.keySet().iterator(), Constants.ID_CNCL_SPARK_PREV, Constants.ID_CNCL_PREV_BREAKDOWN,"PreviousDay");
						JsonObject root = new JsonObject();

						root.add("Today", today);
						root.add("PreviousDay", prevDay);

						response_content = gson.toJson(root);
						// response_content = generateCancelGlanceChart();
					} else if ("top_10_cancelled_SKU".equals(chart_type)) {
						Gson gson = new GsonBuilder().create();

						JsonObject today = Top10SKUCancelled(Constants.SQL_CNCL_FRM_2.keySet().iterator());
						JsonObject prevDay = Top10SKUCancelled(Constants.SQL_CNCL_FRM_2_1.keySet().iterator());
						// response_content = Top10SKUCancelled();

						JsonObject root = new JsonObject();
						root.add("Today", today);
						root.add("PreviousDay", prevDay);

						response_content = gson.toJson(root);
					} else if ("14_day_cancel_stats".equals(chart_type)) {
						response_content = StoreElf14DayCancellationStats();
					} else if ("5_day_auto_cancel_stats".equals(chart_type)) {
						response_content = storeelfAutoCancelStats();
					} else if ("14_day_cust_cancels".equals(chart_type)) {
						JsonObject custCancels = generateCustCancelGraph(Constants.ID_14DAY_CUST_CANCELS);
						Gson gson = new GsonBuilder().create();
						response_content = gson.toJson(custCancels);
					}
				}

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}*/
	
	public void cancel_dashboard(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/cancel_dashboard.jsp";
		Gson gson = new GsonBuilder().create();
		
		
		logger.debug("Dashb oardServlet : cancelDashoardResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				chart_type = request.getParameter("chart");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if ("cancelglance".equals(chart_type)) {						
						//response_content = getCancelDashboardData(chart_type);
						JsonObject today = generateCancelGlanceChart(
								Constants.SQL_CNCL_FRM_1.keySet().iterator(),
								Constants.ID_CNCL_SPARK, Constants.ID_CNCL_BREAKDOWN,
								"Today");
						JsonObject prevDay = generateCancelGlanceChart(
								Constants.SQL_CNCL_FRM_1_1.keySet().iterator(),
								Constants.ID_CNCL_SPARK_PREV,
								Constants.ID_CNCL_PREV_BREAKDOWN, "PreviousDay");
						JsonObject root = new JsonObject();

						root.add("Today", today);
						root.add("PreviousDay", prevDay);

						response_content = gson.toJson(root);											
						
					} else if ("top_10_cancelled_SKU".equals(chart_type)) {
						//response_content = getCancelDashboardData(chart_type);
						
						JsonObject today = Top10SKUCancelled(Constants.SQL_CNCL_FRM_2.keySet().iterator());
						JsonObject prevDay = Top10SKUCancelled(Constants.SQL_CNCL_FRM_2_1.keySet().iterator());			

						JsonObject root = new JsonObject();
						root.add("Today", today);
						root.add("PreviousDay", prevDay);

						response_content = gson.toJson(root);
						
					} else if ("14_day_cancel_stats".equals(chart_type)) {
						//response_content = getCancelDashboardData(chart_type);
						response_content = StoreElf14DayCancellationStats();
						
					} else if ("5_day_auto_cancel_stats".equals(chart_type)) {
						//response_content = getCancelDashboardData(chart_type);
						response_content = storeelfAutoCancelStats();
						
					} else if ("14_day_cust_cancels".equals(chart_type)) {
						//response_content = getCancelDashboardData(chart_type);
						response_content = gson.toJson(generateCustCancelGraph(Constants.ID_14DAY_CUST_CANCELS));
					}
				}

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}
	
	public String getCancelDashboardData(String tableName) throws SQLException
	{
		String response_content = null;
		Connection con = null;
		Statement stmt = null;
		String sql_Select="";
		String response_query="";
		ResultSet rs = null;
		try
		{
		ScheduleCancelDashboard();
		con		= ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
		stmt	= con.createStatement();
		sql_Select = " SELECT jsondata"
				+ "\n FROM STOREELF.storeelf_top10_cancelled where  reference_table='"+tableName+"' ";

		
		rs = stmt.executeQuery(sql_Select);
		while(rs.next()){
			
			 response_query=rs.getString("jsondata");
		}
		
		response_content = response_query;
		}
		// handle EVERY exception!
		catch (IOException e) {
			e.printStackTrace();
			logger.error("error processing request : IOException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing request : SQLException", e);
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(stmt!=null){stmt.close();}
			if(rs!=null){rs.close();}
		}
	
		return response_content;
	}
	
	public static void ScheduleCancelDashboard(){
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Count_Today") != "ERROR")  				Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Count_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Count_Today_Breakdown") != "ERROR")       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Count_Today_Breakdown","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Cancel_Sparkline_Today") != "ERROR")                   Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Cancel_Sparkline_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Dollar_Amount_Today") != "ERROR")         Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Dollar_Amount_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_BOPUS_Expired_Units_Today") != "ERROR")                Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_BOPUS_Expired_Units_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_BOPUS_Expired_Dollars_Today") != "ERROR")              Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_BOPUS_Expired_Dollars_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Count_Yesterday") != "ERROR")             Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Count_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Count_Yesterday_Breakdown") != "ERROR")   Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Count_Yesterday_Breakdown","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Cancel_Sparkline_Yesterday") != "ERROR")               Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Cancel_Sparkline_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Total_Cancel_Dollar_Amount_Yesterday") != "ERROR")     Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Total_Cancel_Dollar_Amount_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_BOPUS_Expired_Units_Yesterday") != "ERROR")            Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_BOPUS_Expired_Units_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_BOPUS_Expired_Dollars_Yesterday") != "ERROR")          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_BOPUS_Expired_Dollars_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Item_A1_Sku_Cancel_Today") != "ERROR")                 Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Item_A1_Sku_Cancel_Today","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Item_A1_Sku_Cancel_Yesterday") != "ERROR")             Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Item_A1_Sku_Cancel_Yesterday","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A1_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A1_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A2_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A2_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A3_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A3_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A4_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A4_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A5_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A5_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A6_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A6_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A7_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A7_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A8_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A8_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_A9_Cancels") != "ERROR")                           Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_A9_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_B10_Cancels") != "ERROR")                          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_B10_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_C11_Cancels") != "ERROR")                          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_C11_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_D12_Cancels") != "ERROR")                          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_D12_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_E13_Cancels") != "ERROR")                          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_E13_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_Day_F14_Cancels") != "ERROR")                          Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_Day_F14_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_DAY_1_Auto_Cancels") != "ERROR")                       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_DAY_1_Auto_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_DAY_2_Auto_Cancels") != "ERROR")                       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_DAY_2_Auto_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_DAY_3_Auto_Cancels") != "ERROR")                       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_DAY_3_Auto_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_DAY_4_Auto_Cancels") != "ERROR")                       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_DAY_4_Auto_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_DAY_5_Auto_Cancels") != "ERROR")                       Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_DAY_5_Auto_Cancels","SCHEDULED");
		if(Constants.STOREELF_SQL_REFRESH_JOBS.get("CD_14Day_Cancels") != "ERROR")                            Constants.STOREELF_SQL_REFRESH_JOBS.put("CD_14Day_Cancels","SCHEDULED");
	}
	
	public static JsonObject generateCustCancelGraph(String sqlid) {
		//logger.debug("Inside 14 day cust cancels....");
		String commonTime = "";
		JsonObject rootObject = new JsonObject();
		JsonObject custCancel_object = new JsonObject();
		JsonArray rootArray = new JsonArray();
		java.util.Date lastRunTimeStamp = null;
		SimpleDateFormat timestampFormat = new SimpleDateFormat(
				"MM/dd hh:mm a");
		String cncl_date = "-";
		String cncl_units = "-";
		
		try{
			
			lastRunTimeStamp = (SQLUtils.getModelObject(sqlid))
					.getLastresulttimestamp();
			
			if (lastRunTimeStamp != null) {
				if (commonTime == "")
					commonTime = timestampFormat.format(lastRunTimeStamp);
			}
			
			MultiColumnModal map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid));
			ConcurrentHashMap<Integer, HashMap<String, Object>> results = null;
		
			if (map != null) {
				try {
					results = map.getResultmap();
					cncl_date = "-";
					cncl_units = "-";
					for (HashMap<String, Object> hMap : results.values()) 
					{
							if (hMap.get("DOC") != null&& hMap.get("DOC").toString() != "")	
								cncl_date = hMap.get("DOC").toString();
							if (hMap.get("UNIT_COUNT") != null && hMap.get("UNIT_COUNT").toString() != "") 
								cncl_units = hMap.get("UNIT_COUNT").toString();	
							custCancel_object.addProperty("CANCEL_DATE", cncl_date);
							custCancel_object.addProperty("UNIT_COUNT", Integer.parseInt(cncl_units.replaceAll(",","")));
							rootArray.add(custCancel_object);
							custCancel_object = new JsonObject();
					}
				} catch (Exception e) {
					logger.error("The following SQLID errored out:" + sqlid, e);
					e.printStackTrace();
				}
			}
		
			rootObject.add("cust_cancel_data", rootArray);
			rootObject.addProperty("last_run_timestamp", commonTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
		return rootObject;
	}
	
	public void cancel_dashboard_visuals(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/cancel_dashboard_visuals.jsp";

		logger.debug("Dashb oardServlet : cancelDashoardResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				chart_type = request.getParameter("chart");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if ("14_day_cancel_stats".equals(chart_type)) {
						response_content = StoreElf14DayCancellationStats();
					} 
				}

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}

	public void warehouse_purging(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/warehouse_purging.jsp";

		JsonObject rootObject = new JsonObject(); 

		JsonArray EFC_rootArray = new JsonArray();
		JsonObject EFC_Object = new JsonObject();
 
		Gson gson = new GsonBuilder().create();

		logger.log(Level.INFO, "DashboardServlet : quickMetricsResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {

			if (StringUtils.equals(request.getMethod(), "POST")) {

				// handle request parameters here
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				Iterator<String> it = Constants.SQL_FRM_PURGE_STATS.keySet()
						.iterator();
				String commonTime = "";
				int efcCount = 0;

				String sqlid = "";
				String desc = "";
				ConcurrentHashMap<Integer, HashMap<String, Object>> map = null;

				GenericTabularModel data = null;

				String tsdesc = "Last Update: ";
				java.util.Date runts = null;
				SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

				HashMap<String,Object> rowData = null;
				while (it.hasNext()) {
					sqlid = it.next();
					map = null;

					try {
						data = ((GenericTabularModel) SQLUtils
								.getModelObject(sqlid));
						map = data.getColmap();
						desc = Constants.SQL_DESC.get(sqlid);
						runts = (SQLUtils.getModelObject(sqlid))
								.getLastresulttimestamp();

						if (runts != null) {
							tsdesc = tsdesc + tsfrmt.format(runts);
							if (commonTime == "")
								commonTime = tsfrmt.format(runts);
						}

						rootObject.addProperty(
								"warehouse_purge_last_run_timestamp",
								commonTime);
						EFC_Object.addProperty("DESCRIPTION", desc);

						if (map != null && map.keySet().size() != 0) {
							for (int i = 0; i < map.keySet().size(); i++) {
								rowData = (HashMap<String,Object>) map.get(i + 1);
								if (rowData.get("PROGRESS") != null
										&& rowData.get("PROGRESS").toString()
												.toUpperCase().equals("ERROR")) {
								}
								EFC_Object.addProperty("PURGE_TYPE",
										rowData.get("PURGE TYPE") + "");
								EFC_Object.addProperty("PURGE",
										rowData.get("PURGE") + "");
								EFC_Object.addProperty("PROGRESS",
										rowData.get("PROGRESS") + "");
								EFC_Object.addProperty("TIME",
										rowData.get("TIME") + "");
								EFC_Object.addProperty("MSG",
										rowData.get("MSG") + "");
								EFC_Object.addProperty("MSG_LOG_ID",
										rowData.get("MSG_LOG_ID") + "");

								EFC_rootArray.add(EFC_Object);
								EFC_Object = new JsonObject();
							} // for
						} else {
							// no data
						}
					} catch (Exception e) {
						logger.error(
								"The following SQLID errored out:" + sqlid, e);
						e.printStackTrace();
					}// catch

					rootObject.add("EFC" + (efcCount + 1), EFC_rootArray);
					EFC_rootArray = new JsonArray();
					EFC_Object = new JsonObject();

					++efcCount;
				}// while

				response_content = gson.toJson(rootObject);

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
			logger.log(Level.INFO, "error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO, "error processing request : Exception", e);
		}
	}

	public void warehouse_management(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/warehouse_management.jsp";

		JsonObject rootObject = new JsonObject();
		JsonObject rootCollate = new JsonObject();
		JsonArray rootCOLLATE_TIMES_Array = new JsonArray();

		JsonObject EFC_Object = new JsonObject();
		JsonArray rootPERCENTAGES_array = new JsonArray();
		JsonObject PERCENTAGES_Object = new JsonObject();
		Gson gson = new GsonBuilder().create();

		logger.log(Level.INFO, "DashboardServlet : quickMetricsResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				Iterator<String> it = Constants.SQL_FRM_COLLATE_TIME.keySet()
						.iterator();
				String commonTime = "";
				int efcCount = 0;

				String sqlid = "";
				String desc = "";
				ConcurrentHashMap<Integer, HashMap<String, Object>> map = null;
				GenericTabularModel data = null;

				String tsdesc = "Last Update: ";
				java.util.Date runts = null;
				SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

				HashMap<String, Object> rowData = null;
				while (it.hasNext()) {
					sqlid = it.next();

					try {
						data = ((GenericTabularModel) SQLUtils
								.getModelObject(sqlid));
						map = data.getColmap();
						desc = Constants.SQL_DESC.get(sqlid);
						runts = (SQLUtils.getModelObject(sqlid))
								.getLastresulttimestamp();

						if (runts != null) {
							tsdesc = tsdesc + tsfrmt.format(runts);
							if (commonTime == "")
								commonTime = tsfrmt.format(runts);
						}

						EFC_Object.addProperty("DESCRIPTION", desc);

						if (map != null && map.keySet().size() != 0) {
							int[] colTotals = new int[14];
							int totalSeconds = 0;
							for (int i = 0; i < map.keySet().size(); i++) {
								rowData = (HashMap<String, Object>) map.get(i + 1);
								colTotals[0] += Integer.parseInt(rowData.get("1_5").toString());
								colTotals[1] += Integer.parseInt(rowData.get("6_10").toString());
								colTotals[2] += Integer.parseInt(rowData.get("11_15").toString());
								colTotals[3] += Integer.parseInt(rowData.get("16_20").toString());
								colTotals[4] += Integer.parseInt(rowData.get("21_30").toString());
								colTotals[5] += Integer.parseInt(rowData.get("31_40").toString());
								colTotals[6] += Integer.parseInt(rowData.get("41_50").toString());
								colTotals[7] += Integer.parseInt(rowData.get("51_60").toString());
								colTotals[8] += Integer.parseInt(rowData.get("61_120").toString());
								colTotals[9] += Integer.parseInt(rowData.get("121_300").toString());
								colTotals[10] += Integer.parseInt(rowData.get("301_1800").toString());
								colTotals[11] += Integer.parseInt(rowData.get("1801_3600").toString());
								colTotals[12] += Integer.parseInt(rowData.get("3601_5400").toString());
								colTotals[13] += Integer.parseInt(rowData.get("5400").toString());
								totalSeconds += Integer.parseInt(rowData.get("TOTAL_PRINTED").toString());

								EFC_Object.addProperty("DTE", rowData.get("DTE").toString());
								EFC_Object.addProperty("1_5", rowData.get("1_5").toString());
								EFC_Object.addProperty("6_10",rowData.get("6_10").toString());
								EFC_Object.addProperty("11_15",rowData.get("11_15").toString());
								EFC_Object.addProperty("16_20",rowData.get("16_20").toString());
								EFC_Object.addProperty("21_30",rowData.get("21_30").toString());
								EFC_Object.addProperty("31_40",rowData.get("31_40").toString());
								EFC_Object.addProperty("41_50",rowData.get("41_50").toString());
								EFC_Object.addProperty("51_60",rowData.get("51_60").toString());
								EFC_Object.addProperty("61_120",rowData.get("61_120").toString());
								EFC_Object.addProperty("121_300",rowData.get("121_300").toString());
								EFC_Object.addProperty("301_1800",rowData.get("301_1800").toString());
								EFC_Object.addProperty("1801_3600", rowData.get("1801_3600").toString());
								EFC_Object.addProperty("3601_5400", rowData.get("3601_5400").toString());
								EFC_Object.addProperty("5400",rowData.get("5400").toString());
								EFC_Object.addProperty("TOTAL_PRINTED", rowData.get("TOTAL_PRINTED").toString());

								rootCOLLATE_TIMES_Array.add(EFC_Object);

								EFC_Object = new JsonObject();
								++efcCount;

							} // for

							for (int i = 0; i < colTotals.length; i++) {
								PERCENTAGES_Object.addProperty("percentage",
										percent(colTotals[i], totalSeconds));
								rootPERCENTAGES_array.add(PERCENTAGES_Object);
								PERCENTAGES_Object = new JsonObject();
							}
							PERCENTAGES_Object.addProperty("percentage",
									totalSeconds);
							rootPERCENTAGES_array.add(PERCENTAGES_Object);

						} else {
							rootCollate.addProperty("error",
									"No data is currently available for EFC: "
											+ efcCount);
						}

						rootCollate.add("COLLATE_TIMES",
								rootCOLLATE_TIMES_Array);
						rootCollate.add("PERCENTAGES", rootPERCENTAGES_array);

						rootObject.add(sqlid, rootCollate);

						rootCollate = new JsonObject();
						PERCENTAGES_Object = new JsonObject();
						rootCOLLATE_TIMES_Array = new JsonArray();
						rootPERCENTAGES_array = new JsonArray();

					} catch (Exception e) {
						logger.error(
								"The following SQLID errored out:" + sqlid, e);
						e.printStackTrace();
					}// catch
				} // while

				// rootObject.add("TOTALS", rootTOTALS_Object);
				response_content = gson.toJson(rootObject);

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();

				logger.log(Level.INFO, "warehouse_management:: "
						+ response_content);

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
			logger.error("error processing request : IOException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
	}

	/**
	 * @param part
	 *            - part to be divided
	 * @param total
	 *            - the ammount taking th percentage from
	 * @return String formatted as HH:MM:SS
	 */
	public double percent(int part, int total) {
		return Math.round(((double) part / total) * 10000) / (double) 100;
	}

	// added for cancel dashboard
	public static JsonObject generateCancelGlanceChart(Iterator<String> cnclIterator, String sparkIterator, String breakdownIterator,
			String selectedDay) {
		JsonObject root = new JsonObject();
		Gson gson = new GsonBuilder().create();
		String commonTime = "";

		// Create objects to read from jsp
		try {
			int icount = 0;
			String sqlid = null;
			Object value = "";
			String description = null;
			java.util.Date lastRunTimeStamp = null;
			SimpleDateFormat timestampFormat = new SimpleDateFormat(
					"MM/dd hh:mm a");

			HashMap<Integer, HashMap<String, String>> resultMap = new HashMap<Integer, HashMap<String, String>>();
			HashMap<Integer, HashMap<String, String>> resultMapQTY = new HashMap<Integer, HashMap<String, String>>();
			HashMap<Integer, HashMap<String, String>> resultMapAMT = new HashMap<Integer, HashMap<String, String>>();

			HashMap<String, String> row = new HashMap<String, String>();

			while (cnclIterator.hasNext()) {
				sqlid = cnclIterator.next();

				value = ((GenericCountModel) SQLUtils.getModelObject(sqlid))
						.getVal();
				if (value == null || value == "")
					value = '-';

				description = Constants.SQL_DESC.get(sqlid);
				lastRunTimeStamp = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				row.put("DESCRIPTION", description);
				row.put("VALUE", value.toString());
				if (description.startsWith("U")){
					resultMapQTY.put(0, row);
				} else if (description.startsWith("D")) {
					resultMapAMT.put(0, row);
				} else {
					resultMap.put(icount, row);
					icount++;
				}
				if (lastRunTimeStamp != null) {
					if (commonTime == "")
						commonTime = timestampFormat.format(lastRunTimeStamp);
				}

				
				row = new HashMap<String, String>();
			}
			root.add("fulfillment_data", gson.toJsonTree(resultMap));
			root.add("cncl_qty_data", gson.toJsonTree(resultMapQTY));
			root.add("cncl_amt_data", gson.toJsonTree(resultMapAMT));
			//variables for Cancel Dashboard -> At a glance sparkline
			JsonObject quantityCancelled = new JsonObject();
			JsonObject dollarAmtCancelled = new JsonObject();
			String cncl_quantity = "-";
			String cncl_dollar = "-";
			String cncl_hour = "-";	
			//grab the description of the SQL model
			sqlid = sparkIterator;
			//grab the SQL model via its description
			MultiColumnModal map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid));
			ConcurrentHashMap<Integer, HashMap<String, Object>> results = null;			
			if(map != null){
				try {
					//store the query results in the concurrent hash map
					results = map.getResultmap();
					if(results != null){
						for (HashMap<String, Object> hMap : results.values())
						{			
							cncl_quantity = hMap.get("TOTAL_COUNT").toString();
							cncl_dollar = hMap.get("DOLLARS_LOST").toString().replaceAll(",", "");
							cncl_hour = hMap.get("TIME").toString();	
							//add returned values as properties in their respected JSON object
							quantityCancelled.addProperty(cncl_hour.replaceFirst("^0+(?!$)", ""), Integer.parseInt(cncl_quantity.replaceAll(",","")));
							dollarAmtCancelled.addProperty(cncl_hour.replaceFirst("^0+(?!$)", ""), Double.parseDouble(cncl_dollar));
						}
					}
				}catch (Exception e) {
					System.err.println("The following SQLID errored out:"
							+ sqlid);
					e.printStackTrace();
				}
			}
			root.add("quantity_cancelled", quantityCancelled);
			root.add("dollar_amount_cancelled", dollarAmtCancelled);
			
			JsonArray		cncl_quantity_array		= new JsonArray();
			JsonArray		cncl_dollar_array		= new JsonArray();
			quantityCancelled = new JsonObject();
			dollarAmtCancelled = new JsonObject();
			cncl_quantity = "-";
			cncl_dollar = "-";
			String cncl_desc = "-";	
			//grab the description of the SQL model
			sqlid = breakdownIterator;
			//grab the SQL model via its description
			map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid));
			results = null;			
			if(map != null){
				try {
					//store the query results in the concurrent hash map
					results = map.getResultmap();
					if(results != null){
						for (HashMap<String, Object> hMap : results.values())
						{			
							cncl_quantity = hMap.get("TOTAL_COUNT").toString();
							cncl_dollar = hMap.get("DOLLARS_LOST").toString();
							cncl_desc = hMap.get("CHANNEL").toString();	
							//add returned values as properties in their respected JSON object
							quantityCancelled.addProperty("CHANNEL", cncl_desc.replaceFirst("^0+(?!$)", ""));
							quantityCancelled.addProperty("TOTAL_COUNT", Integer.parseInt(cncl_quantity.replaceAll(",","")));
							dollarAmtCancelled.addProperty("CHANNEL", cncl_desc.replaceFirst("^0+(?!$)", ""));
							dollarAmtCancelled.addProperty("DOLLARS_LOST", cncl_dollar);
							cncl_quantity_array.add(quantityCancelled);
							cncl_dollar_array.add(dollarAmtCancelled);
							quantityCancelled = new JsonObject();
							dollarAmtCancelled = new JsonObject();
						}
					}
				}catch (Exception e) {
					System.err.println("The following SQLID errored out:"
							+ sqlid);
					e.printStackTrace();
				}
			}
			root.add("quantity_cancelled_breakdown", cncl_quantity_array);
			root.add("dollar_amount_cancelled_breakdown", cncl_dollar_array);	
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		}
		root.addProperty("last_run_timestamp", commonTime);
		return root;
	}

	// starting top 10 cancelled SKU
	public static String StoreElf14DayCancellationStats() {

		//logger.debug("Inside 14 day cancel....");
		Iterator<String> it = Constants.SQL_CNCL_FRM_3.keySet().iterator();
		String commonTime = "";
		String sqlid = null;
		String desc = "";

		JsonObject rootObject = new JsonObject();
		JsonObject Cancel_object = new JsonObject();
		Gson gson = new GsonBuilder().create();
		JsonArray rootArray = new JsonArray();

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		String ord_date = "-";		String efc1_cancel = "-";		String efc1_rsrc = "-";
		String efc2_cancel = "-";	String efc2_rsrc = "-";			String efc3_cancel = "-";
		String efc3_rsrc = "-";		String efc4_cancel = "-";		String efc4_rsrc = "-";
		String rdc_cancel = "-";	String rdc_rsrc = "-";
		
		String stores_cst_cancel = "-";		String stores_est_cancel = "-";		String stores_pst_cancel = "-";
		String stores_mst_cancel = "-";		String stores_mdt_cancel = "-";		String stores_cst_rsrc = "-";
		String stores_est_rsrc = "-";		String stores_pst_rsrc = "-";		String stores_mst_rsrc = "-";
		String stores_mdt_rsrc = "-";

		HashMap<String, Object> map = null;

		while (it.hasNext()) {
			sqlid = it.next();
			//logger.debug("SQl_ID--> " + sqlid);

			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "")
						commonTime = tsfrmt.format(runts);
				}

				Cancel_object.addProperty("DESCRIPTION", desc);

				// <tr class="<%=classid%>" title="<%=tsdesc%>">

				ord_date = "-";		efc1_cancel = "-";		efc1_rsrc = "-";	efc2_cancel = "-";
				efc2_rsrc = "-";	efc3_cancel = "-";		efc3_rsrc = "-";	efc4_cancel = "-";
				efc4_rsrc = "-";	rdc_cancel = "-";		rdc_rsrc = "-";		stores_cst_cancel = "-";
				
				stores_est_cancel = "-";	stores_pst_cancel = "-";	stores_mst_cancel = "-";	stores_mdt_cancel = "-";
				stores_cst_rsrc = "-";		stores_est_rsrc = "-";		stores_pst_rsrc = "-";		stores_mst_rsrc = "-";
				stores_mdt_rsrc = "-";

				if (map != null) {
					if (map.get("ORDERDT") != null && map.get("ORDERDT").toString() != "")				ord_date = map.get("ORDERDT").toString();
					if (map.get("EFC1CANC") != null && map.get("EFC1CANC").toString() != "")			efc1_cancel = map.get("EFC1CANC").toString();
					if (map.get("EFC1RSRCD") != null && map.get("EFC1RSRCD").toString() != "")			efc1_rsrc = map.get("EFC1RSRCD").toString();
					if (map.get("EFC2CANC") != null && map.get("EFC2CANC").toString() != "")			efc2_cancel = map.get("EFC2CANC").toString();
					if (map.get("EFC2RSRCD") != null && map.get("EFC2RSRCD").toString() != "")			efc2_rsrc = map.get("EFC2RSRCD").toString();
					if (map.get("EFC3CANC") != null && map.get("EFC3CANC").toString() != "")			efc3_cancel = map.get("EFC3CANC").toString();
					if (map.get("EFC3RSRCD") != null && map.get("EFC3RSRCD").toString() != "")			efc3_rsrc = map.get("EFC3RSRCD").toString();
					if (map.get("EFC4CANC") != null && map.get("EFC4CANC").toString() != "")			efc4_cancel = map.get("EFC4CANC").toString();
					if (map.get("EFC4RSRCD") != null && map.get("EFC4RSRCD").toString() != "")			efc4_rsrc = map.get("EFC4RSRCD").toString();
					if (map.get("RDCCANC") != null && map.get("RDCCANC").toString() != "")				rdc_cancel = map.get("RDCCANC").toString();
					if (map.get("RDCRSRCD") != null && map.get("RDCRSRCD").toString() != "")			rdc_rsrc = map.get("RDCRSRCD").toString();					
					if (map.get("STORECSTCANC") != null && map.get("STORECSTCANC").toString() != "")	stores_cst_cancel = map.get("STORECSTCANC").toString();
					if (map.get("STOREESTCANC") != null && map.get("STOREESTCANC").toString() != "")	stores_est_cancel = map.get("STOREESTCANC").toString();
					if (map.get("STOREPSTCANC") != null && map.get("STOREPSTCANC").toString() != "")	stores_pst_cancel = map.get("STOREPSTCANC").toString();
					if (map.get("STOREMSTCANC") != null && map.get("STOREMSTCANC").toString() != "")	stores_mst_cancel = map.get("STOREMSTCANC").toString();
					if (map.get("STOREMDTCANC") != null && map.get("STOREMDTCANC").toString() != "")	stores_mdt_cancel = map.get("STOREMDTCANC").toString();
					if (map.get("STORECSTRSRCD") != null && map.get("STORECSTRSRCD").toString() != "")	stores_cst_rsrc = map.get("STORECSTRSRCD").toString();
					if (map.get("STOREESTRSRCD") != null && map.get("STOREESTRSRCD").toString() != "")	stores_est_rsrc = map.get("STOREESTRSRCD").toString();
					if (map.get("STOREPSTRSRCD") != null && map.get("STOREPSTRSRCD").toString() != "")	stores_pst_rsrc = map.get("STOREPSTRSRCD").toString();
					if (map.get("STOREMSTRSRCD") != null && map.get("STOREMSTRSRCD").toString() != "")	stores_mst_rsrc = map.get("STOREMSTRSRCD").toString();
					if (map.get("STOREMDTRSRCD") != null && map.get("STOREMDTRSRCD").toString() != "")	stores_mdt_rsrc = map.get("STOREMDTRSRCD").toString();

					Cancel_object.addProperty("ORDERDT", ord_date);
					Cancel_object.addProperty("EFC1CANC", efc1_cancel);
					Cancel_object.addProperty("EFC1RSRCD", efc1_rsrc);
					Cancel_object.addProperty("EFC2CANC", efc2_cancel);
					Cancel_object.addProperty("EFC2RSRCD", efc2_rsrc);
					Cancel_object.addProperty("EFC3CANC", efc3_cancel);
					Cancel_object.addProperty("EFC3RSRCD", efc3_rsrc);
					Cancel_object.addProperty("EFC4CANC", efc4_cancel);
					Cancel_object.addProperty("EFC4RSRCD", efc4_rsrc);
					Cancel_object.addProperty("RDCCANC", rdc_cancel);
					Cancel_object.addProperty("RDCRSRCD", rdc_rsrc);
					Cancel_object.addProperty("STORECSTCANC", stores_cst_cancel);
					Cancel_object.addProperty("STOREESTCANC", stores_est_cancel);
					Cancel_object.addProperty("STOREPSTCANC", stores_pst_cancel);
					Cancel_object.addProperty("STOREMSTCANC", stores_mst_cancel);
					Cancel_object.addProperty("STOREMDTCANC", stores_mdt_cancel);
					Cancel_object.addProperty("STORECSTRSRCD", stores_cst_rsrc);
					Cancel_object.addProperty("STOREESTRSRCD", stores_est_rsrc);
					Cancel_object.addProperty("STOREPSTRSRCD", stores_pst_rsrc);
					Cancel_object.addProperty("STOREMSTRSRCD", stores_mst_rsrc);
					Cancel_object.addProperty("STOREMDTRSRCD", stores_mdt_rsrc);
				}

				rootArray.add(Cancel_object);

				Cancel_object = new JsonObject();
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}
		}
		rootObject.add("cancel_data", rootArray);
		rootObject.addProperty("last_run_ts", commonTime);

		return gson.toJson(rootObject);
	}// ending 14 day cancellation

	public static String storeelfAutoCancelStats() {// added for auto cancel section

		//logger.debug("Inside 5 day auto cancel....");
		Iterator<String> it = Constants.SQL_CNCL_FRM_4.keySet().iterator();
		String commonTime = "";
		String sqlid = null;
		String desc = "";

		JsonObject rootObject = new JsonObject();
		JsonObject autoCancel_object = new JsonObject();
		Gson gson = new GsonBuilder().create();
		JsonArray rootArray = new JsonArray();

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		String cncl_date = "-";
		String apo_cancel = "-";
		String print_cancel = "-";
		String ds_cancel = "-";
		String gift_cancel = "-";
		String RTAM_cancel = "-";
		String priority_str = "-";
		String priority_dsv = "-";
		String priority_rdc = "-";

		HashMap<String, Object> map = null;

		while (it.hasNext()) {
			sqlid = it.next();

			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "")
						commonTime = tsfrmt.format(runts);
				}

				autoCancel_object.addProperty("DESCRIPTION", desc);

				cncl_date = "-";
				apo_cancel = "-";
				print_cancel = "-";
				ds_cancel = "-";
				gift_cancel = "-";
				RTAM_cancel = "-";
				priority_str = "-";
				priority_dsv = "-";
				priority_rdc = "-";

				if (map != null) {
					if (map.get("CANCEL_DATE") != null && map.get("CANCEL_DATE").toString() != "")								cncl_date = map.get("CANCEL_DATE").toString();
					if (map.get("'APO/FPOCANCEL'") != null && map.get("'APO/FPOCANCEL'").toString() != "")						apo_cancel = map.get("'APO/FPOCANCEL'").toString();
					if (map.get("'AUTO-CANCEL AT PRINT'") != null && map.get("'AUTO-CANCEL AT PRINT'").toString() != "")		print_cancel = map.get("'AUTO-CANCEL AT PRINT'").toString();
					if (map.get("'DSLINECANCEL'") != null && map.get("'DSLINECANCEL'").toString() != "")						ds_cancel = map.get("'DSLINECANCEL'").toString();
					if (map.get("'GIFTWRAPCANCEL'") != null && map.get("'GIFTWRAPCANCEL'").toString() != "")					gift_cancel = map.get("'GIFTWRAPCANCEL'").toString();
					if (map.get("'RTAM/GENUINECANCEL'") != null && map.get("'RTAM/GENUINECANCEL'").toString() != "")			RTAM_cancel = map.get("'RTAM/GENUINECANCEL'").toString();
					if (map.get("'STRPRITYORDCNCL-NOINV'") != null && map.get("'STRPRITYORDCNCL-NOINV'").toString() != "")		priority_str = map.get("'STRPRITYORDCNCL-NOINV'").toString();
					if (map.get("'DSVPRIORITYORDERCANCEL'") != null && map.get("'DSVPRIORITYORDERCANCEL'").toString() != "")	priority_dsv = map.get("'DSVPRIORITYORDERCANCEL'").toString();
					if (map.get("'RDCPRIORITYORDERCANCEL'") != null && map.get("'RDCPRIORITYORDERCANCEL'").toString() != "")	priority_rdc = map.get("'RDCPRIORITYORDERCANCEL'").toString();

					autoCancel_object.addProperty("CANCEL_DATE", cncl_date);
					autoCancel_object.addProperty("APO_CNCL", apo_cancel);
					autoCancel_object.addProperty("PRINT_CNCL", print_cancel);
					autoCancel_object.addProperty("DS_CNCL", ds_cancel);
					autoCancel_object.addProperty("GIFT_CNCL", gift_cancel);
					autoCancel_object.addProperty("RTAM_CNCL", RTAM_cancel);
					autoCancel_object.addProperty("PRIORITY_STR", priority_str);
					autoCancel_object.addProperty("PRIORITY_DSV", priority_dsv);
					autoCancel_object.addProperty("PRIORITY_RDC", priority_rdc);

				}

				rootArray.add(autoCancel_object);

				autoCancel_object = new JsonObject();
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}
		}
		rootObject.add("auto_cancel_data", rootArray);
		rootObject.addProperty("last_run_ts", commonTime);

		return gson.toJson(rootObject);
	}

	public static JsonObject Top10SKUCancelled(Iterator<String> it) {
	
		String commonTime = "";
		String sqlid = null;
		JsonObject root = new JsonObject();
		JsonObject ITEM_Object = new JsonObject();
		JsonArray rootArray = new JsonArray();
	
		// HashMap<String, Object> map = null;
		String tsdesc = "Last Update: ";
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
	
		java.util.Date runts = null;
	
		while (it.hasNext()) {
			sqlid = it.next();
	
			// map = null;
			try { 
				MultiColumnModal hMap = ((MultiColumnModal) SQLUtils.getModelObject(sqlid));
				ConcurrentHashMap<Integer, HashMap<String, Object>> results = null;
				results = hMap.getResultmap();
	
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();
	
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}
	
				Integer cancel_qty = 0;
				String item_id = "-";
				String item_desc = "-";
				String cancel_dollar = "-";
				for (HashMap<String, Object> map : results.values()) {
	
					if (map != null) {
						if (map.get("CANCELQTY") != null && map.get("CANCELQTY").toString() != "")					cancel_qty = Integer.parseInt(map.get("CANCELQTY").toString());
						if (map.get("ITEMID") != null && map.get("ITEMID").toString() != "")						item_id = map.get("ITEMID").toString();
						if (map.get("ITEM_DESC") != null && map.get("ITEM_DESC").toString() != "")					item_desc = map.get("ITEM_DESC").toString();
						if (map.get("CANCDELLEDDOLLAR") != null && map.get("CANCDELLEDDOLLAR").toString() != "")	cancel_dollar = map.get("CANCDELLEDDOLLAR").toString();
	
						ITEM_Object.addProperty("CANCELQTY", cancel_qty);
						ITEM_Object.addProperty("ITEMID", item_id);
						ITEM_Object.addProperty("ITEM_DESC", item_desc);
						ITEM_Object.addProperty("CANCDELLEDDOLLAR",
								cancel_dollar);
	
					}
					rootArray.add(ITEM_Object);
					ITEM_Object = new JsonObject();
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
	
			}
		}
		root.add("fulfillment_data", rootArray);
		root.addProperty("last_run_timestamp", commonTime);
		//logger.debug("at a glance last run time stamp--> " + commonTime);
		return root;
	}// ending 14 day

	public String generateGlanceChart() throws SQLException {
		ResultSet result = null;
		Connection con = null;
		try {
			Iterator<String> iterator = null;
			int icount = 0;
			String commonTime = "";
			String sqlid = null;
			Object value = "";
			String description = null;
			String sql = null;
			int i = 25;
			java.util.Date lastRunTimeStamp = null;
			SimpleDateFormat timestampFormat = new SimpleDateFormat(
					"MM/dd hh:mm a");
			Gson gson = new GsonBuilder().create();

			HashMap<Integer, HashMap<String, String>> resultMap = new HashMap<Integer, HashMap<String, String>>();

			JsonObject root = new JsonObject();
			new JsonObject();

			iterator = Constants.SQL_FRM_1.keySet().iterator();

			HashMap<String, String> row = new HashMap<String, String>();

			while (iterator.hasNext()) {
				sqlid = iterator.next();

				value = ((GenericCountModel) SQLUtils.getModelObject(sqlid))
						.getVal();

				if (value == null || value == "")
					value = '-';

				description = Constants.SQL_DESC.get(sqlid);
				lastRunTimeStamp = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				row.put("DESCRIPTION", description);
				row.put("VALUE", value.toString());

				resultMap.put(icount, row);

				if (lastRunTimeStamp != null) {
					timestampFormat
							.format(lastRunTimeStamp);
					if (commonTime == "")
						commonTime = timestampFormat.format(lastRunTimeStamp);
				}

				icount++;
				row = new HashMap<String, String>();
			}

			// Addition for sparkline graph on glance chart

			// Create objects to read from jsp
			JsonObject total = new JsonObject();
			new JsonArray();

			// ArrayLists for storing chart points/data
			ArrayList<Integer> order_counts = new ArrayList<Integer>();
			ArrayList<String> times = new ArrayList<String>();

			// establish DB connection
			con = ReportActivator.getInstance().getConnection(Constants.OMS);

			// loop through query 24 times (one time for the each of the
			// previous 24 hours)
			while (i > 1) {

				// Retrieves hourly order totals
				sql = "  SELECT trim(TO_CHAR(COUNT(*),'999,999,999,999,999')) AS TOTAL_COUNT, TO_CHAR(sysdate-("
						+ (i - 1)
						+ "/24),'HH12 AM') AS TIME"
						+ " FROM  yfs_order_header "
						+ " WHERE order_date > sysdate-("
						+ (i)
						+ "/24)"
						+ " AND order_date < sysdate-("
						+ (i - 1)
						+ "/24)"
						+ " AND document_type ='0001' "
						+ " AND enterprise_key = 'STOREELF.COM'";

				result = con.createStatement().executeQuery(sql);
				// logger.log(Level.INFO, "Order Count Daily | " + sql);

				// retrieve query result and add to array list. This list of
				// values will be added to JSON later.
				while (result.next()) {
					String TOTAL_COUNT = result.getString("TOTAL_COUNT");
					String TIME = result.getString("TIME");

					// ****MUST BE INTEGER**** for sparkline to read.
					order_counts.add(Integer.parseInt(TOTAL_COUNT.replaceAll(
							",", "")));
					times.add(TIME);
				}
				i--;
			}
			int x = 0;
			// adding data values to JSON
			while (x < order_counts.size()) {
				total.addProperty(times.get(x), order_counts.get(x));
				x++;
			}

			root.add("total_chart_array", total);
			root.add("glance_data", gson.toJsonTree(resultMap));
			root.addProperty("glance_last_run_timestamp", commonTime);

			// Convert HashMap into json object
			return gson.toJson(root);
		}
		// handle EVERY exception!
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("error processing request : ClassNotFoundException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			if(result!=null){result.close();}
			if(con!=null){con.close();}
		}
		return "";
	}

	public String generateAllNodeInventorySnapshotChart() {
		JsonObject rootObject = new JsonObject();

		JsonArray EFCrootArray = new JsonArray();
		JsonArray RDCrootArray = new JsonArray();

		JsonObject EFC_Object = new JsonObject();
		JsonObject RDC_Object = new JsonObject();

		Gson gson = new GsonBuilder().create();

		Iterator<String> it = Constants.SQL_FRM_3.keySet().iterator();
		int icount = 0;
		String commonTime = "";
		HashMap<String, Object> map = null;

		String sqlid = "";
		String val = "";
		String desc = "";

		String sqlidrdc = "";
		String valrdc = "";
		String descrdc = "";

		String tsdesc = null, tsdescrdc = "Last Update: ";
		// String tsdescrdc = "Last Update: ";
		java.util.Date runts = null;
		java.util.Date runtsrdc = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		HashMap<String, Object> maprdc = null;

		while (it.hasNext()) {
			sqlid = it.next();
			val = "";
			desc = "";
			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}

				if (desc.equals("RDC")) {
					EFC_Object.addProperty("DESCRIPTION", desc);

					if (map != null) {
						Iterator<String> itval = map.keySet().iterator();
						while (itval.hasNext()) {
							String col = itval.next();
							val = map.get(col).toString();
							RDC_Object.addProperty(col, val + "");
						}
					} else {
						RDC_Object
								.addProperty("Processing...", "Processing...");
					}

					rootObject.add("RDC_TOTALS", RDC_Object);
					// reset EFC object
					RDC_Object = new JsonObject();

					Iterator<String> itrdc = Constants.SQL_FRM_RDC_3.keySet()
							.iterator();
					
					while (itrdc.hasNext()) {
						sqlidrdc = itrdc.next();
						valrdc = "";
						descrdc = "";
						maprdc = null;
						try {
							maprdc = ((MultiColumnModal) SQLUtils
									.getModelObject(sqlidrdc)).getColmap();
							descrdc = Constants.SQL_DESC.get(sqlidrdc);
							tsdescrdc = "Last Update: ";
							runtsrdc = (SQLUtils.getModelObject(sqlid))
									.getLastresulttimestamp();
							if (runtsrdc != null) {
								tsdescrdc = tsdescrdc + tsfrmt.format(runtsrdc);
							}

							RDC_Object.addProperty("DESCRIPTION", descrdc + "");

							if (maprdc != null) {
								Iterator<String> itvalrdc = maprdc.keySet()
										.iterator();
								while (itvalrdc.hasNext()) {
									String colrdc = itvalrdc.next();
									valrdc = maprdc.get(colrdc).toString();

									RDC_Object.addProperty(colrdc, valrdc + "");
								}
							} else {
								RDC_Object.addProperty("Processing...",
										"Processing...");
							}

							RDCrootArray.add(RDC_Object);

							RDC_Object = new JsonObject();

						} catch (Exception e) {
							e.printStackTrace();
							logger.error("The following SQLID errored out:"
									+ sqlidrdc, e);
						}
					}
				} else {
					EFC_Object.addProperty("DESCRIPTION", desc);

					if (map != null) {
						Iterator<String> itval = map.keySet().iterator();
						while (itval.hasNext()) {
							String col = itval.next();
							val = map.get(col).toString();
							EFC_Object.addProperty(col, val + "");
						}
					} else {
						EFC_Object
								.addProperty("Processing...", "Processing...");
					}

					EFCrootArray.add(EFC_Object);
					// reset EFC object
					EFC_Object = new JsonObject();
				}

				rootObject.addProperty("inventory_snapshot_last_run_timestamp",
						commonTime);
				rootObject.add("EFC", EFCrootArray);
				rootObject.add("RDC", RDCrootArray);

				icount++;
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
			}
		}
		return gson.toJson(rootObject);
	}

	public String generateAllNodeInventorySnapshotChart_REVAMP() {
		Iterator<String> iterator_rdc = null;
		String commonTime = "";
		String sqlid = null;
		String colrdc = "";
		Object value = "";
		Object value_rdc = "";
		String description = null;
		String timestampDescriptionPrefix = "Last Update: ";
		java.util.Date lastRunTimeStamp = null;
		SimpleDateFormat timestampFormat = new SimpleDateFormat("MM/dd hh:mm a");
		new GsonBuilder().create();

		HashMap<String, Object> columnMap_rdc = null;
		HashMap<String, Object> columnMap = null;

		new HashMap<Integer, HashMap<String, HashMap<String, String>>>();

		JsonObject rootObject = new JsonObject();
		JsonObject RDC_Object = new JsonObject();

		try {
			Constants.SQL_FRM_1.keySet().iterator();

			new HashMap<String, String>();

			columnMap = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
					.getColmap();
			description = Constants.SQL_DESC.get(sqlid);
			java.util.Date runts = (SQLUtils.getModelObject(sqlid))
					.getLastresulttimestamp();

			if (runts != null) {
				timestampDescriptionPrefix = timestampDescriptionPrefix
						+ timestampFormat.format(runts);
				if (commonTime == "") {
					commonTime = timestampFormat.format(runts);
				}
			}

			// if(description.equals("RDC")){}
			// show/add description
			// description;
			rootObject.addProperty("DESCRIPTION", description);

			if (columnMap != null) {
				Iterator<String> itval = columnMap.keySet().iterator();
				while (itval.hasNext()) {
					String col = itval.next();
					value = columnMap.get(col).toString();
					rootObject.addProperty(col, value + "");
				}
			} else {
				// description/value = -/-
				rootObject.addProperty("-", "-");
			}

			if (description.equals("RDC")) {
				iterator_rdc = Constants.SQL_FRM_RDC_3.keySet().iterator();
				String sqlidrdc = iterator_rdc.next();

				while (iterator_rdc.hasNext()) {
					try {
						columnMap_rdc = ((MultiColumnModal) SQLUtils
								.getModelObject(sqlidrdc)).getColmap();
						Constants.SQL_DESC.get(sqlidrdc);

						lastRunTimeStamp = (SQLUtils.getModelObject(sqlid))
								.getLastresulttimestamp();

						if (lastRunTimeStamp != null) {
							timestampDescriptionPrefix = timestampDescriptionPrefix
									+ timestampFormat.format(lastRunTimeStamp);
						}
						if (columnMap_rdc != null) {
							Iterator<String> itvalrdc = columnMap_rdc.keySet()
									.iterator();
							while (itvalrdc.hasNext()) {
								colrdc = itvalrdc.next();
								value_rdc = columnMap_rdc.get(colrdc)
										.toString();

								RDC_Object.addProperty(colrdc, value_rdc + "");
							}
						} else {
							rootObject.addProperty("-", "-");
						}
					} catch (Exception e) {
						logger.error("The following SQLID errored out:"
								+ sqlidrdc, e);
						e.printStackTrace();
					}
				}
				rootObject.add("RDC", RDC_Object);
			}
			return rootObject.getAsString();
			// handle EVERY exception!
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing request : Exception", e);
		} finally {
			return "";
		}
	}

	public String redundancyStatistics() {

		Iterator<String> it = Constants.SQL_FRM_4.keySet().iterator();
		String commonTime = "";
		String sqlid = null;
		String val = "";
		String desc = "";

		JsonObject rootObject = new JsonObject();

		JsonArray EFCrootArray = new JsonArray();
		JsonArray RDCrootArray = new JsonArray();

		JsonObject EFC_Object = new JsonObject();
		JsonObject RDC_Object = new JsonObject();
		Gson gson = new GsonBuilder().create();

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		Iterator<String> itrdc = null;

		String commonTimerdc = "";

		Iterator<String> itval = null;

		String sqlidrdc = "";
		String valrdc = "";
		String descrdc = "";

		HashMap<String, Object> maprdc = null;

		String tsdescrdc = "Last Update: ";
		java.util.Date runtsrdc = null;
		SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

		while (it.hasNext()) {
			sqlid = it.next();

			HashMap<String, Object> map = null;
			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);

				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();
				tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();

			}

			if (desc.equals("RDC")) {
				itrdc = Constants.SQL_FRM_RDC_4.keySet().iterator();

				if (map != null) {
					itval = map.keySet().iterator();
					int count = 0;
					while (itval.hasNext()) {
						count++;
						String col = itval.next();
						val = map.get(col).toString();
						if (count % 2 == 0) {
							RDC_Object.addProperty("UNIQUE_SKU_COUNT", val);
						} else {
							RDC_Object.addProperty("UNIQUE_SKU", val);
						}
					}
				} else {
				}

				rootObject.add("RDC_TOTALS", RDC_Object);
				RDC_Object = new JsonObject();

				while (itrdc.hasNext()) {
					sqlidrdc = itrdc.next();
					valrdc = "";
					descrdc = "";

					maprdc = null;
					try {

						maprdc = ((MultiColumnModal) SQLUtils
								.getModelObject(sqlidrdc)).getColmap();
						descrdc = Constants.SQL_DESC.get(sqlidrdc);
						tsdescrdc = "Last Update: ";
						runtsrdc = (SQLUtils.getModelObject(sqlidrdc))
								.getLastresulttimestamp();
						tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

						if (runtsrdc != null) {
							tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
							if (commonTimerdc == "") {
								commonTimerdc = tsfrmtrdc.format(runtsrdc);
							}
						}

						RDC_Object.addProperty("DESCRIPTION", descrdc);

						if (maprdc != null) {
							itval = maprdc.keySet().iterator();
							int count = 0;

							while (itval.hasNext()) {
								count++;
								String col = itval.next();
								valrdc = maprdc.get(col).toString();
								if (count % 2 == 0) {
									RDC_Object.addProperty("UNIQUE_SKU_COUNT",
											valrdc);
								} else {
									RDC_Object
											.addProperty("UNIQUE_SKU", valrdc);
								}

							}
						} else {
							RDC_Object.addProperty("UNIQUE_SKU_COUNT", "0");
							RDC_Object.addProperty("UNIQUE_SKU", "0");
						}

						RDCrootArray.add(RDC_Object);
						RDC_Object = new JsonObject();
					} catch (Exception e) {
						logger.error("The following SQLID errored out:"
								+ sqlidrdc, e);
						e.printStackTrace();

					}
				}
			} else {
				EFC_Object.addProperty("DESCRIPTION", desc);

				if (map != null) {
					itval = map.keySet().iterator();
					int count = 0;
					while (itval.hasNext()) {
						count++;
						String col = itval.next();
						val = map.get(col).toString();
						if (count % 2 == 0) {
							EFC_Object.addProperty("UNIQUE_SKU_COUNT", val);
						} else {
							EFC_Object.addProperty("UNIQUE_SKU", val);
						}
					}
				} else {
				}

				EFCrootArray.add(EFC_Object);
				EFC_Object = new JsonObject();
			}

		}

		rootObject.addProperty("redundancy_last_run_timestamp", commonTime);
		rootObject.add("EFC", EFCrootArray);
		rootObject.add("RDC", RDCrootArray);

		return gson.toJson(rootObject);
	}

	public JsonObject StoreElf14DayFullfillmentPerformance(Iterator<String> it) {

		String commonTime = "";
		String sqlid = null;
		String desc = "";

		JsonObject root = new JsonObject();
		JsonArray rootArray = new JsonArray();
		JsonObject DAY_Object = new JsonObject();

		HashMap<String, Object> map = null;

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		String order_date = "-";
		String order_count = "-";
		String ttl_units = "-";
		String shpd = "-";
		String pend = "-";
		String cncl = "-";

		while (it.hasNext()) {
			sqlid = it.next();

			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				tsdesc = "Last Update: ";
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					commonTime = (commonTime == "") ? tsfrmt.format(runts)
							: commonTime;
				}

				DAY_Object.addProperty("DESCRIPTION", desc);

				order_date = "-";
				order_count = "-";
				ttl_units = "-";
				shpd = "0";
				pend = "0";
				cncl = "0";

				if (map != null) {
					if (map.get("ORDER_DATE") != null && map.get("ORDER_DATE").toString() != "")	order_date = map.get("ORDER_DATE").toString();
					if (map.get("ORDER_COUNT") != null && map.get("ORDER_COUNT").toString() != "")	order_count = map.get("ORDER_COUNT").toString();
					if (map.get("TTL_UNITS") != null && map.get("TTL_UNITS").toString() != "")		ttl_units = map.get("TTL_UNITS").toString();
					if (map.get("SHPD") != null && map.get("SHPD").toString() != "") 				shpd = map.get("SHPD").toString();
					if (map.get("PEND") != null && map.get("PEND").toString() != "") 				pend = map.get("PEND").toString();
					if (map.get("CNCL") != null && map.get("CNCL").toString() != "") 				cncl = map.get("CNCL").toString();

					DAY_Object.addProperty("ORDER_DATE", order_date);
					DAY_Object.addProperty("ORDER_COUNT",
							Integer.parseInt(order_count.replaceAll(",", "")));
					DAY_Object.addProperty("TTL_UNITS", ttl_units);
					DAY_Object.addProperty("SHPD", Double.parseDouble(shpd));
					DAY_Object.addProperty("PEND", Double.parseDouble(pend));
					DAY_Object.addProperty("CNCL", Double.parseDouble(cncl));
				}

				rootArray.add(DAY_Object);
				DAY_Object = new JsonObject();
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}
		}

		root.add("fulfillment_data", rootArray);
		root.addProperty("last_run_timestamp", commonTime);

		return root;
	}

	public String fullfillmentPerformance() {

		JsonObject root = new JsonObject();
		JsonObject EFCrootObject = new JsonObject();
		JsonObject RDCrootObject = new JsonObject();
		JsonObject EFC_Object = new JsonObject();
		JsonObject RDC_Object = new JsonObject();

		Gson gson = new GsonBuilder().create();

		String sqlid = "";
		String desc = "";

		Iterator<String> it = Constants.SQL_FRM_6.keySet().iterator();
		int icount = 0;
		String commonTime = "";

		String average = "0";
		String backlog = "0";
		String pickunitcnt = "0";
		String ttlshpunitcnt = "0";
		String cnclunitcnt = "0";
		String expfulfill = "0";

		String sqlidrdc = "";
		String descrdc = "";
		HashMap<String, Object> maprdc = null;
		HashMap<String, Object> map = null;

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		Iterator<String> itrdc = null;
		int icountrdc = 0;
		String tsdescrdc = "Last Update: ";
		java.util.Date runtsrdc = null;
		SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

		while (it.hasNext()) {
			sqlid = it.next();

			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}

			if (desc.equals("RDC")) {

				// add RDC totals first
				if (map != null) {
					if (map.get("AVERAGE") != null && map.get("AVERAGE").toString() != "") 				average = map.get("AVERAGE").toString();
					if (map.get("BACKLOG") != null && map.get("BACKLOG").toString() != "") 				backlog = map.get("BACKLOG").toString();
					if (map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString() != "")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if (map.get("TTLSHPUNITCNT") != null && map.get("TTLSHPUNITCNT").toString() != "")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if (map.get("CNCLUNITCNT") != null && map.get("CNCLUNITCNT").toString() != "")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if (map.get("EXPFULFILL") != null && map.get("EXPFULFILL").toString() != "")		expfulfill = map.get("EXPFULFILL").toString();
				} else {
					average = "0";
					backlog = "0";
					pickunitcnt = "0";
					ttlshpunitcnt = "0";
					cnclunitcnt = "0";
					expfulfill = "0";
				}

				RDC_Object.addProperty("AVERAGE",
						Double.parseDouble(average.replaceAll(",", "")));
				RDC_Object.addProperty("BACKLOG",
						Integer.parseInt(backlog.replaceAll(",", "")));
				RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
				RDC_Object.addProperty("TTLSHPUNITCNT",
						Integer.parseInt(ttlshpunitcnt.replaceAll(",", "")));
				RDC_Object.addProperty("CNCLUNITCNT",
						Integer.parseInt(cnclunitcnt.replaceAll(",", "")));
				RDC_Object.addProperty("EXPFULFILL", expfulfill);

				root.add("RDC_TOTALS", RDC_Object);

				RDC_Object = new JsonObject();
				// ------------

				itrdc = Constants.SQL_FRM_RDC_6.keySet().iterator();
				icountrdc = 0;
				while (itrdc.hasNext()) {
					sqlidrdc = itrdc.next();
					try {
						maprdc = ((MultiColumnModal) SQLUtils
								.getModelObject(sqlidrdc)).getColmap();
						descrdc = Constants.SQL_DESC.get(sqlidrdc);
						runtsrdc = (SQLUtils.getModelObject(sqlidrdc))
								.getLastresulttimestamp();
						tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

						if (runtsrdc != null) {
							tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
							if (commonTime == "") {
								commonTime = tsfrmtrdc.format(runtsrdc);
							}
						}

						if (maprdc != null) {
							if (maprdc.get("AVERAGE") != null && maprdc.get("AVERAGE").toString() != "") 				average = maprdc.get("AVERAGE").toString();
							if (maprdc.get("BACKLOG") != null && maprdc.get("BACKLOG").toString() != "") 				backlog = maprdc.get("BACKLOG").toString();
							if (maprdc.get("PICKUNITCNT") != null && maprdc.get("PICKUNITCNT").toString() != "")		pickunitcnt = maprdc.get("PICKUNITCNT").toString();
							if (maprdc.get("TTLSHPUNITCNT") != null && maprdc.get("TTLSHPUNITCNT").toString() != "")	ttlshpunitcnt = maprdc.get("TTLSHPUNITCNT").toString();
							if (maprdc.get("CNCLUNITCNT") != null && maprdc.get("CNCLUNITCNT").toString() != "")		cnclunitcnt = maprdc.get("CNCLUNITCNT").toString();
							if (maprdc.get("EXPFULFILL") != null && maprdc.get("EXPFULFILL").toString() != "")			expfulfill = maprdc.get("EXPFULFILL").toString();
						} else {
							average = "0";
							backlog = "0";
							pickunitcnt = "0";
							ttlshpunitcnt = "0";
							cnclunitcnt = "0";
							expfulfill = "0";
						}

						RDC_Object.addProperty("DESCRIPTION", descrdc);
						RDC_Object.addProperty("AVERAGE", Double.parseDouble(average.replaceAll(",", "")));
						RDC_Object.addProperty("BACKLOG",Integer.parseInt(backlog.replaceAll(",", "")));
						RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
						RDC_Object.addProperty("TTLSHPUNITCNT", Integer.parseInt(ttlshpunitcnt.replaceAll(",", "")));
						RDC_Object.addProperty("CNCLUNITCNT", Integer.parseInt(cnclunitcnt.replaceAll(",", "")));
						RDC_Object.addProperty("EXPFULFILL", expfulfill);

						RDCrootObject.add(icountrdc + "", RDC_Object);
						RDC_Object = new JsonObject();
						icountrdc++;
					} catch (Exception e) {
						logger.error("The following SQLID errored out:"
								+ sqlidrdc, e);
						e.printStackTrace();
					}
				}
			} else {

				EFC_Object.addProperty("DESCRIPTION", desc);

				if (map != null) {
					if (map.get("AVERAGE") != null && map.get("AVERAGE").toString() != "") 				average = map.get("AVERAGE").toString();
					if (map.get("BACKLOG") != null && map.get("BACKLOG").toString() != "") 				backlog = map.get("BACKLOG").toString();
					if (map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString() != "")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if (map.get("TTLSHPUNITCNT") != null && map.get("TTLSHPUNITCNT").toString() != "")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if (map.get("CNCLUNITCNT") != null && map.get("CNCLUNITCNT").toString() != "")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if (map.get("EXPFULFILL") != null && map.get("EXPFULFILL").toString() != "")		expfulfill = map.get("EXPFULFILL").toString();
				} else {
					average = "0";
					backlog = "0";
					pickunitcnt = "0";
					ttlshpunitcnt = "0";
					cnclunitcnt = "0";
					expfulfill = "0";
				}
				// sql for pending

				EFC_Object.addProperty("AVERAGE",Double.parseDouble(average.replaceAll(",", "").replace("-", "0")));
				EFC_Object.addProperty("BACKLOG",Integer.parseInt(backlog.replaceAll(",", "").replace("-", "0")));
				EFC_Object.addProperty("PICKUNITCNT", pickunitcnt);
				EFC_Object.addProperty("TTLSHPUNITCNT", Integer.parseInt(ttlshpunitcnt.replaceAll(",", "").replace("-", "0")));
				EFC_Object.addProperty("CNCLUNITCNT", Integer.parseInt(cnclunitcnt.replaceAll(",", "").replace("-","0")));
				EFC_Object.addProperty("EXPFULFILL", expfulfill);

				EFCrootObject.add(icount + "", EFC_Object);

				EFC_Object = new JsonObject();
				icount++;
			}
		}

		root.add("RDC", RDCrootObject);
		root.add("EFC", EFCrootObject);

		root.addProperty("last_run_timestamp", commonTime);

		return gson.toJson(root);
	}

	public String fullfillmentPerformance_vq() {

		JsonObject root = new JsonObject();
		JsonObject EFCrootObject = new JsonObject();
		JsonObject RDCrootObject = new JsonObject();
		new JsonObject();
		JsonObject EFC_Object = new JsonObject();
		JsonObject RDC_Object = new JsonObject();
		new JsonObject();

		Gson gson = new GsonBuilder().create();

		String sqlid = "";
		String desc = "";

		Iterator<String> it = Constants.SQL_FRM_VQ_6.keySet().iterator();
		Iterator<String> it_pend = Constants.SQL_FRM_VQ_PEND.keySet()
				.iterator();
		Constants.SQL_FRM_VQ_PEND.values()
				.iterator();

		int icount = 0;
		String commonTime = "";

		String average = "0";
		String backlog = "0";
		String pickunitcnt = "-";
		String ttlshpunitcnt = "0";
		String cnclunitcnt = "0";
		String expfulfill = "-";

		String sqlidrdc = "";
		String descrdc = "";
		HashMap<String, Object> maprdc = null;
		HashMap<String, Object> map = null;

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		Iterator<String> itrdc = null;
		int icountrdc = 0;
		String tsdescrdc = "Last Update: ";
		java.util.Date runtsrdc = null;
		SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

		while (it.hasNext()) {
			sqlid = it.next();

			map = null;

			try {

				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if (commonTime == "") {
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();

			}

			// refresh it_pend iterator
			it_pend = Constants.SQL_FRM_VQ_PEND.keySet().iterator();

			if (desc.equals("RDC")) {

				// add RDC totals first
				if (map != null) {
					if (map.get("AVERAGE") != null && map.get("AVERAGE").toString() != "") 				average = map.get("AVERAGE").toString();
					if (map.get("BACKLOG") != null && map.get("BACKLOG").toString() != "") 				backlog = map.get("BACKLOG").toString();
					if (map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString() != "")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if (map.get("TTLSHPUNITCNT") != null && map.get("TTLSHPUNITCNT").toString() != "")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if (map.get("CNCLUNITCNT") != null && map.get("CNCLUNITCNT").toString() != "")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if (map.get("EXPFULFILL") != null && map.get("EXPFULFILL").toString() != "") 		expfulfill = map.get("EXPFULFILL").toString();
				} else {
					average = "0";
					backlog = "0";
					pickunitcnt = "0";
					ttlshpunitcnt = "0";
					cnclunitcnt = "0";
					expfulfill = "0";
				}

				int QTY_TOTAL = 0;
				JsonObject RDC_Pending_Object = new JsonObject();
				JsonArray RDC_Pending_Array = new JsonArray();

				String outer_desc = desc;
				while (it_pend.hasNext()) {
					sqlid = it_pend.next();
					if (Constants.SQL_DESC.get(sqlid) == outer_desc) {

						map = null;

						try {
							ConcurrentHashMap<Integer, HashMap<String, Object>> map2 = ((MultiColumnModal) SQLUtils
									.getModelObject(sqlid)).getResultmap();
							desc = Constants.SQL_DESC.get(sqlid);
							runts = (SQLUtils.getModelObject(sqlid))
									.getLastresulttimestamp();

							if (runts != null) {
								tsdesc = tsdesc + tsfrmt.format(runts);
								if (commonTime == "") {
									commonTime = tsfrmt.format(runts);
								}
							}

							for (Entry<Integer, HashMap<String, Object>> entry : map2
									.entrySet()) {

								HashMap<String, Object> bottomMap = entry
										.getValue();

								String DESCRIP = bottomMap.get("DESCRIP")
										.toString();
								String QTY = bottomMap.get("QTY").toString();

								int QTY_VALUE = Integer.parseInt(QTY);
								QTY_TOTAL += QTY_VALUE;

								RDC_Pending_Object.addProperty("DESCRIP",
										DESCRIP);
								RDC_Pending_Object
										.addProperty("QTY", QTY_VALUE);

								RDC_Pending_Array.add(RDC_Pending_Object);
								RDC_Pending_Object = new JsonObject();
							}

							// add to objects here
							// -------------------
						} catch (Exception e) {
							logger.error("The following SQLID errored out:"
									+ sqlid, e);
							e.printStackTrace();

						}
						break;
					} else {
					}
				}

				RDC_Object.addProperty("TOTAL", QTY_TOTAL);
				RDC_Object.add("EFC Array", RDC_Pending_Array);

				RDC_Object.addProperty("AVERAGE",Double.parseDouble(average.replaceAll(",", "")));
				RDC_Object.addProperty("BACKLOG",Integer.parseInt(backlog.replaceAll(",", "")));
				RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
				RDC_Object.addProperty("TTLSHPUNITCNT",Integer.parseInt(ttlshpunitcnt.replaceAll(",", "")));
				RDC_Object.addProperty("CNCLUNITCNT",Integer.parseInt(cnclunitcnt.replaceAll(",", "")));
				RDC_Object.addProperty("EXPFULFILL", expfulfill);

				root.add("RDC_TOTALS", RDC_Object);

				RDC_Object = new JsonObject();
				// ------------

				itrdc = Constants.SQL_FRM_RDC_6.keySet().iterator();
				icountrdc = 0;
				while (itrdc.hasNext()) {
					sqlidrdc = itrdc.next();
					try {
						maprdc = ((MultiColumnModal) SQLUtils
								.getModelObject(sqlidrdc)).getColmap();
						descrdc = Constants.SQL_DESC.get(sqlidrdc);
						runtsrdc = (SQLUtils.getModelObject(sqlidrdc))
								.getLastresulttimestamp();
						tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

						if (runtsrdc != null) {
							tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
							if (commonTime == "") {
								commonTime = tsfrmtrdc.format(runtsrdc);
							}
						}

						if (maprdc != null) {
							if (maprdc.get("AVERAGE") != null && maprdc.get("AVERAGE").toString() != "") average = maprdc.get("AVERAGE").toString();
							if (maprdc.get("BACKLOG") != null && maprdc.get("BACKLOG").toString() != "") backlog = maprdc.get("BACKLOG").toString();
							if (maprdc.get("PICKUNITCNT") != null && maprdc.get("PICKUNITCNT").toString() != "") pickunitcnt = maprdc.get("PICKUNITCNT").toString();
							if (maprdc.get("TTLSHPUNITCNT") != null && maprdc.get("TTLSHPUNITCNT").toString() != "")ttlshpunitcnt = maprdc.get("TTLSHPUNITCNT").toString();
							if (maprdc.get("CNCLUNITCNT") != null && maprdc.get("CNCLUNITCNT").toString() != "") cnclunitcnt = maprdc.get("CNCLUNITCNT").toString();
							if (maprdc.get("EXPFULFILL") != null && maprdc.get("EXPFULFILL").toString() != "") expfulfill = maprdc.get("EXPFULFILL").toString();
						} else {
							average = "0";
							backlog = "0";
							pickunitcnt = "0";
							ttlshpunitcnt = "0";
							cnclunitcnt = "0";
							expfulfill = "0";
						}

						RDC_Object.addProperty("DESCRIPTION", descrdc);
						RDC_Object
								.addProperty("AVERAGE", Double
										.parseDouble(average
												.replaceAll(",", "")));
						RDC_Object.addProperty("BACKLOG",
								Integer.parseInt(backlog.replaceAll(",", "")));
						RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
						RDC_Object.addProperty("TTLSHPUNITCNT", Integer
								.parseInt(ttlshpunitcnt.replaceAll(",", "")));
						RDC_Object.addProperty("CNCLUNITCNT", Integer
								.parseInt(cnclunitcnt.replaceAll(",", "")));
						RDC_Object.addProperty("EXPFULFILL", expfulfill);

						RDCrootObject.add(icountrdc + "", RDC_Object);
						RDC_Object = new JsonObject();
						icountrdc++;
					} catch (Exception e) {
						logger.error("The following SQLID errored out:"
								+ sqlidrdc, e);
						e.printStackTrace();
					}
				}
			} else {

				EFC_Object.addProperty("DESCRIPTION", desc);

				if (map != null) {
					if (map.get("AVERAGE") != null && map.get("AVERAGE").toString() != "") 				average = map.get("AVERAGE").toString();
					if (map.get("BACKLOG") != null && map.get("BACKLOG").toString() != "") 				backlog = map.get("BACKLOG").toString();
					if (map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString() != "")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if (map.get("TTLSHPUNITCNT") != null && map.get("TTLSHPUNITCNT").toString() != "")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if (map.get("CNCLUNITCNT") != null && map.get("CNCLUNITCNT").toString() != "")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if (map.get("EXPFULFILL") != null && map.get("EXPFULFILL").toString() != "")		expfulfill = map.get("EXPFULFILL").toString();
				} else {
					average = "0";
					backlog = "0";
					pickunitcnt = "0";
					ttlshpunitcnt = "0";
					cnclunitcnt = "0";
					expfulfill = "0";
				}
				if (desc == "EFC 1-873" || desc == "EFC 2-809"
						|| desc == "EFC 3-819" || desc == "EFC 4-829") {

					int QTY_TOTAL = 0;
					JsonObject EFC_Pending_Object = new JsonObject();
					JsonArray EFC_Pending_Array = new JsonArray();

					String outer_desc = desc;
					while (it_pend.hasNext()) {
						sqlid = it_pend.next();
						if (Constants.SQL_DESC.get(sqlid) == outer_desc) {

							map = null;

							try {
								ConcurrentHashMap<Integer, HashMap<String, Object>> map2 = ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getResultmap();
								desc = Constants.SQL_DESC.get(sqlid);
								runts = (SQLUtils.getModelObject(sqlid))
										.getLastresulttimestamp();

								if (runts != null) {
									tsdesc = tsdesc + tsfrmt.format(runts);
									if (commonTime == "") {
										commonTime = tsfrmt.format(runts);
									}
								}

								for (Entry<Integer, HashMap<String, Object>> entry : map2
										.entrySet()) {

									HashMap<String, Object> bottomMap = entry
											.getValue();

									String DESCRIP = bottomMap.get("DESCRIP").toString();
									String QTY = bottomMap.get("ORD_QTY").toString();
									bottomMap.get("UNIT_QTY").toString();

									int QTY_VALUE = Integer.parseInt(QTY);
									QTY_TOTAL += QTY_VALUE;

									if (DESCRIP.equals("Printed")) {
										DESCRIP = "Waived";
									}
									EFC_Pending_Object.addProperty("DESCRIP",DESCRIP);
									EFC_Pending_Object.addProperty("QTY",QTY_VALUE);

									EFC_Pending_Array.add(EFC_Pending_Object);
									EFC_Pending_Object = new JsonObject();
								}

								// add to objects here
								// -------------------
							} catch (Exception e) {
								logger.error("The following SQLID errored out:"
										+ sqlid, e);
								e.printStackTrace();

							}
							break;
						} else {
						}
					}

					EFC_Object.addProperty("TOTAL", QTY_TOTAL);
					EFC_Object.add("EFC Array", EFC_Pending_Array);

					// end if
				} else if (desc == "STORES") {
					int QTY_TOTAL = 0;
					JsonObject EFC_Pending_Object = new JsonObject();
					JsonArray EFC_Pending_Array = new JsonArray();

					String outer_desc = desc;
					while (it_pend.hasNext()) {
						sqlid = it_pend.next();
						if (Constants.SQL_DESC.get(sqlid) == outer_desc) {

							map = null;

							try {
								ConcurrentHashMap<Integer, HashMap<String, Object>> map2 = ((MultiColumnModal) SQLUtils
										.getModelObject(sqlid)).getResultmap();
								desc = Constants.SQL_DESC.get(sqlid);
								runts = (SQLUtils.getModelObject(sqlid))
										.getLastresulttimestamp();

								if (runts != null) {
									tsdesc = tsdesc + tsfrmt.format(runts);
									if (commonTime == "") {
										commonTime = tsfrmt.format(runts);
									}
								}

								for (Entry<Integer, HashMap<String, Object>> entry : map2
										.entrySet()) {

									HashMap<String, Object> bottomMap = entry
											.getValue();

									String DESCRIP = bottomMap.get("DESCRIP")
											.toString();
									String QTY = bottomMap.get("QTY")
											.toString();

									int QTY_VALUE = Integer.parseInt(QTY);
									QTY_TOTAL += QTY_VALUE;

									EFC_Pending_Object.addProperty("DESCRIP",
											DESCRIP);
									EFC_Pending_Object.addProperty("QTY",
											QTY_VALUE);

									EFC_Pending_Array.add(EFC_Pending_Object);
									EFC_Pending_Object = new JsonObject();
								}

								// add to objects here
								// -------------------
							} catch (Exception e) {
								logger.error("The following SQLID errored out:"
										+ sqlid, e);
								e.printStackTrace();

							}
							break;
						} else {
						}
					}

					EFC_Object.addProperty("TOTAL", QTY_TOTAL);
					EFC_Object.add("EFC Array", EFC_Pending_Array);

					// end if
				} else if (desc == "BOPUS") {
					int QTY_TOTAL = 0;
					JsonObject EFC_Pending_Object = new JsonObject();
					JsonArray EFC_Pending_Array = new JsonArray();

					String outer_desc = desc;
					while (it_pend.hasNext()) {
						sqlid = it_pend.next();
						if (Constants.SQL_DESC.get(sqlid) == outer_desc) {

							map = null;

							try {
								ConcurrentHashMap<Integer, HashMap<String, Object>> map2 = ((MultiColumnModal) SQLUtils
										.getModelObject(sqlid)).getResultmap();
								desc = Constants.SQL_DESC.get(sqlid);
								runts = (SQLUtils.getModelObject(sqlid))
										.getLastresulttimestamp();

								if (runts != null) {
									tsdesc = tsdesc + tsfrmt.format(runts);
									if (commonTime == "") {
										commonTime = tsfrmt.format(runts);
									}
								}
								if (map2 != null) {
									for (Entry<Integer, HashMap<String, Object>> entry : map2
											.entrySet()) {

										HashMap<String, Object> bottomMap = entry
												.getValue();

										String DESCRIP = bottomMap.get(
												"DESCRIP").toString();
										String QTY = bottomMap.get("QTY")
												.toString();

										int QTY_VALUE = Integer.parseInt(QTY);
										QTY_TOTAL += QTY_VALUE;

										EFC_Pending_Object.addProperty(
												"DESCRIP", DESCRIP);
										EFC_Pending_Object.addProperty("QTY",
												QTY_VALUE);

										EFC_Pending_Array
												.add(EFC_Pending_Object);
										EFC_Pending_Object = new JsonObject();
									}
								}

								// add to objects here
								// -------------------
							} catch (Exception e) {
								logger.error("The following SQLID errored out:"
										+ sqlid, e);
								e.printStackTrace();

							}
							break;
						} else {
						}
					}

					EFC_Object.addProperty("TOTAL", QTY_TOTAL);
					EFC_Object.add("EFC Array", EFC_Pending_Array);

					// end if
				}

				if (desc != "BOPUS") {
					EFC_Object.addProperty("AVERAGE", Double
							.parseDouble(average.replaceAll(",", "").replace(
									"-", "0")));
					EFC_Object.addProperty("BACKLOG", Integer.parseInt(backlog
							.replaceAll(",", "").replace("-", "0")));
					EFC_Object.addProperty("PICKUNITCNT", pickunitcnt);
					EFC_Object.addProperty("TTLSHPUNITCNT", Integer
							.parseInt(ttlshpunitcnt.replaceAll(",", "")
									.replace("-", "0")));
					EFC_Object.addProperty("CNCLUNITCNT", Integer
							.parseInt(cnclunitcnt.replaceAll(",", "").replace(
									"-", "0")));
					EFC_Object.addProperty("EXPFULFILL", expfulfill);
				} else {
					EFC_Object.addProperty("AVERAGE", Double
							.parseDouble(average.replaceAll(",", "").replace(
									"-", "0")));
					EFC_Object.addProperty("BACKLOG", Integer.parseInt(backlog
							.replaceAll(",", "").replace("-", "0")));
					EFC_Object.addProperty("TTLSHPUNITCNT", Integer
							.parseInt(ttlshpunitcnt.replaceAll(",", "")
									.replace("-", "0")));
					EFC_Object.addProperty("CNCLUNITCNT", Integer
							.parseInt(cnclunitcnt.replaceAll(",", "").replace(
									"-", "0")));
				}
				EFCrootObject.add(icount + "", EFC_Object);
				EFC_Object = new JsonObject();
				icount++;

			}
		}

		root.add("RDC", RDCrootObject);
		root.add("EFC", EFCrootObject);

		root.addProperty("last_run_timestamp", commonTime);

		return gson.toJson(root);
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
	public void reload_properties(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/quick_metrics.jsp";

		String password = request.getParameter("password");
		String command_status = "-";

		logger.debug("DashboardServlet : reload_properties | " + requestedPage
				+ "|" + request.getParameter("chart"));

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
			} else {
				if (StringUtils.equals(password, "OmsStoreElf1")) {
					ReportActivator.reloadPropertiesFile();
					command_status = "PROPERTIES_FILE_RELOADED";
				} else {
					command_status = "wrong password";
				}
				request.getRequestDispatcher(
						defaultPage + "?COMMAND_STATUS=" + command_status
								+ "&include=" + jsp_include_page).forward(
						request, response);

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

	public void sales_metrics_visuals(String requestedPage,
			HttpServletRequest request, HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/storeelf_com_metrics/sales_metrics_visuals.jsp";

		logger.log(Level.INFO, "DashboardServlet:sales_metricsResponse | "
				+ requestedPage + "|" + request.getParameter("chart"));

		// JSON Structure
		JsonObject root_return = new JsonObject();
		new JsonArray();
		new JsonObject();
		new JsonArray();
		new JsonObject();

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				chart_type = request.getParameter("chart");
				responseWriter = response.getWriter();

				// content type MUST be "application/json"
				response.setContentType("application/json");

				/*
				 * Things to return 1. 14 day sales stats which include Order
				 * Date, Total "In Progress", Total "Shipped", Total "Cancelled"
				 * in Dollars 2. By day breakup of above three categories based
				 * on Node Grouping a. EFC1-4 b. LFC1-4 c. DSV d. SFS e. Bopus
				 */

				if ("14_day_sales_performance".equals(chart_type)) {
					JsonObject all = StoreElf14DaySalesPerformance(Constants.SQL_FRM_14_DAY_SALES_ALL
							.keySet().iterator());

					root_return.add("all", all);

				} else if ("14_day_sales_breakdown".equals(chart_type)) {
					JsonObject all = StoreElf14DaySalesBreakdown(Constants.SQL_FRM_14_DAY_SALES_BREAKDOWN
							.keySet().iterator());

					root_return.add("all", all);
				}

				// Convert HashMap into json object
				Gson gson = new GsonBuilder().create();
				response_content = gson.toJson(root_return);

				// write content to response writer, flush before closing ...
				// trust me on this one ...
				responseWriter.write(response_content);
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		}
	}

	public JsonObject StoreElf14DaySalesPerformance(Iterator<String> it) {

		String commonTime = "";
		String sqlid = null;
		JsonObject root = new JsonObject();
		JsonArray rootArray = new JsonArray();
		JsonObject DAY_Object = new JsonObject();

		ConcurrentHashMap<Integer, HashMap<String, Object>> map = null;

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		// string objects for the row
		String order_date = "0";
		String active_dollars = "0";
		String shipped_dollars = "0";
		String cancel_order_dollars = "0";

		while (it.hasNext()) {
			sqlid = it.next();

			map = null;

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getResultmap();
				Constants.SQL_DESC.get(sqlid);
				tsdesc = "Last Update: ";
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					commonTime = (commonTime == "") ? tsfrmt.format(runts)
							: commonTime;
				}

				// DAY_Object.addProperty("DESCRIPTION", desc);

				// loop through resultset
				for (HashMap<String, Object> row : map.values()) {

					order_date = "-";
					active_dollars = "0";
					shipped_dollars = "0";
					cancel_order_dollars = "0";

					if (row != null) {
						if (row.get("ORDER_DATE") != null
								&& row.get("ORDER_DATE").toString() != "")
							order_date = row.get("ORDER_DATE").toString();
						if (row.get("ACTIVE_DOLLARS") != null
								&& row.get("ACTIVE_DOLLARS").toString() != "")
							active_dollars = row.get("ACTIVE_DOLLARS")
									.toString();
						if (row.get("SHIPPED_DOLLARS") != null
								&& row.get("SHIPPED_DOLLARS").toString() != "")
							shipped_dollars = row.get("SHIPPED_DOLLARS")
									.toString();
						if (row.get("CANCEL_ORDER_DOLLARS") != null
								&& row.get("CANCEL_ORDER_DOLLARS").toString() != "")
							cancel_order_dollars = row.get(
									"CANCEL_ORDER_DOLLARS").toString();

						DAY_Object.addProperty("ORDER_DATE", order_date);
						DAY_Object.addProperty("ACTIVE_DOLLARS",
								Double.parseDouble(active_dollars));
						DAY_Object.addProperty("SHIPPED_DOLLARS",
								Double.parseDouble(shipped_dollars));
						DAY_Object.addProperty("CANCEL_ORDER_DOLLARS",
								Double.parseDouble(cancel_order_dollars));

					}

					rootArray.add(DAY_Object);
					DAY_Object = new JsonObject();
				}

			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}
		}

		root.add("sales_data", rootArray);
		root.addProperty("last_run_timestamp", commonTime);

		return root;
	}

	public JsonObject StoreElf14DaySalesBreakdown(Iterator<String> it)
			throws FileNotFoundException, SQLException, ClassNotFoundException,
			IOException {

		String commonTime = "";
		String sqlid = null;
		JsonObject root = new JsonObject();
		JsonArray days_array = new JsonArray();
		JsonObject day_object = new JsonObject();
		JsonArray day_rows = new JsonArray();
		JsonObject day_row = new JsonObject();

		ConcurrentHashMap<Integer, HashMap<String, Object>> map = null;

		String tsdesc = "Last Update: ";
		java.util.Date runts = null;
		SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

		// string objects for the row
		String order_date = "-";
		String ship_node = "-";
		String node_type = "-";
		String active_dollars = "0";
		String shipped_dollars = "0";
		String cancel_order_dollars = "0";

		// for date comparison as we are spanning multiple days
		String this_order_date = "-";
		String last_order_date = "-";
		int count = 0;

		while (it.hasNext()) {
			sqlid = it.next();

			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid))
						.getResultmap();
				Constants.SQL_DESC.get(sqlid);
				tsdesc = "Last Update: ";
				runts = (SQLUtils.getModelObject(sqlid))
						.getLastresulttimestamp();

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					commonTime = (commonTime == "") ? tsfrmt.format(runts)
							: commonTime;
				}

				// loop through resultset
				for (HashMap<String, Object> row : map.values()) {

					order_date = "-";
					ship_node = "-";
					node_type = "-";
					active_dollars = "0";
					shipped_dollars = "0";
					cancel_order_dollars = "0";

					if (row != null) {

						if (map.get("ORDER_DATE") != null
								&& map.get("ORDER_DATE").toString() != "")
							order_date = map.get("ORDER_DATE").toString();
						if (map.get("SHIP_NODE") != null
								&& map.get("SHIP_NODE").toString() != "")
							ship_node = map.get("SHIP_NODE").toString();
						if (map.get("NODE_TYPE") != null
								&& map.get("NODE_TYPE").toString() != "")
							node_type = map.get("NODE_TYPE").toString();
						if (map.get("ACTIVE_DOLLARS") != null
								&& map.get("ACTIVE_DOLLARS").toString() != "")
							active_dollars = map.get("ACTIVE_DOLLARS")
									.toString();
						if (map.get("SHIPPED_DOLLARS") != null
								&& map.get("SHIPPED_DOLLARS").toString() != "")
							shipped_dollars = map.get("SHIPPED_DOLLARS")
									.toString();
						if (map.get("CANCEL_ORDER_DOLLARS") != null
								&& map.get("CANCEL_ORDER_DOLLARS").toString() != "")
							cancel_order_dollars = map.get(
									"CANCEL_ORDER_DOLLARS").toString();

						if (count == 0) {
							// first time through resultset, set this, and last
							// order_date as same
							last_order_date = order_date;
							this_order_date = order_date;
						} else {
							// not first time
							last_order_date = this_order_date;
							this_order_date = order_date;
						}

						if (!last_order_date.equalsIgnoreCase(this_order_date)) {
							// we have shifted to a new day, this means new day
							// object, new rows_array
							// add the rows for the current day to an object
							day_object.add("day_rows", day_rows);
							// add the current day to the array of days
							days_array.add(day_object);
							// reset the day object
							day_object = new JsonObject();
							// reset the rows for the current day
							days_array = new JsonArray();
							// reset the current row being added to as it
							// belongs to a new day
							day_row = new JsonObject();
						}
						day_row.addProperty("ORDER_DATE", order_date);
						day_row.addProperty("SHIP_NODE", ship_node);
						day_row.addProperty("NODE_TYPE", node_type);
						day_row.addProperty("ACTIVE_DOLLARS",
								Double.parseDouble(active_dollars));
						day_row.addProperty("SHIPPED_DOLLARS",
								Double.parseDouble(shipped_dollars));
						day_row.addProperty("CANCEL_ORDER_DOLLARS",
								Double.parseDouble(cancel_order_dollars));

						day_rows.add(day_row);
					}

				}

				// add the array of days to the root
				root.add("sales_data", days_array);
				root.addProperty("last_run_timestamp", commonTime);

			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
				e.printStackTrace();
			}
		}

		return root;

	}

	public void setHeaderDownloadableCSV(String filename, HttpServletResponse response){
		if(response.getHeader("Content-Type") != "text/csv"){
			response.setContentType("text/csv");
			response.addHeader("Content-Type", "text/csv");
			response.addHeader("Content-Disposition", "attachment;filename=\""+filename+"_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
			response.setHeader("Content-Disposition", "attachment;filename=\""+filename+"_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
		}				
	}
}
