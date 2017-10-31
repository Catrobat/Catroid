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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.util.UserVariableTestUtils.userVariableEqualsWithinTimeout;

@RunWith(AndroidJUnit4.class)
public class BroadcastForClonesRegressionTest {

	private static final String BROADCAST_MESSAGE_1 = "message1";
	private static final String VARIABLE_NAME = "var1";

	private UserVariable userVariable;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BroadcastHandler.clearActionMaps();
		createProject();
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testIfBroadcastsAreReceivedByClones() {
		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, 2, 1000));
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testIfClonesBroadcastReceiversAreRemovedOnRestart() {
		baseActivityTestRule.launchActivity(null);

		pressBack();
		onView(withId(R.id.stage_dialog_button_restart))
				.perform(click());

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, 2, 1000));
	}

	private void createProject() {
		Project project = new Project(null, "BroadcastForClonesRegressionTest");
		ProjectManager.getInstance().setProject(project);
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = dataContainer.addProjectUserVariable(VARIABLE_NAME);

		Sprite sprite = new Sprite("testSprite");

		Script startScript = new StartScript();
		startScript.addBrick(new CloneBrick());
		startScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		sprite.addScript(startScript);

		Script broadcastReceiveScript = new BroadcastScript(BROADCAST_MESSAGE_1);
		broadcastReceiveScript.addBrick(new ChangeVariableBrick(new Formula(1), userVariable));
		sprite.addScript(broadcastReceiveScript);

		ProjectManager.getInstance().addSprite(sprite);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
