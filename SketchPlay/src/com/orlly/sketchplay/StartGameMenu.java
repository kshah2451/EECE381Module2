package com.orlly.sketchplay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartGameMenu extends Activity {
	
	Button take_picture;
	Button import_picture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_menu);
		take_picture = (Button)findViewById(R.id.take_picture_button);
		import_picture = (Button)findViewById(R.id.import_picture_button);
	}
	
	public void takePicture(View view) {
		
	}
	
}
