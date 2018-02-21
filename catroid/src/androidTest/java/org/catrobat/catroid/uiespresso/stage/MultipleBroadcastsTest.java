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

package org.catrobat.catroid.uiespresso.stage;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.StageTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.catrobat.catroid.uiespresso.util.UserVariableTestUtils.userVariableEqualsWithinTimeout;

@RunWith(AndroidJUnit4.class)
public class MultipleBroadcastsTest {

	private static final String BROADCAST_MESSAGE_1 = "message1";
	private static final int[] VALUES = new int[] {30, 60, -30, -60};
	private Project project;
	private DataContainer dataContainer;
	private ArrayList<UserVariable> userVariables;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createEmptyProject("test");
		dataContainer = project.getDefaultScene().getDataContainer();
		userVariables = new ArrayList<>();
		for (int i = 0; i < VALUES.length; i++) {
			Sprite sprite = new SingleSprite("sprite" + i);
			project.getDefaultScene().addSprite(sprite);
			UserVariable userVariable = dataContainer.addProjectUserVariable("var" + i);
			Script sendBroadcastScript = new StartScript();
			sprite.addScript(sendBroadcastScript);
			sendBroadcastScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
			StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite, BROADCAST_MESSAGE_1, userVariable, VALUES[i]);
			userVariables.add(userVariable);
		}
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testSendMultipleBroadcasts() throws InterruptedException {
		baseActivityTestRule.launchActivity(null);

		for (int i = 0; i < userVariables.size(); i++) {
			Assert.assertTrue(userVariableEqualsWithinTimeout(userVariables.get(i), VALUES[i], 2000));
		}
	}
}
