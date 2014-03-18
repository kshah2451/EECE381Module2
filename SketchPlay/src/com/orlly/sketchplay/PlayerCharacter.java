package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Button;

public class PlayerCharacter {

	private Bitmap bitmap;
	private int x;
	private int y;
	private int direction;
	private boolean touch;
	private int move_rate, jump_rate;
	private int jump_height;
	private int jump_counter;
	private boolean isJumping, isMoving;
	private int closest_right_wall,closest_left_wall, closest_floor;
	
	public PlayerCharacter(Bitmap bitmap, int x, int y){
		
		
		
		
		this.bitmap = bitmap;
		this.x = x; this.y = y;
		this.move_rate = 1; this.jump_rate = 4;
		this.jump_height = 16;
		this.jump_counter = 0;
		this.isJumping = false; this.isMoving = false;
	
		
	}
	
	public int getX(){
		
		return x;
	}
	
	public int getY(){
		
		return y;
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
	
	public void setTouch(Boolean touched){
		this.touch = touched;
	}
	
	public boolean getTouch(){
		return touch;
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
	
	
	
	
	
	
	
	public int getClosest_right_wall() {
		return closest_right_wall;
	}

	public void setClosest_right_wall(int closest_right_wall) {
		this.closest_right_wall = closest_right_wall;
	}

	public int getClosest_left_wall() {
		return closest_left_wall;
	}

	public void setClosest_left_wall(int closest_left_wall) {
		this.closest_left_wall = closest_left_wall;
	}

	public void draw(Canvas canvas, int x, int y){
		
		canvas.drawBitmap(bitmap, x, y , null);

	}
	
	public void move(){
		if(isMoving == true){
			x += direction;
		}
	}
	
	public void jump(){
		//if(touch == true){
		
		jump_counter++;
		y -= jump_rate;
			
		if(jump_counter == jump_height){
			isJumping = false;
			jump_counter = 0;
		}
		
				
	//	}
	}
	
	public void descend(){
		
		y += jump_rate;
		
	}
	
	
	
}
