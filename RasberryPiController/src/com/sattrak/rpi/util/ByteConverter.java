package com.sattrak.rpi.util;

import java.nio.ByteBuffer;

public class ByteConverter {

	public static final int LENGTH_DOUBLE = 8;

	/**
	 * Private constructor so the class can't be instantiated.
	 */
	private ByteConverter() {

	}

	/**
	 * Return a sub-array of the given byte array.
	 * 
	 * @param array
	 *            the source byte array
	 * @param offset
	 *            the index to start the sub-array
	 * @param length
	 *            the length of the sub-array
	 * @return the sub-array
	 */
	public static byte[] getByteSubarray(byte[] array, int offset, int length) {
		byte[] subarray = new byte[length];
		System.arraycopy(array, offset, subarray, 0, length);
		return subarray;
	}

	/**
	 * Convert the given byte array to a hexidecimal string.
	 * 
	 * @param bytes
	 *            the byte array to convert
	 * @return the corresponding hex string
	 */
	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Convert a double into a byte array.
	 * 
	 * @param value
	 *            the double
	 * @return the equivalent byte array
	 */
	public static byte[] doubleToBytes(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	/**
	 * Convert a byte array into a double.
	 * 
	 * @param bytes
	 *            the byte array
	 * @param offset
	 *            the byte at which to start extracting the double
	 * @return the corresponding double
	 */
	public static double bytesToDouble(byte[] bytes, int offset) {
		return ByteBuffer.wrap(bytes).getDouble(offset);
	}

	/**
	 * Convert a double into a byte array, where the bytes are a string
	 * representation of the double. The width of the string is LENGTH_DOUBLE
	 * characters (including the decimal point), and the number of decimal
	 * places is 2. The string is zero-padded at the beginning.
	 * 
	 * @param value
	 *            the double to convert
	 * @return the equivalent byte array of the double as a string
	 */
	public static byte[] doubleToStringBytes(double value) {
		String valueString = String.format("%0" + (LENGTH_DOUBLE - 1) + ".2f",
				value);
		byte[] bytes = new byte[LENGTH_DOUBLE];
		System.arraycopy(valueString.getBytes(), 0, bytes, 0,
				valueString.length());
		// Make sure last element is a null term
		bytes[LENGTH_DOUBLE - 1] = 0;
		return bytes;
	}

	/**
	 * Convert a subset of bytes, starting at offset with length LENGTH_DOUBLE,
	 * into a double. The double should be represented as a string in the byte
	 * array.
	 * 
	 * @param bytes
	 *            the byte array
	 * @param offset
	 *            the byte at which to start extracting the double
	 * @return the corresponding double
	 */
	public static double stringBytesToDouble(byte[] bytes, int offset) {
		return Double.parseDouble(new String(bytes, offset, LENGTH_DOUBLE));
	}

	/**
	 * Convert a short into a byte array.
	 * 
	 * @param value
	 *            the short
	 * @return the equivalent byte array
	 */
	public static byte[] shortToBytes(short value) {
		byte[] bytes = new byte[2];
		ByteBuffer.wrap(bytes).putShort(value);
		return bytes;
	}

	/**
	 * Convert a byte array into a short.
	 * 
	 * @param bytes
	 *            the byte array
	 * @return the corresponding short
	 */
	public static short bytesToShort(byte[] bytes, int offset) {
		return ByteBuffer.wrap(bytes).getShort(offset);
	}

}
