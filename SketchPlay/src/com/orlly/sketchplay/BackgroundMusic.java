package com.orlly.sketchplay;

import android.media.MediaPlayer;

public class BackgroundMusic {
	
	public static MediaPlayer mPlayer;
	
	public static void play() {
		mPlayer.setLooping(true);
		mPlayer.start();
	}
	
	public static void stop() {
		mPlayer.stop();
	}
	
}
