package com.sattrak.rpi.serial;

import com.sattrak.rpi.util.ByteConverter;

public class EnvironmentalResponsePacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: azimuth angle
	private static final int LOCATION_TEMP = 0;

	// Argument 2: elevation angle
	private static final int LOCATION_HUMIDITY = 8;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private double temperature;
	private double humidity;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public EnvironmentalResponsePacket(double temperature, double humidity) {
		setCommand(SerialCommand.RESPONSE_ENV);
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public EnvironmentalResponsePacket(byte[] packetBytes)
			throws InvalidPacketException {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public double getTemperature() {
		return temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Convert arguments to byte arrays
		byte[] tempBytes = ByteConverter.doubleToStringBytes(temperature);
		byte[] humBytes = ByteConverter.doubleToStringBytes(humidity);

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
		temperature = ByteConverter
				.stringBytesToDouble(argBytes, LOCATION_TEMP);
		humidity = ByteConverter.stringBytesToDouble(argBytes,
				LOCATION_HUMIDITY);
	}

}
