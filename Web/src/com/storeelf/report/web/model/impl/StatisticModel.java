package com.storeelf.report.web.model.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StatisticModel implements Serializable {
	private static final long serialVersionUID = -3016064206733874672L;
	private String statisticname;
	private Map<String, String> hoststatmap;

	public StatisticModel(String statisticname) {
		super();
		this.statisticname = statisticname;
		hoststatmap = new HashMap<String, String>();
	}

	public String getStatisticname() {
		return statisticname;
	}

	public void setStatisticname(String statisticname) {
		this.statisticname = statisticname;
	}

	public Map<String, String> getHoststatmap() {
		return hoststatmap;
	}

	public void setHoststatmap(Map<String, String> hoststatmap) {
		this.hoststatmap = hoststatmap;
	}
}
