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
package org.catrobat.catroid.content.backwardcompatibility;

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@XStreamAlias("program")
public class LegacyProjectWithoutScenes implements Serializable {

	private static final long serialVersionUID = 1L;

	private XmlHeader header = new XmlHeader();
	private List<Sprite> objectList = new ArrayList<>();
	private LegacyDataContainer data = null;
	private List<Setting> settings = new ArrayList<>();

	public Project toProject(Context context) throws IOException {
		File projectDir = getDirectory();

		Project project = new Project();
		project.setXmlHeader(getXmlHeader());
		project.getSettings().addAll(getSettings());

		project.getUserVariables().addAll(getProjectUserVariables());
		project.getUserLists().addAll(getProjectUserLists());

		for (Sprite sprite : getSpriteList()) {
			sprite.getUserVariables().addAll(getSpriteUserVariables(sprite));
			sprite.getUserLists().addAll(getSpriteUserLists(sprite));
		}

		Scene scene = new Scene(context.getString(R.string.default_scene_name), project);
		scene.getSpriteList().addAll(getSpriteList());
		project.addScene(scene);

		StorageOperations.createSceneDirectory(scene.getDirectory());

		File automaticScreenshot = new File(projectDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshot = new File(projectDir, SCREENSHOT_MANUAL_FILE_NAME);

		StorageOperations.copyDir(new File(projectDir, IMAGE_DIRECTORY_NAME),
				new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME));
		StorageOperations.copyDir(new File(projectDir, SOUND_DIRECTORY_NAME),
				new File(scene.getDirectory(), SOUND_DIRECTORY_NAME));

		if (automaticScreenshot.exists()) {
			StorageOperations.copyFileToDir(automaticScreenshot, scene.getDirectory());
			automaticScreenshot.delete();
		}
		if (manualScreenshot.exists()) {
			StorageOperations.copyFileToDir(manualScreenshot, scene.getDirectory());
			manualScreenshot.delete();
		}

		StorageOperations.deleteDir(new File(projectDir, IMAGE_DIRECTORY_NAME));
		StorageOperations.deleteDir(new File(projectDir, SOUND_DIRECTORY_NAME));

		return project;
	}

	public XmlHeader getXmlHeader() {
		return header;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public File getDirectory() {
		return new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(header.getProjectName()));
	}

	public List<Sprite> getSpriteList() {
		return objectList;
	}

	public List<UserVariable> getProjectUserVariables() {
		if (data != null && data.projectVariables != null) {
			return data.projectVariables;
		}
		return Collections.emptyList();
	}

	public List<UserList> getProjectUserLists() {
		if (data != null && data.projectLists != null) {
			return data.projectLists;
		}
		return Collections.emptyList();
	}

	public List<UserVariable> getSpriteUserVariables(Sprite sprite) {
		if (data != null && data.spriteVariables.get(sprite) != null) {
			return data.spriteVariables.get(sprite);
		}
		return Collections.emptyList();
	}

	public List<UserList> getSpriteUserLists(Sprite sprite) {
		if (data != null && data.spriteListOfLists.get(sprite) != null) {
			return data.spriteListOfLists.get(sprite);
		}
		return Collections.emptyList();
	}
}
