<script type="text/javascript">

ANGULARJS_APP = angular.module('SessionManagementApp', []);

function SessionController($scope, $http){
	var post_url = STOREELF_ROOT_URI+'/Security/SessionManagement/sessionmanagement';
	var userId='';
	$scope.result = 0;
	$scope.isActive = false;
	$scope.clicked = false;
	$scope.button_search = "Search";
	$scope.button_save = "Save";
	$scope.deleteSuccessfull = 0;
	$scope.error = "Error";
	$scope.currentUserId = '';
	
	
	$scope.searchUsers = function(search) {
		$scope.isActive = true;
		$scope.clicked = true;
		$scope.button_search = "Processing...";
		$http({
		    method: 'POST',
		    url: post_url,
		    data: "userID=" + search.userID + "&userName=" + search.userName, 
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.userdetails = data;
			$scope.isActive = false;
			$scope.clicked = false;
			$scope.button_search = "Search";
			//$scope.getSearchHistory();
		});
	};
	
	$scope.deleteUserId = function(user){
		userId = user;
	};
	
	
	$scope.deleteUser = function(){
		$scope.deleteSuccessfull = 0;
		$http({
		method: 'POST',
	    url: STOREELF_ROOT_URI+'/Security/UserManagement/deleteUser_popup',
	    data: "userID=" + userId.USERNAME,
	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.deleteSuccessfull = data;
		});
	};
	
	$scope.addUser = function (add) {
		$scope.isActive = true;
		$scope.clicked = true;
		$scope.button_save = "Processing...";
		$http({
			method : 'POST',
			url: STOREELF_ROOT_URI+'/Security/UserManagement/addUser_popup',
			data: "userID=" + add.userID + "&firstName=" + add.firstName + "&lastName=" + add.lastName,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.result = JSON.parse(data.replace(";",""));
			$scope.isActive = false;
			$scope.clicked = false;
			$scope.button_save = "Save";
			
		});
	}; 
	
	$scope.loadUserGroupData = function (userID) {
		$scope.currentUserId = userID;
		$http({
			method : 'POST',
			url: STOREELF_ROOT_URI+'/Security/UserManagement/groupDetails_popup',
			data: "userID=" + userID,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			$scope.groupdetails = data;
		});
	};
	
	$scope.commitGroups=function () {
		console.log("committing groups...");
		for(var key in $scope.groupdetails){
			var group = $scope.groupdetails[key];
			 if(group.MODIFIED==true){
				if(group.IS_ASSIGNED>0){
					console.log("committing group assignment:"+group.USER_GROUP_KEY);
					$scope.assignGroup($scope.currentUserId,group.USER_GROUP_KEY);
				}else{
					console.log("committing group removal:"+group.USER_GROUP_KEY);
					$scope.unassignGroup($scope.currentUserId,group.USER_GROUP_KEY);
				}
			}
		}
	};
	
	$scope.assignGroup=function (username, user_group_key) {	
		$http({
			method : 'POST',
			url: STOREELF_ROOT_URI+'/Security/UserManagement/assignGroups',
			data: "username=" + username  + "&user_group_key=" + user_group_key,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {		
		});		
	};
	
	$scope.unassignGroup=function (username, user_group_key) {	
		$http({
			method : 'POST',
			url: STOREELF_ROOT_URI+'/Security/UserManagement/unassignGroups',
			data: "username=" + username  + "&user_group_key=" + user_group_key,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			//$scope.assignGroupData = data;			
		});		
	};
	
	$scope.toggleGroupBtn=function(group){
		for(var key in $scope.groupdetails){
			var g = $scope.groupdetails[key];
			if(g.USER_GROUP_KEY == group.USER_GROUP_KEY){
				 
				if(g.IS_ASSIGNED>0){
					g.IS_ASSIGNED=0;
					console.log("removed group:"+g.USER_GROUP_KEY); 	
				}else{
					g.IS_ASSIGNED=1;
					console.log("assigned group:"+g.USER_GROUP_KEY);
				}
				g.MODIFIED=true;
			}
		}
	};
	
	$scope.init = function () {
		var post_url = STOREELF_ROOT_URI+'/Security/UserManagement/getUserDropdown';
		$http({
		    method: 'POST',
		    url: post_url,
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function(data, status) {
			//console.log(JSON.stringify(data));
			$scope.userGroupDropdown=data;
		});
	};
	
	$scope.resetData = function() {
        $scope.add.userID = '' ;
        $scope.add.firstName = '' ;
        $scope.add.lastName = '' ;
        $scope.search.userID = '' ;
        $scope.search.userName = '';
        $scope.add.userGroup = '';
        $scope.result = 0;
    };
    
    $scope.reloadData = function(){
    	 window.parent.location.reload();
    };
	
}


</script>

<div ng-app="SessionManagementApp">
	<div ng-controller="SessionController" data-ng-init="init()">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Security</a></li>
				<li class="active">Session Management</li>
			</ol>
		</div>
		
		
		<div class="col-sm-12">
			<section class="panel">
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form" ng-enter="searchUsers(search)">
						<div class="form-group">
							<label class="col-sm-2 control-label">Session ID</label>
						    <div class="col-sm-3">
						      <input name="userID" class="form-control" ng-model="search.sessionID" onclick="" value="" type="text" ng-init="search.sessionID=''" placeholder="User ID" />
						    </div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button ng-disabled="!(!!search.sessionID || isLoading())" class="btn btn-default" type="button" ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]"
									ng-click="searchUsers(search)">{{button_search}}</button>
								<button ng-disabled="!(!!search.sessionID || isLoading())" class="btn btn-default" type="button" style="margin-left:25px; ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]"
									ng-click="resetData()">Reset</button>
							</div>
						</div>						
					</form>
				</div>
			</section>
		</div>
		
		
		<div class="col-sm-12" >
			<section class="panel">
				<header class="panel-heading">
            <button type="button"  data-toggle="modal" data-target="#addUserPopupModal" style="background-color:#01DF74;float: right; border:1px solid transparent;
            white-space: nowrap; padding:6px 12px;font-weight: 400; font-size:14px; display: inline-block; border-radius: 4px; align-items: flex-start;">Add New User</button>
            <h4>User Details</h4>
				</header>
				
				<table 
					class="table table-hover table-condensed table-bordered table-responsive"
					summary="Code page support in different versions of MS Windows."
					rules="groups" frame="hsides" >
					<colgroup align="center"></colgroup>
					<colgroup align="left"></colgroup>
					<colgroup span="2" align="center"></colgroup>
					<colgroup span="3" align="center"></colgroup>
					<thead>
						<tr>
							<th>Session ID</th>
							<th>User Name</th>
							<th>Start Date</th>							
							<th>Last Access</th>
							<th>Expires</th>
							<th>Delete</th>							
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="session in sessiondetails">
							<!-- <td><button class="btn btn-primary btn-sm" data-toggle="modal" data-target="#SessionDetailsModal" ng-click="loadUserGroupData(user.USERNAME)">{{sess.USERNAME}} </button></td> -->
							<td ng-bind="session.SESSION_ID"></td>
							<td ng-bind="session.SESSION_USER"></td>
							<td ng-bind="session.START_DATE"></td>
							<td ng-bind="session.LAST_ACCESS"></td>
							<td ng-bind="session.EXPIRES"></td>
							<td><button class="btn btn-primary btn-sm" data-toggle="modal"  data-target="#deleteUserPopupModal" ng-click="deleteUserId(user)" style="background-color:#FC0202; border:1px solid transparent;
            white-space: nowrap; padding:6px 12px;font-weight: 400; font-size:10px; display: inline-block; border-radius: 4px; align-items: flex-start;" >Delete This User</button></td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
		
		<!-- Add User Modal -->
		<div class="modal fade" id="addUserPopupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
<!-- 		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
 -->		        <h4 class="modal-title" id="myModalLabel">Insert User Details </h4>
		      </div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form"
								ng-enter="searchUsers(search)">
								<div class="form-group">
									<label class="col-sm-2 control-label">User ID*</label>
									<div class="col-sm-3">
										<input name="userID" class="form-control"
											ng-model="add.userID" onclick="" value="" type="text"
											ng-init="add.userID=''" placeholder="User ID" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label">User First Name*</label>
									<div class="col-sm-3">
										<input name="firstName" class="form-control"
											ng-model="add.firstName" onclick="" value="" type="text"
											ng-init="add.firstName=''" placeholder="First Name" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label">User Last Name*</label>
									<div class="col-sm-3">
										<input name="lastName" class="form-control"
											ng-model="add.lastName" onclick="" value="" type="text"
											ng-init="add.lastName=''" placeholder="Last Name" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label">Select User Group</label>
									<div class="col-sm-3">
										<select name="userGroup" class="form-control"
											ng-model="add.userGroup">
											<option value="" selected="">----------</option>
											<option ng-repeat="group in userGroupDropdown"
												value={{group.NAME}}>{{group.NAME}}</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button
											ng-disabled="!(!!add.userID && !!add.firstName && !!add.lastName || isLoading())" ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]"
											class="btn btn-default" type="button" ng-click="addUser(add)">{{button_save}}</button>
										<button
											ng-disabled="!(!!add.userID || !!add.firstName || !!add.lastName || isLoading())"
											class="btn btn-default" type="button"
											ng-click="resetData()">Reset</button>
									</div>
								</div>

								<div class="form" ng-show="(result>0)">
									<section>
										<b></b><output  style="color:#15FD04">Saved Successfully</output></b>
									</section>
								</div>
								
								<div class="form" ng-show="!(result==0)">
									<section>
										<h4 style="color:#FD0411" ng-bind="result.error"></h4>
									</section>

								</div>
								
							</form>
						</section>
					</div>
					<div class="modal-footer">
					 <h6 class="modal-content" id="myModalLabel">* - Mandatory Fields</h6>
						<button type="button" class="btn btn-default" data-dismiss="modal" ng-click="reloadData()">Close</button>
					</div>
				</div>
		  </div>
		</div>
		<!-- End of Adduser  -->
		
		<!-- Delete User Modal -->
		<div class="modal fade" id="deleteUserPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h5 align="center" class="modal-title" id="myModalLabel">Please Confirm</h5>
					</div>
					
							<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"  ng-click="deleteUser()" ng-disabled="!(deleteSuccessfull==0)">Yes</button>
										<button class="btn btn-default" type="button" data-dismiss="modal"  ng-disabled="!(deleteSuccessfull==0)" ng-click="resetData()">No</button>
									</div>
								</div>

								<div class="form" ng-hide="(deleteSuccessfull==0)">
									<section>
										<output>User Deleted successfully</output>
									</section>
									<div class="modal-footer">
										<button type="button" class="btn btn-default"
											data-dismiss="modal" ng-click="reloadData()">Close</button>
									</div>
								</div>
							</form>
						</section>
					</div>
					
				</div>
			</div>
		</div>
		<!-- End of Delete User  -->
		
		<!-- Check User's Group Modal -->
<!-- 		<div class="modal fade" id="userGroupDetailsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg"  >
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		        <h4 class="modal-title" id="myModalLabel">User's Subscribed Groups</h4>
		      </div>
		      <div class="modal-body">
		      <section class="panel">
		        <table class="table table-hover table-bordered table-condensed" rules="groups" frame="hsides">
	                <tr>
	                    <th bgcolor=#4DA5AD><font color="white"> User ID</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Group Name</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Group Description</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Default URL</font></th>
	                </tr>

	                 <tr ng-repeat="groupdetail in groupdetails">
	                    <td>{{groupdetail.USERNAME}}</td>
	                    <td>{{groupdetail.NAME}}</td>
	                    <td>{{groupdetail.DESCRIPTION}}</td>
	                    <td>{{groupdetail.DEFAULT_LANDING_PAGE_URI}}</td>
	                </tr>
			    </table>
			    </section>
		      </div>
		      <div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>  -->
		<!-- ################################################################### start assign user group ################################################################### -->
		<div class="modal fade" id="userGroupDetailsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header"><h4 class="modal-title" id="myModalLabel">Assign user group Details </h4></div>
				<div class="modal-body">
					<section class="panel">
						<form name="form" class="form-horizontal" role="form">
							<div style="overflow:auto;height:400px"	>						
								<table class="table table-hover table-condensed table-bordered table-responsive" rules="groups" frame="hsides" >
									<colgroup align="center"></colgroup>
									<colgroup align="left"></colgroup>
									<colgroup span="2" align="center"></colgroup>
									<colgroup span="3" align="center"></colgroup>
									<thead>
										<tr>
											<th>User ID</th>
						                    <th>Group Name</th>
						                    <th>Group Description</th>
						                    <th>Default URL</th>
						                    <th>Assign</th> 
										</tr>
									</thead>
									<tbody>										
										<tr ng-repeat="groupdetail in groupdetails">
						                    <td>{{groupdetail.USERNAME}}</td>
						                    <td>{{groupdetail.NAME}}</td>
						                    <td>{{groupdetail.DESCRIPTION}}</td>
						                    <td>{{groupdetail.DEFAULT_LANDING_PAGE_URI}}</td>
						                    <td>
												<button ng-class="{true: 'btn btn-danger btn-sm', false: 'btn btn-success btn-sm'}[groupdetail.IS_ASSIGNED>0]" if="groupdetail.IS_ASSIGNED>0" ng-click="toggleGroupBtn(groupdetail)" ng-bind="(groupdetail.IS_ASSIGNED>0) ? 'Remove' : 'Assign'"></button>
											</td>
						                </tr>
									</tbody>
								</table>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button class="btn btn-default" type="button" ng-click="commitGroups();" data-dismiss="modal">Save</button>
								</div>
							</div>								
						</form>
					</section>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" ng-click="resetPopupData()">Close</button>
				</div>
			</div>
		</div>
	</div>
		<!-- ################################################################### End assign user group ################################################################### -->
</div>