<script type="text/javascript">
	var	app = angular.module('DashboardApp', []);

	var pending_data_EFC1 	= [];
	var pending_data_EFC2 	= [];
	var pending_data_EFC3 	= [];
	var pending_data_EFC4 	= [];
	var pending_data_STORE 	= [];
	var pending_data_BOPUS 	= [];
	var pending_data_RDC 	= [];
	var first_hit = 0;

	app.filter('removehtml', function(){
		return function(input){
			return input.replace('<b>', '').replace('</b>', '');
		};
	});


 	function StoreElfComVisualStatisticsController($scope, $http, $log, $rootScope, $interval){


		$scope.StoreElf14DaySalesPerformanceStatisticsChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/sales_metrics_visuals',
			    data: "chart=14_day_sales_performance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;
				$scope.sales_data_all 		= data.all.sales_data;

			});
		};

		$scope.StoreElf14DaySalesBreakdownChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/sales_metrics_visuals',
			    data: "chart=14_day_sales_breakdown",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;
				$scope.sales_data_breakdown 		= data.all;

			});
		};





	}



</script>
<script>
  $(function() {
    $( document ).tooltip();
  });
  </script>
  <style>
  label {
    display: inline-block;
    width: 5em;
  }
  </style>


<div ng-app="DashboardApp">
	<div ng-controller="StoreElfComVisualStatisticsController">


		<div >
		<table ng-init="StoreElf14DaySalesPerformanceStatisticsChartData()" class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Order Date</th>
						<th>Active</th>
						<th>Shipped</th>
						<th>Cancelled</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="day in sales_data_all">
						<td>{{day.ORDER_DATE}}</td>
						<td>{{day.ACTIVE_DOLLARS}}</td>
						<td>{{day.SHIPPED_DOLLARS}}</td>
						<td>{{day.CANCEL_ORDER_DOLLARS}}</td>
					</tr>
				</tbody>
			</table>

		</div>

		<div >
		<table ng-init="StoreElf14DaySalesBreakdownChartData()" class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Order Date</th>
						<th>Node Type</th>
						<th>Ship Node</th>
						<th>Active</th>
						<th>Shipped</th>
						<th>Cancelled</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="day in sales_data_breakdown">
						<td>{{day.ORDER_DATE}}</td>
						<td>{{day.NODE_TYPE}}</td>
						<td>{{day.SHIP_NODE}}</td>
						<td>{{day.ACTIVE_DOLLARS}}</td>
						<td>{{day.SHIPPED_DOLLARS}}</td>
						<td>{{day.CANCEL_ORDER_DOLLARS}}</td>
					</tr>
				</tbody>
			</table>

		</div>




	</div>
</div>

<script src="<%=request.getContextPath()%>/public/v3/js/morris.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/raphael-min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/chart_generator.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/jquery.animateNumber.js" type="text/javascript"></script>
<link href="<%=request.getContextPath()%>/public/v3/css/morris.css" rel="stylesheet" />


