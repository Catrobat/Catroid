/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BroadcastForDeletedClonesRegressionTest {

	private static final String BROADCAST_MESSAGE_1 = "message1";
	private ScriptEvaluationGateBrick broadCastReceived;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testBroadcastsToDeletedClones() {
		baseActivityTestRule.launchActivity(null);
		broadCastReceived.waitUntilEvaluated(3000);
	}

	private void createProject() {
		Project project = UiTestUtils.createDefaultTestProject("BroadcastForDeletedClonesRegressionTest");
		Sprite sprite = UiTestUtils.getDefaultTestSprite(project);
		Script startScript = UiTestUtils.getDefaultTestScript(project);

		startScript.addBrick(new CloneBrick());
		startScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Script whenStartAsCloneScript = new WhenClonedScript();
		whenStartAsCloneScript.addBrick(new DeleteThisCloneBrick());
		sprite.addScript(whenStartAsCloneScript);

		Script broadcastReceiveScript = new BroadcastScript(BROADCAST_MESSAGE_1);
		sprite.addScript(broadcastReceiveScript);
		broadCastReceived = ScriptEvaluationGateBrick.appendToScript(broadcastReceiveScript);
	}
}
