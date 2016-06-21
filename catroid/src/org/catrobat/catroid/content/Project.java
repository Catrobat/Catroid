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
import android.os.Build;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("program")
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("header")
	private XmlHeader xmlHeader = new XmlHeader();
	@XStreamAlias("objectList")
	private List<Sprite> spriteList = new ArrayList<>();
	@XStreamAlias("data")
	private DataContainer dataContainer = null;
	@XStreamAlias("settings")
	private List<Setting> settings = new ArrayList<>();

	private transient PhysicsWorld physicsWorld;

	public Project(Context context, String name, boolean landscapeMode) {
		xmlHeader.setProgramName(name);
		xmlHeader.setDescription("");

		xmlHeader.setlandscapeMode(landscapeMode);

		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			Utils.updateScreenWidthAndHeight(context);
		}
		if (landscapeMode) {
			ifPortraitSwitchWidthAndHeight();
		} else {
			ifLandscapeSwitchWidthAndHeight();
		}
		xmlHeader.virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		xmlHeader.virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;
		setDeviceData(context);

		MessageContainer.clear();

		dataContainer = new DataContainer();

		if (context == null) {
			return;
		}
		Sprite background = new Sprite(context.getString(R.string.background));
		background.look.setZIndex(0);
		addSprite(background);
	}

	public Project(Context context, String name) {
		this(context, name, false);
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

	public synchronized void addSprite(Sprite sprite) {
		if (spriteList.contains(sprite)) {
			return;
		}
		spriteList.add(sprite);
	}

	public synchronized boolean removeSprite(Sprite sprite) {
		return spriteList.remove(sprite);
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}

	public void setName(String name) {
		xmlHeader.setProgramName(name);
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

		for (Sprite sprite : spriteList) {
			int tempResources = sprite.getRequiredResources();
			if ((tempResources & Brick.PHYSICS) > 0) {
				sprite.setActionFactory(physicsActionFactory);
				tempResources &= ~Brick.PHYSICS;
			} else {
				sprite.setActionFactory(actionFactory);
			}
			resources |= tempResources;
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

	public PhysicsWorld getPhysicsWorld() {
		if (physicsWorld == null) {
			resetPhysicsWorld();
		}
		return physicsWorld;
	}

	public PhysicsWorld resetPhysicsWorld() {
		return (physicsWorld = new PhysicsWorld(xmlHeader.virtualScreenWidth, xmlHeader.virtualScreenHeight));
	}

	// default constructor for XMLParser
	public Project() {
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void removeUnusedBroadcastMessages() {
		List<String> usedMessages = new ArrayList<>();
		for (Sprite currentSprite : spriteList) {
			for (int scriptIndex = 0; scriptIndex < currentSprite.getNumberOfScripts(); scriptIndex++) {
				Script currentScript = currentSprite.getScript(scriptIndex);
				if (currentScript instanceof BroadcastMessage) {
					addBroadcastMessage(((BroadcastMessage) currentScript).getBroadcastMessage(), usedMessages);
				}

				for (int brickIndex = 0; brickIndex < currentScript.getBrickList().size(); brickIndex++) {
					Brick currentBrick = currentScript.getBrick(brickIndex);
					if (currentBrick instanceof BroadcastMessage) {
						addBroadcastMessage(((BroadcastMessage) currentBrick).getBroadcastMessage(), usedMessages);
					}
				}
			}
			for (UserBrick userBrick : currentSprite.getUserBrickList()) {
				Script userScript = userBrick.getDefinitionBrick().getUserScript();
				for (Brick currentBrick : userScript.getBrickList()) {
					if (currentBrick instanceof BroadcastMessage) {
						addBroadcastMessage(((BroadcastMessage) currentBrick).getBroadcastMessage(), usedMessages);
					}
				}
			}
		}
		MessageContainer.removeUnusedMessages(usedMessages);
	}

	private void addBroadcastMessage(String broadcastMessageToAdd, List<String> broadcastMessages) {
		if (broadcastMessageToAdd != null && !broadcastMessageToAdd.isEmpty()
				&& !broadcastMessages.contains(broadcastMessageToAdd)) {
			broadcastMessages.add(broadcastMessageToAdd);
		}
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

		EV3Sensor.Sensor[] sensorMapping = SettingsActivity.getLegoMindstormsEV3SensorMapping(context);
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
				SettingsActivity.enableLegoMindstormsNXTBricks(context);
				SettingsActivity.setLegoMindstormsNXTSensorMapping(context, ((LegoNXTSetting) setting).getSensorMapping());
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
				SettingsActivity.enableLegoMindstormsEV3Bricks(context);
				SettingsActivity.setLegoMindstormsEV3SensorMapping(context, ((LegoEV3Setting) setting).getSensorMapping());
				return;
			}
		}
	}

	public boolean containsSpriteBySpriteName(Sprite searchedSprite) {
		for (Sprite sprite : spriteList) {
			if (searchedSprite.getName().equals(sprite.getName())) {
				return true;
			}
		}
		return false;
	}

	public UserVariable getProjectVariableWithName(String name) {
		for (UserVariable variable : dataContainer.getProjectVariables()) {
			if (name.equals(variable.getName())) {
				return variable;
			}
		}
		return null;
	}

	public UserList getProjectListWithName(String name) {
		for (UserList list : dataContainer.getProjectLists()) {
			if (name.equals(list.getName())) {
				return list;
			}
		}
		return null;
	}

	public boolean existProjectVariable(UserVariable variable) {
		return dataContainer.existProjectVariable(variable);
	}

	public boolean existSpriteVariable(UserVariable variable, Sprite sprite) {
		if (!spriteList.contains(sprite)) {
			return false;
		}
		return dataContainer.existSpriteVariable(variable, sprite);
	}

	public boolean existProjectList(UserList list) {
		return dataContainer.existProjectList(list);
	}

	public boolean existSpriteList(UserList list, Sprite sprite) {
		if (!spriteList.contains(sprite)) {
			return false;
		}
		return dataContainer.existSpriteList(list, sprite);
	}

	public Sprite getSpriteByUserVariable(UserVariable variable) {
		Sprite spriteByUserVariable = null;
		for (Sprite sprite : spriteList) {
			if (dataContainer.existSpriteVariable(variable, sprite)) {
				spriteByUserVariable = sprite;
				break;
			}
		}
		return spriteByUserVariable;
	}

	public Sprite getSpriteByUserList(UserList list) {
		Sprite spriteByUserList = null;
		for (Sprite sprite : spriteList) {
			if (dataContainer.existSpriteList(list, sprite)) {
				spriteByUserList = sprite;
				break;
			}
		}
		return spriteByUserList;
	}

	public Sprite getSpriteBySpriteName(Sprite searchedSprite) {
		Sprite spriteBySpriteName = null;
		for (Sprite sprite : spriteList) {
			if (searchedSprite.getName().equals(sprite.getName())) {
				spriteBySpriteName = sprite;
				break;
			}
		}
		return spriteBySpriteName;
	}

	public boolean isBackgroundObject(Sprite sprite) {
		if (spriteList.indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}

	public void replaceBackgroundSprite(Sprite unpackedSprite) {
		spriteList.set(0, unpackedSprite);
	}

	public boolean containsSprite(Sprite selectedSprite) {
		for (Sprite sprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			if (sprite.equals(selectedSprite)) {
				return true;
			}
		}
		return false;
	}

	public boolean islandscapeMode() {
		return xmlHeader.islandscapeMode();
	}
}
