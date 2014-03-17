package com.orlly.sketchplay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


public class Game extends Activity{
	
	private int[][] pixel_array;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle bundle = this.getIntent().getExtras();
		int height = bundle.getInt("height", 0);
		int width = bundle.getInt("width", 0);
		pixel_array = MapRender.convertTo2DArray(bundle.getIntArray("pixel_array"), height, width);
		Bitmap bitmap = Bitmap.createBitmap(bundle.getIntArray("pixel_array"), width, height, Bitmap.Config.ARGB_8888);
		setContentView(new MainGamePanel(this,bitmap));
		//Rect dest = new Rect(0,0,View.getWidth(),getHeight());
		//Toast.makeText(this, Integer.toString(height),Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu); 
	    return super.onCreateOptionsMenu(menu);
	}
	
	

	
	
}
