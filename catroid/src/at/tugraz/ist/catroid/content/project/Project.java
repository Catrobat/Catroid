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

import at.tugraz.ist.catroid.content.sprite.Sprite;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

public class Project implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Set<Sprite> spriteList = new HashSet<Sprite>();
	private static Project instance;
	
	public static Project getInstance() {
		if(instance == null) {
			instance = new Project();
		}
		return instance;
	}
	
	private Project() {
	}
	
	
	public synchronized boolean addSprite(Sprite sprite) {
		return spriteList.add(sprite);
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
}
