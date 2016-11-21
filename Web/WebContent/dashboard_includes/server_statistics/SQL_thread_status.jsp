<!--  Coding for the new STOREELF Environment statistics page
@author: Shubham Ranka
@Date: 07/02/2014-->
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	GLOBAL_SIDEBAR_TOGGLE();
	//$('#sql_timeout').spinner({value:15, step: 15, min: 15, max: 120});
	
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

 	function STOREELFSQLThreadStatsController($scope, $http, $log, $interval){
 		/*   var editor = CodeMirror.fromTextArea('code', {
 			    height: "450px",
 			    parserfile: "/public/js/parsesql.js",
 			    stylesheet: "/public/css/sqlcolors.css",
 			    path: "/js/",
 			    textWrapping: false
 			  }); */
 		$scope.confirmExecuted_flag=false;
		$scope.sql_timeout=15;
 		
		$scope.getSQLJobDetails= function() {

			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/SQL_thread_status',
			    //timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.SQLJobDetails = data;
			});
		};
		
/* 		$scope.getTimeout=function(){
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/get_sql_timeout',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.sql_timeout = data;
				//$('#sql_timeout').spinner({value:$scope.sql_timeout, step: 15, min: 15, max: 120});
			});
		}; */
				
		$scope.getThreadList= function() {

			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/get_thread_list',
			    //timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.ThreadList = data;
			});
		};
		
		$scope.getDBConnectionList= function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/get_db_connection_list',
			    //timeout: 30000,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.ConnectionList = data;
			});
		};
		
		
		$scope.getSQLDetails= function(thread) {
			$scope.SQLDetail_SQL = '';
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/get_thread_details',
			    //timeout: 30000,
			    data: "SQL_ID="+thread.SQLID,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.SQLDetail_SQL_ID = thread.SQLID;
				//$scope.SQLDetail_SQL = data;
				$scope.SQL_EDITOR_DETAIL = data;
			});
		};
		
		$scope.setSQLDetails= function() {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/set_thread_details',
			    //timeout: 30000,
			    data: 
			    	"SQL_ID="+$scope.SQLDetail_SQL_ID
			    	+"&SQL="+$scope.SQL_EDITOR_DETAIL,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				console.log(data);
			});
		};
		
		 
		$scope.SQL_TRIGGER= function(sql) {
			var SQL_TRIGGER="FORCE";
			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/SQL_thread_status',
			    //timeout: 30000,
			    data:	"SQL_TRIGGER=" + SQL_TRIGGER + 
	    		"&SQL_ID=" +sql
	    		,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				console.log(data);
			});
		};	 
		
		$scope.KILL_THREAD= function(sql) {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/kill_thread',
			    //timeout: 30000,
			    data: "SQL_ID="+sql,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.OUTPUT = data;
				sql.SQL_STATUS = 'ERROR';
			});
		};
		
		$scope.CANCEL_SQL= function(sql) {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/cancel_sql',
			    //timeout: 30000,
			    data: "SQL_ID="+sql,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.OUTPUT = data;
			});
		};	
		
		$scope.DISABLE_SQL= function(sql) {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/disable_sql',
			    //timeout: 30000,
			    data: "SQL_ID="+sql,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.OUTPUT = data;
				sql.SQL_STATUS = 'DISABLED';
			});
		};	
		
		$scope.ENABLE_SQL= function(sql) {
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/enable_sql',
			    //timeout: 30000,
			    data: "SQL_ID="+sql,
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.OUTPUT = data;
			});
		};	
		
		$scope.ALL=function(ACTION){
			
			$http({
			    method: 'POST',
			    url: STOREELF_ROOT_URI+'/Dashboard/ServerStatistics/modify_all_sql',
			    //timeout: 30000,
			    data: "SQL_TRIGGER="+ACTION+"&SQL_TRIGGER_TYPE=ALL",
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.OUTPUT = data;
			});
			 
			
			
			/* var sql = "";
			var obj = null;
			for(var key in $scope.SQLJobDetails){
				obj = $scope.SQLJobDetails[key];
				sql = obj.SQLID;
				console.log(obj.SQLID);
				switch (ACTION) {
					case "CANCEL_ALL":	$scope.CANCEL_SQL(sql);		break;
					case "RETRY_ALL":	$scope.ENABLE_SQL(sql);		break;
					case "ENABLE_ALL":	$scope.ENABLE_SQL(sql);		break;
					case "DISABLE_ALL":	$scope.DISABLE_SQL(sql);	break;
					case "FORCE_ALL":	$scope.SQL_TRIGGER(sql);	break;
				default:
					break;
				}
			} */
		};
		
		$scope.loadThreadDetails= function(thread, logType) {			
			$scope.ThreadDetail_SQL_ID = thread.SQLID;
			if(logType == 'error'){
				$scope.ThreadDetail_STACK = thread.ERROR_DETAILS;	
			}else{
				$scope.ThreadDetail_STACK = thread.SUCCESS_DETAILS;
			}			
		};
		
		$scope.confirmExecute = function(func_to_run, params){
			$scope.funcToRun = null;
			$scope.funtToRun_params = null;
			$scope.confirmExecuted_flag=false;
			
			$scope.funcToRun = func_to_run;
			$scope.funtToRun_params = params;
			console.log(params);
		};
		
		$scope.executeFunc = function(isExecuteYes){
			if(isExecuteYes==true){
				$scope[$scope.funcToRun]($scope.funtToRun_params);
				$scope.confirmExecuted_flag=true;
			}else{
				$scope.funcToRun = null;
				$scope.funtToRun_params = null;
				$scope.confirmExecuted_flag=false;
			}			 		
		}; 
		
		
 		$scope.refresh = function(){			
			$scope.getSQLJobDetails();
			//$scope.getDBConnectionList();
			console.log('reloading SQL thread status data');
		};
		
		$scope.refreshFaster = function(){
			$scope.getThreadList();
			console.log('reloading SQL thread status data');
		};

		$scope.refresh();
		$scope.refreshFaster();
		$interval($scope.refresh, 10000);
		$interval($scope.refreshFaster, 5000); 
	}
</script>

<div ng-app="ServerStatisticsApp">
	<div ng-controller="STOREELFSQLThreadStatsController">
		<div class="col-md-12">
		
		
               <!--widget start-->
             <section class="panel">
                 <header class="panel-heading tab-bg-dark-navy-blue">
                     <ul class="nav nav-tabs nav-justified ">
                         <li class="active"><a href="#STOREELF_SQL_STATUS" data-toggle="tab">STOREELF SQL STATUS</a></li>
                         <li><a href="#STOREELF_THREAD_STATUS" data-toggle="tab">STOREELF THREAD STATUS</a></li>
                       <!--   <li><a href="#STOREELF_CONNECTION_STATUS" data-toggle="tab">STOREELF CONNECTION STATUS</a></li>      -->                    
                     </ul>
                 </header>
                 <div class="panel-body">
                     <div class="tab-content tasi-tab">
                         <div class="tab-pane active" id="STOREELF_SQL_STATUS">
                        	<table class="table table-hover table-striped table-condensed" data-ng-init="getSQLJobDetails()">
								<thead>
									<tr>
										<th colspan="13" bgcolor=#4DA5AD class = "calloutheader">
											<center><font size="3" color="white">STOREELF SQL STATUS</font></center>
										</th>
									</tr>
									 
									<tr>
										<td colspan="2">
											<button data-toggle="modal" data-target="#ConfirmPopupModal" class="btn green" ng-click="confirmExecute('ALL', 'ENABLE_ALL')">Enable All <i class="fa fa-plus"></i></button>
											<button data-toggle="modal" data-target="#ConfirmPopupModal" class="btn green" ng-click="confirmExecute('ALL', 'DISABLE_ALL')">Disable All <i class="fa fa-plus"></i></button>
										 </td>										 
										 <td colspan="2">
										 	<button data-toggle="modal" data-target="#ConfirmPopupModal" class="btn green" ng-click="confirmExecute('ALL', 'CANCEL_ALL')">Cancel All <i class="fa fa-plus"></i></button>
										 	<button data-toggle="modal" data-target="#ConfirmPopupModal" class="btn green" ng-click="confirmExecute('ALL', 'RETRY_ALL')">Retry All <i class="fa fa-plus"></i></button>
										 </td>										 
										 <td colspan="4">
										 	<button data-toggle="modal" data-target="#ConfirmPopupModal" class="btn green" ng-click="confirmExecute('ALL', 'FORCE_ALL')">Force Refresh All <i class="fa fa-plus"></i></button>
										 </td>
									</tr>
									 									 
								</thead>
								<thead>
									<tr> 
										<th bgcolor=#9CD8DD><i class="fa fa-refresh"></i></th> 
										<th bgcolor=#9CD8DD>SQL ID</th>
										<th bgcolor=#9CD8DD>Last Started</th>
										<th bgcolor=#9CD8DD>Last Completion</th>
										<th bgcolor=#9CD8DD>Duration</th>
										<th bgcolor=#9CD8DD>Next Execution Time</th>
										<th bgcolor=#9CD8DD>Runs/Refreshes Every</th>
										<th bgcolor=#9CD8DD>&nbsp; &nbsp;&nbsp;&nbsp;JOB STATUS</th>
										<th bgcolor=#9CD8DD>&nbsp; &nbsp;&nbsp;&nbsp;Thread SLEEP/WORKING?</th>
										<!-- <th bgcolor=#9CD8DD>&nbsp; &nbsp;&nbsp;&nbsp;CONNECTION STATUS</th> -->
										<!-- <th bgcolor=#9CD8DD><font color="grey">SERVER STATUS</font></th> -->
									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="sql in SQLJobDetails | orderObjectBy:'SQLID'">
											<%-- <shiro:hasRole name="Administrator">
												<td>
													<button data-toggle="button" class="btn btn-white btn-xs" ng-click="SQL_TRIGGER(thread)"><i class="fa fa-refresh"></i></button>
												</td>
											</shiro:hasRole> --%>
											
											<td>						
												<div class="btn-group">
													<button data-toggle="dropdown" class="btn btn-success dropdown-toggle btn-xs" type="button"> </i><span class="caret"></span></button>
													<ul role="menu" class="dropdown-menu"> 
														       
																<li><a data-toggle="modal" data-target="#SQLDetailPopupModal" ng-click="getSQLDetails(sql)">Edit SQL</a></li>
															<li class="divider"></li>
																<li ng-if="sql.SQL_STATUS == 'RUNNING'"><a data-toggle="modal" data-target="#ConfirmPopupModal" ng-click="confirmExecute('CANCEL_SQL', sql.SQLID)">Cancel Query</a></li>
																<li ng-if="sql.SQL_STATUS == 'ERROR'"><a data-toggle="modal" data-target="#ConfirmPopupModal" ng-click="confirmExecute('ENABLE_SQL', sql.SQLID)">Retry SQL</a></li>																
															<li class="divider"></li>
																<li><a data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql,'success')">Success Log</a></li>
																<li><a data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql,'error')">Error Log</a></li>
															<li class="divider"></li>
																<li ng-if="sql.SQL_STATUS == 'DISABLED'"><a data-toggle="modal" data-target="#ConfirmPopupModal" ng-click="confirmExecute('ENABLE_SQL', sql.SQLID)">Enable SQL</a></li>
																<li ng-if="sql.SQL_STATUS != 'DISABLED'"><a data-toggle="modal" data-target="#ConfirmPopupModal" ng-click="confirmExecute('DISABLE_SQL', sql.SQLID)">Disable SQL</a></li>
																<!-- <li ng-if="sql.THREAD_STATUS == 'RUNNABLE' && sql.THREAD_ALIVE == 'true'"><a ng-click="KILL_THREAD(sql)">Kill Thread</a></li> -->
															<li class="divider"></li>
																<li><a data-toggle="modal" data-target="#ConfirmPopupModal" ng-click="confirmExecute('SQL_TRIGGER', sql.SQLID) "><i class="fa fa-refresh"></i> Force Refresh</a></li>														 
													</ul>
												</div><!-- /btn-group -->						
											</td>
											<td>{{sql.SQLID}}</td>
											<td>{{sql.LAST_EXECUTED}}</td>
											<td>{{sql.LAST_COMPLETED}}</td>
											<td>{{sql.SQL_EXECUTION_TIME}}</td>
											<td>{{sql.NEXT_EXECUTION_TS}}</td>
											<td>{{sql.RUNS_EVERY_TS}}</td>
												
					                        <td>
												<!-- <span ng-if="thread.STATUS == 'ERROR'" style="background-color: #ff0000; font-size: 5pt; width:80px; display:block;" class="label  label-danger"><big>ERROR</big></span> -->
												<!-- <button ng-if="thread.STATUS == 'ERROR' || thread.STATUS == 'SCHEDULED' || thread.STATUS == 'WAITING'" style="background-color: #ff0000; font-size: 5pt; width:80px; display:block;" class="btn" data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(thread)">ERROR</button> -->
												<button ng-if="sql.SQL_STATUS == 'ERROR'" style="background-color: #ff0000; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="btn btn-xs" data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql, 'error')"><big>ERROR</big></button>
					                        	
					                        	<!-- <span ng-if="thread.STATUS == 'DONE' && (thread.THREAD_ALIVE == 'false' || thread.THREAD_ALIVE == '-')" style="background-color: #04B404; font-size: 5pt; width:80px; display:block;" class="label label-success"><big>DONE</big></span> -->
					                        	<button ng-if="sql.SQL_STATUS == 'DONE'" style="background-color: #04B404; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="btn btn-xs" data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql, 'success')"><big>DONE</big></button>
					                        	
					                        	<!-- <span ng-if="thread.STATUS == 'SCHEDULED'" style="background-color: #FFBF00; font-size: 5pt; width:80px; display:block;" class="label  label-warning"><big>SCHEDULED</big></span> -->
					                        	<button ng-if="sql.SQL_STATUS == 'SCHEDULED'" style="background-color: #FFBF00; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="btn btn-xs" data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql, 'success')"><big>SCHEDULED</big></button>
					                        	
					                        	<span ng-if="sql.SQL_STATUS == 'RUNNING'" style="background-color: #58c9f3; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="label  label-info"><big>RUNNING</big></span>
					                        	
					                        	<span ng-if="sql.SQL_STATUS == 'DISABLED'" style="font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="label label-inverse"><big>DISABLED</big></span>
					                        	
					                        	<!-- <span ng-if="thread.STATUS == 'WAITING'" style="background-color: #bec3c7; font-size: 5pt; width:80px; display:block;" class="label  label-info"><big>WAITING</big></span> -->
					                        	<!-- <button ng-if="sql.SQL_STATUS == 'WAITING'" style="background-color: #bec3c7; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="btn btn-xs" data-toggle="modal" data-target="#threadDetailPopupModal" ng-click="loadThreadDetails(sql)"><big>WAITING</big></button> -->
					                        </td>
											<td>
												<span ng-if="sql.THREAD_STATUS == '-'" style="background-color: #ff0000; font-size: 5pt; width:80px; display:block;" class="label  label-danger"><big>SLEEP</big></span>
												<span ng-if="sql.THREAD_STATUS == 'RUNNABLE' && (sql.THREAD_ALIVE == 'false' || sql.THREAD_ALIVE == '-')" style="background-color: #ff0000; font-size: 5pt; width:80px; display:block;" class="label label-inverse"><big>INSPECT</big></span>							
												<span ng-if="sql.THREAD_STATUS == 'RUNNABLE'" align="left" style="background-color: #04B404; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="label label-success"><big>WORKING</big></span>
					                        </td>
					                        <td>
					                        	<span ng-if="sql.SQL_CONNECTION_CLOSED == 'true'" style="background-color: #ff0000; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="label  label-info"><big>CLOSED</big></span>
					                        	<span ng-if="sql.SQL_CONNECTION_CLOSED == 'false'" style="background-color: #58c9f3; font-size: 5pt; font-weight: bolder; width:80px; display:block;" class="label  label-info"><big>OPEN</big></span>
					                        </td>
										</tr>
									</tbody>
								</table> 
 
                         </div>
                         <div class="tab-pane" id="STOREELF_THREAD_STATUS">
                             
                             
                                                     			<table class="table table-hover table-striped table-condensed" data-ng-init="getThreadList()">
								<thead>
										<tr><th colspan="13" bgcolor=#4DA5AD class = "calloutheader"><center><font size="3" color="white">STOREELF THREAD STATUS</font></center></th></tr>
										<tr class="blank_row">
					    					<td colspan="3"></td>
										</tr>
										<tr>
									<tr>
										<%-- <shiro:hasRole name="Administrator"> 
											<th bgcolor=#9CD8DD><i class="fa fa-refresh"></i></th> 
										</shiro:hasRole> --%>
										<th bgcolor=#9CD8DD>ID</th>
										<th bgcolor=#9CD8DD>NAME</th>
										<th bgcolor=#9CD8DD>PRIORITY</th>
										<th bgcolor=#9CD8DD>STATE</th>
									</tr>
									</thead>
										<tbody>
										<tr ng-repeat="thread in ThreadList | orderObjectBy:'ID'">
											<td>{{thread.ID}}</td>
											<td>{{thread.NAME}}</td>
											<td>{{thread.PRIORITY}}</td>
											<td>{{thread.STATE}}</td>
										</tr> 
									</tbody>
								</table> 
                             
                         </div>
                         
                       <%--   <div class="tab-pane" id="STOREELF_CONNECTION_STATUS">
                             
                             
                                <table class="table table-hover table-striped table-condensed" data-ng-init="getConnectionList()">
								<thead>
										<tr><th colspan="13" bgcolor=#4DA5AD class = "calloutheader"><center><font size="3" color="white">STOREELF CONNECTION STATUS</font></center></th></tr>
										<tr class="blank_row">
					    					<td colspan="3"></td>
										</tr>
										<tr>
									<tr>
										<shiro:hasRole name="Administrator"> 
											<th bgcolor=#9CD8DD><i class="fa fa-refresh"></i></th> 
										</shiro:hasRole>
										<th bgcolor=#9CD8DD>ID</th>
										<th bgcolor=#9CD8DD>STATEMENT_CLOSED</th>
										<th bgcolor=#9CD8DD>CONNECTION_CLOSED</th>
										<th bgcolor=#9CD8DD>CONNECTION_VALID</th>
									</tr>
									</thead>
										<tbody>
										<tr ng-repeat="connection in ConnectionList | orderObjectBy:'ID'">
											<td>{{connection.ID}}</td>
											<td>{{connection.STATEMENT_CLOSED}}</td>
											<td>{{connection.CONNECTION_CLOSED}}</td>
											<td>{{connection.CONNECTION_VALID}}</td>
										</tr> 
									</tbody>
								</table> 
                             
                         </div> --%>
                         
                        <!--  <div class="tab-pane " id="recent">
                             Recent Item goes here
                         </div> -->
                     </div>
                 </div>
             </section>
             <!--widget end-->
		
		
			<section class="panel">
			</section>
		</div>
		
		<!-- ------------------------------------------------------------------------------------------------------------------------------------ -->
	
		<!-- ############################################################## -->
		<div class="modal fade" id="threadDetailPopupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg"  >
				<div class="modal-content">
					<div class="modal-header">
						<href type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</href>
						<h4 class="calloutheader" id="myModalLabel" ><center>Thread Details: {{ThreadDetail_SQL_ID}}</center></h4>				
					</div>
					<div class="calloutcontent">
						<section class="panel">
							<div style="overflow:auto;height:400px"	>
								<textarea class="ng-binding" style="margin: 0px; width: 100%; min-height: 50vh;color: white;background-color: black;font-family: 'Courier New', Courier;font-size: 10pt;">
									{{ThreadDetail_STACK}}
								</textarea>
							</div>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
		<!-- ############################################################## -->
		
		
		
		<!-- ############################################################## -->
		<div class="modal fade" id="SQLDetailPopupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg"  >
				<div class="modal-content">
					<div class="modal-header">
						<href type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</href>
						<h4 class="calloutheader" id="myModalLabel" ><center>Edit SQL: {{SQLDetail_SQL_ID}}</center></h4>				
					</div>
					<div class="calloutcontent">
						<section class="panel">							
							<form name="form" class="form-horizontal" role="form">
								<textarea id="code" ng-model="SQL_EDITOR_DETAIL" class="ng-binding" style="margin: 0px; width: 100%; min-height: 50vh;color: white;background-color: black;font-family: 'Courier New', Courier;font-size: 10pt;"></textarea>
							</form>	
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal" ng-click="setSQLDetails(SQLDetail_SQL_ID)">Save</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
		<!-- ############################################################## -->
		
		
		<!-- Delete User Modal -->
		<!-- ############################################################## -->
		<div class="modal fade" id="ConfirmPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h5 align="center" class="modal-title" id="myModalLabel">Please Confirm: {{confirmExecute_flag}}</h5>
					</div>
					
							<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"  ng-click="executeFunc(true)" ng-disabled="confirmExecuted_flag==true">Yes</button>
										<button class="btn btn-default" type="button" data-dismiss="modal" ng-disabled="confirmExecuted_flag==true">No</button>
									</div>
								</div>

								<div class="form" ng-show="confirmExecuted_flag==true">
									<section>
										<output>{{OUTPUT}}</output>
									</section>
									<div class="modal-footer">
										<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									</div>
								</div>
							</form>
						</section>
					</div>
					
				</div>
			</div>
		</div>
		<!-- ############################################################## -->
		<!-- End of Delete User  -->
		

	</div>
</div>