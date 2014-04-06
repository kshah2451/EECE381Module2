package com.orlly.sketchplay.menus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.orlly.sketchplay.rendering.MapRender;
import com.orlly.sketchplay.server.ServerTransactions;

public class ImageSelectionMenu extends Activity {

	private Bitmap bitmap;

	/**
	 * ImageView for preview image
	 */
	private ImageView preview;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int REQUEST_CODE = 1;

	private static final int MEDIA_TYPE_IMAGE = 1;

	private static final int SEND_TO_SERVER = 0;
	private static final int RECV_FROM_SERVER = 1;
	private static final int CANCEL = 2;

	private MapRender rendering;
	
	
	private AlertDialog dialog;

	/**
	 * Uniform Resource Identifier - an address that identifies an abstract or
	 * physical resource.
	 */
	private Uri fileUri;
	private Uri returnUri = null;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		// Find views by id attributes identified in XML file
		preview = (ImageView) findViewById(R.id.img_preview);


	}

	/**
	 * Function called when "take a picture" button is pressed
	 * 
	 * @param view
	 */
	public void takePicture(View view) {
		// Create new intent to have camera application capture an image and
		// return it
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Creates and returns a file Uri for saving an image
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		// Add extended data to the intent
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// Start the intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

		Toast.makeText(
				this,
				"Please take picture in landscape mode and set resolution to 640 x 480.",
				Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Return from camera application intent
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// If image captured and saved to fileUri specified in Intent
				// then...

				try {
					if (bitmap != null) {
						bitmap.recycle();
					}

					returnUri = fileUri;

					// Retrives an image from fileURI
					bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), fileUri);

					preview.setImageBitmap(bitmap);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Cancelled image capture",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG)
						.show();
			}
		}

		// Return from import picture intent
		else if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				try {
					returnUri = data.getData();
					InputStream stream = getContentResolver().openInputStream(
							returnUri);
					bitmap = BitmapFactory.decodeStream(stream);
					stream.close();

					// rendering = new MapRender(bitmap, bitmap.getHeight(),
					// bitmap.getWidth());

					// Sets bitmap as content of image view
					preview.setImageBitmap(bitmap);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Function called when "import picture" button is pressed
	 * 
	 * @param view
	 */
	public void importPicture(View view) {
		Intent intent = new Intent();

		// Sets the explicit MIME media type as all image types
		intent.setType("image/*");

		// Sets the action of the intent to allow user to select image and
		// return it.
		intent.setAction(Intent.ACTION_GET_CONTENT);

		// Indicates that intent only wants openable URIs
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Start the intent
		startActivityForResult(intent, REQUEST_CODE);
	}

	/**
	 * Create a file Uri for saving an image
	 * 
	 * @param type
	 * @return
	 */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * Create a File for saving an image
	 * 
	 * @param type
	 * @return
	 */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// Constructs a new file using the specified directory and name
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"SketchPlay");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		/* Create a media file name */
		// Create a timestamp with current time in specified format
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			// Construct a new file with the specified path
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".bmp");
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Function called when "Continue" button is pressed. Launches Game.
	 * 
	 * @param view
	 */
	public void toImageAdjustment(View view) {
		if (returnUri == null) {
			Toast.makeText(
					this,
					"No image specified. Please select an image for the background.",
					Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(this, RenderImageMenu.class);
			intent.putExtra("imageUri", returnUri.toString());
			startActivity(intent);
		}
	}

	/**
	 * Function called when "Send/Receive Image" button is pressed. Opens alert
	 * dialog that lets user choose between sending or receiving.
	 * 
	 * @param view
	 */
	public void serverOptions(View view) {

		
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ImageSelectionMenu.this); // Read Update
		LayoutInflater li = LayoutInflater.from(this);
		View v1 = li.inflate(R.layout.dialog_layout, null);
		builder.setTitle("Send / Receive Image From Server");
		builder.setView(v1);
		
		dialog = builder.create();
		dialog.show();
		
	}
	
	/**
	 * Function called when "Send Image to Server" dialog button is pressed.
	 * Sends an image file to the DE2 server
	 * 
	 * @param view
	 */
	public void sendToServer(View view){
		
		EditText filename = (EditText)dialog.findViewById(R.id.filename);
		String fileToSend = filename.getText().toString();
		
		if(fileToSend == null || fileToSend.trim().equals("") ){
			Toast.makeText(this, "Please Name Your File", Toast.LENGTH_LONG).show();
		}
		
		else{
			
			ServerTransactions server = new ServerTransactions((MyApplication)getApplication());
			server.connectServer();
	
			while(!server.isSocketConnected() && (server.isConnection_timeout()) == false);
	
			if(!server.isConnection_timeout()){
				server.sendServer(bitmap, fileToSend);
				server.setSocketConnected(false);
			}
			else{
				Toast.makeText(this, "Could not connect to server", Toast.LENGTH_LONG).show();
				dialog.dismiss();
				
			}
		}
		
	}
	
	/**
	 * Function called when "Receive Image from Server" dialog button is pressed.
	 * Receives an image file to the DE2 server
	 * 
	 * @param view
	 */
	public void receiveFromServer(View view){
		
		
		
		ServerTransactions server = new ServerTransactions((MyApplication)getApplication());
		server.connectServer();


		while(!server.isSocketConnected() && (server.isConnection_timeout()) == false);

		if(!server.isConnection_timeout()){
			bitmap = server.receiveServer();
			
			while(server.isImageRetrieved() == false);
			if(bitmap == null){
				Toast.makeText(this, "Image could not be succesfully received", Toast.LENGTH_LONG).show();
				
			}
			else{
				preview.setImageBitmap(bitmap);
				Toast.makeText(this, "Image succesfully received", Toast.LENGTH_LONG).show();
				dialog.dismiss();

			}
			server.setSocketConnected(false);
			server.setImageRetrieved(false);

		}
		else{
			Toast.makeText(this, "Could not connect to server", Toast.LENGTH_LONG).show();
			dialog.dismiss();			
		}
		
		
	}

	/**
	 * Function called when "Cancel" dialog button is pressed.
	 * Cancels Dialog Box
	 * 
	 * @param view
	 */
	public void cancel(View view){
		dialog.dismiss();
	}
	

	/**
	 * Function called when "Getting Started" action bar item is pressed.
	 * Launches GettingStarted activity.
	 * 
	 * @param item
	 * @return
	 */
	public boolean gettingStartedActionBar(MenuItem item) {
		Intent intent = new Intent(this, GettingStartedMenu.class);
		startActivity(intent);
		return true;
	}

	/**
	 * Function called when "Options" action bar item (Options icon) is pressed.
	 * Launches GettingStarted activity.
	 * 
	 * @param item
	 * @return
	 */
	public boolean optionsActionBar(MenuItem item) {
		Intent intent = new Intent(this, OptionsMenu.class);
		startActivity(intent);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}






}
