<script type="text/javascript">
	ANGULARJS_APP = angular.module('OrderManagementOrderSearchApp', ['search', 'enterkey']);

	function OrderSearchController($scope, $http, $log, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Utility/OrderManagement/customer_email';
 		$scope.isActive = false;
 		$scope.clicked = false;
 		$scope.button = "Search";

		$scope.searchOrders = function(search) {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";
			$http({
			    method: 'POST',
			    url: post_url,
			    data: "orderNumber=" + search.orderNumber ,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$location.search('orderNumber', search.orderNumber);
				$scope.mailnotes=data.mail_notes;
				$scope.getSearchHistory();
				$scope.isActive = false;
		 		$scope.clicked = false;
		 		$scope.button = "Search";
		 		$scope.searchedOrderNo = search.orderNumber;
		 		
			});
			

		};

	
		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

	 $scope.getSearchHistory = function(){
			//-- set search history
			STOREELFSearchService.setRequestUri(post_url);
			STOREELFSearchService.getHistory();
			STOREELFSearchService.injectIntoScope($scope);
			$scope.search_history_data = STOREELFSearchService.getData();
		}; 

		
	
	}
</script>

<div ng-app="OrderManagementOrderSearchApp">
	<div ng-controller="OrderSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Order Management</a></li>
				<li class="active">Customer Email</li>
			</ol>
		</div>

			<div class="col-sm-12">
				<section class="panel">
					<div class="panel-body">
						<form name="form" class="form-horizontal" role="form" ng-enter="searchOrders(search)">
							<div class="form-group">
								<label class="col-sm-2 control-label">Order No</label>
							    <div class="col-sm-3">
									<input name="orderNumber" class="form-control" ng-model="search.orderNumber" value="" type="text" ng-init="search.orderNumber=''" placeholder="Order No" required/>
							    </div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button ng-disabled="form.$invalid || isLoading()  || (clicked == true)" class="btn" ng-class="{true: 'btn-warning', false: 'btn-search'}[isActive]" type="submit" ng-model="search.button" ng-click="searchOrders(search)">{{button}}</button>
								</div>
							</div>
						</form>
					</div>
				</section>
			</div>

<!-- start -->
		<div class="col-sm-12">
			<section class="panel">
			<header class="panel-heading"  style="float: left">
						Customer Email details:
				</header>
				<header class="panel-heading"  style="float: right">
				(Emails sent to Customer)
				</header>
				<center></center><table class="table table-hover table-condensed table-bordered table-responsive">
					<thead>
						<tr>
						    <th>Order No</th>
							<th>Email Type</th>
							<th>Status</th>
							<th>Sent Time</th>
						</tr>
						<tr ng-repeat="order in mailnotes | orderBy:'MAIL' ">
							<td>{{order.ORDERNO}}</td> 
							<td>{{order.MAIL}}</td>
							<td>{{order.STATUS}}</td>
							<td>{{order.TIME}}</td>
							</tr>
						
					</thead>
					
				</table></center>
				</section>
			</div>
<!-- end -->

			
			    </div>
			  </div>
			</div>
	</div>
</div>