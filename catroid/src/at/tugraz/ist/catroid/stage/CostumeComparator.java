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
package at.tugraz.ist.catroid.stage;

import java.util.Comparator;

import at.tugraz.ist.catroid.content.Costume;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author Johannes Iber
 * 
 */
public class CostumeComparator implements Comparator<Actor> {

	public int compare(Actor object1, Actor object2) {
		Costume costume1 = (Costume) object1;
		Costume costume2 = (Costume) object2;
		if (costume1.zPosition < costume2.zPosition) {
			return -1;
		} else if (costume1.zPosition == costume2.zPosition) {
			return 0;
		} else {
			return 1;
		}
	}

}
