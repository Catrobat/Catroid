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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceVariableAccessor;

import java.io.IOException;

public class WriteVariableOnDeviceAction extends TemporalAction {
	private static final String TAG = WriteVariableOnDeviceAction.class.getSimpleName();
	private UserVariable userVariable;

	@Override
	protected void update(float percent) {
		if (userVariable == null || userVariable.isDummy()) {
			return;
		}

		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(projectName);

		try {
			accessor.writeVariable(userVariable);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}
}
