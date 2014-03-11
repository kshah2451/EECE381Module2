package com.orlly.sketchplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	
	Button play_game;
	Button options;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		play_game = (Button)findViewById(R.id.play_game_button);
		options = (Button)findViewById(R.id.options_button);
		setContentView(R.layout.main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public void toStartGameActivity(View view) {
		Intent intent = new Intent(this, StartGameMenu.class);
		startActivity(intent);
	}
	public void toOptionsActivity(View view) {
		
	}
	
}
