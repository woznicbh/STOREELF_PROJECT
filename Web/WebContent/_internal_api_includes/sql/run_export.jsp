<script type="text/javascript">
	ANGULARJS_APP = angular.module('InternalApiAPP', ['enterkey']);

 	function ExportSqlFileController($scope, $http, $location){
 		var post_url = STOREELF_ROOT_URI+'/_internal_api/SQL/run_export';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Run Export";

 		$scope.run_export = function() {

			$http({
			    method: 'POST',
			    url: post_url,
			    //data: "k=v",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.sql_ids = data;
			});
		};

		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});
	}
</script>


<div ng-app="InternalApiAPP">
	<div ng-controller="ExportSqlFileController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>X</a></li>
				<li><a>X</a></li>
				<li class="active">X</li>
			</ol>
		</div>
		<div class="col-sm-12">
			<section class="panel">
				<div class="panel-body">
					<form name="form" class="form-horizontal ng-valid ng-dirty" role="form">
						<div class="form-group">
						    <div class="col-sm-offset-2 col-sm-10">
						      <button ng-disabled="isLoading()  || (clicked == true)" class="btn btn-large" ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]" type="submit" ng-click="run_export()">{{button}}</button>
						    </div>
						</div>
					</form>
				</div>
			</section>
		</div>

		<div class="col-sm-12">
		<section class="panel" id="scrollSection">
			<table id= "resultsTable" class="table table-hover table-condensed table-bordered">
				<thead>
					<tr>
						<th>SQL ID</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat-start="item in itemResults" >
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails"><i ng-class="{false: 'fa fa-plus-square', true: 'fa fa-minus-square-o'}[toggle_ItemDetails==true]">
						{{item.ITEM_ID}}</i></td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_WEB_ID}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.SHORT_DESCRIPTION}}</td>
						<td>
							<!-- Button trigger modal -->
							<div id="vb">
								<button class="btn btn-default" data-toggle="modal" data-target="#myModal" id="viewButton">
								  View
								</button>
							</div>
							<script>

							</script>

							<!-- Modal -->
							<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							  <div class="modal-dialog">
							    <div class="modal-content">
							      <div class="modal-header">
							        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
							        <h4 class="modal-title" id="myModalLabel">UPC('s) for Item Selected</h4>
							      </div>
							      <div class="modal-body" >
							      <table id= "resultsTable" class="table table-hover table-condensed table-bordered" style="border: none;" >
							      	<thead>
								      	<tr>
								      		<th style="border-top-left-radius: 5px;border: none;">Name</th>
								      		<th style="border-top-right-radius: 5px;border: none;">UPC</th>
								      	</tr>
							      	</thead>
								      	<tr ng-repeat="item in item.UPC" style="font-size: 12px">
								      		<td>{{item.ALIAS_NAME}}</td>
								      		<td>{{item.ALIAS_VALUE}}</td>
								      	</tr>
							      </table>
							      </div>
							      <div class="modal-footer">
							        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							      </div>
							    </div>
							  </div>
							</div>
						</td>

						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.PRODUCT_LINE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.ITEM_TYPE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_NOMADIC}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.EXTN_SHIP_NODE_SOURCE}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.ONHAND_SAFETY_FACTOR_QTY}}</td>
						<td ng-click="toggle_ItemDetails = !toggle_ItemDetails">{{item.PRIMARY_SUPPLIER}}</td>

					</tr>
					<tr ng-show="toggle_ItemDetails" ng-animate="'box'" class="blueBG">

						<th>Item Image</th>
						<th>Breakable</th>
						<th>Gift Wrap</th>
						<th>Baggage</th>
						<th>Hazardous</th>
						<th>Safety Pct</th>
						<th>Direct Ship</th>
						<th>Ship Alone</th>
						<th>Cage</th>
						<th>Plastic GC</th>
					</tr>
					<tr ng-repeat-end ng-show="toggle_ItemDetails" ng-animate="'box'">
						<td id="{{item.IMG_URL}}">
							<button class="btn btn-default" data-toggle="modal" data-target="#imgModal" id="imgButton">
								Open
							</button>
						</td>
						<td>{{item.EXTN_BREAKABLE}}</td>
						<td>{{item.ALLOW_GIFT_WRAP}}</td>
						<td>{{item.EXTN_BAGGAGE}}</td>
						<td>{{item.IS_HAZMAT}}</td>
						<td>{{item.ONHAND_SAFETY_FACTOR_PCT}}</td>
						<td>{{item.EXTN_DIRECT_SHIP_ITEM}}</td>
						<td>{{item.EXTN_SHIP_ALONE}}</td>
						<td>{{item.EXTN_CAGE_ITEM}}</td>
						<td>{{item.EXTN_IS_PLASTIC_GIFT_CARD}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>