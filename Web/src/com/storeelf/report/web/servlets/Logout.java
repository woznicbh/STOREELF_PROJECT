package com.storeelf.report.web.servlets;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;

import com.storeelf.report.web.security.StoreElfJndiLdapRealm;

/**
 * Servlet implementation class Login
 */
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static final Logger logger = Logger.getLogger(Logout.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logout() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String loginpage = getInitParameter("loginpage");
			Subject currentUser = SecurityUtils.getSubject();
			currentUser.logout();
			
			Collection<Realm> realms = ((RealmSecurityManager)SecurityUtils.getSecurityManager()).getRealms();
			for(Realm realm:realms){
				if(realm instanceof StoreElfJndiLdapRealm){
					((StoreElfJndiLdapRealm)realm).clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
					((StoreElfJndiLdapRealm)realm).clearCachedAuthenticationInfo(SecurityUtils.getSubject().getPrincipals());
				}
			}			
			response.sendRedirect(loginpage);
		} catch (AuthenticationException e) {
			logger.error("Error:" + e.getMessage());
			request.setAttribute("simpleShiroApplicationLoginFailure",
					e.getMessage());
			e.printStackTrace();
		}
	}

}
