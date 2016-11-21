<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementPickticketSearchApp', ['search', 'enterkey']);

 	function PickticketSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/pickticket';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.searchPickticket = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "pickticket_no=" + search.pickticket_no ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('pickticket_no', search.pickticket_no );
				$scope.picktickets = data;
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
 			$scope.searchPickticket(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementPickticketSearchApp">
	<div ng-controller="PickticketSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Pickticket</li>
			</ol>
		</div>

		<div class="col-sm-12">
		<section class="panel">

		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchPickticket(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Pickticket No</label>
			    <div class="col-sm-3">
			    	<input name="pickticket_no" class="form-control" ng-model="search.pickticket_no" onclick="" required="TRUE" value="" type="text" ng-init="search.pickticket_no=''" placeholder="Pickticket No">
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchPickticket(search)">{{button}}</button>
				<!-- 	<div class="btn-group">
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
			<table class="table table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th>Pickticket No</th>
						<th>Customer Po Nbr</th>
						<th>Warehouse</th>
						<th>Order Type</th>
						<th>Quantity</th>
						<th>Ship To</th>
						<th>Stat Code</th>
						<th>Order Date</th>
						<th>Create Date Time</th>
						<th>Routing Guide</th>
					</tr>
				</thead>
			<tbody>
					<tr ng-repeat="pickticket in picktickets">
						<td>{{pickticket.PKT_CTRL_NBR}}</td>
						<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{pickticket.CUST_PO_NBR.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{pickticket.CUST_PO_NBR.trim()}}</a></td>
						<td>{{pickticket.WHSE}}</td>
						<td>{{pickticket.ORD_TYPE}}</td>
						<td>{{pickticket.TOTAL_NBR_OF_UNITS}}</td>
						<td>{{pickticket.SHIPTO_STATE}}</td>
						<td>{{pickticket.STAT_CODE}}</td>
						<td>{{pickticket.ORD_DATE}}</td>
						<td>{{pickticket.CREATE_DATE_TIME}}</td>
						<td>{{pickticket.RTE_GUIDE_NBR}}</td>
					</tr>
			</tbody>
			</table>
			</section>
		</div>
	</div>
</div>