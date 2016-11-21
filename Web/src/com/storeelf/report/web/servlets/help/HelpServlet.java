package com.storeelf.report.web.servlets.help;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.storeelf.report.web.servlets.StoreElfHttpServlet;

public class HelpServlet extends StoreElfHttpServlet<Object> {
	
	static final Logger			logger				= Logger.getLogger(HelpServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/help_includes/help.jsp";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelpServlet() {
        super();
    }
    
public void help_content(String requestedPage, HttpServletRequest request, HttpServletResponse response){
    	
		String			pageName	= null;
		String			jsp_include_page	= "/help_includes/help_content.jsp";
			
		try{
				pageName = request.getParameter("pageName");
				response.setContentType("application/json");
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page+"&pageName="+ pageName).forward(request, response);
		}catch (Exception e) {
			logger.error("error", e);
		}

 }
}
