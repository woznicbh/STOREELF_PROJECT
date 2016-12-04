package com.storeelf.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.MessageFactoryImpl;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;

import com.google.common.reflect.ClassPath;
import com.storeelf.util.exception.StoreElfException;

/**
 * @author Pavan Andhukuri
 */

public class WSUtils {
	static final Logger			logger				= Logger.getLogger(WSUtils.class);
	
	public static Class[] getClasses(String packageName) {
		ArrayList<Class> classes = new ArrayList<Class>();
	    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    try {
	        for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if(info.getName().startsWith(packageName)){
                	classes.add( Class.forName(info.getName()) );
                }
	        }
	    }
	    catch (IOException e) {e.printStackTrace();}
	    catch (ClassNotFoundException e) { e.printStackTrace();}
	    return classes.toArray(new Class[classes.size()]);
	}

	public static String callWS(String msgstr, String url)
			throws StoreElfException {
		logger.debug("Input message:\n"+msgstr);
		logger.debug("URL:"+url);
		String str = "";
		try {
			byte[] reqBytes = msgstr.getBytes();
			ByteArrayInputStream bis = new ByteArrayInputStream(reqBytes);
			StreamSource ss = new StreamSource(bis);

			MessageFactoryImpl messageFactory = new MessageFactoryImpl();
			SOAPMessage msg = messageFactory.createMessage();
			SOAPPart soapPart = msg.getSOAPPart();

			soapPart.setContent(ss);

			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);

			SOAPEnvelope resp = call
					.invoke(((org.apache.axis.SOAPPart) soapPart)
							.getAsSOAPEnvelope());
			str = resp.toString();
			logger.debug("Response:\n"+str);
			
		} catch (AxisFault ex) {
			throw new StoreElfException(ex);
		} catch (ServiceException ex) {
			throw new StoreElfException(ex);
		} catch (SOAPException ex) {
			throw new StoreElfException(ex);
		}

		return str;

	}
	
	/**
	 * Calls Webservice.
	 * 
	 * @param targetLink
	 *            the url for the WS
	 * @param soapXMLMsg
	 *            the request msg for the WS
	 * @return the String[status,response] of the WS
	 * 
	 * @throws ConnectTimeoutException 
	 * 			Webservice connection has timed out
	 * @throws HttpException 
	 * 			Webservice Unable to connect
	 * @throws IOException 
	 * 			catch remaining IO Exceptions
	 * @throws Exception 
	 * 			catch remaining Exceptions
	 */
	public static String[] CallWebService(String targetLink, String soapXMLMsg) {

		PostMethod httpPost;
		HttpClient httpclient = new HttpClient();
		int status = 0;
		String response = null;

		httpPost = new PostMethod(targetLink);
		httpPost.addRequestHeader("Content-Type", "text/xml; charset=utf-8");
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
	
}