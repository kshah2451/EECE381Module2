package com.orlly.sketchplay.menus;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.server.ServerTransactions;
import com.orlly.sketchplay.sound.BackgroundMusic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainMenu extends Activity {


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		BackgroundMusic.mPlayer = MediaPlayer.create(this, R.raw.short_change_hero_bg);
		super.onCreate(savedInstanceState);
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
		Intent intent = new Intent(this, ImageSelectionMenu.class);
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
		Intent intent = new Intent(this, GettingStartedMenu.class);
		startActivity(intent);
	}

	
	/**
	 * Function called when "Getting Started" action bar item (Question mark icon) is
	 * pressed. Launches GettingStarted activity.
	 * 
	 * @param item
	 * @return
	 */
	public boolean gettingStartedActionBar(MenuItem item) {
		Intent intent = new Intent(this, GettingStartedMenu.class);
		startActivity(intent);
		return true;
	}

	
	/**
	 * Function called when "Options" action bar item (Options icon) is pressed.
	 * Launches GettingStarted activity.
	 * 
	 * @param item
	 * @return
	 */
	public boolean optionsActionBar(MenuItem item) {
		Intent intent = new Intent(this, OptionsMenu.class);
		startActivity(intent);
		return true;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusic.play();	
	}
	

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
			BackgroundMusic.stop();
			BackgroundMusic.release();
	}

}
