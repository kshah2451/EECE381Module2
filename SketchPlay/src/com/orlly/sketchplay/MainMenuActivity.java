package com.orlly.sketchplay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {

	/**
	 * Start Game button
	 */
	Button start_game;

	/**
	 * Options button
	 */
	Button options;
	
	/**
	 * How To Play button
	 */
	Button getting_started;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		start_game = (Button) findViewById(R.id.start_button);
		options = (Button) findViewById(R.id.options_button);
		getting_started = (Button) findViewById(R.id.getting_started_button);
		setContentView(R.layout.main_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	

	/**
	 * Function called when "start game" button is pressed. Launches
	 * ImageSelectorMenu activity.
	 * 
	 * @param view
	 */
	public void toImageSelector(View view) {
		Intent intent = new Intent(this, ImageSelectorMenu.class);
		startActivity(intent);
	}

	/**
	 * Function called when "options" button is pressed. Launches OptionsMenu
	 * activity.
	 * 
	 * @param view
	 */
	public void toOptionsActivity(View view) {
		Intent intent = new Intent(this, OptionsMenu.class);
		startActivity(intent);
	}

	/**
	 * Function called when "Getting Started" button is pressed. Launches
	 * GettingStarted activity.
	 * 
	 * @param view
	 */
	public void toGettingStarted(View view) {
		Intent intent = new Intent(this, GettingStarted.class);
		startActivity(intent);
	}
	
	/**
	 * Function called when "Getting Started" action bar item is pressed. Launches
	 * GettingStarted activity.
	 * @param item
	 * @return
	 */
	public boolean gettingStartedActionBar(MenuItem item) {
		Intent intent = new Intent(this, GettingStarted.class);
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
