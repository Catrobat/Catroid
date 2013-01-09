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
import java.util.List;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.xml.parser.XMLAlias;

import android.content.Context;
import android.os.Build;
import org.catrobat.catroid.R;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Sprite> spriteList = new ArrayList<Sprite>();

	private String programName;
	private String description;
	@XMLAlias("screenWidth")
	public int virtualScreenWidth = 0;
	@XMLAlias("screenHeight")
	public int virtualScreenHeight = 0;
	private float catrobatLanguageVersion;

	// fields only used on the catrobat.org website so far
	@SuppressWarnings("unused")
	private String applicationBuildName = "";
	@SuppressWarnings("unused")
	private int applicationBuildNumber = 0;
	@SuppressWarnings("unused")
	private String applicationName = "";
	@SuppressWarnings("unused")
	private String applicationVersion = "";
	@SuppressWarnings("unused")
	private String dateTimeUpload = "";
	@SuppressWarnings("unused")
	private String deviceName = "";
	@SuppressWarnings("unused")
	private String mediaLicense = "";
	@SuppressWarnings("unused")
	private String platform = "";
	@SuppressWarnings("unused")
	private int platformVersion = 0;
	@SuppressWarnings("unused")
	private String programLicense = "";
	@SuppressWarnings("unused")
	private String remixOf = "";
	@SuppressWarnings("unused")
	private String uRL = "";
	@SuppressWarnings("unused")
	private String userHandle = "";

	public Project(Context context, String name) {
		programName = name;
		description = "";
		catrobatLanguageVersion = Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION;
		platform = Constants.PLATFORM_NAME;
		applicationBuildName = Constants.APPLICATION_BUILD_NAME;
		applicationBuildNumber = Constants.APPLICATION_BUILD_NUMBER;

		ifLandscapeSwitchWidthAndHeight();
		virtualScreenWidth = Values.SCREEN_WIDTH;
		virtualScreenHeight = Values.SCREEN_HEIGHT;
		setDeviceData(context);
		ifLandscapeSwitchWidthAndHeight();
		virtualScreenWidth = Values.SCREEN_WIDTH;
		virtualScreenHeight = Values.SCREEN_HEIGHT;
		setDeviceData(context);

		if (context == null) {
			return;
		}

		applicationName = context.getString(R.string.app_name);
		Sprite background = new Sprite(context.getString(R.string.background));
		background.costume.zPosition = Integer.MIN_VALUE;
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
		this.programName = name;
	}

	public String getName() {
		return programName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public float getCatrobatLanguageVersion() {
		return this.catrobatLanguageVersion;
	}

	// this method should be removed by the nex refactoring
	// (used only in tests)
	public void setCatrobatLanguageVersion(float catrobatLanguageVersion) {
		this.catrobatLanguageVersion = catrobatLanguageVersion;
	}

	public void setDeviceData(Context context) {
		// TODO add other header values
		deviceName = Build.MODEL;
		platformVersion = Build.VERSION.SDK_INT;

		if (context == null) {
			applicationVersion = "unknown";

		} else {
			applicationVersion = Utils.getVersionName(context);

		}
	}

	// default constructor for XMLParser
	public Project() {

	}

}
