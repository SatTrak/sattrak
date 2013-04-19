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
		return String.format("%02d:%02d:%02d", time.get(Calendar.HOUR),
				time.get(Calendar.MINUTE), time.get(Calendar.SECOND));
	}

	/**
	 * Convert a Calendar date into a string
	 * 
	 * @param date
	 * @return the string represenation
	 */
	public static String getDateString(Calendar date) {
		return String.format("%02d/%02d/%04d", date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DATE), date.get(Calendar.YEAR));
	}

	/**
	 * Return the current date and time in MM-dd-yyyy HH:mm:ss format
	 * 
	 * @return a string of the current date and time
	 */
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	/**
	 * Return a string representation of cal in yyyy-MM-dd_HH-mm-ss format to be
	 * used as a file or directory name
	 * 
	 * @param cal
	 *            the date and time to convert
	 * @return a string representation of cal
	 */
	public static String getAsFilename(Calendar cal) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date r = cal.getTime();
		String strDate = sdfDate.format(r);
		return strDate;
	}

	/**
	 * Return a string representation of s in the format HH:MM:SS.xx
	 * 
	 * @param s
	 *            the number of seconds
	 * @return the string representation
	 */
	public static String getTimeString(double s) {
		int hours = ((int) s) / 3600;
		int minutes = ((int) s - hours * 3600) / 60;
		double seconds = s - hours * 3600 - minutes * 60;
		return String.format("%02d:%02d:%04.2f", hours, minutes, seconds);
	}

}
