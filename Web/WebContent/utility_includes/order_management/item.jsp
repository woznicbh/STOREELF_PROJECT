<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementItemSearchApp', ['search', 'enterkey']);

 	function ItemSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/item';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
 		$scope.searchItems = function(search) {
 			console.log('search::' + search.items);
 			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "items=" + search.items + "&webids=" + search.webids + "&upcs=" + search.upcs,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				STOREELFSearchService.updateAddressBar('items', search.items);
				STOREELFSearchService.updateAddressBar('webids', search.webids);
				STOREELFSearchService.updateAddressBar('upcs', search.upcs);
				$scope.itemResults = data.item_details;
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
 			$scope.searchItems(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>


<div ng-app="OrderManagementItemSearchApp">
	<div ng-controller="ItemSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Item</li>
			</ol>
		</div>
		<div class="col-sm-12">

		<section class="panel">

		<div class="panel-body">

		<form name="form" class="form-horizontal ng-valid ng-dirty" role="form" ng-enter="searchItems(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Item ID</label>
					<div class="col-sm-3">
						<input name="items" class="form-control ng-pristine ng-valid" ng-model="search.items" onclick="" value="" type="text" ng-init="search.items=''" placeholder="Item ID">
					</div>
			</div><!-- /form-group -->
			<div class="form-group">
				<label class="col-sm-2 control-label">Web ID</label>
					<div class="col-sm-3">
						<input name="webids" class="form-control ng-valid ng-dirty" ng-model="search.webids" onclick="" value="" type="text" ng-init="search.webids=''" placeholder="Web ID">
					</div>
			</div><!-- /form-group -->
			<div class="form-group">
				<label class="col-sm-2 control-label">UPC</label>
					<div class="col-sm-3">
						<input name="upcs" class="form-control ng-valid ng-dirty" ng-model="search.upcs" onclick="" value="" type="text" ng-init="search.upcs=''" placeholder="UPC">
					</div>
			</div><!-- /form-group -->
			<div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="!(!!search.items || !!search.webids || !!search.upcs) || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchItems(search); resetForm()">{{button}}</button>
				<!--	<div class="btn-group">
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
		<section class="panel" id="scrollSection">
			<table id= "resultsTable" class="table table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th>Item Id</th>
						<th>Web Id</th>
						<th>Short Desc</th>
						<th>UPC('s)</th>
						<th>Product Line</th>
						<th>Item Type</th>
						<th>Nomadic</th>
						<th>Ship Node Source</th>
						<th>Safety Qty</th>
						<th>Primary Supplier</th>

					</tr>
				</thead>
				<tbody>
					<tr ng-repeat-start="item in itemResults" >
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails"><i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_ItemDetails==true]">
						{{item.ITEM_ID}}</i></td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_WEB_ID}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.SHORT_DESCRIPTION}}</td>
						<td>
							<!-- Button trigger modal -->
							<div id="vb">
								<button class="btn btn-popup" data-toggle="modal" data-target="#myModal" id="viewButton">
								  View
								</button>
							</div>
							<script>
							$(document).ready(function(){
								$('#vb button').click(function(){
									var data = $('td:first', $(this).parents('tr')).text();
								$(".modal-header h4").html("UPC('s) for item: " + data);
							    });

								$('td').click(function(){
								$("#imageCell").empty();
								var data = $('td:first', $(this).parents('tr')).attr('id');
								if (data != "null" && data != ""){
									$("#imageCell").html("<img src="+ data +">");
								} else {
									$("#imageCell").html("No Image Available");
								}
								$(".modal-header h4").html("Item Image");


							    });
							});

							</script>

							<!-- Modal -->
							<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							  <div class="modal-dialog">
							    <div class="modal-content">
							      <div class="modal-header">
							        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
							        <h4 class="modal-title" id="myModalLabel">UPC('s) for Item Selected</h4>
							      </div>
							      <div class="modal-body" >
							      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
							      	<thead>
								      	<tr>
								      		<th style="border-top-left-radius: 5px;border: none;">Name</th>
								      		<th style="border-top-right-radius: 5px;border: none;">UPC</th>
								      	</tr>
							      	</thead>
								      	<tr ng-repeat="item in item.UPC" style="font-size: 12px">
								      		<td>{{item.ALIAS_NAME}}</td>
								      		<td>{{item.ALIAS_VALUE}}</td>
								      	</tr>
							      </table>
							      </div>
							      <div class="modal-footer">
							        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
							      </div>
							    </div>
							  </div>
							</div>
						</td>

						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.PRODUCT_LINE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.ITEM_TYPE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_NOMADIC}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_SHIP_NODE_SOURCE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.ONHAND_SAFETY_FACTOR_QTY}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.PRIMARY_SUPPLIER}}</td>

					</tr>
					<tr ng-show="toggle_ItemDetails" ng-animate="'box'" class="blueBG">

						<th>Item Image</th>
						<th>Breakable</th>
						<th>Gift Wrap</th>
						<th>Baggage</th>
						<th>Hazardous</th>
						<th>Safety Pct</th>
						<th>Direct Ship</th>
						<th>Ship Alone</th>
						<th>Cage</th>
						<th>Plastic GC</th>
					</tr>
					<tr ng-repeat-end ng-show="toggle_ItemDetails" ng-animate="'box'">
						<td id="{{item.IMG_URL}}">
							<button class="btn btn-popup" data-toggle="modal" data-target="#imgModal" id="imgButton">
								Open
							</button>
						</td>
						<td>{{item.EXTN_BREAKABLE}}</td>
						<td>{{item.ALLOW_GIFT_WRAP}}</td>
						<td>{{item.EXTN_BAGGAGE}}</td>
						<td>{{item.IS_HAZMAT}}</td>
						<td>{{item.ONHAND_SAFETY_FACTOR_PCT}}</td>
						<td>{{item.EXTN_DIRECT_SHIP_ITEM}}</td>
						<td>{{item.EXTN_SHIP_ALONE}}</td>
						<td>{{item.EXTN_CAGE_ITEM}}</td>
						<td>{{item.EXTN_IS_PLASTIC_GIFT_CARD}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
	<!-- Modal -->
							<div class="modal fade" id="imgModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							  <div class="modal-dialog">
							    <div class="modal-content">
							      <div class="modal-header">
							        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
							        <h4 class="modal-title" id="myModalLabel">Item Image</h4>
							      </div>
							      <div class="modal-body" >
							      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
							      	<thead>
								      	<tr>
								      		<th style="border-top-left-radius: 5px;border: none;">Image</th>
								      	</tr>
							      	</thead>
								      	<tr style="font-size: 12px">
								      		<td id="imageCell"></td>
								      	</tr>
							      </table>
							      </div>
							      <div class="modal-footer">
							        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
							      </div>
							    </div>
							  </div>
							</div>
</div>