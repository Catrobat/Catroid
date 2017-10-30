/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.annotation.StringRes;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickCoordinatesProvider;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class WhenBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Parameters(name = "{2}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.string.brick_when_started, WhenStartedBrick.class, "WhenStartedBrick"},
				{R.string.brick_when_becomes_true, WhenConditionBrick.class, "WhenConditionBrick"},
				{R.string.brick_when_cloned, WhenClonedBrick.class, "WhenClonedBrick"},
				{R.string.brick_when_background, WhenBackgroundChangesBrick.class, "WhenBackgroundChangesBrick"},
				{R.string.brick_when_touched, WhenTouchDownBrick.class, "WhenTouchDownBrick"},
				{R.string.brick_broadcast_receive, BroadcastReceiverBrick.class, "BroadcastReceiverBrick"},
				{R.string.brick_collision_receive, CollisionReceiverBrick.class, "CollisionReceiverBrick"}
		});
	}

	@Parameter
	public @StringRes int whenBrickNameId;

	@Parameter(1)
	public Class<?> whenBrickClass;

	@Parameter(2)
	public String testName;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("WhenBrickTest");

		if (whenBrickClass.equals(CollisionReceiverBrick.class)) {
			script.addBrick(new CollisionReceiverBrick("TEST"));
		} else if (whenBrickClass.equals(BroadcastReceiverBrick.class)) {
			script.addBrick(new BroadcastReceiverBrick("TEST"));
		} else {
			script.addBrick((Brick) whenBrickClass.newInstance());
		}

		script.addBrick(new PlaceAtBrick());

		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testWhenBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(whenBrickNameId);
		onBrickAtPosition(2).checkShowsText(R.string.brick_place_at);

		onBrickAtPosition(1).performDragNDrop(BrickCoordinatesProvider.UP_TO_TOP);
		onBrickAtPosition(1).checkShowsText(whenBrickNameId);

		onBrickAtPosition(2).performDragNDrop(BrickCoordinatesProvider.UP_TO_TOP);
		onBrickAtPosition(1).checkShowsText(R.string.brick_place_at);
	}
}
