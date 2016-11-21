<style>
.button {
	background-color: #4CAF50; /* Green */
}

.btnq {
	background: #ccc;
}

.divider {
	width: 5px;
	height: auto;
	display: inline-block;
}

.btnq:focus {
	background: gray;
}

.rowspanned {
	position: absolute;
	left: 650px;
	width: 100px;
}

.mycontent-left {
	background-color: rgba(255, 0, 0, 0.2);
}
.centered-text{
	text-align: center;
	background-color: rgba(0, 0, 0, 0);
}

.nav-pills>li>a {
	background-color: #D3D3D3;
}

.nav-pills>li>a:hover {
	background-color: #b1b1d4;
}
</style>

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/public/v3/assets/jquery-multi-select/css/multi-select.css" />
<script type="text/javascript">
	var app = angular.module('myApp', []);
	app
			.controller(
					'reprintCtrl',
					function($scope, $http) {
						$scope.firstName = null, $scope.lastName = null
						$scope.shipment_no = null;
						$scope.container_scm = null;
						$scope.item_id = null;
						$scope.quantity = null;
						$scope.buttonText = "Shipment Number/Key Lookup";
						$scope.myVar = false;
						$scope.showInfo = false;
						$scope.printable = false;
						$scope.buttonText2 = "+";
						$scope.searchButton = "Search";
						$scope.searchButton2 = "Manifest";
						$scope.date = new Date();
						$scope.regex = /^[0-9]/;
						$scope.error = null;
						$scope.to = false;
						$scope.states = "";
						$scope.srvclvl = [ "1BusDay", "2BusDay", "Standard",
								"Priority" ]
						//todo
						$scope.start = false;

						$scope.initStates = function() {

							$(document)
									.ready(
											function() {
												$($scope.states)
														.each(
																function(index,
																		item) {
																	var option = $('<option value="'+item[name]+'"></option>');
																	$(
																			'#statelist')
																			.append(
																					option);
																});
											});

						};

						//toggles the button text and changes the search format
						$scope.toggle = function(bool) {
							$scope.reset();
							$scope.myVar = bool;
							$scope.start = true;

						};

						//gets containers
						$scope.searchShipment = function() {
							$scope.reset();
							$scope.searchButton = "Processing...";

							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/reprint_label',
										//timeout: 30000,
										data : "SHIPNO=" + $scope.shipment_no,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data) {

								$scope.results = data;
								$scope.searchButton = "Search"
							});

						};

						//gets stuff in containers
						$scope.searchContainers = function(input) {
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/reprint_label_container',
										//timeout: 30000,
										data : "CONTAINERSCM=" + input,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data) {
								$scope.t2 = true;
								$scope.t3 = false;
								$scope.printable = false;
								$scope.info = null;
								$scope.containers = data;
							});
						};

						//gets the person info
						$scope.drilldown = function(scm) {
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/reprint_label_drilldown',
										//timeout: 30000,
										data : "CONTAINERSCM=" + scm,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data) {
								$scope.t3 = true;
								$scope.printable = false;
								$scope.info = data;

							});
						};

						//webservice call for shipment key/number lookup
						$scope.webservice = function(add, city, first, last,
								full, phone, zip, state, scm, date, len, wid,
								hei, wei, snk, csc) {
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/call_webservice',
										//timeout: 30000,
										data : "A=" + add + "&C=" + city
												+ "&F=" + first + "&L=" + last
												+ "&FULL=" + full + "&P="
												+ phone + "&Z=" + zip + "&S="
												+ state + "&SCM=" + scm + "&D="
												+ date + "&LEN=" + len
												+ "&WID=" + wid + "&HEI=" + hei
												+ "&WEI=" + wei + "&SNK=" + snk
												+ "&CSC=" + csc,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									})
									.success(
											function(data) {
												$scope.showInfo = true;

												var base64regex = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;

												if (base64regex.test(data)) {
													$scope.b64 = data;
													$scope.errormessage = null;
												} else {
													$scope.errormessage = data;
												}

											});
						};

						//webservice call for manual
						$scope.webservice2 = function(fname, address, city,
								state, phone, zip, scm, date, weight, boxSize,
								storeNumber, servicelvl) {
							$scope.searchButton2 = "Processing...";
							$scope.b64 = null;
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/call_webservice2',
										//timeout: 30000,
										data : "A=" + address + "&C=" + city
												+ "&FULL=" + fname + "&P="
												+ phone + "&Z=" + zip + "&S="
												+ state + "&SCM=" + scm + "&D="
												+ date + "&WEI=" + weight
												+ "&BOX=" + boxSize + "&SNK="
												+ storeNumber + "&CSC="
												+ servicelvl,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									})
									.success(
											function(data) {
												$scope.searchButton2 = "Manifest";
												var base64regex = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;

												if (base64regex.test(data)) {
													$scope.b64 = data;
													$scope.errormessage = null;
												} else {
													$scope.errormessage = data;
												}

											});
						};
						
						//webservice call for manual
						$scope.webservice3 = function(toStoreNum,scm, date, weight, boxSize,
								storeNumber, servicelvl) {
							$scope.searchButton2 = "Processing...";
							$scope.b64 = null;
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/call_webservice3',
										//timeout: 30000,
										data : "TSN="+ toStoreNum + "&SCM=" + scm + "&D="
												+ date + "&WEI=" + weight
												+ "&BOX=" + boxSize + "&SNK="
												+ storeNumber + "&CSC="
												+ servicelvl,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									})
									.success(
											function(data) {
												$scope.searchButton2 = "Manifest";
												var base64regex = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;

												if (base64regex.test(data)) {
													$scope.b64 = data;
													$scope.errormessage = null;
												} else {
													$scope.errormessage = data;
												}

											});
						};

						$scope.errorCheck = function(fname, address, city,
								state, phone, zip, scm, date, weight, boxSize,
								storeNumber, servicelvl) {
							var errors = false;

							//null all
							$scope.fnameE = "";
							$scope.addressE = "";
							$scope.cityE = "";
							$scope.stateE = "";
							$scope.phoneE = "";
							$scope.zipE = "";

							$scope.storenumE = "";
							$scope.dateE = "";
							$scope.srvclvlE = "";
							$scope.weightE = "";
							$scope.sizeE = "";

							if (fname == null) {
								errors = true;
								$scope.fnameE = "Please fill out name field";
							}
							if (address == null) {
								errors = true;
								$scope.addressE = "Please fill out address field";
							}
							if (city == null) {
								errors = true;
								$scope.cityE = "Please fill out city field";
							}
							if (state == null) {
								errors = true;
								$scope.stateE = "Please fill out state field";
							}
							if (phone == null) {
								errors = true;
								$scope.phoneE = "Please fill out phone field correctly";
							}
							if (zip == null) {
								errors = true;
								$scope.zipE = "Please fill out zip field correctly";
							}

							if (storeNumber == null) {
								errors = true;
								$scope.storenumE = "Please fill out store number field";
							}
							if (date == "") {
								errors = true;
								$scope.dateE = "Please fill out date field";
							}
							if (servicelvl == null) {
								errors = true;
								$scope.srvclvlE = "Please fill out service level field";
							}
							if (weight == null) {
								errors = true;
								$scope.weightE = "Please fill out weight field";
							}
							if (boxSize == null) {
								errors = true;
								$scope.sizeE = "Please fill out box size field";
							}

							if (!errors) {
								$scope.webservice2(fname, address, city, state,
										phone, zip, scm, date, weight, boxSize,
										storeNumber, servicelvl);
							}

						};
						
						$scope.errorCheck2 = function(toStoreNum, scm, date, weight, boxSize,
								storeNumber, servicelvl) {
							var errors = false;

							//null all
					
							$scope.toStoreNumE = "";
							$scope.storenumE = "";
							$scope.dateE = "";
							$scope.srvclvlE = "";
							$scope.weightE = "";
							$scope.sizeE = "";

							if (toStoreNum == null) {
								errors = true;
								$scope.toStoreNumE = "Please fill out store number field";
							}
							

							if (storeNumber == null) {
								errors = true;
								$scope.storenumE = "Please fill out store number field";
							}
							if (date == "") {
								errors = true;
								$scope.dateE = "Please fill out date field";
							}
							if (servicelvl == null) {
								errors = true;
								$scope.srvclvlE = "Please fill out service level field";
							}
							if (weight == null) {
								errors = true;
								$scope.weightE = "Please fill out weight field";
							}
							if (boxSize == null) {
								errors = true;
								$scope.sizeE = "Please fill out box size field";
							}

							if (!errors) {
								$scope.webservice3(toStoreNum, scm, date, weight, boxSize,
										storeNumber, servicelvl);
							}

						};


						//populates the stores array
						$scope.getStores = function() {

							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/OrderManagement/get_list_of_stores',
										//data:		"paramOne=" + parameter.fieldName,
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data, status) {
								$scope.stores = data;
							});
						};

						//populates the boxes array
						$scope.getBoxes = function() {
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/init_boxes',
										//timeout: 30000,
										data : "TODO",
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data) {
								$scope.boxes = data;

							});
						};

						//populates the boxes array
						$scope.getSrvclvl = function() {
							$http(
									{
										method : 'POST',
										url : STOREELF_ROOT_URI
												+ '/Utility/AdminFunctions/init_srvclvl',
										//timeout: 30000,
										data : "TODO",
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										}
									}).success(function(data) {
								$scope.srvclvl = data;

							});
						};

						//launches print screen in new tab with label only on it
						$scope.print = function() {
							var w = window.open();
							w.document
									.write("<img width=\"400\"  data-ng-src=data:image/png;base64," + $scope.b64 + " src=data:image/png;base64," + $scope.b64 + ">");
							w.print();

						};

						//reset variables
						$scope.reset = function() {
							$scope.printable = false;
							$scope.b64 = null;
							$scope.errormessage = null;

							$scope.t2 = false;
							$scope.t3 = false;
							$scope.containers = null;
							$scope.results = null;
							$scope.info = null;
							$scope.searchButton = "Search"
							
							$scope.weight=null;
							$scope.date=null;
							$scope.servicelvl=null;
							$scope.scm=null;
							$scope.toStore=null;
							$scope.storeNumber=null;
							$scope.boxSize=null;
							$scope.fname=null;
							$scope.address=null;
							$scope.city=null;
							$scope.state=null;
							$scope.phone=null;
							$scope.zip=null;
							
							$scope.fnameE = "";
							$scope.addressE = "";
							$scope.cityE = "";
							$scope.stateE = "";
							$scope.phoneE = "";
							$scope.zipE = "";

							$scope.storenumE = "";
							$scope.dateE = "";
							$scope.srvclvlE = "";
							$scope.weightE = "";
							$scope.sizeE = "";
							$scope.toStoreNumE = "";

						};

						window.onload = function() {
							document.getElementById("date").value = "";
						}

						$scope.toggle2 = function(val) {
							$scope.to = val;
						}

					});
</script>





<div ng-app="myApp" ng-controller="reprintCtrl">





	<div class="col-sm-12">
		<ol class="breadcrumb">
			<li><a>Utility</a></li>
			<li><a>Admin Functions</a></li>
			<li class="active">Reprint Label</li>
		</ol>
	</div>


	<div class="col-sm-1"></div>
	<ul class="nav nav-pills ">

		<li class="active col-sm-2 centered-text"><a ng-click="toggle(false)"
			data-toggle="tab">Shipment Key/Number</a></li>
		<li class="col-sm-2 centered-text"><a ng-click="toggle(true)" data-toggle="tab">Manual</a></li>
	</ul>

	<div class="col-sm-12">

		<section class="panel">





			<div class="panel-body" ng-show="myVar">
				<form name="manual" id="manual"
					class="form-horizontal ng-pristine ng-invalid ng-invalid-required"
					role="form" data-ng-init="getStores(); getBoxes()" method="post">

					
					<div class="form-group">
						<label class="col-sm-5 control-label text-center"><u>From</u></label>
						<label class="col-sm-3 control-label text-center"><u>To</u></label>

						<div class="btn-group" data-toggle="buttons">

							<Label class="btn btn-tab"  
								ng-click="toggle2(true)">Store<input type="radio" ng-click="toggle2(true)" />
							</Label> <Label ng-selected="true" class="btn btn-tab active" 
								ng-click="toggle2(false)">Person<input type="radio" ng-click="toggle2(false)"/>
							</Label>
						</div>


					</div>




					<div class="form-group">
						<label class="col-sm-2 control-label">Store Number</label>
						<div class="col-sm-3">
							<select name="store_number" class="form-control"
								ng-model="storeNumber">
								<option value=""></option>
								<option ng-repeat="store in stores | orderBy:'store_number'"
									ng-value="{{store.store_number}}">{{store.store_number}}
									- {{store.store_description}}</option>
							</select> <span style="color: red" class="help-block">{{storenumE}}</span>

						</div>
						<div ng-show="!to" class="col-sm-2 control-label">First and
							Last Name:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="text" ng-model="fname"
								placeholder="First and Last Name" name="fname"> <span
								style="color: red" class="help-block">{{fnameE}}</span>
						</div>

						<label ng-show="to" class="col-sm-2 control-label">To
							Store Number</label>
						<div ng-show="to" class="col-sm-3">
							<select name="store_number" class="form-control"
								ng-model="toStore">
								<option value=""></option>
								<option ng-repeat="store in stores | orderBy:'store_number'"
									ng-value="{{store.store_number}}">{{store.store_number}}
									- {{store.store_description}}</option>
							</select> <span style="color: red" class="help-block">{{toStoreNumE}}</span>

						</div>



					</div>



					<div class="form-group">
						<label class="col-md-2 control-label">Order Date: </label>
						<div class="col-md-3">
							<div class="input-group input-large form-control"
								data-date-format="MM-dd-yyyy">
								<input id="date" type="text"
									class="form_datetime-meridian form-control" name="date"
									ng-model="date"> <span style="color: red"
									class="help-block">{{dateE}}</span>
							</div>


						</div>
						<div class="mycontent-left"></div>
						<div ng-show="!to" class="col-sm-2 control-label">Address:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="text" ng-model="address"
								placeholder="Address" name="address"> <span
								style="color: red" class="help-block">{{addressE}}</span>
						</div>
					</div>



					<div class="form-group">
						<div class="col-sm-2 control-label">Service Level:</div>
						<div class="col-sm-3">
							<select class="form-control" ng-model="serviceLvl"
								ng-options="x for x in srvclvl" placeholder="Stores"
								name="servicelvl">
							</select> <span style="color: red" class="help-block">{{srvclvlE}}</span>
						</div>
						<div ng-show="!to" class="col-sm-2 control-label">City:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="text" ng-model="city"
								placeholder="City" name="city"> <span style="color: red"
								class="help-block">{{cityE}}</span>
						</div>

					</div>
					<div class="form-group">
						<div class="col-sm-2 control-label">Weight:</div>
						<div class="col-sm-3">
							<input class="form-control" type="text" ng-model="weight"
								placeholder="Weight" name="weight"> <span
								style="color: red" class="help-block">{{weightE}}</span>
						</div>
						<div ng-show="!to" class="col-sm-2 control-label">State:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="text" ng-model="state"
								placeholder="State" list="statelist" name="state"> <span
								style="color: red" class="help-block">{{stateE}}</span>
							<datalist id="statelist">
								<option value="AL">Alabama</option>
								<option value="AK">Alaska</option>
								<option value="AZ">Arizona</option>
								<option value="AR">Arkansas</option>
								<option value="CA">California</option>
								<option value="CO">Colorado</option>
								<option value="CT">Connecticut</option>
								<option value="DE">Delaware</option>
								<option value="DC">District Of Columbia</option>
								<option value="FL">Florida</option>
								<option value="GA">Georgia</option>
								<option value="HI">Hawaii</option>
								<option value="ID">Idaho</option>
								<option value="IL">Illinois</option>
								<option value="IN">Indiana</option>
								<option value="IA">Iowa</option>
								<option value="KS">Kansas</option>
								<option value="KY">Kentucky</option>
								<option value="LA">Louisiana</option>
								<option value="ME">Maine</option>
								<option value="MD">Maryland</option>
								<option value="MA">Massachusetts</option>
								<option value="MI">Michigan</option>
								<option value="MN">Minnesota</option>
								<option value="MS">Mississippi</option>
								<option value="MO">Missouri</option>
								<option value="MT">Montana</option>
								<option value="NE">Nebraska</option>
								<option value="NV">Nevada</option>
								<option value="NH">New Hampshire</option>
								<option value="NJ">New Jersey</option>
								<option value="NM">New Mexico</option>
								<option value="NY">New York</option>
								<option value="NC">North Carolina</option>
								<option value="ND">North Dakota</option>
								<option value="OH">Ohio</option>
								<option value="OK">Oklahoma</option>
								<option value="OR">Oregon</option>
								<option value="PA">Pennsylvania</option>
								<option value="RI">Rhode Island</option>
								<option value="SC">South Carolina</option>
								<option value="SD">South Dakota</option>
								<option value="TN">Tennessee</option>
								<option value="TX">Texas</option>
								<option value="UT">Utah</option>
								<option value="VT">Vermont</option>
								<option value="VA">Virginia</option>
								<option value="WA">Washington</option>
								<option value="WV">West Virginia</option>
								<option value="WI">Wisconsin</option>
								<option value="WY">Wyoming</option>
							</datalist>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-2 control-label">Container Size:</div>
						<div class="col-sm-3">
							<select class="form-control" ng-model="boxSize"
								ng-options="x for x in boxes" placeholder="Box Size"
								name="boxSize">
							</select> <span style="color: red" class="help-block">{{sizeE}}</span>
						</div>
						<div ng-show="!to" class="col-sm-2 control-label">Phone #:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="number" ng-model="phone"
								name="input2" placeholder="Ex: 2428881742" ng-minlength="10"
								ng-maxlength="11" pattern="regex" name="phone"> <span
								style="color: red" class="help-block">{{phoneE}}</span>
						</div>

					</div>
					<div class="form-group">
						<div class="col-sm-2 control-label">Container SCM:</div>
						<div class="col-sm-3">
							<input class="form-control" type="text" ng-model="scm"
								placeholder="Container SCM" name="scm"> <span
								class="help-block">optional</span>
						</div>
						<div ng-show="!to" class="col-sm-2 control-label">Zip Code:</div>
						<div ng-show="!to" class="col-sm-3">
							<input class="form-control" type="number" ng-model="zip"
								placeholder="ZIP" ng-minlength="5" ng-maxlength="5" name="zip">
							<span style="color: red" class="help-block">{{zipE}}</span>
						</div>
					</div>




					<div class="form-group">

						<div class="col-sm-6 " rowspan="5" ng-show="b64!=null">
							<img width="300" data-ng-src=data:image/png;base64,{{b64}}
								src=data:image/png;base64,{{b64}}>
						</div>
						<div class="col-sm-6 " style="color: red" rowspan="5"
							ng-show="errormessage!=null">{{errormessage}}</div>
					</div>



					<div class="form-group">
						<div class="col-md-1"></div>
						<button ng-show="!to" type="submit"
							class="btn  btn-drpdown btn-sm col-md-1"
							ng-click="errorCheck(fname,address,city,state,phone,zip,scm,date,weight,boxSize,storeNumber,serviceLvl)">{{searchButton2}}</button>
						<button ng-show="to" type="submit"
							class="btn  btn-drpdown btn-sm col-md-1"
							ng-click="errorCheck2(toStore,scm,date,weight,boxSize,storeNumber,serviceLvl)">{{searchButton2}}</button>

						<div class="col-md-1"></div>

						<button ng-click="print()" data-toggle="modal" type="button"
							class="btn  btn-drpdown btn-sm col-md-1" ng-disabled="b64==null">Print</button>
						<div class="col-md-1"></div>
						<button type="reset" ng-click="reset()"
							class="btn  btn-drpdown btn-sm col-md-1">Reset</button>
					</div>
				</form>
			</div>



			<div class="form-group panel-body" ng-show="!myVar">
				<div class="col-sm-4">
					<input class="form-control" type=text ng-model="shipment_no"
						placeholder="Shipment Number/Key">
				</div>
				<div class="divider"></div>
				<button type="button" ng-disabled="!(!!shipment_no)" class="btn"
					ng-click="searchShipment()">{{searchButton}}</button>
			</div>
		</section>
		<table ng-show="!myVar"
			class="table table-hover table-striped table-condensed">
			<thead>
				<tr>
					<th colspan="10">Containers</th>
				</tr>
				<tr>
					<th></th>

					<th bgcolor=#9CD8DD>Container SCM</th>
					<th bgcolor=#9CD8DD>Shipnode Key</th>

				</tr>
			</thead>
			<tbody>
				<tr ng-repeat-start="sql in results" ng-click="t2=!t2; t3=false">
					<td><button type="button" class="btn btn-drpdown btn-sm">
							<span
								ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[t2==true]">
							</span>
						</button></td>
					<td>{{sql.container_scm}}</td>
					<td>{{sql.shipnode_key}}</td>
				</tr>

				<tr ng-show="t2==true">
					<td colspan="10">
						<table class="table table-hover table-striped table-condensed">

							<tr>
								<th colspan="10">Items</th>
							</tr>
							<tr ng-show="t2">
								<td colspan="10">
									<table bgcolor=#008000
										class="table table-hover table-striped table-condensed">

										<tr>
											<th colspan="20" bgcolor=#008000>Info</th>
										</tr>

										<tr bgcolor=#66ff33>
											<th></th>
											<th>Address</th>
											<th>City</th>
											<th>State</th>
											<th>Full Name</th>
											<th>Phone</th>
											<th>ZIP</th>
											<th>Container SCM</th>
											<th>Order Date</th>
											<th>Requested Carrier Service Code</th>
											<th>Shipnode Key</th>
											<th>Container Length</th>
											<th>Container Width</th>
											<th>Container Height</th>
											<th>Container Weight</th>
										</tr>

										<tbody>
											<tr ng-repeat="person in sql.PERSON_ARRAY">
												<td><button data-target="#ConfirmPopupModal"
														data-toggle="modal" type="button"
														class="btn btn-drpdown btn-sm"
														ng-click="webservice(person.address, person.city,person.first_name, person.last_name, person.full_name,person.phone, person.zip, person.state, person.scm, person.odate, person.length, person.width, person.height, person.weight, person.snk, person.csc); printable=true">Print</button>
												</td>
												<td>{{person.address}}</td>
												<td>{{person.city}}</td>
												<td>{{person.state}}</td>
												<td>{{person.full_name}}</td>
												<td>{{person.phone}}</td>
												<td>xxxxx</td>
												<td>{{person.scm}}</td>
												<td>{{person.odate}}</td>
												<td>{{person.csc}}</td>
												<td>{{person.snk}}</td>
												<td>{{person.length}}</td>
												<td>{{person.width}}</td>
												<td>{{person.height}}</td>
												<td>{{person.weight}}</td>
											</tr>
										</tbody>
									</table>
								</td>
							</tr>

							<tr>
								<th bgcolor=#9CD8DD></th>
								<th bgcolor=#9CD8DD>Item ID</th>
								<th bgcolor=#9CD8DD>Quantity</th>
								<th bgcolor=#9CD8DD>Description</th>
							</tr>

							<tbody>


								<tr ng-repeat="item in sql.ITEM_ARRAY">
									<td></td>
									<td>{{item.item_id}}</td>
									<td>{{item.quantity}}</td>
									<td>{{item.description}}</td>
								</tr>
							</tbody>

						</table>
					</td>
				</tr>
				<tr ng-repeat-end ng-show="t3">

				</tr>

			</tbody>
		</table>

		<div class="modal fade" id="ConfirmPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-dismiss="modal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h5 align="center" class="modal-title" id="myModalLabel">Are
							you sure you want to Print this Label</h5>
					</div>

					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button ng-show="!printable" class="btn btn-default"
											type="button" ng-click="print()">Yes</button>
										<button ng-show="!printable" class="btn btn-default"
											type="button" data-dismiss="modal">No</button>
									</div>
								</div>
							</form>
						</section>
					</div>

				</div>
			</div>
		</div>




	</div>
</div>