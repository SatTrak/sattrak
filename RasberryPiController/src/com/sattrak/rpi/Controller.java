package com.sattrak.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.concurrent.DelayQueue;

import com.sattrak.rpi.serial.AckPacket;
import com.sattrak.rpi.serial.EnvironmentalReadPacket;
import com.sattrak.rpi.serial.EnvironmentalResponsePacket;
import com.sattrak.rpi.serial.GpsReadPacket;
import com.sattrak.rpi.serial.GpsResponsePacket;
import com.sattrak.rpi.serial.OrientationReadPacket;
import com.sattrak.rpi.serial.OrientationResponsePacket;
import com.sattrak.rpi.serial.OrientationSetPacket;
import com.sattrak.rpi.serial.SerialComm;
import com.sattrak.rpi.serial.SerialComm.IncorrectResponseException;
import com.sattrak.rpi.serial.SerialCommand;
import com.sattrak.rpi.serial.SerialPacket.InvalidPacketException;
import com.sattrak.rpi.util.FormatUtil;

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

	private SerialComm arduino;
	private DelayQueue<Task> tasks;
	private Thread taskThread;

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
		// Initialize the task thread
		startTaskThread();

		// Initialize Arduino communication on SERIAL_PORT
		arduino = new SerialComm(SERIAL_PORT);

		// Establish connection with Arduino
		System.out
				.println("\nTrying to establish connection with Arduino...\n");
		arduino.establishConnection();
		System.out.println("Connection established with Arduino!\n");
	}

	// ===============================
	// PUBLIC METHODS
	// ===============================

	/**
	 * Add the given task to the delay queue
	 * 
	 * @param t
	 *            the task to add
	 * @throws Exception
	 *             if the time at which the task is scheduled for execution is
	 *             sooner than the necessary delay (or if the time has already
	 *             passed)
	 */
	public void addTask(Task t) throws Exception {
		if (t.getDateTime().getTimeInMillis() - System.currentTimeMillis() < Task.MOTOR_DELAY)
			throw new Exception("Not enough time to execute task "
					+ t.getTitle());
		else
			tasks.add(t);
	}

	/**
	 * Execute the given task by sending commands to the Arduino
	 * 
	 * @param t
	 *            the task to execute
	 * @throws Exception
	 */
	public void executeTask(Task t) throws Exception {
		setOrientation(t.getAzimuth(), t.getElevation());

		// Check if task time has already passed
		long timeToExecute = t.getDateTime().getTimeInMillis();
		if (System.currentTimeMillis() > timeToExecute) {
			throw new Exception(
					"Task "
							+ t.getTitle()
							+ " could not execute becuause the orientation was not set in time.");
		}

		while (System.currentTimeMillis() < timeToExecute) {
		}

		System.out.println("Capturing image at "
				+ FormatUtil.getTimeString(new GregorianCalendar()));
		// TODO take picture
	}

	/**
	 * Request environmental data from the Arduino.
	 * 
	 * @return the response packet containing the environmental data
	 * @throws IncorrectResponseException
	 *             if the correct response was not received
	 * @throws InterruptedException
	 *             if the thread was interrupted during the sendAndReceive
	 */
	public EnvironmentalResponsePacket getEnvironmentalData()
			throws IncorrectResponseException, InterruptedException {
		return (EnvironmentalResponsePacket) arduino
				.sendAndReceive(new EnvironmentalReadPacket());
	}

	/**
	 * Request GPS data from the Arduino.
	 * 
	 * @return the response packet containing the GPS data
	 * @throws IncorrectResponseException
	 *             if the correct response was not received
	 * @throws InterruptedException
	 *             if the thread was interrupted during the sendAndReceive
	 * 
	 */
	public GpsResponsePacket getGpsData() throws IncorrectResponseException,
			InterruptedException {
		return (GpsResponsePacket) arduino.sendAndReceive(new GpsReadPacket());
	}

	/**
	 * Request orientation data from the Arduino
	 * 
	 * @return the response packet containing the orientation data
	 * @throws IncorrectResponseException
	 *             if the correct response was not received
	 * @throws InterruptedException
	 *             if the thread was interrupted during the sendAndReceive
	 */
	public OrientationResponsePacket getOrientation()
			throws IncorrectResponseException, InterruptedException {
		return (OrientationResponsePacket) arduino
				.sendAndReceive(new OrientationReadPacket());
	}

	public OrientationResponsePacket setOrientation(double azimuth,
			double elevation) throws IncorrectResponseException,
			InterruptedException, InvalidPacketException, IOException {
		AckPacket ack = new AckPacket(SerialCommand.NULL);

		// Send orientation set packet until it is ack'd
		while (ack.getAckdCommand() != SerialCommand.SET_ORIENTATION) {
			ack = (AckPacket) arduino.sendAndReceive(new OrientationSetPacket(
					azimuth, elevation));
		}

		// Wait for an orientation response packet after the motors have
		// finished moving
		System.out.println("Waiting for orientation to be set...\n");
		OrientationResponsePacket oRespPacket = new OrientationResponsePacket(
				arduino.receive());
		return oRespPacket;
	}

	// ===============================
	// PRIVATE METHODS
	// ===============================

	/**
	 * Start a thread to execute tasks in the delay queue.
	 */
	private void startTaskThread() {
		// Initialize DelayQueue of tasks
		tasks = new DelayQueue<Task>();

		// Create an infinitely looping runnable that that waits until the task
		// DelayQueue has a task whose time is expired and then executes that
		// task and removes it from the queue.
		Runnable executeTasks = new Runnable() {

			@Override
			public void run() {
				System.out.println("Task thread started at "
						+ FormatUtil.getTimeString(new GregorianCalendar()));
				while (true) {
					Task toExecute;
					try {
						toExecute = tasks.take();
						System.out.println("\nExecuting task:");
						System.out.println(toExecute.toString());
						System.out
								.println("Actual Time: "
										+ FormatUtil
												.getTimeString(new GregorianCalendar()));
						executeTask(toExecute);
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
		};

		taskThread = new Thread(executeTasks);
		taskThread.start();
	}

	// ===============================
	// RUNNABLES AND MAIN
	// ===============================

	/**
	 * This Runnable is executed in a thread spawned from main. It acts as a
	 * text-based user interface for sending requests to the Arduino.
	 */
	private static class UserInput implements Runnable {
		//@formatter:off
		private static final String OPTIONS = "\nOptions:\n" +
				"----------\n" +
				"1. Submit a New Task\n" +
				"2. Get Environmental Data\n" +
				"3. Get GPS Location\n" +
				"4. Get Orientation\n" +
				"5. Set Orientation\n" +
				"6. Quit\n";
		//@formatter:on
		private static final String REQUEST_INPUT = "Enter option number: ";

		@Override
		public void run() {
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				final Controller controller = new Controller();

				while (true) {
					System.out.println(OPTIONS);
					System.out.print(REQUEST_INPUT);
					String option = "";
					try {
						option = keyboard.readLine();

						if (option.equals("1")) {
							// Prompt user for task title
							System.out.print("Task title: ");
							String title = keyboard.readLine();

							// Prompt user for date and time
							Calendar date = promptForDate(keyboard);
							Calendar time = promptForTime(keyboard);
							date.set(Calendar.HOUR_OF_DAY,
									time.get(Calendar.HOUR_OF_DAY));
							date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
							date.set(Calendar.SECOND, time.get(Calendar.SECOND));
							date.set(Calendar.MILLISECOND, time.get(0));

							// Prompt user for duration in ms
							long duration = promptForDuration(keyboard);

							// Prompt user for azimuth and elevation angles
							double azimuth = promptForAngle(keyboard, "Azimuth");
							double elevation = promptForAngle(keyboard,
									"Elevation");

							// Create a task
							Task newTask = new Task(title, date, duration,
									azimuth, elevation);

							// Prompt user to confirm task details
							System.out.println("\nTask Details:");
							System.out.println(newTask.toString());
							System.out.print("Submit this task (y/n)?");

							if (keyboard.readLine().equalsIgnoreCase(("y")))
								try {
									controller.addTask(newTask);
								} catch (Exception e) {
									// Catch exception if task time is too soon
									e.printStackTrace();
								}

						} else if (option.equals("2")) {
							// Get env data
							System.out
									.println("Requesting Environmental Data...\n");
							try {
								EnvironmentalResponsePacket envRespPacket = controller
										.getEnvironmentalData();
								System.out
										.println("Environmental Data Received:\n");
								System.out.println(envRespPacket.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (option.equals("3")) {
							// Get gps location
							System.out.println("Requesting GPS Location...\n");
							try {
								GpsResponsePacket gpsRespPacket = controller
										.getGpsData();
								System.out.println("GPS Data Received:\n");
								System.out.println(gpsRespPacket.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (option.equals("4")) {
							System.out
									.println("Requesting Orientation Data...\n");
							try {
								OrientationResponsePacket oRespPacket = controller
										.getOrientation();
								System.out
										.println("Orientation Data Received:\n");
								System.out.println(oRespPacket.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (option.equals("5")) {
							// Prompt user for azimuth and elevation angles
							double azimuth = promptForAngle(keyboard, "Azimuth");
							double elevation = promptForAngle(keyboard,
									"Elevation");
							System.out
									.println("Setting orientation to azimuth = "
											+ azimuth
											+ ", elevation = "
											+ elevation + "...\n");
							OrientationResponsePacket oRespPacket = controller
									.setOrientation(azimuth, elevation);
							System.out.println(oRespPacket.toString());
						} else if (option.equals("6")) {
							System.out.println("Quitting...");
							System.exit(0);
						} else {
							System.out.println("Invalid option!");
						}

						System.out.println("\n\n");
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Error reading input!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}

		}

		private Calendar promptForDate(BufferedReader keyboard)
				throws IOException {
			Calendar date = new GregorianCalendar();
			boolean valid = true;
			do {
				// Prompt and read input
				System.out
						.print("Task date (Format = DD/MM/YYYY, Default = today): ");
				String dateString = keyboard.readLine();

				// If input is empty, return current date as default
				if (!dateString.isEmpty()) {
					// Tokenize input by slashes
					StringTokenizer dateTokenizer = new StringTokenizer(
							dateString, "/");
					String[] dateTokens = new String[dateTokenizer
							.countTokens()];
					int i = 0;
					while (dateTokenizer.hasMoreTokens()) {
						dateTokens[i] = dateTokenizer.nextToken();
						i++;
					}

					// Check that the input format was correct
					if (dateTokens.length == 3) {
						valid = true;

						// Parse input into a Calenar object
						try {
							int day = Integer.parseInt(dateTokens[0]);
							int month = Integer.parseInt(dateTokens[1]) - 1;
							int year = Integer.parseInt(dateTokens[2]);
							date = new GregorianCalendar(year, month, day);
						} catch (NumberFormatException e) {
							valid = false;
							System.out.println("Format must be DD/MM/YYYY");
						}
					} else {
						valid = false;
						System.out.println("Format must be DD/MM/YYYY");
					}
				} else {
					valid = true;
				}
			} while (!valid); // Repeat until valid input is received

			return date;
		}

		private Calendar promptForTime(BufferedReader keyboard)
				throws IOException {
			Calendar time = new GregorianCalendar();
			boolean valid = true;
			do {
				// Prompt and read input
				System.out
						.print("Task time (Format = HH:MM:SS on 24-hour scale): ");
				String dateString = keyboard.readLine();

				// Tokenize input by colons
				StringTokenizer timeTokenizer = new StringTokenizer(dateString,
						":");
				String[] timeTokens = new String[timeTokenizer.countTokens()];
				int i = 0;
				while (timeTokenizer.hasMoreTokens()) {
					timeTokens[i] = timeTokenizer.nextToken();
					i++;
				}

				// Check that the input format was correct
				if (timeTokens.length == 3) {
					valid = true;

					// Parse input into Calendar object
					try {
						int hour = Integer.parseInt(timeTokens[0]);
						int minute = Integer.parseInt(timeTokens[1]);
						int second = Integer.parseInt(timeTokens[2]);

						time.set(Calendar.HOUR_OF_DAY, hour);
						time.set(Calendar.MINUTE, minute);
						time.set(Calendar.SECOND, second);
					} catch (NumberFormatException e) {
						valid = false;
						System.out.println("Format must be HH:MM:SS");
					}
				} else {
					valid = false;
					System.out.println("Format must be HH:MM:SS");
				}
			} while (!valid);

			return time;
		}

		private long promptForDuration(BufferedReader keyboard)
				throws IOException {
			long duration = 0;
			boolean valid = true;
			do {
				System.out.print("Exposure duration (ms): ");
				String durationString = keyboard.readLine();
				try {
					duration = Long.parseLong(durationString);
					valid = true;
				} catch (NumberFormatException e) {
					valid = false;
					System.out.println("Format must be an integer number");
				}
			} while (!valid);

			return duration;
		}

		private double promptForAngle(BufferedReader keyboard, String angleName)
				throws IOException {
			double angle = 0;
			boolean valid = true;
			do {
				System.out.print(angleName + " angle (degrees): ");
				String angleString = keyboard.readLine();
				try {
					angle = Double.parseDouble(angleString);
					valid = true;
				} catch (NumberFormatException e) {
					valid = false;
					System.out
							.println("Format must be an floating point number");
				}
			} while (!valid);

			return angle;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Start UI thread
		new Thread(new UserInput()).start();

	}

}
