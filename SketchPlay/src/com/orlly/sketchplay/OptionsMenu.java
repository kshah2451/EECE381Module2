package com.orlly.sketchplay;

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
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * Function called when "How to Play" action bar item is pressed. Launches
	 * HowToPlay activity.
	 * @param item
	 * @return
	 */
	public boolean howToPlayActionBar(MenuItem item) {
		Intent intent = new Intent(this, HowToPlay.class);
		startActivity(intent);
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu); 
	    return super.onCreateOptionsMenu(menu);
	}
	
}
