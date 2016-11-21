/**
 * 
 */
package com.storeelf.util.model;

import java.io.Serializable;

/**
 * @author tkmawh6
 * 
 */
public interface SCEnv extends Serializable {
	public int getType();

	public String getUrl();

	public void setUrl(String url);

	public String getUserId();

	public String getProgId();

	public void setUserId(String userId);

	public void setProgId(String progId);
}
