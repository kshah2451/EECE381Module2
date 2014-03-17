package com.orlly.sketchplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class MapRender {
	private int height=480;
	private int width=640;
	private int[] pixels = new int[width*height];
	private float[] hsv = new float[3];
	
	public MapRender(Bitmap pictureToRender){
/*		CODE TO GET GRAYSCALE
  		Canvas canvas = new Canvas(imageToRender);
		Paint paint = new Paint();
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
		paint.setColorFilter(colorFilter);
		canvas.drawBitmap(pictureToRender,0,0,paint);*/
		pictureToRender.getPixels(pixels, 0, width, 0, 0, width, height);
	}
	
	public Bitmap getMapImage(){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
		
				Color.colorToHSV(pixels[i*width + j],hsv);
				if(hsv[1]>.07){
					pixels[i*width + j] = Color.BLACK;
				}
				else{
					pixels[i*width + j] = Color.WHITE;
				}
			}
		}
		Bitmap blackAndWhite = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
		return blackAndWhite;
	}
	
	public static int[][] convertTo2DArray(int[] pixels, int height, int width){
		//height = pictureToRender.getHeight();
		//width = pictureToRender.getWidth();
		int[][] pixels2D = new int[height][width];
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				pixels2D[i][j] = pixels[i*height + j];
			}
			
		}
		return pixels2D;
	}
	
	public int[] getPixelArray(){
		return pixels;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	
}
