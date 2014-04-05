package com.orlly.sketchplay.sound;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundEffects {
	public static SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	
	static float volume = 1.0f;
	
	public static void setVolume(float new_volume){
		volume = new_volume;
	}
	
	public static float getVolume(){
		return volume;
	}
}
