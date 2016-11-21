<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>



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
				<jsp:include page="/help_includes/help_sidebar.jsp" />
			</div>
		</aside>
	<!--sidebar end-->

	<!--main content start-->
			<section id="main-content" class="col-md-10 col-md-offset-2">
				<section class="wrapper">
					<!--main content goes here-->

			<% if(StringUtils.isNotBlank(request.getParameter("include"))){ %>
                    <jsp:include page="<%=request.getParameter(\"include\")%>" flush="true"/>
                <% }else{ %>
                      <script type="text/javascript">
							var	help = angular.module('HelpDetailsApp', []);

							function HelpDetailsController($scope, $http, $log){
								$scope.getHelpDetails= function(name) {

									$http({
									    method: 'POST',
									    url: STOREELF_ROOT_URI+'/Help/Help/help_content',
									    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
									}).success(function(data, status) {
										$scope.helpDetails = data;
									});
								};

							}

						</script>
						<%} %>
						<%
						String stylestr = "style=\"display:none\"";
						if (request.getParameter("pageName") == null || request.getParameter("pageName").equals(""))
						{
							stylestr="";
						}
						%>
					 <%
						String path = null;
						String groovy_version = null, web_version = null, report_version = null, line =null;
						path = application.getRealPath("/help_includes/version/");
						BufferedReader br = new BufferedReader(new FileReader(path + "/STOREELF_version.txt"));

						while((line = br.readLine()) != null)
						{
							String[] value = line.split("=");
							if(value[0].equals("groovy_version")){groovy_version = value[1];}
							else if(value[0].equals("web_version")){web_version = value[1];}
							else if(value[0].equals("report_version")){report_version = value[1];}
						}
						br.close();
						%>

						<div <%=stylestr %> style="background-color:#FFFFFF" width="100%">
						<h3> About <b> <font color = "#54a992">STOREELF </font> </b> </h3><br>
						<h6> <b> Owner : <font color = "#60bdaf">Logistics Omni Channel Service Delivery</font></b> </h6>
						<h6> <b> Contact : <font color = "#60bdaf"> IT-OMNIChannel@storeelf.com </font> </b></h6>
						<h6> <b> Version :  <img style="width: 50px; height: 50px;" border="40"
            src="/Help/help_includes/images/mango.jpg" /> (Mango) </b></h6>
						<h6> <b> Purpose : </b>STOREELF is intended to provide a detailed view of the Logistics Fulfillment Network.
						 The application can be used to monitor progress, identify issues, and drive strategic business decisions.
						STOREELF provides views into inventory, fulfillment performance, sourcing distribution,
						 and more by use of detailed dashboards and reports updated frequently throughout the day.</h6>
						<h6> <b> Support : </b> Please contact Kohl's Help Desk for any issues experienced with STOREELF. All incidents should be routed to Logistics Order Management.</h6>
						<br>
						<h5> <b>Build Info </b></h5>
						<h6> <b> Groovy : </b><%=groovy_version%></h6>
						<h6> <b> Web : </b><%=web_version%></h6>
						<h6> <b> Reports : </b><%=report_version%></h6>
						</div>
						</section>
						</section>


  </section>
  <jsp:include page="/common_includes/static/javascripts.jsp" />

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/fuelux/js/spinner.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-fileupload/bootstrap-fileupload.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-wysihtml5/wysihtml5-0.3.0.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-wysihtml5/bootstrap-wysihtml5.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/moment.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/js/jquery.multi-select.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/js/jquery.quicksearch.js"></script>

		<script src="<%=request.getContextPath()%>/public/v3/js/advanced-form-components.js"></script>
</body>
</html>