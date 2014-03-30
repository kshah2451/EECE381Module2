package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class Background {

	private int hazard_pic_height = 0;
	private int hazard_pic_width = 0;
	private Bitmap final_image;
	
	
	public Background (Bitmap background, Bitmap platform, Bitmap texture, Bitmap hazard){
		
		//Go through the whole image of the user's level, and check the colour of each pixel to
		// determine what map element we'll have to draw
		for(int i=0; i< platform.getWidth(); i++){
			for(int j = 0; j < platform.getHeight(); j++){
				
				
				/*********DRAW PLATFORMS*********/
				//if drawn platform is black, texturize it
				if(platform.getPixel(i, j) == Color.BLACK){
					//replace black coloured-platform with the colour in
					// our texture bitmap specified by the pixel coordinates i,j
					background.setPixel(i, j, texture.getPixel(i, j));
				}
				
				
				
				
				
				/*********DRAW HAZARDS*********/
				//if platform is [insert hazard colour here] then draw hazard on top
				//TO DO: currently only works properly for perfectly flat platforms
				// looks relly weird otherwise. Also, no logic for hazards implemented yet
				// just their image. Also, change Color.BLACK check to whatever the hazard
				// colour will be
				try{
				// check if it's a platform's surface. we only want to put hazards on the surface	
				if((platform.getPixel(i, j) == Color.BLACK) && (platform.getPixel(i, j-1) == Color.WHITE)){
					//draw the hazard using our hazard image
					for(int hazard_height = j - hazard.getHeight(); hazard_height < j; hazard_height++){
						background.setPixel(i, hazard_height, hazard.getPixel(hazard_pic_width, hazard_pic_height));
						hazard_pic_height++;
					}
					
					// reset hazard pic width counter if it reaches end of picture, otherwise increment
					// could maybe use a modulo here, but my implementation of it wasnt working
					if((hazard_pic_width+1) == hazard.getWidth()){
						hazard_pic_width = 0;
					}
					else{
						hazard_pic_width++; 
					}
					
					//reset hazard pic height
					hazard_pic_height = 0;
					
				}
				}catch (IllegalStateException e) {

				} catch (IllegalArgumentException a){
					
				}


			}
		}
		//our final bitmap image will contain the final version of the background
		//image after it has gone been edited above
		this.final_image = background;

			
	}
	

	// draw the bitmap
	public void draw(Canvas canvas){
		canvas.drawBitmap(final_image, 0, 0 , null);
	}
	
	
	
	
	
	
	
	
	
	
}
