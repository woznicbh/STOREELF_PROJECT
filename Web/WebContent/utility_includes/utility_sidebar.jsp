  <%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
  <!-- sidebar menu start-->
              <ul class="sidebar-menu" id="nav-accordion">
                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>Order Management</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="ItemSearchController" href="/Utility/OrderManagement/item">Item</a></li>
						<li><a app-controller-label="OrderSearchController" href="/Utility/OrderManagement/order">Order</a></li>
						<li><a app-controller-label="OrderStatusSearchController" href="/Utility/OrderManagement/order_status">Order Status</a></li>
						<li><a app-controller-label="OrderReleaseSearchController" href="/Utility/OrderManagement/order_release">Order Release</a></li>
						<li><a app-controller-label="OrderReleaseHistorySearchController" href="/Utility/OrderManagement/order_release_history">Order Release History</a></li>
						<li><a app-controller-label="InventorySearchController" href="/Utility/OrderManagement/inventory">Inventory</a></li>
						<li><a app-controller-label="InventoryAuditSearchController" href="/Utility/OrderManagement/inventory_audit">Inventory Audit</a></li>
						<li><a app-controller-label="ShipmentDetailsSearchController" href="/Utility/OrderManagement/shipment_details">Shipment Details</a></li>
						<li><a app-controller-label="PickticketSearchController" href="/Utility/OrderManagement/pickticket">Pickticket</a></li>
						<li><a app-controller-label="InvoiceSearchController" href="/Utility/OrderManagement/invoice">Order Invoice</a></li>
						<li><a app-controller-label="SafetyFactorSearchController" href="/Utility/OrderManagement/customer_email">Customer Email</a></li>
						<li><a app-controller-label="QueueStatusController" href="/Utility/OrderManagement/mqstatus">MQ Status</a></li>
						<li><a app-controller-label="SimDashboardController" href="/Utility/OrderManagement/sim_dashboard">SIM Dashboard</a></li>
						<li><a app-controller-label="OrderExceptionController" href="/Utility/OrderManagement/order_exception">Order Exception</a></li>
						<li><a app-controller-label="ChubInvoiceSearchController" href="/Utility/OrderManagement/chub_invoice">CHUB Invoice</a></li>
                      	<li><a app-controller-label="SafetyFactorSearchController" href="/Utility/OrderManagement/safety_factor">Safety Factor</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>Warehouse Transfer</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="TransferOrderController" href="/Utility/WarehouseTransfer/transfer_orders">Transfer Orders</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>Warehouse Management</span>
                      </a>
                      <ul class="sub">
                      	<!--  Commenting untill we figure out the table for 2012 version -->
						<%-- <li><a app-controller-label="ShipViaController" href="/Utility/WarehouseManagement/ship_via">Ship Via</a></li> --%>
						<!-- Pickticket Detail name has been changed to Distribution Order Detail -->
						<li><a app-controller-label="PicketDetailContoller" href="/Utility/WarehouseManagement/distribution_order_detail">Distribution Order Detail</a></li>
						<li><a app-controller-label="FindCollatePrintTimesController" href="/Utility/WarehouseManagement/collate_print_times">Collate Print Times</a></li>
						<li><a app-controller-label="ShipViaController" href="/Utility/WarehouseManagement/ship_via">Ship Via</a></li>
						<!-- Carton Detail name has been changed to LPN Detail -->
						<li><a app-controller-label="CartonDetailController" href="/Utility/WarehouseManagement/lpn_detail">LPN Detail</a></li>
						<li><a app-controller-label="TaskDetailController" href="/Utility/WarehouseManagement/task_detail">Task Detail</a></li>
						<li><a app-controller-label="ProshipLookupController" href="/Utility/WarehouseManagement/proship_container_lookup">Proship Container Lookup</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>Sourcing Detail</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="UnitCapacityController" href="/Utility/SourcingDetail/sourcing_unit_capacity">Store Unit Capacity</a></li>
						<li><a app-controller-label="SourcingRuleController" href="/Utility/SourcingDetail/sourcing_rule_details">Sourcing Rule Details</a></li>
						<li><a app-controller-label="DistributionGroupController" href="/Utility/SourcingDetail/distribution_group_details">Distribution Group Details</a></li>
                      </ul>
                  </li>
                   <li class="sub-menu">
                      <a href="javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>Admin Functions</span>
                      </a>
                      <ul class="sub">
						<shiro:hasRole name="Order Cancel"><li><a app-controller-label="AdminFunctionsController" href="/Utility/AdminFunctions/order_cancel">Order Cancel</a></li></shiro:hasRole>
                      	<li><a app-controller-label="AdminFunctionsController" href="/Utility/AdminFunctions/reprint_label">Reprint Label</a></li>
                      
                      </ul>
                  </li>
              </ul>
              <!-- sidebar menu end-->
