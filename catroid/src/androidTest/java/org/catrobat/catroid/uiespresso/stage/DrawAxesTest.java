/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.stage;

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.util.UiTestUtils.createEmptyProject;

public class DrawAxesTest {

	private Project project;
	private int landscapeHeight;
	private static final int FONTCOLOR = 0xff000cff;
	private static final float DELTA = 0.1f;
	private static final float FONTPERCENTAGE = 0.025f;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = createEmptyProject("DrawAxesTest");
		baseActivityTestRule.launchActivity(null);
		int screenHeight = project.getXmlHeader().getVirtualScreenHeight();
		int screenWidth = project.getXmlHeader().getVirtualScreenWidth();
		landscapeHeight = (screenHeight > screenWidth) ? screenWidth : screenHeight;
	}

	@Test
	public void testDrawAxes() {
		assertFalse(StageActivity.stageListener.axesOn);

		pressBack();
		onView(withId(R.id.stage_dialog_button_toggle_axes))
				.perform(click());

		assertTrue(StageActivity.stageListener.axesOn);

		Color actualFontColor = StageActivity.stageListener.getAxesFont().getColor();
		Color expectedFontColor = new Color(FONTCOLOR);

		assertEquals(expectedFontColor, actualFontColor);

		onView(isRoot()).perform(CustomActions.wait(5000));

		float actualFontHeight = StageActivity.stageListener.getAxesFont().getCapHeight();
		float expectedFontHeight = FONTPERCENTAGE * landscapeHeight;

		assertEquals(expectedFontHeight, actualFontHeight, DELTA);
	}
}
