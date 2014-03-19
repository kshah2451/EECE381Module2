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
	private DirectionalButton right_button, left_button, up_button;
	private Bitmap bitmap;
	private Bitmap temp_bg;
	int startx = 600;
	int starty = 100;
	
	private int saturation;
	private int value;
	
	private int[][] map_array;
	private MapRender mapRender;
	int right_button_x0;
	int right_button_x1;
	int left_button_x0;
	int left_button_x1;
	int up_button_x0;
	int up_button_x1;
	int right_button_y0;
	int right_button_y1;
	int left_button_y0;
	int left_button_y1;
	int up_button_y0;
	int up_button_y1;

	public MainGamePanel(Context context, Bitmap bitmap, int saturation, int value) {
		super(context);
		getHolder().addCallback(this);
		
		this.saturation = saturation;
		this.value = value;

		// Create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(
				getResources(), R.drawable.afro_man), startx, starty);

		// Create new thread
		thread = new GameThread(getHolder(), this);

		right_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.right_arrow));
		left_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.left_arrow));
		up_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.up_arrow));

		Log.d("size", "width:" + Integer.toString(getWidth()));
		Log.d("size", "height:" + Integer.toString(getHeight()));

		this.bitmap = bitmap;
		
		


		// Set to true so we can interact with surface
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// Scale this.bitmap to android device's screen size
		bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(),
				this.getHeight(), true);

		mapRender = new MapRender(bitmap, this.getHeight(), this.getWidth());

		// Convert bitmap to black and white and return
		temp_bg = mapRender.getMapImage(saturation, value);

		// Create 2D pixel array. Used for collision detection.
		map_array = MapRender.convertTo2DArray(mapRender.getPixelArray(),
				this.getHeight(), this.getWidth());
		
		//initializes positioning of right button
		right_button_x0 = left_button.width + (left_button.width / 2);
		right_button_x1 = right_button_x0 + right_button.width;
		right_button_y0 = getHeight() - right_button.height;
		right_button_y1 = getHeight();
		
		//initializes positioning of left button
		left_button_x0 = 0;
		left_button_x1 = left_button.width;
		left_button_y0 = getHeight() - left_button.height;
		left_button_y1 = getHeight();
		
		//initializes positioning of up button
		up_button_x0 = getWidth() - up_button.width;
		up_button_x1 = up_button_x0 + up_button.width;	
		up_button_y0 = getHeight() - up_button.height;
		up_button_y1 = getHeight();

		
		

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

	public void drawImages(Canvas canvas) {
		canvas.drawBitmap(temp_bg, 0, 0, null);
		player.draw(canvas, player.getX(), player.getY());
		right_button.draw(canvas, right_button_x0, right_button_y0);
		left_button.draw(canvas, left_button_x0,
				left_button_y0);
		up_button.draw(canvas, up_button_x0, up_button_y0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// When user presses the touchscreen
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// call action handler to check what has happened
			handleTouch((int) event.getX(), (int) event.getY());
		}

		// when the user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (player.getIsMoving()) {
				player.setIsMoving(false);
			}

		}
		return true;
	}

	/**
	 * Update player position
	 */
	public void update() {
		if(player.getY() > 0){
			if (player.getIsJumping()) {
				player.jump();
			}
		}

		// if is moving
		// move player if they're going right and aren't bounded by the right
		// side
		if ((player.getX() + player.getBitmap().getWidth() <= getWidth())
				&& (player.getDirection() == player.getMoveRate())) {
			try{
				if (map_array[player.getY()][player.getX()
						+ player.getBitmap().getWidth()] == Color.WHITE) {
					player.move();
				}
			} catch(IndexOutOfBoundsException e) {
				
			}
		}

		// move player if they're going left and aren't bounded by the left side
		else if ((player.getX() > 0)
				&& (player.getDirection() == -(player.getMoveRate()))) {
			player.move();
		}

		if(player.getY()+ player.getBitmap().getHeight() < getHeight()){
			if ((map_array[player.getY() + player.getBitmap().getHeight()][player
					.getX()] == -1)  && (player.getIsJumping() == false)){
				Log.d("debug",
						Integer.toString(map_array[player.getY()
								+ player.getBitmap().getHeight()][player.getX()]));
				player.descend();
			}
		}

	}

	public void handleTouch(int x, int y) {

		// this handles presses on the right button
		if ((x > right_button_x0) && (x <= right_button_x1)) { 
			if ((y > right_button_y0) && (y < right_button_y1)) {
				player.setIsMoving(true);
				player.setDirection(player.getMoveRate());
			}
		// this handles presses on the left button
		} else if ((x > left_button_x0) && (x <= left_button_x1)) { // left
			if ((y > left_button_y0) && (y < left_button_y1)) {
				player.setIsMoving(true);
				player.setDirection(-(player.getMoveRate()));
			}
		// this handles presses on the up button
		} else if ((x > up_button_x0) && (x <= up_button_x1)) { // jump
			if ((y > up_button_y0) && (y < up_button_y1)) {
				player.setIsJumping(true);
			}
		}
	}

	// Class for the buttons -- removeable if we decide to use layout buttons
	class DirectionalButton {
		Bitmap bitmap;
		int width;
		int height;
		boolean touched;


		DirectionalButton(Bitmap bitmap) {
			this.bitmap = bitmap;
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
		}

		void draw(Canvas canvas, int x, int y) {
			canvas.drawBitmap(this.bitmap, x, y, null);
		}

	}

}
