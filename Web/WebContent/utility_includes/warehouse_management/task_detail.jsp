<script type="text/javascript">
	ANGULARJS_APP = angular.module('WarehouseManagementTaskDetailApp', []);
	
 	function TaskDetailController($scope, $http){
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.getTaskDetails = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/task_detail',
			    data: 
			    	"efc_no_task="		+ search.EFCNumber	+
			    	"&task_id="			+ search.taskId
			    	,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				//EFC 1/2/3/4  2012 upgrade starts							
				switch(search.EFCNumber) {
   				case 'EFC1':
   	   			$scope.taskHeaderInformation	= data.UT_Task_Header;
   				$scope.taskDetailInformation	= data.UT_Task_Detail;
   				$scope.taskAllocationInformation	= data.UT_Task_Allocation;
   				$scope.caseDetailInformation	= data.UT_Task_Case_Details;
       			break;
   				case 'EFC2':
   	   	   		$scope.taskHeaderInformation	= data.UT_Task_Header;
   	   			$scope.taskDetailInformation	= data.UT_Task_Detail;
   	   			$scope.taskAllocationInformation	= data.UT_Task_Allocation;
   	   			$scope.caseDetailInformation	= data.UT_Task_Case_Details;
       			break;
   				case 'EFC3':
   	   	   	   	$scope.taskHeaderInformation	= data.UT_Task_Header;
   	   	   		$scope.taskDetailInformation	= data.UT_Task_Detail;
   	   	   		$scope.taskAllocationInformation	= data.UT_Task_Allocation;
   	   	   		$scope.caseDetailInformation	= data.UT_Task_Case_Details;
       			break;
   				case 'EFC4':
   	   	   	   	$scope.taskHeaderInformation	= data.UT_Task_Header;
   	   	   		$scope.taskDetailInformation	= data.UT_Task_Detail;
   	   	   		$scope.taskAllocationInformation	= data.UT_Task_Allocation;
   	   	   		$scope.caseDetailInformation	= data.UT_Task_Case_Details;
       			break;
   				default:
   	   	   	   	$scope.taskHeaderInformation	= data.UT_Task_Header;
   	   			$scope.taskDetailInformation	= data.UT_Task_Detail;
   	   			$scope.taskAllocationInformation	= data.UT_Task_Allocation;
   	   			$scope.caseDetailInformation	= data.UT_Task_Case_Details;
}
				//EFC 1/2/3/4  2012 upgrade ends
				$scope.isActive = false;
				$scope.clicked = false;
				$scope.button = "Search";
			});
		};
	}
</script>
    
<div ng-app="WarehouseManagementTaskDetailApp">
	<div ng-controller="TaskDetailController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">Task Detail</li>
			</ol>
		</div>
		
		<div class="col-sm-12">
		
		<section class="panel">
		
		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-submit="getTaskDetails(search)">
			  <div class="form-group">
			    <label class="col-sm-2 control-label">EFC No*</label>
			    <div class="col-sm-3">
					<select name="EFCNumber" class="form-control" ng-model="search.EFCNumber" data-ng-init="search.EFCNumber='EFC1'" required>
						<option>EFC1</option>
						<option>EFC2</option>
						<option>EFC3</option>
						<option>EFC4</option>
					</select>
			    </div>
			  </div>
			  
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Task Id*</label>
			    <div class="col-sm-3">
					<input name="taskId" type="text" class="form-control" ng-model="search.taskId" placeholder="Task Number" required/>
			    </div>
			  </div>
			  
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="form.$invalid || isLoading() || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getTaskDetails(search)">{{button}}</button>
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
							<th>Task No</th>
							<th>Create Date Time</th>
							<th>Modification Time</th>
							<th>User Id</th>
							<th>Stat Code</th>
							<th>Status</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="row in taskHeaderInformation">
							<td>{{row.TASK_ID}}</td>
							<td>{{row.CREATE_DATE_TIME}}</td>
							<td>{{row.MOD_DATE_TIME	}}</td>
							<td>{{row.USER_ID}}</td>
							<td>{{row.STAT_CODE}}</td>
							<td>{{row.STATUS}}</td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
		
		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Allocation Inventory Id</th>
						<th>Modification Time</th>
						<th>Destination Location</th>
						<th>Need</th>
						<th>SKU</th>
						<th>Qty Alloc</th>
						<th>Qty Pulled</th>
						<th>Stat Code</th>
						<th>Status</th>
						<th>User Id</th>
					</tr>
				</thead>
				<tbody>		
					<tr ng-repeat="row in taskDetailInformation">
						<td>{{row.AID_ID}}</td>
						<td>{{row.MOD_DATE_TIME}}</td>
						<td>{{row.DEST_LOCN}}</td>
						<td>{{row.NEED}}</td>
						<td>{{row.SKU_ID}}</td>	
						<td>{{row.QTY_ALLOC}}</td>
						<td>{{row.QTY_PULLD}}</td>
						<td>{{row.STAT_CODE}}</td>
						<td>{{row.STATUS}}</td>
						<td>{{row.USER_ID}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
		
		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Allocation Inventory Id</th>
						<th>Create Date Time</th>
						<th>Destination Location</th>
						<th>Need</th>
						<th>SKU</th>
						<th>Qty Alloc</th>
						<th>Qty Pulled</th>
						<th>Stat Code</th>
						<th>Status</th>
						<th>Pickticket No</th>
					</tr>
				</thead>
				<tbody>		
					<tr ng-repeat="row in taskAllocationInformation">
						<td>{{row.AID_ID}}</td>	
						<td>{{row.CREATE_DATE_TIME}}</td>	
						<td>{{row.DEST_LOCN}}</td>	
						<td>{{row.NEED}}</td>	
						<td>{{row.SKU_ID}}</td>	
						<td>{{row.QTY_ALLOC}}</td>	
						<td>{{row.QTY_PULLD}}</td>	
						<td>{{row.STAT_CODE}}</td>	
						<td>{{row.STATUS}}</td>	
						<td>{{row.PKT_CTRL_NBR}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
		
		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<caption>
					
				</caption>
				<thead>
					<tr>
						<th>DSP</th>
						<th>Case No</th>
						<th>Stat Code</th>
						<th>Status</th>
						<th>Destination Location</th>
						<th>Sent</th>
						<th>Diverted</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="row in caseDetailInformation">
						<td>{{row.DSP_LOCN}}</td>
						<td>{{row.CASE_NBR}}</td>
						<td>{{row.STAT_CODE}}</td>
						<td>{{row.STATUS}}</td>
						<td>{{row.DEST_LOCN}}</td>
						<td>{{row.SENT_TO}}</td>
						<td>{{row.DIVERTED}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>		
	</div>
</div>