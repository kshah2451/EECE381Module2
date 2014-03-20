package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MapRender {
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
				if (hsv[2] <= value && hsv[1] <= saturation) { // .07
					pixels[i * width + j] = Color.BLACK;
				} else {
					pixels[i * width + j] = Color.WHITE;
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
