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
			<a href="#" class="logo">Log<span>Hub</span></a>
 
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
					
                  	<%-- <shiro:hasPermission name="dashboard:read:*:*">
                     	 <li><a href="/Dashboard/StoreElfComMetrics/quick_metrics_visuals"><i class="fa fa-dashboard"></i><div class="headTitle">Dashboard</div></a>
	                      	 <shiro:hasRole name="Administrator">
	                      	 <ul class="dropdown-menu" style="top:75px">
	                      	 	<li><a href="/Dashboard/ServerStatistics/SQL_thread_status">Dashboard SQL Thread Status</a></li>
	                      	 </ul>
	                      	</shiro:hasRole>
                     	 </li>
                    </shiro:hasPermission> --%>
		           <%--  <shiro:hasPermission name="dashboard:read:*:*">
		            	<li><a href="/Dashboard/StoreElfComMetrics/quick_metrics_visuals"><i class="fa fa-dashboard"></i><div class="headTitle"> Dashboard</div></a></li>
		            </shiro:hasPermission> --%>
		            <shiro:hasPermission name="report:read:*:*"> <li><a href="/Report/Report/reports#/Filter?group=Daily&type=DailyShippedUnitsReport"><i class="fa fa-folder-open"></i><div class="headTitle"> View Reports</div></a></li></shiro:hasPermission>
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
	                         
	                         <shiro:hasPermission name="utility:read:WarehouseTransfer:*">
	                         	<li><a href="/Utility/WarehouseTransfer/transfer_orders">Warehouse Transfer</a></li>
	                         </shiro:hasPermission>
	                         
	                         <shiro:hasPermission name="utility:read:WarehouseManagement:*">
	                         	 <li class ="subItem"><a href="/Utility/WarehouseManagement/distribution_order_detail">Warehouse Management</a>
	                         	 	<ul class="dropdown-menu" style="top:50px">
	                      				<!--  Commenting untill we figure out the table for 2012 version -->
	<%--                          	 		<li><a href="/Utility/WarehouseManagement/ship_via">Ship Via</a></li> --%>
										<!-- Pickticket Detail name has been changed to Distribution Order Detail -->
										<li><a href="/Utility/WarehouseManagement/distribution_order_detail">Distribution Order Detail</a></li>
										<li><a href="/Utility/WarehouseManagement/collate_print_times">Collate Print Times</a></li>
										<li><a href="/Utility/WarehouseManagement/ship_via">Ship Via</a></li>
										<!-- Carton Detail name has been changed to LPN Detail -->
										<li><a href="/Utility/WarehouseManagement/lpn_detail">LPN Detail</a></li>
										<li><a href="/Utility/WarehouseManagement/task_detail">Task Detail</a></li>
										<li><a href="/Utility/WarehouseManagement/proship_container_lookup">Proship Container Lookup</a></li>
								        <!-- <li><a href="/Pace/Audit/ptt_audit_system">PTT Audit System</a></li> -->
	                         	 	</ul>
	                         	 </li>
	                         </shiro:hasPermission>
	                         
	                         <shiro:hasPermission name="utility:read:SourcingDetail:*">
                         	 <li class ="subItem"><a href="/Utility/SourcingDetail/sourcing_unit_capacity">Sourcing Detail</a>
	                         	 <ul class="dropdown-menu" style="top:75px">
	                         	 	<li><a href="/Utility/SourcingDetail/sourcing_unit_capacity">Store Unit Capacity</a></li>
									<li><a href="/Utility/SourcingDetail/sourcing_rule_details">Sourcing Rule Details</a></li>
									<li><a href="/Utility/SourcingDetail/distribution_group_details">Distribution Group Details</a></li>
	                         	 </ul>
                         	 </li>
                         	 </shiro:hasPermission>
                         	 
                         	 <shiro:hasPermission name="utility:read:AdminFunctions:*">
                         	 <li class ="subItem"><a>Admin Functions</a>
	                         	 <ul class="dropdown-menu" style="top:125px">
	                         	 	
	                         	 	<li><a href="/Utility/AdminFunctions/reprint_label">Reprint Label</a></li>
	                         	 	
	                         	 	<li><a href="/Utility/AdminFunctions/order_cancel">Order Cancel</a></li>
	                         	 </ul>
                         	 </li>
                         	 </shiro:hasPermission>
                         	 
                          </ul>
                      </li>
                      
                      <li><a href="/Management/BusinessManagement/item_attribute"><i class="fa fa-bolt"></i> <div class="headTitle">Management</div></a></li>
                      
                      <shiro:hasPermission name="environment:read:*:*">
		             	<li><a href="/Environment/ServerStatistics/app"><i class="fa fa-cogs"></i><div class="headTitle"> Environment</div></a></li>
		             </shiro:hasPermission>
					
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
						<li class="dropdown">
							<a  data-hover="dropdown" id="tester" href="/Logging/LoggingStatistics/splunk_quickview"><i class="fa fa-table"></i><div class="headTitle">Logging <b class=" fa fa-angle-down"></b></div></a>
							<ul class="dropdown-menu">
								<li class="subItem"><a href="/Logging/LoggingStatistics/SQL_thread_status">SQL Thread Status</a></li>	
								<li class="subItem"><a href="/Logging/LoggingStatistics/splunk_quickview">Splunk Quickview</a></li>		
							</ul>
						</li>
					
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

