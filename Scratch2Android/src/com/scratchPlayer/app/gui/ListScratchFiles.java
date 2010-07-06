package com.scratchPlayer.app.gui;

import java.io.File;

//import com.scratchPlayer.app.FileList;

import com.scratchPlayer.app.R;
import com.scratchPlayer.app.R.drawable;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ListScratchFiles extends ListActivity {

	final String fileLocation = "";

	String currentPath = "/sdcard";
	FileList allFilePaths = new FileList();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Drawable bullet = this.getResources().getDrawable(R.drawable.bullet);
		allFilePaths.setBullet(bullet);

		findScratchFiles(fileLocation);

		IconifiedTextListAdapter scratchFileList = new IconifiedTextListAdapter(
				this);
		scratchFileList.setListItems(allFilePaths.getFileNames());
		this.setListAdapter(scratchFileList);
		ProjectFileParams.picked = false;
	}

	// rekursive Search for .sb files
	public void findScratchFiles(String dirName) {
		currentPath = currentPath + "/" + dirName;

		File tmpFile = new File(currentPath);

		String[] lTotalFiles = tmpFile.list();

		for (int i = 0; i < lTotalFiles.length; i++) {
			File helperFile = new File(currentPath + "/" + lTotalFiles[i]);
			if (helperFile.isDirectory()) {
				helperFile = null;
				findScratchFiles(lTotalFiles[i]);
			} else {
				String helperFilePath = currentPath + "/" + lTotalFiles[i];
				if (helperFilePath.endsWith("sb")) {
					allFilePaths.add(helperFile);
				}
			}
			helperFile = null;
		}

		String lTemp = "/" + dirName;
		int lEnd = currentPath.indexOf(lTemp);
		if (lEnd != -1)
			currentPath = currentPath.substring(0, lEnd);
		tmpFile = null;
	}

	// mouseListener for FileList
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("in onListItemClick");
		Bundle info = new Bundle();
		info.putString("filePath", allFilePaths.getFile(position)
				.getAbsolutePath().replace("//", "/"));
		Intent intent = new Intent();
		intent.putExtras(info);
		setResult(RESULT_OK, intent);
		finish();
		System.out.println("end onListItemClick");
	}

}