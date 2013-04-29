package com.sattrak.rpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sattrak.rpi.util.FormatUtil;

public class Camera {
	private static final String VID_FILE = "video.avi";
	private static final String FRAME_FILE = "frame%03d.png";
	private static final String LOG_FILE = "ffmpeg.log";

	private static final String FRAME_RATE = "10/1";
	private static final String FRAME_SIZE = "640x480";

	private String device;

	public Camera(String device) {
		this.device = device;
	}

	/**
	 * Runs a bash script to capture an image from the webcam defined by CAMERA.
	 * 
	 * @param captureTime
	 *            the time at which to begin capturing
	 * @param duration
	 *            the length of the image "exposure" in seconds (must be less
	 *            than 1 min)
	 * @param verbose
	 *            if true, prints the script output to stdout
	 */
	public void captureImage(final Calendar captureTime, final double duration,
			String directory, final boolean verbose) {
		// Get current time and string representation
		Calendar startTime = new GregorianCalendar();
		String startTimestamp = FormatUtil.getAsFilename(startTime);
		if (verbose) {
			System.out.println("Start Time: " + startTimestamp);
		}

		// Time until capture should start (ms)
		long delay = captureTime.getTimeInMillis()
				- startTime.getTimeInMillis();

		// Capture for this long (s) (10 extra seconds just in case)
		double totalDuration = ((double) delay / 1000) + duration + 10;

		// Construct paths for output
		String framesPath = directory + "/frames";
		String logPath = directory + "/" + LOG_FILE;

		// Make sure directories are created
		File file = new File(framesPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		try {
			// Set overcommit memory to true to allow video capture
			new ProcessBuilder(
					Arrays.asList("sysctl", "vm.overcommit_memory=1")).start();

			// Capture the video

			// Build the ffmpeg command
			List<String> captureCmd = Arrays.asList("ffmpeg", "-y", "-v",
					"verbose", "-report", "-an", "-vsync", "2", "-f", "v4l2",
					"-s", FRAME_SIZE, "-i", device, "-vcodec", "copy", "-t",
					FormatUtil.getTimeString(totalDuration), "-timestamp",
					"now", directory + "/" + VID_FILE);
			ProcessBuilder captureBuilder = new ProcessBuilder(captureCmd);
			captureBuilder.redirectErrorStream(true);

			// Set ffreport env variable so that log file is saved at
			// known location
			Map<String, String> env = captureBuilder.environment();
			env.put("FFREPORT", "file=" + logPath);

			// Execute capture and print output if verbose
			Process capture = captureBuilder.start();
			if (verbose) {
				System.out.println("\n Camera capture script output:\n");
				InputStream stdout = capture.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stdout));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			}
			capture.waitFor();
			System.out.println("Video captured. Processing...");

			// Figure out what time the video was actually captured from
			// log file
			FileInputStream fis = new FileInputStream(logPath);
			BufferedReader logFile = new BufferedReader(new InputStreamReader(
					fis));
			String line;
			List<String> timeVals = new ArrayList<String>();
			String gmtOffset = "";
			while ((line = logFile.readLine()) != null) {
				if (line.contains("creation_time")) {
					Matcher numMatcher = Pattern.compile("\\d+").matcher(line);
					while (numMatcher.find()) {
						timeVals.add(numMatcher.group());
					}
					Matcher gmtOffsetMatcher = Pattern.compile("[+-]\\d{4}")
							.matcher(line);
					if (gmtOffsetMatcher.find()) {
						gmtOffset = gmtOffsetMatcher.group();
					}
				}
			}
			int year = Integer.parseInt(timeVals.get(0));
			int month = Integer.parseInt(timeVals.get(1)) - 1;
			int day = Integer.parseInt(timeVals.get(2));
			int hour = Integer.parseInt(timeVals.get(3));
			int minute = Integer.parseInt(timeVals.get(4));
			int second = Integer.parseInt(timeVals.get(5));
			Calendar captureStartTime = new GregorianCalendar(
					TimeZone.getTimeZone("GMT" + gmtOffset));
			captureStartTime.set(year, month, day, hour, minute, second);

			if (verbose) {
				System.out.println("Capture Time: "
						+ FormatUtil.getTimeString(captureStartTime));
			}

			// Get difference between desired and actual video capture
			// times in seconds
			long delayTime = (captureTime.getTimeInMillis() - startTime
					.getTimeInMillis()) / 1000;

			// Close the log file
			logFile.close();
			fis.close();

			// Convert the video to frames

			// Build the ffmpeg command
			List<String> convertCmd = Arrays.asList("ffmpeg", "-y", "-v",
					"verbose", "-i", directory + "/" + VID_FILE, "-ss", ""
							+ delayTime, "-t",
					FormatUtil.getTimeString(duration), "-r", FRAME_RATE,
					framesPath + "/" + FRAME_FILE);
			ProcessBuilder convertBuilder = new ProcessBuilder(convertCmd);
			convertBuilder.redirectErrorStream(true);

			// Execute capture and print output if verbose
			Process convert = convertBuilder.start();
			if (verbose) {
				System.out.println("\n Video->Frames conversion output:\n");
				InputStream stdout = convert.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stdout));
				String convStdoutLine;
				while ((convStdoutLine = reader.readLine()) != null) {
					System.out.println(convStdoutLine);
				}
			}
			convert.waitFor();
			System.out.println("Frames grabbed. Stacking...");

			// Set overcommit memory to false
			new ProcessBuilder(
					Arrays.asList("sysctl", "vm.overcommit_memory=0")).start();

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
