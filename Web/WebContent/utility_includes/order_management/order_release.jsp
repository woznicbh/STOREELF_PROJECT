<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderReleaseSearchApp', ['search', 'enterkey']);

 	function OrderReleaseSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/order_release';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.searchOrderReleases = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
				
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "orderReleaseNumber=" + search.orderReleaseNumber ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('orderReleaseNumber', search.orderReleaseNumber);
				$scope.orderReleaseResults = data.ORDERS;
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
		};
 */
		$scope.autoSearch = function(hash){
			//$scope.getSearchHistory();
 			$scope.searchOrderReleases(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementOrderReleaseSearchApp">
	<div ng-controller="OrderReleaseSearchController">
			<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Order Release</li>
			</ol>
		</div>

		<div class="col-sm-12">

		<section class="panel">
		<header class="panel-heading"></header>
		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchOrderReleases(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Order No</label>
			    <div class="col-sm-3">
			      <input name="orderReleaseNumber" class="form-control" ng-model="search.orderReleaseNumber" onclick="" value="" ng-init="search.orderReleaseNumber=''" type="text" placeholder="Order No" required/>
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchOrderReleases(search)">{{button}}</button>
				<!-- 	<div class="btn-group">
						<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
						<ul role="menu" class="dropdown-menu">
							<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
							<li class="divider"></li>
							<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
						</ul>
					</div> --><!-- /btn-group -->
				</div>
			</div>
		</form>
		</div>
</section>
		</div>

		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th></th>
						<th>SALES ORDER NO</th>
						<th>ORDER DATE</th>
						<th>MODIFYTS</th>
						<th>RELEASE NO</th>
						<th>SHIPNODE KEY</th>
						<th>PICKTICKET NO</th>
						<th>STATUS</th>
						<th>SHIP TO</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat-start="order_release in orderReleaseResults">
						<td>
							<button type="button" class="btn btn-drpdown btn-sm" ng-click="toggle_order_releaseResults = !toggle_order_releaseResults">
								<span ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_order_releaseResults==true]"></span>
							</button>
	              		</td>
						<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{order_release.SALES_ORDER_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{order_release.SALES_ORDER_NO.trim()}}</a></td>
						<td>{{order_release.ORDER_DATE}}</td>
						<td>{{order_release.MODIFYTS}}</td>
						<td>{{order_release.RELEASE_NO}}</td>
						<td>{{order_release.SHIPNODE_KEY}}</td>
						<td><a ng-if='order_release.PICKTICKET_NO.length > 0' class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/pickticket#/search?pickticket_no={{order_release.PICKTICKET_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{order_release.PICKTICKET_NO.trim()}}</a></td>
						<td>{{order_release.STATUS}}</td>
						<td>{{order_release.SHIP_TO}}</td>
					</tr>
					<tr ng-show="toggle_order_releaseResults" ng-animate="'box'" ng-repeat-end>
						<td colspan="9">
							<table class="table table-hover">
								<tr>
									<th>ITEM_ID</th>
									<th>PRIME_LINE_NO</th>
									<th>STATUS_QUANTITY</th>
								</tr>
								<tr ng-repeat="order_line in order_release.ORDER_ARRAY">
									<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/item#/search?items={{order_line.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order_line.ITEM_ID.trim()}}</a> </td>
									<td>{{order_line.PRIME_LINE_NO}}</td>
									<td>{{order_line.STATUS_QUANTITY}}</td></tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>