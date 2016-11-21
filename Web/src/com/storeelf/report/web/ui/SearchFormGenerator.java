/**
 * 
 */
package com.storeelf.report.web.ui;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SearchFieldModal;
import com.storeelf.report.web.model.impl.SearchFieldModalImpl;
import com.storeelf.util.XMLUtils;

/**
 * <B>Class Name:</B><BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Dec 1, 2011 6:06:50 PM<BR/>
 */
public class SearchFormGenerator {
	private static final Logger logger = Logger
			.getLogger(SearchFormGenerator.class.getPackage().getName());

	private static SearchFormGenerator instance;

	/**
	 * @return the instance
	 */
	public static SearchFormGenerator getInstance() {
		if (instance == null) {
			instance = new SearchFormGenerator();
		}
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(SearchFormGenerator instance) {
		SearchFormGenerator.instance = instance;
	}

	public String getForm(String id,HttpServletRequest request) throws SQLException {
		logger.entering(SearchFormGenerator.class.getPackage().getName(),
				"getForm");
		String form = null;
		if("storecapacity".equals(id)){
			return modifyFormForStoreCapacity();			
		}	
		try {
			StreamSource xslInput = new StreamSource(new BufferedInputStream(
					this.getClass().getResourceAsStream("xsl/Search.xsl")));
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(xslInput);
			DOMResult domResult = new DOMResult();
			DOMSource xmlDomSource = null;
			Document docform = XMLUtils.createEmptyDocument();
			String desc = Constants.SRCH_FORM_DESC.get(id);
			TreeMap<String, SearchFieldModal> fieldmap = Constants.FRM_FIELD_MAP.get(id);
			Element formele = docform.createElement("form");
			Iterator<String> it = fieldmap.keySet().iterator();
			formele.setAttribute("description", desc);
			formele.setAttribute("id", id);
			while (it.hasNext()) {
				String fieldid = it.next();
				String fielddesc = ((SearchFieldModalImpl)fieldmap.get(fieldid)).getName();
				String isRequired = (((SearchFieldModalImpl)fieldmap.get(fieldid)).isRequired()+"").toUpperCase();
				String type = ((SearchFieldModalImpl)fieldmap.get(fieldid)).getType().toUpperCase();
				String value = request.getParameter(fieldid);
				Element field = docform.createElement("field");
				if(value==null){
					value="";
				}
				field.setAttribute("id", fieldid);
				field.setAttribute("desc", fielddesc);
				field.setAttribute("value", value);
				field.setAttribute("required", isRequired);
				field.setAttribute("type", type);
				formele.appendChild(field);
			}
			docform.appendChild(formele);
			xmlDomSource = new DOMSource(docform);
			xmlDomSource.setSystemId("search.xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xalan}line-separator", "\n");
			transformer.transform(xmlDomSource, domResult);
			form = XMLUtils.xmlToString(domResult.getNode());
			// Changes for Warehouse Transfer StoreElf Utility Screen - START
			if ("transferorderdetail".equals(id)) {
				form = modifyFormForWarehouseTransfer(form);
			}
			// Changes for Warehouse Transfer StoreElf Utility Screen - END
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		logger.exiting(SearchFormGenerator.class.getPackage().getName(),
				"getForm");
		return form;
	}
	// Changes for Warehouse Transfer StoreElf Utility Screen - START
	private String modifyFormForWarehouseTransfer(String form){
		form = form.replaceAll("<div", "<div style=\"width:150px;\"");
		form = form.replaceAll("</tr>", "");
		form = form.replaceAll("<td/>", "");
		form = form.replaceAll("</table>", "");
		form = form.replaceAll("</form>", "");
		String[] stArr = form.split("<tr>");
		String modifiedForm = "<form class=\"searchform\" method=\"get\" ><table>";
		String strTd = "";
		String strItemIDTd = "", strOrderNoTd = "", strOrderStatusTd = "", strReceivingNodeTd = "", strFromShipmentDate = "", strToShipmentDate = "",
			   strShipmentNoTd = "", strSourceNodeTd = "", strTransferTypeTd = "", strDeptCLSUBCL = getDeptCLSUBCL("Dept-CL-SUB CL","Dept","CL","SUB CL"), 
			   strFromOrderDate = "", strToOrderDate = "", strSubmit = "", strReset = "<td><input class=\"submitbutton\" type=\"reset\" value=\"Reset\"><td/>";
		for (int count = 1; count < stArr.length; count++) {
			strTd = stArr[count];
			if (strTd.contains("Item ID")) {
				strItemIDTd = strTd;
			}
			if (strTd.contains("Order No")) {
				strOrderNoTd = strTd;
			}
			if (strTd.contains("Order Status")) {
				strOrderStatusTd = getSelectOptions("Order Status", "Cancelled", "Created", "Invoiced", "Released", "Scheduled", "Sent Release To WMoS", "Shipped");
			}
			if (strTd.contains("Receiving Node")) {
				strReceivingNodeTd = getSelectOptions("Receiving Node","873","809","819","829");
			}
			if (strTd.contains("Shipment No")) {
				strShipmentNoTd = strTd;
			}
			if (strTd.contains("Source Node")) {
				strSourceNodeTd = getSelectOptions("Source Node","873","809","819","829");
			}
			if (strTd.contains("Transfer Type")) {
				strTransferTypeTd = getSelectOptions("Transfer Type","Warehouse Transfer");
			}
			if (strTd.contains("FromOrderDate")) {
				strFromOrderDate = strTd.replaceAll("<input", "<input size='17'");
			}
			if (strTd.contains("ToOrderDate")) {
				strToOrderDate = strTd.replaceAll("<input", "<input size='17'");
			}
			if (strTd.contains("FromShipmentDate")) {
				strFromShipmentDate = strTd.replaceAll("<input", "<input size='17'");
			}
			if (strTd.contains("ToShipmentDate")) {
				strToShipmentDate = strTd.replaceAll("<input", "<input size='17'");
			}
			if (strTd.contains("submitbutton")) {				
				strSubmit = strTd.replaceAll("<td>", "<td><span style=\"padding-left:20px\">");
				//System.out.println();
			}
		}
		modifiedForm = modifiedForm + "<tr>" + strOrderNoTd + strSourceNodeTd + "</tr>" 
									+ "<tr>" + strOrderStatusTd + strReceivingNodeTd + "</tr>" 
									+ "<tr>" + strShipmentNoTd + strTransferTypeTd + "</tr>" 
									+ "<tr>" + strFromShipmentDate + strItemIDTd + "</tr>"
									+ "<tr>" + strToShipmentDate + strDeptCLSUBCL + "</tr>"
									+ "</table>"
									+ "<table>"
									+ "<tr>" + strFromOrderDate + "</tr>"
									+ "<tr>" + strToOrderDate + strSubmit + strReset + "</tr>" 
									+ "</table></form>";
		return modifiedForm;
	}
	private String getSelectOptions(String... strs){
		String strDropdown = "<td> <div style=\"width:150px;\" class=\"searchfield\">" + strs[0] + "</div> </td><td>" +
			"<select id=\"" + strs[0] + "\" style=\"width: 135px\" id=\"" + strs[0] + "\" name=\"" + strs[0] + "\" onclick=\"selectAll('" + strs[0] + "')\" type=\"text\">";
		strDropdown = strDropdown + "<option value=\"\" selected></option>";
		for (int countOption = 1; countOption < strs.length; countOption++) {
			strDropdown = strDropdown + "<option value=\""+ strs[countOption] +"\">"+ strs[countOption] +"</option>";
		}
		return strDropdown + "</select></td>";
	}
	private String getDeptCLSUBCL(String... strs){
		String strTd = "<td><div style=\"width:150px;\" class=\"searchfield\">" + strs[0] + "</div></td><td>";
		for (int countOption = 1; countOption < strs.length; countOption++) {
			strTd = strTd + "<input class=\"fieldinput\" size=\"3\" id=\"" + strs[countOption] + "\" maxlength=\"10000\" name=\"" + strs[countOption] + "\" onclick=\"selectAll('" + strs[countOption] + "')\" type=\"text\" value=\"\"/>";
		}
		return strTd + "</td>";
	}
	// Changes for Warehouse Transfer StoreElf Utility Screen - END
	// Changes for Store Unit Capacity StoreElf Screen
	private String modifyFormForStoreCapacity() throws SQLException{
		Connection conn = null;
		ResultSet resultset = null;		
		ArrayList<String> al = new ArrayList<String>();
		String []strShipNodes = null;
		String strStoreOptions = "";
		try {			
			conn = ReportActivator.getInstance().getConnection(Constants.OMS);
			resultset = (conn.prepareStatement("select shipnode_key from sterling.yfs_ship_node where node_type = 'STORE' order by shipnode_key")).executeQuery();
			al.add("Store No");
			while (resultset.next()) {
				al.add(resultset.getString(1).trim());
			}
			al.add("All");
			strShipNodes = new String[al.size()];
			strShipNodes = al.toArray(strShipNodes);
			strStoreOptions = getSelectOptions(strShipNodes);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(resultset!=null){resultset.close();}
			if(conn!=null){conn.close();}
		}
		
		String form = "<form class=\"searchform\" method=\"get\" ><table><tr>" + strStoreOptions + "</tr><tr> <td/> <td> <input class=\"submitbutton\" type=\"submit\" value=\"Search\"/> </td> </tr> </table> </form>";
		
		return form;
	}
}
