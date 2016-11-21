<script type="text/javascript">
var	orderSearchApp = angular.module('StoreElfComMetricsApp', []);

	 function WarehousePurgingController($scope, $http){
		$scope.warehousePurgeChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/warehouse_purging',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.warehousePurgeResults_main	= data.EFC1;
				$scope.last_run_timestamp			= data.warehouse_purge_last_run_timestamp;
				$scope.wareHousePurge_label			= "EFC1";
				$scope.warehousePurgeResults_EFC1	= data.EFC1;
				$scope.warehousePurgeResults_EFC2	= data.EFC2;
				$scope.warehousePurgeResults_EFC3	= data.EFC3;
			});
		};
		
		$scope.changeWarehousePurgeResults = function(efc){
			switch (efc) {
				case 'EFC1': $scope.warehousePurgeResults_main = $scope.warehousePurgeResults_EFC1; break;
				case 'EFC2': $scope.warehousePurgeResults_main = $scope.warehousePurgeResults_EFC2; break;
				case 'EFC3': $scope.warehousePurgeResults_main = $scope.warehousePurgeResults_EFC3; break;
				default:	 $scope.warehousePurgeResults_main = $scope.warehousePurgeResults_EFC1; break;
			}
		};
		
	 }
</script>

<!-- Purge Stats BEGIN -->
<div ng-app="StoreElfComMetricsApp">
	<div ng-controller="WarehousePurgingController">
		<section class="panel">
			<header class="panel-heading">3-Day Purge Statistics
				<span>&nbsp;&nbsp;&nbsp;Standby DB</span>
				<span class="tools pull-right" ng-bind-template="(Last Updated: {{last_run_timestamp}})"></span>
				<div class="btn-group">
				  <button class="btn btn-default btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
				      {{wareHousePurge_label}}<span class="caret"></span>
				  </button>
				  <ul class="dropdown-menu">
				    <li><a ng-click="wareHousePurge_label='EFC1';changeWarehousePurgeResults('EFC1')">EFC1</a></li>
				    <!--   For EFC 2 2012 upgrade starts -->
				    <!-- <li><a ng-click="wareHousePurge_label='EFC2';changeWarehousePurgeResults('EFC2')">EFC2</a></li>-->
					<!--   For EFC 2 2012 upgrade ends -->
				    <li><a ng-click="wareHousePurge_label='EFC3';changeWarehousePurgeResults('EFC3')">EFC3</a></li>
				  </ul>
				</div>
			</header>
			<table class="table" style="text-align: center; overflow: hidden" data-ng-init="warehousePurgeChartData()">
				<thead>
					<tr>
						<th>Purge Type</th>
						<th>Purge</th>
						<th>Progress</th>
						<th>Time</th>
						<th>Message</th>
						<th>Message Log ID</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="warehousePurgeResult in warehousePurgeResults_main">
						<td ng-bind-template="{{warehousePurgeResult.PURGE_TYPE}}"></td>
						<td ng-bind-template="{{warehousePurgeResult.PURGE}}"></td>
						<td ng-bind-template="{{warehousePurgeResult.PROGRESS}}"></td>
						<td ng-bind-template="{{warehousePurgeResult.TIME}}"></td>
						<td ng-bind-template="{{warehousePurgeResult.MSG}}"></td>
						<td ng-bind-template="{{warehousePurgeResult.MSG_LOG_ID}}"></td>
					</tr>
				</tbody>
			</table>
		</section>
	</div>	
</div>