package com.sattrak.rpi.serial;

public class NackPacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: Ack'd command
	public static final int LOCATION_NACKD_COMMAND = 0;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private SerialCommand nackdCommand;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public NackPacket(SerialCommand nackdCommand) {
		setCommand(SerialCommand.NACK);
		this.nackdCommand = nackdCommand;
	}

	public NackPacket(byte[] packetBytes) throws Exception {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public SerialCommand getNackdCommand() {
		return nackdCommand;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		byte[] argBytes = new byte[1];
		argBytes[LOCATION_NACKD_COMMAND] = nackdCommand.getValue();
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		nackdCommand = SerialCommand.fromValue(argBytes[LOCATION_NACKD_COMMAND]);
	}

}
