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

package org.catrobat.catroid.formulaeditor.datacontainer;

import java.util.List;

abstract class ProjectDataBehaviour<V> {

	protected abstract List<V> getDataList();
	protected abstract V newInstance(String name);
	protected abstract String getDataName(V data);
	protected abstract void setDataName(V data, String name);

	V add(String name) {
		V dataToAdd = newInstance(name);
		getDataList().add(dataToAdd);
		return dataToAdd;
	}

	V rename(String newName, String oldName) {
		V dataToRename = find(oldName);
		setDataName(dataToRename, newName);
		return dataToRename;
	}

	boolean exists(String name) {
		return find(name) != null;
	}

	V find(String name) {
		for (V data : getDataList()) {
			if (getDataName(data).equals(name)) {
				return data;
			}
		}
		return null;
	}
}
