<script src="<%=request.getContextPath()%>/public/v3/js/morris.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/raphael-min.js" type="text/javascript"></script>
 <link href="<%=request.getContextPath()%>/public/v3/css/morris.css" rel="stylesheet" />
<script type="text/javascript">
	var	app = angular.module('DashboardApp', []);
	var initial_load = 0;

	app.filter('removehtml', function(){
		return function(input){
			return input.replace('<b>', '').replace('</b>', '');
		};
	});


 	function SalesController($scope, $http, $log, $rootScope, $interval){



		$scope.MP14DaySalesChartData = function() {
			$http({
			    method: 'POST',
			    url: '<%=request.getContextPath()%>//MarketPlaceMetrics/sales_metrics',
			    data: "chart=14daysales",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MP14DaySalesResults = data.mp_14_day_sales_data;
				$scope.MP14DaySalesResults_last_run_timestamp = data.last_run_timestamp;

			});
		};

		$scope.MP6MonthSalesChartData = function() {
			$http({
			    method: 'POST',
			    url: '<%=request.getContextPath()%>//MarketPlaceMetrics/sales_metrics',
			    data: "chart=6monthsales",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MP6MonthSalesResults = data.mp_6_month_sales_data;
				$scope.MP6MonthSalesResults_last_run_timestamp = data.last_run_timestamp;

			});
		};

		$scope.refresh = function(){
			if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
				$scope.MP14DaySalesChartData();
				$scope.MP6MonthSalesChartData();
				console.log('reloading dashboard data');
			}
		};

		$scope.refresh();
		$interval($scope.refresh, 30000);
}
</script>


<div ng-app="DashboardApp">
	<div ng-controller="SalesController">
		<div>
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-md-6" id="leftCol">
			<section class="panel">
			<div class="revenue-head">
		                  <span>
		                      <i class="fa fa-bar-chart-o"></i>
		                  </span>
		                  <h3>14 Day Sales Statistics</h3>
		                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MP14DaySalesResults_last_run_timestamp}})"></span>
		              </div>
	              <div class="panel-body" id="dashPadding">
	              <div class="row">
				<table class="table table-hover table-striped table-condensed" description="14 day sales" data-ng-init="MP14DaySalesChartData()">
					<thead>
					<tr>
						<th>Date</th>
						<th>Total Orders</th>
						<th>Total Units</th>
						<th>Total Sales Revenue</th>
						<th>Total Commission</th>
					</thead>
					<tbody>
						<tr ng-repeat="mp_14_day_sales_data in MP14DaySalesResults | orderBy: 'ROWNUM'">
							<td>{{mp_14_day_sales_data.ORDER_DATE}}</td>
							<td>{{mp_14_day_sales_data.ORDER_COUNT}}</td>
							<td>{{mp_14_day_sales_data.TOTAL_UNITS}}</td>
							<td>{{mp_14_day_sales_data.REVENUE}}</td>
							<td>{{mp_14_day_sales_data.COMMISSION}}</td>
						</tr>
					</tbody>
				</table>
				</div>
				</div>
			</section>
			</div>

<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [RIGHT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->

			<div class="col-md-6">
				<section class="panel">
                        <div class="revenue-head">
                            <span>
                                <i class="fa fa-bar-chart-o"></i>
                            </span>
                            <h3>6 Month Sales Statistics</h3>
                            <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MP6MonthSalesResults_last_run_timestamp}})">

                            </span>
                        </div>
                        <div class="panel-body" id="dashPadding">
                        <div class="row">
                            	<table class="table table-hover table-striped table-condensed" data-ng-init="MP6MonthSalesChartData()">
							<thead>
								<tr>
									<th>Month</th>
									<th>Total Orders</th>
									<th>Total Units</th>
									<th>Total Sales Revenue</th>
									<th>Total Commission</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="mp_6_month_sales_data in MP6MonthSalesResults | orderBy: 'ROWNUM'">
									<td> {{mp_6_month_sales_data.SALES_MONTH}}</td>
									<td> {{mp_6_month_sales_data.TOTAL_ORDERS}}</td>
									<td> {{mp_6_month_sales_data.TOTAL_UNITS}}</td>
									<td> {{mp_6_month_sales_data.REVENUE}}</td>
									<td> {{mp_6_month_sales_data.COMMISSION}}</td>
								</tr>
							</tbody>
						</table>
						</div>
                      </section>
                      </div>
       </div>
	</div>
</div>