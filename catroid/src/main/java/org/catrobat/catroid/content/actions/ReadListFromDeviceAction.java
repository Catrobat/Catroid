/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.content.actions;

import android.os.AsyncTask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceUserDataAccessor;

import java.io.File;

public class ReadListFromDeviceAction extends AsynchronousAction {
	private UserList userList;
	private boolean readActionFinished;

	@Override
	public boolean act(float delta) {
		if (userList == null) {
			return true;
		}
		return super.act(delta);
	}

	@Override
	public void initialize() {
		readActionFinished = false;
		new ReadListFromDeviceAction.ReadTask().execute(userList);
	}

	@Override
	public boolean isFinished() {
		return readActionFinished;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	private class ReadTask extends AsyncTask<UserList, Void, Void> {

		@Override
		protected Void doInBackground(UserList[] userList) {
			File projectDirectory = ProjectManager.getInstance().getCurrentProject().getDirectory();
			DeviceUserDataAccessor accessor = new DeviceListAccessor(projectDirectory);

			for (UserList list: userList) {
				accessor.readUserData(list);
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void o) {
			readActionFinished = true;
		}
	}
}
