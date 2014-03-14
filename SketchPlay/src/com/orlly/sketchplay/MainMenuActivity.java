package com.orlly.sketchplay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
	Button how_to_play;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		start_game = (Button) findViewById(R.id.start_button);
		options = (Button) findViewById(R.id.options_button);
		how_to_play = (Button) findViewById(R.id.how_to_play_button);
		setContentView(R.layout.main_menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
	 * Function called when "How to Play" button is pressed. Launches
	 * HowToPlay activity.
	 * 
	 * @param view
	 */
	public void toHowToPlay(View view) {
		Intent intent = new Intent(this, HowToPlay.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu); 
	    return super.onCreateOptionsMenu(menu);
	}
	
	

}
