package com.orlly.sketchplay.game;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private SurfaceHolder surfaceHolder;
	private MainGameView gamePanel;
	
	/**
	 *  Flag to hold game state 
	 */
	private boolean running;
	
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public GameThread(SurfaceHolder surfaceHolder, MainGameView gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}
	
	@Override
	public void run() {
		Canvas canvas;
		while (running) {		
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				if(canvas != null){
					synchronized (surfaceHolder) {
						
						/*It will do the following if the game is being played*/
						if(this.gamePanel.isGame_over() == false){
							// update game state 
							this.gamePanel.update();
							// render state to the screen
							// draws the canvas on the panel
							this.gamePanel.drawImages(canvas);	
						}
						/*It will do the following if the game is over*/
						else{
							this.gamePanel.game_over_screen(canvas);
						}
						
					}
				}
			} finally {
				// in case of an exception the surface is not left in 
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}	// end finally
		}
		
		
	}

}
