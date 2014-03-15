package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback{

	private GameThread thread;
	private PlayerCharacter player;
	private Bitmap bg;
	int startx = 10;
	int starty = 50;
	public MainGamePanel(Context context, Bitmap bitmap) {
		
		super(context);
		getHolder().addCallback(this);

		
		//create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), startx, starty);

		//create new thread
		thread = new GameThread(getHolder(), this);
		
		bg = bitmap;
		
		//set to true so we can interact with surface
		setFocusable(true);
		
	}
	
	
	
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		Rect destination = new Rect(0,0,getWidth(),getHeight());
//		Rect source = new Rect(0,0,bg.getWidth(),bg.getHeight());
//		Paint paint = new Paint();
//		canvas.drawBitmap(bg,source,destination,paint);
//	}




	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//upon creation of surface, start thread
		thread = new GameThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		//allow onDraw method to be called (it's normally set to not draw)
		//this.setWillNotDraw(false);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);

		while(retry){
			try{
				thread.join();
				retry = false;
			
			} catch (InterruptedException e){
				
				
			}
			
		}
	
	}
	
	 public void drawImages(Canvas canvas){
		// canvas.drawColor(Color.GREEN);
		// canvas.drawBitmap(bg, 0, 0, null);
		Rect destination = new Rect(0, 0, getWidth(), getHeight());
		Rect source = new Rect(0, 0, bg.getWidth(), bg.getHeight());
		Paint paint = new Paint();
		canvas.drawBitmap(bg, source, destination, paint);
		player.draw(canvas, player.getX(), player.getY());

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		//when user presses the touchscreen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//call action handler to check what has happened
			//player.handleActionDown((int)event.getX(), (int)event.getY(), this, right_arrow);		
			player.handleTouch((int)event.getX(), (int)event.getY(), this);
		}
		
		
		//when the user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (player.getTouch()) {
				player.setTouch(false);
			}
		}
		
		
		// if the player was touched, then call update()
		if(player.getTouch()){
			this.update();
		}
		
		
		return true;
	} 
	

	//update player location
	public void update() {

	//	move player if they're going right and aren't bounded by the right side
		if((player.getX() + player.getBitmap().getWidth() <= getWidth()) && (player.getDirection()== player.getMoveRate())){
			player.move();
		}
	// move player if they're going left and aren't bounded by the left side	
		else if ((player.getX() > 0) && (player.getDirection() == -(player.getMoveRate()))){
			player.move();

		}
		
	}

	
}
