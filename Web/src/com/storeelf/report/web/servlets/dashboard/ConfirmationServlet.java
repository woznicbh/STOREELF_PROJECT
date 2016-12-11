package com.storeelf.report.web.servlets.dashboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SecurityUtils;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

public class ConfirmationServlet extends StoreElfHttpServlet<Object> {
	private String defaultPage = "/dashboard_includes/confirmation.jsp";
	static final Logger logger = Logger.getLogger(StoreElfComMetricsServlet.class);
	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page the POST must return
	 * json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void confirmation(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/sign_up/confirmation.jsp";

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

			} else {
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
		}
	}
}
