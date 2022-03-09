/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.devices.multiplayer.MultiplayerInterface;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;

import static org.catrobat.catroid.bluetooth.base.BluetoothDevice.MULTIPLAYER;
import static org.catrobat.catroid.common.CatroidService.BLUETOOTH_DEVICE_SERVICE;
import static org.catrobat.catroid.common.Constants.TEXT_FROM_CAMERA_SENSOR_HASHCODE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.convertArgumentToDouble;
import static org.koin.java.KoinJavaComponent.inject;

public class SetVariableAction extends TemporalAction {

	private Scope scope;
	private Formula changeVariable;
	private UserVariable userVariable;

	@Override
	protected void update(float percent) {
		if (userVariable == null) {
			return;
		}
		Object value = changeVariable == null ? Double.valueOf(0d)
				: changeVariable.interpretObject(scope);

		if (changeVariable != null && changeVariable.getRoot().isBoolean(scope)) {
			value = (Double) value != 0;
		}

		boolean isFirstLevelStringTree = false;
		if (changeVariable != null && changeVariable.getRoot().getElementType() == FormulaElement.ElementType.STRING) {
			isFirstLevelStringTree = true;
		}

		try {
			if (!isFirstLevelStringTree && value instanceof String && userVariable.hashCode() != TEXT_FROM_CAMERA_SENSOR_HASHCODE
					&& convertArgumentToDouble(value) != null) {
				value = convertArgumentToDouble(value);
			}
		} catch (NumberFormatException numberFormatException) {
			Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
		}
		userVariable.setValue(value);

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		UserVariable multiplayerVariable = projectManager.getCurrentProject().getMultiplayerVariable(userVariable.getName());
		if (multiplayerVariable != null) {
			MultiplayerInterface multiplayerDevice = getMultiplayerDevice();
			if (multiplayerDevice != null) {
				multiplayerDevice.sendChangedMultiplayerVariables(userVariable);
			}
		}
	}

	public MultiplayerInterface getMultiplayerDevice() {
		return ServiceProvider.getService(BLUETOOTH_DEVICE_SERVICE).getDevice(MULTIPLAYER);
	}

	public void setUserVariable(UserVariable userVariable) {
		if (userVariable == null) {
			return;
		}
		this.userVariable = userVariable;
	}

	public void setChangeVariable(Formula changeVariable) {
		this.changeVariable = changeVariable;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
