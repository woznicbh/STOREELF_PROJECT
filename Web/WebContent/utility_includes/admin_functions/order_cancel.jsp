<style>
/*.modal-header {
	background-color: #58c9f3;
}*/

#cancelModal .modal-footer, #responseModal .modal-footer{
	text-align: center;
}
#cancelModal .modal-header, #errorModal .modal-header{
	background-color: #ff6c60;
}
#errorReason{
	color: #ff6c60;
}
#cancelDescription {
	position: relative;
	padding-bottom: 16px;
}
#cancelDescription textarea{
	width: 80%;
    height: 0;
    opacity: 0;
    font-size: 128%;
    /*padding-bottom: 12px;*/
}
#cancelDescription h5{
	/*opacity: 0;*/
	display:none;
	width: 100%;
	line-height: 0;
	position: absolute;
    bottom: 0;
    left: 0;
}
#cancelModal .modal-body{
	padding-top: 22px;
}
.order-details{
	padding: 10px;
}
.order-details p{
	margin: 0;
	font-size: 120%;
}

#orderInfo {
	display: none;
}

#cancelModalButton{
	margin-right: 6px;
}
.progress{
	height: 0;
	position: relative;
	clear: both;
}
h5.status{
	position: absolute;
	color: rgba(0,0,0,0.8);
	left: 0;
	right: 0;
	font-weight: bold;
	line-height: 2px;
	text-shadow: 0px 0px 2px rgba(255,255,255,0.4);
}

/*TODO GET THIS WORKING WITH NG-CLASS*/
/* maybe just use tr[disabled='true'] or tr[disabled='disabled']*/
tr.false{
	opacity: 0.4;
}
td.small{
	/*width: 46px;*/
	width: 42px;
	text-align:center;
	vertical-align:center;
}
.modal-danger{
	font-size: 16px;
	color: red;
	opacity: .6;
}

th input[type="checkbox"]{
	top: 3px;
	position: relative;
}
/*TODO REMOVE MEEEE*/
.table > thead > tr > th{
	/*background-color: #2A3542 !important;*/
	background-color: #35404d !important;
	/*font-weight: normal;*/
	/*font-size: 11px;*/
}
select{
	font-size: 14px;
	margin: 6px 0 10px;
}
.alert-modal{
	margin-bottom: 0;
	height:0;
	opacity:0;
	padding:0;
}
.alert-modal h5{
	margin: 0;
}
#pagelock{
	position:absolute;
	top:0;
	left:0;
	width:100%;
	height:100%;
	z-index:9999999;
}
.btn-expansion{
}
</style>
<script type="text/javascript">
var efcLookup = new Array();
efcLookup["873"] = "EFC1";
efcLookup["809"] = "EFC2";
efcLookup["819"] = "EFC3";
efcLookup["829"] = "EFC4";
/***************************************************************
**
**				Intialization jQuery
**				All general jquery dom hooks are here
**				(operating in noconflict mode)
**			
****************************************************************/
jQuery(function($) {

	var selected = noscope('selectedCounter');
	$('#cancelReason').change(function() {
		if($('div[data-error="reason"]'));
	  	smoothRemove($('div[data-error="reason"]'));
	  	if($('#cancelReason').val() == 'other') {
	  		$('#cancelDescription textarea').animate({'height':'100px'}).animate({'opacity': '1'}, function(){
		  		$('#cancelDescription h5').fadeIn();
	  		});
	  	} else {
	  		$('#cancelDescription h5').fadeOut(function(){
		  		$('#cancelDescription textarea').animate({'opacity':'0'}).animate({'height':'0'});
	  		});
	  	}
	  	
	});
	//change only fired on focus events
	//using input/propertychange instead
	$('#cancelDescription textarea').bind('input propertychange', function() {
		

		//calculate remaining characters
		var maxLength = parseInt($(this).attr('maxlength'));
		var charLength = $('#cancelDescription textarea').val().length;
		var remainingLength = maxLength - charLength;

		//update the view
		//Pluralization check for aesthetics
		var $remaining = $('#cancelDescription span');
		if(remainingLength == 1) {
			$remaining.text(remainingLength+' character');
		} else {
			$remaining.text(remainingLength+' characters');
		}

	});
	$('#cancelButton').click(function(){
		if(!locked){
			if(validateCancellation()){
				lock();
				$('.progress').animate({height: '24px'});
				cancelOrder();
			}
		}
	});
	$('#resetButton').click(function(){
			requery();
	});
	$('#cancelOrderModalButton').click(function(){
		selectAll(true);
	});
	$('th :checkbox[aria-label="select-all"]').change(function(){
		if(this.checked)
			selectAll(true);
		else
			selectAll(false);
	})
	$('body').on('change', 'td input[type="checkbox"]', function() {
		// update angular
		//TODO: Fix this nonsense
		var numChecked = $('td :checkbox:checked').length;
		$('[ng-controller="OrderCancelController"]').scope().selectedCounter = numChecked;
		$('[ng-controller="OrderCancelController"]').scope().$apply()			

	//	if(numChecked > 0)
	//		$('#cancelModalButton').removeClass('disabled')
	///	else
	//		$('#cancelModalButton').addClass('disabled')
	});
});




/***************************************************************
**
**							Cancellation Methods
**
****************************************************************/
/*  global timer variables  */
var timer;
var timedOut = false; //this will be true after 5 minutes of most recent search
var locked = false;
/*
	NOTE: this was switched to default to true in case there is an order
	that hasn't even been sourced yet but is in OMS. (does that happen?)
*/
function isCancellable(orderRelease){
	/**************** SPECIAL CASE ***********************************/
	//order gets cancelled in oms but not in wmos
	if(parseFloat(orderRelease.STATUS) == 9000 && orderRelease.DO_STATUS != undefined && parseInt(orderRelease.DO_STATUS) <= 130){
		return true
	}
	/*****************************************************************/


	//include logic to make cancellable in wmos if its cancelled in oms
	var stat = parseFloat(orderRelease.STATUS);
	if(stat == 1100|| stat == 1500 || stat == 3200) {
		return true;
	}
	if(orderRelease.NODE_TYPE == 'STORE' || orderRelease.NODE_TYPE == 'RDC'){ 
		if(orderRelease.ORDER_TYPE == 'SHP'){ //SFS
			// if(parseInt(orderRelease.STATUS) > 3350.11){
			if(stat > 3350.03){
	    		return false;
			}
		}
		// else if(parseInt(orderRelease.STATUS) > 3350.03){
		// else if(parseFloat(orderRelease.STATUS) > 3350.11){//BOPUS 3350.03 is invoiced so it should be >= NOT >
		else if(stat > 3350.03 && stat != 3350.11){//BOPUS 3350.03 is invoiced so it should be >= NOT >
	    	return false;
	    }
  //eliminate 3pl and mp (do_status will be undefined)
	}else if((orderRelease.NODE_TYPE != 'DC' && orderRelease.NODE_TYPE != 'RDC') || orderRelease.DO_STATUS == undefined || parseInt(orderRelease.DO_STATUS) > 130) { //EFC
	    return false
	}
  return true
}

/*	checks 5 minute timeout and reason required 	*/
function validateCancellation(){
	if(!timedOut){
		//check fraud reason is checked using html5 validator
		if($('#cancelReason')[0].checkValidity()){
			return true;
		}else{
			modalMessage('Select a reason for cancelling releases', 'reason');
		}
	}else{
		//refresh modal or spawn new modal
		modalMessage('screen timed out, requerying in <span id="countdown">5</span> seconds');
		var countdown = setInterval(function(){
			$("#countdown").html(parseInt($("#countdown").html())-1);
		}, 1000);
		setTimeout(function(){
			clearInterval(countdown);
			requery()	
			$('.alert-info').remove();
		}, 5000);
		return false;
	}
}
function getCancelReason() {
	return $('#cancelReason').val();
}
function getCancelDescription() {
	var cancelDescription = $("#cancelDescription textarea").val();

	//check for null
	if(!cancelDescription || $('#cancelReason').val() != "other") {
		cancelDescription = "";
	}
	return cancelDescription;
}

// function getOrderNumber
function cancelOrder() {
		var tmp = genJson();
		
		//unpack result
		omsReleases = tmp[0];
		wmosOrders = tmp[1];

		//these were added to handle weird edge case where an order is
		//cancelled in OMS but must be cancelled in WM.  The OMS cancel
		//will fail at the webservice, so orders cancelled in OMS must not
		//be included. 
		var omsCancelled = tmp[2];
		//TODO check and make sure these dont need to be global,
		//and need to be checked post requery for cancelvalidate workaround
		wmosCancelled = tmp[3];//cancelled in OMS

		/***************************************************************
		**
		**						    Cancel in OMS	
		**
		****************************************************************/
		progressUpdate(40, "Cancelling in OMS...");
  	if(omsReleases.length > 0){
      $.ajax({
          type: "get",
          //the url where you want to sent the userName and password to
          url: STOREELF_ROOT_URI+'/Utility/AdminFunctions/order_cancel_api',
          contentType: 'application/json',
          dataType: 'text',
          //json object to sent to the authentication url
          data: {
          	location :"oms", 
          	tk : getTk(), 
          	reason_name : getCancelReason(),
          	reason_description : getCancelDescription(),
          	cancel_all : noscope('cancelAll'),
          	order_no : omsReleases[0].sales_order_no,
          	json: JSON.stringify(omsReleases)
          },
          success: function (data) {
        	progressUpdate(60, "Cancelled in OMS");
          	// console.log(data);
			/***************************************************************
			**
			**						    Cancel in WMOS	
			**
			****************************************************************/
			
			if(wmosOrders.length > 0){
				progressUpdate(75, "Cancelling in WMoS...");
				$.ajax({
			        type: "get",
			        //the url where you want to sent the userName and password to
			        url: STOREELF_ROOT_URI+'/Utility/AdminFunctions/order_cancel_api',
			        contentType: 'application/json',
			        dataType: 'text',
			        //json object to sent to the authentication url
			        data: {
			        	location:"wmos", 
			        	tk : getTk(),
			        	reason_name : getCancelReason(),
			        	reason_description : getCancelDescription(),
			        	order_no : omsReleases[0].sales_order_no,
			        	json:JSON.stringify(wmosOrders)
			        },
			        success: function (data) {
			        	//if its a partial or full half-cancel (WM and not OMS)
			        	//wrap everything up and call verify success after that WM call
			        	
			        	if(wmosCancelled.length == 0) {
			        		cancelWrapup(data);
				        } else {
				        	progressUpdate(80, "handling half cancel in WMoS...");
				        }
			        },
			        error: function(data){
			        	cancelFailure("Failed to cancel in WMoS");
			        	// console.log(data);	
			        }
			    });
			}else{
				cancelWrapup(data);
			}
          },
          error: function(data){
			cancelFailure('Failed to cancel in OMS') ;        	
          	// console.log(data);	
          }
      });
    }
    if(omsCancelled.length > 0 && omsCancelled.length != wmosCancelled.length && (omsCancelled.length != 0 && wmosCancelled.length != 0)){
		$.ajax({
			type: "get",
			//the url where you want to sent the userName and password to
			url: STOREELF_ROOT_URI+'/Utility/AdminFunctions/order_cancel_api',
			contentType: 'application/json',
			dataType: 'text',
			//json object to sent to the authentication url
			data: {
				location:"wmos", 
				tk : getTk(),
				reason_name : getCancelReason(),
				reason_description : getCancelDescription(),
				order_no : omsCancelled[0].sales_order_no,
				json:JSON.stringify(wmosOrders)
			},
			success: function (data) {
				
				cancelWrapup(data);
			},
			error: function(data){
				cancelFailure("Failed to cancel in WM");
			}
		});
	}

}	
function fakeCancel(){
	$("input:checked").each(function(){
		var $children = $(this).parent().parent().children();
		var status = $children.get(8);
		var status_name = $children.get(9);
		var do_status = $children.get(10);
		status.innerHTML="9000.00";
		status_name.innerHTML="Cancelled";
		do_status.innerHTML="200";
		$(this).trigger('click');
		$(this).attr('disabled','disabled');
		$(this).parent().parent().addClass("false");
	});	
}

function cancelWrapup(data){
			progressUpdate(100, "Cancelled in WMoS");	
}
	
	function cancelFailure(reason) {
		$('.progress').removeClass('active');
		$('#cancelModal').modal('hide');
		$("#errorReason").text(reason);
		$('#errorModal').modal('show');
		modalReset();
		unlock();
	}
	function getTk() {
		return $('.username').text().trim();
	}
	function genJson() {
		var releases = noscope('orderReleaseResults');
		var omsReleases = [];
		var wmosReleases = [];
		var omsCancelled = [];
		var wmosCancelled = [];
		var index, release;
		var cancelReason = $('#cancelReason').val();
		var completeOrderCancel = noscope('orderCancellable');
		var tk = getTk();
		$('td :checkbox:checked').each(
				function() {
					//this fails because of hidden table rows
					//find actual release index
					releaseIndex = $("tr[ng-repeat-start]").index(
							$(this).parent().parent()[0])
					release = releases[releaseIndex]; //get row index and lookup 
					if (release.TC_ORDER_ID != undefined) {//exists in wmos
						if (release.STATUS == "9000.00") {
							wmosCancelled.push({
								tc_order_id : release.TC_ORDER_ID,
								shipnode_key : parseInt(release.SHIPNODE_KEY)
							});
						} else {
							wmosReleases.push({
								tc_order_id : release.TC_ORDER_ID,
								shipnode_key : parseInt(release.SHIPNODE_KEY),
								order_array : release.ORDER_ARRAY
							});
						}
					}
					/***********************SPECIAL CASE*********************************/
					//only add it to oms if its not already cancelled
					if (release.STATUS == "9000.00") {
						omsCancelled.push({
							sales_order_no : release.SALES_ORDER_NO,
							release_no : release.RELEASE_NO,
							cancel_reason : cancelReason
						});
					} else {
						omsReleases.push({
							sales_order_no : release.SALES_ORDER_NO,
							release_no : release.RELEASE_NO,
							order_array : release.ORDER_ARRAY,
							cancel_reason : cancelReason
						});
					}
					/********************************************************************/
				});
		//attach general order information
		omsReleases[0]['completeOrderCancel'] = completeOrderCancel;
		omsReleases[0]['cancelReason'] = cancelReason;

		//get cancel description
		// var cancelDescription = $("#cancelDescription textarea").val();
		// if(cancelDescription) {
		// 	omsReleases[0]['cancelDescription'] = cancelDescription;
		// } else {
		// 	omsReleases[0]['cancelDescription'] = "";
		// }
		return [ omsReleases, wmosReleases, omsCancelled, wmosCancelled ];
	}
	/***************************************************************
	 **
	 **							Locking
	 **				Prevents user from sending through multiple
	 **				cancellations. prompts when leaving page...
	 **			
	 ****************************************************************/
	function lock() {
		$('body').prepend('<div id="pagelock"></div>')
		locked = true;
		//prompt before leaving page
		window.onbeforeunload = function() {
			return "You are currently cancelling order releases...";
		}
	}

	function unlock() {
		locked = false;
		$("#pagelock").remove();
		window.onbeforeunload = null;
	}

	/***************************************************************
	 **
	 **							Progress Bar Animation
	 **
	 ****************************************************************/
	function progressUpdate(percentage, text) {
		var $bar = $('.progress-bar');

		//update text
		$('.status').html(text)

		//update progress bar
		if (percentage >= 100) {
			$bar.width('100%');
			setTimeout(function() {
				$('.progress').removeClass('active');
				//TODO add in the response update method
				$('#cancelModal').modal('hide');
				$('#responseModal').modal('show');
				unlock();
			}, 2000);

		} else {
			$bar.width(percentage + '%');
		}
	}

	/***************************************************************
	 **
	 **								Modal Utilities
	 **
	 ****************************************************************/
	/* 
	 generate alert for inside modal
	 err is optional, if undefined it will default to an info alert
	 */
	function modalMessage(msg, err) {
		if (!err) {
			$('#cancelModal .modal-body').append(
					'<div class="alert alert-modal alert-info"><h5>' + msg
							+ '</h5></div>');
		} else {
			$('#cancelModal .modal-body').append(
					'<div data-error="' + err + '" class="alert alert-modal alert-danger"><h5>'
							+ msg + '</h5></div>');
		}
		//animate in the alert
		$('.alert-modal').animate({
			height : '45px',
			padding : '15px'
		}).animate({
			opacity : 1
		});
	}

	function modalReset(callback) {
		if (typeof callback === "undefined") {
			callback = function() {
			};
		}

		$('#cancelModal').modal('hide');

		//reset dropdown 
		$("#cancelReason").val(null);
		$("#cancelReason").change(); //manually trigger change event

		//fix for unselecting checkboxes before modal is hidden
		if ($('#responseModal').is(":visible")) {
			$('#responseModal').on('hidden.bs.modal', function() {
				callback();
			});
		} else {
			callback();
		}

		//Reset reason dropdown to placeholder
		$('#cancelReason').val($('#cancelReason option:first').val());

		//reset progress bar
		progressUpdate(5);
		$('.progress').css({
			height : 0
		});

		//hide ALL alerts
		$(".alert").hide().remove()

	}

	function pageReset() {
		//reset timer
		startTimer();

		//reset modal
		modalReset(function() {
			//uncheck active checkboxes AFTER modal is hidden
			selectAll(false);
			//handle select all (bugfix)
			$(":checked").each(function() {
				$(this).trigger('click');
			});
			//$('[ng-controller="OrderCancelController"]').scope().$apply()
		});
	}

	/* smoothly fade out and pull jqeury element from the dom */
	function smoothRemove(el) {
		el.animate({
			opacity : 0
		}).animate({
			height : 0,
			margin : 0,
			padding : 0
		}, function() {
			$(this).remove();
		});
	}
	/***************************************************************
	 **
	 **											Utilities
	 **
	 ****************************************************************/

	/* grab angular variable */
	function noscope(variable) {
		return $('[ng-controller="OrderCancelController"]').scope()[variable];
	}
	/*	Grab username for reporting		*/
	function getUsername() {
		return $('.username').text().trim();
	}

	/*
	 initialize 5 minute timer to prevent the cancellation from being 
	 sent based on stale data.  Called in the success function for
	 any searching.
	 */
	function startTimer() {
		timedOut = false;
		clearTimeout(timer)
		timer = setTimeout(function() {
			timedOut = true
		}, 300000);
		// timer = setTimeout(function(){timedOut = true}, 3000);
	}

	function requery() {
		pageReset();
		$('[ng-controller="OrderCancelController"]').scope()
				.searchOrderReleases(noscope('search'));

	}

	/***************************************************************
	 **
	 **											Select All
	 **
	 ****************************************************************/
	function selectAll(check) {
		$('td :checkbox').each(function() {
			if (check) {
				if (!this.checked)
					$(this).trigger('click'); //using click to trigger change
			} else {//uncheck
				if (this.checked)
					$(this).trigger('click'); //using click to trigger change
			}
		});
		//firefox fix
		//$('[ng-controller="OrderCancelController"]').scope().$apply()
	}

	$('#backButton').click(function() {

		$('#cancelReason').html(null);
	});
</script>
<script type="text/javascript">
 	ANGULARJS_APP = angular.module('AdminFunctionsOrderCancelApp', ['search', 'enterkey']);
	var cancellableCount = 0;
	var totalCount = 0;
	var $my_scope = null;
 	function OrderCancelController($scope, $http, $location, STOREELFSearchService){
 		$my_scope = $scope;
 		var post_url = STOREELF_ROOT_URI+'/Utility/AdminFunctions/order_cancel';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.cancelAll = false;
 		$scope.button = "Search";
 		$scope.selectedCounter = 0;
 		$scope.showCancelAll = function(){
 			if ((totalCount == cancellableCount && totalCount != 0) && ($scope.selectedCounter == 0 || totalCount == $scope.selectedCounter)){
			return false;
		} else {
			return true;
		}};
		
 		$scope.showCancelSelected = function(){
 			if (($scope.selectedCounter > 0 && $scope.selectedCounter != totalCount) || (totalCount != cancellableCount && cancellableCount != 0 && $scope.selectedCounter > 0)) {
			return false;
		} else {
			return true;
		}};
		$scope.showSelectAll = function(){
			if((cancellableCount == 0)){
				return true;
			} else {
				return false;
			}
		};

		$scope.searchOrderReleases = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			pageReset();
			$http({
		    method: 'POST',
		    url: post_url,
		    data: "orderReleaseNumber=" + search.orderReleaseNumber,
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				startTimer();
		 		cancellableCount = 0;
		 		totalCount = 0;
				$scope.orderReleaseResults = data.ORDERS;
				//$scope.getSearchHistory();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.cancelAll = false;
		 		$scope.button = "Search";
		 		
		 		/***************************************************************
		 		**
		 		**							search cancellation logic!
		 		**										order & release
		 		**
		 		***************************************************************/
		 		if($scope.orderReleaseResults.length > 0)
		 			$scope.orderCancellable = true;
		 		else
		 			$scope.orderCancellable = false;
		 		//TODO convert to native for loop (it's way faster)
		 		angular.forEach($scope.orderReleaseResults, function (orderRelease, index) {
		 			orderRelease.cancellable = isCancellable(orderRelease);
		 			if(orderRelease.cancellable == true){
		 				cancellableCount++;
		 			}
		 			totalCount++;
		 		});
			});
		};

		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

		//$scope.getSearchHistory = function(){
		//set search history
		//	STOREELFSearchService.setRequestUri(post_url);
		//	STOREELFSearchService.getHistory();
		//	STOREELFSearchService.injectIntoScope($scope);
		//	$scope.search_history_data = STOREELFSearchService.getData();
		//};

		//$scope.autoSearch = function(hash){
		//	$scope.getSearchHistory();
 		//	$scope.searchOrderReleases(hash);
 		//	STOREELFSearchService.autoFillForm($scope.search, hash);
 		//};
	}
</script>
<div ng-app="AdminFunctionsOrderCancelApp">
	<div ng-controller="OrderCancelController">
			<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Admin Functions</a></li>
				<li class="active">Order Cancel</li>
			</ol>
		</div>

		<div class="col-sm-12">

		<section class="panel">
		<header class="panel-heading"></header>
		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchOrderReleases(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Order No</label>
			    <div class="col-sm-3">
			      <input name="orderReleaseNumber" class="form-control" ng-model="search.orderReleaseNumber" onclick="" value="2754900428" ng-init="search.orderReleaseNumber=''" type="text" placeholder="Order No" required/>
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]" type="submit" ng-click="searchOrderReleases(search)">{{button}}</button>
				<!--<div class="btn-group">
						<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
						<ul role="menu" class="dropdown-menu">
							<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
							<li class="divider"></li>
							<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
						</ul>
					</div>-->
				</div>
			</div> 
		</form>
		</div>
</section>
		</div>

		<div class="col-sm-12">
		<h4> Order Releases</h4>
		<section class="panel">
			<table class="table .table-striped table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th colspan="2"><input type="checkbox" aria-label="select-all" ng-disabled="showSelectAll()"/>SELECT ALL</th>
						<th>SALES ORDER NO</th>
						<th>ORDER DATE</th>
						<th>RELEASE NO</th>
						<th>SHIPNODE KEY</th>
						<th>SHIPNODE TYPE</th>
						<th>OMSe STATUS</th>
						<th>WMOS STATUS</th>
						<th>WMOS ORDER ID</th>
					</tr>
				</thead>
				<tbody>
					<tr class="{{order_release.cancellable}}" ng-repeat-start="order_release in orderReleaseResults">
						<td class="small">
							<input ng-disabled="{{!order_release.cancellable}}" type="checkbox" aria-label="mark_cancel" ng-model="selected">
						</td>
	          			<td class="small" ng-click="toggle_order_releaseResults = !toggle_order_releaseResults">
                        <span class="btn-expansion" ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_order_releaseResults==true]"></span>
						<td><a class="btn btn-xs btn-round btn-info" ng-href="/Utility/OrderManagement/order#/search?orderNumber={{order_release.SALES_ORDER_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{order_release.SALES_ORDER_NO.trim()}}</a></td>
						<td>{{order_release.ORDER_DATE}}</td>
						<td>{{order_release.RELEASE_NO}}</td>
						<td>{{order_release.SHIPNODE_KEY}}</td>
						<td>{{order_release.NODE_TYPE}}</td>
						<td>{{order_release.STATUS}} - {{order_release.STATUS_NAME}}</td>
						<td>{{order_release.DO_STATUS}} - {{order_release.DESCRIPTION}}</td>
						<td><a ng-if='order_release.PICKTICKET_NO.length > 0' class="btn btn-xs btn-round btn-info" ng-href="/Utility/OrderManagement/pickticket#/search?pickticket_no={{order_release.PICKTICKET_NO.trim()}}"><i class="fa fa-briefcase">&nbsp;</i>{{order_release.PICKTICKET_NO.trim()}}</a></td>
					</tr>
					<tr class="{{order_release.cancellable}}" ng-show="toggle_order_releaseResults" ng-animate="'box'" ng-repeat-end>
						<td colspan="2"/>
						<td colspan="8" >
							<table class="table table-hover">
								<tr>
									<th>ITEM ID</th>
									<th>PRIME LINE NO</th>
									<th>STATUS QUANTITY</th>
								</tr>
								<tr ng-repeat="order_line in order_release.ORDER_ARRAY">
									<td><a class="btn btn-xs btn-round btn-info" ng-href="/Utility/OrderManagement/item#/search?items={{order_line.ITEM_ID.trim()}}&webids=&upcs="><i class="fa fa-briefcase">&nbsp;</i>{{order_line.ITEM_ID.trim()}}</a> </td>
									<td>{{order_line.PRIME_LINE_NO}}</td>
									<td>{{order_line.STATUS_QUANTITY}}</td></tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
		<div class="col-sm-12">
				<!-- cancel modal trigger-->
				<button ng-disabled="showCancelSelected()" id="cancelModalButton" type="button" class="btn btn-lg btn-danger " data-toggle="modal" data-target="#cancelModal">Cancel Selected</button>
				<button ng-disabled="showCancelAll()" id="cancelOrderModalButton" type="button" class="btn btn-lg btn-danger " data-toggle="modal" data-target="#cancelModal" ng-click="cancelAll=true">Cancel All</button>
			</div>
			
			<!-- Modal  !(cancellableCount == 0) ||  ---- (selectedCounter < cancellableCount) && -->
			<div id="cancelModal" class="modal fade" role="dialog">
			  <div class="modal-dialog">
			    <!-- Modal content-->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal">&times;</button>
			        <h4 class="modal-title">Are you sure?</h4>
			      </div>
			      <div class="modal-body text-center">
			        <h4>You have selected {{selectedCounter}} order release(s)</h4>
			        <h4 class="modal-danger">This cancellation can not be undone</h4>
			        <select required id="cancelReason">
				      <option value="" disabled selected>Select a reason</option>
			          <option value="fraud">Fraud</option>
			          <option value="misprice">Misprice</option>
			          <option value="remorse">Buyer's Remorse</option>
			          <option value="other">Other</option>
			        </select>
			        <div id="cancelDescription">
			        	<textarea  maxlength="1000" style="clear:both"></textarea>
			        	<h5>You have <span>1000 characters</span> remaining</h5>
					</div>
					<div class="progress progress-striped active ">
						<div class="progress-bar progress-bar-danger"  role="progressbar" aria-valuenow="5" aria-valuemin="0" aria-valuemax="100" style="width: 5%">
						</div>
						<h5 class="status">Verifying status of releases</h5>

					</div>
			      </div>
			      <div class="modal-footer">
			        <button id="cancelButton" type="button" class="btn btn-danger">Cancel Release(s)</button>
			        <button id="backButton" type="button" data-dismiss="modal" class="btn btn-default">Go Back</button>
			      </div>
			    </div>
			  </div>
			</div>

			<div id="responseModal" class="modal fade" role="dialog">
			  <div class="modal-dialog">
			    <!-- Modal content-->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal">&times;</button>
			        <h4 class="modal-title">Cancellation Success</h4>
			      </div>
			      <div class="modal-body text-center">
                    <h4>You have successfully cancelled the order</h4>
			      </div>
			      <div class="modal-footer">
			        <button id="resetButton" type="button" data-dismiss="modal" class="btn btn-default">Okay</button>
			      </div>
			    </div>
			  </div>
			</div>
			<div id="errorModal" class="modal danger fade" role="dialog">
			  <div class="modal-dialog">
			    <!-- Modal content-->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal">&times;</button>
			        <h4 class="modal-title">Cancellation Failure</h4>
			      </div>
			      <div class="modal-body text-center">
			        <h4>Order cancellation was not successful:</h4>
			        <h4 id="errorReason">Something went wrong</h4>
			      </div>
			      <div class="modal-footer">
			        <button id="resetButton" type="button" data-dismiss="modal" class="btn btn-default">Okay</button>
			      </div>
			    </div>
			  </div>
			</div>
	</div>
</div>