package com.orlly.sketchplay.menus;


import com.orlly.sketchplay.menus.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OptionsMenu extends Activity {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);
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
