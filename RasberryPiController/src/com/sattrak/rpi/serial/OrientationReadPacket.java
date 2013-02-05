package com.sattrak.rpi.serial;

public class OrientationReadPacket extends SerialPacket {

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public OrientationReadPacket() {
		setCommand(SerialCommand.READ_ORIENTATION);
	}

	public OrientationReadPacket(byte[] packetBytes) throws Exception {
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
