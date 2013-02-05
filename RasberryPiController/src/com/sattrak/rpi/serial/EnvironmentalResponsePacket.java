package com.sattrak.rpi.serial;

import com.sattrak.rpi.util.ByteConverter;

public class EnvironmentalResponsePacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: azimuth angle
	private static final int LOCATION_TEMP = 0;

	// Argument 2: elevation angle
	private static final int LOCATION_HUMIDITY = 2;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private short temperature;
	private short humidity;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public EnvironmentalResponsePacket(int temperature, int humidity) {
		setCommand(SerialCommand.RESPONSE_ENV);
		this.temperature = (short) temperature;
		this.humidity = (short) humidity;
	}

	public EnvironmentalResponsePacket(byte[] packetBytes) throws Exception {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public short getTemperature() {
		return temperature;
	}

	public short getHumidity() {
		return humidity;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Convert arguments to byte arrays
		byte[] tempBytes = ByteConverter.shortToBytes(temperature);
		byte[] humBytes = ByteConverter.shortToBytes(humidity);

		// Create byte array for all arguments
		byte[] argBytes = new byte[tempBytes.length + humBytes.length];

		// Insert arguments into array
		System.arraycopy(tempBytes, 0, argBytes, LOCATION_TEMP,
				tempBytes.length);
		System.arraycopy(humBytes, 0, argBytes, LOCATION_HUMIDITY,
				humBytes.length);
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		temperature = ByteConverter.bytesToShort(argBytes, LOCATION_TEMP);
		humidity = ByteConverter.bytesToShort(argBytes, LOCATION_HUMIDITY);
	}

}
