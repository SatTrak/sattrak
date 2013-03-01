package com.sattrak.rpi.serial;

public enum SerialCommand {
	//@formatter:off
	NULL(0x00, "Null"),
	ACK(0x01, "Ack"),
	NACK(0x02, "Nack"),
	SET_ORIENTATION(0x03, 0x01, "Orientation Set"),
	READ_ORIENTATION(0x04, 0x05, "Orientation Read"),
	RESPONSE_ORIENTATION(0x05, 0x01, "Orientation Response"),
	READ_ENV(0x06, 0x07, "Environmental Read"),
	RESPONSE_ENV(0x07, 0x01, "Environmental Response"),
	READ_GPS(0x08, 0x09, "GPS Read"),
	RESPONSE_GPS(0x09, 0x01, "GPS Response"),
	ESTABLISH_CONNECTION(0x0A, 0x01, "Establish Connection");
	//@formatter:on

	private byte value;
	private SerialCommand response;
	private String name;

	private SerialCommand(int value, String name) {
		this(value, 0x00, name);
	}

	private SerialCommand(int value, int response, String name) {
		this.value = (byte) value;
		this.response = fromValue((byte) response);
		this.name = name;
	}

	public static SerialCommand fromValue(byte value) {
		switch (value) {
		case 0x01:
			return ACK;
		case 0x02:
			return NACK;
		case 0x03:
			return SET_ORIENTATION;
		case 0x04:
			return READ_ORIENTATION;
		case 0x05:
			return RESPONSE_ORIENTATION;
		case 0x06:
			return READ_ENV;
		case 0x07:
			return RESPONSE_ENV;
		case 0x08:
			return READ_GPS;
		case 0x09:
			return RESPONSE_GPS;
		case 0x0A:
			return ESTABLISH_CONNECTION;
		default:
			return NULL;
		}
	}

	public byte getValue() {
		return value;
	}

	public SerialCommand getResponse() {
		return response;
	}

	@Override
	public String toString() {
		return name;
	}

}
