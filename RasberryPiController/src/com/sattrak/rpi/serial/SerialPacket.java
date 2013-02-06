package com.sattrak.rpi.serial;

public abstract class SerialPacket {

	// ===============================
	// CONSTANTS
	// ===============================
	public static final byte HEADER = (byte) 0xAA;

	public static final int LOCATION_HEADER = 0;
	public static final int LOCATION_COMMAND = 1;
	public static final int LOCATION_ARGS = 2;

	public static final int LENGTH_HEADER = 1;
	public static final int LENGTH_COMMAND = 1;
	public static final int LENGTH_CHECKSUM = 0;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private SerialCommand command;

	// ===============================
	// ABSTRACT METHODS
	// ===============================

	/**
	 * Convert the argument fields into a byte array.
	 * 
	 * @return the byte array of all arguments
	 */
	protected abstract byte[] argsToBytes();

	/**
	 * Set the appropriate argument fields using the values from the given byte
	 * array.
	 * 
	 * @param argBytes
	 *            the byte array of all arguments
	 */
	protected abstract void bytesToArgs(byte[] argBytes);

	// ===============================
	// GETTERS
	// ===============================

	public SerialCommand getCommand() {
		return command;
	}

	public SerialCommand getResponse() {
		return command.getResponse();
	}

	// ===============================
	// SETTERS
	// ===============================

	protected void setCommand(SerialCommand command) {
		this.command = command;
	}

	// ===============================
	// PUBLIC METHODS
	// ===============================

	public byte[] toBytes() {
		// Convert arguments to bytes
		byte[] argBytes = argsToBytes();

		// Create byte array of correct size including arguments
		byte[] packetBytes = new byte[LENGTH_HEADER + LENGTH_COMMAND
				+ argBytes.length + LENGTH_CHECKSUM];

		// Insert header, command and arguments
		packetBytes[LOCATION_HEADER] = HEADER;
		packetBytes[LOCATION_COMMAND] = command.getValue();
		System.arraycopy(argBytes, 0, packetBytes, LOCATION_ARGS,
				argBytes.length);

		// Insert checksum
		// TODO

		return packetBytes;
	}

	public void fromBytes(byte[] packetBytes) throws InvalidPacketException {
		// Validate the packet and extract the argument bytes
		byte[] argBytes = validateAndGetArgBytes(packetBytes);

		// Set the command and arguments
		command = SerialCommand.fromValue(packetBytes[LOCATION_COMMAND]);
		bytesToArgs(argBytes);
	}

	public static SerialCommand getCommand(byte[] packetBytes) {
		return SerialCommand.fromValue(packetBytes[LOCATION_COMMAND]);
	}

	// ===============================
	// PRIVATE METHODS
	// ===============================

	private byte[] validateAndGetArgBytes(byte[] packetBytes)
			throws InvalidPacketException {
		// Validate header
		if (!hasHeader(packetBytes))
			throw new InvalidPacketException("Incorrect header");

		// Validate checksum
		if (!isChecksumValid(packetBytes))
			throw new InvalidPacketException("checksum failed");

		// Return byte array containing argument values
		// Also subtract checksum length when that is implemented
		int argLength = packetBytes.length - LENGTH_HEADER - LENGTH_COMMAND
				- LENGTH_CHECKSUM;
		byte[] argBytes = new byte[argLength];
		System.arraycopy(packetBytes, LOCATION_ARGS, argBytes, 0, argLength);
		return argBytes;
	}

	private boolean hasHeader(byte[] packetBytes) {
		return packetBytes[LOCATION_HEADER] == HEADER;
	}

	private boolean isChecksumValid(byte[] packetBytes) {
		// Validate the checksum
		// TODO
		return true;
	}

	// ===============================
	// CUSTOM EXCEPTIONS
	// ===============================

	public class InvalidPacketException extends Exception {
		public InvalidPacketException(String message) {
			super(message);
		}
	}

}
