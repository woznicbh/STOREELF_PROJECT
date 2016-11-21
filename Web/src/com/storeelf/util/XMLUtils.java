/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storeelf.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * <B>Purpose:</B> XML Utilities<BR/>
 * <B>Creation Date:</B> Oct 21, 2011 3:35:01 PM<BR/>
 */
public class XMLUtils {
	private static XmlFormatter formatter = new XmlFormatter(2, 80);

	/**
	 * 
	 * <B>Purpose:</B> Parses the String to an XML Document Object
	 * 
	 * @param xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document parse(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xml)));

	}

	/**
	 * 
	 * <B>Purpose:</B>Converts an XML Document to a String Object
	 * 
	 * @param node
	 * @return
	 * @throws TransformerException
	 */
	public static String xmlToString(Node node) throws TransformerException {
		if (node == null) {
			return "";
		}
		Source source = new DOMSource(node);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xalan}line-separator", "\n");
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		transformer.transform(source, result);
		return stringWriter.getBuffer().toString();
	}

	/**
	 * 
	 * <B>Purpose:</B> Creates an Empty XML Document object
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createEmptyDocument()
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		Document doc = impl.createDocument(null, null, null);
		return doc;
	}

	/**
	 * 
	 * <B>Purpose:</B> Coverts an XML File to an XML Document Object
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static Document fileToXml(File file) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		InputStream inputStream = new FileInputStream(file);
		org.w3c.dom.Document doc = null;
		try {
			doc = documentBuilderFactory.newDocumentBuilder()
					.parse(inputStream);
		} catch (SAXParseException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * <B>Purpose:</B> Coverts an XML File to an XML Document Object
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static Document fileToXml(InputStream in) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			doc = documentBuilderFactory.newDocumentBuilder().parse(in);
		} catch (SAXParseException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * <B>Purpose:</B> Evaluates an XPath and returns the Node list associated
	 * witht the XPath
	 * 
	 * @param element
	 * @param xpathstring
	 * @return
	 * @throws XPathExpressionException
	 */
	public static NodeList evaluateXPath(Element element, String xpathstring)
			throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xpathstring);
		NodeList result = (NodeList) expr.evaluate(element,
				XPathConstants.NODESET);

		return result;
	}

	/**
	 * 
	 * <B>Purpose:</B> Formats the XML String
	 * 
	 * @param s
	 * @return
	 */
	public static String formatXml(String s) {
		return formatter.format(s, 0);
	}

	/**
	 * 
	 * <B>Purpose:</B> Formats the XML String
	 * 
	 * @param s
	 * @param initialIndent
	 * @return
	 */
	public static String formatXml(String s, int initialIndent) {
		return formatter.format(s, initialIndent);
	}

	/**
	 * 
	 * <B>Purpose:</B>Custom XML Formatter <BR/>
	 * <B>Creation Date:</B> Oct 21, 2011 3:37:23 PM<BR/>
	 */
	private static class XmlFormatter {
		private int indentNumChars;
		private int lineLength;
		private boolean singleLine;

		public XmlFormatter(int indentNumChars, int lineLength) {
			this.indentNumChars = indentNumChars;
			this.lineLength = lineLength;
		}

		public synchronized String format(String s, int initialIndent) {
			int indent = initialIndent;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char currentChar = s.charAt(i);
				if (currentChar == '<') {
					char nextChar = s.charAt(i + 1);
					if (nextChar == '/')
						indent -= indentNumChars;
					if (!singleLine) // Don't indent before closing element if
										// we're creating opening and closing
										// elements on a single line.
						sb.append(buildWhitespace(indent));
					if (nextChar != '?' && nextChar != '!' && nextChar != '/')
						indent += indentNumChars;
					singleLine = false; // Reset flag.
				}
				sb.append(currentChar);
				if (currentChar == '>') {
					if (s.charAt(i - 1) == '/') {
						indent -= indentNumChars;
						sb.append("\n");
					} else {
						int nextStartElementPos = s.indexOf('<', i);
						if (nextStartElementPos > i + 1) {
							String textBetweenElements = s.substring(i + 1,
									nextStartElementPos);

							// If the space between elements is solely newlines,
							// let them through to preserve additional newlines
							// in source document.
							if (textBetweenElements.replaceAll("\n", "")
									.length() == 0) {
								sb.append(textBetweenElements + "\n");
							}
							// Put tags and text on a single line if the text is
							// short.
							else if (textBetweenElements.length() <= lineLength * 0.5) {
								sb.append(textBetweenElements);
								singleLine = true;
							}
							// For larger amounts of text, wrap lines to a
							// maximum line length.
							else {
								sb.append("\n"
										+ lineWrap(textBetweenElements,
												lineLength, indent, null)
										+ "\n");
							}
							i = nextStartElementPos - 1;
						} else {
							sb.append("\n");
						}
					}
				}
			}
			return sb.toString();
		}
	}

	private static String buildWhitespace(int numChars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numChars; i++)
			sb.append(" ");
		return sb.toString();
	}

	/**
	 * Wraps the supplied text to the specified line length.
	 * 
	 * @lineLength the maximum length of each line in the returned string (not
	 *             including indent if specified).
	 * @indent optional number of whitespace characters to prepend to each line
	 *         before the text.
	 * @linePrefix optional string to append to the indent (before the text).
	 * @returns the supplied text wrapped so that no line exceeds the specified
	 *          line length + indent, optionally with indent and prefix applied
	 *          to each line.
	 */
	private static String lineWrap(String s, int lineLength, Integer indent,
			String linePrefix) {
		if (s == null)
			return null;

		StringBuilder sb = new StringBuilder();
		int lineStartPos = 0;
		int lineEndPos;
		boolean firstLine = true;
		while (lineStartPos < s.length()) {
			if (!firstLine)
				sb.append("\n");
			else
				firstLine = false;

			if (lineStartPos + lineLength > s.length())
				lineEndPos = s.length() - 1;
			else {
				lineEndPos = lineStartPos + lineLength - 1;
				while (lineEndPos > lineStartPos
						&& (s.charAt(lineEndPos) != ' ' && s.charAt(lineEndPos) != '\t'))
					lineEndPos--;
			}
			sb.append(buildWhitespace(indent));
			if (linePrefix != null)
				sb.append(linePrefix);

			sb.append(s.substring(lineStartPos, lineEndPos + 1));
			lineStartPos = lineEndPos + 1;
		}
		return sb.toString();
	}

	/**
	 * 
	 * <B>Purpose:</B> Writes the XML Document Object to a File
	 * 
	 * @param doc
	 * @param file
	 * @throws TransformerException
	 */
	public static void writeXmlFile(Node doc, File file)
			throws TransformerException {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(file);
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"2");
		xformer.setOutputProperty(
				"{http://xml.apache.org/xalan}line-separator", "\n");
		xformer.setOutputProperty("standalone", "yes");
		xformer.transform(source, result);
	}

	/**
	 * 
	 * <B>Purpose:</B> XML transformation using XSL
	 * 
	 * @param doc
	 * @param xslInput
	 * @param systemid
	 * @return
	 * @throws TransformerException
	 */
	public static Node transform(Document doc, StreamSource xslInput,
			String systemid) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xslInput);
		DOMResult domResult = new DOMResult();
		DOMSource xmlDomSource = null;
		xmlDomSource = new DOMSource(doc);
		xmlDomSource.setSystemId(systemid);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xalan}line-separator", "\n");
		transformer.transform(xmlDomSource, domResult);
		return domResult.getNode();
	}

	public static Node getChild(Node parent, String name) {
		if (parent == null) {
			return null;
		}
		Node first = parent.getFirstChild();
		if (first == null) {
			return null;
		}

		for (Node node = first; node != null; node = node.getNextSibling()) {
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (name != null && name.equals(node.getNodeName())) {
				return node;
			}
			if (name == null) {
				return node;
			}
		}
		return null;
	}
	// Changes for Warehouse Transfer StoreElf Utility Screen - START
	public static String getXMLString(Node doc) throws TransformerException{
		 DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	}
	public static Element createChildElement(Element parentElement, String strTagName){
		Element eleChildElement = parentElement.getOwnerDocument().createElement(strTagName);
		parentElement.appendChild(eleChildElement);
		return eleChildElement;
	}

	public static Document getDocumentForString(String str)throws IOException,SAXException,ParserConfigurationException{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		return dbFactory.newDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes()));
	}
	public static Element getChildElement(Element parent, String name) {
	    for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
	      if (child instanceof Element && name.equals(child.getNodeName())) {
	        return (Element) child;
	      }
	    }
	    return null;
	}

	public static NodeList getElementsByXpath(Document docInput, String xPath) throws XPathExpressionException{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xPath);
		return ((NodeList) expr.evaluate(docInput, XPathConstants.NODESET));
	}
	public static Document createDocument(String rootElement) throws ParserConfigurationException{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document document = dbFactory.newDocumentBuilder().newDocument();
		document.appendChild(document.createElement(rootElement));
		return document;
	}
	// Changes for Warehouse Transfer StoreElf Utility Screen - END
	
	public static Document transformStringToDoc(String input) throws ParserConfigurationException, Exception{
		ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
		return doc;
	}
}
