package com.sattrak.rpi;

import java.util.Calendar;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.sattrak.rpi.util.FormatUtil;

public class Task implements Delayed {

	// ===============================
	// CONSTANTS
	// ===============================

	// Length of time between setting the orientation and taking the picture.
	// Should be at least as long as the max time it can take the motors to move
	// to any orientation.
	public static final long MOTOR_DELAY = 5000;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private String title;
	private Calendar dateTime;
	private long duration;
	private double azimuth;
	private double elevation;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public Task(String title, Calendar dateTime, long duration, double azimuth,
			double elevation) {
		this.title = title;
		this.dateTime = dateTime;
		this.duration = duration;
		this.azimuth = azimuth;
		this.elevation = elevation;
	}

	// ===============================
	// GETTERS
	// ===============================

	public String getTitle() {
		return title;
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public long getDuration() {
		return duration;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public double getElevation() {
		return elevation;
	}

	// ===============================
	// SETTERS
	// ===============================

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	// ===============================
	// PUBLIC METHODS
	// ===============================

	// ===============================
	// PRIVATE METHODS
	// ===============================

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	public int compareTo(Delayed other) {
		if (dateTime.getTimeInMillis() < ((Task) other).getDateTime()
				.getTimeInMillis())
			return -1;
		else if (dateTime.getTimeInMillis() > ((Task) other).getDateTime()
				.getTimeInMillis())
			return 1;
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long delayInMillis = dateTime.getTimeInMillis()
				- System.currentTimeMillis() - MOTOR_DELAY;
		return unit.convert(delayInMillis, unit);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "Title: " + title + "\n" + 
				"Date: " + FormatUtil.getDateString(dateTime) + "\n" + 
				"Time: " + FormatUtil.getTimeString(dateTime) + "\n" + 
				"Duration: " + duration + " ms\n" + 
				"Azimuth: " + azimuth + " degrees\n" +
				"Elevation: " + elevation + " degrees";
		// @formatter:on
	}

}
