package com.sattrak.rpi.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public abstract class SerialComm {

	// ===============================
	// CONSTANTS
	// ===============================
	private static final int COMM_TIMEOUT = 2000;
	public static final int BAUD_RATE = 9600;
	public static final int DATA_SIZE = 24;

	// ===============================
	// INSTANCE VARIABLES
	// ===============================
	private SerialPort serialPort;
	private InputStream in;
	private OutputStream out;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	public SerialComm(String portName) throws Exception {
		connect(portName);
	}

	// ===============================
	// ABSTRACT METHODS
	// ===============================

	public abstract void handleRxData(byte[] rxBytes);

	// ===============================
	// GETTERS
	// ===============================

	public SerialPort getSerialPort() {
		return serialPort;
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
	 * @throws TooManyListenersException
	 * @throws IOException
	 * 
	 */
	public void connect(String portName) throws NoSuchPortException,
			PortInUseException, UnsupportedCommOperationException,
			TooManyListenersException, IOException {
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

		in = serialPort.getInputStream();
		out = serialPort.getOutputStream();

		// Create and set an event listener for the serial port to act on
		// received data
		SerialPortEventListener packetReceivedListener = new SerialPortEventListener() {

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
					byte[] rxBytes;
					try {
						rxBytes = read();
						handleRxData(rxBytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

		// serialPort.addEventListener(packetReceivedListener);
		// serialPort.notifyOnDataAvailable(true);
	}

	/**
	 * Read DATA_SIZE bytes from the serial port.
	 * 
	 * @return the byte array of incoming data
	 * @throws IOException
	 *             if the read failed or the input stream could not be obtained
	 */
	public byte[] read() throws IOException {
		byte[] data = new byte[DATA_SIZE];
		for (int i = 0; i < DATA_SIZE; i++) {
			data[i] = (byte) in.read();
		}
		// in.read(data);
		return data;
	}

	/**
	 * Write the given byte array to the serial port.
	 * 
	 * @param data
	 *            the byte array of outgoing data
	 * @return true if the write was successful
	 * @throws IOException
	 *             if the write failed or the output stream could not be
	 *             obtained
	 */
	public void write(byte[] data) throws IOException {
		out.write(data);
	}

	/**
	 * Checks if a packet is available on the serial port.
	 * 
	 * @return true if the serial port's input stream has any data available
	 * @throws IOException
	 *             if the input stream could not be obtained or checked
	 */
	public boolean packetAvailable() throws IOException {
		InputStream in = serialPort.getInputStream();
		return in.available() > 0;
	}
}
