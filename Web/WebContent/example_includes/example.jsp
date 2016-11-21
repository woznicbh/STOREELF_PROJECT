<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

	<jsp:include page="/common_includes/static/header.jsp"/>

	<body>
<%-- 		<jsp:include page="/common_includes/static/top_menu.jsp"/> --%>
		
		<div class="container-fluid">
			<div class="col-md-2 panel-group" id="accordion">
				<jsp:include page="/example_includes/example_sidebar.jsp" />
			</div>
			<div class="col-md-10">
				<% if(StringUtils.isNotBlank(request.getParameter("include"))){ %>			
					<jsp:include page="<%=request.getParameter(\"include\")%>" flush="true"/>
				<% } else {%>
					<script type="text/javascript">
						var	app = angular.module('ReportsApp', []);
						
					 	function ReportsController($scope, $http, $log, $rootScope){ 
					 		
					 		$scope.renderReportChart = function(group, type) {				 			
					 			$http({
								    method: 'POST',
								    url: STOREELF_ROOT_URI+'/WebApp/ExampleComponentServlet/example',
								    data: "paramOne=" + parameter.fieldName,
								    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
								}).success(function(data, status) {
									$scope.result   = data;
								});				 	
					 		};				 			
						} 	
					</script>
					
					<div class="jumbotron">
                       <h2>View Example</h2>                       
                   </div>
					
					<div ng-app="ExampleComponentApp">
						<div ng-controller="ExampleModuleController">		
							
							<div>
							
							
								
								<div class="col-md-6">
									<table class="row table table-hover table-condensed table-bordered table-responsive">
										<thead>
											<tr>
												<th>Column Name #1</th>
												<th>Column Name #2</th>
												<th>Column Name #3</th>
												<th>Column Name #4</th>
											</tr>
										</thead>
										<!-- 
											The ngRepeat directive instantiates a template once per item from a collection.
																
											@see http://docs.angularjs.org/api/ng/directive/ngRepeat
										-->
										<tbody>
											<tr ng-repeat="result in results">
												<td>{{result.FIELD_NAME_1}}</td>
												<td>{{result.FIELD_NAME_2}}</td>
												<td>{{result.FIELD_NAME_3}}</td>
												<td>{{result.FIELD_NAME_4}}</td>
											</tr>
										</tbody>
									</table>
								</div>	
								
								<div class="col-md-6 float-right">	
									<table class="row table table-hover table-condensed table-bordered table-responsive">
										<thead>
											<tr>
												<th>Column Name #1</th>
												<th>Column Name #2</th>
												<th>Column Name #3</th>
												<th>Column Name #4</th>
											</tr>
										</thead>
										<!-- 
											The ngRepeat directive instantiates a template once per item from a collection.
																
											@see http://docs.angularjs.org/api/ng/directive/ngRepeat
										-->
										<tbody>
											<tr ng-repeat="result in results">
												<td>{{result.FIELD_NAME_1}}</td>
												<td>{{result.FIELD_NAME_2}}</td>
												<td>{{result.FIELD_NAME_3}}</td>
												<td>{{result.FIELD_NAME_4}}</td>
											</tr>
										</tbody>
									</table>
								</div>
								
							</div>
							
						</div>
						
					</div>
				
				<% } %>
			</div>
			
		</div>		
		
		<%-- <%@ include file="/common_includes/static/footer.html" %> --%>
	</body>
</html>