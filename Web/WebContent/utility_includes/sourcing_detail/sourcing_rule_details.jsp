<script src="<%=request.getContextPath()%>/public/v3/js/pulstate.js"
	type="text/javascript"></script>


<script type="text/javascript">
	
	//your app MUST be named starting with it's associated Servlet name 
	ANGULARJS_APP = angular.module('SourcingDetailApp', []);
	
	//define your controller, function name MUST end with 'Controller'
 	function SourcingRuleController($scope, $http){
 		
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
		
 		//define your controllers calling function
		$scope.getSourceDetails = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
 			$("#ruleTableRight td").remove();
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/sourcing_rule_details',
			    data:		"fulfillment_type=" + search.fulfillment_type + "&region_name=" + search.region_name + "&item_class_1=" + search.item_class_1 + "&item_class_2=" + search.item_class_2 + "&item_class_3=" + search.item_class_3 + "&item_class_4=" + search.item_class_4 + "&item_class_5=" + search.item_class_5,
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.source_details	= data;
				$('#distGroupTable > tbody >tr').empty();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};

		//define your controllers calling function
		$scope.getFulfillments = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_fulfillment',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.fil_list = data;
			});
		};
		
$scope.getRegions = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_region',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.reg_list = data;
			});
		};
		
$scope.getItemClass1 = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/item_class_1',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.item_list_1 = data;
			});
		};

$scope.getItemClass2 = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/item_class_2',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.item_list_2 = data;
			});
		};

$scope.getItemClass3 = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/item_class_3',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.item_list_3 = data;
			});
		};
		
		
$scope.getItemClass4 = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/item_class_4',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.item_list_4 = data;
			});
		};
		
$scope.getItemClass5 = function() {
			
			
 			$http({
			    method:		'POST',
			    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/item_class_5',
			    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.item_list_5 = data;
			});
		};
	
	
 	$scope.getRules = function() {
		
		
			$http({
		    method:		'POST',
		    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_rule',
		    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.rule_list = data;
		});
	};
	
	$scope.getRegionDetails = function(distribution_name) {
		$http({
	    method:		'POST',
	    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_region_details',
	    data:		"distribution_name=" + distribution_name,
	    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
	}).success(function(data, status) {
		$scope.detail_list = data;
		$scope.details_title = distribution_name;
	});
	};
	
	  $scope.rowHighlighted = function(row){
		    $scope.selectedRow = row; 
		 };
		
		$scope.getRuleOrder = function(fulfillment_type, region_name, item_classification, item_classification2, item_classification3, item_classification4, item_classification5) {
			$http({
		    method:		'POST',
		    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/rule_order',
		    data:		"fulfillment_type=" + fulfillment_type + "&region_name=" + region_name + "&item_classification=" + item_classification + "&item_classification2=" + item_classification2 + "&item_classification3=" + item_classification3 + "&item_classification4=" + item_classification4 + "&item_classification5=" + item_classification5,
		    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.source_order_list = data;
		});
};
	//function to get geo-chart information
		
		getRegionByState();
	
		function getRegionByState() {
			
			$http({
		    method:		'POST',
		    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_region_by_state',
						data : "",
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				region_data_list = data;
				//console.log("here" + region_data_list);

				var options = {
					packages : [ 'geomap' ],
					callback : drawMap
				};
				google.load('visualization', '1', options);

				google.setOnLoadCallback(drawMap);
			});

		}
		;

		function drawMap() {

			// setup the new map and its variables
			var data = new google.visualization.DataTable();
			data.addRows(region_data_list.length); // length gives us the number of results in our returned data
			data.addColumn('string', 'STATE');
			data.addColumn('number', 'No');
			data.addColumn('string', 'Region');

			$.each(region_data_list, function(i, v) {
				// set the values for both the name and the population
				data.setValue(i, 0, "US-" + v.STATE);
				data.setValue(i, 1, v.VALUE);
				//console.log(v.STATE);
				data.setValue(i, 2, v.REGION_NAME);
			});

			var container = document.getElementById('map_canvas');
			var geomap = new google.visualization.GeoMap(container);
			var options = {};
			options['dataMode'] = 'regions';
			options['region'] = 'US';
			options['backgroundColor'] = '#FFF';
			options['backgroundColor.fill'] = '#FFF';
			options['colors'] = [ 0xF8945F, 0x09D7F8, 0xFFF731, 0xF8F987,
					0xC2AC96, 0xF84B3B, 0x3A6EF8, 0xBF65FF, 0xFF7C99, 0x62F957,
					0x26C28C ];
			options['showLegend'] = false;
			geomap.draw(data, options);

			google.visualization.events.addListener(geomap, 'select',
					function() {
						var selection = geomap.getSelection()[0];
						var label = data.getValue(selection.row, 2);
						$('#region_drop').val(label).change();

						$('#region_drop').pulsate({
							color : "#A5D16C",
							repeat : false
						});
					});
		}
		;

	};
</script>
<script>
$(document).ready(function(){
	$('#fulfill_drop').val('ALL').change();
	$('#region_drop').val('ALL').change();
	$('#item1_drop').val('ALL').change();
	$('#item2_drop').val('ALL').change();
	$('#item3_drop').val('ALL').change();
	$('#item4_drop').val('ALL').change();
	$('#item5_drop').val('ALL').change();
	
});
</script>
<style>
.selected{
  background:#D3D3D3;
  color:black;
}


</style>



<div ng-app="SourcingDetailApp">
	<div ng-controller="SourcingRuleController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a>
				</li>
				<li><a>Sourcing Detail</a>
				</li>
				<li class="active">Sourcing Rule Details</li>
			</ol>
		</div>
		<div class="col-sm-5">
			<section class="panel">
				<!--  -->
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form" id="sourcingForm">

						<div class="form-group" data-ng-init="getFulfillments()">
							<label class="col-sm-5 control-label">Fulfillment Type</label>
							<div class="col-sm-6">
								<select name="{{fulfillment.FULFILLMENT_TYPE}}"
									class="form-control" ng-model="search.fulfillment_type" id="fulfill_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="fulfillment in fil_list"
										ng-value="{{fulfillment.FULFILLMENT_TYPE}}">{{fulfillment.FULFILLMENT_TYPE}}
									</option>
									<option ng-value=""></option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getRegions()">
							<label class="col-sm-5 control-label">Distribution Region</label>
							<div class="col-sm-6">
								<select name="{{region.REGION_NAME}}" class="form-control"
									ng-model="search.region_name" id="region_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="region in reg_list"
										ng-value="{{region.REGION_NAME}}">{{region.REGION_NAME}}
									</option>
									<option ng-value=""></option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getItemClass1()">
							<label class="col-sm-5 control-label">Item Type</label>
							<div class="col-sm-6">
								<select name="{{item.ITEM_CLASSIFICATION}}" class="form-control" id="item1_drop"
									ng-model="search.item_class_1">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="item in item_list_1"
										ng-value="{{item.ITEM_CLASSIFICATION}}">{{item.ITEM_CLASSIFICATION}}
									</option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getItemClass2()">
							<label class="col-sm-5 control-label">Extn Nomadic</label>
							<div class="col-sm-6">
								<select name="{{item.ITEM_CLASSIFICATION2}}"
									class="form-control" ng-model="search.item_class_2" id="item2_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="item in item_list_2"
										ng-value="{{item.ITEM_CLASSIFICATION2}}">{{item.ITEM_CLASSIFICATION2}}
									</option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getItemClass3()">
							<label class="col-sm-5 control-label">Extn Ship Node
								Source</label>
							<div class="col-sm-6">
								<select name="{{item.ITEM_CLASSIFICATION3}}"
									class="form-control" ng-model="search.item_class_3" id="item3_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="item in item_list_3"
										ng-value="{{item.ITEM_CLASSIFICATION3}}">{{item.ITEM_CLASSIFICATION3}}
									</option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getItemClass4()">
							<label class="col-sm-5 control-label">Web-Ex</label>
							<div class="col-sm-6">
								<select name="{{item.ITEM_CLASSIFICATION4}}"
									class="form-control" ng-model="search.item_class_4" id="item4_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="item in item_list_4"
										ng-value="{{item.ITEM_CLASSIFICATION4}}">{{item.ITEM_CLASSIFICATION4}}
									</option>
								</select>
							</div>
						</div>
						<div class="form-group" data-ng-init="getItemClass5()">
							<label class="col-sm-5 control-label">Web-Ex Ship</label>
							<div class="col-sm-6">
								<select name="{{item.ITEM_CLASSIFICATION5}}"
									class="form-control" ng-model="search.item_class_5" id="item5_drop">
									<option ng-value="ALL"> ALL </option>
									<option ng-repeat="item in item_list_5"
										ng-value="{{item.ITEM_CLASSIFICATION5}}">{{item.ITEM_CLASSIFICATION5}}
									</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button
									ng-disabled="!(!!search.fulfillment_type || !!search.region_name) || isLoading()  || (clicked == true)"
									class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit"
									ng-click="getSourceDetails(search)">{{button}}</button>								
							</div>
						</div>

					</form>
				</div>

			</section>


			<!-- Modal -->
			<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">
								<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
							</button>
							<h4 class="modal-title" id="myModalLabel"
								ng-bind-template="Details for Distribution: {{details_title}}"></h4>
						</div>
						<div class="modal-body">
							<h5 ng-repeat="detail in detail_list.detail_list" ng-show="$first">
								Active Date Range: <font color="red">{{detail.EFFECTIVE_START_DATE}}</font>
								through <font color="red">{{detail.EFFECTIVE_END_DATE}}</font>
							</h5>
							<table id="resultsTable"
								class="table table-hover table-condensed table-bordered"
								style="border: none;">
								<thead>
									<tr>
										<th style="border-top-left-radius: 5px; border: none;">Priority</th>
										<th style="border-top-right-radius: 5px; border: none;">Shipment
											Node</th>
									</tr>
								</thead>
								<tr style="font-size: 12px" ng-repeat="detail in detail_list.detail_list">
									<td>{{detail.PRIORITY}}</td>
									<td>{{detail.SHIPNODE_KEY}}</td>
								</tr>
							</table>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="col-sm-7" id='map_section'>
			<section class="panel">
				<div class="panel-body" style="overflow: hidden">
					<div id='map_canvas' class="google_map_vis"></div>

					<section class="panel">
						<div class="panel-body">
							<div class="swatchContainer">
								<table>
									<tr>
										<td><div class="swatch_square" id="reg1"
												style="background-color: #09D7F8"></div>
										</td>
										<!-- #09D7F8 -->
										<td>Midwest</td>
										<td><div class="swatch_square" id="reg2"
												style="background-color: #C2AC96"></div>
										</td>
										<!-- #C2AC96 -->
										<td>North East</td>
										<td><div class="swatch_square" id="reg3"
												style="background-color: #F84B3B"></div>
										</td>
										<!-- #F84B3B -->
										<td>North West</td>
										<td><div class="swatch_square" id="reg4"
												style="background-color: #FF7C99"></div>
										</td>
										<!-- #FF7C99 -->
										<td>South East</td>
										<td><div class="swatch_square" id="reg5"
												style="background-color: #62F957"></div>
										</td>
										<!-- #62F957 -->
										<td>South West</td>
									</tr>
									<tr>
										<td><div class="swatch_square" id="reg6"
												style="background-color: #F8945F"></div>
										</td>
										<!-- #F8945F -->
										<td>MidAtlantic</td>
										<td><div class="swatch_square" id="reg7"
												style="background-color: #F8F987"></div>
										</td>
										<!-- #F8F987 -->
										<td>New England</td>
										<td><div class="swatch_square" id="reg8"
												style="background-color: #FFF731"></div>
										</td>
										<!-- #FFF731 -->
										<td>Mountain</td>
										<td><div class="swatch_square" id="reg9"
												style="background-color: #3A6EF8"></div>
										</td>
										<!-- #3A6EF8 -->
										<td>Plains</td>
										<td><div class="swatch_square" id="reg10"
												style="background-color: #BF65FF"></div>
										</td>
										<!-- #BF65FF -->
										<td>South</td>
										<td><div class="swatch_square" id="reg11"
												style="background-color: #26C28C"></div>
										</td>
										<td>Wisconsin</td>
									</tr>
								</table>
							</div>
							</div>
					</section>
				</div>
			</section>
		</div>
		<div class="col-sm-7">
			<section class="panel">

				<table
					class="table table-hover table-condensed table-bordered table-responsive">
					<thead>
						<tr>
							<th>Fulfillment Type</th>
							<th>Region Name</th>
							<th>Item Class</th>
							<th>Item Class 2</th>
							<th>Item Class 3</th>
							<th>Item Class 4</th>
							<th>Item Class 5</th>
							

						</tr>
						<tr>
							<th><input class="filter" ng-model="fulfillment">
							</th>
							<th><input class="filter" ng-model="region" id="region_filt">
							</th>
							<th><input class="filter" ng-model="class1">
							</th>
							<th><input class="filter" ng-model="class2">
							</th>
							<th><input class="filter" ng-model="class3">
							</th>
							<th><input class="filter" ng-model="class4">
							</th>
							<th><input class="filter" ng-model="class5">
							</th>

						</tr>
					</thead>
					<tbody>
						<tr  ng-init="rowindex=$index" ng-class="rowindex==selectedRow?'selected':''"
							ng-click="getRuleOrder(result.FULFILLMENT_TYPE,result.REGION_NAME,result.ITEM_CLASSIFICATION,result.ITEM_CLASSIFICATION2,result.ITEM_CLASSIFICATION3,result.ITEM_CLASSIFICATION4,result.ITEM_CLASSIFICATION5);rowHighlighted(rowindex)"
							ng-repeat="result in source_details | filter:{FULFILLMENT_TYPE : fulfillment, REGION_NAME : region, ITEM_CLASSIFICATION : class1, ITEM_CLASSIFICATION2 : class2, ITEM_CLASSIFICATION3 : class3, ITEM_CLASSIFICATION4 : class4, ITEM_CLASSIFICATION5 : class5, DISTRIBUTION_RULE_ID : distRule, SEQ_NO : seq}">

							<td>{{result.FULFILLMENT_TYPE}}</td>
							<td>{{result.REGION_NAME}}</td>
							<td>{{result.ITEM_CLASSIFICATION}}</td>
							<td>{{result.ITEM_CLASSIFICATION2}}</td>
							<td>{{result.ITEM_CLASSIFICATION3}}</td>
							<td>{{result.ITEM_CLASSIFICATION4}}</td>
							<td>{{result.ITEM_CLASSIFICATION5}}</td>
							
						
							
						</tr>
					</tbody>
				</table>
				</section>
		</div>
		<div class="col-sm-5">
			<section class="panel">
				<table
					class="table table-hover table-condensed table-bordered table-responsive">
					<thead>
						<tr>
							<th>Distribution Rule ID</th>
							<th>Sequence Number</th>
						</tr>

					</thead>
					<tbody>
						<tr ng-repeat="result in source_order_list
							| orderBy:'SEQ_NO' | filter:{FULFILLMENT_TYPE : fulfillment, REGION_NAME :
							region, ITEM_CLASSIFICATION : class1, ITEM_CLASSIFICATION2 :
							class2, ITEM_CLASSIFICATION3 :class3, ITEM_CLASSIFICATION4 : class4, ITEM_CLASSIFICATION5 : class5}">
							<td>
								<div id="vb">
									<button
										ng-click="getRegionDetails(result.DISTRIBUTION_RULE_ID)"
										class="btn btn-default" data-toggle="modal"
										data-target="#myModal" id="viewButton"
										ng-model="dist_button_val"
										ng-value="{{result.DISTRIBUTION_RULE_ID}}">
										{{result.DISTRIBUTION_RULE_ID}}</button>
								</div></td>
							<td>{{result.SEQ_NO}}</td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>


	</div>

</div>