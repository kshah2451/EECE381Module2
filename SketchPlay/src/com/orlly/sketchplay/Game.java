package com.orlly.sketchplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

public class Game extends Activity{
	
	private int[][] pixel_array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		Bundle bundle = this.getIntent().getExtras();
		int height = bundle.getInt("height", 0);
		int width = bundle.getInt("width", 0);
		pixel_array = MapRender.convertTo2DArray(bundle.getIntArray("pixel_array"), height, width);
		Bitmap bitmap = Bitmap.createBitmap(bundle.getIntArray("pixel_array"), width, height, Bitmap.Config.ARGB_8888);
		Toast.makeText(this, Integer.toString(height),Toast.LENGTH_SHORT).show();
	}
	
	

	
	
}
