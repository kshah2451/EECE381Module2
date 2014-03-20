package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Button;

public class PlayerCharacter {

	/**
	 * Player's bitmap
	 */
	private Bitmap bitmap;
	
	/**
	 * Player's left x coordinate
	 */
	private int x_left;
	
	/**
	 * Player's right x coordinate
	 */
	private int x_right;
	
	/**
	 * Player's top y coordinate
	 */
	private int y_top;
	
	/**
	 * Player's bottom y coordinate
	 */
	private int y_bottom;
	
	/**
	 * Player's bitmap height
	 */
	private int height;
	
	/**
	 * Player's bitmap width
	 */
	private int width;
	
	/**
	 * Direction player is moving in. Direction > 0 for right, and < 0 for left.
	 */
	private int direction;
	
	/**
	 * Player's move rate
	 */
	private int move_rate;
	
	/**
	 * Player's jump rate
	 */
	private int jump_rate;
	
	/**
	 * Player's maximum jump height
	 */
	private int jump_height;

	private int jump_counter;
	
	/**
	 * Flag to indicate whether player is jumping.
	 */
	private boolean isJumping;
	
	/**
	 * Flag to indicate whether player is moving.
	 */
	private boolean isMoving;
	
	public PlayerCharacter(Bitmap bitmap, int x, int y){
		this.bitmap = bitmap;
		x_left = x; 
		y_top = y;
		height = bitmap.getHeight();
		width = bitmap.getWidth();
		x_right = x_left + width;
		y_bottom = y_top + height;
		move_rate = 1; 
		jump_rate = 4;
		jump_height = 16;
		jump_counter = 0;
		isJumping = false; 
		isMoving = false;
	}
	
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Bitmap getBitmap(){
		return bitmap;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	public int getMoveRate(){
		return move_rate;
	}
	
	public boolean getIsJumping(){
		return isJumping;
	}
	
	public void setIsJumping(boolean isJumping){
		this.isJumping = isJumping;
	}
	
	public boolean getIsMoving(){	
		return isMoving;
	}
	
	public void setIsMoving(boolean isMoving){
		this.isMoving = isMoving;
	}
	
	public int getX_left() {
		return x_left;
	}

	public int getX_right() {
		return x_right;
	}

	public int getY_top() {
		return y_top;
	}

	public int getY_bottom() {
		return y_bottom;
	}

	public void draw(Canvas canvas, int x, int y){
		canvas.drawBitmap(bitmap, x, y , null);
	}
	
	public void move(){
		if(isMoving == true){
			x_left += direction;
			x_right += direction;
		}
	}
	
	public void jump(){
		jump_counter++;
		y_top -= jump_rate;
		y_bottom -= jump_rate;
			
		if(jump_counter == jump_height){
			isJumping = false;
			jump_counter = 0;
		}
	}
	
	public void descend(){
		y_top += jump_rate;
		y_bottom += jump_rate;	
	}	
}
