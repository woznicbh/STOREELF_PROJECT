package com.storeelf.report.web.servlets.security;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;

import com.storeelf.report.web.servlets.StoreElfHttpServlet;

public class AuthenticateServlet extends StoreElfHttpServlet<Object>{


	static final Logger			logger				= Logger.getLogger(AuthenticateServlet.class);
	private static final long	serialVersionUID	= 1L;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public AuthenticateServlet() {
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
	 */
	public void logout(String requestedPage, HttpServletRequest request, HttpServletResponse response){
		try{
			logger.debug(requestedPage + "|" + request.getParameter("parameterValue"));
			try {
				Subject		currentUser = SecurityUtils.getSubject();
							currentUser.logout();
				response.sendRedirect(request.getContextPath() + "/");

			} catch (AuthenticationException e) {
				logger.error("Error:" + e.getMessage());
				request.setAttribute("simpleShiroApplicationLoginFailure", e.getMessage());
			}
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
	}

}
