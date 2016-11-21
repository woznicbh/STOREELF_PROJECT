package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.report.web.model.SQLModel;

public class AgentCriteriaModel extends SQLModel implements Serializable {
	private static final long serialVersionUID = -575805049547618809L;
	private String criteriaid;
	private Map<String, StatisticModel> statistics;
	private Integer threadcount;
	private String servername;
	private String subservicename;

	public final String SQL_STATISTICS_LIST = "SELECT stat.statistic_name, stat.service_name, hostname, server_name, SUM(stat.statistic_value) statvalue FROM yfs_statistics_detail stat WHERE service_name =? AND context_name =? and server_name = ? AND stat.statistics_detail_key > TO_CHAR(sysdate - 1/24,'YYYYMMDDHH24MISS') GROUP BY stat.statistic_name, stat.service_name, hostname, server_name";

	public AgentCriteriaModel(String criteriaid, String subservicename,
			String servername, Integer threadcount, String env)
			throws FileNotFoundException, ClassNotFoundException, SQLException,
			IOException {
		super();
		this.criteriaid = criteriaid;
		this.threadcount = threadcount;
		this.servername = servername;
		this.subservicename = subservicename;
		this.statistics = new HashMap<String, StatisticModel>();
		this.setEnv(env);
		this.refreshStatistics();

	}

	public void refreshStatistics() throws FileNotFoundException,
			ClassNotFoundException, SQLException, IOException {
		Connection con = ReportActivator.getInstance().getConnection(
				Constants.OMS, this.getEnv());
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			
			stmt = con.prepareStatement(this.SQL_STATISTICS_LIST);
			if (this.subservicename == null) {
				stmt.setString(1, this.criteriaid);
				stmt.setString(2, this.criteriaid);
			} else {
				stmt.setString(1, this.subservicename);
				stmt.setString(2, this.subservicename);
			}
			stmt.setString(3, this.servername);
			rset = stmt.executeQuery();

			String statname = null;
			String hostname = null;
			String statvalue = null;

			StatisticModel stat = null;

			while (rset.next()) {
				statname = rset.getString("statistic_name");
				hostname = rset.getString("hostname");
				statvalue = rset.getString("statvalue");

				stat = this.getStatistics().get(statname);

				if (stat == null) {
					stat = new StatisticModel(statname);
				}
				stat.getHoststatmap().put(hostname, statvalue);
				this.statistics.put(statname, stat);
			}
		} finally {
			if(stmt!=null){stmt.close();}
			if(rset!=null){rset.close();}
			if(con!=null){con.close();}
		}
	}

	public List<String> getHostListForCriteria() {
		Set<String> hostset = new HashSet<String>();
		Map<String, StatisticModel> stats = this.getStatistics();
		Iterator<String> itstat = stats.keySet().iterator();

		String stat = null;
		StatisticModel mod = null;

		while (itstat.hasNext()) {
			stat = itstat.next();
			mod = stats.get(stat);
			hostset.addAll(mod.getHoststatmap().keySet());
		}
		List<String> hostlist = new ArrayList<String>(hostset);
		Collections.sort(hostlist);
		return hostlist;
	}

	public String getCriteriaid() {
		return criteriaid;
	}

	public void setCriteriaid(String criteriaid) {
		this.criteriaid = criteriaid;
	}

	public Map<String, StatisticModel> getStatistics() {
		return statistics;
	}

	public void setStatistics(Map<String, StatisticModel> statistics) {
		this.statistics = statistics;
	}

	public Integer getThreadcount() {
		return threadcount;
	}

	public void setThreadcount(Integer threadcount) {
		this.threadcount = threadcount;
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}
}
