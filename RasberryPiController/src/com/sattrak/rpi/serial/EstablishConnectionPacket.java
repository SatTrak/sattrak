package com.sattrak.rpi.serial;

public class EstablishConnectionPacket extends SerialPacket {

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public EstablishConnectionPacket() {
		setCommand(SerialCommand.ESTABLISH_CONNECTION);
	}

	public EstablishConnectionPacket(byte[] packetBytes)
			throws InvalidPacketException {
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

	@Override
	protected String argsToString() {
		return "";
	}

}
