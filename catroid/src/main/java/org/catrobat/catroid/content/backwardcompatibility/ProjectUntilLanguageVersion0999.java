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

package org.catrobat.catroid.content.backwardcompatibility;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@XStreamAlias("program")
public class ProjectUntilLanguageVersion0999 implements Serializable {

	private static final long serialVersionUID = 1L;

	private XmlHeader header = new XmlHeader();
	private List<Setting> settings = new ArrayList<>();
	private List<UserVariable> programVariableList = new ArrayList<>();
	private List<UserList> programListOfLists = new ArrayList<>();
	private List<SceneUntilLanguageVersion0999> scenes = new ArrayList<>();

	public Project toProject() {
		Project project = new Project();
		project.setXmlHeader(getXmlHeader());
		project.getSettings().addAll(getSettings());

		project.getUserVariables().addAll(getUserVariables());
		project.getUserLists().addAll(getUserLists());

		for (SceneUntilLanguageVersion0999 legacyScene : getSceneList()) {
			Scene scene = new Scene(legacyScene.getName(), project);

			for (Sprite sprite : legacyScene.getSpriteList()) {
				sprite.getUserVariables().addAll(legacyScene.getSpriteUserVariables(sprite));
				sprite.getUserLists().addAll(legacyScene.getSpriteUserLists(sprite));
				scene.addSprite(sprite);
			}

			project.addScene(scene);
		}

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

	public List<UserVariable> getUserVariables() {
		return programVariableList;
	}

	public List<UserList> getUserLists() {
		return programListOfLists;
	}

	public List<SceneUntilLanguageVersion0999> getSceneList() {
		return scenes;
	}
}
