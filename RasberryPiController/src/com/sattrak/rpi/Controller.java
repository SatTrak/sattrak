package com.sattrak.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import com.sattrak.rpi.serial.AckPacket;
import com.sattrak.rpi.serial.EnvironmentalReadPacket;
import com.sattrak.rpi.serial.EnvironmentalResponsePacket;
import com.sattrak.rpi.serial.GpsReadPacket;
import com.sattrak.rpi.serial.GpsResponsePacket;
import com.sattrak.rpi.serial.NackPacket;
import com.sattrak.rpi.serial.OrientationResponsePacket;
import com.sattrak.rpi.serial.SerialComm;
import com.sattrak.rpi.serial.SerialCommand;
import com.sattrak.rpi.serial.SerialPacket;
import com.sattrak.rpi.serial.SerialPacket.InvalidPacketException;
import com.sattrak.rpi.util.ByteConverter;

/**
 * A class that holds a delay queue of tasks to be completed. This queue is
 * handled by a thread to automatically run each task at its specified time. It
 * communicates via serial over COM_PORT for tasks that require IO.
 * 
 * @author Alex Thompson
 * 
 */
public class Controller {

	// ===============================
	// CONSTANTS
	// ===============================

	private static final String SERIAL_PORT = "/dev/ttyS80";

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private ArduinoComm arduino;
	private DelayQueue<Task> tasks;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	/**
	 * Create a new Controller that communicates via serial on SERIAL_PORT and
	 * executes tasks at specified times
	 * 
	 * @throws Exception
	 *             if a serial port error occurs
	 */
	public Controller() throws Exception {
		arduino = new ArduinoComm(SERIAL_PORT);
		tasks = new DelayQueue<Task>();
		// startTaskThread();
		// generateTasks();
	}

	// ===============================
	// PUBLIC METHODS
	// ===============================

	/**
	 * Add the given task to the delay queue
	 * 
	 * @param t
	 *            the task to add
	 */
	public void addTask(Task t) {
		tasks.add(t);
	}

	/**
	 * Write the given SerialPacket to the port
	 * 
	 * @param packet
	 */
	public void writePacket(SerialPacket packet) {
		arduino.write(packet.toBytes());
	}

	public void readPacket() {
		byte[] packetBytes = arduino.read();
		handlePacket(packetBytes);
	}

	// ===============================
	// PRIVATE METHODS
	// ===============================

	/**
	 * Create a runnable to execute tasks in the delay queue and start a new
	 * thread with this runnable.
	 */
	private void startTaskThread() {
		Runnable executeTask = new Runnable() {

			@Override
			public void run() {
				System.out.println("Task thread started at "
						+ getTimeString(new GregorianCalendar()));
				while (true) {
					Task toExecute;
					try {
						toExecute = tasks.take();
						System.out.println("Executing task:");
						System.out.println("  Intended Time: "
								+ getTimeString(toExecute.getDateTime()));
						System.out.println("  Actual Time: "
								+ getTimeString(new GregorianCalendar()));
						System.out.println("  Duration: "
								+ toExecute.getDuration() + " ms");
						System.out.println("  Azimuth: "
								+ toExecute.getAzimuth() + " degrees");
						System.out.println("  Elevation: "
								+ toExecute.getElevation() + " degrees");
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		};

		new Thread(executeTask).start();
	}

	/**
	 * Handle a packet received from the Arduino. How it is handled depends on
	 * the command.
	 * 
	 * @param packetBytes
	 *            the byte array received
	 */
	private void handlePacket(byte[] packetBytes) {
		SerialCommand command = SerialPacket.getCommand(packetBytes);
		String commandString = command.toString();
		String argString = "";
		try {
			// Handle all commands that could have been sent by the Arduino
			switch (command) {
			case ACK:
				AckPacket ackPacket = new AckPacket(packetBytes);
				argString = "Ack'd Command: "
						+ ackPacket.getAckdCommand().toString();
				// TODO
				break;
			case NACK:
				NackPacket nackPacket = new NackPacket(packetBytes);
				argString = "Nack'd Command: "
						+ nackPacket.getNackdCommand().toString();
				// TODO
				break;
			case RESPONSE_ORIENTATION:
				OrientationResponsePacket oRespPacket = new OrientationResponsePacket(
						packetBytes);
				argString = "Azimuth: " + oRespPacket.getAzimuth()
						+ "\nElevation: " + oRespPacket.getElevation();
				// TODO
				break;
			case RESPONSE_ENV:
				EnvironmentalResponsePacket envRespPacket = new EnvironmentalResponsePacket(
						packetBytes);
				argString = "Temperature: " + envRespPacket.getTemperature()
						+ " degrees\nHumidity: " + envRespPacket.getHumidity()
						+ "%";
				// TODO
				break;
			case RESPONSE_GPS:
				GpsResponsePacket gpsRespPacket = new GpsResponsePacket(
						packetBytes);
				argString = "Latitude: " + gpsRespPacket.getLatitude()
						+ " degrees\nLongitude: "
						+ gpsRespPacket.getLongitude() + "degrees";
				// TODO
				break;
			default:
				break;
			}

			System.out.println("\nReceived Packet");
			System.out.println("Command: " + commandString);
			System.out.println("Arguments: " + argString);
			System.out.println("Raw bytes: "
					+ ByteConverter.bytesToHex(packetBytes) + "\n");
		} catch (InvalidPacketException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	// ===============================
	// INNER CLASSES
	// ===============================

	/**
	 * Implementation of SerialComm to communicate with Arduino. Implements
	 * handlePacket method.
	 * 
	 * @author Alex Thompson
	 * 
	 */
	public class ArduinoComm extends SerialComm {

		public ArduinoComm(String portName) throws Exception {
			super(portName);
		}

		@Override
		public void handleRxData(byte[] rxBytes) {
			handlePacket(rxBytes);
		}

	}

	// ===============================
	// DEBUGGING METHODS
	// ===============================

	/**
	 * Debugging method for generating arbitrary tasks
	 */
	private void generateTasks() {
		Calendar dateTime0 = new GregorianCalendar();
		dateTime0.set(Calendar.MINUTE, dateTime0.get(Calendar.MINUTE) + 1);
		Task t0 = new Task(dateTime0, 100, 345, 175);
		System.out.println("Task delay: " + t0.getDelay(TimeUnit.MILLISECONDS));
		addTask(t0);
	}

	/**
	 * Debugging method for converting a Calendar time into a string
	 * 
	 * @param time
	 * @return
	 */
	private String getTimeString(Calendar time) {
		return time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + ":"
				+ time.get(Calendar.SECOND);
	}

	private static class UserInput implements Runnable {
		private static final String OPTIONS = "\nOptions:\n----------\n1. Get Environmental Data\n2. Get GPS Location\n";
		private static final String REQUEST_INPUT = "Enter option number: ";

		@Override
		public void run() {
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				final Controller controller = new Controller();

				while (true) {
					System.out.println(OPTIONS);
					System.out.println(REQUEST_INPUT);
					String option = "";
					try {
						option = keyboard.readLine();

						if (option.equals("1")) {
							// Get env data
							System.out
									.println("Requesting Environmental Data...\n");
							EnvironmentalReadPacket packet = new EnvironmentalReadPacket();
							controller.writePacket(packet);
							controller.readPacket();

						} else if (option.equals("2")) {
							// Get gps location
							System.out.println("Requesting GPS Location...\n");
							GpsReadPacket packet = new GpsReadPacket();
							controller.writePacket(packet);
							controller.readPacket();
						} else {
							System.out.println("Invalid option!");
						}

						System.out.println("\nPerform another task? (y/n)");
						String restart = keyboard.readLine();
						if (restart.equals("y")) {
							System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
						} else if (restart.equals("n")) {
							return;
						} else {
							System.out.println("Invalid input!");
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Error reading input!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new UserInput()).start();

	}

}
