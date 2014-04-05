package com.orlly.sketchplay.sound;

import android.media.MediaPlayer;

public class BackgroundMusic {
	
	public static MediaPlayer mPlayer;
	
	private static float volume = 1.0f;
	public static void play() {
		mPlayer.setLooping(true);
		mPlayer.start();
		mPlayer.setVolume( volume, volume);
	}
	
	public static void stop() {
		mPlayer.stop();
	}
	
	public static void release() {
		mPlayer.release();
	}
	
	public static void pause() {
		mPlayer.pause();
	}
	
	public static void setVolume(float new_volume){
		volume = new_volume;
		mPlayer.setVolume(volume, volume);
	}
	public static float getVolume(){
		return volume;
	}
	
	
}
