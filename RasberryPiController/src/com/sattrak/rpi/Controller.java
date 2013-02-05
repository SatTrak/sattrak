package com.sattrak.rpi;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import com.sattrak.rpi.serial.EnvironmentalResponsePacket;
import com.sattrak.rpi.serial.SerialComm;
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

	private static final String COM_PORT = "/dev/ttyS80";

	// ===============================
	// INSTANCE VARIABLES
	// ===============================

	private SerialComm serialComm;
	private DelayQueue<Task> tasks;

	// ===============================
	// CONSTRUCTORS
	// ===============================

	/**
	 * Create a new Controller that communicates via serial on COM_PORT and
	 * executes tasks at specified times
	 * 
	 * @throws Exception
	 *             if a serial port error occurs
	 */
	public Controller() throws Exception {
		// serialComm = new SerialComm(COM_PORT);
		tasks = new DelayQueue<Task>();
		startTaskThread();
		generateTasks();
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EnvironmentalResponsePacket pkt = new EnvironmentalResponsePacket(300,
				50);
		byte[] packetBytes = pkt.toBytes();
		System.out.println("Packet Out: "
				+ ByteConverter.bytesToHex(packetBytes));
		try {
			EnvironmentalResponsePacket pktIn = new EnvironmentalResponsePacket(
					packetBytes);
			System.out.println("Packet In:");
			System.out.println("  temperature: " + pktIn.getTemperature());
			System.out.println("  humidity: " + pktIn.getHumidity());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		try {
			Controller c = new Controller();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

}
