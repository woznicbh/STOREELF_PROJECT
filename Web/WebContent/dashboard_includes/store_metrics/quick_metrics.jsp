<script type="text/javascript">
	var	app = angular.module('StoreMetricsApp', []);
	
 	function QuickMetricsController($scope, $http, $log, $rootScope){ 		 
		
 		$scope.allStoreFulfillmentPerformance = function() {			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreMetrics/quick_metrics',
			    data: "chart=all_store_fulfillment_performance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.results = data;
			});
		};
		
		
				
	} 	
</script>

<div ng-app="StoreMetricsApp">
	<div ng-controller="QuickMetricsController">		
		<div data-ng-init="allStoreFulfillmentPerformance()">
		
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
		
		</div>
	</div>
</div>


