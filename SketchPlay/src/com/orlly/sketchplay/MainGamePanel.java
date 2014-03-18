package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private GameThread thread;
	private PlayerCharacter player;
	private DirectionalButton right_key, left_key, up_key;	
	private Bitmap bitmap;
	private Bitmap temp_bg;
	int startx = 600;
	int starty = 100;
	private int[][] map_array;
	private MapRender mapRender;

	public MainGamePanel(Context context, Bitmap bitmap) {
		super(context);
		getHolder().addCallback(this);

		// Create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(
				getResources(), R.drawable.droid_1), startx, starty);

		// Create new thread
		thread = new GameThread(getHolder(), this);
		
		right_key = new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow));
		left_key = new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow));
		up_key =  new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow));
			
		Log.d("size", "width:" + Integer.toString(getWidth()));
		Log.d("size", "height:" + Integer.toString(getHeight()));

		this.bitmap = bitmap;
		
		// Set to true so we can interact with surface
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// Scale this.bitmap to android device's screen size
		bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(),
				true);
		
		mapRender = new MapRender(bitmap, this.getHeight(), this.getWidth());
		
		// Convert bitmap to black and white and return
		temp_bg = mapRender.getMapImage();
		
		// Create 2D pixel array. Used for collision detection.
		map_array = MapRender.convertTo2DArray(mapRender.getPixelArray(),
				this.getHeight(), this.getWidth());

		// Start thread
		thread = new GameThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);

		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	 public void drawImages(Canvas canvas){
		canvas.drawBitmap(temp_bg, 0, 0, null);
		player.draw(canvas, player.getX(), player.getY());
		right_key.draw(canvas, getWidth()-right_key.width, this.getHeight()-right_key.height);
		left_key.draw(canvas, getWidth()-2*right_key.width, this.getHeight()-right_key.height);
		up_key.draw(canvas, 0, this.getHeight()-up_key.height);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// When user presses the touchscreen
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// call action handler to check what has happened
			// player.handleActionDown((int)event.getX(), (int)event.getY(),
			// this, right_arrow);
			handleTouch((int) event.getX(), (int) event.getY());
		}

		// when the user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (player.getTouch()) {
				player.setTouch(false);

			}
			
			if(player.getIsMoving()){
				player.setIsMoving(false);
			}
						
		}
		return true;	
	}


	/**
	 * Update player position
	 */
	public void update() {
		if(player.getIsJumping()){
			player.jump();
		}
		
		
		//if is moving
	//	move player if they're going right and aren't bounded by the right side
		if((player.getX() + player.getBitmap().getWidth() <= getWidth()) && (player.getDirection()== player.getMoveRate())){			
			if(map_array[player.getY()][player.getX()+player.getBitmap().getWidth()] != Color.BLACK){
				player.move();
			}
		}
		
	// move player if they're going left and aren't bounded by the left side	
		else if ((player.getX() > 0) && (player.getDirection() == -(player.getMoveRate()))){
			player.move();
		}
		
		if((player.getY() != starty) && (player.getIsJumping() == false)){
			player.descend();
		}
		
	}

	public void handleTouch(int x, int y){
		
		
		if((x > getWidth()-right_key.width) && (x <= getWidth())){ //right
			if((y > getHeight()-right_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsMoving(true);
				player.setDirection(player.getMoveRate());
			}
		}
		else if ((x > getWidth()-2*right_key.width) && (x <= getWidth()-right_key.width)){ //left
			if((y > getHeight()-right_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsMoving(true);
				player.setDirection(-(player.getMoveRate()));
			//	player.setIsJumping(true);


			}
		}
		else if((x > 0) && (x <= up_key.width)){    //jump
			if((y > getHeight()-up_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsJumping(true);
			}
		}
		
		else
			player.setTouch(false);
			

	}
	
	
	//Class for the up key -- removeable if we decide to use layout buttons
	class DirectionalButton{
		
		Bitmap bitmap;
		int width;
		int height;
		boolean touched;
		DirectionalButton(Bitmap bitmap){
			
			this.bitmap = bitmap;
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
			
			
		}
		
		
		void draw(Canvas canvas, int x, int y){
			
			canvas.drawBitmap(this.bitmap, x, y , null);

		}
		
		
	}	
	
	

}
