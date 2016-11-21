<!--  Coding for the new STOREELF storeelf properties  page
@author: 
@Date: -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	ANGULARJS_APP = angular.module('ServerStatisticsApp', []);
	
	
	
	
 	function AppServerStatsController($scope, $http, $log){
		$scope.getAppServerDetails= function() {
			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Utility/ServerStatistics/storeelfprop',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.ServerDetails = data;
			});
		};
	}
</script>
    
<div ng-app="ServerStatisticsApp">
	<div ng-controller="AppServerStatsController">
		<div class="col-md-12">
			<section class="panel">
			<table class="table table-hover table-striped table-condensed" data-ng-init="getAppServerDetails()">
			<thead>
					<tr><th colspan="13" bgcolor=#4DA5AD class = "calloutheader"><center><font size="3" color="white">STOREELF PROPERTIES</font></center></th></tr>
					<tr class="blank_row">
    					<td colspan="3"></td>
					</tr>
					<tr>
				<tr>
					<th bgcolor=#9CD8DD><font color="white">DATABASE</font></th>
					<th bgcolor=#9CD8DD><font color="white">ENVIRONMENT</font></th>
					<th bgcolor=#9CD8DD><font color="white">USER</font></th>
					<th bgcolor=#9CD8DD><font color="white">PORT </font></th>
					<th bgcolor=#9CD8DD><font color="white">HOST</font></th>
					<th bgcolor=#9CD8DD><font color="white">SERVICE NAME</font></th>
					
					</tr>
				</thead>
					<tbody>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;" ><big>OMS</big></span></td>
						<td>{{servers.OMS}}</td>
						<td>{{servers.OMS_DB_USER}}</td>
						<td>{{servers.OMS_PORT}}</td>
						<td>{{servers.OMS_DB_HOST}}</td>
						<td>{{servers.OMS_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>OMSr_Training</big></span></td>
						<td>{{servers.OMSr_Training}}</td>
					
						<td>{{servers.OMSr_Training_DB_USER}}</td>
						<td>{{servers.OMSr_Training_PORT}}</td>
						<td>{{servers.OMSr_Training_DB_HOST}}</td>
						<td>{{servers.OMSr_Training_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>GIV</big></span></td>
						<td>{{servers.GIV}}</td>
						<td>{{servers.GIV_DB_USER}}</td>
						<td>{{servers.GIV_PORT}}</td>
						<td>{{servers.GIV_DB_HOST}}</td>
						<td>{{servers.GIV_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC1</big></span></td>
						<td>{{servers.EFC1}}</td>
						<td>{{servers.EFC1_DB_USER}}</td>
						<td>{{servers.EFC1_PORT}}</td>
						<td>{{servers.EFC1_DB_HOST}}</td>
						<td>{{servers.EFC1_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC2</big></span></td>
						<td>{{servers.EFC2}}</td>
						<td>{{servers.EFC2_DB_USER}}</td>
						<td>{{servers.EFC2_PORT}}</td>
						<td>{{servers.EFC2_DB_HOST}}</td>
						<td>{{servers.EFC2_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC3</big></span></td>
						<td>{{servers.EFC3}}</td>
						<td>{{servers.EFC3_DB_USER}}</td>
						<td>{{servers.EFC3_PORT}}</td>
						<td>{{servers.EFC3_DB_HOST}}</td>
						<td>{{servers.EFC3_DB_SERVICE_NAME}}</td>
						
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC4</big></span></td>
						<td>{{servers.EFC4}}</td>
						<td>{{servers.EFC4_DB_USER}}</td>
						<td>{{servers.EFC4_PORT}}</td>
						<td>{{servers.EFC4_DB_HOST}}</td>
						<td>{{servers.EFC4_DB_SERVICE_NAME}}</td>
						
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC1_PRIM</big></span></td>
						<td>{{servers.EFC1_PRIM}}</td>
						<td>{{servers.EFC1_PRIM_DB_USER}}</td>
						<td>{{servers.EFC1_PRIM_PORT}}</td>
						<td>{{servers.EFC1_PRIM_DB_HOST}}</td>
						<td>{{servers.EFC1_PRIM_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC2_PRIM</big></span></td>
						<td>{{servers.EFC2_PRIM}}</td>
						<td>{{servers.EFC2_PRIM_DB_USER}}</td>
						<td>{{servers.EFC2_PRIM_PORT}}</td>
						<td>{{servers.EFC2_PRIM_DB_HOST}}</td>
						<td>{{servers.EFC2_PRIM_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC3_PRIM</big></span></td>
						<td>{{servers.EFC3_PRIM}}</td>
						<td>{{servers.EFC3_PRIM_DB_USER}}</td>
						<td>{{servers.EFC3_PRIM_PORT}}</td>
						<td>{{servers.EFC3_PRIM_DB_HOST}}</td>
						<td>{{servers.EFC3_PRIM_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>EFC4_PRIM</big></span></td>
						<td>{{servers.EFC4_PRIM}}</td>
						<td>{{servers.EFC4_PRIM_DB_USER}}</td>
						<td>{{servers.EFC4_PRIM_PORT}}</td>
						<td>{{servers.EFC4_PRIM_DB_HOST}}</td>
						<td>{{servers.EFC4_PRIM_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>PROSHIP</big></span></td>
						<td>{{servers.PROSHIP}}</td>
						<td>{{servers.PROSHIP_DB_USER}}</td>
						<td>{{servers.PROSHIP_PORT}}</td>
						<td>{{servers.PROSHIP_DB_HOST}}</td>
						<td>{{servers.PROSHIP_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>FS</big></span></td>
						<td>{{servers.FS}}</td>
						<td>{{servers.FS_DB_USER}}</td>
						<td>{{servers.FS_PORT}}</td>
						<td>{{servers.FS_DB_HOST}}</td>
						<td>{{servers.FS_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>MYSQL</big></span></td>
						<td>{{servers.MYSQL}}</td>
						<td>{{servers.MYSQL_DB_USER}}</td>
						<td>{{servers.MYSQL_PORT}}</td>
						<td>{{servers.MYSQL_DB_HOST}}</td>
						<td>{{servers.MYSQL_DB_SERVICE_NAME}}</td>
					</tr>
					<tr ng-repeat="servers in ServerDetails ">
						<td><span style=" font-size: 5pt; width:80px; display:block;"><big>STOREELF</big></span></td>
						<td>{{servers.STOREELF}}</td>
						<td>{{servers.STOREELF_DB_USER}}</td>
						<td>{{servers.STOREELF_PORT}}</td>
						<td>{{servers.STOREELF_DB_HOST}}</td>
						<td>{{servers.STOREELF_DB_SERVICE_NAME}}</td>
					</tr>
				</tbody>
			</table>
			</section>
	</div>	
		
	</div>
</div>