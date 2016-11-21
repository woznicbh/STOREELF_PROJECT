<script type="text/javascript">
	var	app = angular.module('DashboardApp', []); 
	var first_hit = 0;

	app.filter('removehtml', function(){
		return function(input){
			return input.replace('<b>', '').replace('</b>', '');
		};
	});
	
	/**
		Backordered
		scheduled
		created
		released
	**/
 	function StatisticsController2($scope, $http, $log, $rootScope, $interval, $window, $document, $filter){
		$scope.graph_width = 60;
 		$scope.Total7dayFinancialModal								= []; 		
 		
 		$scope.sevenDayFinancial_days						=	[];	$scope.sevenDayFinancial_timestamp					= ''; 
 		 		
 		$scope.chart2 = null;
 		
 		$document.ready(function(){ 
 			try{				
				//set modal width
				$('.modal-dialog').css('width',($(window).width()/100)*$scope.graph_width);				
 				
				//this fixes the black screen of death bug, DO NOT REMOVE
				$('*').on('click', function (){	$('.modal-backdrop').attr('onclick',"$('.modal-backdrop').remove();");	});				
				
	 			$('#7_Day_Financial_Modal').on('shown.bs.modal', function (){				
					$('svg').attr('width', $('.modal-dialog').width());
					//$('svg').attr('height', $('.modal-body2').height());
					try{$window._7_Day_Financial_Modal_barchart.redraw();}catch(err){console.log('_7_Day_Financial_Modal_barchart redraw failed.')}
				});	 			
	 			$('#7_Day_Financial_Modal_date_breakdown').on('shown.bs.modal', function (){
					$('svg').attr('width', $('.modal-dialog').width());	
					//$('svg').attr('height', $('.modal-body2').height());
					try{$window._7_Day_Financial_Modal_barchart_date_breakdown.redraw();}catch(err){console.log('_7_Day_Financial_Modal_barchart_date_breakdown redraw failed.')}
				});
	 			
	 			$('#myModal').on('shown.bs.modal', function () {	
					$('svg').attr('width', $('.modal-dialog').width());	
					$('svg').attr('height', $('.modal-body2').height());
					$window.bar2.redraw();	
				});
 			}catch(err) {
				console.log('shown.bs.modal event handlerFAILED!!');
			    console.log(err);
			}
 			//--------------------------------------------------------------------------------------
 			
 			try{
 				$('#7_Day_Financial_Modal').on('hidden.bs.modal', function (){					$('#7_day_modal_bar_chart').empty();	}); 				
 				$('#7_Day_Financial_Modal_date_breakdown').on('hidden.bs.modal', function (){	$('#7_day_modal_bar_chart_fulfillment_by_date').empty();});
				$('#myModal').on('hidden.bs.modal', function (){	$('#node-graph-area').empty();});
 			}catch(err) {
				console.log('hidden.bs.modal event handler FAILED!!');
			    console.log(err);
			}
 			
 			try{
	 	 		$(document).on("click", "#EFC1", function(){$("#node-graph-area").empty();	$("#modal_title").text("EFC 1 Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_EFC1);	$window.bar2.redraw();});
	 	 		$(document).on("click", "#EFC2", function(){$("#node-graph-area").empty();	$("#modal_title").text("EFC 2 Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_EFC2);	$window.bar2.redraw();});
	 	 		$(document).on("click", "#EFC3", function(){$("#node-graph-area").empty();	$("#modal_title").text("EFC 3 Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_EFC3);	$window.bar2.redraw();});
	 	 		$(document).on("click", "#EFC4", function(){$("#node-graph-area").empty();	$("#modal_title").text("EFC 4 Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_EFC4);	$window.bar2.redraw();});
	 	 		$(document).on("click", "#STORE", function(){$("#node-graph-area").empty();	$("#modal_title").text("STORE Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_STORE);	$window.bar2.redraw();});
	 	 		$(document).on("click", "#RDC", function(){$("#node-graph-area").empty();	$("#modal_title").text("RDC Pending Fulfillment Breakdown");	$scope.small_bar_chart($scope.pending_data_RDC);	$window.bar2.redraw();});
 			}catch(err) {
				console.log('FP click handler FAILED!!');
			    console.log(err);
			}
 			
 	 		 $(document).on("click", "#BOPUS", function(){
 	 			$("#node-graph-area").empty();
 	 			$("#modal_title").text("BOPUS Pending Fulfillment Breakdown");
 				small_bar_chart(pending_data_BOPUS);
 				$window.bar2.redraw();
 	 		});

 	 		//redrawing the graph in the modal. Will not show unless this is done.
 	 		//$('#myModal').on('shown.bs.modal', function () {		$window.bar2.redraw();	});
 			
 		});

		$scope.loadGlanceChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=glance",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				try{
					$scope.glanceResults = data.glance_data;
					$scope.glanceResults_last_run_ts = data.glance_last_run_timestamp;
					$scope.order_counts = data.total_chart_array;

					//convert strings to floats/numbers
					var oc_number = Number(data.glance_data[0].VALUE.replace(/[^0-9\.]+/g,""));		
					var uc_number = Number(data.glance_data[1].VALUE.replace(/[^0-9\.]+/g,""));
					/* var ds_number = parseFloat(data.glance_data[4].VALUE.replace(/[^0-9\.]+/g,""));
					var ss_number = parseFloat(data.glance_data[5].VALUE.replace(/[^0-9\.]+/g,"")); */
					var ds_number = parseFloat(data.glance_data[3].VALUE.replace(/[^0-9\.]+/g,""));	
					var es_number = parseFloat(data.glance_data[4].VALUE.replace(/[^0-9\.]+/g,""));
					var ss_number = parseFloat(data.glance_data[5].VALUE.replace(/[^0-9\.]+/g,""));

					//animations for numbers
					var options= ({	format : 'currency'	});

					var options2= ({
						duration : 1000,
						intStepDecimals: 0,
						intEndDecimals: 0,
						floatStepDecimals: 0,
						floatEndDecimals: 0,
						format: "currency",
						currencyIndicator: "",
						currencyGroupSeparator: (1000).toLocaleString().charAt(1),
						currencyDecimalSeparator: (1.5).toLocaleString().charAt(1),
					  });

					$('#oc').animateNumber(oc_number, options2);	$('#uc').animateNumber(uc_number, options2);
					$('#ds').animateNumber(ds_number, options);		$('#es').animateNumber(es_number, options);
					$('#ss').animateNumber(ss_number, options);
				}catch(err) {
					console.log('inventory_snapshot REQUEST FAILED!!');
				    console.log(err);
				}
			});
		};
		
		$scope.loadAllNodeIventorySnapshotChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=inventory_snapshot",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				try{
					$scope.EFCIventorySnapshotResults = data.EFC;
					$scope.EFCIventorySnapshotResults_last_run_timestamp = data.inventory_snapshot_last_run_timestamp;

					$scope.RDCIventorySnapshotResults = data.RDC;
					$scope.RDCTotalIventorySnapshotResults = data.RDC_TOTALS;

					$scope.RDCSnapshot__TOTAL_COUNT 	= 0;
					$scope.RDCSnapshot__TOTAL_INVTOTAL	= 0;

					angular.forEach(data.RDC, function(value, key){
						$scope.RDCSnapshot__TOTAL_COUNT		+= parseInt(value.COUNT);
						$scope.RDCSnapshot__TOTAL_INVTOTAL	+= parseInt(value.INVTOTAL); //TODO fix
					});
				}catch(err) {
					console.log('inventory_snapshot REQUEST FAILED!!');
				    console.log(err);
				}
			});
		};
		
		$scope.load7dayFinancialChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=7_day_financial&csv=false",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.sevenDayFinancial = data;
				try{
					$scope.buildTotal7dayFinancialData();
				}catch(err) {
					console.log('load7dayFinancialChartData REQUEST FAILED!!');
				    console.log(err);
				}
			});
		};
		
		$scope.csv7dayFinancialChartData = function() {
			
			window.location.href= STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals?chart=7_day_financial&csv=true';
		};
			
		
		$scope.showTotal7dayFinancialModalChart =  function(chartName, stage, type){
			var data = null;
			var filter = '';
			try{
				$('#'+chartName).empty();
				
				if(stage=='Sourcing'){
					filter = 'byStatus';
					data = [
						{channel: 'Created'				,Units: $scope.Total7dayFinancialModal['Sourcing']['units']['Created'		]	,Dollars: $scope.Total7dayFinancialModal['Sourcing']['dollars']['Created'		],	details: 0},
						{channel: 'Backordered'			,Units: $scope.Total7dayFinancialModal['Sourcing']['units']['Backordered'	]	,Dollars: $scope.Total7dayFinancialModal['Sourcing']['dollars']['Backordered'	],	details: 1},
						{channel: 'Scheduled'			,Units: $scope.Total7dayFinancialModal['Sourcing']['units']['Scheduled'		]	,Dollars: $scope.Total7dayFinancialModal['Sourcing']['dollars']['Scheduled'		],	details: 2},
						{channel: 'Released'			,Units: $scope.Total7dayFinancialModal['Sourcing']['units']['Released'		]	,Dollars: $scope.Total7dayFinancialModal['Sourcing']['dollars']['Released'		],	details: 3}
					]; 
				}else if(stage=='Fulfillment'){
					filter = 'byStatus';
					data = [
							{channel: 'WMoS'		,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['WMoS'		]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['WMoS'		],	details: 0},
							{channel: 'DSV'			,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['DSV'		]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['DSV'		],	details: 1},
							{channel: 'SFS'			,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['SFS'		]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['SFS'		],	details: 2},
							{channel: 'BOPUS'		,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['BOPUS'		]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['BOPUS'		],	details: 3},
							{channel: 'LFCs'		,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['LFCs'		]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['LFCs'		],	details: 4},
							{channel: 'Marketplace'	,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['Marketplace']	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['Marketplace'],	details: 5},
							{channel: 'Same Day'	,Units: $scope.Total7dayFinancialModal['Fulfillment']['units']['Same Day'	]	,Dollars: $scope.Total7dayFinancialModal['Fulfillment']['dollars']['Same Day'	],	details: 6}
						];  
				}else if(stage=='Shipped'){
					filter = 'byShipNodeKey';
					data = [
							{channel: 'EFC1'		,Units: $scope.Total7dayFinancialModal['Shipped']['units']['EFC1'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['EFC1'	],	details: 0},
							{channel: 'EFC2'		,Units: $scope.Total7dayFinancialModal['Shipped']['units']['EFC2'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['EFC2'	],	details: 1},
							{channel: 'EFC3'		,Units: $scope.Total7dayFinancialModal['Shipped']['units']['EFC3'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['EFC3'	],	details: 2},
							{channel: 'EFC4'		,Units: $scope.Total7dayFinancialModal['Shipped']['units']['EFC4'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['EFC4'	],	details: 3},
							{channel: 'LFC'			,Units: $scope.Total7dayFinancialModal['Shipped']['units']['LFC'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['LFC'	],	details: 4},
							{channel: 'RDC'			,Units: $scope.Total7dayFinancialModal['Shipped']['units']['RDC'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['RDC'	],	details: 5},
							{channel: 'STORE'		,Units: $scope.Total7dayFinancialModal['Shipped']['units']['STORE'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['STORE'	],	details: 6},
							{channel: 'DSV'			,Units: $scope.Total7dayFinancialModal['Shipped']['units']['DSV'	]	,Dollars: $scope.Total7dayFinancialModal['Shipped']['dollars']['DSV'	],	details: 7}
				        ];
				}else if(stage=='Invoiced'){
					filter = 'byShipNodeKey';
					data = [
							{channel: 'EFC1'	,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['EFC1'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['EFC1'	],	details: 0},
							{channel: 'EFC2'	,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['EFC2'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['EFC2'	],	details: 1},
							{channel: 'EFC3'	,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['EFC3'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['EFC3'	],	details: 2},
							{channel: 'EFC4'	,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['EFC4'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['EFC4'	],	details: 3},
							{channel: 'LFC'		,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['LFC'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['LFC'	],	details: 4},
							{channel: 'RDC'		,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['RDC'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['RDC'	],	details: 5},
							{channel: 'STORE'	,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['STORE'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['STORE'	],	details: 6},
							{channel: 'DSV'		,Units: $scope.Total7dayFinancialModal['Invoiced']['units']['DSV'	]	,Dollars: $scope.Total7dayFinancialModal['Invoiced']['dollars']['DSV'	],	details: 7}
					   ];
				}
				console.log("showTotal7dayFinancialModalChart data:");
				console.log(data);
				$scope[chartName](filter, data, stage, type);
			}catch(err) {
				console.log('showTotal7dayFinancialModalChart FAILED!!');
			    console.log(err);
			}
		};		


		$scope.bar_chart_7_day = function(filter, data, stage, type){
			try{
				var label_index		= 0;
				var bar_hex_color	= (type=='Dollars') ? '#4da74d':'#4a8bc2';
				var filteredArray	= null;
				var filterObject	= null;
				
				$('#7_Day_Financial_Modal #modal_title').text(stage+' Financial Breakdown - '+type);				
				console.log(type+' - bar_chart_filter:');				
				filterObject = function(x,i,a){
					console.log(x);
					console.log(typeof x.Dollars === 'undefined');
					console.log(typeof x.Units === 'undefined');
					if(type=='Dollars'){ return !(typeof x.Dollars === 'undefined' || x.Dollars < 1)};
					if(type=='Units'){  return !(typeof x.Units === 'undefined' || x.Units < 1)};
				};
				
				filteredArray = $filter('filter')(data, filterObject, false);
				
				console.log('bar_chart_7_day');
				console.log(filteredArray);	
				$window._7_Day_Financial_Modal_barchart = Morris.Bar({
					  element: '7_day_modal_bar_chart',
					  data: filteredArray,
					  xkey: 'channel',
					  ykeys: [type],
					  hideHover: false,
			        labels: [type],
			        yLabelFormat: function(y){if(typeof y == 'undefined'){ y=0;} return (type=='Dollars') ? $filter('currency')(y, '$', 2) : $filter('number')(y,0);},
			        barColors: [bar_hex_color],
			        xLabelMargin: 3, xLabelAngle: 30, 
			        parseTime: false,smooth: true,resize: true,redraw: true,resize: true
				});
				
				//on-click clear the existing modal and display the date breakdown
				$window._7_Day_Financial_Modal_barchart.on('click', function(i, row){ 
					//$('#7_Day_Financial_Modal_date_breakdown').modal('toggle');
					console.log("--------------");
					console.log("user clicked modal---> " + i);
					console.log(row);
					
					$('#7_Day_Financial_Modal_date_breakdown #modal_title').text(stage+' Financial Breakdown - '+row.channel+' by date');
					
					$('#7_day_modal_bar_chart_by_date').empty();
					 
					//setup chart for the given status channel
					$scope.bar_chart_7_Day_Financial_Modal_date_breakdown(filter, stage, 'SUM('+type.toUpperCase()+')', type, bar_hex_color, row);
					console.log("--------------");
				});			
				$window._7_Day_Financial_Modal_barchart.redraw();
			}catch(err) {
				console.log('bar_chart_7_day FAILED!!');
			    console.log(err);
			}
		};
		
		
		$scope.buildModelFor7_Day_Financial_Modal_date_breakdown = function(stage, obj, filter){
			try{
				if($scope.sevenDayFinancial_days[filter][stage] != null){
					if($scope.sevenDayFinancial_days[filter][stage].data == null)	$scope.sevenDayFinancial_days[filter][stage].data = [];
					
					var exists			= false;
					var filterCondition = null;						
					
					for(var key in $scope.sevenDayFinancial_days[filter][stage].data){
						filterCondition = (
								(
									filter=='byStatus' 
										&& 
									($scope.sevenDayFinancial_days[filter][stage].data[key]['ORD_DATE'] == obj['ORD_DATE'] && $scope.sevenDayFinancial_days[filter][stage].data[key]['STATUS'] == obj['STATUS'])
								)
							);
						
						if(filterCondition){
							$scope.sevenDayFinancial_days[filter][stage].data[key]['SUM(UNITS)']	+=	obj['SUM(UNITS)'];
							$scope.sevenDayFinancial_days[filter][stage].data[key]['SUM(DOLLARS)']	+=	obj['SUM(DOLLARS)'];
							exists = true;
						}
					}
				
					
					//add item to array
					if(exists==false)	$scope.sevenDayFinancial_days[filter][stage].data.push({ STATUS: obj['STATUS'], SHIPNODEKEY: obj['SHIPNODEKEY'], ORD_DATE: obj['ORD_DATE'], 'SUM(UNITS)': obj['SUM(UNITS)'], 'SUM(DOLLARS)': obj['SUM(DOLLARS)']});	
				}else{
					//create new object for status
					$scope.sevenDayFinancial_days[filter][stage] = {STATUS: obj['STATUS'], data:[{ STATUS: obj['STATUS'], SHIPNODEKEY: obj['SHIPNODEKEY'], ORD_DATE: obj['ORD_DATE'], 'SUM(UNITS)': obj['SUM(UNITS)'], 'SUM(DOLLARS)': obj['SUM(DOLLARS)']}]};
				}
				
			}catch(err) {
				console.log('buildModelFor7_Day_Financial_Modal_date_breakdown FAILED!!');
			    console.log(err);
			}
		};
		
		
		//$scope.bar_chart_7_Day_Financial_Modal_date_breakdown = function(status, dollarsOrUnits, label, barColor, stage){
		$scope.bar_chart_7_Day_Financial_Modal_date_breakdown = function(filter, stage, dollarsOrUnits, label, barColor,  rowObject){
			var chart_data = null;
			var filteredArray = null;
			var filterObject = null;
			//status = stage +'_'+ status;
			try{
				$('#7_Day_Financial_Modal_date_breakdown').modal("show");
				
				//empty the previous graph				
				$('#7_day_modal_bar_chart_by_date').empty();
					
				console.log('bar_chart_7_Day_Financial_Modal_date_breakdown::rowObject:'+rowObject);
												 
				angular.forEach($scope.sevenDayFinancial_days[filter][stage].data, function(value, key) {var date = value.ORD_DATE;  value.ORD_DATE = new Date(date) });				
				console.log($scope.sevenDayFinancial_days[filter][stage].data);
				
				if(typeof rowObject !== 'undefined'){
					console.log('bar_chart_7_Day_Financial_Modal_date_breakdown - '+filter +'_'+ stage +'_'+ rowObject.channel);
					console.log(rowObject);
					
					if(filter == 'byStatus')		filterObject = function(x,i,a){  return (x.STATUS == rowObject.channel)};					
					if(filter == 'byShipNodeKey')	filterObject = function(x,i,a){  return (x.SHIPNODEKEY == rowObject.channel)};
					
					filteredArray = $filter('filter')($scope.sevenDayFinancial_days[filter][stage].data, filterObject, false);
					filteredArray = $filter('orderBy')(filteredArray, 'ORD_DATE', false);
				}else{
					filteredArray = $filter('orderBy')($scope.sevenDayFinancial_days[filter][stage].data, 'ORD_DATE', false); 
				}
				 
				 angular.forEach(filteredArray, function(value, key) { value.ORD_DATE = $filter('date')(value.ORD_DATE, 'MMM dd yyyy'); });				 
				 chart_data = filteredArray;
				 
				 console.log('filteredArray:');
				 console.log(filteredArray);
				
				$window._7_Day_Financial_Modal_barchart_date_breakdown = Morris.Bar({
					  element: '7_day_modal_bar_chart_by_date',
					  data: chart_data,
					  xkey: 'ORD_DATE',
					  // -- NOTE: dollarsOrUnits should equal SUM(DOLLARS) or SUM(UNITS)
					  ykeys: [dollarsOrUnits],
					  hideHover: false,
					  //-- NOTE: label should equal Dollars or Units
			        labels: [label],
			        yLabelFormat: function(y){ 
			        	if(label == 'Dollars'){
			        		return $filter('currency')(y, '$', 2);
			        	}else{
							return $filter('number')(y,0);
			        	}
			        },
			        barColors: [barColor],
			        xLabelMargin: 3, xLabelAngle: 30,
			        parseTime: false,smooth: true,resize: true,redraw: true,resize: true
				});
				$window._7_Day_Financial_Modal_barchart_date_breakdown.redraw();
			}catch(err) {
				console.log('bar_chart_7_Day_Financial_Modal_date_breakdown FAILED!!');
			    console.log(err);
			}
			
		};
		
		$scope.buildTotal7dayFinancialData = function() {
			var total_sourced_dollars	= 0; var total_fulfillment_dollars	= 0; var total_shipped_dollars	= 0; var total_invoiced_dollars	= 0; var total_cancelled_dollars	= 0;			
			var total_sourced_units		= 0; var total_fulfillment_units	= 0; var total_shipped_units	= 0; var total_invoiced_units	= 0; var total_cancelled_units		= 0;
			
			$scope.Total7dayFinancialModal								= [[[]]];			 
			$scope.sevenDayFinancial_days								= [];
			
	 		$scope.Total7dayFinancialModal								= [];
			$scope.Total7dayFinancialModal['Sourcing']					= [];
			$scope.Total7dayFinancialModal['Sourcing']['units']			= [];
			$scope.Total7dayFinancialModal['Sourcing']['dollars']		= [];
				
			$scope.Total7dayFinancialModal['Fulfillment']				= [];
			$scope.Total7dayFinancialModal['Fulfillment']['units']		= [];
			$scope.Total7dayFinancialModal['Fulfillment']['dollars']	= [];
				
			$scope.Total7dayFinancialModal['Shipped']					= [];
			$scope.Total7dayFinancialModal['Shipped']['units']			= [];
			$scope.Total7dayFinancialModal['Shipped']['dollars']		= [];
				
			$scope.Total7dayFinancialModal['Invoiced']					= [];
			$scope.Total7dayFinancialModal['Invoiced']['units']			= [];
			$scope.Total7dayFinancialModal['Invoiced']['dollars']		= [];
				
			$scope.sevenDayFinancial_days['byStatus']					= [];
			$scope.sevenDayFinancial_days['byStatus']['Fulfillment']	= [];
			$scope.sevenDayFinancial_days['byShipNodeKey']				= [];
			$scope.sevenDayFinancial_days['byShipNodeKey']['Shipped']	= [];
			$scope.sevenDayFinancial_days['byShipNodeKey']['Invoiced']	= [];
			
			try{
				var isFirstElement = true;
				var obj = null;
				 
				for (var key in $scope.sevenDayFinancial) {
					 obj = $scope.sevenDayFinancial[key];					 
					 
					 if(isFirstElement){
						 $scope.sevenDayFinancial_timestamp = obj.timestamp;
						 isFirstElement = false;
					 }else{ 
						 //var date = obj['ORD_DATE'];
						 //obj['ORD_DATE'] = new Date(date);
						 
						//build modal
						//$scope.buildModelFor7_Day_Financial_Modal_date_breakdown(obj.STATUS, obj, 'byStatus');
						//$scope.buildModelFor7_Day_Financial_Modal_date_breakdown(obj.SHIPNODEKEY, obj, 'byShipNodeKey');
						 
						 if(["Created", "Backordered", "Scheduled", "Released", "Included In Shipment"].indexOf(obj.STATUS) > -1){
							$scope.buildModelFor7_Day_Financial_Modal_date_breakdown('Sourcing', obj, 'byStatus');	
						 
							if(isNaN($scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]))		$scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]			= 0;
							if(isNaN($scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]))	$scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]		= 0;
															
							 total_sourced_units	= total_sourced_units + obj["SUM(UNITS)"];
							 total_sourced_dollars	= total_sourced_dollars + obj["SUM(DOLLARS)"];
							 
							switch (obj.STATUS) {
								case "Created":		$scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]		+= obj["SUM(UNITS)"]; break;
								case "Backordered":	$scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]		+= obj["SUM(UNITS)"]; break;
								case "Scheduled":	$scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]		+= obj["SUM(UNITS)"]; break;
								case "Released":	$scope.Total7dayFinancialModal['Sourcing']['units'][obj.STATUS]		+= obj["SUM(UNITS)"]; break; 
								default: break;
							}
							
							switch (obj.STATUS) {
								case "Created":		$scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "Backordered":	$scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "Scheduled":	$scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "Released":	$scope.Total7dayFinancialModal['Sourcing']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break; 
								default: break;
							}
							 
						 }else if(["WMoS", "DSV", "SFS", "BOPUS", "LFCs", "Marketplace", "Same Day"].indexOf(obj.STATUS) > -1){	
							$scope.buildModelFor7_Day_Financial_Modal_date_breakdown('Fulfillment', obj, 'byStatus');	
							
							if(isNaN($scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]))	$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]		= 0;
							if(isNaN($scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]))	$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	= 0;
								
							 total_fulfillment_units = total_fulfillment_units + obj["SUM(UNITS)"];
							 total_fulfillment_dollars = total_fulfillment_dollars + obj["SUM(DOLLARS)"];
							 							 
							switch (obj.STATUS) {
								case "WMoS":		$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "DSV":			$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "SFS":			$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "BOPUS":		$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "LFCs":		$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "Marketplace":	$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								case "Same Day":	$scope.Total7dayFinancialModal['Fulfillment']['units'][obj.STATUS]	+= obj["SUM(UNITS)"]; break;
								default: break;
							}
							 
							 switch (obj.STATUS) {
								case "WMoS":		$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "DSV":			$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "SFS":			$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "BOPUS":		$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "LFCs":		$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "Marketplace":	$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								case "Same Day":	$scope.Total7dayFinancialModal['Fulfillment']['dollars'][obj.STATUS]	+= obj["SUM(DOLLARS)"]; break;
								default: break;
							}
							 
							 
						 }else if(obj.STATUS == "Shipped"){
							$scope.buildModelFor7_Day_Financial_Modal_date_breakdown('Shipped', obj, 'byShipNodeKey');
							
							if(isNaN($scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]))		$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]		= 0;
							if(isNaN($scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]))	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	= 0;
								
							 total_shipped_units		= total_shipped_units + obj["SUM(UNITS)"];
							 total_shipped_dollars		= total_shipped_dollars + obj["SUM(DOLLARS)"]; 
							 
							 
							switch (obj.SHIPNODEKEY) {
								case "EFC1":	$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC2":	$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC3":	$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC4":	$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "LFC":		$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "RDC":		$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "STORE":	$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "DSV":		$scope.Total7dayFinancialModal['Shipped']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								default: break;
							}
							 
							 switch (obj.SHIPNODEKEY) {
								case "EFC1":	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC2":	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC3":	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC4":	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "LFC":		$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "RDC":		$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "STORE":	$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "DSV":		$scope.Total7dayFinancialModal['Shipped']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								default: break;
							}
							 
							 
						 }else if(obj.STATUS == "Invoiced"){
							$scope.buildModelFor7_Day_Financial_Modal_date_breakdown('Invoiced', obj, 'byShipNodeKey');
							
							if(isNaN($scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]))		$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	= 0;
							if(isNaN($scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]))	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	= 0;
								
							 total_invoiced_units		= total_invoiced_units + obj["SUM(UNITS)"];
							 total_invoiced_dollars 	= total_invoiced_dollars +  obj["SUM(DOLLARS)"];	
							 
								switch (obj.SHIPNODEKEY) {
								case "EFC1":	$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC2":	$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC3":	$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "EFC4":	$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "LFC":		$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "RDC":		$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "STORE":	$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								case "DSV":		$scope.Total7dayFinancialModal['Invoiced']['units'][obj.SHIPNODEKEY]	+= obj["SUM(UNITS)"]; break;
								default: break;
							}
							 
							 switch (obj.SHIPNODEKEY) {
								case "EFC1":	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC2":	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC3":	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "EFC4":	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "LFC":		$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "RDC":		$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "STORE":	$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								case "DSV":		$scope.Total7dayFinancialModal['Invoiced']['dollars'][obj.SHIPNODEKEY]	+= obj["SUM(DOLLARS)"]; break;
								default: break;
							}
						 }else if(obj.STATUS == "Cancelled"){
							 $scope.buildModelFor7_Day_Financial_Modal_date_breakdown('Cancelled', obj, 'byStatus');	
							 total_cancelled_units		= total_cancelled_units + obj["SUM(UNITS)"];
							 total_cancelled_dollars	= total_cancelled_dollars + obj["SUM(DOLLARS)"];
						 }
					 }
				}
				console.log("$scope.Total7dayFinancialModal:");
				console.log($scope.Total7dayFinancialModal);
				
				console.log("$scope.sevenDayFinancial_days:");
				console.log($scope.sevenDayFinancial_days);
			}catch(err) {
				console.log('getTotal7dayFinancialDollars FAILED!!');
			    console.log(err);
			}
			
			console.log("$scope.sevenDayFinancial_days:");
			console.log($scope.sevenDayFinancial_days);
			
			$scope.getTotal7dayFinancial_sourcedDollars		= total_sourced_dollars;		$scope.getTotal7dayFinancial_sourcedUnits		= total_sourced_units;
			$scope.getTotal7dayFinancial_fulfillmentDollars	= total_fulfillment_dollars;	$scope.getTotal7dayFinancial_fulfillmentUnits	= total_fulfillment_units;
			$scope.getTotal7dayFinancial_shippedDollars		= total_shipped_dollars;		$scope.getTotal7dayFinancial_shippedUnits		= total_shipped_units;
			$scope.getTotal7dayFinancial_invoicedDollars	= total_invoiced_dollars;		$scope.getTotal7dayFinancial_invoicedUnits		= total_invoiced_units;
			$scope.getTotal7dayFinancial_cancelledDollars	= total_cancelled_dollars;		$scope.getTotal7dayFinancial_cancelledUnits		= total_cancelled_units;			
		};
		
		$scope.getTotal7dayFinancialUnits = function() {
			var total_units = 0;
			try{
				for(var i=0;i<$scope.sevenDayFinancial.length();i++){
					total_units += $scope.sevenDayFinancial [i]["SUM(UNITS)"];
				}				
			}catch(err) {
				console.log('getTotal7dayFinancialUnits FAILED!!');
			    console.log(err);
			}
			return total_units;
		};

		$scope.redundancyStatisticsChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=redundancy_statistics",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				try{
					$scope.redundancyStatistics_last_run_timestamp = data.redundancy_last_run_timestamp;
					$scope.EFCstatisticResults = data.EFC;
					$scope.RDCstatisticResults = data.RDC;
					$scope.RDCTotalsstatisticResults = data.RDC_TOTALS;
				}catch(err) {
					console.log('14_day_fullfillment_performance REQUEST FAILED!!');
				    console.log(err);
				}
			});
		};
		//vars for line chart
		var xx = 0;
		var ids =[];
		//vars for bar chart
		var x = 0;
		var bar_ids =[];

		$scope.StoreElf14DayFullfillmentPerformanceStatisticsChartData = function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=14_day_fullfillment_performance",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				//--------------------------------------------
				try{
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;

                $scope.StoreElf14DayFullfillmentPerformanceStatisticsChart_LABEL    = "All";
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_main 		= data.all;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_all 			= data.all;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_dsv 			= data.direct_ship_vendor;
				$scope.StoreElf14DayFullfillmentPerformanceStatistics_ntw 			= data.network;
				//data for the bar graph

				if(x == 0){
				while (x < 14){

					// *adding data elements to array for chart calculation.
					// The 'unshift' places element BEFORE the previous.
					// This way results are displayed in correct date order on the graph.

					bar_ids.unshift({
						name: data.all.fulfillment_data[x].ORDER_DATE,
						shpd: data.all.fulfillment_data[x].SHPD,
						pend: data.all.fulfillment_data[x].PEND,
						cncl: data.all.fulfillment_data[x].CNCL
						});
					x++;
				}

				//creating new max height for y-scale

				bar_chart(bar_ids);
				}

				var getmax = 0;
				if(xx == 0){
				while (xx < 14){

					// *adding data elements to array for chart calculation.
					// The 'unshift' places element BEFORE the previous.
					// This way results are displayed in correct date order on the graph.

					ids.unshift({
						name: data.all.fulfillment_data[xx].ORDER_DATE,
						all: data.all.fulfillment_data[xx].ORDER_COUNT,
						dsv: data.direct_ship_vendor.fulfillment_data[xx].ORDER_COUNT,
						network: data.network.fulfillment_data[xx].ORDER_COUNT
						});

					//retrieving largest order count from array
					if(data.all.fulfillment_data[xx].ORDER_COUNT > getmax){
						getmax = data.all.fulfillment_data[xx].ORDER_COUNT;
					}
					xx++;
				}
				$('#allTS').text(data.all.last_run_timestamp);
				$('#dsvTS').text(data.direct_ship_vendor.last_run_timestamp);
				$('#netTS').text(data.network.last_run_timestamp);
				$('#14percTS').text(data.all.last_run_timestamp);
				//console.log(data.all.last_run_timestamp);

				//creating new max height for y-scale
				getmax = Math.ceil(getmax/1000)*1000;
				area_chart(ids,getmax);
				}
				// end line char
				}catch(err) {
					console.log('14_day_fullfillment_performance REQUEST FAILED!!');
				    console.log(err);
				}

			});
		};

 
		
		$scope.arraymove = function (arr, fromIndex, toIndex) {
		    var element = arr[fromIndex];
		    arr.splice(fromIndex, 1);
		    arr.splice(toIndex, 0, element);
		}

		$scope.FullfillmentPerformanceStatisticsChartData= function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/StoreElfComMetrics/quick_metrics_visuals',
			    data: "chart=fullfillment_performance",
			    timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				//---------------------
				try{
					$scope.FullfillmentPerformanceStatistics_last_run_timestamp = data.last_run_timestamp;
					$scope.RDCFullfillmentPerformanceStatistics			= data.RDC;
					$scope.RDCTotalFullfillmentPerformanceStatistics	= data.RDC_TOTALS;
					
					//*********************************************************************
					//add LFC sum to list
					var LFC_SUM =  {DESCRIPTION:'LFC', AVERAGE:0,BACKLOG:0,TTLSHPUNITCNT:0,CNCLUNITCNT:0, css_style:{'background':'#DF3A47'}};
					var stores_element = null;
					
					for (var key in data.EFC) {
						if(data.EFC[key].DESCRIPTION.indexOf("EFC")				> -1){data.EFC[key].css_style = {'background':'#59BAF1'}; data.EFC[key].BUTTON_DESCRIPTION = (data.EFC[key].DESCRIPTION.split(' ')[0]+data.EFC[key].DESCRIPTION.split(' ')[1]).substring(0,4); $scope.buildModalChartArray(data.EFC[key]);}
						else if(data.EFC[key].DESCRIPTION.indexOf("STORE")		> -1){data.EFC[key].css_style = {'background':'#35D65C'}; data.EFC[key].BUTTON_DESCRIPTION = 'STORE';	$scope.buildModalChartArray(data.EFC[key]); stores_element = data.EFC[key]; delete data.EFC[key]; }
						else if(data.EFC[key].DESCRIPTION.indexOf("RDC")		> -1){data.EFC[key].css_style = {'background':'#373D61'}; data.EFC[key].BUTTON_DESCRIPTION = 'RDC';		$scope.buildModalChartArray(data.EFC[key]);}						
						else if(data.EFC[key].DESCRIPTION.indexOf("LFC")		> -1){
						 	
							LFC_SUM.AVERAGE 		+= data.EFC[key].AVERAGE;
							LFC_SUM.BACKLOG 		+= data.EFC[key].BACKLOG;
							LFC_SUM.TTLSHPUNITCNT	+= data.EFC[key].TTLSHPUNITCNT;
							LFC_SUM.CNCLUNITCNT 	+= data.EFC[key].CNCLUNITCNT;
						}
					}
					
					//remove existing LFC
					for (var key in data.EFC) {		if(data.EFC[key].DESCRIPTION.indexOf("LFC") > -1){delete data.EFC[key];}	}
					
					//add the stores and LFC
					data.EFC[data.EFC.length] = stores_element;
					data.EFC[data.EFC.length+1] = LFC_SUM;
					
					$scope.EFCFullfillmentPerformanceStatistics 		= data.EFC; 
					//**********************************************************************
					//Start RDC chart data
					var j=0;
					var rdc_description = data.RDC_TOTALS.DESCRIPTION;
					var rdc_average 	= data.RDC_TOTALS.AVERAGE;
					var rdc_backlog 	= data.RDC_TOTALS.BACKLOG;
					//var rdc_pending 	= data.RDC_TOTALS.TOTAL;
					var rdc_shipped 	= data.RDC_TOTALS.TTLSHPUNITCNT;
					var rdc_cancel 		= data.RDC_TOTALS.CNCLUNITCNT;
					
					$scope.pending_data_RDC = [];

					while (j < data.RDC_TOTALS['EFC Array'].length){
						//var chart_max =	data.EFC[0].TOTAL;
							$scope.pending_data_RDC.push({
										name: 'RDC' ,
										qty:  data.RDC_TOTALS['EFC Array'][j].QTY,
										desc: data.RDC_TOTALS['EFC Array'][j].DESCRIP
										});
						j++;
					}
					j = 0;
					

					var updatedTS = data.last_run_timestamp;
					$scope.FullfillmentPerformanceUpdatedTS = data.last_run_timestamp;
			}catch(err) {
				console.log('fullfillment_performance REQUEST FAILED!!');
			    console.log(err);
			}

			});
		};
		
		$scope.buildModalChartArray = function (element){
			var manQTY		= 0;
			var manDESC		= "Manifested";
			$scope['pending_data_'+element.BUTTON_DESCRIPTION] = [];
			
			//if(element.DESCRIPTION.indexOf("EFC 1") > -1){
			for(var key in element['EFC Array']){
				if(element['EFC Array'][key].DESCRIP == "Weighed" || element['EFC Array'][key].DESCRIP == "Manifested"){
					 manQTY += element['EFC Array'][key].QTY;
				}else{
					console.log('adding to pending_data_'+element.BUTTON_DESCRIPTION);
					$scope['pending_data_'+element.BUTTON_DESCRIPTION].push({
								name: element.DESCRIPTION,
								qty:  element['EFC Array'][key].QTY,
								desc: element['EFC Array'][key].DESCRIP
								});
				}
				if(element.BUTTON_DESCRIPTION.substring(0,3) == 'EFC' && key == (element['EFC Array'].length -1)){
					$scope['pending_data_'+element.BUTTON_DESCRIPTION].push({
						name: element.DESCRIPTION,
						qty:  manQTY,
						desc: element['EFC Array'][key].DESCRIP
						});
					manQTY= 0;
				}
			}
			//}
		};
		
		//bar chart creation / options
		$scope.small_bar_chart = function (small_bar_values){
			$window.bar2 = Morris.Bar({
				  element: 'node-graph-area',
				  data: small_bar_values,
				  xkey: 'desc',
				  ykeys: ['qty'],
				  hideHover: 'auto',
		          labels: ['Units'],
		          barColors: ['#4a8bc2'],
		          xLabelMargin: 3,
		          xLabelAngle: 30,
		          parseTime: false,
		          smooth: true,
		          resize: true,
		          redraw: true,resize: true
				});

		}

		$scope.refresh = function(){
			try{
				if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
					$scope.loadGlanceChartData();
					$scope.load7dayFinancialChartData();
					$scope.loadAllNodeIventorySnapshotChartData();
					$scope.redundancyStatisticsChartData();
					$scope.StoreElf14DayFullfillmentPerformanceStatisticsChartData();
					$scope.FullfillmentPerformanceStatisticsChartData();
				}
			}catch(err) {
				console.log('refresh FAILED!!');
			    console.log(err);
			}
		};

		$scope.refresh();
		$interval($scope.refresh, 30000);

	}

</script>
<script>$(function() {$( document ).tooltip();});</script>
<style>label {display: inline-block;width: 5em;}</style>


<div ng-app="DashboardApp">
	<div ng-controller="StatisticsController2">

<!-- #################################################################################################################################################### -->		
<!-- ################################################################### [MODAL START] ################################################################### -->
<!-- #################################################################################################################################################### -->

		<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">
								<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
							</button>
							<h4 id="modal_title"> Pending Fulfillment Breakdown </h4>
						</div>
						<div class="modal-body2">
							<div class="graph" style="height:300px; width: 90%;" id="node-graph-area"></div>
						</div>
						<div class="modal-footer2">
							<button type="button" class="btn btn-close" data-dismiss="modal" onclick="$('#node-graph-area').empty();">Close</button>
						</div>
					</div>
				</div>
			</div>

			
		<div class="modal fade" id="7_Day_Financial_Modal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 id="modal_title">Financial Breakdown</h4>
					</div>						
					<div class="modal-body2">
				 
                     
                     <div id="7_day_modal_bar_chart" style="width:100%" class="graph"></div>
                       
					</div>
					<div class="modal-footer2">
						<button type="button" class="btn btn-close"
							data-dismiss="modal" onclick="$('#7_day_modal_bar_chart_by_date').empty();$('#7_day_modal_bar_chart').empty();">Close</button>
					</div>
				</div>
			</div>
		</div>
		 
		<div class="modal fade" id="7_Day_Financial_Modal_date_breakdown" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">
								<span aria-hidden="true">&times;</span><span class="sr-only" onclick="$('#7_Day_Financial_Modal_date_breakdown').modal('toggle');">Close</span>
							</button>
							<h4 id="modal_title">Financial Breakdown - by date</h4>
						</div>						
						<div class="modal-body2">
					  
                      <div id="7_day_modal_bar_chart_by_date" class="graph"></div> 
                      
						</div>
						<div class="modal-footer2">
							<button type="button" class="btn btn-close"
								data-dismiss="modal" onclick="$('#7_day_modal_bar_chart_by_date').empty();$('#7_Day_Financial_Modal_date_breakdown').modal('toggle');">Close</button>
						</div>
					</div>
				</div>
			</div>

<!-- #################################################################################################################################################### -->		
<!-- ################################################################### [MODAL STOP] ################################################################### -->
<!-- #################################################################################################################################################### -->

	<div class="row state-overview">
         <div class="col-md-14 col-sm-6"  >
             <section class="panel" style="height: 60px;">
                 <div class="symbol terques" style="height: 60px; padding: 5px;"><i class="fa fa-shopping-cart"></i></div>
                 <div class="value"><h1 class="count" id="oc">0</h1><p style="font-size: 11px;margin-top: 5px;">Order Count</p></div>
             </section>
         </div>
         <div class="col-md-13 col-sm-6">
             <section class="panel" style="height: 60px;">
                 <div class="symbol red" style="height: 60px; padding: 5px;"><i class="fa fa-barcode"></i></div>
                 <div class="value"><h1 class="count2" id="uc">0</h1><p style="font-size: 11px;margin-top: 5px;">Unit Count</p></div>
             </section>
         </div>
         <div class="col-md-13 col-sm-6">
             <section class="panel" style="height: 60px;">
                 <div class="symbol yellow" style="height: 60px; padding: 5px;"><i class="fa fa-tags"></i></div>
                 <div class="value"><h1 class="count3" id="ds">0</h1><p style="font-size: 11px;margin-top: 5px;">Demand Sales</p></div>
             </section>
         </div>
         <div class="col-md-13 col-sm-6">
             <section class="panel" style="height: 60px;">
                 <div class="symbol blue"  style="height: 60px; padding: 5px;"><i class="fa fa-money"></i></div>
                 <div class="value"><h1 class="count4" id="es">0</h1><p style="font-size: 11px;margin-top: 5px;">ECOM Fulfilled Sales</p></div>
             </section>
         </div>
         <div class="col-md-13 col-sm-6">
             <section class="panel" style="height: 60px;">
                 <div class="symbol green"  style="height: 60px; padding: 5px;"><i class="fa fa-money"></i></div>
                 <div class="value"><h1 class="count5" id="ss">0</h1><p style="font-size: 11px;margin-top: 5px;">Store Fulfilled Sales</p></div>
             </section>
         </div>
     </div> 
     <!-- ---------------------------------------------------------------------------  -->
     <div class="row">
           <div class="col-lg-12">
               <section class="panel">
                <header class="panel-heading" id="chart_title">7 Day Fulfillment Snapshot - (Dollar/Units)
                <a target="_self" ng-href="#" role="button" ng-model="search" ng-click="csv7dayFinancialChartData()">Export</a>
                 <span style="float:right">{{sevenDayFinancial_timestamp}}</span></header>
                   <div class="panel-body" style="padding: 10px;">
                   
                   <table class="lh-dashboard-summary-list">
                   		<thead></thead>
                        <tbody>
                        	<tr>
                        	<td>
                           		<table>
		                           <thead><tr><th colspan="2"><i class="fa fa-download text-primary"><span> Sourcing</span></i></th></tr></thead>
		                           <tbody>
		                           <tr><td><h5>Dollars*:</h5></td> 
			                           <td><a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Sourcing', 'Dollars')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-success btn-xs">
				                           <strong>{{getTotal7dayFinancial_sourcedDollars | currency}}</strong></a>
			                           </td>
		                           </tr>
		                           <tr><td><h5>Units:</h5></td>	
			                           <td><a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Sourcing', 'Units')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-info btn-xs">
			                           <strong>{{getTotal7dayFinancial_sourcedUnits | number}}</strong></a></td>
		                           </tr>
		                           </tbody>
	                           </table>
	                          </td>
                           <td></td> 
                           
                           <td>
                           <table>
	                           <thead><tr><th colspan="2"><i class="fa fa-users text-info"><span> Fulfillment</span></i><th></tr></thead>
	                           <tbody>
	                           <tr><td><h5>Dollars*:</h5></td> 
		                           <td>
		                           	<a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Fulfillment', 'Dollars')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-success btn-xs">
			                           <strong>{{getTotal7dayFinancial_fulfillmentDollars | currency}}</strong></a>
		                           </td>
	                           </tr>
	                           <tr><td><h5>Units:</h5></td>	
		                           <td>
		                           	<a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Fulfillment', 'Units')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-info btn-xs">
		                           <strong>{{getTotal7dayFinancial_fulfillmentUnits | number}}</strong></a>
		                           </td>
	                           </tr>
	                           </tbody>
                           </table> 
                           </td>
                           
                           <td></td>
                           
                           <td>
                           <table>
	                           <thead><tr><th colspan="2"><i class="fa fa-truck text-muted"><span> Shipped</span></i><th></tr></thead>
	                           <tbody>
	                           <tr><td><h5>Dollars*:</h5></td><td>
		                           <a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Shipped', 'Dollars')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-success btn-xs">
		                           	<!-- <a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('Shipped', 'SUM(DOLLARS)', 'Dollars', '#4da74d')" class="btn btn-success btn-xs"> -->
			                           <strong>{{getTotal7dayFinancial_shippedDollars | currency}}</strong></a>
		                           </td>
	                           </tr>
	                           <tr><td><h5>Units:</h5></td><td>
		                           <a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Shipped', 'Units')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-info btn-xs">
		                           	<!-- <a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('Shipped', 'SUM(UNITS)', 'Units', '#4a8bc2')" class="btn btn-info btn-xs"> -->
		                           <strong>{{getTotal7dayFinancial_shippedUnits | number}}</strong></a>
		                           </td>
	                           </tr>
	                           </tbody>
                           </table>
                           </td>
                           <td></td>
                           
                            
                            <td>
                           	<table>
	                           <thead><tr><th colspan="2"><i class="fa fa-money text-success">			<span> Invoiced</span></i></th></tr></thead>
	                           <tbody>
	                           <tr><td><h5>Dollars*:</h5></td> 
		                           <td>
		                           <a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Invoiced', 'Dollars')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-success btn-xs">
		                           	<!-- <a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('Invoiced', 'SUM(DOLLARS)', 'Dollars', '#4da74d')" class="btn btn-success btn-xs"> -->
			                           <strong>{{getTotal7dayFinancial_invoicedDollars | currency}}</strong></a>
		                           </td>
	                           </tr>
	                           <tr><td><h5>Units:</h5></td><td>
		                           <a id="7dayFinancial_fulfillment" ng-click="showTotal7dayFinancialModalChart('bar_chart_7_day', 'Invoiced', 'Units')" data-toggle="modal" data-target="#7_Day_Financial_Modal" class="btn btn-info btn-xs">
		                           	<!-- <a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('Invoiced', 'SUM(UNITS)', 'Units', '#4a8bc2')" class="btn btn-info btn-xs"> -->
		                           <strong>{{getTotal7dayFinancial_invoicedUnits | number}}</strong></a>
		                           </td>
	                           </tr>
	                           </tbody>
                           </table>
	                       </td> 
                            
                            <td></td>
                           
                           <td>
                   			<table>
	                           <thead><tr><th colspan="2"><i class="fa fa-warning text-danger"><span> Cancelled</span></i></th></tr></thead>
	                           <tbody>
	                           <tr><td><h5>Dollars*:</h5></td> 
		                           <td>
		                           	<a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('byStatus', 'Cancelled', 'SUM(DOLLARS)', 'Dollars', '#4da74d')" class="btn btn-success btn-xs">
			                           <strong>{{getTotal7dayFinancial_cancelledDollars | currency}}</strong></a>
		                           </td>
	                           </tr>
	                           <tr><td><h5>Units:</h5></td>	
		                           <td>
		                           	<a id="7dayFinancial_fulfillment" ng-click="bar_chart_7_Day_Financial_Modal_date_breakdown('byStatus', 'Cancelled', 'SUM(UNITS)', 'Units', '#4a8bc2')"  class="btn btn-info btn-xs">
		                           <strong>{{getTotal7dayFinancial_cancelledUnits | number}}</strong></a>
		                           </td>
	                           </tr>
	                           </tbody>
                           </table>
                           </td>
                           </tr>
                            </tbody>                            
                       </table>
                       <b style="color: #A1A1A1; float: right;">*Kohl's cash discount not included in Dollars</b>
                   </div>
               </section>
           </div>
       </div>
     <!-- ---------------------------------------------------------------------------  -->
     
      
        
    <div class="row">
		<div class="col-md-6">
			<section class="panel" id="chartSection">
			 <header class="panel-heading" id="chart_title">
                                  14 Day Fulfillment - Total Orders
                                  <div id="legend"></div>
                                  <div class="row" >
	                                  <div class="col-xs-7" style="margin-top: 4px; float: right;">
	                                  	<div class="col-xs-4" id="allTS" style="font-size: 10px;color: rgb(74, 139, 194);">00/00 00:00 AM</div>
	                                  	<div class="col-xs-4" id="dsvTS" style="font-size: 10px;color: rgb(255, 108, 96);">00/00 00:00 AM</div>
	                                  	<div class="col-xs-4" id="netTS" style="font-size: 10px;color: rgb(169, 216, 110);">00/00 00:00 AM</div>
	                                  </div>
                                  </div>
                              </header>

				<div class="panel-body">
				<!--  set height to match data div -->
                	<div id="graph-area" class="graph" style="height: 300px;"></div>
                </div>
			</section>
			</div>
			<div class="col-md-6" id="bar_graph_module">
				<section class="panel" id="chartSection">
				 <header class="panel-heading" id="chart_title">
	                                  14 Day Order Fulfillment Status
	                                   <div id="legend_bar"></div>
	                                   <div class="row" >
		                                  <div class="col-xs-3" style="margin-top: 4px; float: right;">
		                                  	<div class="col-xs-12" id="14percTS" style="font-size: 10px;">00/00 00:00 AM</div>
		                                  </div>
	                                  </div>
	                              </header>
					<div class="panel-body">
					<!--  set height to match data div -->
	                	<div id="bar-graph" class="graph" style="height: 300px;"></div>
	                </div>
				</section>
			</div>
	</div>
		<div class="row" type="ROW">
			<div id="efc_area"></div>
			
			<div class='col-md-3' ng-repeat="efc_row in EFCFullfillmentPerformanceStatistics | orderBy: 'css_style'">
				<section class='panel' id='weatherNode'>
					<div class='weather-bg' ng-style="efc_row.css_style">
						<div class='panel-body' id='no_padding'>
							<div class='row'>
								<div class='col-xs-5'><i ng-class="{'fa fa-sun-o': (efc_row.AVERAGE < 5 && ['LFC','STORES'].indexOf(efc_row.DESCRIPTION.substring(0,3)) == -1), 'fa fa-cloud': (efc_row.AVERAGE > 5 && ['LFC','STORES'].indexOf(efc_row.DESCRIPTION.substring(0,3)) == -1), 'fa fa-dropbox': efc_row.DESCRIPTION.substring(0,3)=='LFC', 'fa fa-building-o':efc_row.DESCRIPTION.substring(0,3)=='STORE'}"></i>{{efc_row.DESCRIPTION}}</div>
								<div class='col-xs-7'>
									<div class='degree' style='font-size: 50px;'>{{efc_row.AVERAGE | number:2}}</div>
									<div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div>
								</div>
							</div>
							<div class='row'>
								<div class='col-xs-5'></div>
								<div class='col-xs-7' style='margin-top: 4px;'>
									<div class='col-xs-12' style='font-size: 10px;'>{{FullfillmentPerformanceUpdatedTS}}</div>
								</div>
							</div>
						</div>
					</div>
					<footer class='weather-category' style ='padding-bottom: 7px;'>
						<ul>
							<li class='active'><h5>Backlog</h5><span id='bl_span'>{{efc_row.BACKLOG | number:0}}</span></li>
							<li ng-show="efc_row.DESCRIPTION.indexOf('LFC')<0"><h5>In Process</h5><span id='pn_span'><button id="{{efc_row.BUTTON_DESCRIPTION}}" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>{{efc_row.TOTAL | number:0}}</button></span></li>
							<li><h5>Shipped</h5><span id='sh_span'>{{efc_row.TTLSHPUNITCNT | number:0}}</span></li>
							<li><h5>Canceled</h5><span id='cn_span'>{{efc_row.CNCLUNITCNT | number:0}}</span></li>
						</ul>
					</footer>
					<footer class='weather-category' style='padding: 0px;'>
						<ul>
							<li id='module_key'><h5>Sourced: {{ efc_row.BACKLOG+efc_row.TOTAL | number:0}}</h5></li>
						</ul>
					</footer>
				</section>
			</div>
			
			<div class='col-md-3'>
				<section class='panel' id='weatherNode'>
					<div class='weather-bg' style='background: #373D61'>
					<div class='panel-body' id='no_padding'>
						<div class='row'>
							<div class='col-xs-5'><i class='fa fa-truck'></i>RED</div>
							<div class='col-xs-7'><div class='degree' style='font-size: 50px;'>{{RDCTotalFullfillmentPerformanceStatistics.AVERAGE}}</div>
								<div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div>
							</div>
						</div>
						<div class='row'>
							<div class='col-xs-5'></div>
							<div class='col-xs-7' style='margin-top: 4px;'>
								<div class='col-xs-12' style='font-size: 10px;'>{{FullfillmentPerformanceUpdatedTS}}</div>
							</div>
						</div>
					</div>
				</div>
				<footer class='weather-category' style ='padding-bottom: 7px;'>
					<ul>
						<li class='active' style='width:32%'><h5>Backlog</h5><span id='bl_span'><button id="RDC" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>{{RDCTotalFullfillmentPerformanceStatistics.BACKLOG}}</button></span></li>
						<li style='width:32%'><h5>Shipped</h5><span id='sh_span'>{{RDCTotalFullfillmentPerformanceStatistics.TTLSHPUNITCNT}}</span></li>
						<li style='width:32%'><h5>Canceled</h5><span id='cn_span'>{{RDCTotalFullfillmentPerformanceStatistics.CNCLUNITCNT}}</span></li>
					</ul>
				</footer>
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


