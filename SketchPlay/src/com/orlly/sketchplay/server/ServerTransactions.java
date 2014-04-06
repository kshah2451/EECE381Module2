package com.orlly.sketchplay.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
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
	
	public void connectServer(){
		if (myapp.sock != null && myapp.sock.isConnected() && !myapp.sock.isClosed()) {
			socketConnected = true;
			return;
		}

		socketConnected = false;
		new SocketConnect().execute((Void) null);		
		
	}
	
	public static void closeServer(){
		
	}
	
	public  void sendServer(Bitmap bitmap, String filename){
		
		if(bitmap == null){
			return;
		}
		byte[] byteArray = BMPToArray(bitmap);

		
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		
		byte buf[] = new byte[byteArray.length + 13];
		buf[0] = 1; 
		buf[1] = 7; 
		
		System.arraycopy(filename.getBytes(), 0, buf, 2, filename.length());

		System.arraycopy(intToByte(byteArray.length), 0, buf, 9, 4);


		System.arraycopy(byteArray, 0, buf, 13, byteArray.length);

	
		// Now send through the output stream of the socket
		
		OutputStream out;
		try {

			out = myapp.sock.getOutputStream();
			try {
				out.write(buf, 0, byteArray.length + 13);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
		
		
	

	public  Bitmap receiveServer(){
		Bitmap bitmap = null;
		try{	
			bitmap = new retrieveImage().execute().get();
		}catch( ExecutionException ee){
			
		}catch(InterruptedException ie){
			
		}
		
		return bitmap;
		
	}

	
	public Bitmap arrayToBMP(byte[] byteArray){
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 13, byteArray.length-13);
		return bitmap;
	}

	public byte[] BMPToArray(Bitmap bitmap){
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


	public byte[] intToByte(int x){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(x);
		return buffer.array();
	}
	
	public int bytesToInt(byte[] bytes){
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
			

			if (myapp.sock != null && myapp.sock.isConnected()
					&& !myapp.sock.isClosed()) {
				
				try {
					InputStream in = myapp.sock.getInputStream();					
					int bytes_avail = in.available();
					if (bytes_avail > 0) {
						byte buf[] = new byte[bytes_avail];						
						in.read(buf);
						bitmap = arrayToBMP(buf);
					}
					}catch (IOException e) {
						e.printStackTrace();
					}
			}
			
			imageRetrieved = true;
			return bitmap;

			
		}
		
		
		
	}
	
	
	
	
    // This is the Socket Connect asynchronous thread.  Opening a socket
	// has to be done in an Asynchronous thread in Android.  Be sure you
	// have done the Asynchronous Tread tutorial before trying to understand
	// this code.

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {
		final int TIMEOUT = 10000;
			
		@Override
		protected void onPreExecute() {

			connection_timeout = false;
			socketConnected = false;
		}

		// The main parcel of work for this thread.  Opens a socket
		// to connect to the specified IP.
		@Override
		protected Socket doInBackground(Void... voids) {
			Socket s = null;
			String ip = "192.168.1.123";
			Integer port = 50002;
			
			try {
				s = new Socket();
				s.connect(new InetSocketAddress(ip, port), TIMEOUT);
				myapp.sock = s;
				setSocketConnected(true);

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				connection_timeout  = true;
			
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
