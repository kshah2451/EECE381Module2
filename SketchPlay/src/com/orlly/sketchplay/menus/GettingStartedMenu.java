package com.orlly.sketchplay.menus;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.sound.BackgroundMusic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class GettingStartedMenu extends Activity {
	
	private boolean continueMusic = true;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BackgroundMusic.play();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getting_started);
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		continueMusic = false;
//		BackgroundMusic.play();
//		
//	}
//
//	@Override
//	public void onBackPressed() {
//		continueMusic = true;
//		super.onBackPressed();
//	}
//	
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if(!continueMusic) {
//			BackgroundMusic.stop();
//		}	
//	}

}
