package org.catrobat.catroid.content.actions;

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class ReadChatListFromDeviceAction extends AsynchronousAction {
	 UserList userList;
	 public static String jsontransfer;
	public static boolean isFresh = false;
	private boolean readActionFinished;

	@Override
	public boolean act(float delta) {
		return userList == null || super.act(delta);
	}

	@Override
	public void initialize() {
		readActionFinished = false;
		isFresh = false;
		new ReadTask().execute();
	}

	@Override
	public boolean isFinished() {
		return readActionFinished;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	private class ReadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			File projectDirectory = ProjectManager.getInstance().getCurrentProject().getDirectory();
			File jsonDirectory = new File("/data/user/0/org.catrobat"
					+ ".catroid/files/catroidchatbot/");

			File[] jsonFiles = jsonDirectory.listFiles((dir, name) -> name.endsWith(".json"));
			jsontransfer=userList.getName();
			Log.d("value", "JSON file: "+jsontransfer);
			if (jsonFiles != null) {
				for (File jsonFile : jsonFiles) {
					readFileToUserList(jsonFile);
				}
			}
			return null;
		}

		private void readFileToUserList(File aimlFile) {
			try (BufferedReader reader = new BufferedReader(new FileReader(aimlFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					// Use the correct method to add items to the UserList
					if (userList != null) {
						userList.addListItem(line); // Change this to the correct method for adding items
					}
				}
			} catch (IOException e) {
				Log.e("native", "Execution failed: " + e.getLocalizedMessage());
			}
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			readActionFinished = true;
			isFresh = true;
		}
	}
}
