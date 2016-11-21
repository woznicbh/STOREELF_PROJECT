              <!-- sidebar menu start-->
              <ul class="sidebar-menu" id="nav-accordion">
                  <li class="sub-menu">
                      <a href="/Dashboard/javascript:;" >
                          <i class="fa fa-laptop"></i>
                          <span>StoreElf.com Metrics</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="StoreElfComStatisticsController" href="/Dashboard/StoreElfComMetrics/quick_metrics">Quick Metrics</a></li>
						<li><a app-controller-label="StoreElfComVisualStatisticsController" href="/Dashboard/StoreElfComMetrics/quick_metrics_visuals"><!-- <i class="fa fa-warning"></i> --> Visual Metrics</a></li>
						<li><a app-controller-label="HeatMapController" href="/Dashboard/StoreElfComMetrics/heat_maps">Heat Maps</a></li>
						<!--  Business team is not using this screen -->
						<!--  <li><a app-controller-label="WarehousePurgingController" href="/Dashboard/StoreElfComMetrics/warehouse_purging">Warehouse Purging</a></li> -->
						<!-- <li><a app-controller-label="WarehouseManagementController" href="/Dashboard/StoreElfComMetrics/warehouse_management">Warehouse Management</a></li> -->
						<li><a app-controller-label="WarehouseManagementController" target="_blank" href="https://docs.google.com/spreadsheets/d/1h1iw2mKJfx4dU6tuSx_-YOjrHLrtIFFjuIjXIisN4jY/edit?ts=564bae86#gid=180254999">Warehouse Management</a></li>
						
						<li><a app-controller-label="CancelStatisticsController" href="/Dashboard/StoreElfComMetrics/cancel_dashboard"><!-- <i class="fa fa-warning"></i> --> Cancel Dashboard</a></li>
                      </ul>
                  </li>

                  <li class="sub-menu">
                      <a href="/Dashboard/javascript:;" >
                          <i class="fa fa-book"></i>
                          <span>Store Metrics</span>
                      </a>
                      <ul class="sub">
                          <%-- <li><a href="/Dashboard/StoreMetrics/quick_metrics">Store Quick Metrics</a></li> --%>
                          <li><a app-controller-label="StoreStatisticsController" href="/Dashboard/StoreMetrics/all_store_fulfillment_performance">Store Fulfillment Perf</a></li>
                      </ul>
                  </li>

              	  <li class="sub-menu">
                      <a href="/Dashboard/javascript:;" >
                          <i class="fa fa-cloud"></i>
                          <span>MarketPlace Metrics</span>
                      </a>
                      <ul class="sub">
						<li><a app-controller-label="MarketplaceStatisticsController" href="/Dashboard/MarketPlaceMetrics/quick_metrics">Quick Metrics</a></li>
						<li><a app-controller-label="SalesController" href="/Dashboard/MarketPlaceMetrics/sales_metrics">Sales Metrics</a></li>
                      </ul>
                  </li>
              </ul>
              <!-- sidebar menu end-->