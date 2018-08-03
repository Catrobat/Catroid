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
import android.support.annotation.VisibleForTesting;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BroadcastMessageContainer;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;

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

	private transient BroadcastMessageContainer broadcastMessageContainer = new BroadcastMessageContainer();

	public Project() {
	}

	public Project(Context context, String name, boolean landscapeMode, boolean isCastProject) {
		xmlHeader.setProgramName(name);
		xmlHeader.setDescription("");
		xmlHeader.setlandscapeMode(landscapeMode);

		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			ScreenValueHandler.updateScreenWidthAndHeight(context);
		}
		if (landscapeMode) {
			ifPortraitSwitchWidthAndHeight();
		} else {
			ifLandscapeSwitchWidthAndHeight();
		}
		xmlHeader.virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		xmlHeader.virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;

		setDeviceData(context);

		if (isCastProject) {
			setChromecastFields();
		}

		Scene scene = new Scene(context.getString(R.string.default_scene_name, 1), this);
		Sprite backgroundSprite = new Sprite(context.getString(R.string.background));
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		scene.addSprite(backgroundSprite);

		sceneList.add(scene);

		xmlHeader.scenesEnabled = true;
	}

	public Project(Context context, String name, boolean landscapeMode) {
		this(context, name, landscapeMode, false);
	}

	public Project(Context context, String name) {
		this(context, name, false);
	}

	public Project(SupportProject supportProject, Context context) {
		xmlHeader = supportProject.xmlHeader;
		settings = supportProject.settings;

		projectVariables = supportProject.dataContainer.projectVariables;
		projectLists = supportProject.dataContainer.projectLists;

		DataContainer container = new DataContainer(this);
		container.setSpriteUserData(supportProject.dataContainer);

		Scene scene = new Scene(context.getString(R.string.default_scene_name, 1), this);
		scene.setDataContainer(container);
		scene.getSpriteList().addAll(supportProject.spriteList);
		sceneList.add(scene);
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

	public void setChromecastFields() {
		xmlHeader.virtualScreenHeight = ScreenValues.CAST_SCREEN_HEIGHT;
		xmlHeader.virtualScreenWidth = ScreenValues.CAST_SCREEN_WIDTH;
		xmlHeader.setlandscapeMode(true);
		xmlHeader.setIsCastProject(true);
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

	public void setName(String name) {
		xmlHeader.setProgramName(name);
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
		if (isCastProject()) {
			resources = Brick.CAST_REQUIRED;
		}
		ActionFactory physicsActionFactory = new ActionPhysicsFactory();
		ActionFactory actionFactory = new ActionFactory();

		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				int tempResources = sprite.getRequiredResources();
				if ((tempResources & Brick.PHYSICS) != 0) {
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

	public boolean manualScreenshotExists(String manualScreenshotName) {

		String path = PathBuilder.buildProjectPath(getName()) + "/" + manualScreenshotName;
		File manualScreenShot = new File(path);
		if (manualScreenShot.exists()) {
			return false;
		}
		return true;
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

		NXTSensor.Sensor[] sensorMapping = SettingsFragment.getLegoNXTSensorMapping(context);
		for (Setting setting : settings) {
			if (setting instanceof LegoNXTSetting) {
				((LegoNXTSetting) setting).updateMapping(sensorMapping);
				return;
			}
		}

		Setting mapping = new LegoNXTSetting(sensorMapping);
		settings.add(mapping);
	}

	public void saveLegoEV3SettingsToProject(Context context) {
		if (context == null) {
			return;
		}

		if ((getRequiredResources() & Brick.BLUETOOTH_LEGO_EV3) == 0) {
			for (Object setting : settings.toArray()) {
				if (setting instanceof LegoEV3Setting) {
					settings.remove(setting);
					return;
				}
			}
			return;
		}

		EV3Sensor.Sensor[] sensorMapping = SettingsFragment.getLegoEV3SensorMapping(context);
		for (Setting setting : settings) {
			if (setting instanceof LegoEV3Setting) {
				((LegoEV3Setting) setting).updateMapping(sensorMapping);
				return;
			}
		}

		Setting mapping = new LegoEV3Setting(sensorMapping);
		settings.add(mapping);
	}

	public void loadLegoNXTSettingsFromProject(Context context) {
		if (context == null) {
			return;
		}

		for (Setting setting : settings) {
			if (setting instanceof LegoNXTSetting) {
				SettingsFragment.enableLegoMindstormsNXTBricks(context);
				SettingsFragment.setLegoMindstormsNXTSensorMapping(context, ((LegoNXTSetting) setting).getSensorMapping());
				return;
			}
		}
	}

	public void loadLegoEV3SettingsFromProject(Context context) {
		if (context == null) {
			return;
		}

		for (Setting setting : settings) {
			if (setting instanceof LegoEV3Setting) {
				SettingsFragment.enableLegoMindstormsEV3Bricks(context);
				SettingsFragment.setLegoMindstormsEV3SensorMapping(context, ((LegoEV3Setting) setting).getSensorMapping());
				return;
			}
		}
	}

	public boolean isCastProject() {
		return xmlHeader.isCastProject();
	}

	public void updateCollisionFormulasToVersion(float catroidLanguageVersion) {
		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.updateCollisionFormulasToVersion(catroidLanguageVersion);
			}
		}
	}

	public void updateSetPenColorFormulas() {
		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.updateSetPenColorFormulas();
			}
		}
	}

	public void updateArduinoValues994to995() {
		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.updateArduinoValues994to995();
			}
		}
	}

	public BroadcastMessageContainer getBroadcastMessageContainer() {
		return broadcastMessageContainer;
	}

	public void updateCollisionScripts() {
		for (Scene scene : sceneList) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.updateCollisionScripts();
			}
		}
	}

	@VisibleForTesting
	public void setXmlHeader(XmlHeader xmlHeader) {
		this.xmlHeader = xmlHeader;
	}
}
