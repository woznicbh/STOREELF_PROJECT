<script type="text/javascript">
	var	app = angular.module('DashboardApp', []);

 	function StoreStatisticsController($scope, $http, $log, $rootScope){
		$scope.loadGlanceChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreMetrics/all_store_fulfillment_performance',
			    data: "chart=fulfillment",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.regionResults = data.region_array;
				$scope.lastTimeStamp = data.run_ts;
			});
		};



	}
</script>

<div ng-app="DashboardApp">
	<div ng-controller="StoreStatisticsController">
		<div>
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-md-12">
			<section class="panel">
				<header class="panel-heading">Fulfillment Performance
					<span class="tools pull-right" ng-bind-template="(Last Updated: {{lastTimeStamp}})"></span>
				</header>
				<table class="table table-hover table-striped table-condensed" description="fulfillment performance" data-ng-init="loadGlanceChartData()">
					<thead>
					<td>Region</td>
					<td>Average</td>
					<td>Shipped</td>
					<td>Backlog</td>
					<td>Cancelled</td>
					</thead>
					<tbody ng-repeat="region in regionResults" >
						<tr ng-click="toggle_RegionStates = !toggle_RegionStates">
							<td>
								<i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_RegionStates==true]"> {{region.REGION}}</i>
							</td>
							<td>{{region.AVERAGE}}</td>
							<td>{{region.TTLSHPUNITCNT}}</td>
							<td>{{region.BACKLOG}}</td>
							<td>{{region.CNCLUNITCNT}}</td>
						</tr>
						<tr ng-click="toggle_StateStores = !toggle_StateStores" ng-show="toggle_RegionStates" ng-animate="'box'" ng-repeat-start="state in region.state_array">
							<td>
								<i style="padding-left: 20px;" ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_StateStores==true]"> {{state.STATE}}</i>
							</td>
							<td>{{state.AVERAGE}}</td>
							<td>{{state.TTLSHPUNITCNT}}</td>
							<td>{{state.BACKLOG}}</td>
							<td>{{state.CNCLUNITCNT}}</td>
						</tr>
						<tr ng-show="toggle_StateStores && toggle_RegionStates" ng-animate="'box'" ng-repeat="store in state.store_array">

										<td style="padding-left: 40px;"> {{store.STORE_NAME}}</td>
										<td>{{store.AVERAGE}}</td>
										<td>{{store.TTLSHPUNITCNT}}</td>
										<td>{{store.BACKLOG}}</td>
										<td>{{store.CNCLUNITCNT}}</td>

						</tr>
						<tr ng-repeat-end></tr>
					</tbody>
				</table>

			</section>
			</div>
		</div>
	</div>
</div>


