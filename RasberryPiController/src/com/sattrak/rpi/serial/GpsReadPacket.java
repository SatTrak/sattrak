package com.sattrak.rpi.serial;

public class GpsReadPacket extends SerialPacket {

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public GpsReadPacket() {
		setCommand(SerialCommand.READ_GPS);
	}

	public GpsReadPacket(byte[] packetBytes) throws Exception {
		fromBytes(packetBytes);
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		// Return an empty byte array because there are no arguments
		return new byte[0];
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		// Do nothing because there are no arguments
	}

}
