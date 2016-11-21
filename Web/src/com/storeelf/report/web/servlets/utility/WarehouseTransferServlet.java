package com.storeelf.report.web.servlets.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.StringUtils;

public class WarehouseTransferServlet extends StoreElfHttpServlet<Object> {

	static final Logger			logger				= Logger.getLogger(WarehouseTransferServlet.class);
	private static final long	serialVersionUID	= 1L;
	/*private String				error				= "error-response";*/
	private String				defaultPage			= "/utility_includes/utility.jsp";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WarehouseTransferServlet() {
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
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public void transfer_orders(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException{
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/warehouse_transfer/"+requestedPage+".jsp";

		logger.debug(requestedPage + "|0" );

		String exportResult		= request.getParameter("export_result_csv");
		String ajax_request		= request.getParameter("ajax_request");
		
		boolean isPost = StringUtils.equals(request.getMethod(),"POST");
		boolean	isGet = StringUtils.equals(request.getMethod(),"GET");
		boolean isExport = org.apache.commons.lang.StringUtils.equals(exportResult, "true");
		boolean isAjax	= org.apache.commons.lang.StringUtils.equals(ajax_request, "true");
		Connection con 				= ReportActivator.getInstance().getConnection(Constants.OMS);
		String sql 					= null;
		ResultSet result 			= null;
		try{ logger.info("method  --> " + request.getMethod());
			if(		isPost ||  (isGet && (isExport||isAjax))		){
				
				//handle request parameters here
				responseWriter	= response.getWriter();

				saveSearch(request)
				.add("order_number",		request.getParameter("order_number"))
				.add("source_node", 		request.getParameter("source_node"))
				.add("receiving_node", 		request.getParameter("receiving_node"))
				.add("shipment_number", 	request.getParameter("shipment_number"))
				.add("from_shipment_date",	request.getParameter("from_shipment_date"))
				.add("to_shipment_date", 	request.getParameter("to_shipment_date"))
				.add("transfer_type", 		request.getParameter("transfer_type"))
				.add("dept", 				request.getParameter("dept"))
				.add("sub_cl", 				request.getParameter("sub_cl"))
				.add("cl", 					request.getParameter("cl"))
				.add("item_id", 			request.getParameter("item_id"))
				.add("from_order_date", 	request.getParameter("from_order_date"))
				.add("to_order_date", 		request.getParameter("to_order_date"))
				.add("export_result_csv", 	request.getParameter("export_result_csv"))
				.save();

				logger.debug(requestedPage + "|1 POST" ); 
				
				String exportResultDataHeaders	= "";
				String exportResultData			= "ORDER_NO,ORDER_DATE,STATUS,STATUS_DATE,TRANSFER_TYPE,ITEM_COUNT,TOTAL_UNITS,SHIPPED_UNITS,CANCELLED_UNITS,UNIT_VARIANCE,ENTERED_BY\n";
				
				//if user requested a data export, export it otherwise don't trigger download
				boolean triggerDownload			= (org.apache.commons.lang.StringUtils.equals(exportResult, "true")) ? true : false;

				//content type MUST be "application/json"
				response.setContentType("application/json");

				Gson 		gson				= new GsonBuilder().create();

				/*HashMap<Integer, HashMap<String, Object>> map = null;*/
				JsonObject root = new JsonObject();

/*				if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
					logger.debug(requestedPage + "|X---X " +"-----------------------------------" );
					logger.debug(requestedPage + "|X---X \n\n" + getSessionAttribute("transfer_order_result_csv") );
					logger.debug(requestedPage + "|X---X " +"-----------------------------------" );

					response.addHeader("Content-Type", "text/csv");
					response.addHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
					response.setHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");

					response_content = (String) getSessionAttribute("transfer_order_result_csv");
				}else{*/
					if(request.getParameter("showPage") != null){
						logger.debug(requestedPage + "|2 POST" );

						if ((request.getParameter("showPage")).equals("Next")){
							logger.debug(requestedPage + "|2.1 " );
							String strResult = (String) request.getSession().getAttribute("transfer_order_result");
							root.addProperty("transfer_order_result", strResult);
						}
						if ((request.getParameter("showPage")).equals("Previous")){
							logger.debug(requestedPage + "|2.2 " );
							String strResult = (String) request.getSession().getAttribute("transfer_order_result");
							root.addProperty("transfer_order_result", strResult);
						}
					}
					else if(request.getParameter("order_number") == null){
						logger.debug(requestedPage + "|3" );
						String strEntries = request.getParameter("entries");

						request.getSession().setAttribute("ShowTransferOrderRecords",strEntries);
						request.getSession().setAttribute("TransferOrderRecordNo",strEntries);

						String strResult = (String) request.getSession().getAttribute("transfer_order_result");

						root.addProperty("show_transfer_order_records", strEntries);
						root.addProperty("transfer_order_record_no", strEntries);
						root.addProperty("transfer_order_result", strResult);
					}
					else{
						logger.debug(requestedPage + "|4 #" );
						String strOrderNo			= (request.getParameter("order_number")			+"").trim();
						String strSourceNode		= (request.getParameter("source_node")			+"").trim();
						String strOrderStatus		= (request.getParameter("order_status")			+"").trim();
						String strReceivingNode		= (request.getParameter("receiving_node")		+"").trim();
						String strShipmentNo		= (request.getParameter("shipment_number")		+"").trim();
						String strFromShipmentDate	= (request.getParameter("from_shipment_date")	+"").trim();
						String strToShipmentDate	= (request.getParameter("to_shipment_date")		+"").trim();

						String strTransferType		= (request.getParameter("transfer_type")		+"").trim();
						String strDept				= (request.getParameter("dept")					+"").trim();
						String strSUBCL				= (request.getParameter("sub_cl")				+"").trim();
						String strCL				= (request.getParameter("cl")					+"").trim();
						String strItemID			= (request.getParameter("item_id")				+"").trim();
						String strFromOrderDate 	= (request.getParameter("from_order_date")		+"").trim();
						String strToOrderDate		= (request.getParameter("to_order_date")		+"").trim();
						//String strStatus			= (request.getParameter("order_status")		+"").trim();

						HashMap<String, String> hmParams = new HashMap<String, String>();
						
						if ((org.apache.commons.lang.StringUtils.isBlank(strShipmentNo) && org.apache.commons.lang.StringUtils.isBlank(strOrderNo)) &&
																 (org.apache.commons.lang.StringUtils.isBlank(strFromOrderDate) || org.apache.commons.lang.StringUtils.isBlank(strToOrderDate)) &&
																							(org.apache.commons.lang.StringUtils.isBlank(strFromShipmentDate) || org.apache.commons.lang.StringUtils.isBlank(strToShipmentDate))){
							logger.debug(requestedPage + "|5 ");
							root.addProperty("error", "5 | Please enter either order date range or shipment date range as search criteria");
						}
						else if ((org.apache.commons.lang.StringUtils.isNotBlank(strFromOrderDate) || org.apache.commons.lang.StringUtils.isNotBlank(strToOrderDate)) &&
																		(org.apache.commons.lang.StringUtils.isNotBlank(strFromShipmentDate) || org.apache.commons.lang.StringUtils.isNotBlank(strToShipmentDate))){
							logger.debug(requestedPage + "|6 ");
							root.addProperty("error", "6 | Please enter either order dates or shipment dates as search criteria");
						}
						else if (org.apache.commons.lang.StringUtils.isBlank(strShipmentNo) &&
											(org.apache.commons.lang.StringUtils.isNotBlank(strOrderNo) || (org.apache.commons.lang.StringUtils.isNotBlank(strFromOrderDate) && org.apache.commons.lang.StringUtils.isNotBlank(strToOrderDate)))) {
							logger.debug(requestedPage + "|7 ");

							sql = "with orderheader as " +
									"(select distinct h.order_no,h.order_date,h.entered_by,h.order_header_key,h.order_type ,s.status from " +
									"yfs_order_header h ," +
									"yfs_order_line l," +
									"yfs_item i," +
									"yfs_order_release_status s where ";

									sql = setSQLArguments(sql ,hmParams, strOrderNo, strFromOrderDate, strToOrderDate, strItemID, strDept, strCL, strSUBCL, strTransferType, strSourceNode, strReceivingNode, strFromShipmentDate, strToShipmentDate);

							sql = sql +  " h.document_type='0006' and ";
							if (org.apache.commons.lang.StringUtils.isNotBlank(strOrderStatus)) {
								logger.debug(requestedPage + "|7.1 ");
								hmParams.put("OrderStatus", strOrderStatus);
								sql = sql + "s.status =(select distinct status from yfs_status where process_type_key='20030708142438541' and status_name='"+strOrderStatus+"') and ";
							}
							sql = sql +  " h.order_header_key=l.order_header_key and l.item_id=i.item_id  and l.order_line_key=s.order_line_key and s.status_quantity > 0 ),  " +
									"sku_count as " +
									"(select count(distinct(l.item_id)) skucount,l.order_header_key from " +
									"yfs_order_line l,orderheader h where h.order_header_key=l.order_header_key group by l.order_header_key),  " +

									"final_qry as " +
									"(SELECT h.order_no,h.order_date,h.entered_by,h.order_header_key,h.order_type,ct.skucount,  " +

									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='9000'),0) Cancel_Qty,  " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3700'),0) Ship_Qty,  " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3200'),0) Rel_Qty, " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3200.01'),0) SenttoWmoS_Qty,  " +
									"" +
									"(select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0) totqty,  " +
									"(select max(s.status_date) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0) status_date  " +

									"FROM orderheader h,sku_count ct where h.order_header_key=ct.order_header_key)  " +

									"select distinct r.order_no as ORDER_NO,r.order_date ORDER_DATE, " +
									"CASE WHEN ((r.SenttoWmoS_Qty=0) AND (r.Ship_Qty+r.Cancel_Qty=r.totqty) AND (r.Cancel_Qty!=r.totqty)) THEN 'Shipped' " +
									"WHEN ((r.SenttoWmoS_Qty=0) AND (r.Cancel_Qty=r.totqty)) THEN 'Cancelled' " +
									"WHEN ((r.SenttoWmoS_Qty=0) AND (r.Ship_Qty=r.totqty)) THEN 'Shipped' " +
									"WHEN ((r.SenttoWmoS_Qty>0) AND (r.SenttoWmoS_Qty+r.Cancel_Qty=r.totqty)) THEN 'Sent Release To WMoS' " +
									"WHEN ((r.SenttoWmoS_Qty>0) AND (r.Ship_Qty>0)) THEN 'Partially Shipped' " +
									"WHEN ((r.Rel_Qty>0) AND (r.SenttoWmoS_Qty>0)) THEN 'Partially Sent Release To WMoS' " +
									"WHEN ((r.Rel_Qty>0) AND (r.Rel_Qty+r.Cancel_Qty=r.totqty) AND (r.Cancel_Qty!=r.totqty)) THEN 'Released' " +
									"ELSE ((select distinct status_name from yfs_status where process_type_key='20030708142438541' and status=(select max(status) from yfs_order_release_status s where s.order_header_key=r.order_header_key and s.status_quantity > 0 ))) " +
									"END as status, " +
									"r.status_date,r.order_type TRANSFER_TYPE,r.skucount ITEM_COUNT,r.totqty TOTAL_UNITS, " +
									"r.Ship_Qty SHIPPED_UNITS,r.Cancel_Qty CANCELLED_UNITS,(r.totqty-(r.Ship_Qty+r.Cancel_Qty)) as UNIT_VARIANCE,r.entered_by " +
									"from final_qry r order by r.order_no";

							//HashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

							result = con.createStatement().executeQuery(sql);

							JsonArray	table	= new JsonArray();
							JsonObject	row		= new JsonObject();

							int total_record_count = 0;

							logger.debug("Loading results ... | 7.2 ");

							String ORDER_NO			= "";
							String ORDER_DATE		= "";
							String STATUS 			= "";
							String STATUS_DATE		= "";
							String TRANSFER_TYPE	= "";
							String ITEM_COUNT 		= "";
							String TOTAL_UNITS		= "";
							String SHIPPED_UNITS	= "";
							String CANCELLED_UNITS	= "";
							String UNIT_VARIANCE	= "";
							String ENTERED_BY		= "";
							
							int rowCount = 0;

							while(result.next()){
								ORDER_NO		= result.getString("ORDER_NO");
								ORDER_DATE		= result.getString("ORDER_DATE");
								STATUS 			= result.getString("STATUS");
								STATUS_DATE		= result.getString("STATUS_DATE");
								TRANSFER_TYPE	= result.getString("TRANSFER_TYPE");
								ITEM_COUNT 		= result.getString("ITEM_COUNT");
								TOTAL_UNITS		= result.getString("TOTAL_UNITS");
								SHIPPED_UNITS	= result.getString("SHIPPED_UNITS");
								CANCELLED_UNITS	= result.getString("CANCELLED_UNITS");
								UNIT_VARIANCE	= result.getString("UNIT_VARIANCE");
								ENTERED_BY		= result.getString("ENTERED_BY");


								//if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){

									if(exportResultDataHeaders.length()==0){
										//response.addHeader("Content-Type", "text/csv");
									    //response.addHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
									    //response.setHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
										//TODO write csv headers
										for(int i=0;i<result.getMetaData().getColumnCount();i++){
											logger.log(Level.DEBUG, "Column name:"+result.getMetaData().getColumnName(i+1));
											exportResultDataHeaders += result.getMetaData().getColumnName(i+1)+",";
											if((i+1)==result.getMetaData().getColumnCount()-1) exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length()-1);
										}
									}

									exportResultData += ORDER_NO		+ ",";
									exportResultData += ORDER_DATE		+ ",";
									exportResultData += STATUS 			+ ",";
									exportResultData += STATUS_DATE		+ ",";
									exportResultData += TRANSFER_TYPE	+ ",";
									exportResultData += ITEM_COUNT 		+ ",";
									exportResultData += TOTAL_UNITS		+ ",";
									exportResultData += SHIPPED_UNITS	+ ",";
									exportResultData += CANCELLED_UNITS	+ ",";
									exportResultData += UNIT_VARIANCE	+ ",";
									exportResultData += ENTERED_BY		+ "";

									exportResultData += "\n"; 
								
								if(rowCount < 500 && triggerDownload==false){
									row.addProperty("ORDER_NO",			ORDER_NO);
									row.addProperty("ORDER_DATE",		ORDER_DATE);
									row.addProperty("STATUS", 			STATUS);
									row.addProperty("STATUS_DATE",		STATUS_DATE);
									row.addProperty("TRANSFER_TYPE",	TRANSFER_TYPE);
									row.addProperty("ITEM_COUNT", 		ITEM_COUNT);
									row.addProperty("TOTAL_UNITS",		TOTAL_UNITS);
									row.addProperty("SHIPPED_UNITS",	SHIPPED_UNITS);
									row.addProperty("CANCELLED_UNITS",	CANCELLED_UNITS);
									row.addProperty("UNIT_VARIANCE",	UNIT_VARIANCE);
									row.addProperty("ENTERED_BY",		ENTERED_BY);
									table.add(row);
									row = new JsonObject();
								}else{ 
									triggerDownload= true;
									setHeaderDownloadableCSV("transferOrder", response); 
									responseWriter.write(exportResultData);
									responseWriter.flush();
									exportResultData = "";
								}
								
								rowCount++;
								total_record_count++;
							}

							logger.debug(total_record_count+" Results loaded | 7.3 ");

							if(triggerDownload==false){
								response.setContentType("application/json"); 
								root.addProperty("show_transfer_order_records", "10");
								root.addProperty("transfer_order_record_no", "10");
								root.add("transfer_order_result", table);
								//root.addProperty("transfer_order_result_str", strResult);
							}

						}
						else {
							logger.debug(requestedPage + "|8 ");
							sql = "with orderheader as " +
									"(select distinct h.order_no,h.order_date,h.entered_by,h.order_header_key,h.order_type from " +
									"yfs_order_header h,yfs_order_line l,yfs_shipment sh,yfs_shipment_line sl,yfs_item i ,yfs_order_release_status s " +
									"where ";
								if (org.apache.commons.lang.StringUtils.isNotBlank(strShipmentNo)) {
									logger.debug(requestedPage + "|8.1 ");
									hmParams.put("ShipmentNo", strShipmentNo);
									sql = sql + "sh.shipment_no='" + strShipmentNo + "' and ";
								}

								sql = setSQLArguments(sql ,hmParams, strOrderNo, strFromOrderDate, strToOrderDate, strItemID, strDept, strCL, strSUBCL, strTransferType, strSourceNode, strReceivingNode, strFromShipmentDate, strToShipmentDate);
								if (org.apache.commons.lang.StringUtils.isNotBlank(strOrderStatus)) {
									logger.debug(requestedPage + "|8.2 ");
									hmParams.put("OrderStatus", strOrderStatus);
									sql = sql + "s.status =(select distinct status from yfs_status where process_type_key='20030708142438541' and status_name='"+strOrderStatus+"') and ";
								}
								//sql = sql + " h.document_type='0006' and ";
								sql = sql + " h.document_type='0006' and sh.document_type='0006' and " +
										"sh.shipment_key=sl.shipment_key and sl.order_line_key=l.order_line_key and h.order_header_key=l.order_header_key and l.item_id=i.item_id and " +
										"l.order_line_key=s.order_line_key and s.status_quantity > 0),  " +

									"sku_count as " +
									"(select count(distinct(l.item_id)) skucount,sum(l.original_ordered_qty) totqty,l.order_header_key from yfs_order_line l,orderheader h where " +
									"h.order_header_key=l.order_header_key group by l.order_header_key),  " +

									"final_qry as (SELECT h.order_no,h.order_date,h.entered_by,h.order_header_key,h.order_type,ct.skucount,  " +

									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='9000'),0) Cancel_Qty,  " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3700'),0) Ship_Qty,  " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3200'),0) Rel_Qty, " +
									"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0 and s.status='3200.01'),0) SenttoWmoS_Qty,  " +

									"(select sum(s.status_quantity) from yfs_order_release_status s where s.order_header_key=h.order_header_key and s.status_quantity > 0) totqty  FROM orderheader h,sku_count ct where h.order_header_key=ct.order_header_key)  " +

									"select distinct r.order_no as ORDER_NO,r.order_date ORDER_DATE, CASE " +

									"WHEN ((r.SenttoWmoS_Qty=0) AND (r.Ship_Qty+r.Cancel_Qty=r.totqty) AND (r.Cancel_Qty!=r.totqty)) THEN 'Shipped' " +
									"WHEN ((r.SenttoWmoS_Qty=0) AND (r.Cancel_Qty=r.totqty)) THEN 'Cancelled' " +
									"WHEN ((r.SenttoWmoS_Qty=0) AND (r.Ship_Qty=r.totqty)) THEN 'Shipped' " +
									"WHEN ((r.SenttoWmoS_Qty>0) AND (r.SenttoWmoS_Qty+r.Cancel_Qty=r.totqty)) THEN 'Sent Release To WMoS' " +
									"WHEN ((r.SenttoWmoS_Qty>0) AND (r.Ship_Qty>0)) THEN 'Partially Shipped' " +
									"WHEN ((r.Rel_Qty>0) AND (r.SenttoWmoS_Qty>0)) THEN 'Partially Sent Release To WMoS' " +
									"WHEN ((r.Rel_Qty>0) AND (r.Rel_Qty+r.Cancel_Qty=r.totqty) AND (r.Cancel_Qty!=r.totqty)) THEN 'Released' " +
									"ELSE ((select distinct status_name from yfs_status where " +
									"process_type_key='20030708142438541' and status=(select max(status) from " +
									"yfs_order_release_status s where s.order_header_key=r.order_header_key and s.status_quantity > 0 ))) " +
									"END as status, r.order_type TRANSFER_TYPE,r.skucount ITEM_COUNT,r.totqty TOTAL_UNITS, r.Ship_Qty SHIPPED_UNITS,r.Cancel_Qty CANCELLED_UNITS," +
									"(r.totqty-(r.Ship_Qty+r.Cancel_Qty)) as UNIT_VARIANCE,r.entered_by " +
									"from final_qry r order by r.order_no";

									//HashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

									result = con.createStatement().executeQuery(sql);

									logger.debug("SQL | \n"+sql);

									JsonArray	table	= new JsonArray();
									JsonObject	row		= new JsonObject();

									int total_record_count = 0;

									logger.debug("Loading results ... | 8.3 ");

									String ORDER_NO			= "";
									String ORDER_DATE		= "";
									String STATUS 			= "";
									String STATUS_DATE		= "";
									String TRANSFER_TYPE	= "";
									String ITEM_COUNT 		= "";
									String TOTAL_UNITS		= "";
									String SHIPPED_UNITS	= "";
									String CANCELLED_UNITS	= "";
									String UNIT_VARIANCE	= "";
									String ENTERED_BY		= "";
									
									int rowCount = 0;

									while(result.next()){
										ORDER_NO		= result.getString("ORDER_NO");
										ORDER_DATE		= result.getString("ORDER_DATE");
										STATUS 			= result.getString("STATUS");
										STATUS_DATE		= result.getString("STATUS_DATE");
										TRANSFER_TYPE	= result.getString("TRANSFER_TYPE");
										ITEM_COUNT 		= result.getString("ITEM_COUNT");
										TOTAL_UNITS		= result.getString("TOTAL_UNITS");
										SHIPPED_UNITS	= result.getString("SHIPPED_UNITS");
										CANCELLED_UNITS	= result.getString("CANCELLED_UNITS");
										UNIT_VARIANCE	= result.getString("UNIT_VARIANCE");
										ENTERED_BY		= result.getString("ENTERED_BY");



										//if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){

											if(exportResultDataHeaders.length()==0){
												//response.addHeader("Content-Type", "text/csv");
											    //response.addHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
											    //response.setHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
												//TODO write csv headers
												for(int i=0;i<result.getMetaData().getColumnCount();i++){
													logger.log(Level.DEBUG, "Column name:"+result.getMetaData().getColumnName(i+1));
													exportResultDataHeaders += result.getMetaData().getColumnName(i+1)+",";
													if((i+1)==result.getMetaData().getColumnCount()-1) exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length()-1);
												}
											}

											exportResultData += ORDER_NO		+ ",";
											exportResultData += ORDER_DATE		+ ",";
											exportResultData += STATUS 			+ ",";
											exportResultData += STATUS_DATE		+ ",";
											exportResultData += TRANSFER_TYPE	+ ",";
											exportResultData += ITEM_COUNT 		+ ",";
											exportResultData += TOTAL_UNITS		+ ",";
											exportResultData += SHIPPED_UNITS	+ ",";
											exportResultData += CANCELLED_UNITS	+ ",";
											exportResultData += UNIT_VARIANCE	+ ",";
											exportResultData += ENTERED_BY		+ "";

											exportResultData += "\n";  
										
										if(rowCount < 500 && triggerDownload==false){
											row.addProperty("ORDER_NO",			ORDER_NO);
											row.addProperty("ORDER_DATE",		ORDER_DATE);
											row.addProperty("STATUS", 			STATUS);
											//row.addProperty("STATUS_DATE",	STATUS_DATE);
											row.addProperty("TRANSFER_TYPE",	TRANSFER_TYPE);
											row.addProperty("ITEM_COUNT", 		ITEM_COUNT);
											row.addProperty("TOTAL_UNITS",		TOTAL_UNITS);
											row.addProperty("SHIPPED_UNITS",	SHIPPED_UNITS);
											row.addProperty("CANCELLED_UNITS",	CANCELLED_UNITS);
											row.addProperty("UNIT_VARIANCE",	UNIT_VARIANCE);
											row.addProperty("ENTERED_BY",		ENTERED_BY);
											table.add(row);
											row = new JsonObject();
										}else{ 
											triggerDownload= true;
											setHeaderDownloadableCSV("transferOrder", response); 
											responseWriter.write(exportResultData);
											responseWriter.flush();
											exportResultData = "";
										}
										rowCount++;
										total_record_count++;
									}
									
									if(triggerDownload==false){
										response.setContentType("application/json"); 
										root.addProperty("show_transfer_order_records", "10");
										root.addProperty("transfer_order_record_no", "10");
										root.add("transfer_order_result", table);
										//root.addProperty("transfer_order_result_str", strResult);
									}

									logger.debug(total_record_count+" Results loaded | 8.4");
 
						}
						if(con != null) con.close();
						if(result != null) result.close();
						logger.debug(requestedPage + "|9 ");
					} 


					logger.debug(requestedPage + "|10 ");
					logger.debug(requestedPage + "|XX "+ request.getSession().getId());
					logger.debug(requestedPage + "|XX "+ request.getSession().getCreationTime());
					logger.debug(requestedPage + "|XX "+ request.getSession().getLastAccessedTime());
					logger.debug(requestedPage + "|XX " +"-----------------------------------" );
					logger.debug(requestedPage + "|XX \n\n" + getSessionAttribute("transfer_order_result_csv") );
					logger.debug(requestedPage + "|XX " +"-----------------------------------" );
					
					if(triggerDownload==false){ 
						//Convert HashMap into json object
						response_content	= gson.toJson(root);
						responseWriter.write(response_content);
					}
				
					//write content to response writer, flush before closing ... trust me on this one ...
					responseWriter.flush();
					responseWriter.close();
				
				}else{
					logger.debug(requestedPage + "|11 ");
					//assume it's GET request, load JSP
					//request.getRequestDispatcher(jsp_page).include(request, response);
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}

		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally{
			if(result!=null){result.close();}
			if(con!=null){con.close();}
		}
	}

	private String setSQLArguments(String sql,HashMap<String, String> hmParams, String strOrderNo, String strFromOrderDate, String strToOrderDate, String strItemID, String strDept, String strCL, String strSUBCL, String strTransferType, String strSourceNode, String strReceivingNode, String strFromShipmentDate, String strToShipmentDate){
		if (org.apache.commons.lang.StringUtils.isNotBlank(strFromShipmentDate)) {
			//hmParams.put("ShipmentDate", strFromShipmentDate);
			strFromShipmentDate = strFromShipmentDate.replaceAll("-",",");
			strFromShipmentDate = strFromShipmentDate.replaceAll(" ",",");
			strFromShipmentDate = strFromShipmentDate.replaceAll(":",",");
			String splitFromShipmentDate[] = strFromShipmentDate.split(",");
			strFromShipmentDate = splitFromShipmentDate[2]+splitFromShipmentDate[0]+splitFromShipmentDate[1]+splitFromShipmentDate[3]+splitFromShipmentDate[4]+splitFromShipmentDate[5];

			sql = sql + " sh.shipment_key > '"+strFromShipmentDate+"' and ";
			//sql + "sl.modifyts = to_date('"+strFromShipmentDate +"','MM-dd-yyyy HH24:MI:SS') and ";
			//sql = sql + "sh.modifyts > to_date('"+strFromShipmentDate +"','MM-dd-yyyy HH24:MI:SS') and ";
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strToShipmentDate)) {
			//hmParams.put("ShipmentDate", strToShipmentDate);
			//sql + "sl.modifyts = to_date('"+strToShipmentDate +"','MM-dd-yyyy HH24:MI:SS') and ";
			//sql = sql + "sh.modifyts < to_date('"+strToShipmentDate +"','MM-dd-yyyy HH24:MI:SS') and ";
			strToShipmentDate = strToShipmentDate.replaceAll("-",",");
			strToShipmentDate = strToShipmentDate.replaceAll(" ",",");
			strToShipmentDate = strToShipmentDate.replaceAll(":",",");
			String splitToShipmentDate[] = strToShipmentDate.split(",");
			strToShipmentDate = splitToShipmentDate[2]+splitToShipmentDate[0]+splitToShipmentDate[1]+splitToShipmentDate[3]+splitToShipmentDate[4]+splitToShipmentDate[5];
			sql = sql + " sh.shipment_key < '"+strToShipmentDate+"' and ";
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strOrderNo)) {
			sql = sql + "h.order_no='"+strOrderNo +"' and ";
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strFromOrderDate))	{sql = sql + "h.order_date > to_date('"+strFromOrderDate +"','MM-dd-yyyy HH24:MI:SS') and ";}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strToOrderDate))	{sql = sql + "h.order_date < to_date('" + strToOrderDate +"','MM-dd-yyyy HH24:MI:SS') and ";}

		if (org.apache.commons.lang.StringUtils.isNotBlank(strItemID)) {									hmParams.put("ItemID", strItemID);	sql = sql + "l.item_id='" + strItemID + "' and ";		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strDept)) {		strDept = addZero(strDept,4);	hmParams.put("Dept", strDept);		sql = sql + "i.extn_dept='" + strDept + "' and ";		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strCL)) {		strCL = addZero(strCL,4);		hmParams.put("CL", strCL);			sql = sql + "i.extn_class='" + strCL + "' and ";		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strSUBCL)) {	strSUBCL = addZero(strSUBCL,4);	hmParams.put("SUBCL", strSUBCL);	sql = sql + "i.extn_sub_class='" + strSUBCL + "' and ";	}

		/* if (org.apache.commons.lang.StringUtils.isNotBlank(strTransferType)) {
			sql = sql + "h.order_type='" + strTransferType + "' and ";
		} */
		if (org.apache.commons.lang.StringUtils.isNotBlank(strSourceNode)) {	sql = sql + "l.shipnode_key='" + strSourceNode + "' and ";		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(strReceivingNode)) {							hmParams.put("ReceivingNode", strReceivingNode);		sql = sql + "l.receiving_node='" + strReceivingNode + "' and ";		}
		return sql;
	}

	private String addZero(String str, int length){
		int i = str.length();
		if (i < length){
			for (int count = 0; count < length - i; count++) {
				str = "0" + str;
			}
		}
		return str;
	}




	/*************************************************************************
	 * Popup
	 ************************************************************************/

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
	public void transfer_order_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			sql = null;
		Connection		con = null;
		ResultSet    result = null;
		logger.debug(requestedPage + "|");

		String exportResult		= request.getParameter("export_result_csv");

		try{
				Gson 		gson				= new GsonBuilder().create();

				//handle request parameters here
				String strOrder			= (request.getParameter("order_number")+"").trim();
				String strOrderStatus	= (request.getParameter("order_status")+"").trim();
				String strShipmentNo	= (request.getParameter("shipment_number")+"").trim();
				String strDept			= (request.getParameter("dept")+"").trim();
				String strCL			= (request.getParameter("cl")+"").trim();
				String strSUBCL			= (request.getParameter("sub_cl")+"").trim();
				String strReceivingNode	= (request.getParameter("receiving_node")+"").trim();
				String strItemID		= (request.getParameter("item_id")+"").trim();


				String exportResultDataHeaders	= "";
				String exportResultData			= "";

				//HttpSession session		= request.getSession();
				responseWriter			= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");

				logger.debug("Loading results | X");

				if(org.apache.commons.lang.StringUtils.isNotBlank(strOrder)) {
					logger.debug("building SQL | 1");
					//sql = "select distinct ol.prime_line_no prime_line_no,trim(ol.item_id) ITEM_ID,trim(ol.item_short_description) item_description,i.extn_dept ,i.extn_class,i.extn_sub_class, CASE when (ol.original_ordered_qty = ol.ordered_qty) THEN os.status ELSE (select s.status from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0 and trim(s.status)!='9000') END as status, ol.shipnode_key,ol.receiving_node, ol.original_ordered_qty TOTAL_UNITS, ol.shipped_quantity SHIPPED_UNITS, (ol.original_ordered_qty-ol.ordered_qty)CANCELLED_UNITS, (ol.ordered_qty-ol.shipped_quantity) UNIT_VARIANCE from yfs_order_header h, yfs_order_line ol, yfs_item i, yfs_order_release_status os where h.order_no='"+strOrder+"' and h.order_header_key=ol.order_header_key and ol.item_id=i.item_id and ol.order_line_key=os.order_line_key and os.status_quantity > 0";
					sql = "with order_search as " +
							"( select distinct ol.prime_line_no LINE_NO,trim(ol.item_id) ITEM_ID,trim(ol.item_short_description) ITEM_DESC," +
							"i.extn_dept DEPT,i.extn_class CLASS1,i.extn_sub_class SUB_CLASS, ol.order_line_key, " +
							"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0 and s.status='9000'),0) Cancel_Qty,  " +
							"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0 and s.status='3700'),0) Ship_Qty,  " +
							"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0 and s.status='3200'),0) Rel_Qty, " +
							"COALESCE((select sum(s.status_quantity) from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0 and s.status='3200.01'),0) SenttoWmoS_Qty,  " +
							"(select sum(s.status_quantity) from yfs_order_release_status s where s.order_line_key=ol.order_line_key and s.status_quantity > 0) totqty,  " +
							"ol.shipnode_key FROM_NODE,ol.receiving_node TO_NODE, " +
							"ol.shipped_quantity SHIPPED_UNITS, " +
							"ol.original_ordered_qty TOTAL_UNITS, " +
							"(ol.original_ordered_qty-ol.ordered_qty)CANCELLED_UNITS, " +
							"(ol.ordered_qty-ol.shipped_quantity) UNIT_VARIANCE," +
							"h.order_no, r.extn_pick_ticket_no " +
							"from " +
							"yfs_order_header h, " +
							"yfs_order_line ol, " +
							"yfs_item i, " +
							"yfs_order_release_status os, " +
							"STERLING.yfs_order_release r " +
							"where " +
							" h.order_no='" + strOrder + "' and ";

					if(org.apache.commons.lang.StringUtils.isNotBlank(strOrderStatus)){
						logger.debug("building SQL | 1.1");
						sql = sql + "os.status=(select distinct status from yfs_status where process_type_key='20030708142438541' and status_name='"+strOrderStatus+"') and ";
					}

					if(org.apache.commons.lang.StringUtils.isNotBlank(strReceivingNode))	{sql = sql + "ol.receiving_node='"+ strReceivingNode +"' and ";	logger.debug("building SQL | 1.2");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strItemID))			{sql = sql + "ol.item_id='"+ strItemID +"' and ";				logger.debug("building SQL | 1.3");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strDept))				{sql = sql + "i.extn_dept='"+ strDept +"' and ";				logger.debug("building SQL | 1.4");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strCL))				{sql = sql + "i.extn_class='"+ strCL +"' and ";					logger.debug("building SQL | 1.5");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strSUBCL))			{sql = sql + "i.extn_sub_class='"+ strSUBCL +"' and ";			logger.debug("building SQL | 1.6");}

					logger.debug("building SQL | 2");

					sql = sql + "r.order_release_key = os.order_release_key and ";
					sql = sql +  " h.document_type='0006' and " +
							"h.order_header_key=ol.order_header_key and " +
							"ol.item_id=i.item_id and " +
							"ol.order_line_key=os.order_line_key and " +
							"h.order_header_key=r.order_header_key and os.status_quantity > 0),  " +

							"shipment_search as " +
							"(select distinct sl.prime_line_no LINE_NO,trim(ol.item_id) ITEM_ID, sl.modifyts SHIPMENT_DATE,trim(sh.shipment_no) shipment_no, " +
							"sl.shipment_line_no SHIPMENT_LINE,sh.bol_no BOL,sl.quantity UNITS,sl.extn_batch_no BATCH_NO,sl.extn_curr_retail_price ITEM_RETAIL_PRICE,h.order_no " +
							"from " +
							"yfs_order_header h, " +
							"yfs_order_line ol, " +
							"yfs_shipment sh, " +
							"yfs_shipment_line sl, " +
							"yfs_item i " +
							"where " +
							" h.order_no='" + strOrder + "' and " ;

					if(org.apache.commons.lang.StringUtils.isNotBlank(strShipmentNo)){
						logger.debug("building SQL | 2.1");
						sql = sql + "sh.shipment_no='"+ strShipmentNo +"' and ";
					}
					/* if(org.apache.commons.lang.StringUtils.isNotBlank(strOrderStatus)){
						sql = sql + "os.status=(select distinct status from yfs_status where process_type_key='20030708142438541' and status_name='"+strOrderStatus+"') and ";
					} */
					logger.debug("building SQL | 3");

					if(org.apache.commons.lang.StringUtils.isNotBlank(strReceivingNode))	{sql = sql + "ol.receiving_node='"+ strReceivingNode +"' and ";	logger.debug("building SQL | 3.1");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strItemID))			{sql = sql + "ol.item_id='"+ strItemID +"' and ";				logger.debug("building SQL | 3.2");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strDept))				{sql = sql + "i.extn_dept='"+ strDept +"' and ";				logger.debug("building SQL | 3.3");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strCL))				{sql = sql + "i.extn_class='"+ strCL +"' and ";					logger.debug("building SQL | 3.4");}
					if(org.apache.commons.lang.StringUtils.isNotBlank(strSUBCL))			{sql = sql + "i.extn_sub_class='"+ strSUBCL +"' and ";			logger.debug("building SQL | 3.5");}

					logger.debug("building SQL | 4");

					sql = sql + " h.document_type='0006' and sh.document_type='0006' and sh.shipment_key=sl.shipment_key and sl.order_header_key=h.order_header_key and sl.order_line_key=ol.order_line_key and " +
							"h.order_header_key=ol.order_header_key and ol.item_id=i.item_id  )  " +

							"select distinct order_search.LINE_NO prime_line_no,order_search.ITEM_ID,order_search.ITEM_DESC item_description,order_search.DEPT extn_dept,order_search.CLASS1 extn_class,order_search.SUB_CLASS extn_sub_class, " +
							"CASE WHEN ((order_search.SenttoWmoS_Qty=0) AND (order_search.Ship_Qty+order_search.Cancel_Qty=order_search.totqty) AND (order_search.Cancel_Qty!=order_search.totqty)) THEN 'Shipped' " +
							"WHEN ((order_search.SenttoWmoS_Qty=0) AND (order_search.Cancel_Qty=order_search.totqty)) THEN 'Cancelled' " +
							"WHEN ((order_search.SenttoWmoS_Qty=0) AND (order_search.Ship_Qty=order_search.totqty)) THEN 'Shipped' " +
							"WHEN ((order_search.SenttoWmoS_Qty>0) AND (order_search.SenttoWmoS_Qty+order_search.Cancel_Qty=order_search.totqty)) THEN 'Sent Release To WMoS' " +
							"WHEN ((order_search.SenttoWmoS_Qty>0) AND (order_search.Ship_Qty>0)) THEN 'Partially Shipped' " +
							"WHEN ((order_search.Rel_Qty>0) AND (order_search.SenttoWmoS_Qty>0)) THEN 'Partially Sent Release To WMoS' " +
							"WHEN ((order_search.Rel_Qty>0) AND (order_search.Rel_Qty+order_search.Cancel_Qty=order_search.totqty) AND (order_search.Cancel_Qty!=order_search.totqty)) THEN 'Released' " +
							"ELSE ((select distinct status_name from yfs_status where process_type_key='20030708142438541' and status=(select max(status) from yfs_order_release_status s where s.order_line_key=order_search.order_line_key and s.status_quantity > 0 ))) " +
							"END as status, " +
							"order_search.FROM_NODE shipnode_key,order_search.TO_NODE receiving_node,order_search.TOTAL_UNITS,order_search.SHIPPED_UNITS, " +
							"order_search.CANCELLED_UNITS,order_search.UNIT_VARIANCE,shipment_search.SHIPMENT_DATE,shipment_search.SHIPMENT_NO,shipment_search.SHIPMENT_LINE,shipment_search.BOL, " +
							"shipment_search.UNITS,shipment_search.BATCH_NO,trim(TO_CHAR(shipment_search.ITEM_RETAIL_PRICE,'$999999999999999.99')) ITEM_RETAIL_PRICE,trim(TO_CHAR((shipment_search.UNITS*shipment_search.ITEM_RETAIL_PRICE),'$999999999999999.99')) DISTRIBUTED_RETAIL_PRICE, " +
							"order_search.extn_pick_ticket_no from " +
							"order_search,shipment_search where ";

					if(org.apache.commons.lang.StringUtils.isBlank(strShipmentNo)){
						logger.debug("building SQL | 4.1");
						sql = sql + "order_search.order_no=shipment_search.order_no(+) and order_search.LINE_NO=shipment_search.LINE_NO(+) order by order_search.LINE_NO";
					}
					else{
						logger.debug("building SQL | 4.2");
						sql = sql + "order_search.order_no=shipment_search.order_no and order_search.LINE_NO=shipment_search.LINE_NO order by order_search.LINE_NO";
					}
					logger.debug("building SQL | -------- \n\n"+sql+"\n\n------");

					con = ReportActivator.getInstance().getConnection(Constants.OMS);

					//HashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

					result = con.createStatement().executeQuery(sql);

					JsonObject	root				= new JsonObject();

					JsonArray	ORDER_LINE_ARRAY	= new JsonArray();
					JsonObject	ORDER_LINE			= new JsonObject();
					JsonObject	shipment_line		= new JsonObject();

					int record_index			= 0;

					logger.debug("Loading results | 1");

					String PRIME_LINE_NO			= "";
					String ITEM_ID					= "";
					String ITEM_DESCRIPTION 		= "";
					String EXTN_DEPT				= "";
					String EXTN_CLASS				= "";
					String EXTN_SUB_CLASS			= "";
					String STATUS					= "";
					//String STATUS					= "";
					String RECEIVING_NODE 			= "";
					String SHIPNODE_KEY				= "";
					String TOTAL_UNITS				= "";
					String SHIPPED_UNITS			= "";
					String CANCELLED_UNITS			= "";
					String UNIT_VARIANCE 			= "";
					String EXTN_PICK_TICKET_NO		= "";

					String SHIPMENT_DATE			= "";
					String SHIPMENT_NO              = "";
					String SHIPMENT_LINE            = "";
					String BOL                      = "";
					String UNITS                    = "";
					String BATCH_NO                 = "";
					String ITEM_RETAIL_PRICE        = "";
					String DISTRIBUTED_RETAIL_PRICE = "";


					while(result.next()){

						PRIME_LINE_NO			= result.getString("PRIME_LINE_NO");
						ITEM_ID					= result.getString("ITEM_ID");
						ITEM_DESCRIPTION 		= result.getString("ITEM_DESCRIPTION");
						EXTN_DEPT				= result.getString("EXTN_DEPT");
						EXTN_CLASS				= result.getString("EXTN_CLASS");
						EXTN_SUB_CLASS			= result.getString("EXTN_SUB_CLASS");
						STATUS					= result.getString("STATUS");

						RECEIVING_NODE 			= result.getString("RECEIVING_NODE");
						SHIPNODE_KEY			= result.getString("SHIPNODE_KEY");
						TOTAL_UNITS				= result.getString("TOTAL_UNITS");
						SHIPPED_UNITS			= result.getString("SHIPPED_UNITS");
						CANCELLED_UNITS			= result.getString("CANCELLED_UNITS");
						UNIT_VARIANCE 			= result.getString("UNIT_VARIANCE");
						EXTN_PICK_TICKET_NO		= result.getString("EXTN_PICK_TICKET_NO");

						SHIPMENT_DATE			= result.getString("SHIPMENT_DATE");
						SHIPMENT_NO              = result.getString("SHIPMENT_NO");
						SHIPMENT_LINE            = result.getString("SHIPMENT_LINE");
						BOL                      = result.getString("BOL");
						UNITS                    = result.getString("UNITS");
						BATCH_NO                 = result.getString("BATCH_NO");
						ITEM_RETAIL_PRICE        = result.getString("ITEM_RETAIL_PRICE");
						DISTRIBUTED_RETAIL_PRICE = result.getString("DISTRIBUTED_RETAIL_PRICE");



						if(
								(org.apache.commons.lang.StringUtils.isNotBlank(strShipmentNo) && org.apache.commons.lang.StringUtils.equals(strShipmentNo, SHIPMENT_NO))
								||
								org.apache.commons.lang.StringUtils.isBlank(strShipmentNo)
						){
							if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
								logger.debug("Loading results | 1.2");

								if(exportResultDataHeaders.length()==0){
									logger.debug("Loading results | 1.3");
									response.addHeader("Content-Type", "text/csv");
								    response.addHeader("Content-Disposition", "attachment;filename=\"transferOrderDetails_"+strOrder+".csv\"");
								    response.setHeader("Content-Disposition", "attachment;filename=\"transferOrderDetails_"+strOrder+".csv\"");
									//TODO write csv headers
									for(int i=0;i<result.getMetaData().getColumnCount();i++){
										logger.log(Level.DEBUG, "Column name:"+result.getMetaData().getColumnName(i+1));
										exportResultDataHeaders += result.getMetaData().getColumnName(i+1)+",";
										if((i+1)==result.getMetaData().getColumnCount()-1) exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length()-1);
									}
									exportResultDataHeaders = "ORDER_NO, " + exportResultDataHeaders;
									logger.debug("Loading results | 1.4");
								}
								logger.debug("Loading results | 2");
								exportResultData += strOrder				+ ",";

								exportResultData += PRIME_LINE_NO			+ ",";
								exportResultData += ITEM_ID					+ ",";
								exportResultData += ITEM_DESCRIPTION		+ ",";
								exportResultData += EXTN_DEPT				+ ",";
								exportResultData += EXTN_CLASS	 			+ ",";
								exportResultData += EXTN_SUB_CLASS			+ ",";
								exportResultData += STATUS					+ ",";

								exportResultData += SHIPNODE_KEY			+ ",";
								exportResultData += RECEIVING_NODE			+ ",";
								exportResultData += TOTAL_UNITS				+ ",";
								exportResultData += SHIPPED_UNITS			+ ",";
								exportResultData += CANCELLED_UNITS			+ ",";
								exportResultData += UNIT_VARIANCE			+ ",";

								exportResultData += SHIPMENT_DATE			+ ",";
								exportResultData += SHIPMENT_NO             + ",";
								exportResultData += SHIPMENT_LINE           + ",";
								exportResultData += BOL                     + ",";
								exportResultData += UNITS                   + ",";
								exportResultData += BATCH_NO                + ",";
								exportResultData += ITEM_RETAIL_PRICE       + ",";
								exportResultData += DISTRIBUTED_RETAIL_PRICE+ ",";
								exportResultData += EXTN_PICK_TICKET_NO		+ "";

								exportResultData += "\n";
							}else{
								logger.debug("Loading results | 3");
								shipment_line.addProperty("SHIPMENT_DATE",				SHIPMENT_DATE);
								shipment_line.addProperty("SHIPMENT_NO", 				SHIPMENT_NO);
								shipment_line.addProperty("SHIPMENT_LINE",				SHIPMENT_LINE);
								shipment_line.addProperty("BOL",						BOL);
								shipment_line.addProperty("UNITS",						UNITS);
								shipment_line.addProperty("BATCH_NO",					BATCH_NO);
								shipment_line.addProperty("ITEM_RETAIL_PRICE",			ITEM_RETAIL_PRICE);
								shipment_line.addProperty("DISTRIBUTED_RETAIL_PRICE",	DISTRIBUTED_RETAIL_PRICE);

								//ORDER_LINE = new JsonObject();

								//add array if non-existing
								if(record_index==0 || ( (ORDER_LINE.has("PRIME_LINE_NO")) && !org.apache.commons.lang.StringUtils.equals(ORDER_LINE.get("PRIME_LINE_NO").getAsString(),PRIME_LINE_NO))){
									ORDER_LINE		= new JsonObject();

									ORDER_LINE.add("SHIPMENT_LINE_ARRAY", 		new JsonArray());
									ORDER_LINE.addProperty("ORDER_NO",			strOrder);
									ORDER_LINE.addProperty("PRIME_LINE_NO",		PRIME_LINE_NO);
									ORDER_LINE.addProperty("ITEM_ID",			ITEM_ID);
									ORDER_LINE.addProperty("ITEM_DESCRIPTION", 	ITEM_DESCRIPTION);
									ORDER_LINE.addProperty("EXTN_DEPT",			EXTN_DEPT);
									ORDER_LINE.addProperty("EXTN_CLASS",		EXTN_CLASS);
									ORDER_LINE.addProperty("EXTN_SUB_CLASS",	EXTN_SUB_CLASS);
									ORDER_LINE.addProperty("STATUS",			STATUS);
									ORDER_LINE.addProperty("RECEIVING_NODE", 	RECEIVING_NODE);
									ORDER_LINE.addProperty("SHIPNODE_KEY",		SHIPNODE_KEY);
									ORDER_LINE.addProperty("TOTAL_UNITS",		TOTAL_UNITS);
									ORDER_LINE.addProperty("SHIPPED_UNITS",		SHIPPED_UNITS);
									ORDER_LINE.addProperty("CANCELLED_UNITS",	CANCELLED_UNITS);
									ORDER_LINE.addProperty("UNIT_VARIANCE", 	UNIT_VARIANCE);
									ORDER_LINE.addProperty("EXTN_PICK_TICKET_NO",EXTN_PICK_TICKET_NO);

									//shipment_line_map.add(PRIME_LINE_NO);

									//add shipment line to this order line
									ORDER_LINE.getAsJsonArray("SHIPMENT_LINE_ARRAY").add(shipment_line);

									//create array for this order line add order line object to array
									ORDER_LINE_ARRAY.add(ORDER_LINE);

								}else{
									//add shipment line to this order line
									ORDER_LINE.getAsJsonArray("SHIPMENT_LINE_ARRAY").add(shipment_line);
								}

								logger.debug("Loading results | 4");

								//ORDER_LINE		= new JsonObject();
								shipment_line	= new JsonObject();
							}
						}
						record_index++;
					}


					logger.debug(record_index+" Results loaded");

					//add line to the table
					//table.add(line);
					root.add("shipment_details", ORDER_LINE_ARRAY);
					response_content	= gson.toJson(root);
					//response_content	= gson.toJson(table);
				}
				else{
					logger.debug("Loading results | 5");
					response_content = "-error-";
				}

				//Convert HashMap into json object
				//response_content	= gson.toJson(result);

				//write content to response writer, flush before closing ... trust me on this one ...
				if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
					//response.reset();
					logger.debug("Loading results | 6");
					response_content = exportResultDataHeaders+"\n"+exportResultData;
				}
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
				logger.debug("Loading results | 7");
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.error("error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally{
			if(result!=null){result.close();}
			if(con!=null){con.close();}
		}
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
