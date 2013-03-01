package com.sattrak.rpi.util;

import java.util.Calendar;

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

}
