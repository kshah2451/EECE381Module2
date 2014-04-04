package com.orlly.sketchplay.menus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import com.orlly.sketchplay.menus.R;
import com.orlly.sketchplay.rendering.MapRender;
import com.orlly.sketchplay.server.ServerTransactions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);

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
				Log.d("SketchPlay", "failed to create directory");
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

		builder.setTitle("Send / Receive Image From Server");
		builder.setItems(R.array.server_options_array,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// Send to server
						if (which == SEND_TO_SERVER) {
							MyApplication app = (MyApplication) getApplication();
							String msg = "1231412";

							ServerTransactions server_connect = new ServerTransactions(
									app);
							server_connect.connectServer();

							byte buf[] = new byte[msg.length() + 1];
							buf[0] = (byte) msg.length();
							System.arraycopy(msg.getBytes(), 0, buf, 1,
									msg.length());

							OutputStream out;
							try {
								out = app.sock.getOutputStream();
								try {
									out.write(buf, 0, msg.length() + 1);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// Receive from server
						else if (which == RECV_FROM_SERVER) {

						}
						// Do nothing, user cancelled
						else if (which == CANCEL) {

						}
					}
				});

		builder.create().show();
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

	public void connectServer() {
		MyApplication myapp = (MyApplication) getApplication();
		if (myapp.sock != null && myapp.sock.isConnected()
				&& !myapp.sock.isClosed()) {
			return;
		}

		new SocketConnect().execute((Void) null);
	}

	// @Override
	// protected void onResume() {
	// BackgroundMusic.play();
	// super.onResume();
	// }
	//
	// @Override
	// public void onBackPressed() {
	// BackgroundMusic.play();
	// super.onBackPressed();
	// }
	//
	//
	// @Override
	// protected void onStop() {
	// BackgroundMusic.stop();
	// super.onStop();
	// }

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		@Override
		protected Socket doInBackground(Void... params) {
			Socket socket = null;
			String ip = "192.168.1.133";
			int port = 50002;

			try {
				socket = new Socket(ip, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return socket;
		}

		protected void onPostExecute(Socket s) {
			MyApplication myApp = (MyApplication) ImageSelectionMenu.this
					.getApplication();
			myApp.sock = s;
		}

	}

	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			MyApplication app = (MyApplication) getApplication();
			if (app.sock != null && app.sock.isConnected()
					&& !app.sock.isClosed()) {
				try {
					InputStream in = app.sock.getInputStream();

					int bytes_avail = in.available();
					if (bytes_avail > 0) {
						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						final String s = new String(buf, 0, bytes_avail,
								"US-ASCII");

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
