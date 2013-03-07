package com.sattrak.rpi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatUtil {

	/**
	 * Private constructor so the class can't be instantiated.
	 */
	private FormatUtil() {
	}

	/**
	 * Convert a Calendar time into a string
	 * 
	 * @param time
	 * @return the string represenation
	 */
	public static String getTimeString(Calendar time) {
		return time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + ":"
				+ time.get(Calendar.SECOND);
	}

	/**
	 * Convert a Calendar date into a string
	 * 
	 * @param date
	 * @return the string represenation
	 */
	public static String getDateString(Calendar date) {
		return (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE)
				+ "/" + date.get(Calendar.YEAR);
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

}
