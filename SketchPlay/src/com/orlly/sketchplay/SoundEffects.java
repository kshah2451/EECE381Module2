package com.orlly.sketchplay;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundEffects {
	public static SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	Context context;
	
	static float r_volume = 1.0f;
	static float l_volume = 1.0f;
	public SoundEffects(Context context){
		this.context = context;
	}
	/*
	public static void playEffect(String effect){
		if(effect=="pain"){
			sp.play(soundIds[0], l_volume, r_volume, 1, 1, 1.0f);
		}
		else if(effect=="jump"){
			sp.play(soundIds[1], l_volume, r_volume, 1, 1, 1.0f);
		}
	}*/

}
