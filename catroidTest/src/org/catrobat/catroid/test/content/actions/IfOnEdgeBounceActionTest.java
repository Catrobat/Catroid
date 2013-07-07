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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

public class IfOnEdgeBounceActionTest extends InstrumentationTestCase {

	private Action ifOnEdgeBounceAction;
	private Sprite sprite;
	private final float width = 100;
	private final float height = 100;

	private final int screenWidth = 480;
	private final int screenHeight = 800;

	private final float TOP_BORDER_POSITION = screenHeight / 2f;
	private final float BOTTOM_BORDER_POSITION = -TOP_BORDER_POSITION;
	private final float RIGHT_BORDER_POSITION = screenWidth / 2f;
	private final float LEFT_BORDER_POSITION = -RIGHT_BORDER_POSITION;

	private final float BOUNCE_TOP_POSITION = TOP_BORDER_POSITION - (height / 2f);
	private final float BOUNCE_BOTTOM_POSITION = -BOUNCE_TOP_POSITION;
	private final float BOUNCE_RIGHT_POSITION = RIGHT_BORDER_POSITION - (width / 2f);
	private final float BOUNCE_LEFT_POSITION = -BOUNCE_RIGHT_POSITION;

	@Override
	public void setUp() throws Exception {
		sprite = new Sprite("Test");
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);

		ifOnEdgeBounceAction = ExtendedActions.ifOnEdgeBounce(sprite);

		Project project = new Project();
		project.getXmlHeader().virtualScreenWidth = screenWidth;
		project.getXmlHeader().virtualScreenHeight = screenHeight;

		ProjectManager.getInstance().setProject(project);
	}

	public void testNoBounce() {
		ifOnEdgeBounceAction.act(1.0f);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testTopBounce() {
	}

	public void testBottomBounce() {
	}

	public void testLeftBounce() {
	}

	public void testRightBounce() {
	}
}
