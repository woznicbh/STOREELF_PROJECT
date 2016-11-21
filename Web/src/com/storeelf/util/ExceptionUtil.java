package com.storeelf.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ExceptionUtil {

	/**
	 * Generates new RunTimeException with original thrown exception plus logging.
	 * 
	 * @param e
	 *            the Exception caught in the original exception
	 * @param logger
	 *            the logger being used by the class throwing the exception
	 * @param msg
	 *            the message you want posted in the logs          
	 */
	public static void HandleCatchErrorException(Exception e, Logger logger, String msg){
		e.printStackTrace();
		logger.log(Level.ERROR, msg, e);
		throw new RuntimeException(e);
	}
}
