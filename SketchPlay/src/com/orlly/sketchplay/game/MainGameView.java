package com.orlly.sketchplay.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.orlly.sketchplay.menus.MyApplication;
import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.rendering.MapRender;
import com.orlly.sketchplay.sound.BackgroundMusic;
import com.orlly.sketchplay.sound.SoundEffects;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{

	int count_left = 1;
	int count_right = 1;

	private GameThread thread;
	private Player player;
	private DirectionalButton right_button, left_button, up_button;
	
	private MyApplication application;

	private String theme;

	private Bitmap bitmap;
	private Bitmap temp_bg;
	private Bitmap gold_texture;
	private Bitmap victory_screen;
	private GameLevel game_level;
	
	private Sensor accelerometer;
	private SensorManager sensorManager;
	// spawn point (starting top left corner position)
	int startx = 5;
	int starty = 5;

	boolean game_over;
	
	//Flag to determine if the player is falling into the sky when the phone is tilted 
	//Prevents descend() from occurring at the same time as falling into the sky
	private boolean fallingSky = true;

	private int saturation;
	private int value;

	/* Timer bullshit */
	private long startTime;
	private Paint timerText;
	private long timeNow;
	private long timeToGo;
	private int second, minute;

	private boolean moveOkay = true;
	//private float tilt_threshold;

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

	int soundIDs[];
	
	Handler handler = new Handler();
	private Runnable onEverySecond = new Runnable() {
		public void run() {
			// do real work here

			Log.d("time", "timer exec");
			handler.postDelayed(onEverySecond, 1000);
		}
	};

	public MainGameView(Context context, Bitmap bitmap, int saturation,	int value, String theme, MyApplication application) {
		super(context);
		getHolder().addCallback(this);

		this.theme = theme;
		this.saturation = saturation;
		this.value = value;
		this.game_over = false;
		this.application = application;

		/* Game Timer initializations */
		this.startTime = System.currentTimeMillis();
		this.timeNow = System.currentTimeMillis();
		this.timerText = new Paint();
		timerText.setColor(Color.RED);
		timerText.setTextSize(100);
		
		//Array for sound effects to play in soundpool
		soundIDs = new int[4];
		this.soundIDs[0] = SoundEffects.sp.load(context, R.raw.jump, 1);
		this.soundIDs[1] = SoundEffects.sp.load(context, R.raw.listen, 1);
		this.soundIDs[2] = SoundEffects.sp.load(context, R.raw.pain, 1);
		this.soundIDs[3] = SoundEffects.sp.load(context, R.raw.step, 1);
		
		//this.tilt_threshold = application.getTiltSensitivity();

		setWillNotDraw(false);

		// Create new player character
		player = new Player(BitmapFactory.decodeResource(getResources(),
				R.drawable.afro_man_colour_right1), startx, starty,
				getContext());

		// Create new thread
		thread = new GameThread(getHolder(), this);

		// Create the buttons
		right_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.right_arrow));
		left_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.left_arrow));
		up_button = new DirectionalButton(BitmapFactory.decodeResource(
				getResources(), R.drawable.up_arrow));

		// set our bitmap bg as the passed in bitmap image
		this.bitmap = bitmap;
		
		//Sensor manager for the accelerometer
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//Listener for player tilting the phone
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		

		// Set to true so we can interact with surface
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("image", "surface changed");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		Log.d("image", "surface created");
		
		
		// Scale this.bitmap to android device's screen size
		bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(),
				this.getHeight(), true);

		gold_texture = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.gold_texture), this.getWidth(), this
				.getHeight(), true);

		victory_screen = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.victory), this
				.getWidth(), this.getHeight(), true);

		mapRender = new MapRender(bitmap, this.getHeight(), this.getWidth());

		// Convert bitmap to black and white and return
		temp_bg = mapRender.getMapImage(saturation, value);

		// create the game level image using these various parameters
		game_level = new GameLevel(this, temp_bg, gold_texture);
		game_level.selectImages(theme);
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

		BackgroundMusic.stop();
		get_BGM(theme);
		BackgroundMusic.play();

		// Start thread
		thread = new GameThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		Log.d("image", "surface destroyed");
		
		
		// Set thread flags to let it know that it needs to terminate
		boolean retry = true;
		thread.setRunning(false);

		// wait for threads to die
		while (retry) {
			try {
				thread.join();
				retry = false;

			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}
		
		temp_bg = null;
		bitmap = null;
		
		//game_level.recycle_images();
		//gold_texture.recycle();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Checks for the first finger down touch event
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			// Call action handler to check what area of touch screen has been
			// touched
			handleTouch((int) event.getX(event.getActionIndex()),
					(int) event.getY(event.getActionIndex()));

		}

		// Checks for the second finger down touch even
		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {

			handleTouch((int) event.getX(event.getActionIndex()),
					(int) event.getY(event.getActionIndex()));
		}

		// Check to see if user lifts their finger off the screen
		// ** might have to do one for the second finger to come off
		if (event.getAction() == MotionEvent.ACTION_UP) {
			player.setIsMoving(false);
		}

		return true;
	}

	/**
	 * Update player state (jumping, moving left/right, descending, dying, etc.)
	 */
	public void update() {

		/** TIMER UPDATES **/
		timeNow = System.currentTimeMillis();
		timeToGo = ((timeNow - startTime) / 1000) - 10;
		second = (int) timeToGo % 60;
		minute = (int) timeToGo / 60;

		/** JUMP STATE **/
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

		/** MOVE RIGHT STATE **/
		// Check to see if player's right x position is less than right edge of
		// screen, and if player is moving in the right direction.
		if ((player.getX_right() <= getWidth()) && (player.getDirection() > 0)) {
			try {
				// If black pixel is detected to the right of the player,
				// disallow movement.

				for (int i = 0; i < player.getHeight(); i++) {
					if (temp_bg.getPixel(player.getX_right() + 1,
							player.getY_top() + i) == Color.BLACK) {

						// if the black pixel is above his feet (the top 5/6s of
						// his body)
						// disallow movement
						if (i < ((5 * player.getHeight()) / 6)) {
							this.moveOkay = false;
						}

						// otherwise, he can walk on it and it's treated as a
						// slope.
						// So set his y position to the top of that black pixel
						else {
							player.setY_bottom((player.getY_bottom() - ((player
									.getHeight() - 1) - i)) - 1);
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

		/** MOVE LEFT STATE **/
		// Check to see if player's left x position is greater than left edge of
		// screen, and if player is moving in the left direction.
		else if ((player.getX_left() > 0) && (player.getDirection() < 0)) {
			try {
				// If black pixel is detected to the left of the player...

				for (int i = 0; i < player.getHeight(); i++) {
					if (temp_bg.getPixel(player.getX_left() - 1,
							player.getY_top() + i) == Color.BLACK) {

						// if the black pixel is above his feet (the top 5/6s of
						// his body)
						// disallow movement
						if (i < ((5 * player.getHeight()) / 6)) {
							this.moveOkay = false;
						}

						// otherwise, he can walk on it and it's treated as a
						// slope.
						// So set his y position to the top of that black pixel
						else {
							player.setY_bottom((player.getY_bottom() - ((player
									.getHeight() - 1) - i)) - 1);
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

		/** DESCENDING STATE **/
		// Check to see if player's bottom y position is less than bottom of
		// screen
		if (player.getY_bottom() < this.getHeight() - 2) {
			try {
				if (player.getIsJumping() == false) { // don't descend if he's
														// in the middle of a
														// jump
					// Check if there's a black pixel below him. If there is,
					// disallow descent
					for (int i = 0; i < player.getWidth(); i++) {
						if (temp_bg.getPixel(player.getX_left() + i,
								player.getY_bottom() + 1) == Color.BLACK) {
							this.moveOkay = false;
						}

						/** DEATH STATE **/
						// Check if he lands on a red/orange platform
						else if ((temp_bg.getPixel(player.getX_left() + i,
								player.getY_bottom() + 1) == Color.RED)
								|| temp_bg.getPixel(player.getX_left() + i,
										player.getY_bottom() + 1) == 0xFFFFA500) {
							SoundEffects.sp.play(soundIDs[2],
									SoundEffects.getVolume(),
									SoundEffects.getVolume(), 1, 0, 1.0f);
							player.die(startx, starty);
						}

						/** VICTORY STATE **/
						// Check if he lands on a blue/cyan platform
						else if ((temp_bg.getPixel(player.getX_left() + i,
								player.getY_bottom() + 1) == Color.BLUE)
								|| temp_bg.getPixel(player.getX_left() + i,
										player.getY_bottom() + 1) == Color.CYAN) {
							end_game();
						}

					}
					if (this.moveOkay == true  && this.fallingSky == true) {
						player.descend();
					}
					fallingSky = true;
					this.moveOkay = true;

				}
			} catch (IllegalStateException e) {
			} catch (IllegalArgumentException a) {

			}
		}

		// temporary fix for the player getting stuck on the ground
		if (player.getY_bottom() > this.getHeight() - 2) {
			player.setY_bottom(getHeight() - 3);

		}
	}

	/**
	 * Draws background and player with updated position
	 * 
	 * @param canvas
	 */
	public void drawImages(Canvas canvas) {
		String timeToDisplay;
		
		game_level.draw(canvas);
		player.draw(canvas, player.getX_left(), player.getY_top());
		right_button.draw(canvas, right_button_x0, right_button_y0);
		left_button.draw(canvas, left_button_x0, left_button_y0);
		up_button.draw(canvas, up_button_x0, up_button_y0);

		if (!game_over) {
			if (second < 10) {
				timeToDisplay = Integer.toString(minute) + ":" + "0"
						+ Integer.toString(second);
			} else {
				timeToDisplay = Integer.toString(minute) + ":"
						+ Integer.toString(second);
			}
			canvas.drawText(timeToDisplay,
					(this.getWidth() / 2) - (timerText.getTextSize()),
					timerText.getTextSize(), timerText);
		}

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
				SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(),
						SoundEffects.getVolume(), 1, 0, 1.0f);
				if (count_right % 2 == 0) {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_colour_right1));
				} else {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_colour_right2));
				}
				count_right++;
			}
			// This handles presses on the left button
		} else if ((x > left_button_x0) && (x <= left_button_x1)) { // left
			if ((y > left_button_y0) && (y < left_button_y1)) {
				player.setIsMoving(true);
				player.setDirection(-(player.getMoveRate()));
				SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(),
						SoundEffects.getVolume(), 1, 0, 1.0f);
				if (count_left % 2 == 0) {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_colour_left1));
				} else {
					player.setBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.afro_man_colour_left2));
				}
				count_left++;
			}
			// This handles presses on the up button
		} else if ((x > up_button_x0) && (x <= up_button_x1)) { // jump
			if ((y > up_button_y0) && (y < up_button_y1)) {
				player.setIsJumping(true);
				SoundEffects.sp.play(soundIDs[0], SoundEffects.getVolume(),
						SoundEffects.getVolume(), 1, 0, 1.0f);
			}
		}
	}

	public void get_BGM(String theme) {

		if (theme.equals("Forest")) {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.forest_theme);
		}

		else if (theme.equals("Desert")) {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.desert_theme);
		}

		else if (theme.equals("Snow")) {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.snow_theme);
		}

		else if (theme.equals("Volcano")) {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.lava_theme);
		}

		else if (theme.equals("Space")) {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.space_theme);
		}

		else {
			BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
					R.raw.funk);
		}

	}

	/**
	 * Getter for the game over flag
	 * 
	 * @return
	 */
	public boolean isGame_over() {
		return game_over;
	}

	/**
	 * Proceeds to end the game by setting the game over flag to true. Then it
	 * stops and releases the in-game music, and plays the victory screen music
	 */
	public void end_game() {

		this.game_over = true;
		BackgroundMusic.stop();
		BackgroundMusic.release();
		BackgroundMusic.mPlayer = MediaPlayer.create(this.getContext(),
				R.raw.win);
		BackgroundMusic.play();

	}

	/**
	 * Draws the victory screen
	 * 
	 * @param canvas
	 */
	public void game_over_screen(Canvas canvas) {
		String yourTimeMessage = "Your Time: " + minute + ":" + second;
		canvas.drawBitmap(victory_screen, 0, 0, null);
		timerText.setTextAlign(Align.CENTER);
		canvas.drawText(yourTimeMessage,
				(getWidth() / 2) - (timerText.getTextSize()),
				timerText.getTextSize(), timerText);

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
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		boolean tilt = application.getTilt();
		float tilt_threshold = application.getTiltSensitivity();
		if(tilt){
			//sensor_values[0] is X direction, sensor_values[1] is Y Direction, sensor_values[2] is Z Direction
			float[] sensor_values = event.values;

			//When the device is tilted forward on its side AND upright to either direction
			if(sensor_values[0] < -tilt_threshold && (sensor_values[1] > tilt_threshold || sensor_values[1] < tilt_threshold)){
				fallingSky = false;
				handleAcceleration((int)sensor_values[0], (int)sensor_values[1]);
			}

			//When the device is tilted forward on its side only
			else if(sensor_values[0] < -tilt_threshold){
				fallingSky = false;
				handleAcceleration((int)sensor_values[0], 0);
			}

			//When the device is upright in either direction only 
			else if((sensor_values[1] > tilt_threshold || sensor_values[1] < -tilt_threshold) && sensor_values[0] > -tilt_threshold){
				handleAcceleration(0, (int)sensor_values[1]);
			}
		}
	}
	
	//Function to handle changes in device acceleration
	public void handleAcceleration(int x, int y){
		player.toss(x, y, this.getWidth());
	}

}
