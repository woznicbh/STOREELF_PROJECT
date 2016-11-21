<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderStatusSearchApp', ['search', 'enterkey']);

 	function OrderStatusSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/order_status';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		$scope.searchOrderStatus = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "orderNumbers=" + search.orderNumbers,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('orderNumbers', search.orderNumbers);
				$scope.orderStatusResult = data.order_statuses;
				//$scope.getSearchHistory();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};

		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

		/* $scope.getSearchHistory = function(){
			//-- set search history
			STOREELFSearchService.setRequestUri(post_url);
			STOREELFSearchService.getHistory();
			STOREELFSearchService.injectIntoScope($scope);
			$scope.search_history_data = STOREELFSearchService.getData();
		}; */

		$scope.autoSearch = function(hash){
			//$scope.getSearchHistory();
 			$scope.searchOrderStatus(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementOrderStatusSearchApp">
	<div ng-controller="OrderStatusSearchController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Order Status</li>
			</ol>
		</div>

		<div class="col-sm-12">
		<section class="panel">

		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchOrderStatus(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Order No</label>
			    <div class="col-sm-3">
			      <textarea name="orderNumbers" class="form-control" ng-model="search.orderNumbers" onclick="" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No" required></textarea>
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="button" ng-click="searchOrderStatus(search)">{{button}}</button>
					<!--  <div class="btn-group">
						<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
						<ul role="menu" class="dropdown-menu">
							<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
							<li class="divider"></li>
							<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
						</ul>
					</div>--><!-- /btn-group -->
				</div>
			</div>
		</form>
		</div>
</section>
		</div>

		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Order Number</th>
						<th>Order Status</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="orderStatus in orderStatusResult">
						<td>{{orderStatus.ORDER_NO}}</td>
						<td>{{orderStatus.STATUS}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>