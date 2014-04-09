package com.orlly.sketchplay.menus;

import java.net.Socket;

import android.app.Application;

public class MyApplication extends Application {
	public Socket sock = null;
	public String theme = "Forest";
	
	private boolean tilt_option = true;
	private float tilt_sensitivity = 5.0f;
	private int tilt_seekbar = 25; 
	
	public void toggleTilt(boolean tilt_option){
		this.tilt_option = tilt_option;
	}
	
	public boolean getTilt(){
		return this.tilt_option;
	}
	
	public void setTiltSensitivity(float tilt_sensitivity){
		this.tilt_sensitivity = tilt_sensitivity;
	}
	
	public float getTiltSensitivity(){
		return this.tilt_sensitivity;
	}
	public void setTiltSeekbar(int tilt_seekbar){
		this.tilt_seekbar = tilt_seekbar;
	}
	public int getTiltSeekbar(){
		return this.tilt_seekbar;
	}
	
}
