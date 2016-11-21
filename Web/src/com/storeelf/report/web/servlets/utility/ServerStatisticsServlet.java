/*<!--  Coding for the new storeelf Environment statistics page
@author: Shubham Ranka
@Date: 07/02/2014-->*/

package com.storeelf.report.web.servlets.utility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.agents.StoreElfRefreshSQLAgent;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.XProperties;

/**
 * Servlet implementation class OrderManagementServlet
 *
 * each *Response method MUST have the following types as arguments in this
 * order: ExampleResponse(String page, HttpServletRequest rq,
 * HttpServletResponse rs)
 *
 * @author tkmagh4
 * @web.servlet name=OrderManagementServlet
 */
public class ServerStatisticsServlet extends StoreElfHttpServlet<Object> {
	static final Logger logger = Logger
			.getLogger(ServerStatisticsServlet.class);
	private static final long serialVersionUID = 1L;
	private String defaultPage = "/utility_includes/utility.jsp";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServerStatisticsServlet() {
		super();
	}

	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page the POST must return
	 * json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */

	/** ------------App Servers Servlet------------------ **/
	 
	public void SQL_thread_status(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

//		String search_value = null;
//		Connection con = null;
//		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/server_statistics/SQL_thread_status.jsp";
		JsonArray root = null;

		try {
			root = new JsonArray();
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
//			Integer rowCount = 0;
			
			String SQL_TRIGGER = request.getParameter("SQL_TRIGGER");
			String SQL_ID = request.getParameter("SQL_ID");

			if ("POST".equals(request.getMethod())) {
				
				if(!com.storeelf.util.StringUtils.isVoid(SQL_TRIGGER) && !com.storeelf.util.StringUtils.isVoid(SQL_ID)){
					//trigger refresh
					if(Constants.STOREELF_SQL_REFRESH_JOBS.containsKey(SQL_ID)){
						//if currently flagged as running OR thread does not exist
						if(Constants.STOREELF_SQL_REFRESH_JOBS.get(SQL_ID).equals("RUNNING") || SQLUtils.getThreadStatus(SQL_ID)!=null){
							content = SQL_ID+" is already RUNNING";
						}else{
							Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, SQL_TRIGGER);
						}
					}else{
						Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, SQL_TRIGGER);
						content = SQL_ID+" is an unknown SQLID but StoreElf will run it anyway.";
					}
					responseWriter.write(content);
					responseWriter.flush();
					responseWriter.close();
				}else{
	//				String sqlid = Constants.ID_SERVER_STAT;
					//String env = "PROD";
					//String sqlid = "";
					//String sql_status = "";
					//TODO: extract time from modal object within map
					//String sql_execution_time = "";
	
					JsonObject sql_thread_object = new JsonObject();
					SQLModel model = null;
					Calendar next_execution_time_date = null;
					String next_execution_time_str = "-";
					long execution_waittime_ms = 0l;
					String RUNS_EVERY_TS = "";
	
					
					Thread thread = null;
	
					for(Entry<String, String> sql_thread: Constants.STOREELF_SQL_REFRESH_JOBS.entrySet()){
						model = SQLUtils.getModelObject(sql_thread.getKey());
						next_execution_time_date = Calendar.getInstance();
	
						sql_thread_object.addProperty("SQLID", model.getId());
						sql_thread_object.addProperty("LAST_EXECUTED", (model.getLastexecutetimestamp() !=null ? model.getLastexecutetimestamp().toString() : "-" ));
						sql_thread_object.addProperty("LAST_COMPLETED", (model.getLastresulttimestamp() !=null ? model.getLastresulttimestamp().toString() : "-"));
						
						if(model.getLastresulttimestamp() !=null && model.getLastexecutetimestamp() !=null){
							String date_difference = com.storeelf.util.DateUtils.getDateDifferenceFull(model.getLastexecutetimestamp(), model.getLastresulttimestamp());
							sql_thread_object.addProperty("SQL_EXECUTION_TIME", date_difference);
						}else{
							//String date_difference = com.storeelf.util.DateUtils.getDateDifferenceFull(model.getLastexecutetimestamp(), (new Date()));
							//sql_thread_object.addProperty("SQL_EXECUTION_TIME", date_difference);
						}
						
	
						execution_waittime_ms = Constants.SQL_TIME_MAP.get(model.getId());
						next_execution_time_date.setTimeInMillis( (model.getLastexecutetimestamp() !=null ? model.getLastexecutetimestamp().getTime() : 0l) + execution_waittime_ms );
	
						if(model.getLastexecutetimestamp() !=null){
							next_execution_time_date.setTimeInMillis( model.getLastexecutetimestamp().getTime() + execution_waittime_ms );
							next_execution_time_str = next_execution_time_date.getTime().toString();
						}
	
						sql_thread_object.addProperty("NEXT_EXECUTION_TS", next_execution_time_str);
	
	//					RUNS_EVERY_TS = String.format("%d min, %d sec",
	//						    TimeUnit.MILLISECONDS.toMinutes(Constants.SQL_TIME_MAP.get(model.getId())),
	//						    TimeUnit.MILLISECONDS.toSeconds(Constants.SQL_TIME_MAP.get(model.getId())) -
	//						    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Constants.SQL_TIME_MAP.get(model.getId())))
	//						);
	
						RUNS_EVERY_TS = String.format("%02d Hours | %02d Minutes | %02d Seconds",
							    TimeUnit.MILLISECONDS.toHours(Constants.SQL_TIME_MAP.get(model.getId())),
							    TimeUnit.MILLISECONDS.toMinutes(Constants.SQL_TIME_MAP.get(model.getId())) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Constants.SQL_TIME_MAP.get(model.getId()))),
							    TimeUnit.MILLISECONDS.toSeconds(Constants.SQL_TIME_MAP.get(model.getId())) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Constants.SQL_TIME_MAP.get(model.getId())))
							);
	
	
	
						sql_thread_object.addProperty("RUNS_EVERY_TS", RUNS_EVERY_TS);
						sql_thread_object.addProperty("SQL_STATUS", Constants.STOREELF_SQL_REFRESH_JOBS.get(model.getId()));
						sql_thread_object.addProperty("SUCCESS_DETAILS", Constants.STOREELF_SQL_JOBS_SUCCESSLOG.get(model.getId())+"");
						sql_thread_object.addProperty("ERROR_DETAILS", Constants.STOREELF_SQL_JOBS_ERRORLOG.get(model.getId())+"");
						
						/*if(Constants.STOREELF_SQL_STMT_MAP.containsKey(model.getId())){
							PreparedStatement ps = Constants.STOREELF_SQL_STMT_MAP.get(model.getId());
							
							sql_thread_object.addProperty("SQL_STATEMENT_CLOSED", ps.isClosed()+"");
							
							try{				sql_thread_object.addProperty("SQL_CONNECTION_CLOSED", ps.getConnection().isClosed()+"");}
							catch(Exception e){	sql_thread_object.addProperty("SQL_CONNECTION_CLOSED", "true");}
							
							try{				sql_thread_object.addProperty("SQL_CONNECTION_VALID", ps.getConnection().isValid(2)+"");}
							catch(Exception e){	sql_thread_object.addProperty("SQL_CONNECTION_VALID", "false");}	
						}*/
						
						try{
							thread = getThreadStatus(model.getId());
							
							sql_thread_object.addProperty("THREAD_STATUS", thread.getState().toString());
							sql_thread_object.addProperty("THREAD_ALIVE", thread.isAlive()+"");
						}
						catch(Exception e){
							sql_thread_object.addProperty("THREAD_STATUS", "-");
							sql_thread_object.addProperty("THREAD_ALIVE", "-");
						}
						root.add(sql_thread_object);
						sql_thread_object = new JsonObject();
					}
	
					/*for(Thread t:threadArray){
						sql_thread_object.addProperty("SQLID", t.getName());
						sql_thread_object.addProperty("STATUS", t.getState().toString());
						sql_thread_object = new JsonObject();
						root.add(sql_thread_object);
					}*/
	
					Gson gson = new GsonBuilder().create();
					content = gson.toJson(root);
					responseWriter.write(content);
					responseWriter.flush();
					responseWriter.close();
				}
			} else {
				// assume it's GET request, load JSP
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	
	public void set_thread_details(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");
		String SQL = request.getParameter("SQL");

		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				String previousSQL = Constants.SQL_MAP.get(SQL_ID);
				
				Constants.SQL_MAP.put(SQL_ID,SQL);
				
				responseWriter.write(SQL_ID+" SQL_ID has been changed from:");
				responseWriter.write("\n\r------------------------");
				responseWriter.write("\n\r"+previousSQL);
				responseWriter.write("\n\r------------------------");
				responseWriter.write("\n\rto: ");
				responseWriter.write("\n\r------------------------");
				responseWriter.write("\n\r"+SQL);
				responseWriter.write("\n\r------------------------");
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public void get_thread_details(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");

		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				
				responseWriter.write(Constants.SQL_MAP.get(SQL_ID));
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public void get_thread_list(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {

//		String search_value = null;
//		Connection con = null;
//		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;

		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				Gson gson = new GsonBuilder().create();
				ArrayList<HashMap<String,String>> threadList = new ArrayList<HashMap<String,String>>();
				HashMap<String, String> map = new HashMap<>();
				
				for(Thread thread :SQLUtils.getThreadStoreElfThreads()){
					map.put("NAME", thread.getName());
					map.put("ID", thread.getId()+"");
					map.put("PRIORITY", thread.getPriority()+"");
					map.put("CLASS", thread.getClass()+"");
					map.put("STACK", thread.getStackTrace()+"");
					//map.put("GROUP_NAME", thread.getThreadGroup().getName());
					//map.put("GROUP_COUNT", thread.getThreadGroup().activeCount()+"");
					map.put("STATE", thread.getState().name());
					threadList.add(map);
					map = new HashMap<>();
				}
				
				content = gson.toJson(threadList);
				
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	  
	public void get_db_connection_list(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

//		String search_value = null;
//		Connection con = null;
//		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		PreparedStatement ps = null;
		
		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				Gson gson = new GsonBuilder().create();
				
				ArrayList<HashMap<String,String>> connectionList = new ArrayList<HashMap<String,String>>();
				
				HashMap<String, String> map = new HashMap<>();
				
				String sql_id = "";
				
				for(Entry<String, PreparedStatement> connection :Constants.STOREELF_SQL_STMT_MAP.entrySet()){
					sql_id = connection.getKey();
					ps = connection.getValue();
					
					
					//map.put("NAME", thread.getName());
					map.put("ID", sql_id);
					map.put("STATEMENT_CLOSED", ps.isClosed()+"");
					try{				map.put("CONNECTION_CLOSED", ps.getConnection().isClosed()+"");}
					catch(Exception e){	map.put("CONNECTION_CLOSED", "true");}
					
					try{				map.put("CONNECTION_VALID", ps.getConnection().isValid(2)+"");}
					catch(Exception e){	map.put("CONNECTION_VALID", "false");}
					
					connectionList.add(map);
					map = new HashMap<>();
				}
				
				content = gson.toJson(connectionList);
				
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(ps!=null){ps.close();}
		}
	}
	
	
	public void kill_thread(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");

		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				
				Thread sql_thread = SQLUtils.getThreadStatus(SQL_ID);
				
				try{
					//try to interrupt the thread
					if(sql_thread!=null && sql_thread.isInterrupted()==false){
						sql_thread.interrupt();
					
						if(sql_thread.isInterrupted()){
							Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "ERROR");
							//add new thread to replace the old
							try{
								Thread	storeelfRefreshSQLAgent = new StoreElfRefreshSQLAgent((new Date()).getTime()+"");
										//storeelfRefreshSQLAgent.setDaemon(true);									 
								Constants.STOREELF_UTILITY_EXECUTOR_SERVICE.execute(storeelfRefreshSQLAgent);
							}catch(Exception e){
								responseWriter.write("error: unable to create new thread");
								logger.error("error: unable to create new thread", e);
							}
						}else{
							sql_thread.stop();
							responseWriter.write("warn: Thread interruption attempt may have failed, disabling anyway.");
							logger.error("warn: Thread interruption attempt may have failed, disabling anyway.");
							
							Thread	storeelfRefreshSQLAgent = new StoreElfRefreshSQLAgent((new Date()).getTime()+"");
							Constants.STOREELF_UTILITY_EXECUTOR_SERVICE.execute(storeelfRefreshSQLAgent);
						}
					}else{
						responseWriter.write("warn: No active threads with SQLID exist, disabling anyway.");
						logger.error("warn: No active threads with SQLID exist, disabling anyway.");
					}
				}catch(Exception e){
					responseWriter.write("error: unable to interrupt thread");
					logger.error("error: unable to interrupt thread", e);
				}
				
				
				responseWriter.write("done");
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public void modify_all_sql(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter responseWriter = null;
		String SQL_TRIGGER = request.getParameter("SQL_TRIGGER");
		
		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {				
				//Thread sql_thread = SQLUtils.getThreadStatus(SQL_ID);		
				String job_sql_id = "";
				
				//if(SQL_ID.equals("DISABLE_ALL_SQL")){
					for(Entry<String, String> job:Constants.STOREELF_SQL_REFRESH_JOBS.entrySet()){
						job_sql_id = job.getKey();
						 request.setAttribute("SQL_ID", job_sql_id);
						switch (SQL_TRIGGER) {
							case "CANCEL_ALL":	cancel_sql(requestedPage, request, response);	break;
							case "RETRY_ALL":	enable_sql(requestedPage, request, response);	break;
							case "ENABLE_ALL":	enable_sql(requestedPage, request, response);	break;
							case "DISABLE_ALL":	disable_sql(requestedPage, request, response);	break;
							case "FORCE_ALL":	force_sql(requestedPage, request, response);	break;
							default: break;
						}	
						
						/*try{
							if(Constants.STOREELF_SQL_STMT_MAP.containsKey(job_sql_id)){
								 PreparedStatement stmt = Constants.STOREELF_SQL_STMT_MAP.get(job_sql_id);
								 
								 if(stmt.isClosed()==false){
									 try{
										 stmt.cancel();
									 }catch(Exception e){
										 responseWriter.write("warn: unable to cancel SQL '"+job_sql_id+"'");
										logger.error("warn: unable to cancel SQL '"+job_sql_id+"'", e);
									 }
								 }else{
									 responseWriter.write("warn: unable to cancel SQL '"+job_sql_id+"', Statement Object closed. Will Disable anyway ");
								 }						 
							 }
							 Constants.STOREELF_SQL_REFRESH_JOBS.put(job_sql_id, SQL_TRIGGER);				 
						}catch(Exception e){
							responseWriter.write("warn: unable to cancel SQL '"+job_sql_id+"', not sure why. Check logs.");
							logger.error("warn: unable to cancel SQL '"+job_sql_id+"', not sure why. Check logs.", e);
						}*/
					}
				//}

				try{
					//try to interrupt the thread
//					if(sql_thread!=null && sql_thread.isInterrupted()==false){
//						sql_thread.interrupt();
//					
//						if(sql_thread.isInterrupted()){
//							Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "DISABLED");
//							//add new thread to replace the old
//							try{
//								Thread	storeelfRefreshSQLAgent = new StoreElfRefreshSQLAgent((new Date()).getTime()+"");
//										//storeelfRefreshSQLAgent.setDaemon(true);									 
//								Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE.execute(storeelfRefreshSQLAgent);
//							}catch(Exception e){
//								responseWriter.write("error: unable to create new thread");
//								logger.error("error: unable to create new thread", e);
//							}
//						}else{						
//							responseWriter.write("warn: Thread interruption attempt may have failed, disabling anyway.");
//							logger.error("warn: Thread interruption attempt may have failed, disabling anyway.");
//						}
//					}else{
//						responseWriter.write("warn: No active threads with SQLID exist, disabling anyway.");
//						logger.error("warn: No active threads with SQLID exist, disabling anyway.");
//					}
				}catch(Exception e){
					responseWriter.write("warn: unable to interrupt thread, disabling anyway.\n");
					logger.error("warn: unable to interrupt thread, disabling anyway.", e);
				}
				
				//Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "DISABLED");
				
				responseWriter.write(" Done");
				responseWriter.flush();
				responseWriter.close();
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public void cancel_sql(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");
		if(SQL_ID==null) SQL_ID = request.getAttribute("SQL_ID")+"";
		String SQL_TRIGGER_TYPE = request.getParameter("SQL_TRIGGER_TYPE");
		PreparedStatement stmt = null;
		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				
				try{
					 if(Constants.STOREELF_SQL_STMT_MAP.containsKey(SQL_ID)){
						 stmt = Constants.STOREELF_SQL_STMT_MAP.get(SQL_ID);
						 
						 if(stmt.isClosed()==false){
							 try{
								 stmt.cancel();
								 Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "ERROR");
							 }catch(Exception e){
								 responseWriter.write("warn: unable to cancel SQL");
								logger.error("warn: unable to cancel SQL", e);
							 }
						 }else{
							 responseWriter.write("warn: unable to cancel SQL, Statement Object closed ");
						 }
					 }
				}catch(Exception e){
					responseWriter.write("warn: unable to cancel SQL, not sure why. Check logs.");
					logger.error("warn: unable to cancel SQL, not sure why. Check logs.", e);
				}
				
				responseWriter.write(" Done");
				responseWriter.flush();
				if(SQL_TRIGGER_TYPE=="ALL"){}else{responseWriter.close();}
			}

		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}
	
	public void disable_sql(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {
		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");
		if(SQL_ID==null) SQL_ID = request.getAttribute("SQL_ID")+"";
		String SQL_TRIGGER_TYPE = request.getParameter("SQL_TRIGGER_TYPE");
		PreparedStatement stmt = null;
		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				
				//Thread sql_thread = SQLUtils.getThreadStatus(SQL_ID);				
				try{
					 if(Constants.STOREELF_SQL_STMT_MAP.containsKey(SQL_ID)){
						 stmt = Constants.STOREELF_SQL_STMT_MAP.get(SQL_ID);
						 
						 if(stmt.isClosed()==false){
							 try{
								 stmt.cancel();
							 }catch(Exception e){
								 responseWriter.write("warn: unable to cancel SQL");
								logger.error("warn: unable to cancel SQL", e);
							 }
						 }else{
							 responseWriter.write("warn: unable to cancel SQL, Statement Object closed. Will Disable anyway ");
						 }						 
					 }
				}catch(Exception e){
					responseWriter.write("warn: unable to cancel SQL, not sure why. Check logs.");
					logger.error("warn: unable to cancel SQL, not sure why. Check logs.", e);
				}
								
				try{
					//try to interrupt the thread
//					if(sql_thread!=null && sql_thread.isInterrupted()==false){
//						sql_thread.interrupt();
//					
//						if(sql_thread.isInterrupted()){
//							Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "DISABLED");
//							//add new thread to replace the old
//							try{
//								Thread	storeelfRefreshSQLAgent = new StoreElfRefreshSQLAgent((new Date()).getTime()+"");
//										//storeelfRefreshSQLAgent.setDaemon(true);									 
//								Constants.STOREELF_DASHBOARD_EXECUTOR_SERVICE.execute(storeelfRefreshSQLAgent);
//							}catch(Exception e){
//								responseWriter.write("error: unable to create new thread");
//								logger.error("error: unable to create new thread", e);
//							}
//						}else{						
//							responseWriter.write("warn: Thread interruption attempt may have failed, disabling anyway.");
//							logger.error("warn: Thread interruption attempt may have failed, disabling anyway.");
//						}
//					}else{
//						responseWriter.write("warn: No active threads with SQLID exist, disabling anyway.");
//						logger.error("warn: No active threads with SQLID exist, disabling anyway.");
//					}
				}catch(Exception e){
					responseWriter.write("warn: unable to interrupt thread, disabling anyway.");
					logger.error("warn: unable to interrupt thread, disabling anyway.", e);
				}
				
				
				Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "DISABLED");
				
				
				responseWriter.write(" Done");
				responseWriter.flush();
				if(SQL_TRIGGER_TYPE=="ALL"){}else{responseWriter.close();}
			}

		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(stmt!=null){stmt.close();}
		}
	}
	
	public void enable_sql(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter responseWriter = null;
		String SQL_ID = request.getParameter("SQL_ID");
		if(SQL_ID==null) SQL_ID = request.getAttribute("SQL_ID")+"";
		String SQL_TRIGGER_TYPE = request.getParameter("SQL_TRIGGER_TYPE");

		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, "SCHEDULED");				
				responseWriter.write("done");
				responseWriter.flush();
				if(SQL_TRIGGER_TYPE=="ALL"){}else{responseWriter.close();}
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	
	

	public void force_sql(String requestedPage, HttpServletRequest request,HttpServletResponse response){
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/server_statistics/SQL_thread_status.jsp";
		try {
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
//			Integer rowCount = 0;
			
			//String SQL_TRIGGER = request.getParameter("SQL_TRIGGER");
			String SQL_TRIGGER = "FORCE";
			String SQL_ID = request.getParameter("SQL_ID");
			if(SQL_ID==null) SQL_ID = request.getAttribute("SQL_ID")+"";
			String SQL_TRIGGER_TYPE = request.getParameter("SQL_TRIGGER_TYPE");

			if ("POST".equals(request.getMethod())) {				
				
					//trigger refresh
					if(Constants.STOREELF_SQL_REFRESH_JOBS.containsKey(SQL_ID)){
						//if currently flagged as running OR thread does not exist
						if(Constants.STOREELF_SQL_REFRESH_JOBS.get(SQL_ID).equals("RUNNING") || SQLUtils.getThreadStatus(SQL_ID)!=null){
							content = SQL_ID+" is already RUNNING";
						}else{
							Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, SQL_TRIGGER);
						}
					}else{
						Constants.STOREELF_SQL_REFRESH_JOBS.put(SQL_ID, SQL_TRIGGER);
						content = SQL_ID+" is an unknown SQLID but StoreElf will run it anyway.";
					}
					responseWriter.write(content);
					responseWriter.flush();
					if(SQL_TRIGGER_TYPE=="ALL"){}else{responseWriter.close();}
				
			} else {
				// assume it's GET request, load JSP
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	
		
	}
	
	public void SQL_connection_status(String requestedPage, HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

//		String search_value = null;
//		String sql = null;
		String content = "-error-";
		PrintWriter responseWriter = null;
		String jsp_include_page = "/utility_includes/server_statistics/SQL_connection_status.jsp";
		JsonArray root = null;
		Connection con = null;
		
		try {
			root = new JsonArray();
			responseWriter = response.getWriter();
			ReportActivator.server_instance =  "OMS";
			response.setContentType("application/json");
			if ("POST".equals(request.getMethod())) {
				JsonObject sql_connection_object = new JsonObject();

				String key = null;
				

				for(Entry<String, Connection> sql_connection: Constants.STOREELF_SQL_CONNECTIONS.entrySet()){
					key = sql_connection.getKey();
					con = sql_connection.getValue();

					if(con.isClosed()==false){
						sql_connection_object.addProperty("KEY0", key.split("__")[0]);
						sql_connection_object.addProperty("KEY2", key.split("__")[2]);
						sql_connection_object.addProperty("KEY3", key.split("__")[3]);
						root.add(sql_connection_object);
						sql_connection_object = new JsonObject();
					}
				}

				Gson gson = new GsonBuilder().create();
				content = gson.toJson(root);
				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			} else {
				// assume it's GET request, load JSP
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}

		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if(con!=null && !con.getMetaData().getURL().contains("k2ms2055")) {con.close();}
		}
	}

	public Thread getThreadStatus(String SQLID){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for(Thread t:threadArray){
			if(t.getName().contains(SQLID)) return t;
		}
		return null;
	}

	public void storeelfprop(String requestedPage, HttpServletRequest request,
		HttpServletResponse response) {


	
	String content = "-error-";
	PrintWriter responseWriter = null;
	String jsp_include_page = "/utility_includes/server_statistics/storeelfprop.jsp";
	HashMap<Integer, HashMap<String, Object>> result = null;
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	Integer rowCount = 0;

	try {
		result = new HashMap<Integer, HashMap<String, Object>>();
		responseWriter = response.getWriter();
		response.setContentType("application/json");
		if ("POST".equals(request.getMethod())) {
			
		XProperties	props	= ReportActivator.getInstance().getSystemProperties();
		//this.systemProperties.load(this.getClass().getResourceAsStream("storeelf.properties"));
		
			String OMS=props.getProperty("OMS.DB_INS");
			String OMSr=props.getProperty("OMSr.DB_INS");
			String OMSr_Training=props.getProperty("OMSr_Training.DB_INS");
			String GIV=props.getProperty("GIV.DB_INS");
			String EFC1=props.getProperty("EFC1.DB_INS");
			String EFC2=props.getProperty("EFC2.DB_INS");
			String EFC3=props.getProperty("EFC3.DB_INS");
			String EFC4=props.getProperty("EFC4.DB_INS");
			String EFC1_PRIM=props.getProperty("EFC1_PRIM.DB_INS");
			String EFC2_PRIM=props.getProperty("EFC2_PRIM.DB_INS");
			String EFC3_PRIM=props.getProperty("EFC3_PRIM.DB_INS");
			String EFC4_PRIM=props.getProperty("EFC4_PRIM.DB_INS");
			String PROSHIP=props.getProperty("PROSHIP.DB_INS");
			String FS=props.getProperty("FS.DB_INS");
			String MYSQL=props.getProperty("MYSQL.DB_INS");
			String STOREELF=props.getProperty("STOREELF.DB_INS");
			resultMap.put("OMS",OMS);
			resultMap.put("OMSr",OMSr);
			resultMap.put("OMSr_Training",OMSr_Training);
			resultMap.put("GIV",GIV);
			resultMap.put("EFC1",EFC1);
			resultMap.put("EFC2",EFC2);
			resultMap.put("EFC3",EFC3);
			resultMap.put("EFC4",EFC4);
			resultMap.put("EFC1_PRIM",EFC1_PRIM);
			resultMap.put("EFC2_PRIM",EFC2_PRIM);
			resultMap.put("EFC3_PRIM",EFC3_PRIM);
			resultMap.put("EFC4_PRIM",EFC4_PRIM);
			resultMap.put("PROSHIP",PROSHIP);
			resultMap.put("FS",FS);
			resultMap.put("MYSQL",MYSQL);
			resultMap.put("STOREELF",STOREELF);
			//oms db credantials
			
			
			String OMS_DB_USER=props.getProperty("OMS."+OMS+".DB_USER");
			String OMS_PORT=props.getProperty("OMS."+OMS+".DB_PORT");
			String OMS_DB_HOST=props.getProperty("OMS."+OMS+".DB_HOST");
			String OMS_DB_SERVICE_NAME=props.getProperty("OMS."+OMS+".DB_SERVICE_NAME");
			resultMap.put("OMS_DB_USER",OMS_DB_USER);
			resultMap.put("OMS_PORT",OMS_PORT);
			resultMap.put("OMS_DB_HOST",OMS_DB_HOST);
			resultMap.put("OMS_DB_SERVICE_NAME",OMS_DB_SERVICE_NAME);
			
			//OMSr_Training db credantials
			String OMSr_Training_DB_USER=props.getProperty("OMSr_Training."+OMSr_Training+".DB_USER");
			String OMSr_Training_PORT=props.getProperty("OMSr_Training."+OMSr_Training+".DB_PORT");
			String OMSr_Training_DB_HOST=props.getProperty("OMSr_Training."+OMSr_Training+".DB_HOST");
			String OMSr_Training_DB_SERVICE_NAME=props.getProperty("OMSr_Training."+OMSr_Training+".DB_SERVICE_NAME");
			if(OMSr_Training_DB_USER!=null)
				resultMap.put("OMSr_Training_DB_USER",OMSr_Training_DB_USER);
			else
				resultMap.put("OMSr_Training_DB_USER","NA");
			if(OMSr_Training_PORT!=null)
				resultMap.put("OMSr_Training_PORT",OMSr_Training_PORT);
			else
				resultMap.put("OMSr_Training_PORT","NA");
			if(OMSr_Training_DB_HOST!=null)
				resultMap.put("OMSr_Training_DB_HOST",OMSr_Training_DB_HOST);
			else
				resultMap.put("OMSr_Training_DB_HOST","NA");
			
			if(OMSr_Training_DB_SERVICE_NAME!=null)
				resultMap.put("OMSr_Training_DB_SERVICE_NAME",OMSr_Training_DB_SERVICE_NAME);
			else
				resultMap.put("OMSr_Training_DB_SERVICE_NAME","NA");
			
			
			
			//GIV db credantials
			String GIV_DB_USER=props.getProperty("GIV."+GIV+".DB_USER");
			String GIV_PORT=props.getProperty("GIV."+GIV+".DB_PORT");
			String GIV_DB_HOST=props.getProperty("GIV."+GIV+".DB_HOST");
			String GIV_DB_SERVICE_NAME=props.getProperty("GIV."+GIV+".DB_SERVICE_NAME");
			if(GIV_DB_USER!=null)
				resultMap.put("GIV_DB_USER",GIV_DB_USER);
			else
				resultMap.put("GIV_DB_USER","NA");
			if(GIV_PORT!=null)
				resultMap.put("GIV_PORT",GIV_PORT);
			else
				resultMap.put("GIV_PORT","NA");
			if(GIV_DB_HOST!=null)
				resultMap.put("GIV_DB_HOST",GIV_DB_HOST);
			else
				resultMap.put("GIV_DB_HOST","NA");
			if(GIV_DB_SERVICE_NAME!=null)
				resultMap.put("GIV_DB_SERVICE_NAME",GIV_DB_SERVICE_NAME);
			else
				resultMap.put("GIV_DB_SERVICE_NAME","NA");
			
			
			//EFC1 db credantials
			String EFC1_DB_USER=props.getProperty("EFC1."+EFC1+".DB_USER");
			String EFC1_PORT=props.getProperty("EFC1."+EFC1+".DB_PORT");
			String EFC1_DB_HOST=props.getProperty("EFC1."+EFC1+".DB_HOST");
			String EFC1_DB_SERVICE_NAME=props.getProperty("EFC1."+EFC1+".DB_SERVICE_NAME");
			if(EFC1_DB_USER!=null)
				resultMap.put("EFC1_DB_USER",EFC1_DB_USER);
			else
				resultMap.put("EFC1_DB_USER","NA");
			if(EFC1_PORT!=null)
				resultMap.put("EFC1_PORT",EFC1_PORT);
			else
				resultMap.put("EFC1_PORT","NA");
			if(EFC1_DB_HOST!=null)
				resultMap.put("EFC1_DB_HOST",EFC1_DB_HOST);
			else
				resultMap.put("EFC1_DB_HOST","NA");
			if(EFC1_DB_SERVICE_NAME!=null)
				resultMap.put("EFC1_DB_SERVICE_NAME",EFC1_DB_SERVICE_NAME);
			else
				resultMap.put("EFC1_DB_SERVICE_NAME","NA");
			
			
			//EFC2 db credantials
			String EFC2_DB_USER=props.getProperty("EFC2."+EFC2+".DB_USER");
			String EFC2_PORT=props.getProperty("EFC2."+EFC2+".DB_PORT");
			String EFC2_DB_HOST=props.getProperty("EFC2."+EFC2+".DB_HOST");
			String EFC2_DB_SERVICE_NAME=props.getProperty("EFC2."+EFC2+".DB_SERVICE_NAME");
			if(EFC2_DB_USER!=null)
				resultMap.put("EFC2_DB_USER",EFC2_DB_USER);
			else
				resultMap.put("EFC2_DB_USER","NA");
			if(EFC2_PORT!=null)
				resultMap.put("EFC2_PORT",EFC2_PORT);
			else
				resultMap.put("EFC2_PORT","NA");
			if(EFC2_DB_HOST!=null)
				resultMap.put("EFC2_DB_HOST",EFC2_DB_HOST);
			else
				resultMap.put("EFC2_DB_HOST","NA");
			if(EFC2_DB_SERVICE_NAME!=null)
				resultMap.put("EFC2_DB_SERVICE_NAME",EFC2_DB_SERVICE_NAME);
			else
				resultMap.put("EFC2_DB_SERVICE_NAME","NA");
			
			
			//EFC3 db credantials
			String EFC3_DB_USER=props.getProperty("EFC3."+EFC3+".DB_USER");
			String EFC3_PORT=props.getProperty("EFC3."+EFC3+".DB_PORT");
			String EFC3_DB_HOST=props.getProperty("EFC3."+EFC3+".DB_HOST");
			String EFC3_DB_SERVICE_NAME=props.getProperty("EFC3."+EFC3+".DB_SERVICE_NAME");
			if(EFC3_DB_USER!=null)
				resultMap.put("EFC3_DB_USER",EFC3_DB_USER);
			else
				resultMap.put("EFC3_DB_USER","NA");
			if(EFC3_PORT!=null)
				resultMap.put("EFC3_PORT",EFC3_PORT);
			else
				resultMap.put("EFC3_PORT","NA");
			if(EFC3_DB_HOST!=null)
				resultMap.put("EFC3_DB_HOST",EFC3_DB_HOST);
			else
				resultMap.put("EFC3_DB_HOST","NA");
			if(EFC3_DB_SERVICE_NAME!=null)
				resultMap.put("EFC3_DB_SERVICE_NAME",EFC3_DB_SERVICE_NAME);
			else
				resultMap.put("EFC3_DB_SERVICE_NAME","NA");
			
			
			
			//EFC4 db credantials
			String EFC4_DB_USER=props.getProperty("EFC4."+EFC4+".DB_USER");
			String EFC4_PORT=props.getProperty("EFC4."+EFC4+".DB_PORT");
			String EFC4_DB_HOST=props.getProperty("EFC4."+EFC4+".DB_HOST");
			String EFC4_DB_SERVICE_NAME=props.getProperty("EFC4."+EFC4+".DB_SERVICE_NAME");
			if(EFC4_DB_USER!=null)
				resultMap.put("EFC4_DB_USER",EFC4_DB_USER);
			else
				resultMap.put("EFC4_DB_USER","NA");
			if(EFC4_PORT!=null)
				resultMap.put("EFC4_PORT",EFC4_PORT);
			else
				resultMap.put("EFC4_PORT","NA");
			if(EFC4_DB_HOST!=null)
				resultMap.put("EFC4_DB_HOST",EFC4_DB_HOST);
			else
				resultMap.put("EFC4_DB_HOST","NA");
			if(EFC4_DB_SERVICE_NAME!=null)
				resultMap.put("EFC4_DB_SERVICE_NAME",EFC4_DB_SERVICE_NAME);
			else
				resultMap.put("EFC4_DB_SERVICE_NAME","NA");
			
			//EFC1_PRIM db credantials
			String EFC1_PRIM_DB_USER=props.getProperty("EFC1_PRIM."+EFC1_PRIM+".DB_USER");
			String EFC1_PRIM_PORT=props.getProperty("EFC1_PRIM."+EFC1_PRIM+".DB_PORT");
			String EFC1_PRIM_DB_HOST=props.getProperty("EFC1_PRIM."+EFC1_PRIM+".DB_HOST");
			String EFC1_PRIM_DB_SERVICE_NAME=props.getProperty("EFC1_PRIM."+EFC1_PRIM+".DB_SERVICE_NAME");
			if(EFC1_PRIM_DB_USER!=null)
				resultMap.put("EFC1_PRIM_DB_USER",EFC1_PRIM_DB_USER);
			else
				resultMap.put("EFC1_PRIM_DB_USER","NA");
			if(EFC1_PRIM_PORT!=null)
				resultMap.put("EFC1_PRIM_PORT",EFC1_PRIM_PORT);
			else
				resultMap.put("EFC1_PRIM_PORT","NA");
			if(EFC1_PRIM_DB_HOST!=null)
				resultMap.put("EFC1_PRIM_DB_HOST",EFC1_PRIM_DB_HOST);
			else
				resultMap.put("EFC1_PRIM_DB_HOST","NA");
			if(EFC1_PRIM_DB_SERVICE_NAME!=null)
				resultMap.put("EFC1_PRIM_DB_SERVICE_NAME",EFC1_PRIM_DB_SERVICE_NAME);
			else
				resultMap.put("EFC1_PRIM_DB_SERVICE_NAME","NA");
			
			
			//EFC2_PRIM db credantials
			String EFC2_PRIM_DB_USER=props.getProperty("EFC2_PRIM."+EFC2_PRIM+".DB_USER");
			String EFC2_PRIM_PORT=props.getProperty("EFC2_PRIM."+EFC2_PRIM+".DB_PORT");
			String EFC2_PRIM_DB_HOST=props.getProperty("EFC2_PRIM."+EFC2_PRIM+".DB_HOST");
			String EFC2_PRIM_DB_SERVICE_NAME=props.getProperty("EFC2_PRIM."+EFC2_PRIM+".DB_SERVICE_NAME");
			if(EFC2_PRIM_DB_USER!=null)
				resultMap.put("EFC2_PRIM_DB_USER",EFC2_PRIM_DB_USER);
			else
				resultMap.put("EFC2_PRIM_DB_USER","NA");
			if(EFC2_PRIM_PORT!=null)
				resultMap.put("EFC2_PRIM_PORT",EFC2_PRIM_PORT);
			else
				resultMap.put("EFC2_PRIM_PORT","NA");
			if(EFC2_PRIM_DB_HOST!=null)
				resultMap.put("EFC2_PRIM_DB_HOST",EFC2_PRIM_DB_HOST);
			else
				resultMap.put("EFC2_PRIM_DB_HOST","NA");
			if(EFC2_PRIM_DB_SERVICE_NAME!=null)
				resultMap.put("EFC2_PRIM_DB_SERVICE_NAME",EFC2_PRIM_DB_SERVICE_NAME);
			else
				resultMap.put("EFC2_PRIM_DB_SERVICE_NAME","NA");
			
			
			//EFC3_PRIM db credantials
			String EFC3_PRIM_DB_USER=props.getProperty("EFC3_PRIM."+EFC3_PRIM+".DB_USER");
			String EFC3_PRIM_PORT=props.getProperty("EFC3_PRIM."+EFC3_PRIM+".DB_PORT");
			String EFC3_PRIM_DB_HOST=props.getProperty("EFC3_PRIM."+EFC3_PRIM+".DB_HOST");
			String EFC3_PRIM_DB_SERVICE_NAME=props.getProperty("EFC3_PRIM."+EFC3_PRIM+".DB_SERVICE_NAME");
			if(EFC3_PRIM_DB_USER!=null)
				resultMap.put("EFC3_PRIM_DB_USER",EFC3_PRIM_DB_USER);
			else
				resultMap.put("EFC3_PRIM_DB_USER","NA");
			if(EFC3_PRIM_PORT!=null)
				resultMap.put("EFC3_PRIM_PORT",EFC3_PRIM_PORT);
			else
				resultMap.put("EFC3_PRIM_PORT","NA");
			if(EFC3_PRIM_DB_HOST!=null)
				resultMap.put("EFC3_PRIM_DB_HOST",EFC2_PRIM_DB_HOST);
			else
				resultMap.put("EFC3_PRIM_DB_HOST","NA");
			if(EFC3_PRIM_DB_SERVICE_NAME!=null)
				resultMap.put("EFC3_PRIM_DB_SERVICE_NAME",EFC3_PRIM_DB_SERVICE_NAME);
			else
				resultMap.put("EFC3_PRIM_DB_SERVICE_NAME","NA");
			
			
			
			//EFC4_PRIM db credantials
			String EFC4_PRIM_DB_USER=props.getProperty("EFC4_PRIM."+EFC4_PRIM+".DB_USER");
			String EFC4_PRIM_PORT=props.getProperty("EFC4_PRIM."+EFC4_PRIM+".DB_PORT");
			String EFC4_PRIM_DB_HOST=props.getProperty("EFC4_PRIM."+EFC4_PRIM+".DB_HOST");
			String EFC4_PRIM_DB_SERVICE_NAME=props.getProperty("EFC4_PRIM."+EFC4_PRIM+".DB_SERVICE_NAME");
			if(EFC4_PRIM_DB_USER!=null)
				resultMap.put("EFC4_PRIM_DB_USER",EFC4_PRIM_DB_USER);
			else
				resultMap.put("EFC4_PRIM_DB_USER","NA");
			if(EFC4_PRIM_PORT!=null)
				resultMap.put("EFC4_PRIM_PORT",EFC4_PRIM_PORT);
			else
				resultMap.put("EFC4_PRIM_PORT","NA");
			if(EFC4_PRIM_DB_HOST!=null)
				resultMap.put("EFC4_PRIM_DB_HOST",EFC4_PRIM_DB_HOST);
			else
				resultMap.put("EFC4_PRIM_DB_HOST","NA");
			if(EFC4_PRIM_DB_SERVICE_NAME!=null)
				resultMap.put("EFC4_PRIM_DB_SERVICE_NAME",EFC4_PRIM_DB_SERVICE_NAME);
			else
				resultMap.put("EFC4_PRIM_DB_SERVICE_NAME","NA");
			
			
			//PROSHIP db credantials
			String PROSHIP_DB_USER=props.getProperty("PROSHIP."+ PROSHIP+".DB_USER");
			String PROSHIP_PORT=props.getProperty("PROSHIP."+PROSHIP+".DB_PORT");
			String PROSHIP_DB_HOST=props.getProperty("PROSHIP."+PROSHIP+".DB_HOST");
			String PROSHIP_DB_SERVICE_NAME=props.getProperty("PROSHIP."+PROSHIP+".DB_SERVICE_NAME");
			if(PROSHIP_DB_USER!=null)
				resultMap.put("PROSHIP_DB_USER",PROSHIP_DB_USER);
			else
				resultMap.put("PROSHIP_DB_USER","NA");
			if(PROSHIP_PORT!=null)
				resultMap.put("PROSHIP_PORT",PROSHIP_PORT);
			else
				resultMap.put("PROSHIP_PORT","NA");
			if(PROSHIP_DB_HOST!=null)
				resultMap.put("PROSHIP_DB_HOST",PROSHIP_DB_HOST);
			else
				resultMap.put("PROSHIP_DB_HOST","NA");
			if(PROSHIP_DB_SERVICE_NAME!=null)
				resultMap.put("PROSHIP_DB_SERVICE_NAME",PROSHIP_DB_SERVICE_NAME);
			else
				resultMap.put("PROSHIP_DB_SERVICE_NAME","NA");
			
			//FS db credantials
			String FS_DB_USER=props.getProperty("FS."+ FS+".DB_USER");
			String FS_PORT=props.getProperty("FS."+FS+".DB_PORT");
			String FS_DB_HOST=props.getProperty("FS."+FS+".DB_HOST");
			String FS_DB_SERVICE_NAME=props.getProperty("FS."+FS+".DB_SERVICE_NAME");
			if(FS_DB_USER!=null)
				resultMap.put("FS_DB_USER",FS_DB_USER);
			else
				resultMap.put("FS_DB_USER","NA");
			if(FS_PORT!=null)
				resultMap.put("FS_PORT",FS_PORT);
			else
				resultMap.put("FS_PORT","NA");
			if(FS_DB_HOST!=null)
				resultMap.put("FS_DB_HOST",FS_DB_HOST);
			else
				resultMap.put("FS_DB_HOST","NA");
			if(FS_DB_SERVICE_NAME!=null)
				resultMap.put("FS_DB_SERVICE_NAME",FS_DB_SERVICE_NAME);
			else
				resultMap.put("FS_DB_SERVICE_NAME","NA");
			
			
			//MYSQL db credantials
			String MYSQL_DB_USER=props.getProperty("MYSQL."+ MYSQL+".DB_USER");
			String MYSQL_PORT=props.getProperty("MYSQL."+MYSQL+".DB_PORT");
			String MYSQL_DB_HOST=props.getProperty("MYSQL."+MYSQL+".DB_HOST");
			String MYSQL_DB_SERVICE_NAME=props.getProperty("MYSQL."+MYSQL+".DB_SERVICE_NAME");
			if(MYSQL_DB_USER!=null)
				resultMap.put("MYSQL_DB_USER",MYSQL_DB_USER);
			else
				resultMap.put("MYSQL_DB_USER","NA");
			if(MYSQL_PORT!=null)
				resultMap.put("MYSQL_PORT",MYSQL_PORT);
			else
				resultMap.put("MYSQL_PORT","NA");
			if(MYSQL_DB_HOST!=null)
				resultMap.put("MYSQL_DB_HOST",MYSQL_DB_HOST);
			else
				resultMap.put("MYSQL_DB_HOST","NA");
			if(MYSQL_DB_SERVICE_NAME!=null)
				resultMap.put("MYSQL_DB_SERVICE_NAME",MYSQL_DB_SERVICE_NAME);
			else
				resultMap.put("MYSQL_DB_SERVICE_NAME","NA");
			
			
			//STOREELF db credantials
			String STOREELF_DB_USER=props.getProperty("STOREELF."+ STOREELF+".DB_USER");
			String STOREELF_PORT=props.getProperty("STOREELF."+STOREELF+".DB_PORT");
			String STOREELF_DB_HOST=props.getProperty("STOREELF."+STOREELF+".DB_HOST");
			String STOREELF_DB_SERVICE_NAME=props.getProperty("STOREELF."+STOREELF+".DB_SERVICE_NAME");
			if(STOREELF_DB_USER!=null)
				resultMap.put("STOREELF_DB_USER",STOREELF_DB_USER);
			else
				resultMap.put("STOREELF_DB_USER","NA");
			if(STOREELF_PORT!=null)
				resultMap.put("STOREELF_PORT",STOREELF_PORT);
			else
				resultMap.put("STOREELF_PORT","NA");
			if(STOREELF_DB_HOST!=null)
				resultMap.put("STOREELF_DB_HOST",STOREELF_DB_HOST);
			else
				resultMap.put("STOREELF_DB_HOST","NA");
			if(STOREELF_DB_SERVICE_NAME!=null)
				resultMap.put("STOREELF_DB_SERVICE_NAME",STOREELF_DB_SERVICE_NAME);
			else
				resultMap.put("STOREELF_DB_SERVICE_NAME","NA");
			
			
			System.out.println("resultMap==>"+resultMap);
			
			result.put(rowCount, resultMap);
				
			
			Gson gson = new GsonBuilder().create();
			content = gson.toJson(result);
			System.out.println("content==>"+content);
			responseWriter.write(content);
			responseWriter.flush();
			responseWriter.close();
		} else {
			// assume it's GET request, load JSP
			request.getRequestDispatcher(
					defaultPage + "?include=" + jsp_include_page).forward(
					request, response);
		}

	} catch (Exception e) {
		logger.error("error", e);
	}
}

}
