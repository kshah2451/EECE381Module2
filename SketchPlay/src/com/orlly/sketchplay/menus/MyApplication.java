package com.orlly.sketchplay.menus;

import java.net.Socket;

import android.app.Application;

public class MyApplication extends Application {
	public Socket sock = null;
	public String theme = "Forest";
	
	private boolean tilt_option = true;
	
	public void toggleTilt(boolean tilt_option){
		this.tilt_option = tilt_option;
	}
	
	public boolean getTilt(){
		return this.tilt_option;
	}
	
}
