package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class Background {

	private int hazard_pic_height = 0;
	private int hazard_pic_width = 0;
	private int treasure_pic_height = 0;
	private int treasure_pic_width = 0;
	int width_count = 0;
	private Bitmap final_image;
	private Bitmap background;
	private Bitmap platform;
	private Bitmap texture;
	private Bitmap hazard;
	private Bitmap treasure;
	private Bitmap gold_texture;


	private boolean treasureSet = false;


	
	
	public Background (Bitmap background, Bitmap platform, Bitmap texture, Bitmap hazard, Bitmap treasure, Bitmap gold_texture){
		
		this.background = background;
		this.platform = platform;
		this.texture  = texture;
		this.hazard = hazard;
		this.treasure = treasure;
		this.gold_texture = gold_texture;

		

			
	}
	
	/**
	 * This will generate the game level image by calling several functions that each add 
	 * a visual element to the final game level image (the texturized platforms, the hazards
	 * , the victory platform, etc)
	 */
	public void generate_level_image(){
		//Go through the whole image of the user's level, and check the colour of each pixel to
		// determine what map element we'll have to draw
		for(int i=0; i< platform.getWidth(); i++){
			for(int j = 0; j < platform.getHeight(); j++){
				texturize_platforms(i,j);
				set_hazards(i, j);
				
				
			/*	
				if(treasureSet == false){
					set_treasures(i,j);
				}
			*/
				
			}
		}
		
		
		this.final_image = background;
	}
	
	/**
	 * Adds a texture to the black platforms that the user draws and that
	 * we render on our map
	 * @param i
	 * @param j
	 */
	
	public void texturize_platforms(int i, int j){
		
		/*********DRAW PLATFORMS*********/
		//if drawn platform is black, texturize it
		if((platform.getPixel(i, j) == Color.BLACK)|| (platform.getPixel(i, j) == Color.RED) ||(platform.getPixel(i, j) == 0xFFFFA500)){
			//replace black coloured-platform with the colour in
			// our texture bitmap specified by the pixel coordinates i,j
			background.setPixel(i, j, texture.getPixel(i, j));
		}
		else if((platform.getPixel(i, j) == Color.BLUE)|| (platform.getPixel(i, j) == Color.CYAN)){
			background.setPixel(i, j, gold_texture.getPixel(i, j));

			
		}
		
		
		
	}
	
	/**
	 * Sets a hazard image on top of red and orange platforms as drawn by the user
	 * and rendered by our MapRender
	 * @param i
	 * @param j
	 */
	
	public void set_hazards(int i, int j){
		boolean drawOkay = true;

		/***DRAW HAZARDS***/
		//if platform is [insert hazard colour here] then draw hazard on top
		//TO DO: currently only works properly for perfectly flat platforms
		// looks relly weird otherwise. Also, no logic for hazards implemented yet
		// just their image. Also, change Color.BLACK check to whatever the hazard
		// colour will be
		try{
		// check if it's a platform's surface. we only want to put hazards on the surface	
		if(((platform.getPixel(i, j) == Color.RED)||(platform.getPixel(i, j) == 0xFFFFA500)) && (platform.getPixel(i, j-1) == Color.WHITE)){
			
			
			
			//Check if there is space to draw a hazard. This checks if there is head room above a platform
			for(int check_for_space = j-1; check_for_space > j-hazard.getHeight(); check_for_space--){
				if(platform.getPixel(i, check_for_space) != Color.WHITE){
					drawOkay = false;
					break;
				}
				else
					drawOkay = true;
			}

			if(drawOkay){
				//draw the hazard using our hazard image
				for(int hazard_height = j - hazard.getHeight(); hazard_height < j; hazard_height++){
					if(hazard.getPixel(hazard_pic_width, hazard_pic_height)!= Color.BLACK){
						background.setPixel(i, hazard_height, hazard.getPixel(hazard_pic_width, hazard_pic_height));
					}
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
			
		}
		}catch (IllegalStateException e) {
		} catch (IllegalArgumentException a){

		}
		
		
		
	}
	
	/**
	 * Sets treasure image on top of Blue or Cyan platforms as drawn by the user and
	 * rendered by our MapRender class. Currently unused as the algorithm logic is flawed -
	 * it won't work for most cases
	 * @param i
	 * @param j
	 */
	
	public void set_treasures(int i, int j){
	
	try{
	//DRAW VICTORY PLATFORM 
	if((platform.getPixel(i, j) == Color.BLACK) && (platform.getPixel(i, j-1) == Color.WHITE)){

		int scaled_width = 1;
		int scaled_height = 1;
		
		int first_pixel_x = i;	//the first victory platform pixel we came across - its x value
		int first_pixel_y = j;  //the first victory platform pixel we came across - its y value
		int count = 0;
		int[] pixel_heights = new int[1000]; int height_count = 0;  //an array that will collect the height values of all pixels that
																   // make up the surface of the victory platform
		int height_average = 0;										// the average of all those heights
		width_count = 0;											//the length of that victory platform
		
		//fill up the height average array with -1's
		for(int a = 0; a < 1000; a++){

			pixel_heights[a] = -1;
		}
		
		
		//maybe change != Color.WHITE to == Color.YELLOW
		
		//while there is a victory-colour pixel to the right of the current pixel, and it is at most 1 above it in both directions
		// then we consider that pixel as being connected to this one
		while((platform.getPixel(i+1, j)== Color.BLACK) ||
			  (platform.getPixel(i+1, j-1)== Color.BLACK) || (platform.getPixel(i+1, j+1)== Color.BLACK) ){
				
			
			//let's check if that next pixel is a surface pixel or if it's inside the platform. if it's in the platform
			// then let's locate the surface, but only count pixels that are at most 5 pixels above (anything higher probably
			// isn't connected)
			while((platform.getPixel(i+1, j-1) != Color.WHITE) && (count <= 5)){
				j = j-1;
				count++;
			}
			
			// keep track of the pixel's height
			pixel_heights[height_count] = j;
			height_count++;
			//increment width count
			width_count++;
			
			//move on to the next pixel
			i++;
			
			
		}
			//get the average height of connected victory platform pixels
			for(int a = 0; a < height_count; a++){
				if(pixel_heights[a] == -1){
					break;
				}
				else{
					height_average += pixel_heights[a];
				}
			}
				if(height_count != 0){
					height_average = height_average/height_count;
				}
				else{
					height_average = first_pixel_y;
				}
			
		
	
			
			//now the drawing part
				

				if(width_count > 5*treasure.getWidth()){
					scaled_width = width_count/3;
				}
				else if(width_count < treasure.getWidth()/5){
					scaled_width = treasure.getWidth();
				}
				
				
				this.treasure = Bitmap.createScaledBitmap(treasure, scaled_width,
						(scaled_width)/2 , true);
			

				treasure_pic_width = 0;					//midpoint
				for(int treasure_width =  first_pixel_x + (width_count/2); treasure_width < (first_pixel_x + (width_count/2)) + treasure.getWidth(); treasure_width++){
					for(int treasure_height = height_average - treasure.getHeight(); treasure_height < height_average; treasure_height++){
						Log.d("treasures", "getPixel height: " + Integer.toString(treasure_pic_height));
						Log.d("treasures", "getPixel width: " + Integer.toString(treasure_pic_width));
						background.setPixel(treasure_width, treasure_height, treasure.getPixel(treasure_pic_width, treasure_pic_height));
						treasure_pic_height++;
					}
					treasure_pic_height = 0;
					treasure_pic_width++;
					
				}
				
				treasureSet = true;
				
	}
	}catch (IllegalStateException e) {
		Log.d("treasures", "illegal state");

	} catch (IllegalArgumentException a){
		Log.d("treasures", "getPixel height that throws exc: " + Integer.toString(treasure_pic_height));
		Log.d("treasures", "illegal arg");

	}
	
	
	
	
   }
	
	/**
	 * This draws the final game level image that we generate above
	 * @param canvas
	 */

	// draw the bitmap
	public void draw(Canvas canvas){
		canvas.drawBitmap(final_image, 0, 0 , null);
	}
	
	
	
	
	
	
	
	
	
	
}
