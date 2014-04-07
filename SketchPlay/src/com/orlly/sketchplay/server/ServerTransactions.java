package com.orlly.sketchplay.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.orlly.sketchplay.menus.MyApplication;

public class ServerTransactions {

	private MyApplication myapp;
	private boolean socketConnected = false;
	private boolean connection_timeout = false;
	private boolean imageRetrieved = false;

	public ServerTransactions(MyApplication myapp) {
		this.myapp = myapp;
	}

	public void connectServer() {
		if (myapp.sock != null && myapp.sock.isConnected()
				&& !myapp.sock.isClosed()) {
			socketConnected = true;
			return;
		}

		socketConnected = false;
		new SocketConnect().execute((Void) null);

	}

	public static void closeServer() {

	}

	public void sendServer(Bitmap bitmap, String filename) {

		if (bitmap == null) {
			return;
		}
		byte[] byteArray = BMPToArray(bitmap);

		// byte buf[] = new byte[byteArray.length + 13];
		byte buf_file_name[] = new byte[filename.length() + 6
				+ byteArray.length];
		buf_file_name[0] = 1;
		buf_file_name[1] = (byte) filename.length();

		System.arraycopy(filename.getBytes(), 0, buf_file_name, 2,
				filename.length());

		System.arraycopy(intToByte(byteArray.length), 0, buf_file_name, 3, 4);

		System.arraycopy(byteArray, 0, buf_file_name, 7, byteArray.length);

		// Send stuff and await confirmation
		OutputStream out;
		try {

			out = myapp.sock.getOutputStream();
			try {
				out.write(buf_file_name, 0, buf_file_name.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Bitmap receiveServer(String filename) {
		Bitmap bitmap = null;

		byte[] filename_buf = new byte[filename.length() + 2];

		filename_buf[0] = 2;
		filename_buf[1] = (byte) filename.length();
		System.arraycopy(filename.getBytes(), 0, filename_buf, 2,
				filename.length());

		OutputStream out;
		try {

			out = myapp.sock.getOutputStream();
			try {
				out.write(filename_buf, 0, filename_buf.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bitmap = new retrieveImage().execute().get();
		} catch (ExecutionException ee) {

		} catch (InterruptedException ie) {

		}

		return bitmap;

	}

	public Bitmap arrayToBMP(byte[] byteArray) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 13,
				byteArray.length - 13);
		return bitmap;
	}

	public byte[] BMPToArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	public boolean isSocketConnected() {
		return socketConnected;
	}

	public void setSocketConnected(boolean socketConnected) {
		this.socketConnected = socketConnected;
	}

	public boolean isConnection_timeout() {
		return connection_timeout;
	}

	public void setConnection_timeout(boolean connection_timeout) {
		this.connection_timeout = connection_timeout;
	}

	public boolean isImageRetrieved() {
		return imageRetrieved;
	}

	public void setImageRetrieved(boolean imageRetrieved) {
		this.imageRetrieved = imageRetrieved;
	}

	public byte[] intToByte(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(x);
		return buffer.array();
	}

	public int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(bytes);
		buffer.flip();
		return buffer.getInt();
	}

	public class retrieveImage extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected void onPreExecute() {

			imageRetrieved = false;

		}

		@Override
		protected Bitmap doInBackground(Void... voids) {
			Socket s = null;
			Bitmap bitmap = null;
			
			ArrayList<String> fileList = new ArrayList<String>();
			
			String tempFile = "";

			int bytes_avail = 0;
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0L;

			byte buf[] = new byte[1];

			if (myapp.sock != null && myapp.sock.isConnected()
					&& !myapp.sock.isClosed()) {

				try {
					InputStream in = myapp.sock.getInputStream();
					while (bytes_avail <= 0 && elapsedTime < 60 * 1000) {
						bytes_avail = in.available();
						elapsedTime = (new Date()).getTime() - startTime;
						if (bytes_avail > 0) {
							buf = new byte[1];
							in.read(buf);
							bytes_avail = 0;
							break;
						}
					}

					if (buf[0] == '2') {
						// File is not found
						startTime = System.currentTimeMillis();
						elapsedTime = 0L;
						while (bytes_avail <= 0 && elapsedTime < 60 * 1000) {
							bytes_avail = in.available();
							elapsedTime = (new Date()).getTime() - startTime;
							if (bytes_avail > 0) {
								buf = new byte[1];
								in.read(buf);
								bytes_avail = 0;
								break;
							}
						}

						if (buf[0] == '2') {
							// Quit transaction
						} else {
							while (buf[0] != 1) {
								Log.d("server", "" + (char) buf[0]);
								startTime = System.currentTimeMillis();
								elapsedTime = 0L;
								while (bytes_avail <= 0
										&& elapsedTime < 60 * 1000) {
									bytes_avail = in.available();
									elapsedTime = (new Date()).getTime()
											- startTime;
									if (bytes_avail > 0) {
										buf = new byte[1];
										in.read(buf);
										bytes_avail = 0;
										if(buf[0] != 32) {
											tempFile += (char) buf[0];
										}
										else {
											fileList.add(tempFile);
											tempFile = "";
										}
										break;
									}
								}
							}
							
							//Log.d("server", fileList);
							
							
						}

					} else if (buf[0] == '1') {
						// File is found
						while (buf[0] != 1) {

							// get next char
							startTime = System.currentTimeMillis();
							elapsedTime = 0L;
							while (bytes_avail <= 0 && elapsedTime < 60 * 1000) {
								bytes_avail = in.available();
								elapsedTime = (new Date()).getTime()
										- startTime;
								if (bytes_avail > 0) {
									buf = new byte[1];
									in.read(buf);
									bytes_avail = 0;
									break;
								}
							}

							if (buf[0] != 1) {
								// do something with char
								Log.d("server", "" + (char) buf[0]);
							}
						}

					} else {

					}

					// bitmap = arrayToBMP(buf);

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			imageRetrieved = true;
			return bitmap;

		}
	}

	// This is the Socket Connect asynchronous thread. Opening a socket
	// has to be done in an Asynchronous thread in Android. Be sure you
	// have done the Asynchronous Tread tutorial before trying to understand
	// this code.

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {
		final int TIMEOUT = 10000;

		@Override
		protected void onPreExecute() {

			connection_timeout = false;
			socketConnected = false;
		}

		// The main parcel of work for this thread. Opens a socket
		// to connect to the specified IP.
		@Override
		protected Socket doInBackground(Void... voids) {
			Socket s = null;
			String ip = "192.168.1.144";
			Integer port = 50002;

			try {
				s = new Socket();
				s.connect(new InetSocketAddress(ip, port), TIMEOUT);
				myapp.sock = s;
				setSocketConnected(true);

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				connection_timeout = true;

			}

			return s;
		}

		// After executing the doInBackground method, this is
		// automatically called, in the UI (main) thread to store
		// the socket in this app's persistent storage
		@Override
		protected void onPostExecute(Socket s) {

			Log.d("server", "onPostExecute is called");
		}
	}

}
