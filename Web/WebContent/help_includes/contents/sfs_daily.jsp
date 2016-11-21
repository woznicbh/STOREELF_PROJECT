<div>
<table  class="panel" width="100%">
        <tr>
          <td>
          <h5> <b>Order Releases by Order (Daily) :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous Calendar Day</h6>
		 <h6> <b>Description : </b> <br>This report displays the same data points as in the weekly version of the Order Releases by Order report. The report looks back at the previous calendar day's data.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Daily Stores Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous Calendar Day</h6>
		 <h6> <b>Description : </b> <br>This report displays a list of stores that are part of the OCF network
		  with different demand, fulfillment, cancellation and backlog numbers for the last 
		  calendar day listed against each store. The list displays parameters such as number of orders 
		  and units sent to the store in the last calendar day, the number of orders cancelled, the number
		   of shipments shipped, the average number of units in each shipment and outbound shipments per order,
		    average fulfillment time for each store, a backlog number of orders and a split of the orders
		     that were shipped with 2 days and after 3 days. 
		 The dollar value of items shipped is also displayed.</h6>  
          </td>
        </tr><tr>
          <td>
          <h5> <b>Stores Fulfillment Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous Calendar Day</h6>
		 <h6> <b>Description : </b> <br>
		 This report displays the aggregate number of units sourced from stores on the previous calendar day, the number of orders that generated the demand and a breakup of what type of orders were they - single or multi and regular or priority. The report also summarizes some high level statistics - the number of units shipped the previous day, the number of units cancelled and the backlog number of units on all stores as of the report run time.

A separate table lists the demand units, processed units, backlog units, number of orders processed and cancelled units by stores and it expands the summarised data from the first section of the report. 

This report only displays information for orders that were created within the last 14 days for better performance of the reporting queries.
		 </h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Ecommerce Transportation Report - Stores and RDC :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous Calendar Day</h6>
		 <h6> <b>Description : </b> <br>This report displays a by store report of the number of outbound shipment containers and units. The data is classified by store and by shipping method (UPS Ground or UPS Next day etc.) for each store. The same data is displayed for RDCs in a separate tab. 

This report only displays shipment information for orders that were created within the last 14 days for better performance of the reporting queries.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Overdue Orders Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>As of midnight Yesterday</h6>
		 <h6> <b>Description : </b> <br>This report shows a list of all orders that are older than 3 days and not shipped as of the report creation time (around midnight every day). It is a snapshot and does not look at the entire day yesterday or any length of time. However, only orders created in the last 14 days will be considered in this report.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Executive Statistics Report (Daily) :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Yesterday</h6>
		 <h6> <b>Description : </b> <br>This report shows high level demand, fulfillment, in progress and settlement (units or dollars) at each EFC, RDC and one "All Stores" group. This report also lists the backlog at the same location groups. Each EFC and each RDC will be separately reported, while stores will be abstracted to an "all stores" level. This report will be created during the non peak time of the year. </h6>
		 <h6> The first section of the report shows the demand statistics and settlement statistics. The total units demanded at each EFC, each RDC and all stores together can be seen along with the percentage to the total units in the network. Across the same nodes, the number of orders settled as well as the dollar value of the settlements will also be displayed. All these data would be displayed for the previous calendar day. </h6>
		 <h6> The next section in this report shows the in process inventory - for EFCs as "Sent to EFC" or "Shipped". For RDCs/Stores - In process inventory will show "Pack in Progress", "Packed" or "Shipped".</h6>
		 <h6>The last section on this report is for backlogs - Orders will be shown separated into buckets - less than 2 days old, 2 to 5 days old and more than 5 days old. This data is accurate to the time the report was run and considers all orders created in the last 14 days.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Store Order Aging Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Rolling Last 7 days</h6>
		 <h6> <b>Description : </b> <br>This report displays the average age of orders that were shipped during each of the last 7 days. This report shows the total number of orders shipped each day during the last 7 days and also the percentage on each day that was shipped on the same calendar day, the next calendar day, the second calendar day etc. so on till shipped on the 7th calendar day or later. The last column shows the percentage of orders that were shipped within 2 calendar days (and not 48 hours) - which shows all orders that were shipped on the same day or on the first day.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Loss Prevention - Shipment Weight Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous calendar Day</h6>
		 <h6> <b>Description : </b> <br>This report displays a list of all shipments from yesterday across all stores that were heavier when shipped by a certain threshold than a calculated weight. The calculated weight uses SKU weights and the actual weight is obtained from the scales when the shipping labels are created.</h6>  
          </td>
        </tr>
        <tr>
          <td>
          <h5> <b>Daily Summary Report :</b></h5>
          <h6><b>Frequency : </b>Daily</h6>
          <h6><b> Reported Time : </b>Previous calendar Day  (Midnight on yesterday - 7th day to Midnight yesterday)</h6>
		  <h6> <b>Description : </b> <br>This report displays day by day data for each of the last 7 days for all stores in the Omni Channel network. This report has 5 distinct sections. All the sections of this report show only orders that were created in the last 14 days for better performance and faster processing. Each of them is described below -</h6>
		  <h6> <b>Order Statistics - All Stores :</b>This section shows the end of the day picture of orders at stores by shipment status (Awaiting Pick List Print, Shipment Cancelled, Shipment Pack In Progress, Shipment Pick List Printed, Shipment Shipped, Shipment Packed) in the reverse chronological order (yesterday first, day before next etc.).
				Fully cancelled shipments will appear in the cancelled units line against each day. Any partially cancelled units can be determined from the difference in "Original Quantity" and "Shipped Quantity" in the shipped status row.</h6>
		  <h6><b> Incoming Order Statistics : </b> This section shows by date the number of units and number of orders that were sent for fulfillment to all stores each day.</h6>		  
          <h6><b>Outgoing Shipments - Fulfillment Statistics :</b> This section shows the number of outgoing shipments, shipment container, the total units shipped and the average fulfillment time for all stores by reverse chronological order of dates.</h6>
          <h6><b>Incoming Orders - Unit Count Statistics : </b> This section of the report shows the the distribution of the orders across each day by the number of units in each order - 1 unit orders, 2 unit orders, 3 unit orders, 4 unit orders, 5 unit orders and greater than 5 unit orders.</h6>
          <h6><b>Incoming Orders - Split Order Statistics : </b> This section displays the number of split orders per day across all stores. The definition of split orders is - any order that has been sourced from more than one node and at least one of the nodes is a store (except if one of the node is EFC and the items sent to the EFC are ship alone items).</h6>
          <h6><b>Overdue Orders List :</b> This section shows a list of all orders that are older than 3 days and not shipped yet.</h6>
          </td>
        </tr>
      </table>
</div>