package com.sattrak.rpi.serial;

import com.sattrak.rpi.util.ByteConverter;

public class GpsResponsePacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: azimuth angle
	private static final int LOCATION_LATITUDE = 0;

	// Argument 2: elevation angle
	private static final int LOCATION_LONGITUDE = 7;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private double latitude;
	private double longitude;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public GpsResponsePacket(double latitude, double longitude) {
		setCommand(SerialCommand.RESPONSE_GPS);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public GpsResponsePacket(byte[] packetBytes) throws InvalidPacketException {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public double getAzimuth() {
		return latitude;
	}

	public double getElevation() {
		return longitude;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Convert arguments to byte arrays
		byte[] latBytes = ByteConverter.doubleToBytes(latitude);
		byte[] lonBytes = ByteConverter.doubleToBytes(longitude);

		// Create byte array for all arguments
		byte[] argBytes = new byte[latBytes.length + lonBytes.length];

		// Insert arguments into array
		System.arraycopy(latBytes, 0, argBytes, LOCATION_LATITUDE,
				latBytes.length);
		System.arraycopy(lonBytes, 0, argBytes, LOCATION_LONGITUDE,
				lonBytes.length);
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		latitude = ByteConverter.bytesToDouble(argBytes, LOCATION_LATITUDE);
		longitude = ByteConverter.bytesToDouble(argBytes, LOCATION_LONGITUDE);
	}

}
