package com.storeelf.report.web.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.StoreElfSearchHistoryEntityModel;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.report.web.security.StoreElfCache;
import com.storeelf.report.web.security.StoreElfCacheManager;
import com.storeelf.util.XProperties;



/**
 * This custom servlet will serve as a top-level processing servlet to handle ALL request(s), response(s) and session(s)
 *
 *
 * @author tkmagh4
 * @version 1.0
 * @param <T>
 */
@MultipartConfig
public class StoreElfHttpServlet<T> extends HttpServlet{
	private final String default_page					= "/index.jsp";
	//private final String error_page						= "/error.jsp";
	private final String error_500_page					= "/500.jsp";
	private final String error_404_page					= "/404.jsp";
	private final String access_denied_page				= "/access-denied.jsp";
	private static final long serialVersionUID			= 1L;
	static final Logger logger							= Logger.getLogger(StoreElfHttpServlet.class);
	public HashMap<String, String> contextParameters	= new HashMap<String, String>();

    /**
     * @see HttpServlet#HttpServlet()
     * @author tkmagh4
     */
    public StoreElfHttpServlet() {
        super();
    }

    /**
     *
     * This method's first argument will always accept generic Types to assure ALL servlet class types are processed
     *
     * @param c
     * @param request
     * @param response
     * @author tkmagh4
     */
    public void process(HttpServletRequest request, HttpServletResponse response){
    	Object servletInstance				= null;
		Map<?, ?>		requestParameterMap	= null;
		boolean			invokedSuccessfully = false;

		String requestedPage				= "";
		String req_servlet					= "";
		String req_section 					= "";
		String req_component_name			= "";
		String req_module_name				= "";
		Class<?> servletClass				= null;
		JsonObject 	upTime = new JsonObject();
		JsonArray   upTime_array = new JsonArray();
		JsonObject		rootReturn  			= new JsonObject();
		String			content 			= "-error-";
		PrintWriter		response_Writer	= null;
		Gson 			gsonn	         		= new GsonBuilder().create();
		

		try{
			//[servlet]/[section]/[component_name]/[module_name]
			String[]	REQ_URI		= StringUtils.split(request.getRequestURI(), '/');

			request.setAttribute("SERVLET_CONTEXT"	, getServletContext());
			request.setAttribute("REPORTS_PATH"		, getServletContext().getRealPath("Reports"));

			//map URI segments
			for(int i=0;i<REQ_URI.length;i++){
				switch (i) {
					case 0: req_section				= (REQ_URI[i] != null) ? REQ_URI[i] : " "; break;
					case 1: req_component_name		= (REQ_URI[i] != null) ? REQ_URI[i] : " "; break;
					case 2: req_module_name			= (REQ_URI[i] != null) ? REQ_URI[i] : " "; break;
				default: break;
				}
			}
			
			
			
			//response.setHeader("X-Powered-By","Your mom");
			response.setHeader("X-Powered-By","Logistics Service Delivery");
			response.setHeader("X-Frame-Options", "SAMEORIGIN");
			

			logger.debug("----------------------------------------------");
			logger.debug("StringUtils.equalsIgnoreCase:: " +StringUtils.equalsIgnoreCase(req_section, getServletContext().getContextPath().substring(1)));
			logger.debug("-:getServletContext().getContextPath().substring(1): "+getServletContext().getContextPath().substring(1));
			logger.debug("-:getServletContext().getContextPath(): " 		+ getServletContext().getContextPath());
			logger.debug("--: request.getRequestURI()"+request.getRequestURI());
			logger.debug("1:req_section: " 			+ req_section);
			logger.debug("2:req_component_name: "	+ req_component_name);
			logger.debug("3:req_module_name: " 		+ req_module_name);
			logger.debug("----------------------------------------------");

			logger.debug("SESSION ID: "			+ request.getSession().getId());
			logger.debug("SESSION CREATE: "		+ request.getSession().getCreationTime());
			logger.debug("SESSION LAST ACCESS: "+ request.getSession().getLastAccessedTime());

			Subject	currentUser							= SecurityUtils.getSubject();


			/************************************************************
			 * START SUBJECT READ AUTHORIZATION
			 * here we only validate the read permissions, let's pass the write/update/delete permissions to the servlets
			 */
				boolean hasReadPermission		= currentUser.isPermitted(StringUtils.lowerCase(req_section)+':'+"read"+":"+StringUtils.lowerCase(req_component_name)+":"+org.apache.commons.lang3.StringUtils.lowerCase(req_module_name)+"");
				logger.debug("REQUESTING_PERMISSION: '"+StringUtils.lowerCase(req_section)+':'+"read"+":"+StringUtils.lowerCase(req_component_name)+":"+org.apache.commons.lang3.StringUtils.lowerCase(req_module_name)+"'");
				logger.debug("isAuthorizationEnabled: "+ isAuthorizationEnabled());
				logger.debug("HAS_READ_PERMISSION: "+ hasReadPermission);
			/**
			 * END SUBJECT READ AUTHORIZATION
			 ***************************************************************/
			
			/************************************************************************
			 * Change storeelf.properties property 'STOREELF.IS_AUTHORIZATION_ENABLED' 
			 * value to N to disable authorization
			 **/
				
			
				/**
				 * StoreElf Timer Code value calculation
				 */
					
					String time="";
					RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
					long ms = rb.getUptime();
					long days=0;
					long hr=0;
					long min=0;
					long sec=0;
					
					sec= (ms/1000);
					
					if(sec>=86400)
					{
						days=sec/86400;
						sec=sec%86400;
						if(days>1)
						time=time+days+" days ";
						else
							time=time+days+" day ";
					}
					if(sec>=3600)
					{
						hr=sec/3600;
						sec=sec%3600;
						if(hr>1)
						{
						time=time+hr+" hrs ";
						}
						else
						{
							time=time+hr+" hr ";
						}
					}
					if(sec>=60)
					{
						min=sec/60;
						sec=sec%60;
						if(min>1)
						{
						time=time+min+" mins ";
						}
						else
						{
							time=time+min+" min ";
						}
					}
					time=time+sec+" sec";
					
					if(time.equalsIgnoreCase(""))
					{
						time="0 sec";
					}					
					ServletContext application = getServletConfig().getServletContext();
					application.setAttribute("TimeUp", time);
					
				//if authr is disabled, grant permissions
			if(isAuthorizationEnabled()==false)		hasReadPermission=true;
			
			//HOOK: if requesting access to public directory, grant access
			if(StringUtils.equalsIgnoreCase(req_component_name, "public")) hasReadPermission=true;
			
			//HOOK: return session object if requested
			if(StringUtils.equals(req_component_name, "SESSION")){
				DefaultSecurityManager				scm				= ((DefaultWebSecurityManager)SecurityUtils.getSecurityManager());
				StoreElfCacheManager<?, ?>	lcm				= ((StoreElfCacheManager<?, ?>)scm.getCacheManager());
				StoreElfCache							cache			= lcm.getCache("SHIRO_STOREELF_SESSION_MAP");
				Object								cacheObject 	= null;						
				byte[]								returnObject	= null;
				
				if(cache!=null)	cacheObject = cache.get(req_module_name);
				
				if(cacheObject!=null){
					if(cacheObject instanceof SimpleSession){
						returnObject = SerializationUtils.serialize((SimpleSession)cacheObject);
					}
				}else{
					returnObject = "!".getBytes();
				}
				
				ServletOutputStream	responseOutputStream = response.getOutputStream();
				responseOutputStream.write(returnObject);
				responseOutputStream.flush();
				responseOutputStream.close();						
			}else if(StringUtils.equals(req_component_name, "HAZELCAST")){
				HazelcastInstance		hazelcastInstance	= Hazelcast.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME);
				PrintWriter responseWriter = response.getWriter();
				Gson gson = new GsonBuilder().create();
				String response_content = "-error-";
				
				IMap<Object, Object> STOREELF_SESSIONS			= hazelcastInstance.getMap("STOREELF_SESSIONS");
				
				Object sessionid = request.getParameter("sessionid");
				
				switch (req_module_name){
				case "member":
					response_content = hazelcastInstance.getCluster().getLocalMember()+"";							
					break;
				case "members":
					response_content = hazelcastInstance.getCluster().getMembers()+"";
					break;
				case "cluster":
					response_content = hazelcastInstance.getCluster()+"";
					break;
				case "config":
					response_content = hazelcastInstance.getConfig()+"";
					break;
				case "session":
					SimpleSession session = (SimpleSession) STOREELF_SESSIONS.get(sessionid);
					response_content = (session!=null) ? session+"" : "NOT FOUND";
					break;
				default: 
					break;
				}
				
				responseWriter.write(response_content);
				responseWriter.flush();
				responseWriter.close();
				invokedSuccessfully=true;
			}
				
			if(hasReadPermission){
				String ip_addr			= getClientIpAddr(request);
				String username			= currentUser.getPrincipal()+"";
				String threadName		= Thread.currentThread().getName();
				
				//thread name will mark section, user and remote address.
				String newThreadName	= (threadName.contains("__")) ? threadName.split("__")[0]+"__"+req_section+"_"+username+"_"+ip_addr : threadName+"__"+req_section+"_"+username+"_"+ip_addr;
						
				//set thread the name of the currentUser
				Thread.currentThread().setName(newThreadName);
				
				//HOOK: validate the servlet and sections
				if(StringUtils.isNotBlank(req_section)){
					
					/********************************************************************************************
					 * Start HOOKS
					 **/
					if(StringUtils.equals(req_component_name, "LOGGER")){				
						switch (req_module_name) {
							case "ALL": 	logger.setLevel(Level.ALL);		break;
							case "DEBUG":	logger.setLevel(Level.DEBUG);	break;
							case "ERROR":	logger.setLevel(Level.ERROR);	break;
							case "FATAL":	logger.setLevel(Level.FATAL);	break;
							case "INFO":	logger.setLevel(Level.INFO);	break;
							case "OFF":		logger.setLevel(Level.OFF);		break;
							case "TRACE":	logger.setLevel(Level.TRACE);	break;
							case "WARN":	logger.setLevel(Level.WARN);	break;
							default:	break;
						}
					}else if(StringUtils.equals(req_component_name, "CONSTANTS")){
						//This HOOK sets ANY 'Constants' variable that's String/Long/Int/Boolean
						try{
							String[] kv_entry = req_module_name.split("=");							
							String field_name	= kv_entry[0];
							String field_value	= kv_entry[1];
							
							Field  field		= Constants.class.getDeclaredField(field_name);
							String field_type	= Constants.class.getDeclaredField(field_name).getType().getName();
							
							if(field_type == "java.lang.String")	field.set(com.storeelf.report.web.Constants.class, field_value);
							else if(field_type == "long")			field.set(com.storeelf.report.web.Constants.class, Long.parseLong(field_value));
							else if(field_type == "int")			field.set(com.storeelf.report.web.Constants.class, Integer.parseInt(field_value));
							else if(field_type == "boolean")		field.set(com.storeelf.report.web.Constants.class, Boolean.parseBoolean(field_value));
							
							//kv_entry[0]
							//Constants.STOREELF_SQL_TIMEOUT_MINUTES
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					/**
					 * Stop HOOKS
					 ********************************************************************************************/
					
						//if the request is for a static file witin the 'public' directory, forward to our custom handler
						if(StringUtils.equalsIgnoreCase(req_component_name, "public")==true || request.getRequestURI().endsWith("ico")){
							logger.debug("Loading static file: "+request.getRequestURI());
							boolean isStaticFile = getPublic(request, response);
							if(isStaticFile) return;
						}/*else if(request.getRequestURI().endsWith("jsp")){
							routeToJSP(request, response, request.getRequestURI());
							return;
						}*/else if(req_component_name.startsWith(";")==true){
							req_component_name="";
						}
						
						if(StringUtils.isNotBlank(req_component_name)){
							
							//your package should have a lowercase name(which follows java "standards"), if it does NOT this will NOT work
							String className = "com.storeelf.report.web.servlets."+(req_section.toLowerCase());
			
							/**********************************************************************************************************************
							 * the true power of StoreElfHttpServlet would be this line of code, this IS THEE MOST IMPORTANT line of code in StoreElf
							 **********************************************************************************************************************/
							try{
								servletClass = Class.forName(className + "." + (req_component_name+"Servlet"));
							}catch(ClassNotFoundException e)	{logger.trace("error returning servlet class object for name ("+className + "." + (req_component_name+"Servlet")+" : ClassNotFoundException", e);
							}catch(Exception e)					{logger.error("error returning servlet class object : Exception", e);}
							
							/*********************************************************************************************************************/
			
							logger.debug("requested URI: "+"com.storeelf.report.web.servlets."+(req_section.toLowerCase())+"."+req_component_name+"Servlet");
							logger.debug("requested URI: "+request.getRequestURI());
							logger.debug("requested URL: "+request.getRequestURL());
							logger.debug("servlet path : "+req_servlet);
							logger.debug("URI : " + req_section+"/"+req_component_name+"/"+req_module_name);
			
							//do stuff with the parameters
							requestParameterMap	= request.getParameterMap();
			
							logger.debug("StoreElfHttpServlet:"+requestParameterMap.size()+":"+requestParameterMap.containsKey("module")+": 2");
			
							requestedPage = (request.getParameter("module") == null) ? req_module_name : request.getParameter("module");
							//saveSearchMap(request, (HashMap<String, Object>) requestParameterMap);
			
								//cycle through available page attributes to determine response method
								if(StringUtils.isNotBlank(requestedPage)){
			
									try{
										//validate section, if null forward to error page
										if(servletClass!=null){
											//check if this request is a static file request
											/*getPublic(request, response);
			
											//give up
											error_500(request, response, requestedPage);
										}else{*/
											servletInstance = servletClass.newInstance();
			
											response.setHeader("Access-Control-Allow-Origin", "*");
											response.setHeader("Access-Control-Allow-Headers", "Range");
											response.setHeader("Access-Control-Expose-Headers", "Cache-Control, Content-Encoding, Content-Range");
											logger.debug("requestedPage: "+requestedPage);
								         									         	
											servletClass.getMethod(requestedPage, String.class, HttpServletRequest.class, HttpServletResponse.class)
											.invoke(servletInstance , requestedPage, request, response);
									
								         	invokedSuccessfully	= true;
										}
										servletInstance		= null;
									}
									catch(IllegalAccessException e)		{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': IllegalAccessException", e);}
									catch(IllegalArgumentException e)	{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
									catch(InvocationTargetException e)	{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': InvocationTargetException", e);}
									catch(NoSuchMethodException e)		{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': NoSuchMethodException", e);}
									catch(SecurityException e)			{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': SecurityException", e);}
									catch(NullPointerException e)		{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': NullPointerException", e);}
									catch(RuntimeException e)			{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': RuntimeException", e);}
									catch(InstantiationException e)		{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': InstantiationException", e);}
									catch(Exception e)					{logger.error("error invoking servlet class-method/component-module for: '"+request.getRequestURI()+"': Exception", e);}
			
									//something went horribly wrong above, PANIC!!!
									if(invokedSuccessfully==false){
										error_404(request, response, requestedPage);
									}
								}
						}else{
							/** no component name has been provided **/
			
							//no servlet name provided, this shouldn't matter either way as tomcat handles this.
							if(StringUtils.isNotBlank(req_servlet)){
								//call the section's default page
								request.getRequestDispatcher("/"+req_section.toLowerCase()+"_includes/"+req_section.toLowerCase()+".jsp").forward(request, response);
							}else{
								error_404(request, response, requestedPage);
							}
						}
				}else{
					//no include found, TODO add error to user ("'page' request parameter is missing")
					error_404(request, response, requestedPage);
					//response.sendRedirect(request.getContextPath() + "/" + System.getProperty("STOREELF_VERSION") + "/");
				}
			}else{
				logger.debug("ACCESS DENIED FOR:"+currentUser.getPrincipal());
				request.getRequestDispatcher(access_denied_page).forward(request, response);
			}
			
			
			}
		
		catch(IllegalArgumentException e)	{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
		catch(IOException e)				{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IOException", e);}
		catch(SecurityException e)			{logger.error("error processing GET request for: '"+request.getRequestURI()+"': SecurityException", e);}
		catch(Exception e)					{logger.error("error processing GET request for: '"+request.getRequestURI()+"': Exception", e);}
		finally{
			//error(requestedPage + " NOT FOUND", requestedPage + " does NOT exist -" ,request, response);
			//TODO clean this, servlet should be removed from heap but in a better manner. consider super class via top-down approach
			//if(response.isCommitted()) servletClass = null;
			//FIXEDDDD!!			
		}
    }

	/**
	 * @author tkmagh4
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * This method will be unused until further notice
	 *
	 * @author tkmagh4
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	public String getDefault_page() {	return default_page;	}
	public void setSessionAttribute(String k, Object v){	SecurityUtils.getSubject().getSession().setAttribute(k, v);}
	public Object getSessionAttribute(String k){	return	SecurityUtils.getSubject().getSession().getAttribute(k);}

	public StoreElfSearchHistoryEntityModel saveSearch(HttpServletRequest request){
		ConcurrentHashMap<String, StoreElfSearchHistoryEntityModel> search_history = null;

		if(getSessionAttribute("search_history") == null){
			setSessionAttribute("search_history", new ConcurrentHashMap<String, StoreElfSearchHistoryEntityModel>());
		}

		search_history = (ConcurrentHashMap<String, StoreElfSearchHistoryEntityModel>) getSessionAttribute("search_history");

		if(!search_history.containsKey(request.getRequestURI()+"")){
			search_history.put(request.getRequestURI()+"", new StoreElfSearchHistoryEntityModel());
		}
		return search_history.get(request.getRequestURI()+"");
	}

	public void saveSearchMap(HttpServletRequest request, HashMap<String, Object> search_values){
		HashMap<String, HashMap<String, Object>> search_history = (HashMap<String, HashMap<String, Object>>) SecurityUtils.getSubject().getSession().getAttribute("search_history");

		//validate search history session map object
		if(search_history !=null){
			//store all search values
			search_history.put(request.getRequestURI(), search_values);
		}else{
			setSessionAttribute("search_history", new HashMap<String, HashMap<String, HashMap<String, Object>>>());
		}
	}

	public String getSystemProperty(String prop) {
		return System.getProperty(prop);
	}

	public boolean isAuthorizationEnabled() throws IOException{
		XProperties systemProperties = ReportActivator.getXProperties();

		return (systemProperties.getProperty("STOREELF.IS_AUTHORIZATION_ENABLED").equalsIgnoreCase("Y") || systemProperties.getProperty("STOREELF.IS_AUTHORIZATION_ENABLED") == "y");
	}
		
	public String cleanURLforStaticContent(HttpServletRequest request){
		String url = request.getRequestURI();
		try{ url = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		}catch(Exception e){}

		String[] REQ_URI = StringUtils.split(  url  , '/');
		
		if( StringUtils.equals(REQ_URI[0],getServletContext().getContextPath().substring(1))){
			String path = "";
			for(int i=0;i<REQ_URI.length;i++){
				if(i>0) path += '/'+REQ_URI[i];
			}
			logger.info("requested file path:"+path);
			return path;
		}else{
			return request.getRequestURI();
		}
	}
		
	private void error_500(HttpServletRequest request, HttpServletResponse response, String requestedPage){
		try{
			if("POST".equals(request.getMethod())){
				PrintWriter	responseWriter = response.getWriter();
							responseWriter.write("ERROR 500");
							responseWriter.flush();
							responseWriter.close();
			}else{
				request.getRequestDispatcher(error_500_page + "?errorMessageTitle=" + requestedPage + " NOT FOUND" + "&errorMessageBody=" + requestedPage + " does NOT exist").forward(request, response);
			}
	}
		catch(IllegalArgumentException e)	{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
		catch(IOException e)				{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IOException", e);}
		catch(SecurityException e)			{logger.error("error processing GET request for: '"+request.getRequestURI()+"': SecurityException", e);}
		catch(Exception e)					{logger.error("error processing GET request for: '"+request.getRequestURI()+"': Exception", e);}
	}
	
	private void forwardTologin(HttpServletRequest request, HttpServletResponse response, String requestedPage){
		try{
			if("POST".equals(request.getMethod())){
				PrintWriter	responseWriter = response.getWriter();
							responseWriter.write("ERROR - PLEASE LOGIN");
							responseWriter.flush();
							responseWriter.close();
			}else{
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		}
		catch(IllegalArgumentException e)	{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
		catch(IOException e)				{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IOException", e);}
		catch(SecurityException e)			{logger.error("error processing GET request for: '"+request.getRequestURI()+"': SecurityException", e);}
		catch(Exception e)					{logger.error("error processing GET request for: '"+request.getRequestURI()+"': Exception", e);}
	}
	
	private void routeToJSP(HttpServletRequest request, HttpServletResponse response, String requestedPage){
		try{
			if("POST".equals(request.getMethod())){
				PrintWriter	responseWriter = response.getWriter();
							responseWriter.write("Don't be silly");
							responseWriter.flush();
							responseWriter.close();
			}else{
				request.getRequestDispatcher(requestedPage).forward(request, response);
			}
		}
		catch(IllegalArgumentException e)	{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
		catch(IOException e)				{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IOException", e);}
		catch(SecurityException e)			{logger.error("error processing GET request for: '"+request.getRequestURI()+"': SecurityException", e);}
		catch(Exception e)					{logger.error("error processing GET request for: '"+request.getRequestURI()+"': Exception", e);}
	}

	private void error_404(HttpServletRequest request, HttpServletResponse response, String requestedPage){
		try{
			if("POST".equals(request.getMethod())){
				PrintWriter	responseWriter = response.getWriter();
							responseWriter.write("ERROR 404");
							responseWriter.flush();
							responseWriter.close();
			}else{
				request.getRequestDispatcher(error_404_page).forward(request, response);
			}
		}
		catch(IllegalArgumentException e)	{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IllegalArgumentException", e);}
		catch(IOException e)				{logger.error("error processing GET request for: '"+request.getRequestURI()+"': IOException", e);}
		catch(SecurityException e)			{logger.error("error processing GET request for: '"+request.getRequestURI()+"': SecurityException", e);}
		catch(Exception e)					{logger.error("error processing GET request for: '"+request.getRequestURI()+"': Exception", e);}
	}

	private boolean getPublic(HttpServletRequest request, HttpServletResponse response){
		try{
	    	String filename = cleanURLforStaticContent(request);

	    	logger.info("filename:"+getServletContext().getRealPath(filename));
	        File file = new File(getServletContext().getRealPath(filename));
 
	        if(file.exists()){
		        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		        response.setHeader("Content-Length", String.valueOf(file.length()));
		        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		        Files.copy(file.toPath(), response.getOutputStream());

		        response.getOutputStream().flush();
		        response.getOutputStream().close();
		        return true;
	        }else{
	        	logger.info("FILE DOES NOT EXIST - filename:"+getServletContext().getRealPath(filename));
	        }
		}catch(Exception e)				{logger.error("error returning static content : Exception", e);}
		return false;
	}
	
	public static String getClientIpAddr(HttpServletRequest request) {  
		
		/*String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		   if (ipAddress == null) {  
			   ipAddress = request.getRemoteAddr();  
		   }*/
		   
/*        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  */
        return request.getRemoteHost();  
    }
 
	/**
	 * unused, for now!!
	 * Let's hope we never need to use this
	 */
	public void sweepThreads(){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

		for(Thread t:threadArray){
			if(t.getState().equals(State.BLOCKED)){
				t.getName();
				logger.debug("killing thread: "+t.getId()+" | "+t.getName());
				t.stop();
			}
		}
	}
}
