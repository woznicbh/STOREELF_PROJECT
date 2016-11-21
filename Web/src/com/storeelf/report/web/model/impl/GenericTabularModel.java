/**
 *
 */
package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.exception.StoreElfException;

/**
 * <B>Class Name:GenericTabularModel</B>This class is used to <BR/>
 * <B>Purpose: Model tabular data as a column map</B> <BR/>
 * <B>Creation Date:</B> Jun 19, 2013 <BR/>
 */
public class GenericTabularModel extends SQLModel implements Serializable {

	private static final long serialVersionUID = 6060398411332059982L;
	private boolean processed;
	private ConcurrentHashMap<Integer, HashMap<String, Object>> colmap;

	public GenericTabularModel(String id) {
		super(id);
	}

	public GenericTabularModel() {
		super();
	}

	/**
	 * @return the colmap
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws StoreElfException
	 */
	public ConcurrentHashMap<Integer, HashMap<String, Object>> getColmap() throws StoreElfException {
		if (this.getResultmap() == null)
			setProcessed(false);
		else {
			try {
				colmap = this.getResultmap();
			} catch (NullPointerException e) {
				colmap = null;
			}
		}
		return colmap;
	}

	/**
	 * @param colmap
	 *            the colmap to set
	 */
	public void setColmap(ConcurrentHashMap<Integer, HashMap<String, Object>> colmap) {
		this.colmap = colmap;
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * @param processed
	 *            the processed to set
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
