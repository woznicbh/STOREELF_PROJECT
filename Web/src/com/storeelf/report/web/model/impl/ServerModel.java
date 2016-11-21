package com.storeelf.report.web.model.impl;

import java.io.Serializable;

import com.storeelf.report.web.Constants;

public class ServerModel implements Serializable {
	private static final long serialVersionUID = 2139427467909698257L;
	
	private String servername;
	private String hostname;
	private String servertype;
	private Integer activecount;
	private Integer inactivecount;
	private Integer exceptioncount;
	private Integer totalcount;

	public ServerModel() {
		super();
	}

	public ServerModel(String servername, String hostname, String servertype,
			Integer activecount) {
		super();
		this.servername = servername;
		this.hostname = hostname;
		this.servertype = servertype;
		this.activecount = activecount;
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getServertype() {
		return servertype;
	}

	public void setServertype(String servertype) {
		this.servertype = servertype;
	}

	public Integer getActivecount() {
		return activecount;
	}

	public void setActivecount(Integer activecount) {
		this.activecount = activecount;
	}

	public Integer getInactivecount() {
		return inactivecount;
	}

	public void setInactivecount(Integer inactivecount) {
		this.inactivecount = inactivecount;
	}

	public Integer getExceptioncount() {
		return exceptioncount;
	}

	public void setExceptioncount(Integer exceptioncount) {
		this.exceptioncount = exceptioncount;
	}

	public Integer getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(Integer totalcount) {
		this.totalcount = totalcount;
	}

	public boolean isAppServer() {
		if (this.servertype.equals(Constants.APPSERVER)) {
			return true;
		}
		return false;
	}

	public boolean isAgentServer() {
		if (this.servertype.equals(Constants.AGENTSERVER)) {
			return true;
		}
		return false;
	}

	public boolean isIntServer() {
		if (this.servertype.equals(Constants.INTSERVER)) {
			return true;
		}
		return false;
	}

	public boolean isHealthMonitor() {
		if (this.servertype.equals(Constants.HEALTHMONITOR)) {
			return true;
		}
		return false;
	}

	public boolean equals(ServerModel mod) {
		boolean check = false;
		if (mod.getServername().equals(servername)
				&& mod.getHostname().equals(hostname)
				&& mod.getServertype().equals(servertype)) {
			check = true;
		}
		return check;
	}

	@Override
	public String toString() {
		return "{" + this.hostname + ":" + this.servername + ":"
				+ this.servertype + ":" + this.activecount + ":"
				+ this.inactivecount + ":" + this.exceptioncount + "}";
	}
}
