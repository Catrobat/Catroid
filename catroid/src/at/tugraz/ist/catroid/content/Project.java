/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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
	private String versionName;
	private int versionCode;

	//only used for catroid website
	@SuppressWarnings("unused")
	private String deviceName;
	@SuppressWarnings("unused")
	private String screenResolution;

	public Project(Context context, String name) {
		this.name = name;
		deviceName = Build.MODEL;
		screenResolution = Values.SCREEN_WIDTH + "/" + Values.SCREEN_HEIGHT;

		if (context == null) {
			versionName = "unknown";
			versionCode = 0;
			return;
		}

		Sprite background = new Sprite(context.getString(R.string.background));
		addSprite(background);

		versionName = Utils.getVersionName(context);
		versionCode = Utils.getVersionCode(context);
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

	public int getMaxZValue() {
		int maxZValue = Integer.MIN_VALUE;
		for (Sprite sprite : spriteList) {
			maxZValue = Math.max(sprite.getZPosition(), maxZValue);
		}
		return maxZValue;
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

	public String getVersionName() {
		return versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setDeviceData() {
		deviceName = Build.MODEL;
		screenResolution = Values.SCREEN_WIDTH + "/" + Values.SCREEN_HEIGHT;
	}
}
