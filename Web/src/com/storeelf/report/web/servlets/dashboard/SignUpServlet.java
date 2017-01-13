package com.storeelf.report.web.servlets.dashboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.SecurityUtils;
import com.storeelf.util.StripeUtils;
import com.stripe.model.Card;
import com.stripe.model.Customer;

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
				String stripeToken=request.getParameter("stripeToken");
				String token_type=request.getParameter("stripeTokenType");
				String firstName = request.getParameter("firstName");
				String lastName = request.getParameter("lastName");;
				String email = request.getParameter("stripeEmail");
				String address = request.getParameter("address");
				String city = request.getParameter("city");
				String state = request.getParameter("state");
				String zip = request.getParameter("zip");
				String username = request.getParameter("username");
				String fbToken = request.getParameter("facebookToken");
				
				Object salt = SecurityUtils.returnSalt();
				String saltedPass = SecurityUtils.returnSaltedPassword(request.getParameter("password"), salt);
				
				Customer customer = null;
				Card cc = null;
				String last4 = null;
				String cardType = null;
				String stripeId = null;
				
				try {
					customer = StripeUtils.createStripeCustomer(stripeToken, email);
					cc = StripeUtils.addAndReturnCC(stripeToken, customer);

					last4 = cc.getLast4();
					cardType = cc.getBrand();
					stripeId = customer.getId();

					StripeUtils.addSubscription(stripeId);
				} catch (Exception e){
					logger.error("Unable to create Stripe Customer or Subscription for email : "+email);
					logger.error("Stripe Error", e);
					
					responseWriter = response.getWriter();
					responseWriter.write("Error");
					responseWriter.flush();
					responseWriter.close();
				}
				
				// TODO add user to the db;
				SQLUtils.insertUpdateMySql("Insert into se_user (username,password, first_name, last_name, createts, modifyts, salt, "
				 		+ "email_address, address, city, state, zip, facebook_token, stripe_cust_id, cc_last4, cc_type, inactive_reason)"
				 		+ "Values('"+username+"','"+saltedPass+"','"+firstName+"','"+lastName+"',NOW(),NOW(),'"+salt+"',"
				 				+ "'"+email+"','"+address+"','"+city+"','"+state+"','"+zip+"','"+fbToken+"','"+stripeToken+"','"+last4+"','"+cardType+"'"
				 						+ ",'Pending Email Confirmation')");
				// SQLUtils.insertUpdateMySql(Insert sql for creating se_user_group_list record tied to se_user table)

				String encryptUsername = Base64.getUrlEncoder().encodeToString(
						SecurityUtils.symmetricEncrypt(username, StoreElfConstants.STOREELF_CERT_KEY).getBytes());

				// TODO send sign up confirmation email with encoded/encrypted
				// username as input parameter

				
				//doesn't change url currently, can use redirect function if thats what we want.
				
				/*Ben - Can we create a simple "please check your email for confirmation" page and forward there? 
				I think that makes the most sense. Agree?*/
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
					
					//this should forward to the Sign in page when username has been validated through email
					request.getRequestDispatcher(defaultPage + "?include=" + "sign_in_page").forward(request, response);

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
