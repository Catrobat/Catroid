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

import android.content.Context;

public class BrickField implements Cloneable {

	private String name;
	private int viewId;
	private BrickFieldObject object;

	public BrickField(String name, int viewId, BrickFieldObject object) {
		this.name = name;
		this.viewId = viewId;
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public int getViewId() {
		return viewId;
	}

	public BrickFieldObject getObject() {
		return object;
	}

	public String getDisplayText(Context context) {
		return object.getDisplayText(context);
	}

	@Override
	public BrickField clone() throws CloneNotSupportedException {
		return new BrickField(name, viewId, object.clone());
	}
}
