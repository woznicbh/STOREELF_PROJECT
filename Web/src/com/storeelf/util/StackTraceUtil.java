/**
 * 
 */
package com.storeelf.util;

/**
 * 
 * <B>Purpose:</B> Provides generic Stack Trace utils<BR/>
 * <B>Creation Date:</B> Oct 21, 2011 3:34:24 PM<BR/>
 */
public final class StackTraceUtil {

	public static String getStackTrace(Throwable aThrowable) {
		return getCustomStackTrace(aThrowable);
	}

	/**
	 * Defines a custom format for the stack trace as String.
	 */
	public static String getCustomStackTrace(Throwable aThrowable) {
		// add the class name and any message passed to constructor
		final StringBuilder result = new StringBuilder("EXCEPTION: ");
		result.append(aThrowable.toString());
		final String NEW_LINE = "\n";
		result.append(NEW_LINE);

		// add each element of the stack trace
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append(element);
			result.append(NEW_LINE);
		}
		return result.toString();
	}
}
