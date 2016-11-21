package com.storeelf.report.web.servlets._internal_api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.servlets.StoreElfHttpServlet;

public class SQLServlet extends StoreElfHttpServlet<Object> {
	static final Logger			logger				= Logger.getLogger(SQLServlet.class);
	private static final long	serialVersionUID	= 1L;
	private String				defaultPage			= "/_internal_api_includes/_internal_api.jsp";


	/**
	 * Each *'Response' method should handle both GET and POST request methods <br>
	 * the GET must return a corresponding JSP page
	 * the POST must return json/xml for client-side processing
	 *
	 * @param requestedPage
	 * @param request
	 * @param response
	 */
	public void run_export(String requestedPage, HttpServletRequest request, HttpServletResponse response){ 
		String			content 				= "-error-";
		PrintWriter		responseWriter			= null;
		String			jsp_include_page		= "/_internal_api_includes/sql/run_export.jsp";
		Gson 			gson	         		= new GsonBuilder().create();
		JsonArray		rootReturn  			= new JsonArray();
		JsonObject		sqlFileJsonObject		= new JsonObject();

		//logger.log(Level.INFO, "OrderManagementServlet:orderResponse | " + requestedPage + "|" + request.getParameter("orderNumber"));

		logger.debug("0 export started... ");

		try{
			if( "POST".equals(request.getMethod()) ){
				//request_uri	= request.getParameter("request_uri");

				responseWriter	= response.getWriter();
				response.setContentType("application/json");

				logger.debug("1 export started... ");

				try{
					String	SQL					= null;
					String	INSTANCE_TYPE		= null;
					Long	SQL_TIMEOUT			= null;

					File sqlFile				= null;

					boolean deleteSuccessful	= false;

					logger.debug("2 export started... ");

					for(String SQL_ID :Constants.SQL_MAP.keySet()){
						Constants.STOREELF_SQL_FILE_EXPORT_RUNNING = true;

						logger.debug("exporting: "+SQL_ID);

						SQL				= Constants.SQL_MAP.get(SQL_ID);
						INSTANCE_TYPE	= Constants.SQL_INST.get(SQL_ID);
						SQL_TIMEOUT		= Constants.SQL_TIME_MAP.get(SQL_ID);

						String path = request.getServletContext().getRealPath("/sql_includes/"+SQL_ID+"."+INSTANCE_TYPE+"."+SQL_TIMEOUT);

						logger.debug("STOREELF_SQL_FILE_EXPORT_RUNNING = "+Constants.STOREELF_SQL_FILE_EXPORT_RUNNING);
						logger.debug("target sqlFilePath = "+path);

						sqlFile 		= new File(path);

						logger.debug("actual sqlFilePath = "+sqlFile.getAbsolutePath());

						deleteSuccessful = (sqlFile.exists()) ? sqlFile.delete() : false;
						logger.debug("deleted = "+deleteSuccessful);

						if(deleteSuccessful){
							sqlFile 		= new File(path);
							sqlFile.createNewFile();
							
							BufferedWriter	bufferedWriter				= new BufferedWriter(new FileWriter(sqlFile, true));
											bufferedWriter.write(SQL);
											bufferedWriter.flush();
											bufferedWriter.close();

							logger.debug("exported "+SQL_ID);

							sqlFileJsonObject.addProperty(SQL_ID, "updated");
							rootReturn.add(sqlFileJsonObject);
							sqlFileJsonObject = new JsonObject();
						}
					}

					Constants.STOREELF_SQL_FILE_EXPORT_RUNNING = false;

				}catch(Exception e){
					logger.error("export job failed:", e);
					e.printStackTrace();
					Constants.STOREELF_SQL_FILE_EXPORT_RUNNING = false;
				}

				//finally prep the rootReturn object to be sent to the page
				content = gson.toJson(rootReturn);

				responseWriter.write(content);
				responseWriter.flush();
				responseWriter.close();
			}else{
				request.getRequestDispatcher(defaultPage + "?include=" + jsp_include_page).forward(request, response);
			}
		}
		catch (IOException e)			{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : IOException", e);}
		catch (Exception e)				{e.printStackTrace(); logger.log(Level.ERROR, "error processing request : Exception", e);}
	}
}
