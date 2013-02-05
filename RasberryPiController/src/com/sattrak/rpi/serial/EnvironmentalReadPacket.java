package com.sattrak.rpi.serial;

public class EnvironmentalReadPacket extends SerialPacket {

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public EnvironmentalReadPacket() {
		setCommand(SerialCommand.READ_ENV);
	}

	public EnvironmentalReadPacket(byte[] packetBytes) throws Exception {
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
