<%@ page import="org.apache.commons.lang.StringUtils"%>
<script src="<%=request.getContextPath()%>/public/v3/js/morris.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/raphael-min.js" type="text/javascript"></script>
 <link href="<%=request.getContextPath()%>/public/v3/css/morris.css" rel="stylesheet" />
<script type="text/javascript">
	var	app = angular.module('DashboardApp', []);
	var initial_load = 0;
	var COMMAND_STATUS = null;
<% if(StringUtils.isNotBlank(request.getParameter("COMMAND_STATUS"))){ %>
	COMMAND_STATUS = '<%=request.getParameter("COMMAND_STATUS")%>';
	alert("Command Status:"+COMMAND_STATUS);
<% } %>

	app.filter('removehtml', function(){
		return function(input){
			return input.replace('<b>', '').replace('</b>', '');
		};
	});




 	function StoreElfComStatisticsController($scope, $http, $log, $rootScope, $interval){



		$scope.loadGlanceChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics',
			    data: "chart=glance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.glanceResults = data.glance_data;
				$scope.glanceResults_last_run_ts = data.glance_last_run_timestamp;
				$scope.order_counts = data.total_chart_array;

				var result = [];
				for(i in data.total_chart_array)
				    result.push([data.total_chart_array [i]]);
				//console.log("RESULT: ");

				$scope.load_spark_line(result);
				//console.log("startekjgl3tu4hilu54yilh.ui");
			});
		};

		$scope.load_spark_line = function(result){

			// Draw a sparkline for the #sparkline element
			try{
			$('#sparkline').sparkline(result, {
			    type: "bar",
			    // Map the offset in the list of values to a name to use in the tooltip
			    tooltipFormat: '{{value}}',
			    tooltipValueLookups: {
			        'offset': {}
			    },


			    //create #sparkline div to show the graph in the desired location.

			});
			}catch(e){ console.log('fail');}
		}

		$scope.loadAllNodeIventorySnapshotChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics',
			    data: "chart=inventory_snapshot",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.EFCIventorySnapshotResults = data.EFC;
				$scope.EFCIventorySnapshotResults_last_run_timestamp = data.inventory_snapshot_last_run_timestamp;

				$scope.RDCIventorySnapshotResults = data.RDC;
				$scope.RDCTotalIventorySnapshotResults = data.RDC_TOTALS;

				$scope.RDCSnapshot__TOTAL_COUNT 	= 0;
				$scope.RDCSnapshot__TOTAL_INVTOTAL	= 0;

				angular.forEach(data.RDC, function(value, key){
					$scope.RDCSnapshot__TOTAL_COUNT		+= parseInt(value.COUNT);
					$scope.RDCSnapshot__TOTAL_INVTOTAL	+= parseInt(value.INVTOTAL); //TODO fix
				});
			});
		};

		$scope.redundancyStatisticsChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics',
			    data: "chart=redundancy_statistics",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.redundancyStatistics_last_run_timestamp = data.redundancy_last_run_timestamp;
				$scope.EFCstatisticResults = data.EFC;
				$scope.RDCstatisticResults = data.RDC;
				$scope.RDCTotalsstatisticResults = data.RDC_TOTALS;
			});
		};


		$scope.StoreElf14DayFullfillmentPerformanceStatisticsChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics',
			    data: "chart=14_day_fullfillment_performance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;

                $scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL    = "All";
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_main 		= data.all;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_all 			= data.all;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_dsv 			= data.direct_ship_vendor;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_ntw 			= data.network;


			});
		};

		$scope.changeStoreElf14DayFullfillmentPerformanceStatisticsChart = function(chart){
			switch (chart) {
				case 'ALL':
					$scope.StoreElf14DayFullfillmentPerformanceStatistics_main = $scope.StoreElf14DayFullfillmentPerformanceStatistics_all;
					$scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL='ALL';
				break;

				case 'Direct Ship':
					$scope.StoreElf14DayFullfillmentPerformanceStatistics_main = $scope.StoreElf14DayFullfillmentPerformanceStatistics_dsv;
					$scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL='DS';
				break;

				case 'Network':
					$scope.StoreElf14DayFullfillmentPerformanceStatistics_main = $scope.StoreElf14DayFullfillmentPerformanceStatistics_ntw;
					$scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL='NT';
				break;

				default:
					$scope.StoreElf14DayFullfillmentPerformanceStatistics_main = $scope.StoreElf14DayFullfillmentPerformanceStatistics_all;
					$scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL='ALL';
				break;
			}
		};

		$scope.FullfillmentPerformanceStatisticsChartData= function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics',
			    data: "chart=fullfillment_performance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.FullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;
				$scope.RDCFullfillmentPerformanceStatistics			= data.RDC;
				$scope.RDCTotalFullfillmentPerformanceStatistics	= data.RDC_TOTALS;
				$scope.EFCFullfillmentPerformanceStatistics 		= data.EFC;
			});
		};
		$scope.refresh = function(){
			if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
				$scope.loadGlanceChartData();
				$scope.loadAllNodeIventorySnapshotChartData();
				$scope.redundancyStatisticsChartData();
				$scope.StoreElf14DayFullfillmentPerformanceStatisticsChartData();
				$scope.FullfillmentPerformanceStatisticsChartData();
				console.log('reloading dashboard data');
			}
		};

		$scope.refresh();
		$interval($scope.refresh, 30000);

	}


</script>


<div ng-app="DashboardApp">
	<div ng-controller="StoreElfComStatisticsController">
		<div>
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-md-6" id="leftCol">
			<section class="panel" id="glanceSection">
			<div class="revenue-head">
		                  <span>
		                      <i class="fa fa-bar-chart-o"></i>
		                  </span>
		                  <h3>At a Glance</h3>
		                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{glanceResults_last_run_ts}})"></span>
		              </div>
	              <div class="panel-body" id="dashPadding">
	              <div class="row">
				<table class="table table-hover table-striped table-condensed" description="at a glance" data-ng-init="loadGlanceChartData()">
					<thead>
					</thead>
					<tbody>
						<tr ng-repeat="glance in glanceResults">
							<td>{{glance.DESCRIPTION}}: </td>
							<td>{{glance.VALUE}}</td>
							<!-- sparkline added here -->
							<td><span id="sparkline">&nbsp;</span></td>
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
		                  <h3>All Node Inventory Snapshot</h3>
		                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{EFCIventorySnapshotResults_last_run_timestamp}})"></span>
		              </div>
	              <div class="panel-body" id="dashPadding">
	              <div class="row">
				<table class="table table-hover table-striped table-condensed" description="All Node Inventory Snapshot" data-ng-init="loadAllNodeIventorySnapshotChartData()">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th>SKU Count</th>
							<th>Inventory Count</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="EFCsnapshot in EFCIventorySnapshotResults">
							<td>{{EFCsnapshot.DESCRIPTION | removehtml}}: </td>
							<td>{{EFCsnapshot.COUNT}}</td>
							<td>{{EFCsnapshot.INVTOTAL}}</td>
						</tr>
						<tr ng-click="toggle_RDCIventorySnapshotResults = !toggle_RDCIventorySnapshotResults">
							<td colspan="1">
								<i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_RDCIventorySnapshotResults==true]"> RDC</i>
							</td>
							<td>{{RDCTotalIventorySnapshotResults.COUNT}} </td>
							<td>{{RDCTotalIventorySnapshotResults.INVTOTAL}}</td>
						</tr>
						<tr ng-show="toggle_RDCIventorySnapshotResults" ng-animate="'box'" ng-repeat="RDCSnapshot in RDCIventorySnapshotResults">
							<td>{{RDCSnapshot.DESCRIPTION}}: </td>
							<td>{{RDCSnapshot.COUNT}}</td>
							<td>{{RDCSnapshot.INVTOTAL}}</td>
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
	                  <h3>Redundancy Statistics</h3>
	                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{redundancyStatistics_last_run_timestamp}})"></span>
	              </div>
              <div class="panel-body" id="dashPadding">
              <div class="row">
					<table class="table table-hover table-striped table-condensed" data-ng-init="redundancyStatisticsChartData()">
						<thead>
							<tr>
								<th>&nbsp;</th>
								<th>Unique SKU%</th>
								<th>Unique SKU Count</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="efc in EFCstatisticResults">
								<td>{{efc.DESCRIPTION}}: </td>
								<td>{{efc.UNIQUE_SKU}}%</td>
								<td>{{efc.UNIQUE_SKU_COUNT}}</td>
							</tr>
							<tr ng-click="toggle_RDCstatisticResults = !toggle_RDCstatisticResults">
								<td>
									<i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_RDCstatisticResults==true]"> RDC</i>
								</td>
								<td>{{RDCTotalsstatisticResults.UNIQUE_SKU}}%</td>
								<td>{{RDCTotalsstatisticResults.UNIQUE_SKU_COUNT}}</td>

							</tr>
							<!-- SHOULD TRY AND COMBINE THE BUTTON WITH THE RDC RESULTS ROW -->
							<tr ng-show="toggle_RDCstatisticResults" ng-animate="'box'" ng-repeat="rdc_stats in RDCstatisticResults">
								<td>{{rdc_stats.DESCRIPTION}}: </td>
								<td>{{rdc_stats.UNIQUE_SKU}}%</td>
								<td>{{rdc_stats.UNIQUE_SKU_COUNT}}</td>
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
                            <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{StoreElf14DayFullfillmentPerformanceStatistics_main.last_run_timestamp}})">

                            </span>
                        </div>
                        <div class="panel-body" id="dashPadding">
                        <div class="row">
                            	<table class="table table-hover table-striped table-condensed" data-ng-init="StoreElf14DayFullfillmentPerformanceStatisticsChartData();changeStoreElf14DayFullfillmentPerformanceStatisticsChart('ALL')">
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
								<tr ng-repeat="StoreElf14DayFullfillmentStat in StoreElf14DayFullfillmentPerformanceStatistics_main.fulfillment_data">
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.ORDER_DATE}}"></td>
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.ORDER_COUNT}}"></td>
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.TTL_UNITS}}"></td>
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.SHPD}}%"></td>
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.PEND}}%"></td>
									<td ng-bind-template="{{StoreElf14DayFullfillmentStat.CNCL}}%"></td>
								</tr>
							</tbody>
						</table>
						</div>
                            </div>
                          <div class="panel-footer revenue-foot" data-ng-init="StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL='ALL'">
                              <ul>
                                  <li ng-class="{'first active': StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL=='ALL'}">
                                      <a ng-click="changeStoreElf14DayFullfillmentPerformanceStatisticsChart('ALL')">
                                          <i class="fa fa-bullseye"></i>
                                          All
                                      </a>
                                  </li>
                                  <li ng-class="{'active': StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL=='DS'}">
                                      <a ng-click="changeStoreElf14DayFullfillmentPerformanceStatisticsChart('Direct Ship')">
                                          <i class=" fa fa-th-large"></i>
                                          Direct Ship
                                      </a>
                                  </li>
                                  <li ng-class="{'last active': StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL=='NT'}">
                                      <a ng-click="changeStoreElf14DayFullfillmentPerformanceStatisticsChart('Network')">
                                          <i class=" fa fa-align-justify"></i>
                                          Network
                                      </a>
                                  </li>
                              </ul>
                          </div>
                      </section>

			<section class="panel">
				<div class="revenue-head">
                  <span>
                      <i class="fa fa-bar-chart-o"></i>
                  </span>
                  <h3>Fulfillment Performance</h3>
                  <span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{FullfillmentPerformanceStatistics_last_run_timestamp}})"></span>
              </div>
              <div class="panel-body" id="dashPadding">
              <div class="row">
					<table class="table table-hover table-striped table-condensed" data-ng-init="FullfillmentPerformanceStatisticsChartData()">
						<thead>
							<tr>
								<th></th>
								<th>Avg Fulfill Days</th>
								<th>Backlog</th>
								<th>Shipped</th>
								<th>Cancelled</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="FullfillmentStat in EFCFullfillmentPerformanceStatistics">
								<td>{{FullfillmentStat.DESCRIPTION}}</td>
								<td>{{FullfillmentStat.AVERAGE}}</td>
								<td>{{FullfillmentStat.BACKLOG}}</td>
								<td>{{FullfillmentStat.TTLSHPUNITCNT}}</td>
								<td>{{FullfillmentStat.CNCLUNITCNT}}</td>
							</tr>
							<tr ng-click="toggle_RDCFullfillmentPerformanceStatistics = !toggle_RDCFullfillmentPerformanceStatistics">
								<td>
									<i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_RDCFullfillmentPerformanceStatistics==true]"> RDC</i>
								</td>
								<td>{{RDCTotalFullfillmentPerformanceStatistics.AVERAGE}}</td>
								<td>{{RDCTotalFullfillmentPerformanceStatistics.BACKLOG}}</td>
								<td>{{RDCTotalFullfillmentPerformanceStatistics.TTLSHPUNITCNT}}</td>
								<td>{{RDCTotalFullfillmentPerformanceStatistics.CNCLUNITCNT}}</td>
							</tr>
							<!-- NEED TO ADD THE AVERAGES FOR THE RDC's -->
							<tr ng-show="toggle_RDCFullfillmentPerformanceStatistics" ng-animate="'box'" ng-repeat="RDCFullfillmentStat in RDCFullfillmentPerformanceStatistics">
								<td>{{RDCFullfillmentStat.DESCRIPTION}}</td>
								<td>{{RDCFullfillmentStat.AVERAGE}}</td>
								<td>{{RDCFullfillmentStat.BACKLOG}}</td>
								<td>{{RDCFullfillmentStat.TTLSHPUNITCNT}}</td>
								<td>{{RDCFullfillmentStat.CNCLUNITCNT}}</td>
							</tr>
						</tbody>
					</table>
					</div>
					</div>
				</section>

			</div>
			<!-- right side -->

		</div>
	</div>
</div>


