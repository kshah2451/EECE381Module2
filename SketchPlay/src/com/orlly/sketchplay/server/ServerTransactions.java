package com.orlly.sketchplay.server;

import com.orlly.sketchplay.menus.MyApplication;

import android.content.Context;
import android.graphics.Bitmap;

public class ServerTransactions {
	
	private MyApplication myapp;
	
	public ServerTransactions(MyApplication myapp) {
		this.myapp = myapp;
	}
	
	public void connectServer(){
//		if(myapp.sock != null && myapp.sock.isConnected() && !myapp.sock.isClosed()) {
//			return;
//		}
//		
//		new SocketConnect().execute((Void) null);
	}
	
	public static void closeServer(){
		
	}
	
	public static void sendServer(){
		
	}

	public static Bitmap receiveServer(){
		return null;
	}

	
	private Bitmap arrayToBMP(){
		return null;
	}

	private char[] BMPToArray(){
		return null;
	}

}
