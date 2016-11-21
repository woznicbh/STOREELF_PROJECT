/**
 * 
 */
package com.storeelf.util.model.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.storeelf.util.WSUtils;
import com.storeelf.util.XMLUtils;
import com.storeelf.util.exception.StoreElfException;
import com.storeelf.util.model.SCAPI;
import com.storeelf.util.model.SCEnv;

/**
 * @author tkmawh6
 * 
 */
public class SCWSAPI implements SCAPI {
	private static final Logger log = LoggerFactory.getLogger(SCWSAPI.class);

	private static final long serialVersionUID = -8508633923750820476L;
	private static final int TYPE = 1;
	private String name;
	private boolean isFlow;
	private Document template;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#getType()
	 */
	@Override
	public int getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#execute(org.w3c.dom.Document,
	 * com.storeelf.util.model.SCEnv)
	 */
	@Override
	public Document execute(Document indoc, SCEnv env) throws StoreElfException {
		Document outdocfinal = null;
		try {
			Document outdoc = null;
			log.debug("Input Document:\n" + XMLUtils.xmlToString(indoc));
			Document doc = XMLUtils.createEmptyDocument();
			Element multiApi = doc.createElement("MultiApi");
			Element api = doc.createElement("API");
			if (this.isFlow) {
				api.setAttribute("FlowName", this.getName());
			} else {
				api.setAttribute("Name", this.getName());
			}
			Element input = doc.createElement("Input");
			input.appendChild(doc.adoptNode(indoc.getDocumentElement()
					.cloneNode(true)));
			api.appendChild(input);
			if (this.template != null) {
				Element template = doc.createElement("Template");
				template.appendChild(doc.adoptNode(this.template
						.getDocumentElement().cloneNode(true)));
				api.appendChild(template);
			}
			multiApi.appendChild(api);
			doc.appendChild(multiApi);
			log.debug("MultiAPI input:\n"
					+ XMLUtils.xmlToString(doc.getDocumentElement()));
			String inputString = StringEscapeUtils.escapeXml(XMLUtils
					.xmlToString(doc.getDocumentElement()));
			String soapmsg = this.generateSoapXML(inputString, env);
			log.debug("Generated Soap Message:\n" + soapmsg);
			String resp = WSUtils.callWS(soapmsg, env.getUrl());
			outdoc = XMLUtils.parse(resp);
			log.debug("Response:\n" + resp);
			log.debug("Response Document:\n" + XMLUtils.xmlToString(outdoc));
			String apioutput = outdoc.getElementsByTagName("result").item(0)
					.getTextContent();
			apioutput = StringEscapeUtils.unescapeXml(apioutput);
			log.debug("Multi APi output:" + apioutput);
			Document outdoctmp = XMLUtils.parse(apioutput);
			Element outnd = ((Element) XMLUtils.evaluateXPath(
					outdoctmp.getDocumentElement(), "/MultiApi/API/Output")
					.item(0));
			log.debug("Output Node:"
					+ XMLUtils.xmlToString(XMLUtils.getChild(outnd, null)));
			outdocfinal = XMLUtils.createEmptyDocument();
			outdocfinal.appendChild(outdocfinal.adoptNode(XMLUtils.getChild(
					outnd, null)));
			log.debug(XMLUtils.xmlToString(outdocfinal));
		} catch (ParserConfigurationException e) {
			throw new StoreElfException(e);
		} catch (TransformerException e) {
			throw new StoreElfException(e);
		} catch (SAXException e) {
			throw new StoreElfException(e);
		} catch (IOException e) {
			throw new StoreElfException(e);
		} catch (XPathExpressionException e) {
			throw new StoreElfException(e);
		}

		return outdocfinal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#setTemplate(org.w3c.dom.Document)
	 */
	@Override
	public void setTemplate(Document template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#getTemplate()
	 */
	@Override
	public Document getTemplate() {
		return this.template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#isFlow()
	 */
	@Override
	public boolean isFlow() {
		return this.isFlow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.storeelf.util.model.SCAPI#setFlow(boolean isFlow)
	 */
	@Override
	public void setFlow(boolean isFlow) {
		this.isFlow = isFlow;
	}

	private String generateSoapXML(String input, SCEnv env) {
		String str = "<soapenv:Envelope xmlns:soapenv="
				+ "\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:typ=\"http://yantra.com/yantrawebservices/types\">"
				+ "   <soapenv:Header/>  <soapenv:Body>      <typ:multiApi>"
				+ "         <String_1>&lt;Environment userId=&quot;";
		str = str + env.getUserId();
		str = str
				+ "&quot; progId=&quot;"
				+ env.getProgId()
				+ "&quot;/&gt;</String_1>        <String_2>"
				+ input
				+ "</String_2>      </typ:multiApi>   </soapenv:Body></soapenv:Envelope>";
		return str;
	}
}
