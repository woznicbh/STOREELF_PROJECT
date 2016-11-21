/**
 * 
 */
package com.storeelf.util.exception;

/**
 * @author tkmawh6
 * 
 */
public class StoreElfException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StoreElfException() {
		super();
	}

	public StoreElfException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		//super(arg0, arg1, arg2, arg3);
	}

	public StoreElfException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public StoreElfException(String arg0) {
		super(arg0);
	}

	public StoreElfException(Throwable arg0) {
		super(arg0);
	}

}
