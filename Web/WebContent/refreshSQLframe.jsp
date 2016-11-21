<%@page import="com.storeelf.report.web.Constants"%>
<%@page import="com.storeelf.util.StringUtils"%>
<%
	String sqlidval = request.getParameter("sqlid");
	String sql = null;
					
	if(!StringUtils.isVoid(sqlidval)){
		sql = Constants.SQL_MAP.get(sqlidval);
		%>
		<%=sql%>
		<%
	}
%>