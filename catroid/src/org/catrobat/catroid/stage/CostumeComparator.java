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
package org.catrobat.catroid.stage;

import java.util.Comparator;

import org.catrobat.catroid.content.Costume;


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
