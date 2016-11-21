package com.storeelf.report.web.model.impl;

import java.io.Serializable;

public class GenericModel implements Serializable {
	private static final long serialVersionUID = 8893476820359383800L;
	private String name;
	private Object value;

	public GenericModel() {
		super();
	}

	public GenericModel(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
