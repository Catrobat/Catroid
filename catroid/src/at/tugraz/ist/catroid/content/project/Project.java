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

import at.tugraz.ist.catroid.content.sprite.Sprite;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sprite> spriteList = new ArrayList<Sprite>();
	private String projectTitle;

	public Project(String projectName) {
		setProjectTitle(projectName);
		Sprite stage = new Sprite("Stage");
		stage.setZPosition(Integer.MIN_VALUE);
		addSprite(stage);
	}
	
	public synchronized void addSprite(Sprite sprite) {
		if (spriteList.contains(sprite))
			return;
		spriteList.add(sprite);
	}
	
	public synchronized boolean removeSprite(Sprite sprite) {
		return spriteList.remove(sprite);
	}
	
	public int getMaxZValue() {
		int maxZValue = Integer.MIN_VALUE;
		for (Sprite s : spriteList) {
			maxZValue = s.getZPosition() > maxZValue ? s.getZPosition() : maxZValue;
		}
		return maxZValue;
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}
	
	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getProjectTitle() {
		return projectTitle;
	}
	
}
