/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.content.res.Resources;
import android.os.Build;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.BaseDataContainer;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@XStreamAlias("program")
// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
// CHECKSTYLE DISABLE IndentationCheck FOR 7 LINES
@XStreamFieldKeyOrder({
		"header",
		"settings",
		"scenes",
		"programVariableList",
		"programListOfLists"
})
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("header")
	private XmlHeader xmlHeader = new XmlHeader();
	@XStreamAlias("settings")
	private List<Setting> settings = new ArrayList<>();
	@XStreamAlias("programVariableList")
	private List<UserVariable> projectVariables = new ArrayList<>();
	@XStreamAlias("programListOfLists")
	private List<UserList> projectLists = new ArrayList<>();
	@XStreamAlias("scenes")
	private List<Scene> sceneList = new ArrayList<>();

	public Project(Context context, String name, boolean landscapeMode) {
		xmlHeader.setProgramName(name);
		xmlHeader.setDescription("");

		handleLandscapeMode(landscapeMode, context);
		setDeviceData(context);

		MessageContainer.clear();
		//This is used for tests
		if (context == null) {

			//Because in test project we can't find the string
			sceneList.add(new Scene(context, "Scene 1", this));
		} else {
			sceneList.add(new Scene(context, context.getString(R.string.default_scene_name, 1), this));
		}
		xmlHeader.scenesEnabled = true;
	}

	public void handleLandscapeMode(boolean landscapeMode, Context context) {
		xmlHeader.setlandscapeMode(landscapeMode);

		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			UtilUi.updateScreenWidthAndHeight(context);
		}
		if (landscapeMode) {
			ifPortraitSwitchWidthAndHeight();
		} else {
			ifLandscapeSwitchWidthAndHeight();
		}
		xmlHeader.virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		xmlHeader.virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;
	}

	public Project(Context context, String name) {
		this(context, name, false);
	}

	public Project(SupportProject oldProject, Context context) {
		xmlHeader = oldProject.xmlHeader;
		settings = oldProject.settings;
		projectVariables = oldProject.dataContainer.projectVariables;
		projectLists = oldProject.dataContainer.projectLists;
		Scene scene;

		try {
			scene = new Scene(context, context.getString(R.string.default_scene_name, 1), this);
		} catch (Resources.NotFoundException e) {
			//Because in test project we can't find the string
			scene = new Scene(context, "Scene 1", this);
		}
		DataContainer container = new DataContainer(this);
		removeInvalidVariablesAndLists(oldProject.dataContainer);
		container.setSpriteVariablesForSupportContainer(oldProject.dataContainer);
		scene.setDataContainer(container);
		scene.setSpriteList(oldProject.spriteList);
		sceneList.add(scene);
	}

	public void removeInvalidVariablesAndLists(BaseDataContainer dataContainer) {
		if (dataContainer == null) {
			return;
		}

		if (dataContainer.spriteListOfLists != null) {
			Iterator listIterator = dataContainer.spriteListOfLists.keySet().iterator();
			while (listIterator.hasNext()) {
				Sprite sprite = (Sprite) listIterator.next();
				if (sprite == null) {
					listIterator.remove();
				}
			}
		}

		if (dataContainer.spriteVariables != null) {
			Iterator variablesIterator = dataContainer.spriteVariables.keySet().iterator();
			while (variablesIterator.hasNext()) {
				Sprite sprite = (Sprite) variablesIterator.next();
				if (sprite == null) {
					variablesIterator.remove();
				}
			}
		}
	}

	public List<Scene> getSceneList() {
		return sceneList;
	}

	public List<String> getSceneOrder() {
		List<String> sceneOrder = new ArrayList<>();
		for (Scene scene : sceneList) {
			sceneOrder.add(scene.getName());
		}
		return sceneOrder;
	}

	public void setSceneList(List<Scene> scenes) {
		sceneList = scenes;
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

	public List<UserVariable> getProjectVariables() {
		if (projectVariables == null) {
			projectVariables = new ArrayList<>();
		}
		return projectVariables;
	}

	public List<UserList> getProjectLists() {
		if (projectLists == null) {
			projectLists = new ArrayList<>();
		}
		return projectLists;
	}

	public UserVariable getProjectVariableWithName(String name) {
		for (UserVariable variable : getProjectVariables()) {
			if (name.equals(variable.getName())) {
				return variable;
			}
		}
		return null;
	}

	public UserList getProjectListWithName(String name) {
		for (UserList list : getProjectLists()) {
			if (name.equals(list.getName())) {
				return list;
			}
		}
		return null;
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

	private void ifPortraitSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH < ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

	public boolean isScenesEnabled() {
		return sceneList.size() > 1;
	}

	public void setName(String name) {
		xmlHeader.setProgramName(name);
	}

	public List<Sprite> getSpriteListWithClones() {
		if (StageActivity.stageListener != null) {
			return StageActivity.stageListener.getSpritesFromStage();
		} else { // e.g. for ActionTests there is no Stage, only use sprites from Project
			return getDefaultScene().getSpriteList();
		}
	}

	public String getName() {
		return xmlHeader.getProgramName();
	}

	public void setDescription(String description) {
		xmlHeader.setDescription(description);
	}

	public String getDescription() {
		return xmlHeader.getDescription();
	}

	public void setScreenMode(ScreenModes screenMode) {
		xmlHeader.setScreenMode(screenMode);
	}

	public ScreenModes getScreenMode() {
		return xmlHeader.getScreenMode();
	}

	public float getCatrobatLanguageVersion() {
		return xmlHeader.getCatrobatLanguageVersion();
	}

	public XmlHeader getXmlHeader() {
		return this.xmlHeader;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;
		ActionFactory physicsActionFactory = new ActionPhysicsFactory();
		ActionFactory actionFactory = new ActionFactory();

		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				int tempResources = sprite.getRequiredResources();
				if ((tempResources & Brick.PHYSICS) > 0) {
					sprite.setActionFactory(physicsActionFactory);
					tempResources &= ~Brick.PHYSICS;
				} else {
					sprite.setActionFactory(actionFactory);
				}
				resources |= tempResources;
			}
		}
		return resources;
	}

	// this method should be removed by the nex refactoring
	// (used only in tests)
	public void setCatrobatLanguageVersion(float catrobatLanguageVersion) {
		xmlHeader.setCatrobatLanguageVersion(catrobatLanguageVersion);
	}

	public void setDeviceData(Context context) {
		// TODO add other header values
		xmlHeader.setPlatform(Constants.PLATFORM_NAME);
		xmlHeader.setPlatformVersion((double) Build.VERSION.SDK_INT);
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

	// default constructor for XMLParser
	public Project() {
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

	public boolean containsScene(Scene scene) {
		return getSceneOrder().contains(scene.getName());
	}

	public boolean manualScreenshotExists(String manualScreenshotName) {

		String path = Utils.buildProjectPath(getName()) + "/" + manualScreenshotName;
		File manualScreenShot = new File(path);
		if (manualScreenShot.exists()) {
			return false;
		}
		return true;
	}

	public void setXmlHeader(XmlHeader xmlHeader) {
		this.xmlHeader = xmlHeader;
	}

	public void saveLegoNXTSettingsToProject(Context context) {
		if (context == null) {
			return;
		}

		if ((getRequiredResources() & Brick.BLUETOOTH_LEGO_NXT) == 0) {
			for (Object setting : settings.toArray()) {
				if (setting instanceof LegoNXTSetting) {
					settings.remove(setting);
					return;
				}
			}
			return;
		}

		NXTSensor.Sensor[] sensorMapping = SettingsActivity.getLegoMindstormsNXTSensorMapping(context);
		for (Setting setting : settings) {
			if (setting instanceof LegoNXTSetting) {
				((LegoNXTSetting) setting).updateMapping(sensorMapping);
				return;
			}
		}

		Setting mapping = new LegoNXTSetting(sensorMapping);
		settings.add(mapping);
	}

	public void loadLegoNXTSettingsFromProject(Context context) {

		if (context == null) {
			return;
		}

		for (Setting setting : settings) {
			if (setting instanceof LegoNXTSetting) {
				SettingsActivity.enableLegoMindstormsNXTBricks(context);
				SettingsActivity.setLegoMindstormsNXTSensorMapping(context, ((LegoNXTSetting) setting).getSensorMapping());
				return;
			}
		}
	}

	public void refreshSpriteReferences() {
		for (Scene scene : sceneList) {
			scene.refreshSpriteReferences();
		}
	}

	public void updateCollisionFormulasToNewVersion() {
		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.updateCollisionFormulasToNewVersion();
			}
		}
	}
}
