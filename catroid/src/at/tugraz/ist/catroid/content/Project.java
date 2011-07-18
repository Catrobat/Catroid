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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sprite> spriteList = Collections.synchronizedList(new LinkedList());
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

		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo("at.tugraz.ist.catroid", 0);
			versionName = packageInfo.versionName;
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versionName = "unknown";
			versionCode = 0;
		}

	}

	public synchronized void addSprite(Sprite sprite) {
		if (spriteList.contains(sprite)) {
			sprite.setZPosition(spriteList.size() - 1);
			return;
		}
		spriteList.add(sprite);
		sprite.setZPosition(spriteList.size() - 1);
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

	public synchronized void moveSpriteLayer(Sprite sprite, int depthdistance) {
		Log.v("MSL", "start");
		int spriteZPos = sprite.getZPosition();
		int maxZPos = spriteList.size();
		int currentSpriteZPos = spriteList.indexOf(sprite);
		int newPosition = 0;
		if (spriteZPos != currentSpriteZPos) {
			Log.v("NewLayer", "List is inconsistent");
			//return;
		}
		newPosition = currentSpriteZPos - depthdistance - 1;
		if (newPosition <= 0) {
			newPosition = 1;
		}
		spriteList.remove(currentSpriteZPos);
		spriteList.add(newPosition, sprite);
		sprite.setZPosition(newPosition);
		Log.v("MSL", "end");
	}

	public synchronized void moveSpriteLayerToTop(Sprite sprite) {
		int spriteZPos = sprite.getZPosition();
		int maxZPos = spriteList.size();
		int currentSpriteZPos = spriteList.indexOf(sprite);
		if (spriteZPos != currentSpriteZPos) {
			Log.v("NewLayer", "List is inconsistent");
			//return;
		}

		spriteList.remove(currentSpriteZPos);
		spriteList.add(sprite);
		sprite.setZPosition(spriteList.size() - 1);
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

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setDeviceData() {
		deviceName = Build.MODEL;
		screenResolution = Values.SCREEN_WIDTH + "/" + Values.SCREEN_HEIGHT;
	}
}
