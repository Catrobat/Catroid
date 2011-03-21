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
package at.tugraz.ist.catroid.content.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sprite> spriteList = new ArrayList<Sprite>();
	private List<Script> scriptList = new ArrayList<Script>();
	private String name;
	private String versionName;
	private int versionCode;
	
	public Project(Context context, String name) throws NameNotFoundException {
        setName(name.replaceAll(" ", ""));
		
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo("at.tugraz.ist.catroid", 0);
		versionName = packageInfo.versionName;
		versionCode =  packageInfo.versionCode;
		
		Sprite stage = new Sprite("Stage");
		addSprite(stage);
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
	
	public synchronized void addScript(Script script) {
		if (scriptList.contains(script)) {
            return;
        }
		scriptList.add(script);
	}
	
	public synchronized boolean removeScript(Script script) {
		return scriptList.remove(script);
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
}
