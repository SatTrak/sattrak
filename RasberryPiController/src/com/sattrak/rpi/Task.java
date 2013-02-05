package com.sattrak.rpi;

import java.util.Calendar;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Task implements Delayed {

	// ===============================
	// CONSTANTS
	// ===============================

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private Calendar dateTime;
	private long duration;
	private double azimuth;
	private double elevation;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public Task(Calendar dateTime, long duration, double azimuth,
			double elevation) {
		this.dateTime = dateTime;
		this.duration = duration;
		this.azimuth = azimuth;
		this.elevation = elevation;
	}

	// ===============================
	// GETTERS
	// ===============================

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
				- System.currentTimeMillis();
		return unit.convert(delayInMillis, unit);
	}

}
