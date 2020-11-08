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
package org.catrobat.catroid.content;

import android.content.Context;
import android.os.Build;

import com.badlogic.gdx.math.Rectangle;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BroadcastMessageContainer;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.utils.Utils.SPEECH_RECOGNITION_SUPPORTED_LANGUAGES;

@XStreamAlias("program")
@XStreamFieldKeyOrder({
		"header",
		"settings",
		"scenes",
		"programVariableList",
		"programListOfLists",
		"programMultiplayerVariableList"
})
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("header")
	private XmlHeader xmlHeader = new XmlHeader();
	@XStreamAlias("settings")
	private List<Setting> settings = new ArrayList<>();
	@XStreamAlias("programVariableList")
	private List<UserVariable> userVariables = new ArrayList<>();
	@XStreamAlias("programMultiplayerVariableList")
	private List<UserVariable> multiplayerVariables = new ArrayList<>();
	@XStreamAlias("programListOfLists")
	private List<UserList> userLists = new ArrayList<>();
	@XStreamAlias("scenes")
	private List<Scene> sceneList = new ArrayList<>();

	private transient File directory;

	private transient BroadcastMessageContainer broadcastMessageContainer = new BroadcastMessageContainer();

	public Project() {
	}

	public Project(Context context, String name, boolean landscapeMode, boolean isCastProject) {
		xmlHeader.setProjectName(name);
		xmlHeader.setDescription("");
		xmlHeader.setlandscapeMode(landscapeMode);

		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			ScreenValueHandler.updateScreenWidthAndHeight(context);
		}
		if (landscapeMode && ScreenValues.SCREEN_WIDTH < ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		} else if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}

		xmlHeader.virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		xmlHeader.virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;

		if (isCastProject) {
			xmlHeader.virtualScreenHeight = ScreenValues.CAST_SCREEN_HEIGHT;
			xmlHeader.virtualScreenWidth = ScreenValues.CAST_SCREEN_WIDTH;
			xmlHeader.setlandscapeMode(true);
			xmlHeader.setIsCastProject(true);
		}

		Scene scene = new Scene(context.getString(R.string.default_scene_name, 1), this);
		Sprite backgroundSprite = new Sprite(context.getString(R.string.background));
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		scene.addSprite(backgroundSprite);

		sceneList.add(scene);
		xmlHeader.scenesEnabled = true;

		setDeviceData(context);
	}

	public Project(Context context, String name, boolean landscapeMode) {
		this(context, name, landscapeMode, false);
	}

	public Project(Context context, String name) {
		this(context, name, false);
	}

	public File getDirectory() {
		if (directory == null) {
			directory = new File(DEFAULT_ROOT_DIRECTORY,
					FileMetaDataExtractor.encodeSpecialCharsForFileSystem(getName()));
		}
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public List<Scene> getSceneList() {
		return sceneList;
	}

	public List<String> getSceneNames() {
		List<String> names = new ArrayList<>();
		for (Scene scene : sceneList) {
			names.add(scene.getName());
		}
		return names;
	}

	public void addScene(Scene scene) {
		sceneList.add(scene);
	}

	public void removeScene(Scene scene) {
		sceneList.remove(scene);
	}

	public Scene getDefaultScene() {
		return sceneList.get(0);
	}

	public List<UserVariable> getUserVariables() {
		if (userVariables == null) {
			userVariables = new ArrayList<>();
		}
		return userVariables;
	}

	public UserVariable getUserVariable(String name) {
		for (UserVariable variable : userVariables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public boolean addUserVariable(UserVariable userVariable) {
		return userVariables.add(userVariable);
	}

	public boolean removeUserVariable(String name) {
		for (UserVariable variable : userVariables) {
			if (variable.getName().equals(name)) {
				return userVariables.remove(variable);
			}
		}
		return false;
	}

	public List<UserList> getUserLists() {
		if (userLists == null) {
			userLists = new ArrayList<>();
		}
		return userLists;
	}

	public UserList getUserList(String name) {
		for (UserList list : userLists) {
			if (list.getName().equals(name)) {
				return list;
			}
		}
		return null;
	}

	public boolean addUserList(UserList userList) {
		return userLists.add(userList);
	}

	public boolean removeUserList(String name) {
		for (UserList list : userLists) {
			if (list.getName().equals(name)) {
				return userLists.remove(list);
			}
		}
		return false;
	}

	public List<UserVariable> getMultiplayerVariables() {
		if (multiplayerVariables == null) {
			multiplayerVariables = new ArrayList<>();
		}
		return multiplayerVariables;
	}

	public UserVariable getMultiplayerVariable(String name) {
		for (UserVariable variable : multiplayerVariables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public boolean addMultiplayerVariable(UserVariable multiplayerVariable) {
		return multiplayerVariables.add(multiplayerVariable);
	}

	public void resetUserData() {
		for (UserVariable userVariable : userVariables) {
			userVariable.reset();
		}
		for (UserList userList : userLists) {
			userList.reset();
		}
		for (UserVariable multiplayerVariable : multiplayerVariables) {
			multiplayerVariable.reset();
		}
	}

	public List<Sprite> getSpriteListWithClones() {
		if (StageActivity.stageListener != null) {
			return StageActivity.stageListener.getSpritesFromStage();
		} else {
			return getDefaultScene().getSpriteList();
		}
	}

	public void fireToAllSprites(EventWrapper event) {
		for (Sprite sprite : getSpriteListWithClones()) {
			sprite.look.fire(event);
		}
	}

	public String getName() {
		return xmlHeader.getProjectName();
	}

	public void setName(String name) {
		xmlHeader.setProjectName(name);
	}

	public String getDescription() {
		return xmlHeader.getDescription();
	}

	public void setDescription(String description) {
		xmlHeader.setDescription(description);
	}

	public String getNotesAndCredits() {
		return xmlHeader.getNotesAndCredits();
	}

	public void setNotesAndCredits(String notesAndCredits) {
		xmlHeader.setNotesAndCredits(notesAndCredits);
	}

	public ScreenModes getScreenMode() {
		return xmlHeader.getScreenMode();
	}

	public void setScreenMode(ScreenModes screenMode) {
		xmlHeader.setScreenMode(screenMode);
	}

	public double getCatrobatLanguageVersion() {
		return xmlHeader.getCatrobatLanguageVersion();
	}

	public XmlHeader getXmlHeader() {
		return this.xmlHeader;
	}

	public Rectangle getScreenRectangle() {
		int virtualScreenWidth = xmlHeader.virtualScreenWidth;
		int virtualScreenHeight = xmlHeader.virtualScreenHeight;
		return new Rectangle(-virtualScreenWidth / 2, -virtualScreenHeight / 2, virtualScreenWidth, virtualScreenHeight);
	}

	public Brick.ResourcesSet getRequiredResources() {
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();

		if (isCastProject()) {
			resourcesSet.add(Brick.CAST_REQUIRED);
		}
		ActionFactory physicsActionFactory = new ActionPhysicsFactory();
		ActionFactory actionFactory = new ActionFactory();

		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.addRequiredResources(resourcesSet);
				if (resourcesSet.contains(Brick.PHYSICS)) {
					sprite.setActionFactory(physicsActionFactory);
					resourcesSet.remove(Brick.PHYSICS);
				} else {
					sprite.setActionFactory(actionFactory);
				}
			}
		}
		return resourcesSet;
	}

	public void setCatrobatLanguageVersion(double catrobatLanguageVersion) {
		xmlHeader.setCatrobatLanguageVersion(catrobatLanguageVersion);
	}

	public void setDeviceData(Context context) {
		xmlHeader.setPlatform(Constants.PLATFORM_NAME);
		xmlHeader.setPlatformVersion(String.valueOf(Build.VERSION.SDK_INT));
		xmlHeader.setDeviceName(Build.MODEL);

		xmlHeader.setCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		xmlHeader.setApplicationBuildName(Constants.APPLICATION_BUILD_NAME);
		xmlHeader.setApplicationBuildNumber(Constants.APPLICATION_BUILD_NUMBER);

		if (context == null) {
			xmlHeader.setApplicationVersion("unknown");
			xmlHeader.setApplicationName("unknown");
		} else {
			xmlHeader.setApplicationVersion(Utils.getVersionName(context));
			xmlHeader.setApplicationName(context.getString(R.string.app_name));
		}
	}

	public void setTags(List<String> tags) {
		xmlHeader.setTags(tags);
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public Scene getSceneByName(String name) {
		for (Scene scene : sceneList) {
			if (scene.getName().equals(name)) {
				return scene;
			}
		}
		return null;
	}

	public boolean isCastProject() {
		return xmlHeader.isCastProject();
	}

	public BroadcastMessageContainer getBroadcastMessageContainer() {
		return broadcastMessageContainer;
	}

	public void setXmlHeader(XmlHeader xmlHeader) {
		this.xmlHeader = xmlHeader;
	}

	public void updateUserDataReferences(String oldName, String newName, UserData<?> item) {
		for (Scene scene : sceneList) {
			scene.updateUserDataReferences(oldName, newName, item);
		}
	}

	public void deselectElements(List<UserData<?>> elements) {
		for (Scene scene : sceneList) {
			scene.deselectElements(elements);
		}
	}

	public void setListeningLanguageTag() {
		if (xmlHeader.getListeningLanguageTag().isEmpty()
				&& !SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.isEmpty()) {
			// first item represents the default speech recognition language
			xmlHeader.setListeningLanguageTag(SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.get(0));
		}
	}

	public boolean isGlobalVariable(UserData<?> item) {
		return getUserVariables().contains(item) || getUserLists().contains(item)
				|| getMultiplayerVariables().contains(item);
	}

	public void checkForInvisibleSprites() {
		for (Scene scene : sceneList) {
			scene.checkForInvisibleSprites();
		}
	}
}
