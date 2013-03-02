package com.sattrak.rpi.serial;

public class AckPacket extends SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================

	// Argument 1: Ack'd command
	public static final int LOCATION_ACKD_COMMAND = 0;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private SerialCommand ackdCommand;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public AckPacket(SerialCommand ackdCommand) {
		setCommand(SerialCommand.ACK);
		this.ackdCommand = ackdCommand;
	}

	public AckPacket(byte[] packetBytes) throws InvalidPacketException {
		fromBytes(packetBytes);
	}

	// ===============================
	// GETTERS
	// ===============================

	public SerialCommand getAckdCommand() {
		return ackdCommand;
	}

	// ===============================
	// OVERRIDDEN METHODS
	// ===============================

	@Override
	protected byte[] argsToBytes() {
		byte[] argBytes = new byte[1];
		argBytes[LOCATION_ACKD_COMMAND] = ackdCommand.getValue();
		return argBytes;
	}

	@Override
	protected void bytesToArgs(byte[] argBytes) {
		ackdCommand = SerialCommand.fromValue(argBytes[LOCATION_ACKD_COMMAND]);
	}

	@Override
	protected String argsToString() {
		return "Ack'd Command: " + ackdCommand.toString();
	}

}
