<script type="text/javascript">
	var	app = angular.module('CancelDashboardApp', []);
	
	app.filter('orderObjectBy', function() {
		  return function(items, field, reverse) {
		    var filtered = [];
		    angular.forEach(items, function(item) {
		      filtered.push(item);
		    });
		    filtered.sort(function (a, b) {
		    	return (a[field] < b[field]) ? 1 : ((a[field] > b[field]) ? -1 : 0);
		    });
		    if(reverse) filtered.reverse();
		    return filtered;
		  };
		});
	
 	function CancelStatisticsController($scope, $http, $log, $rootScope, $interval){
 		
 		
		$scope.loadGlanceChartData = function() {	
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/cancel_dashboard',
			    data: "chart=cancelglance",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.cancelGlanceResultsChart_LABEL = "Today";
				$scope.cancelGlanceResults_main = data.Today;
				$scope.cancelGlanceResults_Today = data.Today;
				$scope.cancelGlanceResults_prevDay = data.PreviousDay;
				//appliation model scoped variables for today and previous day quantity and dollar amounts
				var result = [];
				
				for(i in $scope.cancelGlanceResults_prevDay.dollar_amount_cancelled)
				    result.push([$scope.cancelGlanceResults_prevDay.dollar_amount_cancelled [i]]);
				$scope.prevDollarsResult = result;
				
				result = [];
				for(i in $scope.cancelGlanceResults_Today.dollar_amount_cancelled)
				    result.push([$scope.cancelGlanceResults_Today.dollar_amount_cancelled [i]]);
				$scope.dollarsResult = result;
				$scope.load_dollars_spark_line($scope.dollarsResult);
				
				result = [];
				for(i in $scope.cancelGlanceResults_prevDay.quantity_cancelled)
				    result.push([$scope.cancelGlanceResults_prevDay.quantity_cancelled [i]]);
				$scope.prevUnitsResult = result;
				
				result = [];
				for(i in $scope.cancelGlanceResults_Today.quantity_cancelled)
				    result.push([$scope.cancelGlanceResults_Today.quantity_cancelled [i]]);
				$scope.unitsResult = result;
				$scope.load_units_spark_line($scope.unitsResult);
			});
		};
		
		//load cancelled unit sparklines
		$scope.load_units_spark_line = function(result){
			try{
				$("[id='sparkline Units cancelled today']").sparkline(result, {
				    type: "bar",
				    tooltipFormat: '{{value}}',
				    tooltipValueLookups: {
				        'offset': {}
				    },
				});
				$("[id='sparkline Units cancelled yesterday']").sparkline(result, {
				    type: "bar",
				    tooltipFormat: '{{value}}',
				    tooltipValueLookups: {
				        'offset': {}
				    },
				});
			}catch(e){ console.log('fail');}
			
		};
		
		//load cancelled dollar amount sparklines
		$scope.load_dollars_spark_line = function(result){
			try{
				$("[id='sparkline Dollars cancelled today']").sparkline(result, {
				    type: "bar",
				    tooltipFormat: '{{value}}',
				    tooltipValueLookups: {
				        'offset': {}
				    },
				});
				$("[id='sparkline Dollars cancelled yesterday']").sparkline(result, {
				    type: "bar",
				    tooltipFormat: '{{value}}',
				    tooltipValueLookups: {
				        'offset': {}
				    },
				});
			}catch(e){ console.log('fail');}
			
		};
		
		$scope.changeCancelGlance = function(chart){
			
			switch (chart) {
			case 'Today':			$scope.cancelGlanceResults_main = $scope.cancelGlanceResults_Today;
									$scope.load_units_spark_line($scope.unitsResult);
									$scope.load_dollars_spark_line($scope.dollarsResult);
									break;
			case 'Previous Day':	$scope.cancelGlanceResults_main = $scope.cancelGlanceResults_prevDay;
									$scope.load_units_spark_line($scope.prevUnitsResult);
									$scope.load_dollars_spark_line($scope.prevDollarsResult);
									break;
			default:				$scope.cancelGlanceResults_main = $scope.cancelGlanceResults_Today;
									$scope.load_units_spark_line($scope.unitsResult);
									$scope.load_dollars_spark_line($scope.dollarsResult);
									break;
			}
			
		};
		
		$scope.Top10CancelledSKU = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/cancel_dashboard',
			    data: "chart=top_10_cancelled_SKU",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				
				$scope.Top10cancelledSKUsChart_LABEL = "Today";
				$scope.Top10cancelledSKUs_main = data.Today;
				$scope.Top10cancelledSKUs_today = data.Today;
				$scope.Top10cancelledSKUs_prevDay = data.PreviousDay;
			});
		};
		
		$scope.changeTop10CancelledSKU = function(chart){
			
			switch (chart) {
			case 'Today':			$scope.Top10cancelledSKUs_main = $scope.Top10cancelledSKUs_today;	break;
			case 'Previous Day':	$scope.Top10cancelledSKUs_main = $scope.Top10cancelledSKUs_prevDay;	break;
			default:			$scope.Top10cancelledSKUs_main = $scope.Top10cancelledSKUs_today;;	break;
			}
			
		};
				
		$scope.storeelf14DayCustCancels = function() {
			var x = 0;
			var ids =[];
			var getmax = 0;
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/cancel_dashboard',
			    data: "chart=14_day_cust_cancels",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$("#graph-area-cust-cancel").empty();
				$scope.StoreElf14dayCustCancelstats = data;	
				if (data.cust_cancel_data.length == 14)
				{
					while (x < 14){
						ids.unshift({
							name: data.cust_cancel_data[x].CANCEL_DATE.substring(0,4) + '-' + data.cust_cancel_data[x].CANCEL_DATE.substring(4,6) + '-' + data.cust_cancel_data[x].CANCEL_DATE.substring(6),
							UNIT_COUNT: data.cust_cancel_data[x].UNIT_COUNT,
							});
						if(parseInt(data.cust_cancel_data[x].UNIT_COUNT) > parseInt(getmax)){
							getmax = data.cust_cancel_data[x].UNIT_COUNT;
						}
						x++;
					}				
					ids.sort(function (a, b) {
						  if (a.name > b.name) {
						    return 1;
						  }
						  if (a.name < b.name) {
						    return -1;
						  }
						  // a must be equal to b
						  return 0;
						});
					getmax = Math.ceil(getmax/100)*100;
					area_chart_cust_cancel(ids,getmax);
				}
			});	
		}
		
		$scope.storeelf14DayCancelData = function() {	
			var x = 0;
			var ids =[];
			var getmax = 0;
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/cancel_dashboard',
			    data: "chart=14_day_cancel_stats",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$("#graph-area-cancel").empty();
				$("#graph-area-rsrc").empty();
				$("#store-graph-area-cancel").empty();
				$("#store-graph-area-rsrc").empty();		
				$scope.StoreElf14dayCancelstats = data;	
				while (x < 14){
					// *adding data elements to array for chart calculation. 
					// The 'unshift' places element BEFORE the previous. 
					// This way results are displayed in correct date order on the graph.
					//alert("test" + data);
					ids.unshift({
						name: data.cancel_data[x].ORDERDT,
						efc1: data.cancel_data[x].EFC1CANC,
						efc2: data.cancel_data[x].EFC2CANC,
						efc3: data.cancel_data[x].EFC3CANC,
						efc4: data.cancel_data[x].EFC4CANC,
						rdc: data.cancel_data[x].RDCCANC,
						});
					
					//retrieving largest cancel count from array
					
					if(parseInt(data.cancel_data[x].EFC1CANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].EFC1CANC;
					}
					if(parseInt(data.cancel_data[x].EFC2CANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].EFC2CANC;					
					}
					if(parseInt(data.cancel_data[x].EFC3CANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].EFC3CANC;					
					}
					if(parseInt(data.cancel_data[x].EFC4CANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].EFC4CANC;				
					}
					if(parseInt(data.cancel_data[x].RDCCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].RDCCANC;				
					}
					x++;
				}				
				//console.log(data.all.last_run_timestamp);	
				//creating new max height for y-scale

				getmax = Math.ceil(getmax/100)*100;
				area_chart_cancel(ids,getmax);
				
				
				ids = [];
				getrsMax=0;
				x = 0;
					while(x<14){
						ids.unshift({
						name: data.cancel_data[x].ORDERDT,
						efc1: data.cancel_data[x].EFC1RSRCD,
						efc2: data.cancel_data[x].EFC2RSRCD,
						efc3: data.cancel_data[x].EFC3RSRCD,
						efc4: data.cancel_data[x].EFC4RSRCD,
						rdc: data.cancel_data[x].RDCRSRCD,
						stores: data.cancel_data[x].STORERSRCD
						});
					
					//retrieving largest cancel count from array
					
					if(parseInt(data.cancel_data[x].EFC1RSRCD) > parseInt(getrsMax)){
						getrsMax = data.cancel_data[x].EFC1RSRCD;
					}
					if(parseInt(data.cancel_data[x].EFC2RSRCD) > parseInt(getrsMax)){
						getrsMax = data.cancel_data[x].EFC2RSRCD;
					}
					if(parseInt(data.cancel_data[x].EFC3RSRCD) > parseInt(getrsMax)){
						getrsMax = data.cancel_data[x].EFC3RSRCD;
					}
					if(parseInt(data.cancel_data[x].EFC4RSRCD) > parseInt(getrsMax)){
						getrsMax = data.cancel_data[x].EFC4RSRCD;
					}
					if(parseInt(data.cancel_data[x].RDCRSRCD) > parseInt(getrsMax)){
						getrsMax = data.cancel_data[x].RDCRSRCD;
					}
					x++;
				}
				//console.log(data.all.last_run_timestamp);				
				//creating new max height for y-scale
				getrsMax = Math.ceil(getrsMax/100)*100;
				area_chart_resrc(ids,getrsMax);
				
				
				ids = [];
				getmax = 0;
				x = 0;
				while (x < 14){
					
					// *adding data elements to array for chart calculation. 
					// The 'unshift' places element BEFORE the previous. 
					// This way results are displayed in correct date order on the graph.
					//alert("test" + data);
					ids.unshift({
						name: data.cancel_data[x].ORDERDT,
						cst: data.cancel_data[x].STORECSTCANC,
						est: data.cancel_data[x].STOREESTCANC,
						pst: data.cancel_data[x].STOREPSTCANC,
						mst: data.cancel_data[x].STOREMSTCANC,
						mdt: data.cancel_data[x].STOREMDTCANC,
						});
					
					//retrieving largest cancel count from array
					
					if(parseInt(data.cancel_data[x].STORECSTCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].STORECSTCANC;					
					}
					if(parseInt(data.cancel_data[x].STOREESTCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].STOREESTCANC;					
					}
					if(parseInt(data.cancel_data[x].STOREPSTCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].STOREPSTCANC;				
					}
					if(parseInt(data.cancel_data[x].STOREMSTCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].STOREMSTCANC;			
					}
					if(parseInt(data.cancel_data[x].STOREMDTCANC) > parseInt(getmax)){
						getmax = data.cancel_data[x].STOREMDTCANC;			
					}
					x++;
				}
				//creating new max height for y-scale
				getmax = Math.ceil(getmax/100)*100;
				store_area_chart_cancel(ids,getmax);
				
				
				ids = [];
				getrsMax=0;
				x = 0;
					while (x < 14){
						
						// *adding data elements to array for chart calculation. 
						// The 'unshift' places element BEFORE the previous. 
						// This way results are displayed in correct date order on the graph.
						//alert("test" + data);
						ids.unshift({
							name: data.cancel_data[x].ORDERDT,
							cst: data.cancel_data[x].STORECSTRSRCD,
							est: data.cancel_data[x].STOREESTRSRCD,
							pst: data.cancel_data[x].STOREPSTRSRCD,
							mst: data.cancel_data[x].STOREMSTRSRCD,
							mdt: data.cancel_data[x].STOREMDTRSRCD,
							});
						
						//retrieving largest cancel count from array
						
						if(parseInt(data.cancel_data[x].STORECSTRSRCD) > parseInt(getrsMax)){
							getrsMax = data.cancel_data[x].STORECSTRSRCD;
							
						}
						if(parseInt(data.cancel_data[x].STOREESTRSRCD) > parseInt(getrsMax)){
							getrsMax = data.cancel_data[x].STOREESTRSRCD;
							
						}
						if(parseInt(data.cancel_data[x].STOREESTRSRCD) > parseInt(getrsMax)){
							getrsMax = data.cancel_data[x].STOREESTRSRCD;
							
						}
						if(parseInt(data.cancel_data[x].STOREMSTRSRCD) > parseInt(getrsMax)){
							getrsMax = data.cancel_data[x].STOREMSTRSRCD;
							
						}
						if(parseInt(data.cancel_data[x].STOREMDTCANC) > parseInt(getrsMax)){
							getrsMax = data.cancel_data[x].STOREMDTRSRCD;
							
						}
						x++;
					}
				
				//console.log(data.all.last_run_timestamp);
				
				//creating new max height for y-scale
				getrsMax = Math.ceil(getrsMax/100)*100;
				store_area_chart_resrc(ids,getrsMax);
				

			});
			
		};
		
		$scope.storeelfAutoCancelData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/cancel_dashboard',
			    data: "chart=5_day_auto_cancel_stats",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.StoreElf5dayAutoCancelstats = data;
			});
		};
		
		$scope.refresh = function(){
			if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
				$scope.loadGlanceChartData();
				$scope.storeelfAutoCancelData();
				$scope.Top10CancelledSKU();
				$scope.storeelf14DayCancelData();
				$scope.storeelf14DayCustCancels();
				console.log('reloading dashboard data');
			}
		};	
		$scope.refresh();
		$interval($scope.refresh, 30000);
		
	} 	
</script>

<div ng-app="CancelDashboardApp">
	<div ng-controller="CancelStatisticsController">			
		<div>
		<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [LEFT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
			<div class="col-md-7">
				<section class="panel">
				
				<table class="table table-hover table-striped table-condensed" description="at a glance" data-ng-init="loadGlanceChartData()">
					<thead>
						<tr>
							<th colspan="2"><font size="4px">At a glance</font><span class="rev-combo pull-right" ng-bind-template="(Last Updated: {{cancelGlanceResults_main.last_run_timestamp}})"></span></th>
							<th colspan="6">
								<div class="btn-group">
								  <button class="btn btn-sval btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
								       {{cancelGlanceResultsChart_LABEL}}<span class="caret"></span>
								  </button>
								  <ul class="dropdown-menu">
                                    <li><a ng-click="cancelGlanceResultsChart_LABEL='Today';changeCancelGlance('Today')">Today</a></li>
                                    <li><a ng-click="cancelGlanceResultsChart_LABEL='Previous Day';changeCancelGlance('Previous Day')">Previous Day</a></li>
								  </ul>
								</div>
							</th>
						</tr>
					</thead>
					<tbody>						
						<tr ng-repeat="glance in cancelGlanceResults_main.fulfillment_data">
							<td>{{glance.DESCRIPTION}}: </td>
							<td>{{glance.VALUE}}</td>
							<td></td>
						</tr>
						<tr ng-click="toggle_cncl_qty_data = !toggle_cncl_qty_data">
							<td><i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_cncl_qty_data==true]"> Units cancelled: </i></td>
							<td>{{cancelGlanceResults_main.cncl_qty_data[0].VALUE}}</td>
							<td><span id="sparkline {{cancelGlanceResults_main.cncl_qty_data[0].DESCRIPTION}}">&nbsp;</span></td>
						</tr>
						<tr ng-show="toggle_cncl_qty_data" ng-animate="'box'" ng-repeat="glance in cancelGlanceResults_main.quantity_cancelled_breakdown">
							<td><div style="padding-left: 15px">{{glance.CHANNEL}}:</div></td>
							<td>{{glance.TOTAL_COUNT}}</td>
							<td></td>
						</tr>
						<tr ng-click="toggle_cncl_amt_data = !toggle_cncl_amt_data">
							<td><i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_cncl_amt_data==true]"> Dollars cancelled: </i></td>
							<td>{{cancelGlanceResults_main.cncl_amt_data[0].VALUE}}</td>
							<td><span id="sparkline {{cancelGlanceResults_main.cncl_amt_data[0].DESCRIPTION}}">&nbsp;</span></td>
						</tr>
						<tr ng-show="toggle_cncl_amt_data" ng-animate="'box'" ng-repeat="glance in cancelGlanceResults_main.dollar_amount_cancelled_breakdown">
							<td><div style="padding-left: 15px">{{glance.CHANNEL}}:</div></td>
							<td>{{glance.DOLLARS_LOST}}</td>
							<td></td>
						</tr>
					</tbody>
				</table>
				</section>
				
				<!-- XXXXXXXXXXXXXXXXXXXXX Auto cancel section XXXXXXXXXXXXXXXXXXXXXXXXXX -->
				<section class="panel">
				
				<table class="table table-hover table-striped table-condensed" description="auto cancel glance" data-ng-init="storeelfAutoCancelData()">
					<thead>
						<tr>
							<th colspan="9"><font size="4px">OMS Auto cancel stats</font><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf5dayAutoCancelstats.last_run_ts}})"></span></th>
							
						</tr>
						<tr>
						<th><font size="1px">Cancelled&nbsp;Date</font></th>
						<th><font size="1px">APO/FPO 
											 Cancel</font></th>
						<th><font size="1px">Cancel at
											 Print</font></th>
						<th><font size="1px">DS 
											 Cancel</font></th>
						<th><font size="1px">Gift-wrap
											 Cancel</font></th>											   
						<th><font size="1px">Priority Store
											 Cancel</font></th>
						<th><font size="1px">Priority DSV
											 Cancel</font></th>
						<th><font size="1px">Priority RDC
											 Cancel</font></th>
						<th><font size="1px">RTAM/Genuine
											 Cancel</font></th>
						
					</tr>  
					</thead>
					<tbody>						
						<tr ng-repeat="autoCancelStats in StoreElf5dayAutoCancelstats.auto_cancel_data | orderObjectBy:'CANCEL_DATE'">
							<td>{{autoCancelStats.CANCEL_DATE}}</td>
							<td>{{autoCancelStats.APO_CNCL}}</td>
							<td>{{autoCancelStats.PRINT_CNCL}}</td>
							<td>{{autoCancelStats.DS_CNCL}}</td>
							<td>{{autoCancelStats.GIFT_CNCL}}</td>
							<td>{{autoCancelStats.PRIORITY_STR}}</td>
							<td>{{autoCancelStats.PRIORITY_DSV}}</td>
							<td>{{autoCancelStats.PRIORITY_RDC}}</td>
							<td>{{autoCancelStats.RTAM_CNCL}}</td>
					</tr>
					</tbody>
				</table>
				</section>
				
				</div>

	<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [RIGHT COL] XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->			
			<div class="col-md-5">
			<section class="panel">
				<table class="table table-hover table-striped table-condensed" data-ng-init="Top10CancelledSKU();changeTop10CancelledSKU('Today')">
					<thead>
						<tr>
							<th colspan="3"><font size="3px">Top 10 cancelled</font><span class="tools pull-right" ng-bind-template="(Last Updated: {{Top10cancelledSKUs_main.last_run_timestamp}})"></span></th>
							<th colspan="1">
								<div class="btn-group">
								  <button class="btn btn-sval btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
								      {{Top10cancelledSKUsChart_LABEL}}<span class="caret"></span>
								  </button>
								  <ul class="dropdown-menu">
                                    <li><a ng-click="Top10cancelledSKUsChart_LABEL='Today';changeTop10CancelledSKU('Today')">Today</a></li>
                                    <li><a ng-click="Top10cancelledSKUsChart_LABEL='Previous Day';changeTop10CancelledSKU('Previous Day')">Previous Day</a></li>
								  </ul>
								</div>
							</th>
						</tr>
						<tr>
							<th><font size="2.5px">Count</font></th>
							<th><font size="2.5px">ItemID</font></th>
							<th><font size="2.5px">Item Description</font></th>
							<th><font size="2.5px">Cancelled Dollar</font></th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="cancelledSKU in Top10cancelledSKUs_main.fulfillment_data | orderBy: ['-CANCELQTY', '-ITEMID'] ">
							<td>{{cancelledSKU.CANCELQTY}}</td>
							<td>{{cancelledSKU.ITEMID}}</td>
							<td>{{cancelledSKU.ITEM_DESC}}</td>
							<td>&#36;{{cancelledSKU.CANCDELLEDDOLLAR}}</td>
						</tr>						
					</tbody>
				</table>
				</section>
				
				
			</div>
			
			
			<div class="col-md-14">
				<section class="panel">
					<!-- 14 Day Cancellation graphs -->
					<!-- Cancel graph-->
					<div class="col-md-12">
					<section class="panel" id="chartSection">
				 	<header class="panel-heading" id="chart_title">
	                                  <font size="2px"><b>Customer Cancels 14 day - units</b><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf14dayCustCancelstats.last_run_timestamp}})"></span></font>
	                                  <div id="custCancelLegend"></div>
	                                 </header>
	                               
					<div class="panel-body">
	                	<div id="graph-area-cust-cancel" class="graph" style="height: 300px;"></div>
	                </div>
				</section>
					</div>
			
				</section>
			</div>
			
			<div class="col-md-14">
				<section class="panel">
					<!-- 14 Day Cancellation graphs -->
					<!-- Cancel graph-->
					<div class="col-md-6">
					<section class="panel" id="chartSection">
					 <header class="panel-heading" id="chart_title">
		                                  <font size="2px"><b>FC 14 day - units resourced from</b><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf14dayCancelstats.last_run_ts}})"></span></font>
		                                  <div id="legend"></div>
		                                 </header>
		                               
						<div class="panel-body">
						<!--  set height to match data div -->
		                	<div id="graph-area-cancel" class="graph" style="height: 300px;"></div>
		                </div>
					</section>
					</div>
					
					<!-- Resource graph -->
					<div class="col-md-6">
					<section class="panel" id="chartSection">
					 <header class="panel-heading" id="chart_title">
		                                  <font size="2px"><b>FC 14 day - units resourced to</b><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf14dayCancelstats.last_run_ts}})"></span></font>
		                                  <div id="legendrsrc"></div>
		            </header>
		                               
						<div class="panel-body">
						<!--  set height to match data div -->
		                	<div id="graph-area-rsrc" class="graph" style="height: 300px;"></div>
		                </div>
					</section>
					</div>
			
				</section>
			</div>
			
			<!-- Store Cancel graph-->
			<div class="col-md-6">
			<section class="panel" id="chartSection">
			 <header class="panel-heading" id="chart_title">
                                  <font size="2px"><b>Store 14 day - units resourced from</b><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf14dayCancelstats.last_run_ts}})"></span></font>
                                  <div id="storelegend"></div>
                                 </header>
                               
				<div class="panel-body">
				<!--  set height to match data div -->
                	<div id="store-graph-area-cancel" class="graph" style="height: 300px;"></div>
                </div>
			</section>
			</div>
			
			<!-- Store Resource graph -->
			<div class="col-md-6">
			<section class="panel" id="chartSection">
			 <header class="panel-heading" id="chart_title">
                                  <font size="2px"><b>Store 14 day - units resourced to</b><span class="tools pull-right" ng-bind-template="(Last Updated: {{StoreElf14dayCancelstats.last_run_ts}})"></span></font>
                                  <div id="storelegendrsrc"></div>
            </header>
                               
				<div class="panel-body">
				<!--  set height to match data div -->
                	<div id="store-graph-area-rsrc" class="graph" style="height: 300px;"></div>
                </div>
			</section>
			</div>	
		</div>
	</div>
</div>


<script src="<%=request.getContextPath()%>/public/v3/js/morris.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/raphael-min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/chart_generator.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/public/v3/js/jquery.animateNumber.js" type="text/javascript"></script>
<link href="<%=request.getContextPath()%>/public/v3/css/morris.css" rel="stylesheet" />