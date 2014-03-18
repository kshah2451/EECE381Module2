package com.orlly.sketchplay;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class Game extends Activity {

	private Bitmap background_bmp;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = new Intent();
		intent = getIntent();
		Uri imageUri = Uri.parse(intent.getStringExtra("imageUri"));

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

		setContentView(new MainGamePanel(this, background_bmp));

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
