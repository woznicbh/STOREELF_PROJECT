<script type="text/javascript">
	
	//your app MUST be named starting with it's associated Servlet name 
	ANGULARJS_APP = angular.module('OrderManagementSimDashboardApp', []);
	
	/**
		this will add a custom directive as 'my-custom-attribute'; the JS will auto-insert the '-'
		Example:
			<div my-custom-attribute="something" targetAttribute="">text</div>
			<img my-custom-attribute="something" src=""/>
	**/
	ANGULARJS_APP.directive('mycustomattribute', function() {
	    return {
	        link: function(scope, element, attrs) {
	            var fullPathUrl = "<%=request.getContextPath()%>"+"/";
	            if(element[0].tagName === "IMG") {
	                attrs.$set('targetAttribute', fullPathUrl + attrs.fullPath);
	            }
	        },
	    }
	});
	
 	function SimDashboardController($scope, $http){
 		
 		$scope.predicate = 'sku';
 		$scope.reverse = 'first';
 		
		$scope.getDashboard = function(search) {
			
			
 			if(search=="csv"){
 				
 				window.location.href=STOREELF_ROOT_URI+'/Utility/OrderManagement/sim_dashboard' + "?type=" + search;
 				
 			} else {
 			
	 			$http({
					//request method, this should remain 'POST'
				    method: 'POST',
				    url: STOREELF_ROOT_URI+'/Utility/OrderManagement/sim_dashboard',
				    data: "type=" + search,
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {				
					$scope.results = data.sim_array;
					$scope.update_time = data.update_time;
				});
 			}
		};
	}
</script>
    
<div ng-app="OrderManagementSimDashboardApp">
	<div ng-controller="SimDashboardController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">SIM Dashboard</li>
			</ol>
		</div>
						
		<!-- SIMPLE TABLE -->
		<div class="col-sm-12">
		<section class="panel">
			<header class="panel-heading">
				<a href="#" role="button" ng-model="search" ng-click="getDashboard('csv')">Export All</a>						
				<b style="float: right">Last Updated: {{update_time}}</b>													
			</header>
			<table class="table table-hover table-condensed table-bordered table-responsive" ng-init="getDashboard('table')">
				<thead>
					<tr>
						<th ng-click="predicate = 'EXTN_DEPT'; reverse=!reverse">Dept
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'EXTN_DEPT'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'ITEM_ID'; reverse=!reverse">SKU
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'ITEM_ID'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHORT_DESCRIPTION'; reverse=!reverse">Description
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'SHORT_DESCRIPTION'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'ITEM_TYPE'; reverse=!reverse">Itemtype
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'ITEM_TYPE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SHIPNODE_KEY'; reverse=!reverse">Node
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'SHIPNODE_KEY'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'SUPPLY'; reverse=!reverse">Supply
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'SUPPLY'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'DEMANDQTY'; reverse=!reverse">Demand
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'DEMANDQTY'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'AVAILABLE'; reverse=!reverse">Available
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'AVAILABLE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'EXTN_SHIP_NODE_SOURCE'; reverse=!reverse">Node Source
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'EXTN_SHIP_NODE_SOURCE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>						
						<th ng-click="predicate = 'EXTN_DIRECT_SHIP_ITEM'; reverse=!reverse">Direct Ship
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'EXTN_DIRECT_SHIP_ITEM'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'EXTN_SHIP_ALONE'; reverse=!reverse">Ship Alone
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'EXTN_SHIP_ALONE'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
						<th ng-click="predicate = 'EXTN_CAGE_ITEM'; reverse=!reverse">Cage
							<span style="float: right" ng-show="reverse == 'first'">
							<span style="position: absolute"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
							
							<span style="float: right" ng-show="predicate == 'EXTN_CAGE_ITEM'">
							<span ng-show="!reverse"> <i class="fa fa-sort-desc" id="orderArrow"></i></span>
							<span ng-show="reverse"> <i class="fa fa-sort-asc" id="orderArrow"></i></span>
							</span>
						</th>
					</tr>
					<tr>
						<th><input class="filter" ng-keypress="filterStuff('EXTN_DEPT', 'dept')" ng-model="dept"></th>
						<th><input class="filter" ng-keypress="filterStuff('ITEM_ID','sku')" ng-model="sku"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="description"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="itemtype"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="node"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="supply"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="demand"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="available"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="nodesource"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="directship"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="shipalone"></th>
						<th><input class="filter" ng-keypress="filterStuff('','this')" ng-model="cage"></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="result in results | orderBy:predicate:reverse | filter:{EXTN_DEPT : dept, ITEM_ID : sku, SHORT_DESCRIPTION : description, ITEM_TYPE : itemtype, SHIPNODE_KEY : node, SUPPLY : supply, DEMANDQTY : demand, AVAILABLE : available, 
					EXTN_SHIP_NODE_SOURCE : nodesource, EXTN_RED_PACK_LIST_TYPE : redpacktype, EXTN_NOMADIC : nomadic, EXTN_DIRECT_SHIP_ITEM : directship, EXTN_SHIP_ALONE : shipalone, EXTN_CAGE_ITEM : cage,
					EXTN_IS_PLASTIC_GIFT_CARD : giftcard, EXTN_BREAKABLE : breakable, ALLOW_GIFT_WRAP : allowgift, EXTN_BAGGAGE : baggage}">
						<td>{{result.EXTN_DEPT}}</td>
						<td>{{result.ITEM_ID}}</td>
						<td>{{result.SHORT_DESCRIPTION}}</td>
						<td>{{result.ITEM_TYPE}}</td>
						<td>{{result.SHIPNODE_KEY}}</td>
						<td>{{result.SUPPLY}}</td>
						<td>{{result.DEMANDQTY}}</td>
						<td>{{result.AVAILABLE}}</td>
						<td>{{result.EXTN_SHIP_NODE_SOURCE}}</td>
						<td>{{result.EXTN_DIRECT_SHIP_ITEM}}</td>
						<td>{{result.EXTN_SHIP_ALONE}}</td>
						<td>{{result.EXTN_CAGE_ITEM}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>		
	</div>
</div>