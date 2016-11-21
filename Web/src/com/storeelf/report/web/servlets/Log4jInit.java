package com.storeelf.report.web.servlets;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;

/**
 * Servlet implementation class Log4jInit
 */
public class Log4jInit extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		String prefix = getServletContext().getRealPath("/");
		String file = getInitParameter("log4j-init-file");

		if (file != null) {
			PropertyConfigurator.configure(prefix +File.separatorChar+ file);
			System.out.println("Log4J Logging started: " + prefix + file);
		} else {
			System.out.println("Log4J Is not configured for your Application: "
					+ prefix + file);
		}
		
		
	}
	

}
