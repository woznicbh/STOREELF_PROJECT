<script type="text/javascript">
	ANGULARJS_APP = angular.module('WarehouseManagementCartonDetailApp', []);
	
 	function CartonDetailController($scope, $http){
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.getCartonDetails = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
				
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/lpn_detail',
			    data: 
			    	"efc_no_carton="	+ search.EFCNumber	+
			    	"&carton_no="		+ search.cartonNumber
			    	,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				//EFC 1/2/3/4 2012 upgrade starts			
				switch(search.EFCNumber) {
   				case 'EFC1':
   					$scope.cartonHeaderInformation		= data.UT_Carton_Search_Carton_Header;
   					$scope.cartonDetailInformation		= data.UT_Carton_Search_Carton_Detail;
   					$scope.pickticketHeaderInformation	= data.UT_Carton_Search_Pickticket_Header;
       			break;
   				case 'EFC2':
   					$scope.cartonHeaderInformation		= data.UT_Carton_Search_Carton_Header;
   					$scope.cartonDetailInformation		= data.UT_Carton_Search_Carton_Detail;
   					$scope.pickticketHeaderInformation	= data.UT_Carton_Search_Pickticket_Header;
       			break;
   				case 'EFC3':
   					$scope.cartonHeaderInformation		= data.UT_Carton_Search_Carton_Header;
   					$scope.cartonDetailInformation		= data.UT_Carton_Search_Carton_Detail;
   					$scope.pickticketHeaderInformation	= data.UT_Carton_Search_Pickticket_Header;
       			break;
   				case 'EFC4':
   					$scope.cartonHeaderInformation		= data.UT_Carton_Search_Carton_Header;
   					$scope.cartonDetailInformation		= data.UT_Carton_Search_Carton_Detail;
   					$scope.pickticketHeaderInformation	= data.UT_Carton_Search_Pickticket_Header;
       			break;
   				default:
   					$scope.cartonHeaderInformation		= data.UT_Carton_Search_Carton_Header;
					$scope.cartonDetailInformation		= data.UT_Carton_Search_Carton_Detail;
					$scope.pickticketHeaderInformation	= data.UT_Carton_Search_Pickticket_Header;
}
				//EFC 1/2/3/4 2012 upgrade ends				
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};
	}
</script>
    
<div ng-app="WarehouseManagementCartonDetailApp">
	<!-- 
		The ngController directive attaches a controller class to the view. 
		This is a key aspect of how angular supports the principles behind the Model-View-Controller design pattern.
		
		ng-controller should be defined as your defined controller above
		  
		@see http://docs.angularjs.org/api/ng/directive/ngController
	-->
	<div ng-controller="CartonDetailController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">LPN Detail</li>
			</ol>
		</div>
		
		<div class="col-sm-12">
		
		<section class="panel">
		
		<div class="panel-body">	
		
		<form name="form" class="form-horizontal" role="form" ng-submit="getCartonDetails(search)">
			  <div class="form-group">
			    <label class="col-sm-2 control-label">EFC No</label>
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
			    <label class="col-sm-2 control-label">Carton No</label>
			    <div class="col-sm-3">
					<input type="text" class="form-control" ng-model="search.cartonNumber" placeholder="Carton Number" required/>
			    </div>
			  </div>
			  
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="form.$invalid || isLoading() || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getCartonDetails(search)">{{button}}</button>
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
						<th>Carton No</th>
						<th>Type</th>
						<th>Singles</th>
						<th>Divert</th>
						<th>GRP Code</th>
						<th>Chute Assignment</th>
						<th>Chute Id</th>
					</tr>
				</thead>
				<tbody>				
					<tr ng-repeat="row in cartonHeaderInformation">
						<td>{{row.CARTON_NBR}}</td>
						<td>{{row.TYPE}}</td>
						<td>{{row.SINGLES}}</td>
						<td>{{row.DIVERT}}</td>
						<td>{{row.CARTON_GRP_CODE}}</td>
						<td>{{row.CHUTE_ASSIGN_TYPE}}</td>
						<td>{{row.CHUTE_ID}}</td>
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
						<th>Carton No</th>
						<th>Pickticket No</th>
						<th>Sequence No</th>
						<th>SKU</th>
						<th>To Be Packed</th>
						<th>Packed</th>
						<th>Modification Time</th>
						<th>User Id</th>
						<th>Line Item Stat</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>				
					<tr ng-repeat="row in cartonDetailInformation">
						<td>{{row.CARTON_NBR}}</td>
						<td>{{row.PKT_CTRL_NBR}}</td>
						<td>{{row.PKT_SEQ_NBR}}</td>
						<td>{{row.SKU_ID}}</td>
						<td>{{row.TO_BE_PAKD}}</td>
						<td>{{row.PAKD}}</td>
						<td>{{row.MOD_DATE_TIME}}</td>
						<td>{{row.USER_ID}}</td>
						<td>{{row.LINE_ITEM_STAT}}</td>
						<td>{{row.STATUS}}</td>
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
						<th>Pickticket No</th>
						<th>Customer Po Nbr</th>
						<th>Order Type</th>
						<th>Stat Code</th>
						<th>Order Date</th>
					</tr>
				</thead>
				<tbody>		
					<tr ng-repeat="row in pickticketHeaderInformation">
						<td>{{row.PKT_CTRL_NBR}}</td>
						<td>{{row.CUST_PO_NBR}}</td>
						<td>{{row.ORD_TYPE}}</td>
						<td>{{row.STAT_CODE}}</td>
						<td>{{row.ORD_DATE}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>		
	</div>
</div>