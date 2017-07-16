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

package org.catrobat.catroid.copypaste;

import org.catrobat.catroid.storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Clipboard<T extends ClipboardItem> {

	public static final String TAG = Clipboard.class.getSimpleName();

	private Class<T> type;
	private List<T> clipboard = new ArrayList<>();

	public Clipboard(Class type) throws IOException {
		this.type = type;
		StorageManager.clearDirectory(ClipboardHandler.getClipboardDirectory());
	}

	Class getItemType() {
		return type;
	}

	public void addToClipboard(List<T> items) throws CloneNotSupportedException, IOException {
		for (T item : items) {
			T clone = (T) item.clone();
			clone.copyResourcesToDirectory(ClipboardHandler.getClipboardDirectory());
			clipboard.add(clone);
		}
	}

	public List<T> getItemsFromClipboard() throws CloneNotSupportedException {
		List<T> clonedItems = new ArrayList<>();

		for (T item : clipboard) {
			clonedItems.add((T) item.clone());
		}
		return clonedItems;
	}

	public int getClipboardSize() {
		return clipboard.size();
	}
}
