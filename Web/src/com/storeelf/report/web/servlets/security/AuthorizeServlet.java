package com.storeelf.report.web.servlets.security;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.storeelf.report.web.servlets.StoreElfHttpServlet;

public class AuthorizeServlet extends StoreElfHttpServlet<Object>{

	static final Logger			logger				= Logger.getLogger(AuthorizeServlet.class);
	private static final long	serialVersionUID	= 1L;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public AuthorizeServlet() {
        super();
    }
    
    

}
