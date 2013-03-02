package com.sattrak.rpi.serial;

import com.sattrak.rpi.util.ByteConverter;

public class OrientationResponsePacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: azimuth angle
	private static final int LOCATION_AZIMUTH = 0;

	// Argument 2: elevation angle
	private static final int LOCATION_ELEVATION = 7;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private double azimuth;
	private double elevation;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public OrientationResponsePacket(double azimuth, double elevation) {
		setCommand(SerialCommand.RESPONSE_ORIENTATION);
		this.azimuth = azimuth;
		this.elevation = elevation;
	}

	public OrientationResponsePacket(byte[] packetBytes)
			throws InvalidPacketException {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public double getAzimuth() {
		return azimuth;
	}

	public double getElevation() {
		return elevation;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Convert arguments to byte arrays
		byte[] azBytes = ByteConverter.doubleToStringBytes(azimuth);
		byte[] elBytes = ByteConverter.doubleToStringBytes(elevation);

		// Create byte array for all arguments
		byte[] argBytes = new byte[azBytes.length + elBytes.length];

		// Insert arguments into array
		System.arraycopy(azBytes, 0, argBytes, LOCATION_AZIMUTH, azBytes.length);
		System.arraycopy(elBytes, 0, argBytes, LOCATION_ELEVATION,
				elBytes.length);
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		azimuth = ByteConverter.stringBytesToDouble(argBytes, LOCATION_AZIMUTH);
		elevation = ByteConverter.stringBytesToDouble(argBytes,
				LOCATION_ELEVATION);
	}

	@Override
	protected String argsToString() {
		//@formatter:off
		return 	"Azimuth: " + azimuth + " degrees\n" +
				"Elevation: " + elevation + " degrees";
		//@formatter:on
	}

}
