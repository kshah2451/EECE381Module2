package com.orlly.sketchplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class ImageAdjustmentMenu extends Activity implements SeekBar.OnSeekBarChangeListener{
	
	private Button play_game;
	private SeekBar saturation;
	private SeekBar value;
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
		play_game = (Button)findViewById(R.id.play_game_button);
		saturation = (SeekBar)findViewById(R.id.saturation);
		value = (SeekBar)findViewById(R.id.value);
		saturation.setOnSeekBarChangeListener(this);
		rendering_preview = (ImageView)findViewById(R.id.img_preview);
		
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rendering = new MapRender(bitmap, bitmap.getHeight(), bitmap.getWidth());
		rendering_preview.setImageBitmap(rendering.getMapImage(0,0,0));
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		rendering_preview.setImageBitmap(rendering.getMapImage(0,0,0));
		
	}
	
	/**
	 * Function called when "Play Game" button is pressed. Launches Game.
	 * @param view
	 */
	public void playGame(View view){		
		Intent intent = new Intent(this, Game.class);
		intent.putExtra("imageUri", imageUri.toString());
		startActivity(intent);
		rendering_preview.setImageBitmap(rendering.getMapImage(0,0,0));
	}
	
	public void renderPicture(){
		rendering_preview.setImageBitmap(rendering.getMapImage(0,0,0));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {		
	}
}
