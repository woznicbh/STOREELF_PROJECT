/*****************************************************************************
 * Class Name		: XpathParser.java
 * Description	    : This Class has utility function to convert a list of 
 * 					  XPath to corresponding XML
 * Modification Log	:
 * ---------------------------------------------------------------------------
 * Ver #	Date			Author				        Modification
 * ---------------------------------------------------------------------------
 * 0.00a   Aug 26, 2010			Srijith Kartha		Initial Version
 * ---------------------------------------------------------------------------
 ****************************************************************************/

package com.storeelf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XpathParser {
	public static final String ELEMENT_XPATH = "XPath";
	public static final String ATTR_XPATH = "XPath";
	public static final String ATTR_VALUE = "Value";
	public static char CHAR_ATTR = '@';
	public static char CHAR_ELE_DELIM = '/';

	/**
	 * This function is used to convert a document with a list of XPath to
	 * corresponding XML document. Sample XPath document format should be as
	 * follows:
	 * 
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <Root>
	 * 	<XPath XPath='MultiApi/API/@Name' Value='manageInventoryMonitorRule' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@InventoryMonitorRule' Value='Parameterized' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@InventoryMonitorRuleName' Value='Parameterized' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@InventoryMonitorRuleType' Value='EVENT' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@LeadTimeLevel1Qty' Value='Parameterized' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@LeadTimeLevel2Qty' Value='Parameterized' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@LeadTimeLevel3Qty' Value='Parameterized' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@MaxMonitorDays' Value='730' />
	 * 	<XPath XPath='MultiApi/API/Input/InventoryMonitorRules/@OrganizationCode' Value='Canada' />
	 * </Root>
	 * }
	 * </pre>
	 * 
	 * @param docXpath
	 *            : XPath Document.
	 * @return The XML document populated with the attribute values from the
	 *         input XPath XML.
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	public Document createDocFromXPath(Document docXpath)
			throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			IOException {
		Document docOut = XMLUtils.createEmptyDocument();
		HashMap<String, ArrayList<Attr>> hmAttrMap = this.getAttributeMap(
				docXpath, docOut);
		HashMap<String, HashMap<String, Element>> hmEleMap = this
				.getChildElementMap(docXpath, docOut);
		docOut = this.createDocumentStructure(docOut, hmEleMap, hmAttrMap);
		return docOut;
	}

	/**
	 * This the main entry method used to create the XML from the list of XPath
	 * 
	 * @param docOut
	 *            : Final Output XML Document
	 * @param hmElementMap
	 *            : Element maps extracted from the XPath list
	 * @param hmAttributeMap
	 *            : Attribute map extracted from the XPath list
	 * @return Returns the output Document for the XPath list.
	 */
	protected Document createDocumentStructure(Document docOut,
			HashMap<String, HashMap<String, Element>> hmElementMap,
			HashMap<String, ArrayList<Attr>> hmAttributeMap) {
		HashMap<String, Element> hmElement = hmElementMap
				.get(XpathParser.CHAR_ELE_DELIM + "");
		String sRoot = hmElement.keySet().iterator().next();
		Element eleRoot = hmElement.get(sRoot);
		String sRootEleName = eleRoot.getNodeName()
				+ XpathParser.CHAR_ELE_DELIM;
		this.addAttributes(sRootEleName, eleRoot, hmAttributeMap);
		this.addElements(sRootEleName, hmElementMap, hmAttributeMap, eleRoot);
		docOut.appendChild(eleRoot);
		return docOut;
	}

	/**
	 * This method is used to add all the child elements for the provided root
	 * element.
	 * 
	 * @param sRootEleName
	 *            : Root element name
	 * @param hmElementMap
	 *            : Full Map of all the Elements listed.
	 * @param hmAttributeMap
	 *            : Full Map of all the Attributes listed.
	 * @param eleRootEle
	 *            Element object of the selected Root Element.
	 */
	protected void addElements(String sRootEleName,
			HashMap<String, HashMap<String, Element>> hmElementMap,
			HashMap<String, ArrayList<Attr>> hmAttributeMap, Element eleRootEle) {
		HashMap<String, Element> hmChildElements = hmElementMap
				.get(sRootEleName);
		if (hmChildElements != null) {
			Iterator<String> itChildEle = hmChildElements.keySet().iterator();
			while (itChildEle.hasNext()) {
				String sChildEle = itChildEle.next();
				Element eleChildEle = hmChildElements.get(sChildEle);
				String sChildEleName = eleChildEle.getNodeName();
				String sSeq = "";
				if (!(eleChildEle.getAttribute("Seq") == null || eleChildEle
						.getAttribute("Seq").equals(""))) {
					sSeq = eleChildEle.getAttribute("Seq");
				}
				String sQualifiedName = sRootEleName + sChildEleName + sSeq
						+ XpathParser.CHAR_ELE_DELIM;
				this.addAttributes(sQualifiedName, eleChildEle, hmAttributeMap);
				this.addElements(sQualifiedName, hmElementMap, hmAttributeMap,
						eleChildEle);
				eleRootEle.appendChild(eleChildEle);
			}
		}
	}

	/**
	 * This function is used to add the appropriate attributes for the selected
	 * root element.
	 * 
	 * @param sRootEleName
	 *            : Selected Root element name.
	 * @param eleRoot
	 *            : Element object for the Selected root element.
	 * @param hmAttributeMap
	 *            : Full map for all the attributes listed in the input
	 */
	protected void addAttributes(String sRootEleName, Element eleRoot,
			HashMap<String, ArrayList<Attr>> hmAttributeMap) {
		ArrayList<Attr> alAttrList = hmAttributeMap.get(sRootEleName);
		if (alAttrList != null) {
			Iterator<Attr> itAttr = alAttrList.iterator();
			while (itAttr.hasNext()) {
				Attr attrAttribute = itAttr.next();
				eleRoot.setAttribute(attrAttribute.getName(), attrAttribute.getValue());
			}
		}
	}

	/**
	 * This function is used to retrieve the map for attributes listed in the
	 * input
	 * 
	 * @param docIn
	 *            : Input XML Document containing the XPaths
	 * @param docOut
	 *            : Final Output XML Document to which all the attributes would
	 *            belong
	 * @return The map of all the Attributes listed in the input XPath list.
	 */
	protected HashMap<String, ArrayList<Attr>> getAttributeMap(Document docIn,
			Document docOut) {
		HashMap<String, ArrayList<Attr>> hmAttrMap = new HashMap<String, ArrayList<Attr>>();
		NodeList nlXPathList = docIn
				.getElementsByTagName(XpathParser.ELEMENT_XPATH);
		for (int count = 0; count < nlXPathList.getLength(); count++) {
			Element eleXPath = (Element) nlXPathList.item(count);
			String sXPath = eleXPath.getAttribute(XpathParser.ATTR_XPATH);
			String sValue = eleXPath.getAttribute(XpathParser.ATTR_VALUE);
			int iAttrDelim = sXPath.indexOf(XpathParser.CHAR_ATTR);
			if (iAttrDelim != -1) {
				String sElementXPath = sXPath.substring(0, iAttrDelim);
				String sAttrName = sXPath.substring(iAttrDelim + 1);
				Attr attrAttribute = docOut.createAttributeNS(sElementXPath,
						sAttrName);
				attrAttribute.setNodeValue(sValue);
				ArrayList<Attr> alAttrList = hmAttrMap.get(sElementXPath);
				if (alAttrList == null) {
					alAttrList = new ArrayList<Attr>();
				}
				alAttrList.add(attrAttribute);
				hmAttrMap.put(sElementXPath, alAttrList);
			}
		}
		return hmAttrMap;
	}

	/**
	 * This function is used to create a Map of all the elements used in the
	 * input XPath list to their child elements
	 * 
	 * @param docIn
	 *            : Input XPath XML Document.
	 * @param docOut
	 *            : Final output document to which the Elements would belong to.
	 * @return The Map of all the Elements used in the XPath Document.
	 */
	protected HashMap<String, HashMap<String, Element>> getChildElementMap(
			Document docIn, Document docOut) {
		HashMap<String, HashMap<String, Element>> hmEleMap = new HashMap<String, HashMap<String, Element>>();
		NodeList nlXPathList = docIn
				.getElementsByTagName(XpathParser.ELEMENT_XPATH);
		for (int count = 0; count < nlXPathList.getLength(); count++) {
			Element eleXPath = (Element) nlXPathList.item(count);
			String sXPath = eleXPath.getAttribute(XpathParser.ATTR_XPATH);
			String[] saElements = sXPath.split(XpathParser.CHAR_ELE_DELIM + "");
			for (int ieleCount = 1; ieleCount < saElements.length; ieleCount++) {
				String sElementXpath = "";
				String sParentXpath = "";
				String sElementName = "";
				for (int i = 0; i < ieleCount; i++) {
					sElementXpath = sElementXpath + saElements[i]
							+ XpathParser.CHAR_ELE_DELIM;
					if (i < (ieleCount - 1)) {
						sParentXpath = sParentXpath + saElements[i]
								+ XpathParser.CHAR_ELE_DELIM;
					}
				}
				if (ieleCount == 1) {
					sParentXpath = XpathParser.CHAR_ELE_DELIM + "";
				}
				if (ieleCount > 0) {
					sElementName = saElements[ieleCount - 1];
				}
				HashMap<String, Element> childEleMap = hmEleMap
						.get(sParentXpath);
				if (childEleMap == null) {
					childEleMap = new HashMap<String, Element>();
				}
				int iIterIndex = sElementName.lastIndexOf('_');
				String sIter = "";
				if (iIterIndex != -1) {
					sIter = sElementName.substring(iIterIndex);
					sElementName = sElementName.substring(0, iIterIndex);
				}
				Element eleChildEle = docOut.createElement(sElementName);
				if (!StringUtils.isVoid(sIter)) {
					eleChildEle.setAttribute("Seq", sIter);
				}
				childEleMap.put(sElementXpath, eleChildEle);
				hmEleMap.put(sParentXpath, childEleMap);
			}
		}
		return hmEleMap;
	}

	public Document getXPathListFromXML(Document xmlDoc)
			throws ParserConfigurationException {
		Document outdoc = XMLUtils.createEmptyDocument();
		Element rootEle = outdoc.createElement("Root");
		this.getXPathElement(xmlDoc.getDocumentElement(), xmlDoc.getDocumentElement().getNodeName(), rootEle, outdoc);
		outdoc.appendChild(rootEle);
		return outdoc;
	}

	public Element getXPathElement(Element ele, String elexpath,
			Element xpathEle, Document outdoc) {
		NodeList nl = ele.getChildNodes();
		NamedNodeMap attributes = ele.getAttributes();
		for (int iCount = 0; iCount < nl.getLength(); iCount++) {
			Node nchild = nl.item(iCount);
			if (nchild.getNodeType() == Node.ELEMENT_NODE) {
				this.getXPathElement((Element) nchild, elexpath
						+ XpathParser.CHAR_ELE_DELIM + nchild.getNodeName(),
						xpathEle,outdoc);
			}
		}
		for(int iCount=0;iCount<attributes.getLength();iCount++){
			Node attr = attributes.item(iCount);
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			String xpath = elexpath+XpathParser.CHAR_ELE_DELIM+XpathParser.CHAR_ATTR+attrName;
			Element eleXpath = outdoc.createElement("XPath");
			eleXpath.setAttribute("XPath", xpath);
			eleXpath.setAttribute("Value", attrValue);
			xpathEle.appendChild(eleXpath);
		}
		
		return xpathEle;
	}

	public static void main(String[] args) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			InputStream inputStream = new FileInputStream(
					new File(
							"C:\\SK\\Generic\\eclipse\\workspace\\com.slang.yantra.utils\\src\\com\\slang\\yantra\\utils\\xpath\\test_in.xml"));
			Document doc = documentBuilderFactory.newDocumentBuilder().parse(
					inputStream);
			XpathParser parser = new XpathParser();
			Document docOut = parser.createDocFromXPath(doc);
			System.out.println(XMLUtils.xmlToString(docOut));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}