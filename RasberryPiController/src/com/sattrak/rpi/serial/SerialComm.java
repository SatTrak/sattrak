package com.sattrak.rpi.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialComm {

	// ===============================
	// CONSTANTS
	// ===============================
	private static final int COMM_TIMEOUT = 2000;
	private static final int BAUD_RATE = 9600;
	private static final int DATA_SIZE = 1024;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private SerialPort serialPort;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public SerialComm(String portName) throws Exception {
		connect(portName);
	}

	// ===============================
	// PUBLIC METHODS
	// ===============================

	/**
	 * Connect to the specified serial port
	 * 
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * 
	 */
	public void connect(String portName) throws NoSuchPortException,
			PortInUseException, UnsupportedCommOperationException {
		// Get the identifier for the intended port
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);

		// Open the port
		CommPort commPort = portIdentifier.open(this.getClass().getName(),
				COMM_TIMEOUT);

		// Set the parameters
		serialPort = (SerialPort) commPort;
		serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	}

	/**
	 * Read DATA_SIZE bytes from the serial port.
	 * 
	 * @return the byte array of incoming data
	 */
	public byte[] read() {
		byte[] data = new byte[DATA_SIZE];
		InputStream in;
		try {
			in = serialPort.getInputStream();
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Write the given byte array to the serial port.
	 * 
	 * @param data
	 *            the byte array of outgoing data
	 * @return true if the write was successful
	 */
	public boolean write(byte[] data) {
		OutputStream out;
		boolean success = false;
		try {
			out = serialPort.getOutputStream();
			out.write(data);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}
}
