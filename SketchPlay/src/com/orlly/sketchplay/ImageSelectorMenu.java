package com.orlly.sketchplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageSelectorMenu extends Activity {
	
	/**
	 * Button views
	 */
	Button take_picture;
	Button import_picture;
	Button play_game;
	
	private Bitmap bitmap;
	private ImageView preview;
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int REQUEST_CODE = 1;
	
	private static final int MEDIA_TYPE_IMAGE = 1;
	
	private MapRender rendering;
	
	/**
	 * Uniform Resource Identifier - an address that identifies an abstract
	 * or physical resource.
	 */
	private Uri fileUri; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_menu);
		
		// Find views by id attributes identified in XML file
		take_picture = (Button)findViewById(R.id.take_picture_button);
		import_picture = (Button)findViewById(R.id.import_picture_button);
		play_game = (Button)findViewById(R.id.play_game_button);
		preview = (ImageView)findViewById(R.id.img_preview);
	}
	
	
	/**
	 * Function called when "take a picture" button is pressed
	 * @param view
	 */
	public void takePicture(View view) {
		// Create new intent to have camera application capture an image and return it
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// Creates and returns a file Uri for saving an image
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		
		// Add extended data to the intent
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		
		// Start the intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
			// If image captured and saved to fileUri specified in Intent then...
				
				try {
					// Retrives an image from fileURI
					if(bitmap != null) {
						bitmap.recycle();
					}
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
					
					//ADDED IN BY DAVID
					rendering = new MapRender(bitmap);
					// Sets bitmap as content of image view
					preview.setImageBitmap(rendering.getMapImage());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(resultCode == RESULT_CANCELED) {
			// If user cancelled the image capture then...
				
				Toast.makeText(this, "Cancelled image capture", Toast.LENGTH_LONG).show();
				
				// TODO: Add code to do something		
			}
			else {
			// If image capture failed then...
				
				Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG).show();
				
				// TODO: Add code to do something
			}
		}
		else if(requestCode == REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
				try {
					InputStream stream = getContentResolver().openInputStream(data.getData());
					bitmap = BitmapFactory.decodeStream(stream);
					stream.close();
					
					// TODO: Add code to do image manipulation / conversion
					rendering = new MapRender(bitmap);
					// Sets bitmap as content of image view
					preview.setImageBitmap(rendering.getMapImage());
					
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}


	/**
	 * Function called when "import picture" button is pressed
	 * @param view
	 */
	public void importPicture(View view) {
		Intent intent = new Intent();
		
		// Sets the explicit MIME media type as all image types
		intent.setType("image/*");
			
		// Sets the action of the intent to allow user to select image and return it.
		intent.setAction(Intent.ACTION_GET_CONTENT);
		
		// Indicates that intent only wants openable URIs
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		
		// Start the intent
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	
	/**
	 * Create a file Uri for saving an image
	 * @param type
	 * @return
	 */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	
	/**
	 * Create a File for saving an image
	 * @param type
	 * @return
	 */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
		// Constructs a new file using the specified directory and name
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "SketchPlay");

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("SketchPlay", "failed to create directory");
	            return null;
	        }
	    }

	    /* Create a media file name */
	    // Create a timestamp with current time in specified format
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	    	// Construct a new file with the specified path
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".bmp");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	
	
	
	
	
	public void playGame(View view){
		Intent intent = new Intent(this, Game.class);
		
//background code that i had to temporarily remove
//		Bundle bundle = new Bundle();
//		bundle.putIntArray("pixel_array", rendering.getPixelArray());
//		bundle.putInt("width",rendering.getWidth());
//		bundle.putInt("height",rendering.getHeight());
//		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	
}
