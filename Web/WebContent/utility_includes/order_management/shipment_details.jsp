<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementShipmentDetailsSearchApp', ['search', 'enterkey']);

 	function ShipmentDetailsSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/shipment_details';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.searchShipment = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "carton_no=" + search.carton_no + "&order_no=" + search.order_no + "&shipment_no="
			    + search.shipment_no + "&tracking_no=" + search.tracking_no,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('carton_no', search.carton_no );
				$location.search('order_no', search.order_no );
				$location.search('shipment_no', search.shipment_no );
				$location.search('tracking_no', search.tracking_no );
				$scope.shipmentResults = data.shipment_details;
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
 			$scope.searchShipment(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementShipmentDetailsSearchApp">
	<div ng-controller="ShipmentDetailsSearchController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Shipment Details</li>
			</ol>
		</div>
		<div class="col-sm-12">
		<section class="panel">

		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchShipment(search)">
		<div class="form-group">
			<label class="col-sm-2 control-label">Carton No</label>
			<div class="col-sm-3">
		      <input name="carton_no" class="form-control" maxlength="100" ng-model="search.carton_no" onclick="" type="text" ng-init="search.carton_no=''" placeholder="Carton No">
		    </div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Order No</label>
			<div class="col-sm-3">
		      <input name="order_no" class="form-control" maxlength="100" ng-model="search.order_no" onclick="" type="text" ng-init="search.order_no=''" placeholder="Order No">
		    </div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Shipment No</label>
			<div class="col-sm-3">
		      <input name="shipment_no" class="form-control" maxlength="100" ng-model="search.shipment_no" onclick="" type="text" ng-init="search.shipment_no=''" placeholder="Shipment No">
		    </div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Tracking No</label>
			<div class="col-sm-3">
		      <input name="tracking_no" class="form-control" maxlength="100" ng-model="search.tracking_no" onclick="" type="text" ng-init="search.tracking_no=''" placeholder="Tracking No">
		    </div>
		</div>
			<!-- New code ends -->


		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button ng-disabled="!(!!search.carton_no || !!search.order_no || !!search.shipment_no || !!search.tracking_no || isLoading()) || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchShipment(search)">{{button}}</button>
				<!-- <div class="btn-group">
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
			<thead><tr><th colspan="11">Results</th></tr>
				<tr>
					<th>[+]</th>
					<th>Shipment No</th>
					<th>Order No</th>
					<th>Actual Shipment Date</th>
					<th>Expected Shipment Date</th>
					<th>Pickticket No</th>
					<th>Carrier</th>
					<th>Service Level</th>
					<th>Ship Node</th>
					<th>Status</th>
					<th>Assigned To</th>
				</tr>
			</thead>
			<tbody>
				<tr ng-repeat-start="shipment in shipmentResults">
					<td>
						<button type="button" class="btn btn-drpdown btn-sm" ng-click="toggle_ShipmentTrackingResults = !toggle_ShipmentTrackingResults">
							<span ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_ShipmentTrackingResults==true]">  </span>
						</button>
					</td>
					<td>{{shipment.SHIPMENT_NO}}</td>
					<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{shipment.ORDER_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{shipment.ORDER_NO.trim()}}</a> </td>
					<td>{{shipment.ACTUAL_SHIPMENT_DATE}}</td>
					<td>{{shipment.EXPECTED_SHIPMENT_DATE}}</td>
					<td>{{shipment.PICKTICKET_NO}}</td>
					<td>{{shipment.CARRIER_SERVICE_CODE}}</td>
					<td>{{shipment.SCAC}}</td>
					<td>{{shipment.SHIPNODE_KEY}}</td>
					<td>{{shipment.STATUS}}</td>
					<td>{{shipment.USERNAME}}</td>
				</tr>
				<tr ng-show="toggle_ShipmentTrackingResults" ng-animate="'box'" ng-repeat-end>
						<td colspan="10">
							<table class="table table-hover table-condensed table-bordered">
								<tr>
									<th>Carton No</th>
									<th>Tracking No</th>
									<th>Release No</th>
									<th>Order Line No</th>
									<th>Item ID</th>
									<th>Item Description</th>
									<th>Quantity</th>
									<th>Weight</th>
								</tr>
								<tr ng-repeat="order_line in shipment.ORDER_LINE_ARRAY">
									<td>{{order_line.CONTAINER_SCM}}</td>
									<td><a href="{{order_line.TRACKING_LINK}}" target="_blank">{{order_line.TRACKING_NO}}</a></td>
									<td>{{order_line.RELEASE_NO}}</td>
									<td>{{order_line.PRIME_LINE_NO}}</td>
									<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/item#/search?items={{order_line.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order_line.ITEM_ID.trim()}}</a></td>
									<td>{{order_line.ITEM_DESCRIPTION}}</td>
									<td>{{order_line.QUANTITY}}</td>
									<td>{{order_line.CONTAINER_GROSS_WEIGHT}}</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>