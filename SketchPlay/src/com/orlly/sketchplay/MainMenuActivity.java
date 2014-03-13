package com.orlly.sketchplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	
	/**
	 * Play Game button
	 */
	Button play_game;
	
	/**
	 * Options button
	 */
	Button options;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		play_game = (Button)findViewById(R.id.play_game_button);
		options = (Button)findViewById(R.id.options_button);
		setContentView(R.layout.main_menu);
	}
	
	
	/**
	 * Function called when "play game" button is pressed.
	 * Launches StartGameMenu activity.
	 * @param view
	 */
	public void toStartGameActivity(View view) {
		Intent intent = new Intent(this, StartGameMenu.class);
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
