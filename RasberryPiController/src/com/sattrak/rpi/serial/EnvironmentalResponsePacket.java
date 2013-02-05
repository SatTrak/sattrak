package com.sattrak.rpi.serial;

public class EnvironmentalResponsePacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: azimuth angle
	private static final int LOCATION_TEMP = 0;

	// Argument 2: elevation angle
	private static final int LOCATION_HUMIDITY = 1;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private byte temperature;
	private byte humidity;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public EnvironmentalResponsePacket(byte temperature, byte humidity) {
		setCommand(SerialCommand.RESPONSE_ENV);
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public EnvironmentalResponsePacket(byte[] packetBytes) throws Exception {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public byte getTemperature() {
		return temperature;
	}

	public byte getHumidity() {
		return humidity;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Create byte array for all arguments
		byte[] argBytes = new byte[2];

		// Insert arguments into array
		argBytes[LOCATION_TEMP] = temperature;
		argBytes[LOCATION_HUMIDITY] = humidity;
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		temperature = argBytes[LOCATION_TEMP];
		humidity = argBytes[LOCATION_HUMIDITY];
	}

}
