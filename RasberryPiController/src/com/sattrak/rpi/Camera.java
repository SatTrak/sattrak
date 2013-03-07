package com.sattrak.rpi;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Camera {

	private String device;

	public Camera(String device) {
		this.device = device;
		Gst.init();
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
		pngEncoder.set("snapshot", "TRUE");
		final Element mfsink = ElementFactory.make("filesink", "sink");
		mfsink.set("location", "snap.png");

		pipe.addMany(videosource, colorFilter, colorConverter, pngEncoder,
				mfsink);
		videosource.link(colorFilter, colorConverter, pngEncoder, mfsink);

		pipe.setState(State.PLAYING);
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

	public static void main(String[] args) {
		Camera cam = new Camera("/dev/video0");
		cam.takeSnapshot();

	}
}
