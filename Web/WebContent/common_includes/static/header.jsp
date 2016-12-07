<script type="text/javascript">
/*function TimeCtrl($scope, $timeout) {
    $scope.clock = "loading clock..."; // initialise the time variable
    $scope.tickInterval = 1000 //ms

    var tick = function() {
        $scope.clock = Date.now() // get the current time
        $timeout(tick, $scope.tickInterval); // reset the timer
    }

    // Start the timer
    $timeout(tick, $scope.tickInterval);
}*/

/*function TimeCtrl()
{
int k=application.getAttribute("TimeUp");
setInterval(setTime, 1000);
System.out.println("TimeCtrl Func");

function setTime()
{
    ++k;
    System.out.println("SetTime Func "+k);
    document.getElementById("timer").innerHTML=k; 
    //secondsLabel.innerHTML = pad(totalSeconds%60);
    //minutesLabel.innerHTML = pad(parseInt(totalSeconds/60));
}
}*/

//ANGULARJS_APP = angular.module('TimerApp',[]);

	var app = angular.module('TimerApp', []);
	app.controller('Timer', function($scope) {
	    $scope.time = function() {
	        return application.getAttribute("TimeUp");
	    };
	   
	});
	
 	</script>



<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!-- 		<div class="sidebar-toggle-box">
				<div data-original-title="Toggle Navigation" data-placement="right" class="icon-reorder tooltips"></div>
			</div> -->
			<div class="sidebar-toggle-box pull-left">
                  <div class="fa fa-bars tooltips" data-placement="right" data-original-title="Toggle Navigation"></div>
            </div>

			<!--logo start-->
			<a href="#" class="logo">Store<span>Elf</span></a>
 
			<div class="horizontal-menu navbar-collapse collapse ">
                  <ul class="nav navbar-nav">
                  
                  	<shiro:hasPermission name="dashboard:read:*:*">		            			
						<li class="dropdown">
							<a  data-hover="dropdown"  href="/Dashboard/StoreElfComMetrics/quick_metrics_visuals"><i class="fa fa-dashboard"></i><div class="headTitle"> Dashboard <b class=" fa fa-angle-down"></b></div></a>
							<shiro:hasRole name="Administrator">
							<ul class="dropdown-menu">
								<li class="subItem"><a href="/Dashboard/ServerStatistics/SQL_thread_status">Dashboard SQL Thread Status</a></li>
							</ul>
							</shiro:hasRole>
						</li>
					</shiro:hasPermission>

		             <li class="dropdown">
                          <a  data-hover="dropdown" id="tester" href="/Utility/OrderManagement/item"><i class="fa fa-briefcase"></i><div class="headTitle"> Utility <b class=" fa fa-angle-down"></b></div></a>
                          <ul class="dropdown-menu">                         
                          
                          	<shiro:hasPermission name="utility:read:WarehouseTransfer:*">
                          		<shiro:hasRole name="Administrator"><li  class ="subItem"><a href="/Utility/ServerStatistics/SQL_thread_status">Utility SQL Thread Status</a></li></shiro:hasRole>
	                          	<li class ="subItem"><a href="/Utility/OrderManagement/item">Order Management</a>
		                          <ul class="dropdown-menu">
		                              	<li><a id="control" href="/Utility/OrderManagement/item">Item</a></li>
										<li><a href="/Utility/OrderManagement/order">Order</a></li>
										<li><a href="/Utility/OrderManagement/order_status">Order Status</a></li>
										<li><a href="/Utility/OrderManagement/order_release">Order Release</a></li>
										<li><a href="/Utility/OrderManagement/order_release_history">Order Release History</a></li>
										<li><a href="/Utility/OrderManagement/inventory">Inventory</a></li>
										<li><a href="/Utility/OrderManagement/inventory_audit">Inventory Audit</a></li>
										<li><a href="/Utility/OrderManagement/shipment_details">Shipment Details</a></li>
										<li><a href="/Utility/OrderManagement/pickticket">Pickticket</a></li>
										<li><a href="/Utility/OrderManagement/invoice">Order Invoice</a></li>
										<li><a href="/Utility/OrderManagement/customer_email">Customer Email</a></li>
										<li><a href="/Utility/OrderManagement/mqstatus">MQ Status</a></li>
										<li><a href="/Utility/OrderManagement/sim_dashboard">SIM Dashboard</a></li>
										<li><a href="/Utility/OrderManagement/order_exception">Order Exception</a></li>
										<li><a href="/Utility/OrderManagement/chub_invoice">CHUB Invoicing Details</a></li>
										<li><a href="/Utility/OrderManagement/safety_factor">Safety Factor</a></li>
										
		                          </ul>
	                         </li>
	                         </shiro:hasPermission>
                         	 
                          </ul>
                      </li>
					
					<shiro:hasPermission name="security:read:*:*">		            			
						<li class="dropdown">
							<a  data-hover="dropdown" id="tester" href="/Security/UserManagement/usermanagement"><i class="fa fa-lock"></i><div class="headTitle"> Security <b class=" fa fa-angle-down"></b></div></a>
							<ul class="dropdown-menu">
								<li class="subItem"><a href="/Security/UserManagement/usermanagement">User Management</a></li>
								<li><a href="/Security/GroupManagement/groupmanagement">Group Management</a></li>
								<li class="subItem"><a href="/Security/RoleManagement/rolemanagement">Role Management</a></li>
								<li class="subItem"><a href="/Security/SessionManagement/sessionmanagement">Session Management</a></li>				
							</ul>
						</li>
					</shiro:hasPermission>
					
		            <shiro:hasPermission name="help:read:*:*">
		            	<li><a href="/Help"><i class="fa fa-question-circle"></i><div class="headTitle"> Help</div></a></li>
		            </shiro:hasPermission>
		            
		                		            
                  </ul>
              </div>
              
				
			<div class="nav notify-row" id="top_menu">
				<!--  notification goes here -->
			</div>
			<div class="top-nav ">
				<!--search & user info goes here-->
				<%-- <jsp:include page="/common_includes/static/top_menu.jsp"/> --%>
				<!--search & user info start-->
                <ul class="nav pull-right top-menu">
                    <!-- user login dropdown start-->
                    <li class="dropdown">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                            <!-- <img alt="" src="img/avatar1_small.jpg"> -->
                            <i class=" fa fa-user"></i>
                            <span class="username"><shiro:principal/><shiro:hasRole name="Administrator">(Admin)</shiro:hasRole>
                           	</span>
                            <b class="caret"></b>
                        </a>
                        <ul class="dropdown-menu extended logout">
                            <div class="log-arrow-up"></div>
<!--                        <li><a href="#"><i class=" fa fa-suitcase"></i>Profile</a></li>
                            <li><a href="#"><i class="fa fa-cog"></i> Settings</a></li>
                            <li><a href="#"><i class="fa fa-bell-o"></i> Notification</a></li> -->
                            <li><a href="/Security/Authenticate/logout"><i class="fa fa-key"></i> Log Out</a></li>
                        </ul>
                    </li>
                    
                    <!-- user login dropdown end -->
                </ul>
                <!--search & user info end-->
			</div>

