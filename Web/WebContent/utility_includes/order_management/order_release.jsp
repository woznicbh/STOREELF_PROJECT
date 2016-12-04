<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderReleaseSearchApp', ['search', 'enterkey']);

 	function OrderReleaseSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/order_release';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		$scope.orderByField = 'PRODUCT NAME';
 		$scope.reverseSort = false;
 		
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
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};

		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

		$scope.autoSearch = function(hash){
 			$scope.searchOrderReleases(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div data-ng-app="InventoryApp">
	<div data-ng-controller="InventoryController">
		<div class="col-sm-12">

			<section class="panel">
				<header class="panel-heading"></header>
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form"
						data-ng-enter="searchOrderReleases(search)">
						<div class="form-group">
							<label class="col-sm-2 control-label">Order No</label>
							<div class="col-sm-3">
								<input name="orderReleaseNumber" class="form-control"
									data-ng-model="search.orderReleaseNumber" onclick="" value=""
									data-ng-init="search.orderReleaseNumber=''" type="text"
									placeholder="Order No" required />
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button
									data-ng-disabled="form.$invalid || isLoading()  || (clicked == true)"
									class="btn"
									data-ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]"
									type="submit" data-ng-click="searchOrderReleases(search)">{{button}}</button>
							</div>
						</div>
					</form>
				</div>
			</section>
		</div>

		<div class="col-sm-12">
		<section class="panel">
			<span class="label">Ordered By: {{orderByField}}, Reverse Sort: {{reverseSort}}</span><br><br>
			<table class="table table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th></th>
						<th>PRODUCT PHOTO</th>
						<th>
							<a href="#" data-ng-click="orderByField='PRODUCT_NAME'; reverseSort = !reverseSort">PRODUCT NAME
								<span data-ng-show="orderByField == 'PRODUCT_NAME'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
						<th>SIZES AVAILABLE</th>
						<th>
							<a href="#" data-ng-click="orderByField='FOR_SALE'; reverseSort = !reverseSort">FOR SALE
								<span data-ng-show="orderByField == 'FOR_SALE'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
						<th>
							<a href="#" data-ng-click="orderByField='SOLD'; reverseSort = !reverseSort">SOLD
								<span data-ng-show="orderByField == 'SOLD'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
						<th>
							<a href="#" data-ng-click="orderByField='PURCHASED_PRICE'; reverseSort = !reverseSort">PURCHASED PRICE
								<span data-ng-show="orderByField == 'PURCHASED_PRICE'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
						<th>
							<a href="#" data-ng-click="orderByField='RETAIL_PRICE'; reverseSort = !reverseSort">RETAIL PRICE
								<span data-ng-show="orderByField == 'RETAIL_PRICE'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
						<th>
							<a href="#" data-ng-click="orderByField='TOTAL_REVENUE'; reverseSort = !reverseSort">TOTAL REVENUE
								<span data-ng-show="orderByField == 'TOTAL_REVENUE'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>						
						</th>
						<th>
							<a href="#" data-ng-click="orderByField='TOTAL_INCOME'; reverseSort = !reverseSort">TOTAL INCOME
								<span data-ng-show="orderByField == 'TOTAL_INCOME'"><span data-ng-show="!reverseSort" class=" fa fa-angle-up"></span><span data-ng-show="reverseSort" class=" fa fa-angle-down"></span></span>
							</a>
						</th>
					</tr>
				</thead>
				<tbody>
					<tr data-ng-repeat-start="inventory in inventoryResults|orderByField:reverseSort">
						<td>
							<button type="button" class="btn btn-drpdown btn-sm" data-ng-click="toggle_product_informationResults = !toggle_product_informationResults">
								<span data-ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_product_informationResults==true]"></span>
							</button>
	              		</td>
						<td>{{inventory.PRODUCT_PHOTO}}</td>
						<td>{{inventory.PRODUCT_NAME}}</td>
						<td>{{inventory.PRODUCT_SIZES}}</td>
						<td>{{inventory.FOR_SALE}}</td>
						<td>{{inventory.SOLD}}</td>
						<td>{{inventory.PURCHASED_PRICE}}</td>
						<td>{{inventory.RETAIL_PRICE}}</td>
						<td>{{inventory.TOTAL_REVENUE}}</td>
						<td>{{inventory.TOTAL_INCOME}}</td>
					</tr>
					<tr data-ng-show="toggle_product_informationResults" data-ng-animate="'box'" data-ng-repeat-end>
						<td colspan="9">
							<table class="table table-hover">
								<tr>
									<th>ITEM_ID</th>
									<th>PRIME_LINE_NO</th>
									<th>STATUS_QUANTITY</th>
								</tr>
								<tr data-ng-repeat="order_line in order_release.ORDER_ARRAY">
									<td>{{order_line.ITEM_ID.trim()}}</td>
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