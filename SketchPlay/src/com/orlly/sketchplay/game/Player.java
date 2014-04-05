package com.orlly.sketchplay.game;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.sound.SoundEffects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Player {
	
	
	private Context context;
	
	
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
	 * Player's fall rate
	 */
	private int fall_rate;
	
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
	
	/**
	 * Flag to indicate whether player is climbing.
	 */
	private boolean isClimbing;


	private int count = 10;
	
	private boolean animation_flag;
	
	int soundIDs[];
	
	float left_volume = 1.0f;
	float right_volume = 1.0f;
	
	public Player(Bitmap bitmap, int x, int y, Context context){
		this.bitmap = bitmap;
		this.context = context;
		x_left = x; 
		y_top = y;
		height = bitmap.getHeight();
		width = bitmap.getWidth();
		x_right = x_left + width;
		y_bottom = y_top + height;
		move_rate = 1; 
		jump_rate = 4; fall_rate = 6;
		jump_height = 16;
		jump_counter = 0;
		isJumping = false; 
		isMoving = false;
		soundIDs = new int[4];
		this.soundIDs[0] = SoundEffects.sp.load(context, R.raw.jump, 1);
		this.soundIDs[1] = SoundEffects.sp.load(context, R.raw.listen, 1);
		this.soundIDs[2] = SoundEffects.sp.load(context, R.raw.pain, 1);
		this.soundIDs[3] = SoundEffects.sp.load(context, R.raw.step, 1);
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
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
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
	
	public int getFallRate(){
		return fall_rate;
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
	
	public boolean isClimbing() {
		return isClimbing;
	}

	public void setClimbing(boolean isClimbing) {
		this.isClimbing = isClimbing;
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

	public void setY_top() {
		this.y_top = this.y_bottom - height;
	}
	
	public void setY_bottom(int num) {
		this.y_bottom = num;
		setY_top();
	}
	
	public void draw(Canvas canvas, int x, int y){
		canvas.drawBitmap(bitmap, x, y , null);
	}
	
	public void move(){
		if(isMoving == true){
			x_left += direction;
			x_right += direction;
			count--;
			if (count == 0) {
				if(direction < 0) {
					if(animation_flag == true) {
						this.bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.afro_man_left1);
						SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(), SoundEffects.getVolume(), 1, 0, 1.0f);
						animation_flag = false;
					} else {
						this.bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.afro_man_left2);
						SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(), SoundEffects.getVolume(), 1, 0, 1.0f);
						animation_flag = true;
					}	
				} else {
					if(animation_flag == true) {
						this.bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.afro_man_right1);
						SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(), SoundEffects.getVolume(), 1, 0, 1.0f);
						animation_flag = false;
					} else {
						this.bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.afro_man_right2);
						SoundEffects.sp.play(soundIDs[3], SoundEffects.getVolume(), SoundEffects.getVolume(), 1, 0, 1.0f);
						animation_flag = true;
					}	
				}
				count = 10;
			}
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
		y_top += fall_rate;
		y_bottom += fall_rate;	
	}	
	
	public void die(int startx, int starty){
		y_top = starty;
		y_bottom = y_top + height;
		x_left = startx;
		x_right = x_left+ width;
	}
	
	
	
}
