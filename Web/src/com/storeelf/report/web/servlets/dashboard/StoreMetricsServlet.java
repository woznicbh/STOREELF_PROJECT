package com.storeelf.report.web.servlets.dashboard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.impl.GenericCountModel;
import com.storeelf.report.web.model.impl.MultiColumnModal;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;

/**
 * Servlet implementation class OrderManagementServlet
 *
 * each *Response method MUST have the following types as arguments in this order:
 * ExampleResponse(String page, HttpServletRequest rq, HttpServletResponse rs)
 *
 * @author tkmagh4
 * @web.servlet
 *   name=OrderManagementServlet
 */
public class StoreMetricsServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(StoreMetricsServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/dashboard_includes/dashboard.jsp";


    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoreMetricsServlet() {
        super();
    }

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void all_store_fulfillment_performance(String requestedPage, HttpServletRequest request, HttpServletResponse response){

		String			chart_type			= null;
		String			response_content 	= "-error-";
		PrintWriter		responseWriter		= null;
		JsonObject      root_return         = new JsonObject();
		Gson            gson            = new GsonBuilder().create();
		String			jsp_include_page	= "/dashboard_includes/store_metrics/all_store_fulfillment_performance.jsp";

		logger.log(Level.INFO, "StoreMetricsServlet : allStoreFulfillmentPerformanceResponse | " + requestedPage + "|" + request.getParameter("chart"));

		try{
			if(StringUtils.equals(request.getMethod(),"POST")){

				//handle request parameters here
				chart_type		= request.getParameter("chart");
				responseWriter	= response.getWriter();

				//content type MUST be "application/json"
				response.setContentType("application/json");


				if (!com.storeelf.util.StringUtils.isVoid(chart_type)) {
					if("fulfillment".equals(chart_type)){								root_return	= generateFulfillmentChart();}
					//Chart type 2
					//Chart type 3
					//Etc..

				}else if("fullfillment_performance".equals(chart_type)){
					response_content	= fullfillmentPerformance();
				}

				response_content = gson.toJson(root_return);

				//write content to response writer, flush before closing ... trust me on this one ...
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				//assume it's GET request, load JSP
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		//handle EVERY exception!
		catch (IOException e)			{e.printStackTrace(); logger.error("error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
	}

	public JsonObject generateFulfillmentChart() throws FileNotFoundException, SQLException, ClassNotFoundException, IOException{

		Connection		conOMS			 	 = null;
		String          store_state_sql  	 = null;
		String          state_region_sql 	 = null;
		JsonObject      root_return      	 = new JsonObject();
		HashMap<String,String> store_state   = new HashMap<String,String>();
		HashMap<String,String> state_region  = new HashMap<String,String>();


		conOMS = ReportActivator.getInstance().getConnection(Constants.OMS);

		ConcurrentHashMap<Integer, HashMap<String, Object>> storeRegionFulfillList = new ConcurrentHashMap<Integer, HashMap<String, Object>>();
		ConcurrentHashMap<Integer, HashMap<String, Object>> storeStatesFulfillList = new ConcurrentHashMap<Integer, HashMap<String, Object>>();
		ConcurrentHashMap<Integer, HashMap<String, Object>> storeFulfillList = new ConcurrentHashMap<Integer, HashMap<String, Object>>();
		String sqlName = "";
		String commonTime = "";
		try{

			store_state_sql =     " WITH REGIONS"
						+ " AS (SELECT r.REGION_NAME, r.region_key"
						+ " FROM STERLING.YFS_REGION r"
						+ " WHERE     r.REGION_SCHEMA_KEY = 'ALL_US'"
						+ " AND r.REGION_LEVEL_NAME = 'Region'"
						+ " AND r.region_name <> 'APOFPO'),"
						+ " STATES"
						+ " AS (SELECT r2.region_name AS state,"
						+ " r2.REGION_KEY AS stateid,"
						+ " r.region_key AS regionid"
						+ " FROM regions r, yfs_region r2"
						+ " WHERE     r2.REGION_SCHEMA_KEY = 'ALL_US'"
						+ " AND r2.region_level_name = 'State'"
						+ " AND r2.PARENT_REGION_KEY = r.region_key"
						+ " AND LENGTH (TRIM (r2.REGION_NAME)) = 2),"
						+ " STORES"
						+ " AS (SELECT s.description || ' - ' || s.SHIPNODE_KEY AS storename,"
						+ " s.SHIPNODE_KEY AS storeid,"
						+ " states.stateid"
						+ " FROM yfs_ship_node s,"
						+ " yfs_person_info p,"
						+ " STATES states"
						+ " WHERE     s.NODE_TYPE = 'STORE'"
						+ " AND p.PERSON_INFO_KEY = s.SHIP_NODE_ADDRESS_KEY"
						+ " AND states.state = TRIM (p.STATE))"
						+ " SELECT st.storename, s.state"
						+ " FROM regions r, states s, stores st"
						+ " where s.stateid = st.stateid"
						+ " group by st.storename, s.state order by s.state";

			ConcurrentHashMap<Integer, HashMap<String, Object>> store_state_results = SQLUtils.getSQLResult(store_state_sql, conOMS);

			for (HashMap<String, Object> map : store_state_results.values()) {
				store_state.put(String.valueOf(map.get("STORENAME")).trim(), String.valueOf(map.get("STATE")).trim());
			}

			state_region_sql = " WITH REGIONS"
					+ " AS (SELECT r.REGION_NAME, r.region_key"
					+ " FROM STERLING.YFS_REGION r"
					+ " WHERE     r.REGION_SCHEMA_KEY = 'ALL_US'"
					+ " AND r.REGION_LEVEL_NAME = 'Region'"
					+ " AND r.region_name <> 'APOFPO'),"
					+ " STATES"
					+ " AS (SELECT r2.region_name AS state,"
					+ " r2.REGION_KEY AS stateid,"
					+ " r.region_key AS regionid"
					+ " FROM regions r, yfs_region r2"
					+ " WHERE     r2.REGION_SCHEMA_KEY = 'ALL_US'"
					+ " AND r2.region_level_name = 'State'"
					+ " AND r2.PARENT_REGION_KEY = r.region_key"
					+ " AND LENGTH (TRIM (r2.REGION_NAME)) = 2),"
					+ " STORES"
					+ " AS (SELECT s.description || ' - ' || s.SHIPNODE_KEY AS storename,"
					+ " s.SHIPNODE_KEY AS storeid,"
					+ " states.stateid"
					+ " FROM yfs_ship_node s,"
					+ " yfs_person_info p,"
					+ " STATES states"
					+ " WHERE     s.NODE_TYPE = 'STORE'"
					+ " AND p.PERSON_INFO_KEY = s.SHIP_NODE_ADDRESS_KEY"
					+ " AND states.state = TRIM (p.STATE))"
					+ " SELECT r.region_name, s.state"
					+ " FROM regions r, states s, stores st"
					+ " WHERE r.region_key = s.regionid"
					+ " group by r.region_name, s.state order by r.region_name";
			
			conOMS = ReportActivator.getInstance().getConnection(Constants.OMS);
			ConcurrentHashMap<Integer, HashMap<String, Object>> state_region_results = SQLUtils.getSQLResult(state_region_sql, conOMS);

			for (HashMap<String, Object> map : state_region_results.values()) {
				state_region.put(String.valueOf(map.get("STATE")).trim(), String.valueOf(map.get("REGION_NAME")).trim());
			}

			Constants.ID_STORE_SQL = Constants.SQL_MAP.get(Constants.ID_STORE_REGION_FULFILL_SQL);
			sqlName = Constants.ID_STORE_REGION_FULFILL_SQL;
			storeRegionFulfillList =  SQLUtils.getModelObject(Constants.ID_STORE_REGION_FULFILL_SQL).getResultmap();
			//Sample [{PICKUNITCNT=N/A, TTLSHPUNITCNT=1,127, EXPFULFILL=0.04, CNCLUNITCNT=73, BACKLOG=1,133, REGION=South, AVERAGE=0.89}]

			Constants.ID_STORE_SQL = Constants.SQL_MAP.get(Constants.ID_STORE_STATES_FULFILL_SQL);
			sqlName = Constants.ID_STORE_STATES_FULFILL_SQL;
			storeStatesFulfillList = SQLUtils.getModelObject(Constants.ID_STORE_STATES_FULFILL_SQL).getResultmap();
			//Sample [{PICKUNITCNT=N/A, TTLSHPUNITCNT=662, STATE=NJ, EXPFULFILL=0.03, CNCLUNITCNT=33, BACKLOG=772, AVERAGE=0.97}]

			Constants.ID_STORE_SQL = Constants.SQL_MAP.get(Constants.ID_STORE_FULFILL_SQL);
			sqlName = Constants.ID_STORE_FULFILL_SQL;
			storeFulfillList = SQLUtils.getModelObject(Constants.ID_STORE_FULFILL_SQL).getResultmap();
			//Sample [{PICKUNITCNT=N/A, TTLSHPUNITCNT=59, STORE_NAME=StoreElf of Oakland Square - 8, EXPFULFILL=0, CNCLUNITCNT=5, BACKLOG=28, AVERAGE=0.34}

			java.util.Date runts = ( SQLUtils.getModelObject(Constants.ID_STORE_FULFILL_SQL)).getLastresulttimestamp();
			SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");

			if (runts != null) {

				if(commonTime==""){
					commonTime = tsfrmt.format(runts);
				}
			}

			root_return.addProperty("run_ts", commonTime);
			/*
			 * Get a list of all the Regions
			 * -Load into array (region object + statistics for region)
			 *
			 *   For each Region
			 *	  	Get a list of all the states in the Region
			 *		-Load into array (state object + state statistics)
			 *
			 *			For each state
			 *			  Get a list of all the stores in that state that are SFS
			 *			  -Load into array (store object + store statistics)
			 *
			 */

			JsonArray  region_array = new JsonArray();
			JsonObject region       = new JsonObject();

			JsonArray  state_array  = new JsonArray();
			JsonObject state        = new JsonObject();

			JsonArray  store_array  = new JsonArray();
			JsonObject store        = new JsonObject();

			for(HashMap<String, Object> regionDetail : storeRegionFulfillList.values()){
				//get a hash of the Region Details
				//region_row = (HashMap<String,String>) regionDetail;

				if((regionDetail.get("TTLSHPUNITCNT")!=null) && (String.valueOf(regionDetail.get("TTLSHPUNITCNT"))!="")){
					region.addProperty("TTLSHPUNITCNT", String.valueOf(regionDetail.get("TTLSHPUNITCNT")));
				} else {
					region.addProperty("TTLSHPUNITCNT", "0");
				}
				if((regionDetail.get("CNCLUNITCNT")!=null) && (String.valueOf(regionDetail.get("CNCLUNITCNT"))!="")){
					region.addProperty("CNCLUNITCNT", String.valueOf(regionDetail.get("CNCLUNITCNT")));
				} else {
					region.addProperty("CNCLUNITCNT", "0");
				}
				if((regionDetail.get("BACKLOG")!=null) && (String.valueOf(regionDetail.get("BACKLOG"))!="")){
					region.addProperty("BACKLOG", String.valueOf(regionDetail.get("BACKLOG")));
				} else {
					region.addProperty("BACKLOG", "0");
				}
				if((regionDetail.get("AVERAGE")!=null) && (String.valueOf(regionDetail.get("AVERAGE"))!="")){
					region.addProperty("AVERAGE", String.valueOf(regionDetail.get("AVERAGE")));
				} else {
					region.addProperty("AVERAGE", "0");
				}
				region.addProperty("REGION", String.valueOf(regionDetail.get("REGION")));


				//for given region, find all states
				//if region-state hashmap.get(state) == this region
				//  add the state info to the state object
				//  add the state object to the state array


				for(HashMap<String, Object> stateDetail : storeStatesFulfillList.values()){
					//state_row = (HashMap<String,String>) stateDetail;
					//get the state-region pairing and see if this state is a match

					if(state_region.get(stateDetail.get("STATE")).equalsIgnoreCase(String.valueOf(regionDetail.get("REGION")))){
						//If the state belongs to the region above
						if((stateDetail.get("TTLSHPUNITCNT")!=null) && (String.valueOf(stateDetail.get("TTLSHPUNITCNT"))!="")){
							state.addProperty("TTLSHPUNITCNT", String.valueOf(stateDetail.get("TTLSHPUNITCNT")));
						} else {
							state.addProperty("TTLSHPUNITCNT", "0");
						}
						if((stateDetail.get("CNCLUNITCNT")!=null) && (String.valueOf(stateDetail.get("CNCLUNITCNT"))!="")){
							state.addProperty("CNCLUNITCNT", String.valueOf(stateDetail.get("CNCLUNITCNT")));
						} else {
							state.addProperty("CNCLUNITCNT", "0");
						}
						if((stateDetail.get("BACKLOG")!=null) && (String.valueOf(stateDetail.get("BACKLOG"))!="")){
							state.addProperty("BACKLOG", String.valueOf(stateDetail.get("BACKLOG")));
						} else {
							state.addProperty("BACKLOG", "0");
						}
						if((stateDetail.get("AVERAGE")!=null) && (String.valueOf(stateDetail.get("AVERAGE"))!="")){
							state.addProperty("AVERAGE", String.valueOf(stateDetail.get("AVERAGE")));
						} else {
							state.addProperty("AVERAGE", "0");
						}
						state.addProperty("STATE", String.valueOf(stateDetail.get("STATE")));

						for(HashMap<String, Object> storeDetail : storeFulfillList.values()){
							//store_row = (HashMap<String,String>) storeDetail;
							//get the store-state pairing and see if this store is a match

							if(store_state.get(storeDetail.get("STORE_NAME")).equalsIgnoreCase(String.valueOf(stateDetail.get("STATE")))){
								//If the store belongs to the state above
								if((storeDetail.get("TTLSHPUNITCNT")!=null) && (String.valueOf(storeDetail.get("TTLSHPUNITCNT"))!="")){
									store.addProperty("TTLSHPUNITCNT", String.valueOf(storeDetail.get("TTLSHPUNITCNT")));
								} else {
									store.addProperty("TTLSHPUNITCNT", "0");
								}
								if((storeDetail.get("CNCLUNITCNT")!=null) && (String.valueOf(storeDetail.get("CNCLUNITCNT"))!="")){
									store.addProperty("CNCLUNITCNT", String.valueOf(storeDetail.get("CNCLUNITCNT")));
								} else {
									store.addProperty("CNCLUNITCNT", "0");
								}
								if((storeDetail.get("BACKLOG")!=null) && (String.valueOf(storeDetail.get("BACKLOG"))!="")){
									store.addProperty("BACKLOG", String.valueOf(storeDetail.get("BACKLOG")));
								} else {
									store.addProperty("BACKLOG", "0");
								}
								if((storeDetail.get("AVERAGE")!=null) && (String.valueOf(storeDetail.get("AVERAGE"))!="")){
									store.addProperty("AVERAGE", String.valueOf(storeDetail.get("AVERAGE")));
								} else {
									store.addProperty("AVERAGE", "0");
								}
								store.addProperty("STORE_NAME", String.valueOf(storeDetail.get("STORE_NAME")));

								//add store to the stores array
								store_array.add(store);
							}


							//clear the store object
							store = new JsonObject();
						}
						//stores done being added to the stores array for this state
						state.add("store_array", store_array);
						//clear the stores array for the next state's work
						store_array = new JsonArray();

						//add the final state object to the root state array
						state_array.add(state);

					}

					//clear the state object
					state = new JsonObject();

				}

				//states done being added to the state array for this region
				region.add("state_array", state_array);
				//clear the state array
				state_array = new JsonArray();


				//add the final region object to the root region array
				region_array.add(region);

				//clear the region object
				region = new JsonObject();
			}

			//all done processing, add the region_array to the root_return object
			root_return.add("region_array", region_array);
		}catch (Exception e) {
			logger.error("The following SQLID errored out:" + sqlName, e);
		} finally {
			if(conOMS!=null){conOMS.close();}
		}

		return root_return;
	}


	/**
	* @param part- part to be divided
	* @param total- the ammount taking th percentage from
	* @return String formatted as HH:MM:SS
	*/
	public double percent(int part, int total){
		return Math.round(((double)part / total) * 10000) / (double)100;
	}

	//added for cancel dashboard
	public JsonObject generateCancelGlanceChart(Iterator<String> iterator){
		JsonObject root				= new JsonObject();
		JsonObject Cancel_object	= new JsonObject();
		JsonArray	rootArray		= new JsonArray();
		String commonTime							= "";
		try{
//			Iterator<String> iterator					= null;
			int icount 									= 0;

			String sqlid								= null;
			Object value								= "";
			String description							= null;
			String timestampDescriptionPrefix			= "Last Update: ";
			java.util.Date lastRunTimeStamp				= null;
			SimpleDateFormat timestampFormat			= new SimpleDateFormat("MM/dd hh:mm a");



			HashMap<Integer, HashMap<String, String>> resultMap = new HashMap<Integer, HashMap<String,String>>();

//			iterator = Constants.SQL_CNCL_FRM_1.keySet().iterator();

			HashMap<String, String> row			= new HashMap<String, String>();

			while (iterator.hasNext()) {
				sqlid = iterator.next();

				value = ((GenericCountModel) SQLUtils.getModelObject(sqlid)).getVal();
//					logger.debug(value.toString());
				if (value == null || value =="") value = '-';

				description			= timestampDescriptionPrefix + Constants.SQL_DESC.get(sqlid);
				lastRunTimeStamp	= (SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();

//				row.put("DESCRIPTION", description);
//				row.put("VALUE", value.toString());

				Cancel_object.addProperty("DESCRIPTION", description);
				Cancel_object.addProperty("VALUE", value.toString());

				rootArray.add(Cancel_object);
				Cancel_object = new JsonObject();

				resultMap.put(icount, row);

				if (lastRunTimeStamp != null) {
					if(commonTime=="") commonTime = timestampFormat.format(lastRunTimeStamp);
				}

				icount++;
//				row			= new HashMap<String, String>();
			}

			//Convert HashMap into json object
//			return gson.toJson(resultMap);
			root.add("fulfillment_data", rootArray );
			root.addProperty("last_run_timestamp", commonTime);
			}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}

		return root;
	}
	//end of cancel glance

	//starting top 10 cancelled SKU
	public String StoreElf14DayCancellationStats(){

		logger.debug("Inside 14 day cancel....");
		Iterator<String> it = Constants.SQL_CNCL_FRM_3.keySet().iterator();
		int icount = 0;
		String commonTime = "";
		String sqlid = null;
		String desc = "";

		JsonObject rootObject	= new JsonObject();
		JsonObject Cancel_object	= new JsonObject();
		Gson 		gson		= new GsonBuilder().create();


		while (it.hasNext()) {
			sqlid = it.next();
			logger.debug("SQl_ID--> " + sqlid);


			HashMap<String, Object> map = null;
			try {
				map = ((MultiColumnModal) SQLUtils
						.getModelObject(sqlid)).getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				String tsdesc = "Last Update: ";
				java.util.Date runts = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
				SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if(commonTime==""){
						commonTime = tsfrmt.format(runts);
					}
				}

				Cancel_object.addProperty("DESCRIPTION", desc);

				String ord_date = "-";
				String efc1_cancel = "-";
				String efc1_rsrc = "-";
				String efc2_cancel = "-";
				String efc2_rsrc = "-";
				String efc3_cancel = "-";
				String efc3_rsrc = "-";
				String efc4_cancel = "-";
				String efc4_rsrc = "-";
				String rdc_cancel= "-";
				String rdc_rsrc = "-";
				String stores_cancel = "-";
				String stores_rsrc = "-";

				if (map != null) {
					if(map.get("ORDERDT")!= null && map.get("ORDERDT").toString()!="")			ord_date = map.get("ORDERDT").toString();
					if(map.get("EFC1CANC")!= null && map.get("EFC1CANC").toString()!="")		efc1_cancel = map.get("EFC1CANC").toString();
					if(map.get("EFC1RSRCD") != null && map.get("EFC1RSRCD").toString()!="")		efc1_rsrc = map.get("EFC1RSRCD").toString();
					if(map.get("EFC2CANC")!=null && map.get("EFC2CANC").toString()!="")			efc2_cancel = map.get("EFC2CANC").toString();
					if(map.get("EFC2RSRCD")!= null && map.get("EFC2RSRCD").toString()!="")		efc2_rsrc = map.get("EFC2RSRCD").toString();
					if(map.get("EFC3CANC")!= null && map.get("EFC3CANC").toString()!="")		efc3_cancel = map.get("EFC3CANC").toString();
					if(map.get("EFC3RSRCD") != null && map.get("EFC3RSRCD").toString()!="")		efc3_rsrc = map.get("EFC3RSRCD").toString();
					if(map.get("EFC4CANC")!=null && map.get("EFC4CANC").toString()!="")			efc4_cancel = map.get("EFC4CANC").toString();
					if(map.get("EFC4RSRCD") != null && map.get("EFC4RSRCD").toString()!="")		efc4_rsrc = map.get("EFC4RSRCD").toString();
					if(map.get("RDCCANC") != null && map.get("RDCCANC").toString()!="")			rdc_cancel = map.get("RDCCANC").toString();
					if(map.get("RDCRSRCD") != null && map.get("RDCRSRCD").toString()!="")		rdc_rsrc = map.get("RDCRSRCD").toString();
					if(map.get("STORECANC") != null && map.get("STORECANC").toString()!="")		stores_cancel = map.get("STORECANC").toString();
					if(map.get("STORERSRCD") != null && map.get("STORERSRCD").toString()!="")	stores_rsrc = map.get("STORERSRCD").toString();

					Cancel_object.addProperty("ORDERDT", ord_date);
					logger.debug("Order Date:-->" + ord_date);
					Cancel_object.addProperty("EFC1CANC", efc1_cancel);
					Cancel_object.addProperty("EFC1RSRCD", efc1_rsrc);
					Cancel_object.addProperty("EFC2CANC", efc2_cancel);
					Cancel_object.addProperty("EFC2RSRCD", efc2_rsrc);
					Cancel_object.addProperty("EFC3CANC", efc3_cancel);
					Cancel_object.addProperty("EFC3RSRCD", efc3_rsrc);
					Cancel_object.addProperty("EFC4CANC", efc4_cancel);
					Cancel_object.addProperty("EFC4RSRCD", efc4_rsrc);
					Cancel_object.addProperty("RDCCANC", rdc_cancel);
					Cancel_object.addProperty("RDCRSRCD", rdc_rsrc);
					Cancel_object.addProperty("STORECANC", stores_cancel);
					Cancel_object.addProperty("STORERSRCD", stores_rsrc);
				}

				rootObject.add(icount+"", Cancel_object);

				Cancel_object = new JsonObject();
				icount++;
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid);
			}
		}

		return gson.toJson(rootObject);
	}//ending 14 day cancellation

	public JsonObject Top10SKUCancelled(Iterator<String> it){

			String commonTime = "";
			String sqlid = null;
			String desc = "";

			JsonObject root			= new JsonObject();
			JsonObject ITEM_Object	= new JsonObject();
			JsonArray	rootArray	= new JsonArray();

			while (it.hasNext()) {
				sqlid = it.next();

				HashMap<String, Object> map = null;
				try {
					map = ((MultiColumnModal) SQLUtils
							.getModelObject(sqlid)).getColmap();
					desc = Constants.SQL_DESC.get(sqlid);
					String tsdesc = "Last Update: ";
					java.util.Date runts = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
					SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
					if (runts != null) {
						tsdesc = tsdesc + tsfrmt.format(runts);
						if(commonTime==""){
							commonTime = tsfrmt.format(runts);
						}
					}

					ITEM_Object.addProperty("DESCRIPTION", desc);

					String cancel_qty = "-";
					String item_id = "-";
					String item_desc = "-";
					String cancel_dollar = "-";

					if (map != null) {
						if(map.get("CANCELQTY")!= null && map.get("CANCELQTY").toString()!="")				cancel_qty = map.get("CANCELQTY").toString();
						if(map.get("ITEMID")!= null && map.get("ITEMID").toString()!="")					item_id = map.get("ITEMID").toString();
						if(map.get("ITEM_DESC") != null && map.get("ITEM_DESC").toString()!="")				item_desc = map.get("ITEM_DESC").toString();
						if(map.get("CANCDELLEDDOLLAR")!=null && map.get("CANCDELLEDDOLLAR").toString()!="")	cancel_dollar = map.get("CANCDELLEDDOLLAR").toString();

						ITEM_Object.addProperty("CANCELQTY", cancel_qty);

						logger.debug("Cancelled QTY:-->" + cancel_qty);

						ITEM_Object.addProperty("ITEMID", item_id);
						ITEM_Object.addProperty("ITEM_DESC", item_desc);
						ITEM_Object.addProperty("CANCDELLEDDOLLAR", cancel_dollar);

					}
					rootArray.add(ITEM_Object);

					ITEM_Object = new JsonObject();
				} catch (Exception e) {
					logger.error("The following SQLID errored out:"
							+ sqlid, e);

				}
			}
			root.add("fulfillment_data", rootArray );
			root.addProperty("last_run_timestamp", commonTime);

			return root;
		}//ending 14 day

	public String generateGlanceChart(){
		try{
			Iterator<String> iterator					= null;
			int icount 									= 0;
			String commonTime							= "";
			String sqlid								= null;
			Object value								= "";
			String description							= null;
			java.util.Date lastRunTimeStamp				= null;
			SimpleDateFormat timestampFormat			= new SimpleDateFormat("MM/dd hh:mm a");
			Gson 		gson							= new GsonBuilder().create();

			HashMap<Integer, HashMap<String, String>> resultMap = new HashMap<Integer, HashMap<String,String>>();

			JsonObject root = new JsonObject();

			iterator = Constants.SQL_FRM_1.keySet().iterator();

			HashMap<String, String> row			= new HashMap<String, String>();

			while (iterator.hasNext()) {
				sqlid = iterator.next();

				value = ((GenericCountModel) SQLUtils.getModelObject(sqlid)).getVal();

				if (value == null || value =="") value = '-';

				description			= Constants.SQL_DESC.get(sqlid);
				lastRunTimeStamp	= (SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();

				row.put("DESCRIPTION", description);
				row.put("VALUE", value.toString());

				resultMap.put(icount, row);

				if (lastRunTimeStamp != null) {
					if(commonTime=="") commonTime = timestampFormat.format(lastRunTimeStamp);
				}

				icount++;
				row			= new HashMap<String, String>();
			}

			root.add("glance_data", gson.toJsonTree(resultMap));
			root.addProperty("glance_last_run_timestamp", commonTime );

			//Convert HashMap into json object
			return gson.toJson(root);
			}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		return "";
	}

	public String generateAllNodeInventorySnapshotChart(){
		JsonObject rootObject		= new JsonObject();

		JsonArray EFCrootArray		= new JsonArray();
		JsonArray RDCrootArray		= new JsonArray();

		JsonObject EFC_Object		= new JsonObject();
		JsonObject RDC_Object		= new JsonObject();

		Gson 		gson							= new GsonBuilder().create();

			Iterator<String> it = Constants.SQL_FRM_3.keySet().iterator(); 
			String commonTime = "";

			while (it.hasNext()) {
				String sqlid = it.next();
				String val = "";
				String desc = "";
				HashMap<String, Object> map = null;
				try {
					map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getColmap();
					desc = Constants.SQL_DESC.get(sqlid);
					String tsdesc = "Last Update: ";
					java.util.Date runts = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
					SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
					if (runts != null) {
						tsdesc = tsdesc + tsfrmt.format(runts);
						if(commonTime==""){
							commonTime = tsfrmt.format(runts);
						}
					}

					if(desc.equals("RDC")){
						EFC_Object.addProperty("DESCRIPTION", desc);

						if (map != null) {
							Iterator<String> itval = map.keySet().iterator();
							while (itval.hasNext()) {
								String col = itval.next();
								val = map.get(col).toString();
								RDC_Object.addProperty(col, val+"");
							}
						}
						else {
							RDC_Object.addProperty("Processing...", "Processing...");
						}

						rootObject.add("RDC_TOTALS", RDC_Object);
						//reset EFC object
						RDC_Object = new JsonObject();

						Iterator<String> itrdc = Constants.SQL_FRM_RDC_3.keySet().iterator(); 
						while (itrdc.hasNext()) {
							String sqlidrdc = itrdc.next();
							String valrdc = "";
							String descrdc = "";
							HashMap<String, Object> maprdc = null;
							try {
								maprdc = ((MultiColumnModal) SQLUtils.getModelObject(sqlidrdc)).getColmap();
								descrdc = Constants.SQL_DESC.get(sqlidrdc);
								String tsdescrdc = "Last Update: ";
								java.util.Date runtsrdc = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
								SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");
								if (runtsrdc != null) {
									tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
								}

								RDC_Object.addProperty("DESCRIPTION", descrdc+"");

								if (maprdc != null) {
									Iterator<String> itvalrdc = maprdc.keySet().iterator();
									while (itvalrdc.hasNext()) {
										String colrdc = itvalrdc.next();
										valrdc = maprdc.get(colrdc).toString();

										RDC_Object.addProperty(colrdc, valrdc+"");
									}
								}
								else {
									RDC_Object.addProperty("Processing...", "Processing...");
								}

								RDCrootArray.add(RDC_Object); 
								RDC_Object = new JsonObject();

							}
							catch (Exception e){e.printStackTrace(); logger.error("The following SQLID errored out:" + sqlidrdc, e);}
						}
					}else{
						EFC_Object.addProperty("DESCRIPTION", desc);

						if (map != null) {
							Iterator<String> itval = map.keySet().iterator();
							while (itval.hasNext()) {
								String col = itval.next();
								val = map.get(col).toString();
								EFC_Object.addProperty(col, val+"");
							}
						}
						else {
							EFC_Object.addProperty("Processing...", "Processing...");
						}

						EFCrootArray.add(EFC_Object);
						//reset EFC object
						EFC_Object = new JsonObject();
					}

					rootObject.addProperty("inventory_snapshot_last_run_timestamp", commonTime);
					rootObject.add("EFC", EFCrootArray);
					rootObject.add("RDC", RDCrootArray);
  
				} catch (Exception e) {
					logger.error("The following SQLID errored out:" + sqlid);
				}
			}
			return gson.toJson(rootObject);
	}

	public String generateAllNodeInventorySnapshotChart_REVAMP(){
		Iterator<String> iterator					= null;
		Iterator<String> iterator_rdc				= null;
		int icount 									= 0;
		int icount_rdc								= 0;
		String commonTime							= "";
		String sqlid								= null;
		String colrdc 								= "";
		Object value								= "";
		Object value_rdc							= "";
		String description							= null;
		String description_rdc						= null;
		String timestampDescriptionPrefix			= "Last Update: ";
		String timestampDescription					= null;

		java.util.Date lastRunTimeStamp				= null;
		SimpleDateFormat timestampFormat			= new SimpleDateFormat("MM/dd hh:mm a");
		Gson 		gson							= new GsonBuilder().create();

		HashMap<String, Object> columnMap_rdc		= null;
		HashMap<String, Object> columnMap			= null;

		/**
		 *  {0,
		 *  	{
		 *  		DESCRIPTION: "EFC 1-837",
		 *  		SKU_COUNT: "325,172"
		 *  		INVENTORY_COUNT: "6,852,978"
		 *  	}
		 *  }
		 *
		 **/
		HashMap<Integer, HashMap<String, HashMap<String, String>>> resultMap = new HashMap<Integer, HashMap<String, HashMap<String, String>>>();

		JsonObject rootObject = new JsonObject();
		JsonObject RDC_Object = new JsonObject();

		try{
			iterator = Constants.SQL_FRM_1.keySet().iterator();

			HashMap<String, String> row			= new HashMap<String, String>();

			columnMap = ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getColmap();
			description = Constants.SQL_DESC.get(sqlid);
			java.util.Date runts = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();

			if (runts != null) {
				timestampDescriptionPrefix = timestampDescriptionPrefix + timestampFormat.format(runts);
				if(commonTime==""){
					commonTime = timestampFormat.format(runts);
				}
			}

			//show/add description
			rootObject.addProperty("DESCRIPTION", description);

			if (columnMap != null) {
				Iterator<String> itval = columnMap.keySet().iterator();
				while (itval.hasNext()) {
					String col = itval.next();
					value = columnMap.get(col).toString();
					rootObject.addProperty(col, value+"");
				}
			}
			else {
				// description/value = -/-
				rootObject.addProperty("-", "-");
			}

			if(description.equals("RDC")){
				iterator_rdc		= Constants.SQL_FRM_RDC_3.keySet().iterator();
				String sqlidrdc		= iterator_rdc.next();

				while (iterator_rdc.hasNext()) {
					try {
						columnMap_rdc = ((MultiColumnModal) SQLUtils.getModelObject(sqlidrdc)).getColmap();

						lastRunTimeStamp = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();

						if (lastRunTimeStamp != null) {
							timestampDescriptionPrefix = timestampDescriptionPrefix + timestampFormat.format(lastRunTimeStamp);
						}
						if (columnMap_rdc != null) {
							Iterator<String> itvalrdc = columnMap_rdc.keySet().iterator();
							while (itvalrdc.hasNext()) {
								colrdc = itvalrdc.next();
								value_rdc = columnMap_rdc.get(colrdc).toString();

								RDC_Object.addProperty(colrdc, value_rdc+"");
							}
						}
						else {
							rootObject.addProperty("-", "-");
						}
					} catch (Exception e) {
						logger.error("The following SQLID errored out:" + sqlidrdc, e);
					}
				}
				rootObject.add("RDC", RDC_Object);
			}
			return rootObject.getAsString();
		//handle EVERY exception!
		}
		catch (Exception e)				{e.printStackTrace(); logger.error("error processing request : Exception", e);}
		finally{}
		return "";
	}

	public String redundancyStatistics(){

		Iterator<String> it = Constants.SQL_FRM_4.keySet().iterator();
		String commonTime = "";
		String sqlid = null;
		String val = "";
		String desc = "";

		JsonObject	rootObject		= new JsonObject();

		JsonArray	EFCrootArray	= new JsonArray();
		JsonArray	RDCrootArray	= new JsonArray();

		JsonObject	EFC_Object		= new JsonObject();
		JsonObject	RDC_Object		= new JsonObject();
		Gson 		gson			= new GsonBuilder().create();


		while (it.hasNext()) {
			sqlid = it.next();


			HashMap<String, Object> map = null;
			try {
				map							= ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getColmap();
				desc						= Constants.SQL_DESC.get(sqlid);
				String tsdesc				= "Last Update: ";
				java.util.Date runts		= ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
				SimpleDateFormat tsfrmt		= new SimpleDateFormat("MM/dd hh:mm a");

				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if(commonTime==""){
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid);
				e.printStackTrace();

			}

			if(desc.equals("RDC")){
				Iterator<String> itrdc = Constants.SQL_FRM_RDC_4.keySet().iterator();
				String commonTimerdc = "";

				if (map != null) {
					Iterator<String> itval = map.keySet().iterator();
					int count=0;
					while (itval.hasNext()) {
						count++;
						String col = itval.next();
						val = map.get(col).toString();
						if(count%2==0){
							RDC_Object.addProperty("UNIQUE_SKU_COUNT", val);
						} else {
							RDC_Object.addProperty("UNIQUE_SKU", val);
						}
					}
				}
				else {}

				rootObject.add("RDC_TOTALS", RDC_Object);
				RDC_Object	= new JsonObject();

				while (itrdc.hasNext()) {
					String sqlidrdc = itrdc.next();
					String valrdc = "";
					String descrdc = "";
					HashMap<String, Object> maprdc = null;
					try {

						maprdc = ((MultiColumnModal) SQLUtils.getModelObject(sqlidrdc)).getColmap();
						descrdc = Constants.SQL_DESC.get(sqlidrdc);
						String tsdescrdc = "Last Update: ";
						java.util.Date runtsrdc = ( SQLUtils.getModelObject(sqlidrdc)).getLastresulttimestamp();
						SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");

						if (runtsrdc != null) {
							tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
							if(commonTimerdc==""){
								commonTimerdc = tsfrmtrdc.format(runtsrdc);
							}
						}

						RDC_Object.addProperty("DESCRIPTION", descrdc);

						if (maprdc != null) {
							Iterator<String> itval = maprdc.keySet().iterator();
							int count=0;

							while (itval.hasNext()) {
								count++;
								String col = itval.next();
								valrdc = maprdc.get(col).toString();
								if(count%2==0){
									RDC_Object.addProperty("UNIQUE_SKU_COUNT", valrdc);
								} else {
									RDC_Object.addProperty("UNIQUE_SKU", valrdc);
								}

							}
						} else {
							RDC_Object.addProperty("UNIQUE_SKU_COUNT", "0");
							RDC_Object.addProperty("UNIQUE_SKU", "0");
						}

						RDCrootArray.add(RDC_Object);
						RDC_Object = new JsonObject();
					} catch (Exception e) {
						logger.error("The following SQLID errored out:"
								+ sqlidrdc,e);
						e.printStackTrace();

					}
				}
			}else{
				EFC_Object.addProperty("DESCRIPTION", desc);

				if (map != null) {
					Iterator<String> itval = map.keySet().iterator();
					int count=0;
					while (itval.hasNext()) {
						count++;
						String col = itval.next();
						val = map.get(col).toString();
						if(count%2==0){
							EFC_Object.addProperty("UNIQUE_SKU_COUNT", val);
						} else {
							EFC_Object.addProperty("UNIQUE_SKU", val);
						}
					}
				} else {
				}

				EFCrootArray.add(EFC_Object);
				EFC_Object	= new JsonObject();
			}


		}

		rootObject.addProperty("redundancy_last_run_timestamp", commonTime);
		rootObject.add("EFC", EFCrootArray);
		rootObject.add("RDC", RDCrootArray);

		return gson.toJson(rootObject);
	}

	public JsonObject StoreElf14DayFullfillmentPerformance(Iterator<String> it){

		//Iterator<String> it = Constants.SQL_FRM_5.keySet().iterator();

		String commonTime = "";
		String sqlid = null;
		String desc = "";

		JsonObject	root		= new JsonObject();
		JsonArray	rootArray	= new JsonArray();
		JsonObject	DAY_Object	= new JsonObject();

		while (it.hasNext()) {
			sqlid = it.next();

			HashMap<String, Object> map = null;
			try {
				map		= ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getColmap();
				desc	= Constants.SQL_DESC.get(sqlid);
				String				tsdesc	= "Last Update: ";
				java.util.Date		runts	= ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
				SimpleDateFormat	tsfrmt	= new SimpleDateFormat("MM/dd hh:mm a");

				if (runts != null) {
					tsdesc		= tsdesc + tsfrmt.format(runts);
					commonTime	= (commonTime=="") ? tsfrmt.format(runts) : commonTime;
				}

				DAY_Object.addProperty("DESCRIPTION", desc);

				// <tr class="<%=classid%>" title="<%=tsdesc%>">

				String order_date = "-";
				String order_count = "-";
				String ttl_units = "-";
				String shpd = "-";
				String pend = "-";
				String cncl = "-";

				if (map != null) {
					if(map.get("ORDER_DATE")	!= null && map.get("ORDER_DATE").toString()!="")	order_date	= map.get("ORDER_DATE").toString();
					if(map.get("ORDER_COUNT")	!= null && map.get("ORDER_COUNT").toString()!="")	order_count	= map.get("ORDER_COUNT").toString();
					if(map.get("TTL_UNITS") 	!= null && map.get("TTL_UNITS").toString()!="")		ttl_units	= map.get("TTL_UNITS").toString();
					if(map.get("SHPD")			!= null && map.get("SHPD").toString()!="")			shpd		= map.get("SHPD").toString();
					if(map.get("PEND")			!= null && map.get("PEND").toString()!="")			pend		= map.get("PEND").toString();
					if(map.get("CNCL")			!= null && map.get("CNCL").toString()!="")			cncl		= map.get("CNCL").toString();

					DAY_Object.addProperty("ORDER_DATE", order_date);
					DAY_Object.addProperty("ORDER_COUNT", order_count);
					DAY_Object.addProperty("TTL_UNITS", ttl_units);
					DAY_Object.addProperty("SHPD", shpd);
					DAY_Object.addProperty("PEND", pend);
					DAY_Object.addProperty("CNCL", cncl);
				}

				rootArray.add(DAY_Object);
				DAY_Object = new JsonObject();
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid,e);
				e.printStackTrace();
			}
		}

		root.add("fulfillment_data", rootArray );
		root.addProperty("last_run_timestamp", commonTime);

		return root;
	}

	public String fullfillmentPerformance(){

		JsonObject root				= new JsonObject();
		JsonObject EFCrootObject	= new JsonObject();
		JsonObject RDCrootObject	= new JsonObject();
		JsonObject EFC_Object		= new JsonObject();
		JsonObject RDC_Object		= new JsonObject();
		Gson 		gson			= new GsonBuilder().create();

		String sqlid = "";
		String desc = "";

		Iterator<String> it = Constants.SQL_FRM_6.keySet().iterator();
		int icount = 0;
		String commonTime = "";

		String average = "-";
		String backlog = "-";
		String pickunitcnt = "-";
		String ttlshpunitcnt = "-";
		String cnclunitcnt = "-";
		String expfulfill = "-";

		String sqlidrdc = "";
		String descrdc = "";
		HashMap<String, Object> maprdc = null;

		while (it.hasNext()) {
			sqlid = it.next();

			HashMap<String, Object> map = null;
			try {
				map = ((MultiColumnModal) SQLUtils.getModelObject(sqlid)).getColmap();
				desc = Constants.SQL_DESC.get(sqlid);
				String tsdesc = "Last Update: ";
				java.util.Date runts = ( SQLUtils.getModelObject(sqlid)).getLastresulttimestamp();
				SimpleDateFormat tsfrmt = new SimpleDateFormat("MM/dd hh:mm a");
				if (runts != null) {
					tsdesc = tsdesc + tsfrmt.format(runts);
					if(commonTime==""){
						commonTime = tsfrmt.format(runts);
					}
				}
			} catch (Exception e) {
				logger.error("The following SQLID errored out:" + sqlid, e);
			}


			if(desc.equals("RDC")){

				//add RDC totals first
				if (map != null) {

					if(map.get("AVERAGE")!= null && map.get("AVERAGE").toString()!="")				average = map.get("AVERAGE").toString();
					if(map.get("BACKLOG")!= null && map.get("BACKLOG").toString()!="")				backlog = map.get("BACKLOG").toString();
					if(map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString()!="")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if(map.get("TTLSHPUNITCNT")!=null && map.get("TTLSHPUNITCNT").toString()!="")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if(map.get("CNCLUNITCNT")!=null && map.get("CNCLUNITCNT").toString()!="")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if(map.get("EXPFULFILL")!=null && map.get("EXPFULFILL").toString()!="")			expfulfill = map.get("EXPFULFILL").toString();
				}else {
					average = "-";
					backlog = "-";
					pickunitcnt = "-";
					ttlshpunitcnt = "-";
					cnclunitcnt = "-";
					expfulfill = "-";
				}

				RDC_Object.addProperty("AVERAGE", average);
				RDC_Object.addProperty("BACKLOG", backlog);
				RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
				RDC_Object.addProperty("TTLSHPUNITCNT", ttlshpunitcnt);
				RDC_Object.addProperty("CNCLUNITCNT", cnclunitcnt);
				RDC_Object.addProperty("EXPFULFILL", expfulfill);

				root.add("RDC_TOTALS", RDC_Object);

				RDC_Object	= new JsonObject();
				//------------

				Iterator<String> itrdc = Constants.SQL_FRM_RDC_6.keySet().iterator();
				int icountrdc = 0;
				while (itrdc.hasNext()) {
					sqlidrdc = itrdc.next();
					try {
						maprdc = ((MultiColumnModal) SQLUtils.getModelObject(sqlidrdc)).getColmap();
						descrdc = Constants.SQL_DESC.get(sqlidrdc);
						String tsdescrdc = "Last Update: ";
						java.util.Date runtsrdc = ( SQLUtils.getModelObject(sqlidrdc)).getLastresulttimestamp();
						SimpleDateFormat tsfrmtrdc = new SimpleDateFormat("MM/dd hh:mm a");
						if (runtsrdc != null) {
							tsdescrdc = tsdescrdc + tsfrmtrdc.format(runtsrdc);
							if(commonTime==""){
								commonTime = tsfrmtrdc.format(runtsrdc);
							}
						}


						if (maprdc != null) {

							if(maprdc.get("AVERAGE")!= null && maprdc.get("AVERAGE").toString()!="")			average = maprdc.get("AVERAGE").toString();
							if(maprdc.get("BACKLOG")!= null && maprdc.get("BACKLOG").toString()!="")			backlog = maprdc.get("BACKLOG").toString();
							if(maprdc.get("PICKUNITCNT") != null && maprdc.get("PICKUNITCNT").toString()!="")	pickunitcnt = maprdc.get("PICKUNITCNT").toString();
							if(maprdc.get("TTLSHPUNITCNT")!=null && maprdc.get("TTLSHPUNITCNT").toString()!="")	ttlshpunitcnt = maprdc.get("TTLSHPUNITCNT").toString();
							if(maprdc.get("CNCLUNITCNT")!=null && maprdc.get("CNCLUNITCNT").toString()!="")		cnclunitcnt = maprdc.get("CNCLUNITCNT").toString();
							if(maprdc.get("EXPFULFILL")!=null && maprdc.get("EXPFULFILL").toString()!="")		expfulfill = maprdc.get("EXPFULFILL").toString();
						}else {
							average = "-";
							backlog = "-";
							pickunitcnt = "-";
							ttlshpunitcnt = "-";
							cnclunitcnt = "-";
							expfulfill = "-";
						}

						RDC_Object.addProperty("DESCRIPTION", descrdc);
						RDC_Object.addProperty("AVERAGE", average);
						RDC_Object.addProperty("BACKLOG", backlog);
						RDC_Object.addProperty("PICKUNITCNT", pickunitcnt);
						RDC_Object.addProperty("TTLSHPUNITCNT", ttlshpunitcnt);
						RDC_Object.addProperty("CNCLUNITCNT", cnclunitcnt);
						RDC_Object.addProperty("EXPFULFILL", expfulfill);

						RDCrootObject.add(icountrdc+"", RDC_Object);
						RDC_Object	= new JsonObject();
					icountrdc++;
					} catch (Exception e) {
						logger.error("The following SQLID errored out:" + sqlidrdc,e);
						e.printStackTrace();

					}
				}
			}else{

				EFC_Object.addProperty("DESCRIPTION", desc);


				if (map != null) {

					if(map.get("AVERAGE")!= null && map.get("AVERAGE").toString()!="")				average = map.get("AVERAGE").toString();
					if(map.get("BACKLOG")!= null && map.get("BACKLOG").toString()!="")				backlog = map.get("BACKLOG").toString();
					if(map.get("PICKUNITCNT") != null && map.get("PICKUNITCNT").toString()!="")		pickunitcnt = map.get("PICKUNITCNT").toString();
					if(map.get("TTLSHPUNITCNT")!=null && map.get("TTLSHPUNITCNT").toString()!="")	ttlshpunitcnt = map.get("TTLSHPUNITCNT").toString();
					if(map.get("CNCLUNITCNT")!=null && map.get("CNCLUNITCNT").toString()!="")		cnclunitcnt = map.get("CNCLUNITCNT").toString();
					if(map.get("EXPFULFILL")!=null && map.get("EXPFULFILL").toString()!="")			expfulfill = map.get("EXPFULFILL").toString();
				}else {
					average = "-";
					backlog = "-";
					pickunitcnt = "-";
					ttlshpunitcnt = "-";
					cnclunitcnt = "-";
					expfulfill = "-";
				}

				EFC_Object.addProperty("AVERAGE", average);
				EFC_Object.addProperty("BACKLOG", backlog);
				EFC_Object.addProperty("PICKUNITCNT", pickunitcnt);
				EFC_Object.addProperty("TTLSHPUNITCNT", ttlshpunitcnt);
				EFC_Object.addProperty("CNCLUNITCNT", cnclunitcnt);
				EFC_Object.addProperty("EXPFULFILL", expfulfill);

				EFCrootObject.add(icount+"", EFC_Object);

				EFC_Object	= new JsonObject();
				icount++;
			}
		}

		root.add("RDC", RDCrootObject);
		root.add("EFC", EFCrootObject);

		root.addProperty("last_run_timestamp", commonTime);

		return gson.toJson(root);
	}

}