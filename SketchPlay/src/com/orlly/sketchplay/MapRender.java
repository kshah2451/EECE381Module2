package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MapRender {
	
	public static final int ORANGE = 0xFFFFA500;
	private int height;
	private int width;
	
	/**
	 * Stores bitmap pixels
	 */
	private int[] pixels;

	private float[] hsv = new float[3];

	public MapRender(Bitmap pictureToRender, int bmp_height, int bmp_width) {
		height = bmp_height;
		width = bmp_width;
		pixels = new int[width * height];
		pictureToRender.getPixels(pixels, 0, width, 0, 0, width, height);
	}

	/**
	 * Converts this.bitmap into a black and white bitmap. Modifies the pixels
	 * array to contain the updated black and white bitmap's pixels.
	 * 
	 * @return Converted black and white bitmap.
	 */
	public Bitmap getMapImage(int saturation_tracker, int value_tracker) {
		float saturation=(float)saturation_tracker;
		saturation = (float)saturation/100;
		float value= (float)value_tracker;
		value = (float)value / 100;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color.colorToHSV(pixels[i * width + j], hsv);
				if (hsv[2] <= value) {
					pixels[i * width + j] = Color.BLACK;
				}
				else if(hsv[1] <= saturation) {
					pixels[i * width + j] = Color.WHITE;
				}
				else{
		if(hsv[0]<=40){
						pixels[i * width + j] = ORANGE;
					}
					else if(hsv[0]<=70 && hsv[0]>40){
						pixels[i * width + j] = Color.YELLOW;
					} 
					else if(hsv[0]<=140 && hsv[0]>70){
						pixels[i * width + j] = Color.GREEN;
					}
					
					else if(hsv[0]<=200 && hsv[0]>140){
						pixels[i * width + j] = Color.CYAN;
					}
					else if(hsv[0]<=260 && hsv[0]>200){
						pixels[i * width + j] = Color.BLUE;
					}
					
					else if(hsv[0]<=310 && hsv[0]>260){
						pixels[i * width + j] = Color.MAGENTA;
					}
					


					else{
						pixels[i * width + j] = Color.RED;
					}
				}
			}
		}
		Bitmap blackAndWhite = Bitmap.createBitmap(pixels, 0, width, width,
				height, Bitmap.Config.ARGB_8888);
		return blackAndWhite;
	}

	
	/**
	 * Return this.pixels
	 * @return this.pixels
	 */
	public int[] getPixelArray() {
		return pixels;
	}
	
	/**
	 * Return this.height
	 * @return this.height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Return this.width
	 * @return this.width
	 */
	public int getWidth() {
		return width;
	}

}
