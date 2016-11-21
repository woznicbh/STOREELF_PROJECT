<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />

<script type="text/javascript">
	ANGULARJS_APP = angular.module('WarehouseManagementCollatePrintTimesApp', ['ngAnimate', 'ngSanitize', 'mgcrea.ngStrap']);

 	function FindCollatePrintTimesController($scope, $http){
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		$scope.clearRequesterCheckbox = function(){
 			if($scope.search.printArea=='ALL'){
 				$scope.search.print_requester = false;	
 			}
 	    };
				
		$scope.findCollatePrintTimes = function() {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/collate_print_times',
			    data:	 
				    	"efc_number="			+ $scope.search.EFC_number
				    	+"&print_area="			+ $scope.search.print_area
				    	+"&time_breakdown="		+ $scope.search.time_breakdown				    	
				    	+"&from_date="			+ $scope.search.from_date
				    	+"&until_date="			+ $scope.search.until_date				    					    	
				    	+"&print_requester="	+ $scope.search.print_requester
				    	+"&print_server="		+ $scope.search.print_server
			    	,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data) {
				$scope.collatePrintTimes = data.data;				
				$scope.collatePrintTimes_groupByPrintServerFlag = data.user_requested_print_server;
				$scope.collatePrintTimes_showPrintRequesterFlag = data.user_requested_print_requester;
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};
	}
</script>
    
<div ng-app="WarehouseManagementCollatePrintTimesApp">
	<div ng-controller="FindCollatePrintTimesController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">Collate Print Times</li>
			</ol>
		</div>
		<div class="col-sm-12">
		
		<section class="panel">
		
		<div class="panel-body">
		<form name="form" class="form-horizontal"tasi-form role="form" ng-submit="findCollatePrintTimes()">
			  <div class="form-group">
			    <label class="col-sm-2 control-label">EFC No*</label>
			    <div class="col-sm-3">
					<select name="EFC_number" class="form-control" ng-model="search.EFC_number" data-ng-init="search.EFC_number='EFC1'" required>
						<option selected="selected">EFC1</option>					
						<option>EFC2</option>					
						<option>EFC3</option>
						<option>EFC4</option>						
					</select>
			    </div>
			  </div>
			  
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Limit Print Area To</label>
			    <div class="col-sm-3">
					<select name="print_area" class="form-control" id="limit_print_area_to" ng-model="search.print_area" data-ng-init="search.print_area='ALL'" ng-change="clearRequesterCheckbox()">
						<option selected="selected">ALL</option>
						<option>PACKOUT</option>
						<option>PEAK</option>
						<option>PUTWALL</option>
						<option>SORTER</option>
						<option>WAVE</option>
					</select>
			    </div>
			  </div>
<!-- Commented because 2012 version DB is not having any print servers-->				  
<!-- 			  <div class="form-group animate-hide animate-show" ng-show="search.print_area!='ALL'">
			    <div class="col-sm-offset-2 col-sm-10">
			      <div class="checkbox">
				    <label>
				      <input name="print_requester" type="checkbox" ng-model="search.print_requester" data-ng-init="search.print_requester=false"> Show Print Requester
				    </label>
				  </div>
			    </div>
			  </div> -->
			  			  
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Time Breakdown</label>
			    <div class="col-sm-3">
					<select name="time_breakdown" class="form-control" id="time_breakdown" ng-model="search.time_breakdown" data-ng-init="search.time_breakdown='DAILY'" required>
						<option>DAILY</option>
						<option>HOURLY</option>
					</select>
			    </div>
			  </div>
			
			<div class="form-group">
				<label class="control-label col-md-2">Date range* <small>(dynamic)</small></label>
				<div class="col-md-10">
					<div class="input-group input-large" data-date="2012-01-01T00:00:00Z" data-date-format="mm-dd-yyyy+HH:mm:ss">
						<input type="text" class="form_datetime-meridian-new form-control" name="from_date" ng-model="search.from_date" required="required">
						<span class="input-group-addon">To</span>
						<input type="text" class="form_datetime-meridian-new form-control" name="until_date" ng-model="search.until_date" required="required">
					</div>
					<span class="help-block">Select date range</span>
				</div>
			</div>					
<!-- Commented because 2012 version DB is not having any print servers-->				
<!-- 			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <div class="checkbox">
				    <label>
				      <input type="checkbox" ng-model="search.print_server" data-ng-init="search.print_server=false"> Group by Print Server
				    </label>
				  </div>
			    </div>
			  </div> -->
			  
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="!(!!search.EFC_number && !!search.from_date && !!search.until_date)  || (clicked == true)" type="submit" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" ng-click="findCollatePrintTimes()">{{button}}</button>
			    </div>
			  </div>
		</form>
		</div>
</section>
		</div>
		
		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Date</th>
						<th ng-show="collatePrintTimes_groupByPrintServerFlag">Server</th>
						<th ng-show="collatePrintTimes_showPrintRequesterFlag">Requester</th>
						<th>1-5</th>
						<th>6-10</th>
						<th>11-15</th>
						<th>16-20</th>
						<th>21-30</th>
						<th>31-40</th>
						<th>41-50</th>
						<th>51-60</th>
						<th>61-120</th>
						<th>121-300</th>
						<th>301-1800</th>
						<th>1800+</th>
						<th>Total</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="row in collatePrintTimes" display-loading-notification>
						<td>{{row.DTE}}</td>
						<td ng-show="collatePrintTimes_groupByPrintServerFlag">{{row.PRINT_SRV}}</td>
						<td ng-show="collatePrintTimes_showPrintRequesterFlag">{{row.PRNT_AREA}}</td>
						<td>{{row.1_5}}</td>
						<td>{{row.6_10}}</td>
						<td>{{row.11_15}}</td>
						<td>{{row.16_20}}</td>
						<td>{{row.21_30}}</td>
						<td>{{row.31_40}}</td>
						<td>{{row.41_50}}</td>
						<td>{{row.51_60}}</td>
						<td>{{row.61_120}}</td>
						<td>{{row.121_300}}</td>
						<td>{{row.301_1800}}</td>
						<td>{{row.1800}}</td>
						<td>{{row.TOTAL}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>