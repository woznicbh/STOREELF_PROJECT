/**
\n *
 */

package com.storeelf.report.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;

import org.apache.mina.util.ConcurrentHashSet;
import org.apache.shiro.session.mgt.SimpleSession;

import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.listener.StoreElfModelMapListener;
import com.storeelf.report.web.listener.StoreElfSessionListener;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.report.web.model.SearchFieldModal;
import com.storeelf.report.web.model.impl.SearchFieldModalImpl;
import com.storeelf.util.SQLUtils;

/**
 * <B>Class Name:</B><BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Nov 1, 2011 3:42:58 PM<BR/>
 *
 * ----------------------------------------------------------------------
 *
 * TODO: Export SQL queries to flatfile in sql_includes folder
 * 	file name pattern: {SQLID}_{DB-TYPE}_{REFRESH-TIME}_{REFRESH_TIME_TYPE}.SQL
 * 	Example:
 * 		efc1vqfullfrm_EFC_3600000_MILLISECOND.SQL
 * 		efc2vqfullfrm_EFC_3600000_MILLISECOND.SQL
 * 		efc3vqfullfrm_EFC_3600000_MILLISECOND.SQL
 *
 * Upon startup a Hashmap of entries should be filled with SQL Ids and metadata
 *
 * SQL_METADATA_MAP.put("SQLID","{LAST-MODIFIED-TIMESTAMP}")
 * SQL_CACHED_DATA_MAP.put("SQLID","{SQL-QUERY}")
 * SQL_TIME_MAP.put("SQLID", "{REFRESH-TIME}")
 * ----------------------------------------------------------------------
 *
 */
public class Constants{
	//public static HazelcastInstance							hazelcastInstance						= null;
	public static boolean										STOREELF_SECURITY_ENABLED					= false;
	public static ServletContext								STOREELF_SERVLET_CONTEXT					= null;								
	public static String										STOREELF_HOST								= "";
	public static String										STOREELF_CERT_KEY							= null;							//Integer.parseInt(ReportActivator.getInstance().getSystemProperty(""));
	public static final int										STOREELF_DASHBOARD_SQL_THREAD_MAX			= 64; //Integer.parseInt(ReportActivator.getInstance().getSystemProperty(""));
	public static final int										STOREELF_MYSQL_THREAD_MAX					= 1; //Integer.parseInt(ReportActivator.getInstance().getSystemProperty(""));
	public static final int										STOREELF_SESSION_THREAD_MAX				= 2; //Integer.parseInt(ReportActivator.getInstance().getSystemProperty(""));
	public static int											STOREELF_MAX_SLEEP_MILLISECONDS			= 5000;
	public static String										STOREELF_HAZELCAST_INSTANCE_NAME			= "";
	
	//TODO: change the map value from String based "SCHEDULE", "RUNNING", ...etc to hold the modal object itself
	public static ConcurrentHashMap<String, String>				STOREELF_SQL_REFRESH_JOBS					= new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, PreparedStatement>	STOREELF_SQL_STMT_MAP						= new ConcurrentHashMap<String, PreparedStatement>();
	public static ConcurrentHashMap<String, String>				STOREELF_SQL_JOBS_ERRORLOG				= new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String>				STOREELF_SQL_JOBS_SUCCESSLOG				= new ConcurrentHashMap<String, String>();	
	public static ConcurrentHashMap<String, SQLModel>			STOREELF_SQLMODEL_MAP						= new ConcurrentHashMap<String, SQLModel>();
	public static ConcurrentHashMap<String, SimpleSession>		STOREELF_SESSION_REFRESH_JOBS				= new ConcurrentHashMap<String, SimpleSession>();
	public static ConcurrentSkipListMap<String, Connection>		STOREELF_SQL_CONNECTIONS					= new ConcurrentSkipListMap<String, Connection>();
	
	public static ConcurrentSkipListMap<String, String>			STOREELF_SQL_FILE_MAP						= new ConcurrentSkipListMap<String, String>();
	
	public static ConcurrentHashSet<String> 					STOREELF_SQL_AGENT_WHITELIST				= new ConcurrentHashSet<String>();
	
	//public static ExecutorService								STOREELF_SQL_FILE_MONITOR_EXECUTOR_SERVICE= Executors.newSingleThreadExecutor();
	public static boolean										STOREELF_SQL_FILE_EXPORT_RUNNING			= false;
	public static boolean										STOREELF_HZ_WAN_REPLICATION				= true;
	
	public static ScheduledExecutorService						STOREELF_HZ_KEEP_ALIVE_EXECUTOR_SERVICE	= null;//Executors.newSingleThreadScheduledExecutor();
	public static ExecutorService								STOREELF_SESSION_EXECUTOR_SERVICE			= null;//Executors.newSingleThreadExecutor();
	public static ExecutorService								STOREELF_DASHBOARD_EXECUTOR_SERVICE		= null;//Executors.newFixedThreadPool(STOREELF_DASHBOARD_SQL_THREAD_MAX);
	public static ExecutorService								STOREELF_ENVIRONMENTS_EXECUTOR_SERVICE	= null;//Executors.newFixedThreadPool(STOREELF_ENVIRONMENTS_SQL_THREAD_MAX);
	public static ExecutorService								STOREELF_UTILITY_EXECUTOR_SERVICE			= null;//Executors.newFixedThreadPool(1);
	public static ExecutorService								STOREELF_LOGGING_EXECUTOR_SERVICE			= null;
	
	public static StoreElfSessionListener							STOREELF_SESSION_LISTENER					= new StoreElfSessionListener();
	public static StoreElfModelMapListener						STOREELF_MODELMAP_LISTENER				= new StoreElfModelMapListener();

	public static int									STOREELF_MAX_SEARCH_HISTORY				= 10;
	public static int									STOREELF_MAX_CONCURRENT_SESSIONS_CACHED	= 10000;
	public static int									STOREELF_SQL_TIMEOUT_MINUTES				= 60;
	public static int									STOREELF_SQL_CONNECTION_MAX				= 64;

	public static HashMap<String, HashMap<String, String>> GLOBAL_SESSION_ATTRIBUTE_MAP	= new HashMap<String, HashMap<String, String>>();

	public static final String STOREELF_RO = "STOREELF_RO";
	public static final String STOREELF_WR = "STOREELF_WR";
	public static final String OMS = "OMS";
	public static final String OMSPII = "OMS_PII";
	public static final String EFC = "EFC";
	public static final String EFC1 = "EFC1";
	public static final String EFC2 = "EFC2";
	public static final String EFC3 = "EFC3";
	public static final String EFC4 = "EFC4";
	public static final String EFC1_AUDIT = "EFC1_AUDIT";
	public static final String EFC2_AUDIT = "EFC2_AUDIT";
	public static final String EFC3_AUDIT = "EFC3_AUDIT";
	public static final String EFC4_AUDIT = "EFC4_AUDIT";
    public static final String PROSHIP = "PROSHIP";
	public static final String EFC1_PRIM = "EFC1_PRIM";
	public static final String EFC2_PRIM = "EFC2_PRIM";
	public static final String EFC3_PRIM = "EFC3_PRIM";
	public static final String EFC4_PRIM = "EFC4_PRIM";
	public static final String RDC865 = "RDC865";
	public static final String RDC810 = "RDC810";
	public static final String RDC840 = "RDC840";
	public static final String RDC855 = "RDC855";
	public static final String RDC830 = "RDC830";
	public static final String RDC890 = "RDC890";
	public static final String RDC885 = "RDC885";
	public static final String RDC875 = "RDC875";
	public static final String RDC860 = "RDC860";
	public static final String USE_COGNOS = "USE_COGNOS";
	public static final String COGNOS_LINK = "COGNOS_LINK";
	public static final String COGNOS_HOURLY = "COGNOS_HOURLY";
	public static final String COGNOS_DAILY = "COGNOS_DAILY";
	public static final String COGNOS_WEEKLY = "COGNOS_WEEKLY";
	public static final String COGNOS_ON_DEMAND = "COGNOS_ON_DEMAND";
	public static final String GIV = "GIV";
	public static final HashMap<String,String> STOREELF_MYSQL_DASHBOARD_PILOT = new HashMap<String,String>();

	public static final ArrayList<String> EFC_LIST = new ArrayList<String>();
	static {
		EFC_LIST.add(EFC1);
		EFC_LIST.add(EFC2);
		EFC_LIST.add(EFC3);
		EFC_LIST.add(EFC4);

	}
	
	//Deep declared
	
		public static final ArrayList<String> CANCEL_DASHBOARD_TABLES = new ArrayList<String>();
		static
		{
			CANCEL_DASHBOARD_TABLES.add("cancelglance");
			CANCEL_DASHBOARD_TABLES.add("top_10_cancelled_SKU");
			CANCEL_DASHBOARD_TABLES.add("14_day_cancel_stats");
			CANCEL_DASHBOARD_TABLES.add("5_day_auto_cancel_stats");
			CANCEL_DASHBOARD_TABLES.add("14_day_cust_cancels");
			
		}
		
		public static final String INVALID_CANCELGLANCE =	"{\"Today\":{\"fulfillment_data\":{\"0\":{\"DESCRIPTION\":\"BOPUS expired pick-up dollars\",\"VALUE\":\"-\"},\"1\":{\"DESCRIPTION\":\"BOPUS expired pick-up units\",\"VALUE\":\"-\"}},\"cncl_qty_data\":{\"0\":{\"DESCRIPTION\":\"Units cancelled today\",\"VALUE\":\"-\"}},\"cncl_amt_data\":{\"0\":{\"DESCRIPTION\":\"Dollars cancelled today\",\"VALUE\":\"-\"}},\"quantity_cancelled\":{},\"dollar_amount_cancelled\":{},\"quantity_cancelled_breakdown\":[],\"dollar_amount_cancelled_breakdown\":[],\"last_run_timestamp\":\"\"},\"PreviousDay\":{\"fulfillment_data\":{\"0\":{\"DESCRIPTION\":\"BOPUS expired pick-up dollars\",\"VALUE\":\"-\"},\"1\":{\"DESCRIPTION\":\"BOPUS expired pick-up units\",\"VALUE\":\"-\"}},\"cncl_qty_data\":{\"0\":{\"DESCRIPTION\":\"Units cancelled yesterday\",\"VALUE\":\"-\"}},\"cncl_amt_data\":{\"0\":{\"DESCRIPTION\":\"Dollars cancelled yesterday\",\"VALUE\":\"-\"}},\"quantity_cancelled\":{},\"dollar_amount_cancelled\":{},\"quantity_cancelled_breakdown\":[],\"dollar_amount_cancelled_breakdown\":[],\"last_run_timestamp\":\"\"}}";
		public static final String INVALID_TOP_10_CANCELLED_SKU =	"{\"Today\":{\"fulfillment_data\":[],\"last_run_timestamp\":\"\"},\"PreviousDay\":{\"fulfillment_data\":[],\"last_run_timestamp\":\"\"}}";
		public static final String INVALID_14_DAY_CANCEL_STATS	=	"{\"cancel_data\":[{},{},{},{},{},{},{},{},{},{},{},{},{},{}],\"last_run_ts\":\"\"}";
		public static final String INVALID_5_DAY_AUTO_CANCEL_STATS	=	"{\"auto_cancel_data\":[{},{},{},{},{}],\"last_run_ts\":\"\"}";
		public static final String INVALID_14_DAY_CUST_CANCELS	= "{\"cust_cancel_data\":[],\"last_run_timestamp\":\"\"}";

	public static final String PROP_SEPERATOR = ".";

	public static final String PROP_THREAD_COUNT = "THREAD_COUNT";
	public static final String PROP_ENTRY_DATE_FORMAT = "ENTRY_DATE_FORMAT";
	public static final String PROP_DB_INSTANCE = "DB_INS";
	public static final String PROP_DB_USR = "DB_USER";
	public static final String PROP_DB_PWD = "DB_PWD";
	public static final String PROP_DB_HOST = "DB_HOST";
	public static final String PROP_URL = "URL";
	public static final String PROP_ENV_TYPE = "ENV_TYPE";
	public static final String PROP_USER = "USER";
	public static final String PROP_PROG = "PROG";
	public static final String PROP_DB_PORT = "DB_PORT";
	public static final String PROP_DB_SID = "DB_SID";
	public static final String PROP_DB_SERVICE_NAME = "DB_SERVICE_NAME";
	public static final String PROP_DB_TYPE = "DB_TYPE";
	public static final String PROP_DB_URL = "DB_URL";
	public static final String PROP_DB_SCHEMA = "DB_SCHEMA";
	public static final String ENV_TYPE = "ENV_TYPE";

	public static final String PROCESSING = "Processing...";

	public static final String DBTYPE_ORACLE = "ORACLE";
	public static final String DBTYPE_MYSQL = "MYSQL";
	public static final String DBTYPE_DB2 = "DB2";

	public static final String ENVTYPE_WS = "WS";
	public static final String ENVTYPE_HTTP = "HTTP";

	public static final String REPORT_CARTON_PROCESSED = "CartonProcessedReport";
	public static final String REPORT_DSV_ORDER_STAT = "DSVOrderStatisticsReport";
	public static final String REPORT_FULFILLMENT_STAT = "SourcingDistributionReport";
	public static final String REPORT_HOURLY_STAT = "HourlyStatisticsReport";
	public static final String REPORT_ORDER_DIST = "OrderDistributionReport";
	public static final String REPORT_ORDER_STAT_PER_NODE = "OrderStatisticsPerFulfillmentNodeReport";
	public static final String REPORT_PEND_ORDER = "PendingOrderStatisticsReport";
	public static final String REPORT_PEND_ORDER_PER_HR = "PendingOrderStatisticsReportPerHour";
	public static final String REPORT_ORDER_FUL_PERF = "OrderFulfillmentPerformanceReport";
	public static final String REPORT_ORDER_DIST_HR = "HourlyOrderDistributionReport";
	public static final String REPORT_EFC_DAILY_SHIPPED_UNITS = "DailyShippedUnitsReport";
	public static final String REPORT_HOURLY_STAT_BYDATE = "HourlyStatisticsReportByDate";
	public static final String REPORT_ORDER_DIST_NODSV = "OrderDistributionReportNoDSV";
	public static final String REPORT_FULFILLMENT_STAT_NODSV = "SourcingDistributionReportNoDSV";
	public static final String REPORT_FULFILLMENT_STAT_NODSV_7DAYS = "SourcingDistributionReportNoDSV7Days";
	public static final String REPORT_FULFILLMENT_STAT_NODSV_30DAYS = "SourcingDistributionReportNoDSV30Days";
	public static final String REPORT_FULFILLMENT_STAT_HOURLY = "SourcingDistributionReportHourly";
	public static final String REPORT_FULFILLMENT_STAT_NODSV_HOURLY = "SourcingDistributionReportNoDSVHourly";
	public static final String REPORT_ORDER_DIST_HOURLY = "OrderDistributionReportHourly";
	public static final String REPORT_ORDER_DIST_EFC_HOURLY = "OrderDistributionReportPerEFCHourly";
	public static final String REPORT_ORDER_DIST_NODSV_HOURLY = "OrderDistributionReportNoDSVHourly";
	public static final String REPORT_DEPT_INV = "EFCDeptInvReport";
	public static final String REPORT_SALES_BY_STATE = "SalesByState";
	public static final String REPORT_REGIONAL_SOURCING_CLASSIFICATION = "RegionalSourcingClassifcation";
	public static final String REPORT_GROUPED_FULFILLMENT_STAT = "SourcingDistributionReportByZone";
	public static final String REPORT_GROUPED_FULFILLMENT_STAT_NODSV = "SourcingDistributionReportByZoneNoDSV";
	public static final String REPORT_GROUPED_FULFILLMENT_STAT_HOURLY = "SourcingDistributionReportByZoneHourly";
	public static final String REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY = "SourcingDistributionReportByZoneNoDSVHourly";

	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865 = "RDCHourlyInProcessShipmentReport-865";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810 = "RDCHourlyInProcessShipmentReport-810";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840 = "RDCHourlyInProcessShipmentReport-840";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855 = "RDCHourlyInProcessShipmentReport-855";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830 = "RDCHourlyInProcessShipmentReport-830";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890 = "RDCHourlyInProcessShipmentReport-890";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885 = "RDCHourlyInProcessShipmentReport-885";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875 = "RDCHourlyInProcessShipmentReport-875";
	public static final String REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860 = "RDCHourlyInProcessShipmentReport-860";

	public static final String REPORT_ADHOC_SIMRED = "SIMInventoryReport";

	public static final String REPORT_ORDER_EXECPTION_REPORT = "RDCOrderExecptionReport";

	public static final String REPORT_RDC_ORDER_TURNTIME_865 = "RDCOrderTurntimeReport-865";
	public static final String REPORT_RDC_ORDER_TURNTIME_810 = "RDCOrderTurntimeReport-810";
	public static final String REPORT_RDC_ORDER_TURNTIME_840 = "RDCOrderTurntimeReport-840";
	public static final String REPORT_RDC_ORDER_TURNTIME_855 = "RDCOrderTurntimeReport-855";
	public static final String REPORT_RDC_ORDER_TURNTIME_830 = "RDCOrderTurntimeReport-830";
	public static final String REPORT_RDC_ORDER_TURNTIME_890 = "RDCOrderTurntimeReport-890";
	public static final String REPORT_RDC_ORDER_TURNTIME_885 = "RDCOrderTurntimeReport-885";
	public static final String REPORT_RDC_ORDER_TURNTIME_875 = "RDCOrderTurntimeReport-875";
	public static final String REPORT_RDC_ORDER_TURNTIME_860 = "RDCOrderTurntimeReport-860";

	public static final String REPORT_RDC_DAILY_SCORECARD_865 = "RDCDailyScorecardReport-865";
	public static final String REPORT_RDC_DAILY_SCORECARD_810 = "RDCDailyScorecardReport-810";
	public static final String REPORT_RDC_DAILY_SCORECARD_840 = "RDCDailyScorecardReport-840";
	public static final String REPORT_RDC_DAILY_SCORECARD_855 = "RDCDailyScorecardReport-855";
	public static final String REPORT_RDC_DAILY_SCORECARD_830 = "RDCDailyScorecardReport-830";
	public static final String REPORT_RDC_DAILY_SCORECARD_890 = "RDCDailyScorecardReport-890";
	public static final String REPORT_RDC_DAILY_SCORECARD_885 = "RDCDailyScorecardReport-885";
	public static final String REPORT_RDC_DAILY_SCORECARD_875 = "RDCDailyScorecardReport-875";
	public static final String REPORT_RDC_DAILY_SCORECARD_860 = "RDCDailyScorecardReport-860";

	public static final String REPORT_RDC_DAILY_TURNTIME_865 = "RDCDailyTurntimeReport-865";
	public static final String REPORT_RDC_DAILY_TURNTIME_810 = "RDCDailyTurntimeReport-810";
	public static final String REPORT_RDC_DAILY_TURNTIME_840 = "RDCDailyTurntimeReport-840";
	public static final String REPORT_RDC_DAILY_TURNTIME_855 = "RDCDailyTurntimeReport-855";
	public static final String REPORT_RDC_DAILY_TURNTIME_830 = "RDCDailyTurntimeReport-830";
	public static final String REPORT_RDC_DAILY_TURNTIME_890 = "RDCDailyTurntimeReport-890";
	public static final String REPORT_RDC_DAILY_TURNTIME_885 = "RDCDailyTurntimeReport-885";
	public static final String REPORT_RDC_DAILY_TURNTIME_875 = "RDCDailyTurntimeReport-875";
	public static final String REPORT_RDC_DAILY_TURNTIME_860 = "RDCDailyTurntimeReport-860";

	public static final String REPORT_WMOS_RED_SKU = "REDSKUReport";
	public static final String REPORT_SKU_CANCEL = "SKUCancelReport";
	public static final String REPORT_ORDER_RESOURCE = "OrderResourceReport";
	public static final String REPORT_INVENTORY_TRANSFER = "InventoryTransferDailyReport";
	public static final String REPORT_PREV_DAY_STORE_SHIPMENTS = "PrevDayStoreShipmentsReport";
	public static final String REPORT_RDC_SCORE_CARD = "RDCScorecardReport";
	public static final String REPORT_RDC_DAILY_METRICS = "RDCDailyMetricsReport";
	
	public static final HashMap<String,String> REPOSITORIES_MAP=new HashMap<String,String>(); 
	static
	{
		/*REPOSITORIES_MAP.put("OMS","F:\\Git_New\\OMSe_Capacity\\Unix");
		REPOSITORIES_MAP.put("GIV","F:\\Git_New\\GIV_Capacity\\UNIX\\scripts");
		REPOSITORIES_MAP.put("OMSr", "F:\\Git_New\\IM_OMS-POC-POS-master\\Unix");*/
		REPOSITORIES_MAP.put("OMS","D:\\prod\\apps\\of\\StoreElf\\OMSe_Capacity\\Unix");
		REPOSITORIES_MAP.put("GIV","D:\\prod\\apps\\of\\StoreElf\\GIV_Capacity\\UNIX\\scripts");
		REPOSITORIES_MAP.put("OMSr", "D:\\prod\\apps\\of\\StoreElf\\IM_OMS-POC-POS\\Unix");
	}

	public static TreeMap<String, String> REPORT_MAP = new TreeMap<String, String>();
	static {
		REPORT_MAP.put(REPORT_ORDER_EXECPTION_REPORT,				"RDC Order Execption Report");
		REPORT_MAP.put(REPORT_ADHOC_SIMRED,							"SIM/RED Inventory Report");
		REPORT_MAP.put(REPORT_CARTON_PROCESSED, 					"Cartons Shipped Report");
		REPORT_MAP.put(REPORT_DSV_ORDER_STAT, 						"DSV Order Statistics");
		REPORT_MAP.put(REPORT_FULFILLMENT_STAT,						"Daily Sourcing Distribution by EFC - With DSV");
		REPORT_MAP.put(REPORT_HOURLY_STAT,							"Hourly Order Statistics");
		REPORT_MAP.put(REPORT_ORDER_DIST,							"Sales by State");
		REPORT_MAP.put(REPORT_ORDER_STAT_PER_NODE,					"Units Per Order");
		REPORT_MAP.put(REPORT_PEND_ORDER, 							"Order Statistics Report");
		REPORT_MAP.put(REPORT_PEND_ORDER_PER_HR,					"Order Statistics Per Hour Report");
		REPORT_MAP.put(REPORT_ORDER_FUL_PERF, 						"Order Aging");
		REPORT_MAP.put(REPORT_ORDER_DIST_HOURLY, 					"Hourly Sales Units by EFC");
		REPORT_MAP.put(REPORT_EFC_DAILY_SHIPPED_UNITS,				"Daily Shipped Units");
		REPORT_MAP.put(REPORT_ORDER_DIST_NODSV, 					"Sales by State (No DSV)");
		REPORT_MAP.put(REPORT_FULFILLMENT_STAT_NODSV,				"Daily Sourcing Distribution by EFC - No DSV");
		REPORT_MAP.put(REPORT_FULFILLMENT_STAT_NODSV_HOURLY,		"Hourly Sales by Region by EFC");
		REPORT_MAP.put(REPORT_ORDER_DIST_NODSV_HOURLY,				"Hourly Orders by Nodes");
		REPORT_MAP.put(REPORT_DEPT_INV, 							"EFC Department Unique SKU Report");
		REPORT_MAP.put(REPORT_SALES_BY_STATE, 						"Sales by State (Last 4 Weeks)");
		REPORT_MAP.put(REPORT_REGIONAL_SOURCING_CLASSIFICATION,		"Region Listing Report");
		REPORT_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT,				"Daily Sourcing Distribution by EFC by Zone - With DSV");
		REPORT_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV,		"Daily Sourcing Distribution by EFC by Zone - No DSV");
		REPORT_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_HOURLY,		"Hourly Sourcing Distribution - With DSV (By Zone and EFC)");
		REPORT_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY,"Hourly Sourcing Distribution - No DSV (By Zone and EFC)");
		// report name changed as part of Hourly sourcing distribution changes
		REPORT_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY,	"Hourly Sourcing Distribution - (By Zone and Nodes)");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810,		"RDC Hourly In Process Shipment for 810");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840,		"RDC Hourly In Process Shipment for 840");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855,		"RDC Hourly In Process Shipment for 855");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830,		"RDC Hourly In Process Shipment for 830");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890,		"RDC Hourly In Process Shipment for 890");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885,		"RDC Hourly In Process Shipment for 885");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875,		"RDC Hourly In Process Shipment for 875");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865,		"RDC Hourly In Process Shipment for 865");
		REPORT_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860,		"RDC Hourly In Process Shipment for 860");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_810,				"RDC Order Turn Time Report for 810");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_840,				"RDC Order Turn Time Report for 840");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_855,				"RDC Order Turn Time Report for 855");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_830,				"RDC Order Turn Time Report for 830");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_890,				"RDC Order Turn Time Report for 890");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_885,				"RDC Order Turn Time Report for 885");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_875,				"RDC Order Turn Time Report for 875");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_865,				"RDC Order Turn Time Report for 865");
		REPORT_MAP.put(REPORT_RDC_ORDER_TURNTIME_860,				"RDC Order Turn Time Report for 860");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_810,				"RDC Daily Scorecard Report for 810");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_840,				"RDC Daily Scorecard Report for 840");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_855,				"RDC Daily Scorecard Report for 855");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_830,				"RDC Daily Scorecard Report for 830");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_890,				"RDC Daily Scorecard Report for 890");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_885,				"RDC Daily Scorecard Report for 885");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_875,				"RDC Daily Scorecard Report for 875");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_865,				"RDC Daily Scorecard Report for 865");
		REPORT_MAP.put(REPORT_RDC_DAILY_SCORECARD_860,				"RDC Daily Scorecard Report for 860");

		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_810,				"RDC Daily Turntime Report for 810");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_840,				"RDC Daily Turntime Report for 840");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_855,				"RDC Daily Turntime Report for 855");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_830,				"RDC Daily Turntime Report for 830");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_890,				"RDC Daily Turntime Report for 890");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_885,				"RDC Daily Turntime Report for 885");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_875,				"RDC Daily Turntime Report for 875");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_865,				"RDC Daily Turntime Report for 865");
		REPORT_MAP.put(REPORT_RDC_DAILY_TURNTIME_860,				"RDC Daily Turntime Report for 860");




		REPORT_MAP.put(REPORT_WMOS_RED_SKU,				" WMOS RED SKU Report");
		REPORT_MAP.put(REPORT_SKU_CANCEL, 				"SKU Cancel Report");
		REPORT_MAP.put(REPORT_ORDER_RESOURCE, 			"Order Resource Report");
		REPORT_MAP.put(REPORT_INVENTORY_TRANSFER,		"Inventory Transfer Report");
		REPORT_MAP.put(REPORT_PREV_DAY_STORE_SHIPMENTS, "Previous Day Store Shipments");
		REPORT_MAP.put(REPORT_RDC_DAILY_METRICS, 		"RDC Daily Metrics");
		REPORT_MAP.put(REPORT_RDC_SCORE_CARD, 			"RDC Scorecard Report");
	}



	// OCF changes Start

	/* New code 05/09/2014 starts for the tracking link change */

	public static HashMap<String, String> REPORT_MAP_Tracking = new HashMap<String, String>();
	static {
		REPORT_MAP_Tracking.put("UPS",					"http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("FXSP", "https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		/*Links for some more carriers*/
		REPORT_MAP_Tracking.put("BWTI_FXRS.CAFE.HDL","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FXSP_3D","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FXSP_HD","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FXSP_ND","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FXSP_SC","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FXSP_SP","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		
		/*Rest Tracking links*/
		REPORT_MAP_Tracking.put("BWTI_FXRS.SP.SM","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		
		//REPORT_MAP_Tracking.put("ABF_C","https://www.abfs.com/tools/trace/default.asp");
		//REPORT_MAP_Tracking.put("ABF_W","https://www.abfs.com/tools/trace/default.asp");
		
		REPORT_MAP_Tracking.put("BWTI_FXRS.FXRS.2DA","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("BWTI_FXRS.FXRS.EXP","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");	
		REPORT_MAP_Tracking.put("BWTI_FXRS.FXRS.PRI","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("BWTI_FXRS.FXRS.STD","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");	
		REPORT_MAP_Tracking.put("BWTI_USPS.USPS.PM","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("EST_09","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("EST_C","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("EST_R","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FedEx Ground","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("FedEx Next Day Delivery","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("HDU_W","https://www.homedirectusa.com/cms/");
		REPORT_MAP_Tracking.put("LASERSHIP","http://www.lasership.com/track/");
		REPORT_MAP_Tracking.put("MXD_R","http://www.mxdgroup.com/track-delivery/");
		REPORT_MAP_Tracking.put("MXD_T","http://www.mxdgroup.com/track-delivery/");
		REPORT_MAP_Tracking.put("MXD_W","http://www.mxdgroup.com/track-delivery/");
		REPORT_MAP_Tracking.put("NSD_09","http://nstracking.nonstopdelivery.com/selfserve/display_courier_external.do?rhop_jobno=");
		REPORT_MAP_Tracking.put("NSD_C","http://nstracking.nonstopdelivery.com/selfserve/display_courier_external.do?rhop_jobno=");
		REPORT_MAP_Tracking.put("NSD_R","http://nstracking.nonstopdelivery.com/selfserve/display_courier_external.do?rhop_jobno=");
		REPORT_MAP_Tracking.put("NSD_T","http://nstracking.nonstopdelivery.com/selfserve/display_courier_external.do?rhop_jobno=");
		REPORT_MAP_Tracking.put("Priority Air","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.2DA","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.EXP","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.GND","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.HDL","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.PRI","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.SM","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.FWS.STD","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("PS_ALF.ONTRAC.CT","http://www.ontrac.com/trackingdetail.asp?tracking=");
		REPORT_MAP_Tracking.put("Standard Ground","https://www.fedex.com/apps/fedextrack/?action=track&tracknumbers=");
		REPORT_MAP_Tracking.put("UPS Ground","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_C","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		
		//REPORT_MAP_Tracking.put("UPS_R","http://www.nvclogistics.com/track.aspx");
		
		REPORT_MAP_Tracking.put("UPS_T","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_W","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("U10","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("U30","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("USPS","https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&text28777=&tLabels=");
		REPORT_MAP_Tracking.put("USPS Priority Mail","https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&text28777=&tLabels=");
		REPORT_MAP_Tracking.put("USPS_CG","https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&text28777=&tLabels=");
		REPORT_MAP_Tracking.put("USPS_FC","https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&text28777=&tLabels=");
		REPORT_MAP_Tracking.put("USPS_SC","https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&text28777=&tLabels=");
		
		REPORT_MAP_Tracking.put("UPS_3D","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_CG","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_ND","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_SC","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("UPS_SP","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.2DA","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.3DS","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.GND","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.NDA","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.NDS","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.SPP","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("BWTI_UPS.UPS.SPS","http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=");
		REPORT_MAP_Tracking.put("ONTRAC","http://www.ontrac.com/tracking.asp?trackingres=submit&tracking_number=");
		REPORT_MAP_Tracking.put("ONTC","http://www.ontrac.com/tracking.asp?trackingres=submit&tracking_number=");
		REPORT_MAP_Tracking.put("LAZR","http://www.lasership.com/track/");
		REPORT_MAP_Tracking.put("PS_ALF.SCRIPTED_LASERSHIP.GND","http://www.lasership.com/track/");
	}
	
	public static String ABFS_CARRIER =  "https://www.abfs.com/tools/trace/default.asp";
	public static String UPS_R =  "https://www.abfs.com/tools/trace/default.asp";
	public static String TRACKING_ERROR_PAGE = "/Utility/tracking_error.jsp";

	public static HashMap<String, String> STATE_LIST = new HashMap<String, String>();
	static {
		STATE_LIST.put("AL", "Alabama");
		STATE_LIST.put("AK", "Alaska");
		STATE_LIST.put("AZ", "Arizona");
		STATE_LIST.put("AR", "Arkansas");
		STATE_LIST.put("CA", "California");
		STATE_LIST.put("CO", "Colorado");
		STATE_LIST.put("CT", "Connecticut");
		STATE_LIST.put("DE", "Delaware");
		STATE_LIST.put("FL", "Florida");
		STATE_LIST.put("GA", "Georgia");
		STATE_LIST.put("HI", "Hawaii");
		STATE_LIST.put("ID", "Idaho");
		STATE_LIST.put("IL", "Illinois");
		STATE_LIST.put("IN", "Indiana");
		STATE_LIST.put("IA", "Iowa");
		STATE_LIST.put("KS", "Kansas");
		STATE_LIST.put("KY", "Kentucky");
		STATE_LIST.put("LA", "Louisiana");
		STATE_LIST.put("ME", "Maine");
		STATE_LIST.put("MD", "Maryland");
		STATE_LIST.put("MA", "Massachusetts");
		STATE_LIST.put("MI", "Michigan");
		STATE_LIST.put("MN", "Minnesota");
		STATE_LIST.put("MS", "Mississippi");
		STATE_LIST.put("MO", "Missouri");
		STATE_LIST.put("MT", "Montana");
		STATE_LIST.put("NE", "Nebraska");
		STATE_LIST.put("NV", "Nevada");
		STATE_LIST.put("NH", "New Hampshire");
		STATE_LIST.put("NJ", "New Jersey");
		STATE_LIST.put("NM", "New Mexico");
		STATE_LIST.put("NY", "New York");
		STATE_LIST.put("NC", "North Carolina");
		STATE_LIST.put("ND", "North Dakota");
		STATE_LIST.put("OH", "Ohio");
		STATE_LIST.put("OK", "Oklahoma");
		STATE_LIST.put("OR", "Oregon");
		STATE_LIST.put("PA", "Pennsylvania");
		STATE_LIST.put("RI", "Rhode Island");
		STATE_LIST.put("SC", "South Carolina");
		STATE_LIST.put("SD", "South Dakota");
		STATE_LIST.put("TN", "Tennessee");
		STATE_LIST.put("TX", "Texas");
		STATE_LIST.put("UT", "Utah");
		STATE_LIST.put("VT", "Vermont");
		STATE_LIST.put("VA", "Virginia");
		STATE_LIST.put("WA", "Washington");
		STATE_LIST.put("WV", "West Virginia");
		STATE_LIST.put("WI", "Wisconsin");
		STATE_LIST.put("WY", "Wyoming");
	}

	// OCF changes End

	public static TreeMap<String, String> DAILY_MAP = new TreeMap<String, String>();
	static {
		DAILY_MAP.put(REPORT_CARTON_PROCESSED,				REPORT_MAP.get(REPORT_CARTON_PROCESSED));
		DAILY_MAP.put(REPORT_ORDER_STAT_PER_NODE,			REPORT_MAP.get(REPORT_ORDER_STAT_PER_NODE));
		DAILY_MAP.put(REPORT_PEND_ORDER, 					REPORT_MAP.get(REPORT_PEND_ORDER));
		DAILY_MAP.put(REPORT_EFC_DAILY_SHIPPED_UNITS,		REPORT_MAP.get(REPORT_EFC_DAILY_SHIPPED_UNITS));
		DAILY_MAP.put(REPORT_FULFILLMENT_STAT_NODSV,		REPORT_MAP.get(REPORT_FULFILLMENT_STAT_NODSV));
		DAILY_MAP.put(REPORT_DEPT_INV, 						REPORT_MAP.get(REPORT_DEPT_INV));
		DAILY_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV,REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT_NODSV));
		DAILY_MAP.put(REPORT_ORDER_DIST_NODSV,				REPORT_MAP.get(REPORT_ORDER_DIST_NODSV));
		DAILY_MAP.put(REPORT_SKU_CANCEL, 					REPORT_MAP.get(REPORT_SKU_CANCEL));
		DAILY_MAP.put(REPORT_ORDER_RESOURCE,				REPORT_MAP.get(REPORT_ORDER_RESOURCE));
		DAILY_MAP.put(REPORT_INVENTORY_TRANSFER,			REPORT_MAP.get(REPORT_INVENTORY_TRANSFER));
		DAILY_MAP.put(REPORT_RDC_DAILY_METRICS,				REPORT_MAP.get(REPORT_RDC_DAILY_METRICS));

	}

	public static TreeMap<String, String> HOURLY_MAP = new TreeMap<String, String>();
	static {
		HOURLY_MAP.put(REPORT_ORDER_DIST_HOURLY,					REPORT_MAP.get(REPORT_ORDER_DIST_HOURLY));
		HOURLY_MAP.put(REPORT_FULFILLMENT_STAT_NODSV_HOURLY,		REPORT_MAP.get(REPORT_FULFILLMENT_STAT_NODSV_HOURLY));
		HOURLY_MAP.put(REPORT_ORDER_DIST_NODSV_HOURLY,				REPORT_MAP.get(REPORT_ORDER_DIST_NODSV_HOURLY));
		HOURLY_MAP.put(REPORT_HOURLY_STAT, 							REPORT_MAP.get(REPORT_HOURLY_STAT));
		HOURLY_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY,REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY));

	}

	public static TreeMap<String, String> HOURLY_TIMESTAMP_MAP = new TreeMap<String, String>();
	static {
		HOURLY_TIMESTAMP_MAP.put(REPORT_ORDER_DIST_HOURLY,						REPORT_MAP.get(REPORT_ORDER_DIST_HOURLY));
		HOURLY_TIMESTAMP_MAP.put(REPORT_FULFILLMENT_STAT_NODSV_HOURLY,			REPORT_MAP.get(REPORT_FULFILLMENT_STAT_NODSV_HOURLY));
		HOURLY_TIMESTAMP_MAP.put(REPORT_ORDER_DIST_NODSV_HOURLY,				REPORT_MAP.get(REPORT_ORDER_DIST_NODSV_HOURLY));
		HOURLY_TIMESTAMP_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_HOURLY,		REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT_HOURLY));
		HOURLY_TIMESTAMP_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY,	REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT_NODSV_HOURLY));
	}

	public static TreeMap<String, String> DATE_HOURLY_MAP = new TreeMap<String, String>();
	static {
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_865,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_865));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875));
		DATE_HOURLY_MAP.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860,	REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_865,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_865));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_875,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_875));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_810,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_810));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_840,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_840));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_855,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_855));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_830,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_830));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_890,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_890));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_885,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_885));
		DATE_HOURLY_MAP.put(REPORT_RDC_ORDER_TURNTIME_860,				REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_860));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_865,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_865));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_875,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_875));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_810,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_810));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_840,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_840));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_855,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_855));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_830,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_830));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_890,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_890));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_885,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_885));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_SCORECARD_860,				REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_860));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_865,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_865));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_875,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_875));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_810,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_810));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_840,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_840));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_855,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_855));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_830,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_830));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_890,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_890));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_885,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_885));
		DATE_HOURLY_MAP.put(REPORT_RDC_DAILY_TURNTIME_860,				REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_860));
	}

	public static TreeMap<String, String> ADHOC_MAP = new TreeMap<String, String>();
	static {
		ADHOC_MAP.put(REPORT_ADHOC_SIMRED, REPORT_MAP.get(REPORT_ADHOC_SIMRED));
		ADHOC_MAP.put(REPORT_ORDER_EXECPTION_REPORT,					REPORT_MAP.get(REPORT_ORDER_EXECPTION_REPORT));
	}

	public static TreeMap<String, String> WEEKLY_MAP = new TreeMap<String, String>();
	static {
		WEEKLY_MAP.put(REPORT_SALES_BY_STATE,					REPORT_MAP.get(REPORT_SALES_BY_STATE));
	}

	public static TreeMap<String, Integer> DB_TYPE_MAP = new TreeMap<String, Integer>();
	static {
		DB_TYPE_MAP.put(DBTYPE_ORACLE, 1);
		DB_TYPE_MAP.put(DBTYPE_DB2, 2);
		DB_TYPE_MAP.put(DBTYPE_MYSQL, 3);
	}

	public static TreeMap<String, String> REFERENCE_MAP = new TreeMap<String, String>();
	static {
		REFERENCE_MAP.put(REPORT_REGIONAL_SOURCING_CLASSIFICATION,					REPORT_MAP.get(REPORT_REGIONAL_SOURCING_CLASSIFICATION));
	}

	public static TreeMap<String, String> SIM_MAP = new TreeMap<String, String>();
	static {

		SIM_MAP.put(REPORT_WMOS_RED_SKU, REPORT_MAP.get(REPORT_WMOS_RED_SKU));
		SIM_MAP.put(REPORT_PREV_DAY_STORE_SHIPMENTS, REPORT_MAP.get(REPORT_PREV_DAY_STORE_SHIPMENTS));
		SIM_MAP.put(REPORT_RDC_SCORE_CARD, REPORT_MAP.get(REPORT_RDC_SCORE_CARD));

	}

	public static TreeMap<String, String> SIM_MAP_RDC1 = new TreeMap<String, String>();
	static {

		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_865));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_875));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_810));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_840));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_855));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_830));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_890));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_885));
		SIM_MAP_RDC1.put(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860,					REPORT_MAP.get(REPORT_RDC_HOURLY_IN_PROCESS_SHIPMENT_860));

	}

	public static TreeMap<String, String> SIM_MAP_RDC2 = new TreeMap<String, String>();
	static {

		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_865,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_865));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_875,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_875));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_810,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_810));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_840,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_840));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_855,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_855));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_830,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_830));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_890,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_890));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_885,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_885));
		SIM_MAP_RDC2.put(REPORT_RDC_ORDER_TURNTIME_860,					REPORT_MAP.get(REPORT_RDC_ORDER_TURNTIME_860));

	}

	public static TreeMap<String, String> SIM_MAP_RDC3 = new TreeMap<String, String>();
	static {

		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_865,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_865));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_875,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_875));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_810,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_810));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_840,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_840));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_855,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_855));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_830,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_830));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_890,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_890));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_885,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_885));
		SIM_MAP_RDC3.put(REPORT_RDC_DAILY_SCORECARD_860,					REPORT_MAP.get(REPORT_RDC_DAILY_SCORECARD_860));

	}

	public static TreeMap<String, String> SIM_MAP_RDC4 = new TreeMap<String, String>();
	static {

		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_865,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_865));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_875,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_875));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_810,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_810));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_840,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_840));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_855,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_855));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_830,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_830));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_890,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_890));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_885,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_885));
		SIM_MAP_RDC4.put(REPORT_RDC_DAILY_TURNTIME_860,					REPORT_MAP.get(REPORT_RDC_DAILY_TURNTIME_860));

	}

	public static TreeMap<String, TreeMap<String, String>> SIM_MAP_RDC = new TreeMap<String, TreeMap<String, String>>();
	static {

		SIM_MAP_RDC.put("RDC Hourly In Process Shipment Reports", SIM_MAP_RDC1);
		SIM_MAP_RDC.put("RDC Order Turntime Reports", SIM_MAP_RDC2);
		SIM_MAP_RDC.put("RDC Daily Scorecard Reports", SIM_MAP_RDC3);
		SIM_MAP_RDC.put("RDC Daily Turntime Reports", SIM_MAP_RDC4);

	}

	public static TreeMap<String, String> DSV_MAP = new TreeMap<String, String>();
	static {

		DSV_MAP.put(REPORT_FULFILLMENT_STAT,					REPORT_MAP.get(REPORT_FULFILLMENT_STAT));
		DSV_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT,					REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT));
		DSV_MAP.put(REPORT_DSV_ORDER_STAT,					REPORT_MAP.get(REPORT_DSV_ORDER_STAT));
		DSV_MAP.put(REPORT_GROUPED_FULFILLMENT_STAT_HOURLY,					REPORT_MAP.get(REPORT_GROUPED_FULFILLMENT_STAT_HOURLY));
	}
	
	public static String ID_SE_MYSQL_ROLES								= "SE_Role_Map";
	public static String ID_SE_MYSQL_ROLE_PERMS							= "SE_Role_Permission_Map";
	
	/* MAPS and VARIABLES for creating the SQLS in the Report Screen */
	public static String ID_7_DAY_FNCL_OVRVW							= "QM_7_Day_Financial_Overview";
	public static String ID_ODR_FRM 									= "QM_AaG_1_Order_Count_Today";
	public static String ID_UNITCT_FRM 									= "QM_AaG_2_Order_Unit_Count_Today";
	public static String ID_PICK_FRM 									= "QM_AaG_3_Avg_Picktickets_Per_Order_Today";
	public static String ID_PICK_FRM_HR 								= "QM_AaG_4_Avg_Picktickets_Per_Hour";
	public static String ID_ODR_DOLLAR_FRM 								= "QM_AaG_5_odrfrmdollar";
	public static String ID_FUFILLMENT_SALES 							= "QM_AaG_6_Demand_Sales_Today";
	public static String ID_SETTLED_SALES_ECOM 							= "QM_AaG_7_Settled_Sales_ECOM_Today_COSA";
	public static String ID_SETTLED_SALES_STORE 						= "QM_AaG_7_Settled_Sales_STORE_Today_COSA";

	// 14-day Fullfillment Performance
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_DAY1_FULLFILL 								= "QM_14DfP_Day_A1_Fulfillment_ALL";
	public static String ID_DAY2_FULLFILL 								= "QM_14DfP_Day_A2_Fulfillment_ALL";
	public static String ID_DAY3_FULLFILL 								= "QM_14DfP_Day_A3_Fulfillment_ALL";
	public static String ID_DAY4_FULLFILL 								= "QM_14DfP_Day_A4_Fulfillment_ALL";
	public static String ID_DAY5_FULLFILL 								= "QM_14DfP_Day_A5_Fulfillment_ALL";
	public static String ID_DAY6_FULLFILL 								= "QM_14DfP_Day_A6_Fulfillment_ALL";
	public static String ID_DAY7_FULLFILL 								= "QM_14DfP_Day_A7_Fulfillment_ALL";
	public static String ID_DAY8_FULLFILL 								= "QM_14DfP_Day_A8_Fulfillment_ALL";
	public static String ID_DAY9_FULLFILL 								= "QM_14DfP_Day_A9_Fulfillment_ALL";
	public static String ID_DAY10_FULLFILL 								= "QM_14DfP_Day_B10_Fulfillment_ALL";
	public static String ID_DAY11_FULLFILL 								= "QM_14DfP_Day_C11_Fulfillment_ALL";
	public static String ID_DAY12_FULLFILL 								= "QM_14DfP_Day_D12_Fulfillment_ALL";
	public static String ID_DAY13_FULLFILL 								= "QM_14DfP_Day_E13_Fulfillment_ALL";
	public static String ID_DAY14_FULLFILL 								= "QM_14DfP_Day_F14_Fulfillment_ALL";

	// 14-day DSV Fulfillment Performance
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_DAY1_FULLFILL_DSV 							= "QM_14DfP_Day_A1_Fulfillment_DSV";
	public static String ID_DAY2_FULLFILL_DSV 							= "QM_14DfP_Day_A2_Fulfillment_DSV";
	public static String ID_DAY3_FULLFILL_DSV 							= "QM_14DfP_Day_A3_Fulfillment_DSV";
	public static String ID_DAY4_FULLFILL_DSV 							= "QM_14DfP_Day_A4_Fulfillment_DSV";
	public static String ID_DAY5_FULLFILL_DSV 							= "QM_14DfP_Day_A5_Fulfillment_DSV";
	public static String ID_DAY6_FULLFILL_DSV 							= "QM_14DfP_Day_A6_Fulfillment_DSV";
	public static String ID_DAY7_FULLFILL_DSV 							= "QM_14DfP_Day_A7_Fulfillment_DSV";
	public static String ID_DAY8_FULLFILL_DSV 							= "QM_14DfP_Day_A8_Fulfillment_DSV";
	public static String ID_DAY9_FULLFILL_DSV 							= "QM_14DfP_Day_A9_Fulfillment_DSV";
	public static String ID_DAY10_FULLFILL_DSV 							= "QM_14DfP_Day_B10_Fulfillment_DSV";
	public static String ID_DAY11_FULLFILL_DSV 							= "QM_14DfP_Day_C11_Fulfillment_DSV";
	public static String ID_DAY12_FULLFILL_DSV 							= "QM_14DfP_Day_D12_Fulfillment_DSV";
	public static String ID_DAY13_FULLFILL_DSV 							= "QM_14DfP_Day_E13_Fulfillment_DSV";
	public static String ID_DAY14_FULLFILL_DSV 							= "QM_14DfP_Day_F14_Fulfillment_DSV";

	// 14-day Network Fulfillment Performance
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_DAY1_FULLFILL_NETWORK 						= "QM_14DfP_Day_A1_Fulfillment_Network";
	public static String ID_DAY2_FULLFILL_NETWORK 						= "QM_14DfP_Day_A2_Fulfillment_Network";
	public static String ID_DAY3_FULLFILL_NETWORK 						= "QM_14DfP_Day_A3_Fulfillment_Network";
	public static String ID_DAY4_FULLFILL_NETWORK 						= "QM_14DfP_Day_A4_Fulfillment_Network";
	public static String ID_DAY5_FULLFILL_NETWORK 						= "QM_14DfP_Day_A5_Fulfillment_Network";
	public static String ID_DAY6_FULLFILL_NETWORK 						= "QM_14DfP_Day_A6_Fulfillment_Network";
	public static String ID_DAY7_FULLFILL_NETWORK 						= "QM_14DfP_Day_A7_Fulfillment_Network";
	public static String ID_DAY8_FULLFILL_NETWORK 						= "QM_14DfP_Day_A8_Fulfillment_Network";
	public static String ID_DAY9_FULLFILL_NETWORK 						= "QM_14DfP_Day_A9_Fulfillment_Network";
	public static String ID_DAY10_FULLFILL_NETWORK 						= "QM_14DfP_Day_B10_Fulfillment_Network";
	public static String ID_DAY11_FULLFILL_NETWORK 						= "QM_14DfP_Day_C11_Fulfillment_Network";
	public static String ID_DAY12_FULLFILL_NETWORK 						= "QM_14DfP_Day_D12_Fulfillment_Network";
	public static String ID_DAY13_FULLFILL_NETWORK 						= "QM_14DfP_Day_E13_Fulfillment_Network";
	public static String ID_DAY14_FULLFILL_NETWORK 						= "QM_14DfP_Day_F14_Fulfillment_Network";

	// fullfillment Performance console
	public static String ID_EFC1_FULLFILL 								= "QM_FP_EFC1_Fulfillment_Performance";
	public static String ID_EFC2_FULLFILL 								= "QM_FP_EFC2_Fulfillment_Performance";
	public static String ID_EFC3_FULLFILL 								= "QM_FP_EFC3_Fulfillment_Performance";
	public static String ID_EFC4_FULLFILL 								= "QM_FP_EFC4_Fulfillment_Performance";
	public static String ID_EFC1_VQ_FULLFILL							= "VM_FP_EFC1_Visual_Fulfillment_Performance";
	public static String ID_EFC2_VQ_FULLFILL 							= "VM_FP_EFC2_Visual_Fulfillment_Performance";
	public static String ID_EFC3_VQ_FULLFILL 							= "VM_FP_EFC3_Visual_Fulfillment_Performance";
	public static String ID_EFC4_VQ_FULLFILL 							= "VM_FP_EFC4_Visual_Fulfillment_Performance";
	public static String ID_EFC1_VQ_PEND								= "VM_FP_EFC1_Pending_Fulfillment";
	public static String ID_EFC2_VQ_PEND								= "VM_FP_EFC2_Pending_Fulfillment";
	public static String ID_EFC3_VQ_PEND								= "VM_FP_EFC3_Pending_Fulfillment";
	public static String ID_EFC4_VQ_PEND								= "VM_FP_EFC4_Pending_Fulfillment";
	public static String XBOPUS_PEND									= "VM_FP_BOPUS_Pending_Fulfillment";
	public static String STORE_PEND										= "VM_FP_SFS_Pending_Fulfillment";
	public static String ID_RDC_PEND 									= "VM_FP_RDC_Pending_Fulfillment";
	public static String ID_RDC810_FULLFILL 							= "QM_FP_RDC810_Fulfillment_Performance";
	public static String ID_RDC830_FULLFILL 							= "QM_FP_RDC830_Fulfillment_Performance";
	public static String ID_RDC840_FULLFILL 							= "QM_FP_RDC840_Fulfillment_Performance";
	public static String ID_RDC855_FULLFILL 							= "QM_FP_RDC855_Fulfillment_Performance";
	public static String ID_RDC860_FULLFILL 							= "QM_FP_RDC860_Fulfillment_Performance";
	public static String ID_RDC865_FULLFILL 							= "QM_FP_RDC865_Fulfillment_Performance";
	public static String ID_RDC875_FULLFILL 							= "QM_FP_RDC875_Fulfillment_Performance";
	public static String ID_RDC885_FULLFILL 							= "QM_FP_RDC885_Fulfillment_Performance";
	public static String ID_RDC890_FULLFILL 							= "QM_FP_RDC890_Fulfillment_Performance";
	public static String ID_RDCTOTAL_FULLFILL 							= "QM_FP_RDC_Total_Fulfillment_Performance";

	/**3PL Changes starts**/
	private static final String ID_LFC1_FULLFILL 						= "QM_FP_LFC1_Fulfillment_Performance";
	private static final String ID_LFC2_FULLFILL 						= "QM_FP_LFC2_Fulfillment_Performance";
	private static final String ID_LFC3_FULLFILL 						= "QM_FP_LFC3_Fulfillment_Performance";
	private static final String ID_LFC4_FULLFILL 						= "QM_FP_LFC4_Fulfillment_Performance";
	/**3PL Changes Ends**/

	//OCF changes start
	public static String ID_STORES_FULLFILL 							= "QM_FP_SFS_Total_Fulfillment_Performance";
	public static String ID_BOPUS_FULLFILL 								= "QM_FP_BOPUS_Total_Fulfillment_Performance";
	//OCF changes End

	// Inventory Console
	public static String ID_DSV_INV_COUNT 								= "QM_IS_DSV_Inventory_Count";
	public static String ID_EFC1_INV_COUNT 								= "QM_IS_EFC1_Inventory_Count";
	public static String ID_EFC2_INV_COUNT 								= "QM_IS_EFC2_Inventory_Count";
	public static String ID_EFC3_INV_COUNT 								= "QM_IS_EFC3_Inventory_Count";
	public static String ID_EFC4_INV_COUNT 								= "QM_IS_EFC4_Inventory_Count";
	public static String ID_RDC810_INV_COUNT 							= "QM_IS_RDC810_Inventory_Count";
	public static String ID_RDC830_INV_COUNT 							= "QM_IS_RDC830_Inventory_Count";
	public static String ID_RDC840_INV_COUNT 							= "QM_IS_RDC840_Inventory_Count";
	public static String ID_RDC855_INV_COUNT 							= "QM_IS_RDC855_Inventory_Count";
	public static String ID_RDC860_INV_COUNT 							= "QM_IS_RDC860_Inventory_Count";
	public static String ID_RDC865_INV_COUNT 							= "QM_IS_RDC865_Inventory_Count";
	public static String ID_RDC875_INV_COUNT 							= "QM_IS_RDC875_Inventory_Count";
	public static String ID_RDC885_INV_COUNT 							= "QM_IS_RDC885_Inventory_Count";
	public static String ID_RDC890_INV_COUNT 							= "QM_IS_RDC890_Inventory_Count";
	public static String ID_RDCTOTAL_INV_COUNT 							= "QM_IS_RDC_Total_Inventory_Count";

	public static String ID_LFC1_INV_COUNT 								= "QM_IS_LFC1_Inventory_Count";
	public static String ID_LFC2_INV_COUNT 								= "QM_IS_LFC2_Inventory_Count";
	public static String ID_LFC3_INV_COUNT 								= "QM_IS_LFC3_Inventory_Count";
	public static String ID_LFC4_INV_COUNT 								= "QM_IS_LFC4_Inventory_Count";
	//OCF changes start
	public static String ID_STORE_INV_COUNT 							= "QM_IS_SFS_Total_Inventory_Count";
	//OCF changes End

	public static String ID_NETWORK_INV_COUNT 							= "QM_IS_Total_Network_Inventory_Count";

	// EFC unique SKU console
	public static String ID_EFC1_REDUNDANCY_COUNT 						= "QM_RS_EFC1_Inventory_Redundancy_Count";
	public static String ID_EFC3_REDUNDANCY_COUNT 						= "QM_RS_EFC3_Inventory_Redundancy_Count";
	public static String ID_EFC2_REDUNDANCY_COUNT 						= "QM_RS_EFC2_Inventory_Redundancy_Count";
	public static String ID_EFC4_REDUNDANCY_COUNT 						= "QM_RS_EFC4_Inventory_Redundancy_Count";
	public static String ID_LFC1_REDUNDANCY_COUNT 						= "QM_RS_LFC1_Inventory_Redundancy_Count";
	public static String ID_LFC2_REDUNDANCY_COUNT 						= "QM_RS_LFC2_Inventory_Redundancy_Count";
	public static String ID_LFC3_REDUNDANCY_COUNT 						= "QM_RS_LFC3_Inventory_Redundancy_Count";
	public static String ID_LFC4_REDUNDANCY_COUNT 						= "QM_RS_LFC4_Inventory_Redundancy_Count";
	public static String ID_RDC810_REDUNDANCY_COUNT 					= "QM_RS_RDC810_Inventory_Redundancy_Count";
	public static String ID_RDC830_REDUNDANCY_COUNT 					= "QM_RS_RDC830_Inventory_Redundancy_Count";
	public static String ID_RDC840_REDUNDANCY_COUNT 					= "QM_RS_RDC840_Inventory_Redundancy_Count";
	public static String ID_RDC855_REDUNDANCY_COUNT 					= "QM_RS_RDC855_Inventory_Redundancy_Count";
	public static String ID_RDC860_REDUNDANCY_COUNT 					= "QM_RS_RDC860_Inventory_Redundancy_Count";
	public static String ID_RDC865_REDUNDANCY_COUNT 					= "QM_RS_RDC865_Inventory_Redundancy_Count";
	public static String ID_RDC875_REDUNDANCY_COUNT 					= "QM_RS_RDC875_Inventory_Redundancy_Count";
	public static String ID_RDC885_REDUNDANCY_COUNT 					= "QM_RS_RDC885_Inventory_Redundancy_Count";
	public static String ID_RDC890_REDUNDANCY_COUNT 					= "QM_RS_RDC890_Inventory_Redundancy_Count";

	public static String ID_RDCTOTAL_REDUNDANCY_COUNT 					= "QM_RS_RDC_Total_Inventory_Redundancy_Count";
	//OCF changes start
	public static String ID_STORES_REDUNDANCY_COUNT 					= "QM_RS_SFS_Total_Inventory_Redundancy_Count";
	//OCF changes End

	//added for cancel dashboard--- glance
	public static String ID_CANCEL_COUNT 								= "CD_Total_Cancel_Count_Today";
	public static String ID_CNCL_BREAKDOWN						        = "CD_Total_Cancel_Count_Today_Breakdown";
	public static String ID_CNCL_SPARK									= "CD_Cancel_Sparkline_Today";
	public static String ID_CNCL_DOLLAR_COUNT 							= "CD_Total_Cancel_Dollar_Amount_Today";
	public static String ID_BOPUS_EXPIRED_COUNT 							= "CD_BOPUS_Expired_Units_Today";
	public static String ID_BOPUS_EXPIRED_DOLLAR_COUNT 							= "CD_BOPUS_Expired_Dollars_Today";

	public static String ID_CANCEL_COUNT_PREV 							= "CD_Total_Cancel_Count_Yesterday";
	public static String ID_CNCL_PREV_BREAKDOWN							= "CD_Total_Cancel_Count_Yesterday_Breakdown";
	public static String ID_CNCL_SPARK_PREV								= "CD_Cancel_Sparkline_Yesterday";
	public static String ID_CNCL_DOLLAR_COUNT_PREV 						= "CD_Total_Cancel_Dollar_Amount_Yesterday";
	public static String ID_BOPUS_EXPIRED_COUNT_PREV 							= "CD_BOPUS_Expired_Units_Yesterday";
	public static String ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV 							= "CD_BOPUS_Expired_Dollars_Yesterday";

	//added for cancel dashboard----10 most cancelled SKU
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_ITEM1_COUNT 								= "CD_Item_A1_Sku_Cancel_Today";
	/* public static String ID_ITEM2_COUNT 								= "CD_Item_A2_Sku_Cancel_Today";
	public static String ID_ITEM3_COUNT 								= "CD_Item_A3_Sku_Cancel_Today";
	public static String ID_ITEM4_COUNT 								= "CD_Item_A4_Sku_Cancel_Today";
	public static String ID_ITEM5_COUNT 								= "CD_Item_A5_Sku_Cancel_Today";
	public static String ID_ITEM6_COUNT 								= "CD_Item_A6_Sku_Cancel_Today";
	public static String ID_ITEM7_COUNT 								= "CD_Item_A7_Sku_Cancel_Today";
	public static String ID_ITEM8_COUNT 								= "CD_Item_A8_Sku_Cancel_Today";
	public static String ID_ITEM9_COUNT 								= "CD_Item_A9_Sku_Cancel_Today";
	public static String ID_ITEM10_COUNT 								= "CD_Item_B10_Sku_Cancel_Today";*/

	public static String ID_ITEM1_COUNT_PREV 							= "CD_Item_A1_Sku_Cancel_Yesterday";
	/*public static String ID_ITEM2_COUNT_PREV 							= "CD_Item_A2_Sku_Cancel_Yesterday";
	public static String ID_ITEM3_COUNT_PREV 							= "CD_Item_A3_Sku_Cancel_Yesterday";
	public static String ID_ITEM4_COUNT_PREV 							= "CD_Item_A4_Sku_Cancel_Yesterday";
	public static String ID_ITEM5_COUNT_PREV 							= "CD_Item_A5_Sku_Cancel_Yesterday";
	public static String ID_ITEM6_COUNT_PREV 							= "CD_Item_A6_Sku_Cancel_Yesterday";
	public static String ID_ITEM7_COUNT_PREV 							= "CD_Item_A7_Sku_Cancel_Yesterday";
	public static String ID_ITEM8_COUNT_PREV 							= "CD_Item_A8_Sku_Cancel_Yesterday";
	public static String ID_ITEM9_COUNT_PREV 							= "CD_Item_A9_Sku_Cancel_Yesterday";
	public static String ID_ITEM10_COUNT_PREV 							= "CD_Item_B10_Sku_Cancel_Yesterday";*/

	//added for cancel dashBoard-----14 day cancellation stats
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_DAY01_CNCL_STAT 							= "CD_Day_A1_Cancels";
	public static String ID_DAY02_CNCL_STAT 							= "CD_Day_A2_Cancels";
	public static String ID_DAY03_CNCL_STAT 							= "CD_Day_A3_Cancels";
	public static String ID_DAY04_CNCL_STAT 							= "CD_Day_A4_Cancels";
	public static String ID_DAY05_CNCL_STAT 							= "CD_Day_A5_Cancels";
	public static String ID_DAY06_CNCL_STAT 							= "CD_Day_A6_Cancels";
	public static String ID_DAY07_CNCL_STAT 							= "CD_Day_A7_Cancels";
	public static String ID_DAY08_CNCL_STAT 							= "CD_Day_A8_Cancels";
	public static String ID_DAY09_CNCL_STAT 							= "CD_Day_A9_Cancels";
	public static String ID_DAY10_CNCL_STAT 							= "CD_Day_B10_Cancels";
	public static String ID_DAY11_CNCL_STAT 							= "CD_Day_C11_Cancels";
	public static String ID_DAY12_CNCL_STAT 							= "CD_Day_D12_Cancels";
	public static String ID_DAY13_CNCL_STAT 							= "CD_Day_E13_Cancels";
	public static String ID_DAY14_CNCL_STAT 							= "CD_Day_F14_Cancels";

	//added for auto cancel section
	public static String ID_DAY01_AUTO_CNCL_STAT 						= "CD_DAY_1_Auto_Cancels";
	public static String ID_DAY02_AUTO_CNCL_STAT						= "CD_DAY_2_Auto_Cancels";
	public static String ID_DAY03_AUTO_CNCL_STAT 						= "CD_DAY_3_Auto_Cancels";
	public static String ID_DAY04_AUTO_CNCL_STAT 						= "CD_DAY_4_Auto_Cancels";
	public static String ID_DAY05_AUTO_CNCL_STAT 						= "CD_DAY_5_Auto_Cancels";
	
	public static String ID_14DAY_CUST_CANCELS							= "CD_14Day_Cancels";

	// Server Status Console
	public static String ID_SERVER_STAT 								= "SRV_OMS_Server_Stats";
	public static String ID_SERVER_STAT_GIV 							= "SRV_GIV_Server_Stats";

	// OMSr
	public static String ID_SERVER_STAT_OMSr							= "SRV_OMSr_Server_Stats";

	// OMSr Training
	public static String ID_SERVER_STAT_OMSr_Training					= "SRV_OMSr_Training_Server_Stats";


	// SIM/RED AdHoc report variables
	public static String ID_SIM_REPORT 									= "RP_SIM_Dashboard";

	// RDC Order Execption Report
	public static String ID_ORDER_EXCEPTION_REPORT 						= "RP_Order_Exception_Dashboard";
	
	//OCF changes Start
	public static String ID_STORE_SQL 									= "SFP_Store";
	public static String ID_STORE_LABEL_SQL 							= "SFP_Store_Label";
	public static String ID_STORE_REGION_FULFILL_SQL 					= "SFP_Store_Region_Fulfillment";
	public static String ID_STORE_STATES_FULFILL_SQL 					= "SFP_Store_States_Fulfillment";
	public static String ID_STORE_FULFILL_SQL 							= "SFP_Store_Fulfillment";

	public static String ID_STORE_REGION_INV_SQL 						= "SFP_Store_Region_Inventory";
	public static String ID_STORE_STATES_INV_SQL 						= "SFP_Store_States_Inventory";
	public static String ID_STORE_INV_SQL 								= "SFP_Store_Inventory";

	public static String ID_STORE_REGION_REDUNDANCY_SQL 				= "SFP_Store_Region_Redundancy";
	public static String ID_STORE_STATES_REDUNDANCY_SQL 				= "SFP_Store_States_Redundancy";
	public static String ID_STORE_REDUNDANCY_SQL 						= "SFP_Store_Redundancy";
	//OCF changes End
	//Collate Print Times for each EFC 1 - 3
	public static String ID_COLLATE_TIME1 								= "WM_Collate_Print_Time_EFC1";
	public static String ID_COLLATE_TIME2 								= "WM_Collate_Print_Time_EFC2";
	public static String ID_COLLATE_TIME3 								= "WM_Collate_Print_Time_EFC3";
	public static String ID_COLLATE_TIME4 								= "WM_Collate_Print_Time_EFC4";

	//Wave Summary EFC 1-4
	public static String ID_WAVE_SUMMARY1 								= "WM_Wave_Summary_EFC1";
	public static String ID_WAVE_SUMMARY2 								= "WM_Wave_Summary_EFC2";
	public static String ID_WAVE_SUMMARY3 								= "WM_Wave_Summary_EFC3";
	public static String ID_WAVE_SUMMARY4 								= "WM_Wave_Summary_EFC4";

	//Purge Statistics
	public static String ID_PURGE_STATS1								= "WM_Purge_Stats_EFC1";
	public static String ID_PURGE_STATS2								= "WM_Purge_Stats_EFC2";
	public static String ID_PURGE_STATS3								= "WM_Purge_Stats_EFC3";
	public static String ID_PURGE_STATS4								= "WM_Purge_Stats_EFC4";

	//14 Day Sales Statistics
	// Naming includes "A-F" due to limitations of String sorting,
	// must pair B-F to maintain the "10,11,etc.." for the day/item
	public static String ID_SALES_STATS0								= "SM_14DSP_Day_A0_Sales_Fulfillment";
	public static String ID_SALES_STATS1								= "SM_14DSP_Day_A1_Sales_Fulfillment";
	public static String ID_SALES_STATS2								= "SM_14DSP_Day_A2_Sales_Fulfillment";
	public static String ID_SALES_STATS3								= "SM_14DSP_Day_A3_Sales_Fulfillment";
	public static String ID_SALES_STATS4								= "SM_14DSP_Day_A4_Sales_Fulfillment";
	public static String ID_SALES_STATS5								= "SM_14DSP_Day_A5_Sales_Fulfillment";
	public static String ID_SALES_STATS6								= "SM_14DSP_Day_A6_Sales_Fulfillment";
	public static String ID_SALES_STATS7								= "SM_14DSP_Day_A7_Sales_Fulfillment";
	public static String ID_SALES_STATS8								= "SM_14DSP_Day_A8_Sales_Fulfillment";
	public static String ID_SALES_STATS9								= "SM_14DSP_Day_A9_Sales_Fulfillment";
	public static String ID_SALES_STATS10								= "SM_14DSP_Day_B10_Sales_Fulfillment";
	public static String ID_SALES_STATS11								= "SM_14DSP_Day_C11_Sales_Fulfillment";
	public static String ID_SALES_STATS12								= "SM_14DSP_Day_D12_Sales_Fulfillment";
	public static String ID_SALES_STATS13								= "SM_14DSP_Day_E13_Sales_Fulfillment";
	public static String ID_SALES_STATS_ALL_14							= "SM_14DSP_All_Days_Sales_Fulfillment";
	public static String ID_SALES_STATS_BREAKDOWN						= "SM_14DSP_Breakdown_Sales_Fulfillment";

	//Utility Forms declared above SQL_MAP to be entered
	/*  Search by PickTicket */
	public static String UTIL_FRM_PKTHDR   								= "UT_Pickticket_Header";
	public static String UTIL_FRM_PKTDTL   								= "UT_Pickticket_Detail";
	public static String UTIL_FRM_CRTNHDR  								= "UT_Carton_Header";
	public static String UTIL_FRM_CRTNDTL  								= "UT_Carton_Detail";
	public static String UTIL_FRM_CRTNTYP  								= "UT_Carton_Type";
	public static String UTIL_FRM_MANHDR   								= "UT_Manifest_Header";
	public static String UTIL_FRM_MANDTL  								= "UT_Manifest_Detail";
	public static String UTIL_FRM_OPKTHDR 								= "UT_Output_Pickticket_Header";
	public static String UTIL_FRM_OPKTDTL  								= "UT_Output_Pickticket_Detail";
	public static String UTIL_FRM_OCRTNHDR 								= "UT_Output_Carton_Header";
	public static String UTIL_FRM_OCRTNDTL 								= "UT_Output_Carton_Detail";
	public static String UTIL_FRM_CNCLS    								= "UT_Cancels";
	public static String UTIL_FRM_INV_WM   								= "UT_Inventory_WM";

	/* Search by Carton */
	public static String UTIL_FRM_CARTON_HEADER 						= "UT_Carton_Search_Carton_Header";
	public static String UTIL_FRM_CARTON_DETAIL 						= "UT_Carton_Search_Carton_Detail";
	public static String UTIL_FRM_CARTON_PKTHDR 						= "UT_Carton_Search_Pickticket_Header";

	/* Search by Task for 2006 */
	public static String UTIL_FRM_TASK_HDR 								= "UT_Task_Header";
	public static String UTIL_FRM_TASK_DTL 								= "UT_Task_Detail";
	public static String UTIL_FRM_TASK_ALLOCATION 						= "UT_Task_Allocation";
	public static String UTIL_FRM_TASK_CASE 							= "UT_Task_Case_Details";

	public static String ID_UTIL_OMS_CANCEL_SQL							= "UT_Order_Cancel_OMS";
	public static String ID_UTIL_CANCEL_ORDER_WMOS_SQL					= "UT_Order_Cancel_WMOS";
	public static String ID_UTIL_CANCEL_ORDER_WMOS_SQL2					= "UT_Order_Cancel_WMOS2";
	//Code changes for Heat Map tab - Google chart implementation
	public static String ID_HEAT_MAP_EFC_SQL 							= "HM_EFC";
	public static String ID_HEAT_MAP_STORES_SQL 						= "HM_Stores";
	public static String ID_HEAT_MAP_OVERALL_SQL 						= "HM_Overall";

	//Start MarketPlace 2014 changes
	public static String MP_14_DAY_SALES_SQL 							= "MP_14_Day_Sales_Sql";
	public static String MP_6_MONTH_SALES_SQL 							= "MP_6_Month_Sales_Sql";
	public static String MP_DAILY_DELINQ_ORDER_SQL						= "MP_Daily_Delinq_Order_Sql";
	public static String MP_PO_CLOSED_YESTERDAY_SQL 					= "MP_PO_Closed_Yesterday_Sql";
	public static String MP_14_DAY_FULFILL_SQL 							= "MP_14_Day_Fufill_Sql";
	public static String MP_DAILY_FULFILL_SQL 							= "MP_Daily_Fulfill_Sql";
	public static String MP_PRODUCT_TREND_SQL 							= "MP_Product_Trend_Sql";
	//End MarketPlace 2014 changes

	public static TreeMap<String, String> SQL_MAP = new TreeMap<String, String>();

	
	
	public static TreeMap<String, String> SQL_MODEL_MAP = new TreeMap<String, String>();
	static {
		
		SQL_MODEL_MAP.put(ID_SE_MYSQL_ROLES, "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SE_MYSQL_ROLE_PERMS, "com.storeelf.report.web.model.impl.MultiColumnModal");
		
		SQL_MODEL_MAP.put(ID_7_DAY_FNCL_OVRVW, "com.storeelf.report.web.model.impl.MultiColumnModal");
		
		SQL_MODEL_MAP.put(ID_ODR_FRM,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_UNITCT_FRM,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_PICK_FRM,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_PICK_FRM_HR,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_ODR_DOLLAR_FRM,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_SETTLED_SALES_ECOM,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_SETTLED_SALES_STORE,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_FUFILLMENT_SALES,					"com.storeelf.report.web.model.impl.GenericCountModel");

		//added for cancel dashboard
		SQL_MODEL_MAP.put(ID_CANCEL_COUNT,						"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_CNCL_BREAKDOWN,			"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_CNCL_SPARK, 						"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_CNCL_DOLLAR_COUNT,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_BOPUS_EXPIRED_COUNT,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT,					"com.storeelf.report.web.model.impl.GenericCountModel");

		SQL_MODEL_MAP.put(ID_CANCEL_COUNT_PREV,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_CNCL_PREV_BREAKDOWN,			"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_CNCL_SPARK_PREV, 						"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_CNCL_DOLLAR_COUNT_PREV,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_BOPUS_EXPIRED_COUNT_PREV,					"com.storeelf.report.web.model.impl.GenericCountModel");
		SQL_MODEL_MAP.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV,					"com.storeelf.report.web.model.impl.GenericCountModel");

		SQL_MODEL_MAP.put(ID_ITEM1_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM1_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		/* SQL_MODEL_MAP.put(ID_ITEM2_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM3_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM4_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM5_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM6_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM7_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM8_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM9_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM10_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM2_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM3_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM4_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM5_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM6_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM7_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM8_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM9_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_ITEM10_COUNT_PREV,					"com.storeelf.report.web.model.impl.MultiColumnModal"); */



		//14 day cancellation stats
		SQL_MODEL_MAP.put(ID_DAY01_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY02_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY03_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY04_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY05_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY06_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY07_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY08_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY09_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY10_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY11_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY12_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY13_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY14_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		//added for auto cancel
		SQL_MODEL_MAP.put(ID_DAY01_AUTO_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY02_AUTO_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY03_AUTO_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY04_AUTO_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY05_AUTO_CNCL_STAT,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		SQL_MODEL_MAP.put(ID_14DAY_CUST_CANCELS, 					"com.storeelf.report.web.model.impl.MultiColumnModal");
		
		//ending-Mir

		SQL_MODEL_MAP.put(ID_DAY1_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY2_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY3_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY4_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY5_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY6_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY7_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		// Including upto 14 days Fullfillment Performance
		SQL_MODEL_MAP.put(ID_DAY8_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY9_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY10_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY11_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY12_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY13_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY14_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		SQL_MODEL_MAP.put(ID_DAY1_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY2_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY3_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY4_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY5_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY6_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY7_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		//Including upto 14 days Fullfillment Performance
		SQL_MODEL_MAP.put(ID_DAY8_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY9_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY10_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY11_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY12_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY13_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY14_FULLFILL_DSV,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		SQL_MODEL_MAP.put(ID_DAY1_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY2_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY3_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY4_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY5_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY6_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY7_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		//Including upto 14 days Fullfillment Performance

		SQL_MODEL_MAP.put(ID_DAY8_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY9_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY10_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY11_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY12_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY13_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_DAY14_FULLFILL_NETWORK,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		//14 Day Sales Stats
		SQL_MODEL_MAP.put(ID_SALES_STATS0,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS1,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS2,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS3,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS4,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS5,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS6,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS7,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS8,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS9,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS10,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS11,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS12,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS13,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS_ALL_14,			"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_SALES_STATS_BREAKDOWN,			"com.storeelf.report.web.model.impl.MultiColumnModal");

		//OCF change start
		SQL_MODEL_MAP.put(ID_STORES_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_BOPUS_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_STORE_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_STORES_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_STORE_LABEL_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_REGION_FULFILL_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_STATES_FULFILL_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_REGION_INV_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_STATES_INV_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_REGION_REDUNDANCY_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_STATES_REDUNDANCY_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_FULFILL_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_INV_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_STORE_REDUNDANCY_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		//OCF changes End

		SQL_MODEL_MAP.put(ID_EFC1_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC2_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC3_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC4_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC1_VQ_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC2_VQ_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC3_VQ_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC4_VQ_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC1_VQ_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC2_VQ_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC3_VQ_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC4_VQ_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(XBOPUS_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(STORE_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");

	  /**3PL Changes starts**/
		SQL_MODEL_MAP.put(ID_LFC1_FULLFILL,			        "com.storeelf.report.web.model.impl.MultiColumnModal");
		    SQL_MODEL_MAP.put(ID_LFC2_FULLFILL,			        "com.storeelf.report.web.model.impl.MultiColumnModal");
		    SQL_MODEL_MAP.put(ID_LFC3_FULLFILL,			        "com.storeelf.report.web.model.impl.MultiColumnModal");
		    SQL_MODEL_MAP.put(ID_LFC4_FULLFILL,			        "com.storeelf.report.web.model.impl.MultiColumnModal");
		    /**3PL Changes Ends**/
		SQL_MODEL_MAP.put(ID_RDC865_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC810_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC840_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC855_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC830_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC890_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC885_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC875_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC860_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDCTOTAL_FULLFILL,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		SQL_MODEL_MAP.put(ID_DSV_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC1_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC2_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC3_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC4_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
	  /**3PL Changes starts**/
    SQL_MODEL_MAP.put(ID_LFC1_INV_COUNT,	            "com.storeelf.report.web.model.impl.MultiColumnModal");
        SQL_MODEL_MAP.put(ID_LFC2_INV_COUNT,	            "com.storeelf.report.web.model.impl.MultiColumnModal");
        SQL_MODEL_MAP.put(ID_LFC3_INV_COUNT,	            "com.storeelf.report.web.model.impl.MultiColumnModal");
        SQL_MODEL_MAP.put(ID_LFC4_INV_COUNT,	            "com.storeelf.report.web.model.impl.MultiColumnModal");
        /**3PL Changes Ends**/
		SQL_MODEL_MAP.put(ID_RDC865_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC810_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC840_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC855_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC830_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC890_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC885_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC875_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC860_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDCTOTAL_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC_PEND,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_NETWORK_INV_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		SQL_MODEL_MAP.put(ID_SERVER_STAT,					"com.storeelf.report.web.model.impl.ServerListModel");
		SQL_MODEL_MAP.put(ID_SERVER_STAT_GIV,					"com.storeelf.report.web.model.impl.ServerListModel");
		SQL_MODEL_MAP.put(ID_SERVER_STAT_OMSr,					"com.storeelf.report.web.model.impl.ServerListModel");
		SQL_MODEL_MAP.put(ID_SERVER_STAT_OMSr_Training,				"com.storeelf.report.web.model.impl.ServerListModel");
		SQL_MODEL_MAP.put(ID_EFC2_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC1_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC3_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_EFC4_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
	  /**3PL Changes starts**/
	  SQL_MODEL_MAP.put(ID_LFC2_REDUNDANCY_COUNT,		          "com.storeelf.report.web.model.impl.MultiColumnModal");
	      SQL_MODEL_MAP.put(ID_LFC1_REDUNDANCY_COUNT,		          "com.storeelf.report.web.model.impl.MultiColumnModal");
	      SQL_MODEL_MAP.put(ID_LFC3_REDUNDANCY_COUNT,		          "com.storeelf.report.web.model.impl.MultiColumnModal");
	      SQL_MODEL_MAP.put(ID_LFC4_REDUNDANCY_COUNT,		          "com.storeelf.report.web.model.impl.MultiColumnModal");
	      /**3PL Changes Ends**/
		SQL_MODEL_MAP.put(ID_RDC865_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC810_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC840_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC855_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC830_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC890_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC885_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC875_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDC860_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(ID_RDCTOTAL_REDUNDANCY_COUNT,					"com.storeelf.report.web.model.impl.MultiColumnModal");

		// Added with WMOS Dashboard changes.
		SQL_MODEL_MAP.put(ID_COLLATE_TIME1,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_COLLATE_TIME2,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_COLLATE_TIME3,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_COLLATE_TIME4,					"com.storeelf.report.web.model.impl.GenericTabularModel");

		SQL_MODEL_MAP.put(ID_WAVE_SUMMARY1,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_WAVE_SUMMARY2,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_WAVE_SUMMARY3,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_WAVE_SUMMARY4,					"com.storeelf.report.web.model.impl.GenericTabularModel");


		SQL_MODEL_MAP.put(ID_PURGE_STATS1,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_PURGE_STATS2,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		SQL_MODEL_MAP.put(ID_PURGE_STATS3,					"com.storeelf.report.web.model.impl.GenericTabularModel");
		//end wmos dashbaord changes

		SQL_MODEL_MAP.put(ID_SIM_REPORT,					"com.storeelf.report.web.model.impl.SIMInventoryModel");
		SQL_MODEL_MAP.put(ID_ORDER_EXCEPTION_REPORT,					"com.storeelf.report.web.model.impl.RDCOrderExceptionModel");
		//changes for google pie charts in heat map tab
		SQL_MODEL_MAP.put(ID_HEAT_MAP_EFC_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_HEAT_MAP_STORES_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");
		SQL_MODEL_MAP.put(ID_HEAT_MAP_OVERALL_SQL,					"com.storeelf.report.web.model.impl.StoresDataModel");

		//Start MarketPlace 2014 changes
		SQL_MODEL_MAP.put(MP_14_DAY_SALES_SQL,        "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_6_MONTH_SALES_SQL,       "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_DAILY_DELINQ_ORDER_SQL,  "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_PO_CLOSED_YESTERDAY_SQL, "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_14_DAY_FULFILL_SQL,      "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_DAILY_FULFILL_SQL,       "com.storeelf.report.web.model.impl.MultiColumnModal");
		SQL_MODEL_MAP.put(MP_PRODUCT_TREND_SQL,       "com.storeelf.report.web.model.impl.MultiColumnModal");
		//End MarketPlace 2014 changes
		
	}


	public static TreeMap<String, Long> SQL_TIME_MAP = new TreeMap<String, Long>();
	static {
		SQL_TIME_MAP.put(ID_SE_MYSQL_ROLES, Long.valueOf(30000));
		SQL_TIME_MAP.put(ID_SE_MYSQL_ROLE_PERMS, Long.valueOf(30000));
		
		SQL_TIME_MAP.put(ID_7_DAY_FNCL_OVRVW, Long.valueOf(900000));
		
		SQL_TIME_MAP.put(ID_ODR_FRM, Long.valueOf(900000));    //time was changed from 300000ms(5min) to 900000ms (15min) for peak and heavy system load
		SQL_TIME_MAP.put(ID_UNITCT_FRM, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_PICK_FRM, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_PICK_FRM_HR, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_ODR_DOLLAR_FRM, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_SETTLED_SALES_ECOM, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_SETTLED_SALES_STORE, Long.valueOf(900000));
		SQL_TIME_MAP.put(ID_FUFILLMENT_SALES, Long.valueOf(900000));
		//added for cancel dashboard
		SQL_TIME_MAP.put(ID_CANCEL_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_CNCL_BREAKDOWN, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_CNCL_SPARK, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_CNCL_DOLLAR_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_BOPUS_EXPIRED_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT, Long.valueOf(3600000));

		SQL_TIME_MAP.put(ID_CANCEL_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_CNCL_PREV_BREAKDOWN, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_CNCL_SPARK_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_CNCL_DOLLAR_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_BOPUS_EXPIRED_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV, Long.valueOf(86400000));

		SQL_TIME_MAP.put(ID_ITEM1_COUNT, Long.valueOf(3600000));
		/* SQL_TIME_MAP.put(ID_ITEM2_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM3_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM4_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM5_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM6_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM7_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM8_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM9_COUNT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_ITEM10_COUNT, Long.valueOf(3600000)); */

		SQL_TIME_MAP.put(ID_ITEM1_COUNT_PREV, Long.valueOf(86400000));
		/* SQL_TIME_MAP.put(ID_ITEM2_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM3_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM4_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM5_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM6_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM7_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM8_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM9_COUNT_PREV, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_ITEM10_COUNT_PREV, Long.valueOf(86400000)); */

		SQL_TIME_MAP.put(ID_DAY01_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY02_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY03_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY04_CNCL_STAT, Long.valueOf(28800000));
		SQL_TIME_MAP.put(ID_DAY05_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY06_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY07_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY08_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY09_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY10_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY11_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY12_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY13_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY14_CNCL_STAT, Long.valueOf(3600000));

		SQL_TIME_MAP.put(ID_DAY01_AUTO_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY02_AUTO_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY03_AUTO_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY04_AUTO_CNCL_STAT, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_DAY05_AUTO_CNCL_STAT, Long.valueOf(3600000));
		
		SQL_TIME_MAP.put(ID_14DAY_CUST_CANCELS, Long.valueOf(3600000));


		SQL_TIME_MAP.put(ID_DAY1_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY2_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY3_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY4_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY5_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY6_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY7_FULLFILL, Long.valueOf(14400000));
		// Including upto 14 days Fullfillment Performance
		SQL_TIME_MAP.put(ID_DAY8_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY9_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY10_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY11_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY12_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY13_FULLFILL, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY14_FULLFILL, Long.valueOf(14400000));

		SQL_TIME_MAP.put(ID_DAY1_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY2_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY3_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY4_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY5_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY6_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY7_FULLFILL_DSV, Long.valueOf(14400000));
		// Including upto 14 days Fullfillment Performance
		SQL_TIME_MAP.put(ID_DAY8_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY9_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY10_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY11_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY12_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY13_FULLFILL_DSV, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY14_FULLFILL_DSV, Long.valueOf(14400000));

		SQL_TIME_MAP.put(ID_DAY1_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY2_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY3_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY4_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY5_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY6_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY7_FULLFILL_NETWORK, Long.valueOf(14400000));
		// Including upto 14 days Fullfillment Performance
		SQL_TIME_MAP.put(ID_DAY8_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY9_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY10_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY11_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY12_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY13_FULLFILL_NETWORK, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_DAY14_FULLFILL_NETWORK, Long.valueOf(14400000));

		//14 Day Sales Stats
		SQL_TIME_MAP.put(ID_SALES_STATS0, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS1, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS2, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS3, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS4, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS5, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS6, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS7, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS8, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS9, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS10, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS11, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS12, Long.valueOf(14400000));
		SQL_TIME_MAP.put(ID_SALES_STATS13, Long.valueOf(14400000));
        SQL_TIME_MAP.put(ID_SALES_STATS_ALL_14, Long.valueOf(14400000));
        SQL_TIME_MAP.put(ID_SALES_STATS_BREAKDOWN, Long.valueOf(14400000));

		SQL_TIME_MAP.put(ID_EFC1_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC1_VQ_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC1_VQ_PEND, Long.valueOf(3600000));
		//OCF change start
		SQL_TIME_MAP.put(ID_STORES_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_BOPUS_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_STORE_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_STORES_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_STORE_LABEL_SQL, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_STORE_REGION_FULFILL_SQL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_STORE_STATES_FULFILL_SQL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_STORE_REGION_INV_SQL, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_STORE_STATES_INV_SQL, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_STORE_REGION_REDUNDANCY_SQL, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_STORE_STATES_REDUNDANCY_SQL, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_STORE_FULFILL_SQL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_STORE_INV_SQL, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_STORE_REDUNDANCY_SQL, Long.valueOf(21600000));
		//OCF changes End
		SQL_TIME_MAP.put(ID_EFC2_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC3_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC4_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC2_VQ_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC3_VQ_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC4_VQ_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC2_VQ_PEND, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC3_VQ_PEND, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_EFC4_VQ_PEND, Long.valueOf(3600000));
		SQL_TIME_MAP.put(XBOPUS_PEND, Long.valueOf(3600000));
		SQL_TIME_MAP.put(STORE_PEND, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC_PEND, Long.valueOf(3600000));
	  /**3PL Changes starts**/
		SQL_TIME_MAP.put(ID_LFC1_FULLFILL, Long.valueOf(3600000));
	  SQL_TIME_MAP.put(ID_LFC2_FULLFILL, Long.valueOf(3600000));
    SQL_TIME_MAP.put(ID_LFC3_FULLFILL, Long.valueOf(3600000));
    SQL_TIME_MAP.put(ID_LFC4_FULLFILL, Long.valueOf(3600000));
    /**3PL Changes Ends**/
		SQL_TIME_MAP.put(ID_RDC865_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC810_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC840_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC855_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC830_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC890_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC885_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC875_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDC860_FULLFILL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_RDCTOTAL_FULLFILL, Long.valueOf(3600000));

		SQL_TIME_MAP.put(ID_DSV_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_EFC1_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_EFC2_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_EFC3_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_EFC4_INV_COUNT, Long.valueOf(86400000));
	  /**3PL Changes starts**/
    SQL_TIME_MAP.put(ID_LFC1_INV_COUNT, Long.valueOf(86400000));
    SQL_TIME_MAP.put(ID_LFC2_INV_COUNT, Long.valueOf(86400000));
    SQL_TIME_MAP.put(ID_LFC3_INV_COUNT, Long.valueOf(86400000));
    SQL_TIME_MAP.put(ID_LFC4_INV_COUNT, Long.valueOf(86400000));
    /**3PL Changes Ends**/
		SQL_TIME_MAP.put(ID_RDC865_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC810_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC840_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC855_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC830_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC890_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC885_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC875_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDC860_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_RDCTOTAL_INV_COUNT, Long.valueOf(86400000));
		SQL_TIME_MAP.put(ID_NETWORK_INV_COUNT, Long.valueOf(86400000));

		SQL_TIME_MAP.put(ID_SERVER_STAT, Long.valueOf(60000));
		SQL_TIME_MAP.put(ID_SERVER_STAT_GIV, Long.valueOf(60000));
		SQL_TIME_MAP.put(ID_SERVER_STAT_OMSr, Long.valueOf(60000));
		SQL_TIME_MAP.put(ID_SERVER_STAT_OMSr_Training, Long.valueOf(60000));
		SQL_TIME_MAP.put(ID_EFC1_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_EFC2_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_EFC3_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_EFC4_REDUNDANCY_COUNT, Long.valueOf(21600000));
	  /**3PL Changes starts**/
    SQL_TIME_MAP.put(ID_LFC1_REDUNDANCY_COUNT, Long.valueOf(21600000));
    SQL_TIME_MAP.put(ID_LFC2_REDUNDANCY_COUNT, Long.valueOf(21600000));
    SQL_TIME_MAP.put(ID_LFC3_REDUNDANCY_COUNT, Long.valueOf(21600000));
    SQL_TIME_MAP.put(ID_LFC4_REDUNDANCY_COUNT, Long.valueOf(21600000));
    /**3PL Changes Ends**/
		SQL_TIME_MAP.put(ID_RDC865_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC810_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC840_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC855_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC830_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC890_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC885_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC875_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDC860_REDUNDANCY_COUNT, Long.valueOf(21600000));
		SQL_TIME_MAP.put(ID_RDCTOTAL_REDUNDANCY_COUNT, Long.valueOf(21600000));

		SQL_TIME_MAP.put(ID_SIM_REPORT, Long.valueOf(1800000));
		SQL_TIME_MAP.put(ID_ORDER_EXCEPTION_REPORT, Long.valueOf(1800000));
		
		//WMOS dashboard changes
		//10 minutes
		SQL_TIME_MAP.put(ID_COLLATE_TIME1, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_COLLATE_TIME2, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_COLLATE_TIME3, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_COLLATE_TIME4, Long.valueOf(600000));

		SQL_TIME_MAP.put(ID_WAVE_SUMMARY1, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_WAVE_SUMMARY2, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_WAVE_SUMMARY3, Long.valueOf(600000));
		SQL_TIME_MAP.put(ID_WAVE_SUMMARY4, Long.valueOf(600000));

		//1 hour
		SQL_TIME_MAP.put(ID_PURGE_STATS1, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_PURGE_STATS2, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_PURGE_STATS3, Long.valueOf(3600000));
		//changes for google pie chart in heat map tab
		SQL_TIME_MAP.put(ID_HEAT_MAP_EFC_SQL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_HEAT_MAP_STORES_SQL, Long.valueOf(3600000));
		SQL_TIME_MAP.put(ID_HEAT_MAP_OVERALL_SQL, Long.valueOf(3600000));

		//Start MarketPlace 2014 Changes
		SQL_TIME_MAP.put(MP_14_DAY_SALES_SQL, Long.valueOf(14400000)); 			//4 hours
		SQL_TIME_MAP.put(MP_6_MONTH_SALES_SQL, Long.valueOf(604800000)); 		//1 week
		SQL_TIME_MAP.put(MP_DAILY_DELINQ_ORDER_SQL, Long.valueOf(14400000)); 	//4 hours
		SQL_TIME_MAP.put(MP_PO_CLOSED_YESTERDAY_SQL, Long.valueOf(86400000)); 	//1 Day
		SQL_TIME_MAP.put(MP_14_DAY_FULFILL_SQL, Long.valueOf(14400000)); 		//4 hours
		SQL_TIME_MAP.put(MP_DAILY_FULFILL_SQL, Long.valueOf(14400000));			//4 hours
		SQL_TIME_MAP.put(MP_PRODUCT_TREND_SQL, Long.valueOf(14400000));			//4 hours
		//End MarketPlace 2014 Changes
	}

	public static TreeMap<String, String> SQL_FRM_SIM = new TreeMap<String, String>();
	static {
		SQL_FRM_SIM.put(ID_SIM_REPORT, SQL_MAP.get(ID_SIM_REPORT));
	}

	public static TreeMap<String, String> SQL_FRM_ODR_EXECPTION = new TreeMap<String, String>();
	static {
		SQL_FRM_ODR_EXECPTION.put(ID_ORDER_EXCEPTION_REPORT,					SQL_MAP.get(ID_ORDER_EXCEPTION_REPORT));

	}

	public static TreeMap<String, String> SQL_FRM_1 = new TreeMap<String, String>();
	static {
		
		SQL_FRM_1.put(ID_ODR_FRM, SQL_MAP.get(ID_ODR_FRM));
		SQL_FRM_1.put(ID_UNITCT_FRM, SQL_MAP.get(ID_UNITCT_FRM));
		SQL_FRM_1.put(ID_PICK_FRM, SQL_MAP.get(ID_PICK_FRM));
		//SQL_FRM_1.put(ID_PICK_FRM_HR, SQL_MAP.get(ID_PICK_FRM_HR));
		SQL_FRM_1.put(ID_ODR_DOLLAR_FRM, SQL_MAP.get(ID_ODR_DOLLAR_FRM));
		//SQL_FRM_1.put(ID_SETTLED_SALES, SQL_MAP.get(ID_SETTLED_SALES));
		SQL_FRM_1.put(ID_SETTLED_SALES_ECOM, SQL_MAP.get(ID_SETTLED_SALES_ECOM));
		SQL_FRM_1.put(ID_SETTLED_SALES_STORE, SQL_MAP.get(ID_SETTLED_SALES_STORE));
		//SQL_FRM_1.put(ID_FUFILLMENT_SALES, SQL_MAP.get(ID_FUFILLMENT_SALES));
	}


	//Added for cancel dashboard by Mir----Glance
	public static TreeMap<String, String> SQL_CNCL_FRM_1 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1.put(ID_CANCEL_COUNT, SQL_MAP.get(ID_CANCEL_COUNT));
		SQL_CNCL_FRM_1.put(ID_CNCL_DOLLAR_COUNT, SQL_MAP.get(ID_CNCL_DOLLAR_COUNT));
		SQL_CNCL_FRM_1.put(ID_BOPUS_EXPIRED_COUNT, SQL_MAP.get(ID_BOPUS_EXPIRED_COUNT));
		SQL_CNCL_FRM_1.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT, SQL_MAP.get(ID_BOPUS_EXPIRED_DOLLAR_COUNT));
	}

	public static TreeMap<String, String> SQL_CNCL_FRM_1_1 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1_1.put(ID_CANCEL_COUNT_PREV, SQL_MAP.get(ID_CANCEL_COUNT_PREV));
		SQL_CNCL_FRM_1_1.put(ID_CNCL_DOLLAR_COUNT_PREV, SQL_MAP.get(ID_CNCL_DOLLAR_COUNT_PREV));
		SQL_CNCL_FRM_1_1.put(ID_BOPUS_EXPIRED_COUNT_PREV, SQL_MAP.get(ID_BOPUS_EXPIRED_COUNT_PREV));
		SQL_CNCL_FRM_1_1.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV, SQL_MAP.get(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV));
	}

	public static TreeMap<String, String> SQL_CNCL_FRM_1_2 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1_2.put(ID_CNCL_SPARK, SQL_MAP.get(ID_CNCL_SPARK));
	}

	public static TreeMap<String, String> SQL_CNCL_FRM_1_3 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1_3.put(ID_CNCL_SPARK_PREV, SQL_MAP.get(ID_CNCL_SPARK_PREV));
	}
	
	public static TreeMap<String, String> SQL_CNCL_FRM_1_4 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1_4.put(ID_CNCL_BREAKDOWN, SQL_MAP.get(ID_CNCL_BREAKDOWN));
	}
	
	public static TreeMap<String, String> SQL_CNCL_FRM_1_5 = new TreeMap<String, String>();
	static {
		SQL_CNCL_FRM_1_5.put(ID_CNCL_PREV_BREAKDOWN, SQL_MAP.get(ID_CNCL_PREV_BREAKDOWN));
	}

	//Added for cancel dashboard by Mir----Top 10 cancelled
	public static TreeMap<String, String> SQL_CNCL_FRM_2 = new TreeMap<String, String>();
	static{
		SQL_CNCL_FRM_2.put(ID_ITEM1_COUNT, SQL_MAP.get(ID_ITEM1_COUNT));
		/* SQL_CNCL_FRM_2.put(ID_ITEM2_COUNT, SQL_MAP.get(ID_ITEM2_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM3_COUNT, SQL_MAP.get(ID_ITEM3_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM4_COUNT, SQL_MAP.get(ID_ITEM4_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM5_COUNT, SQL_MAP.get(ID_ITEM5_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM6_COUNT, SQL_MAP.get(ID_ITEM6_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM7_COUNT, SQL_MAP.get(ID_ITEM7_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM8_COUNT, SQL_MAP.get(ID_ITEM8_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM9_COUNT, SQL_MAP.get(ID_ITEM9_COUNT));
		SQL_CNCL_FRM_2.put(ID_ITEM10_COUNT, SQL_MAP.get(ID_ITEM10_COUNT)); */
	}

	public static TreeMap<String, String> SQL_CNCL_FRM_2_1 = new TreeMap<String, String>();
	static{
		SQL_CNCL_FRM_2_1.put(ID_ITEM1_COUNT_PREV, SQL_MAP.get(ID_ITEM1_COUNT_PREV));
		/*SQL_CNCL_FRM_2_1.put(ID_ITEM2_COUNT_PREV, SQL_MAP.get(ID_ITEM2_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM3_COUNT_PREV, SQL_MAP.get(ID_ITEM3_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM4_COUNT_PREV, SQL_MAP.get(ID_ITEM4_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM5_COUNT_PREV, SQL_MAP.get(ID_ITEM5_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM6_COUNT_PREV, SQL_MAP.get(ID_ITEM6_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM7_COUNT_PREV, SQL_MAP.get(ID_ITEM7_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM8_COUNT_PREV, SQL_MAP.get(ID_ITEM8_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM9_COUNT_PREV, SQL_MAP.get(ID_ITEM9_COUNT_PREV));
		SQL_CNCL_FRM_2_1.put(ID_ITEM10_COUNT_PREV, SQL_MAP.get(ID_ITEM10_COUNT_PREV)); */
	}


	public static TreeMap<String, String> SQL_CNCL_FRM_3 = new TreeMap<String, String>();
	static{
		SQL_CNCL_FRM_3.put(ID_DAY01_CNCL_STAT, SQL_MAP.get(ID_DAY01_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY02_CNCL_STAT, SQL_MAP.get(ID_DAY02_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY03_CNCL_STAT, SQL_MAP.get(ID_DAY03_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY04_CNCL_STAT, SQL_MAP.get(ID_DAY04_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY05_CNCL_STAT, SQL_MAP.get(ID_DAY05_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY06_CNCL_STAT, SQL_MAP.get(ID_DAY06_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY07_CNCL_STAT, SQL_MAP.get(ID_DAY07_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY08_CNCL_STAT, SQL_MAP.get(ID_DAY08_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY09_CNCL_STAT, SQL_MAP.get(ID_DAY09_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY10_CNCL_STAT, SQL_MAP.get(ID_DAY10_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY11_CNCL_STAT, SQL_MAP.get(ID_DAY11_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY12_CNCL_STAT, SQL_MAP.get(ID_DAY12_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY13_CNCL_STAT, SQL_MAP.get(ID_DAY13_CNCL_STAT));
		SQL_CNCL_FRM_3.put(ID_DAY14_CNCL_STAT, SQL_MAP.get(ID_DAY14_CNCL_STAT));
	}

	public static TreeMap<String, String> SQL_CNCL_FRM_4 = new TreeMap<String, String>();
	static{

		SQL_CNCL_FRM_4.put(ID_DAY01_AUTO_CNCL_STAT, SQL_MAP.get(ID_DAY01_AUTO_CNCL_STAT));
		SQL_CNCL_FRM_4.put(ID_DAY02_AUTO_CNCL_STAT, SQL_MAP.get(ID_DAY02_AUTO_CNCL_STAT));
		SQL_CNCL_FRM_4.put(ID_DAY03_AUTO_CNCL_STAT, SQL_MAP.get(ID_DAY03_AUTO_CNCL_STAT));
		SQL_CNCL_FRM_4.put(ID_DAY04_AUTO_CNCL_STAT, SQL_MAP.get(ID_DAY04_AUTO_CNCL_STAT));
		SQL_CNCL_FRM_4.put(ID_DAY05_AUTO_CNCL_STAT, SQL_MAP.get(ID_DAY05_AUTO_CNCL_STAT));

	}
	
	public static TreeMap<String, String> SQL_CNCL_FRM_5 = new TreeMap<String, String>();
	static{
		SQL_CNCL_FRM_5.put(ID_14DAY_CUST_CANCELS, SQL_MAP.get(ID_14DAY_CUST_CANCELS));
	}


	public static TreeMap<String, String> SQL_FRM_6 = new TreeMap<String, String>();
	static {
		SQL_FRM_6.put(ID_EFC1_FULLFILL, SQL_MAP.get(ID_EFC1_FULLFILL));
		SQL_FRM_6.put(ID_EFC2_FULLFILL, SQL_MAP.get(ID_EFC2_FULLFILL));
		SQL_FRM_6.put(ID_EFC3_FULLFILL, SQL_MAP.get(ID_EFC3_FULLFILL));
		SQL_FRM_6.put(ID_EFC4_FULLFILL, SQL_MAP.get(ID_EFC4_FULLFILL));
	  /**3PL Changes starts**/
	  SQL_FRM_6.put(ID_LFC1_FULLFILL, SQL_MAP.get(ID_LFC1_FULLFILL));
    SQL_FRM_6.put(ID_LFC2_FULLFILL, SQL_MAP.get(ID_LFC2_FULLFILL));
    SQL_FRM_6.put(ID_LFC3_FULLFILL, SQL_MAP.get(ID_LFC3_FULLFILL));
    SQL_FRM_6.put(ID_LFC4_FULLFILL, SQL_MAP.get(ID_LFC4_FULLFILL));
    /**3PL Changes Ends**/
		SQL_FRM_6.put(ID_RDCTOTAL_FULLFILL, SQL_MAP.get(ID_RDCTOTAL_FULLFILL));
		//OCF changes Start
		SQL_FRM_6.put(ID_STORES_FULLFILL, SQL_MAP.get(ID_STORES_FULLFILL));
		//OCF changes End
	}
	public static TreeMap<String, String> SQL_FRM_VQ_6 = new TreeMap<String, String>();
	static {
		SQL_FRM_VQ_6.put(ID_EFC1_VQ_FULLFILL, SQL_MAP.get(ID_EFC1_VQ_FULLFILL));
		SQL_FRM_VQ_6.put(ID_EFC2_VQ_FULLFILL, SQL_MAP.get(ID_EFC2_VQ_FULLFILL));
		SQL_FRM_VQ_6.put(ID_EFC3_VQ_FULLFILL, SQL_MAP.get(ID_EFC3_VQ_FULLFILL));
		SQL_FRM_VQ_6.put(ID_EFC4_VQ_FULLFILL, SQL_MAP.get(ID_EFC4_VQ_FULLFILL));
	  /**3PL Changes starts**/
		SQL_FRM_VQ_6.put(ID_LFC1_FULLFILL, SQL_MAP.get(ID_LFC1_FULLFILL));
		SQL_FRM_VQ_6.put(ID_LFC2_FULLFILL, SQL_MAP.get(ID_LFC2_FULLFILL));
		SQL_FRM_VQ_6.put(ID_LFC3_FULLFILL, SQL_MAP.get(ID_LFC3_FULLFILL));
		SQL_FRM_VQ_6.put(ID_LFC4_FULLFILL, SQL_MAP.get(ID_LFC4_FULLFILL));
    /**3PL Changes Ends**/
		SQL_FRM_VQ_6.put(ID_RDCTOTAL_FULLFILL, SQL_MAP.get(ID_RDCTOTAL_FULLFILL));
		//OCF changes Start
		SQL_FRM_VQ_6.put(ID_STORES_FULLFILL, SQL_MAP.get(ID_STORES_FULLFILL));
		//SQL_FRM_VQ_6.put(ID_BOPUS_FULLFILL, SQL_MAP.get(ID_BOPUS_FULLFILL));
		//OCF changes End
	}

	public static TreeMap<String, String> SQL_FRM_VQ_PEND = new TreeMap<String, String>();
	static {
		SQL_FRM_VQ_PEND.put(ID_EFC1_VQ_PEND, SQL_MAP.get(ID_EFC1_VQ_PEND));
		SQL_FRM_VQ_PEND.put(ID_EFC2_VQ_PEND, SQL_MAP.get(ID_EFC2_VQ_PEND));
		SQL_FRM_VQ_PEND.put(ID_EFC3_VQ_PEND, SQL_MAP.get(ID_EFC3_VQ_PEND));
		SQL_FRM_VQ_PEND.put(ID_EFC4_VQ_PEND, SQL_MAP.get(ID_EFC4_VQ_PEND));
		SQL_FRM_VQ_PEND.put(XBOPUS_PEND, SQL_MAP.get(XBOPUS_PEND));
		SQL_FRM_VQ_PEND.put(ID_RDC_PEND, SQL_MAP.get(ID_RDC_PEND));
		SQL_FRM_VQ_PEND.put(STORE_PEND, SQL_MAP.get(STORE_PEND));

	}

	public static TreeMap<String, String> SQL_FRM_RDC_6 = new TreeMap<String, String>();
	static {
		SQL_FRM_RDC_6.put(ID_RDC865_FULLFILL, SQL_MAP.get(ID_RDC865_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC810_FULLFILL, SQL_MAP.get(ID_RDC810_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC840_FULLFILL, SQL_MAP.get(ID_RDC840_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC855_FULLFILL, SQL_MAP.get(ID_RDC855_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC830_FULLFILL, SQL_MAP.get(ID_RDC830_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC890_FULLFILL, SQL_MAP.get(ID_RDC890_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC885_FULLFILL, SQL_MAP.get(ID_RDC885_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC875_FULLFILL, SQL_MAP.get(ID_RDC875_FULLFILL));
		SQL_FRM_RDC_6.put(ID_RDC860_FULLFILL, SQL_MAP.get(ID_RDC860_FULLFILL));
	}

	public static TreeMap<String, String> SQL_FRM_3 = new TreeMap<String, String>();
	static {
		SQL_FRM_3.put(ID_DSV_INV_COUNT, SQL_MAP.get(ID_DSV_INV_COUNT));
		SQL_FRM_3.put(ID_EFC1_INV_COUNT, SQL_MAP.get(ID_EFC1_INV_COUNT));
		SQL_FRM_3.put(ID_EFC2_INV_COUNT, SQL_MAP.get(ID_EFC2_INV_COUNT));
		SQL_FRM_3.put(ID_EFC3_INV_COUNT, SQL_MAP.get(ID_EFC3_INV_COUNT));
		SQL_FRM_3.put(ID_EFC4_INV_COUNT, SQL_MAP.get(ID_EFC4_INV_COUNT));
		SQL_FRM_3.put(ID_RDCTOTAL_INV_COUNT, SQL_MAP.get(ID_RDCTOTAL_INV_COUNT));
		SQL_FRM_3.put(ID_NETWORK_INV_COUNT, SQL_MAP.get(ID_NETWORK_INV_COUNT));
	  /**3PL Changes starts**/
		SQL_FRM_3.put(ID_LFC1_INV_COUNT, SQL_MAP.get(ID_LFC1_INV_COUNT));
    SQL_FRM_3.put(ID_LFC2_INV_COUNT, SQL_MAP.get(ID_LFC2_INV_COUNT));
    SQL_FRM_3.put(ID_LFC3_INV_COUNT, SQL_MAP.get(ID_LFC3_INV_COUNT));
    SQL_FRM_3.put(ID_LFC4_INV_COUNT, SQL_MAP.get(ID_LFC4_INV_COUNT));
    /**3PL Changes Ends**/
		//OCF change start
		//SQL_FRM_3.put(ID_STORE_INV_COUNT, SQL_MAP.get(ID_STORE_INV_COUNT));
		//OCF change End
	}

	public static TreeMap<String, String> SQL_FRM_SERVER_STAT = new TreeMap<String, String>();
	static {
		SQL_FRM_SIM.put(ID_SERVER_STAT, SQL_MAP.get(ID_SERVER_STAT));
		SQL_FRM_SIM.put(ID_SERVER_STAT_GIV, SQL_MAP.get(ID_SERVER_STAT_GIV));
		SQL_FRM_SIM.put(ID_SERVER_STAT_OMSr, SQL_MAP.get(ID_SERVER_STAT_OMSr));
		SQL_FRM_SIM.put(ID_SERVER_STAT_OMSr_Training, SQL_MAP.get(ID_SERVER_STAT_OMSr_Training));
}

	public static TreeMap<String, String> SQL_FRM_RDC_3 = new TreeMap<String, String>();
	static {
		SQL_FRM_RDC_3
				.put(ID_RDC865_INV_COUNT, SQL_MAP.get(ID_RDC865_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC810_INV_COUNT, SQL_MAP.get(ID_RDC810_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC840_INV_COUNT, SQL_MAP.get(ID_RDC840_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC855_INV_COUNT, SQL_MAP.get(ID_RDC855_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC830_INV_COUNT, SQL_MAP.get(ID_RDC830_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC890_INV_COUNT, SQL_MAP.get(ID_RDC890_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC885_INV_COUNT, SQL_MAP.get(ID_RDC885_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC875_INV_COUNT, SQL_MAP.get(ID_RDC875_INV_COUNT));
		SQL_FRM_RDC_3
				.put(ID_RDC860_INV_COUNT, SQL_MAP.get(ID_RDC860_INV_COUNT));
	}

	public static TreeMap<String, String> SQL_FRM_4 = new TreeMap<String, String>();
	static {
		SQL_FRM_4.put(ID_EFC1_REDUNDANCY_COUNT,					SQL_MAP.get(ID_EFC1_REDUNDANCY_COUNT));
		SQL_FRM_4.put(ID_EFC2_REDUNDANCY_COUNT,					SQL_MAP.get(ID_EFC2_REDUNDANCY_COUNT));
		SQL_FRM_4.put(ID_EFC3_REDUNDANCY_COUNT,					SQL_MAP.get(ID_EFC3_REDUNDANCY_COUNT));
		SQL_FRM_4.put(ID_EFC4_REDUNDANCY_COUNT,					SQL_MAP.get(ID_EFC4_REDUNDANCY_COUNT));
	  /**3PL Changes starts**/
	  SQL_FRM_4.put(ID_LFC1_REDUNDANCY_COUNT,		                SQL_MAP.get(ID_LFC1_REDUNDANCY_COUNT));
	            SQL_FRM_4.put(ID_LFC2_REDUNDANCY_COUNT,		                SQL_MAP.get(ID_LFC2_REDUNDANCY_COUNT));
	            SQL_FRM_4.put(ID_LFC3_REDUNDANCY_COUNT,		                SQL_MAP.get(ID_LFC3_REDUNDANCY_COUNT));
	            SQL_FRM_4.put(ID_LFC4_REDUNDANCY_COUNT,		                SQL_MAP.get(ID_LFC4_REDUNDANCY_COUNT));
	  /**3PL Changes Ends**/
		SQL_FRM_4.put(ID_RDCTOTAL_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDCTOTAL_REDUNDANCY_COUNT));
		//OCF changes start
		//SQL_FRM_4.put(ID_STORES_REDUNDANCY_COUNT,				//	SQL_MAP.get(ID_STORES_REDUNDANCY_COUNT));
		//OCF changes End
	}

	public static TreeMap<String, String> SQL_FRM_RDC_4 = new TreeMap<String, String>();
	static {
		SQL_FRM_RDC_4.put(ID_RDC865_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC865_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC810_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC810_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC840_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC840_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC855_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC855_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC830_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC830_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC890_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC890_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC885_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC885_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC875_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC875_REDUNDANCY_COUNT));
		SQL_FRM_RDC_4.put(ID_RDC860_REDUNDANCY_COUNT,					SQL_MAP.get(ID_RDC860_REDUNDANCY_COUNT));
	}

	public static TreeMap<String, String> SQL_FRM_5 = new TreeMap<String, String>();
	static {
		SQL_FRM_5.put(ID_DAY1_FULLFILL, SQL_MAP.get(ID_DAY1_FULLFILL));
		SQL_FRM_5.put(ID_DAY2_FULLFILL, SQL_MAP.get(ID_DAY2_FULLFILL));
		SQL_FRM_5.put(ID_DAY3_FULLFILL, SQL_MAP.get(ID_DAY3_FULLFILL));
		SQL_FRM_5.put(ID_DAY4_FULLFILL, SQL_MAP.get(ID_DAY4_FULLFILL));
		SQL_FRM_5.put(ID_DAY5_FULLFILL, SQL_MAP.get(ID_DAY5_FULLFILL));
		SQL_FRM_5.put(ID_DAY6_FULLFILL, SQL_MAP.get(ID_DAY6_FULLFILL));
		SQL_FRM_5.put(ID_DAY7_FULLFILL, SQL_MAP.get(ID_DAY7_FULLFILL));
		// Including upto 14 days Fullfillment Performance
		SQL_FRM_5.put(ID_DAY8_FULLFILL, SQL_MAP.get(ID_DAY8_FULLFILL));
		SQL_FRM_5.put(ID_DAY9_FULLFILL, SQL_MAP.get(ID_DAY9_FULLFILL));
		SQL_FRM_5.put(ID_DAY10_FULLFILL, SQL_MAP.get(ID_DAY10_FULLFILL));
		SQL_FRM_5.put(ID_DAY11_FULLFILL, SQL_MAP.get(ID_DAY11_FULLFILL));
		SQL_FRM_5.put(ID_DAY12_FULLFILL, SQL_MAP.get(ID_DAY12_FULLFILL));
		SQL_FRM_5.put(ID_DAY13_FULLFILL, SQL_MAP.get(ID_DAY13_FULLFILL));
		SQL_FRM_5.put(ID_DAY14_FULLFILL, SQL_MAP.get(ID_DAY14_FULLFILL));
	}

	public static TreeMap<String, String> SQL_FRM_7 = new TreeMap<String, String>();
	static {
		SQL_FRM_7.put(ID_DAY1_FULLFILL_DSV, SQL_MAP.get(ID_DAY1_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY2_FULLFILL_DSV, SQL_MAP.get(ID_DAY2_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY3_FULLFILL_DSV, SQL_MAP.get(ID_DAY3_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY4_FULLFILL_DSV, SQL_MAP.get(ID_DAY4_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY5_FULLFILL_DSV, SQL_MAP.get(ID_DAY5_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY6_FULLFILL_DSV, SQL_MAP.get(ID_DAY6_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY7_FULLFILL_DSV, SQL_MAP.get(ID_DAY7_FULLFILL_DSV));
		// Including upto 14 days Fullfillment Performance
		SQL_FRM_7.put(ID_DAY8_FULLFILL_DSV, SQL_MAP.get(ID_DAY8_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY9_FULLFILL_DSV, SQL_MAP.get(ID_DAY9_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY10_FULLFILL_DSV, SQL_MAP.get(ID_DAY10_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY11_FULLFILL_DSV, SQL_MAP.get(ID_DAY11_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY12_FULLFILL_DSV, SQL_MAP.get(ID_DAY12_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY13_FULLFILL_DSV, SQL_MAP.get(ID_DAY13_FULLFILL_DSV));
		SQL_FRM_7.put(ID_DAY14_FULLFILL_DSV, SQL_MAP.get(ID_DAY14_FULLFILL_DSV));
	}

	public static TreeMap<String, String> SQL_FRM_8 = new TreeMap<String, String>();
	static {
		SQL_FRM_8.put(ID_DAY1_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY1_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY2_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY2_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY3_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY3_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY4_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY4_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY5_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY5_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY6_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY6_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY7_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY7_FULLFILL_NETWORK));
		// Including upto 14 days Fullfillment Performance
		SQL_FRM_8.put(ID_DAY8_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY8_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY9_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY9_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY10_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY10_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY11_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY11_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY12_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY12_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY13_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY13_FULLFILL_NETWORK));
		SQL_FRM_8.put(ID_DAY14_FULLFILL_NETWORK,					SQL_MAP.get(ID_DAY14_FULLFILL_NETWORK));
	}

	//WMOS Dashboard changes
	public static TreeMap<String, String> SQL_FRM_COLLATE_TIME = new TreeMap<String, String>();
	static {
		SQL_FRM_COLLATE_TIME.put(ID_COLLATE_TIME1, SQL_MAP.get(ID_COLLATE_TIME1));
		SQL_FRM_COLLATE_TIME.put(ID_COLLATE_TIME2, SQL_MAP.get(ID_COLLATE_TIME2));
		SQL_FRM_COLLATE_TIME.put(ID_COLLATE_TIME3, SQL_MAP.get(ID_COLLATE_TIME3));
		SQL_FRM_COLLATE_TIME.put(ID_COLLATE_TIME4, SQL_MAP.get(ID_COLLATE_TIME4));
	}

	public static TreeMap<String, String> SQL_FRM_WAVE_SUMMARY = new TreeMap<String, String>();
	static {
		SQL_FRM_WAVE_SUMMARY.put(ID_WAVE_SUMMARY1, SQL_MAP.get(ID_WAVE_SUMMARY1));
		SQL_FRM_WAVE_SUMMARY.put(ID_WAVE_SUMMARY2, SQL_MAP.get(ID_WAVE_SUMMARY2));
		SQL_FRM_WAVE_SUMMARY.put(ID_WAVE_SUMMARY3, SQL_MAP.get(ID_WAVE_SUMMARY3));
		SQL_FRM_WAVE_SUMMARY.put(ID_WAVE_SUMMARY4, SQL_MAP.get(ID_WAVE_SUMMARY4));
	}

	public static TreeMap<String, String> SQL_FRM_PURGE_STATS = new TreeMap<String, String>();
	static {
		SQL_FRM_PURGE_STATS.put(ID_PURGE_STATS1, SQL_MAP.get(ID_PURGE_STATS1));
		SQL_FRM_PURGE_STATS.put(ID_PURGE_STATS2, SQL_MAP.get(ID_PURGE_STATS2));
		SQL_FRM_PURGE_STATS.put(ID_PURGE_STATS3, SQL_MAP.get(ID_PURGE_STATS3));
	}

	public static TreeMap<String, String> SQL_FRM_14_DAY_SALES = new TreeMap<String, String>();
	static {
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS0, SQL_MAP.get(ID_SALES_STATS0));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS1, SQL_MAP.get(ID_SALES_STATS1));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS2, SQL_MAP.get(ID_SALES_STATS2));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS3, SQL_MAP.get(ID_SALES_STATS3));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS4, SQL_MAP.get(ID_SALES_STATS4));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS5, SQL_MAP.get(ID_SALES_STATS5));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS6, SQL_MAP.get(ID_SALES_STATS6));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS7, SQL_MAP.get(ID_SALES_STATS7));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS8, SQL_MAP.get(ID_SALES_STATS8));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS9, SQL_MAP.get(ID_SALES_STATS9));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS10, SQL_MAP.get(ID_SALES_STATS10));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS11, SQL_MAP.get(ID_SALES_STATS11));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS12, SQL_MAP.get(ID_SALES_STATS12));
		SQL_FRM_14_DAY_SALES.put(ID_SALES_STATS13, SQL_MAP.get(ID_SALES_STATS13));
	}

	//Start MarketPlace 2014 changes

	public static TreeMap<String, String> SQL_FRM_14_DAY_SALES_MP = new TreeMap<String, String>();
		static {SQL_FRM_14_DAY_SALES_MP.put(MP_14_DAY_SALES_SQL, SQL_MAP.get(MP_14_DAY_SALES_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_6_MONTH_SALES_MP = new TreeMap<String, String>();
		static{SQL_FRM_6_MONTH_SALES_MP.put(MP_6_MONTH_SALES_SQL, SQL_MAP.get(MP_6_MONTH_SALES_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_DAILY_DELINQ_MP = new TreeMap<String, String>();
		static{SQL_FRM_DAILY_DELINQ_MP.put(MP_DAILY_DELINQ_ORDER_SQL, SQL_MAP.get(MP_DAILY_DELINQ_ORDER_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_PO_CLOSED_YESTERDAY_MP = new TreeMap<String, String>();
		static{SQL_FRM_PO_CLOSED_YESTERDAY_MP.put(MP_PO_CLOSED_YESTERDAY_SQL, SQL_MAP.get(MP_PO_CLOSED_YESTERDAY_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_14_DAY_FULFILL_MP = new TreeMap<String, String>();
		static{SQL_FRM_14_DAY_FULFILL_MP.put(MP_14_DAY_FULFILL_SQL, SQL_MAP.get(MP_14_DAY_FULFILL_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_DAILY_FULFILL_MP = new TreeMap<String, String>();
		static{SQL_FRM_DAILY_FULFILL_MP.put(MP_DAILY_FULFILL_SQL, SQL_MAP.get(MP_DAILY_FULFILL_SQL));
		}

	public static TreeMap<String, String> SQL_FRM_PRODUCT_TREND_MP = new TreeMap<String, String>();
		static{SQL_FRM_PRODUCT_TREND_MP.put(MP_PRODUCT_TREND_SQL, SQL_MAP.get(MP_PRODUCT_TREND_SQL));
		}

		//End MarketPlace 2014 changes

    public static TreeMap<String, String> SQL_FRM_14_DAY_SALES_ALL = new TreeMap<String, String>();
    static {
        SQL_FRM_14_DAY_SALES_ALL.put(ID_SALES_STATS_ALL_14, SQL_MAP.get(ID_SALES_STATS_ALL_14));
    }

    public static TreeMap<String, String> SQL_FRM_14_DAY_SALES_BREAKDOWN = new TreeMap<String, String>();
    static {
        SQL_FRM_14_DAY_SALES_BREAKDOWN.put(ID_SALES_STATS_BREAKDOWN, SQL_MAP.get(ID_SALES_STATS_BREAKDOWN));
    }

	public static TreeMap<String, String> SQL_DESC = new TreeMap<String, String>();
	static {
		SQL_DESC.put(ID_SE_MYSQL_ROLES, "StoreElf User Roles");
		SQL_DESC.put(ID_SE_MYSQL_ROLE_PERMS, "StoreElf Role Permissions");
		
		SQL_DESC.put(ID_7_DAY_FNCL_OVRVW, "7 Day Financial Overview");
		SQL_DESC.put(ID_ODR_FRM, "Total Order Count Today");
		SQL_DESC.put(ID_UNITCT_FRM, "Total Order Unit Count Today");
		/*SQL_DESC.put(ID_PICK_FRM, "Average Number of Pick tickets per Order");
		SQL_DESC.put(ID_PICK_FRM_HR, "Average Number of Pick tickets per Hour");*/
		SQL_DESC.put(ID_PICK_FRM, "Avg Number of Pick tickets per Order");					 //shorten name to fit on single line
		SQL_DESC.put(ID_PICK_FRM_HR, "Avg Number of Pick tickets per Hour");
		SQL_DESC.put(ID_ODR_DOLLAR_FRM, "Total Demand Sales Today");
		SQL_DESC.put(ID_SETTLED_SALES_ECOM, "Total Settled Sales ECOM Today (COSA)");
		SQL_DESC.put(ID_SETTLED_SALES_STORE, "Total Settled Sales STORE Today (COSA)");
		SQL_DESC.put(ID_FUFILLMENT_SALES, "Total In Fufillment");

		//Added for cancel dashboard
		SQL_DESC.put(ID_CANCEL_COUNT, "Units cancelled today");
		SQL_DESC.put(ID_CNCL_DOLLAR_COUNT, "Dollars cancelled today");
		SQL_DESC.put(ID_BOPUS_EXPIRED_COUNT , "BOPUS expired pick-up units");
		SQL_DESC.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT , "BOPUS expired pick-up dollars");

		SQL_DESC.put(ID_CANCEL_COUNT_PREV, "Units cancelled yesterday");
		SQL_DESC.put(ID_CNCL_DOLLAR_COUNT_PREV, "Dollars cancelled yesterday");
		SQL_DESC.put(ID_BOPUS_EXPIRED_COUNT_PREV, "BOPUS expired pick-up units");
		SQL_DESC.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV, "BOPUS expired pick-up dollars");


		SQL_DESC.put(ID_EFC1_FULLFILL, "EFC 1-873");
		SQL_DESC.put(ID_EFC2_FULLFILL, "EFC 2-809");
		SQL_DESC.put(ID_EFC3_FULLFILL, "EFC 3-819");
		SQL_DESC.put(ID_EFC4_FULLFILL, "EFC 4-829");

		SQL_DESC.put(ID_EFC1_VQ_FULLFILL, "EFC 1-873");
		SQL_DESC.put(ID_EFC2_VQ_FULLFILL, "EFC 2-809");
		SQL_DESC.put(ID_EFC3_VQ_FULLFILL, "EFC 3-819");
		SQL_DESC.put(ID_EFC4_VQ_FULLFILL, "EFC 4-829");

		SQL_DESC.put(ID_EFC1_VQ_PEND, "EFC 1-873");
		SQL_DESC.put(ID_EFC2_VQ_PEND, "EFC 2-809");
		SQL_DESC.put(ID_EFC3_VQ_PEND, "EFC 3-819");
		SQL_DESC.put(ID_EFC4_VQ_PEND, "EFC 4-829");
		SQL_DESC.put(XBOPUS_PEND, "BOPUS");
		SQL_DESC.put(STORE_PEND, "STORES");

	  /**3PL Changes starts**/
    SQL_DESC.put(ID_LFC1_FULLFILL, "LFC 1-869");
    SQL_DESC.put(ID_LFC2_FULLFILL, "LFC 2-879");
    SQL_DESC.put(ID_LFC3_FULLFILL, "LFC 3-859");
    SQL_DESC.put(ID_LFC4_FULLFILL, "LFC 4-889");
    /**3PL Changes Ends**/
		SQL_DESC.put(ID_RDC865_FULLFILL, "RDC-865");
		SQL_DESC.put(ID_RDC810_FULLFILL, "RDC-810");
		SQL_DESC.put(ID_RDC840_FULLFILL, "RDC-840");
		SQL_DESC.put(ID_RDC855_FULLFILL, "RDC-855");
		SQL_DESC.put(ID_RDC830_FULLFILL, "RDC-830");
		SQL_DESC.put(ID_RDC890_FULLFILL, "RDC-890");
		SQL_DESC.put(ID_RDC885_FULLFILL, "RDC-885");
		SQL_DESC.put(ID_RDC875_FULLFILL, "RDC-875");
		SQL_DESC.put(ID_RDC860_FULLFILL, "RDC-860");
		SQL_DESC.put(ID_RDCTOTAL_FULLFILL, "RDC");
		SQL_DESC.put(ID_RDC_PEND, "RDC");

		//OCF changes Start
		SQL_DESC.put(ID_STORES_FULLFILL, "STORES");
		SQL_DESC.put(ID_BOPUS_FULLFILL, "BOPUS");
		SQL_DESC.put(ID_STORE_INV_COUNT, "STORES");
		SQL_DESC.put(ID_STORES_REDUNDANCY_COUNT, "STORES");
		//OCF Changes End

		SQL_DESC.put(ID_DSV_INV_COUNT, "DSV");
		SQL_DESC.put(ID_EFC1_INV_COUNT, "EFC 1-873");
		SQL_DESC.put(ID_EFC2_INV_COUNT, "EFC 2-809");
		SQL_DESC.put(ID_EFC3_INV_COUNT, "EFC 3-819");
		SQL_DESC.put(ID_EFC4_INV_COUNT, "EFC 4-829");
	  /**3PL Changes starts**/
    SQL_DESC.put(ID_LFC1_INV_COUNT, "LFC 1-869");
    SQL_DESC.put(ID_LFC2_INV_COUNT, "LFC 2-879");
    SQL_DESC.put(ID_LFC3_INV_COUNT, "LFC 3-859");
    SQL_DESC.put(ID_LFC4_INV_COUNT, "LFC 4-889");
    /**3PL Changes Ends**/
		SQL_DESC.put(ID_RDC865_INV_COUNT, "RDC-865");
		SQL_DESC.put(ID_RDC810_INV_COUNT, "RDC-810");
		SQL_DESC.put(ID_RDC840_INV_COUNT, "RDC-840");
		SQL_DESC.put(ID_RDC855_INV_COUNT, "RDC-855");
		SQL_DESC.put(ID_RDC830_INV_COUNT, "RDC-830");
		SQL_DESC.put(ID_RDC890_INV_COUNT, "RDC-890");
		SQL_DESC.put(ID_RDC885_INV_COUNT, "RDC-885");
		SQL_DESC.put(ID_RDC875_INV_COUNT, "RDC-875");
		SQL_DESC.put(ID_RDC860_INV_COUNT, "RDC-860");
		SQL_DESC.put(ID_RDCTOTAL_INV_COUNT, "RDC");
		SQL_DESC.put(ID_NETWORK_INV_COUNT, "<b>Total Network</b>");

		SQL_DESC.put(ID_EFC1_REDUNDANCY_COUNT, "EFC 1-873");
		SQL_DESC.put(ID_EFC2_REDUNDANCY_COUNT, "EFC 2-809");
		SQL_DESC.put(ID_EFC3_REDUNDANCY_COUNT, "EFC 3-819");
		SQL_DESC.put(ID_EFC4_REDUNDANCY_COUNT, "EFC 4-829");
	  /**3PL Changes starts**/
		SQL_DESC.put(ID_LFC1_REDUNDANCY_COUNT, "LFC 1-869");
    SQL_DESC.put(ID_LFC2_REDUNDANCY_COUNT, "LFC 2-879");
    SQL_DESC.put(ID_LFC3_REDUNDANCY_COUNT, "LFC 3-859");
    SQL_DESC.put(ID_LFC4_REDUNDANCY_COUNT, "LFC 4-889");
    /**3PL Changes Ends**/
		SQL_DESC.put(ID_RDC865_REDUNDANCY_COUNT, "RDC-865");
		SQL_DESC.put(ID_RDC810_REDUNDANCY_COUNT, "RDC-810");
		SQL_DESC.put(ID_RDC840_REDUNDANCY_COUNT, "RDC-840");
		SQL_DESC.put(ID_RDC855_REDUNDANCY_COUNT, "RDC-855");
		SQL_DESC.put(ID_RDC830_REDUNDANCY_COUNT, "RDC-830");
		SQL_DESC.put(ID_RDC890_REDUNDANCY_COUNT, "RDC-890");
		SQL_DESC.put(ID_RDC885_REDUNDANCY_COUNT, "RDC-885");
		SQL_DESC.put(ID_RDC875_REDUNDANCY_COUNT, "RDC-875");
		SQL_DESC.put(ID_RDC860_REDUNDANCY_COUNT, "RDC-860");
		SQL_DESC.put(ID_RDCTOTAL_REDUNDANCY_COUNT, "RDC");

		SQL_DESC.put(ID_SIM_REPORT, "SIM/RED");
		SQL_DESC.put(ID_ORDER_EXCEPTION_REPORT, "RDC Order Exception Report");
		
		SQL_DESC.put(ID_COLLATE_TIME1, "Collate Print Time (Seconds)");
		SQL_DESC.put(ID_COLLATE_TIME2, "Collate Print Time (Seconds)");
		SQL_DESC.put(ID_COLLATE_TIME3, "Collate Print Time (Seconds)");
		SQL_DESC.put(ID_COLLATE_TIME4, "Collate Print Time (Seconds)");

		SQL_DESC.put(ID_WAVE_SUMMARY1, "Wave Summary and Details");
		SQL_DESC.put(ID_WAVE_SUMMARY2, "Wave Summary and Details");
		SQL_DESC.put(ID_WAVE_SUMMARY3, "Wave Summary and Details");
		SQL_DESC.put(ID_WAVE_SUMMARY4, "Wave Summary and Details");

		SQL_DESC.put(ID_PURGE_STATS1, "Purge Statistics");
		SQL_DESC.put(ID_PURGE_STATS2, "Purge Statistics");
		SQL_DESC.put(ID_PURGE_STATS3, "Purge Statistics");

		//Start MarketPlace 2014 Changes
		SQL_DESC.put(MP_14_DAY_SALES_SQL, "14 Day Sales Stats");
		SQL_DESC.put(MP_6_MONTH_SALES_SQL, "6 Month Sales Stats");
		SQL_DESC.put(MP_DAILY_DELINQ_ORDER_SQL, "Daily Delinquent Order Stats");
		SQL_DESC.put(MP_PO_CLOSED_YESTERDAY_SQL, "PO's Closed Yesterday Stats");
		SQL_DESC.put(MP_14_DAY_FULFILL_SQL, "14 Day Fulfillment Stats");
		SQL_DESC.put(MP_DAILY_FULFILL_SQL, "Daily Fulfillment Stats");
		SQL_DESC.put(MP_PRODUCT_TREND_SQL, "Product Trend Stats");
		//End MarketPlace 2014 Changes
	}

	public static TreeMap<String, String> SQL_INST = new TreeMap<String, String>();
	static {
		
		SQL_INST.put(ID_SE_MYSQL_ROLES, STOREELF_RO);
		SQL_INST.put(ID_SE_MYSQL_ROLE_PERMS, STOREELF_RO);

		SQL_INST.put(ID_7_DAY_FNCL_OVRVW,OMS);
		SQL_INST.put(ID_ODR_FRM, OMS);
		SQL_INST.put(ID_UNITCT_FRM, OMS);
		SQL_INST.put(ID_PICK_FRM, OMS);
		SQL_INST.put(ID_PICK_FRM_HR, OMS);
		SQL_INST.put(ID_ODR_DOLLAR_FRM, OMS);
		SQL_INST.put(ID_SETTLED_SALES_ECOM, OMS);
		SQL_INST.put(ID_SETTLED_SALES_STORE, OMS);
		SQL_INST.put(ID_FUFILLMENT_SALES, OMS);

		//Added for cancel dashboard
		SQL_INST.put(ID_CANCEL_COUNT,OMS);
		SQL_INST.put(ID_CNCL_BREAKDOWN, OMS);
		SQL_INST.put(ID_CNCL_DOLLAR_COUNT, OMS);
		SQL_INST.put(ID_CNCL_SPARK, OMS);
		SQL_INST.put(ID_BOPUS_EXPIRED_COUNT, OMS);
		SQL_INST.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT, OMS);

		SQL_INST.put(ID_CANCEL_COUNT_PREV,OMS);
		SQL_INST.put(ID_CNCL_PREV_BREAKDOWN, OMS);
		SQL_INST.put(ID_CNCL_DOLLAR_COUNT_PREV, OMS);
		SQL_INST.put(ID_CNCL_SPARK_PREV, OMS);
		SQL_INST.put(ID_BOPUS_EXPIRED_COUNT_PREV, OMS);
		SQL_INST.put(ID_BOPUS_EXPIRED_DOLLAR_COUNT_PREV, OMS);

		//Added for cancel dashboard-----Top 10 SKU cancelled
		SQL_INST.put(ID_ITEM1_COUNT, OMS);
		/* SQL_INST.put(ID_ITEM2_COUNT, OMS);
		SQL_INST.put(ID_ITEM3_COUNT, OMS);
		SQL_INST.put(ID_ITEM4_COUNT, OMS);
		SQL_INST.put(ID_ITEM5_COUNT, OMS);
		SQL_INST.put(ID_ITEM6_COUNT, OMS);
		SQL_INST.put(ID_ITEM7_COUNT, OMS);
		SQL_INST.put(ID_ITEM8_COUNT, OMS);
		SQL_INST.put(ID_ITEM9_COUNT, OMS);
		SQL_INST.put(ID_ITEM10_COUNT, OMS); */

		SQL_INST.put(ID_ITEM1_COUNT_PREV, OMS);
	  /*SQL_INST.put(ID_ITEM2_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM3_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM4_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM5_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM6_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM7_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM8_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM9_COUNT_PREV, OMS);
		SQL_INST.put(ID_ITEM10_COUNT_PREV, OMS); */

		//added for cancel dashboard------14 day cancellation stats
		SQL_INST.put(ID_DAY01_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY02_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY03_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY04_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY05_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY06_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY07_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY08_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY09_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY10_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY11_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY12_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY13_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY14_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY1_FULLFILL, OMS);
		SQL_INST.put(ID_DAY2_FULLFILL, OMS);
		SQL_INST.put(ID_DAY3_FULLFILL, OMS);
		SQL_INST.put(ID_DAY4_FULLFILL, OMS);
		SQL_INST.put(ID_DAY5_FULLFILL, OMS);
		SQL_INST.put(ID_DAY6_FULLFILL, OMS);
		SQL_INST.put(ID_DAY7_FULLFILL, OMS);

		//added for auto cancel section
		SQL_INST.put(ID_DAY01_AUTO_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY02_AUTO_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY03_AUTO_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY04_AUTO_CNCL_STAT, OMS);
		SQL_INST.put(ID_DAY05_AUTO_CNCL_STAT, OMS);
		
		SQL_INST.put(ID_14DAY_CUST_CANCELS, OMS);

		SQL_INST.put(ID_DAY1_FULLFILL, OMS);
		SQL_INST.put(ID_DAY2_FULLFILL, OMS);
		SQL_INST.put(ID_DAY3_FULLFILL, OMS);
		SQL_INST.put(ID_DAY4_FULLFILL, OMS);
		SQL_INST.put(ID_DAY5_FULLFILL, OMS);
		SQL_INST.put(ID_DAY6_FULLFILL, OMS);
		SQL_INST.put(ID_DAY7_FULLFILL, OMS);
		// Including upto 14 days Fullfillment Performance
		SQL_INST.put(ID_DAY8_FULLFILL, OMS);
		SQL_INST.put(ID_DAY9_FULLFILL, OMS);
		SQL_INST.put(ID_DAY10_FULLFILL, OMS);
		SQL_INST.put(ID_DAY11_FULLFILL, OMS);
		SQL_INST.put(ID_DAY12_FULLFILL, OMS);
		SQL_INST.put(ID_DAY13_FULLFILL, OMS);
		SQL_INST.put(ID_DAY14_FULLFILL, OMS);

		SQL_INST.put(ID_DAY1_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY2_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY3_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY4_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY5_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY6_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY7_FULLFILL_DSV, OMS);
		// Including upto 14 days Fullfillment Performance
		SQL_INST.put(ID_DAY8_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY9_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY10_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY11_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY12_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY13_FULLFILL_DSV, OMS);
		SQL_INST.put(ID_DAY14_FULLFILL_DSV, OMS);

		SQL_INST.put(ID_DAY1_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY2_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY3_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY4_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY5_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY6_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY7_FULLFILL_NETWORK, OMS);
		// Including upto 14 days Fullfillment Performance
		SQL_INST.put(ID_DAY8_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY9_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY10_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY11_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY12_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY13_FULLFILL_NETWORK, OMS);
		SQL_INST.put(ID_DAY14_FULLFILL_NETWORK, OMS);

		//14 Day Sales Stats
		SQL_INST.put(ID_SALES_STATS0, OMS);
		SQL_INST.put(ID_SALES_STATS1, OMS);
		SQL_INST.put(ID_SALES_STATS2, OMS);
		SQL_INST.put(ID_SALES_STATS3, OMS);
		SQL_INST.put(ID_SALES_STATS4, OMS);
		SQL_INST.put(ID_SALES_STATS5, OMS);
		SQL_INST.put(ID_SALES_STATS6, OMS);
		SQL_INST.put(ID_SALES_STATS7, OMS);
		SQL_INST.put(ID_SALES_STATS8, OMS);
		SQL_INST.put(ID_SALES_STATS9, OMS);
		SQL_INST.put(ID_SALES_STATS10, OMS);
		SQL_INST.put(ID_SALES_STATS11, OMS);
		SQL_INST.put(ID_SALES_STATS12, OMS);
		SQL_INST.put(ID_SALES_STATS13, OMS);
        SQL_INST.put(ID_SALES_STATS_ALL_14, OMS);
        SQL_INST.put(ID_SALES_STATS_BREAKDOWN, OMS);

		SQL_INST.put(ID_EFC1_FULLFILL, EFC1);
		SQL_INST.put(ID_EFC2_FULLFILL, EFC2);
		SQL_INST.put(ID_EFC3_FULLFILL, EFC3);
		SQL_INST.put(ID_EFC4_FULLFILL, EFC4);

		SQL_INST.put(ID_EFC1_VQ_FULLFILL, EFC1);
		SQL_INST.put(ID_EFC2_VQ_FULLFILL, EFC2);
		SQL_INST.put(ID_EFC3_VQ_FULLFILL, EFC3);
		SQL_INST.put(ID_EFC4_VQ_FULLFILL, EFC4);

		SQL_INST.put(ID_EFC1_VQ_PEND, EFC1);
		SQL_INST.put(ID_EFC2_VQ_PEND, EFC2);
		SQL_INST.put(ID_EFC3_VQ_PEND, EFC3);
		SQL_INST.put(ID_EFC4_VQ_PEND, EFC4);
		SQL_INST.put(XBOPUS_PEND, OMS);
		SQL_INST.put(STORE_PEND, OMS);

	  /**3PL Changes starts**/
		SQL_INST.put(ID_LFC1_FULLFILL, OMS);
    SQL_INST.put(ID_LFC2_FULLFILL, OMS);
    SQL_INST.put(ID_LFC3_FULLFILL, OMS);
    SQL_INST.put(ID_LFC4_FULLFILL, OMS);
    /**3PL Changes Ends**/
		SQL_INST.put(ID_RDC865_FULLFILL, OMS);
		SQL_INST.put(ID_RDC810_FULLFILL, OMS);
		SQL_INST.put(ID_RDC840_FULLFILL, OMS);
		SQL_INST.put(ID_RDC855_FULLFILL, OMS);
		SQL_INST.put(ID_RDC830_FULLFILL, OMS);
		SQL_INST.put(ID_RDC890_FULLFILL, OMS);
		SQL_INST.put(ID_RDC885_FULLFILL, OMS);
		SQL_INST.put(ID_RDC875_FULLFILL, OMS);
		SQL_INST.put(ID_RDC860_FULLFILL, OMS);
		SQL_INST.put(ID_RDCTOTAL_FULLFILL, OMS);

		//OCF changes start
		SQL_INST.put(ID_STORE_INV_COUNT, GIV);
		SQL_INST.put(ID_STORES_FULLFILL, OMS);
		SQL_INST.put(ID_BOPUS_FULLFILL, OMS);
		SQL_INST.put(ID_STORES_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_STORE_LABEL_SQL, OMS);
		SQL_INST.put(ID_STORE_REGION_FULFILL_SQL, OMS);
		SQL_INST.put(ID_STORE_STATES_FULFILL_SQL, OMS);
		SQL_INST.put(ID_STORE_REGION_INV_SQL, GIV);
		SQL_INST.put(ID_STORE_STATES_INV_SQL, GIV);
		SQL_INST.put(ID_STORE_REGION_REDUNDANCY_SQL, GIV);
		SQL_INST.put(ID_STORE_STATES_REDUNDANCY_SQL, GIV);
		SQL_INST.put(ID_STORE_FULFILL_SQL, OMS);
		SQL_INST.put(ID_STORE_INV_SQL, GIV);
		SQL_INST.put(ID_STORE_REDUNDANCY_SQL, GIV);
		//OCF changes End

		SQL_INST.put(ID_DSV_INV_COUNT, GIV);
		SQL_INST.put(ID_EFC1_INV_COUNT, GIV);
		SQL_INST.put(ID_EFC2_INV_COUNT, GIV);
		SQL_INST.put(ID_EFC3_INV_COUNT, GIV);
		SQL_INST.put(ID_EFC4_INV_COUNT, GIV);
	  /**3PL Changes starts**/
    SQL_INST.put(ID_LFC1_INV_COUNT, GIV);
    SQL_INST.put(ID_LFC2_INV_COUNT, GIV);
    SQL_INST.put(ID_LFC3_INV_COUNT, GIV);
    SQL_INST.put(ID_LFC4_INV_COUNT, GIV);
    /**3PL Changes Ends**/

		SQL_INST.put(ID_RDC865_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC810_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC840_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC855_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC830_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC890_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC885_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC875_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC860_INV_COUNT, GIV);
		SQL_INST.put(ID_RDCTOTAL_INV_COUNT, GIV);
		SQL_INST.put(ID_RDC_PEND, OMS);
		SQL_INST.put(ID_NETWORK_INV_COUNT, GIV);

		SQL_INST.put(ID_SERVER_STAT, OMS);
		SQL_INST.put(ID_SERVER_STAT_GIV, GIV);
		SQL_INST.put(ID_EFC1_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_EFC2_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_EFC3_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_EFC4_REDUNDANCY_COUNT, GIV);

	  /**3PL Changes starts**/
	  SQL_INST.put(ID_LFC1_REDUNDANCY_COUNT, GIV);
    SQL_INST.put(ID_LFC2_REDUNDANCY_COUNT, GIV);
    SQL_INST.put(ID_LFC3_REDUNDANCY_COUNT, GIV);
    SQL_INST.put(ID_LFC4_REDUNDANCY_COUNT, GIV);
    /**3PL Changes Ends**/
		SQL_INST.put(ID_RDC865_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC810_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC840_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC855_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC830_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC890_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC885_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC875_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDC860_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_RDCTOTAL_REDUNDANCY_COUNT, GIV);
		SQL_INST.put(ID_SIM_REPORT, GIV);
		SQL_INST.put(ID_ORDER_EXCEPTION_REPORT, OMS);
		//changes for google pie chart in heat map tab
		SQL_INST.put(ID_HEAT_MAP_EFC_SQL, OMS);
		SQL_INST.put(ID_HEAT_MAP_STORES_SQL, OMS);
		SQL_INST.put(ID_HEAT_MAP_OVERALL_SQL, OMS);

		//Changes for WMOS Dashboard.
		SQL_INST.put(ID_COLLATE_TIME1, EFC1_PRIM);
		SQL_INST.put(ID_COLLATE_TIME2, EFC2_PRIM);
		SQL_INST.put(ID_COLLATE_TIME3, EFC3_PRIM);
		SQL_INST.put(ID_COLLATE_TIME4, EFC4_PRIM);

		SQL_INST.put(ID_WAVE_SUMMARY1, EFC1_PRIM);
		SQL_INST.put(ID_WAVE_SUMMARY2, EFC2_PRIM);
		SQL_INST.put(ID_WAVE_SUMMARY3, EFC3_PRIM);
		SQL_INST.put(ID_WAVE_SUMMARY4, EFC4_PRIM);

		SQL_INST.put(ID_PURGE_STATS1, EFC1);
		SQL_INST.put(ID_PURGE_STATS2, EFC2);
		SQL_INST.put(ID_PURGE_STATS3, EFC3);

		//Start MarketPlace 2014 Changes
		SQL_INST.put(MP_14_DAY_SALES_SQL, OMS);
		SQL_INST.put(MP_6_MONTH_SALES_SQL, OMS);
		SQL_INST.put(MP_DAILY_DELINQ_ORDER_SQL, OMS);
		SQL_INST.put(MP_PO_CLOSED_YESTERDAY_SQL, OMS);
		SQL_INST.put(MP_14_DAY_FULFILL_SQL, OMS);
		SQL_INST.put(MP_DAILY_FULFILL_SQL, OMS);
		SQL_INST.put(MP_PRODUCT_TREND_SQL, OMS);
		//End MarketPlace 2014 Changes

	}

	/** UTILITIES SECTION **/
	/** MAPS and VARIABLES for creating the SCREENS for Utilities **/

	/* Section to add the form IDs Common to both Search and Result forms */
	public static String UTIL_FRM_ITEM = "item";
	public static String UTIL_FRM_ORDER = "order";
	public static String UTIL_FRM_ORDERSTATUS = "orderstatus";
	public static String UTIL_FRM_ORDERRELEASE = "pickticket";
	public static String UTIL_FRM_INV = "inventory";
	public static String UTIL_FRM_EFC = "inventory_efc";
	public static String UTIL_FRM_INV_AUDIT = "invaudit";
	public static String UTIL_FRM_SHIP = "shipment";
	public static String UTIL_FRM_FS_INV = "fashioninv";
	public static String UTIL_FRM_PICKTICKETS_DETAIL = "pickticket_detail";

	public static String FLD_TYPE_TEXT = "TEXT";
	public static String FLD_TYPE_DATE = "DATE";
	public static String FLD_TYPE_TEXTAREA = "TEXTAREA";
	// Added for Warehouse Transfer StoreElf Utility Screen
	public static String UTIL_FRM_TRANSFER = "transferorderdetail";
	public static String UTIL_FRM_TRANSFER_ORDER = "transferorderlinedetail";
	public static String UTIL_STORE_CAPACITY = "storecapacity";			// Added for Store Unit Capacity StoreElf Screen

	//Added for WMOS utilities section
	public static String UTIL_FRM_SHIPVIA  = "shipvia";

	/** MAPS and VARIABLES for creating the SEARCH SCREENS for Utilities **/

	/* Section to add the descriptions for the Search form IDs */
	public static TreeMap<String, String> SRCH_FORM_DESC = new TreeMap<String, String>();
	static {
		SRCH_FORM_DESC.put(UTIL_FRM_ITEM, "Search by Item");
		SRCH_FORM_DESC.put(UTIL_FRM_ORDER, "Search by Order");
		SRCH_FORM_DESC.put(UTIL_FRM_ORDERSTATUS, "Search by Order");
		SRCH_FORM_DESC.put(UTIL_FRM_ORDERRELEASE,					"Search For Order Release Details");
		SRCH_FORM_DESC.put(UTIL_FRM_INV, "Search Inventory by Item");
		SRCH_FORM_DESC.put(UTIL_FRM_INV_AUDIT, "Search Inventory Audit");
		SRCH_FORM_DESC.put(UTIL_FRM_SHIPVIA, "Search Shipment Details");
		SRCH_FORM_DESC.put(UTIL_FRM_PICKTICKETS_DETAIL,					"Search by Pickticket number");
		SRCH_FORM_DESC.put(UTIL_FRM_SHIPVIA, "Search Ship Via's");
		SRCH_FORM_DESC.put(UTIL_FRM_PKTDTL, "Pickticket Detail");
	}

	/* Section to create field maps for each of the forms */
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_ITEM = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_ORDER = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_ORDERSTATUS = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_ORDERRELEASE = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_INV = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_INV_AUDIT = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_SHIP = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_PICKTICKETNUMBER = new TreeMap<String, SearchFieldModal>();
	// Added for Warehouse Transfer StoreElf Utility Screen
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_TRANSFER = new TreeMap<String, SearchFieldModal>();
	// Added for Store Unit Capacity StoreElf Screen
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_STORE_CAPACITY = new TreeMap<String, SearchFieldModal>();
	// Added for WMOS utility screen
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_SHIPVIA = new TreeMap<String, SearchFieldModal>();
	public static TreeMap<String, SearchFieldModal> SRCH_FORM_PKTDTL = new TreeMap<String, SearchFieldModal>();
	static {
		try {
			SRCH_FORM_ITEM.put("ItemID", new SearchFieldModalImpl("ItemID",						"Item ID", true, FLD_TYPE_TEXTAREA));
			SRCH_FORM_ORDER.put("OrderNo", new SearchFieldModalImpl("OrderNo",						"Order No", true, FLD_TYPE_TEXT));
			SRCH_FORM_ORDERSTATUS.put("OrderNo", new SearchFieldModalImpl(
					"OrderNo", "Order-Max 500", true, FLD_TYPE_TEXTAREA));
			SRCH_FORM_ORDERRELEASE.put("OrderNo", new SearchFieldModalImpl(
					"OrderNo", "Order No", true, FLD_TYPE_TEXT));
			SRCH_FORM_INV.put("ItemID", new SearchFieldModalImpl("ItemID",						"Item ID", true, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("ItemID", new SearchFieldModalImpl(
					"ItemID", "Item ID", true, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("ShipNode", new SearchFieldModalImpl(
					"ShipNode", "Ship Node", true, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("ItemID", new SearchFieldModalImpl(
					"ItemID", "Item ID", true, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("DateFrom", new SearchFieldModalImpl(
					"DateFrom", "From Date", true, FLD_TYPE_DATE));
			SRCH_FORM_INV_AUDIT.put("DateTo", new SearchFieldModalImpl(
					"DateTo", "To Date", true, FLD_TYPE_DATE));
			SRCH_FORM_INV_AUDIT.put("ShipNode", new SearchFieldModalImpl(
					"ShipNode", "Ship Node", false, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("TranNo", new SearchFieldModalImpl(
					"TranNo", "PIX Tran No", false, FLD_TYPE_TEXT));
			SRCH_FORM_INV_AUDIT.put("TranType", new SearchFieldModalImpl(
					"TranType", "PIX Tran Type", false, FLD_TYPE_TEXT));
			SRCH_FORM_SHIP.put("OrderNo", new SearchFieldModalImpl("OrderNo",						"Order No", false, FLD_TYPE_TEXT));
			SRCH_FORM_SHIP.put("TrackingNo", new SearchFieldModalImpl(
					"TrackingNo", "Tracking No", false, FLD_TYPE_TEXT));
			SRCH_FORM_SHIP.put("CartonNo", new SearchFieldModalImpl("CartonNo",						"Carton No", false, FLD_TYPE_TEXT));
			SRCH_FORM_SHIP.put("ShipmentNo", new SearchFieldModalImpl("ShipmentNo",				"Shipment No", false, FLD_TYPE_TEXT));
			SRCH_FORM_PICKTICKETNUMBER.put("PickticketNo",						new SearchFieldModalImpl("PickticketNo", "Pickticket No",								true, FLD_TYPE_TEXT));
			// Changes for Warehouse Transfer StoreElf Utility Screen - START
			SRCH_FORM_TRANSFER.put("Order No", new SearchFieldModalImpl("Order No",						"Order No", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Order Status", new SearchFieldModalImpl("Order Status",						"Order Status", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Shipment No", new SearchFieldModalImpl("Shipment No",						"Shipment No", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Item ID", new SearchFieldModalImpl("Item ID",						"Item ID", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Source Node", new SearchFieldModalImpl("Source Node",						"Source Node", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Receiving Node", new SearchFieldModalImpl("Receiving Node",						"Receiving Node", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("Transfer Type", new SearchFieldModalImpl("Transfer Type",						"Transfer Type", false, FLD_TYPE_TEXT));
			SRCH_FORM_TRANSFER.put("FromOrderDate", new SearchFieldModalImpl("FromOrderDate",						"Order Create Date", false, FLD_TYPE_DATE));
			SRCH_FORM_TRANSFER.put("ToOrderDate", new SearchFieldModalImpl("ToOrderDate",						"To", false, FLD_TYPE_DATE));
			SRCH_FORM_TRANSFER.put("FromShipmentDate", new SearchFieldModalImpl("FromShipmentDate",						"From Shipment Date", false, FLD_TYPE_DATE));
			SRCH_FORM_TRANSFER.put("ToShipmentDate", new SearchFieldModalImpl("ToShipmentDate",						"To Shipment Date", false, FLD_TYPE_DATE));
			// Changes for Warehouse Transfer StoreElf Utility Screen - END

			//Changes for WMS utility screen - BEGIN
			SRCH_FORM_SHIPVIA.put("ship_via_code", new SearchFieldModalImpl("Ship Via Code",						"Ship Via Code", true, FLD_TYPE_TEXT));
			SRCH_FORM_SHIPVIA.put("ship_via_desc", new SearchFieldModalImpl("Ship Via Description",						"Ship Via Description", false, FLD_TYPE_TEXT));

			SRCH_FORM_PKTDTL.put("pkt_cntrl_nbr", new SearchFieldModalImpl("Pickticket Control Number",					"Pickticket No", true, FLD_TYPE_TEXT));
			SRCH_FORM_PKTDTL.put("efc_no", new SearchFieldModalImpl("efc number",					"EFC No", true, FLD_TYPE_TEXT));
			// Added for Store Unit Capacity StoreElf Screen
			SRCH_FORM_STORE_CAPACITY.put("StoreNo", new SearchFieldModalImpl("Store No",						"Store No", false, FLD_TYPE_TEXT));
			//Changes for WMS utility screen - END
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Section to add the field maps to the central map for each form ID */
	public static TreeMap<String, TreeMap<String, SearchFieldModal>> FRM_FIELD_MAP = new TreeMap<String, TreeMap<String, SearchFieldModal>>();
	static {
		FRM_FIELD_MAP.put(UTIL_FRM_SHIPVIA, SRCH_FORM_SHIPVIA); //added for WMOS Utility section
		FRM_FIELD_MAP.put(UTIL_FRM_PKTDTL, SRCH_FORM_PKTDTL); //added for WMOS Utility section
		FRM_FIELD_MAP.put(UTIL_FRM_TRANSFER, SRCH_FORM_TRANSFER); // Added for Warehouse Transfer StoreElf Utility Screen
		FRM_FIELD_MAP.put(UTIL_STORE_CAPACITY, SRCH_FORM_STORE_CAPACITY); // Added for Store Unit Capacity StoreElf Screen
		FRM_FIELD_MAP.put(UTIL_FRM_ITEM, SRCH_FORM_ITEM);
		FRM_FIELD_MAP.put(UTIL_FRM_ORDER, SRCH_FORM_ORDER);
		FRM_FIELD_MAP.put(UTIL_FRM_ORDERSTATUS, SRCH_FORM_ORDERSTATUS);
		FRM_FIELD_MAP.put(UTIL_FRM_ORDERRELEASE, SRCH_FORM_ORDERRELEASE);
		FRM_FIELD_MAP.put(UTIL_FRM_INV, SRCH_FORM_INV);
		FRM_FIELD_MAP.put(UTIL_FRM_INV_AUDIT, SRCH_FORM_INV_AUDIT);
		FRM_FIELD_MAP.put(UTIL_FRM_SHIP, SRCH_FORM_SHIP);
		FRM_FIELD_MAP.put(UTIL_FRM_PICKTICKETS_DETAIL,					SRCH_FORM_PICKTICKETNUMBER);
	}

	/** MAPS and VARIABLES for the RESULT FORM for Utilities **/

	public static final String COL_ITEM_ID = "item_id";
	public static final String COL_ACTIVE = "active";
	public static final String COL_CASE = "case";
	public static final String COL_UNALLOC_CASE = "unalloc_case";
	public static final String COL_TRANS = "trans";
	public static final String COL_UNALLOC_TRANS = "unalloc_trans";
	public static final String COL_ALLOC_TOTAL = "alloc_total";
	public static final String COL_UNALLOC_TOTAL = "unalloc_total";
	public static final String COL_CARTON = "carton";
	public static final String COL_ITEM = "item";
	public static final String COL_ITEM_TYPE = "item_type";
	public static final String COL_PRODUCT_LINE = "product_line";
	public static final String COL_DIRECT_SHIP = "extn_direct_ship_item";
	public static final String COL_SHIP_ALONE = "extn_ship_alone";
	public static final String COL_BREAKABLE = "extn_breakable";
	public static final String COL_CAGE_ITEM = "extn_cage_item";
	public static final String COL_PLASTIC_GIFT_CARD = "extn_is_plastic_gift_card";
	public static final String COL_GIFT_WRAP_FLAG = "allow_gift_wrap";
	public static final String COL_BAGGAGE = "extn_baggage";
	public static final String COL_ORDER_NO = "order_no";
	public static final String COL_SHIPNODE_KEY = "shipnode_key";
	public static final String COL_EXTN_SHIP_NODE_SOURCE = "extn_ship_node_source";
	public static final String COL_SHIPNODE = "ship_node";
	public static final String COL_PRIME_LINE_NO = "prime_line_no";
	public static final String COL_ORIGINAL_ORDERED_QTY = "original_ordered_qty";
	public static final String COL_GIFT_FLAG = "gift_flag";
	public static final String COL_GIFT_WRAP = "GIFT_WRAP";
	public static final String COL_DS_ITEM = "DS_ITEM";
	public static final String COL_STATE = "state";
	public static final String COL_HAZMAT_FLAG = "is_hazmat";
	public static final String COL_SALES_ORDER_NO = "SALES_ORDER_NO";
	public static final String COL_RELEASE_NO = "RELEASE_NO";
	public static final String COL_EXTN_PICK_TICKET_NO = "EXTN_PICK_TICKET_NO";
	public static final String COL_PICK_TICKET_NO = "PICKTICKET_NO";
	public static final String COL_STATUS = "STATUS";
	public static final String COL_ORDER_DATE = "ORDER_DATE";
	public static final String COL_INV_TYPE = "INV_TYPE";
	public static final String COL_QUANTITY = "QUANTITY";
	public static final String COL_ON_HAND_HELD_QUANTITY = "on_hand_held_qty";
	public static final String COL_ON_HAND_QUANTITY = "on_hand_qty";
	public static final String COL_TRANSACTION_TYPE = "transaction_type";
	public static final String COL_REF_1 = "reference_1";
	public static final String COL_REF_2 = "reference_2";
	public static final String COL_REF_3 = "reference_3";
	public static final String COL_REF_4 = "reference_4";
	public static final String COL_REF_5 = "reference_5";
	public static final String COL_SUPPLY_TYPE = "supply_type";
	public static final String COL_MODIFY_PROG_ID = "modifyprogid";
	public static final String COL_STATUS_QUANTITY = "STATUS_QUANTITY";
	public static final String COL_MODIFY_TS = "MODIFYTS";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
	public static final String COL_TRACKING_NO = "tracking_no";
	public static final String COL_ACTUAL_SHIPMENT_DATE = "actual_shipment_date";
	public static final String COL_EXPECTED_SHIPMENT_DATE = "expected_shipment_date";
	public static final String COL_SCAC = "scac";
	public static final String COL_CARRIER_SERVICE_CODE = "carrier_service_code";
	public static final String COL_ITEM_DESC = "item_description";
	public static final String COL_CONT_SCM = "container_scm";
	public static final String COL_CONT_GROSS_WT = "container_gross_weight";
	public static final String COL_SHIP_NO = "shipment_no";
	public static final String COL_SHIP_TO = "SHIP_TO";
	public static final String COL_RED_PACK_LIST_TYPE = "extn_red_pack_list_type";
	public static final String COL_SHIP_NODE_SOURCE = "extn_ship_node_source";
	public static final String COL_NOMADIC_FLAG = "extn_nomadic";
	private static final String COL_CUST_PO_NBR = "CUST_PO_NBR";
	private static final String COL_WHSE = "WHSE";
	private static final String COL_ORD_TYPE = "ORD_TYPE";
	private static final String COL_SHIPTO_STATE = "SHIPTO_STATE";
	private static final String COL_STAT_CODE = "STAT_CODE";
	private static final String COL_ORD_DATE = "ORD_DATE";
	private static final String COL_CREATE_DATE_TIME = "CREATE_DATE_TIME";
	private static final String COL_RTE_GUIDE_NBR = "RTE_GUIDE_NBR";
	private static final String COL_PKT_CTRL_NBR = "PKT_CTRL_NBR";
	private static final String COL_TOTAL_NBR_OF_UNITS = "TOTAL_NBR_OF_UNITS";
	private static final String COL_SUPPLY = "SUPPLY";
	private static final String COL_DEMANDQTY = "DEMANDQTY";
	private static final String COL_AVAILABLE = "AVAILABLE";
	private static final String COL_NODE = "NODE";
	private static final String COL_RED_SKU_ID = "SKU_ID";
	private static final String COL_RED_SKU_DESC = "SKU_DESC";
	private static final String COL_RED_DSP_LOCN = "DSP_LOCN";
	private static final String COL_RED_AREA = "AREA";
	private static final String COL_RED_LOC_TBF = "LOC_TBF";
	private static final String COL_RED_LOC_TBP = "LOC_TBP";
	private static final String COL_RED_LOC_OH = "LOC_OH";
	private static final String COL_PROFILE_ID = "profile_id";
	private static final String COL_EXTN_CART_NUMBER = "extn_cart_number";
	private static final String COL_ON_ORDER = "On Order";
	private static final String COL_EXPECTED = "Store Expected";
	// Changes for Warehouse Transfer StoreElf Utility Screen - START
	public static final String COL_STATUS_DATE = "STATUS_DATE";
	public static final String COL_TRANSFER_TYPE = "TRANSFER_TYPE";
	public static final String COL_ITEM_COUNT = "ITEM_COUNT";
	public static final String COL_TOTAL_UNITS = "TOTAL_UNITS";
	public static final String COL_SHIPPED_UNITS = "SHIPPED_UNITS";
	public static final String COL_CANCELLED_UNITS = "CANCELLED_UNITS";
	public static final String COL_UNIT_VARIANCE = "UNIT_VARIANCE";
	public static final String COL_CREATED_BY = "ENTERED_BY";
	public static final String COL_EXTN_DEPT = "EXTN_DEPT";
	public static final String COL_CLASS = "EXTN_CLASS";
	public static final String COL_SUB_CLASS = "EXTN_SUB_CLASS";
	public static final String COL_LINE_STATUS = "STATUS";
	public static final String COL_RECEIVING_NODE = "RECEIVING_NODE";
	public static final String COL_FROM_NODE = "shipnode_key";
	public static final String COL_SHIPMENT_DATE = "SHIPMENT_DATE";
	public static final String COL_SHIPMENT_LINE = "SHIPMENT_LINE";
	public static final String COL_BOL = "BOL";
	public static final String COL_UNITS = "UNITS";
	public static final String COL_BATCH_NO = "BATCH_NO";
	public static final String COL_ITEM_RETAIL_PRICE = "ITEM_RETAIL_PRICE";
	public static final String COL_DISTRIBUTED_RETAIL_PRICE = "DISTRIBUTED_RETAIL_PRICE";
	public static final String COL_SAFETY_FACTOR = "ONHAND_SAFETY_FACTOR_QTY";
	public static final String COL_SAFETY_PCT = "ONHAND_SAFETY_FACTOR_PCT";
	// Changes for Warehouse Transfer StoreElf Utility Screen - END
	// Added for Store Unit Capacity StoreElf Screen - START
	public static final String COL_SUNDAY_CAPACITY = "SUNDAY_CAPACITY";
	public static final String COL_MONDAY_CAPACITY = "MONDAY_CAPACITY";
	public static final String COL_TUESDAY_CAPACITY = "TUESDAY_CAPACITY";
	public static final String COL_WEDNESDAY_CAPACITY = "WEDNESDAY_CAPACITY";
	public static final String COL_THURSDAY_CAPACITY = "THURSDAY_CAPACITY";
	public static final String COL_FRIDAY_CAPACITY = "FRIDAY_CAPACITY";
	public static final String COL_SATURDAY_CAPACITY = "SATURDAY_CAPACITY";
	public static final String COL_NODE_KEY = "NODE_KEY";
	public static final String COL_UNIT_SOURCED_TODAY = "UNIT_SOURCED_TODAY";

	public static final String COL_CAPACITY_QUANTITY = "QUANTITY";
	public static final String COL_CAPACITY_SHIPMENT_DATE = "SHIPMENT_DATE";
	public static final String COL_CAPACITY_DAY = "DAY";
	// Added for Store Unit Capacity StoreElf Screen - END

	//Changes for WMOS Utilities screen BEGIN
	//ship via
	public static final String COL_USER_ID_SHIPVIA = "USER_ID ";
	public static final String COL_MOD_DATE_TIME_SHIPVIA = "MOD_DATE_TIME";
	public static final String COL_CREATE_DATE_TIME_SHIPVIA = "CREATE_DATE_TIME";
	public static final String COL_LABEL_TYPE = "LABEL_TYPE";
	public static final String COL_SERV_TYPE = "SERV_TYPE";
	public static final String COL_CARR_ID = "CARR_ID";
	public static final String COL_SHIP_VIA_DESC = "SHIP_VIA_DESC";
	public static final String COL_SHIP_VIA = "SHIP_VIA";


	//pktdtl pktHeader
	public static final String COL_PKT_CTRL_NBR_PKTHDR = "PKT_CTRL_NBR";
	public static final String COL_WHSE_PKTHDR = "WHSE";
	public static final String COL_ECOMM_ORD_PKTHDR = "ECOMM_ORD";
	public static final String COL_TYPE = "TYPE";
	public static final String COL_PRTY_CODE= "PRTY_CODE";
	public static final String COL_ORD_DATE_PKTHDR = "ORD_DATE";
/*	public static final String COL_SHIPTO_NAME= "SHIPTO_NAME";*/
	public static final String COL_TOTAL_UNITS_PKTHDR = "TOTAL_UNITS";
	public static final String COL_STATUS_PKTHDR = "STATUS";

	//pktdetail
	public static final String COL_PKT_SEQ_NBR_PKTDTL = "PKT_SEQ_NBR";
	public static final String COL_SKU_ID_PKTDTL = "SKU_ID";
	public static final String COL_ORIG_QTY = "ORIG_QTY";
	public static final String COL_PKT_QTY_PKTDTL = "PKT_QTY";
	public static final String COL_CANC_QTY  = "CANC_QTY";
	public static final String COL_TO_BE_VERF = "TO_BE_VERF";
	public static final String COL_VERF_PAKD = "VERF_PAKD";
	public static final String COL_UNITS_PAKD_PKTDTL = "UNITS_PAKD";
	public static final String COL_SPL_INSTR_CODE_2 = "SPL_INSTR_CODE_2";
	public static final String COL_STATUS_PKTDTL = "STATUS";
	public static final String COL_CONVEY_FLAG = "CONVEY_FLAG";
	public static final String COL_CHUTE_ASGN = "CHUTE_ASGN";
	public static final String COL_CARTON_TYPE_PKTDTL = "CARTON_TYPE";
	public static final String COL_USER_ID_PKTDTL = "USER_ID";

	//cartonHeader
	public static final String COL_CARTON_NBR_CRTNHDR = "CARTON_NBR";
	public static final String COL_MISC_INSTR_CODE_1  = "TYPE";
	//EFC 3 upgrade
	public static final String COL_SINGLES  = "SINGLES";
	public static final String COL_DIVERT  = "DIVERT";
	public static final String COL_CARTON_GRP_CODE = "CARTON_GRP_CODE";
	public static final String COL_CHUTE_ASSIGN_TYPE = "CHUTE_ASSIGN_TYPE";
	public static final String COL_CHUTE_ID = "CHUTE_ID";
	//public static final String COL_TOTAL_QTY = "TOTAL_QTY";
	//public static final String COL_LOAD_NBR= "LOAD_NBR";

	//cartonDetail
	public static final String COL_CARTON_NBR_CRTNDTL = "CARTON_NBR";
	public static final String COL_PKT_CTRL_NBR_CRTNDTL= "PKT_CTRL_NBR";
	public static final String COL_PKT_SEQ_NBR_CRTNDTL= "PKT_SEQ_NBR";
	public static final String COL_SKU_ID_CRTNDTL= "SKU_ID";
	public static final String COL_CARTON_SEQ_NBR_CRTNDTL= "CARTON_SEQ_NBR";
	public static final String COL_TO_BE_PAKD_CRTNDTL= "TO_BE_PAKD";
	public static final String COL_PAKD_CRTNDTL= "PAKD";
	public static final String COL_LSTATUS= "LSTATUS";
	public static final String COL_USER_ID_CRTNDTL= "USER_ID";
	public static final String COL_MOD_DATE_TIME_CRTNDTL= "MOD_DATE_TIME";

	//cartonType
	public static final String COL_CARTON_NBR_CRTNTYP= "CARTON_NBR";
	public static final String COL_CARTON_TYPE= "CARTON_TYPE";
	public static final String COL_CARTON_SIZE= "CARTON_SIZE";
	public static final String COL_ACTL_CNTR_VOL= "ACTL_CNTR_VOL";
	public static final String COL_MAX_CNTR_VOL= "MAX_CNTR_VOL";
	public static final String COL_MAX_CNTR_WT= "MAX_CNTR_WT";
	public static final String COL_WIDTH= "WIDTH";
	public static final String COL_HT= "HT";

//manifest header
	public static final String COL_MANIF_NBR_MANHDR = "MANIF_NBR";
	public static final String COL_MANIF_TYPE = "MANIF_TYPE";
	public static final String COL_CREATE_DATE_TIME_MANHDR = "CREATE_DATE_TIME";
	public static final String COL_CLOSE_DATE = "CLOSE_DATE";
	public static final String COL_STATUS_MANHDR = "STATUS";
	public static final String COL_PIKUP_REC_NBR = "PIKUP_REC_NBR";

	//manifest carton details
	public static final String COL_MANIF_NBR = "MANIF_NBR";
	public static final String COL_CARTON_NBR_MANDTL = "CARTON_NBR";
	public static final String COL_CREATE_DATE_TIME_MANDTL = "CREATE_DATE_TIME";
	public static final String COL_USER_ID = "USER_ID";
	public static final String COL_STATUS_MANDTL = "STATUS";

	//Output Pick Ticket Header
	public static final String COL_PKT_CTRL_NBR_OPKTHDR = "PKT_CTRL_NBR";
	public static final String COL_ECOMM_ORD = "ECOMM_ORD";
	public static final String COL_INVC_BATCH_NBR_OPKTHDR = "INVC_BATCH_NBR";
	public static final String COL_CREATE_DATE_TIME_OPKTHDR = "CREATE_DATE_TIME";
	public static final String COL_MOD_DATE_TIME_OPKTHDR = "MOD_DATE_TIME";
	public static final String COL_PROC_DATE_TIME_OPKTHDR = "PROC_DATE_TIME";
	public static final String COL_STATUS_OPKTHDR = "STATUS";


	/* Output Pick Ticket Detail: Tables(outpt_pkt_dtl) */
	public static final String COL_PKT_SEQ_NBR_OPKTDTL = "PKT_SEQ_NBR";
	public static final String COL_SKU_ID_OPKTDTL = "SKU_ID";
	public static final String COL_ORIG_PKT_QTY_OPKTDTL= "ORIG_PKT_QTY";
	public static final String COL_PKT_QTY= "PKT_QTY";
	public static final String COL_CANCEL_QTY= "CANCEL_QTY";
	public static final String COL_SHPD_QTY_OPKTDTL= "SHPD_QTY";
	public static final String COL_CREATE_DATE_TIME_OPKTDTL= "CREATE_DATE_TIME";

	/* Output Carton header: Tables(outpt_carton_hdr/sys_code) */
	public static final String COL_INVC_BATCH_NBR_OCRTNHDR = "INVC_BATCH_NBR";
	public static final String COL_PKT_CTRL_NBR_OCRTNHDR = "PKT_CTRL_NBR";
	public static final String COL_CARTON_NBR_OCRTNHDR = "CARTON_NBR";
	public static final String COL_CREATE_DATE_TIME_OCRTNHDR = "CREATE_DATE_TIME";
	public static final String COL_MOD_DATE_TIME_OCRTNHDR = "MOD_DATE_TIME";
	public static final String COL_PROC_DATE_TIME = "PROC_DATE_TIME";
	public static final String COL_STATUS_OCRTNHDR = "STATUS";

	/* Output Carton Detail: Tables(outpt_carton_dtl) */
	public static final String COL_INVC_BATCH_NBR = "INVC_BATCH_NBR";
	public static final String COL_CARTON_NBR = "CARTON_NBR";
	public static final String COL_CARTON_SEQ_NBR = "CARTON_SEQ_NBR";
	public static final String COL_PKT_CTRL_NBR_OCRTNDTL = "PKT_CTRL_NBR";
	public static final String COL_PKT_SEQ_NBR_OCRTNDTL = "PKT_SEQ_NBR";
	public static final String COL_SKU_ID_OCRTNDTL = "SKU_ID";
	public static final String COL_UNITS_PAKD = "UNITS_PAKD";
	public static final String COL_MOD_DATE_TIME = "MOD_DATE_TIME";


	/* Cancels sent to oms: Tables(outpt_pkt_dtl) */
	/* Condition: If (orig_pkt_qty - shpd_qty) greater than zero */
	public static final String COL_PKT_CTRL_NBR_CNCLS = "PKT_CTRL_NBR";
	public static final String COL_PKT_SEQ_NBR_CNCLS = "PKT_SEQ_NBR";
	public static final String COL_SKU_ID_CNCLS = "SKU_ID";
	public static final String COL_ORIG_PKT_QTY = "ORIG_PKT_QTY";
	public static final String COL_SHPD_QTY = "SHPD_QTY";
	public static final String COL_ORIGMINUSSHPD = "DIFFORIGSHPD";


	/* Invoice Detail: Tables(outpt_carton_dtl) */
	public static final String COL_PKT_CTRL_NBR_INV = "PKT_CTRL_NBR";
	public static final String COL_PKT_SEQ_NBR = "PKT_SEQ_NBR";
	public static final String COL_SKU_ID = "SKU_ID";
	public static final String COL_SUM_UNITS_PAKD = "SUM(OCD.UNITS_PAKD)";

	/* Carton Header(carton_hdr) */
	public static final String COL_CARTON_NO = "CARTON_NBR";
	public static final String COL_CARTON_TYPE_CRTN = "TYPE";
	public static final String COL_CARTON_SINGLES = "SINGLES";
	public static final String COL_CARTON_DIVERT = "DIVERT";
	public static final String COL_CARTON_GRP = "CARTON_GRP_CODE";
	public static final String COL_CARTON_CHUTE_ASGN = "CHUTE_ASSIGN_TYPE";
	public static final String COL_CARTON_CHUTE_ID = "CHUTE_ID";

	/* Carton Detail(carton_dtl) */
	public static final String COL_CARTON_NO_DTL = "CARTON_NBR";
	public static final String COL_CARTON_PKT_CTRL = "PKT_CTRL_NBR";
	public static final String COL_CARTON_SEQ = "PKT_SEQ_NBR";
	public static final String COL_CARTON_SKU = "SKU_ID";
	public static final String COL_CARTON_TBP = "TO_BE_PAKD";
	public static final String COL_CARTON_PAKD = "PAKD";
	public static final String COL_CARTON_MODTIME = "MOD_DATE_TIME";
	public static final String COL_CARTON_USER_ID = "USER_ID";
	public static final String COL_CARTON_LINE = "LINE_ITEM_STAT";
	public static final String COL_CARTON_STATUS = "STATUS";

	/* Pick Ticket Header(pkt_hdr/carton_hdr) */
	public static final String COL_CARTON_CTRL_NBR = "PKT_CTRL_NBR";
	public static final String COL_CARTON_CUST_PO = "CUST_PO_NBR";
	public static final String COL_CARTON_ORD_TYPE = "ORD_TYPE";
	public static final String COL_CARTON_STAT_CODE = "STAT_CODE";
	public static final String COL_CARTON_ORD_DATE = "ORD_DATE";

	/* Task Header(task_hdr) */
	public static final String COL_TASK_HDR_ID = "TASK_ID";
	public static final String COL_TASK_CRE_TIME = "CREATE_DATE_TIME";
	public static final String COL_TASK_MODTIME = "MOD_DATE_TIME";
	public static final String COL_TASK_USERID = "USER_ID";
	public static final String COL_TASK_STAT_CODE = "STAT_CODE";
	public static final String COL_TASK_STATUS = "STATUS";

	/* Task Detail(task_dtl/sys_code) */
	public static final String COL_TASK_AID_ID = "AID_ID";
	public static final String COL_TASK_MOD_DATE_TIME = "MOD_DATE_TIME";
	public static final String COL_TASK_DEST_LOCN = "DEST_LOCN";
	public static final String COL_TASK_NEED = "NEED";
	public static final String COL_TASK_SKU = "SKU_ID";
	public static final String COL_TASK_QTY_ALLOC = "QTY_ALLOC";
	public static final String COL_TASK_QTY_PULLED = "QTY_PULLD";
	public static final String COL_TASK_STAT_CODE2 = "STAT_CODE";
	public static final String COL_TASK_STATUS2 = "STATUS";
	public static final String COL_TASK_USER_ID = "USER_ID";

	/*  Allocation Inventory Detail(alloc_invn_dtl/sys_code) */
	public static final String COL_TASK_AID_ID2 = "AID_ID";
	public static final String COL_TASK_CREATE_DATE_TIME = "CREATE_DATE_TIME";
	public static final String COL_TASK_DEST_LOCN2 = "DEST_LOCN";
	public static final String COL_TASK_NEED2 = "NEED";
	public static final String COL_TASK_SKU2= "SKU_ID";
	public static final String COL_TASK_QTY_ALLOC2 = "QTY_ALLOC";
	public static final String COL_TASK_QTY_PULLED2 = "QTY_PULLD";
	public static final String COL_TASK_STAT_CODE3 = "STAT_CODE";
	public static final String COL_TASK_STATUS3 = "STATUS";
	public static final String COL_TASK_USR_ID3 = "USER_ID";
	public static final String COL_TASK_PKT_NO = "PKT_CTRL_NBR";

	/* Case Details(case_hdr) */
	public static final String COL_TASK_CASE_DSP = "DSP_LOCN";
	public static final String COL_TASK_CASE_NBR = "CASE_NBR";
	public static final String COL_TASK_CASE_STAT = "STAT_CODE";
	public static final String COL_TASK_CASE_STATUS = "STATUS";
	public static final String COL_TASK_CASE_DEST = "DEST_LOCN";
	public static final String COL_TASK_CASE_SENT = "SENT_TO";
	public static final String COL_TASK_CASE_DIVERTED = "DIVERTED";

	//Changes for WMOS Utilities screen END

	/* Map with the column name and description */
	public static HashMap<String, String> SQL_COL_TO_DESC_MAP = new HashMap<String, String>();
	static {
		SQL_COL_TO_DESC_MAP.put(COL_EXTN_SHIP_NODE_SOURCE,					"extn_ship_node_source");
		SQL_COL_TO_DESC_MAP.put(COL_RED_PACK_LIST_TYPE, "RED Pack List Type");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_NODE_SOURCE, "Ship Node Source");
		SQL_COL_TO_DESC_MAP.put(COL_NOMADIC_FLAG, "Nomadic Flag");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM_ID, "Item ID");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM, "Item ID");
		SQL_COL_TO_DESC_MAP.put(COL_ACTIVE, "Active");
		SQL_COL_TO_DESC_MAP.put(COL_CASE, "Case");
		SQL_COL_TO_DESC_MAP.put(COL_TRANS, "Trans");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON, "Carton");
		SQL_COL_TO_DESC_MAP.put(COL_UNALLOC_CASE, "Unalloc Case");
		SQL_COL_TO_DESC_MAP.put(COL_UNALLOC_TRANS, "Unalloc Trans");
		SQL_COL_TO_DESC_MAP.put(COL_ALLOC_TOTAL, "Total Allocatable Inventory");
		SQL_COL_TO_DESC_MAP.put(COL_UNALLOC_TOTAL,					"Total Un-Allocatable Inventory");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM_TYPE, "Item Type");
		SQL_COL_TO_DESC_MAP.put(COL_PRODUCT_LINE, "Product Line");
		SQL_COL_TO_DESC_MAP.put(COL_DIRECT_SHIP, "Direct Ship Flag");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_ALONE, "Ship Alone Flag");
		SQL_COL_TO_DESC_MAP.put(COL_CAGE_ITEM, "Cage Item Flag");
		SQL_COL_TO_DESC_MAP.put(COL_PLASTIC_GIFT_CARD, "Gift Card Flag");
		SQL_COL_TO_DESC_MAP.put(COL_BREAKABLE, "Breakable Flag");
		SQL_COL_TO_DESC_MAP.put(COL_GIFT_WRAP_FLAG, "Item Allow Gift Wrap");
		SQL_COL_TO_DESC_MAP.put(COL_BAGGAGE, "Baggage Flag");
		SQL_COL_TO_DESC_MAP.put(COL_ORDER_NO, "Order No");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPNODE_KEY, "Ship Node");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPNODE, "Ship Node");
		SQL_COL_TO_DESC_MAP.put(COL_PRIME_LINE_NO, "Line No");
		SQL_COL_TO_DESC_MAP.put(COL_ORIGINAL_ORDERED_QTY, "Quantity");
		SQL_COL_TO_DESC_MAP.put(COL_GIFT_FLAG, "Gift Flag");
		SQL_COL_TO_DESC_MAP.put(COL_GIFT_WRAP, "Gift Wrap Flag");
		SQL_COL_TO_DESC_MAP.put(COL_DS_ITEM, "DS Item Flag");
		SQL_COL_TO_DESC_MAP.put(COL_STATE, "State");
		SQL_COL_TO_DESC_MAP.put(COL_HAZMAT_FLAG, "Hazmat Flag");
		SQL_COL_TO_DESC_MAP.put(COL_SALES_ORDER_NO, "Order No");
		SQL_COL_TO_DESC_MAP.put(COL_EXTN_PICK_TICKET_NO, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_PICK_TICKET_NO, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS, "Status");
		SQL_COL_TO_DESC_MAP.put(COL_RELEASE_NO, "Release No");
		SQL_COL_TO_DESC_MAP.put(COL_ORDER_DATE, "Order Date");
		SQL_COL_TO_DESC_MAP.put(COL_INV_TYPE, "Inventory Type");
		SQL_COL_TO_DESC_MAP.put(COL_QUANTITY, "Quantity");
		SQL_COL_TO_DESC_MAP.put(COL_ON_HAND_HELD_QUANTITY,					"On Hand Held Quantity");
		SQL_COL_TO_DESC_MAP.put(COL_SAFETY_FACTOR, "Safety Factor");
		SQL_COL_TO_DESC_MAP.put(COL_SAFETY_PCT,"Safety Percentage");
		SQL_COL_TO_DESC_MAP.put(COL_ON_HAND_QUANTITY, "On Hand Quantity");
		SQL_COL_TO_DESC_MAP.put(COL_TRANSACTION_TYPE, "Transaction Type");
		SQL_COL_TO_DESC_MAP.put(COL_REF_1, "PIX Tran No");
		SQL_COL_TO_DESC_MAP.put(COL_REF_2, "PIX Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_REF_3, "PIX Create Date");
		SQL_COL_TO_DESC_MAP.put(COL_REF_4, "PIX Tran Type");
		SQL_COL_TO_DESC_MAP.put(COL_REF_5, "Reference 5");
		SQL_COL_TO_DESC_MAP.put(COL_SUPPLY_TYPE, "Supply Type");
		SQL_COL_TO_DESC_MAP.put(COL_MODIFY_PROG_ID, "Modify Program ID");
		SQL_COL_TO_DESC_MAP.put(COL_MODIFY_TS, "Last Modified Timestamp");
		SQL_COL_TO_DESC_MAP.put(COL_DESCRIPTION, "Description");
		SQL_COL_TO_DESC_MAP.put(COL_SHORT_DESCRIPTION, "Description");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_NO, "Shipment No");
		SQL_COL_TO_DESC_MAP.put(COL_TRACKING_NO, "Tracking No");
		SQL_COL_TO_DESC_MAP.put(COL_ACTUAL_SHIPMENT_DATE,					"Actual Shipment Date");
		SQL_COL_TO_DESC_MAP.put(COL_EXPECTED_SHIPMENT_DATE,					"Expected Shipment Date");
		SQL_COL_TO_DESC_MAP.put(COL_SCAC, "Carrier");
		SQL_COL_TO_DESC_MAP.put(COL_CARRIER_SERVICE_CODE,					"Carrier Service Code");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM_DESC, "Item Description");
		SQL_COL_TO_DESC_MAP.put(COL_CONT_SCM, "Container SCM");
		SQL_COL_TO_DESC_MAP.put(COL_CONT_GROSS_WT, "Container Weight");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_TO, "Ship To");

		SQL_COL_TO_DESC_MAP.put(COL_CUST_PO_NBR, "Order No");
		SQL_COL_TO_DESC_MAP.put(COL_WHSE, "Ship Node");
		SQL_COL_TO_DESC_MAP.put(COL_ORD_TYPE, "Order Type");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPTO_STATE, "Ship To");
		SQL_COL_TO_DESC_MAP.put(COL_STAT_CODE, "Status Code");
		SQL_COL_TO_DESC_MAP.put(COL_ORD_DATE, "Order Date");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME, "Pickticket CreateDate");
		SQL_COL_TO_DESC_MAP.put(COL_RTE_GUIDE_NBR, "Routing Guide");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_TOTAL_NBR_OF_UNITS, "Quantity");

		SQL_COL_TO_DESC_MAP.put(COL_SUPPLY, "Supply");
		SQL_COL_TO_DESC_MAP.put(COL_DEMANDQTY, "DemandQty");
		SQL_COL_TO_DESC_MAP.put(COL_AVAILABLE, "Available");
		SQL_COL_TO_DESC_MAP.put(COL_NODE, "Node");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_QUANTITY, "Quantity");
		SQL_COL_TO_DESC_MAP.put(COL_PROFILE_ID, "Profile ID");
		SQL_COL_TO_DESC_MAP.put(COL_EXTN_CART_NUMBER, "Node");

		// Changes for Warehouse Transfer StoreElf Utility Screen - START
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_DATE, "Status Date");
		SQL_COL_TO_DESC_MAP.put(COL_TRANSFER_TYPE, "Transfer Type");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM_COUNT, "Item Count");
		SQL_COL_TO_DESC_MAP.put(COL_TOTAL_UNITS, "Total Units");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPPED_UNITS, "Shipped Units");
		SQL_COL_TO_DESC_MAP.put(COL_CANCELLED_UNITS, "Cancelled Units");
		SQL_COL_TO_DESC_MAP.put(COL_UNIT_VARIANCE, "Unit Variance");
		SQL_COL_TO_DESC_MAP.put(COL_CREATED_BY, "Created By");
		SQL_COL_TO_DESC_MAP.put(COL_EXTN_DEPT, "Dept");
		SQL_COL_TO_DESC_MAP.put(COL_CLASS, "Class");
		SQL_COL_TO_DESC_MAP.put(COL_SUB_CLASS, "Sub Class");
		SQL_COL_TO_DESC_MAP.put(COL_RECEIVING_NODE, "To Node");
		SQL_COL_TO_DESC_MAP.put(COL_FROM_NODE, "From Node");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPMENT_DATE, "Shipment Date");
		SQL_COL_TO_DESC_MAP.put(COL_SHIPMENT_LINE, "Shipment Line");
		SQL_COL_TO_DESC_MAP.put(COL_BATCH_NO, "Batch No");
		SQL_COL_TO_DESC_MAP.put(COL_ITEM_RETAIL_PRICE, "Item Retail Price");
		SQL_COL_TO_DESC_MAP.put(COL_DISTRIBUTED_RETAIL_PRICE, "Distributed Retail Price");
		SQL_COL_TO_DESC_MAP.put(COL_UNITS, "Units");
		// Changes for Warehouse Transfer StoreElf Utility Screen - END
		// Added for Store Unit Capacity StoreElf Screen - START
		SQL_COL_TO_DESC_MAP.put(COL_SUNDAY_CAPACITY , "Sunday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_MONDAY_CAPACITY , "Monday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_TUESDAY_CAPACITY , "Tuesday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_WEDNESDAY_CAPACITY , "Wednesday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_THURSDAY_CAPACITY , "Thursday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_FRIDAY_CAPACITY , "Friday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_SATURDAY_CAPACITY , "Saturday Capacity");
		SQL_COL_TO_DESC_MAP.put(COL_NODE_KEY , "Store No");
		SQL_COL_TO_DESC_MAP.put(COL_UNIT_SOURCED_TODAY , "Unit Sourced Today");
		// Added for Store Unit Capacity StoreElf Screen - END

		//Changes For WMOS Utility screen BEGIN
		//ship via
		SQL_COL_TO_DESC_MAP.put(COL_USER_ID , "User ID ");
		SQL_COL_TO_DESC_MAP.put(COL_MOD_DATE_TIME , "Modification Time");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_SHIPVIA , "Date Created");
		SQL_COL_TO_DESC_MAP.put(COL_LABEL_TYPE , "Label Type");
		SQL_COL_TO_DESC_MAP.put(COL_SERV_TYPE , "Serv Type");
		SQL_COL_TO_DESC_MAP.put(COL_CARR_ID , "Carrier ID");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_VIA_DESC , "Description");
		SQL_COL_TO_DESC_MAP.put(COL_SHIP_VIA , "Code");
		//Changes For WMOS Utility screen END

		//pkthdr
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_PKTHDR , "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_WHSE_PKTHDR, "Warehouse");
		SQL_COL_TO_DESC_MAP.put(COL_ECOMM_ORD_PKTHDR, "Ecomm Order");
		SQL_COL_TO_DESC_MAP.put(COL_TYPE, "Type");
		SQL_COL_TO_DESC_MAP.put(COL_PRTY_CODE, "Priority Code");
		SQL_COL_TO_DESC_MAP.put(COL_ORD_DATE_PKTHDR, "Order Date");
/*		SQL_COL_TO_DESC_MAP.put(COL_SHIPTO_NAME, "Shipto Name");*/
		SQL_COL_TO_DESC_MAP.put(COL_TOTAL_UNITS_PKTHDR, "Total Units");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_PKTHDR, "Status");

		//pktdtl
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR_PKTDTL, "Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID_PKTDTL, "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_ORIG_QTY, "Orig Qty");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_QTY_PKTDTL, "Pkt Qty");
		SQL_COL_TO_DESC_MAP.put(COL_CANC_QTY , "Cancelled Qty");
		SQL_COL_TO_DESC_MAP.put(COL_TO_BE_VERF, "To Be Verfied");
		SQL_COL_TO_DESC_MAP.put(COL_VERF_PAKD, "Verfied");
		SQL_COL_TO_DESC_MAP.put(COL_UNITS_PAKD_PKTDTL, "Units Packed");
		SQL_COL_TO_DESC_MAP.put(COL_SPL_INSTR_CODE_2, "SPL Instr Code 2");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_PKTDTL, "Status");
		SQL_COL_TO_DESC_MAP.put(COL_CONVEY_FLAG, "Convey Flag");
		SQL_COL_TO_DESC_MAP.put(COL_CHUTE_ASGN, "Chute Assignment");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_TYPE_PKTDTL, "Carton Type");
		SQL_COL_TO_DESC_MAP.put(COL_USER_ID_PKTDTL, "User Id");

		//carton header
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR_CRTNHDR, "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_MISC_INSTR_CODE_1 , "Type");
		SQL_COL_TO_DESC_MAP.put(COL_SINGLES , "Singles");
		SQL_COL_TO_DESC_MAP.put(COL_DIVERT , "Divert");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_GRP_CODE, "GRP Code");
		SQL_COL_TO_DESC_MAP.put(COL_CHUTE_ASSIGN_TYPE, "Chute Asgn Code");
		SQL_COL_TO_DESC_MAP.put(COL_CHUTE_ID, "Chute Id");
		//SQL_COL_TO_DESC_MAP.put(COL_TOTAL_QTY, "Total Qty");
		//SQL_COL_TO_DESC_MAP.put(COL_LOAD_NBR, "Load No");

		//cartondtl
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR_CRTNDTL, "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_CRTNDTL, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR_CRTNDTL, "Pkt Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID_CRTNDTL, "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_SEQ_NBR_CRTNDTL, "Carton Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_TO_BE_PAKD_CRTNDTL, "To Be Packed");
		SQL_COL_TO_DESC_MAP.put(COL_PAKD_CRTNDTL, "Packed");
		SQL_COL_TO_DESC_MAP.put(COL_LSTATUS, "LStatus");
		SQL_COL_TO_DESC_MAP.put(COL_USER_ID_CRTNDTL, "User ID");
		SQL_COL_TO_DESC_MAP.put(COL_MOD_DATE_TIME_CRTNDTL, "Modification Time");

		//cartontype
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR_CRTNTYP, "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_TYPE, "Carton Type");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_SIZE, "Carton Size");
		SQL_COL_TO_DESC_MAP.put(COL_ACTL_CNTR_VOL, "Actual Container Volume");
		SQL_COL_TO_DESC_MAP.put(COL_MAX_CNTR_VOL, "Max Container Volume");
		SQL_COL_TO_DESC_MAP.put(COL_MAX_CNTR_WT, "Max Container Weight");
		SQL_COL_TO_DESC_MAP.put(COL_WIDTH, "Width");
		SQL_COL_TO_DESC_MAP.put(COL_HT, "Height");

		//manifesthdr
		SQL_COL_TO_DESC_MAP.put(COL_MANIF_NBR_MANHDR , "Manifest No");
		SQL_COL_TO_DESC_MAP.put(COL_MANIF_TYPE , "Manifest Type");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_MANHDR , "Created");
		SQL_COL_TO_DESC_MAP.put(COL_CLOSE_DATE , "Closed");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_MANHDR , "Status");
		SQL_COL_TO_DESC_MAP.put(COL_PIKUP_REC_NBR , "Pickup Rec No");

		//mandtl
		SQL_COL_TO_DESC_MAP.put(COL_MANIF_NBR , "Manifest No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR_MANDTL , "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_MANDTL , "Created");
		SQL_COL_TO_DESC_MAP.put(COL_USER_ID , "User Id");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_MANDTL , "Status");

		//outputpkthdr
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_OPKTHDR , "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_ECOMM_ORD , "Ecomm Order");
		SQL_COL_TO_DESC_MAP.put(COL_INVC_BATCH_NBR_OPKTHDR , "Batch No");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_OPKTHDR , "Created");
		SQL_COL_TO_DESC_MAP.put(COL_MOD_DATE_TIME_OPKTHDR , "Modified");
		SQL_COL_TO_DESC_MAP.put(COL_PROC_DATE_TIME_OPKTHDR , "Proccess");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_OPKTHDR , "Status");

		//output pktdtl
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR_OPKTDTL , "Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID_OPKTDTL , "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_ORIG_PKT_QTY_OPKTDTL, "Original Qty");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_QTY, "Pkt Qty");
		SQL_COL_TO_DESC_MAP.put(COL_CANCEL_QTY, "Cancelled Qty");
		SQL_COL_TO_DESC_MAP.put(COL_SHPD_QTY_OPKTDTL, "Shipped Qty");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_OPKTDTL, "Created");

		//output cartonhdr
		SQL_COL_TO_DESC_MAP.put(COL_INVC_BATCH_NBR_OCRTNHDR , "Batch No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_OCRTNHDR , "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR_OCRTNHDR , "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_CREATE_DATE_TIME_OCRTNHDR , "Created");
		SQL_COL_TO_DESC_MAP.put(COL_MOD_DATE_TIME_OCRTNHDR , "Modified");
		SQL_COL_TO_DESC_MAP.put(COL_PROC_DATE_TIME , "Processed");
		SQL_COL_TO_DESC_MAP.put(COL_STATUS_OCRTNHDR , "Status");

		//output carton dtl
		SQL_COL_TO_DESC_MAP.put(COL_INVC_BATCH_NBR , "Batch No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_NBR , "Carton No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_SEQ_NBR , "Carton Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_OCRTNDTL , "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR_OCRTNDTL , "Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID_OCRTNDTL , "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_UNITS_PAKD , "Units Packed");
		SQL_COL_TO_DESC_MAP.put(COL_MOD_DATE_TIME , "Modified");

		//cancels
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_CNCLS, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR_CNCLS, "Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID_CNCLS, "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_ORIG_PKT_QTY, "Original Qty");
		SQL_COL_TO_DESC_MAP.put(COL_SHPD_QTY, "Shipped Qty");
		SQL_COL_TO_DESC_MAP.put(COL_ORIGMINUSSHPD , "Orig Qty - Shipped Qty");

		//invoice
		SQL_COL_TO_DESC_MAP.put(COL_PKT_CTRL_NBR_INV , "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_PKT_SEQ_NBR , "Sequence No");
		SQL_COL_TO_DESC_MAP.put(COL_SKU_ID , "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_SUM_UNITS_PAKD , "Sum Units Packed");


		/* Carton Header(carton_hdr) */
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_NO, "Carton No");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_TYPE_CRTN, "Type");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_SINGLES, "Singles");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_DIVERT, "Divert");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_GRP, "GRP Code");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_CHUTE_ASGN, "Chute Assignment");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_CHUTE_ID, "Chute Id");

		/* Carton Detail(carton_dtl) */
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_NO_DTL, "Carton No");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_PKT_CTRL, "Pickticket No");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_SEQ, "Sequence No");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_SKU, "SKU");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_TBP, "To Be Packed");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_PAKD, "Packed");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_MODTIME, "Modification Time");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_USER_ID, "User ID");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_LINE, "Line Item Stat");
		 SQL_COL_TO_DESC_MAP.put(COL_CARTON_STATUS, "Status");

		/* Pick Ticket Header(pkt_hdr/carton_hdr) */
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_CTRL_NBR, "Pickticket No");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_CUST_PO, "Customer Po Nbr");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_ORD_TYPE, "Order Type");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_STAT_CODE, "Stat Code");
		SQL_COL_TO_DESC_MAP.put(COL_CARTON_ORD_DATE, "Order Date");

		// Task Header(task_hdr)
		SQL_COL_TO_DESC_MAP.put(COL_TASK_HDR_ID, "Task Id");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CRE_TIME, "Create Time");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_MODTIME, "Modification Time");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_USERID, "User Id");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STAT_CODE, "Status Code");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STATUS, "Status");

		// Task Detail(task_dtl/sys_code)
		SQL_COL_TO_DESC_MAP.put(COL_TASK_AID_ID, "Allocation Inventory Id");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_MOD_DATE_TIME, "Modification Time");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_DEST_LOCN, "Destination Location");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_NEED, "Need");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_SKU, "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_QTY_ALLOC, "Qty Alloc");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_QTY_PULLED, "Qty Pulled");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STAT_CODE2, "Status Code");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STATUS2, "Status");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_USER_ID, "User Id");

		// Allocation Inventory Detail(alloc_invn_dtl/sys_code)
		SQL_COL_TO_DESC_MAP.put(COL_TASK_AID_ID2 , "Allocation Inventory Id");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CREATE_DATE_TIME , "Create Date Time");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_DEST_LOCN2 , "Destination Location");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_NEED2 , "Need");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_SKU2, "SKU");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_QTY_ALLOC2 , "Qty Alloc");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_QTY_PULLED2 , "Qty Pulled");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STAT_CODE3 , "Stat Code");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_STATUS3 , "Status");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_USR_ID3 , "User Id");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_PKT_NO , "Pickticket No");

		// Case Details(case_hdr)
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_DSP, "DSP");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_NBR, "Case No");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_STAT, "Stat Code");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_STATUS, "Status");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_DEST, "Destination Location");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_SENT, "Sent");
		SQL_COL_TO_DESC_MAP.put(COL_TASK_CASE_DIVERTED, "Diverted");
	}

	/* Map to columns for the forms */
	public static HashMap<Integer, String> FRM_ITEM_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_ITEM_COL_MAP.put(1, COL_ITEM_ID);
		FRM_ITEM_COL_MAP.put(2, COL_SHORT_DESCRIPTION);
		FRM_ITEM_COL_MAP.put(3, COL_ITEM_TYPE);
		FRM_ITEM_COL_MAP.put(4, COL_PRODUCT_LINE);
		FRM_ITEM_COL_MAP.put(5, COL_DIRECT_SHIP);
		FRM_ITEM_COL_MAP.put(6, COL_SHIP_ALONE);
		FRM_ITEM_COL_MAP.put(7, COL_CAGE_ITEM);
		FRM_ITEM_COL_MAP.put(8, COL_PLASTIC_GIFT_CARD);
		FRM_ITEM_COL_MAP.put(9, COL_BREAKABLE);
		FRM_ITEM_COL_MAP.put(10, COL_GIFT_WRAP_FLAG);
		FRM_ITEM_COL_MAP.put(11, COL_BAGGAGE);
		FRM_ITEM_COL_MAP.put(12, COL_RED_PACK_LIST_TYPE);
		FRM_ITEM_COL_MAP.put(13, COL_NOMADIC_FLAG);
		FRM_ITEM_COL_MAP.put(14, COL_SHIP_NODE_SOURCE);
		FRM_ITEM_COL_MAP.put(15, COL_SAFETY_FACTOR);
		FRM_ITEM_COL_MAP.put(16, COL_SAFETY_PCT);
		FRM_ITEM_COL_MAP.put(17, COL_HAZMAT_FLAG);
	}

	public static HashMap<Integer, String> FRM_ORDER_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_ORDER_COL_MAP.put(1, COL_ORDER_NO);
		FRM_ORDER_COL_MAP.put(2, COL_SHIPNODE_KEY);
		FRM_ORDER_COL_MAP.put(3, COL_PRIME_LINE_NO);
		FRM_ORDER_COL_MAP.put(4, COL_STATUS);
		FRM_ORDER_COL_MAP.put(5, COL_ITEM_ID);
		FRM_ORDER_COL_MAP.put(6, COL_STATUS_QUANTITY);
		FRM_ORDER_COL_MAP.put(7, COL_GIFT_FLAG);
		FRM_ORDER_COL_MAP.put(8, COL_GIFT_WRAP);
		FRM_ORDER_COL_MAP.put(9, COL_SHIP_ALONE);
		FRM_ORDER_COL_MAP.put(10, COL_CAGE_ITEM);
		FRM_ORDER_COL_MAP.put(11, COL_PLASTIC_GIFT_CARD);
		FRM_ORDER_COL_MAP.put(12, COL_BREAKABLE);
		FRM_ORDER_COL_MAP.put(13, COL_GIFT_WRAP_FLAG);
		FRM_ORDER_COL_MAP.put(14, COL_BAGGAGE);
		FRM_ORDER_COL_MAP.put(15, COL_HAZMAT_FLAG);
		FRM_ORDER_COL_MAP.put(16, COL_SHIP_TO);
		FRM_ORDER_COL_MAP.put(17, COL_ORDER_DATE);
		FRM_ORDER_COL_MAP.put(18, COL_DS_ITEM);
		FRM_ORDER_COL_MAP.put(19, COL_CARRIER_SERVICE_CODE);
	}

	public static HashMap<Integer, String> FRM_ORDERRELEASE_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_ORDERRELEASE_COL_MAP.put(1, COL_SALES_ORDER_NO);
		FRM_ORDERRELEASE_COL_MAP.put(2, COL_RELEASE_NO);
		FRM_ORDERRELEASE_COL_MAP.put(3, COL_SHIPNODE_KEY);
		FRM_ORDERRELEASE_COL_MAP.put(4, COL_PICK_TICKET_NO);
		FRM_ORDERRELEASE_COL_MAP.put(5, COL_STATUS);
		FRM_ORDERRELEASE_COL_MAP.put(6, COL_ORDER_DATE);
		FRM_ORDERRELEASE_COL_MAP.put(7, COL_PRIME_LINE_NO);
		FRM_ORDERRELEASE_COL_MAP.put(8, COL_STATUS_QUANTITY);
		FRM_ORDERRELEASE_COL_MAP.put(9, COL_ITEM_ID);
		FRM_ORDERRELEASE_COL_MAP.put(10, COL_MODIFY_TS);
		FRM_ORDERRELEASE_COL_MAP.put(11, COL_SHIP_TO);
	}

	public static HashMap<Integer, String> FRM_PICKTICKET_DETAIL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_PICKTICKET_DETAIL_COL_MAP.put(1, COL_PKT_CTRL_NBR);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(2, COL_CUST_PO_NBR);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(3, COL_WHSE);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(4, COL_ORD_TYPE);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(5, COL_TOTAL_NBR_OF_UNITS);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(6, COL_SHIPTO_STATE);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(7, COL_STAT_CODE);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(8, COL_ORD_DATE);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(9, COL_CREATE_DATE_TIME);
		FRM_PICKTICKET_DETAIL_COL_MAP.put(10, COL_RTE_GUIDE_NBR);
	}

	public static HashMap<Integer, String> FRM_ORDERSTATUS_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_ORDERSTATUS_COL_MAP.put(1, COL_SALES_ORDER_NO);
		FRM_ORDERSTATUS_COL_MAP.put(2, COL_STATUS);
	}

	public static HashMap<Integer, String> FRM_INV_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_INV_COL_MAP.put(1, COL_ITEM_ID);
		FRM_INV_COL_MAP.put(2, COL_SHORT_DESCRIPTION);
		FRM_INV_COL_MAP.put(3, COL_INV_TYPE);
		FRM_INV_COL_MAP.put(4, COL_SHIPNODE_KEY);
		FRM_INV_COL_MAP.put(5, COL_QUANTITY);
	}
	public static HashMap<Integer, String> FRM_INV_COL_MAP_EFC = new HashMap<Integer, String>();
	static {
		FRM_INV_COL_MAP_EFC.put(1, COL_ITEM_TYPE);
		FRM_INV_COL_MAP_EFC.put(2, COL_ACTIVE);
		FRM_INV_COL_MAP_EFC.put(3, COL_CASE);
		FRM_INV_COL_MAP_EFC.put(4, COL_TRANS);
		FRM_INV_COL_MAP_EFC.put(5, COL_CARTON);
		FRM_INV_COL_MAP_EFC.put(6, COL_UNALLOC_CASE);
		FRM_INV_COL_MAP_EFC.put(7, COL_UNALLOC_TRANS);
		FRM_INV_COL_MAP_EFC.put(8, COL_ALLOC_TOTAL);
		FRM_INV_COL_MAP_EFC.put(9, COL_UNALLOC_TOTAL);
	}

	public static HashMap<Integer, String> FRM_INV_COL_MAP_EFC_RED = new HashMap<Integer, String>();
	static {
		FRM_INV_COL_MAP_EFC_RED.put(1, COL_RED_SKU_ID);
		FRM_INV_COL_MAP_EFC_RED.put(2, COL_RED_SKU_DESC);
		FRM_INV_COL_MAP_EFC_RED.put(3, COL_RED_DSP_LOCN);
		FRM_INV_COL_MAP_EFC_RED.put(4, COL_RED_AREA);
		FRM_INV_COL_MAP_EFC_RED.put(5, COL_RED_LOC_TBF);
		FRM_INV_COL_MAP_EFC_RED.put(6, COL_RED_LOC_TBP);
		FRM_INV_COL_MAP_EFC_RED.put(7, COL_RED_LOC_OH);
	}

	public static HashMap<Integer, String> FRM_INV_COL_MAP_FS = new HashMap<Integer, String>();
	static {
		FRM_INV_COL_MAP_FS.put(1, COL_SHIPNODE);
		FRM_INV_COL_MAP_FS.put(2, COL_QUANTITY);
		FRM_INV_COL_MAP_FS.put(3, COL_EXPECTED);
		FRM_INV_COL_MAP_FS.put(4, COL_ON_ORDER);

	}

	public static HashMap<Integer, String> FRM_INV_AUDIT_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_INV_AUDIT_COL_MAP.put(1, COL_ITEM);
		FRM_INV_AUDIT_COL_MAP.put(2, COL_SHORT_DESCRIPTION);
		FRM_INV_AUDIT_COL_MAP.put(3, COL_SHIPNODE);
		FRM_INV_AUDIT_COL_MAP.put(4, COL_QUANTITY);
		FRM_INV_AUDIT_COL_MAP.put(5, COL_ON_HAND_QUANTITY);
		FRM_INV_AUDIT_COL_MAP.put(6, COL_TRANSACTION_TYPE);
		FRM_INV_AUDIT_COL_MAP.put(7, COL_REF_1);
		FRM_INV_AUDIT_COL_MAP.put(8, COL_REF_2);
		FRM_INV_AUDIT_COL_MAP.put(9, COL_REF_3);
		FRM_INV_AUDIT_COL_MAP.put(10, COL_REF_4);
		FRM_INV_AUDIT_COL_MAP.put(11, COL_SUPPLY_TYPE);
		FRM_INV_AUDIT_COL_MAP.put(12, COL_MODIFY_PROG_ID);
		FRM_INV_AUDIT_COL_MAP.put(13, COL_MODIFY_TS);
	}
	public static HashMap<Integer, String> FRM_SHIP_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_SHIP_COL_MAP.put(1, COL_SHIP_NO);
		FRM_SHIP_COL_MAP.put(2, COL_ORDER_NO);
		FRM_SHIP_COL_MAP.put(3, COL_TRACKING_NO);
		FRM_SHIP_COL_MAP.put(4, COL_ACTUAL_SHIPMENT_DATE);
		FRM_SHIP_COL_MAP.put(5, COL_EXPECTED_SHIPMENT_DATE);
		FRM_SHIP_COL_MAP.put(6, COL_PICK_TICKET_NO);
		FRM_SHIP_COL_MAP.put(7, COL_SCAC);
		FRM_SHIP_COL_MAP.put(8, COL_CARRIER_SERVICE_CODE);
		FRM_SHIP_COL_MAP.put(9, COL_SHIPNODE_KEY);
		FRM_SHIP_COL_MAP.put(10, COL_STATUS);
		FRM_SHIP_COL_MAP.put(11, COL_CONT_SCM);
		FRM_SHIP_COL_MAP.put(12, COL_RELEASE_NO);
		FRM_SHIP_COL_MAP.put(13, COL_PRIME_LINE_NO);
		FRM_SHIP_COL_MAP.put(14, COL_ITEM_ID);
		FRM_SHIP_COL_MAP.put(15, COL_ITEM_DESC);
		FRM_SHIP_COL_MAP.put(16, COL_QUANTITY);
		FRM_SHIP_COL_MAP.put(17, COL_CONT_GROSS_WT);
	}

	// Changes for Warehouse Transfer StoreElf Utility Screen - START
	public static HashMap<Integer, String> FRM_TRANSFER_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_TRANSFER_COL_MAP.put(1, COL_ORDER_NO);
		FRM_TRANSFER_COL_MAP.put(2, COL_ORDER_DATE);
		FRM_TRANSFER_COL_MAP.put(3, COL_STATUS);
		FRM_TRANSFER_COL_MAP.put(4, COL_STATUS_DATE);
		FRM_TRANSFER_COL_MAP.put(5, COL_TRANSFER_TYPE);
		FRM_TRANSFER_COL_MAP.put(6, COL_ITEM_COUNT);
		FRM_TRANSFER_COL_MAP.put(7, COL_TOTAL_UNITS);
		FRM_TRANSFER_COL_MAP.put(8, COL_SHIPPED_UNITS);
		FRM_TRANSFER_COL_MAP.put(9, COL_CANCELLED_UNITS);
		FRM_TRANSFER_COL_MAP.put(10, COL_UNIT_VARIANCE);
		FRM_TRANSFER_COL_MAP.put(11, COL_CREATED_BY);
	}
	public static HashMap<Integer, String> FRM_TRANSFER_ORDER_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_TRANSFER_ORDER_COL_MAP.put(1, COL_PRIME_LINE_NO);
		FRM_TRANSFER_ORDER_COL_MAP.put(2, COL_ITEM_ID);
		FRM_TRANSFER_ORDER_COL_MAP.put(3, COL_ITEM_DESC);
		FRM_TRANSFER_ORDER_COL_MAP.put(4, COL_EXTN_DEPT);
		FRM_TRANSFER_ORDER_COL_MAP.put(5, COL_CLASS);
		FRM_TRANSFER_ORDER_COL_MAP.put(6, COL_SUB_CLASS);
		FRM_TRANSFER_ORDER_COL_MAP.put(7, COL_LINE_STATUS);
		FRM_TRANSFER_ORDER_COL_MAP.put(8, COL_FROM_NODE);
		FRM_TRANSFER_ORDER_COL_MAP.put(9, COL_RECEIVING_NODE);
		FRM_TRANSFER_ORDER_COL_MAP.put(10, COL_TOTAL_UNITS);
		FRM_TRANSFER_ORDER_COL_MAP.put(11, COL_SHIPPED_UNITS);
		FRM_TRANSFER_ORDER_COL_MAP.put(12, COL_CANCELLED_UNITS);
		FRM_TRANSFER_ORDER_COL_MAP.put(13, COL_UNIT_VARIANCE);
		FRM_TRANSFER_ORDER_COL_MAP.put(14, COL_EXTN_PICK_TICKET_NO);

		FRM_TRANSFER_ORDER_COL_MAP.put(15, COL_SHIPMENT_DATE);
		FRM_TRANSFER_ORDER_COL_MAP.put(16, COL_SHIP_NO);
		FRM_TRANSFER_ORDER_COL_MAP.put(17, COL_SHIPMENT_LINE);
		FRM_TRANSFER_ORDER_COL_MAP.put(18, COL_BOL);
		FRM_TRANSFER_ORDER_COL_MAP.put(19, COL_UNITS);
		FRM_TRANSFER_ORDER_COL_MAP.put(20, COL_BATCH_NO);
		FRM_TRANSFER_ORDER_COL_MAP.put(21, COL_ITEM_RETAIL_PRICE);
		FRM_TRANSFER_ORDER_COL_MAP.put(22, COL_DISTRIBUTED_RETAIL_PRICE);
	}
	// Changes for Warehouse Transfer StoreElf Utility Screen - END
	// Added for Store Unit Capacity StoreElf Screen - START
	public static HashMap<Integer, String> FRM_STORE_CAPACITY_MAP = new HashMap<Integer, String>();
	static {
		FRM_STORE_CAPACITY_MAP.put(1, COL_NODE_KEY);
		FRM_STORE_CAPACITY_MAP.put(2, COL_UNIT_SOURCED_TODAY);
		FRM_STORE_CAPACITY_MAP.put(3, COL_SUNDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(4, COL_MONDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(5, COL_TUESDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(6, COL_WEDNESDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(7, COL_THURSDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(8, COL_FRIDAY_CAPACITY);
		FRM_STORE_CAPACITY_MAP.put(9, COL_SATURDAY_CAPACITY);

	}
	// Added for Store Unit Capacity StoreElf Screen - END
	//Changes for WMOS Utility Screen BEGIN
	public static HashMap<Integer, String> FRM_TRANSFER_SHIP_VIA = new HashMap<Integer, String>();
	static {
		FRM_TRANSFER_SHIP_VIA.put(1, COL_USER_ID);
		FRM_TRANSFER_SHIP_VIA.put(2, COL_MOD_DATE_TIME);
		FRM_TRANSFER_SHIP_VIA.put(3, COL_CREATE_DATE_TIME_SHIPVIA);
		FRM_TRANSFER_SHIP_VIA.put(4, COL_LABEL_TYPE);
		FRM_TRANSFER_SHIP_VIA.put(5, COL_SERV_TYPE);
		FRM_TRANSFER_SHIP_VIA.put(6, COL_CARR_ID);
		FRM_TRANSFER_SHIP_VIA.put(7, COL_SHIP_VIA_DESC);
		FRM_TRANSFER_SHIP_VIA.put(8, COL_SHIP_VIA);
	}



	public static HashMap<Integer, String> FRM_PKTHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_PKTHDR_COL_MAP.put(1, COL_PKT_CTRL_NBR_PKTHDR);
		FRM_PKTHDR_COL_MAP.put(2, COL_WHSE_PKTHDR);
		FRM_PKTHDR_COL_MAP.put(3, COL_ECOMM_ORD_PKTHDR);
		FRM_PKTHDR_COL_MAP.put(4, COL_TYPE);
		FRM_PKTHDR_COL_MAP.put(5, COL_PRTY_CODE);
		FRM_PKTHDR_COL_MAP.put(6, COL_ORD_DATE_PKTHDR);
		/*FRM_PKTHDR_COL_MAP.put(7, COL_SHIPTO_NAME);*/
		FRM_PKTHDR_COL_MAP.put(7, COL_TOTAL_UNITS_PKTHDR);
		FRM_PKTHDR_COL_MAP.put(8, COL_STATUS_PKTHDR);
	}

	public static HashMap<Integer, String> FRM_PKTDTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_PKTDTL_COL_MAP.put(1, COL_PKT_SEQ_NBR_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(2, COL_SKU_ID_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(3, COL_ORIG_QTY);
		FRM_PKTDTL_COL_MAP.put(4, COL_PKT_QTY_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(5, COL_CANC_QTY );
		FRM_PKTDTL_COL_MAP.put(6, COL_TO_BE_VERF);
		FRM_PKTDTL_COL_MAP.put(7, COL_VERF_PAKD);
		FRM_PKTDTL_COL_MAP.put(8, COL_UNITS_PAKD_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(9, COL_SPL_INSTR_CODE_2);
		FRM_PKTDTL_COL_MAP.put(10, COL_STATUS_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(11, COL_CONVEY_FLAG);
		FRM_PKTDTL_COL_MAP.put(12, COL_CHUTE_ASGN);
		FRM_PKTDTL_COL_MAP.put(13, COL_CARTON_TYPE_PKTDTL);
		FRM_PKTDTL_COL_MAP.put(14, COL_USER_ID_PKTDTL);
	}

	public static HashMap<Integer, String> FRM_CRTNHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_CRTNHDR_COL_MAP.put(1, COL_CARTON_NBR_CRTNHDR);
		FRM_CRTNHDR_COL_MAP.put(2, COL_MISC_INSTR_CODE_1 );
		FRM_CRTNHDR_COL_MAP.put(7, COL_SINGLES);
		FRM_CRTNHDR_COL_MAP.put(3, COL_DIVERT );
		FRM_CRTNHDR_COL_MAP.put(4, COL_CARTON_GRP_CODE);
		FRM_CRTNHDR_COL_MAP.put(5, COL_CHUTE_ASSIGN_TYPE);
		FRM_CRTNHDR_COL_MAP.put(6, COL_CHUTE_ID);
		//FRM_CRTNHDR_COL_MAP.put(7, COL_TOTAL_QTY);
		//FRM_CRTNHDR_COL_MAP.put(8, COL_LOAD_NBR);
	}

	public static HashMap<Integer, String> FRM_CRTNDTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_CRTNDTL_COL_MAP.put(1, COL_CARTON_NBR_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(2, COL_PKT_CTRL_NBR_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(3, COL_PKT_SEQ_NBR_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(4, COL_SKU_ID_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(5, COL_CARTON_SEQ_NBR_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(6, COL_TO_BE_PAKD_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(7, COL_PAKD_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(8, COL_LSTATUS);
		FRM_CRTNDTL_COL_MAP.put(9, COL_USER_ID_CRTNDTL);
		FRM_CRTNDTL_COL_MAP.put(10, COL_MOD_DATE_TIME_CRTNDTL);
	}

	public static HashMap<Integer, String> FRM_CRTNTYP_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_CRTNTYP_COL_MAP.put(1, COL_CARTON_NBR_CRTNTYP);
		FRM_CRTNTYP_COL_MAP.put(2, COL_CARTON_TYPE);
		FRM_CRTNTYP_COL_MAP.put(3, COL_CARTON_SIZE);
		FRM_CRTNTYP_COL_MAP.put(4, COL_ACTL_CNTR_VOL);
		FRM_CRTNTYP_COL_MAP.put(5, COL_MAX_CNTR_VOL);
		FRM_CRTNTYP_COL_MAP.put(6, COL_MAX_CNTR_WT);
		FRM_CRTNTYP_COL_MAP.put(7, COL_WIDTH);
		FRM_CRTNTYP_COL_MAP.put(8, COL_HT);
	}

	public static HashMap<Integer, String> FRM_MANHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_MANHDR_COL_MAP.put(1, COL_MANIF_NBR_MANHDR );
		FRM_MANHDR_COL_MAP.put(2, COL_MANIF_TYPE );
		FRM_MANHDR_COL_MAP.put(3, COL_CREATE_DATE_TIME_MANHDR );
		FRM_MANHDR_COL_MAP.put(4, COL_CLOSE_DATE );
		FRM_MANHDR_COL_MAP.put(5, COL_STATUS_MANHDR );
		FRM_MANHDR_COL_MAP.put(6, COL_PIKUP_REC_NBR );
	}


	public static HashMap<Integer, String> FRM_MANDTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_MANDTL_COL_MAP.put(1, COL_MANIF_NBR );
		FRM_MANDTL_COL_MAP.put(2, COL_CARTON_NBR_MANDTL );
		FRM_MANDTL_COL_MAP.put(3, COL_CREATE_DATE_TIME_MANDTL );
		FRM_MANDTL_COL_MAP.put(4, COL_USER_ID );
		FRM_MANDTL_COL_MAP.put(5, COL_STATUS_MANDTL );
	}

	public static HashMap<Integer, String> FRM_OPKTHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_OPKTHDR_COL_MAP.put(1, COL_PKT_CTRL_NBR_OPKTHDR );
		FRM_OPKTHDR_COL_MAP.put(2, COL_ECOMM_ORD );
		FRM_OPKTHDR_COL_MAP.put(3, COL_INVC_BATCH_NBR_OPKTHDR );
		FRM_OPKTHDR_COL_MAP.put(4, COL_CREATE_DATE_TIME_OPKTHDR );
		FRM_OPKTHDR_COL_MAP.put(5, COL_MOD_DATE_TIME_OPKTHDR );
		FRM_OPKTHDR_COL_MAP.put(6, COL_PROC_DATE_TIME_OPKTHDR );
		FRM_OPKTHDR_COL_MAP.put(7, COL_STATUS_OPKTHDR );
	}

	public static HashMap<Integer, String> FRM_OPKTDTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_OPKTDTL_COL_MAP.put(1, COL_PKT_SEQ_NBR_OPKTDTL );
		FRM_OPKTDTL_COL_MAP.put(2, COL_SKU_ID_OPKTDTL );
		FRM_OPKTDTL_COL_MAP.put(3, COL_ORIG_PKT_QTY_OPKTDTL);
		FRM_OPKTDTL_COL_MAP.put(4, COL_PKT_QTY);
		FRM_OPKTDTL_COL_MAP.put(5, COL_CANCEL_QTY);
		FRM_OPKTDTL_COL_MAP.put(6, COL_SHPD_QTY_OPKTDTL);
		FRM_OPKTDTL_COL_MAP.put(7, COL_CREATE_DATE_TIME_OPKTDTL);
	}

	public static HashMap<Integer, String> FRM_OCRTNHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_OCRTNHDR_COL_MAP.put(1, COL_INVC_BATCH_NBR_OCRTNHDR );
		FRM_OCRTNHDR_COL_MAP.put(2, COL_PKT_CTRL_NBR_OCRTNHDR );
		FRM_OCRTNHDR_COL_MAP.put(3, COL_CARTON_NBR_OCRTNHDR );
		FRM_OCRTNHDR_COL_MAP.put(4, COL_CREATE_DATE_TIME_OCRTNHDR );
		FRM_OCRTNHDR_COL_MAP.put(5, COL_MOD_DATE_TIME_OCRTNHDR );
		FRM_OCRTNHDR_COL_MAP.put(6, COL_PROC_DATE_TIME );
		FRM_OCRTNHDR_COL_MAP.put(7, COL_STATUS_OCRTNHDR );
	}

	public static HashMap<Integer, String> FRM_OCRTNDTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_OCRTNDTL_COL_MAP.put(1, COL_INVC_BATCH_NBR );
		FRM_OCRTNDTL_COL_MAP.put(2, COL_CARTON_NBR );
		FRM_OCRTNDTL_COL_MAP.put(3, COL_CARTON_SEQ_NBR );
		FRM_OCRTNDTL_COL_MAP.put(4, COL_PKT_CTRL_NBR_OCRTNDTL );
		FRM_OCRTNDTL_COL_MAP.put(5, COL_PKT_SEQ_NBR_OCRTNDTL );
		FRM_OCRTNDTL_COL_MAP.put(6, COL_SKU_ID_OCRTNDTL );
		FRM_OCRTNDTL_COL_MAP.put(7, COL_UNITS_PAKD );
		FRM_OCRTNDTL_COL_MAP.put(8, COL_MOD_DATE_TIME );
	}

	public static HashMap<Integer, String> FRM_CNCLS_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_CNCLS_COL_MAP.put(1, COL_PKT_CTRL_NBR_CNCLS);
		FRM_CNCLS_COL_MAP.put(2, COL_PKT_SEQ_NBR_CNCLS);
		FRM_CNCLS_COL_MAP.put(3, COL_SKU_ID_CNCLS);
		FRM_CNCLS_COL_MAP.put(4, COL_ORIG_PKT_QTY);
		FRM_CNCLS_COL_MAP.put(5, COL_SHPD_QTY);
		FRM_CNCLS_COL_MAP.put(6, COL_ORIGMINUSSHPD);
	}

	public static HashMap<Integer, String> FRM_INV_COL_MAP_WM = new HashMap<Integer, String>();
	static {
		FRM_INV_COL_MAP_WM.put(1, COL_PKT_CTRL_NBR_INV );
		FRM_INV_COL_MAP_WM.put(2, COL_PKT_SEQ_NBR );
		FRM_INV_COL_MAP_WM.put(3, COL_SKU_ID );
		FRM_INV_COL_MAP_WM.put(4, COL_SUM_UNITS_PAKD );

	}

	public static HashMap<Integer, String> FRM_CARTON_HEADER_COL_MAP = new HashMap<Integer, String>();
	static {
		 FRM_CARTON_HEADER_COL_MAP.put(1,COL_CARTON_NO);
		 FRM_CARTON_HEADER_COL_MAP.put(2,COL_CARTON_TYPE_CRTN);
		 FRM_CARTON_HEADER_COL_MAP.put(3,COL_CARTON_SINGLES);
		 FRM_CARTON_HEADER_COL_MAP.put(4,COL_CARTON_DIVERT);
		 FRM_CARTON_HEADER_COL_MAP.put(5,COL_CARTON_GRP);
		 FRM_CARTON_HEADER_COL_MAP.put(6,COL_CARTON_CHUTE_ASGN);
		 FRM_CARTON_HEADER_COL_MAP.put(7,COL_CARTON_CHUTE_ID);
	}

	public static HashMap<Integer, String> FRM_CARTON_DETAIL_COL_MAP = new HashMap<Integer, String>();
	static {
		 FRM_CARTON_DETAIL_COL_MAP.put(1,COL_CARTON_NO_DTL);
		 FRM_CARTON_DETAIL_COL_MAP.put(2,COL_CARTON_PKT_CTRL);
		 FRM_CARTON_DETAIL_COL_MAP.put(3,COL_CARTON_SEQ);
		 FRM_CARTON_DETAIL_COL_MAP.put(4,COL_CARTON_SKU);
		 FRM_CARTON_DETAIL_COL_MAP.put(5,COL_CARTON_TBP);
		 FRM_CARTON_DETAIL_COL_MAP.put(6,COL_CARTON_PAKD);
		 FRM_CARTON_DETAIL_COL_MAP.put(7,COL_CARTON_MODTIME);
		 FRM_CARTON_DETAIL_COL_MAP.put(8,COL_CARTON_USER_ID);
		 FRM_CARTON_DETAIL_COL_MAP.put(9,COL_CARTON_LINE);
		 FRM_CARTON_DETAIL_COL_MAP.put(10,COL_CARTON_STATUS);
	}
	public static HashMap<Integer, String> FRM_CARTON_PKTHDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_CARTON_PKTHDR_COL_MAP.put(1,COL_CARTON_CTRL_NBR);
		FRM_CARTON_PKTHDR_COL_MAP.put(2,COL_CARTON_CUST_PO);
		FRM_CARTON_PKTHDR_COL_MAP.put(3,COL_CARTON_ORD_TYPE);
		FRM_CARTON_PKTHDR_COL_MAP.put(4,COL_CARTON_STAT_CODE);
		FRM_CARTON_PKTHDR_COL_MAP.put(5,COL_CARTON_ORD_DATE);
	}

	public static HashMap<Integer, String> FRM_TASK_HDR_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_TASK_HDR_COL_MAP.put(1,COL_TASK_HDR_ID);
		FRM_TASK_HDR_COL_MAP.put(2,COL_TASK_CRE_TIME);
		FRM_TASK_HDR_COL_MAP.put(3,COL_TASK_MODTIME);
		FRM_TASK_HDR_COL_MAP.put(4,COL_TASK_USERID);
		FRM_TASK_HDR_COL_MAP.put(5,COL_TASK_STAT_CODE);
		FRM_TASK_HDR_COL_MAP.put(6,COL_TASK_STATUS);
	}
	public static HashMap<Integer, String> FRM_TASK_DTL_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_TASK_DTL_COL_MAP.put(1,COL_TASK_AID_ID);
		FRM_TASK_DTL_COL_MAP.put(2,COL_TASK_MOD_DATE_TIME);
		FRM_TASK_DTL_COL_MAP.put(3,COL_TASK_DEST_LOCN);
		FRM_TASK_DTL_COL_MAP.put(4,COL_TASK_NEED);
		FRM_TASK_DTL_COL_MAP.put(5,COL_TASK_SKU);
		FRM_TASK_DTL_COL_MAP.put(6,COL_TASK_QTY_ALLOC);
		FRM_TASK_DTL_COL_MAP.put(7,COL_TASK_QTY_PULLED);
		FRM_TASK_DTL_COL_MAP.put(8,COL_TASK_STAT_CODE);
		FRM_TASK_DTL_COL_MAP.put(9,COL_TASK_STATUS);
		FRM_TASK_DTL_COL_MAP.put(10,COL_TASK_USER_ID);

	}
	public static HashMap<Integer, String> FRM_TASK_ALLOCATION_COL_MAP = new HashMap<Integer, String>();
	static {
		// Allocation Inventory Detail(alloc_invn_dtl/sys_code)
		FRM_TASK_ALLOCATION_COL_MAP.put(1,COL_TASK_AID_ID2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(2,COL_TASK_CREATE_DATE_TIME );
		FRM_TASK_ALLOCATION_COL_MAP.put(3,COL_TASK_DEST_LOCN2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(4,COL_TASK_NEED2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(5,COL_TASK_SKU2);
		FRM_TASK_ALLOCATION_COL_MAP.put(6,COL_TASK_QTY_ALLOC2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(7,COL_TASK_QTY_PULLED2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(8,COL_TASK_STAT_CODE2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(9,COL_TASK_STATUS2 );
		FRM_TASK_ALLOCATION_COL_MAP.put(10,COL_TASK_PKT_NO );
	}
	public static HashMap<Integer, String> FRM_TASK_CASE_COL_MAP = new HashMap<Integer, String>();
	static {
		FRM_TASK_CASE_COL_MAP.put(1,COL_TASK_CASE_DSP);
		FRM_TASK_CASE_COL_MAP.put(2,COL_TASK_CASE_NBR);
		FRM_TASK_CASE_COL_MAP.put(3,COL_TASK_CASE_STAT);
		FRM_TASK_CASE_COL_MAP.put(4,COL_TASK_CASE_STATUS);
		FRM_TASK_CASE_COL_MAP.put(5,COL_TASK_CASE_DEST);
		FRM_TASK_CASE_COL_MAP.put(6,COL_TASK_CASE_SENT);
		FRM_TASK_CASE_COL_MAP.put(7,COL_TASK_CASE_DIVERTED);
	}

	//Changes for WMOS Utility Screen END

	/* Map to have form ID to column map */
	public static HashMap<String, HashMap<Integer, String>> FRM_RES_FLDS = new HashMap<String, HashMap<Integer, String>>();
	static {
		FRM_RES_FLDS.put(UTIL_FRM_ITEM, FRM_ITEM_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_ORDER, FRM_ORDER_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_ORDERSTATUS, FRM_ORDERSTATUS_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_ORDERRELEASE, FRM_ORDERRELEASE_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_INV, FRM_INV_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_INV_AUDIT, FRM_INV_AUDIT_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_SHIP, FRM_SHIP_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_EFC, FRM_INV_COL_MAP_EFC);
		FRM_RES_FLDS.put(UTIL_FRM_FS_INV, FRM_INV_COL_MAP_FS);
		FRM_RES_FLDS.put(UTIL_FRM_PICKTICKETS_DETAIL,					FRM_PICKTICKET_DETAIL_COL_MAP);
		// Changes for Warehouse Transfer StoreElf Utility Screen - START
		FRM_RES_FLDS.put(UTIL_FRM_TRANSFER, FRM_TRANSFER_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_TRANSFER_ORDER, FRM_TRANSFER_ORDER_COL_MAP);
		// Changes for Warehouse Transfer StoreElf Utility Screen - END
		// Added for Store Unit Capacity StoreElf Screen
		FRM_RES_FLDS.put(UTIL_STORE_CAPACITY, FRM_STORE_CAPACITY_MAP);

		//Changes to WMOS Utility Screen BEGIN
		FRM_RES_FLDS.put(UTIL_FRM_SHIPVIA, FRM_TRANSFER_SHIP_VIA);
		FRM_RES_FLDS.put(UTIL_FRM_PKTHDR, FRM_PKTHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_PKTDTL, FRM_PKTDTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CRTNHDR, FRM_CRTNHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CRTNDTL, FRM_CRTNDTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CRTNTYP, FRM_CRTNTYP_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_MANHDR, FRM_MANHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_MANDTL, FRM_MANDTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_OPKTHDR, FRM_OPKTHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_OPKTDTL, FRM_OPKTDTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_OCRTNHDR, FRM_OCRTNHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_OCRTNDTL, FRM_OCRTNDTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CNCLS, FRM_CNCLS_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_INV_WM, FRM_INV_COL_MAP_WM);
		FRM_RES_FLDS.put(UTIL_FRM_CARTON_HEADER, FRM_CARTON_HEADER_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CARTON_DETAIL, FRM_CARTON_DETAIL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_CARTON_PKTHDR, FRM_CARTON_PKTHDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_TASK_HDR, FRM_TASK_HDR_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_TASK_DTL, FRM_TASK_DTL_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_TASK_ALLOCATION, FRM_TASK_ALLOCATION_COL_MAP);
		FRM_RES_FLDS.put(UTIL_FRM_TASK_CASE, FRM_TASK_CASE_COL_MAP);
		//Changes to WMOS Utility Screen END
	}

	/* Map of all the Server Environments for Server Statistics */
	public static Map<String, String> SRV_ENV = new TreeMap<String, String>();
	static {
		SRV_ENV.put("PROD", "Production");
		SRV_ENV.put("STRESS", "Stress");
		SRV_ENV.put("QA_A", "QA A");
		SRV_ENV.put("QA_B", "QA B");
		SRV_ENV.put("QA_C", "QA C");
		SRV_ENV.put("QA_D", "QA D");
		SRV_ENV.put("TEST_A", "Test A");
		SRV_ENV.put("TEST_B", "Test B");
		SRV_ENV.put("TEST_C", "Test C");
	}
	
	
	//HERE
	//placeholders until the constants file is 100% clean
	public static Map<String,String> SQL_ID_MAP=new TreeMap<String, String>();
	public static Map<String,String> SQL_MODEL_MAP2=new TreeMap<String, String>();
	public static Map<String,String> SQL_INST_MAP=new TreeMap<String, String>();
	public static Map<String,String> SQL_QUERY_MAP=new TreeMap<String, String>();
	public static Map<String,String> SQL_DESC_MAP=new TreeMap<String, String>();
	public static Map<String,Long> SQL_TIME_MAP2=new TreeMap<String, Long>();

	
}