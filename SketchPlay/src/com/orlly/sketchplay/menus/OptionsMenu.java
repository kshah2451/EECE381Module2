package com.orlly.sketchplay.menus;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.sound.BackgroundMusic;
import com.orlly.sketchplay.sound.SoundEffects;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class OptionsMenu extends Activity {
	
	private SeekBar soundeffects_volume;
	private SeekBar music_volume;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		soundeffects_volume = (SeekBar) findViewById(R.id.soundeffects_volume);
		music_volume = (SeekBar) findViewById(R.id.music_volume);
		
		music_volume.setProgress((int)(BackgroundMusic.getVolume()*100));
		soundeffects_volume.setProgress((int)(SoundEffects.getVolume()*100));
		
		music_volume.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {
					private float new_volume;
					@Override
					public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser) {
					}
					@Override
					public void onStartTrackingTouch(SeekBar volume) {			
					}
					@Override
					public void onStopTrackingTouch(SeekBar volume) {
						new_volume = volume.getProgress();
						new_volume = new_volume/100;
						BackgroundMusic.setVolume(new_volume);
					}
		});
		
		soundeffects_volume.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {
					private float new_volume;
					@Override
					public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser) {
					}
					@Override
					public void onStartTrackingTouch(SeekBar volume) {			
					}
					@Override
					public void onStopTrackingTouch(SeekBar volume) {
						new_volume = volume.getProgress();
						new_volume = new_volume/100;
						SoundEffects.setVolume(new_volume);
					}
				});
	}
	

//	@Override
//	protected void onResume() {
//		BackgroundMusic.play();
//		super.onResume();
//	}
//	
//	
//	@Override
//	public void onBackPressed() {
//		BackgroundMusic.play();
//		super.onBackPressed();
//	}
//
//
//	@Override
//	protected void onStop() {
//		BackgroundMusic.stop();
//		super.onStop();
//	}
	
}
