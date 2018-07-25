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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AdMobHideBannerBrick;
import org.catrobat.catroid.content.bricks.AdMobShowBannerBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class AdMobShowBannerBrickAndAdMobHideBannerBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject("AdMobBannerBrickTest");
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testBothBricks() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_show_admob_banner);

		onData(anything()).inAdapterView(withId(R.id.brick_admob_show_banner_position))
				.atPosition(0).check(matches(withText(R.string.brick_admob_position_top)));
		onData(anything()).inAdapterView(withId(R.id.brick_admob_show_banner_position))
				.atPosition(1).check(matches(withText(R.string.brick_admob_position_bottom)));

		onBrickAtPosition(1).checkShowsText(R.string.brick_admob_show_banner_size);

		onData(anything()).inAdapterView(withId(R.id.brick_admob_show_banner_size))
				.atPosition(0).check(matches(withText(R.string.brick_admob_size_banner)));
		onData(anything()).inAdapterView(withId(R.id.brick_admob_show_banner_size))
				.atPosition(1).check(matches(withText(R.string.brick_admob_size_smart_banner)));
		onData(anything()).inAdapterView(withId(R.id.brick_admob_show_banner_size))
				.atPosition(2).check(matches(withText(R.string.brick_admob_size_large_banner)));

		onBrickAtPosition(2).checkShowsText(R.string.brick_hide_admob_banner);
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite1 = new Sprite("testAdMobBanner");
		Script sprite1StartScript = new StartScript();
		sprite1.addScript(sprite1StartScript);

		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);

		sprite1StartScript.addBrick(new AdMobShowBannerBrick(AdMobShowBannerBrick.AdMobBannerPositionEnum.TOP, AdMobShowBannerBrick.AdMobBannerSizeEnum.LARGE_BANNER));
		sprite1StartScript.addBrick(new AdMobHideBannerBrick());
	}
}
