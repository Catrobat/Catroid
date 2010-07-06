package com.scratchPlayer.app.gui;

import java.io.File;
import java.util.ArrayList;


//import com.scratchPlayer.app.IconifiedText;

import android.graphics.drawable.Drawable;

public class FileList {
	private ArrayList<File> myFileList;
	private Drawable bullet;

	FileList() {
		myFileList = new ArrayList<File>();
		bullet = null;
	}

	public void setBullet(Drawable bullet){
		this.bullet = bullet;
	}
	public void add(File file) {
		myFileList.add(file);
	}

	public File getFile(int index) {
		return myFileList.get(index);
	}

	public ArrayList<IconifiedText> getFileNames() {
		ArrayList<IconifiedText> fileNames = new ArrayList<IconifiedText>();
		
		for (int i = 0; i < myFileList.size(); i++) {
			fileNames.add(new IconifiedText(myFileList.get(i).getName(), bullet));
		}
		return fileNames;

	}
}
