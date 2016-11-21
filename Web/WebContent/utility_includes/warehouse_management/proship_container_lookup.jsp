<script type="text/javascript">
	ANGULARJS_APP = angular.module('WarehouseManagementProshipContainerLookupApp', ['search', 'enterkey']);

 	function ProshipLookupController($scope, $http, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/WarehouseManagement/proship_container_lookup';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";
 		
		$scope.searchProship = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "paramOne=" + search.container_id,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				STOREELFSearchService.updateAddressBar('container_id', search.container_id);
				$scope.ProshipContainer = data;
				//$scope.getSearchHistory();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
			});
		};

			// Pams comment
			
		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

		/* $scope.getSearchHistory = function(){
			//-- set search history
			STOREELFSearchService.setRequestUri(post_url);
			STOREELFSearchService.getHistory();
			STOREELFSearchService.injectIntoScope($scope);
			$scope.search_history_data = STOREELFSearchService.getData();
		};
 */
		$scope.autoSearch = function(hash){
			//$scope.getSearchHistory();
 			$scope.searchProship(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="WarehouseManagementProshipContainerLookupApp">
	<div ng-controller="ProshipLookupController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Warehouse Management</a></li>
				<li class="active">Proship Container Lookup</li>
			</ol>
		</div>

		<div class="col-sm-12">
		<section class="panel">

		<div class="panel-body">
		<form name="form" class="form-horizontal" role="form" ng-enter="searchProship(search)">
			<div class="form-group">
				<label class="col-sm-2 control-label">Container ID</label>
			    <div class="col-sm-3">
			      <input name="container_ids" class="form-control" ng-model="search.container_id" onclick="" value="" type="text" ng-init="search.container_id=''" placeholder="Container ID" required/>
			    </div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="button" ng-click="searchProship(search)">{{button}}</button>
					<!-- <div class="btn-group">
						<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
						<ul role="menu" class="dropdown-menu">
							<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
							<li class="divider"></li>
							<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
						</ul>
					</div>--><!-- /btn-group -->
				</div>
			</div>
		</form>
		</div>
</section>
		</div>

		<div class="col-sm-12">
		<section class="panel">
			<table class="table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Container ID</th>
						<th>Shipper Name</th>
						<th>Ship Via</th>
						<th>Tracking No</th>
						<th>Divert Lane</th>
						<th>Server</th>
						<th>Bagger Station</th>
						<th>CreateTS</th>
						<th>ConfirmTS</th>
						<th>ShipViaTS</th>
						<th>Sent 8001</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>{{ProshipContainer.CONTAINER_ID}}</td>
						<td>{{ProshipContainer.SHIPPER_NAME}}</td>
						<td>{{ProshipContainer.SHIPVIA}}</td>
						<td>{{ProshipContainer.TRACKING_NUMBER}}</td>
						<td>{{ProshipContainer.DIVERT_LANE}}</td>
						<td>{{ProshipContainer.SERVER_NAME}}</td>
						<td>{{ProshipContainer.BAGGER}}</td>
						<td>{{ProshipContainer.CREATETS}}</td>
						<td>{{ProshipContainer.CONFIRMTS}}</td>
						<td>{{ProshipContainer.SHIPVIATS}}</td>
						<td>{{ProshipContainer.SENT_8001}}</td>
					</tr>
				</tbody>
			</table>
			</section>
		</div>
	</div>
</div>