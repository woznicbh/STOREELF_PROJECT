<%@page import="com.storeelf.report.web.Constants"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%
	String section = getServletContext().getContextPath().substring(1).toLowerCase();

	//if(SecurityUtils.getSubject().isAuthenticated()){
	//	if(Constants.STOREELF_SECURITY_ENABLED==true){
	//		if(SecurityUtils.getSubject().isPermitted(section+":"+"read:*:*")){
	//			//request.getRequestDispatcher("/"+section+"_includes/"+section+".jsp" + "?include=" + "/dashboard_includes/storeelf_com_metrics/quick_metrics_visuals.jsp").forward(request, response);
	//			request.getRequestDispatcher("/"+section+"_includes/"+section+".jsp").forward(request, response);
	//		}else{
	//			request.getRequestDispatcher("//access-denied.jsp").forward(request, response);
	//		}
	//	}else{
			request.getRequestDispatcher("/"+section+"_includes/"+section+".jsp").forward(request, response);
	//	}
	//}else{
	//	request.getRequestDispatcher("/login.jsp").forward(request, response);
	//} 
%>