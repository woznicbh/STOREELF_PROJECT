<script type="text/javascript">
	ANGULARJS_APP = angular.module('WarehouseManagementPickticketDetailApp', []);

 	function PicketDetailContoller($scope, $http){
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";

		$scope.getPickTicketDetail = function(pickticket) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";

			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/WarehouseManagement/distribution_order_detail',
			    data: "pickticket_control_number=" + pickticket.controlNumber + "&efc_number=" + pickticket.efcNumber ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(result_data, status) {
				//$scope.pickticketDetailResults = result_data;
				//Fixed the broken functionality as JSON references were incorrect --start
				//accordian
				$scope.pickTicketDetailInformation_header				= result_data.UT_Pickticket_Header;		//table
				$scope.pickTicketDetailInformation						= result_data.UT_Pickticket_Detail;		//table

				//accordian
				$scope.cartonHeaderDetailInformation_header				= result_data.UT_Carton_Header;	//table
				$scope.cartonHeaderDetailInformation_detail				= result_data.UT_Carton_Detail;	//table
				$scope.cartonHeaderDetailInformation_type				= result_data.UT_Carton_Type;	//table

				//accordian
				$scope.manifestInformation_header						= result_data.UT_Manifest_Header;		//table
				$scope.manifestInformation_detail						= result_data.UT_Manifest_Detail;		//table

				//accordian
				$scope.outputPickticketHeaderDetailInformation_header	= result_data.UT_Output_Pickticket_Header;		//table
				$scope.outputPickticketHeaderDetailInformation_detail	= result_data.UT_Output_Pickticket_Detail;		//table

				//accordian
				$scope.outputCartonHeaderDetailInformation_header		= result_data.UT_Output_Carton_Header;	//table
				$scope.outputCartonHeaderDetailInformation_detail		= result_data.UT_Output_Carton_Detail;	//table

				//accordian
				$scope.cancelationsInformation							= result_data.UT_Cancels;		//table

				//accordian
				$scope.invoiceInformation								= result_data.UT_Inventory_WM;			//table
				//Fixed the broken functionality as JSON references were incorrect --end
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};
	}
</script>

<div ng-app="WarehouseManagementPickticketDetailApp">
	<!--
		The ngController directive attaches a controller class to the view.
		This is a key aspect of how angular supports the principles behind the Model-View-Controller design pattern.

		ng-controller should be defined as your defined controller above

		@see http://docs.angularjs.org/api/ng/directive/ngController
	-->
	<div ng-controller="PicketDetailContoller">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">Distribution Order Detail</li>
			</ol>
		</div>
		<div class="col-sm-12">
		<section class="panel">

		<div class="panel-body">
			<form name="form" class="form-horizontal" role="form" ng-submit="getPickTicketDetail(pickticket)">
			  <div class="form-group">
			    <label class="col-sm-2 control-label">EFC No</label>
			    <div class="col-sm-3"> <!-- Size the div including the selector to 2 columns only -->
			     <select name="efcNumber" class="form-control" ng-model="pickticket.efcNumber" required>
					<option selected="selected" value="EFC1">EFC1</option>
					<option value="EFC2">EFC2</option>
					<option value="EFC3">EFC3</option>
					<option value="EFC4">EFC4</option>
				</select>
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-2 control-label">Pickticket No</label>
			    <div class="col-sm-3">
			      <input name="controlNumber" class="form-control" maxlength="100" ng-model="pickticket.controlNumber" onclick="" value="" type="text" required/>
			    </div>
			  </div>
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button ng-disabled="form.$invalid || isLoading() || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-click="getPickTicketDetail(pickticket)">{{button}}</button>
			    </div>
			  </div>
			</form>
			</div>
</section>
		</div>
		<!-- ######################################################## -->
			<div class="col-sm-12 panel-group" id="PickticketDetailAccordion">

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailOne">
			          Distribution Order Header and Detail Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailOne" class="panel-collapse collapse in">
			      <div class="panel-body">

			        <table class="table table-hover table-condensed table-bordered table-responsive">
			        	<thead>
							<tr>
								<th>Pickticket No</th>
								<th>Warehouse</th>
								<th>Ecomm Order</th>
								<th>Type</th>
								<th>Priority Code</th>
								<th>Order Date</th>
								<!-- <th>Shipto Name</th> -->
								<th>Total Units</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in pickTicketDetailInformation_header">
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.WHSE}}</td>
								<td>{{row.ECOMM_ORD}}</td>
								<td>{{row.TYPE}}</td>
								<td>{{row.PRTY_CODE}}</td>
								<td>{{row.ORD_DATE}}</td>
								<!-- <td>{{row.SHIPTO_NAME}}</td> -->
								<td>{{row.TOTAL_UNITS}}</td>
								<td>{{row.STATUS}}</td>
							</tr>
						</tbody>
					</table>

			        <table class="table table-hover table-condensed table-bordered table-responsive">
			        	<thead>
							<tr>
								<th>Sequence No</th>
								<th>SKU</th>
								<th>Orig Qty</th>
								<th>Pkt Qty</th>
								<th>Cancelled Qty</th>
								<th>To Be Verfied</th>
								<th>Verfied</th>
								<th>Units Packed</th>
								<th>SPL Instr Code 2</th>
								<th>Status</th>
								<th>Convey Flag</th>
								<th>Chute Assignment</th>
								<th>Carton Type</th>
								<th>User Id</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in pickTicketDetailInformation">
								<td>{{row.PKT_SEQ_NBR}}</td>
								<td>{{row.SKU_ID}}</td>
								<td>{{row.ORIG_QTY}}</td>
								<td>{{row.PKT_QTY}}</td>
								<td>{{row.CANC_QTY}}</td>
								<td>{{row.TO_BE_VERF}}</td>
								<td>{{row.VERF_PAKD}}</td>
								<td>{{row.UNITS_PAKD}}</td>
								<td>{{row.SPL_INSTR_CODE_2}}</td>
								<td>{{row.STATUS}}</td>
								<td>{{row.CONVEY_FLAG}}</td>
								<td>{{row.CHUTE_ASGN}}</td>
								<td>{{row.CARTON_TYPE}}</td>
								<td>{{row.USER_ID}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailTwo">
			          LPN Header, Detail, and Type Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailTwo" class="panel-collapse collapse">
			      <div class="panel-body">
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
<!-- 									<th>Total Qty</th>
									<th>Load No</th> -->
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="row in cartonHeaderDetailInformation_header">
									<td>{{row.CARTON_NBR}}</td>
									<td>{{row.TYPE}}</td>
									<td>{{row.SINGLES}}</td>
									<td>{{row.DIVERT}}</td>
									<td>{{row.CARTON_GRP_CODE}}</td>
									<td>{{row.CHUTE_ASSIGN_TYPE}}</td>
									<td>{{row.CHUTE_ID}}</td>
<!-- 									<td>{{row.TOTAL_QTY}}</td>
									<td>{{row.LOAD_NBR}}</td> -->
								</tr>
							</tbody>
						</table>

			      		<table class="table table-hover table-condensed table-bordered table-responsive">
							<thead>
								<tr>
									<th>Carton No</th>
									<th>Pickticket No</th>
									<th>Sequence No</th>
									<th>SKU</th>
									<th>Carton Sequence No</th>
									<th>To Be Packed</th>
									<th>Packed</th>
									<th>LStatus</th>
									<th>User Id</th>
									<th>Modification Time</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="row in cartonHeaderDetailInformation_detail">
									<td>{{row.CARTON_NBR}}</td>
									<td>{{row.PKT_CTRL_NBR}}</td>
									<td>{{row.PKT_SEQ_NB}}</td>
									<td>{{row.SKU_ID}}</td>
									<td>{{row.CARTON_SEQ_NBR}}</td>
									<td>{{row.TO_BE_PAKD}}</td>
									<td>{{row.PAKD}}</td>
									<td>{{row.LSTATUS}}</td>
									<td>{{row.USER_ID}}</td>
									<td>{{row.MOD_DATE_TIME}}</td>
								</tr>
							</tbody>
						</table>

						<table class="table table-hover table-condensed table-bordered table-responsive">
							<thead>
								<tr>
									<th>Carton No</th>
									<th>Carton Type</th>
									<th>Carton Size</th>
									<th>Actual Container Volume</th>
									<th>Max Container Volume</th>
									<th>Max Container Weight</th>
									<th>Width</th>
									<th>Height</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="row in cartonHeaderDetailInformation_type">
									<td>{{row.CARTON_NBR}}</td>
									<td>{{row.CARTON_TYPE}}</td>
									<td>{{row.CARTON_SIZE}}</td>
									<td>{{row.ACTL_CNTR_VOL}}</td>
									<td>{{row.MAX_CNTR_VOL}}</td>
									<td>{{row.MAX_CNTR_WT}}</td>
									<td>{{row.WIDTH}}</td>
									<td>{{row.HT}}</td>
								</tr>
							</tbody>
						</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailThree">
			          Manifest Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailThree" class="panel-collapse collapse">
			      <div class="panel-body">
			        <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Manifest No</th>
								<th>Manifest Type</th>
								<th>Create Date Time</th>
								<th>Closed</th>
								<th>Status</th>
								<th>Pickup Rec No</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in manifestInformation_header">
								<td>{{row.MANIF_NBR}}</td>
								<td>{{row.MANIF_TYPE}}</td>
								<td>{{row.CREATE_DATE_TIME}}</td>
								<td>{{row.CLOSE_DATE}}</td>
								<td>{{row.STATUS}}</td>
								<td>{{row.PIKUP_REC_NBR}}</td>
							</tr>
						</tbody>
					</table>

					<table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Manifest No</th>
								<th>Carton No</th>
								<th>Create Date Time</th>
								<th>User Id</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in manifestInformation_detail">
								<td>{{row.MANIF_NBR}}</td>
								<td>{{row.CARTON_NBR}}</td>
								<td>{{row.CREATE_DATE_TIME}}</td>
								<td>{{row.USER_ID}}</td>
								<td>{{row.STATUS}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailFour">
			          Output Distribution Order Header and Detail Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailFour" class="panel-collapse collapse">
			      <div class="panel-body">
			         <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Pickticket No</th>
								<th>Ecomm Order</th>
								<th>Batch No</th>
								<th>Create Date Time</th>
								<th>Modification Time</th>
								<th>Processed</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in outputPickticketHeaderDetailInformation_header">
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.ECOMM_ORD}}</td>
								<td>{{row.INVC_BATCH_NBR}}</td>
								<td>{{row.CREATE_DATE_TIME}}</td>
								<td>{{row.MOD_DATE_TIME}}</td>
								<td>{{row.PROC_DATE_TIME}}</td>
								<td>{{row.STATUS}}</td>
							</tr>
						</tbody>
					</table>

					 <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Sequence No</th>
								<th>SKU</th>
								<th>Original Qty</th>
								<th>Pkt Qty</th>
								<th>Cancelled Qty</th>
								<th>Shipped Qty</th>
								<th>Create Date Time</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in outputPickticketHeaderDetailInformation_detail">
								<td>{{row.PKT_SEQ_NBR}}</td>
								<td>{{row.SKU_ID}}</td>
								<td>{{row.ORIG_PKT_QTY}}</td>
								<td>{{row.PKT_QTY}}</td>
								<td>{{row.CANCEL_QTY}}</td>
								<td>{{row.SHPD_QTY}}</td>
								<td>{{row.CREATE_DATE_TIME}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailFive">
			          Output LPN Header and Detail Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailFive" class="panel-collapse collapse">
			      <div class="panel-body">
			        <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Batch No</th>
								<th>Pickticket No</th>
								<th>Carton No</th>
								<th>Create Date Time</th>
								<th>Modification Time</th>
								<th>Processed</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in outputCartonHeaderDetailInformation_header">
								<td>{{row.INVC_BATCH_NBR}}</td>
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.CARTON_NBR}}</td>
								<td>{{row.CREATE_DATE_TIME}}</td>
								<td>{{row.MOD_DATE_TIME}}</td>
								<td>{{row.PROC_DATE_TIME}}</td>
								<td>{{row.STATUS}}</td>
							</tr>
						</tbody>
					</table>

					<table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Batch No</th>
								<th>Carton No</th>
								<th>Carton Sequence No</th>
								<th>Pickticket No</th>
								<th>Sequence No</th>
								<th>SKU</th>
								<th>Units Packed</th>
								<th>Modification Time</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in outputCartonHeaderDetailInformation_detail">
								<td>{{row.INVC_BATCH_NBR}}</td>
								<td>{{row.CARTON_NBR}}</td>
								<td>{{row.CARTON_SEQ_NBR}}</td>
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.PKT_SEQ_NBR}}</td>
								<td>{{row.SKU_ID}}</td>
								<td>{{row.UNITS_PAKD}}</td>
								<td>{{row.MOD_DATE_TIME}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailSix">
			          Cancellations Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailSix" class="panel-collapse collapse">
			      <div class="panel-body">
			        <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Pickticket No</th>
								<th>Sequence No</th>
								<th>SKU</th>
								<th>Original Qty</th>
								<th>Shipped Qty</th>
								<th>Orig Qty - Shipped Qty</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in cancelationsInformation">
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.PKT_SEQ_NBR}}</td>
								<td>{{row.SKU_ID}}</td>
								<td>{{row.ORIG_PKT_QTY}}</td>
								<td>{{row.SHPD_QTY}}</td>
								<td>{{row.DIFFORIGSHPD}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>

			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
			        <a data-toggle="collapse" data-parent="#PickticketDetailAccordion" href="#collapsePickticketDetailSeven">
			          Invoice Information
			        </a>
			      </h4>
			    </div>
			    <div id="collapsePickticketDetailSeven" class="panel-collapse collapse">
			      <div class="panel-body">
			       <table class="table table-hover table-condensed table-bordered table-responsive">
						<thead>
							<tr>
								<th>Pickticket No</th>
								<th>Sequence No</th>
								<th>SKU</th>
								<th>Sum Units Packed</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="row in invoiceInformation">
								<td>{{row.PKT_CTRL_NBR}}</td>
								<td>{{row.PKT_SEQ_NBR}}</td>
								<td>{{row.SKU_ID}}</td>
								<td>{{row.UNITS_PAKD}}</td>
							</tr>
						</tbody>
					</table>
			      </div>
			    </div>
			  </div>


			</div>
		<!-- ######################################################## -->

	</div>
</div>