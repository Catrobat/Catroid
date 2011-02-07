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
package at.tugraz.ist.catroid.test.content.project;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.content.project.Project;

public class ProjectTest extends AndroidTestCase {
	private Sprite  bottomSprite = new Sprite("bottom");
	private Sprite  topSprite    = new Sprite("top");
	private Project project      = new Project(null);
	private int		maxZ		 = 17;
	
	
	public void testAddRemoveSprite() {
		assertTrue("bottomSprite was not added to data structure",		project.addSprite(bottomSprite));
		assertTrue("topSprite was not added to data structure", 		project.addSprite(topSprite));
		assertTrue("bottomSprite was not removed from data structure", 	project.removeSprite(bottomSprite));
		assertTrue("topSprite was not removed from data structure", 	project.removeSprite(topSprite));
	}
	
	public void testGetMaxZValue() {
		bottomSprite.setZPosition(maxZ - 5);
		topSprite.setZPosition(maxZ);
		
		assertTrue("bottomSprite was not added to data structure", project.addSprite(bottomSprite));
		assertTrue("topSprite was not added to data structure",    project.addSprite(topSprite));
		assertEquals("Maximum Z value was incorrect", project.getMaxZValue(), maxZ);
	}
}
