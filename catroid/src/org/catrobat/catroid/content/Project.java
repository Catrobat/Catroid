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

import android.content.Context;
import android.os.Build;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.DataContainer;
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
	private List<Sprite> spriteList = new ArrayList<Sprite>();
	@XStreamAlias("data")
	private DataContainer dataContainer = null;
	@XStreamAlias("settings")
	private List<Setting> settings = new ArrayList<Setting>();

	public Project(Context context, String name, boolean landscape) {
		xmlHeader.setProgramName(name);
		xmlHeader.setDescription("");

		if (landscape) {
			ifPortraitSwitchWidthAndHeight();
		} else {
			ifLandscapeSwitchWidthAndHeight();
		}
		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			Utils.updateScreenWidthAndHeight(context);
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

		for (Sprite sprite : spriteList) {
			resources |= sprite.getRequiredResources();
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
		List<String> usedMessages = new ArrayList<String>();
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

	public boolean checkIfPhiroProProject() {
		return xmlHeader.isPhiroProject();
	}

	public void setIsPhiroProProject(boolean isPhiroProject) {
		xmlHeader.setIsPhiroProject(isPhiroProject);
	}

}
