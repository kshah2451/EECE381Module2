package com.orlly.sketchplay.menus;


import java.io.FileNotFoundException;
import java.io.IOException;

import com.orlly.sketchplay.game.MainGameView;
import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.sound.BackgroundMusic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {

	private Bitmap background_bmp;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Intent intent = new Intent();
		intent = getIntent();
		Uri imageUri = Uri.parse(intent.getExtras().getString("imageUri"));
		int saturation = intent.getExtras().getInt("saturation");
		int value = intent.getExtras().getInt("value");

		try {
			background_bmp = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), imageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("debug", "BMP - FileNotFoundException");
		} catch (IOException e) {
			Log.d("debug", "BMP - IOException");
			e.printStackTrace();
		}

		setContentView(new MainGameView(this, background_bmp, saturation,
				value));
	}

	
	@Override
	public void onBackPressed() {
		BackgroundMusic.stop();
		BackgroundMusic.release();
		BackgroundMusic.mPlayer = MediaPlayer.create(this, R.raw.short_change_hero_bg);
		BackgroundMusic.play();
		super.onBackPressed();
	}

	/**
	 * Function called when "Getting Started" action bar item is pressed.
	 * Launches GettingStarted activity.
	 * 
	 * @param item
	 * @return
	 */
	public boolean gettingStartedActionBar(MenuItem item) {
		Intent intent = new Intent(this, GettingStartedMenu.class);
		BackgroundMusic.stop();
		BackgroundMusic.mPlayer = MediaPlayer.create(this, R.raw.short_change_hero_bg);
		BackgroundMusic.play();
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
		BackgroundMusic.stop();
		BackgroundMusic.mPlayer = MediaPlayer.create(this, R.raw.short_change_hero_bg);
		BackgroundMusic.play();
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
