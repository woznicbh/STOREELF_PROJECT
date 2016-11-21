<script type="text/javascript">

	//your app MUST be named starting with it's associated Servlet name
	ANGULARJS_APP = angular.module('OrderManagementOrderExceptionApp', []);


 	function OrderExceptionController($scope, $http){

 		$scope.predicate = 'days_aged';
 		$scope.reverse = 'first';

		$scope.getDashboard = function(search) {


 			if(search=="csv"){

 				window.location.href=STOREELF_ROOT_URI+'/Utility/OrderManagement/order_exception' + "?type=" + search;

 			} else {

	 			$http({
					//request method, this should remain 'POST'
				    method: 'POST',
				    url: STOREELF_ROOT_URI+'/Utility/OrderManagement/order_exception',
				    data: "type=" + search,
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					$scope.results = data.exception_array;
					$scope.update_time = data.update_time;
				});
 			}
		};
	}
</script>

<div ng-app="OrderManagementOrderExceptionApp">
	<div ng-controller="OrderExceptionController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Order Exception</li>
			</ol>
		</div>

		<!-- SIMPLE TABLE -->
		<div class="col-sm-12">
		<section class="panel">
			<header class="panel-heading">
				<a href="#" role="button" ng-model="search" ng-click="getDashboard('csv')">Export All</a>
				<b style="float: right">Last Updated: {{update_time}}</b>
			</header>
			<table class="table table-hover table-condensed table-bordered table-responsive" ng-init="getDashboard('table')">
				<thead>
					<tr>
						<th ng-click="predicate = 'ORDER_DATE'; reverse=!reverse">Order Date
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'ORDER_DATE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'STATUS_DATE'; reverse=!reverse">Status Date
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'STATUS_DATE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'DESCRIPTION'; reverse=!reverse">Status
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'DESCRIPTION'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'DAYS_AGED'; reverse=!reverse">Days Aged
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'DAYS_AGED'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'ORDER_NO'; reverse=!reverse">Order_No
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'ORDER_NO'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHIPMENT_NO'; reverse=!reverse">Shipment_No
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'SHIPMENT_NO'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHIPMENT_TYPE'; reverse=!reverse">Shipment Type
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'SHIPMENT_TYPE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHIPNODE_KEY'; reverse=!reverse">Node
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'SHIPNODE_KEY'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'QUANTITY'; reverse=!reverse">Qty
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'QUANTITY'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHIP_TO_ADDRESS'; reverse=!reverse">Address
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>

							<span style="float: right" ng-show="predicate == 'SHIP_TO_ADDRESS'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>

					<tr>
						<th><input class="filter" ng-model="order_date"></th>
						<th><input class="filter" ng-model="status_date"></th>
						<th><input class="filter" ng-model="description"></th>
						<th><input class="filter" ng-model="days_aged"></th>
						<th><input class="filter" ng-model="order_no"></th>
						<th><input class="filter" ng-model="shipment_no"></th>
						<th><input class="filter" ng-model="shipment_type"></th>
						<th><input class="filter" ng-model="shipnode_key"></th>
						<th><input class="filter" ng-model="quantity"></th>
						<th><input class="filter" ng-model="address"></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="result in results | orderBy:predicate:reverse | filter:{ORDER_DATE : order_date,
					STATUS_DATE : status_date,
					DESCRIPTION : description,
					DAYS_AGED : days_aged,
					ORDER_NO : order_no,
					SHIPMENT_NO : shipment_no,
					SHIPMENT_TYPE : shipment_type,
					SHIPNODE_KEY : shipnode_key,
					QUANTITY : quantity,
					SHIP_TO_ADDRESS : address}">
						<td>{{result.ORDER_DATE | date : 'medium'}}</td>
						<td>{{result.STATUS_DATE | date : 'medium'}}</td>
						<td>{{result.DESCRIPTION}}</td>
						<td>{{result.DAYS_AGED}}</td>
						<td>{{result.ORDER_NO}}</td>
						<td>{{result.SHIPMENT_NO}}</td>
						<td>{{result.SHIPMENT_TYPE}}</td>
						<td>{{result.SHIPNODE_KEY}}</td>
						<td>{{result.QUANTITY}}</td>
						<td>{{result.SHIP_TO_ADDRESS}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>