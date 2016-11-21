<link rel="stylesheet" href="<%=request.getContextPath()%>/public/v2_public_files/css/autocomplete.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/autocomplete.js"></script>
<script type="text/javascript">
	
	//your app MUST be named starting with it's associated Servlet name 
	ANGULARJS_APP = angular.module('WarehouseManagementShipViaApp', ['autocomplete']);
	
	//define your controller, function name MUST end with 'Controller'
 	function ShipViaController($scope, $http){
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		$scope.shipViaAutoCompleteData = null;
 		$scope.searchparam='';
 		$scope.codemodelchange = function(ship){
 	          $scope.searchparam = ship;
 	      }
 		
 		$scope.preLoadShipVia = function() {
 			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/ship_via',
			    data: "return_type=auto_complete",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.shipViaAutoCompleteData = data;
				
			});
 		};
 		
 		//define your controllers calling function 		
		$scope.AutoCompleteShipViaCodes = function(shipvia) {};
		
 		//define your controllers calling function
		$scope.AutoCompleteShipViaDesc = function(shipvia) {
			if(shipvia){
				if($scope.shipViaAutoCompleteData){
					var inputFieldValue_description_code = $scope.shipViaAutoCompleteData.list[shipvia.description];
					
					if(inputFieldValue_description_code){
						$("#ship_via_code").val(inputFieldValue_description_code);
						shipvia.code = inputFieldValue_description_code;
					}
				}
			}
		};
		
 		//define your controllers calling function
		$scope.ShipVia = function(shipvia) {
			if(shipvia){
				$scope.isActive = true;
				$scope.clicked = true;
				$scope.button = "Processing...";
				$http({
				    method: 'POST',
				    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/ship_via',
				    data: "return_type=shipvia" + "&ship_via_code="+shipvia.code + "&ship_via_desc="+shipvia.description,
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					$scope.shipViaData = data;
					$scope.isActive = false;
			 		$scope.clicked = false;
			 		$scope.button = "Search";
				});
			}else{
				alert("Error - form incomplete");
			} 
		};		
	}
</script>
<script>
$(document).ready(function(){
	$('.codefind').keyup(function(){
		if($(this).val()){
			$('.codecomplete').addClass('open');	
		}else{
			$('.codecomplete').removeClass('open');
		}
	});
});
</script>


    
<div ng-app="WarehouseManagementShipViaApp">	
	<div ng-controller="ShipViaController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">Ship Via</li>
			</ol>
		</div>
		<div class="col-sm-12" data-ng-init="preLoadShipVia()">
		<section class="panel">
		
		<div class="panel-body">	
			<form name="form" class="form-horizontal shipviadrop" role="form" ng-submit="ShipVia(shipvia)">
			<!-- TODO: UPDATE THIS CALENDAR WITH NEW FLATLAB CAL. -->
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Ship Via Code</label>
			    <div class="col-sm-4">			      
			      <autocomplete name="code" attr-input-id="ship_via_code" attr-placeholder="Ship Via Code" ng-model="shipvia.code" data="shipViaAutoCompleteData.codes" required></autocomplete>
			    </div>
			  </div>
			  <div class="form-group">			    
			    <label class="col-sm-2 control-label">Ship Via Desc</label>
			    <div class="col-sm-4">			      
			      <autocomplete name="description" attr-input-id="ship_via_desc" attr-placeholder="Ship Via Description" ng-model="shipvia.description" data="shipViaAutoCompleteData.descs" on-type="AutoCompleteShipViaDesc(shipvia)"></autocomplete>
			    </div>
			  </div>
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="form.$invalid || isLoading() || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="ShipVia(shipvia)">{{button}}</button>
			    </div>
			  </div>
			</form>
			</div>
			
		
</section>
		</div><!-- /input-group -->
		
		
		<div class="col-sm-12">
			<section class="panel">
				<table class="table table-hover table-condensed table-bordered table-responsive">
			     	<thead>
				        <tr>
					        <th>EFC</th>
					        <th>Description</th>
					        <th>Code</th>
					        <th>Carrier ID</th>
					        <th>Label Type</th>
					        <th>Service Type</th>
				        </tr>
			     	</thead>
					<tr ng-repeat="result in shipViaData | filter:shipvia.description ">
						<td>{{result.EFC}}</td>
						<td>{{result.DESCRIPTION}}</td>
						<td>{{result.SHIP_VIA}}</td>
						<td>{{result.CARRIER_ID}}</td>
						<td>{{result.LABEL_TYPE}}</td>
						<td>{{result.SERVICE_LEVEL_INDICATOR}}</td>
					</tr>
				</table>
			</section>
		</div>
	</div>
</div>