/**
 * 
 */
package com.storeelf.util.model.impl;

import com.storeelf.util.model.SCEnv;

/**
 * @author tkmawh6
 * 
 */
public class SCWSEnv implements SCEnv {

	/**
	 * 
	 */
	private static final long serialVersionUID = -600808216066583242L;

	public static final int TYPE = 1;

	private String url;
	private String userId;
	private String progId;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	@Override
	public int getType() {
		return SCWSEnv.TYPE;
	}

}
