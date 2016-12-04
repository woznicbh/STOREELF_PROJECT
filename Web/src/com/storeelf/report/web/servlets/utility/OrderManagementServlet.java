package com.storeelf.report.web.servlets.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.SQLConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.XProperties;
/**
 * Servlet implementation class OrderManagementServlet
 *
 * each *Response method MUST have the following types as arguments in this order:
 * ExampleResponse(String page, HttpServletRequest rq, HttpServletResponse rs)
 *
 * @author tkmagh4
 * @web.servlet
 *   name=OrderManagementServlet
 */
public class OrderManagementServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(OrderManagementServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/utility_includes/utility.jsp";
	private String response_content;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderManagementServlet() {
		super();
	}

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
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
	public void order(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException{
		String			search_value			= null;
		Connection		con						= ReportActivator.getInstance().getConnection(Constants.OMS);
		Connection		conOMSr_TXN_PRM			= null;
		String			sql						= null;
		String			omsr_txn_sql			= null;
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/utility_includes/order_management/order.jsp";
		Gson 			gson	         		= new GsonBuilder().create();
		JsonObject		rootReturn  			= new JsonObject();
		JsonArray		so_line_array		= new JsonArray();
		JsonArray		po_line_array			= new JsonArray();
		JsonObject		order_line_object 		= new JsonObject();
		String			order_createts			= null;
		JsonArray		order_header_array		= new JsonArray();
		JsonArray		order_receipt_array		= new JsonArray();
		JsonObject		order_header_object 		= new JsonObject();
		JsonObject		order_receipt_object			= new JsonObject();
		JsonObject		order_header_charges 		= new JsonObject();
		JsonArray		order_header_charges_array		= new JsonArray();
		JsonObject		order_line_charges 		= new JsonObject();
		JsonArray		order_line_charges_array		= new JsonArray();
		JsonArray		order_line_tax_array = new JsonArray();
		JsonObject		order_line_tax 		= new JsonObject();
		JsonObject 		order_total_field = new JsonObject();
		JsonArray 		order_total_field_array = new JsonArray();
		JsonObject 		promotion_code = new JsonObject();
		JsonArray 		promotion_code_array=new JsonArray();
		JsonObject 		promotion_code_name = new JsonObject();
		JsonArray 		promotion_code_name_array=new JsonArray();
		JsonObject 		promotion_code_name_blank = new JsonObject();
		JsonArray 		promotion_code_name_blank_array=new JsonArray();
		BigDecimal		value=BigDecimal.ZERO;
		BigDecimal 		sum=BigDecimal.ZERO;
		boolean 		SO = true;
		boolean			isPOCorder = false;
		//logger.log(Level.INFO, "OrderManagementServlet:orderResponse | " + requestedPage + "|" + request.getParameter("orderNumber"));
		PreparedStatement	omsState	= null;
		ResultSet countResult = null;
		ResultSet rs = null;
		ResultSet orderTax = null;
		ResultSet rs2 = null;
		ResultSet orderSet = null;
		try{
			if( "POST".equals(request.getMethod()) ){
				responseWriter	= response.getWriter();
				response.setContentType("application/json");
				search_value	= request.getParameter("orderNumber");
				isPOCorder		= request.getParameter("isPOCorder").equals("true");;

				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					String nonDirectShip_search_value = search_value;
					String DirectShip_search_value = search_value;
					if (search_value.contains("_")){
						SO = false;
						nonDirectShip_search_value = search_value.substring(0,(search_value.indexOf("_")));
					}
					else{
						DirectShip_search_value = search_value + "_%";
					}

					if(isPOCorder) conOMSr_TXN_PRM			= ReportActivator.getInstance().getConnection(Constants.OMS);

					sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ORDER_LINE_SQL);
					omsState = con.prepareStatement(sql);
					omsState.setString(1, nonDirectShip_search_value);
					omsState.setString(2, DirectShip_search_value);

					omsr_txn_sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ORDER_LINE_SQL2);

					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getPreparedSQLResult(omsState, SQLConstants.ID_UTIL_ORDER_LINE_SQL, con);
					ConcurrentHashMap<Integer, HashMap<String, Object>> result_OMSr = null;
					if(isPOCorder){
						result_OMSr = SQLUtils.getSQLResult(omsr_txn_sql, conOMSr_TXN_PRM);

						logger.info("------------------> result_OMSr size:"+result_OMSr.size());
						System.out.println("------------------> result_OMSr size:"+result_OMSr.size());
						result.putAll(result_OMSr);
					}
					//iterate through the returned result set and add items to a JsonObject
					for (HashMap<String, Object> map : result.values()) {
						order_line_object.addProperty("ORDER_LINE_KEY", String.valueOf(map.get("ORDER_LINE_KEY")).trim());
						order_line_object.addProperty("CHAINED_FROM_ORDER_LINE_KEY", String.valueOf(map.get("CHAINED_FROM_ORDER_LINE_KEY")).trim());
						order_line_object.addProperty("ORDER_NO", String.valueOf(map.get("ORDER_NO")).trim());
						order_line_object.addProperty("CHAINED_ORDER_NO", String.valueOf(map.get("CHAINED_ORDER_NO")).trim());
						order_line_object.addProperty("SHIPNODE_KEY", String.valueOf(map.get("SHIPNODE_KEY")).trim());
						order_line_object.addProperty("PRIME_LINE_NO", Integer.parseInt(String.valueOf(map.get("PRIME_LINE_NO")).trim()));
						order_line_object.addProperty("CHAINED_PRIME_LINE_NO", String.valueOf(map.get("CHAINED_PRIME_LINE_NO")).trim());
						order_line_object.addProperty("STATUS", String.valueOf(map.get("STATUS")).trim());
						order_line_object.addProperty("ITEM_ID", String.valueOf(map.get("ITEM_ID")).trim());
						order_line_object.addProperty("STATUS_QUANTITY", String.valueOf(map.get("STATUS_QUANTITY")).trim());
						if(String.valueOf(map.get("SHIPNODE_KEY")).trim().equals("MP")){
							order_line_object.addProperty("GIFT_FLAG", "N/A");
							order_line_object.addProperty("GIFT_WRAP", "N/A");
							order_line_object.addProperty("EXTN_SHIP_ALONE", "N/A");
							order_line_object.addProperty("EXTN_CAGE_ITEM", "N/A");
							order_line_object.addProperty("EXTN_IS_PLASTIC_GIFT_CARD", "N/A");
							order_line_object.addProperty("EXTN_BREAKABLE", "N/A");
							order_line_object.addProperty("ALLOW_GIFT_WRAP", "N/A");
							order_line_object.addProperty("EXTN_BAGGAGE", "N/A");
							order_line_object.addProperty("IS_HAZMAT", "N/A");
						}
						else{
							order_line_object.addProperty("GIFT_FLAG", String.valueOf(map.get("GIFT_FLAG")).trim());
							order_line_object.addProperty("GIFT_WRAP", String.valueOf(map.get("GIFT_WRAP")).trim());
							order_line_object.addProperty("EXTN_SHIP_ALONE", String.valueOf(map.get("EXTN_SHIP_ALONE")).trim());
							order_line_object.addProperty("EXTN_CAGE_ITEM", String.valueOf(map.get("EXTN_CAGE_ITEM")).trim());
							order_line_object.addProperty("EXTN_IS_PLASTIC_GIFT_CARD", String.valueOf(map.get("EXTN_IS_PLASTIC_GIFT_CARD")).trim());
							order_line_object.addProperty("EXTN_BREAKABLE", String.valueOf(map.get("EXTN_BREAKABLE")).trim());
							order_line_object.addProperty("ALLOW_GIFT_WRAP", String.valueOf(map.get("ALLOW_GIFT_WRAP")).trim());
							order_line_object.addProperty("EXTN_BAGGAGE", String.valueOf(map.get("EXTN_BAGGAGE")).trim());
							order_line_object.addProperty("IS_HAZMAT", String.valueOf(map.get("IS_HAZMAT")).trim());
						}
						order_line_object.addProperty("SHIP_TO", String.valueOf(map.get("SHIP_TO")).trim());
						order_line_object.addProperty("ORDER_DATE", String.valueOf(map.get("ORDER_DATE")).trim());
						order_line_object.addProperty("DS_ITEM", String.valueOf(map.get("DS_ITEM")).trim());
						order_line_object.addProperty("CARRIER_SERVICE_CODE", String.valueOf(map.get("CARRIER_SERVICE_CODE")).trim());
						//add the order_line_object to a JsonArray
						if (!String.valueOf(map.get("ORDER_NO")).trim().contains("_"))
						{						
							String order_line_key = String.valueOf(map.get("ORDER_LINE_KEY")).trim();
							sql = "SELECT COUNT(*) COUNT"
									+"\n FROM yfs_order_line a"
									+"\n WHERE a.chained_from_order_line_key like '"+ order_line_key +"'";
							countResult = con.createStatement().executeQuery(sql);
							int COUNT = 0;
							while (countResult.next()) {
								COUNT = countResult.getInt("COUNT");
							}
							if (COUNT > 0)
							{
								order_line_object.addProperty("CHAINED_ORDER", "PO");
							}
							else
							{
								order_line_object.addProperty("CHAINED_ORDER", "N/A");
							}
							so_line_array.add(order_line_object);
						}
						else
						{
							order_line_object.addProperty("CHAINED_ORDER", "SO");
							po_line_array.add(order_line_object);
						}

						//clear out the line object to be used again
						order_line_object = new JsonObject();
					}

					//add the array of items to the root object
					if (SO)
					{
						rootReturn.add("order_lines", so_line_array);
						rootReturn.add("chained_order_lines", po_line_array);
					}
					else
					{
						rootReturn.add("order_lines", po_line_array);
						rootReturn.add("chained_order_lines", so_line_array);
					}

					//next grab the createTS for the order and add it as a property to the root object
					sql = "select createts from yfs_order_header oh where oh.order_no like '"+ search_value +"%'";							
					result = SQLUtils.getSQLResult(sql, con);

					//This will only ever return one value, as an order cannot have more than one createts
					for (HashMap<String, Object> map : result.values()) {
						order_createts = String.valueOf(map.get("CREATETS"));
					}

					//start order header changes 
					sql =   "SELECT head.order_no,                                     "
							+ " \n   CASE                                              "
							+ " \n     WHEN hold.status = '1100'                       "
							+ " \n     THEN 'Y'                                        "
							+ " \n     ELSE 'N'                                        "
							+ " \n   END                     AS ON_HOLD,               "
							+ " \n   UPPER(pi.address_line1) AS address,               "
							+ " \n   UPPER(trim(pi.state))   AS state,                 "
							+ " \n   pi.zip_code,                                      "
							+ " \n   SUBSTR(notes.REASON_CODE,7) AS shipnode,          "
							+ " \n   notes.note_text             AS ReceiptID          "
							+ " \n FROM yfs_order_header head                 		   "
							+ " \n   left outer join yfs_order_hold_type hold          "
							+ " \n   on head.order_header_key = hold.order_header_key  "
							+ " \n   join yfs_person_info pi                           "
							+ " \n   on head.bill_to_key      = pi.person_info_key     "
							+ " \n   join yfs_notes notes                              "
							+ " \n   on head.order_header_key = notes.table_key        "
							+ " \n WHERE head.order_no  = '"+ nonDirectShip_search_value +"'   "
							+ " \n AND notes.table_name      ='YFS_ORDER_HEADER'       "
							+ " \n AND notes.reason_code LIKE 'Pree%'                  ";

					//start order header changes 
					omsr_txn_sql =   "SELECT head.order_no,                                     "
							+ " \n   CASE                                              "
							+ " \n     WHEN hold.status = '1100'                       "
							+ " \n     THEN 'Y'                                        "
							+ " \n     ELSE 'N'                                        "
							+ " \n   END                     AS ON_HOLD,               "
							//+ " \n   UPPER(pi.address_line1) AS address,               "
							+ " \n   'n/a'	AS address,               "
							//+ " \n   UPPER(trim(pi.state))   AS state,                 "
							+ " \n   'n/a'   AS state,                 "
							//+ " \n   pi.zip_code as ,                                      "
							//+ " \n   SUBSTR(notes.REASON_CODE,7) AS shipnode,          "
							+ " \n   'n/a' AS shipnode,          "
							//+ " \n   notes.note_text             AS ReceiptID          "
							+ " \n   'n/a'             AS ReceiptID          "
							+ " \n FROM yfs_order_header head                 		   "
							+ " \n   left outer join yfs_order_hold_type hold          "
							+ " \n   on head.order_header_key = hold.order_header_key  "
							//+ " \n   join yfs_person_info pi                           "
							//+ " \n   on head.bill_to_key      = pi.person_info_key     "
							//+ " \n   join yfs_notes notes                              "
							//+ " \n   on head.order_header_key = notes.table_key        "
							+ " \n WHERE head.order_no  = '"+ nonDirectShip_search_value +"'   "
							//+ " \n AND notes.table_name      ='YFS_ORDER_HEADER'       "
							//+ " \n AND notes.reason_code LIKE 'Pree%'                  "
							;

					//result = SQLUtils.getSQLResult(sql, con);
					if(isPOCorder) conOMSr_TXN_PRM		= ReportActivator.getInstance().getConnection(Constants.OMS);							

					int result_size = 0;
					orderSet = con.createStatement().executeQuery(sql);

					//if OMS query returns 0, try OMSr
					//							if(orderSet.getFetchSize()<1)	orderSet = conOMSr_TXN_PRM.createStatement().executeQuery(omsr_txn_sql);

					while (orderSet.next()) {
						result_size++;
						order_header_object.addProperty("ON_HOLD", orderSet.getString("ON_HOLD").trim());				
						order_header_object.addProperty("ORDER_NO", orderSet.getString("ORDER_NO").trim());
						order_header_object.addProperty("STATE", orderSet.getString("STATE").trim());
						order_header_object.addProperty("ADDRESS", orderSet.getString("ADDRESS").trim());
						order_header_array = new JsonArray();
						order_header_array.add(order_header_object);	
						order_receipt_object.addProperty("RECEIPTID", orderSet.getString("RECEIPTID").trim());
						order_receipt_object.addProperty("SHIPNODE", orderSet.getString("SHIPNODE").trim());
						order_receipt_array.add(order_receipt_object);
						order_receipt_object = new JsonObject();
					}

					if(isPOCorder){
						logger.info("------------------> result_OMSr size:"+result_OMSr.size());
						System.out.println("------------------> result_OMSr size:"+result_OMSr.size());

						if(result_size<1){
							orderSet = conOMSr_TXN_PRM.createStatement().executeQuery(omsr_txn_sql);
							while (orderSet.next()) {
								result_size++;
								order_header_object.addProperty("ON_HOLD", orderSet.getString("ON_HOLD").trim());				
								order_header_object.addProperty("ORDER_NO", orderSet.getString("ORDER_NO").trim());
								order_header_object.addProperty("STATE", orderSet.getString("STATE").trim());
								order_header_object.addProperty("ADDRESS", orderSet.getString("ADDRESS").trim());
								order_header_array = new JsonArray();
								order_header_array.add(order_header_object);	
								order_receipt_object.addProperty("RECEIPTID", orderSet.getString("RECEIPTID").trim());
								order_receipt_object.addProperty("SHIPNODE", orderSet.getString("SHIPNODE").trim());
								order_receipt_array.add(order_receipt_object);
								order_receipt_object = new JsonObject();
							}
						}
					}

					//--
					sql ="select order_no,record_type,charge_category,charge_name,reference,charge,invoiced_charge from yfs_header_charges hc, yfs_order_header h" 
							+ " \n where hc.header_key=h.order_header_key and order_no='"+search_value+"'";
					//ResultSet headercharges = con.createStatement().executeQuery(sql);
					ConcurrentHashMap<Integer, HashMap<String, Object>> headercharges = SQLUtils.getSQLResult(sql, con);
					//System.out.println(headercharges);
					for (HashMap<String, Object> map : headercharges.values()) {
						//while(headercharges.next()){
						order_header_charges.addProperty("ORDER_NO", String.valueOf(map.get("ORDER_NO")).trim());
						order_header_charges.addProperty("RECORD_TYPE", String.valueOf(map.get("RECORD_TYPE")).trim());
						order_header_charges.addProperty("CHARGE_CATEGORY", String.valueOf(map.get("CHARGE_CATEGORY")).trim());
						order_header_charges.addProperty("CHARGE_NAME", String.valueOf(map.get("CHARGE_NAME")).trim());
						order_header_charges.addProperty("REFERENCE", String.valueOf(map.get("REFERENCE")).trim());
						order_header_charges.addProperty("CHARGE", String.valueOf(map.get("CHARGE")).trim());
						order_header_charges.addProperty("INVOICED_CHARGE", String.valueOf(map.get("INVOICED_CHARGE")).trim());
						order_header_charges_array.add(order_header_charges);
						order_header_charges = new JsonObject();
					}

					sql ="select order_no,prime_line_no,record_type,charge_category,charge_name,reference,chargeperunit,chargeperline,invoiced_charge_per_line "
							+"\n from yfs_line_charges hc, yfs_order_header h ,yfs_order_line l"
							+"\n where hc.header_key=h.order_header_key and hc.line_key=l.order_line_key  and order_no='"+search_value+"' order by prime_line_no";
					ConcurrentHashMap<Integer, HashMap<String, Object>> linecharges = SQLUtils.getSQLResult(sql, con);
					//ResultSet linecharges = con.createStatement().executeQuery(sql);
					//System.out.println(headercharges);
					for (HashMap<String, Object> map : linecharges.values()) {
						//	order_line_object.addProperty("ORDER_LINE_KEY", String.valueOf(map.get("INVOICED_CHARGE_PER_LINE")).trim());
						//while(linecharges.next()){
						order_line_charges.addProperty("ORDER_NO",  String.valueOf(map.get("ORDER_NO")).trim());
						order_line_charges.addProperty("PRIME_LINE_NO", Integer.parseInt(String.valueOf(map.get("PRIME_LINE_NO")).trim()));
						order_line_charges.addProperty("RECORD_TYPE", String.valueOf(map.get("RECORD_TYPE")).trim());
						order_line_charges.addProperty("CHARGE_CATEGORY",  String.valueOf(map.get("CHARGE_CATEGORY")).trim());
						order_line_charges.addProperty("CHARGE_NAME", String.valueOf(map.get("CHARGE_NAME")).trim());
						order_line_charges.addProperty("REFERENCE", String.valueOf(map.get("REFERENCE")).trim());
						order_line_charges.addProperty("CHARGEPERUNIT", String.valueOf(map.get("CHARGEPERUNIT")).trim());
						order_line_charges.addProperty("CHARGEPERLINE", String.valueOf(map.get("CHARGEPERLINE")).trim());
						order_line_charges.addProperty("INVOICED_CHARGE_PER_LINE", String.valueOf(map.get("INVOICED_CHARGE_PER_LINE")).trim());
						order_line_charges_array.add(order_line_charges);
						order_line_charges = new JsonObject();
					}

					//New Part

					if (search_value.contains("_")){

						String sql1= "select ol.prime_line_no as Line_no from (select prime_line_no,order_header_key from sterling.yfs_order_line where order_header_key in                                                "
								+ " \n (select order_header_key from sterling.yfs_ordeR_header where ordeR_no='"+ DirectShip_search_value +"')) t5, sterling.yfs_order_line ol where                                                    "
								+ " \n t5.prime_line_no=ol.prime_line_no                                                                                                                                                     "
								+ " \n and ol.order_header_key=t5.order_header_key ";

						rs2 = con.createStatement().executeQuery(sql1);
						while (rs2.next())
						{
							String lineno=rs2.getString("Line_no").trim();

							sql =" select "
									+ " \n t1.Line_no,t1.item_id,t1.Description,t1.Qty,t1.List_Price,t1.Price_Each,t1.Total_Price,t3.Fulfillment,t4.TAXPERCENTAGE,t4.tax "
									+ " \n from (select distinct (ol.prime_line_no) as Line_no,i.item_id,i.description,ol.ordered_qty as QTY,ol.list_price,ol.unit_price as Price_Each,ol.Invoiced_Extended_Price as Total_Price "
									+ " \n  from sterling.yfs_order_line ol, sterling.yfs_item i                                                                                                                                 "
									+ " \n where i.item_id=ol.item_id                                                                                                                                                            "
									+ " \n and ol.order_header_key in (select order_header_key from sterling.yfs_order_header where order_no= '"+ nonDirectShip_search_value +"' ) order by ol.prime_line_no) t1,                        "
									+ " \n (select distinct(ol.prime_line_no) as Line_no, (CASE  WHEN ol.line_type = 'DSV'                                                                                                       "
									+ " \n                       THEN 'DS'                                                                                                                                                       "
									+ " \n                       END) as Fulfillment  from sterling.yfs_order_line ol                                                                                                            "
									+ " \n where ol.order_header_key in (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')                                                       "
									+ " \n and ol.line_type='DSV'                                                                                                                                                                "
									+ " \n order by line_no) t3,                                                                                                                                                                 "
									+ " \n (select ol.prime_line_no as Line_no,tb.tax_percentage*100 as TAXPERCENTAGE,tb.tax from sterling.yfs_tax_breakup tb, sterling.yfs_order_line ol                                        "
									+ " \n where tb.header_key =ol.order_header_key                                                                                                                                              "
									+ " \n and tb.line_key=ol.order_line_key                                                                                                                                                     "
									+ " \n and tb.header_key in                                                                                                                                                                  "
									+ " \n (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')                                                                                    "
									+ " \n order by prime_line_no) t4                                                                                                                                                           "
									+ " \n where t1.Line_no=t3.Line_no                                                                                                                                                           "
									+ " \n and t1.Line_no=t4.Line_no                                                                                                                                                             "
									+ " \n and t1.Line_no='"+ lineno +"'    ";

							System.out.println("SQL:" +sql);
							orderTax = con.createStatement().executeQuery(sql);
							while (orderTax.next()) {
								order_line_tax.addProperty("LINE_NO", orderTax.getString("LINE_NO").trim());				
								order_line_tax.addProperty("ITEM_ID", orderTax.getString("ITEM_ID").trim());
								order_line_tax.addProperty("DESCRIPTION", orderTax.getString("DESCRIPTION").trim());
								order_line_tax.addProperty("FULFILLMENT", orderTax.getString("FULFILLMENT").trim());
								order_line_tax.addProperty("QTY", orderTax.getString("QTY").trim());
								order_line_tax.addProperty("LIST_PRICE", orderTax.getString("LIST_PRICE").trim());
								order_line_tax.addProperty("PRICE_EACH", orderTax.getString("PRICE_EACH").trim());
								order_line_tax.addProperty("TOTAL_PRICE", orderTax.getString("TOTAL_PRICE").trim());													
								order_line_tax.addProperty("TAXPERCENTAGE", orderTax.getString("TAXPERCENTAGE").trim()+"%");
								order_line_tax.addProperty("TAX", orderTax.getString("TAX").trim());
								//									order_line_tax_array = new JsonArray();
								order_line_tax_array.add(order_line_tax);	
								order_line_tax = new JsonObject();

							}
						}
						rs2.close(); 								
					}
					else{

						sql =   " select t1.Line_no,t1.item_id,t1.Description,t1.Qty,t1.List_Price,t1.Price_Each,t1.Total_Price,t3.Fulfillment,t4.TAXPERCENTAGE,t4.tax															"
								+ " \n from (select distinct (ol.prime_line_no) as Line_no,i.item_id,i.description,ol.ordered_qty as QTY,ol.list_price,ol.unit_price as Price_Each,ol.Invoiced_Extended_Price as Total_Price    "
								+ " \n  from sterling.yfs_order_line ol, sterling.yfs_item i                                                                                                                                    "
								+ " \n where i.item_id=ol.item_id                                                                                                                                                               "
								+ " \n and ol.order_header_key in (select order_header_key from sterling.yfs_order_header where order_no= '"+ nonDirectShip_search_value +"' ) order by ol.prime_line_no) t1,                           "
								+ " \n (select t2.Line_no,t2.Fulfillment from (select distinct(ol.prime_line_no) as Line_no, (CASE WHEN s.delivery_method = 'PICK'                                                              "
								+ " \n                       THEN 'BOPUS'                                                                                                                                                       "
								+ " \n                       WHEN s.delivery_method = 'SHP'                                                                                                                                     "
								+ " \n                       THEN 'EFC'                                                                                                                                                         "
								+ " \n                   END) as Fulfillment  from sterling.yfs_order_line ol,sterling.yfs_shipment_line sl,sterling.yfs_shipment s where                                                       "
								+ " \n sl.order_line_key=ol.order_line_key                                                                                                                                                      "
								+ " \n and sl.shipment_key=s.shipment_key                                                                                                                                                       "
								+ " \n and ol.order_header_key in (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')                                                            "
								+ " \n union                                                                                                                                                                                    "
								+ " \n select distinct(ol.prime_line_no) as Line_no, (CASE  WHEN ol.line_type = 'DSV'                                                                                                           "
								+ " \n                       THEN 'DS'                                                                                                                                                          "
								+ " \n 					  END) as Fulfillment  from sterling.yfs_order_line ol                                                                                                                  "
								+ " \n where ol.order_header_key in (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')                                                          "
								+ " \n and ol.line_type='DSV') t2                                                                                                                                                               "
								+ " \n order by t2.line_no) t3,                                                                                                                                                                 "
								+ " \n (select ol.prime_line_no as Line_no,tb.tax_percentage*100 as TAXPERCENTAGE,tb.tax from sterling.yfs_tax_breakup tb, sterling.yfs_order_line ol                                           "
								+ " \n where tb.header_key =ol.order_header_key                                                                                                                                                 "
								+ " \n and tb.line_key=ol.order_line_key                                                                                                                                                        "
								+ " \n and tb.header_key in                                                                                                                                                                     "
								+ " \n (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')                                                                                      	"
								+ " \n order by prime_line_no) t4                                                                                                                                                               "
								+ " \n where t1.Line_no=t3.Line_no                                                                                                                                                              "
								+ " \n and t1.Line_no=t4.Line_no ";

						orderTax = con.createStatement().executeQuery(sql);
						while (orderTax.next()) {
							order_line_tax.addProperty("LINE_NO", orderTax.getString("LINE_NO").trim());				
							order_line_tax.addProperty("ITEM_ID", orderTax.getString("ITEM_ID").trim());
							order_line_tax.addProperty("DESCRIPTION", orderTax.getString("DESCRIPTION").trim());
							order_line_tax.addProperty("FULFILLMENT", orderTax.getString("FULFILLMENT").trim());
							order_line_tax.addProperty("QTY", orderTax.getString("QTY").trim());
							order_line_tax.addProperty("LIST_PRICE", orderTax.getString("LIST_PRICE").trim());
							order_line_tax.addProperty("PRICE_EACH", orderTax.getString("PRICE_EACH").trim());
							order_line_tax.addProperty("TOTAL_PRICE", orderTax.getString("TOTAL_PRICE").trim());													
							order_line_tax.addProperty("TAXPERCENTAGE", orderTax.getString("TAXPERCENTAGE").trim()+"%");
							order_line_tax.addProperty("TAX", orderTax.getString("TAX").trim());
							//									order_line_tax_array = new JsonArray();
							order_line_tax_array.add(order_line_tax);	
							order_line_tax = new JsonObject();

						}
						orderTax.close();
					}

					//result = SQLUtils.getSQLResult(sql, con);

					// To calculate SubTotal and Discount
					result_size=0;									
					sql = "select sum(ol.ordered_qty*unit_price) as SubTotal, sum(ol.other_charges) as Discount "
							+ "from sterling.yfs_order_line ol, sterling.yfs_order_header oh "
							+ "where ol.order_header_key=oh.order_header_key "
							+ "and oh.order_no='"+ nonDirectShip_search_value +"'";

					omsr_txn_sql = "select sum(ol.ordered_qty*unit_price) as SubTotal, sum(ol.other_charges) as Discount "
							+ "from omsomni01.yfs_order_line ol, omsomni01.yfs_order_header oh "
							+ "where ol.order_header_key=oh.order_header_key "
							+ "and oh.order_no='"+ nonDirectShip_search_value +"'";

					conOMSr_TXN_PRM		= ReportActivator.getInstance().getConnection(Constants.OMS);
					rs = con.createStatement().executeQuery(sql);
					while (rs.next())
					{
						result_size++;
						order_total_field.addProperty("SubTotal","SUBTOTAL");
						value=BigDecimal.valueOf(rs.getDouble(1)); //Double.parseDouble(rs.getString(1));
						order_total_field.addProperty("SubTotalValue","$"+value);
						sum=sum.add(value);
						order_total_field.addProperty("Discount","DISCOUNT");
						value=BigDecimal.valueOf(rs.getDouble(2));
						//order_total_field.addProperty("DISCOUNTVALUE","$"+Math.abs(value));
						order_total_field.addProperty("DISCOUNTVALUE","$"+value.doubleValue());

						sum=sum.add(value);								
					}

					if(isPOCorder){							
						if(result_size<1){							
							rs= conOMSr_TXN_PRM.createStatement().executeQuery(omsr_txn_sql);
							while (rs.next())
							{									
								result_size++;
								order_total_field.addProperty("SubTotal","SUBTOTAL");
								value=BigDecimal.valueOf(rs.getDouble(1)); //Double.parseDouble(rs.getString(1));
								order_total_field.addProperty("SubTotalValue","$"+value);
								sum=sum.add(value);
								order_total_field.addProperty("Discount","DISCOUNT");
								value=BigDecimal.valueOf(rs.getDouble(2));
								//order_total_field.addProperty("DISCOUNTVALUE","$"+Math.abs(value));
								order_total_field.addProperty("DISCOUNTVALUE","$"+value.doubleValue());

								sum=sum.add(value);								
							}
						}
					}

					//Calculate StoreElfCash
					sql = "select distinct(charge_name) as CHARGENAME,sum(chargeamount) over (partition by charge_name) as Charge_Amount"
							+ " from sterling.yfs_line_charges where header_key in "
							+ "(select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"') and charge_name='STOREELF_CASH'";

					rs = con.createStatement().executeQuery(sql);
					order_total_field.addProperty("Charge_Name","STOREELF CASH");
					if (rs.next())
					{					
						value=BigDecimal.valueOf(Double.parseDouble(rs.getString(2)));
						order_total_field.addProperty("Charge_Amount","$"+value);										
					}							
					else
					{
						order_total_field.addProperty("Charge_Amount","$0.00");
					}

					int count=0;

					// To Calculate Shipping/Sales Taxname and TaxAmount
					sql = "select distinct(tax_name) as TAXNAME,sum(tax) over (partition by tax_name) as TaxAmount"
							+ " from sterling.yfs_tax_breakup where header_key in"
							+ " (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')";

					rs = con.createStatement().executeQuery(sql);
					while(rs.next())
					{	
						count=1;
						order_total_field.addProperty("Tax_Name",(rs.getString("TAXNAME").toUpperCase()).trim());
						value=BigDecimal.valueOf(Double.parseDouble(rs.getString(2)));
						order_total_field.addProperty("Tax_Amount","$"+value);
						sum=sum.add(value);		
					}

					if(count==0)
					{
						order_total_field.addProperty("Tax_Name","OTHER TAX");
						order_total_field.addProperty("Tax_Amount","$0.00");

					}

					count=0;
					//Calculate Shipping/Sales Tax Percentage
					sql="select Charge_category as ChargeCategory,(tax_percentage*100) as TAXPERCENTAGE from sterling.yfs_tax_breakup "
							+ "where tax_percentage>0 and header_key in (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"') "
							+ "group by Charge_category,tax_percentage";
					rs = con.createStatement().executeQuery(sql);
					while(rs.next())
					{					
						count=1;
						order_total_field.addProperty("Charge_category",(rs.getString("ChargeCategory").toUpperCase()).trim()+ " PA");
						value=BigDecimal.valueOf(Double.parseDouble(rs.getString(2)));
						order_total_field.addProperty("TAXPERCENTAGE",(value+"%"));

					}

					if(count==0)
					{
						order_total_field.addProperty("Charge_category"," TAX PA");
						order_total_field.addProperty("TAXPERCENTAGE","0%");
					}

					count=0;
					//Calculate Shipping Charge
					sql=" select charge_category as CHARCAT,charge as CHARGES from sterling.yfs_header_charges "
							+ "where header_key in (select order_header_key from sterling.yfs_order_header where order_no='"+ nonDirectShip_search_value +"')"; 	
					rs = con.createStatement().executeQuery(sql);
					while(rs.next())
					{					
						count=1;
						order_total_field.addProperty("ChargeCategory",(rs.getString("CHARCAT").toUpperCase()).trim()+" CHARGE");
						value=BigDecimal.valueOf(Double.parseDouble(rs.getString(2)));
						order_total_field.addProperty("CHARGES","$"+value);
						sum=sum.add(value);
					}

					if(count==0)
					{
						order_total_field.addProperty("ChargeCategory","SHIPPING CHARGE");
						order_total_field.addProperty("CHARGES","$0.00");
					}

					order_total_field.addProperty("OrderTotalField","ORDER TOTAL");
					order_total_field.addProperty("OrderTotalValue","$"+ sum.multiply(new BigDecimal(100.0)).divide(new BigDecimal(100.0)) );//Math.round(sum.multiply(new BigDecimal(100.0)))/100.0);
					order_total_field_array.add(order_total_field);	
					order_total_field = new JsonObject();

					//Calculate StoreElf Promotion Code

					sql="select p.description as DES,t.CHARGE1 as CHARGE from (select header_key,reference,sum(invoiced_charge_per_line) as CHARGE1 "
							+ " from sterling.yfs_line_charges where header_key in "
							+ " (select order_header_key from sterling.yfs_order_header where order_no ='"+ nonDirectShip_search_value +"') "
							+ " and reference not in (' ')group by header_key,reference)t, sterling.yfs_promotion p "
							+ " where t.header_key=p.order_header_key and t.reference=p.promotion_id";

					System.out.println(sql);
					rs = con.createStatement().executeQuery(sql);


					while(rs.next())
					{					
						count=1;
						promotion_code.addProperty("PROMOTION_DESCRIPTION",(rs.getString("DES")).trim());
						promotion_code.addProperty("PROMOTION_CHARGE","$"+(rs.getString("CHARGE")).trim());
						promotion_code_array.add(promotion_code);	
						promotion_code = new JsonObject();
					}

					if(count==1)
					{
						promotion_code_name.addProperty("PROMOTION_DESCRIPTION_FIELD_NAME","APPLIED PROMOTIONS");
						promotion_code_name.addProperty("PROMOTION_CHARGE_FIELD_NAME","CHARGE"); 
						promotion_code_name_array.add(promotion_code_name);	
						promotion_code_name = new JsonObject();

					}

					else 
					{
						promotion_code_name_blank.addProperty("PROMOTION_DESCRIPTION_FIELD_BLANK","No promotion is applied for this order");
						promotion_code_name_blank_array.add(promotion_code_name_blank);	
						promotion_code_name_blank = new JsonObject();
					}
					System.out.println(promotion_code_name_array);

					//--
					//end
					rootReturn.add("order_headers", order_header_array);
					rootReturn.add("receipts", order_receipt_array);

					rootReturn.add("header_charges",order_header_charges_array);
					rootReturn.add("line_charges",order_line_charges_array);

					//add the createts to the root object
					rootReturn.addProperty("createts", order_createts);
					rootReturn.add("order_line_tax",order_line_tax_array);
					rootReturn.add("order_total",order_total_field_array);
					rootReturn.add("promotioncode",promotion_code_array);
					rootReturn.add("promotionname",promotion_code_name_array);
					rootReturn.add("promotionnameblank",promotion_code_name_blank_array);
					//finally prep the rootReturn object to be sent to the page
					content = gson.toJson(rootReturn);
					System.out.println(rootReturn);
				}

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();

			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
			
			
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(omsState != null){omsState.close();}
			if(countResult != null){countResult.close();}
			if(omsState != null){omsState.close();}
			if(rs != null){rs.close();}
			if(orderTax != null){orderTax.close();}
			if(rs2 != null){rs2.close();}
			if(orderSet != null){orderSet.close();}
			if(con!=null) {con.close();}
			if(conOMSr_TXN_PRM!=null){conOMSr_TXN_PRM.close();}
		}
	}

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public void example_module(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value		= null;
		Connection		connection			= null;
		String			sql_query			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/example_includes/example_component/example_module.jsp";

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
		finally {
			if(connection!=null) connection.close();
		}
	}

	/*<Vaibhav code>*/
	public void invoice(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String				search_value		= null;
		Connection			con					= null;
		String				sql					= null;
		String				content 			= "-error-";
		PrintWriter			responseWriter		= null;
		String				jsp_include_page	= "/utility_includes/order_management/invoice.jsp";
		PreparedStatement	omsState			= null;

		logger.log(Level.INFO, "OrderManagementServlet:invoiceResponse | " + requestedPage + "|" + request.getParameter("orderNumber") + "["+search_value+"]" );

		try{
			if( "POST".equals(request.getMethod()) ){
				search_value	= request.getParameter("orderNumber");

				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					search_value	= search_value.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.OMS);

					sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ORDER_INVOICE_SQL);
					omsState = con.prepareStatement(sql);
					omsState.setString(1, search_value);

					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getPreparedSQLResult(omsState, SQLConstants.ID_UTIL_ORDER_INVOICE_SQL, con);
					Gson 		gson	= new GsonBuilder().create();
					content = gson.toJson(result);
				}
				responseWriter.write(content);
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
		finally{
			if(omsState!=null){omsState.close();}
			if(con!=null){con.close();}
		}
	}


	public void invoice_popup(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String				orderNo				= null;
		String 				invoiceNo 			= null;
		Connection			con					= null;
		String				sql					= null;
		String				content 			= "-error-";
		PrintWriter			responseWriter		= null;
		String				jsp_include_page	= "/utility_includes/order_management/invoice_popup.jsp";
		PreparedStatement	omsState			= null;

		logger.log(Level.INFO, "OrderManagementServlet:invoice_popupResponse | " + requestedPage + "|" + request.getParameter("orderNumber") + "["+orderNo+"]" );

		try{
			if( "POST".equals(request.getMethod()) ){
				logger.debug("In the post Method");
				orderNo	=  request.getParameter("orderNo");
				invoiceNo =  request.getParameter("invoiceNo");
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(orderNo)) {
					orderNo	= orderNo.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.OMS);

					sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ORDER_INVOICE_POPUP_SQL);
					omsState = con.prepareStatement(sql);
					omsState.setString(1,  orderNo);
					omsState.setString(2, invoiceNo);

					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getPreparedSQLResult(omsState, SQLConstants.ID_UTIL_ORDER_INVOICE_POPUP_SQL, con);
					Gson 		gson	= new GsonBuilder().create();
					content = gson.toJson(result);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page + "&isPoppup=true").forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally{
			if(omsState!=null){omsState.close();};
			if(con!=null){con.close();}
		}
	}

	/*</Vaibhav code>*/


	public void item(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value_item 	= null;
		String			search_value_webid 	= null;
		String			search_value_upc 	= null;
		Connection		con					= null;
		Connection		conGIV				= null;
		String			sql					= null;
		String			sql1				= null;
		String			sql2				= null;
		String			content 			= "-error-";
		PrintWriter		responseWriter		= null;
		String			items				= "";
		String			webids				= "";
		String			upcs				= "";
		JsonObject      imgReturn      		= new JsonObject();
		ArrayList<ResultSet> resultList 	= new ArrayList<ResultSet>();
		String			jsp_include_page	= "/utility_includes/order_management/item.jsp";
		PreparedStatement	givState		= null;
		ResultSet result = null;
		logger.log(Level.INFO, "OrderManagementServlet:itemResponse | "
				+ requestedPage 				 +	"|"
				+ request.getParameter("items") + 	"|"
				+ request.getParameter("webids")+ 	"|"
				+ request.getParameter("upcs"));

		try{
			Gson 		gson	= new GsonBuilder().create();
			if(StringUtils.isNotBlank(requestedPage) && StringUtils.equals(requestedPage, "item")){

				if(StringUtils.equals(request.getMethod(),"POST")){
					search_value_item	= request.getParameter("items");
					search_value_webid	= request.getParameter("webids");
					search_value_upc	= request.getParameter("upcs");

					responseWriter	= response.getWriter();
					response.setContentType("application/json");

					for(String itm: search_value_item.split(",")){
						items += "'"+itm.trim()+"',";
					}
					for(String web: search_value_webid.split(",")){
						webids += "'"+web.trim()+"',";
					}
					for(String upc: search_value_upc.split(",")){
						upcs += "'"+upc.trim()+"',";
					}
					if(items=="undefined"){
						items = "";
					}else if(webids=="undefined"){
						webids = "";
					}else if(upcs=="undefined"){
						upcs = "";
					}


					if (StringUtils.isNotBlank(search_value_item) || StringUtils.isNotBlank(search_value_webid) || StringUtils.isNotBlank(search_value_upc)) {

						//OMS Query for fields, Item, Web_ID, Desc, UPC, Primary Supplier etc. (non - sourcing relation)
						con				= ReportActivator.getInstance().getConnection(Constants.OMS);

						sql1 = "\nSELECT ";

						if(StringUtils.isNotBlank(search_value_item)){
							sql1 = sql1 +"DISTINCT yfs_item.item_id,"
									+"\n  yfs_item.extn_web_id,";
						}else if(StringUtils.isNotBlank(search_value_webid)){
							sql1 = sql1 +"DISTINCT yfs_item.extn_web_id,"
									+"\n  yfs_item.item_id,";
						}else if(StringUtils.isNotBlank(search_value_upc)){
							sql1 = sql1 +"DISTINCT yfs_item_alias.alias_value,"
									+"\n  yfs_item.extn_web_id,"
									+"\n  yfs_item.item_id,";
						}
						sql1 += SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ITEM_DETAIL_SQL);

						//Only item_id
						if (StringUtils.isNotBlank(search_value_item) && StringUtils.isBlank(search_value_webid)  && StringUtils.isBlank(search_value_upc)) {
							sql1 = sql1
									+"\nWHERE yfs_item.item_id in (" + items.substring(0,(items.length()-1)) + ") ";
							//Only web_id
						} else if (StringUtils.isNotBlank(search_value_webid) && StringUtils.isBlank(search_value_item) && StringUtils.isBlank(search_value_upc)) {
							sql1 = sql1
									+"\nWHERE yfs_item.extn_web_id in (" + webids.substring(0,(webids.length()-1)) + ") ";
							//Only UPC
						} else if (StringUtils.isBlank(search_value_webid) && StringUtils.isBlank(search_value_item) && StringUtils.isNotBlank(search_value_upc)) {
							sql1 = sql1
									+"\nWHERE yfs_item_alias.alias_value in (" + upcs.substring(0,(upcs.length()-1)) + ") ";
							//item_id AND web_id
						} else if (StringUtils.isNotBlank(search_value_webid) && StringUtils.isNotBlank(search_value_item) && StringUtils.isBlank(search_value_upc)) {
							sql1 = sql1
									+"\nWHERE yfs_item.extn_web_id in (" + webids.substring(0,(webids.length()-1)) + ") "
									+ "\nAND yfs_item.item_id in (" + items.substring(0,(items.length()-1)) + ") ";
							//item_id AND upc
						} else if (StringUtils.isBlank(search_value_webid) && StringUtils.isNotBlank(search_value_item) && StringUtils.isNotBlank(search_value_upc)) {
							sql1 = sql1
									+"\nWHERE yfs_item.item_id in (" + items.substring(0,(items.length()-1)) + ") "
									+ "\nAND yfs_item_alias.alias_value in (" + upcs.substring(0,(upcs.length()-1)) + ") ";
							//web_id AND upc
						} else if (StringUtils.isNotBlank(search_value_webid) && StringUtils.isBlank(search_value_item) && StringUtils.isNotBlank(search_value_upc)) {
							sql = sql
									+"\nWHERE yfs_item.extn_web_id in (" + webids.substring(0,(webids.length()-1)) + ") "
									+ "\nAND yfs_item_alias.alias_value in (" + upcs.substring(0,(upcs.length()-1)) + ") ";
							//All
						} else if (StringUtils.isNotBlank(search_value_webid) && StringUtils.isNotBlank(search_value_item) && StringUtils.isNotBlank(search_value_upc) ) {
							sql1 = sql1
									+"\nWHERE yfs_item.extn_web_id in (" + webids.substring(0,(webids.length()-1)) + ") "
									+ "\nAND yfs_item.item_id in (" + items.substring(0,(items.length()-1)) + ") "
									+ "\nAND yfs_item_alias.alias_value in (" + upcs.substring(0,(upcs.length()-1)) + ") ";
						}
						sql1= sql1
								+"\nORDER BY yfs_item.item_id ASC";


						JsonObject	root2			= new JsonObject();
						JsonArray	ITEM_ARRAY		= new JsonArray();
						JsonObject	ITEM_LINE		= new JsonObject();
						JsonArray	UPC_ARRAY		= new JsonArray();
						JsonObject	UPC_LINE		= new JsonObject();

						result = con.createStatement().executeQuery(sql1);

						logger.log(Level.INFO, "OrderManagementServlet:itemResponse | " + sql1);

						ArrayList<String> item_ids = new ArrayList<String>();
						ArrayList<String> web_ids = new ArrayList<String>();

						//connection for GIV columns
						conGIV				= ReportActivator.getInstance().getConnection(Constants.GIV);

						//building item JSON Object
						while(result.next()){
							String ITEM_ID					 	= result.getString("ITEM_ID"					).trim();
							String EXTN_WEB_ID					= result.getString("EXTN_WEB_ID"				);
							String SHORT_DESCRIPTION         	= result.getString("SHORT_DESCRIPTION"			);
							String PRIMARY_SUPPLIER         	= result.getString("PRIMARY_SUPPLIER"			);
							String EXTN_DIRECT_SHIP_ITEM       	= result.getString("EXTN_DIRECT_SHIP_ITEM"		);
							String EXTN_SHIP_ALONE              = result.getString("EXTN_SHIP_ALONE"			);
							String EXTN_CAGE_ITEM         		= result.getString("EXTN_CAGE_ITEM"				);
							String EXTN_IS_PLASTIC_GIFT_CARD	= result.getString("EXTN_IS_PLASTIC_GIFT_CARD"	);
							String EXTN_BREAKABLE               = result.getString("EXTN_BREAKABLE"				);
							String ALLOW_GIFT_WRAP              = result.getString("ALLOW_GIFT_WRAP"			);

							sql2 = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_ITEM_DETAIL_SQL2);
							givState = conGIV.prepareStatement(sql2);
							givState.setString(1, ITEM_ID);

							ConcurrentHashMap<Integer, HashMap<String, Object>> result2 = SQLUtils.getPreparedSQLResult(givState, SQLConstants.ID_UTIL_ITEM_DETAIL_SQL2, conGIV);

							for (HashMap<String, Object>map : result2.values()) {
								ITEM_LINE.addProperty("ITEM_TYPE", String.valueOf(map.get("ITEM_TYPE")).trim());
								ITEM_LINE.addProperty("PRODUCT_LINE", String.valueOf(map.get("PRODUCT_LINE")).trim());
								ITEM_LINE.addProperty("EXTN_SHIP_NODE_SOURCE", String.valueOf(map.get("EXTN_SHIP_NODE_SOURCE")).trim());
								ITEM_LINE.addProperty("EXTN_NOMADIC", String.valueOf(map.get("EXTN_NOMADIC")).trim());
								ITEM_LINE.addProperty("ONHAND_SAFETY_FACTOR_QTY", String.valueOf(map.get("ONHAND_SAFETY_FACTOR_QTY")).trim());
							}

							String EXTN_BAGGAGE                	= result.getString("EXTN_BAGGAGE"				);
							String IS_HAZMAT                	= result.getString("IS_HAZMAT"					);
							String EXTN_RED_PACK_LIST_TYPE      = result.getString("EXTN_RED_PACK_LIST_TYPE"	);
							String ONHAND_SAFETY_FACTOR_PCT     = result.getString("ONHAND_SAFETY_FACTOR_PCT"	);


							ITEM_LINE.addProperty("ITEM_ID",				ITEM_ID						);
							ITEM_LINE.addProperty("EXTN_WEB_ID",			EXTN_WEB_ID					);
							ITEM_LINE.addProperty("SHORT_DESCRIPTION",		SHORT_DESCRIPTION			);
							ITEM_LINE.addProperty("PRIMARY_SUPPLIER",		PRIMARY_SUPPLIER			);
							ITEM_LINE.addProperty("EXTN_DIRECT_SHIP_ITEM",	EXTN_DIRECT_SHIP_ITEM		);
							ITEM_LINE.addProperty("EXTN_SHIP_ALONE",		EXTN_SHIP_ALONE				);
							ITEM_LINE.addProperty("EXTN_CAGE_ITEM",			EXTN_CAGE_ITEM				);
							ITEM_LINE.addProperty("EXTN_IS_PLASTIC_GIFT_CARD",EXTN_IS_PLASTIC_GIFT_CARD	);
							ITEM_LINE.addProperty("EXTN_BREAKABLE",			EXTN_BREAKABLE				);
							ITEM_LINE.addProperty("ALLOW_GIFT_WRAP",		ALLOW_GIFT_WRAP				);

							ITEM_LINE.addProperty("EXTN_BAGGAGE",			EXTN_BAGGAGE				);
							ITEM_LINE.addProperty("IS_HAZMAT",				IS_HAZMAT					);
							ITEM_LINE.addProperty("EXTN_RED_PACK_LIST_TYPE",EXTN_RED_PACK_LIST_TYPE		);
							ITEM_LINE.addProperty("ONHAND_SAFETY_FACTOR_PCT",ONHAND_SAFETY_FACTOR_PCT	);

							imgReturn = itemDetails(ITEM_ID);
							String img_path = String.valueOf(imgReturn.get("IMG_URL"));
							img_path = img_path.replace("\"", "");

							ITEM_LINE.addProperty("IMG_URL",			img_path);


							item_ids.add(ITEM_ID);
							web_ids.add(EXTN_WEB_ID);

							ITEM_ARRAY.add(ITEM_LINE);
							int i=0;

							//query for finding UPC's per item_id or web_id
							sql =  "\nSELECT yfs_item_alias.alias_value,"
									+"yfs_item_alias.alias_name,"
									+"yfs_item.item_id"
									+"\nFROM Yfs_Item"
									+"\nJOIN Yfs_Item_Alias ON yfs_item_alias.item_key = yfs_item.item_key"
									+"\nWHERE yfs_item.item_id in ('" + item_ids.get(i).trim() +"') "
									+"\nAND yfs_item.extn_web_id in ('" + web_ids.get(i).trim() + "') "
									+"\nAND yfs_item_alias.alias_name LIKE 'U%'"
									+"\nORDER BY yfs_item.item_id ASC";
							logger.debug("item_id @ index: "+i+" = " + item_ids.get(i));
							logger.debug("web_id @ index: "+i+" = " + web_ids.get(i));

							logger.log(Level.INFO, "OrderManagementServlet:itemResponse | " + sql);


							//creating UPC JSON Object for each Item_ID found
							while(i < item_ids.size()){
								resultList.add(i, con.createStatement().executeQuery(sql));

								while(resultList.get(i).next()){
									String ALIAS_VALUE		  = resultList.get(i).getString("ALIAS_VALUE");
									String ALIAS_NAME		  = resultList.get(i).getString("ALIAS_NAME");


									UPC_LINE.addProperty("ALIAS_VALUE",			ALIAS_VALUE);
									UPC_LINE.addProperty("ALIAS_NAME",			ALIAS_NAME);



									UPC_ARRAY.add(UPC_LINE);
									ITEM_LINE.add("UPC", UPC_ARRAY);
									UPC_LINE		= new JsonObject();
								}
								i++;
							}

							//clearing the results lists
							item_ids.clear();
							resultList.clear();
							ITEM_LINE		= new JsonObject();
							UPC_ARRAY = new JsonArray();

						}

						//add JSON array to root.
						content	= gson.toJson(root2);
						root2.add("item_details", ITEM_ARRAY);
						content	= gson.toJson(root2);

					}
					responseWriter.write(content);
					responseWriter.flush();
					responseWriter.close();
				}else{
					//assume it's GET request, load JSP
					//request.getRequestDispatcher(jsp_page).include(request, response);
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}
			}else{
				//no include found
				request.getRequestDispatcher(defaultPage).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(givState!=null) givState.close();
			if(result!=null) result.close();
			if(con!=null) con.close();
			if(conGIV!=null) conGIV.close();
		}
	}

	public void order_status(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value	= null;
		Connection		con				= null;
		String			sql				= null;
		String			content 		= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page		= "/utility_includes/order_management/order_status.jsp";
		JsonArray		status_array			= new JsonArray();
		JsonObject		status_object 		= new JsonObject();
		JsonObject		rootReturn  			= new JsonObject();

		logger.log(Level.INFO, "OrderManagementServlet:order_statusResponse | " + requestedPage + "|" + request.getParameter("orderNumbers"));

		try{
			if(StringUtils.isNotBlank(requestedPage) && StringUtils.equals(requestedPage, "order_status")){

				if(StringUtils.equals(request.getMethod(),"POST")){
					search_value	= request.getParameter("orderNumbers");
					responseWriter	= response.getWriter();
					response.setContentType("application/json");

					if (!com.storeelf.util.StringUtils.isVoid(search_value)) {

						search_value = search_value.trim();
						search_value = search_value.replaceAll("\\s+","");
						search_value = search_value.replaceAll("[\n\r]", "");
						search_value = search_value.replaceAll("'","");
						search_value = search_value.replaceAll(",","','");
						search_value = "'" + search_value + "'";

						con				= ReportActivator.getInstance().getConnection(Constants.OMS);


						sql = "WITH t1 AS                                    " +    
								"  (SELECT a.order_no,                         " +
								"    a.order_header_key,                       " +
								"    a.document_type                           " +
								"  FROM yfs_order_header a                     " +
								"  WHERE a.order_no IN (" + search_value + ")  " +
								"  )                                           " +
								"SELECT t1.order_no,                           " +
								"  CASE                                        " +
								"    WHEN MIN(s.STATUS) = '3350.035'           " +
								"    THEN 'Ready For Customer Pick Up'         " +
								"    WHEN MIN(s.STATUS) = '3700.2'             " +
								"    THEN 'Customer Picked Up'                 " +
								"    WHEN MIN(s.STATUS) = '3700.11'            " +
								"    THEN 'Expired Pickup'                     " +
								"    WHEN MIN(s.STATUS) <= 2100                " +
								"    THEN 'Sourcing'                           " +
								"    WHEN MIN(s.status) >= 2100                " +
								"    AND MIN(s.status)   < 3700                " +
								"    THEN 'In Fulfillment'                     " +
								"    WHEN MIN(s.status) = 3700                 " +
								"    THEN 'Shipped'                            " +
								"    WHEN MIN(s.status) = 9000                 " +
								"    THEN 'Cancelled'                          " +
								"    ELSE 'Invoiced'                           " +
								"  END Status                                  " +
								"FROM t1                                       " +
								"JOIN yfs_order_release_status s               " +
								"ON s.order_header_key   = t1.order_header_key " +
								"WHERE s.status_quantity > 0                   " +
								"AND s.status           <> '1400'              " +
								"GROUP BY t1.order_no			                ";

						ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

						for (HashMap<String, Object> map : result.values()) {
							status_object.addProperty("ORDER_NO", String.valueOf(map.get("ORDER_NO")).trim());
							status_object.addProperty("STATUS", String.valueOf(map.get("STATUS")).trim());
							status_array.add(status_object);
							status_object = new JsonObject();
						}
						rootReturn.add("order_statuses", status_array);

						Gson 		gson	= new GsonBuilder().create();
						content = gson.toJson(rootReturn);
					}
					responseWriter.write(content);
					responseWriter.flush();
					responseWriter.close();
				}else{
					//assume it's GET request, load JSP
					//request.getRequestDispatcher(jsp_page).include(request, response);
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}
			}else{
				//no include found
				request.getRequestDispatcher(defaultPage).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally{
			if(con!=null) con.close();
		}
	}


	public void order_release_history(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value	= null;
		Connection		con				= null;
		String			sql				= null;
		String			response_content 		= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page		= "/utility_includes/order_management/order_release_history.jsp";

		logger.log(Level.INFO, "OrderManagementServlet:order_release_history Response | " + requestedPage + "|" + request.getParameter("orderNumbers"));
		ResultSet result = null;
		try{
			Gson 		gson				= new GsonBuilder().create();
			if(StringUtils.isNotBlank(requestedPage) && StringUtils.equals(requestedPage, "order_release_history")){

				if(StringUtils.equals(request.getMethod(),"POST")){
					search_value	= request.getParameter("orderNumbers");
					responseWriter	= response.getWriter();
					response.setContentType("application/json");

					if (!com.storeelf.util.StringUtils.isVoid(search_value)) {

						search_value = search_value.trim();
						search_value = search_value.replaceAll("[\n\r]", "");
						search_value = search_value.replaceAll("'","");
						search_value = search_value.replaceAll(",","','");
						search_value = "'" + search_value + "'";
						con				= ReportActivator.getInstance().getConnection(Constants.OMS);

						JsonArray	root				= new JsonArray();
						JsonObject	row					= new JsonObject();

						sql = "SELECT"+
								"  o.ORDER_NO as ORDR_NO, "+
								"  TO_NUMBER((SELECT l.LINE_SEQ_NO FROM YFS_ORDER_LINE l WHERE l.ORDER_LINE_KEY = rs.order_line_key)) as LNE_NO, "+
								"  (SELECT l.ITEM_ID FROM STERLING.YFS_ORDER_LINE l WHERE l.ORDER_LINE_KEY = rs.order_line_key) as ITEM_ID," +
								"  TRIM(rs.STATUS) as STAT, "+
								"  (SELECT s.STATUS_NAME FROM yfs_status s WHERE PROCESS_TYPE_KEY = 'ORDER_FULFILLMENT' AND rs.status = s.STATUS) as STAT_MAP, "+
								"  rs.STATUS_DATE, rs.CREATETS, rs.CREATEPROGID, rs.CREATEUSERID, rs.MODIFYTS, rs.MODIFYPROGID, rs.MODIFYUSERID, rs.order_line_key "+
								" FROM yfs_order_header o,  yfs_order_release_status rs "+
								" WHERE rs.order_header_key = o.order_header_key "+
								" AND o.ORDER_NO = "+ search_value +""+
								" ORDER BY LNE_NO, rs.status_date asc"

									;

						String CREATEPROGID		= null;
						String CREATETS         = null;
						String CREATEUSERID     = null;
						String LNE_NO           = null;
						String ITEM_ID           = null;
						String MODIFYPROGID     = null;
						String MODIFYTS         = null;
						String MODIFYUSERID     = null;
						String ORDER_LINE_KEY   = null;
						String ORDR_NO          = null;
						String STAT             = null;
						String STATUS_DATE      = null;
						String STAT_MAP         = null;

						result = con.createStatement().executeQuery(sql);

						while(result.next()){
							CREATEPROGID		= result.getString("CREATEPROGID");
							CREATETS         = result.getString("CREATETS");
							CREATEUSERID     = result.getString("CREATEUSERID");
							LNE_NO           = result.getString("LNE_NO");
							ITEM_ID           = result.getString("ITEM_ID");
							MODIFYPROGID     = result.getString("MODIFYPROGID");
							MODIFYTS         = result.getString("MODIFYTS");
							MODIFYUSERID     = result.getString("MODIFYUSERID");
							ORDER_LINE_KEY   = result.getString("ORDER_LINE_KEY");
							ORDR_NO          = result.getString("ORDR_NO");
							STAT             = result.getString("STAT");
							STATUS_DATE      = result.getString("STATUS_DATE");
							STAT_MAP         = result.getString("STAT_MAP");

							row.addProperty("CREATEPROGID", 	CREATEPROGID  );
							row.addProperty("CREATETS", 		CREATETS      );
							row.addProperty("CREATEUSERID", 	CREATEUSERID  );
							row.addProperty("LNE_NO", 			LNE_NO        );
							row.addProperty("ITEM_ID", 			ITEM_ID       );
							row.addProperty("MODIFYPROGID", 	MODIFYPROGID  );
							row.addProperty("MODIFYTS", 		MODIFYTS      );
							row.addProperty("MODIFYUSERID", 	MODIFYUSERID  );
							row.addProperty("ORDER_LINE_KEY",	ORDER_LINE_KEY);
							row.addProperty("ORDR_NO", 			ORDR_NO       );
							row.addProperty("STAT", 			STAT          );
							row.addProperty("STATUS_DATE", 		STATUS_DATE   );
							row.addProperty("STAT_MAP", 		STAT_MAP      );

							root.add(row);
							row = new JsonObject();

						}

						response_content	= gson.toJson(root);

						/*ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);
							Gson 		gson	= new GsonBuilder().create();
										content = gson.toJson(result);*/
					}
					responseWriter.write(response_content);
					responseWriter.flush();
					responseWriter.close();
				}else{
					//assume it's GET request, load JSP
					//request.getRequestDispatcher(jsp_page).include(request, response);
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}
			}else{
				//no include found
				request.getRequestDispatcher(defaultPage).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally{
			if(result!=null) result.close();
			if(con!=null) con.close();
		}
	}

	public void order_release(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value	= null;
		Connection		con				= null;
		String			sql				= null;
		String			response_content	= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page		= "/utility_includes/order_management/order_release.jsp";

		logger.log(Level.INFO, "OrderManagementServlet:order_releaseResponse | " + requestedPage + "|" + request.getParameter("orderReleaseNumber"));
		ResultSet result = null;
		try{
			Gson 		gson				= new GsonBuilder().create();
			if(StringUtils.isNotBlank(requestedPage) && StringUtils.equals(requestedPage, "order_release")){

				if(StringUtils.equals(request.getMethod(),"POST")){
					search_value	= request.getParameter("orderReleaseNumber");
					responseWriter	= response.getWriter();
					response.setContentType("application/json");

					if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
						search_value	= search_value.trim();

						con				= ReportActivator.getInstance().getConnection(Constants.OMS);

						sql = " SELECT yfs_order_release.SALES_ORDER_NO, TO_CHAR(yfs_order_release.ORDER_DATE,'MM/DD/YYYY HH24:MI:SS') ORDER_DATE, TO_CHAR(yfs_order_release_status.modifyts,'MM/DD/YYYY HH24:MI:SS') modifyts, CASE YFS_ORDER_RELEASE.document_type WHEN '0005' THEN yfs_order_release.RELEASE_NO||TO_CHAR(SUBSTR(SALES_ORDER_NO,INSTR(SALES_ORDER_NO,'_',1)+0,2)) ELSE TO_CHAR(yfs_order_release.RELEASE_NO) END AS RELEASE_NO_EX, yfs_order_release.RELEASE_NO,yfs_order_release.SHIPNODE_KEY, yfs_order_release.EXTN_PICK_TICKET_NO AS PICKTICKET_NO, line.PRIME_LINE_NO, line.item_id, yfs_order_release_status.status_quantity, CASE st.status WHEN '1100' THEN 'Created' WHEN '1100.001' THEN 'DSV Order Shipped' WHEN '1300' THEN 'Back Ordered' WHEN '1310' THEN 'Unscheduled' WHEN '1400' THEN 'Cancelled' WHEN '1500' THEN 'Scheduled' WHEN '3200' THEN 'Released' WHEN '3200.03' THEN 'Sent To WMoS' WHEN '3350.01' THEN 'SA Sent To WMoS' WHEN '3700' THEN 'Shipped' WHEN '3700.0004' THEN 'Invoiced' WHEN '8000' THEN 'Held' WHEN '9000' THEN 'Cancelled' WHEN '9020' THEN 'Shorted'  WHEN '3350'        THEN      'Included In Shipment' WHEN '3350.035'    THEN      'Ready For Customer Pick Up' WHEN '3350.11' THEN      'Awaiting Store Pick' WHEN '3350.12'     THEN      'Store Pick in Progress' WHEN '3350.13' THEN      'Store Pick Completed' WHEN '3350.14' THEN      'Placed in Hold Location' WHEN '3700.2'      THEN      'Customer Picked Up' ELSE ST.STATUS_NAME END AS STATUS, (trim(yfs_person_info.address_line1) || trim(yfs_person_info.address_line2) ||', '|| trim(yfs_person_info.city)||', '|| trim(yfs_person_info.state)||', '|| trim(yfs_person_info.zip_code)) AS SHIP_TO FROM yfs_order_release, yfs_status st, yfs_order_release_status, yfs_order_line line,yfs_person_info WHERE yfs_order_release.sales_order_no like '"+ search_value + "%' AND yfs_order_release.order_release_key = yfs_order_release_status.order_release_key AND st.status = yfs_order_release_status.status AND st.process_type_key in ('ORDER_FULFILLMENT','PO_FULFILLMENT') and yfs_order_release_status.status_quantity > 0 and line.order_line_key=yfs_order_release_status.order_line_key and yfs_order_release.ship_to_key=yfs_person_info.person_info_key UNION SELECT yfs_order_release.SALES_ORDER_NO, TO_CHAR(yfs_order_release.ORDER_DATE,'MM/DD/YYYY HH24:MI:SS') ORDER_DATE, TO_CHAR(yfs_order_release_status.modifyts,'MM/DD/YYYY HH24:MI:SS') modifyts, TO_CHAR(yfs_shipment_line.RELEASE_NO) AS RELEASE_NO_EX, yfs_shipment_line.RELEASE_NO, yfs_shipment.SHIPNODE_KEY, yfs_shipment.PICKTICKET_NO, yfs_shipment_line.PRIME_LINE_NO, yfs_shipment_line.item_id, yfs_order_release_status.status_quantity, CASE st.status WHEN '1100' THEN 'Created' WHEN '1100.001' THEN 'DSV Order Shipped' WHEN '1300' THEN 'Back Ordered' WHEN '1310' THEN 'Unscheduled' WHEN '1400' THEN 'Cancelled' WHEN '1500' THEN 'Scheduled' WHEN '3200' THEN 'Released' WHEN '3200.03' THEN 'Sent To WMoS' WHEN '3350.01' THEN 'SA Sent To WMoS' WHEN '3700' THEN 'Shipped' WHEN '3700.0004' THEN 'Invoiced' WHEN '8000' THEN 'Held' WHEN '9000' THEN 'Cancelled' WHEN '9020' THEN 'Shorted'  WHEN '3350'        THEN      'Included In Shipment' WHEN '3350.035' THEN      'Ready For Customer Pick Up' WHEN '3350.11'     THEN      'Awaiting Store Pick' WHEN '3350.12'     THEN      'Store Pick in Progress' WHEN '3350.13'     THEN      'Store Pick Completed' WHEN '3350.14'     THEN      'Placed in Hold Location' WHEN '3700.2' THEN      'Customer Picked Up' ELSE ST.STATUS_NAME END AS STATUS, (trim(yfs_person_info.address_line1) || trim(yfs_person_info.address_line2) ||', '|| trim(yfs_person_info.city)||', '|| trim(yfs_person_info.state)||', '|| trim(yfs_person_info.zip_code)) AS SHIP_TO FROM yfs_order_release, yfs_shipment_line yfs_shipment_line, YFS_SHIPMENT, yfs_status st, yfs_order_release_status,yfs_person_info WHERE yfs_order_release.EXTN_PICK_TICKET_NO = ' ' AND yfs_order_release.ORDER_RELEASE_KEY = yfs_shipment_line.ORDER_RELEASE_KEY AND yfs_order_release.SALES_ORDER_NO like '"+ search_value + "%' AND yfs_shipment_line.SHIPMENT_KEY = yfs_shipment.SHIPMENT_KEY AND yfs_order_release.order_release_key = yfs_order_release_status.order_release_key AND st.status = yfs_order_release_status.status AND st.process_type_key in ('ORDER_FULFILLMENT','PO_FULFILLMENT') and yfs_order_release.ship_to_key=yfs_person_info.person_info_key and yfs_order_release_status.status_quantity > 0 and YFS_SHIPMENT.PICKTICKET_NO <> null ";

						result = con.createStatement().executeQuery(sql);
						JsonObject	ORDER_RELEASE			= new JsonObject();
						JsonObject	ORDER_DETAILS	= new JsonObject();
						JsonObject	root				= new JsonObject();
						JsonArray	ORDER_RELEASE_ARRAY	= new JsonArray();

						int record_index			= 0;

						while(result.next()){

							String SALES_ORDER_NO					= result.getString("SALES_ORDER_NO");
							String ORDER_DATE                     	= result.getString("ORDER_DATE");
							String MODIFYTS        					= result.getString("MODIFYTS");
							String RELEASE_NO       				= result.getString("RELEASE_NO");
							String SHIPNODE_KEY                 	= result.getString("SHIPNODE_KEY");
							String PICKTICKET_NO               		= result.getString("PICKTICKET_NO").trim();
							String RELEASE_NO_EX 					= result.getString("RELEASE_NO_EX");
							String STATUS                       	= result.getString("STATUS");
							String SHIP_TO	                       	= result.getString("SHIP_TO");

							String ITEM_ID               			= result.getString("ITEM_ID");
							String PRIME_LINE_NO               		= result.getString("PRIME_LINE_NO");
							String STATUS_QUANTITY                  = result.getString("STATUS_QUANTITY");

							logger.log(Level.INFO, "OrderManagementServlet:order_releaseResponse | " + requestedPage + "|" + request.getParameter("orderReleaseNumber"));

							ORDER_DETAILS.addProperty("ITEM_ID",ITEM_ID);
							ORDER_DETAILS.addProperty("PRIME_LINE_NO",PRIME_LINE_NO);
							ORDER_DETAILS.addProperty("STATUS_QUANTITY",STATUS_QUANTITY);



							if(record_index==0 || ( (ORDER_RELEASE.has("RELEASE_NO_EX")) && !org.apache.commons.lang.StringUtils.equals(ORDER_RELEASE.get("RELEASE_NO_EX").getAsString(), RELEASE_NO_EX))){

								ORDER_RELEASE = new JsonObject();

								ORDER_RELEASE.add("ORDER_ARRAY", 			new JsonArray());

								ORDER_RELEASE.addProperty("SALES_ORDER_NO",			SALES_ORDER_NO		);
								ORDER_RELEASE.addProperty("ORDER_DATE",			ORDER_DATE		);
								ORDER_RELEASE.addProperty("MODIFYTS",			MODIFYTS			);
								ORDER_RELEASE.addProperty("RELEASE_NO",			RELEASE_NO			);
								ORDER_RELEASE.addProperty("RELEASE_NO_EX", RELEASE_NO_EX);
								ORDER_RELEASE.addProperty("SHIPNODE_KEY",			SHIPNODE_KEY		);
								ORDER_RELEASE.addProperty("PICKTICKET_NO",				PICKTICKET_NO				);
								ORDER_RELEASE.addProperty("STATUS",		STATUS	);
								ORDER_RELEASE.addProperty("SHIP_TO",				SHIP_TO			);

								ORDER_RELEASE.getAsJsonArray("ORDER_ARRAY").add(ORDER_DETAILS);

								ORDER_RELEASE_ARRAY.add(ORDER_RELEASE);
							}else{

								ORDER_RELEASE.getAsJsonArray("ORDER_ARRAY").add(ORDER_DETAILS);
							}
							logger.log(Level.INFO, "OrderManagementServlet:order_releaseResponse | " + requestedPage + "|" + request.getParameter("orderReleaseNumber"));
							ORDER_DETAILS= new JsonObject();

							record_index++;
						}
						logger.log(Level.INFO, "OrderManagementServlet:order_releaseResponse | " + requestedPage + "|" + request.getParameter("orderReleaseNumber"));
						root.add("ORDERS", ORDER_RELEASE_ARRAY);
						response_content	= gson.toJson(root);

					}
					responseWriter.write(response_content);
					responseWriter.flush();
					responseWriter.close();
				}else{
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}
			}
			
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(result!=null) result.close();
			if(con!=null) con.close();
		}
	}


	public void inventory(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		String			search_value	= null;
		int             type            = 1;
		String			content 		= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page= "/utility_includes/order_management/inventory.jsp";
		JsonObject      rootReturn      = new JsonObject();
		Gson            gson            = new GsonBuilder().create();

		logger.log(Level.INFO, "OrderManagementServlet:inventoryResponse | " + requestedPage + "|" + request.getParameter("item"));

		try{
			if(StringUtils.isNotBlank(requestedPage) && StringUtils.equals(requestedPage, "inventory")){

				if(StringUtils.equals(request.getMethod(),"POST")){
					search_value	= request.getParameter("item");
					type	= Integer.parseInt(request.getParameter("type")); //OMS - 1, GIV - 2, EFC - 3, FS - 4
					responseWriter	= response.getWriter();
					response.setContentType("application/json");

					if (!com.storeelf.util.StringUtils.isVoid(search_value)) {

						search_value	= search_value.trim();

						switch(type){
						case 1: //do work to combine resulting JsonObjects
							rootReturn = omsInventory(search_value);   //oms summary data
							//rootReturn.add("efc", omsInventory(search_value));   //efc (oms) summary + detail data
							//rootReturn.add("rdc", omsInventory(search_value));   //rdc (oms) summary + detail data
							//rootReturn.add("dsv", omsInventory(search_value));   //dsv (oms) summary + detail data
							//rootReturn.add("giv", givInventory(search_value)); //giv store summary + detail data
							content = gson.toJson(rootReturn);
							break;
						case 2: content = efcInventory(search_value);
						break;
						case 3: content = fsInventory(search_value);
						break;
						case 4: rootReturn = ecomInventory(search_value);
						content = gson.toJson(rootReturn);
						break;
						case 5: rootReturn = omsInventory2(search_value);
						content = gson.toJson(rootReturn);
						break;

						default: rootReturn.addProperty("error", "Incorrect Request Parameter");
						break;
						}

					}
					responseWriter.write(content);
					responseWriter.flush();
					responseWriter.close();
				}else{
					//assume it's GET request, load JSP
					//request.getRequestDispatcher(jsp_page).include(request, response);
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
				}
			}else{
				//no include found
				request.getRequestDispatcher(defaultPage).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}

	
	public JsonObject omsInventory2(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{


		String			  search_value	  			 = search_string;
		Connection		  conGIV1	      			 = null;
		String            sql_giv1        			 = null;
		String            sql_alert       			 = null;
		PreparedStatement givState					 = null;
		PreparedStatement alertState        		 = null;
		JsonObject root_return = null;

		try{
		conGIV1 = ReportActivator.getInstance().getConnection(Constants.GIV); //Modified connection for GIV R.1.1 Changes

		sql_giv1 = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_OMS_SQL_QUICK);			

		givState = conGIV1.prepareStatement(sql_giv1);			
		givState.setString(1, search_value.trim());	

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms = SQLUtils.getPreparedSQLResult(givState,SQLConstants.ID_UTIL_INV_OMS_SQL, conGIV1);

		sql_alert = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_OMS_ALERT_SQL);		

		alertState = conGIV1.prepareStatement(sql_alert);
		alertState.setString(1, search_value.trim());
		alertState.setString(2, search_value.trim());

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms2 = SQLUtils.getPreparedSQLResult(alertState,SQLConstants.ID_UTIL_INV_OMS_ALERT_SQL, conGIV1);

		
		/*
		 * Begin Logic to create the end result set
		 * 
		 */
		root_return = new JsonObject();
		JsonObject oms_root    = new JsonObject();


		int tot_supply=0;
		int tot_demand=0;
		int row_supply=0;
		int row_demand=0;
		String short_desc="";
		String item_id="";
		int available_to_ecomm=0;
		//String avail_quant = "";
		//String sfty_factor = "";

		//iterate through returned result set and perform calculations for total supply/demand and available to Ecomm
		for (HashMap<String, Object> map : result_oms.values()) {
			item_id = String.valueOf(map.get("ITEM_ID"));
			short_desc = String.valueOf(map.get("SHORT_DESCRIPTION"));
			row_demand = Integer.valueOf(String.valueOf(map.get("DEMAND")));
			row_supply = Integer.valueOf(String.valueOf(map.get("SUPPLY")));
			tot_supply += row_supply;
			tot_demand += row_demand;

		}

		available_to_ecomm = tot_supply-tot_demand;

		//Add calculated results to a Hash Map to return as JSON

		JsonObject oms_summary = new JsonObject();

		oms_summary.addProperty("ITEM_ID", item_id);
		oms_summary.addProperty("SHORT_DESCRIPTION", short_desc);
		oms_summary.addProperty("TOTAL_SUPPLY", String.valueOf(tot_supply));
		oms_summary.addProperty("TOTAL_DEMAND", String.valueOf(tot_demand));
		oms_summary.addProperty("AVAILABLE_TO_ECOMM", String.valueOf(available_to_ecomm));
		//retrieve available onhand value from query result
		for (HashMap<String, Object> map : result_oms2.values()) {
			for(Entry<String, Object> row :map.entrySet()){
				oms_summary.addProperty(row.getKey(), String.valueOf(row.getValue()));
			}
		}

		JsonObject imgReturn = itemDetails(item_id);
		String img_path = String.valueOf(imgReturn.get("IMG_URL"));
		img_path = img_path.replace("\"", "");

		oms_summary.addProperty("IMG_URL",			img_path);

		oms_root.add("summary", oms_summary);
		


		String row_shipnode_key 		= "";
		String oms_row_supply       	= "";
		String oms_row_demand       	= "";
		int row_available_to_fulfill 	= 0;
		int total_supply_efc			= 0;
		int total_supply_rdc            = 0;
		int total_supply_dsv            = 0;
		int total_supply_stores         = 0;
		int total_demand_efc			= 0;
		int total_demand_rdc            = 0;
		int total_demand_dsv            = 0;
		int total_demand_stores         = 0;
		int total_available_to_fulfill_efc  = 0;
		int total_available_to_fulfill_rdc  = 0;
		int total_available_to_fulfill_dsv  = 0;
		int total_available_to_fulfill_stores = 0;
		//EFC object
		JsonObject efc_summary          = new JsonObject(); //summary
		JsonObject efc_inventory        = new JsonObject();
		JsonArray  efc_inventory_array  = new JsonArray();

		JsonObject rdc_summary          = new JsonObject();
		JsonObject rdc_inventory        = new JsonObject();
		JsonArray  rdc_inventory_array  = new JsonArray();

		JsonObject dsv_summary          = new JsonObject();
		JsonObject dsv_inventory        = new JsonObject();
		JsonArray  dsv_inventory_array  = new JsonArray();

		//OMS - EFC/RDC/DSV SECTION
		//for the result set of OMS data, iterate through each data row and add them to a HashMap<String,String>
		for(HashMap <String,Object> map : result_oms.values()){

			row_shipnode_key = String.valueOf(map.get("SHIPNODE_KEY"));
			oms_row_supply = String.valueOf(map.get("SUPPLY"));
			oms_row_demand = String.valueOf(map.get("DEMAND"));
			row_available_to_fulfill = Integer.parseInt(oms_row_supply) - Integer.parseInt(oms_row_demand);

			//if the shipnode_key isn't blank or null
			if((!row_shipnode_key.trim().equalsIgnoreCase("")) && (row_shipnode_key!=null)){

				if(row_shipnode_key.startsWith("E") || row_shipnode_key.startsWith("L")){
					//put in the values from the DB row
					efc_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					efc_inventory.addProperty("SUPPLY",oms_row_supply);
					efc_inventory.addProperty("DEMAND", oms_row_demand);
					efc_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

					total_supply_efc += Integer.parseInt(oms_row_supply);
					total_demand_efc += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_efc += row_available_to_fulfill;


					efc_inventory_array.add(efc_inventory);

					efc_inventory = new JsonObject();

				} else if (row_shipnode_key.startsWith("R")){
					//put in the values from the DB row
					rdc_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					rdc_inventory.addProperty("SUPPLY",oms_row_supply);
					rdc_inventory.addProperty("DEMAND", oms_row_demand);
					rdc_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));


					//add to the running totals
					total_supply_rdc += Integer.parseInt(oms_row_supply);
					total_demand_rdc += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_rdc += row_available_to_fulfill;

					rdc_inventory_array.add(rdc_inventory);

					rdc_inventory = new JsonObject();

				} else if (row_shipnode_key.length() > 4) {

					//put in the values from the DB row
					dsv_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					dsv_inventory.addProperty("SUPPLY",oms_row_supply);
					dsv_inventory.addProperty("DEMAND", oms_row_demand);
					dsv_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

					//add to the running totals
					total_supply_dsv += Integer.parseInt(oms_row_supply);
					total_demand_dsv += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_dsv += row_available_to_fulfill;

					dsv_inventory_array.add(dsv_inventory);

					dsv_inventory = new JsonObject();

				} else {

			
					total_supply_stores += Integer.parseInt(oms_row_supply);
					total_demand_stores += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_stores += row_available_to_fulfill;

				
				}

			}

		}


		//prepare the efc_summary object
		efc_summary.addProperty("SUPPLY", total_supply_efc);
		efc_summary.addProperty("DEMAND", total_demand_efc);
		efc_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_efc);
		//add the efc_summary and inventory array to the root object
		oms_root.add("efc_summary", efc_summary);
		oms_root.add("efc_inventory", efc_inventory_array);
		//prepare the rdc_summary object
		rdc_summary.addProperty("SUPPLY", total_supply_rdc);
		rdc_summary.addProperty("DEMAND", total_demand_rdc);
		rdc_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_rdc);
		//add the rdc_summary and inventory array to the root object
		oms_root.add("rdc_summary", rdc_summary);
		oms_root.add("rdc_inventory", rdc_inventory_array);
		//prepare the dsv_summary object
		dsv_summary.addProperty("SUPPLY", total_supply_dsv);
		dsv_summary.addProperty("DEMAND", total_demand_dsv);
		dsv_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_dsv);
		//add the dsv_summary and inventory array to the root object
		oms_root.add("dsv_summary", dsv_summary);
		oms_root.add("dsv_inventory", dsv_inventory_array);
		//prepare the store_summary object
	

		//add all the root objects to the "returned" root
		root_return.add("oms", oms_root);

		try{
			
		}catch (Exception e){
			logger.log(Level.INFO, "omsInventory - Cannot close GIV1 connection as it is already closed." );
		}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(givState!=null){givState.close();}
			if(alertState!=null){alertState.close();}
			if(conGIV1!=null) conGIV1.close();
		}
		return root_return;
	}
	
	public JsonObject omsInventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{


		String			  search_value	  			 = search_string;
		Connection		  conGIV1	      			 = null;
		String            sql_giv1        			 = null;
		String            sql_alert       			 = null;
		PreparedStatement givState					 = null;
		PreparedStatement alertState        		 = null;
		JsonObject root_return 						 = null;

		try{
		conGIV1 = ReportActivator.getInstance().getConnection(Constants.GIV); //Modified connection for GIV R.1.1 Changes

		sql_giv1 = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_OMS_SQL);			

		givState = conGIV1.prepareStatement(sql_giv1);			
		givState.setString(1, search_value.trim());	


		ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms = SQLUtils.getPreparedSQLResult(givState,SQLConstants.ID_UTIL_INV_OMS_SQL, conGIV1);

		sql_alert = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_OMS_ALERT_SQL);			

		alertState = conGIV1.prepareStatement(sql_alert);
		alertState.setString(1, search_value.trim());
		alertState.setString(2, search_value.trim());

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms2 = SQLUtils.getPreparedSQLResult(alertState,SQLConstants.ID_UTIL_INV_OMS_ALERT_SQL, conGIV1);

		/*
		 * Begin Logic to create the end result set
		 * 
		 */
		root_return = new JsonObject();
		JsonObject oms_root    = new JsonObject();
		JsonObject giv_root    = new JsonObject();

		//OMS Section header row

		//return on page is ITEM_ID||SHORT_DESCRIPTION||TOTAL_SUPPLY||TOTAL_DEMAND||AVAILABLE_TO_ECOMM

		int tot_supply=0;
		int tot_demand=0;
		int row_supply=0;
		int row_demand=0;
		String short_desc="";
		String item_id="";
		int available_to_ecomm=0;
		//String avail_quant = "";
		//String sfty_factor = "";

		//iterate through returned result set and perform calculations for total supply/demand and available to Ecomm
		for (HashMap<String, Object> map : result_oms.values()) {
			item_id = String.valueOf(map.get("ITEM_ID"));
			short_desc = String.valueOf(map.get("SHORT_DESCRIPTION"));
			row_demand = Integer.valueOf(String.valueOf(map.get("DEMAND")));
			row_supply = Integer.valueOf(String.valueOf(map.get("SUPPLY")));
			tot_supply += row_supply;
			tot_demand += row_demand;

		}

		//retrieve available onhand value from query result
		//				for (HashMap<String, Object> map : result_oms2.values()) {
		//					avail_quant = String.valueOf(map.get("ONHAND_AVAILABLE_QUANTITY"));
		//				}
		/*				logger.debug(sql_sfty);
				//retrieve safety
				for (HashMap<String, Object> map : result_oms3.values()) {
					sfty_factor = String.valueOf(map.get("ONHAND_SAFETY_FACTOR_QTY"));
					if(sfty_factor.equals("null")){
						logger.debug("qty: "+ sfty_factor);
						sfty_factor="0";
					}
				}*/
		available_to_ecomm = tot_supply-tot_demand;

		//Add calculated results to a Hash Map to return as JSON

		JsonObject oms_summary = new JsonObject();

		oms_summary.addProperty("ITEM_ID", item_id);
		oms_summary.addProperty("SHORT_DESCRIPTION", short_desc);
		oms_summary.addProperty("TOTAL_SUPPLY", String.valueOf(tot_supply));
		oms_summary.addProperty("TOTAL_DEMAND", String.valueOf(tot_demand));
		oms_summary.addProperty("AVAILABLE_TO_ECOMM", String.valueOf(available_to_ecomm));
		//oms_summary.addProperty("ONHAND_AVAILABLE_QUANTITY", String.valueOf(avail_quant));

		//retrieve available onhand value from query result
		for (HashMap<String, Object> map : result_oms2.values()) {
			for(Entry<String, Object> row :map.entrySet()){
				oms_summary.addProperty(row.getKey(), String.valueOf(row.getValue()));
			}
		}

		JsonObject imgReturn = itemDetails(item_id);
		String img_path = String.valueOf(imgReturn.get("IMG_URL"));
		img_path = img_path.replace("\"", "");

		oms_summary.addProperty("IMG_URL",			img_path);

		oms_root.add("summary", oms_summary);

		//efc (oms) summary + details
		//SAMPLE OUTPUT
		//ITEM_ID	SHORT_DESCRIPTION	SHIPNODE_KEY	SUPPLY	DEMAND
		//87890076	BIG 1 BTH DOVE		EFC 2-809		11465	4
		//87890076	BIG 1 BTH DOVE		EFC 3-819		17280	56
		//87890076	BIG 1 BTH DOVE		EFC 1-873		11195	28
		//87890076	BIG 1 BTH DOVE		EFC 4-829		16390	13
		//87890076	BIG 1 BTH DOVE		RDC - 865		1363	0


		String row_shipnode_key 		= "";
		String oms_row_supply       	= "";
		String oms_row_demand       	= "";
		int row_available_to_fulfill 	= 0;
		int total_supply_efc			= 0;
		int total_supply_rdc            = 0;
		int total_supply_dsv            = 0;
		int total_supply_stores         = 0;
		int total_demand_efc			= 0;
		int total_demand_rdc            = 0;
		int total_demand_dsv            = 0;
		int total_demand_stores         = 0;
		int total_available_to_fulfill_efc  = 0;
		int total_available_to_fulfill_rdc  = 0;
		int total_available_to_fulfill_dsv  = 0;
		int total_available_to_fulfill_stores = 0;
		//EFC object
		JsonObject efc_summary          = new JsonObject(); //summary
		JsonObject efc_inventory        = new JsonObject();
		JsonArray  efc_inventory_array  = new JsonArray();

		JsonObject rdc_summary          = new JsonObject();
		JsonObject rdc_inventory        = new JsonObject();
		JsonArray  rdc_inventory_array  = new JsonArray();

		JsonObject dsv_summary          = new JsonObject();
		JsonObject dsv_inventory        = new JsonObject();
		JsonArray  dsv_inventory_array  = new JsonArray();

		JsonObject store_summary          = new JsonObject();
		JsonObject store_inventory        = new JsonObject();
		JsonArray  store_inventory_array  = new JsonArray();

		//OMS - EFC/RDC/DSV SECTION
		//for the result set of OMS data, iterate through each data row and add them to a HashMap<String,String>
		for(HashMap <String,Object> map : result_oms.values()){

			row_shipnode_key = String.valueOf(map.get("SHIPNODE_KEY"));
			oms_row_supply = String.valueOf(map.get("SUPPLY"));
			oms_row_demand = String.valueOf(map.get("DEMAND"));
			row_available_to_fulfill = Integer.parseInt(oms_row_supply) - Integer.parseInt(oms_row_demand);

			//if the shipnode_key isn't blank or null
			if((!row_shipnode_key.trim().equalsIgnoreCase("")) && (row_shipnode_key!=null)){

				if(row_shipnode_key.startsWith("E") || row_shipnode_key.startsWith("L")){

					//put in the values from the DB row
					efc_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					efc_inventory.addProperty("SUPPLY",oms_row_supply);
					efc_inventory.addProperty("DEMAND", oms_row_demand);
					efc_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

					//add to the running totals
					total_supply_efc += Integer.parseInt(oms_row_supply);
					total_demand_efc += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_efc += row_available_to_fulfill;

					efc_inventory_array.add(efc_inventory);

					efc_inventory = new JsonObject();

				} else if (row_shipnode_key.startsWith("R")){

					//put in the values from the DB row
					rdc_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					rdc_inventory.addProperty("SUPPLY",oms_row_supply);
					rdc_inventory.addProperty("DEMAND", oms_row_demand);
					rdc_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

					//add to the running totals
					total_supply_rdc += Integer.parseInt(oms_row_supply);
					total_demand_rdc += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_rdc += row_available_to_fulfill;

					rdc_inventory_array.add(rdc_inventory);

					rdc_inventory = new JsonObject();

				} else if (row_shipnode_key.length() > 4) {

					//put in the values from the DB row
					dsv_inventory.addProperty("SHIPNODE_KEY", row_shipnode_key);
					dsv_inventory.addProperty("SUPPLY",oms_row_supply);
					dsv_inventory.addProperty("DEMAND", oms_row_demand);
					dsv_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

					//add to the running totals
					total_supply_dsv += Integer.parseInt(oms_row_supply);
					total_demand_dsv += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_dsv += row_available_to_fulfill;

					dsv_inventory_array.add(dsv_inventory);

					dsv_inventory = new JsonObject();

				} else {

					//put in the values from the DB row for STORES
					store_inventory.addProperty("SHIPNODE_KEY", Integer.parseInt(row_shipnode_key));
					store_inventory.addProperty("SUPPLY",Integer.parseInt(oms_row_supply));
					store_inventory.addProperty("DEMAND", Integer.parseInt(oms_row_demand));
					store_inventory.addProperty("AVAILABLE_TO_FULFILL", row_available_to_fulfill);
					//store_inventory.addProperty("ONHAND_SAFETY_FACTOR_QTY", sfty_factor);

					//add to the running totals
					total_supply_stores += Integer.parseInt(oms_row_supply);
					total_demand_stores += Integer.parseInt(oms_row_demand);
					total_available_to_fulfill_stores += row_available_to_fulfill;

					store_inventory_array.add(store_inventory);

					store_inventory = new JsonObject();

				}

			}

		}

		//prepare the efc_summary object
		efc_summary.addProperty("SUPPLY", total_supply_efc);
		efc_summary.addProperty("DEMAND", total_demand_efc);
		efc_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_efc);
		//add the efc_summary and inventory array to the root object
		oms_root.add("efc_summary", efc_summary);
		oms_root.add("efc_inventory", efc_inventory_array);
		//prepare the rdc_summary object
		rdc_summary.addProperty("SUPPLY", total_supply_rdc);
		rdc_summary.addProperty("DEMAND", total_demand_rdc);
		rdc_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_rdc);
		//add the rdc_summary and inventory array to the root object
		oms_root.add("rdc_summary", rdc_summary);
		oms_root.add("rdc_inventory", rdc_inventory_array);
		//prepare the dsv_summary object
		dsv_summary.addProperty("SUPPLY", total_supply_dsv);
		dsv_summary.addProperty("DEMAND", total_demand_dsv);
		dsv_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_dsv);
		//add the dsv_summary and inventory array to the root object
		oms_root.add("dsv_summary", dsv_summary);
		oms_root.add("dsv_inventory", dsv_inventory_array);
		//prepare the store_summary object
		store_summary.addProperty("SUPPLY", total_supply_stores);
		store_summary.addProperty("DEMAND", total_demand_stores);
		store_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_stores);
		//add the dsv_summary and inventory array to the root object
		oms_root.add("store_summary", store_summary);
		oms_root.add("store_inventory", store_inventory_array);

		//add the GIV Summary + Details to the root object
		giv_root = givInventory(search_string);

		//add all the root objects to the "returned" root

		root_return.add("oms", oms_root);
		root_return.add("giv", giv_root);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(givState!=null){givState.close();}
			if(alertState!=null){alertState.close();}
			if(conGIV1!=null){conGIV1.close();}
		}
		return root_return;
	}


	public void inventory_audit(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value_from_date		= "";
		String			search_value_to_date		= "";
		String			search_value_item		    = "";
		String			search_value_ship_node		= "";
		String			search_value_pix_tran_no	= "";
		String			search_value_pix_tran_type  = "";
		Connection		con				= null;
		String			sql				= null;
		String			content 		= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page		= "/utility_includes/order_management/inventory_audit.jsp";
		SimpleDateFormat outformat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat informat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		ResultSet result = null;
		logger.log(Level.INFO, "OrderManagementServlet:inventory_auditResponse | " + requestedPage + "|" + request.getParameter("from_date") + "|" + request.getParameter("to_date")+ "|" + request.getParameter("item")+ "|" + request.getParameter("ship_node")+ "|" + request.getParameter("pix_tran_no")+ "|" + request.getParameter("pix_tran_type"));

		String exportResult		= request.getParameter("export_result_csv");
		String ajax_request		= request.getParameter("ajax_request");

		boolean isPost = StringUtils.equals(request.getMethod(),"POST");
		boolean	isGet = StringUtils.equals(request.getMethod(),"GET");
		boolean isExport = org.apache.commons.lang.StringUtils.equals(exportResult, "true");
		boolean isAjax	= org.apache.commons.lang.StringUtils.equals(ajax_request, "true");


		try{ 
			if(		isPost ||  (isGet && (isExport||isAjax))		){
				//if(StringUtils.equals(request.getMethod(),"POST")|| org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
				String exportResultData			= "ITEM,SHORT_DESCRIPTION,SHIP_NODE,MODIFYTS,QUANTITY,ON_HAND_HELD_QTY,ON_HAND_QTY,TRANSACTION_TYPE,REFERENCE_1,REFERENCE_2,REFERENCE_3,REFERENCE_4,REFERENCE_5,SUPPLY_TYPE,MODIFYPROGID\n";

				//if user requested a data export, export it otherwise don't trigger download
				boolean triggerDownload			= (org.apache.commons.lang.StringUtils.equals(exportResult, "true")) ? true : false;

				search_value_from_date	= request.getParameter("from_date");
				search_value_to_date	= request.getParameter("to_date");

				try{
					search_value_from_date = outformat.format(informat.parse(search_value_from_date));
				}catch(Exception e){ search_value_from_date = "Error parsing Date";}
				try{
					search_value_to_date = outformat.format(informat.parse(search_value_to_date));
				}catch(Exception e){ search_value_from_date = "Error parsing Date";}

				search_value_item	= request.getParameter("item").trim();
				search_value_ship_node	= request.getParameter("ship_node").trim();
				search_value_pix_tran_no	= request.getParameter("pix_tran_no").trim();
				search_value_pix_tran_type	= request.getParameter("pix_tran_type").trim();					 

				responseWriter	= response.getWriter();
				response.setContentType("application/json");
				/*					if(org.apache.commons.lang.StringUtils.equals(exportResult, "true")){
						logger.debug(requestedPage + "|X---X " +"-----------------------------------" );
						logger.debug(requestedPage + "|X---X \n\n" + getSessionAttribute("inventory_audit_csv") );
						logger.debug(requestedPage + "|X---X " +"-----------------------------------" );

						response.addHeader("Content-Type", "text/csv");
						response.addHeader("Content-Disposition", "attachment;filename=\"inventoryAudit_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
						response.setHeader("Content-Disposition", "attachment;filename=\"inventoryAudit_"+(new SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");

						content = (String) getSessionAttribute("inventory_audit_csv");
					}else{*/
				if (StringUtils.isNotBlank(search_value_from_date)) {
					search_value_from_date	= search_value_from_date.trim();
					search_value_to_date	= search_value_to_date.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.GIV); //Modified connection for GIV R.1.1 Changes

					//Modified Schema name in below sql for GIV R.1.1 Changes
					sql = "SELECT aud.item, item.SHORT_DESCRIPTION, aud.ship_node, aud.modifyts,aud.quantity, aud.on_hand_held_qty, aud.on_hand_qty,"
							+ " aud.transaction_type, aud.reference_1, aud.reference_2, aud.reference_3, aud.reference_4,"
							+ " aud.reference_5, aud.supply_type, aud.modifyprogid "
							+ " FROM gv_admin.yfs_inventory_audit aud"
							+ " JOIN gv_admin.yfs_item item ON item.item_id = aud.item   "
							+ " WHERE aud.inventory_audit_key>'"
							+ search_value_from_date
							+ "' and "
							+ "aud.inventory_audit_key<'"
							+ search_value_to_date
							+ "' "
							+ "AND item.uom                 ='EACH' "
							+ "AND item.organization_code   ='DEFAULT'";
					if (StringUtils.isNotBlank(search_value_ship_node)) {
						sql = sql
								+ "and aud.ship_node= '"
								+ search_value_ship_node
								+ "' ";
					}
					if (StringUtils.isNotBlank(search_value_pix_tran_no)) {
						sql = sql
								+ "and aud.reference_1= '"
								+ search_value_pix_tran_no
								+ "' ";
					}
					if (StringUtils.isNotBlank(search_value_pix_tran_type)) {
						sql = sql
								+ "and aud.transaction_type= '"
								+ search_value_pix_tran_type
								+ "' ";
					}
					sql = sql
							+ " and aud.item='"
							+ search_value_item
							+ "' order by aud.modifyts desc";



					result = con.createStatement().executeQuery(sql);

					JsonArray    AUDIT_ARRAY            = new JsonArray();
					JsonObject    AUDIT_LINE            = new JsonObject();

					String ITEM							= "";
					String SHORT_DESCRIPTION			= "";
					String SHIP_NODE					= "";
					String QUANTITY						= "";
					String MODIFYTS_STR					= "";

					Timestamp ORD_DATE					= null;
					String MODIFYTS						= "";

					String ON_HAND_HELD_QTY				= "";
					String ON_HAND_QTY					= "";
					String TRANSACTION_TYPE				= "";
					String REFERENCE_1					= "";
					String REFERENCE_2					= "";
					String REFERENCE_3					= "";
					String REFERENCE_4					= "";
					String REFERENCE_5					= "";

					String SUPPLY_TYPE					= "";
					String MODIFYPROGID					= "";

					int rowcount = 0;

					while(result.next()){
						ITEM						= result.getString("ITEM");
						SHORT_DESCRIPTION			= result.getString("SHORT_DESCRIPTION");
						SHIP_NODE					= result.getString("SHIP_NODE");
						QUANTITY					= result.getString("QUANTITY");
						MODIFYTS_STR				= result.getString("MODIFYTS");

						//--conversions to allow front-end date sorting provided a string-- 08/26/2014
						//convert string to timestamp.
						//convert timestampt to milliseconds.
						//convert milliseconds to string.
						ORD_DATE					= java.sql.Timestamp.valueOf(MODIFYTS_STR);

						MODIFYTS					= String.valueOf(ORD_DATE.getTime());

						ON_HAND_HELD_QTY			= result.getString("ON_HAND_HELD_QTY");
						ON_HAND_QTY					= result.getString("ON_HAND_QTY");
						TRANSACTION_TYPE			= result.getString("TRANSACTION_TYPE");
						REFERENCE_1					= result.getString("REFERENCE_1");
						REFERENCE_2					= result.getString("REFERENCE_2");
						REFERENCE_3					= result.getString("REFERENCE_3");
						REFERENCE_4					= result.getString("REFERENCE_4");
						REFERENCE_5					= result.getString("REFERENCE_5");

						SUPPLY_TYPE					= result.getString("SUPPLY_TYPE");
						MODIFYPROGID				= result.getString("MODIFYPROGID");

						//start export        
						/* if(exportResultDataHeaders.length()==0){
									//response.addHeader("Content-Type", "text/csv");
								    //response.addHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
								    //response.setHeader("Content-Disposition", "attachment;filename=\"transferOrders_"+ORDER_NO+".csv\"");
									//TODO write csv headers
									for(int i=0;i<result.getMetaData().getColumnCount();i++){
										logger.log(Level.DEBUG, "Column name:"+result.getMetaData().getColumnName(i+1));
										exportResultDataHeaders += result.getMetaData().getColumnName(i+1)+",";
										if((i+1)==result.getMetaData().getColumnCount()-1) exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length()-1);
									}
								}*/

						//LocalDate date = LocalDate.parse(MODIFYTS_STR, format).toString();

						exportResultData += ITEM		+ ",";
						exportResultData += SHORT_DESCRIPTION		+ ",";
						exportResultData += SHIP_NODE 			+ ",";
						exportResultData += MODIFYTS_STR + ",";
						exportResultData += QUANTITY		+ ",";

						exportResultData += " "	+ ",";
						exportResultData += ON_HAND_QTY	+ ",";
						exportResultData += TRANSACTION_TYPE 		+ ",";
						exportResultData += REFERENCE_1		+ ",";
						exportResultData += REFERENCE_2	+ ",";
						exportResultData += REFERENCE_3	+ ",";
						exportResultData += REFERENCE_4	+ ",";
						exportResultData += " "	+ ",";
						exportResultData += SUPPLY_TYPE		+ ",";
						exportResultData += MODIFYPROGID		+ ",";


						exportResultData += "\n";

						logger.debug("exportResultData==>"+exportResultData);
						//end export      

						logger.log(Level.INFO, "WarehouseTransferServlet:transfer_order_popup | Loading results | 3");


						if(rowcount <500 && triggerDownload==false){
							AUDIT_LINE.addProperty("ITEM",                    ITEM                );
							AUDIT_LINE.addProperty("SHORT_DESCRIPTION",        SHORT_DESCRIPTION    );
							AUDIT_LINE.addProperty("SHIP_NODE",                SHIP_NODE            );
							AUDIT_LINE.addProperty("MODIFYTS",                MODIFYTS            );
							AUDIT_LINE.addProperty("QUANTITY",                QUANTITY            );
							AUDIT_LINE.addProperty("ON_HAND_HELD_QTY",        ON_HAND_HELD_QTY    );
							AUDIT_LINE.addProperty("ON_HAND_QTY",            ON_HAND_QTY            );
							AUDIT_LINE.addProperty("TRANSACTION_TYPE",        TRANSACTION_TYPE    );
							AUDIT_LINE.addProperty("REFERENCE_1",            REFERENCE_1            );
							AUDIT_LINE.addProperty("REFERENCE_2",            REFERENCE_2            );
							AUDIT_LINE.addProperty("REFERENCE_3",            REFERENCE_3            );
							AUDIT_LINE.addProperty("REFERENCE_4",            REFERENCE_4            );
							AUDIT_LINE.addProperty("REFERENCE_5",            REFERENCE_5            );
							AUDIT_LINE.addProperty("SUPPLY_TYPE",            SUPPLY_TYPE            );
							AUDIT_LINE.addProperty("MODIFYPROGID",            MODIFYPROGID        );


							AUDIT_ARRAY.add(AUDIT_LINE);
							AUDIT_LINE        = new JsonObject();
						}else{
							triggerDownload= true;
							setHeaderDownloadableCSV("inventoryAudit", response);
							responseWriter.write(exportResultData);
							responseWriter.flush();
							exportResultData = "";
						}

						rowcount++;
					}

					//HashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

					Gson 		gson	= new GsonBuilder().create();

					if(triggerDownload==false){
						content = gson.toJson(AUDIT_ARRAY);
						responseWriter.write(content);
					}
					//setSessionAttribute("inventory_audit_csv",  exportResultDataHeaders+"\n"+exportResultData);
				}						
				//}

				//responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(result!=null) result.close();
			if(con!=null) con.close();
		}
	}


	public void shipment_details(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value_carton, search_value_order, search_value_shipment, search_value_tracking	= null;
		Connection		con					= null;
		String			sql					= null;
		String			response_content	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/order_management/shipment_details.jsp";
		ResultSet result = null;
		//logger.log(Level.INFO, "OrderManagementServlet:orderResponse | " + requestedPage + "|" + request.getParameter("orderNumber"));

		try{
			Gson 		gson				= new GsonBuilder().create();

			if( "POST".equals(request.getMethod()) ){
				search_value_carton	= request.getParameter("carton_no").trim();
				search_value_order	= request.getParameter("order_no").trim();
				search_value_shipment	= request.getParameter("shipment_no").trim();
				search_value_tracking	= request.getParameter("tracking_no").trim();

				responseWriter	= response.getWriter();
				response.setContentType("application/json");
				con = ReportActivator.getInstance().getConnection(Constants.OMS);

				//check to make sure they aren't all void

				if (!com.storeelf.util.StringUtils.isVoid(search_value_tracking)
						|| !com.storeelf.util.StringUtils.isVoid(search_value_carton)
						|| !com.storeelf.util.StringUtils.isVoid(search_value_order)
						|| !com.storeelf.util.StringUtils.isVoid(search_value_shipment))	{

					//There are 7 steps to creating this query, they are labeled
					//first create the top STATIC portion of the sql (1/7)
					sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SHIPMENT_LINE_DETAILS_SQL);

					//add this table if tracking_no or container_scm is present in the search criteria
					//otherwise upper portion will fail if no container exists (2/7)
					if (!com.storeelf.util.StringUtils.isVoid(search_value_tracking) || !com.storeelf.util.StringUtils.isVoid(search_value_carton)) {
						sql += " ,STERLING.YFS_SHIPMENT_CONTAINER sc";
					}

					//add the next STATIC portion of the query
					sql += SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SHIPMENT_LINE_DETAILS_SQL2);

					//add this join if tracking_no or container_scm is present in the search criteria
					//otherwise upper portion will fail if no container exists (3/7)
					if (!com.storeelf.util.StringUtils.isVoid(search_value_tracking) || !com.storeelf.util.StringUtils.isVoid(search_value_carton)) {
						sql+= " and sc.SHIPMENT_KEY = s.SHIPMENT_KEY";
					}

					//add the dynamic portion of the top sub-query based on user search criteria (4/7)
					if (!com.storeelf.util.StringUtils.isVoid(search_value_order)){
						if(search_value_order.length() == 24){
							sql+= " AND oh.order_header_key = '"+search_value_order+"'";
						} else{
							sql+= " AND oh.ORDER_NO like '"+search_value_order+"%'";
						}
					}

					if (!com.storeelf.util.StringUtils.isVoid(search_value_shipment)){
						if(search_value_shipment.length() == 24){
							sql+= " and s.shipment_key ='"+search_value_shipment+"'";
						} else{
							sql+= " and s.shipment_no ='"+search_value_shipment+"'";
						}
					}

					if (!com.storeelf.util.StringUtils.isVoid(search_value_tracking)){
						sql+= " and sc.TRACKING_NO = '"+search_value_tracking+"'";
					}

					if(!com.storeelf.util.StringUtils.isVoid(search_value_carton)){
						if(search_value_carton.length() == 24){
							sql+= " and sc.shipment_container_key = '"+search_value_carton+"'";
						} else {
							sql+= " and sc.CONTAINER_SCM = '"+search_value_carton+"'";
						}
					}

					//add the next static portion of the query (5/7)
					sql += SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SHIPMENT_LINE_DETAILS_SQL3);

					//add the dynamic portion of the bottom sub-query based on user search criteria (6/7)

					if (!com.storeelf.util.StringUtils.isVoid(search_value_order)){
						if(search_value_order.length() == 24){
							sql+= " AND oh.order_header_key = '"+search_value_order+"'";
						} else{
							sql+= " AND oh.ORDER_NO like '"+search_value_order+"%'";
						}							
					}

					if (!com.storeelf.util.StringUtils.isVoid(search_value_shipment)){
						if(search_value_shipment.length() == 24){
							sql+= " and s.shipment_key ='"+search_value_shipment+"'";
						} else{
							sql+= " and s.shipment_no ='"+search_value_shipment+"'";
						}
					}

					if (!com.storeelf.util.StringUtils.isVoid(search_value_tracking)){
						sql+= " and sc.TRACKING_NO = '"+search_value_tracking+"'";
					}

					if(!com.storeelf.util.StringUtils.isVoid(search_value_carton)){
						if(search_value_carton.length() == 24){
							sql+= " and sc.shipment_container_key = '"+search_value_carton+"'";
						} else {
							sql+= " and sc.CONTAINER_SCM = '"+search_value_carton+"'";
						}
					}

					//add the last STATIC portion of the query (7/7)
					sql += SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SHIPMENT_LINE_DETAILS_SQL4);

					logger.debug(sql);
					result = con.createStatement().executeQuery(sql);

					JsonObject	root				= new JsonObject();
					JsonArray	SHIPMENT_LINE_ARRAY	= new JsonArray();
					JsonObject	ORDER_LINE			= new JsonObject();
					JsonObject	SHIPMENT_LINE		= new JsonObject();

					int record_index			= 0;

					/* New code 05/09/2014 starts for the tracking link change */
					//HashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);

					//####################################################

					while(result.next()){

						String SHIPMENT_NO					= result.getString("SHIPMENT_NO");
						String ORDER_NO                     = result.getString("ORDER_NO");
						String ACTUAL_SHIPMENT_DATE         = result.getString("ACTUAL_SHIPMENT_DATE");
						String EXPECTED_SHIPMENT_DATE       = result.getString("EXPECTED_SHIPMENT_DATE");
						String PICKTICKET_NO                = result.getString("PICKTICKET_NO");
						String CARRIER_SERVICE_CODE         = result.getString("CARRIER_SERVICE_CODE");
						String SCAC	                       	= result.getString("SCAC");
						String SHIPNODE_KEY                 = result.getString("SHIPNODE_KEY");
						String STATUS                       = result.getString("STATUS");
						String USERNAME                     = result.getString("USERNAME");

						String CONTAINER_SCM                = result.getString("CONTAINER_SCM");
						String TRACKING_LINK                = "";
						String TRACKING_NO                  = result.getString("TRACKING_NO");
						String RELEASE_NO                   = result.getString("RELEASE_NO");
						String PRIME_LINE_NO                = result.getString("PRIME_LINE_NO");
						String ITEM_ID                      = result.getString("ITEM_ID");
						String ITEM_DESCRIPTION				= result.getString("ITEM_DESCRIPTION");
						String QUANTITY                     = result.getString("QUANTITY");
						String CONTAINER_GROSS_WEIGHT       = result.getString("CONTAINER_GROSS_WEIGHT");


						//TRACKING_LINK = Constants.REPORT_MAP_Tracking.get(CARRIER_SERVICE_CODE) + TRACKING_NO;

						if(CARRIER_SERVICE_CODE.equals("ABF_C")||CARRIER_SERVICE_CODE.equals("ABF_W"))
						{
							TRACKING_LINK = Constants.ABFS_CARRIER;
						}
						else if(CARRIER_SERVICE_CODE.equals("UPS_R"))
						{
							TRACKING_LINK = Constants.UPS_R;
						}
						else if(Constants.REPORT_MAP_Tracking.containsKey(CARRIER_SERVICE_CODE)) 
						{
							TRACKING_LINK = Constants.REPORT_MAP_Tracking.get(CARRIER_SERVICE_CODE) + TRACKING_NO;
						}
						else
						{
							TRACKING_LINK = Constants.TRACKING_ERROR_PAGE;
						}


						ORDER_LINE.addProperty("CONTAINER_SCM",			CONTAINER_SCM		);
						ORDER_LINE.addProperty("TRACKING_LINK",			TRACKING_LINK		);
						ORDER_LINE.addProperty("TRACKING_NO",			TRACKING_NO			);
						ORDER_LINE.addProperty("RELEASE_NO",			RELEASE_NO			);
						ORDER_LINE.addProperty("PRIME_LINE_NO",			PRIME_LINE_NO		);
						ORDER_LINE.addProperty("ITEM_ID",				ITEM_ID				);
						ORDER_LINE.addProperty("ITEM_DESCRIPTION",		ITEM_DESCRIPTION	);
						ORDER_LINE.addProperty("QUANTITY",				QUANTITY			);
						ORDER_LINE.addProperty("CONTAINER_GROSS_WEIGHT",CONTAINER_GROSS_WEIGHT);

						//ORDER_LINE = new JsonObject();

						//add array if non-existing
						if(record_index==0 || ( (SHIPMENT_LINE.has("SHIPMENT_NO")) && !org.apache.commons.lang.StringUtils.equals(SHIPMENT_LINE.get("SHIPMENT_NO").getAsString(), SHIPMENT_NO))){
							SHIPMENT_LINE		= new JsonObject();

							SHIPMENT_LINE.add("ORDER_LINE_ARRAY", 			new JsonArray());
							SHIPMENT_LINE.addProperty("CONTAINER_SCM",			CONTAINER_SCM		);

							//logger.debug("Switching tracking link from:"+ TRACKING_LINK + " - to:"+Constants.REPORT_MAP_Tracking.get(CARRIER_SERVICE_CODE) + TRACKING_NO);


							//ORDER_LINE.addProperty("TRACKING_LINK",			TRACKING_LINK		);

							SHIPMENT_LINE.addProperty("SHIPMENT_NO",			SHIPMENT_NO);
							SHIPMENT_LINE.addProperty("ORDER_NO",				ORDER_NO);
							SHIPMENT_LINE.addProperty("ACTUAL_SHIPMENT_DATE",	ACTUAL_SHIPMENT_DATE);
							SHIPMENT_LINE.addProperty("EXPECTED_SHIPMENT_DATE", EXPECTED_SHIPMENT_DATE);
							SHIPMENT_LINE.addProperty("PICKTICKET_NO",			PICKTICKET_NO);
							SHIPMENT_LINE.addProperty("CARRIER_SERVICE_CODE",	CARRIER_SERVICE_CODE);
							SHIPMENT_LINE.addProperty("SCAC",					SCAC);
							SHIPMENT_LINE.addProperty("SHIPNODE_KEY",			SHIPNODE_KEY);
							SHIPMENT_LINE.addProperty("STATUS",					STATUS);
							SHIPMENT_LINE.addProperty("USERNAME",               USERNAME);

							//add shipment line to this order line
							SHIPMENT_LINE.getAsJsonArray("ORDER_LINE_ARRAY").add(ORDER_LINE);

							//create array for this order line add order line object to array
							SHIPMENT_LINE_ARRAY.add(SHIPMENT_LINE);

						}else{
							//add shipment line to this order line
							SHIPMENT_LINE.getAsJsonArray("ORDER_LINE_ARRAY").add(ORDER_LINE);
						}

						logger.log(Level.INFO, "WarehouseTransferServlet:transfer_order_popup | Loading results | 4");

						ORDER_LINE		= new JsonObject();
						//SHIPMENT_LINE	= new JsonObject();

						record_index++;
					}


					logger.log(Level.INFO, "WarehouseTransferServlet:transfer_order_popup | "+record_index+" Results loaded");

					//add line to the table
					//table.add(line);
					root.add("shipment_details", SHIPMENT_LINE_ARRAY);
					response_content	= gson.toJson(root);
				}
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
			
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(result!=null) result.close();
			if(con!=null) con.close();
		}
	}


	public void customer_email(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value		= null;
		Connection		con			= null;
		String			sql			= null;
		String			content 	= "-error-";
		PrintWriter		responseWriter		= null;
		String			jsp_include_page	= "/utility_includes/order_management/customer_email.jsp";
		Gson 			gson	         		= new GsonBuilder().create();

		JsonObject		rootReturn  			= new JsonObject();
		JsonObject		order_mail_object 		= new JsonObject();
		JsonArray		order_mail_object_array		= new JsonArray();
		PreparedStatement	omsState		= null;

		List<String> notes = new ArrayList<String>();
		List<String> async = new ArrayList<String>();
		Map<String,String> mailtypes = new HashMap<String,String>();
		Map<String,String> mailtimes = new HashMap<String,String>();
		Map<String,String> output = new HashMap<String,String>();

		/**Distinct MailTypes Hardcoded, where key : yfs_notes value and  Value : yfs_async_req value**/
		mailtypes.put("AutoRefund","StoreElfRefundNotificationRetrigger");
		mailtypes.put("OrderDelay","StoreElfCallOrdrDelayForBOPUSAsyncService");
		mailtypes.put("CustomerPickedUp","StoreElfPickedUpNotificationRetrigger");
		mailtypes.put("FinalPickUpReminder","StoreElfFinalPickUpReminderRetrigger");
		mailtypes.put("OrderDelivery","StoreElfDeliveredNotificationRetrigger");
		mailtypes.put("Packslip","StoreElfCallEmailPackslipAsyncReqService");
		mailtypes.put("InitialPickUpReminder","StoreElfPickpUpReminderRetriggerService");
		mailtypes.put("ReadyForCustomerPickup","StoreElfReadyForCustomerPickUpRetrigger");
		mailtypes.put("PartialShipment"," ");
		mailtypes.put("OrderModification"," ");
		mailtypes.put("OrderCancellation"," ");
		mailtypes.put("CompleteShipment"," ");
		mailtypes.put("PaymentAndKCUpdate","StoreElfPaymentAndKCUpdateRetry");
		mailtypes.put("StoreElfCashEarnedActivation","StoreElfCashEarnedActivationSyncService");

		/**Taking the Maps Keys and Values in List**/
		List<String> Keys = new ArrayList<String>(mailtypes.keySet());
		List<String> Values = new ArrayList<String>(mailtypes.values());

		logger.log(Level.INFO, "OrderManagementServlet:invoiceResponse | " + requestedPage + "|" + request.getParameter("orderNumber") + "["+search_value+"]" );

		try{
			if( "POST".equals(request.getMethod()) ){
				search_value	= request.getParameter("orderNumber");

				responseWriter	= response.getWriter();
				response.setContentType("application/json");
				//con				= ReportActivator.getInstance().getConnection(Constants.OMS);
				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					search_value	= search_value.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.OMS);

					sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_CUSTOMER_EMAIL_SQL);
					omsState = con.prepareStatement(sql);
					omsState.setString(1, search_value);

					ConcurrentHashMap<Integer, HashMap<String, Object>> mailnotes = SQLUtils.getPreparedSQLResult(omsState, SQLConstants.ID_UTIL_CUSTOMER_EMAIL_SQL, con);

					for (HashMap<String, Object> map : mailnotes.values()) 
					{
						notes.add(String.valueOf(map.get("MAIL")).trim());
						mailtimes.put(String.valueOf(map.get("MAIL")).trim()+"Email", String.valueOf(map.get("TIME")).trim());
					}


					sql = "\n select /*+ parallel(8) full(r)  */  r.service_name as MAIL, 'NA' as TIME from sterling.yfs_async_req r where TO_CHAR(substr(r.message,instr(r.message,'urn:orderNumber>')+16,10))='"+ search_value +"' "
							+ "\n  ";		
					//System.out.println("test:"+sql);
					ConcurrentHashMap<Integer, HashMap<String, Object>> mailasync = SQLUtils.getSQLResult(sql, con);
					for (HashMap<String, Object> map : mailasync.values()) 
					{
						async.add(String.valueOf(map.get("MAIL")).trim());
					}

					for (int i=0;i<mailtypes.size();i++)
					{				 
						if(notes.contains(Keys.get(i)))
						{
							output.put(Keys.get(i)+"Email","Success");
						}
						else if( async.contains(Values.get(i)))
						{
							output.put(Keys.get(i)+"Email","Failure");
						}
						else
						{
							output.put(Keys.get(i)+"Email","NA");
						}
					}

					Set<String> Mails = output.keySet();
					Object[] mail=Mails.toArray();

					for(int i=0;i<mail.length;i++)
					{
						order_mail_object.addProperty("ORDERNO", search_value);
						order_mail_object.addProperty("MAIL",mail[i].toString());
						order_mail_object.addProperty("STATUS",output.get(mail[i]));
						String mailname=mail[i].toString();
						String time=mailtimes.get(mailname);
						System.out.println("Time test :: " + time);
						String TIME_prop = (time==null)?"NA":time;
						order_mail_object.addProperty("TIME",TIME_prop);
						/*if(time.length()<=0)
								 {
									System.out.println("Inside if");
									 order_mail_object.addProperty("TIME","NA");
								 }
								 else
								 {
									 System.out.println("Inside else");
								 order_mail_object.addProperty("TIME",time);
								 }*/


						order_mail_object_array.add(order_mail_object);
						order_mail_object = new JsonObject();
					}
					rootReturn.add("mail_notes", order_mail_object_array);
					//System.out.println("Anikets: "+async);
					content = gson.toJson(rootReturn);
					System.out.println(rootReturn);
				}
				responseWriter.write(content);
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
		finally {
			if(omsState!=null){omsState.close();}
			if(con!=null) con.close();
		}
	}

	public void pickticket(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		String			search_value	= null;
		Connection		con				= null;
		String			sql				= null;
		String			content 		= "-error-";
		PrintWriter		responseWriter	= null;
		String			jsp_include_page		= "/utility_includes/order_management/pickticket.jsp";

		//logger.log(Level.INFO, "OrderManagementServlet:orderResponse | " + requestedPage + "|" + request.getParameter("orderNumber"));

		try{
			if( "POST".equals(request.getMethod()) ){
				search_value	= request.getParameter("pickticket_no");

				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
					search_value	= search_value.trim();
					con				= ReportActivator.getInstance().getConnection(Constants.EFC2);
					//For EFC 1/2/3/4 2012 upgrade starts
					sql =  		" select p.tc_order_id as PKT_CTRL_NBR,p.ext_purchase_order as CUST_PO_NBR,p.o_facility_alias_id as WHSE,p.order_type as ORD_TYPE,to_number(NVL(p.tax_id, 0)) as TOTAL_NBR_OF_UNITS,p.d_state_prov as SHIPTO_STATE,p.order_date_dttm as ORD_DATE, "
							+ " TO_DATE(TO_CHAR(p.created_dttm,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS') as CREATE_DATE_TIME,p.lane_name as RTE_GUIDE_NBR,CASE p.DO_STATUS "
							+ "	WHEN 110 "
							+ " THEN 'Unselected' "
							+ " WHEN 115 "
							+ " THEN 'Preview Wave Selected' "
							+ " WHEN 120 "
							+ " THEN 'Printed' "
							+ " WHEN 140 "
							+ " THEN 'In Packing' "
							+ " WHEN 150 "
							+ " THEN 'Packed' "
							+ " WHEN 160 "
							+ " THEN 'Weighed' "
							+ " WHEN 170 "
							+ " THEN 'Manifested' "
							+ " WHEN 190 "
							+ " THEN 'Shipped' "
							+ " WHEN 200 "
							+ " THEN 'Cancelled' "
							+ " ELSE 'Other' "
							+ " END STAT_CODE  "
							+ " FROM WMOS_EFC.ORDERS@EFC1 p "
							+ " WHERE p.tc_order_id = '" + search_value + "' "
							+ " UNION"
							+ " select p.tc_order_id as PKT_CTRL_NBR,p.ext_purchase_order as CUST_PO_NBR,p.o_facility_alias_id,p.order_type,to_number(NVL(p.tax_id, 0)),p.d_state_prov,p.order_date_dttm, "
							+ " TO_DATE(TO_CHAR(p.created_dttm,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS'),p.lane_name,CASE p.DO_STATUS "
							+ "	WHEN 110 "
							+ " THEN 'Unselected' "
							+ " WHEN 115 "
							+ " THEN 'Preview Wave Selected' "
							+ " WHEN 120 "
							+ " THEN 'Printed' "
							+ " WHEN 140 "
							+ " THEN 'In Packing' "
							+ " WHEN 150 "
							+ " THEN 'Packed' "
							+ " WHEN 160 "
							+ " THEN 'Weighed' "
							+ " WHEN 170 "
							+ " THEN 'Manifested' "
							+ " WHEN 190 "
							+ " THEN 'Shipped' "
							+ " WHEN 200 "
							+ " THEN 'Cancelled' "
							+ " ELSE 'Other' "
							+ " END STAT_CODE  "
							+ " FROM WMOS_EFC.ORDERS p "
							+ " WHERE p.tc_order_id = '" + search_value + "' "
							+ " UNION"
							+ " select p.tc_order_id,p.ext_purchase_order,p.o_facility_alias_id,p.order_type,to_number(NVL(p.tax_id, 0)),p.d_state_prov,p.order_date_dttm, "
							+ " TO_DATE(TO_CHAR(p.created_dttm,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS'),p.lane_name,CASE p.DO_STATUS "
							+ "	WHEN 110 "
							+ " THEN 'Unselected' "
							+ " WHEN 115 "
							+ " THEN 'Preview Wave Selected' "
							+ " WHEN 120 "
							+ " THEN 'Printed' "
							+ " WHEN 140 "
							+ " THEN 'In Packing' "
							+ " WHEN 150 "
							+ " THEN 'Packed' "
							+ " WHEN 160 "
							+ " THEN 'Weighed' "
							+ " WHEN 170 "
							+ " THEN 'Manifested' "
							+ " WHEN 190 "
							+ " THEN 'Shipped' "
							+ " WHEN 200 "
							+ " THEN 'Cancelled' "
							+ " ELSE 'Other' "
							+ " END STAT_CODE  "
							+ " FROM WMOS_EFC.ORDERS@EFC3 p "
							+ " WHERE p.tc_order_id = '" + search_value + "' "
							+ " UNION"
							+ " select p.tc_order_id,p.ext_purchase_order,p.o_facility_alias_id,p.order_type,to_number(NVL(p.tax_id, 0)),p.d_state_prov,p.order_date_dttm, "
							+ " TO_DATE(TO_CHAR(p.created_dttm,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS'),p.lane_name,CASE p.DO_STATUS "
							+ "	WHEN 110 "
							+ " THEN 'Unselected' "
							+ " WHEN 115 "
							+ " THEN 'Preview Wave Selected' "
							+ " WHEN 120 "
							+ " THEN 'Printed' "
							+ " WHEN 140 "
							+ " THEN 'In Packing' "
							+ " WHEN 150 "
							+ " THEN 'Packed' "
							+ " WHEN 160 "
							+ " THEN 'Weighed' "
							+ " WHEN 170 "
							+ " THEN 'Manifested' "
							+ " WHEN 190 "
							+ " THEN 'Shipped' "
							+ " WHEN 200 "
							+ " THEN 'Cancelled' "
							+ " ELSE 'Other' "
							+ " END STAT_CODE  "
							+ " FROM WMOS_EFC.ORDERS@EFC4 p "
							+ " WHERE p.tc_order_id = '" + search_value + "' ";	
					//For EFC 1/2/3/4 2012 upgrade ends
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);
					Gson 		gson	= new GsonBuilder().create();
					content = gson.toJson(result);
				}
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
		finally {
			if(con!=null) con.close();
		}
	}





	public JsonObject givInventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conGIV			= null;
		String          sql_giv         = null;
		PreparedStatement givState = null;
		JsonObject giv_root             = null;
		try{
		conGIV = ReportActivator.getInstance().getConnection(Constants.GIV);
		sql_giv = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_GIV_SQL);

		givState = conGIV.prepareStatement(sql_giv);
		givState.setString(1, search_value);

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_giv = SQLUtils.getPreparedSQLResult(givState, SQLConstants.ID_UTIL_INV_GIV_SQL, conGIV);

		//******************************************************************************************************
		//GIV DISPLAY LOGIC
		//******************************************************************************************************
		//GIV Section Calculations

		String row_shipnode_key 		= "";
		String row_supply       		= "";
		String row_demand       		= "";
		int row_available_to_fulfill 	= 0;
		int total_supply_giv  				= 0;
		int total_demand_giv  				= 0;
		int total_available_to_fulfill_giv  = 0;
		giv_root             = new JsonObject();
		JsonObject giv_summary          = new JsonObject();
		JsonObject giv_inventory        = new JsonObject();
		JsonArray  giv_inventory_array  = new JsonArray();

		//GIV - STORES SECTION
		//for the result set of GIV data, iterate through each data row and add them to a HashMap<String,String>
		for(HashMap <String,Object> map : result_giv.values()){
			row_shipnode_key = String.valueOf(map.get("SHIPNODE_KEY"));
			row_supply = String.valueOf(map.get("SUPPLY"));
			row_demand = String.valueOf(map.get("DEMAND"));
			row_available_to_fulfill = Integer.parseInt(row_supply) - Integer.parseInt(row_demand);

			//if the shipnode_key isn't blank or null
			if((!row_shipnode_key.trim().equalsIgnoreCase("")) && (row_shipnode_key!=null)){

				//put in the values from the DB row
				giv_inventory.addProperty("SHIPNODE_KEY", Integer.parseInt(row_shipnode_key));
				giv_inventory.addProperty("SUPPLY",row_supply);
				giv_inventory.addProperty("DEMAND", row_demand);
				giv_inventory.addProperty("AVAILABLE_TO_FULFILL", String.valueOf(row_available_to_fulfill));

				//add to the running totals
				total_supply_giv += Integer.parseInt(row_supply);
				total_demand_giv += Integer.parseInt(row_demand);
				total_available_to_fulfill_giv += row_available_to_fulfill;

				giv_inventory_array.add(giv_inventory);

				giv_inventory = new JsonObject();

			}

		}

		//prepare the egivsummary object
		giv_summary.addProperty("SUPPLY", total_supply_giv);
		giv_summary.addProperty("DEMAND", total_demand_giv);
		giv_summary.addProperty("AVAILABLE_TO_FULFILL", total_available_to_fulfill_giv);
		//add the efc_summary and inventory array to the root object
		giv_root.add("giv_summary", giv_summary);
		giv_root.add("giv_inventory", giv_inventory_array);


		//******************************************************************************************************
		//END GIV DISPLAY LOGIC
		//******************************************************************************************************

		
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(givState!=null){givState.close();}
			if(conGIV!=null) conGIV.close();
		}
		return giv_root;
	}


	public String efcInventory(String search_string){
		String                                       to_return             = "";
		HashMap<Integer, HashMap<String, String>>    return_hash           = new HashMap<Integer, HashMap<String, String>>();


		try {
			//add the rows from each EFC to the hash to finally return
			return_hash.putAll(efc1Inventory(search_string));
			return_hash.putAll(efc2Inventory(search_string));
			return_hash.putAll(efc3Inventory(search_string));
			return_hash.putAll(efc4Inventory(search_string));
		}

		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}

		Gson	gson	= new GsonBuilder().create();
		to_return = gson.toJson(return_hash);

		return to_return;
	}


	public HashMap<Integer, HashMap<String, String>> efc1Inventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conEFC1			= null;
		String          sql_efc1        = null;
		String			sql1_efc		= null;
		String			sql2_efc		= null;
		HashMap<Integer, HashMap<String, String>> wrap_efc1 = null;
		try{
		conEFC1 = ReportActivator.getInstance().getConnection(Constants.EFC1);

		sql1_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL);
		sql2_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL2);

		sql_efc1 =  sql1_efc.trim() +search_value.trim() +sql2_efc.trim();


		ConcurrentHashMap<Integer, HashMap<String, Object>> result_efc1 = SQLUtils.getSQLResult(sql_efc1, conEFC1);
		HashMap<String, String> final_result_efc1 = new HashMap<String, String>();

		for (HashMap<String, Object> map : result_efc1.values()) {
			final_result_efc1.put("SHIP_NODE","WMOS EFC 1-873");
			final_result_efc1.put("ITEM_TYPE",String.valueOf(map.get("ITEM_TYPE")));
			final_result_efc1.put("ACTIVE",String.valueOf(map.get("ACTIVE")));
			final_result_efc1.put("CASE",String.valueOf(map.get("CASE")));
			final_result_efc1.put("TRANS",String.valueOf(map.get("TRANS")));
			final_result_efc1.put("CARTON",String.valueOf(map.get("CARTON")));
			final_result_efc1.put("UNALLOC_CASE",String.valueOf(map.get("UNALLOC_CASE")));
			final_result_efc1.put("UNALLOC_TRANS",String.valueOf(map.get("UNALLOC_TRANS")));
			final_result_efc1.put("ALLOC_TOTAL",String.valueOf(map.get("ALLOC_TOTAL")));
			final_result_efc1.put("UNALLOC_TOTAL",String.valueOf(map.get("UNALLOC_TOTAL")));
		}

		wrap_efc1 = new HashMap<Integer, HashMap<String, String>>();
		wrap_efc1.put(1, final_result_efc1);


		//Gson	gson	= new GsonBuilder().create();
		//to_return = gson.toJson(final_result_efc1);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(conEFC1!=null) conEFC1.close();
		}
		return wrap_efc1;
	}


	public HashMap<Integer, HashMap<String, String>> efc2Inventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conEFC2			= null;
		String          sql_efc2        = null;
		String			sql1_efc		= null;
		String			sql2_efc		= null;
		HashMap<Integer, HashMap<String, String>> wrap_efc2 = null;
		//String          to_return       = null;
		try{
		conEFC2 = ReportActivator.getInstance().getConnection(Constants.EFC2);
		// For EFC 2 2012 upgrade starts

		sql1_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL);
		sql2_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL2); 

		sql_efc2 = 	sql1_efc.trim() +search_value.trim() +sql2_efc.trim();
		// For EFC 2 2012 upgrade ends			
		ConcurrentHashMap<Integer, HashMap<String, Object>> result_efc2 = SQLUtils.getSQLResult(sql_efc2, conEFC2);
		HashMap<String, String> final_result_efc2 = new HashMap<String, String>();

		for (HashMap<String, Object> map : result_efc2.values()) {
			final_result_efc2.put("SHIP_NODE","WMOS EFC 2-809");
			final_result_efc2.put("ITEM_TYPE",String.valueOf(map.get("ITEM_TYPE")));
			final_result_efc2.put("ACTIVE",String.valueOf(map.get("ACTIVE")));
			final_result_efc2.put("CASE",String.valueOf(map.get("CASE")));
			final_result_efc2.put("TRANS",String.valueOf(map.get("TRANS")));
			final_result_efc2.put("CARTON",String.valueOf(map.get("CARTON")));
			final_result_efc2.put("UNALLOC_CASE",String.valueOf(map.get("UNALLOC_CASE")));
			final_result_efc2.put("UNALLOC_TRANS",String.valueOf(map.get("UNALLOC_TRANS")));
			final_result_efc2.put("ALLOC_TOTAL",String.valueOf(map.get("ALLOC_TOTAL")));
			final_result_efc2.put("UNALLOC_TOTAL",String.valueOf(map.get("UNALLOC_TOTAL")));
		}

		wrap_efc2 = new HashMap<Integer, HashMap<String, String>>();
		wrap_efc2.put(2, final_result_efc2);


		//Gson	gson	= new GsonBuilder().create();
		//to_return = gson.toJson(final_result_efc1);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(conEFC2!=null) conEFC2.close();
		}
		return wrap_efc2;
	}


	public HashMap<Integer, HashMap<String, String>> efc3Inventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conEFC3			= null;
		String          sql_efc3        = null;
		String			sql1_efc		= null;
		String			sql2_efc		= null;
		HashMap<Integer, HashMap<String, String>> wrap_efc3 = null;
		//String          to_return       = null;
		try{
		conEFC3 = ReportActivator.getInstance().getConnection(Constants.EFC3);

		sql1_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL);
		sql2_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL2);

		sql_efc3 = sql1_efc.trim() +search_value.trim() +sql2_efc.trim();

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_efc3 = SQLUtils.getSQLResult(sql_efc3, conEFC3);
		HashMap<String, String> final_result_efc3 = new HashMap<String, String>();

		for (HashMap<String, Object> map : result_efc3.values()) {
			final_result_efc3.put("SHIP_NODE","WMOS EFC 3-819");
			final_result_efc3.put("ITEM_TYPE",String.valueOf(map.get("ITEM_TYPE")));
			final_result_efc3.put("ACTIVE",String.valueOf(map.get("ACTIVE")));
			final_result_efc3.put("CASE",String.valueOf(map.get("CASE")));
			final_result_efc3.put("TRANS",String.valueOf(map.get("TRANS")));
			final_result_efc3.put("CARTON",String.valueOf(map.get("CARTON")));
			final_result_efc3.put("UNALLOC_CASE",String.valueOf(map.get("UNALLOC_CASE")));
			final_result_efc3.put("UNALLOC_TRANS",String.valueOf(map.get("UNALLOC_TRANS")));
			final_result_efc3.put("ALLOC_TOTAL",String.valueOf(map.get("ALLOC_TOTAL")));
			final_result_efc3.put("UNALLOC_TOTAL",String.valueOf(map.get("UNALLOC_TOTAL")));
		}


		wrap_efc3 = new HashMap<Integer, HashMap<String, String>>();
		wrap_efc3.put(3, final_result_efc3);


		//Gson	gson	= new GsonBuilder().create();
		//to_return = gson.toJson(final_result_efc1);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(conEFC3!=null) conEFC3.close();
		}

		return wrap_efc3;
	}


	public HashMap<Integer, HashMap<String, String>> efc4Inventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conEFC4			= null;
		String          sql_efc4        = null;
		String			sql1_efc		= null;
		String			sql2_efc		= null;
		//String          to_return       = null;
		HashMap<Integer, HashMap<String, String>> wrap_efc4 = null;
		try{
		conEFC4 = ReportActivator.getInstance().getConnection(Constants.EFC4);

		sql1_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL);
		sql2_efc = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_INV_EFC_SQL2);

		sql_efc4 = sql1_efc.trim() +search_value.trim() +sql2_efc.trim();

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_efc4 = SQLUtils.getSQLResult(sql_efc4, conEFC4);
		HashMap<String, String> final_result_efc4 = new HashMap<String, String>();

		for (HashMap<String, Object> map : result_efc4.values()) {
			final_result_efc4.put("SHIP_NODE","WMOS EFC 4-829");
			final_result_efc4.put("ITEM_TYPE",String.valueOf(map.get("ITEM_TYPE")));
			final_result_efc4.put("ACTIVE",String.valueOf(map.get("ACTIVE")));
			final_result_efc4.put("CASE",String.valueOf(map.get("CASE")));
			final_result_efc4.put("TRANS",String.valueOf(map.get("TRANS")));
			final_result_efc4.put("CARTON",String.valueOf(map.get("CARTON")));
			final_result_efc4.put("UNALLOC_CASE",String.valueOf(map.get("UNALLOC_CASE")));
			final_result_efc4.put("UNALLOC_TRANS",String.valueOf(map.get("UNALLOC_TRANS")));
			final_result_efc4.put("ALLOC_TOTAL",String.valueOf(map.get("ALLOC_TOTAL")));
			final_result_efc4.put("UNALLOC_TOTAL",String.valueOf(map.get("UNALLOC_TOTAL")));
		}


		wrap_efc4 = new HashMap<Integer, HashMap<String, String>>();
		wrap_efc4.put(4, final_result_efc4);


		//Gson	gson	= new GsonBuilder().create();
		//to_return = gson.toJson(final_result_efc1);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(conEFC4!=null) conEFC4.close();
		}
		return wrap_efc4;
	}


	public String fsInventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		Connection		conFS			= null;
		String          sql_fs          = null;
		String          fs_schema       = null;
		String          to_return       = null;
		try{
		conFS = ReportActivator.getInstance().getConnection(Constants.OMS);


		fs_schema = ReportActivator.getInstance().getReportDBMap()
				.get(Constants.OMS).getSchema();

		sql_fs =  "\n  WITH x"
				+ "\n  AS (SELECT t.loc_nbr ship_node, t.onhand_qty quantity"
				+ "\n  FROM " + fs_schema +".tskst t, " + fs_schema + ".xlt_sku x"
				+ "\n  WHERE     x.itm_nbr = t.item_nmbr"
				+ "\n  AND x.sku_nbr = " + search_value
				+ "\n  AND t.loc_nbr IN (873, 809, 819, 829, 859, 869, 879, 889)),"
				+ "\n  y"
				+ "\n  AS (SELECT d.LOC_NBR ship_node, d.TRN_CLSN_QTY expected"
				+ "\n  FROM " + fs_schema +".xlt_sku x, " + fs_schema +".PCT_TRN_CLSN_BAL d"
				+ "\n  WHERE     x.sku_nbr = " + search_value
				+ "\n  AND x.INTR_UPC_ID = d.INTR_UPC_ID"
				+ "\n  AND d.loc_nbr IN (873, 809, 819, 829, 859, 869, 879, 889)"
				+ "\n  AND d.TRN_CLSN_CDE = 'SE'),"
				+ "\n  z"
				+ "\n  AS (SELECT t.STR_NBR AS ship_node, SUM(t.ONORD_UNT_QTY) On_Order_QTY"
				+ "\n  FROM " + fs_schema +".XLT_SKU x, " + fs_schema +".TSKSTOO t"
				+ "\n  WHERE     x.SKU_NBR = " + search_value
				+ "\n  AND x.ITM_NBR = t.ITM_NBR"
				+ "\n  AND t.STR_NBR IN (873, 809, 819, 829, 859, 869, 879, 889)"
				+ "\n  GROUP BY t.STR_NBR)"
				+ "\n  SELECT CASE x.ship_node"
				+ "\n  WHEN 873 THEN 0"
				+ "\n  WHEN 809 THEN 1"
				+ "\n  WHEN 819 THEN 2"
				+ "\n  WHEN 829 THEN 3"
				+ "\n  WHEN 859 THEN 4"
				+ "\n  WHEN 869 THEN 5"
				+ "\n  WHEN 879 THEN 6"
				+ "\n  WHEN 889 THEN 7"
				+ "\n  END"
				+ "\n  AS NODE,"
				+ "\n  x.ship_node,"
				+ "\n  x.quantity,"
				+ "\n  COALESCE (y.expected, 0) as \"STORE_EXPECTED\","
				+ "\n  COALESCE (z.On_Order_QTY, 0) AS \"ON_ORDER\""
				+ "\n  FROM x"
				+ "\n  LEFT JOIN y"
				+ "\n  ON x.ship_node = y.ship_node"
				+ "\n  LEFT JOIN z"
				+ "\n  ON y.ship_node = z.ship_node"
				+ "\n  ORDER BY NODE"
				+ "\n  WITH UR";

		ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms = SQLUtils.getSQLResult(sql_fs, conFS);
		Gson	gson	= new GsonBuilder().create();
		to_return = gson.toJson(result_oms);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(conFS!=null) conFS.close();
		}
		return to_return;
	}


	public JsonObject ecomInventory(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		HttpClient 		httpclient		= null;
		GetMethod		httpGet			= null;

		JsonObject root_return = new JsonObject();
		JsonObject row_ecomm   = new JsonObject();
		JsonArray  ecomm_inventory_array = new JsonArray();

		//*************************************************************************************************************


		XProperties	props	= ReportActivator.getInstance().getSystemProperties();
		//props.load(getResourceAsStream("/WEB-INF/classes/com/storeelf/report/web/init/storeelf.properties"));
		//props.load(this.getClass().getResourceAsStream("storeelf.properties"));
		String instance	= props.getProperty("OPENAPI.INS");
		String version	= props.getProperty("OPENAPI.VERSION");
		String url 		= props.getProperty("OPENAPI."+instance+".URL");
		String api_key	= props.getProperty("OPENAPI."+instance+".API_KEY");

		httpclient	= new HttpClient();
		httpclient.getHostConfiguration().setProxy("proxy.storeelf.com", 3128);
		//httpGet = new GetMethod("http://api-atg.storeelf.com/v1/inventory/sku/"+search_value);
		httpGet = new GetMethod(url+version+"/inventory/sku/" +search_value);

		//httpGet		= new HttpGet("https://qe13-openapi.storeelfecommerce.com/v1/inventory/sku/"+search_value);
		httpGet.addRequestHeader("X-APP-API_KEY", api_key);
		httpGet.addRequestHeader("context","application/xml");

		httpclient.executeMethod(httpGet);
		// The underlying HTTP connection is still held by the response object
		// to allow the response content to be streamed directly from the network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST either fully consume the response content  or abort request
		// execution by calling CloseableHttpResponse#close().


		//httpEntity										= closableHttpResponse.getEntity();

		String		responseContent 					= httpGet.getResponseBodyAsString();
		// do something useful with the response body
		// and ensure it is fully consumed

		Document	doc									= Jsoup.parse(responseContent);
		Elements	stores								= doc.select("stores");

		//String storeNumber = "0";
		//String channel = "N/A";
		//String availability = "N/A";
		//String availableStock = "0";
		//String allocatedStock = "0";

		for(Element store: stores){

			row_ecomm.addProperty("STORE_NUMBER", String.valueOf(store.select("storeNum").text().trim()));
			row_ecomm.addProperty("CHANNEL", String.valueOf(store.select("channel").text().trim()));
			row_ecomm.addProperty("AVAILABILITY", String.valueOf(store.select("availability").text().trim()));
			row_ecomm.addProperty("AVAILABLE_STOCK", String.valueOf(store.select("availableStock").text().trim()));

			if(!store.select("allocatedStock").hasAttr("xsi:nil")){
				row_ecomm.addProperty("ALLOCATED_STOCK", String.valueOf(store.select("allocatedStock").text().trim()));
			} else{
				row_ecomm.addProperty("ALLOCATED_STOCK", "0");
			}

			ecomm_inventory_array.add(row_ecomm);

			row_ecomm = new JsonObject();
		}

		root_return.add("ecomm_array", ecomm_inventory_array);

		//*************************************************************************************************************


		return root_return;
	}


	public JsonObject itemDetails(String search_string) throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{
		String			search_value	= search_string;
		HttpClient 		httpclient		= null;
		GetMethod		httpGet			= null;

		//JsonObject root_return = new JsonObject();
		JsonObject img_url   = new JsonObject();
		//JsonArray  url_item_array = new JsonArray();

		//*************************************************************************************************************


		XProperties	props	= ReportActivator.getInstance().getSystemProperties();
		//props.load(getResourceAsStream("/WEB-INF/classes/com/storeelf/report/web/init/storeelf.properties"));
		//props.load(this.getClass().getResourceAsStream("storeelf.properties"));
		String instance	= props.getProperty("OPENAPI.INS");
		String version	= props.getProperty("OPENAPI.VERSION");
		String url 		= props.getProperty("OPENAPI."+instance+".URL");
		String api_key	= props.getProperty("OPENAPI."+instance+".API_KEY");

		httpclient	= new HttpClient();
		httpclient.getHostConfiguration().setProxy("proxy.storeelf.com", 3128);
		search_value=search_value.trim();
		httpGet = new GetMethod(url+version+"/product?skuCode=" +search_value+"&skuDetail=true&invFilter=false");
		logger.debug("Search URL= "+url+version+"/product?skuCode=" +search_value+"&skuDetail=true&invFilter=false");
		httpGet.addRequestHeader("X-APP-API_KEY", api_key);
		httpGet.addRequestHeader("context","application/xml");

		httpclient.executeMethod(httpGet);

		String		responseContent 					= httpGet.getResponseBodyAsString();
		// do something useful with the response body
		// and ensure it is fully consumed

		Document	doc									= Jsoup.parse(responseContent);
		Elements	images								= doc.select("images");

		for(Element image: images){

			img_url.addProperty("IMG_URL", String.valueOf(image.select("url").text().trim()));
			//url_item_array.add(img_url);
			//img_url = new JsonObject();
		}

		//root_return.add("img_url", img_url);

		//*************************************************************************************************************


		return img_url;
	}


	//Chub invoicing function 01/05/2015				
	public void chub_invoice(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String field_name = null;
		String field_value = null;
		String from_date = null;
		String to_date = null;
		String vendor_id = null;
		String dept_id = null;
		String item_id = null;
		Connection con = null;
		String sql = null;
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/order_management/chub_invoice.jsp";
		Gson gson = new GsonBuilder().create();
		JsonObject root = new JsonObject();
		SimpleDateFormat outformat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat informat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		ResultSet result = null;

		String exportResult = request.getParameter("export_result_csv");
		String ajax_request = request.getParameter("ajax_request");

		boolean isPost = StringUtils.equals(request.getMethod(), "POST");
		boolean isGet = StringUtils.equals(request.getMethod(), "GET");
		boolean isExport = org.apache.commons.lang.StringUtils.equals(exportResult, "true");
		boolean isAjax = org.apache.commons.lang.StringUtils.equals(ajax_request, "true");

		try {
			logger.info("method  --> " + request.getMethod());
			// if(StringUtils.equals(request.getMethod(),"POST") ||
			// org.apache.commons.lang.StringUtils.equals(exportResult,
			// "true")){
			if (isPost || (isGet && (isExport || isAjax))) {

				responseWriter = response.getWriter();

				String exportResultDataHeaders = "";
				String exportResultData = "SHIP_DATE,VENDOR_INVOICE_NUM,VENDOR_ID,CHUB_ORDER_NO,DEPARTMENT,INTERNAL_PO,PO_STATUS,SHIPMENT_QUANTITY,ITEM_ID\n";

				// if user requested a data export, export it otherwise don't
				// trigger download
				boolean triggerDownload = (org.apache.commons.lang.StringUtils.equals(exportResult, "true")) ? true
						: false;

				response.setContentType("application/json");

				/* HashMap<Integer, HashMap<String, Object>> map = null; */
				/*
				 * if(org.apache.commons.lang.StringUtils.equals(exportResult,
				 * "true")){ response.addHeader("Content-Type", "text/csv");
				 * response.addHeader("Content-Disposition",
				 * "attachment;filename=\"chubInvoiceDetails_"+(new
				 * SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
				 * response.setHeader("Content-Disposition",
				 * "attachment;filename=\"chubInvoiceDetails_"+(new
				 * SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
				 * 
				 * response_content = (String)
				 * getSessionAttribute("chub_invoice_details_csv"); }else{
				 */
				if (request.getParameter("showPage") != null) {

					if ((request.getParameter("showPage")).equals("Next")) {
						String strResult = (String) request.getSession().getAttribute("chub_invoice_result");
						root.addProperty("chub_invoice_result", strResult);
					}
					if ((request.getParameter("showPage")).equals("Previous")) {
						logger.debug(requestedPage + "|2.2 ");
						String strResult = (String) request.getSession().getAttribute("chub_invoice_result");
						root.addProperty("chub_invoice_result", strResult);
					}
				} else if (request.getParameter("field_value") == null) {
					String strEntries = request.getParameter("entries");

					request.getSession().setAttribute("ShowChubInvoiceRecords", strEntries);
					request.getSession().setAttribute("ChubInvoiceRecordNo", strEntries);

					String strResult = (String) request.getSession().getAttribute("chub_invoice_result");

					root.addProperty("show_chub_invoice_records", strEntries);
					root.addProperty("chub_invoice_record_no", strEntries);
					root.addProperty("chub_invoice_result", strResult);
				} else {
					field_name = request.getParameter("field_name").trim();
					field_value = request.getParameter("field_value").trim();
					vendor_id = request.getParameter("vendor_id").trim();
					dept_id = request.getParameter("dept_id").trim();
					from_date = request.getParameter("from_date").trim();
					logger.info("FROM DATE--> " + from_date);
					to_date = request.getParameter("to_date").trim();
					item_id = request.getParameter("item_id").trim();

					if (!com.storeelf.util.StringUtils.isVoid(field_name)
							|| !com.storeelf.util.StringUtils.isVoid(field_value)) {

						field_name = field_name.trim();
						field_value = (field_value.contains(",")) ? formatData(field_value, ",") : field_value;
						vendor_id = (vendor_id.contains(",")) ? formatData(vendor_id, ",") : vendor_id;
						dept_id = (dept_id.contains(",")) ? formatData(dept_id, ",") : dept_id;

						if (!com.storeelf.util.StringUtils.isVoid(from_date))
							from_date = outformat.format(informat.parse(from_date));
						if (!com.storeelf.util.StringUtils.isVoid(to_date))
							to_date = outformat.format(informat.parse(to_date));

						con = ReportActivator.getInstance().getConnection(Constants.OMS);

						sql = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_CHUB_INVOICE_SQL);

						if (!com.storeelf.util.StringUtils.isVoid(item_id))
							sql += "AND YFS_SHIPMENT_LINE.item_id = '" + item_id + "'  ";
						if (!com.storeelf.util.StringUtils.isVoid(to_date))
							sql += "AND yfs_shipment.shipment_key < '" + to_date + "'  ";
						if (!com.storeelf.util.StringUtils.isVoid(from_date))
							sql += "AND yfs_shipment.shipment_key > '" + from_date + "'  ";

						if (!com.storeelf.util.StringUtils.isVoid(field_name) && (field_name.equals("chub_no"))
								&& !com.storeelf.util.StringUtils.isVoid(field_value))
							sql += "AND YFS_SHIPMENT_LINE.ORDER_NO in (" + field_value + ")";
						else if (!com.storeelf.util.StringUtils.isVoid(field_name) && (field_name.equals("invoice_no"))
								&& !com.storeelf.util.StringUtils.isVoid(field_value))
							sql += "AND YFS_SHIPMENT.EXTN_DSV_INVOICE_NUM in (" + field_value + ")";

						else if (!com.storeelf.util.StringUtils.isVoid(field_name) && (field_name.equals("int_po_no"))
								&& !com.storeelf.util.StringUtils.isVoid(field_value))
							sql += "AND K.INT_PO_NO in (" + field_value + ")";

						// adding vendor and/or dept_id search
						if (!com.storeelf.util.StringUtils.isVoid(vendor_id))
							sql += " AND YFS_SHIPMENT.SHIPNODE_KEY in (" + vendor_id + ")";
						if (!com.storeelf.util.StringUtils.isVoid(dept_id))
							sql += " AND K.DEPARTMENT in (" + dept_id + ")";

						con = ReportActivator.getInstance().getConnection(Constants.OMS);
						logger.error(sql);

						String union_sql = sql.replace(" STERLING.YFS_SHIPMENT ", " STERLING.YFS_SHIPMENT_H ");
						union_sql = union_sql.replace(" STERLING.YFS_SHIPMENT_LINE ", " STERLING.YFS_SHIPMENT_LINE_H ");
						union_sql = union_sql.replace(" STERLING.K_OFT_SHIPMENT_INTPO ",
								" STERLING.K_OFT_SHIPMENT_INTPO_H ");

						result = con.createStatement().executeQuery(sql + " union all " + union_sql
								+ "   order by ship_date, vendor_invoice_num, vendor_id, chub_order_no, department, internal_po");

						// ResultSet result =
						// con.createStatement().executeQuery(sql);

						JsonArray table = new JsonArray();
						JsonObject row = new JsonObject();

						int rowCount = 0;

						String SHIP_DATE = "";
						String VENDOR_INVOICE_NUM = "";
						String VENDOR_ID = "";
						String CHUB_ORDER_NO = "";
						String DEPARTMENT = "";
						String INTERNAL_PO = "";
						String PO_STATUS = "";
						String SHIPMENT_QUANTITY = "";
						String ITEM_ID = "";

						while (result.next()) {
							SHIP_DATE = result.getString("SHIP_DATE");
							VENDOR_INVOICE_NUM = result.getString("VENDOR_INVOICE_NUM");
							VENDOR_ID = result.getString("VENDOR_ID");
							CHUB_ORDER_NO = result.getString("CHUB_ORDER_NO");
							DEPARTMENT = result.getString("DEPARTMENT");
							INTERNAL_PO = result.getString("INTERNAL_PO");
							PO_STATUS = result.getString("PO_STATUS");
							SHIPMENT_QUANTITY = result.getString("SHIPMENT_QUANTITY");
							ITEM_ID = result.getString("ITEM_ID");

							if (exportResultDataHeaders.length() == 0) {

								// TODO write csv headers
								for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
									exportResultDataHeaders += result.getMetaData().getColumnName(i + 1) + ",";
									if ((i + 1) == result.getMetaData().getColumnCount() - 1)
										exportResultDataHeaders.subSequence(0, exportResultDataHeaders.length() - 1);
								}
							}

							exportResultData += SHIP_DATE + ",";
							exportResultData += VENDOR_INVOICE_NUM + ",";
							exportResultData += VENDOR_ID + ",";
							exportResultData += CHUB_ORDER_NO + ",";
							exportResultData += DEPARTMENT + ",";
							exportResultData += INTERNAL_PO + ",";
							exportResultData += PO_STATUS + ",";
							exportResultData += SHIPMENT_QUANTITY + ",";
							exportResultData += ITEM_ID + ",";

							exportResultData += "\n";

							if (rowCount < 500 && triggerDownload == false) {
								row.addProperty("CHUB_ORDER_NO", CHUB_ORDER_NO);
								row.addProperty("SHIP_DATE", SHIP_DATE);
								row.addProperty("VENDOR_INVOICE_NUM", VENDOR_INVOICE_NUM);
								row.addProperty("VENDOR_ID", VENDOR_ID);
								row.addProperty("DEPARTMENT", DEPARTMENT);
								row.addProperty("INTERNAL_PO", INTERNAL_PO);
								row.addProperty("PO_STATUS", PO_STATUS);
								row.addProperty("SHIPMENT_QUANTITY", SHIPMENT_QUANTITY);
								row.addProperty("ITEM_ID", ITEM_ID);
								table.add(row);
								row = new JsonObject();
							} else {
								triggerDownload = true;
								setHeaderDownloadableCSV("chubInvoice", response);
								responseWriter.write(exportResultData);
								responseWriter.flush();
								exportResultData = "";
							}
							rowCount++;
						}
						root.addProperty("show_chub_invoice_records", "25");
						root.addProperty("chub_invoice_record_no", "25");
						root.add("chub_invoice_result", table);
					}

					if (triggerDownload == false) {
						response.setContentType("application/json");
					}

				}

				// setSessionAttribute("chub_invoice_details_csv",
				// exportResultDataHeaders+"\n"+exportResultData);
				// Convert HashMap into json object
				response_content = gson.toJson(root);

				//////// }

				if (triggerDownload == false) {
					response_content = gson.toJson(root);
					responseWriter.write(response_content);
				}

				responseWriter.flush();
				responseWriter.close();
			} else {
				// assume it's GET request, load JSP

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
		} finally {
			if(result != null) result.close();
			if(con != null) con.close();
		}
	}

	public void safety_factor(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String field_value = null;
		String store_no = null;
		String node_type = null;
		String sku_dept = null;
		String dept_number = null;
		Connection con = null;
		String sqlNodeItem = null;
		String sqlDlvryMthd = null;
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/order_management/safety_factor.jsp";
		Gson gson = new GsonBuilder().create();
		JsonObject root = new JsonObject();
		ResultSet result = null;
		ResultSet resultDM = null;
		String exportResult = request.getParameter("export_result_csv");
		String ajax_request = request.getParameter("ajax_request");

		boolean isPost = StringUtils.equals(request.getMethod(), "POST");
		boolean isGet = StringUtils.equals(request.getMethod(), "GET");
		boolean isExport = org.apache.commons.lang.StringUtils.equals(exportResult, "true");
		boolean isAjax = org.apache.commons.lang.StringUtils.equals(ajax_request, "true");
		//
		try {
			logger.info("method  --> " + request.getMethod());
			if (isPost || (isGet && (isExport || isAjax))) {
				responseWriter = response.getWriter();
				String exportResultData = "STORE_NO,SOURCING_TYPE,DEPT,SKU,SAFETY_FACTOR,SS_TYPE\n";

				// if user requested a data export, export it otherwise don't
				// trigger download
				boolean triggerDownload = (org.apache.commons.lang.StringUtils.equals(exportResult, "true")) ? true
						: false;

				/* HashMap<Integer, HashMap<String, Object>> map = null; */
				/*
				 * if(org.apache.commons.lang.StringUtils.equals(exportResult,
				 * "true")){ response.addHeader("Content-Type", "text/csv");
				 * response.addHeader("Content-Disposition",
				 * "attachment;filename=\"safetyFactorDetails_"+(new
				 * SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
				 * response.setHeader("Content-Disposition",
				 * "attachment;filename=\"safetyFactorDetails_"+(new
				 * SimpleDateFormat("MMddyyyy")).format(new Date())+".csv\"");
				 * 
				 * response_content = (String)
				 * getSessionAttribute("safety_factor_details_csv"); }else{
				 */
				if (request.getParameter("showPage") != null) {

					if ((request.getParameter("showPage")).equals("Next")) {
						String strResult = (String) request.getSession().getAttribute("safety_factor_result");
						root.addProperty("safety_factor_result", strResult);
					}
					if ((request.getParameter("showPage")).equals("Previous")) {
						logger.debug(requestedPage + "|2.2 ");
						String strResult = (String) request.getSession().getAttribute("safety_factor_result");
						root.addProperty("safety_factor_result", strResult);
					}
				} else if (request.getParameter("field_value") == null) {
					String strEntries = request.getParameter("entries");

					request.getSession().setAttribute("ShowSafetyFactorRecords", strEntries);
					request.getSession().setAttribute("SafetyFactorRecordNo", strEntries);

					String strResult = (String) request.getSession().getAttribute("safety_factor_result");

					root.addProperty("show_safety_factor_records", strEntries);
					root.addProperty("safety_factor_record_no", strEntries);
					root.addProperty("safety_factor_result", strResult);
				} else {

					field_value = request.getParameter("field_value");
					store_no = request.getParameter("store_number");
					node_type = request.getParameter("node_type");
					sku_dept = request.getParameter("sku_dept");
					dept_number = request.getParameter("dept_number");

					exportResultData = "STORE_NO,SOURCING_TYPE,DEPT,SKU,SAFETY_FACTOR,SAFETY_STOCK_TYPE,\n";
					field_value = field_value.replaceAll("\\s+", "");
					field_value = field_value.replaceAll(",", "','");

					con = ReportActivator.getInstance().getConnection(Constants.GIV);
					sqlNodeItem = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SAFETY_FACTOR_SQL);

					if (!("".equals(store_no))) {
						sqlNodeItem += "AND ind.node = '" + store_no + "' ";
					}
					if ("SKU".equals(sku_dept) && !("".equals(field_value))) {
						sqlNodeItem += "and ind.item_id in ('" + field_value + "')	";
					}
					if ("DEPT".equals(sku_dept) && !("".equals(dept_number))) {
						sqlNodeItem += "and i.extn_dept  = '" + dept_number + "'	";
					}

					logger.debug(sqlNodeItem);
					result = con.createStatement().executeQuery(sqlNodeItem);

					JsonArray table = new JsonArray();
					JsonObject row = new JsonObject();

					String STORE_NO = "";
					String SOURCING_TYPE = "";
					String DEPT = "";
					String SKU = "";
					String SAFETY_FACTOR = "";
					String SS_TYPE = "";

					int sqlNodeItem_rowCount = 0;

					while (result.next()) {
						STORE_NO = result.getString("STORE_NO").trim();
						DEPT = result.getString("DEPT");
						SKU = result.getString("SKU").trim();
						SAFETY_FACTOR = result.getString("SAFETY_FACTOR");
						SS_TYPE = "STORE SS";

						exportResultData += STORE_NO + ",";
						exportResultData += SOURCING_TYPE + ",";
						exportResultData += DEPT + ",";
						exportResultData += SKU + ",";
						exportResultData += SAFETY_FACTOR + ",";
						exportResultData += SS_TYPE + ",";

						exportResultData += "\n";

						if (sqlNodeItem_rowCount < 500 && triggerDownload == false) {
							row.addProperty("STORE_NO", STORE_NO);
							row.addProperty("SOURCING_TYPE", SOURCING_TYPE);
							row.addProperty("DEPT", DEPT);
							row.addProperty("SKU", SKU);
							row.addProperty("SAFETY_FACTOR", SAFETY_FACTOR);
							row.addProperty("SS_TYPE", SS_TYPE);

							table.add(row);
							row = new JsonObject();
						} else {
							triggerDownload = true;
							setHeaderDownloadableCSV("safetyFactorDetails", response);
							responseWriter.write(exportResultData);
							responseWriter.flush();
							exportResultData = "";
						}

						sqlNodeItem_rowCount++;
					}

					STORE_NO = "";
					SOURCING_TYPE = "";
					DEPT = "";
					SKU = "";
					SAFETY_FACTOR = "";
					SS_TYPE = "";

					int resultDM_rowCount = 0;

					if (!("".equals(field_value)) || !("".equals(dept_number))) {
						sqlDlvryMthd = SQLConstants.SQL_MAP.get(SQLConstants.ID_UTIL_SAFETY_FACTOR_SQL2);

						if ("SKU".equals(sku_dept) && !("".equals(field_value))) {
							sqlDlvryMthd += "SKU in ('" + field_value + "') ";
						}
						if ("DEPT".equals(sku_dept) && !("".equals(dept_number))) {
							sqlDlvryMthd += "dept = '" + dept_number + "'";
						}
						if (!("ALL".equals(node_type))) {
							sqlDlvryMthd += "AND attribute_value = ('" + node_type + "') ";
						}

						logger.debug(sqlDlvryMthd);
						resultDM = con.createStatement().executeQuery(sqlDlvryMthd);

						while (resultDM.next()) {

							DEPT = resultDM.getString("DEPT");
							SKU = resultDM.getString("SKU").trim();
							SOURCING_TYPE = resultDM.getString("SOURCING_TYPE");
							SAFETY_FACTOR = resultDM.getString("SAFETY_FACTOR");
							SS_TYPE = resultDM.getString("SS_TYPE");

							exportResultData += STORE_NO + ",";
							exportResultData += SOURCING_TYPE + ",";
							exportResultData += DEPT + ",";
							exportResultData += SKU + ",";
							exportResultData += SAFETY_FACTOR + ",";
							exportResultData += SS_TYPE + ",";

							exportResultData += "\n";

							if (resultDM_rowCount < 500 && triggerDownload == false) {
								row.addProperty("STORE_NO", STORE_NO);
								row.addProperty("SOURCING_TYPE", SOURCING_TYPE);
								row.addProperty("DEPT", DEPT);
								row.addProperty("SKU", SKU);
								row.addProperty("SAFETY_FACTOR", SAFETY_FACTOR);
								row.addProperty("SS_TYPE", SS_TYPE);

								table.add(row);
								row = new JsonObject();
							} else {
								triggerDownload = true;
								setHeaderDownloadableCSV("safetyFactorDetails", response);
								responseWriter.write(exportResultData);
								responseWriter.flush();
								exportResultData = "";
							}
							resultDM_rowCount++;
						}
					}

					if (triggerDownload == false) {
						response.setContentType("application/json");
						root.addProperty("result_greater_than_500", "false");
						root.addProperty("show_safety_factor_records", "25");
						root.addProperty("safety_factor_record_no", "25");
						root.add("safety_factor_result", table);
					}

				}

				if (triggerDownload == false) {
					response_content = gson.toJson(root);
					responseWriter.write(response_content);
				}

				responseWriter.flush();
				responseWriter.close();
			} else {
				// assume it's GET request, load JSP

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
		} finally {
			if(resultDM != null) resultDM.close();
			if(result != null) result.close();
			if(con != null) con.close();
		}

	}

	private String formatData(String input,String delimeter){
		ArrayList<String> list = new ArrayList<String>();
		String out = "'";
		try {
			list.add(input);
			String[] arr  = input.split("\\r?"+delimeter);


			for(int i = 0;i<arr.length;i++){

				if(i < arr.length-1)
					out = out + arr[i].trim()  + "','";

				else
					out = out + arr[i].trim() + "'";
			}

		} catch(Exception e){e.printStackTrace();}
		return out;

	}

	public void get_list_of_stores(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		Connection		connection			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		ResultSet			resultset		= null;
		
		try{

			//handle request parameters here
			responseWriter	= response.getWriter();

			
			JsonArray			stores			= new JsonArray();
			JsonObject			store			= new JsonObject();

			//content type MUST be "application/json"
			response.setContentType("application/json");

			connection		= ReportActivator.getInstance().getConnection(Constants.OMS);
			resultset		= (connection.prepareStatement("SELECT SHIPNODE_KEY, DESCRIPTION FROM yfs_ship_node WHERE node_type = 'STORE' order by shipnode_key")).executeQuery();

			//stores.add("Store No");

			while (resultset.next()) {
				store.addProperty("store_number", Integer.parseInt(resultset.getString(1).trim()));
				store.addProperty("store_description", resultset.getString(2).trim());

				stores.add(store);
				store = new JsonObject();
			}

			//store.addProperty("store_number", "ALL");
			//store.addProperty("store_description", "ALL");
			stores.add(store);

			store = null;

			//Convert HashMap into json object
			Gson 		gson				= new GsonBuilder().create();
			response_content	= gson.toJson(stores);

			//write content to response writer, flush before closing ... trust me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();

			
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.error("error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally {
			if(resultset!=null){resultset.close();}
			if(connection!=null){connection.close();}
		}
	}

	public void get_list_of_depts(String requestedPage, HttpServletRequest request, HttpServletResponse response) throws SQLException{
		Connection		connection			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		ResultSet		resultset		= null;
		try{

			//handle request parameters here
			responseWriter	= response.getWriter();

			
			JsonArray			depts			= new JsonArray();
			JsonObject			dept			= new JsonObject();

			//content type MUST be "application/json"
			response.setContentType("application/json");

			connection		= ReportActivator.getInstance().getConnection(Constants.GIV);
			resultset		= (connection.prepareStatement("Select distinct(category_id) as DEPT, Description from yfs_category where Length(trim(category_id)) <=4 AND Length(trim(description)) > 1 AND ASCII(category_id) <65 order by category_id")).executeQuery();

			//stores.add("Store No");

			String dept_number = "";

			while (resultset.next()) {

				dept_number = resultset.getString(1).trim();

				if(dept_number.equals("3PL"))
				{
					dept.addProperty("dept_number", dept_number);
				} else {
					dept.addProperty("dept_number", Integer.parseInt(dept_number));
				}
				dept.addProperty("dept_description", resultset.getString(2).trim());

				depts.add(dept);
				dept = new JsonObject();
			}


			depts.add(dept);

			dept = null;

			//Convert HashMap into json object
			Gson 		gson				= new GsonBuilder().create();
			response_content	= gson.toJson(depts);

			//write content to response writer, flush before closing ... trust me on this one ...
			responseWriter.write(response_content);
			responseWriter.flush();
			responseWriter.close();

			
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (ClassNotFoundException e){e.printStackTrace(); logger.error("error processing request : ClassNotFoundException", e);}
		catch (SQLException e)			{e.printStackTrace(); logger.error("error processing request : SQLException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally{
			if(resultset!=null) resultset.close();
			if(connection!=null) connection.close();
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


/*public void saveSearch(HttpServletRequest request, Object ... search_values){
			//HashMap<String, Map> search_history = (HashMap<String, Map>) getSessionAttribute(sessionId, "search_history");
			HashMap<Object[], String> search_history = (HashMap<Object[], String>) SecurityUtils.getSubject().getSession().getAttribute("search_history");
			if(search_history !=null){
				//store all search values
				//SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSS");
				//search_history.put(date_format.format(new Date())+"_________"+request.getRequestURI(), search_values);
				//search_history.put(request.getRequestURI(), search_values);
				if(search_history.containsKey(search_values)){
					if(!search_history.get(search_values).equals(request.getRequestURI()+"")){
						search_history.put(search_values, request.getRequestURI()+"");
					}
				}else{
					search_history.put(search_values, request.getRequestURI()+"");
				}
			}else{
				SecurityUtils.getSubject().getSession().setAttribute("search_history", new ConcurrentHashMap<String, HashMap<Object[], String>>());
				//setSessionAttribute(request.getSession().getId(), "search_history", new HashMap<String, String[]>());
			}
		}*/
