package com.orlly.sketchplay.menus;

import java.io.FileNotFoundException;
import java.io.IOException;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.rendering.MapRender;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class RenderImageMenu extends Activity {

	// Background task variables
	private ProgressDialog pd;
	private Context context;
	private Bitmap backBmp;
	private Button render_image_button;
	private SeekBar saturation;
	private SeekBar value;
	private int saturation_tracker = 50;
	private int value_tracker = 50;
	private ImageView rendering_preview;
	private Uri imageUri;
	private Bitmap bitmap;
	private MapRender rendering;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adjustment_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		// Background task stuff
		context = this;
		render_image_button = (Button) findViewById(R.id.render_picture_button);

		Intent intent = new Intent();
		intent = getIntent();
		imageUri = Uri.parse(intent.getStringExtra("imageUri"));

		// Find views by id attributes identified in XML file
		saturation = (SeekBar) findViewById(R.id.saturation);
		value = (SeekBar) findViewById(R.id.value);
		rendering_preview = (ImageView) findViewById(R.id.img_preview);

		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), imageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rendering = new MapRender(bitmap, bitmap.getHeight(), bitmap.getWidth());
		rendering_preview.setImageBitmap(rendering.getMapImage(50, 50));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		rendering_preview.setImageBitmap(rendering.getMapImage(
				saturation_tracker, value_tracker));
	}

	/**
	 * Function called when "Play Game" button is pressed. Launches Game.
	 * 
	 * @param view
	 */
	public void playGame(View view) {
		Intent intent = new Intent(this, Game.class);

		// Create a bundle to put imageURI, saturation, and value to pass to
		// Game activity
		Bundle bundle = new Bundle();
		bundle.putString("imageUri", imageUri.toString());
		bundle.putInt("saturation", saturation_tracker);
		bundle.putInt("value", value_tracker);
		intent.putExtras(bundle);
		startActivity(intent);
		
		Toast.makeText(this, "Loading... Please wait", Toast.LENGTH_LONG).show();
	}

	/**
	 * Function called when "Render Picture" button is pressed. Renders image in
	 * preview.
	 * 
	 * @param view
	 */
	public void renderPicture(View view) {
		view.setEnabled(false);
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Rendering...");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					// Do something...
					saturation_tracker = saturation.getProgress();
					value_tracker = value.getProgress();

					rendering = new MapRender(bitmap, bitmap.getHeight(),
							bitmap.getWidth());

					// Set preview to reflect rendered bitmap
					backBmp = rendering.getMapImage(saturation_tracker,
							value_tracker);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (pd != null) {
					pd.dismiss();
					render_image_button.setEnabled(true);
					rendering_preview.setImageBitmap(backBmp);
				}
			}

		};
		task.execute((Void[]) null);
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
	
}
