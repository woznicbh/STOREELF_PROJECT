/**
 * 
 */
package com.storeelf.report.web.model.impl;

import java.io.Serializable;

import com.storeelf.report.web.model.SearchFieldModal;
import com.storeelf.util.StringUtils;

/**
 * <B>Class Name:</B><BR/>
 * <B>Purpose:</B> Generic Implementation of SearchFieldModal<BR/>
 * <B>Creation Date:</B> Dec 5, 2011 7:32:27 AM<BR/>
 */
public class SearchFieldModalImpl implements SearchFieldModal, Serializable {
	private static final long serialVersionUID = -8296361291680537656L;
	private String id;
	private String name;
	private boolean isRequired;
	private String type;

	public SearchFieldModalImpl(String id, String name) throws Exception {
		super();
		if (StringUtils.isVoid(id)) {
			throw new Exception("ID Cannot be null");
		}
		this.id = id;
		this.name = name;
		this.isRequired = false;
		this.type = "TEXT";
	}

	public SearchFieldModalImpl(String id, String name, boolean isRequired,
			String type) throws Exception {
		super();
		if (StringUtils.isVoid(id)) {
			throw new Exception("ID Cannot be null");
		}
		this.id = id;
		this.name = name;
		this.isRequired = isRequired;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isRequired
	 */
	public boolean isRequired() {
		return isRequired;
	}

	/**
	 * @param isRequired
	 *            the isRequired to set
	 */
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
