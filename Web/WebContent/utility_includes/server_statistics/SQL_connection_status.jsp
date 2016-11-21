<!--  Coding for the new STOREELF Environment statistics page
@author: Shubham Ranka
@Date: 07/02/2014-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	ANGULARJS_APP = angular.module('ServerStatisticsApp', []);

	ANGULARJS_APP.directive('ngEnter', function () {
		console.log('---');
	    return function (scope, element, attrs) {
	        element.bind("keydown keypress", function (event) {
	            if(event.which === 13) {
	                scope.$apply(function (){
	                    scope.$eval(attrs.ngEnter);
	                });

	                event.preventDefault();
	            }
	        });
	    };
	});

	ANGULARJS_APP.filter('orderObjectBy', function() {
		  return function(items, field, reverse) {
		    var filtered = [];
		    angular.forEach(items, function(item) {
		      filtered.push(item);
		    });
		    filtered.sort(function (a, b) {
		    	return (a[field] > b[field]) ? 1 : ((a[field] < b[field]) ? -1 : 0);
		    });
		    if(reverse) filtered.reverse();
		    return filtered;
		  };
		});

 	function STOREELFSQLConnectionStatsController($scope, $http, $log){
		$scope.getAppServerDetails= function() {

			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/ServerStatistics/SQL_connection_status',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.ConnectionDetails = data;
			});
		};

/* 		$scope.refresh = function(){
			if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
				$scope.getAppServerDetails();
				console.log('reloading SQL thread status data');
			}
		};

		$scope.refresh();
		$interval($scope.refresh, 30000); */
	}
</script>

<div ng-app="ServerStatisticsApp">
	<div ng-controller="STOREELFSQLConnectionStatsController">
		<div class="col-md-12">
			<section class="panel">
			<table class="table table-hover table-striped table-condensed" data-ng-init="getAppServerDetails()">
			<thead>
					<tr><th colspan="13" bgcolor=#4DA5AD class = "calloutheader"><center><font size="3" color="white">STOREELF SQL CONNECTION STATUS(will take a few seconds to load)</font></center></th></tr>
					<tr class="blank_row">
    					<td colspan="3"></td>
					</tr>
					<tr>
				<tr>
					<th bgcolor=#9CD8DD><font color="grey">USERNAME</font></th>
					<th bgcolor=#9CD8DD><font color="grey">URL</font></th>
					<th bgcolor=#9CD8DD><font color="grey">CONNECTION-HASH</font></th>
					<!-- <th bgcolor=#9CD8DD><font color="grey">Duration</font></th> -->
				</tr>
				</thead>
					<tbody>
					<tr ng-repeat="con in ConnectionDetails | orderObjectBy:'KEY0'">
						<td>{{con.KEY0}}</td>
						<td>{{con.KEY2}}</td>
						<td>{{con.KEY3}}</td>
						<td>
							<span ng-if="thread.THREAD_ALIVE == 'false' || thread.THREAD_ALIVE == '-'" style="background-color: #ff0000; font-size: 5pt; width:80px; display:block;" class="label  label-danger"><big>SLEEP</big></span>
							<span ng-if="thread.THREAD_ALIVE == 'true'" align="left" style="background-color: #04B404; font-size: 5pt; width:80px; display:block;" class="label label-success"><big>WORKING</big></span>
                        </td>
					</tr>
				</tbody>
			</table>
			</section>
	</div>

	</div>
</div>