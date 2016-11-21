package com.storeelf.report.web.servlets.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.SQLConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.ExceptionUtil;
import com.storeelf.util.SQLUtils;

public class AdminFunctionsServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(AdminFunctionsServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/utility_includes/utility.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminFunctionsServlet() {
		super();
	}


	/**
	 * For Shipment Number/Key Lookup
	 * Sets up the XML message and sends it to  webservice_call() to execute.
	 * Writes the base64 encoded label to the related jsp function.
	 * @param requestedPage
	 * @param request
	 * @param response
	 */

	public void call_webservice(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter responseWriter = null;
		String soapXMLMsg="";
		String targetLink= ReportActivator.systemProperties.getProperty("REPRINT_LABEL_TARGET_LINK");
		try {
			responseWriter = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String address=request.getParameter("A");
		String city=request.getParameter("C");
		String state=request.getParameter("S");
		String full=request.getParameter("FULL");
		String phone=request.getParameter("P");
		String zip=request.getParameter("Z");
		String id=request.getParameter("SCM");
		String date=request.getParameter("D");
		String snk=request.getParameter("SNK");
		String length=request.getParameter("LEN");
		String width=request.getParameter("WID");
		String height=request.getParameter("HEI");
		String weight=request.getParameter("WEI");

		//this is gonna be broken due to  conflict in naming convention between proship and storeelf
		String csc=request.getParameter("CSC");

		//header stuff
		soapXMLMsg+="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:psws=\"http://psws.proshipservices.com/\" xmlns:pros=\"http://schttp://ksms1578:3278/pws-httphemas.datacontract.org/2004/07/ProShipWebServices\">"
				+"<soapenv:Header> "
				+ "</soapenv:Header>"
				+ "<soapenv:Body>"
				+"<psws:Ship xmlns:pros=\"http://schemas.datacontract.org/2004/07/ProShipWebServices\" xmlns:psws=\"http://psws.proshipservices.com/\">"
				+ "<psws:shipment>";

		//location part 1
		soapXMLMsg+= "<pros:ConsigneeAddress1>" + address + "</pros:ConsigneeAddress1>"
				+"<pros:ConsigneeAddress2/>"
				+"<pros:ConsigneeCity>" +city + "</pros:ConsigneeCity>"
				+ "<pros:ConsigneeContact>" + full  + "</pros:ConsigneeContact>";

		//location 2
		soapXMLMsg+="<pros:ConsigneeCountry>US</pros:ConsigneeCountry>"
				+ "<pros:ConsigneePhone>" + phone + "</pros:ConsigneePhone>"
				+ "<pros:ConsigneePostalcode>" + zip + "</pros:ConsigneePostalcode>"
				+ "<pros:ConsigneeResidential>True</pros:ConsigneeResidential>"
				+ "<pros:ConsigneeState>" +state + "</pros:ConsigneeState>";

		//custom nodes
		soapXMLMsg+="<pros:CustomNodes>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CARTON_ID</pros:NodePath>"
				+ "<pros:Value> " + id + "</pros:Value>" 
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CUSTOMER_ORDER_DATE</pros:NodePath>"
				+ "<pros:Value>" + date + "</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_SERVICE_LEVEL</pros:NodePath>"
				+ "<pros:Value>"+csc+"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_LABEL_OUTPUT_TYPE</pros:NodePath>"
				+ "<pros:Value>PNG</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_DESCRIPTION</pros:NodePath>"
				+ "<pros:Value>StoreElf.com</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_STORE_NUMBER</pros:NodePath>" 
				+ "<pros:Value>" + snk +"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "</pros:CustomNodes>";

		//box and footer
		soapXMLMsg+="<pros:Packages>"
				+ "<pros:ProShipPackage>"
				+ "<pros:Dimension>" + length+ "x" + width + "x" + height + "</pros:Dimension>"
				+ "<pros:MiscReference1/>"
				+ "<pros:Packaging>CUSTOM</pros:Packaging>"
				+ "<pros:Weight>" +weight+"</pros:Weight>"
				+ "</pros:ProShipPackage>"
				+ "</pros:Packages>"
				+ "<pros:ShipperReference/>"
				+ "<pros:Terms>SHIPPER</pros:Terms>"
				+ "</psws:shipment>"
				+ "</psws:Ship>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";



		String webResponse[]=webservice_call(targetLink, soapXMLMsg);

		String label=getValue(webResponse[1]);
		responseWriter.write(label);
		responseWriter.flush();
		responseWriter.close();

	}

	/**
	 * For Manual:
	 * Sets up the XML message and sends it to  webservice_call() to execute. 
	 * Writes the base64 encoded label to the related jsp function.
	 * @param requestedPage
	 * @param request
	 * @param response
	 */

	public void call_webservice2(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter responseWriter = null;
		String soapXMLMsg="";
		String targetLink= ReportActivator.systemProperties.getProperty("REPRINT_LABEL_TARGET_LINK");
		try {
			responseWriter = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String address=request.getParameter("A");
		String city=request.getParameter("C");
		String state=request.getParameter("S");
		String full=request.getParameter("FULL");
		String phone=request.getParameter("P");

		if(phone.startsWith("1")){
			phone=phone.substring(1);
		}

		String zip=request.getParameter("Z");
		String id=request.getParameter("SCM");
		String date=request.getParameter("D");
		String snk=request.getParameter("SNK");
		String box=request.getParameter("BOX");
		String weight=request.getParameter("WEI");

		String csc=request.getParameter("CSC");



		if(box.equalsIgnoreCase("dbox shipalone")){
			Connection con =null;
			//poll the database for yfs_item 
			String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_ShipAlone") + id + "'";
			try {
				con = ReportActivator.getInstance().getConnection(Constants.OMSr);
				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {
					box=String.valueOf(map.get("UNIT_LENGTH")).trim() +"X" +String.valueOf(map.get("UNIT_WIDTH")).trim()+"X"+String.valueOf(map.get("UNIT_HEIGHT")).trim();
				}

			} catch (ClassNotFoundException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}






		}
		else if(box.equalsIgnoreCase("bag")){
			box="10x5x2";
		}else if(box.equalsIgnoreCase("standard box")){
			box="20x16x15";
		}else{
			box="26x20x15";
		}

		//header stuff
		soapXMLMsg+="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:psws=\"http://psws.proshipservices.com/\" xmlns:pros=\"http://schttp://ksms1578:3278/pws-httphemas.datacontract.org/2004/07/ProShipWebServices\">"
				+"<soapenv:Header> "
				+ "</soapenv:Header>"
				+ "<soapenv:Body>"
				+"<psws:Ship xmlns:pros=\"http://schemas.datacontract.org/2004/07/ProShipWebServices\" xmlns:psws=\"http://psws.proshipservices.com/\">"
				+ "<psws:shipment>";

		//location part 1
		soapXMLMsg+= "<pros:ConsigneeAddress1>" + address + "</pros:ConsigneeAddress1>"
				+"<pros:ConsigneeAddress2/>"
				+"<pros:ConsigneeCity>" +city + "</pros:ConsigneeCity>"
				+ "<pros:ConsigneeContact>" + full  + "</pros:ConsigneeContact>";

		//location 2
		soapXMLMsg+="<pros:ConsigneeCountry>US</pros:ConsigneeCountry>"
				+ "<pros:ConsigneePhone>" + phone + "</pros:ConsigneePhone>"
				+ "<pros:ConsigneePostalcode>" + zip + "</pros:ConsigneePostalcode>"
				+ "<pros:ConsigneeResidential>True</pros:ConsigneeResidential>"
				+ "<pros:ConsigneeState>" +state + "</pros:ConsigneeState>";

		//custom nodes
		soapXMLMsg+="<pros:CustomNodes>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CARTON_ID</pros:NodePath>"
				+ "<pros:Value> " + id + "</pros:Value>" 
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CUSTOMER_ORDER_DATE</pros:NodePath>"
				+ "<pros:Value>" + date + "</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_SERVICE_LEVEL</pros:NodePath>"
				+ "<pros:Value>"+csc+"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_LABEL_OUTPUT_TYPE</pros:NodePath>"
				+ "<pros:Value>PNG</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_DESCRIPTION</pros:NodePath>"
				+ "<pros:Value>StoreElf.com</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_STORE_NUMBER</pros:NodePath>" 
				+ "<pros:Value>" + snk +"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "</pros:CustomNodes>";

		//box and footer
		soapXMLMsg+="<pros:Packages>"
				+ "<pros:ProShipPackage>"
				+ "<pros:Dimension>" + box + "</pros:Dimension>"
				+ "<pros:MiscReference1/>"
				+ "<pros:Packaging>CUSTOM</pros:Packaging>"
				+ "<pros:Weight>" +weight+"</pros:Weight>"
				+ "</pros:ProShipPackage>"
				+ "</pros:Packages>"
				+ "<pros:ShipperReference/>"
				+ "<pros:Terms>SHIPPER</pros:Terms>"
				+ "</psws:shipment>"
				+ "</psws:Ship>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";



		String webResponse[]=webservice_call(targetLink, soapXMLMsg);

		String label="";
		if(webResponse[0].equals("200")){
			label=getValue(webResponse[1]);
		}
		else{
			label=getErrorCode(webResponse[1]);
		}

		responseWriter.write(label);
		responseWriter.flush();
		responseWriter.close();

	}
	
	public void call_webservice3(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

		PrintWriter responseWriter = null;
		String soapXMLMsg="";
		String targetLink= ReportActivator.systemProperties.getProperty("REPRINT_LABEL_TARGET_LINK");
		try {
			responseWriter = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String toStoreNum=request.getParameter("TSN");
		String address=null;
		String city=null;
		String state=null;
		String full=null;
		String phone=null;
		String zip=null;

		String id=request.getParameter("SCM");
		String date=request.getParameter("D");
		String snk=request.getParameter("SNK");
		String box=request.getParameter("BOX");
		String weight=request.getParameter("WEI");
		String csc=request.getParameter("CSC");



		if(box.equalsIgnoreCase("dbox shipalone")){
			Connection con =null;
			//poll the database for yfs_item 
			String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_ShipAlone") + id + "'";
			try {
				con = ReportActivator.getInstance().getConnection(Constants.OMSr);
				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {
					box=String.valueOf(map.get("UNIT_LENGTH")).trim() +"X" +String.valueOf(map.get("UNIT_WIDTH")).trim()+"X"+String.valueOf(map.get("UNIT_HEIGHT")).trim();
				}
				

			} catch (ClassNotFoundException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(con!=null){con.close();}
			}

		}
		else if(box.equalsIgnoreCase("bag")){
			box="10x5x2";
		}else if(box.equalsIgnoreCase("standard box")){
			box="20x16x15";
		}else{
			box="26x20x15";
		}
		
		
		//now get the info from the database
		Connection con =null;
		//poll the database for yfs_item 
		String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_Store_Info") + toStoreNum + "'";
		try {
			con = ReportActivator.getInstance().getConnection(Constants.OMSPII);
			ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);
			for(HashMap<String, Object> map : rs.values()){
				address=String.valueOf(map.get("ADDRESS_LINE1")).trim();
				city=String.valueOf(map.get("CITY")).trim();
				state=String.valueOf(map.get("STATE")).trim();
				full=String.valueOf(map.get("ORGANIZATION_NAME")).trim(); 
				phone=String.valueOf(map.get("DAY_PHONE")).trim();
				zip=String.valueOf(map.get("ZIP_CODE")).trim();
			}
			

		} catch (ClassNotFoundException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(con!=null){con.close();}
		}
		
		
		

		//header stuff
		soapXMLMsg+="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:psws=\"http://psws.proshipservices.com/\" xmlns:pros=\"http://schttp://ksms1578:3278/pws-httphemas.datacontract.org/2004/07/ProShipWebServices\">"
				+"<soapenv:Header> "
				+ "</soapenv:Header>"
				+ "<soapenv:Body>"
				+"<psws:Ship xmlns:pros=\"http://schemas.datacontract.org/2004/07/ProShipWebServices\" xmlns:psws=\"http://psws.proshipservices.com/\">"
				+ "<psws:shipment>";

		//location part 1
		soapXMLMsg+= "<pros:ConsigneeAddress1>" + address + "</pros:ConsigneeAddress1>"
				+"<pros:ConsigneeAddress2/>"
				+"<pros:ConsigneeCity>" +city + "</pros:ConsigneeCity>"
				+ "<pros:ConsigneeContact>" + full  + "</pros:ConsigneeContact>";

		//location 2
		soapXMLMsg+="<pros:ConsigneeCountry>US</pros:ConsigneeCountry>"
				+ "<pros:ConsigneePhone>" + phone + "</pros:ConsigneePhone>"
				+ "<pros:ConsigneePostalcode>" + zip + "</pros:ConsigneePostalcode>"
				+ "<pros:ConsigneeResidential>True</pros:ConsigneeResidential>"
				+ "<pros:ConsigneeState>" +state + "</pros:ConsigneeState>";

		//custom nodes
		soapXMLMsg+="<pros:CustomNodes>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CARTON_ID</pros:NodePath>"
				+ "<pros:Value> " + id + "</pros:Value>" 
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_CUSTOMER_ORDER_DATE</pros:NodePath>"
				+ "<pros:Value>" + date + "</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_SERVICE_LEVEL</pros:NodePath>"
				+ "<pros:Value>"+csc+"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_LABEL_OUTPUT_TYPE</pros:NodePath>"
				+ "<pros:Value>PNG</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_DESCRIPTION</pros:NodePath>"
				+ "<pros:Value>StoreElf.com</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "<pros:ProShipCustomNodeItem>"
				+ "<pros:NodePath>CCN_STOREELF_STORE_NUMBER</pros:NodePath>" 
				+ "<pros:Value>" + snk +"</pros:Value>"
				+ "</pros:ProShipCustomNodeItem>"
				+ "</pros:CustomNodes>";

		//box and footer
		soapXMLMsg+="<pros:Packages>"
				+ "<pros:ProShipPackage>"
				+ "<pros:Dimension>" + box + "</pros:Dimension>"
				+ "<pros:MiscReference1/>"
				+ "<pros:Packaging>CUSTOM</pros:Packaging>"
				+ "<pros:Weight>" +weight+"</pros:Weight>"
				+ "</pros:ProShipPackage>"
				+ "</pros:Packages>"
				+ "<pros:ShipperReference/>"
				+ "<pros:Terms>SHIPPER</pros:Terms>"
				+ "</psws:shipment>"
				+ "</psws:Ship>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";



		String webResponse[]=webservice_call(targetLink, soapXMLMsg);

		String label="";
		if(webResponse[0].equals("200")){
			label=getValue(webResponse[1]);
		}
		else{
			label=getErrorCode(webResponse[1]);
		}

		responseWriter.write(label);
		responseWriter.flush();
		responseWriter.close();

	}


	private String getErrorCode(String in) {
		String s= in.split("<faultstring xml:lang=\"en-US\">")[1].split("</faultstring>")[0];

		return s;
	}


	/**
	 * Takes the XML message, adds the SOAP Action header, sends the message to the Proship server
	 * Returns the response XML from Proship
	 * @param targetLink
	 * @param soapXMLMsg
	 * @return
	 */

	public static String[] webservice_call(String targetLink, String soapXMLMsg) {

		PostMethod httpPost;
		HttpClient httpclient = new HttpClient();
		int status = 0;
		String response = null;
		String action="http://psws.proshipservices.com/IProShipWebService/Ship"; //will be the string from the soapui action area

		//just using Rate does not do the deed
		//String action="http://psws.proshipservices.com/IProShipWebService/Rate";

		httpPost = new PostMethod(targetLink);
		httpPost.addRequestHeader("Content-Type", "text/xml; charset=utf-8");

		//add soap action header
		httpPost.addRequestHeader("SOAPAction", action);
		httpPost.setRequestBody(soapXMLMsg);
		httpclient.setConnectionTimeout(10000);
		try {
			status = httpclient.executeMethod(httpPost);
			response = httpPost.getResponseBodyAsString();


			logger.debug("status: " + status);
			logger.debug("response: " + response);
		} catch (ConnectTimeoutException e) {
			ExceptionUtil.HandleCatchErrorException(e, logger, "ConnectTimeoutException Calling WS : ConnectTimeoutException");
		} catch (HttpException e) {
			ExceptionUtil.HandleCatchErrorException(e, logger, "HttpException Calling WS : HttpException");
		} catch (IOException e) {
			ExceptionUtil.HandleCatchErrorException(e, logger, "IOException Calling WS : IOException");
		} catch (Exception e) {
			ExceptionUtil.HandleCatchErrorException(e, logger, "Exception Calling WS : Exception");
		}
		return new String[] {String.valueOf(status), response};
	}

	/**
	 * Splits apart the XML and grabs the base64 encoded label
	 * @param in
	 * @return
	 */

	public static String getValue(String in){
		String s=in.split("CCN_LABEL</a:NodePath><a:Value>")[1].split("</a:Value></a:ProShipCustomNodeItem></a:CustomNodes>")[0];


		return s;
	}


	/**
	 * Queries the sterling database and grabs the different sizes of boxes
	 * Writes an array of them back to the jsp function
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */

	public void init_boxes(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		String content=null;
		PrintWriter responseWriter = null;
		JsonArray STORE_ARRAY=new JsonArray();


		if (StringUtils.equals(request.getMethod(), "POST")) {  
			//sql for getting store numbers
			Connection con = null;


			String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_Init_Boxes");
			try {
				con = ReportActivator.getInstance().getConnection(Constants.OMSr);
				responseWriter=response.getWriter();


				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {
					String box=String.valueOf(map.get("ITEM_ID")).trim();
					JsonPrimitive element = null;
					if(box.equalsIgnoreCase("standard box")){
					 element = new JsonPrimitive(box+ " (20x16x15)" );
					}else if(box.equalsIgnoreCase("oversize box")){
						element = new JsonPrimitive(box+ " (26x20x15)" );
					}
					else if(box.equalsIgnoreCase("bag")){
						element = new JsonPrimitive(box+ " (10x5x2)" );
					}
					else{
						element = new JsonPrimitive(box+ " (custom)" );
					}
					STORE_ARRAY.add(element);
				}

				Gson gson = new GsonBuilder().create();
				content = gson.toJson(STORE_ARRAY);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(con!=null){con.close();}
			}
		}


	}

	/**
	 * Queries the sterling database and grabs the different ship nodes.
	 * Writes an array of them back to the jsp function. 
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public void init_stores(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		String content=null;
		PrintWriter responseWriter = null;

		JsonArray STORE_ARRAY=new JsonArray();


		if (StringUtils.equals(request.getMethod(), "POST")) {  
			//sql for getting store numbers
			Connection con = null;


			String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_Init_Stores");			
			try {
				con = ReportActivator.getInstance().getConnection(Constants.OMSr);
				responseWriter = response.getWriter();


				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {
					JsonPrimitive element = new JsonPrimitive( String.valueOf(map.get("SHIPNODE_KEY")).trim());
					STORE_ARRAY.add(element);
				}

				Gson gson = new GsonBuilder().create();
				content = gson.toJson(STORE_ARRAY);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(con!=null){con.close();}
			}
		}	
	}

	/**
	 * Queries the sterling database and grabs the different ship nodes.
	 * Writes an array of them back to the jsp function. 
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public void init_srvclvl(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		String content=null;
		PrintWriter responseWriter = null;

		JsonArray STORE_ARRAY=new JsonArray();


		if (StringUtils.equals(request.getMethod(), "POST")) {  
			//sql for getting store numbers
			Connection con = null;


			String sql = SQLConstants.SQL_MAP.get("Reprint_Manual_Init_Svclvl");
			try {
				con = ReportActivator.getInstance().getDB(Constants.PROSHIP).getNewConnection();

				responseWriter = response.getWriter();



				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {
					JsonPrimitive element = new JsonPrimitive( String.valueOf(map.get("SRVC_LVL_DESC")).trim());
					STORE_ARRAY.add(element);
				}
				
				Gson gson = new GsonBuilder().create();
				content = gson.toJson(STORE_ARRAY);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(con!=null){con.close();}
			}
		}	
	}


	
	

	/**
	 * Main function of the reprint label webpage.
	 * Looks up Container SCM'S that are related to the requested Shipment Number 
	 * Using the Container SCM's, calls reprint_label_container
	 * Using the Container SCM's, calls reprint_label_drilldown
	 * Writes a nested JSON Array of information related to the Shipment Number back to the function on the jsp
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */

	public void reprint_label(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

		String content=null;

		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/admin_functions/reprint_label.jsp";

		JsonArray CONTAINERS_ARRAY=new JsonArray();
		JsonObject CONTAINER= new JsonObject();
		
		


		if (StringUtils.equals(request.getMethod(), "POST")) {
			//hits

			String shipno=request.getParameter("SHIPNO").trim();
			Connection con = null;


			try {

				//oms is busted????
				con = ReportActivator.getInstance().getConnection(Constants.OMSr);

				responseWriter = response.getWriter();


				String sql=null;


				if(shipno.length()<24)
					sql = SQLConstants.SQL_MAP.get("Reprint_Shipment_SCM") + shipno + "'";

				else
					sql = SQLConstants.SQL_MAP.get("Reprint_Shipment_SCM2") + shipno + "'"; 



				ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

				for (HashMap<String, Object> map : rs.values()) {

					String shipmentnum = String.valueOf(map.get("SHIPMENT_NO")).trim();
					String containerscm = String.valueOf(map.get("CONTAINER_SCM")).trim();
					String shipnodekey = String.valueOf(map.get("SHIPNODE_KEY")).trim();

					CONTAINER.addProperty("ship_no", shipmentnum);
					CONTAINER.addProperty("container_scm", containerscm);
					CONTAINER.addProperty("shipnode_key", shipnodekey);

					JsonArray details = reprint_label_container(containerscm);
					CONTAINER.add("ITEM_ARRAY", new JsonArray());
					CONTAINER.add("PERSON_ARRAY", new JsonArray());
					CONTAINER.getAsJsonArray("ITEM_ARRAY").addAll(details);

					JsonArray person = reprint_label_drilldown(containerscm);
					CONTAINER.getAsJsonArray("PERSON_ARRAY").addAll(person);

					CONTAINERS_ARRAY.add(CONTAINER);
					CONTAINER=new JsonObject();

				}

				Gson gson = new GsonBuilder().create();
				content = gson.toJson(CONTAINERS_ARRAY);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				if(con!=null){con.close();}
			}

		}
		else{
			try {
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page)
				.forward(request, response);
			} catch (ServletException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Queries the database looking for items located in the input Container SCM.
	 * Returns JSON Array of Items in the container
	 * @param container
	 * @return
	 * @throws SQLException 
	 */

	public JsonArray reprint_label_container(String container) throws SQLException {



		JsonArray root = null;

		//hits
		root = new JsonArray();

		Connection con = null;


		try {
			//oms is busted????
			con = ReportActivator.getInstance().getConnection(Constants.OMSr);



			String sql=null;
			sql = SQLConstants.SQL_MAP.get("Reprint_Both_Container") + container + "'"; 


			JsonObject data=new JsonObject();

			ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

			for (HashMap<String, Object> map : rs.values()) {



				String containerscm =  String.valueOf(map.get("CONTAINER_SCM")).trim();
				String itemid =  String.valueOf(map.get("ITEM_ID")).trim();
				String quantity = String.valueOf(map.get("QUANTITY")).trim();
				data.addProperty("container_scm", containerscm);
				data.addProperty("item_id", itemid);
				data.addProperty("quantity", quantity);
				data.addProperty("description", String.valueOf(map.get("SHORT_DESCRIPTION")).trim());

				root.add(data);
				data = new JsonObject();

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(con!=null){con.close();}
		}
		return root;


	}

	/**
	 * Queries the database looking for people with items in the input Container SCM
	 * Returns JSON Array of Person Info
	 * @param container
	 * @return
	 * @throws SQLException 
	 */

	public JsonArray reprint_label_drilldown(String container) throws SQLException {


		JsonArray root = null;

		//hits
		root = new JsonArray();
		String contscm=container;
		Connection con = null;

		try {
			//oms is busted????
			con = ReportActivator.getInstance().getConnection(Constants.OMSPII);

			//debug why it does not like she shipment_mo
			String sql = SQLConstants.SQL_MAP.get("Reprint_Both_Person") + contscm + "'"; 
			//String sql2 = SQLConstants.SQL_MAP.get("Reprint_Both_Person2") + contscm + "'"; 

			JsonObject data=new JsonObject();

			ConcurrentHashMap<Integer, HashMap<String, Object>> rs = SQLUtils.getSQLResult(sql, con);

			for (HashMap<String, Object> map : rs.values()) {

				String address=String.valueOf(map.get("ADDRESS_LINE1")).trim();
				String city=String.valueOf(map.get("CITY")).trim();
				String state=String.valueOf(map.get("STATE")).trim();
				String first=String.valueOf(map.get("FIRST_NAME")).trim();
				String last=String.valueOf(map.get("LAST_NAME")).trim();
				String phone=String.valueOf(map.get("DAY_PHONE")).trim();
				String zip=String.valueOf(map.get("ZIP_CODE")).trim();
				String scm=String.valueOf(map.get("CONTAINER_SCM")).trim();
				String odate=String.valueOf(map.get("ORDER_DATE")).trim();
				String csc=String.valueOf(map.get("REQUESTED_CARRIER_SERVICE_CODE")).trim();
				String snk=String.valueOf(map.get("SHIPNODE_KEY")).trim();
				String len=String.valueOf(map.get("CONTAINER_LENGTH")).trim();
				String wid=String.valueOf(map.get("CONTAINER_WIDTH")).trim();
				String hei=String.valueOf(map.get("CONTAINER_HEIGHT")).trim();
				String wei=String.valueOf(map.get("ACTUAL_WEIGHT")).trim();

				csc=csc.replace("Ground", "").trim();


				data.addProperty("address", address);
				data.addProperty("city", city);
				data.addProperty("state", state);
				data.addProperty("first_name", first);
				data.addProperty("last_name", last);
				data.addProperty("full_name", first + " " + last);
				data.addProperty("phone", phone);
				data.addProperty("zip", zip);
				data.addProperty("scm", scm);
				data.addProperty("odate", odate);
				data.addProperty("csc", csc);
				data.addProperty("snk", snk);
				data.addProperty("length", len);
				data.addProperty("width", wid);
				data.addProperty("height", hei);
				data.addProperty("weight", wei);


				root.add(data);
				data = new JsonObject();
				//con2.close();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(con!=null){con.close();}
		}

		return root;

	}




	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page the POST must return json/xml for client-side
	 * processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public void order_cancel(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		String search_value = null;
		Connection omsCon = null;
		Connection wmosCon = null;
		String omsSql = null;
		String wmosSql = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/admin_functions/order_cancel.jsp";
		String[] EFC_KEYS = new String[] { "873", "809", "819", "829" };
		ConcurrentHashMap<Integer, HashMap<String, Object>> result_wmos;
		PreparedStatement wmosStmt = null;
		PreparedStatement omsStmt = null;
		try {
			Gson gson = new GsonBuilder().create();
			if (StringUtils.isNotBlank(requestedPage)
					&& StringUtils.equals(requestedPage, "order_cancel")) {

				if (StringUtils.equals(request.getMethod(), "POST")) {
					search_value = request.getParameter("orderReleaseNumber");
					responseWriter = response.getWriter();
					response.setContentType("application/json");

					if (!com.storeelf.util.StringUtils.isVoid(search_value)) {
						search_value = search_value.trim();

						omsCon = ReportActivator.getInstance().getConnection(Constants.OMS);
						omsSql = Constants.SQL_MAP.get(Constants.ID_UTIL_OMS_CANCEL_SQL);

						omsStmt = omsCon.prepareStatement(omsSql);
						omsStmt.setString(1, search_value);
						omsStmt.setString(2, search_value);
						ConcurrentHashMap<Integer, HashMap<String, Object>> result_oms = SQLUtils
								.getPreparedSQLResult(omsStmt, Constants.ID_UTIL_OMS_CANCEL_SQL,
										omsCon);
						JsonObject ORDER_RELEASE = new JsonObject();
						JsonObject ORDER_DETAILS = new JsonObject();
						JsonObject root = new JsonObject();
						JsonArray ORDER_RELEASE_ARRAY = new JsonArray();

						int record_index = 0;

						for (HashMap<String, Object> map : result_oms.values()) {

							String SHIPNODE_KEY = String.valueOf(map.get("SHIPNODE_KEY")).trim();
							String PICKTICKET_NO = String.valueOf(map.get("PICKTICKET"));
							String RELEASE_NO_EX = String.valueOf(map.get("RELEASE_NO_EX"));

							logger.log(
									Level.INFO,
									"OrderManagementServlet:order_releaseResponse | "
											+ requestedPage + "|"
											+ request.getParameter("orderReleaseNumber"));

							ORDER_DETAILS.addProperty("ITEM_ID", String.valueOf(map.get("ITEM_ID"))
									.trim());
							ORDER_DETAILS.addProperty("PRIME_LINE_NO",
									String.valueOf(map.get("PRIME_LINE_NO")));
							ORDER_DETAILS.addProperty("SUB_LINE_NO",
									String.valueOf(map.get("SUB_LINE_NO")));
							ORDER_DETAILS.addProperty("STATUS_QUANTITY",
									String.valueOf(map.get("STATUS_QUANTITY")));

							if (record_index == 0
									|| ((ORDER_RELEASE.has("RELEASE_NO_EX")) && !org.apache.commons.lang.StringUtils
											.equals(ORDER_RELEASE.get("RELEASE_NO_EX")
													.getAsString(), RELEASE_NO_EX))) {

								ORDER_RELEASE = new JsonObject();
								/* Start commenting out here to test ONLY OMS */
								if (Arrays.asList(EFC_KEYS).contains(SHIPNODE_KEY)) {
									// means its in WMOS NOT in MP/3pl
									// query proper EFC

									switch (SHIPNODE_KEY) {
									case "873":
										wmosCon = ReportActivator.getInstance().getConnection(
												Constants.EFC1);
										break;
									case "809":
										wmosCon = ReportActivator.getInstance().getConnection(
												Constants.EFC2);
										break;
									case "819":
										wmosCon = ReportActivator.getInstance().getConnection(
												Constants.EFC3);
										break;
									case "829":
										wmosCon = ReportActivator.getInstance().getConnection(
												Constants.EFC4);
										break;
									}

									if (PICKTICKET_NO != null && PICKTICKET_NO.length() > 1) {
										wmosSql = Constants.SQL_MAP
												.get(Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL);
										wmosStmt = wmosCon.prepareStatement(wmosSql);
										wmosStmt.setString(1, PICKTICKET_NO);
										result_wmos = SQLUtils.getPreparedSQLResult(wmosStmt,
												Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL, wmosCon);
									} else {// this should never be tripped
										wmosSql = Constants.SQL_MAP
												.get(Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL);
										wmosStmt = wmosCon.prepareStatement(wmosSql);
										wmosStmt.setString(1, search_value);
										result_wmos = SQLUtils.getPreparedSQLResult(wmosStmt,
												Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL, wmosCon);
									}

									for (HashMap<String, Object> wmosMap : result_wmos.values()) {
										ORDER_RELEASE.addProperty("TC_ORDER_ID",
												String.valueOf(wmosMap.get("TC_ORDER_ID")));
										ORDER_RELEASE.addProperty("DESCRIPTION",
												String.valueOf(wmosMap.get("DESCRIPTION")));
										ORDER_RELEASE.addProperty("DO_STATUS",
												String.valueOf(wmosMap.get("DO_STATUS")));
									}

								}

								/* Stop commenting out here to test ONLY OMS */

								ORDER_RELEASE.add("ORDER_ARRAY", new JsonArray());

								ORDER_RELEASE.addProperty("SALES_ORDER_NO",
										String.valueOf(map.get("SALES_ORDER_NO")));
								ORDER_RELEASE.addProperty("ORDER_DATE",
										String.valueOf(map.get("ORDER_DATE")));
								ORDER_RELEASE.addProperty("NODE_TYPE",
										String.valueOf(map.get("NODE_TYPE")).trim());
								ORDER_RELEASE.addProperty("RELEASE_NO",
										String.valueOf(map.get("RELEASE_NO")));
								ORDER_RELEASE.addProperty("RELEASE_NO_EX",
										String.valueOf(map.get("RELEASE_NO_EX")));
								ORDER_RELEASE.addProperty("SHIPNODE_KEY",
										String.valueOf(map.get("SHIPNODE_KEY")).trim());
								ORDER_RELEASE.addProperty("PICKTICKET_NO",
										String.valueOf(map.get("PICKTICKET")));
								ORDER_RELEASE.addProperty("STATUS",
										String.valueOf(map.get("STATUS")).trim());
								ORDER_RELEASE.addProperty("STATUS_NAME",
										String.valueOf(map.get("STATUS_NAME")).trim());

								ORDER_RELEASE.getAsJsonArray("ORDER_ARRAY").add(ORDER_DETAILS);

								ORDER_RELEASE_ARRAY.add(ORDER_RELEASE);
							} else {

								ORDER_RELEASE.getAsJsonArray("ORDER_ARRAY").add(ORDER_DETAILS);
							}
							// logger.log(Level.INFO,
							// "OrderManagementServlet:order_releaseResponse | " + requestedPage +
							// "|" + request.getParameter("orderReleaseNumber"));
							ORDER_DETAILS = new JsonObject();

							record_index++;
						}
						// logger.log(Level.INFO, "OrderManagementServlet:order_releaseResponse | "
						// + requestedPage + "|" + request.getParameter("orderReleaseNumber"));
						root.add("ORDERS", ORDER_RELEASE_ARRAY);
						response_content = gson.toJson(root);
					}
					responseWriter.write(response_content);
					responseWriter.flush();
					responseWriter.close();

				} else {
					request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page)
					.forward(request, response);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : IOException", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : ClassNotFoundException", e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : SQLException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.ERROR, "error processing request : Exception", e);
		} finally {
			if (wmosStmt!=null){wmosStmt.close();}
			if (omsStmt!=null){omsStmt.close();}
			if (omsCon != null){omsCon.close();}
			if (wmosCon != null){wmosCon.close();}
		}
	}

	public void order_cancel_api(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws ConnectTimeoutException, HttpException,
	SQLException, ParserConfigurationException, IOException, Exception {

		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/order_management/order_cancel.jsp";

		if (StringUtils.isNotBlank(requestedPage)
				&& StringUtils.equals(requestedPage, "order_cancel_api")) {
			if (StringUtils.equals(request.getMethod(), "GET")) {

				String res = null;

				String releases = request.getParameter("json");
				JsonParser parser = new JsonParser();
				JsonArray jarr = (JsonArray) parser.parse(releases);
				String location = request.getParameter("location");
				String cancel_all = request.getParameter("cancel_all");

				if (location.equals("wmos")) {
					logger.debug("attempting to cancel in wm...");
					res = OrderCancelHelper.CallWmosApi(request, jarr);
				} else if (location.equals("oms")) {
					logger.debug("attempting to cancel in oms...");
					res = OrderCancelHelper.CallOmsApi(request, jarr, cancel_all);
				}

				/*
				 * RESPONSE
				 */
				responseWriter = response.getWriter();
				if (res.indexOf("error") < 0) {
					responseWriter.write("SUCCESS");
				} else {
					responseWriter.write("FAILURE");
					response.setStatus(500);
				}
				responseWriter.flush();
				responseWriter.close();
			} else {
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(
						request, response);
			}
		}
	}
}
