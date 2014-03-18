package com.orlly.sketchplay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class Game extends Activity{
	
	private int[][] pixel_array;
	private Button right_button;
	private MainGamePanel gamepanel;
	int startx = 10;
	int starty = 50;
	private PlayerCharacter player;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
/*Background code that wasn't compatible with xml-created buttons*/
/*		Bundle bundle = this.getIntent().getExtras();
		int height = bundle.getInt("height", 0);
		int width = bundle.getInt("width", 0);
		pixel_array = MapRender.convertTo2DArray(bundle.getIntArray("pixel_array"), height, width);
		Bitmap bitmap = Bitmap.createBitmap(bundle.getIntArray("pixel_array"), width, height, Bitmap.Config.ARGB_8888);
*/
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_bg);
		

		
		gamepanel= new MainGamePanel(this, bitmap);
		setContentView(gamepanel);
		

	
		//content view if we wanted to use xml layout:
//		setContentView(R.layout.main_game);
		
		
		//Rect dest = new Rect(0,0,View.getWidth(),getHeight());
		//Toast.makeText(this, Integer.toString(height),Toast.LENGTH_SHORT).show();
		
		
		
// on click function that i had in mind		
/*		
		right_button = (Button)findViewById(R.id.right_button);
		right_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "moves", Toast.LENGTH_LONG).show();
			//	player.setTouch(true);
				player.setDirection(player.getMoveRate());
				player.move();

			}
		}); */
		
		
	}
	
	

	
	
}
