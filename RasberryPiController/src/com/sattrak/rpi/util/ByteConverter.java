package com.sattrak.rpi.util;

import java.nio.ByteBuffer;

public class ByteConverter {

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
	 * @return the corresponding double
	 */
	public static double bytesToDouble(byte[] bytes, int offset) {
		byte[] doubleBytes = getByteSubarray(bytes, offset, 8);
		return ByteBuffer.wrap(doubleBytes).getDouble();
	}

}
