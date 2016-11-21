              <!-- sidebar menu start-->
              <ul class="sidebar-menu" id="nav-accordion">
                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-user"></i>
                          <span>User Management</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="UserSearchController" href="/Security/UserManagement/usermanagement">User Management</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-group"></i>
                          <span>Group Management</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="GroupSearchController" href="/Security/GroupManagement/groupmanagement">Group Management</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-info-circle"></i>
                          <span>Role Management</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="RoleSearchController" href="/Security/RoleManagement/rolemanagement">Role Management</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-clock-o"></i>
                          <span>Session Management</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="StatisticsController" href="/Security/UserManagementServlet/session">Session Management</a></li>
                      </ul>
                  </li>
              </ul>
              <!-- sidebar menu end-->