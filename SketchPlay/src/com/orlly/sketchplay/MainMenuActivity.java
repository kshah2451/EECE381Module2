package com.orlly.sketchplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		start_game = (Button)findViewById(R.id.start_button);
		options = (Button)findViewById(R.id.options_button);
		setContentView(R.layout.main_menu);
	}
	
	
	/**
	 * Function called when "play game" button is pressed.
	 * Launches StartGameMenu activity.
	 * @param view
	 */
	public void toImageSelector(View view) {
		Intent intent = new Intent(this, ImageSelectorMenu.class);
		startActivity(intent);
	}
	
	/**
	 * Function called when "options" button is pressed.
	 * Launches OptionsMenu activity.
	 * @param view
	 */
	public void toOptionsActivity(View view) {
		
	}
	
}
