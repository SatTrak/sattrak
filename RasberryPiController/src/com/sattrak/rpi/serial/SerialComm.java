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
import java.util.TooManyListenersException;

import com.sattrak.rpi.serial.SerialPacket.InvalidPacketException;

public class SerialComm {

	// ===============================
	// CONSTANTS
	// ===============================

	private static final int COMM_TIMEOUT = 2000; // ms
	public static final int BAUD_RATE = 9600;
	public static final int DATA_SIZE = 24; // bytes
	private static final int MAX_RETRIES = 5;
	private static final int RETRY_DELAY = 2000; // ms
	private static final int ARDUINO_INIT_DELAY = 2000; // ms

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
	}

	/**
	 * Send packets to Arduino until we get a valid expected response. Flush
	 * buffer each time.
	 * 
	 * @throws Exception
	 */
	public void establishConnection() throws Exception {
		Thread.sleep(ARDUINO_INIT_DELAY);
		sendAndReceive(new EstablishConnectionPacket());
		// boolean isAck = false;
		// SerialCommand ackd = SerialCommand.NULL;
		// int tries = 0;
		// System.out.println("Establishing connection with Arduino...");
		// Thread.sleep(2000);
		// do {
		// // If max tries was reached, throw an exception
		// if (tries == MAX_SERIAL_RETRIES) {
		// throw new Exception(
		// "Failed to establish connection with Arduino: max retries reached");
		// }
		//
		// // Increment number of tries
		// tries++;
		//
		// // Send a test packet
		// EstablishConnectionPacket testPacket = new
		// EstablishConnectionPacket();
		// send(testPacket.toBytes());
		// System.out.println("Sent packet " + tries);
		// System.out.println("Packet bytes: "
		// + ByteConverter.bytesToHex(testPacket.toBytes()));
		//
		// // Wait for response
		// byte[] packetBytes = receive();
		// System.out.println("Received response " + tries);
		// System.out.println("Packet bytes: "
		// + ByteConverter.bytesToHex(packetBytes));
		//
		// // Check if response matches expected
		// SerialCommand response = SerialPacket.getCommand(packetBytes);
		// System.out.println("Response command: " + response.toString());
		// if (response == SerialCommand.ACK) {
		// AckPacket ackPacket;
		// try {
		// ackPacket = new AckPacket(packetBytes);
		// isAck = true;
		// ackd = ackPacket.getAckdCommand();
		// System.out.println("	Ack'd command: " + ackd.toString());
		// } catch (InvalidPacketException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// // Flush everything from read buffer
		// boolean flushSucceeded = false;
		// while (!flushSucceeded) {
		// try {
		// flushReadBuffer();
		// flushSucceeded = true;
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// // Delay to let the Arduino get ready
		// Thread.sleep(2000);
		// } while (!(isAck && ackd == SerialCommand.ESTABLISH_CONNECTION));

	}

	/**
	 * Receive DATA_SIZE bytes from the serial port.
	 * 
	 * @return the byte array of incoming data
	 * @throws IOException
	 *             if the receive failed
	 */
	public byte[] receive() throws IOException {
		byte[] data = new byte[DATA_SIZE];
		for (int i = 0; i < DATA_SIZE; i++) {
			data[i] = (byte) in.read();
		}
		return data;
	}

	/**
	 * Send the given byte array over the serial port.
	 * 
	 * @param data
	 *            the byte array of outgoing data
	 * @throws IOException
	 *             if the send failed
	 */
	public void send(byte[] data) throws IOException {
		out.write(data);
	}

	/**
	 * Send the given packet over the serial port.
	 * 
	 * @param packet
	 *            the packet to send
	 * @throws IOException
	 *             if the send failed
	 */
	public void send(SerialPacket packet) throws IOException {
		send(packet.toBytes());
	}

	/**
	 * Send a SerialPacket and wait for the expected response. This will retry
	 * up to MAX_SERIAL_RETRIES times.
	 * 
	 * @param outPacket
	 *            the SerialPacket to send
	 * @return the SerialPacket received in response
	 * @throws Exception
	 *             if the expected response was not received
	 */
	public SerialPacket sendAndReceive(SerialPacket outPacket) throws Exception {
		SerialCommand expectedResponse = outPacket.getResponse();
		SerialCommand actualResponse = SerialCommand.NACK;
		SerialPacket inPacket = new NackPacket(outPacket.getCommand());
		byte[] packetBytes = new byte[0];
		boolean correctResponseReceived = false;

		// Send the packet and wait for a response until the response has the
		// expected command or until max retries has been reached
		int retries = 0;
		while (!correctResponseReceived && retries < MAX_RETRIES) {
			try {
				send(outPacket);
				packetBytes = receive();
				actualResponse = SerialPacket.getCommand(packetBytes);
				if (actualResponse == expectedResponse) {
					if (actualResponse == SerialCommand.ACK) {
						correctResponseReceived = new AckPacket(packetBytes).getAckdCommand() == outPacket
								.getCommand();
					} else {
						correctResponseReceived = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			retries++;
			flushReadBuffer();
			Thread.sleep(RETRY_DELAY);
		}

		// Check if the loop exited because the response command matched
		// expected
		if (correctResponseReceived) {
			try {
				// Create a SerialPacket from the incoming bytes
				switch (actualResponse) {
				case ACK:
					inPacket = new AckPacket(packetBytes);
					break;
				case NACK:
					inPacket = new NackPacket(packetBytes);
					break;
				case RESPONSE_ORIENTATION:
					inPacket = new OrientationResponsePacket(packetBytes);
					break;
				case RESPONSE_ENV:
					inPacket = new EnvironmentalResponsePacket(packetBytes);
					break;
				case RESPONSE_GPS:
					inPacket = new GpsResponsePacket(packetBytes);
					break;
				default:
					break;
				}
			} catch (InvalidPacketException e) {
				e.printStackTrace();
			}

			return inPacket;

		} else {
			throw new Exception("Failed to receive " + expectedResponse
					+ " in response to " + outPacket.getCommand());
		}
	}

	/**
	 * Checks if a packet is available on the serial port.
	 * 
	 * @return true if the serial port's input stream has any data available
	 * @throws IOException
	 *             if the input stream could not be obtained or checked
	 */
	public boolean packetAvailable() throws IOException {
		return in.available() > 0;
	}

	/**
	 * Read data from serial port input stream until the stream is empty
	 * 
	 * @throws IOException
	 *             if an error occurs getting the input stream or reading from
	 *             it
	 */
	public void flushReadBuffer() throws IOException {
		System.out.println("Flushing input stream...\n");
		while (packetAvailable()) {
			in.read();
		}
	}
}
