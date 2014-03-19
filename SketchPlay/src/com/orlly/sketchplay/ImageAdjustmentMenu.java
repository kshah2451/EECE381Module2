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
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ImageAdjustmentMenu extends Activity {

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
	}

	/**
	 * Function called when "Render Picture" button is pressed. Renders image in
	 * preview.
	 * 
	 * @param view
	 */
	public void renderPicture(View view) {
		saturation_tracker = saturation.getProgress();
		value_tracker = value.getProgress();
		
		rendering = new MapRender(bitmap, bitmap.getHeight(), bitmap.getWidth());
		
		// Set preview to reflect rendered bitmap
		rendering_preview.setImageBitmap(rendering.getMapImage(
				saturation_tracker, value_tracker));
	}
}
