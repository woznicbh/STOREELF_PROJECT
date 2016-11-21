<script src="/Dashboard/public/v3/js/morris.min.js" type="text/javascript"></script>
<script src="/Dashboard/public/v3/js/raphael-min.js" type="text/javascript"></script>
 <link href="/Dashboard/public/v3/css/morris.css" rel="stylesheet" />
<script type="text/javascript">
	var	app = angular.module('DashboardApp', []);
	var initial_load = 0;

	app.filter('removehtml', function(){
		return function(input){
			return input.replace('<b>', '').replace('</b>', '');
		};
	});




 	function MarketplaceStatisticsController($scope, $http, $log, $rootScope, $interval){



		$scope.MPDailyDelinqChartData = function() {
			$http({
			    method: 'POST',
			    url: '/Dashboard/MarketPlaceMetrics/quick_metrics',
			    data: "chart=dailydelinq",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MPDailyDelinqResults = data.delinquent_data;
				$scope.MPDailyDelinqResults_last_run_timestamp = data.last_run_timestamp;

			});
		};

		$scope.MPPOClosedYesterdayChartData = function() {
			$http({
			    method: 'POST',
			    url: '/Dashboard/MarketPlaceMetrics/quick_metrics',
			    data: "chart=po_closed_yesterday",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MPPOClosedYesterdayResults = data.po_closed_yesterday_data;
				$scope.MPPOClosedYesterdayResults_last_run_timestamp = data.last_run_timestamp;

			});
		};

		$scope.MP14DayFulfillPerformChartData = function() {
			$http({
			    method: 'POST',
			    url: '/Dashboard/MarketPlaceMetrics/quick_metrics',
			    data: "chart=14_day_fulfill_perform",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MP14DayFulfillPerformResults = data.mp_14_day_fulfill_data;
				$scope.MP14DayFulfillPerformResults_last_run_timestamp = data.last_run_timestamp;

			});
		};


		$scope.MPDailyFulfillPerformChartData = function() {
			$http({
			    method: 'POST',
			    url: '/Dashboard/MarketPlaceMetrics/quick_metrics',
			    data: "chart=daily_fulfill_perform",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MPDailyFulfillPerformResults    = data.daily_fulfill_data;
				$scope.MPDailyFulfillPerform_last_run_timestamp = data.last_run_timestamp;

			});
		};

		$scope.MPProductTrendChartData= function() {
			$http({
			    method: 'POST',
			    url: '/Dashboard/MarketPlaceMetrics/quick_metrics',
			    data: "chart=product_trend",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.MPProductTrendResults			= data.product_trend_data;
				$scope.MPProductTrend_last_run_timestamp = data.last_run_timestamp;

			});
		};
		$scope.refresh = function(){
			if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
				$scope.MPDailyDelinqChartData();
				$scope.MPPOClosedYesterdayChartData();
				$scope.MP14DayFulfillPerformChartData();
				$scope.MPDailyFulfillPerformChartData();
				$scope.MPProductTrendChartData();
				console.log('reloading dashboard data');
			}
		};

		$scope.refresh();
		$interval($scope.refresh, 30000);

	}


</script>


<div ng-app="DashboardApp">
	<div ng-controller="MarketplaceStatisticsController">
		<div>
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-md-6" id="leftCol">
			<section class="panel">
			<div class="revenue-head">
		                  <span>
		                      <i class="fa fa-bar-chart-o"></i>
		                  </span>
		                  <h3>PO's Closed Yesterday</h3>
		                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MPPOClosedYesterdayResults_last_run_timestamp}})"></span>
		              </div>
	              <div class="panel-body" id="dashPadding">
	              <div class="row">
				<table class="table table-hover table-striped table-condensed" description="po closed yesterday" data-ng-init="MPPOClosedYesterdayChartData()">
					<thead>
					<tr>
						<th>Business Days Old</th>
						<th>Order Count</th>
						<th>%TTL</th>
					</thead>
					<tbody>
						<tr ng-repeat="po_closed_yesterday_data in MPPOClosedYesterdayResults | orderBy: 'DAYS_OLD'">
							<td>{{po_closed_yesterday_data.DAYS_OLD}}</td>
							<td>{{po_closed_yesterday_data.ORDER_COUNT}}</td>
							<td>{{po_closed_yesterday_data.PCT}}%</td>
						</tr>
					</tbody>
				</table>
				</div>
				</div>
			</section>

			<section class="panel">
				<div class="revenue-head">
		                  <span>
		                      <i class="fa fa-bar-chart-o"></i>
		                  </span>
		                  <h3>Daily Fulfillment Performance</h3>
		                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MPDailyFulfillPerform_last_run_timestamp}})"></span>
		              </div>
	              <div class="panel-body" id="dashPadding">
	              <div class="row">
				<table class="table table-hover table-striped table-condensed" description="Daily Fulfillment Performance" data-ng-init="MPDailyFulfillPerformChartData()">
					<thead>
						<tr>
							<th>Date</th>
							<th>Orders Received</th>
							<th>Orders Cancelled</th>
							<th>Orders Shipped</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="daily_fulfill_data in MPDailyFulfillPerformResults | orderBy: 'ROWNUM'">
							<td>{{daily_fulfill_data.CALENDAR_DATE}}</td>
							<td>{{daily_fulfill_data.ORDERS_RECEIVED}}</td>
							<td>{{daily_fulfill_data.ORDERS_CANCELLED}}</td>
							<td>{{daily_fulfill_data.ORDERS_SHIPPED}}</td>
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
                            <h3>14 Day Fulfillment Performance</h3>
                            <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MP14DayFulfillPerformResults_last_run_timestamp}})">

                            </span>
                        </div>
                        <div class="panel-body" id="dashPadding">
                        <div class="row">
                            	<table class="table table-hover table-striped table-condensed" data-ng-init="MP14DayFulfillPerformChartData()">
							<thead>
								<tr>
									<th>Date</th>
									<th>Total Orders</th>
									<th>Total Units</th>
									<th>Shipped %</th>
									<th>Pending %</th>
									<th>Cancelled %</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="mp_14_day_fulfill_data in MP14DayFulfillPerformResults | orderBy: 'ROWNUM'">
									<td> {{mp_14_day_fulfill_data.ORDER_DATE}}</td>
									<td> {{mp_14_day_fulfill_data.TOTAL_ORDERS}}</td>
									<td> {{mp_14_day_fulfill_data.TOTAL_UNITS}}</td>
									<td> {{mp_14_day_fulfill_data.SHIPPED_PCT}}%</td>
									<td> {{mp_14_day_fulfill_data.PENDING_PCT}}%</td>
									<td> {{mp_14_day_fulfill_data.CANCELLED_PCT}}%</td>
								</tr>
							</tbody>
						</table>
						</div>
                      </section>

			<section class="panel">
				<div class="revenue-head">
                  <span>
                      <i class="fa fa-bar-chart-o"></i>
                  </span>
                  <h3>Product Trends - Previous 14 Days</h3>
                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MPProductTrend_last_run_timestamp}})"></span>
              </div>
              <div class="panel-body" id="dashPadding">
              <div class="row">
					<table class="table table-hover table-striped table-condensed" data-ng-init="MPProductTrendChartData()">
						<thead>
							<tr>
								<th>Primary Product Type</th>
								<th>Product Type</th>
								<th>Units Sold</th>
								<th>Total Sales Revenue</th>
								<th>Total Commission Earned</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="product_trend_data in MPProductTrendResults | orderBy:'UNITS_SOLD':true">
								<td>{{product_trend_data.EXTN_PRI_PRODUCT_TYPE}}</td>
								<td>{{product_trend_data.EXTN_PRODUCT_TYPE}}</td>
								<td>{{product_trend_data.UNITS_SOLD}}</td>
								<td>{{product_trend_data.REVENUE}}</td>
								<td>{{product_trend_data.COMMISSION}}</td>
							</tr>
						</tbody>
					</table>
					</div>
					</div>
				</section>

			</div>
			<!-- right side -->

<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [BOTTOM WHOLE WIDTH] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-sm-12">
				<section class="panel">
					<div class="revenue-head">
	                  <span>
	                      <i class="fa fa-bar-chart-o"></i>
	                  </span>
	                  <h3>Delinquent Order Statistics</h3>
	                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{MPDailyDelinqResults_last_run_timestamp}})"></span>
	              </div>
              <div class="panel-body" id="dashPadding">
              <div class="row">
					<table class="table table-hover table-striped table-condensed" data-ng-init="MPDailyDelinqChartData()">
						<thead>
							<tr>
								<th colspan=3><center>Standard Shipping</center></th>
                                <th colspan=3><center>Priority Shipping</center></th>
                                <th colspan=3><center>All</center></th>
							</tr>
						</thead>
						<tbody>
							<tr>
                                <td><center><b>Delinquent Count</b></center></td>
                                <td><center><b>Open Orders</b></center></td>
                                <td><center><b>%TTL</b></center></td>
                                <td style="background-color:#D3D3D3"><center><b>Delinquent Count</b></center></td>
                                <td style="background-color:#D3D3D3"><center><b>Open Orders</b></center></td>
                                <td style="background-color:#D3D3D3"><center><b>%TTL</b></center></td>
                                <td><center><b>Delinquent Count</b></center></td>
                                <td><center><b>Open Orders</b></center></td>
                                <td><center><b>%TTL</b></center></td>
                             </tr>
                             <tr ng-repeat="delinquent_data in MPDailyDelinqResults">
								<td><center>{{delinquent_data.STANDARD_CNT}}</center></td>
								<td><center>{{delinquent_data.TOTAL_OPEN}}</center></td>
								<td><center>{{delinquent_data.STANDARD_PCT}}%</center></td>
								<td style="background-color:#D3D3D3"><center>{{delinquent_data.PRIORITY_CNT}}</center></td>
								<td style="background-color:#D3D3D3"><center>{{delinquent_data.TOTAL_OPEN2}}</center></td>
								<td style="background-color:#D3D3D3"><center>{{delinquent_data.PRIORITY_PCT}}%</center></td>
								<td><center>{{delinquent_data.ALL_DELINQ}}</center></td>
								<td><center>{{delinquent_data.TOTAL_OPEN3}}</center></td>
								<td><center>{{delinquent_data.TOTAL_PCT}}%</center></td>
							</tr>
						</tbody>
					</table>
					</div>
					</div>
				</section>
			</div>

		</div>
	</div>
</div>

