<%@page import="org.apache.shiro.SecurityUtils"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content=""/>
    <meta name="author" content="TKMAGH4"/>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.ico">

    <title>Access Denied</title>

    <!-- Bootstrap core CSS -->
    <link href="<%=request.getContextPath()%>/public/v3/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/public/v3/css/bootstrap-reset.css" rel="stylesheet">
    <!--external css-->
    <link href="<%=request.getContextPath()%>/public/v3/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
    <!-- Custom styles for this template -->
    <link href="<%=request.getContextPath()%>/public/v3/css/style.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/public/v3/css/style-responsive.css" rel="stylesheet" />

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 tooltipss and media queries -->
    <!--[if lt IE 9]>
    <script src="<%=request.getContextPath()%>/public/v3/js/html5shiv.js"></script>
    <script src="<%=request.getContextPath()%>/public/v3/js/respond.min.js"></script>
    <![endif]-->

        <script>
    	//consolefix for IE
    	if ( ! window.console ) console = { log: function(){} };
    </script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/jquery-migrate-1.2.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/angular-1.2.18.min.js"></script>
	<!-- <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.18/angular-route.js"></script> -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/angular-1.2.18-sanitize.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/angular-1.2.18-animate.min.js"></script>

	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/bootstrap-hover-dropdown.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/jquery.gritter.min.js"></script>
    <%-- RETIRED --%>
	<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/STOREELF.js"></script> --%>

	<jsp:include page="/common_includes/static/javascript_globals.jsp"/>

<script>

//condition to ignore local dev sessions
$(document).ready(function () {
	if(window.location.href.indexOf("localhost") < 0 || window.location.href.indexOf("qsms") < 0 || window.location.href.indexOf("tsms") < 0){

		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
			  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
			  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
			  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

			  ga('create', 'UA-47775537-1', 'auto');
			  ga('require', 'displayfeatures');
			  ga('send', 'pageview');
	}
});


</script>
<link href="<%=request.getContextPath()%>/public/v3/assets/morris.js-0.4.3/morris.css" rel="stylesheet"/>
<script type='text/javascript' src='https://www.google.com/jsapi'></script>
</head>

  <body class="body-denied">

    <div class="container">
    		<!--header start-->
		<header class="header white-bg">
			<jsp:include page="/common_includes/static/header.jsp"/>
		</header>
		<!--header end-->

      <section class="error-wrapper">
          <i class="icon-500"></i>
          <h1>Access Denied</h1>
          <h2>Grrr :-p</h2>
          <p class="page-denied">Looks like user '<%=SecurityUtils.getSubject().getPrincipal()%>' lacks permission to access this page. <a href="javascript:history.back()">Click here to Go back</a></p>
      </section>

    </div>

<jsp:include page="/common_includes/static/javascripts.jsp" />

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/fuelux/js/spinner.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/moment.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker.js"></script>

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/js/jquery.multi-select.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/js/jquery.quicksearch.js"></script>

		<script type=	"text/javascript" src="<%=request.getContextPath()%>/public/v3/js/modules/search.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/modules/enterkey.js"></script>

		<script src="<%=request.getContextPath()%>/public/v3/js/advanced-form-components.js"></script>
  </body>
</html>
