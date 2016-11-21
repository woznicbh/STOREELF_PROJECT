/**
 * 
 */
package com.storeelf.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <B>Class Name:</B><BR/>
 * <B>Purpose:</B> <BR/>
 * <B>Creation Date:</B> Sep 12, 2011 2:51:06 PM<BR/>
 */
public class DateUtils {

	/**
	 * 
	 * <B>Purpose: </B>This utility method returns a past date before number of
	 * days.
	 * 
	 * @param days
	 * @return
	 */
	public static Date getBeforeDays(Date date, int days) {
		long backDateMS = date.getTime() - ((long) days) * 24 * 60
				* 60 * 1000;
		Date backDate = new Date();
		backDate.setTime(backDateMS);
		return backDate;
	}
	
	/**
	 * 
	 * <B>Purpose: </B>This utility method returns a past date before number of
	 * days.
	 * 
	 * @param days
	 * @return
	 */
	public static Date getAfterDays(Date date, int days) {
		long frontDateMS = date.getTime() + ((long) days) * 24 * 60
				* 60 * 1000;
		Date frontDate = new Date();
		frontDate.setTime(frontDateMS);
		return frontDate;
	}
	
	/**
	 * 
	 * <B>Purpose: </B>This utility method returns a past date before number of
	 * days.
	 * 
	 * @param days
	 * @return
	 */
	public static Date getDateBeforeDays(int days) {
		long backDateMS = (new Date()).getTime() - ((long) days) * 24 * 60
				* 60 * 1000;
		Date backDate = new Date();
		backDate.setTime(backDateMS);
		return backDate;
	}

	/**
	 * 
	 * <B>Purpose:</B>This utility method returns a future date after number of
	 * days.
	 * 
	 * @param days
	 * @return
	 */
	public static Date getDateAfterDays(int days) {
		long backDateMS = (new Date()).getTime() + ((long) days) * 24 * 60
				* 60 * 1000;
		Date backDate = new Date();
		backDate.setTime(backDateMS);
		return backDate;
	}
	
	
	public static Date getSysdateTillHour(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date sysdate = cal.getTime();
		return sysdate;
	}
	
	
	public static Date getTodaysDate(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date sysdate = cal.getTime();
		return sysdate;
	}
	
	/**
	 * Generates String variable containing the date format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".
	 * 
	 * @return the created date string      
	 */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String created = formatter.format(date);
        return created;
    }
    
	public static int getDateDifference(Date d1,Date d2){
		long ms = d1.getTime()-d2.getTime();
		return (int) (ms/(1000 * 60 *60 * 24));
	}
	
	public static String getDateDifferenceFull(Date start, Date end) {
		
		String result = "";

		try { 
			//in milliseconds
			long diff = end.getTime() - start.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			result+=diffDays + " Days, ";
			result+=diffHours + " Hrs, ";
			result+=diffMinutes + " Mins, ";
			result+=diffSeconds + " Secs.";			
		}catch (Exception e) {e.printStackTrace();}
		
		return result;

	}
}
