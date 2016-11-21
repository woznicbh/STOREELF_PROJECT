<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBFQ2llSsD5fEqoYpQ1ZqoGMNJGNJ8jPy0"></script>
 <style type="text/css">
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map-canvas { height: 450px }
    </style>
<script type="text/javascript">

//api-key: AIzaSyBFQ2llSsD5fEqoYpQ1ZqoGMNJGNJ8jPy0
	
	//your app MUST be named starting with it's associated Servlet name 
	ANGULARJS_APP = angular.module('SourcingDetailApp', []);
	
	//define your controller, function name MUST end with 'Controller'
 	function DistributionGroupController($scope, $http){
 	

	$scope.distribution_group_details = function() {
		$http({
	    method:		'POST',
	    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/distribution_group_details',
	    //data:		"dist_region=" + search.dist_region + "&ship_node=" + search.ship_node,
	    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
	}).success(function(data, status) {
		$scope.detail_list = data;
	});
	};
	
	$scope.get_group_details = function() {
		$http({
	    method:		'POST',
	    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_group_details',
	    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
	}).success(function(data, status) {
		$scope.detail_list = data;
	});
	};
	
	$scope.getRegionDetails = function(distribution_name) {
		$http({
	    method:		'POST',
	    url: 		STOREELF_ROOT_URI+'/Utility/SourcingDetail/get_region_details',
	    data:		"distribution_name=" + distribution_name,
	    headers:	{'Content-Type': 'application/x-www-form-urlencoded'}
	}).success(function(data, status) {
		$scope.group_detail_list = data;
		$scope.group_details_title = distribution_name;
		
		google.maps.event.addDomListener(window, 'load', $scope.initialize(data.node_list));
	});
	};
	
	
	
	$scope.initialize = function(data) {
        var mapOptions = {
          center: new google.maps.LatLng(38.9982, -94.2141),
          zoom: 4
        };
        
        var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
        var marker = null;
        
        $.each( $scope.group_detail_list.node_list, function(i, data) {
        	
				marker = new google.maps.Marker({
				    position: new google.maps.LatLng(data.LATITUDE, data.LONGITUDE),
				    title:(data.SHIP_NODE)
				});

				// To add the marker to the map, call setMap();
				marker.setMap(map);
				/*  google.maps.event.addListener(marker, 'click', function() {
			        	infowindow.open(map,marker);
			      	  }); */
		});
      };
      	
};
</script>
   
<div ng-app="SourcingDetailApp">
	<div ng-controller="DistributionGroupController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Utility</a></li>
				<li><a>Sourcing Detail</a></li>
				<li class="active">Distribution Group Details</li>
			</ol>
		</div>
		
		<div class="col-sm-4">
		<section class="panel">
		
		
			<table class="table table-hover table-condensed table-bordered table-responsive"  id="distGroupTable" data-ng-init="get_group_details()">
				<thead>
					<tr><th>Distribution Groups</th></tr>
					<tr><th><input class="filter" ng-model="name" placeholder="Type to narrow results"></th></tr>
				</thead>
				<tbody>
					<tr ng-repeat="result in detail_list | filter:name">
						<td>
							<button ng-click="getRegionDetails(result.DISTRIBUTION_RULE_ID)" class="btn btn-default" data-toggle="modal" data-target="#myModal" id="viewButton" ng-model="dist_button_val" ng-value="{{result.DISTRIBUTION_RULE_ID}}">
								  {{result.DISTRIBUTION_RULE_ID}}
								</button>
						</td>
					</tr>
				</tbody>
			</table>
			</section>
			</div>
			
			<div class="col-sm-8">
				<section class="panel">
					<div id="map-canvas" data-ng-init="getNodeLocations()"></div>
				</section>
			</div>
			
			<div class="col-sm-8">
		<section class="panel">
		
		
			<table class="table table-hover table-condensed table-bordered table-responsive"  id="distGroupTable">
				<thead>
					<tr>
						<th>Priority</th>
						<th>Shipment Node</th>
						<th>Location</th>
					</tr>
				</thead>
					<tbody>
					<tr ng-repeat="result in group_detail_list.detail_list | orderBy:['PRIORITY','SHIPNODE_KEY']">
						<td>{{result.PRIORITY}}</td>
						<td>{{result.SHIPNODE_KEY}}</td>
						<td>{{result.CITY}}, {{result.STATE}}</td>
					</tr>
				</tbody>
			</table>
			</section>
			</div>
	</div>
			
		
		
	</div>
	
