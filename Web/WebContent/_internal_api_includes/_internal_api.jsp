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
				<%-- <jsp:include page="/utility_includes/utility_sidebar.jsp" /> --%>
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
							var	app = angular.module('UtilityApp', []);

						 	function ReportsController($scope, $http, $log, $rootScope){

						 		$scope.renderReportChart = function(group, type) {
						 			$http({
									    method: 'POST',
									    url: STOREELF_ROOT_URI+'/Report/Report/reports',
									    data: (group.length>0) ? "group"+"="+ group : "",
									    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
									}).success(function(data, status) {
										$scope.report_result = data;
										$scope.report_type   = type;
									});
						 		};
							}
						</script>

						<div ng-app="UtilityApp">
							<div ng-controller="UtilityController">

								<div>

									<div class="col-md-12">
										<div class="panel panel-default">
											<div class="panel-heading">
												<h3 class="panel-title">Item search</h3>
											</div>
											<div class="panel-body">
												<form name="form" class="form-horizontal" role="form" ng-submit="searchItems(search)">
													<div class="form-group">
														<label class="col-sm-3 control-label">Item ID</label>
															<div class="col-sm-9">
																<input class="form-control" ng-model="search.items" onclick="" required="TRUE" value="" type="text" ng-init="search.items=''" placeholder="Item ID">
															</div>
													</div><!-- /form-group -->
													<div class="form-group">
													    <div class="col-sm-offset-2 col-sm-10">
													      <button ng-disabled="form.$invalid || isLoading()" class="btn btn-default" type="button" ng-click="searchItems(search)">Search</button>
													    </div>
													  </div>
												</form>
											</div>
										</div>
									</div>

									<!--  #################### order #################### -->

									<div class="col-md-4">
										<div class="panel panel-default">
											<div class="panel-heading">
												<h3 class="panel-title">Order Search</h3>
											</div>
											<div class="panel-body">
												<form name="form" class="form-horizontal" role="form" ng-submit="searchOrders(search)">
													<div class="form-group">
														<label class="col-sm-3 control-label">Order No</label>
													    <div class="col-sm-9">
													      <input class="form-control" ng-model="search.orderNumber" required="TRUE" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No">
													    </div>
													</div>
													<div class="form-group">
														<div class="col-sm-offset-2 col-sm-10">
															<button ng-disabled="form.$invalid || isLoading()" class="btn btn-default" type="button" ng-model="search.button" ng-click="searchOrders(search)">Search</button>
														</div>
													</div>
												</form>
											</div>
										</div>
									</div>

									<div class="col-md-4">
										<div class="panel panel-default">
											<div class="panel-heading">
												<h3 class="panel-title">Order Status Search</h3>
											</div>
											<div class="panel-body">
												<form name="form" class="form-horizontal" role="form" ng-submit="searchOrderStatus(search)">
													<div class="form-group">
														<label class="col-sm-3 control-label">Order No</label>
													    <div class="col-sm-9">
													      <input class="form-control" ng-model="search.orderNumbers" onclick="" required="TRUE" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No">
													    </div>
													</div>
													<div class="form-group">
														<div class="col-sm-offset-2 col-sm-10">
															<button ng-disabled="form.$invalid || isLoading()" class="btn btn-default" type="button" ng-click="searchOrderStatus(search)">Search</button>
														</div>
													</div>
												</form>
											</div>
										</div>
									</div>

									<div class="col-md-4">
										<div class="panel panel-default">
											<div class="panel-heading">
												<h3 class="panel-title">Order Release Search</h3>
											</div>
											<div class="panel-body">
												<form name="form" class="form-horizontal" role="form" ng-submit="searchOrderReleases(search)">
													<div class="form-group">
														<label class="col-sm-3 control-label">Order No</label>
													    <div class="col-sm-9">
													      <input class="form-control" ng-model="search.orderReleaseNumber" onclick="" required="TRUE" value="" ng-init="search.orderReleaseNumber=''" type="text" placeholder="Order No">
													    </div>
													</div>
													<div class="form-group">
														<div class="col-sm-offset-2 col-sm-10">
															<button ng-disabled="form.$invalid || isLoading()" class="btn btn-default" type="button" ng-click="searchOrderReleases(search)">Search</button>
														</div>
													</div>
												</form>
											</div>
										</div>
									</div>

								</div>
							</div>
						</div>

				<% }%>

				</section>
			</section>
		<!--main content end-->

		</section>


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

		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/modules/search.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/modules/enterkey.js"></script>

		<script src="<%=request.getContextPath()%>/public/v3/js/advanced-form-components.js"></script>
	</body>
</html>