package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback{

	private GameThread thread;
	private PlayerCharacter player;
	private Bitmap bg;
	private UpKey upkey;
	int startx = 10;
	int starty = 50;
	
	
	
	
	public MainGamePanel(Context context) {
		
		super(context);
		getHolder().addCallback(this);

		
		//create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), startx, starty);

		//create new thread
		thread = new GameThread(getHolder(), this);
		
		upkey = new UpKey(BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow));

		
		
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
		 canvas.drawColor(Color.GREEN);
		// canvas.drawBitmap(bg, 0, 0, null);
	//	Rect destination = new Rect(0, 0, getWidth(), getHeight());
//		Rect source = new Rect(0, 0, bg.getWidth(), bg.getHeight());
//		Paint paint = new Paint();
	//	canvas.drawBitmap(bg, source, destination, paint);
		player.draw(canvas, player.getX(), player.getY());
		 upkey.draw(canvas, getWidth()-upkey.width, this.getHeight()-upkey.height);
		 upkey.draw(canvas, getWidth()-2*upkey.width, this.getHeight()-upkey.height);
		
	}
	
@Override
	public boolean onTouchEvent(MotionEvent event){
		//when user presses the touchscreen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//call action handler to check what has happened
			//player.handleActionDown((int)event.getX(), (int)event.getY(), this, right_arrow);		
			handleTouch((int)event.getX(), (int)event.getY());
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

	public void handleTouch(int x, int y){
		
		
		if((x > getWidth()-upkey.width) && (x <= getWidth())){
			if((y > getHeight()-upkey.height) && (y < getHeight())){
				player.setTouch(true);
				player.setDirection(player.getMoveRate());
			}
		}
		else if ((x > getWidth()-2*upkey.width) && (x <= getWidth()-upkey.width)){
			if((y > getHeight()-upkey.height) && (y < getHeight())){
				player.setTouch(true);
				player.setDirection(-(player.getMoveRate()));
			}
		}
		else
			player.setTouch(false);
			
	}
	
	
	//Class for the up key -- removeable if we decide to use layout buttons
	class UpKey{
		
		Bitmap bitmap;
		int width;
		int height;
		UpKey(Bitmap bitmap){
			
			this.bitmap = bitmap;
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
			
			
		}
		
		
		void draw(Canvas canvas, int x, int y){
			
			canvas.drawBitmap(this.bitmap, x, y , null);

		}
		
		
	}	
	
	
	
	
	
		

	
	
	

	
}
