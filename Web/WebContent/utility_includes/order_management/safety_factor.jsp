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
ANGULARJS_APP = angular.module('OrderManagementSafetyFactorApp', ['search', 'enterkey', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap']);
ANGULARJS_APP.filter('startFrom',        ANGULARJS_FILTER_startFrom);
ANGULARJS_APP.filter('range',                 ANGULARJS_FILTER_range);

function SafetyFactorSearchController($scope, $http, $location, $window){
		$scope.pageSize			= 25;
		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/safety_factor';
		$scope.isActive = false;
		$scope.button = "Search";
		$scope.clicked = false;
		$scope.showModal = false;
		$scope.export_download_btn_label = "Export & Download";
		$scope.isLargeDownload = false;

		$scope.getSafetyFactorDetails = function(search, export_result_csv) {
			$('#export_all_trigger').css('visibility','hidden');
			$scope.currentPage		= 0;

			var req_parameters =
				"field_value="			+ (($scope.search.field_value)												?	$scope.search.field_value	:'')+
			    "&store_number="		+ (($scope.search.store_number)												?	$scope.search.store_number	:'')+
			    "&sku_dept="			+ (($scope.search.sku_dept)													?	$scope.search.sku_dept		:'')+
			    "&node_type="			+ (($scope.search.node_type)												?	$scope.search.node_type		:'')+
			    "&dept_number="			+ (($scope.search.dept_number)												?	$scope.search.dept_number	:'')+
				"&export_result_csv="	+ ((export_result_csv)														?	export_result_csv			:'false')+
				"&ajax_request=true"
				;

			/* if (export_result_csv == true) {
				window.location.href = post_url + '?' + req_parameters;
			} else { */
				if(search) $scope.search = search;
				$scope.isActive = true;
				$scope.button = "Processing...";
				$scope.clicked = true;
				$scope.safety_factor_numberOfPages = 0;
				$http({
					method : 'POST',
					url : post_url + '?' + req_parameters,
					//data : req_parameters,
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					}
				}).success(function(data, status, headers) {
					$location.search('field_value', $scope.search.field_value);
					$location.search('store_number', $scope.search.store_number);
					$location.search('sku_dept', $scope.search.sku_dept);
					$location.search('node_type', $scope.search.node_type);
					$location.search('dept_number', $scope.search.dept_number);
					$location.search('export_result_csv', export_result_csv);
					$scope.isActive = false;
					$scope.button = "Search";
					$scope.clicked = false;
					$scope.res_greater_than_500=data.result_greater_than_500;
					console.log("headers:"+headers('content-type'));
					if($scope.res_greater_than_500 == "true" || headers('content-type') == 'text/csv;charset=ISO-8859-1' || export_result_csv){
						var resultTxt = "			Exporting Result Set 			\n "+"\n			Press OK to Confirm Export and \"Download Now\" link will appear below 			";
						
						if(export_result_csv==true){
							 $scope.download_message = "Click download button above to retrieve data in CSV format.";	 
						 }else{
							 $scope.download_message = "Dataset too large to display, please click download button above to retrieve data in CSV format.";
							 $scope.isLargeDownload = true;
						 }
						
						// if ($window.confirm(resultTxt) == true) {
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
							.attr("download", "safetyFactorDetails.csv")
							.attr("style", "")
							;
							 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
						//}
						 $scope.export_download_btn_label = "Download";
						 $scope.safety_factor_data = null;
					}
					else{
						$scope.isLargeDownload = false;
						$scope.export_download_btn_label = "Export & Download"; 
						$scope.safety_factor_data = data;
						console.log($scope.safety_factor_data);
						$scope.safety_factor_numberOfPages = getNumberOfPages($scope.safety_factor_data.safety_factor_result.length,$scope.pageSize);
					}
				});
			//}
		};


		$scope.getListforDropDowns = function() {

 				$http({
			    	method:		'POST',
			    	url: 		STOREELF_ROOT_URI+'/Utility/OrderManagement/get_list_of_stores',
			    	//data:		"paramOne=" + parameter.fieldName,
			    	headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					$scope.stores = data;
				});

 				$http({
				    method:		'POST',
				    url: 		STOREELF_ROOT_URI+'/Utility/OrderManagement/get_list_of_depts',
				    //data:		"paramOne=" + parameter.fieldName,
				    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					$scope.depts = data;
				});
			};
	}
</script>

<div ng-app="OrderManagementSafetyFactorApp">
	<div ng-controller="SafetyFactorSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Safety Factor</li>
			</ol>
		</div>
		<div class="col-sm-12">
			<section class="panel">

				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form" data-ng-init="getListforDropDowns()">
						<div class="form-group">
				    		<label class="col-sm-2 control-label">Store Number</label>
				    			<div class="col-sm-3">
									<select name="store_number" class="form-control" ng-model="search.store_number" required>
										<option value=""></option>
										<option ng-repeat="store in stores | orderBy:'store_number'"  ng-value="{{store.store_number}}">{{store.store_number}} - {{store.store_description}}</option>
									</select>
				    			</div>
				  		</div>

				  		<div class="form-group">
				  			<label class="col-sm-2 control-label">SKU or Department?</label>
				   				<div class="col-sm-3">
									<select name="node_type" class="form-control" ng-model="search.sku_dept" data-ng-init="search.sku_dept='SKU'"required>
										<option value="SKU" ng-selected="true">SKU</option>
										<option value="DEPT">DEPARTMENT</option>
									</select>
				    			</div>
				  		</div>

				  		<div class="form-group" ng-hide="search.sku_dept == 'DEPT'">
							<label class="col-sm-2 control-label">Enter Value(s)</label>
								<div class="col-sm-5">

									<textarea rows="5" class="form-control" maxlength="7001"
									ng-model="search.field_value" onclick="" type="text"
									ng-init="search.field_value=''" placeholder="Please Enter Values Example: 05521001, 05521002, 05521003, 05521004, ..."
									> </textarea>
								</div>
				  		</div>

				  		<div class="form-group" ng-hide="search.sku_dept != 'DEPT'">
				    		<label class="col-sm-2 control-label">Department Number</label>
				    			<div class="col-sm-3">
									<select name="dept_number" class="form-control" ng-model="search.dept_number" required>
										<option value=""></option>
										<option ng-repeat="dept in depts "  ng-value="{{dept.dept_number}}">{{dept.dept_number}} - {{dept.dept_description}}</option>
									</select>
				    			</div>
				  		</div>


				  		<div class="form-group">
				  			<label class="col-sm-2 control-label">Sourcing Type</label>
				   				<div class="col-sm-3">
									<select name="node_type" class="form-control" ng-model="search.node_type" data-ng-init="search.node_type='ALL'"required>
										<option value="ALL">ALL</option>
										<option value="SHP">SFS</option>
										<option value="PICK">BOPUS</option>
									</select>
				    			</div>
				  		</div>

						<div class="form-group">
				   			 <div class="col-sm-offset-2 col-sm-10">
				      			<button ng-disabled="
									!(
										!!(search.sku_dept=='DEPT' && search.dept_number)
											||
										!!(search.sku_dept=='SKU' && search.field_value)
											||
										isLoading()
									)
										||
									(clicked==true)
								" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getSafetyFactorDetails($location.search(), false); $('#export_all_trigger').css('visibility','hidden')">{{button}}</button>
								<!-- <a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;">Download Now</a> -->
								<span class="help-block">{{download_message}}</span>
				    		</div>
				  		</div>

					</form>
				</div>
			</section>
		</div>
		<div class="col-sm-12" >
			<section class="panel">
				<header class="panel-heading">
					<a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;" ng-click="(isLargeDownload) ? console.log('user downloading data...') : getSafetyFactorDetails($location.search(), true);" >{{export_download_btn_label}}</a>
					<!-- <a role="button" ng-click="getSafetyFactorDetails($location.search(), true)">Export All</a> -->
				</header>
				<br>
				<h5><font color="blue"> <i>If Store level Safety factor exists that will be the safety factor for BOPUS and SFS</i></font></h5>
				<table id="safety_factor_table"
					class="table table-hover table-condensed table-bordered table-responsive"
					summary="Code page support in different versions of MS Windows."
					rules="groups" frame="hsides" >
					<colgroup align="center"></colgroup>
					<colgroup align="left"></colgroup>
					<colgroup span="2" align="center"></colgroup>
					<colgroup span="3" align="center"></colgroup>
					<thead>
						<tr>
							<th>Store No</th>
							<th>Sourcing Type</th>
							<th>Dept</th>
							<th>SKU</th>
							<th>Safety Factor</th>
							<th>Safety Stock Type</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="r in safety_factor_data.safety_factor_result | orderBy:['SKU','-SS_TYPE'] | startFrom:currentPage*pageSize | limitTo:pageSize">
							<td ng-bind="r.STORE_NO"></td>
							<td ng-bind="r.SOURCING_TYPE"></td>
							<td ng-bind="r.DEPT"></td>
							<td ng-bind="r.SKU"></td>
							<td ng-bind="r.SAFETY_FACTOR"></td>
							<td ng-bind="r.SS_TYPE"></td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
		<div class="col-sm-12 text-center">
			<div class="btn-group" ng-show="safety_factor_data != null">
				<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">Previous</button>
   				<button type="button" class="btn btn-default" disabled="disabled" ng-bind-template="Page {{currentPage+1}} of {{safety_factor_numberOfPages}}"></button>
   				<button type="button" class="btn btn-default" ng-disabled="currentPage >=safety_factor_data.safety_factor_result.length/pageSize - 1" ng-click="currentPage=currentPage+1">Next</button>
   			</div>
		</div>
	</div>
</div>