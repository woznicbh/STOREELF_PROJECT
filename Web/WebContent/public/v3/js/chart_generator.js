//script to create charts based on data passed.

$( document ).ready(function() {
	$(window).resize(function() {
		//window.m.redraw();
		window.bar.redraw();
	});
	$(".sidebar-toggle-box").click(function() {
		//window.m.redraw();
		window.bar.redraw();
	});
});

function commaSeparateNumber(val){
    while (/(\d+)(\d{3})/.test(val.toString())){
      val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
    }
    return val;
  }


//14 Day - Cust Cancel graph generator - Mir
function area_chart_cust_cancel(the_values, max){
	//making sure y-scale isn't too large

	var newMax = max + 100;
	
//create the actual chart with options
	window.m = Morris.Bar({
		element: 'graph-area-cust-cancel',
		data: the_values,
		xkey: 'name',
		ykeys: ['UNIT_COUNT'],
		labels: ['Units Cancelled'],
		hideHover: 'auto',
		ymax: newMax,
		xLabelMargin: 3,
		xLabelAngle: 30,
      	lineWidth: 2,
      	pointSize: 4,
      	//lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
		barColors: ['#4a8bc2'],
      	fillOpacity: 0.0,
     //enable if showing overlapping area chart
      	behaveLikeLine: true,
      	parseTime: false,
      	smooth: true,
      	resize: true,
      	redraw: true
  });
//dynamically create legend based off colors and inputs used
$('#custCancelLegend').empty();
window.m.options.labels.forEach(function(label, i){
	var hov_descrip = "type= "+label;
	//adding hover descriptions to labels
	hov_descrip = "Shows total customer cancelled units." ;
    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.barColors[i]);
    $('#custCancelLegend').append(legendItem);
});
}


//14 Day - Cancel graph generator - Mir
function area_chart_cancel(the_values, max){
	
	//making sure y-scale isn't too large

	var newMax = max + 100;
	
//create the actual chart with options
	window.m = Morris.Area({
		element: 'graph-area-cancel',
		data: the_values,

		xkey: 'name',
		ykeys: ['efc1', 'efc2','efc3','efc4','rdc'],
		labels: ['EFC1', 'EFC2','EFC3','EFC4','RDC'],
		hideHover: 'auto',
		ymax: newMax,
		xLabelMargin: 3,
		xLabelAngle: 30,
      	lineWidth: 2,
      	pointSize: 4,
      	//lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
		lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
      	fillOpacity: 0.0,
     //enable if showing overlapping area chart
      	behaveLikeLine: true,
      	parseTime: false,
      	smooth: true,
      	resize: true,
      	redraw: true
  });

//dynamically create legend based off colors and inputs used
$('#legend').empty();
window.m.options.labels.forEach(function(label, i){
	var hov_descrip = "type= "+label;
	//adding hover descriptions to labels
	if(label == 'EFC1'){
		hov_descrip = "Shows total backordered qty from efc1." ;
	}else if(label == 'EFC2'){
		hov_descrip = "Shows total backordered qty from efc2." ;
	}else if(label == 'EFC3'){
		hov_descrip = "Shows total backordered qty from efc3." ;
	}else if(label == 'EFC4'){
		hov_descrip = "Shows total backordered qty from efc4." ;
	}else if(label == 'RDC'){
		hov_descrip = "Shows total backordered qty from rdc." ;
	}else if(label == 'STORES'){
		hov_descrip = "Shows total backordered qty from stores." ;
	}

    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
    $('#legend').append(legendItem);
});
}

//14 Day - Cancel graph generator - Mir
function store_area_chart_cancel(the_values, max){

	//making sure y-scale isn't too large

	var newMax = max + 100;

//create the actual chart with options
	window.m = Morris.Area({
		element: 'store-graph-area-cancel',
		data: the_values,

		xkey: 'name',
		ykeys: ['cst', 'est','pst','mst','mdt'],
		labels: ['CST', 'EST','PST','MST','MDT'],
		hideHover: 'auto',
		ymax: newMax,
		xLabelMargin: 3,
		xLabelAngle: 30,
      	lineWidth: 2,
      	pointSize: 4,
      	//lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
		lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
      	fillOpacity: 0.0,
     //enable if showing overlapping area chart
      	behaveLikeLine: true,
      	parseTime: false,
      	smooth: true,
      	resize: true,
      	redraw: true
  });

//dynamically create legend based off colors and inputs used
$('#storelegend').empty();
window.m.options.labels.forEach(function(label, i){
	var hov_descrip = "type= "+label;
	//adding hover descriptions to labels
	if(label == 'CST'){
		hov_descrip = "Shows total backordered qty from CST." ;
	}else if(label == 'EST'){
		hov_descrip = "Shows total backordered qty from EST." ;
	}else if(label == 'PST'){
		hov_descrip = "Shows total backordered qty from PST." ;
	}else if(label == 'MST'){
		hov_descrip = "Shows total backordered qty from MST." ;
	}else if(label == 'MDT'){
		hov_descrip = "Shows total backordered qty from MDT." ;
	}

    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
    $('#storelegend').append(legendItem);
});

}



//14 Day - resource graph generator - Mir
function area_chart_resrc(the_values, max){

	//making sure y-scale isn't too large

	var newMax = max + 100;

//create the actual chart with options
	window.m = Morris.Area({
		element: 'graph-area-rsrc',
		data: the_values,

		xkey: 'name',
		ykeys: ['efc1', 'efc2','efc3','efc4','rdc'],
		labels: ['EFC1', 'EFC2','EFC3','EFC4','RDC'],
		hideHover: 'auto',
		ymax: newMax,
		xLabelMargin: 3,
		xLabelAngle: 30,
      	lineWidth: 2,
      	pointSize: 4,
      	//lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
		lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
      	fillOpacity: 0.0,
     //enable if showing overlapping area chart
      	behaveLikeLine: true,
      	parseTime: false,
      	smooth: true,
      	resize: true,
      	redraw: true
  });

//dynamically create legend based off colors and inputs used
$('#legendrsrc').empty();
window.m.options.labels.forEach(function(label, i){
	var hov_descrip = "type= "+label;
	//adding hover descriptions to labels
	if(label == 'EFC1'){
		hov_descrip = "Shows total resourced qty to efc1." ;
	}else if(label == 'EFC2'){
		hov_descrip = "Shows total resourced qty to efc2." ;
	}else if(label == 'EFC3'){
		hov_descrip = "Shows total resourced qty to efc3." ;
	}else if(label == 'EFC4'){
		hov_descrip = "Shows total resourced qty to efc4." ;
	}else if(label == 'RDC'){
		hov_descrip = "Shows total resourced qty to rdc." ;
	}else if(label == 'STORES'){
		hov_descrip = "Shows total resourced qty to stores." ;
	}

    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
    $('#legendrsrc').append(legendItem);
});

}//Cancel dashboard graphs

//14 Day - resource graph generator - Mir
function store_area_chart_resrc(the_values, max){

	//making sure y-scale isn't too large

	var newMax = max + 100;

//create the actual chart with options
	window.m = Morris.Area({
		element: 'store-graph-area-rsrc',
		data: the_values,

		xkey: 'name',
		ykeys: ['cst', 'est','pst','mst','mdt'],
		labels: ['CST', 'EST','PST','MST','MDT'],
		hideHover: 'auto',
		ymax: newMax,
		xLabelMargin: 3,
		xLabelAngle: 30,
      	lineWidth: 2,
      	pointSize: 4,
      	//lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
		lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e', '#c2814a' , '#c24a8b'],
      	fillOpacity: 0.0,
     //enable if showing overlapping area chart
      	behaveLikeLine: true,
      	parseTime: false,
      	smooth: true,
      	resize: true,
      	redraw: true
  });

//dynamically create legend based off colors and inputs used
$('#storelegendrsrc').empty();
window.m.options.labels.forEach(function(label, i){
	var hov_descrip = "type= "+label;
	//adding hover descriptions to labels
	if(label == 'CST'){
		hov_descrip = "Shows total resourced qty to CST." ;
	}else if(label == 'EST'){
		hov_descrip = "Shows total resourced qty to EST." ;
	}else if(label == 'PST'){
		hov_descrip = "Shows total resourced qty to PST." ;
	}else if(label == 'MST'){
		hov_descrip = "Shows total resourced qty to MST." ;
	}else if(label == 'MDT'){
		hov_descrip = "Shows total resourced qty to MDT." ;
	}

    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
    $('#storelegendrsrc').append(legendItem);
});

}//Cancel dashboard graphs

function area_chart(the_values, max){

			//making sure y-scale isn't too large
			var newMax = max + 10000;

		//create the actual chart with options
		window.m = Morris.Area({
        	element: 'graph-area',
        	data: the_values,

	          xkey: 'name',
	          ykeys: ['all', 'dsv','network'],
	          labels: ['All', 'DSV','Network'],
	          hideHover: 'auto',
	          ymax: newMax,
	          xLabelMargin: 3,
	          xLabelAngle: 30,
	          lineWidth: 2,
	          pointSize: 4,
	          lineColors: ['#4a8bc2', '#ff6c60', '#a9d86e'],
	          fillOpacity: 0.3,
	         //enable if showing overlapping area chart
	          behaveLikeLine: true,
	          parseTime: false,
	          smooth: true,
	          resize: true,
	          redraw: true
	      });

		//dynamically create legend based off colors and inputs used
		window.m.options.labels.forEach(function(label, i){
			var hov_descrip = "type= "+label;
			//adding hover descriptions to labels
			if(label == 'All'){
				hov_descrip = "Shows total order volume for ALL StoreElf order types." ;
			}else if(label == 'DSV'){
				hov_descrip = "Shows total order volume sourced by Direct Ship Vendors." ;
			}else if(label == 'Network'){
				hov_descrip = "Shows total order volume sourced by the Network." ;
			}
		    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
		    $('#legend').append(legendItem);
		});

		}

	function bar_chart_7_day_fulfillment_units(elementID, dataArray){
		Window.bar3 = Morris.Bar({
			  element: elementID,
			  data: dataArray,
			  xkey: 'channel',
			  ykeys: ['units'],
			  hideHover: 'auto',
	        labels: ['Units'],
	        barColors: ['#4a8bc2'],
	        xLabelMargin: 3,
	        xLabelAngle: 30,
	        parseTime: false,
	        smooth: true,
	        resize: true,
	        redraw: true
			});
	}
	
	/*function bar_chart_7_day_fulfillment_dollars(elementID, dataArray){
		Window.bar3 = Morris.Bar({
			  element: elementID,
			  data: dataArray,
			  xkey: 'channel',
			  ykeys: ['dollars'],
			  hideHover: 'auto',
	        labels: ['Dollars'],
	        barColors: ['#4a8bc2'],
	        xLabelMargin: 3,
	        xLabelAngle: 30,
	        parseTime: false,
	        smooth: true,
	        resize: true,
	        redraw: true
			});
	}*/

		//bar chart creation / options
		function bar_chart(bar_values){
			window.bar = Morris.Bar({
				  element: 'bar-graph',
				  data: bar_values,
				  xkey: 'name',
				  ykeys: ['shpd', 'pend','cncl'],
				  ymax: 100,
				  hideHover: 'auto',
		          labels: ['Shipped %', 'Pending %','Canceled %'],
		          barColors: ['#a9d86e', '#4a8bc2', '#ff6c60'],
		          xLabelMargin: 3,
		          xLabelAngle: 30,
		          parseTime: false,
		          smooth: true,
		          resize: true,
		          redraw: true,
		          stacked: true
				})
				//.on('click', function(i, row){
					/*alert("bar="+i+" :"+row);
					console.log("bar="+i+" :"+row);*/
					//$('#bar_graph_module').modal({show: true});
				//});

			//dynamically create legend based off colors and inputs used
			window.bar.options.labels.forEach(function(label, i){
			    var legendItem = $('<span ></span>').text(label ).css('color', window.bar.options.barColors[i]);
			    $('#legend_bar').append(legendItem);
			});
		}


		function sales_bar_chart(bar_values){
			window.bar = Morris.Bar({
				  element: 'bar-graph',
				  data: bar_values,
				  xkey: 'name',
				  ykeys: ['shpd', 'pend','cncl'],
				  //ymax: 10000000,
				  hideHover: 'auto',
		          labels: ['Shipped $', 'Pending $','Canceled $'],
		          barColors: ['#a9d86e', '#4a8bc2', '#ff6c60'],
		          xLabelMargin: 3,
		          xLabelAngle: 30,
		          parseTime: false,
		          smooth: true,
		          resize: true,
		          redraw: true,
		          stacked: true
				});

			//dynamically create legend based off colors and inputs used
			window.bar.options.labels.forEach(function(label, i){
			    var legendItem = $('<span ></span>').text(label ).css('color', window.bar.options.barColors[i]);
			    $('#legend_bar').append(legendItem);
			});
		}

function sales_area_chart(the_values, max){

			//making sure y-scale isn't too large
			var newMax = max + 10000;

		//create the actual chart with options
		window.m = Morris.Area({
        	element: 'graph-area',
        	data: the_values,

	          xkey: 'name',
	          ykeys: ['shpd', 'pend','cncl'],
	          labels: ['Shipped', 'Pending','Cancelled'],
	          hideHover: 'auto',
	          //ymax: newMax,
	          xLabelMargin: 3,
	          xLabelAngle: 30,
	          lineWidth: 2,
	          pointSize: 4,
	          lineColors: ['#a9d86e', '#4a8bc2', '#ff6c60'],
	          fillOpacity: 0.3,
	         //enable if showing overlapping area chart
	          behaveLikeLine: true,
	          parseTime: false,
	          smooth: true,
	          resize: true,
	          redraw: true
	      });

		//dynamically create legend based off colors and inputs used
		window.m.options.labels.forEach(function(label, i){
			var hov_descrip = "type= "+label;
			//adding hover descriptions to labels
			if(label == 'All'){
				hov_descrip = "Shows total order volume for ALL StoreElf order types." ;
			}else if(label == 'DSV'){
				hov_descrip = "Shows total order volume sourced by Direct Ship Vendors." ;
			}else if(label == 'Network'){
				hov_descrip = "Shows total order volume sourced by the Network." ;
			}
		    var legendItem = $("<span title='"+hov_descrip+"' ></span>").text(label).css('color', window.m.options.lineColors[i]);
		    $('#legend').append(legendItem);
		});

		}

		//bar chart creation / options
		function small_bar_chart(small_bar_values){
			window.bar2 = Morris.Bar({
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
		          redraw: true
				});

		}


		//donut chart creation / options
//		function donut_chart(donut_values){
//			window.donut = Morris.Donut({
//				  element: 'donut-efc',
//				  data: donut_values,
//				  colors: ['#4a8bc2', '#a9d86e', '#ff6c60'],
//				});
//
//		}
/*
		function createEFCNodeModule(description,average,backlog,pending,shipped,cancel,tot_average, sourced, updatedTS){
			var icon_html = 'fa-sun-o';
			var desc_id = null;
			tot_average = 5;
			if(tot_average > average){
				icon_html = 'fa-sun-o';
			}else{
				icon_html = 'fa-cloud';
			}

			if(description == "EFC 1-873"){
				desc_id = "EFC1";
			} else if(description == "EFC 2-809"){
				desc_id = "EFC2";
			} else if(description == "EFC 3-819"){
				desc_id = "EFC3";
			}else if(description == "EFC 4-829"){
				desc_id = "EFC4";
			}
			var sourced = backlog + pending;
			//html to create 'weather' module. (Shows fulfillment performance)
			var htmlChunk = "<div class='col-md-3'><section class='panel' id='weatherNode'><div class='weather-bg' style='background: #59BAF1'><div class='panel-body' id='no_padding'><div class='row'><div class='col-xs-5'><i class='fa "+icon_html+"'></i>"+description+"</div><div class='col-xs-7'><div class='degree' style='font-size: 50px;'>"+average+"</div><div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div></div></div><div class='row'><div class='col-xs-5'></div><div class='col-xs-7' style='margin-top: 4px;'><div class='col-xs-12' style='font-size: 10px;'>"+updatedTS+"</div></div></div></div></div><footer class='weather-category' style ='padding-bottom: 7px;'><ul><li class='active'><h5>Backlog</h5><span id='bl_span'>"+commaSeparateNumber(backlog)+"</span></li><li><h5>In Process</h5><span id='pn_span'><button id="+desc_id+" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>"+commaSeparateNumber(pending)+"</button></span></li><li><h5>Shipped</h5><span id='sh_span'>"+commaSeparateNumber(shipped)+"</span></li><li><h5>Canceled</h5><span id='cn_span'>"+commaSeparateNumber(cancel)+"</span></li></ul></footer><footer class='weather-category' style='padding: 0px;'><ul><li id='module_key'><h5>Sourced: "+commaSeparateNumber(sourced)+"</h5></li></ul></footer></section></div>";
			$('#efc_area').before(htmlChunk);
		}

		function createLFCNodeModule(description,average,backlog,shipped,cancel, updatedTS){

			//html to create 'weather' module. (Shows fulfillment performance)
			var htmlChunk = "<div class='col-md-3'><section class='panel' id='weatherNode'><div class='weather-bg' style='background: #DF3A47;'><div class='panel-body' id='no_padding'><div class='row'><div class='col-xs-5'><i class='fa fa-dropbox'></i>LFC</div><div class='col-xs-7'><div class='degree' style='font-size: 50px;'>"
			+ average
			+ "</div><div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div></div></div><div class='row'><div class='col-xs-5'></div><div class='col-xs-7' style='margin-top: 4px;'><div class='col-xs-12' style='font-size: 10px;'>"
			+ updatedTS
			+ "</div></div></div></div></div><footer class='weather-category' style ='padding-bottom: 7px;'><ul><li class='active' style='width:33%'><h5>Backlog</h5><span id='bl_span'>"
			+ commaSeparateNumber(backlog)
			+ "</span></li><li style='width:33%'><h5>Shipped</h5><span id='sh_span'>"
			+ commaSeparateNumber(shipped)
			+ "</span></li><li style='width:33%'><h5>Canceled</h5><span id='cn_span'>"
			+ commaSeparateNumber(cancel)
			+ "</span></li></ul></footer></section></div>";
			$('#lfc_area').after(htmlChunk);
		}

		function createRDCNodeModule(description,average,backlog,shipped,cancel, updatedTS){

			desc_id="RDC";

			//html to create 'weather' module. (Shows fulfillment performance)
			var htmlChunk = "<div class='col-md-3'><section class='panel' id='weatherNode'><div class='weather-bg' style='background: #373D61'><div class='panel-body' id='no_padding'><div class='row'><div class='col-xs-5'><i class='fa fa-truck'></i>RED</div><div class='col-xs-7'><div class='degree' style='font-size: 50px;'>"+average+"</div><div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div></div></div><div class='row'><div class='col-xs-5'></div><div class='col-xs-7' style='margin-top: 4px;'><div class='col-xs-12' style='font-size: 10px;'>"+updatedTS+"</div></div></div></div></div><footer class='weather-category' style ='padding-bottom: 7px;'><ul><li class='active' style='width:33%'><h5>Backlog</h5><span id='bl_span'><button id="+desc_id+" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>"+commaSeparateNumber(backlog)+"</button></span></li><li style='width:33%'><h5>Shipped</h5><span id='sh_span'>"+commaSeparateNumber(shipped)+"</span></li><li style='width:33%'><h5>Canceled</h5><span id='cn_span'>"+commaSeparateNumber(cancel)+"</span></li></ul></footer></section></div>";

			$('#store_rdc_area').after(htmlChunk);
		}

		function createSTORENodeModule(description,average,backlog,shipped,cancel, updatedTS){

		 if(description == "STORES"){
				desc_id = "STORE";
			}else {
				desc_id = "0";
					};

			//html to create 'weather' module. (Shows fulfillment performance)
			var htmlChunk = "<div class='col-md-3'><section class='panel' id='weatherNode'><div class='weather-bg' style='background: #35D65C'><div class='panel-body' id='no_padding'><div class='row'><div class='col-xs-5'><i class='fa fa-building-o'></i>"+description+"</div><div class='col-xs-7'><div class='degree' style='font-size: 50px;'>"+average+"</div><div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Days</div></div></div><div class='row'><div class='col-xs-5'></div><div class='col-xs-7' style='margin-top: 4px;'><div class='col-xs-12' style='font-size: 10px;'>"+updatedTS+"</div></div></div></div></div><footer class='weather-category' style ='padding-bottom: 7px;'><ul><li class='active' style='width:33%'><h5>Backlog</h5><span id='bl_span'><button id="+desc_id+" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>"+commaSeparateNumber(backlog)+"</button></span></li><li style='width:33%'><h5>Shipped</h5><span id='sh_span'>"+commaSeparateNumber(shipped)+"</span></li><li style='width:33%'><h5>Canceled</h5><span id='cn_span'>"+commaSeparateNumber(cancel)+"</span></li></ul></footer></section></div>";
			$('#store_rdc_area').after(htmlChunk);
		}
		function createBOPUSNodeModule(average, backlog, picked, pending, canceled, updatedTS){
			var desc_id = "BOPUS";

				//html to create 'weather' module. (Shows fulfillment performance)
				var htmlChunk = "<div class='col-md-3'><section class='panel' id='weatherNode'><div class='weather-bg' style='background: #8A8D89'><div class='panel-body' id='no_padding'><div class='row'><div class='col-xs-5'><i class='fa fa-gift'></i>BOPUS</div><div class='col-xs-7'><div class='degree' style='font-size: 50px;'>"+commaSeparateNumber(average)+"</div><div class='col-xs-12' style='margin-top: -15px;font-size: 14px;'>Avg Fulfill Hours</div></div></div><div class='row'><div class='col-xs-5'></div><div class='col-xs-7' style='margin-top: 4px;'><div class='col-xs-12' style='font-size: 10px;'>"+updatedTS+"</div></div></div></div></div><footer class='weather-category' style ='padding-bottom: 7px;'><ul><li class='active' style='width:25%'><h5>Backlog</h5><span id='bl_span'>"+commaSeparateNumber(backlog)+"</span></li><li style='width:25%;'><h5>In Process</h5><span id='pn_span'><button id="+desc_id+" class='btn2 btn-info' data-toggle='modal' data-target='#myModal'>"+commaSeparateNumber(pending)+"</button></span></li><li style='width:25%'><h5>Picked Up</h5><span id='sh_span'>"+commaSeparateNumber(picked)+"</span></li><li style='width:25%'><h5>Canceled</h5><span id='cn_span'>"+commaSeparateNumber(canceled)+"</span></li></ul></footer></section></div>";
				$('#store_rdc_area').after(htmlChunk);
			}
*/
