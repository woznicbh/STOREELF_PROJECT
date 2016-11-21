<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />
	<script type="text/javascript">
	
	//your app MUST be named starting with it's associated Servlet name 
	ANGULARJS_APP = angular.module('SourcingDetailApp', []);
	
	//define your controller, function name MUST end with 'Controller'
 	function UnitCapacityController($scope, $http, $location){
 		var post_url =STOREELF_ROOT_URI+'/Utility/SourcingDetail/sourcing_unit_capacity';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		$scope.export_download_btn_label = "Export & Download";
		$scope.isLargeDownload = false;
		
 		//define your controllers calling function
		$scope.getStoreUnitCapacity = function(search, export_result_csv) {
 			
 			var req_parameters =
			    "store_number="			+ (($scope.search.store_number)												?	$scope.search.store_number	:'')+
			    "&field_value="			+ (($scope.search.field_value)												?	$scope.search.field_value	:'')+
			    "&node_type="			+ (($scope.search.node_type)												?	$scope.search.node_type		:'')+
			    "&zero_cap="			+ (($scope.search.zero_cap)													?	$scope.search.zero_cap  	:'')+
			    "&export_result_csv="	+ ((export_result_csv)														?	export_result_csv			:'')+
			    "&ajax_request=true"
			    ;
 			
 			//if (export_result_csv == true) {
			//	window.location.href = post_url + '?' + req_parameters;
			//} else {
				if(search) $scope.search = search;
 				$scope.isActive = true;
 				$scope.clicked = true;
 				$scope.button = "Processing...";
 				
				$http({
			    	method:		'POST',
			    	url: 		post_url,
			   		data:		"store_number=" 		+ $scope.search.store_number
			    		 	  + "&field_value=" 		+ search.field_value
			    		 	  + "&node_type="   		+ search.node_type
			    		  	  + "&zero_cap="			+ search.zero_cap
			    		  	  + "&export_result_csv="	+ export_result_csv,
			    	headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status, headers) {
					$location.search('store_number', $scope.search.field_name);
					$location.search('field_value', $scope.search.field_value);
					$location.search('node_type', $scope.search.vendor_id);
					$location.search('zero_cap', $scope.search.from_date);
					$location.search('export_result_csv', export_result_csv);
					$scope.isActive = false;
					$scope.clicked = false;
					$scope.button = "Search";
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
							.attr("download", "SourcingUnitCapacity.csv")
							.attr("style", "")
							;
							 //window.open('data:text/csv;charset=ISO-8859-1,' + encodeURIComponent(data) );
						//}
						
						$scope.export_download_btn_label = "Download";
						$scope.store_capacity_data = null;
					}else{
						$scope.isLargeDownload = false;
						$scope.export_download_btn_label = "Export & Download"; 
						$scope.store_capacity_data	= data;
					}
				});
 			
				//};
  		};

		//define your controllers calling function
		$scope.getListOfStores = function() {
 			
 				$http({
			    	method:		'POST',
			    	url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_list_of_stores',
			    	//data:		"paramOne=" + parameter.fieldName,
			    	headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					$scope.stores = data;
				});
			};			
	}
</script>
    
<div ng-app="SourcingDetailApp">
	<div ng-controller="UnitCapacityController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Sourcing Detail</a></li>
				<li class="active">Sourcing Unit Capacity</li>
			</ol>
		</div>
		<div class="col-sm-12">
		<section class="panel">
		
		<div class="panel-body">
			<form name="form" class="form-horizontal" role="form" data-ng-init="getListOfStores()">
				<div class="form-group">
				    <label class="col-sm-2 control-label">Store Number</label>
				    <div class="col-sm-3">
						<select name="store_number" class="form-control" ng-model="search.store_number" ng-disabled="(!!search.field_value || !!search.zero_cap  || isLoading())" required>
							<option value=""></option>
							<option value="ALL - ALL">ALL - ALL</option>
							<option ng-repeat="store in stores | orderBy:'store_number'"  ng-value="{{store.store_number}}">{{store.store_number}} - {{store.store_description}}</option>
						</select>
				    </div>
				  </div>
				  <div class="form-group">
							<label class="col-sm-2 control-label">Enter Store Numbers</label>
							<div class="col-sm-5">

								<textarea rows="5" class="form-control" maxlength="7001" 
									ng-disabled="(!!search.store_number || !!search.zero_cap || isLoading())"
									ng-model="search.field_value" onclick="" type="text"
									ng-init="search.field_value=''" placeholder="41, 91, 109, 565, 1414, ..."
									> </textarea>
							</div>
				  	</div>
				  	<div class="form-group">
				  	<label class="col-sm-2 control-label">Sourcing Type</label>
				    <div class="col-sm-3">
						<select name="node_type" class="form-control" ng-model="search.node_type" data-ng-init="search.node_type='ALL'"required>
							<option value="ALL">ALL</option>
							<option value="SFS">SFS</option>
							<option value="BOPUS">BOPUS</option>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
			    	<div class="col-sm-offset-2 col-sm-10">
			      		<div class="checkbox">
				    		<label>
				      			<input type="checkbox" ng-model="search.zero_cap" data-ng-init="search.zero_cap=false"> Select to Return 0 Capacity Stores Only
				    		</label>
				  		</div>
			    	</div>
			  	  </div>
			  			
				  <div class="form-group">
				    <div class="col-sm-offset-2 col-sm-10">
				      <button ng-disabled="!((!!search.field_value || !!search.store_number) || !!search.zero_cap || isLoading()) || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getStoreUnitCapacity(search, false);  $('#export_all_trigger').css('visibility','hidden')">{{button}}</button>
				      <span class="help-block">{{download_message}}</span>
				    </div>
				  </div>
			</form>
			</div>
</section>
		</div>
		<div class="col-sm-12">
		<section class="panel" ng-show="store_capacity_data != null">
			<header class="panel-heading">
					<!-- <a role="button" ng-click="getStoreUnitCapacity(search, true)">Export	All</a> -->
					<a id="export_all_trigger" class="btn btn-default" style="visibility: hidden;" ng-click="(isLargeDownload) ? console.log('user downloading data...') : getStoreUnitCapacity(search, true);" >{{export_download_btn_label}}</a>
			</header>
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Store No</th>
						<th>Sourcing Type</th>
						<th>Sunday Capacity</th>
						<th>Monday Capacity</th>
						<th>Tuesday Capacity</th>
						<th>Wednesday Capacity</th>
						<th>Thursday Capacity</th>
						<th>Friday Capacity</th>
						<th>Saturday Capacity</th>
					</tr>
					<tbody>
					<tr ng-repeat="result in store_capacity_data.store_capacity_results | orderBy:['STORE','SOURCING_TYPE']">
						<td>{{result.STORE}}</td>
						<td>{{result.SOURCING_TYPE}}</td>
						<td>{{result.SUNDAY_CAPACITY}}</td>
						<td>{{result.MONDAY_CAPACITY}}</td>
						<td>{{result.TUESDAY_CAPACITY}}</td>
						<td>{{result.WEDNESDAY_CAPACITY}}</td>
						<td>{{result.THURSDAY_CAPACITY}}</td>
						<td>{{result.FRIDAY_CAPACITY}}</td>
						<td>{{result.SATURDAY_CAPACITY}}</td>
					</tr>
				</tbody>
				</thead>
			</table>
			</section>
		</div>
	</div>
</div>