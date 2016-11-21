<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderSearchApp', ['search', 'enterkey']);

	function OrderSearchController($scope, $http, $log, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/mqstatus';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";

		$scope.searchOrders = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "queueName=" + search.queueName ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('queueName', search.queueName);
				$scope.queuenotes=data.queuedetails;
				$scope.getSearch();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
		 		$scope.searchedqueueName = search.queueName;
		 		STOREELFSearchService.setRequestUri(post_url);
		 		
			});
			

		};
		
		
		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});
		
		 $scope.getSearch = function(){
				//-- set search history
				STOREELFSearchService.setRequestUri(post_url);
				//STOREELFSearchService.getHistory();
				//STOREELFSearchService.injectIntoScope($scope);
				//$scope.search_history_data = STOREELFSearchService.getData();
			}; 

	
	}
</script>

<div ng-app="OrderManagementOrderSearchApp">
	<div ng-controller="OrderSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">MQ Queue Status</li>
			</ol>
		</div>

			<div class="col-sm-12">
				<section class="panel">
					<div class="panel-body">
						<form name="form" class="form-horizontal" role="form" ng-enter="searchQueue(search)">
							<div class="form-group">
								<label class="col-sm-2 control-label">Queue Name</label>
							    <div class="col-sm-3">
									<input name="queueName" class="form-control" ng-model="search.queueName" value="" type="text" ng-init="search.queueName=''" placeholder="Queue Name" required/>
							    </div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-model="search.button" ng-click="searchOrders(search)">{{button}}</button>
								</div>
							</div>
						</form>
					</div>
				</section>
			</div>

<!-- start -->
		<div class="col-sm-12">
			<section class="panel">
			<header class="panel-heading"  style="float: left">
							Queue Details
				</header>
					<center></center><table class="table table-hover table-condensed table-bordered table-responsive">
					<thead>
					<tr>
						<th>Queue Depth</th>
						<th>%</th>
						<th>Max Depth</th>
						<th>I/P</th>
						<th>O/P</th>
						<th>Dequeue Rate</th>
						<th>Enqueue Rate</th>
						<th>UC</th>
						<th>Queue Name</th>
					</tr>
					<tr ng-repeat="queue in queuenotes">
							
							<td>{{queue.QUEUEDEPTH}}</td>
                            <td>{{queue.PERCENTAGE}}</td>
                            <td>{{queue.MAX}}</td>
                            <td>{{queue.INPUT}}</td>
                            <td>{{queue.OUTPUT}}</td>
                            <td>{{queue.DEQUEUE}}</td>
                            <td>{{queue.ENQUEUE}}</td>
                            <td>{{queue.UC}}</td>
                            <td>{{queue.QUEUENAME}}</td>
							
					</tr>
					</thead>
											
				</table></center>
				</section>
			</div>
<!-- end -->

			
			    </div>
			  </div>
			</div>
	</div>
</div>