<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />

<script type="text/javascript">
	//your app MUST be named starting with it's associated Servlet name
	ANGULARJS_APP = angular.module('WarehouseTransferApp', ['ngAnimate', 'ngSanitize', 'mgcrea.ngStrap', 'search', 'enterkey']);

	//We already have a limitTo filter built-in to angular,
	//let's make a startFrom filter
	ANGULARJS_APP.filter('startFrom',	ANGULARJS_FILTER_startFrom);
	ANGULARJS_APP.filter('range', 		ANGULARJS_FILTER_range);

	//define your controller, function name MUST end with 'Controller'
 	function TransferOrderController($scope, $http, $location, $window, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/WarehouseTransfer/transfer_orders';
 		$scope.pageSize			= ANGULARJS_PAGINATION_pageSize;
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search"; 
 		$scope.download_message = null;
		$scope.export_download_btn_label = "Export & Download";
		$scope.isLargeDownload = false;
 		
 		$scope.getTransferOrderDetails = function(export_result_csv) {
 			$scope.currentPage		= 0;

 			var req_parameters	=
 				"order_number="			+ (($scope.search.order_number)												?	$scope.search.order_number		:'')+
			    "&source_node="			+ (($scope.search.source_node)												?	$scope.search.source_node		:'')+
			    "&order_status="		+ (($scope.search.order_status)												?	$scope.search.order_status		:'')+
			    "&receiving_node="		+ (($scope.search.receiving_node)											?	$scope.search.receiving_node	:'')+
			    "&shipment_number="		+ (($scope.search.shipment_number)											?	$scope.search.shipment_number	:'')+
			    "&from_shipment_date="	+ (($scope.search.from_shipment_date)										?	$scope.search.from_shipment_date:'')+
			    "&to_shipment_date="	+ (($scope.search.to_shipment_date)											?	$scope.search.to_shipment_date	:'')+

			    "&transfer_type="		+ (($scope.search.transfer_type)											?	$scope.search.transfer_type		:'')+
			    "&dept="				+ (($scope.search.dept)														?	$scope.search.dept				:'')+
			    "&sub_cl="				+ (($scope.search.sub_cl)													?	$scope.search.sub_cl			:'')+
			    "&cl="					+ (($scope.search.cl)														?	$scope.search.cl				:'')+
			    "&item_id="				+ (($scope.search.item_id)													?	$scope.search.item_id			:'')+
			    "&from_order_date="		+ (($scope.search.from_order_date)											?	$scope.search.from_order_date	:'')+
			    "&to_order_date="		+ (($scope.search.to_order_date)											?	$scope.search.to_order_date		:'')+
			    "&export_result_csv="	+ ((export_result_csv)														?	export_result_csv	:'')
			    "&ajax_request=true"
			    ;

 			//if(export_result_csv==true){
 			//	window.location.href = req_url + '?' + req_parameters;
 			//}else{
 				$scope.isActive = true;
 				$scope.clicked = true;
 				$scope.button = "Processing...";
 				
	 			$http({
				    method: 'POST',
				    url: post_url,
				    data: req_parameters,
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status, headers) {
					$location.search('order_number', $scope.search.order_number);
					$location.search('source_node', $scope.search.source_node);
					$location.search('order_status', $scope.search.order_status);
					$location.search('receiving_node', $scope.search.receiving_node);
					$location.search('shipment_number', $scope.search.shipment_number);
					$location.search('from_shipment_date', $scope.search.from_shipment_date);
					$location.search('to_shipment_date', $scope.search.to_shipment_date);

					$location.search('transfer_type', $scope.search.transfer_type);
					$location.search('dept', $scope.search.dept);
					$location.search('sub_cl', $scope.search.sub_cl);
					$location.search('cl', $scope.search.cl);
					$location.search('item_id', $scope.search.item_id);
					$location.search('from_order_date', $scope.search.from_order_date);
					$location.search('to_order_date', $scope.search.to_order_date);
					$location.search('export_result_csv', export_result_csv);
					
					
					if($scope.res_greater_than_500 == "true" || headers('content-type') == 'text/csv;charset=ISO-8859-1' || export_result_csv){
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
							.attr("download", "transferOrder.csv")
							.attr("style", "")
							;
							 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
						//}
						$scope.export_download_btn_label = "Download";
						$scope.transfer_order_data = null;
					}
					else{
						$scope.isLargeDownload = false;
						$scope.export_download_btn_label = "Export & Download"; 
						$scope.download_message = "";
						$scope.transfer_order_data			= data;
						$scope.transfer_order_numberOfPages	= getNumberOfPages($scope.transfer_order_data.transfer_order_result.length, $scope.pageSize);
					}					

					//$scope.getSearchHistory();
					$scope.isActive = false;
			 		$scope.clicked = false;
			 		$scope.button = "Search";
				}).error(function(data, status, headers, config) {
					// called asynchronously if an error occurs
					// or server returns response with an error status.
					console.log("ERROR");
				});
 			//}
 		};

 		$scope.getTransferOrderLines = function(order_number, export_result_csv) {
 			$scope.popup_currentPage	= 0;

 			$scope.popup_title_order_number = order_number; 

 			var req_url			= STOREELF_ROOT_URI+'/Utility/WarehouseTransfer/transfer_order_popup';
 			var req_parameters	=
 				"order_number="			+ (($scope.search.order_number)												?	$scope.search.order_number		: order_number)+
			    "&source_node="			+ (($scope.search.source_node)												?	$scope.search.source_node		:'')+
			    "&order_status="		+ (($scope.search.order_status)												?	$scope.search.order_status		:'')+
			    "&receiving_node="		+ (($scope.search.receiving_node)											?	$scope.search.receiving_node	:'')+
			    "&shipment_number="		+ (($scope.search.shipment_number)											?	$scope.search.shipment_number	:'')+
			    "&from_shipment_date="	+ (($scope.search.from_shipment_date)										?	$scope.search.from_shipment_date:'')+
			    "&to_shipment_date="	+ (($scope.search.to_shipment_date)											?	$scope.search.to_shipment_date	:'')+

			    "&transfer_type="		+ (($scope.search.transfer_type)											?	$scope.search.transfer_type		:'')+
			    "&dept="				+ (($scope.search.dept)														?	$scope.search.dept				:'')+
			    "&sub_cl="				+ (($scope.search.sub_cl)													?	$scope.search.sub_cl			:'')+
			    "&cl="					+ (($scope.search.cl)														?	$scope.search.cl				:'')+
			    "&item_id="				+ (($scope.search.item_id)													?	$scope.search.item_id			:'')+
			    "&from_order_date="		+ (($scope.search.from_order_date)											?	$scope.search.from_order_date	:'')+
			    "&to_order_date="		+ (($scope.search.to_order_date)											?	$scope.search.to_order_date		:'')+
			    "&export_result_csv="	+ ((export_result_csv)														?	export_result_csv	:'')
			    "&ajax_request=true"
			    ;

 			//if(export_result_csv){
 			//	window.location.href= req_url + '?' + req_parameters;
 			//}else{ 
 				console.log(req_parameters);
	 			$http({
				    method: 'POST',
				    url: req_url,
				    data: req_parameters,
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status, headers) {
					
					console.log("headers:"+headers('content-type'));
					if($scope.res_greater_than_500 == "true" || headers('content-type') == 'text/csv;charset=ISO-8859-1' || export_result_csv){
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
							.attr("download", "transferOrder.csv")
							.attr("style", "")
							;
							 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
						//}
					}
					else{
						$scope.download_message = "";	 
						$scope.popup_transfer_order_data			= data.shipment_details;
						$scope.popup_transfer_order_numberOfPages	= getNumberOfPages($scope.popup_transfer_order_data.length, $scope.pageSize);
					}
					
				}).error(function(data, status, headers, config) {
					// called asynchronously if an error occurs
					// or server returns response with an error status.
					console.log("ERROR");
				});
 			//}
 		};


        $scope.resetData = function() {
            $scope.search.order_number = '' ;
            $scope.search.source_node = '' ;
            $scope.search.order_status = '' ;
            $scope.search.receiving_node = '' ;
            $scope.search.shipment_number = '' ;
            $scope.search.from_shipment_date = '' ;
            $scope.search.to_shipment_date = '' ;
            $scope.search.transfer_type = '' ;
            $scope.search.dept = '' ;
            $scope.search.sub_cl = '' ;
            $scope.search.cl = '' ;
            $scope.search.item_id = '' ;
            $scope.search.from_order_date = '' ;
            $scope.search.to_order_date = '' ;
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
 			//$scope.getTransferOrderDetails(false);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};

	}
</script>



<div ng-app="WarehouseTransferApp">
	<div ng-controller="TransferOrderController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Transfer</a></li>
				<li class="active">Transfer Order</li>
			</ol>
		</div>

		<div class="col-sm-12">

		<section class="panel">

		<div class="panel-body">
			<form name="form" class="form-horizontal" role="form">
				<div class="col-sm-6">
					<div class="form-group">
						<label class="col-sm-3 control-label">Order No</label>
						<div class="col-sm-9">
							<input name="order_number" type="text" class="form-control" ng-model="search.order_number" placeholder="Order Number"/>
						</div>
					 </div>

					 <div class="form-group">
						<label class="col-sm-3 control-label">Order Status</label>
						<div class="col-sm-9">
							<select name="order_status" class="form-control" ng-model="search.order_status">
								<option value="" selected="">----------</option>
								<option value="Cancelled">Cancelled</option>
								<option value="Created">Created</option>
								<option value="Invoiced">Invoiced</option>
								<option value="Released">Released</option>
								<option value="Scheduled">Scheduled</option>
								<option value="Sent Release To WMoS">Sent Release To WMoS</option>
								<option value="Shipped">Shipped</option>
							</select>
						</div>
					 </div>

					 <div class="form-group">
						<label class="col-sm-3 control-label">Shipment No</label>
						<div class="col-sm-9">
							<input name="shipment_number" type="text" class="form-control" ng-model="search.shipment_number" id="" placeholder="Shipment Number"/>
						</div>
					 </div>

					<div class="form-group">
						<label class="control-label col-md-3">Shipment Date range <small>(dynamic)</small></label>
						<div class="col-md-9">
							<div class="input-group input-large" data-date="2012-01-01T00:00:00Z" data-date-format="mm-dd-yyyy+HH:mm:ss">
								<input type="text" class="form_datetime-meridian form-control" name="from_shipment_date" ng-model="search.from_shipment_date"/>
								<span class="input-group-addon">To</span>
								<input type="text" class="form_datetime-meridian form-control" name="to_shipment_date" ng-model="search.to_shipment_date"/>
							</div>
							<span class="help-block">Select date range</span>
						</div>
					</div>

					<div class="form-group">
						<label class="control-label col-md-3">Order Create Date range <small>(dynamic)</small></label>
						<div class="col-md-9">
							<div class="input-group input-large" data-date="2012-01-01T00:00:00Z" data-date-format="MM-dd-yyyy+HH:mm">
								<input type="text" class="form_datetime-meridian form-control" name="from_order_date" ng-model="search.from_order_date"/>
								<span class="input-group-addon">To</span>
								<input type="text" class="form_datetime-meridian form-control" name="to_order_date" ng-model="search.to_order_date"/>
							</div>
							<span class="help-block">Select date range</span>
						</div>
					</div>


				</div>

				<!-- ############################################# ############################################# #############################################-->
				<!-- ############################################# ############################################# #############################################-->
				<!-- ############################################# ############################################# #############################################-->

				<div class="col-sm-6">
					<div class="form-group">
						<label class="col-sm-3 control-label">Source Node</label>
						<div class="col-sm-9">
							<select class="form-control" ng-model="search.source_node">
								<option value="" selected="">----------</option>
								<option value="873">873</option>
								<option value="809">809</option>
								<option value="819">819</option>
								<option value="829">829</option>
								<option value="859">859</option>
								<option value="869">869</option>
								<option value="879">879</option>
								<option value="889">889</option>
							</select>
						</div>
					 </div>

					<div class="form-group">
						<label class="col-sm-3 control-label">Receiving Node</label>
						<div class="col-sm-9">
							<select class="form-control" ng-model="search.receiving_node">
								<option value="" selected="">----------</option>
								<option value="873">873</option>
								<option value="809">809</option>
								<option value="819">819</option>
								<option value="829">829</option>
								<option value="859">859</option>
								<option value="869">869</option>
								<option value="879">879</option>
								<option value="889">889</option>
							</select>
						</div>
					 </div>

					 <div class="form-group">
						<label class="col-sm-3 control-label">Transfer Type</label>
						<div class="col-sm-9">
							<select class="form-control" ng-model="search.transfer_type">
								<option value="" selected="">----------</option>
								<option value="Warehouse Transfer">Warehouse Transfer</option>
							</select>
						</div>
					 </div>

					<div class="form-group">
						<label class="col-sm-3 control-label">Item ID</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" ng-model="search.item_id" id="item_id" placeholder="Item ID">
						</div>
					 </div>

					 <div class="form-group">
						<label class="col-sm-3 control-label">Dept | CL | SUB CL</label>
						<div class="col-sm-9">
							<div class="form-group row">
						        <div class="col-md-3"><input type="text" class="form-control" ng-model="search.dept" 	id="inputKey"	placeholder="Dept"></div>
						        <div class="col-md-3"><input type="text" class="form-control" ng-model="search.cl" 	 	id="inputValue" placeholder="CL"></div>
						        <div class="col-md-3"><input type="text" class="form-control" ng-model="search.sub_cl"	id="inputValue" placeholder="SUB CL"></div>
						    </div>
						</div>
					 </div>
				</div>

				<div class="form-group">
				    <div class="col-sm-offset-2 col-sm-10">
				    	<button ng-disabled="!(( !!search.order_number || (!!search.from_order_date && !!search.to_order_date )) || ( !!search.shipment_number || (!!search.from_shipment_date && !!search.to_shipment_date ))) || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getTransferOrderDetails(false)">{{button}}</button>
                        <button ng-disabled="!(( !!search.order_number || (!!search.from_order_date && !!search.to_order_date )) || ( !!search.shipment_number || (!!search.from_shipment_date && !!search.to_shipment_date )))" class="btn btn-rset" type="button" ng-click="resetData()">Reset</button>
                        <!-- <a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;">Download Now</a> -->
                        <span class="help-block">{{download_message}}</span>
                    <!--  <div class="btn-group">
							<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
							<ul role="menu" class="dropdown-menu">
								<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
								<li class="divider"></li>
								<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
							</ul>
						</div>--><!-- /btn-group -->
				    </div>
				</div>

				<div ng-show="error">Invalid:
					<span bg-bind-template="error"></span>
				</div>

			</form>
			</div>
		</section>
		</div>

		<div class="col-sm-12 text-center" ng-show="transfer_order_data != null">
			<div class="btn-group">
				<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">Previous</button>
   				<button type="button" class="btn btn-default" disabled="disabled" ng-bind-template="Page {{currentPage+1}} of {{transfer_order_numberOfPages}}"></button>
   				<button type="button" class="btn btn-default" ng-disabled="currentPage >= transfer_order_data.transfer_order_result.length/pageSize - 1" ng-click="currentPage=currentPage+1">Next</button>
   			</div>
   		</div>

   		<div class="col-sm-12" ng-show="transfer_order_data != null">
   		<section class="panel">
   			<header class="panel-heading">
   				<a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;" ng-click="(isLargeDownload) ? console.log('user downloading data...') : getTransferOrderDetails(true);" >{{export_download_btn_label}}</a>
				<!-- <a href="#" role="button" ng-click="getTransferOrderDetails(true)">Export All</a> -->
			</header>
			<table id="transfer_order_table" class="table table-hover table-condensed table-bordered table-responsive"  summary="Code page support in different versions of MS Windows." rules="groups" frame="hsides" >
	     		<colgroup align="center"></colgroup>
				<colgroup align="left"></colgroup>
				<colgroup span="2" align="center"></colgroup>
				<colgroup span="3" align="center"></colgroup>
		     	<thead>
			        <tr>
						<th>Order No</th>
						<th>Order Date</th>
						<th>Status</th>
						<th>Status Date</th>
						<th>Transfer Type</th>
						<th>Item Count</th>
						<th>Total Units</th>
						<th>Shipped Units</th>
						<th>Cancelled Units</th>
						<th>Unit Variance</th>
						<th>Created By</th>
			        </tr>
		     	</thead>
		     	<tbody>
					<tr ng-repeat="result in transfer_order_data.transfer_order_result | startFrom:currentPage*pageSize | limitTo:pageSize">
						<td>
							<button ng-disabled="form.$invalid || isLoading()" class="btn btn-primary btn-sm" data-toggle="modal" data-target="#orderLines" ng-click="getTransferOrderLines(result.ORDER_NO)" ng-bind="result.ORDER_NO"></button>
						</td>
						<td ng-bind="result.ORDER_DATE"></td>
						<td ng-bind="result.STATUS"></td>
						<td ng-bind="result.STATUS_DATE"></td>
						<td ng-bind="result.TRANSFER_TYPE"></td>
						<td ng-bind="result.ITEM_COUNT"></td>
						<td ng-bind="result.TOTAL_UNITS"></td>
						<td ng-bind="result.SHIPPED_UNITS"></td>
						<td ng-bind="result.CANCELLED_UNITS"></td>
						<td ng-bind="result.UNIT_VARIANCE"></td>
						<td ng-bind="result.ENTERED_BY"></td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
		<div class="col-sm-12 text-center">
			<div class="btn-group" ng-show="transfer_order_data != null">
				<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">Previous</button>
   				<button type="button" class="btn btn-default" disabled="disabled" ng-bind-template="Page {{currentPage+1}} of {{transfer_order_numberOfPages}}"></button>
   				<button type="button" class="btn btn-default" ng-disabled="currentPage >= transfer_order_data.transfer_order_result.length/pageSize - 1" ng-click="currentPage=currentPage+1">Next</button>
   			</div>
		</div>

		<div class="modal fade" id="orderLines" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg" style="width: 70%" >
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel" ng-bind-template="Order No : {{popup_title_order_number}}"></h4>
					</div>
					<div class="modal-body">

						<a href="#" class="btn btn-lg active" role="button" ng-click="getTransferOrderLines(popup_title_order_number, true)">Export All</a>

						<div class="text-center">
                            <div class="btn-group text-left" ng-show="popup_transfer_order_data != null">
								<button type="button" class="btn btn-default" ng-disabled="popup_currentPage == 0" ng-click="popup_currentPage=popup_currentPage-1">Previous</button>
			    				<button type="button" class="btn btn-default" disabled="disabled">Page {{popup_currentPage+1}} of {{popup_transfer_order_numberOfPages}}</button>
			    				<button type="button" class="btn btn-default" ng-disabled="popup_currentPage >= popup_transfer_order_data.length/pageSize - 1" ng-click="popup_currentPage=popup_currentPage+1">Next</button>
			    			</div>
                        </div>

						<section class="panel">
						<table id="popup" class="table table-hover table-bordered table-condensed">
							<thead>
								<tr>
									<th>[+]</th>
									<th>Order No</th>
									<th>Line No</th>
									<th>Item ID</th>
									<th>Item Description</th>
									<th>Dept</th>
									<th>Class</th>
									<th>Sub Class</th>
									<th>Status</th>
									<th>From Node</th>
									<th>To Node</th>
									<th>Total Units</th>
									<th>Shipped Units</th>
									<th>Cancelled Units</th>
									<th>Unit Variance</th>
									<th>Pickticket No</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat-start="r in popup_transfer_order_data | startFrom:popup_currentPage*pageSize | limitTo:pageSize">
									<td>
										<button type="button" class="btn btn-default btn-sm" ng-click="toggle_TransferOrderResults = !toggle_TransferOrderResults">
											<span ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle_TransferOrderResults==true]">  </span>
										</button>
									</td>
									<td>{{r.ORDER_NO}}</td>
									<td>{{r.PRIME_LINE_NO}}</td>
									<td>{{r.ITEM_ID}}</td>
									<td>{{r.ITEM_DESCRIPTION}}</td>
									<td>{{r.EXTN_DEPT}}</td>
									<td>{{r.EXTN_CLASS}}</td>
									<td>{{r.EXTN_SUB_CLASS}}</td>
									<td>{{r.STATUS}}</td>
									<td>{{r.SHIPNODE_KEY}}</td>
									<td>{{r.RECEIVING_NODE}}</td>
									<td>{{r.TOTAL_UNITS}}</td>
									<td>{{r.SHIPPED_UNITS}}</td>
									<td>{{r.CANCELLED_UNITS}}</td>
									<td>{{r.UNIT_VARIANCE}}</td>
									<td>{{r.EXTN_PICK_TICKET_NO}}</td>
								</tr>
								<tr ng-show="toggle_TransferOrderResults" ng-repeat-end>
									<td colspan="16">
										<table class="table table-hover table-bordered table-condensed">
										<thead>
											<th>Shipment Date</th>
											<th>Shipment Number</th>
											<th>Shipment Line</th>
											<th>BOL</th>
											<th>Units</th>
											<th>Batch No</th>
											<th>Item Retail Price</th>
											<th>Distributed Retail Price</th>
										</thead>
										<tbody>
											<tr ng-repeat="shipmentLine in r.SHIPMENT_LINE_ARRAY">
												<td>{{shipmentLine.SHIPMENT_DATE}}</td>
												<td>{{shipmentLine.SHIPMENT_NO}}</td>
												<td>{{shipmentLine.SHIPMENT_LINE}}</td>
												<td>{{shipmentLine.BOL}}</td>
												<td>{{shipmentLine.UNITS}}</td>
												<td>{{shipmentLine.BATCH_NO}}</td>
												<td>{{shipmentLine.ITEM_RETAIL_PRICE}}</td>
												<td>{{shipmentLine.DISTRIBUTED_RETAIL_PRICE}}</td>
											</tr>
										</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default text-right" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>