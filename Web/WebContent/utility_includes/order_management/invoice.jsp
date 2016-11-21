<script type="text/javascript">
	//var	orderSearchApp = angular.module('OrderManagementInvoiceSearchApp', ['ngDialog']);
	ANGULARJS_APP = angular.module('OrderManagementInvoiceSearchApp', ['search', 'enterkey']);

 	function InvoiceSearchController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/invoice';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.searchInvoices = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
				
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "orderNumber=" + search.orderNumber ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('orderNumber', search.orderNumber );
				$scope.invoices = data;
				//$scope.getSearchHistory();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};

		$scope.loadInvoiceData = function (invoiceNo,orderNo) {
			$http({
				method : 'POST',
				url: STOREELF_ROOT_URI+'/Utility/OrderManagement/invoice_popup',
				data: "orderNo=" + orderNo + "&invoiceNo=" + invoiceNo,
				headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.invoicepopups = data;
				$scope.invoicepopups_metadata_invoiceno = invoiceNo;
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
 			$scope.searchInvoices(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
 	}
</script>



<div ng-app="OrderManagementInvoiceSearchApp">
	<div ng-controller="InvoiceSearchController">
		<div class="col-sm-12">
            <ol class="breadcrumb">
                <li><a>Utility</a></li>
                <li><a>Order Management</a></li>
                <li class="active">Invoice</li>
            </ol>
        </div>


		<div class="col-sm-12">
		<p> <h4> Do not pass Orders older than 2 Months </h4> </p>

		<section class="panel">

		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchInvoices(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Order No</label>
			    <div class="col-sm-3">
			      <input name="orderNumber" class="form-control" ng-model="search.orderNumber" onclick="" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No" required/>
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchInvoices(search)">{{button}}</button>
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
				<thead>
					<tr>
						<th bgcolor=#4DA5AD><font color="white">Order No</font></th>
						<th bgcolor=#4DA5AD><font color="white">Order Date</font></th>
						<th bgcolor=#4DA5AD><font color="white">Invoice Date</font></th>
						<th bgcolor=#4DA5AD><font color="white">Invoice Number</font></th>
						<th bgcolor=#4DA5AD><font color="white">Store</font></th>
						<th bgcolor=#4DA5AD><font color="white">Register</font></th>
						<th bgcolor=#4DA5AD><font color="white">Transaction</font></th>
						<th bgcolor=#4DA5AD><font color="white">Total Tax</font></th>
						<th bgcolor=#4DA5AD><font color="white">Total Discount</font></th>
						<th bgcolor=#4DA5AD><font color="white">Amount</font></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="invoice in invoices">
						<td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{invoice.ORDER_NUMBER.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{invoice.ORDER_NUMBER.trim()}}</a></td>
						<td>{{invoice.ORDER_DATE}}</td>
						<td>{{invoice.POS_DATE}}</td>
						<td><button class="btn btn-popup btn-sm" data-toggle="modal" data-target="#invoicePopupModal" ng-click="loadInvoiceData(invoice.INVOICE_NUMBER, invoice.ORDER_NUMBER)">{{invoice.INVOICE_NUMBER}}</button></td>
						<td>{{invoice.STORE_NUMBER}}</td>
					    <td>{{invoice.REGISTER}}</td>
					    <td>{{invoice.TRANSACTION}}</td>
						<td>{{invoice.TOTAL_TAX_ON_INVOICE}}</td>
						<td>{{invoice.TOTAL_DISCOUNTS}}</td>
						<td>{{invoice.MERCHANDISE_TOTAL}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>

		<div class="modal fade" id="invoicePopupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg"  >
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		        <h4 class="modal-title" id="myModalLabel">Invoice {{invoicepopups_metadata_invoiceno}}</h4>
		      </div>
		      <div class="modal-body">
		      <section class="panel">
		        <table class="table table-hover table-bordered table-condensed">
	                <tr>
	                    <th bgcolor=#4DA5AD><font color="white">Order No</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Tender Type</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Item ID</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Quantity</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Unit Price</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Discount</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Tax Amount</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Tax Percentage</font></th>
	                </tr>

	                 <tr ng-repeat="invoicepopup in invoicepopups">
	                    <td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{invoicepopup.ORDER_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{invoicepopup.ORDER_NO.trim()}}</a></td>
	                    <td>{{invoicepopup.TENDER_TYPE}}</td>
	                    <td><a class="btn btn-xs btn-round btn-highlight" ng-href="/Utility/OrderManagement/item#/search?items={{invoicepopup.SKU.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{invoicepopup.SKU.trim()}}</a></td>
	                    <td>{{invoicepopup.QUANTITY}}</td>
	                    <td>{{invoicepopup.UNITPRICE}}</td>
	                    <td>{{invoicepopup.DISCOUNT}}</td>
	                    <td>{{invoicepopup.TAXAMOUNT}}</td>
	                    <td>{{invoicepopup.TAX_PERCENTAGE}}</td>
	                </tr>
			    </table>
			    </section>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-close" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
	</div>
</div>