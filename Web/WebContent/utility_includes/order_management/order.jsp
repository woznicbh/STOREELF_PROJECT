<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderSearchApp', ['search', 'enterkey']);

 	function OrderSearchController($scope, $http, $log, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/order';
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
			    data: "orderNumber=" + search.orderNumber + "&isPOCorder="+search.isPOCorder,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				
				//these vars need to be reset first to prevent crash problems
				
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
				$location.search('orderNumber', search.orderNumber);
				$scope.order_date = data.order_lines[0].ORDER_DATE;
				$scope.orders = data.order_lines;
				$scope.headercharges=data.header_charges;
				$scope.linecharges=data.line_charges;
				$scope.chainedOrders = data.chained_order_lines;
				$scope.orderheaders = data.order_headers;
				$scope.receipts = data.receipts;
				$scope.create_ts = data.createts;
				//$scope.getSearchHistory();

		 		$scope.searchedOrderNo = search.orderNumber;
		 		$scope.lineTax = data.order_line_tax;
		 		$scope.ordertotal=data.order_total;
		 		$scope.promo=data.promotioncode;
		 		$scope.promoname=data.promotionname;
		 		$scope.promonameblank=data.promotionnameblank;
		 		
			});
			

		};
		
		$scope.getChainedOrder = function(orderNo, type) {
			if (type == 'PO')
			{
				$scope.orderType = "Purchase Order";
				$scope.lineHeader = "SO Line No";
				$scope.orderHeader = "SO Order No"
				$scope.orderFilter = orderNo;
			}
			else
			{
				$scope.orderType = "Sales Order";
				$scope.lineHeader = "PO Line No";
				$scope.orderHeader = "PO Order No"
				$scope.orderFilter = orderNo.substring(0,10);
			}
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
 			$scope.searchOrders(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementOrderSearchApp">
	<div ng-controller="OrderSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Order</li>
			</ol>
		</div>

			<div class="col-sm-12">
				<section class="panel">
					<div class="panel-body">
						<form name="form" class="form-horizontal" role="form" ng-enter="searchOrders(search)">
							<div class="form-group">
								<label class="col-sm-2 control-label">Order No</label>
							    <div class="col-sm-3">
									<input name="orderNumber" class="form-control" ng-model="search.orderNumber" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No" required/>
							    
							    	<label class="checkbox-inline"><input type="checkbox" id="inlineCheckbox1" ng-model="search.isPOCorder" ng-init="search.isPOCorder=false">is POC Order?</label>
							    </div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-model="search.button" ng-click="searchOrders(search)">{{button}}</button>
									<!--<div class="btn-group">
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

<!-- start -->
		<div class="col-sm-12">
			<section class="panel">
			<header class="panel-heading"  style="float: left">
						Order Header details:
				</header>
				<header class="panel-heading"  style="float: right">
				
					
				Order Create Timestamp: {{create_ts}}
				</header>
				<table class="table table-hover table-condensed table-bordered table-responsive">
					<thead>
						<tr>
							<th>Order No</th>
							<th>Order Date</th>
							<th>Onhold</th>
							<th>Address</th>
							<th>State</th>
							<th>Receipt(s)</th>
							<th>Header Charges</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="order in orderheaders">
							<td>{{order.ORDER_NO}}</td>
							<td>{{order_date}}</td>
							<td>{{order.ON_HOLD}}</td>
							<td>{{order.ADDRESS}}</td>
							<td>{{order.STATE}}</td>
							<td><a class="btn btn-xs btn-popup"><i style="cursor:pointer"class="fa fa-list-alt" fa-tags="1" data-toggle="modal" data-target="#receiptModal"></i> {{order.COUNT}}</a></td>
							<td width=10%><a class="btn btn-xs btn-popup" style="padding: 2px;margin: 2px;"><i style="cursor:pointer"class="fa fa-arrow-down" fa-tags="1" data-toggle="modal" data-target="#headerchargesModal"> Header Charges</i></a></td>
						</tr>
					</tbody>
				</table>
				</section>
			</div>
<!-- end -->

			<div class="col-sm-12">
			<section class="panel">
			<header class="panel-heading"  style="float: left">
						Order Line details:
				</header>
				<a class="btn btn-xs btn-popup panel-heading" style="float: right; padding: 2px;margin: 2px;"><i style="cursor:pointer" class="fa fa-arrow-down" fa-tags="1" data-toggle="modal" data-target="#itemDetailsModal"> Item Details</i></a>
				<a class="btn btn-xs btn-popup panel-heading" style="float: right; padding: 2px;margin: 2px;"><i style="cursor:pointer" class="fa fa-arrow-down" fa-tags="1" data-toggle="modal" data-target="#linechargesModal"> Line Charges</i></a>
				<table class="table table-hover table-condensed table-bordered table-responsive">
				
					<thead>
						<tr>
							<th>Line No</th>
							<th>Order No</th>
							<th>Chained Order</th>
							<th>From Node</th>
							<th>Status</th>
							<th>Item ID</th>
							<th>Quantity</th>
							<th>Ship To</th>
							<th>Carrier Service Code</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="order in orders | orderBy:'PRIME_LINE_NO' | filter: {ORDER_NO: searchedOrderNo}">
							<td>{{order.PRIME_LINE_NO}}</td>
							<td>{{order.ORDER_NO}}</td>
							<td id = "{{order.ORDER_NO}}"><a ng-if="order.CHAINED_ORDER.trim() !== 'N/A'" class="btn btn-xs btn-round btn-info" ng-click="getChainedOrder(order.CHAINED_ORDER_NO, order.CHAINED_ORDER)"><i style="cursor:pointer"class="fa fa-folder" fa-tags="1" data-toggle="modal" data-target="#chainedOrderModal"/> {{order.CHAINED_ORDER_NO}}</a></td>
							<td>{{order.SHIPNODE_KEY}}</td>
							<td>{{order.STATUS}}</td>
							<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/item#/search?items={{order.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order.ITEM_ID.trim()}}</a></td>
							<td>{{order.STATUS_QUANTITY}}</td>
							<td>{{order.SHIP_TO}}</td>
							<td>{{order.CARRIER_SERVICE_CODE}}</td>
						</tr>
					</tbody>
				</table>
				</section>
			</div>

			<div class="col-sm-12">
			<section class="panel">
			<header class="panel-heading"  style="float: left">
						Shopping Cart:
				</header>
				
				<table class="table table-hover table-condensed table-bordered table-responsive">
				<a class="btn btn-xs btn-popup panel-heading" style="float: right; padding: 2px;margin: 2px;"><i style="cursor:pointer" class="fa fa-arrow-down" fa-tags="1" data-toggle="modal" data-target="#OrderTotalModal"> Order Total</i></a>
					<thead>
						<tr>
							<th>Line No</th>
							<th>Item ID</th>
							<th>Item Description</th>
							<th>Fullfillment</th>
							<th>Ordered Qty</th>
							<th>List Price</th>
							<th>Price Each</th>
							<th>Total Price</th>
							<th>Tax Percentage</th>
							<th>Tax</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="lt in lineTax">
							<td>{{lt.LINE_NO}}</td>
							<td>{{lt.ITEM_ID}}</td>
							<td>{{lt.DESCRIPTION}}</td>
							<td>{{lt.FULFILLMENT}}</td>
							<td>{{lt.QTY}}</td>
							<td>{{lt.LIST_PRICE}}</td>
							<td>{{lt.PRICE_EACH}}</td>
							<td>{{lt.TOTAL_PRICE}}</td>
							<td>{{lt.TAXPERCENTAGE}}</td>
							<td>{{lt.TAX}}</td>
						</tr>
					</tbody>
				</table>
				</section>
			</div>
			
					<!-- Modal -->
			<div class="modal fade" id="receiptModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:35%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel">Order Receipts</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
			      	<thead>
					<tr>
						<th>Receipt Id</th>
						<th>Ship Node</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="receipt in receipts ">
						<td>{{receipt.RECEIPTID}}</td>
						<td>{{receipt.SHIPNODE}}</td>
					</tr>
				</tbody>
			      </table>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
			      </div>
			    </div>
			  </div>
			</div>

			<div class="modal fade" id="itemDetailsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:95%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel">Item Details</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
			      	<thead>
						<tr>
							<th>Line No</th>
							<th>Item ID</th>
							<th>Gift Flag</th>
							<th>Gift Wrap Flag</th>
							<th>Ship Alone Flag</th>
							<th>Cage Item Flag</th>
							<th>Gift Card Flag</th>
							<th>Breakable Flag</th>
							<th>Item Allow Gift Wrap</th>
							<th>Baggage Flag</th>
							<th>Hazmat Flag</th>
							<th>DS Item Flag</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="order in orders | orderBy:'PRIME_LINE_NO' | filter: {ORDER_NO: searchedOrderNo}">
							<td>{{order.PRIME_LINE_NO}}</td>
							<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/item#/search?items={{order.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order.ITEM_ID.trim()}}</a></td>
							<td>{{order.GIFT_FLAG}}</td>
							<td>{{order.GIFT_WRAP}}</td>
							<td>{{order.EXTN_SHIP_ALONE}}</td>
							<td>{{order.EXTN_CAGE_ITEM}}</td>
							<td>{{order.EXTN_IS_PLASTIC_GIFT_CARD}}</td>
							<td>{{order.EXTN_BREAKABLE}}</td>
							<td>{{order.ALLOW_GIFT_WRAP}}</td>
							<td>{{order.EXTN_BAGGAGE}}</td>
							<td>{{order.IS_HAZMAT}}</td>
							<td>{{order.DS_ITEM}}</td>
						</tr>
					</tbody>
			      </table>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
			      </div>
			    </div>
			  </div>
			</div>
			<div class="modal fade" id="headerchargesModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:80%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel">Header Charge Details</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
			      	<thead>
						<tr>
							<th>Order No</th>
							<th>Record Type</th>
							<th>Charge Category</th>
							<th>Charge Name</th>
							<th>Reference</th>
							<th>Charge</th>
							<th>Invoiced Charge</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="orderch in headercharges | orderBy:'CHARGE_NAME' | filter: {ORDER_NO: searchedOrderNo}">
							<td>{{orderch.ORDER_NO}}</td>
							<td>{{orderch.RECORD_TYPE}}</td>
							<td>{{orderch.CHARGE_NAME}}</td>
							<td>{{orderch.CHARGE_CATEGORY}}</td>
							<td>{{orderch.REFERENCE}}</td>
							<td>{{orderch.CHARGE}}</td>
							<td>{{orderch.INVOICED_CHARGE}}</td>
						</tr>
					</tbody>
			      </table>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="linechargesModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:80%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel">Line Charge Details</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
			      	<thead>
						<tr>
							<th>Order No</th>
							<th>Prime Line No</th>
							<th>Record Type</th>
							<th>Charge Category</th>
							<th>Charge Name</th>
							<th>Reference</th>
							<th>Charge per Unit</th>
							<th>Charge per Line</th>
							<th>Invoiced Charge Per Line</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="orderch in linecharges | orderBy: 'PRIME_LINE_NO' | filter: {ORDER_NO: searchedOrderNo}">
							<td>{{orderch.ORDER_NO}}</td>
							<td>{{orderch.PRIME_LINE_NO}}</td>
							<td>{{orderch.RECORD_TYPE}}</td>
							<td>{{orderch.CHARGE_NAME}}</td>
							<td>{{orderch.CHARGE_CATEGORY}}</td>
							<td>{{orderch.REFERENCE}}</td>
							<td>{{orderch.CHARGEPERUNIT}}</td>
							<td>{{orderch.CHARGEPERLINE}}</td>
							<td>{{orderch.INVOICED_CHARGE_PER_LINE}}</td>
						</tr>
					</tbody>
			      </table>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
			      </div>
			    </div>
			  </div>
			</div>

			<div class="modal fade" id="chainedOrderModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:95%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel">{{orderType}}</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
			      	<thead>
						<tr>
							<th>Line No</th>
							<th>Order No</th>
							<th>From Node</th>
							<th>Status</th>
							<th>Item ID</th>
							<th>Quantity</th>
							<th>Ship To</th>
							<th>Carrier Service Code</th>
							<th>{{lineHeader}}</th>
							<th>{{orderHeader}}</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="order in chainedOrders | orderBy:'PRIME_LINE_NO' | filter: {ORDER_NO: orderFilter}">
							<td>{{order.PRIME_LINE_NO}}</td>
							<td>{{order.ORDER_NO}}</td>
							<td>{{order.SHIPNODE_KEY}}</td>
							<td>{{order.STATUS}}</td>
							<td><a class="btn btn-xs btn-round btn-popup" ng-href="/Utility/OrderManagement/item#/search?items={{order.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order.ITEM_ID.trim()}}</a></td>
							<td>{{order.STATUS_QUANTITY}}</td>
							<td>{{order.SHIP_TO}}</td>
							<td>{{order.CARRIER_SERVICE_CODE}}</td>
							<td>{{order.CHAINED_PRIME_LINE_NO}}</td>
							<td>{{order.CHAINED_ORDER_NO}}</td>
						</tr>
					</tbody>
			      </table>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="OrderTotalModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog" style="width:25%">
			    <div class="modal-content">
			      <div class="modal-header" id="mhimage">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			        <h4 class="modal-title" id="myModalLabel" align="center" >Order Total</h4>
			      </div>
			      <div class="modal-body" >
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;width: 60%; margin-left:25%;" align="center"  > 
			     
			    <tbody ng-repeat="ot in ordertotal">
						
							<tr>
								<td style="font-weight: bold;">{{ot.SubTotal}}</td>
								<td>{{ot.SubTotalValue}}</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">{{ot.Discount}}</td>
								<td>{{ot.DISCOUNTVALUE}}</td>
							</tr>
							
							<tr>
								<td style="font-weight: bold;">{{ot.Charge_Name}}</td>
								<td>{{ot.Charge_Amount}}</td>
							</tr>
							
							<tr>
								<td style="font-weight: bold;">{{ot.Tax_Name}}</td>
								<td>{{ot.Tax_Amount}}</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">{{ot.Charge_category}}</td>
								<td>{{ot.TAXPERCENTAGE}}</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">{{ot.ChargeCategory}}</td>
								<td>{{ot.CHARGES}}</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">{{ot.OrderTotalField}}</td>
								<td style="font-weight: bold;color:red">{{ot.OrderTotalValue}}</td>
							</tr>
						
				</tbody>
			      </table>
			      
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;width: 80%; margin-left:15%;" align="center" > 
						
						<tr ng-repeat="p in promoname">
							<td style="font-weight: bold;">{{p.PROMOTION_DESCRIPTION_FIELD_NAME}}</td>
							<td style="font-weight: bold;">{{p.PROMOTION_CHARGE_FIELD_NAME}}</td>
						</tr>
						
						<tr ng-repeat="p in promo">
							<td>{{p.PROMOTION_DESCRIPTION}}</td>
							<td>{{p.PROMOTION_CHARGE}}</td>
						</tr>
						
			      </table>
			      
			      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;width: 90%; margin-left:10%;" align="center"  >
			      		
			      		<tr ng-repeat="p in promonameblank">
							<td style="font-weight: bold;color:red;font-size:12px;">{{p.PROMOTION_DESCRIPTION_FIELD_BLANK}}</td>
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
</div>