/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.content;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookDataHistory extends MediaHistory {
	private static final Map<Integer, LookDataHistory> INSTANCE = new HashMap<>();

	public static LookDataHistory getInstance(int spriteId) {
		if (!INSTANCE.containsKey(spriteId)) {
			INSTANCE.put(spriteId, new LookDataHistory());
		}
		return INSTANCE.get(spriteId);
	}

	public static boolean getAllUndoRedoStatus() {
		boolean result = false;

		for (LookDataHistory history : INSTANCE.values()) {
			result |= history.isRedoable();
			result |= history.isUndoable();
		}

		return result;
	}

	public static void applyChanges(String projectName) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {
			return;
		}

		List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		ArrayList<String> filesToKeep = new ArrayList<>();

		for (Sprite sprite : sprites) {
			for (LookData lookData : sprite.getLookDataList()) {
				if (!filesToKeep.contains(lookData.getAbsolutePath())) {
					filesToKeep.add(lookData.getAbsolutePath());
				}
			}
		}

		if (StorageHandler.getInstance().getSoundFileList(projectName) == null) {
			INSTANCE.clear();
			return;
		}

		for (String fileName : StorageHandler.getInstance().getLookFileList(projectName)) {
			String filePath = Utils.buildPath(Constants.DEFAULT_ROOT, projectName, Constants.IMAGE_DIRECTORY, fileName);
			if (!filesToKeep.contains(filePath) && !filePath.contains(Constants.NO_MEDIA_FILE)) {
				File fileToDelete = new File(filePath);
				fileToDelete.delete();
			}
		}

		INSTANCE.clear();
	}
}
