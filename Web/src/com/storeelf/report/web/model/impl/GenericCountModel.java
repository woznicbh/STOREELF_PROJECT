/**
 * 
 */
package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.exception.StoreElfException;

/**
 * <B>Class Name:</B><BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Nov 15, 2011 1:34:39 AM<BR/>
 */
public class GenericCountModel extends SQLModel implements Serializable {

	private static final long serialVersionUID = -4305909212190886532L;

	/**
	 * Required for Refective instantiation
	 */
	public GenericCountModel() {
		super();
	}

	public GenericCountModel(String id) {
		super(id);
	}

	private Object val;

	/**
	 * @return the val
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws StoreElfException 
	 */
	public Object getVal() throws StoreElfException {
		if (this.getResultmap() == null)
			val = Constants.PROCESSING;
		else if (this.getResultmap().isEmpty()) {
			val = "";
		} else {
			val = this.getResultmap().get(1).get("COUNT");
		}
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(Object val) {
		this.val = val;
	}
}
