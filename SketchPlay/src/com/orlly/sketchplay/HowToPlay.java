package com.orlly.sketchplay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class HowToPlay extends Activity{

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.how_to_play);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	

}
