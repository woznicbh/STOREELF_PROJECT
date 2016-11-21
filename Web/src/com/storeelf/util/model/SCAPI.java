/**
 * 
 */
package com.storeelf.util.model;

import java.io.Serializable;

import org.w3c.dom.Document;

import com.storeelf.util.exception.StoreElfException;

/**
 * @author tkmawh6
 * 
 */
public interface SCAPI extends Serializable {
	public int getType();

	public boolean isFlow();

	public void setFlow(boolean flow);

	public String getName();

	public void setName(String name);

	public Document execute(Document indoc, SCEnv env) throws StoreElfException;

	public void setTemplate(Document template);

	public Document getTemplate();
}
