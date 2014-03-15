package com.orlly.sketchplay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

public class Game extends Activity{
	
	private int[][] pixel_array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		int height = bundle.getInt("height", 0);
		int width = bundle.getInt("width", 0);
		pixel_array = MapRender.convertTo2DArray(bundle.getIntArray("pixel_array"), height, width);
		Bitmap bitmap = Bitmap.createBitmap(bundle.getIntArray("pixel_array"), width, height, Bitmap.Config.ARGB_8888);
		setContentView(new MainGamePanel(this,bitmap));
		//Rect dest = new Rect(0,0,View.getWidth(),getHeight());
		//Toast.makeText(this, Integer.toString(height),Toast.LENGTH_SHORT).show();
	}
	
	

	
	
}
