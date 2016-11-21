<script type="text/javascript">

	//your app MUST be named starting with it's associated Servlet name
	var	App = angular.module('WarehouseManagementApp', []);

	//define your controller, function name MUST end with 'Controller'
 	function WarehouseManagementController($scope, $http){

 		//define your controllers calling function
		$scope.LoadWarehouseManagementStatistics = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/warehouse_management',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.results = data;

				$scope.WarehouseManagementStatisticsChart_LABEL	= "EFC1";
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC1;

				$scope.LoadWarehouseManagementStatistics_EFC1 = $scope.results.WM_Collate_Print_Time_EFC1;
				$scope.LoadWarehouseManagementStatistics_EFC2 = $scope.results.WM_Collate_Print_Time_EFC2;
				$scope.LoadWarehouseManagementStatistics_EFC3 = $scope.results.WM_Collate_Print_Time_EFC3;
				$scope.LoadWarehouseManagementStatistics_EFC4 = $scope.results.WM_Collate_Print_Time_EFC4;
			});
		};

		$scope.switchWarehouseManagementStatisticsChart = function(efc){
			//change ng-show toggle to true
			switch (efc) {
			case 'EFC1':
				console.log("chart 1");
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC1;
				break;
			case 'EFC2':
				console.log("chart 2");
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC2;
				break;
			case 'EFC3':
				console.log("chart 3");
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC3;
				break;
			case 'EFC4':
				console.log("chart 4");
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC4;
				break;

			default:
				$scope.LoadWarehouseManagementStatistics_MAIN = $scope.results.WM_Collate_Print_Time_EFC1;
				break;
			}
		};
	}
</script>

<div ng-app="WarehouseManagementApp">
	<div ng-controller="WarehouseManagementController">
		<div class="col-sm-12">
		<section class="panel">
			<header class="panel-heading">
				Quick 24-Hour Collate Print Time Performance by Hour (seconds)
				<span>&nbsp;&nbsp;&nbsp;Primary DB</span>
				<span class="tools" ng-bind-template="(Last Updated: {{EFCIventorySnapshotResults_last_run_timestamp}})"></span>
				<div class="btn-group  pull-right">
					<button class="btn btn-sval btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
						Select EFC (<strong> {{WarehouseManagementStatisticsChart_LABEL}}  </strong>)<span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a ng-click="WarehouseManagementStatisticsChart_LABEL='EFC1';switchWarehouseManagementStatisticsChart('EFC1')">EFC1</a></li>
						<li><a ng-click="WarehouseManagementStatisticsChart_LABEL='EFC2';switchWarehouseManagementStatisticsChart('EFC2')">EFC2</a></li>						
						<li><a ng-click="WarehouseManagementStatisticsChart_LABEL='EFC3';switchWarehouseManagementStatisticsChart('EFC3')">EFC3</a></li>
						<li><a ng-click="WarehouseManagementStatisticsChart_LABEL='EFC4';switchWarehouseManagementStatisticsChart('EFC4')">EFC4</a></li>
					</ul>
				</div>
			</header>

			<table class="table table-hover table-condensed table-bordered table-responsive" data-ng-init="LoadWarehouseManagementStatistics()">
					<thead>
	 					<tr style='font-weight: bold;' class="text-right">
							<th>Hour (CST)</th>
							<th style="color:#0BA11D">1-5</th>
							<th style="color:#0BA11D">6-10</th>
							<th style="color:#0BA11D">11-15</th>
							<th style="color:#0BA11D">16-20</th>
							<th style="color:#0BA11D">21-30</th>
							<th style="color:#0BA11D">31-40</th>
							<th style="color:#0BA11D">41-50</th>
							<th style="color:#0BA11D">51-60</th>
							<th style="color:#ED620C">61-120</th>
							<th style="color:#ED620C">121-300</th>
							<th style="color:#ED620C">301-1800</th>
							<th style="color:#ED0C0C">1801-3600</th>
							<th style="color:#ED0C0C">3601-5400</th>
							<th style="color:#ED0C0C">5400+</th>
							<th>Total</th>
						</tr>
					</thead>
				<tbody>
					<tr ng-repeat="r in LoadWarehouseManagementStatistics_MAIN.COLLATE_TIMES" class="text-right">
						<td ng-bind-template="{{r.DTE}}">		</td>
						<td ng-bind-template="{{r.1_5}}">		</td>
						<td ng-bind-template="{{r.6_10}}">		</td>
						<td ng-bind-template="{{r.11_15}}">		</td>
						<td ng-bind-template="{{r.16_20}}">		</td>
						<td ng-bind-template="{{r.21_30}}">		</td>
						<td ng-bind-template="{{r.31_40}}">		</td>
						<td ng-bind-template="{{r.41_50}}">		</td>
						<td ng-bind-template="{{r.51_60}}">		</td>
						<td ng-bind-template="{{r.61_120}}">	</td>
						<td ng-bind-template="{{r.121_300}}">	</td>
						<td ng-bind-template="{{r.301_1800}}">	</td>
						<td ng-bind-template="{{r.1801_3600}}">	</td>
						<td ng-bind-template="{{r.3601_5400}}">	</td>
						<td ng-bind-template="{{r.5400}}">		</td>
						<td style="font-weight: bold">{{r.TOTAL_PRINTED}}</td>
					</tr>
				</tbody>

				<tfoot>
					<tr style="font-weight: bold">
						<td>Percentages</td>
						<td ng-repeat="percentage in LoadWarehouseManagementStatistics_MAIN.PERCENTAGES" class="text-right">{{percentage.percentage}}</td>
					</tr>
				</tfoot>
			</table>
			</section>
		</div>
	</div>
</div>