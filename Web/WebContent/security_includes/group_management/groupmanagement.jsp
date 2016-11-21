<script type="text/javascript">
	ANGULARJS_APP = angular.module('GroupManagementSearchApp', []);

	function GroupSearchController($scope, $http) {
		var post_url = STOREELF_ROOT_URI
				+ '/Security/GroupManagement/groupmanagement';
		$scope.delresult = 0;
		$scope.result = 0;
		$scope.resultUpdate = 0;
		var delGroupKey = '';
		$scope.currentGroupId = '';

		$scope.searchGroups = function(search) {
			$http(
					{
						method : 'POST',
						url : post_url,
						data : "groupDesc=" + search.groupDescription
								+ "&groupName=" + search.groupName,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.groupdetails = data;
				//$scope.getSearchHistory();
			});
		};

		/* $scope.deleteGroup = function(del){
			$http({
			method: 'POST',
		    url: STOREELF_ROOT_URI+'/Security/GroupManagement/deleteGroup_popup',
		    data: "groupKey=" + del.groupKey + "&groupName=" + del.groupName,
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				$scope.groupdetails = data;
			});
		};
		$scope.resetPopupData = function() {
		    $scope.del.groupKey = '' ;
		    $scope.del.groupName = '' ;
		    $scope.del = 0;
		}; */
		$scope.addGroup = function(add) {
			$scope.result = 0;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/addGroup_popup',
						data : "groupDesc=" + add.groupDesc + "&groupName="
								+ add.groupName,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.result = data;
			});
		};

		$scope.deleteGroupKey = function(group) {
			$scope.delresult = 0;
			delGroupKey = group.USER_GROUP_KEY;
		};

		$scope.deleteGroup = function() {

			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/delGroup_popup',
						data : "groupKey=" + delGroupKey,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {

				$scope.deletedGroupKey = delGroupKey;
				$scope.delresult = JSON.parse(data.replace(";", ""));

			});
		};

		$scope.editGroupKey = function(group) {
			$scope.resultUpdate = 0;
			$scope.modalGroupKey = group.USER_GROUP_KEY;
			$scope.edit.groupName = group.NAME;
			$scope.edit.groupDesc = group.DESCRIPTION;
		};

		$scope.editGroup = function(edit) {

			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/editGroup_popup',
						data : "editGroupKey=" + $scope.modalGroupKey,
						data : "editGroupKey=" + $scope.modalGroupKey
								+ "&groupname=" + $scope.edit.groupName
								+ "&groupdesc=" + $scope.edit.groupDesc,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.resultUpdate = data;
			});
		};

		$scope.loadUserGroupData = function(groupKey) {
			$scope.currentGroupId = groupKey;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/userDetails_popup',
						data : "groupKey=" + groupKey,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.userdetails = data;
			});
		};

		$scope.loadRoleGroupData = function(roleGroupKey) {
			$scope.currentGroupId = roleGroupKey;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/roleDetails_popup',
						data : "roleGroupKey=" + roleGroupKey,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.roledetails = data;
				
			});
		};

		$scope.commitRoles = function() {
			console.log("committing roles...");
			for ( var key in $scope.roledetails) {
				var role = $scope.roledetails[key];
				if (role.MODIFIED == true) {
					if (role.IS_ASSIGNED > 0) {
						console.log("committing role assignment:"
								+ role.USER_ROLE_KEY);
						$scope.assignRole($scope.currentGroupId,
								role.USER_ROLE_KEY);
					} else {
						console.log("committing role removal:"
								+ role.USER_ROLE_KEY);
						$scope.unassignRole($scope.currentGroupId,
								role.USER_ROLE_KEY);
					}
				}
			}
		};

		$scope.assignRole = function(user_group_key, user_role_key) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/assignRoles',
						data : "user_group_key=" + user_group_key
								+ "&user_role_key=" + user_role_key,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
			});
		};

		$scope.unassignRole = function(user_group_key, user_role_key) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/GroupManagement/unassignRoles',
						data : "user_group_key=" + user_group_key
								+ "&user_role_key=" + user_role_key,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				//$scope.assignRoleData = data;			
			});
		};

		$scope.toggleRoleBtn = function(role) {
			for ( var key in $scope.roledetails) {
				var r = $scope.roledetails[key];
				if (r.USER_ROLE_KEY == role.USER_ROLE_KEY) {

					if (r.IS_ASSIGNED > 0) {
						r.IS_ASSIGNED = 0;
						console.log("removed role:" + r.USER_ROLE_KEY);
					} else {
						r.IS_ASSIGNED = 1;
						console.log("assigned role:" + r.USER_ROLE_KEY);
					}
					r.MODIFIED = true;
				}
			}
		};

		$scope.resetPopupData = function() {
			$scope.add.groupNumber = '';
			$scope.add.groupName = '';
			$scope.add.lastName = '';
			$scope.add.groupDesc = '';
			$scope.result = 0;
		};

	}
</script>


<div ng-app="GroupManagementSearchApp">
	<div ng-controller="GroupSearchController">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Security</a></li>
				<li class="active">Group Management</li>
			</ol>
		</div>


		<div class="col-sm-12">
			<section class="panel">
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form"
						ng-enter="searchGroups(search)">

						<div class="form-group">
							<label class="col-sm-2 control-label">Group Name</label>
							<div class="col-sm-3">
								<input name="groupName" class="form-control"
									ng-model="search.groupName" onclick="" value="" type="text"
									ng-init="search.groupName=''" placeholder="Group Name" />
							</div>
						</div>

						<div class="form-group">
							<label class="col-sm-2 control-label">Group Desc</label>
							<div class="col-sm-3">
								<input name="groupDescription" class="form-control"
									ng-model="search.groupDescription" onclick="" value=""
									type="text" ng-init="search.groupDescription=''"
									placeholder="Group Desc" />
							</div>
						</div>




						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button
									ng-disabled="!(!!search.groupDescription ||!!search.groupName || isLoading())"
									class="btn btn-default" type="button"
									ng-click="searchGroups(search)">Search</button>
							</div>
						</div>
					</form>
				</div>
			</section>
		</div>


		<div class="col-sm-12">
			<section class="panel">
				<header class="panel-heading">
					<button type="button" data-toggle="modal"
						data-target="#addGroupPopupModal"
						style="background-color: #01DF74; float: right; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-weight: 400; font-size: 14px; display: inline-block; border-radius: 4px; align-items: flex-start;">Add
						New Group</button>
					<h4>Group Details</h4>
				</header>
				<table
					class="table table-hover table-condensed table-bordered table-responsive"
					summary="Code page support in different versions of MS Windows."
					rules="groups" frame="hsides">
					<colgroup align="center"></colgroup>
					<colgroup align="left"></colgroup>
					<colgroup span="2" align="center"></colgroup>
					<colgroup span="3" align="center"></colgroup>
					<thead>
						<tr>
							<th>Group Key</th>
							<th>Group Name</th>
							<th>Description</th>
							<th>Is Active</th>
							<th>Default Page</th>
							<th>Createts</th>
							<th>Modifyts</th>
							<th>CreateUserName</th>
							<th>ModifyUserName</th>
							<th>View Members</th>
							<th>View Roles</th>
							<th>Edit</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="group in groupdetails">
							<td ng-bind="group.USER_GROUP_KEY"></td>
							<td ng-bind="group.NAME"></td>
							<td ng-bind="group.DESCRIPTION"></td>
							<td ng-bind="group.IS_ACTIVE"></td>
							<td ng-bind="group.DEFAULT_LANDING_PAGE_URI"></td>
							<td ng-bind="group.CREATETS"></td>
							<td ng-bind="group.MODIFYTS"></td>
							<td ng-bind="group.CREATEUSERNAME"></td>
							<td ng-bind="group.MODIFYUSERNAME"></td>
							<td><button class="btn btn-primary btn-sm"
									data-toggle="modal" data-target="#userGroupDetailsModal"
									ng-click="loadUserGroupData(group.USER_GROUP_KEY) ">View
									Members</button></td>
							<td><button class="btn btn-primary btn-sm"
									data-toggle="modal" data-target="#groupRoleDetailsModal"
									ng-click="loadRoleGroupData(group.USER_GROUP_KEY) ">View
									Roles</button></td>
							<td><button class="btn btn-primary btn-sm"
									data-toggle="modal" data-target="#editPopupModal"
									ng-click="editGroupKey(group) ">Modify</button></td>
							<td><button class="btn btn-primary btn-sm"
									data-toggle="modal" data-target="#deleteGroupConfirmModal"
									ng-click="deleteGroupKey(group) "
									style="background-color: #FC0202; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-weight: 400; font-size: 10px; display: inline-block; border-radius: 4px; align-items: flex-start;">Delete</button></td>

						</tr>
					</tbody>
				</table>
			</section>
		</div>

		<div class="modal fade" id="addGroupPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">Insert Group
							Details</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form"
								ng-enter="searchGroups(search)">


								<div class="form-group">
									<label class="col-sm-2 control-label">Group Name</label>
									<div class="col-sm-3">
										<input name="groupName" class="form-control"
											ng-model="add.groupName" onclick="" value="" type="text"
											ng-init="add.groupName=''" placeholder="Group Name" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label">Group Desc</label>
									<div class="col-sm-3">
										<input name="groupDesc" class="form-control"
											ng-model="add.groupDesc" onclick="" value="" type="text"
											ng-init="add.groupDesc=''" placeholder="Group Desc" />
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button
											ng-disabled="!(!!add.groupDesc || !!add.groupName || isLoading())"
											class="btn btn-default" type="button" data-dismiss="modal"
											ng-click="addGroup(add);">Save</button>
										<button
											ng-disabled="!(!!add.groupDesc || !!add.groupName || isLoading())"
											class="btn btn-default" type="button"
											ng-click="resetPopupData()">Reset</button>
									</div>
								</div>

								<div class="form" ng-hide="(result==0)">
									<section>
										<output>Saved Successfully</output>
									</section>
								</div>
							</form>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"
							ng-click="resetPopupData()">Close</button>
					</div>
				</div>
			</div>
		</div>

		<!-- Delete User Code -->
		<div class="modal fade" id="deleteGroupConfirmModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h5 align="center" class="modal-title" id="myModalLabel">Confirm
							Deletion</h5>
					</div>

					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form"
								ng-enter="searchGroups(search)">

								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button
											ng-disabled="(!!del.groupKey && !!del.groupName || isLoading())"
											class="btn btn-default" type="button"
											ng-click="deleteGroup();">Yes</button>
										<button
											ng-disabled="(!!del.groupKey || !!del.groupName || isLoading())"
											class="btn btn-default" type="button"
											ng-click="resetPopupData()">No</button>
									</div>
								</div>
								<div class="form" ng-show="(delresult==1)">
									<section>
										<output>Group key {{deletedGroupKey}} deleted
											successfully</output>
									</section>

								</div>

								<div class="form" ng-show="!(delresult==0)">
									<section>
										<h4 style="color: #FD0411" ng-bind="delresult.error"></h4>
									</section>

								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal" ng-click="resetPopupData()">Close</button>
								</div>
							</form>
						</section>
					</div>

				</div>
			</div>
		</div>

		<!-- start editGroup -->
		<div class="col-sm-12 modal fade" id="editPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">Modify User Group</h4>
					</div>
					<div class="modal-body">
						<section class="panel">


							<div class="panel-body">
								<form name="form" class="form-horizontal" role="form">
									<div class="form-group">
										<label class="col-sm-2 control-label">Group Key</label>
										<div class="col-sm-3">
											<label class="col-sm-2 control-label"
												ng-model="modalGroupKey">{{modalGroupKey}}</label>
										</div>
									</div>

									<div class="form-group">
										<label class="col-sm-2 control-label">Group Name</label>
										<div class="col-sm-3">
											<input name="editGroupName" class="form-control"
												ng-model="edit.groupName" onclick="" value="" type="text"
												ng-init="edit.groupName=''" placeholder="Group Name" />
										</div>
									</div>

									<div class="form-group">
										<label class="col-sm-2 control-label">Group Desc</label>
										<div class="col-sm-3">
											<input name="editGroupDesc" class="form-control"
												ng-model="edit.groupDesc" onclick="" value="" type="text"
												ng-init="edit.groupDesc=''" placeholder="Group Desc" />
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<button
												ng-disabled="!(!!edit.groupDesc || !!edit.groupName || isLoading())"
												class="btn btn-default" type="button"
												ng-click="editGroup(edit);">Save</button>
											<button
												ng-disabled="!(!!edit.groupDesc || !!edit.groupName || isLoading())"
												class="btn btn-default" type="button"
												ng-click="resetPopupData()">Reset</button>
										</div>
									</div>
								</form>
							</div>
							<div class="form" ng-hide="(resultUpdate==0)">
								<section>
									<output>Saved Successfully</output>
								</section>
							</div>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"
							ng-click="resetPopupData();">Close</button>
					</div>
				</div>
			</div>
		</div>
		<!-- end editgroup -->



		<!-- start usergroup modal -->
		<!-- Check User's Group Modal -->
		<div class="modal fade" id="userGroupDetailsModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">Group's Subscribed
							Users</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<table class="table table-hover table-bordered table-condensed"
								rules="groups" frame="hsides">
								<tr>
									<th bgcolor=#4DA5AD><font color="white"> First Name</font></th>
									<th bgcolor=#4DA5AD><font color="white">Last Name</font></th>
									<th bgcolor=#4DA5AD><font color="white">UserName</font></th>
								</tr>

								<tr ng-repeat="userdetail in userdetails">
									<td>{{userdetail.FIRST_NAME}}</td>
									<td>{{userdetail.LAST_NAME}}</td>
									<td>{{userdetail.USERNAME}}</td>
								</tr>
							</table>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
		<!--  end user group modal  
	<div class="modal fade" id="roleGroupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg"  >
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		        <h4 class="modal-title" id="myModalLabel">Group's Subscribed Users</h4>
		      </div>
		      <div class="modal-body">
		      <section class="panel">
		        <table class="table table-hover table-bordered table-condensed" rules="groups" frame="hsides">
	                <tr>
	                    <th bgcolor=#4DA5AD><font color="white"> Group Key</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Role Name</font></th>
	                    <th bgcolor=#4DA5AD><font color="white">Role Desc</font></th>
	                    
	                </tr>

						 <tr ng-repeat="roledetail in roledetails">
						    <td>{{roledetail.USER_GROUP_KEY}}</td>
						    <td>{{roledetail.NAME}}</td>
						    <td>{{roledetail.DESCRIPTION}}</td>
						</tr>
					</table>
				</section>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
end user group modal -->

		<!-- ################################################################### start assign group role ################################################################### -->
		<div class="modal fade" id="groupRoleDetailsModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">Assign group role
							Details</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div style="overflow: auto; height: 400px">
									<table ng-init="reverse=false; predicate='GROUP_KEY'"
										class="table table-hover table-condensed table-bordered table-responsive"
										rules="groups" frame="hsides">
										<colgroup align="center"></colgroup>
										<colgroup align="left"></colgroup>
										<colgroup span="2" align="center"></colgroup>
										<colgroup span="3" align="center"></colgroup>
										<thead>
											<!-- 										 <tr>
											<th>User ID</th>
						                    <th>Group Name</th>
						                    <th>Group Description</th>
						                    <th>Default URL</th>
						                    <th>Assign</th> 
										</tr>
										 -->
											<tr>
												<th ng-click="predicate = 'GROUP_KEY'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'GROUP_KEY'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Group Key</th>
												<th ng-click="predicate = 'NAME'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right" ng-show="predicate == 'NAME'">
														<span ng-show="!reverse"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span
														ng-show="reverse"> <i class="fa fa-sort-asc"
															id="orderArrow"></i></span>
												</span>Role Name</th>
												<th ng-click="predicate = 'DESCRIPTION'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'DESCRIPTION'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Role Desc</th>
												<th>Assign</th>
											</tr>
										</thead>
										<tbody>
											<!-- <tr ng-repeat="groupdetail in groupdetails">
						                    <td>{{groupdetail.USERNAME}}</td>
						                    <td>{{groupdetail.NAME}}</td>
						                    <td>{{groupdetail.DESCRIPTION}}</td>
						                    <td>{{groupdetail.DEFAULT_LANDING_PAGE_URI}}</td>
						                    <td>
												<button ng-class="{true: 'btn btn-danger btn-sm', false: 'btn btn-success btn-sm'}[groupdetail.IS_ASSIGNED>0]" if="groupdetail.IS_ASSIGNED>0" ng-click="toggleGroupBtn(groupdetail)" ng-bind="(groupdetail.IS_ASSIGNED>0) ? 'Remove' : 'Assign'"></button>
											</td>
						                </tr> -->

											<tr ng-repeat="roledetail in roledetails | orderBy : predicate : reverse">
												<td>{{roledetail.USER_GROUP_KEY}}</td>
												<td>{{roledetail.NAME}}</td>
												<td>{{roledetail.DESCRIPTION}}</td>
												<td>
													<button
														ng-class="{true: 'btn btn-danger btn-sm', false: 'btn btn-success btn-sm'}[roledetail.IS_ASSIGNED>0]"
														if="roledetail.IS_ASSIGNED>0"
														ng-click="toggleRoleBtn(roledetail)"
														ng-bind="(roledetail.IS_ASSIGNED>0) ? 'Remove' : 'Assign'"></button>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"
											ng-click="commitRoles();" data-dismiss="modal">Save</button>
									</div>
								</div>
							</form>
						</section>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"
							ng-click="resetPopupData()">Close</button>
					</div>
				</div>
			</div>
		</div>
		<!-- ################################################################### End assign user group ################################################################### -->



	</div>