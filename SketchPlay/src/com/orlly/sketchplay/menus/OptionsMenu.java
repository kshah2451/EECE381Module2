package com.orlly.sketchplay.menus;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import com.orlly.sketchplay.sound.BackgroundMusic;
import com.orlly.sketchplay.sound.SoundEffects;

public class OptionsMenu extends Activity {
	
	private SeekBar soundeffects_volume;
	private SeekBar music_volume;
	private Spinner theme_spinner;
	private CheckBox tilt_option;
	private MyApplication application;
	private SeekBar tilt_sensitivity;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		application = (MyApplication)getApplication();
		
		theme_spinner = (Spinner)findViewById(R.id.theme_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.theme_options_array, android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		theme_spinner.setAdapter(adapter);

		soundeffects_volume = (SeekBar) findViewById(R.id.soundeffects_volume);
		soundeffects_volume.setProgress((int)(SoundEffects.getVolume()*100));
		
		music_volume = (SeekBar) findViewById(R.id.music_volume);
		music_volume.setProgress((int)(BackgroundMusic.getVolume()*100));
		
		tilt_option = (CheckBox) findViewById(R.id.tilt_option);
		tilt_option.setChecked(application.getTilt());
		
		tilt_sensitivity = (SeekBar) findViewById(R.id.tilt_sensitivity);
		tilt_sensitivity.setProgress(application.getTiltSeekbar());
		
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
		theme_spinner.setOnItemSelectedListener(
			 new OnItemSelectedListener(){
				
				 	@Override
				    public void onItemSelected(AdapterView<?> parent, View view, 
				            int pos, long id) {
				        // An item was selected. You can retrieve the selected item using
				        // parent.getItemAtPosition(pos)
				    	application.theme =  (String) parent.getItemAtPosition(pos);
				    	Log.d("theme", application.theme);
				 	}
					
				 	@Override
				    public void onNothingSelected(AdapterView<?> parent) {
				        // Another interface callback
				 		application.theme = "Forest";
				    	Log.d("theme", application.theme);

				    }
				 
				 
			 });
		
		tilt_option.setOnCheckedChangeListener(
				new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
						application.toggleTilt(isChecked);
					}
				}
		);
		
		tilt_sensitivity.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener(){

					@Override
					public void onProgressChanged(SeekBar seekBar, int sensitivity, boolean fromUser) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						float new_sensitivity;
						application.setTiltSeekbar(seekBar.getProgress());
						new_sensitivity = (float) (125/seekBar.getProgress());
						application.setTiltSensitivity(new_sensitivity);
					}
				}
		);
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
