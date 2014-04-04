package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	int count_left = 1;
	int count_right = 1;

	private GameThread thread;
	private PlayerCharacter player;
	private DirectionalButton right_button, left_button, up_button;
	private Bitmap bitmap;
	private Bitmap temp_bg;
	private Bitmap visual_bg;
	private Bitmap texture;
	private Bitmap hazard;
	private Bitmap treasure;
	private Bitmap gold_texture;
	private Background game_level;
	private Bitmap level_image;
	//spawn point (starting top left corner position)
	int startx = 5;
	int starty = 5;

	private int saturation;
	private int value;

	private boolean moveOkay = true;

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
	
	Handler handler = new Handler();
	private Runnable onEverySecond=new Runnable() {
	    public void run() {
	        // do real work here

	        	Log.d("time", "timer exec");
	            handler.postDelayed(onEverySecond, 1000);
	    }
	};

	public MainGamePanel(Context context, Bitmap bitmap, int saturation,
			int value) {
		super(context);
		getHolder().addCallback(this);

		this.saturation = saturation;
		this.value = value;

		setWillNotDraw(false);

		// Create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(
				getResources(), R.drawable.afro_man_right1), startx, starty, getContext());

		// Create new thread
		thread = new GameThread(getHolder(), this);

		
		//Create the buttons
		right_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.right_arrow));
		left_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.left_arrow));
		up_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.up_arrow));

		//set our bitmap bg as the passed in bitmap image
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
		bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(),
				this.getHeight(), true);
		// the background image
		visual_bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.forest_bg), this.getWidth(),
				this.getHeight(), true);
		// the image containing the platform textures
		texture = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.forest_texture), this.getWidth(),
				this.getHeight(), true);
		
		hazard = Bitmap.createBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.forest_hazard));
		
		treasure = Bitmap.createBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.treasure));
		
		gold_texture =Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.gold_texture), this.getWidth(),
				this.getHeight(), true);
		

		
		Log.d("debug", "Scaled Bitmap height: " + bitmap.getHeight());
		Log.d("debug", "Scaled Bitmap width: " + bitmap.getWidth());

		mapRender = new MapRender(bitmap, this.getHeight(), this.getWidth());

		// Convert bitmap to black and white and return
		temp_bg = mapRender.getMapImage(saturation, value);
		
		// create the game level image using these various parameters
		game_level = new Background(visual_bg, temp_bg, texture, hazard, treasure, gold_texture);
		game_level.generate_level_image();

		// Initializes positioning of right button
		right_button_x0 = left_button.width + (left_button.width / 2);
		right_button_x1 = right_button_x0 + right_button.width;
		right_button_y0 = getHeight() - right_button.height;
		right_button_y1 = getHeight();

		// Initializes positioning of left button
		left_button_x0 = 0;
		left_button_x1 = left_button.width;
		left_button_y0 = getHeight() - left_button.height;
		left_button_y1 = getHeight();

		// Initializes positioning of up button
		up_button_x0 = getWidth() - up_button.width;
		up_button_x1 = up_button_x0 + up_button.width;
		up_button_y0 = getHeight() - up_button.height;
		up_button_y1 = getHeight();

		BackgroundMusic.play();

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
		
		//stop the background music
		BackgroundMusic.stop();
		BackgroundMusic.release();
		
		
	}

	/**
	 * Draws background and player with updated position
	 * 
	 * @param canvas
	 */
	public void drawImages(Canvas canvas) {
	//	canvas.drawBitmap(temp_bg, 0, 0, null);
	//	canvas.drawBitmap(level_image, 0, 0, null);
		game_level.draw(canvas);
		player.draw(canvas, player.getX_left(), player.getY_top());
		right_button.draw(canvas, right_button_x0, right_button_y0);
		left_button.draw(canvas, left_button_x0, left_button_y0);
		up_button.draw(canvas, up_button_x0, up_button_y0);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int fingerCount = event.getPointerCount();
		
		// Check to see if user has touched the touch screen

		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			// Call action handler to check what area of touch screen has been touched
			handleTouch((int) event.getX(event.getActionIndex()), (int) event.getY(event.getActionIndex()));
			Log.d("touchevent", "first finger");

			
		}
		
		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
			Log.d("touchevent", "second finger");

			handleTouch((int) event.getX(event.getActionIndex()), (int) event.getY(event.getActionIndex()));
		}

		// Check to see if user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			player.setIsMoving(false);
		}

		return true;
	}

	/**
	 * Update player position
	 */
	public void update() {

		Log.d("fall", "Player's height: " + Integer.toString(player.getY_bottom()));
		Log.d("fall", "Screen's height: " + Integer.toString(this.getHeight()));
		Log.d("fall", "Screen's height - 3: " + Integer.toString(this.getHeight() - 3));

		// Check to see if player is below top of screen
		if (player.getY_top() > 0) {
			// Check to see if player is already jumping. If so, then allow
			// jump.
			if (player.getIsJumping()) {
				player.jump();
			}
		}
		// If player reaches ceiling, set jumping flag to false to disallow
		// jumping.
		else {
			player.setIsJumping(false);
		}

		// Check to see if player's right x position is less than right edge of
		// screen, and if player is moving in the right direction.
		if ((player.getX_right() <= getWidth()) && (player.getDirection() > 0)) {
			try {
				// If black pixel is detected to the right of the player,
				// disallow movement.

				for (int i = 0; i < player.getHeight(); i++) {
					if (temp_bg.getPixel(player.getX_right() + 1,
							player.getY_top() + i) == Color.BLACK) {
						//this.moveOkay = false;
						
						
						//if the black pixel is above his feet (the top 2/3s of his body)
						// disallow movement
						if (i < ((5*player.getHeight())/6)) {
							this.moveOkay = false;

						}
					
						//otherwise, he can walk on it and it's treated as a slope
						else{
							player.setY_bottom((player.getY_bottom() - ((player.getHeight()-1)-i))-1);
						//	player.setY_top();
						}
					
					
						break;
					
					
					
					}
						
					
				}

				if (this.moveOkay == true) {
					player.move();
				}
				this.moveOkay = true;

			} catch (IllegalStateException e) {
				Log.d("fall", "throws state exception");

			} catch (IllegalArgumentException a) {
				Log.d("fall", "throws argument exception");

			}

		}

		// Check to see if player's left x position is greater than left edge of
		// screen, and if player is moving in the left direction.
		else if ((player.getX_left() > 0) && (player.getDirection() < 0)) {
			try {
				// If black pixel is detected to the left of the player...
	
				for (int i = 0; i < player.getHeight(); i++) {
					if (temp_bg.getPixel(player.getX_left()-1, player.getY_top()
							+ i) == Color.BLACK) {

					
						//if the black pixel is above his feet (the top 2/3s of his body)
						// disallow movement
						if (i < ((5*player.getHeight())/6)) {
							this.moveOkay = false;

						}
					
						//otherwise, he can walk on it and it's treated as a slope
						else{
							player.setY_bottom((player.getY_bottom() - ((player.getHeight()-1)-i))-1);
							//player.setY_top();
						}
					
					
						break;
					
					
					
					}
				}

				if (this.moveOkay == true) {
					player.move();

				}
				this.moveOkay = true;

			} catch (IllegalStateException e) {

			} catch (IllegalArgumentException a) {

			}
		}
		


		// Check to see if player's bottom y position is less than bottom of
		// screen
		if (player.getY_bottom() < this.getHeight() - 2) {
			try {
				if (player.getIsJumping() == false) {
					for (int i = 0; i < player.getWidth(); i++) {
						if (temp_bg.getPixel(player.getX_left() + i,
								player.getY_bottom() + 1) == Color.BLACK) {
							this.moveOkay = false;
						}
						else if((temp_bg.getPixel(player.getX_left() + i,
								player.getY_bottom() + 1) == Color.RED)|| temp_bg.getPixel(player.getX_left() + i,
										player.getY_bottom() + 1) == 0xFFFFA500){
							player.die(startx, starty);
						}
					}
					if (this.moveOkay == true) {
						player.descend();
					}

					
					this.moveOkay = true;

				}
			} catch (IllegalStateException e) {
			} catch (IllegalArgumentException a){

			}
		}
		
		
		//temporary fix for the player getting stuck on the ground
		if (player.getY_bottom() > this.getHeight() - 2) {
			player.setY_bottom(getHeight()-3);
			
		
		}
		
		//if (temp_bg.getPixel(player.getX_left()-1, player.getY_top()


		
		
	}

	/**
	 * Checks the region that was touched on touch screen.
	 * 
	 * @param x
	 * @param y
	 */
	public void handleTouch(int x, int y) {
		// This handles presses on the right button
		if ((x > right_button_x0) && (x <= right_button_x1)) {
			if ((y > right_button_y0) && (y < right_button_y1)) {
				player.setIsMoving(true);
				player.setDirection(player.getMoveRate());
				if (count_right % 2 == 0) {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_right1));
				} else {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_right2));
				}
				count_right++;
			}
			// This handles presses on the left button
		} else if ((x > left_button_x0) && (x <= left_button_x1)) { // left
			if ((y > left_button_y0) && (y < left_button_y1)) {
				player.setIsMoving(true);
				player.setDirection(-(player.getMoveRate()));
				if (count_left % 2 == 0) {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_left1));
				} else {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_left2));
				}
				count_left++;
			}
			// This handles presses on the up button
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
