/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.Utils;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sprite> spriteList = new ArrayList<Sprite>();
	private String name;

	// Only used for Catroid website
	@SuppressWarnings("unused")
	private String deviceName;
	@SuppressWarnings("unused")
	private String versionName;
	@SuppressWarnings("unused")
	private int versionCode;

	private String screenResolution;
	public transient int VIRTUAL_SCREEN_WIDTH = 0;
	public transient int VIRTUAL_SCREEN_HEIGHT = 0;

	public Project(Context context, String name) {
		this.name = name;
		deviceName = Build.MODEL;
		screenResolution = Values.SCREEN_WIDTH + "/" + Values.SCREEN_HEIGHT;
		VIRTUAL_SCREEN_WIDTH = Values.SCREEN_WIDTH;
		VIRTUAL_SCREEN_HEIGHT = Values.SCREEN_HEIGHT;

		if (context == null) {
			return;
		}

		Sprite background = new Sprite(context.getString(R.string.background));
		background.costume.zPosition = Integer.MIN_VALUE;
		addSprite(background);

		versionName = Utils.getVersionName(context);
		versionCode = Utils.getVersionCode(context);

	}

	protected Object readResolve() {
		if (screenResolution != null) {
			String[] resolutions = screenResolution.split("/");
			VIRTUAL_SCREEN_WIDTH = Integer.valueOf(resolutions[0]);
			VIRTUAL_SCREEN_HEIGHT = Integer.valueOf(resolutions[1]);
		}
		return this;
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
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDeviceData() {
		deviceName = Build.MODEL;
		screenResolution = VIRTUAL_SCREEN_WIDTH + "/" + VIRTUAL_SCREEN_HEIGHT;
	}
}
