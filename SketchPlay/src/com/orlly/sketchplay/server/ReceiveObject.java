package com.orlly.sketchplay.server;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ReceiveObject {
	
	private Bitmap bitmap;
	
	private ArrayList<String> filename_list;
	
	private int receiveStatus;
	
	public ReceiveObject() {
		bitmap = null;
		filename_list = new ArrayList<String>();
		receiveStatus = -1;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public ArrayList<String> getFilename_list() {
		return filename_list;
	}

	public void setFilename_list(ArrayList<String> filename_list) {
		this.filename_list = filename_list;
	}

	public int getReceiveStatus() {
		return receiveStatus;
	}

	public void setReceiveStatus(int receiveStatus) {
		this.receiveStatus = receiveStatus;
	}
	
}
