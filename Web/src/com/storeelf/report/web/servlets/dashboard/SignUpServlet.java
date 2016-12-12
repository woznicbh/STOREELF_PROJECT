package com.storeelf.report.web.servlets.dashboard;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.impl.GenericCountModel;
import com.storeelf.report.web.model.impl.GenericTabularModel;
import com.storeelf.report.web.model.impl.MultiColumnModal;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.SecurityUtils;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Plan;
import com.stripe.model.Subscription;

/**
 * Servlet implementation class OrderManagementServlet
 *
 * each *Response method MUST have the following types as arguments in this
 * order: ExampleResponse(String page, HttpServletRequest rq,
 * HttpServletResponse rs)
 *
 * @author tkmagh4
 * @web.servlet name=OrderManagementServlet
 */
public class SignUpServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger.getLogger(SignUpServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/dashboard_includes/dashboard.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignUpServlet() {
		super();
		
	}

    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config); //added this line then it worked
    	
    	
    }

    

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page the POST must return
	 * json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void sign_up(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/sign_up/sign_up.jsp";
		String confirmation_page = "/dashboard_includes/sign_up/confirmation.jsp";
		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {
				String token=request.getParameter("stripeToken");
				String token_type=request.getParameter("stripeTokenType");
				String firstName = "";
				String lastName = "";
				String email = request.getParameter("stripeEmail");
				String city = "";
				String state = "";
				String zip = "";
				String username = "";
				String password = SecurityUtils.symmetricEncrypt("", StoreElfConstants.STOREELF_CERT_KEY);

				Stripe.apiKey = StoreElfConstants.STRIPE_TEST_KEY;

				Map<String, Object> custParams = new HashMap<String, Object>();
				custParams.put("email", email);

				Customer customer = Customer.create(custParams);

				String stripeId = customer.getId();

				Map<String, Object> subParams = new HashMap<String, Object>();
				subParams.put("customer", stripeId);
				subParams.put("plan", "StoreElf_Monthly");
				subParams.put("tax_percent", 5.05);
				subParams.put("trial_period_days", 30);

				Subscription.create(subParams);

				// TODO add user to the db;
				// SQLUtils.insertUpdateMySql(Insert sql for creating user in
				// se_user table and setting the account as
				// inactive with a reason of "Pending Email Confirmation")
				// SQLUtils.insertUpdateMySql(Insert sql for creating se_user_group_list record tied to se_user table)

				String encryptUsername = Base64.getUrlEncoder().encodeToString(
						SecurityUtils.symmetricEncrypt(username, StoreElfConstants.STOREELF_CERT_KEY).getBytes());

				// TODO send sign up confirmation email with encoded/encrypted
				// username as input parameter

//				responseWriter = response.getWriter();
//				responseWriter.write("Success");
//				responseWriter.flush();
//				responseWriter.close();
				
				//doesn't change url currently, can use redirect function if thats what we want.
				request.getRequestDispatcher(defaultPage + "?include=" + confirmation_page).forward(request, response);

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

	public void email_validation(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		String chart_type = null;
		String response_content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/dashboard_includes/sign_up/sign_up.jsp";

		try {
			if (StringUtils.equals(request.getMethod(), "POST")) {

				String username = SecurityUtils.symmetricDecrypt(
						new String(Base64.getDecoder().decode("".getBytes()), "utf-8"),
						StoreElfConstants.STOREELF_CERT_KEY);

				Connection conRO = ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);

				ConcurrentHashMap<Integer, HashMap<String, Object>> usernameHM = SQLUtils.getSQLResult(
						"Select username from se_user where active='N' and inactive_reason='Pending Email Confirmation' and username='"
								+ username + "'",
						conRO);

				if (!usernameHM.isEmpty()) {
					SQLUtils.insertUpdateMySql(
							"update se_user set active='Y' and inactive_reason='' where username='" + username + "'");
					responseWriter = response.getWriter();
					responseWriter.write("Success");
					responseWriter.flush();
					responseWriter.close();

				} else {
					responseWriter = response.getWriter();
					responseWriter.write("Error");
					responseWriter.flush();
					responseWriter.close();

				}

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
