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
package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;

import android.test.AndroidTestCase;

public class PointToActionTest extends AndroidTestCase {

	public void testPointTo() {

		Project project = new Project(null, "testProject");

		Sprite sprite1 = new Sprite("cat1");
		Script startScript1 = new StartScript(sprite1);
		PlaceAtBrick placeAt1 = new PlaceAtBrick(sprite1, 300, 400);
		SetSizeToBrick size1 = new SetSizeToBrick(sprite1, 20.0);
		startScript1.addBrick(placeAt1);
		startScript1.addBrick(size1);

		Sprite sprite2 = new Sprite("cat2");
		Script startScript2 = new StartScript(sprite2);
		PlaceAtBrick placeAt2 = new PlaceAtBrick(sprite2, -400, -300);
		SetSizeToBrick size2 = new SetSizeToBrick(sprite2, 20.0);
		startScript2.addBrick(placeAt2);
		startScript2.addBrick(size2);
		sprite2.addScript(startScript2);

		PointToBrick pointToBrick = new PointToBrick(sprite1, sprite2, null);
		startScript1.addBrick(pointToBrick);
		sprite1.addScript(startScript1);

		project.addSprite(sprite1);
		project.addSprite(sprite2);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(startScript1);

		sprite2.createStartScriptActionSequence();
		sprite1.createStartScriptActionSequence();

		while (!sprite2.look.getAllActionsAreFinished()) {
			sprite2.look.act(1.0f);
		}

		while (!sprite1.look.getAllActionsAreFinished()) {
			sprite1.look.act(1.0f);
		}

		assertEquals("Wrong direction", -135.0, sprite1.look.getRotation(), 1e-3);
	}

}
