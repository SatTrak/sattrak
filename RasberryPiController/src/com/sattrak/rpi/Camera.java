package com.sattrak.rpi;

import java.io.IOException;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.FileSink;

public class Camera {

	private String device;

	public Camera(String device) {
		this.device = device;
		Gst.init();
	}

	public void test() throws InterruptedException {
		final Pipeline pipe = new Pipeline("testpipe");
		final Element testsource = ElementFactory.make("videotestsrc",
				"testsource");
		final Element pngEncoder = ElementFactory.make("pngenc", "pngEncoder");
		final Element fsink = ElementFactory.make("filesink", "sink");
		fsink.set("location", "test.png");

		pipe.addMany(testsource, pngEncoder, fsink);
		Element.linkMany(testsource, pngEncoder, fsink);
		pipe.setState(State.PLAYING);
		Thread.sleep(3000);
		pipe.setState(State.PAUSED);
	}

	public void takeSnapshot() {
		final Pipeline pipe = new Pipeline("snapshotpipe");
		final Element videosource = ElementFactory.make("v4l2src", "source");
		videosource.set("device", device);
		final Element colorFilter = ElementFactory.make("capsfilter",
				"colorFilter");
		colorFilter.setCaps(Caps.fromString("video/x-raw-yuv, framerate=30/1"));
		final Element colorConverter = ElementFactory.make("ffmpegcolorspace",
				"colorConverter");
		final Element pngEncoder = ElementFactory.make("pngenc", "pngEncoder");
		// pngEncoder.set("snapshot", "TRUE");
		// final Element fsink = ElementFactory.make("filesink", "sink");
		// fsink.set("location", "images/snap.png");
		final FileSink fsink = new FileSink("sink");
		fsink.setLocation("images/snap.png");

		pipe.addMany(videosource, colorFilter, colorConverter, pngEncoder,
				fsink);
		Element.linkMany(videosource, colorFilter, colorConverter, pngEncoder,
				fsink);

		pipe.setState(State.PLAYING);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipe.setState(State.NULL);
	}

	public void takeMulti() {
		final Pipeline pipe = new Pipeline("multishotpipe");
		final Element videosource = ElementFactory.make("v4l2src", "source");
		videosource.set("device", device);
		final Element colorFilter = ElementFactory.make("capsfilter",
				"colorFilter");
		colorFilter.setCaps(Caps.fromString("video/x-raw-yuv, framerate=30/1"));
		final Element colorConverter = ElementFactory.make("ffmpegcolorspace",
				"colorConverter");
		final Element pngEncoder = ElementFactory.make("pngenc", "pngEncoder");
		final Element mfsink = ElementFactory.make("multifilesink", "sink");
		mfsink.set("location", "frame%d.png");

		pipe.addMany(videosource, colorFilter, colorConverter, pngEncoder,
				mfsink);
		videosource.link(colorFilter, colorConverter, pngEncoder, mfsink);

		pipe.setState(State.PLAYING);
	}

	public void runScript() {
		String cmd = "/bin/bash /home/alex/SatTrak/capture.sh";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			Thread.sleep(5000);
			proc.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void launch() {
		String cmd = "v4l2src device=/dev/video0 ! video/x-raw/yuv,framerate=30/1 ! ffmpegcolorspace ! pngenc ! filesink location=./images/snap.png";
		final Pipeline pipe = Pipeline.launch(cmd);
		pipe.setState(State.PLAYING);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipe.setState(State.NULL);
	}

	public static void main(String[] args) {
		Camera cam = new Camera("/dev/video0");
		cam.runScript();
	}
}
