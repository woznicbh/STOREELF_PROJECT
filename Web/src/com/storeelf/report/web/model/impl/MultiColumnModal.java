/**
 * 
 */
package com.storeelf.report.web.model.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import com.storeelf.report.web.model.SQLModel;
import com.storeelf.util.exception.StoreElfException;

/**
 * <B>Class Name:</B>This class is used to <BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Nov 18, 2011 4:45:10 PM<BR/>
 */
public class MultiColumnModal extends SQLModel implements Serializable {

	private static final long serialVersionUID = 2645550790928307320L;
	private boolean processed;
	private HashMap<String, Object> colmap;

	public MultiColumnModal(String id) {
		super(id);
	}

	public MultiColumnModal() {
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
	public HashMap<String, Object> getColmap() throws StoreElfException {
		if (this.getResultmap() == null)
			setProcessed(false);
		else {
			try {
				colmap = this.getResultmap().get(1);
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
	public void setColmap(HashMap<String, Object> colmap) {
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
