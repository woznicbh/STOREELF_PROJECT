package com.storeelf.report.web.servlets.utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.DateUtils;
import com.storeelf.util.ExceptionUtil;
import com.storeelf.util.StoreElfAudit;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.SecurityUtils;
import com.storeelf.util.WSUtils;
import com.storeelf.util.XMLUtils;
import com.storeelf.util.XProperties;

public class OrderCancelHelper {
	static final Logger logger = Logger.getLogger(OrderCancelHelper.class);

	/**
	 * Builds the request and calls OMS cancel webservice and checks that it has properly cancelled.
	 * 
	 * @param releases
	 *            the JsonArray containing the selected releases for cancel in OMS
	 * @param cancel_all
	 *            HashMap to check if the EFC has been activated
	 * @return string status "200" if successful and "error" if failed
	 * @throws ConnectTimeoutException
	 *             when the Webservice times out
	 * @throws HttpException
	 *             when the webservice is unable to connect
	 * @throws SQLException
	 *             catch any SQLExceptions from db queries
	 * @throws IOException
	 *             catch remaining IO exceptions
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static String CallOmsApi(HttpServletRequest request, JsonArray releases,
			String cancel_all) throws ConnectTimeoutException, HttpException, SQLException,
			IOException, Exception {

		XProperties systemProperties = ReportActivator.getXProperties();
		String omsLink = systemProperties.getProperty("ORDERCANCEL.OMS.LINK");
		String soapXMLMsg = "";
		String sales_order_no = getSalesOrderNo(releases);
		boolean alreadyAllCancelled = true;
		boolean positiveWSReturn = true;
		boolean allCancelled = true;
		ArrayList<String> releaseList = getReleaseList(releases);
		String result[] = null;

		alreadyAllCancelled = IsOMSOrderReleasesCancelled(releaseList, sales_order_no);

		if (!alreadyAllCancelled) {
			soapXMLMsg = InputToOMSCancelWS(releases, systemProperties, cancel_all);
			result = WSUtils.CallWebService(omsLink, soapXMLMsg);
			positiveWSReturn = ValidateOmsWSResponse(result[1]);
			sleep(Integer.parseInt(systemProperties.getProperty("ORDERCANCEL.OMS.SLEEP")));
			allCancelled = IsOMSOrderReleasesCancelled(releaseList, sales_order_no);
		}

		if ((allCancelled && positiveWSReturn) || alreadyAllCancelled) {
			if (!alreadyAllCancelled) {
				StoreElfAudit.WriteAuditRecord(request, "oms", releases);
			}
			return "200";
		} else {
			logger.log(Level.ERROR, "CallOMSApi Failed :: Response from webservice :: " + result[1]
					+ "\n");
			return "error";
		}
	}

	/**
	 * Converts the String response of the Webservice to a DOM XML and checks the ResponseCode and
	 * ResponseMessage
	 * 
	 * @param response
	 *            the String response from the OMS webservice call
	 * @return boolean of true if WS did not return a failure and false if it did
	 * @throws ParserConfigurationException
	 *             when the method is unable to parse the string into a XML
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static boolean ValidateOmsWSResponse(String response)
			throws ParserConfigurationException, Exception {
		
		Document doc = XMLUtils.transformStringToDoc(response);

		Element docEle = doc.getDocumentElement();
		Element responseEle = (Element) docEle.getElementsByTagName("ns5:Response").item(0);
		
		if (responseEle != null) {
			String responseCode = responseEle.getAttribute("ResponseCode");
			String responseMessage = responseEle.getAttribute("ResponseMessage");

			if (responseCode.equalsIgnoreCase("FAILURE")) {
				logger.log(Level.ERROR, "OMS Webservice call failed with the following error :: " + responseMessage);
				return false;
			} else if (responseCode.equalsIgnoreCase("SUCCESS")) {
				logger.log(Level.INFO, "OMS Webservice call Succeeded :: " + responseMessage);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks OMS DB with the Order Number and Release numbers to see if they have been cancelled.
	 * 
	 * @param releaseList
	 *            an arrayList of all the releases that were cancelled in the order
	 * @param sales_order_no
	 *            the order number of the order being cancelled
	 * @return the boolean if it has been successfully cancelled or not
	 * @throws SQLException
	 *             when there is an issue with the sql
	 * @throws IOException
	 *             when there is an I/O issue with the DB
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static boolean IsOMSOrderReleasesCancelled(ArrayList<String> releaseList,
			String sales_order_no) throws SQLException {

		Connection omsCon = null;
		String didOMSCancelSql = null;
		ResultSet resultset = null;
		String status = null;
		boolean allCancelled = true;

		didOMSCancelSql = " select r.RELEASE_NO, r.SALES_ORDER_NO,ol.prime_line_no, ors.STATUS "
				+ "  from yfs_order_release r, " + "    yfs_order_release_status ors, "
				+ "    yfs_status stat, " + "    yfs_order_header oh, " + "    yfs_order_line ol, "
				+ "    yfs_ship_node sn " + "    where r.ORDER_HEADER_KEY = oh.order_header_key "
				+ "    and ors.ORDER_RELEASE_KEY = r.ORDER_RELEASE_KEY "
				+ "    and sn.SHIPNODE_KEY = r.SHIPNODE_KEY "
				+ "    and ors.ORDER_LINE_KEY = ol.ORDER_LINE_KEY  "
				+ "    and ors.status = stat.status " + "    and ors.STATUS_QUANTITY > 0 "
				+ "    and stat.process_type_key = 'ORDER_FULFILLMENT' " + "	  and oh.ORDER_NO = '"
				+ sales_order_no + "' " + "    and r.release_no in (";
		for (int h = 0; h < releaseList.size(); h++) {
			didOMSCancelSql += "'" + releaseList.get(h) + "'";

			if (h != releaseList.size() - 1) {
				didOMSCancelSql += ",";
			}
		}
		didOMSCancelSql += ")";
		try {
			omsCon = ReportActivator.getInstance().getConnection(Constants.OMS);
			resultset = omsCon.prepareStatement(didOMSCancelSql).executeQuery();

			while (resultset.next()) {
				status = resultset.getString("STATUS").trim();
				if (!status.equalsIgnoreCase("9000")) {
					allCancelled = false;
				}
			}
		} catch (SQLException e) {
			ExceptionUtil.HandleCatchErrorException(e, logger,
					"SQLException in checking if OMS cancelled");
		} catch (IOException e) {
			ExceptionUtil.HandleCatchErrorException(e, logger,
					"IOException in checking if OMS cancelled");
		} catch (Exception e) {
			ExceptionUtil.HandleCatchErrorException(e, logger,
					"Exception in checking if OMS cancelled");
		} finally {
			if(resultset!=null){resultset.close();}
			if(omsCon!=null){omsCon.close();}
		}
		return allCancelled;
	}

	/**
	 * Generate input request for OMS web service for both OrderLine and Order cancel scenarios by
	 * checking cancel_all.
	 * 
	 * @param releases
	 *            the JsonArray containing all the Order Releases
	 * @param systemProperties
	 *            systemProperties to access props
	 * @param cancel_all
	 *            states whether we are cancelling the whole order or part
	 * @param releaseList
	 *            an arrayList to grab all the related releases being cancelled in OMS
	 * 
	 * @return the generated string request
	 */
	public static String InputToOMSCancelWS(JsonArray releases, XProperties systemProperties,
			String cancel_all) {
		String sales_order_no = null;
		String cancel_reason = null;
		String prime_line_no = null;
		String sub_line_no = null;
		String soapXMLMsg = null;
		JsonElement releaseEle, orderLineEle = null;
		JsonObject releaseObj, orderLineObj = null;
		JsonArray jArray = null;

		String omsUser = systemProperties.getProperty("ORDERCANCEL.OMS.USER");
		String omsPass = systemProperties.getProperty("ORDERCANCEL.OMS.PASS");
		String omsNode = systemProperties.getProperty("ORDERCANCEL.OMS.NODE");

		for (int i = 0; i < releases.size(); i++) {
			releaseEle = releases.get(i);
			releaseObj = releaseEle.getAsJsonObject();

			if (i == 0) {
				sales_order_no = releaseObj.get("sales_order_no").toString();
				sales_order_no = sales_order_no.replace("\"", "");
				cancel_reason = releaseObj.get("cancel_reason").toString();
				cancel_reason = cancel_reason.replace("\"", "");

				soapXMLMsg = "<soapenv:Envelope  "
						+ " xmlns:inp1=\"http://www.sterlingcommerce.com/jaxws/YFS/createEnvironment/input\"  "
						+ " xmlns:inp=\"http://webservices.oms.storeelf.com/jaxws/StoreElfCancelOrderNOrderLineWS/cancelOrder/input\"  "
						+ " xmlns:web=\"http://webservices.oms.storeelf.com/\"  "
						+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"  "
						+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"  "
						+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  "
						+ " <soapenv:Header>  "
						+ "   <MessageHeader version=\"1.0\" xmlns=\"urn:storeelf:xml:schemas:message-header:v1_0\">"
						+ "     <MessageID xmlns=\"\">" + com.storeelf.util.StringUtils.generateUuid()
						+ "</MessageID>" + "     <CreateDateTime xmlns=\"\">"
						+ DateUtils.getCurrentDate() + "</CreateDateTime>"
						+ "     <From xmlns=\"\" systemCode=\"OMS\" app=\"storeelf\" nodeID=\""
						+ omsNode + "\" module=\"SoapUI\"/>" + "   </MessageHeader>"
						+ " </soapenv:Header>" + " <soapenv:Body>" + "   <web:cancelOrder>"
						+ "     <env userId=\"" + omsUser + "\" password=\""
						+ SecurityUtils.symmetricDecrypt(omsPass, Constants.STOREELF_CERT_KEY)
						+ "\"/>" + "     <input OrganizationCode=\"STOREELF.COM\">"
						+ "       <inp:OrderHeader ";
				if (!cancel_all.equalsIgnoreCase("true")) {
					soapXMLMsg += " CompleteOrderCancel=\"N\" ";
				} else {
					soapXMLMsg += " CompleteOrderCancel=\"Y\" ";
				}
				soapXMLMsg += "OrderNumber=\"" + sales_order_no + "\" cancelReason=\"fraud\"/>";
				if (!cancel_all.equalsIgnoreCase("true")) {
					soapXMLMsg += "<inp:OrderLines> ";
				}
			}
			if (!cancel_all.equalsIgnoreCase("true")) {
				jArray = releaseObj.getAsJsonArray("order_array");

				for (int j = 0; j < jArray.size(); j++) {
					orderLineEle = jArray.get(j);
					orderLineObj = orderLineEle.getAsJsonObject();
					prime_line_no = orderLineObj.get("PRIME_LINE_NO").toString();
					prime_line_no = prime_line_no.replace("\"", "");
					sub_line_no = orderLineObj.get("SUB_LINE_NO").toString();
					sub_line_no = sub_line_no.replace("\"", "");

					soapXMLMsg += "<inp:OrderLine PrimeLineNo=\"" + prime_line_no
							+ "\" SubLineNo=\"" + sub_line_no + "\"/>";
				}
			}
			if (i == (releases.size() - 1)) {
				if (!cancel_all.equalsIgnoreCase("true")) {
					soapXMLMsg += "</inp:OrderLines>";
				}
				soapXMLMsg += " </input> " + "</web:cancelOrder> " + "</soapenv:Body> "
						+ "</soapenv:Envelope>";
			}
		}
		return soapXMLMsg;
	}

	/**
	 * Grabs the Sales Order No from the first release.
	 * 
	 * @param releases
	 *            an array of all the Json object releases
	 * 
	 * @return the grabbed sales_order_no
	 */
	public static String getSalesOrderNo(JsonArray releases) {
		JsonElement releaseEle = null;
		JsonObject releaseObj = null;
		String sales_order_no = null;

		releaseEle = releases.get(0);
		releaseObj = releaseEle.getAsJsonObject();
		sales_order_no = releaseObj.get("sales_order_no").toString();
		sales_order_no = sales_order_no.replace("\"", "");

		return sales_order_no;
	}

	/**
	 * Grabs the Release No and stores it in a list.
	 * 
	 * @param releases
	 *            an array of all the Json object releases
	 * 
	 * @return the ArrayList of the Releases
	 */
	public static ArrayList<String> getReleaseList(JsonArray releases) {

		String release_no = null;
		ArrayList<String> releaseList = new ArrayList<String>();

		for (int i = 0; i < releases.size(); i++) {
			release_no = releases.get(i).getAsJsonObject().get("release_no").toString();
			release_no = release_no.replace("\"", "");
			releaseList.add(release_no);
		}

		return releaseList;
	}

	/**
	 * Builds the request and calls WMOS to signin, then cancel, then sign out using webservices and
	 * checks that it has properly cancelled.
	 * 
	 * @param orders
	 *            the JsonArray containing the selected releases for cancel in WMOS
	 * @return string status "200" if successful and "error" if failed
	 * @throws ConnectTimeoutException
	 *             when the Webservice times out
	 * @throws HttpException
	 *             when the webservice is unable to connect
	 * @throws SQLException
	 *             catch any SQLExceptions from db queries
	 * @throws IOException
	 *             catch remaining IO exceptions
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static String CallWmosApi(HttpServletRequest request, JsonArray orders) throws ConnectTimeoutException,
			HttpException, IOException, Exception {

		XProperties systemProperties = ReportActivator.getXProperties();
		int status = 0;
		String result = null;
		boolean allCancelled = true;

		HashMap<String, String> efcLinks = OrderCancelHelper.efcLinks(systemProperties);

		HashMap<String, Boolean> efcFlags = OrderCancelHelper.efcFlags();

		HashMap<String, String> efcTokens = new HashMap<String, String>();

		/****************************************************************************************
		 **
		 ** Sign in!
		 ** 
		 *****************************************************************************************/
		OrderCancelHelper.CallSignIntoWmos(orders, efcFlags, systemProperties, efcTokens, efcLinks);

		/****************************************************************************************
		 **
		 ** Cancel!
		 ** 
		 *****************************************************************************************/
		result = OrderCancelHelper.CallCancelInWmos(orders, efcLinks, efcTokens);

		/****************************************************************************************
		 **
		 ** Sign Out!
		 ** 
		 *****************************************************************************************/
		status = OrderCancelHelper.CallSignOutWmos(orders, efcFlags, efcLinks, efcTokens);

		/*****************************************************************************************/
		sleep(Integer.parseInt(systemProperties.getProperty("ORDERCANCEL.WM.SLEEP")));
		allCancelled = OrderCancelHelper.IsWMOSReleaseCancelled(orders);

		if (allCancelled) {
			StoreElfAudit.WriteAuditRecord(request, "wmos", orders);
			return "200";
		} else {
			logger.log(Level.ERROR, "CallWmosApi Failed :: Response from webservice :: "+ result +"\n");
			return "error";
		}
	}

	/**
	 * Generate input request for WMOS sign in web service.
	 * 
	 * @param systemProperties
	 *            systemProperties to access props
	 * 
	 * @return the generated string request
	 */
	public static String InputToWMOSSignIn(XProperties systemProperties) {

		String wmos_username = systemProperties.getProperty("ORDERCANCEL.WM.USER");
		String wmos_password = systemProperties.getProperty("ORDERCANCEL.WM.PASS");

		String soapXMLMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:q0=\"http://security.services.scope.manh.com\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "  <q0:signIn>"
				+ "    <q0:userId>"
				+ wmos_username
				+ "</q0:userId>"
				+ "    <q0:password>"
				+ SecurityUtils.symmetricDecrypt(wmos_password, Constants.STOREELF_CERT_KEY)
				+ "</q0:password>" + "  </q0:signIn>" + "</soapenv:Body>" + "</soapenv:Envelope>";

		return soapXMLMsg;
	}

	/**
	 * Loops through Orders JsonArray to get the ShipNode to create the SignIn Webservice URL and
	 * calls to sign into that EFC.
	 * 
	 * @param orders
	 *            the JsonArray containing the selected orders
	 * @param efcFlags
	 *            HashMap to check if the EFC has been activated
	 * @param systemProperties
	 *            systemProperties to access props
	 * @param efcTokens
	 *            the sign in tokens for the EFCs
	 * @param efcLinks
	 *            the Webservice urls for the EFCs
	 * @throws ConnectTimeoutException
	 *             when the Webservice times out
	 * @throws HttpException
	 *             when the webservice is unable to connect
	 * @throws IOException
	 *             catch remaining IO exceptions
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static void CallSignIntoWmos(JsonArray orders, HashMap<String, Boolean> efcFlags,
			XProperties systemProperties, HashMap<String, String> efcTokens,
			HashMap<String, String> efcLinks) throws ConnectTimeoutException, HttpException,
			IOException, Exception {

		JsonObject order = null;
		String shipnode_key = null;
		String token = null;
		String targetLink = null;
		String response[] = null;
		String soapXMLMsg = InputToWMOSSignIn(systemProperties);

		for (JsonElement orderElement : orders) {
			order = orderElement.getAsJsonObject();
			// make sure we haven't authenticated with the efc yet
			shipnode_key = order.get("shipnode_key").toString();
			if (!efcFlags.get(shipnode_key)) {
				targetLink = efcLinks.get(shipnode_key) + "SecurityWebService";
				response = WSUtils.CallWebService(targetLink, soapXMLMsg);
				token = ParseSignInResponse(response[1]);
				efcTokens.put(shipnode_key, token);
				if ((Integer.parseInt(response[0]) >= 200)
						&& (Integer.parseInt(response[0]) <= 205)) {
					efcFlags.put(shipnode_key, true);
				}
			}
		}
	}

	/**
	 * Generate input request for WMOS cancel web service.
	 * 
	 * @param token
	 *            is the token from sign in needed for request
	 * @param tc_order_id
	 *            is the orderId in WMOS which is the external Pick Ticket number in
	 *            yfs_order_release.extn_pick_ticket_no
	 * 
	 * 
	 * @return the generated string request
	 */
	public static String InputToWMOSCancel(String token, String tc_order_id) {

		String soapXMLMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:q0=\"http://service.wmos.scope.manh.com\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soapenv:Body>"
				+ "	<q0:cancelOrder>"
				+ "		<q0:param0>"
				+ token
				+ "</q0:param0>"
				+ "		<q0:param1>"
				+ tc_order_id
				+ "</q0:param1>"
				+ "		<q0:param2>12</q0:param2>"
				+ "		<q0:param3>A</q0:param3>"
				+ "	</q0:cancelOrder>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		return soapXMLMsg;
	}

	/**
	 * Loops through Orders Array getting the ShipNode to get the WMOS Cancel URL and gets the
	 * tc_order_id to cancel release in WMOS.
	 * 
	 * @param orders
	 *            the JsonArray containing the selected orders
	 * @param efcLinks
	 *            url hashmap to map efc to WMOS WS url
	 * @param efcTokens
	 *            the tokens for each EFCs Sign in to the WMOS WS
	 * @return the status of the WS response
	 * @throws ConnectTimeoutException
	 *             when the Webservice times out
	 * @throws HttpException
	 *             when the webservice is unable to connect
	 * @throws IOException
	 *             catch remaining IO exceptions
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static String CallCancelInWmos(JsonArray orders, HashMap<String, String> efcLinks,
			HashMap<String, String> efcTokens) throws ConnectTimeoutException, HttpException,
			IOException, Exception {

		String shipnode_key = null;
		String CancelSoapXMLMsg = null;
		String targetLink = null;
		String token = null;
		JsonElement x;
		JsonObject y;
		String result[] = null;

		for (int i = 0; i < orders.size(); i++) {

			x = orders.get(i);
			y = x.getAsJsonObject(); // convert to usable json object

			shipnode_key = y.get("shipnode_key").toString();
			targetLink = efcLinks.get(shipnode_key) + "CancelService";
			String tc_order_id = y.get("tc_order_id").toString();
			tc_order_id = tc_order_id.replace("\"", "");
			token = efcTokens.get(shipnode_key);

			if (tc_order_id != null) {
				CancelSoapXMLMsg = InputToWMOSCancel(token, tc_order_id);
				result = WSUtils.CallWebService(targetLink, CancelSoapXMLMsg);
			}
		}
		return result[1];
	}

	/**
	 * Generate input request for WMOS sign out web service.
	 * 
	 * @param token
	 *            is the token from sign in needed for request
	 * 
	 * @return the generated string request
	 */
	public static String InputToWmosSignOut(String token) {

		String soapXMLMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:q0=\"http://security.services.scope.manh.com\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soapenv:Body>"
				+ "<q0:signOut>"
				+ "		<q0:authToken>"
				+ token
				+ "</q0:authToken>" + "	</q0:signOut>" + "</soapenv:Body>" + "</soapenv:Envelope>";

		return soapXMLMsg;
	}

	/**
	 * Loops through efcFlags HashMap and for any EFCs that are active(true) it calls WMOS to sign
	 * out.
	 * 
	 * @param orders
	 *            the JsonArray containing the selected orders
	 * @param efcFlags
	 *            checks if the efc is active
	 * @param efcLinks
	 *            the Webservice url for each EFC
	 * @param efcTokens
	 *            the tokens for each EFCs Sign in to the WMOS WS
	 * @return the status of the WS response
	 * @throws ConnectTimeoutException
	 *             when the Webservice times out
	 * @throws HttpException
	 *             when the webservice is unable to connect
	 * @throws IOException
	 *             catch remaining IO exceptions
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static int CallSignOutWmos(JsonArray orders, HashMap<String, Boolean> efcFlags,
			HashMap<String, String> efcLinks, HashMap<String, String> efcTokens)
			throws ConnectTimeoutException, HttpException, IOException, Exception {
		Boolean value;
		String SignOutSoapXMLMsg = null;
		String token = null;
		String result[] = null;
		String targetLink = null;
		for (Map.Entry<String, Boolean> entry : efcFlags.entrySet()) {
			String key = entry.getKey();
			value = entry.getValue();
			token = efcTokens.get(key);
			if (value) {
				targetLink = efcLinks.get(key) + "SecurityWebService";
				SignOutSoapXMLMsg = InputToWmosSignOut(token);
				result = WSUtils.CallWebService(targetLink, SignOutSoapXMLMsg);
			}
		}
		return Integer.parseInt(result[0]);
	}

	/**
	 * Loops through Orders Array logging into the specific EFC DB Connection for each release and
	 * checks that each of the EFC Orders were cancelled in WMOS.
	 * 
	 * @param orders
	 *            the JsonArray containing the selected orders
	 * @return the boolean if it has been successfully cancelled or not
	 * @throws SQLException
	 *             when there is an issue with the sql
	 * @throws IOException
	 *             when there is an I/O issue with the DB
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static boolean IsWMOSReleaseCancelled(JsonArray orders) throws SQLException {
		Connection wmosCon = null;
		String wmosSql = null;
		String status = null;
		String order_no = null;
		boolean allCancelled = true;
		PreparedStatement wmosStmt = null;
		JsonElement x;
		JsonObject y;
		ConcurrentHashMap<Integer, HashMap<String, Object>> result_wmos;

		for (int i = 0; i < orders.size(); i++) {

			x = orders.get(i);
			y = x.getAsJsonObject();

			String tc_order_id = y.get("tc_order_id").toString();
			String shipnode_key = y.get("shipnode_key").toString();
			tc_order_id = tc_order_id.replace("\"", "");

			try {
				switch (shipnode_key) {
				case "873":
					wmosCon = ReportActivator.getInstance().getConnection(Constants.EFC1);
					break;
				case "809":
					wmosCon = ReportActivator.getInstance().getConnection(Constants.EFC2);
					break;
				case "819":
					wmosCon = ReportActivator.getInstance().getConnection(Constants.EFC3);
					break;
				case "829":
					wmosCon = ReportActivator.getInstance().getConnection(Constants.EFC4);
					break;
				}

				wmosSql = Constants.SQL_MAP.get(Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL);
				wmosStmt = wmosCon.prepareStatement(wmosSql);
				wmosStmt.setString(1, tc_order_id);
				result_wmos = SQLUtils.getPreparedSQLResult(wmosStmt,
						Constants.ID_UTIL_CANCEL_ORDER_WMOS_SQL, wmosCon);

				for (HashMap<String, Object> wmosMap : result_wmos.values()) {
					status = String.valueOf(wmosMap.get("DO_STATUS")).trim();
					order_no = String.valueOf(wmosMap.get("TC_ORDER_ID")).trim();
					
					if (!status.equalsIgnoreCase("200")) {
						allCancelled = false;
						logger.log(Level.ERROR, "Everything is Not Cancelled :: TC_ORDER_ID :: "+order_no+" :: DO_STATUS :: "+status);
					}
				}
			} catch (SQLException e) {
				ExceptionUtil.HandleCatchErrorException(e, logger,
						"SQLException in checking if WMOS cancelled after Webservice");
			} catch (IOException e) {
				ExceptionUtil.HandleCatchErrorException(e, logger,
						"IOException in checking if WMOS cancelled after Webservice");
			} catch (Exception e) {
				ExceptionUtil.HandleCatchErrorException(e, logger,
						"Exception in checking if WMOS cancelled after Webservice");
			} finally {
				if(wmosCon!=null){wmosCon.close();}
				if(wmosStmt!=null){wmosStmt.close();}
			}
		}

		return allCancelled;
	}

	/**
	 * Converts the String response of the Webservice to a DOM XML and grabs the Token from the
	 * response
	 * 
	 * @param response
	 *            the String response from the OMS webservice call
	 * @return String of the Token
	 * @throws ParserConfigurationException
	 *             when the method is unable to parse the string into a XML
	 * @throws Exception
	 *             catch any remaining exceptions
	 */
	public static String ParseSignInResponse(String response) throws ParserConfigurationException,
			Exception {

		Document doc = XMLUtils.transformStringToDoc(response);

		Element docEle = doc.getDocumentElement();
		Element responseEle = (Element) docEle.getElementsByTagName("ns:return").item(0);
		String token = responseEle.getTextContent();

		if (token == null || token.equals("")) {
			throw new RuntimeException("Token is null for WMOS signIn");
		}
		return token;
	}

	/**
	 * Generate HashMap containing EFC # and corresponding Webservice URL.
	 * 
	 * @param systemProperties
	 *            systemProperties to access props
	 * 
	 * @return the generated HashMap<String, String>
	 */
	public static HashMap<String, String> efcLinks(XProperties systemProperties) {

		HashMap<String, String> efcLinks = new HashMap<String, String>();
		efcLinks.put("873", systemProperties.getProperty("ORDERCANCEL.WM.EFC1"));
		efcLinks.put("809", systemProperties.getProperty("ORDERCANCEL.WM.EFC2"));
		efcLinks.put("819", systemProperties.getProperty("ORDERCANCEL.WM.EFC3"));
		efcLinks.put("829", systemProperties.getProperty("ORDERCANCEL.WM.EFC4"));

		return efcLinks;
	}

	/**
	 * Generate HashMap containing EFC # and initial false setting for if it is active
	 * 
	 * @return the generated HashMap<String, Boolean>
	 */
	public static HashMap<String, Boolean> efcFlags() {

		HashMap<String, Boolean> efcFlags = new HashMap<String, Boolean>();
		efcFlags.put("873", false);
		efcFlags.put("809", false);
		efcFlags.put("819", false);
		efcFlags.put("829", false);

		return efcFlags;
	}
	
	public static void sleep(int milleseconds) {
		try {
			Thread.sleep(milleseconds); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
