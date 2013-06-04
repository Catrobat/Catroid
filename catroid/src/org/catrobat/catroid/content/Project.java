/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.os.Build;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("program")
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("header")
	private XmlHeader xmlHeader = new XmlHeader();
	@XStreamAlias("objectList")
	private List<Sprite> spriteList = new ArrayList<Sprite>();
	@XStreamAlias("variables")
	private UserVariablesContainer userVariables = null;

	public Project(Context context, String name) {
		xmlHeader.setProgramName(name);
		xmlHeader.setDescription("");
		xmlHeader.setCatrobatLanguageVersion(Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION);
		xmlHeader.setPlatform(Constants.PLATFORM_NAME);
		xmlHeader.setApplicationBuildName(Constants.APPLICATION_BUILD_NAME);
		xmlHeader.setApplicationBuildNumber(Constants.APPLICATION_BUILD_NUMBER);

		ifLandscapeSwitchWidthAndHeight();
		xmlHeader.virtualScreenWidth = Values.SCREEN_WIDTH;
		xmlHeader.virtualScreenHeight = Values.SCREEN_HEIGHT;
		setDeviceData(context);

		MessageContainer.clear();

		userVariables = new UserVariablesContainer();

		if (context == null) {
			return;
		}

		xmlHeader.setApplicationName(context.getString(R.string.app_name));
		Sprite background = new Sprite(context.getString(R.string.background));
		background.look.setZIndex(0);
		addSprite(background);
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (Values.SCREEN_WIDTH > Values.SCREEN_HEIGHT) {
			int tmp = Values.SCREEN_HEIGHT;
			Values.SCREEN_HEIGHT = Values.SCREEN_WIDTH;
			Values.SCREEN_WIDTH = tmp;
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

	public float getCatrobatLanguageVersion() {
		return xmlHeader.getCatrobatLanguageVersion();
	}

	public XmlHeader getXmlHeader() {
		return this.xmlHeader;
	}

	// this method should be removed by the nex refactoring
	// (used only in tests)
	public void setCatrobatLanguageVersion(float catrobatLanguageVersion) {
		xmlHeader.setCatrobatLanguageVersion(catrobatLanguageVersion);
	}

	public void setDeviceData(Context context) {
		// TODO add other header values
		xmlHeader.setDeviceName(Build.MODEL);
		xmlHeader.setPlatformVersion(Build.VERSION.SDK_INT);

		if (context == null) {
			xmlHeader.setApplicationVersion("unknown");

		} else {
			xmlHeader.setApplicationVersion(Utils.getVersionName(context));

		}
	}

	// default constructor for XMLParser
	public Project() {
	}

	public UserVariablesContainer getUserVariables() {
		return userVariables;
	}

	public void removeUnusedBroadcastMessages() {
		List<String> usedMessages = new LinkedList<String>();
		List<Sprite> spriteList = getSpriteList();
		if (spriteList != null) {
			for (Sprite currentSprite : spriteList) {
				for (int scriptIndex = 0; scriptIndex < currentSprite.getNumberOfScripts(); ++scriptIndex) {
					Script currentScript = currentSprite.getScript(scriptIndex);
					if (currentScript.getClass().getSimpleName().equals(BroadcastScript.class.getSimpleName())) {
						addMessageToList(
								((BroadcastReceiverBrick) currentScript.getScriptBrick()).getSelectedMessage(),
								usedMessages);
					}
					List<Brick> brickList = currentScript.getBrickList();
					if (brickList != null) {
						for (Brick currentBrick : brickList) {
							if (currentBrick.getClass().getSimpleName().equals(BroadcastBrick.class.getSimpleName())) {
								addMessageToList(((BroadcastBrick) currentBrick).getSelectedMessage(), usedMessages);
							} else if (currentBrick.getClass().getSimpleName()
									.equals(BroadcastWaitBrick.class.getSimpleName())) {
								addMessageToList(((BroadcastWaitBrick) currentBrick).getSelectedMessage(), usedMessages);
							}
						}
					}
				}
			}
		}
		MessageContainer.removeOtherMessages(usedMessages);
	}

	private void addMessageToList(String message, List<String> list) {
		if (message != null && !message.equals("") && !list.contains(message)) {
			list.add(message);
		}
	}
}
