package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private GameThread thread;
	private PlayerCharacter player;
	private Bitmap bitmap;
	private Bitmap temp_bg;
	int startx = 10;
	int starty = 50;
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
				false);
		
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

	public void drawImages(Canvas canvas) {
		canvas.drawBitmap(temp_bg, 0, 0, null);
		player.draw(canvas, player.getX(), player.getY());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// When user presses the touchscreen
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// call action handler to check what has happened
			// player.handleActionDown((int)event.getX(), (int)event.getY(),
			// this, right_arrow);
			player.handleTouch((int) event.getX(), (int) event.getY(), this);
		}

		// when the user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (player.getTouch()) {
				player.setTouch(false);
			}
		}

		// if the player was touched, then call update()
		if (player.getTouch()) {
			this.update();
		}

		return true;
	}

	/**
	 * Update player position
	 */
	public void update() {

		// Move player if they're going right and aren't bounded by the right
		// side
		if ((player.getX() + player.getBitmap().getWidth() <= getWidth())
				&& (player.getDirection() == player.getMoveRate())) {
			player.move();
		}
		// Move player if they're going left and aren't bounded by the left side
		else if ((player.getX() > 0)
				&& (player.getDirection() == -(player.getMoveRate()))) {
			player.move();

		}
	}

}
