/**
 * 
 */
package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SQLModel;

/**
 * @author tkmawh6
 * 
 */
public class ServerDetailModel extends SQLModel implements Serializable {
	private static final long serialVersionUID = 3400667102469746803L;
	
	private String servername;
	private String servertype;
	private List<AgentCriteriaModel> criterialist;
	public static String server_instance = "OMS" ;

	public final String SQL_CRITERIA_LIST = "SELECT distinct trim(ser.server_name) server_name, DECODE(ser.server_type,'00','INT','01','AGENT') server_type, flow.flow_name,sub.sub_flow_name, NVL(EXTRACT(xmltype('' || sub.config_xml || ''), '/SubFlowConfig/Link/Properties/@Threads').getStringVal(),'0') threads FROM yfs_server ser, yfs_sub_flow sub, yfs_flow flow WHERE sub.server_key =ser.server_key and flow.flow_key=sub.flow_key and ser.server_name = ?";

	public ServerDetailModel(String servername,String env) throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException {
		super();
		this.servername = servername;
		this.criterialist = new ArrayList<AgentCriteriaModel>();
		this.setEnv(env);
		this.refreshCriteriaList();
	}

	public void refreshCriteriaList() throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException {
		String y = null;
		if (server_instance.equals("OMS"))
			 y = Constants.OMS;
		else if (server_instance.equals("GIV"))
			y = Constants.GIV;
		else if (server_instance.equals("OMSr"))
			y = Constants.OMSr;
		else if (server_instance.equals("OMSr_Training"))
			y = Constants.OMSr_Training;	
		
		Connection con = ReportActivator.getInstance().getConnection(
				y, this.getEnv());
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			
			stmt = con.prepareStatement(this.SQL_CRITERIA_LIST);
			stmt.setString(1, this.servername);
			rset = stmt.executeQuery();
			
			String criteriaid = null;
			Integer threads = null;
			String subflowname = null;
			
			AgentCriteriaModel crt = null;
			
			while (rset.next()) {
				criteriaid = rset.getString("flow_name");
				threads = rset.getInt("threads");
				subflowname = rset.getString("sub_flow_name");
				this.setServertype(rset.getString("server_type"));
				
				
				if (this.servertype.equals(Constants.AGENTSERVER)) {
					subflowname = null;
				}
				crt = new AgentCriteriaModel(criteriaid,
						subflowname, this.servername, threads,this.getEnv());
				this.criterialist.add(crt);

			}
		} finally {
			if(stmt!=null){stmt.close();}
			if(rset!=null){rset.close();}
			if(con!=null){con.close();}
		}
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public List<AgentCriteriaModel> getCriterialist() {
		return criterialist;
	}

	public void setCriterialist(List<AgentCriteriaModel> criterialist) {
		this.criterialist = criterialist;
	}

	public String getServertype() {
		return servertype;
	}

	public void setServertype(String servertype) {
		this.servertype = servertype;
	}
}
