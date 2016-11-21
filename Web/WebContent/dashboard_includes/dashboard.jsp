<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">

	<jsp:include page="/common_includes/static/head.jsp"/>

	<body>
	
	<section id="container" >
		<!--header start-->
		<header class="header white-bg">
			<jsp:include page="/common_includes/static/header.jsp"/>
		</header>			
		<!--header end-->
		
		<!--sidebar start-->
		<aside class="col-md-2 col-xs-12" style="padding-left: 0px;">
			<div id="sidebar"  class="nav-collapse ">
				<jsp:include page="/dashboard_includes/dashboard_sidebar.jsp" />	
			</div>
		</aside>
		<!--sidebar end-->

		<!--main content start-->
			<section id="main-content" class="col-md-10 col-md-offset-2">
				<section class="wrapper">
					<!--main content goes here-->
					
						<%-- if the 'include' parameter is NOT provided, load content from the '/dashboard_includes/storeelf_com_metrics/quick_metrics.jsp' --%>											
						<% if(StringUtils.isNotBlank(request.getParameter("include"))){ %>			
							<jsp:include page="<%=request.getParameter(\"include\")%>" flush="true"/>
						<% }else{ %>
							<script type="text/javascript">
							<!--
							window.location = "/Dashboard/StoreElfComMetrics/quick_metrics_visuals"
							//-->
							</script>
						<% } %>
					
				</section>
			</section>
		<!--main content end-->
				
		</section>
		<jsp:include page="/common_includes/static/javascripts.jsp" />
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/jquery.sparkline.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/Chart.min.js"></script>
		<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/chart-master/Chart.js"></script> --%>
	</body>
</html>