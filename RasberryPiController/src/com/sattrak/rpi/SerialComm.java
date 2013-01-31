package com.sattrak.rpi;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

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
	 * @throws Exception
	 */
	public void connect(String portName) throws Exception {
		// Get the identifier for the intended port
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);

		// Throw an exception of that port is already being used
		if (portIdentifier.isCurrentlyOwned())
			throw new Exception("Port " + portName + " is currently in use");

		// Open the port
		CommPort commPort = portIdentifier.open(this.getClass().getName(),
				COMM_TIMEOUT);

		// Check if it is a serial port
		if (commPort instanceof SerialPort) {
			serialPort = (SerialPort) commPort;

			// Set the parameters
			serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} else {
			throw new Exception("Only serial ports are handled");
		}
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

	// ===============================
	// PRIVATE METHODS
	// ===============================

}
