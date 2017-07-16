/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.data.brick;

import android.view.View;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.copypaste.ClipboardItem;
import org.catrobat.catroid.storage.DirectoryPathInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Brick implements Serializable, ClipboardItem, View.OnClickListener {

	public static final String TAG = Brick.class.getSimpleName();

	protected int resourceId;
	Set<BrickField> brickFields = new HashSet<>();

	public int getResourceId() {
		return resourceId;
	}

	public Set<BrickField> getBrickFields() {
		return brickFields;
	}

	public BrickField getBrickFieldById(int viewId) throws Exception {
		for (BrickField brickField : brickFields) {
			if (brickField.getViewId() == viewId) {
				return brickField;
			}
		}
		throw new Exception("Brick field not found.");
	}

	public abstract Action getAction();

	@Override
	public abstract Brick clone() throws CloneNotSupportedException;

	@Override
	public void copyResourcesToDirectory(DirectoryPathInfo directoryPathInfo) throws IOException {
	}
}
