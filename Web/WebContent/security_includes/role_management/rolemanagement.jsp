<script type="text/javascript">
	ANGULARJS_APP = angular.module('RoleManagementSearchApp', []);
	// custom filer used on ohject. Note standard OrderBy only works on array, not on object

app.filter('orderObjectBy', function() {
  return function(items, field, reverse) {
    var filtered = [];
    angular.forEach(items, function(item) {
      filtered.push(item);
    });
    filtered.sort(function (a, b) {
      return (a[field] > b[field] ? 1 : -1);
    });
    if(reverse) filtered.reverse();
    return filtered;
  };
});
	
	function RoleSearchController($scope, $http) {
		var post_url = STOREELF_ROOT_URI
				+ '/Security/RoleManagement/rolemanagement';
		$scope.result = 0;
		$scope.resultUpdate = 0;
		$scope.roleDelete = 0;
		$scope.addpermission = 0;
		$scope.permissionDelete = 0;
		$scope.assignedPermissionsArray = [];
		$scope.viewPermissionsForRole = [];
		$scope.viewPermissions = [];
		$scope.currentRoleId = null;

		$scope.isActive = false;
		$scope.clicked = false;
		$scope.button = "Search";
		$scope.userRoleDropdown = [];
		var rollId = '';
		var permissionKey = '';
		
		var compare = function(a,b){
		    if (parseInt(a.position) < parseInt(b.position))
		         return -1;
		    if (parseInt(a.position) > parseInt(b.position))
		        return 1;
		    return 0;
		};

		$scope.searchUsers = function() {
			$scope.isActive = true;
			$scope.clicked = true;
			$scope.button = "Processing...";

			$http({
				method : 'POST',
				url : post_url,
				data : "userRole=" + $scope.search.userRole,
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.roledetails = data;
				$scope.isActive = false;
				$scope.clicked = false;
				$scope.button = "Search";
			});
		};

		$scope.addUserRole = function(add) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/addUserRole',
						data : "userRoleId=" + add.userRoleId + "&userRole="
								+ add.userRole,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.result = data;
			});
		};

		$scope.deleteRoleId = function(user) {
			rollId = user.USER_ROLE_KEY;
		};

		$scope.deleteRole = function() {
			$scope.roleDelete = 0;
			$http({
				method : 'POST',
				url : STOREELF_ROOT_URI + '/Security/RoleManagement/deleteRole',
				data : "userRoleID=" + rollId,
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.roleDelete = data;
				$scope.roleIdDelete = rollId;

			});
		};

		$scope.EditRole = function(user) {
			$scope.edit.userRoleId = user.USER_ROLE_KEY;
			$scope.edit.userRoleName = user.NAME;
		};

		$scope.editUserRole = function(edit) {
			$scope.resultUpdate = 0;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/editUserRole',
						data : "userRoleID=" + edit.userRoleId + "&userRole="
								+ edit.userRoleName,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.resultUpdate = data;
				$scope.getRoles();
			});

			//$scope.search.userRole=edit.userRoleId;
			//$scope.searchUsers();
		};

		$scope.viewPermissionsFunc = function(role_id) {
			$scope.viewPermissions = [];
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/viewPermissions',
						//data: "user_role_key="+role_id,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
						
				$scope.viewPermissions = data;
				console.log($scope.viewPermissions);
				if (role_id != null && role_id != 'null')
					$scope.currentRoleId = role_id;
			});
		};

		$scope.viewPermissionsForRole = function(role_id) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/viewPermissions',
						data : "user_role_key=" + role_id,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.viewPermissionsForRoleData = data;
				if (role_id != null && role_id != 'null') {
					$scope.currentRoleId = role_id;
				}
			});
		};

		$scope.listPermissions = function() {
			console.log("Preloading permissions data...")
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/listPermissions',
						//data: "user_role_key="+role_id,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.listPermissions = data;
				for (key in data) {
					console.log("permission:" + key);
				}
			});
		};

		$scope.addPermissions = function(add, servleturi) {
			$scope.addpermission = 0;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/addPermissions',
						data : "section=" + add.section + "&function="
								+ add.function_p + "&servleturi=" + servleturi,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.addpermission = data;
			});
		};

		$scope.assignPermission = function(user_role_key, user_permission_key) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/assignPermissions',
						data : "user_role_key=" + user_role_key
								+ "&user_permission_key=" + user_permission_key,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				//$scope.assignpermissionData = data;			
			});
		};

		$scope.removePermission = function(user_role_key, user_permission_key) {
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/removePermissions',
						data : "user_role_key=" + user_role_key
								+ "&user_permission_key=" + user_permission_key,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				//$scope.assignpermissionData = data;			
			});
		};

		$scope.commitPermissions = function() {
			console.log("committing permissions...");
			for ( var key in $scope.viewPermissionsForRoleData) {
				var permission = $scope.viewPermissionsForRoleData[key];
				if (permission.MODIFIED == true) {
					if (permission.HAS_PERMISSION > 0) {
						console.log("committing permission assignment:"
								+ permission.USER_PERMISSION_KEY);
						$scope.assignPermission($scope.currentRoleId,
								permission.USER_PERMISSION_KEY);
					} else {
						console.log("committing permission removal:"
								+ permission.USER_PERMISSION_KEY);
						$scope.removePermission($scope.currentRoleId,
								permission.USER_PERMISSION_KEY);
					}
				}
			}
			$scope.viewPermissionsForRoleData = [];
		};

		$scope.togglePermissionBtn = function(permission) {
			for ( var key in $scope.viewPermissionsForRoleData) {
				var perm = $scope.viewPermissionsForRoleData[key];
				if (perm.USER_PERMISSION_KEY == permission.USER_PERMISSION_KEY) {
					perm.MODIFIED = true;

					if (perm.HAS_PERMISSION > 0) {
						perm.HAS_PERMISSION = 0;
						console.log("removed permission:"
								+ perm.USER_PERMISSION_KEY);
					} else {
						perm.HAS_PERMISSION = 1;
						console.log("assigned permission:"
								+ perm.USER_PERMISSION_KEY);
					}
				}
			}
		};

		$scope.deletePermissionKey = function(permission) {
			permissionKey = permission.USER_PERMISSION_KEY;
		};

		$scope.deletePermission = function() {
			$scope.permissionDelete = 0;
			$http(
					{
						method : 'POST',
						url : STOREELF_ROOT_URI
								+ '/Security/RoleManagement/deletePermission',
						data : "permissionkey=" + permissionKey,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).success(function(data, status) {
				$scope.permissionDelete = data;
				$scope.permissionIdDelete = permissionKey;

				for ( var key in $scope.viewPermissions) {
					var perm = $scope.viewPermissions[key];
					if (perm.USER_PERMISSION_KEY == permissionKey) {
						delete $scope.viewPermissions[key];
					}
				}
			});
		};

		$scope.getRoles = function() {
			var post_url = STOREELF_ROOT_URI
					+ '/Security/RoleManagement/getRoleDropdown';
			$http({
				method : 'POST',
				url : post_url,
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
			}).success(function(data, status) {
				$scope.dropdown = data;
				var arr = [], collection = [];

				$.each(data, function(index, value) {
					if ($.inArray(value.NAME, arr) == -1) {
						arr.push(value.NAME);
						collection.push(value);
					}
				});
				$scope.userRoleDropdown = collection;
			});
		};

		$scope.init = function() {
			$scope.getRoles();
		};

		$scope.resetPopupData = function() {
			$scope.result = 0;
			$scope.resultUpdate = 0;
		};

	}
</script>

<div ng-app="RoleManagementSearchApp">
	<div ng-controller="RoleSearchController"
		data-ng-init="init();viewPermissionsFunc(null);">
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Security</a></li>
				<li class="active">Role Management</li>
			</ol>
		</div>


		<div class="col-sm-12">
			<section class="panel">
				<div class="panel-body">
					<form name="form" class="form-horizontal" role="form"
						ng-enter="searchUsers()">

						<div class="form-group">
							<label class="col-sm-2 control-label">User Role</label>
							<div class="col-sm-3">
								<select name="userRole" class="form-control"
									ng-model="search.userRole">
									<option value="" selected="">----------</option>
									<option ng-repeat="role in userRoleDropdown"
										value={{role.NAME}}>{{role.NAME}}</option>

								</select>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button
									ng-disabled="!(!!search.userRole|| isLoading()||(clicked == true))"
									class="btn btn-default"
									ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]"
									type="button" ng-click="searchUsers();">{{button}}</button>
							</div>
						</div>
					</form>
				</div>
			</section>
		</div>


		<div class="col-sm-12">
			<section class="panel">

				<header class="panel-heading">
					<!-- <button class="btn btn-default"  type="button" data-toggle="modal" data-target="#viewPermissionPopupModal" ng-click="listPermissions()">List Permission</button> -->
					<button class="btn btn-default" type="button" data-toggle="modal"
						data-target="#viewPermissionPopupModal"
						ng-click="viewPermissionsFunc(null)">View Permission</button>
					<button class="btn btn-default" type="button" data-toggle="modal"
						data-target="#addPermissionPopupModal" ng-click="">Add
						Permission</button>

					<button type="button" data-toggle="modal"
						data-target="#addUserRolePopupModal"
						style="background-color: #01DF74; float: right; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-weight: 400; font-size: 14px; display: inline-block; border-radius: 4px; align-items: flex-start;">Add
						Role</button>


					<h4>User Role Details</h4>
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
							<th>User Role Id</th>
							<th>User Role</th>
							<th>Edit Role</th>
							<th>Delete Role</th>
							<th>Assign Permissions</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="user in roledetails">

							<td ng-bind="user.USER_ROLE_KEY"></td>
							<td ng-bind="user.NAME"></td>

							<td colspan="1">
								<button type="button" class="btn btn-default btn-sm"
									data-toggle="modal" data-target="#editPopupModal"
									ng-click="EditRole(user);">
									<span>Edit</span>
								</button>
							</td>
							<td><button class="btn btn-primary btn-sm"
									data-toggle="modal" data-target="#deleteRolePopupModal"
									ng-click="deleteRoleId(user)"
									style="background-color: #FC0202; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-weight: 400; font-size: 10px; display: inline-block; border-radius: 4px; align-items: flex-start;">Delete
								</button></td>
							<td><button class="btn btn-primary btn-sm" type="button"
									data-toggle="modal" data-target="#assignPermissionPopupModal"
									ng-click="viewPermissionsForRole(user.USER_ROLE_KEY)">Assign
									Permissions</button></td>

						</tr>
					</tbody>
				</table>
			</section>
		</div>



		<!-- ################################################################### Start Add Role Popup ###################################################################-->
		<div class="modal fade" id="addUserRolePopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">Insert Role Details
						</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">



								<div class="form-group">
									<label class="col-sm-2 control-label">User Role</label>
									<div class="col-sm-3">
										<input name="userRole" class="form-control"
											ng-model="add.userRole" onclick="" value="" type="text"
											ng-init="add.userRole=''" placeholder="User Role" />
									</div>
								</div>


								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button
											ng-disabled="!(!!add.userRoleId||!!add.userRole  || isLoading())"
											class="btn btn-default" type="button"
											ng-click="addUserRole(add);resetPopupData();"
											data-dismiss="modal">Save</button>
										<button
											ng-disabled="!(!!add.userRoleId||!!add.userRole  || isLoading())"
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
		<!-- ################################################################### End Add Role Popup ###################################################################-->
		<!-- ################################################################### Start Edit Role Popup ###################################################################-->
		<div class="col-sm-12 modal fade" id="editPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel"
							ng-bind-template="Modify User Role - {{edit.userRoleName}}"></h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<div class="panel-body">
								<form name="form" class="form-horizontal" role="form">
									<div class="form-group">
										<label class="col-sm-2 control-label">Role ID</label>
										<div class="col-sm-3">
											<label class="col-sm-2 control-label"
												ng-bind-template="{{edit.userRoleId}}"></label>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">Existing Role
											Name</label>
										<div class="col-sm-3">
											<label class="col-sm-2 control-label">{{edit.userRoleName}}</label>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">New Role Name</label>
										<div class="col-sm-3">
											<input name="userRoleId" class="form-control"
												ng-model="edit.userRoleId" type="hidden"
												ng-init="edit.userRoleId=''" /> <input name="userRoleNAme"
												class="form-control" ng-model="edit.userRoleName"
												type="text" ng-init="edit.userRoleName=''"
												placeholder="User Role Name" />
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<button class="btn btn-default"
												ng-class="{true: 'btn-warning', false: 'btn-default'}[isActive]"
												type="button" ng-click="editUserRole(edit);">Save</button>
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
		<!-- ################################################################### End Edit Role Popup ###################################################################-->
		<!-- ################################################################### Start Delete Role Popup ###################################################################-->
		<div class="modal fade" id="deleteRolePopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">

						<h5 align="center" class="modal-title" id="myModalLabel">Are
							you sure, Sir?</h5>
					</div>

					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"
											ng-click="deleteRole();">Yes</button>
										<button class="btn btn-default" type="button"
											ng-click="resetPopupData()">No</button>
									</div>
								</div>
								<div class="form" ng-hide="(roleDelete==0)">
									<section>
										<output>Deleted Role Id {{roleIdDelete}} successfully</output>
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
		<!-- ################################################################### End Delete Role ################################################################### -->

		<!-- ################################################################### start view permission ################################################################### -->
		<div class="modal fade" id="viewPermissionPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">View Permission
							Details</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div style="overflow: auto; height: 200px">
									<table
										class="table table-hover table-condensed table-bordered table-responsive"
										summary="Code page support in different versions of MS Windows."
										rules="groups" frame="hsides"
										ng-init="reverse=false; predicate='SECTION'" id="dvData">
										<colgroup align="center"></colgroup>
										<colgroup align="left"></colgroup>
										<colgroup span="2" align="center"></colgroup>
										<colgroup span="3" align="center"></colgroup>
										<thead>
											<tr>
												<th
													ng-click="predicate = 'USER_PERMISSION_KEY'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'USER_PERMISSION_KEY'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Permission Key</th>
												<th
													ng-click="predicate = 'SECTION'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'SECTION'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Section</th>
												<th
													ng-click="predicate = 'FUNCTION'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'FUNCTION'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Function</th>
												<th
													ng-click="predicate = 'SERVLET_URI'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'SERVLET_URI'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span><th>Servlet Uri</th>
												<th>Delete</th>

											</tr>
										</thead>
										<tbody id="vperms">
											<tr
												ng-repeat="vPermission in viewPermissions | orderBy : predicate : reverse">
												<td ng-bind="vPermission.USER_PERMISSION_KEY">{{vPermission.USER_PERMISSION_KEY}}</td>
												<td ng-bind="vPermission.SECTION">{{vPermission.SECTION}}</td>
												<td ng-bind="vPermission.FUNCTION"></td>
												<td ng-bind="vPermission.SERVLET_URI"></td>

												<td><button class="btn btn-primary btn-sm"
														data-toggle="modal"
														data-target="#deletePermissionPopupModal"
														data-dismiss="modal"
														ng-click="deletePermissionKey(vPermission)"
														style="background-color: #FC0202; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-weight: 400; font-size: 10px; display: inline-block; border-radius: 4px; align-items: flex-start;">Delete
													</button></td>

											</tr>
										</tbody>
									</table>
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
		<!-- ################################################################### End view permission ################################################################### -->
		<!-- ################################################################### start assign permission ################################################################### -->
		<div class="modal fade" id="assignPermissionPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">Assign Permission
							Details</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">
								<div style="overflow: auto; height: 400px">
									<table
										class="table table-hover table-condensed table-bordered table-responsive"
										rules="groups" frame="hsides">
										<colgroup align="center"></colgroup>
										<colgroup align="left"></colgroup>
										<colgroup span="2" align="center"></colgroup>
										<colgroup span="3" align="center"></colgroup>
										<thead>
											<tr>
													<th
													ng-click="predicate = 'USER_PERMISSION_KEY'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'USER_PERMISSION_KEY'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Permission Key</th>
												<th
													ng-click="predicate = 'SECTION'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'SECTION'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Section</th>
												<th
													ng-click="predicate = 'FUNCTION'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'FUNCTION'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Function</th>
												<th
													ng-click="predicate = 'SERVLET_URI'; reverse=!reverse"><span
													style="float: right" ng-show="reverse == 'first'"> <span
														style="position: absolute"> <i
															class="fa fa-sort-desc" id="orderArrow"></i></span> <span>
															<i class="fa fa-sort-asc" id="orderArrow"></i>
													</span></span><span style="float: right"
													ng-show="predicate == 'SERVLET_URI'"> <span
														ng-show="!reverse"> <i class="fa fa-sort-desc"
															id="orderArrow"></i></span> <span ng-show="reverse"> <i
															class="fa fa-sort-asc" id="orderArrow"></i></span>
												</span>Servlet Uri</th>
												<th>Assign</th>
											</tr>
										</thead>
										<tbody>
											<tr
												ng-repeat="(key,permission) in viewPermissionsForRoleData| orderBy:predicate:reverse">
												<td ng-bind="permission.USER_PERMISSION_KEY"></td>
												<td ng-bind="permission.SECTION"></td>
												<td ng-bind="permission.FUNCTION"></td>
												<td ng-bind="permission.SERVLET_URI"></td>
												<td>
													<button
														ng-class="{true: 'btn btn-danger btn-sm', false: 'btn btn-success btn-sm'}[permission.HAS_PERMISSION>0]"
														if="permission.HAS_PERMISSION>0"
														ng-click="togglePermissionBtn(permission)"
														ng-bind="(permission.HAS_PERMISSION>0) ? 'Remove' : 'Assign'"></button>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"
											ng-click="commitPermissions();resetPopupData()"
											data-dismiss="modal">Save</button>
										<button class="btn btn-default" type="button"
											ng-click="resetPopupData()">Reset</button>
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
		<!-- ################################################################### End assign permission ################################################################### -->
		<!-- ################################################################### start add permission ################################################################### -->
		<div data-ng-init="listPermissions()" class="modal fade"
			id="addPermissionPopupModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">View Permission
							Details</h4>
					</div>
					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">

								<div class="form-group">
									<label class="col-m-2 control-label">Section</label>
									<div class="col-m-4">


										<select name="section" ng-model="add.section" ng-click=""
											type="text" class="form-control m-bot15">
											<option selected="selected" readonly>Select Section</option>
											<option
												ng-repeat="(section_key, section_data) in listPermissions">{{section_key}}</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-m-2 control-label">Component</label>
									<div class="col-m-4">
										<select name="component" ng-model="add.component" type="text"
											class="form-control m-bot15">
											<option selected="selected" readonly>Select
												Component(Servlet)</option>
											<option value="*">*</option>
											<option
												ng-repeat="(component_key, component_data) in listPermissions[add.section]">{{component_key}}</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-m-2 control-label">Module</label>
									<div class="col-m-4">
										<select name="module" ng-model="add.module" type="text"
											class="form-control m-bot15">
											<option selected="selected" readonly>Select
												Module(Method)</option>
											<option value="*">*</option>
											<option
												ng-repeat="module_key in listPermissions[add.section][add.component]  track by $index">{{module_key}}</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-m-2 control-label">Function</label>
									<div class="col-m-4">
										<select name="Function" ng-model="add.function_p" type="text"
											class="form-control m-bot15">
											<option selected="selected" readonly>Select Function</option>
											<option value="create">Create</option>
											<option value="read">Read</option>
											<option value="update">Update</option>
											<option value="delete">Delete</option>

										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-m-2 control-label">Servlet URI</label>
									<div class="col-m-4">
										<input name="servleturi" class="form-control"
											ng-model="add.servleturi"
											ng-value="add.section+':'+add.function_p+':'+add.component+':'+add.module"
											type="text" />
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<button
											ng-disabled="!(!!add.section||!!add.function_p||!!add.servleturi  || isLoading())"
											class="btn btn-default" type="button"
											ng-click="addPermissions(add, (add.component+':'+add.module));resetPopupData()"
											data-dismiss="modal">Save</button>
										<button
											ng-disabled="!(!!add.section||!!add.function_p||!!add.servleturi  || isLoading())"
											class="btn btn-default" type="button"
											ng-click="resetPopupData()">Reset</button>
									</div>
								</div>

								<div class="form" ng-hide="(addpermission==0)">
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
		<!-- ################################################################### End add permission ################################################################### -->
		<!-- Start Delete Permission Popup-->
		<div class="modal fade" id="deletePermissionPopupModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">

						<h5 align="center" class="modal-title" id="myModalLabel">Are
							you sure, Sir?</h5>
					</div>

					<div class="modal-body">
						<section class="panel">
							<form name="form" class="form-horizontal" role="form">

								<div class="form-group">
									<div align="center" class="col-sm-offset-2 col-sm-10">
										<button class="btn btn-default" type="button"
											ng-click="deletePermission();">Yes</button>
										<button class="btn btn-default" type="button"
											ng-click="resetPopupData()">No</button>
									</div>
								</div>
								<div class="form" ng-hide="(permissionDelete==0)">
									<section>
										<output>Permission Id {{permissionIdDelete}} deleted
											successfully</output>
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
		<!-- ################################################################### End Delete Permission ################################################################### -->
	</div>
</div>