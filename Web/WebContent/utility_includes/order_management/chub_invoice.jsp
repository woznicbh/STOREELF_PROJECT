
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />
<script type="text/javascript">
ANGULARJS_APP = angular.module('OrderManagementChubInvoiceApp', ['search', 'enterkey', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap']);

ANGULARJS_APP.filter('startFrom',	ANGULARJS_FILTER_startFrom);
ANGULARJS_APP.filter('range', 		ANGULARJS_FILTER_range);
ANGULARJS_APP
.config(function($datepickerProvider) {
angular.extend($datepickerProvider.defaults, {
format: 'YYYY/MM/DD',
startWeek: 1,
autoclose: 'true',
formatViewType: 'date'
});
});

function ChubInvoiceSearchController($scope, $http, $location, STOREELFSearchService, $window){
		$scope.pageSize			= 25;
		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/chub_invoice';

		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		$scope.export_download_btn_label = "Export & Download";
		$scope.isLargeDownload = false;

		$scope.getChubInvoiceDetails = function(search, export_result_csv) {
			$('#export_all_trigger').css('visibility','hidden');
			$scope.currentPage		= 0;

			var req_parameters =
				"field_name="			+ (($scope.search.field_name)												?	$scope.search.field_name	:'')+
			    "&field_value="			+ (($scope.search.field_value)												?	$scope.search.field_value	:'')+
			    "&vendor_id="			+ (($scope.search.vendor_id)												?	$scope.search.vendor_id		:'')+
			    "&from_date="			+ (($scope.search.from_date)												?	$scope.search.from_date		:'')+
			    "&to_date="				+ (($scope.search.to_date)													?	$scope.search.to_date		:'')+
			    "&dept_id="				+ (($scope.search.dept_id)													?	$scope.search.dept_id		:'')+
			    "&item_id="				+ (($scope.search.item_id)													?	$scope.search.item_id		:'')+
				"&export_result_csv="	+ ((export_result_csv)														?	export_result_csv			:'')+
				"&ajax_request=true"
				;

			/* if (export_result_csv == true) {
				window.location.href = post_url + '?' + req_parameters;
			} else { */
				if(search) $scope.search = search;
				$scope.isActive = true;
 				$scope.clicked = true;
 				$scope.button = "Processing...";

				$http({
					method : 'POST',
					url : post_url,
					data : req_parameters,
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					}
				}).success(function(data, status, headers) {
					$location.search('field_name', $scope.search.field_name);
					$location.search('field_value', $scope.search.field_value);
					$location.search('vendor_id', $scope.search.vendor_id);
					$location.search('from_date', $scope.search.from_date);
					$location.search('to_date', $scope.search.to_date);
					$location.search('dept_id', $scope.search.dept_id);
					$location.search('item_id', $scope.search.item_id);
					$location.search('export_result_csv', export_result_csv);

					$scope.isActive = false;
					$scope.clicked = false;
					$scope.button = "Search";
					
					
					if(data.length == 0){
		            	alert('Your search returned 0 values');
	                }
	                if(headers('content-type') == 'text/csv;charset=ISO-8859-1' || export_result_csv){
						var resultTxt = "			Exporting Result Set 			\n "+"\n			Press OK to Confirm Export and \"Download Now\" link will appear below 			";
						 
						if(export_result_csv==true){
							 $scope.download_message = "Click download button above to retrieve data in CSV format.";	 
						 }else{
							 $scope.download_message = "Dataset too large to display, please click download button above to retrieve data in CSV format.";
						 }
						
						//if ($window.confirm(resultTxt) == true) {
							 //$window.location.href = post_url + '?' + req_parameters+"true";
							 //$window.location.href = 'data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) ;
							 if($("#export_all_trigger").attr('pulsate') == null){
								jQuery('#export_all_trigger').pulsate({
									color: "#E74955",
									reach: 15,
									repeat: 3,
									speed: 1000,
									glow: true
								});
								$("#export_all_trigger").attr('pulsate','true');
							}
							$("#export_all_trigger")
							.attr("href", 'data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data))
							.attr("download", "chubInvoice.csv")
							.attr("style", "")
							;
							 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
						 //   }
						$scope.export_download_btn_label = "Download";
						$scope.chub_invoice_data = null;
					}else{
						$scope.isLargeDownload = false;
						$scope.export_download_btn_label = "Export & Download"; 
						$scope.chub_invoice_data = data;
						$scope.chub_invoice_numberOfPages = getNumberOfPages($scope.chub_invoice_data.chub_invoice_result.length,$scope.pageSize);
					}
					//$scope.getSearchHistory();
				});
			//}
		};


		$scope.clearForm = function(search){
				$scope.search.field_value  = '',
				$scope.search.vendor_id	   = '',
				$scope.search.from_date	   = '',
				$scope.search.to_date	   = '',
				$scope.search.dept_id      = '',
				$scope.search.item_id	   = ''
		};

        /* $scope.getSearchHistory = function(){
            //-- set search history
            STOREELFSearchService.setRequestUri(post_url);
            STOREELFSearchService.getHistory();
            STOREELFSearchService.injectIntoScope($scope);
            $scope.search_history_data = STOREELFSearchService.getData();
        }; */

        $scope.autoSearch = function(){
			//$scope.getSearchHistory();
 			//$scope.getChubInvoiceDetails($location.search(), false);
 			//STOREELFSearchService.autoFillForm($scope.search, $location.search());
 		};
	}
</script>

<div ng-app="OrderManagementChubInvoiceApp">
	<div ng-controller="ChubInvoiceSearchController" data-ng-init="autoSearch()">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">CHUB Invoice</li>
			</ol>
		</div>
		<div class="col-sm-12">
			<section class="panel">
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form" tasi-form	ng-submit="getChubInvoiceDetails(false)" data-ng-init="search.field_name='invoice_no'">
						<h5>Select Search Criteria <small> (Select any one of the below)</small></h5>
						<div class="btn-row">
						<div class="btn-group" data-toggle="buttons">
						<Label class="btn btn-tab" for="chub_no" ng-selected="true" name="field_name" data-ng-model="search.field_name" ng-click="clearForm(search);search.field_name='chub_no'">Commerce Hub No<input type="radio" ng-id="chub_no" /></Label>
						<Label class="btn btn-tab active" for="invoice_no" name="field_name" data-ng-model="search.field_name"ng-click="clearForm(search);search.field_name='invoice_no'">Invoice No<input type="radio" ng-id="invoice_no" /></Label>
						<Label class="btn btn-tab" for="int_po_no" name="field_name" data-ng-model="search.field_name"ng-click="clearForm(search);search.field_name='int_po_no'">Internal PO No<input type="radio" ng-id="int_po_no" /></Label>
						<Label class="btn btn-tab" for="advanced_search" name="field_name" data-ng-model="search.field_name"ng-click="clearForm(search);search.field_name='advanced_search'">Advanced Search<input type="radio" ng-id="advanced_search" /></Label>
						</div>
						</div>
						<br>

						<!--  Added advanced search -->
						<!-- <div class="form-group">
			    			<div class="col-sm-10">
			      				<div class="checkbox">
				    			<label>
				      				<input type="checkbox" ng-model="search.advanced_search" data-ng-init="search.advanced_search=false"> Advanced Search
				    			</label>
				  				</div>
			    			</div>
			  			</div> -->

						<div class="form-group" ng-hide="(!!(search.field_name=='advanced_search')  || isLoading())">
							<label class="col-sm-2 control-label">Enter Search Value</label>
							<div class="col-sm-5">

								<textarea rows="5" class="form-control" maxlength="7001"
									ng-disabled="!(!!search.field_name  || isLoading())"
									ng-model="search.field_value" onclick="" type="text"
									ng-init="search.field_value=''" placeholder="Enter value"
									> </textarea>
							</div>
						</div>
						

						<!-- Adding department search -->
						<div class="form-group"
							ng-hide="!(!!(search.field_name=='advanced_search')  || isLoading())">
							<label class="col-sm-2 control-label">Enter Department</label>
							<div class="col-sm-5">
								<input name="dept_id" class="form-control" maxlength="100"
									ng-model="search.dept_id" onclick="" type="text" height="100"
									ng-init="search.dept_id=''" placeholder="Enter department">
							</div>
						</div>
						
						<!-- Adding department search -->
						<div class="form-group">
							<label class="col-sm-2 control-label">Enter Item ID/SKU</label>
							<div class="col-sm-5">
								<input name="item_id" class="form-control" maxlength="100"
									ng-model="search.item_id" onclick="" type="text" height="100"
									ng-init="search.item_id=''" placeholder="Enter item ID">
							</div>
						</div>

						<div class="form-group"
							ng-hide="!(!!(search.field_name=='advanced_search' || search.field_name=='invoice_no')  || isLoading())">
							<label class="col-sm-2 control-label">Enter Vendor ID</label>
							<div class="col-sm-5">
								<input name="vendor_id" class="form-control" maxlength="100"
									ng-model="search.vendor_id" onclick="" type="text" height="100"
									ng-init="search.vendor_id=''" placeholder="Enter vendor ID">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-2">Date Range</label>
							<div class="col-md-10">
								<div class="input-group input-large" data-date="2012/01/01"
									data-date-format="YYYY/MM/DD">
									<input type="text" class="form_datetime-meridian form-control"
										name="from_date" ng-model="search.from_date"
										ng-init="search.from_date=''" required /> <span
										class="input-group-addon">To</span> <input type="text"
										class="form_datetime-meridian form-control" name="to_date"
										ng-model="search.to_date" ng-init="search.to_date=''" required />
								</div>
								<span class="help-block">Select date range</span>
							</div>
						</div>



						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button ng-disabled="!(!!search.field_value ||((!!search.vendor_id || !!search.dept_id || search.item_id || (!!search.from_date && !!search.to_date)))  || isLoading()) || (clicked == true)"
									class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit"
									ng-click="getChubInvoiceDetails(search, false); $('#export_all_trigger').css('visibility','hidden')">{{button}}</button>
									<!-- <a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;">Download Now</a> -->
									<span class="help-block">{{download_message}}</span>
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
		<div class="col-sm-12" >
			<section class="panel">
				<header class="panel-heading">
				<a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;" ng-click="(isLargeDownload) ? console.log('user downloading data...') : getChubInvoiceDetails($location.search(), true);" >{{export_download_btn_label}}</a>
					<!-- <a role="button" ng-click="getChubInvoiceDetails($location.search(), true)">Export	All</a> -->
				</header>
				<table id="chub_invoice_table"
					class="table table-hover table-condensed table-bordered table-responsive"
					summary="Code page support in different versions of MS Windows."
					rules="groups" frame="hsides" >
					<colgroup align="center"></colgroup>
					<colgroup align="left"></colgroup>
					<colgroup span="2" align="center"></colgroup>
					<colgroup span="3" align="center"></colgroup>
					<thead>
						<tr>
							<th>Shipment Date</th>
							<th>Vendor Invoice No</th>
							<th>Vendor Id</th>
							<th>CHUB Order No</th>
							<th>Item Dept No</th>
							<th>Internal PO</th>
							<th>PO Status</th>
							<th>Shipment Qty</th>
							<th>SKU</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="result in chub_invoice_data.chub_invoice_result | startFrom:currentPage*pageSize | limitTo:pageSize">
							<td ng-bind="result.SHIP_DATE"></td>
							<td ng-bind="result.VENDOR_INVOICE_NUM"></td>
							<td ng-bind="result.VENDOR_ID"></td>
							<td ng-bind="result.CHUB_ORDER_NO"></td>
							<td ng-bind="result.DEPARTMENT"></td>
							<td ng-bind="result.INTERNAL_PO"></td>
							<td ng-bind="result.PO_STATUS"></td>
							<td ng-bind="result.SHIPMENT_QUANTITY"></td>
							<td ng-bind="result.ITEM_ID"></td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
		<div class="col-sm-12 text-center">
			<div class="btn-group" ng-show="chub_invoice_data != null">
				<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">Previous</button>
   				<button type="button" class="btn btn-default" disabled="disabled" ng-bind-template="Page {{currentPage+1}} of {{chub_invoice_numberOfPages}}"></button>
   				<button type="button" class="btn btn-default" ng-disabled="currentPage >=chub_invoice_data.chub_invoice_result.length/pageSize - 1" ng-click="currentPage=currentPage+1">Next</button>
   			</div>
		</div>
		<!-- <div class="col-sm-12 text-center">
<div class="btn-group" ng-show="chub_invoice_data != null">
<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">Previous</button>
<button type="button" class="btn btn-default" disabled="disabled" ng-bind-template="Page {{currentPage+1}} of {{transfer_order_numberOfPages}}"></button>
<button type="button" class="btn btn-default" ng-disabled="currentPage >= chub_invoice_data.chub_invoice_result.length/pageSize - 1" ng-click="currentPage=currentPage+1">Next</button>
</div>
</div>
<div class="col-sm-12" ng-show="transfer_order_data != null">
<section class="panel">
<header class="panel-heading">
<a href="#" role="button" ng-model="search" ng-click="getChubInvoiceDetais(true)">Export All</a>
</header>
<table class="table table-hover table-condensed table-bordered" summary="Code page support in different versions of MS Windows." rules="groups" frame="hsides">
<colgroup align="center"></colgroup>
<colgroup align="left"></colgroup>
<colgroup span="2" align="center"></colgroup>
<colgroup span="3" align="center"></colgroup>
<thead>
<tr>
<th colspan="10">Results</th>
</tr>
<tr>
<th>Shipment Date</th>
<th>Vendor Invoice No</th>
<th>Vendor Id</th>
<th>Order No</th>
<th>Item Dept No</th>
</tr>
</thead>
<tbody>
<tr ng-repeat="result in chub_invoice_data.chub_invoice_result | startFrom:currentPage*pageSize | limitTo:pageSize">
<td>{{invoicedata.SHIP_DATE}}</td>
<td>{{invoicedata.EXTN_DSV_INVOICE_NUM}}</td>
<td>{{invoicedata.SHIPNODE_KEY}}</td>
<td>{{invoicedata.ORDER_NO}}</td>
<td>{{invoicedata.EXTN_DEPT}}</td>
</tr>
</tbody>
</table>
</section>
</div> -->
	</div>
</div>