package com.sattrak.rpi.serial;

public enum SerialCommand {
	//@formatter:off
	NULL(0x00),
	ACK(0x01),
	NACK(0x02),
	SET_ORIENTATION(0x03, 0x01),
	READ_ORIENTATION(0x04, 0x05),
	RESPONSE_ORIENTATION(0x05, 0x01),
	READ_ENV(0x06, 0x07),
	RESPONSE_ENV(0x07, 0x01),
	READ_GPS(0x08, 0x09),
	RESPONSE_GPS(0x09, 0x01);
	//@formatter:on

	private byte value;
	private SerialCommand response;

	private SerialCommand(int value) {
		this(value, 0x00);
	}

	private SerialCommand(int value, int response) {
		this.value = (byte) value;
		this.response = fromValue((byte) response);
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

}
