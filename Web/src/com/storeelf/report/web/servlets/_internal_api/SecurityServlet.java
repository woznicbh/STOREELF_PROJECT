package com.storeelf.report.web.servlets._internal_api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SecurityUtils;

public class SecurityServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(SQLServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/_internal_api_includes/_internal_api.jsp";
	
	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void generate_new_key(String requestedPage, HttpServletRequest request, HttpServletResponse response){ 
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/_internal_api_includes/securiy/generate_new_key.jsp";

		try{
			if( "GET".equals(request.getMethod()) ){
				//request_uri	= request.getParameter("request_uri");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				try{
					content = SecurityUtils.generateBase64Key(128);
					//content = SecurityUtils.generateKey("").toString();
					logger.debug("new cert key requested:"+content);
				}catch(Exception e){
					logger.error("1",e);
				}

				//finally prep the rootReturn object to be sent to the page
				//content = gson.toJson(rootReturn);

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				//responseWriter.write("error, use POST instead");
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
	
	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void encrypt_value(String requestedPage, HttpServletRequest request, HttpServletResponse response){ 
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/_internal_api_includes/securiy/generate_new_key.jsp";
		String			key						= "";
		String			input_text				= "";
		
		try{
			if( "GET".equals(request.getMethod()) ){
				key			= request.getParameter("key");
				input_text	= request.getParameter("input_text");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				try{
					logger.debug("encrypting: "+input_text);
					key = (StringUtils.isNotBlank(key)) ? key : Constants.STOREELF_CERT_KEY;
					logger.debug("using key: "+key);
					content = SecurityUtils.symmetricEncrypt(input_text, key);					
					
				}catch(Exception e){
					logger.error("error encrypting value provided",e);
				}

				//finally prep the rootReturn object to be sent to the page
				//content = gson.toJson(rootReturn);

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				//responseWriter.write("error, use POST instead");
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
	
	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void decrypt_value(String requestedPage, HttpServletRequest request, HttpServletResponse response){ 
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/_internal_api_includes/securiy/generate_new_key.jsp";
		String			key						= "";
		String			input_text				= "";
		
		try{
			if( "GET".equals(request.getMethod()) ){
				key			= request.getParameter("key");
				input_text	= request.getParameter("input_text");
				
				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				try{
					key = (StringUtils.isNotBlank(key)) ? key : Constants.STOREELF_CERT_KEY;					
					content = SecurityUtils.symmetricDecrypt(input_text, key);
					
					logger.debug("new cert key requested:"+content);
				}catch(Exception e){
					logger.error("error encrypting value provided",e);
				}

				//finally prep the rootReturn object to be sent to the page
				//content = gson.toJson(rootReturn);

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				//request.getRequestDispatcher(jsp_page).include(request, response);
				//responseWriter.write("error, use POST instead");
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
}
