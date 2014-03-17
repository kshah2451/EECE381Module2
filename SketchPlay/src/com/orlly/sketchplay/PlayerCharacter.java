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
	private int move_rate;
	
	public PlayerCharacter(Bitmap bitmap, int x, int y){
		
		
		
		
		this.bitmap = bitmap;
		this.x = x; this.y = y;
		this.move_rate = 4;
	
		
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
	
	
	public void draw(Canvas canvas, int x, int y){
		
		canvas.drawBitmap(bitmap, x, y , null);

	}
	
	public void move(){
		if(touch == true){
			x += direction;
		}
	}
	
	

	public void handleTouch(int x, int y, MainGamePanel panel){
		
		
		if((x > panel.getWidth()/2)){
			setTouch(true);
			direction = move_rate;
		}
		else if (x <= panel.getWidth()/2){
			setTouch(true);
			direction = -(move_rate);
		}
		else
			setTouch(false);
		
		
	}
}
