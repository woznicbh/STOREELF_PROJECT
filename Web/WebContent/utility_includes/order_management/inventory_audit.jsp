<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />

<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementInventoryAuditSearchApp', ['ngAnimate', 'ngSanitize', 'mgcrea.ngStrap', 'search', 'enterkey']);


	ANGULARJS_APP
		.config(function($datepickerProvider) {
		  angular.extend($datepickerProvider.defaults, {
		    dateFormat: 'MM/dd/yyyy',
		    startWeek: 1,
		    autoclose: 'true'
		  });
		});

 	function InventoryAuditSearchController($scope, $http, $location, STOREELFSearchService, $window){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/inventory_audit';

 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		$scope.export_download_btn_label = "Export & Download";
		$scope.isLargeDownload = false;
 		
		$scope.searchInventoryAudit = function(search,export_result_csv) {
			$('#export_all_trigger').css('visibility','hidden');
			var req_parameters =
				"from_date="			+ (($scope.search.from_date)												?	$scope.search.from_date	:'')+
			    "&to_date="			+ (($scope.search.to_date)												?	$scope.search.to_date	:'')+
			    "&item="			+ (($scope.search.item)												?	$scope.search.item		:'')+
			    "&ship_node="			+ (($scope.search.ship_node)												?	$scope.search.ship_node		:'')+
			    "&pix_tran_no="				+ (($scope.search.pix_tran_no)													?	$scope.search.pix_tran_no		:'')+
			    "&pix_tran_type="				+ (($scope.search.pix_tran_type)													?	$scope.search.pix_tran_type		:'')+
				"&export_result_csv="	+ ((export_result_csv)														?	export_result_csv			:'')+
				"&ajax_request=true"
				;

			
			//if (export_result_csv == true) {				
			//	window.location.href = post_url + '?' + req_parameters;
			//}	else{
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
				
			$http({
			    method: 'POST',
			    url: post_url,
			    data: req_parameters,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status, headers) {
				$location.search('from_date', $scope.search.from_date );
				$location.search('to_date', $scope.search.to_date);
				$location.search('item', $scope.search.item);
				$location.search('ship_node', $scope.search.ship_node );
				$location.search('pix_tran_no', $scope.search.pix_tran_no);
				$location.search('pix_tran_type', $scope.search.pix_tran_type);
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
						.attr("download", "inventoryAudit.csv")
						.attr("style", "")
						;
						 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
					//    }
						$scope.export_download_btn_label = "Download";
						$scope.inventoryAuditResult = null;
				}else{
					$scope.isLargeDownload = false;
					$scope.export_download_btn_label = "Export & Download"; 
					$scope.inventoryAuditResult = data;
				}
                
                //$scope.getSearchHistory();
			});
			//}
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
 			$scope.searchInventoryAudit(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="OrderManagementInventoryAuditSearchApp">
	<div ng-controller="InventoryAuditSearchController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Inventory Audit</li>
			</ol>
		</div>
		<div class="col-sm-12">

		<section class="panel">

	        <div class="panel-body">

		<form name="form" class="form-horizontal" role="form" ng-enter="searchInventoryAudit(search)">

			<div class="form-group">
				<label class="control-label col-md-2">Audit Date range*</label>
				<div class="col-md-10">
					<div class="input-group input-large" data-date="2012-01-01T00:00:00Z" data-date-format="mm-dd-yyyy+HH:mm:ss">
						<input type="text" class="form_datetime-meridian form-control" name="from" ng-model="search.from_date" required/>
						<span class="input-group-addon">To</span>
						<input type="text" class="form_datetime-meridian form-control" name="to" ng-model="search.to_date" required/>
					</div>
					<span class="help-block">Select date range</span>
				</div>
			</div>

			  <div class="form-group">
			    <label class="col-sm-2 control-label">Item ID*</label>
			    <div class="col-sm-3">
					<input name="item" type="text" class="form-control" ng-model="search.item" PLaceholder="Item ID  (Required)" ng-init="search.item=''" required/>
			    </div>

			  </div>
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Ship Node</label>
			    <div class="col-sm-3">
					<input name="ship_node" type="text" class="form-control" ng-model="search.ship_node" Placeholder="Ship Node" ng-init="search.ship_node=''" >
			    </div>
			  </div>

			  <div class="form-group">
			    <label class="col-sm-2 control-label">Pix Tran No</label>
			    <div class="col-sm-3">
					<input name="pix_tran_no" type="text" class="form-control" ng-model="search.pix_tran_no" Placeholder="Pix Tran No" ng-init="search.pix_tran_no=''" >
			    </div>
			  </div>

			  <div class="form-group">
			    <label class="col-sm-2 control-label">Pix Tran Type</label>
			    <div class="col-sm-3">
					<input name="pix_tran_type" type="text" class="form-control" ng-model="search.pix_tran_type" Placeholder="Pix Tran Type" ng-init="search.pix_tran_type=''" >
			    </div>
			  </div>

			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="form.$invalid || isLoading() || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="searchInventoryAudit(search,false); $('#export_all_trigger').css('visibility','hidden')">{{button}}</button>
				  <!-- <a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;">Download Now</a> -->
				  <span class="help-block">{{download_message}}</span>
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
		<header class="panel-heading">
					<a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;" ng-click="(isLargeDownload) ? console.log('user downloading data...') : searchInventoryAudit($location.search(),true);" >{{export_download_btn_label}}</a>
					<!-- <a id="export_all" role="button" ng-click="searchInventoryAudit($location.search(),true)">Export All</a> -->
				</header>
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
				<tr>
					<th>Item</th>
					<th>Short Desc</th>
					<th>Ship Node</th>
					<th>Qty</th>
					<th>On Hand Qty</th>
					<th>Transaction Type</th>
					<th>PIX Tran No</th>
					<th>PIX Seq No</th>
					<th>PIX Create Date</th>
					<th>PIX Tran Type</th>
					<th>Supply Type</th>
					<th>ModifyProgID</th>
					<th>Modifyts</th>
				</tr>
				</thead>
				<tbody>
				<tr ng-repeat="inventoryAudit in inventoryAuditResult">
					<td>{{inventoryAudit.ITEM}}</td>
					<td>{{inventoryAudit.SHORT_DESCRIPTION}}</td>
					<td>{{inventoryAudit.SHIP_NODE}}</td>
					<td>{{inventoryAudit.QUANTITY}}</td>
					<td>{{inventoryAudit.ON_HAND_QTY}}</td>
					<td>{{inventoryAudit.TRANSACTION_TYPE}}</td>
					<td>{{inventoryAudit.REFERENCE_1}}</td>
					<td>{{inventoryAudit.REFERENCE_2}}</td>
					<td>{{inventoryAudit.REFERENCE_3}}</td>
					<td>{{inventoryAudit.REFERENCE_4}}</td>
					<td>{{inventoryAudit.SUPPLY_TYPE}}</td>
					<td>{{inventoryAudit.MODIFYPROGID}}</td>
					<td>{{inventoryAudit.MODIFYTS | date : 'medium'}}</td>
				</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>