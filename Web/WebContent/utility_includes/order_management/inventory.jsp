<script type="text/javascript">
	GLOBAL_SIDEBAR_TOGGLE();

	ANGULARJS_APP = angular.module('OrderManagementInventorySearchApp', [
			'search', 'enterkey' ]);
	$(document).ready(function() {
		$('.weather-bg').hide();
	});

	ANGULARJS_APP.filter('orderObjectBy', function() {
		return function(items, field, reverse) {
			var filtered = [];
			angular.forEach(items, function(item) {
				filtered.push(item);
			});
			filtered.sort(function(a, b) {
				return (a[field] > b[field]) ? 1 : ((a[field] < b[field]) ? -1
						: 0);
			});
			if (reverse)
				filtered.reverse();
			return filtered;
		};

	});

	function InventorySearchController($scope, $http, $location,
			STOREELFSearchService) {

		var post_url = STOREELF_ROOT_URI + '/Utility/OrderManagement/inventory';
		$scope.sep1 = "Style"
		$scope.sep2 = "Size"
		$scope.sep3 = "Color"
		$scope.sep4 = "Name"
		$scope.sep5 = "Additional"
		$scope.sep6 = "Additional2"
		$scope.rows = [ {
			"val1" : "Amelia",
			"val2" : "Cool"
		}, {
			"val1" : "Other",
			"val2" : "Bad"
		} ]

	}
</script>
<style>
.weather-category ul li {
	width: 19%;
	text-align: center;
	border-right: 1px solid #e6e6e6;
	display: inline-block;
	font-size: 14px;
}

.grayedout {
	background-color: #D3D3D3;
	font-size: 15px;
	text-align: center;
}
</style>

<div ng-app="OrderManagementInventorySearchApp">
	<div ng-controller="InventorySearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Inventory</li>
			</ol>
		</div>

		<div class="panel panel-default">
			<section class="panel-body">
				<div class="col-sm-4">
					<label for="in1">{{sep1}}</label><input type="in1"
						class="form-control" id="in1" ng-model="in1">
				</div>
				<div class="col-sm-4">
					<label for="in2">{{sep2}}</label><input type="in2"
						class="form-control" id="in2" ng-model="in2">
				</div>
				<div class="col-sm-4">
					<label for="in3">{{sep3}}</label><input type="in3"
						class="form-control" id="in3" ng-model="in3">
				</div>
				<div class="col-sm-4">
					<label for="in4">{{sep4}}</label><input type="in4"
						class="form-control" id="in4" ng-model="in4">
				</div>
				<div class="col-sm-4">
					<label for="in5">{{sep5}}</label><input type="in5"
						class="form-control" id="in5" ng-model="in5">
				</div>
				<div class="col-sm-4">
					<label for="in6">{{sep6}}</label><input type="in6"
						class="form-control" id="in6" ng-model="in6">
				</div>
			</section>
		</div>

		<br>


		<div class="panel panel-default">
			<div class="panel-heading">
				<label>Test</label>
			</div>
			<div class="panel-body">
				<table class="table table-responsive table-hover">
					<thead>
						<th>{{sep1}}</th>
						<th>{{sep2}}</th>
					</thead>
					<tbody>
						<tr ng-repeat-start="row in rows" ng-click="t2=!t2">
							<td><button type="button" class="btn btn-drpdown btn-sm">
									<span
										ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[t2==true]">
									</span>
								</button></td>
							<td>{{row.val1}}</td>
							<td>{{row.val2}}</td>
						</tr>
						<tr ng-show="t2==true">
						</tr>
						<tr ng-repeat-end ng-show="t3">
						</tr>
					</tbody>
				</table>
			</div>

		</div>

		<div class="col-sm-4"></div>



	</div>
</div>