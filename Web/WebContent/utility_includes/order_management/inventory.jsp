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
		$scope.isActive = false;
		$scope.isActive2 = false;
		$scope.clicked = false;
		$scope.button = "Full Search";
		$scope.button2 = " Quick Search";
		$scope.quickSearch = false;
		$scope.autoSearch = function() {
			var itemId = $location.search().item;

			console.log('itemId::' + itemId);

			if (itemId) {
				alert(itemId);
			}
		}

		$scope.searchAll = function(search) {

			$location.search('item', search.item);

			$scope.searchOMSInventory(search);

			$('.weather-bg').show();

		};

		$scope.searchQuick = function(search) {
			$scope.searchOMSInventory2(search);
			$('.weather-bg').show();
		};

		$scope.searchOMSInventory2 = function(search) {
			$scope.isActive2 = true;
			$scope.clicked = true;
			$scope.quickSearch = true;
			$scope.button2 = "Processing...";
			$http({
				method : 'POST',
				url : post_url,
				data : "item=" + search.item + "&type=5",
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.omsSummary = data.oms.summary;
				$scope.efcSummary = data.oms.efc_summary;
				$scope.efcDetails = data.oms.efc_inventory;
				if (!$scope.efcDetails.length == 0) {
					$scope.toggle_EFCDetails = true;
				}
				$scope.rdcSummary = data.oms.rdc_summary;
				$scope.rdcDetails = data.oms.rdc_inventory;
				if (!$scope.rdcDetails.length == 0) {
					$scope.toggle_RDCDetails = true;
				}
				$scope.dsvSummary = data.oms.dsv_summary;
				$scope.dsvDetails = data.oms.dsv_inventory;
				if (!$scope.dsvDetails.length == 0) {
					$scope.toggle_DSVDetails = true;
				}

				$scope.searchEFCInventory(search);

				$scope.isActive2 = false;
				$scope.clicked = false;
				$scope.button2 = " Quick Search";
			});

		};

		$scope.tag_click = function() {

			$("#imageCell").empty();
			var data = $('i.fa.fa-tags').attr('id');
			console.log(data);
			if (data != "null" && data != "") {
				$("#imageCell").html("<img src="+ data +">");
			} else {
				$("#imageCell").html("No Image Available");
			}
			$("#mhimage h4").html("Item Image");

		};

		$scope.searchOMSInventory = function(search) {
			$scope.quickSearch = false;
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			$http({
				method : 'POST',
				url : post_url,
				data : "item=" + search.item + "&type=1",
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.omsSummary = data.oms.summary;
				$scope.efcSummary = data.oms.efc_summary;
				$scope.efcDetails = data.oms.efc_inventory;
				if (!$scope.efcDetails.length == 0) {
					$scope.toggle_EFCDetails = true;
				}
				$scope.rdcSummary = data.oms.rdc_summary;
				$scope.rdcDetails = data.oms.rdc_inventory;
				if (!$scope.rdcDetails.length == 0) {
					$scope.toggle_RDCDetails = true;
				}
				$scope.dsvSummary = data.oms.dsv_summary;
				$scope.dsvDetails = data.oms.dsv_inventory;
				if (!$scope.dsvDetails.length == 0) {
					$scope.toggle_DSVDetails = true;
				}
				$scope.storeSummary = data.oms.store_summary;
				$scope.storeDetails = data.oms.store_inventory;
				if (!$scope.storeDetails.length == 0) {
					$scope.toggle_StoreDetails = true;
				}
				$scope.givSummary = data.giv.giv_summary;
				$scope.givDetails = data.giv.giv_inventory;
				//$scope.getSearchHistory();

				$scope.searchEFCInventory(search);
			});

		};

		$scope.searchEFCInventory = function(search) {
			$http({
				method : 'POST',
				url : post_url,
				data : "item=" + search.item + "&type=2",
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.efcResults = data;

				$scope.searchFSInventory(search);
			});
		};

		$scope.searchFSInventory = function(search) {
			$http({
				method : 'POST',
				url : post_url,
				data : "item=" + search.item + "&type=3",
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.fsResults = data;

				$scope.searchEcommInventory(search);
			});
		};

		$scope.searchEcommInventory = function(search) {
			$http({
				method : 'POST',
				url : post_url,
				data : "item=" + search.item + "&type=4",
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.ecommResults = data.ecomm_array;
				$scope.ecommAvailable = data.ecomm_array[0].AVAILABLE_STOCK;
				$scope.ecommAvailability = data.ecomm_array[0].AVAILABILITY;
				//$scope.getSearchHistory();
				$scope.isActive = false;
				$scope.clicked = false;
				$scope.button = "Full Search";

			});
		};

		$scope.$on('$locationChangeStart', function() {
			if (!jQuery.isEmptyObject($location.search()))
				$scope.autoSearch($location.search());
		});

		/* $scope.getSearchHistory = function() {
			//-- set search history
			STOREELFSearchService.setRequestUri(post_url);
			STOREELFSearchService.getHistory();
			STOREELFSearchService.injectIntoScope($scope);
			$scope.search_history_data = STOREELFSearchService.getData();

		}; */

		$scope.autoSearch = function(hash) {
			//$scope.getSearchHistory();
			$scope.searchAll(hash);
			STOREELFSearchService.autoFillForm($scope.search, hash);
		};

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
  text-align:center;
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

		<div class="col-sm-12">
			<section class="panel">

				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form"
						ng-enter="searchAll(search)">
						<div class="form-group">
							<label class="col-sm-2 control-label">Item ID</label>
							<div class="col-sm-3">
								<input name="item" class="form-control" ng-model="search.item"
									onclick="" value="" type="text" ng-init="search.item=''"
									placeholder="Item ID" required />
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">


								<button
									ng-disabled="form.$invalid || isLoading() || (clicked == true)"
									class="btn"
									ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive2]"
									type="button"data-toggle="tooltip" title="Gets everything but the Store info"
									ng-click="searchQuick(search)">{{button2}}</button>
								<button
									ng-disabled="form.$invalid || isLoading() || (clicked == true)"
									class="btn" data-toggle="tooltip" title="Fills all tables"
									ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]"
									type="submit" ng-click="searchAll(search)">{{button}}</button>

								<!-- <div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-default dropdown-toggle" type="button">
										History <span class="caret"></span>
									</button>
									<ul role="menu" class="dropdown-menu">
										<li><a href="#"><i class="fa fa-clock-o"></i> Clear
												Search History (I don't work yet ... sry)</a></li>
										<li class="divider"></li>
										<li ng-repeat="result in search_history_data"
											ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a
											href="#"><i class="fa fa-clock-o"
												ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
									</ul>
								</div> -->
								<!-- /btn-group -->
							</div>
						</div>
					</form>
				</div>
			</section>


		</div>


		<div class="col-sm-8">

			<div class="col-sm-12">
				<!-- OMS Results Table -->
				<section class="panel">
					<div class="panel-heading">Omni Channel Summary</div>
					<div class="weather-bg">
						<div class="panel-body" ng-if="omsSummary">
							<div class="row">
								<div class="col-xs-6">
									<i style="cursor: pointer" class="fa fa-tags" fa-tags="1"
										ng-click="tag_click()" data-toggle="modal"
										data-target="#imgModal" id="{{omsSummary.IMG_URL}}"></i> <a
										class="btn btn-xs btn-round btn-highlight"
										ng-href="/Utility/OrderManagement/item#/search?items={{omsSummary.ITEM_ID.trim()}}&webids=&upcs=">{{omsSummary.ITEM_ID.trim()}}</a>
								</div>
								<div class="col-xs-6">
									<p style="margin: 0px;">Ecom Inventory</p>
									<div class="degree" style="margin-top: -15px; font-size: 60px"
										ng-if="ecommAvailability=='In Stock'">{{ecommAvailable}}</div>
									<div class="degree" style="margin-top: -15px; font-size: 45px"
										ng-if="ecommAvailability=='Out of Stock'">Out of Stock</div>
								</div>
							</div>
						</div>
					</div>

					<!-- Modal -->
					<div class="modal fade" id="imgModal" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header" id="mhimage">
									<button type="button" class="close" data-dismiss="modal">
										<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
									</button>
									<h4 class="modal-title" id="myModalLabel">Item Image</h4>
								</div>
								<div class="modal-body">
									<table id="resultsTable"
										class="table table-hover table-condensed table-bordered"
										style="border: none;">
										<thead>
											<tr>
												<th style="border-top-left-radius: 5px; border: none;">Image</th>
											</tr>
										</thead>
										<tr style="font-size: 12px">
											<td id="imageCell"></td>
										</tr>
									</table>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Close</button>
								</div>
							</div>
						</div>
					</div>

					<footer class="weather-category">
						<p
							style="text-align: center; font-size: 16px; line-height: .5; height: 10px;">{{omsSummary.SHORT_DESCRIPTION}}</p>
						<ul
							style="border-top: 1px solid rgb(192, 192, 192); padding-top: 10px;">
							<li class="active"><h5>Total Supply</h5>{{omsSummary.TOTAL_SUPPLY}}</li>
							<li><h5>Total Demand</h5>{{omsSummary.TOTAL_DEMAND}}</li>
							<li><h5>Available Onhand</h5>{{omsSummary.ONHAND_AVAILABLE_QUANTITY}}</li>
							<li><h5>Last Sent to Ecom</h5>{{omsSummary.ALERT_RAISED_ON}}</li>
							<li><h5>Store Clearance Item</h5>{{omsSummary.STORE_CLEARANCE_ITEM}}</li>

						</ul>
					</footer>
				</section>
			</div>

			<div class="col-sm-12">
				<!-- WMOS Results Table -->
				<section class="panel">
					<table class="table table-hover table-condensed table-bordered">
						<thead>
							<tr>
								<th colspan="10">WMOS Inventory</th>
							</tr>
							<tr>
								<th>EFC</th>
								<th>Item Type</th>
								<th>Active</th>
								<th>Case</th>
								<th>Trans</th>
								<th>Carton</th>
								<th>Unalloc Case</th>
								<th>Unalloc Trans</th>
								<th>Total Alloc Inv</th>
								<th>Total Un-Alloc Inv</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="item in efcResults">
								<td>{{item.SHIP_NODE}}</td>
								<td>{{item.ITEM_TYPE}}</td>
								<td>{{item.ACTIVE}}</td>
								<td>{{item.CASE}}</td>
								<td>{{item.TRANS}}</td>
								<td>{{item.CARTON}}</td>
								<td>{{item.UNALLOC_CASE}}</td>
								<td>{{item.UNALLOC_TRANS}}</td>
								<td>{{item.ALLOC_TOTAL}}</td>
								<td>{{item.UNALLOC_TOTAL}}</td>
							</tr>
						</tbody>
					</table>
				</section>
			</div>
		</div>

		<div class="col-sm-4">
			<div class="col-sm-12">
				<!-- GIV Results Table -->
				<section class="panel">
					<table class="table table-hover table-condensed table-bordered">
						<thead>
							<tr>
								<th colspan="4">GIV</th>
							</tr>
							<tr>
								<th>Ship Node</th>
								<th>Supply</th>
								<th>Demand</th>
								<th>Available To Fulfill</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<!-- | orderObjectBy:'SHIPNODE_KEY':true"> -->
								<td colspan="1">
									<button type="button" class="btn btn-drpdown btn-sm"
										ng-click="toggle_EFCDetails = !toggle_EFCDetails">
										<span
											ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_EFCDetails==true]">EFC</span>
									</button>
								</td>
								<td>{{efcSummary.SUPPLY}}</td>
								<td>{{efcSummary.DEMAND}}</td>
								<td>{{efcSummary.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr ng-show="toggle_EFCDetails" ng-animate="'box'"
								ng-repeat="efcDetail in efcDetails | orderObjectBy:'SHIPNODE_KEY'">
								<td>{{efcDetail.SHIPNODE_KEY}}</td>
								<td>{{efcDetail.SUPPLY}}</td>
								<td>{{efcDetail.DEMAND}}</td>
								<td>{{efcDetail.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr>
								<!-- | orderObjectBy:'SHIPNODE_KEY':true"> -->
								<td colspan="1">
									<button type="button" class="btn btn-drpdown btn-sm"
										ng-click="toggle_RDCDetails = !toggle_RDCDetails">
										<span
											ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_RDCDetails==true]">RDC</span>
									</button>
								</td>
								<td>{{rdcSummary.SUPPLY}}</td>
								<td>{{rdcSummary.DEMAND}}</td>
								<td>{{rdcSummary.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr ng-show="toggle_RDCDetails" ng-animate="'box'"
								ng-repeat="rdcDetail in rdcDetails | orderObjectBy:'SHIPNODE_KEY'">
								<td>{{rdcDetail.SHIPNODE_KEY}}</td>
								<td>{{rdcDetail.SUPPLY}}</td>
								<td>{{rdcDetail.DEMAND}}</td>
								<td>{{rdcDetail.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr>
								<!-- | orderObjectBy:'SHIPNODE_KEY':true"> -->
								<td colspan="1">
									<button type="button" class="btn btn-drpdown btn-sm"
										ng-click="toggle_DSVDetails = !toggle_DSVDetails">
										<span
											ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_DSVDetails==true]">DSV</span>
									</button>
								</td>
								<td>{{dsvSummary.SUPPLY}}</td>
								<td>{{dsvSummary.DEMAND}}</td>
								<td>{{dsvSummary.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr ng-show="toggle_DSVDetails" ng-animate="'box'"
								ng-repeat="dsvDetail in dsvDetails | orderObjectBy:'SHIPNODE_KEY'">
								<td>{{dsvDetail.SHIPNODE_KEY}}</td>
								<td>{{dsvDetail.SUPPLY}}</td>
								<td>{{dsvDetail.DEMAND}}</td>
								<td>{{dsvDetail.AVAILABLE_TO_FULFILL}}</td>
							</tr>
							<tr ng-show="!quickSearch"
								>
								<!-- | orderObjectBy:'SHIPNODE_KEY':true"> -->
								<td colspan="1">
									<button
										ng-disabled="form.$invalid || isLoading() || quickSearch"
										class="btn btn-popup btn-sm" data-toggle="modal"
										data-target="#storesPopupModal" ng-click="reverse='first'">Stores</button>
									
								</td>
								<td >{{storeSummary.SUPPLY}}</td>
								<td >{{storeSummary.DEMAND}}</td>
								<td >{{storeSummary.AVAILABLE_TO_FULFILL}}</td>
							</tr>
						</tbody>
					</table>
					<div class="grayedout" ng-show="quickSearch"> Stores Unavailable in Quick Search </div>
				</section>
			</div>

			<!-- GIV Section / Popup  -->
			<div class="col-sm-12 modal fade" id="storesPopupModal" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">Item ID:
								{{omsSummary.ITEM_ID}}</h4>
						</div>
						<div class="modal-body">
							<section class="panel">
								<!--  Commented based on removing safety factor displayed at item level -->
								<!-- <h5 ng-repeat="storeDetail in storeDetails" ng-show="$first">
								Safety Factor Quantity: <font color="red">{{storeDetail.ONHAND_SAFETY_FACTOR_QTY}}</font>
								<font color="blue"> <i>(Safety stock is at a Item Level.  For more details use Safety factor screen)</i></font>
							</h5> -->
								<table class="table table-hover table-bordered table-condensed">
									<thead>
										<tr>
											<th colspan="4">Stores</th>
										</tr>
										<tr>
											<th style="width: 20%" id="orderByArrow"
												ng-click="predicate = 'SHIPNODE_KEY'; reverse=!reverse">
												<input placeholder="Ship Node" style="width: 90%"
												class="filter" ng-model="ship_node"> <span
												style="float: right" ng-show="reverse == 'first'"> <span
													style="position: absolute"> <i
														class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
														<i class="fa fa-sort-asc" id="orderArrow"></i>
												</span>
											</span> <span style="float: right"
												ng-show="predicate == 'SHIPNODE_KEY'"> <span
													ng-show="!reverse"> <i class="fa fa-sort-desc"
														id="orderArrow"></i></span> <span ng-show="reverse"> <i
														class="fa fa-sort-asc" id="orderArrow"></i></span>
											</span>
											</th>
											<th id="orderByArrow"
												ng-click="predicate = 'SUPPLY'; reverse=!reverse">Supply
												<span style="float: right" ng-show="reverse == 'first'">
													<span style="position: absolute"> <i
														class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
														<i class="fa fa-sort-asc" id="orderArrow"></i>
												</span>
											</span> <span ng-show="predicate == 'SUPPLY'" style="float: right">
													<span ng-show="!reverse"> <i class="fa fa-sort-desc"
														id="orderArrow"></i></span> <span ng-show="reverse"> <i
														class="fa fa-sort-asc" id="orderArrow"></i></span>
											</span>
											</th>
											<th id="orderByArrow"
												ng-click="predicate = 'DEMAND'; reverse=!reverse">Demand
												<span style="float: right" ng-show="reverse == 'first'">
													<span style="position: absolute"> <i
														class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
														<i class="fa fa-sort-asc" id="orderArrow"></i>
												</span>
											</span> <span ng-show="predicate == 'DEMAND'" style="float: right">
													<span ng-show="!reverse"> <i class="fa fa-sort-desc"
														id="orderArrow"></i></span> <span ng-show="reverse"> <i
														class="fa fa-sort-asc" id="orderArrow"></i></span>
											</span>
											</th>
											<th id="orderByArrow"
												ng-click="predicate = 'AVAILABLE_TO_FULFILL'; reverse=!reverse">Available
												<span style="float: right" ng-show="reverse == 'first'">
													<span style="position: absolute"> <i
														class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
														<i class="fa fa-sort-asc" id="orderArrow"></i>
												</span>
											</span> <span ng-show="predicate == 'AVAILABLE_TO_FULFILL'"
												style="float: right"> <span ng-show="!reverse">
														<i class="fa fa-sort-desc" id="orderArrow"></i>
												</span> <span ng-show="reverse"> <i class="fa fa-sort-asc"
														id="orderArrow"></i></span>
											</span>
											</th>
										</tr>
									</thead>
									<tbody>
										<tr
											ng-repeat="storeDetail in storeDetails | orderBy:predicate:reverse | filter:{SHIPNODE_KEY : ship_node}">
											<td>{{storeDetail.SHIPNODE_KEY}}</td>
											<td>{{storeDetail.SUPPLY}}</td>
											<td>{{storeDetail.DEMAND}}</td>
											<td>{{storeDetail.AVAILABLE_TO_FULFILL}}</td>
										</tr>
									</tbody>
								</table>
							</section>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>

			<div class="col-sm-12">
				<!-- FS Results Table -->
				<section class="panel">
					<table class="table table-hover table-condensed table-bordered">
						<thead>
							<tr>
								<th colspan="4">Fashion Sales</th>
							</tr>
							<tr>
								<th>Ship Node</th>
								<th>Quantity</th>
								<th>Store Expected</th>
								<th>On Order</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="item in fsResults">
								<td>{{item.SHIP_NODE}}</td>
								<td>{{item.QUANTITY}}</td>
								<td>{{item.STORE_EXPECTED}}</td>
								<td>{{item.ON_ORDER}}</td>
							</tr>
						</tbody>
					</table>
				</section>
			</div>



		</div>



	</div>
</div>