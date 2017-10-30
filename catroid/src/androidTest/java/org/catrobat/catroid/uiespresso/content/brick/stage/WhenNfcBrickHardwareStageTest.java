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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UserVariableTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.NUM_DETECTED_TAGS;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_ID;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_MESSAGE;

@RunWith(AndroidJUnit4.class)
public class WhenNfcBrickHardwareStageTest {
	private int waitingTime = 2000;
	private int whenNfcBrickPosition;
	private UserVariable numDetectedTags;
	private UserVariable readTagId;
	private UserVariable readTagMessage;
	private String catrobatUrl = "https://www.catrobat.org";
	private String tagID = "123456";

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProjectWithNfcAndSetVariable();
		baseActivityTestRule.launchActivity(null);
	}

	private void createProjectWithNfcAndSetVariable() {
		Project project = new Project(null, "whenNfcBrickHardwareTest");
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		numDetectedTags = dataContainer.addProjectUserVariable(NUM_DETECTED_TAGS);
		readTagId = dataContainer.addProjectUserVariable(READ_TAG_ID);
		readTagMessage = dataContainer.addProjectUserVariable(READ_TAG_MESSAGE);

		Sprite sprite = new Sprite("testSprite");
		WhenNfcScript script = new WhenNfcScript();
		ChangeVariableBrick changeVariableBrickNumDetectedTags = new ChangeVariableBrick(new Formula(1), numDetectedTags);
		script.addBrick(changeVariableBrickNumDetectedTags);

		SetVariableBrick setVariableBrickId = new SetVariableBrick(Sensors.NFC_TAG_ID);
		setVariableBrickId.setUserVariable(readTagId);
		script.addBrick(setVariableBrickId);

		SetVariableBrick setVariableBrickMessage = new SetVariableBrick(Sensors.NFC_TAG_MESSAGE);
		setVariableBrickMessage.setUserVariable(readTagMessage);
		script.addBrick(setVariableBrickMessage);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		numDetectedTags.setValue(0);
		whenNfcBrickPosition = 0;
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	@Flaky
	public void testWhenNfcHardware() {
		onBrickAtPosition(whenNfcBrickPosition).checkShowsText(R.string.brick_when_nfc);
		onView(withId(R.id.button_play)).perform(click());
		SensorTestArduinoServerConnection.emulateNfcTag(true, tagID, catrobatUrl);
		Assert.assertTrue(UserVariableTestUtils.userVariableContainsWithinTimeout(readTagId, tagID, waitingTime));
		Assert.assertTrue(UserVariableTestUtils.userVariableContainsWithinTimeout(readTagMessage, catrobatUrl, waitingTime));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(numDetectedTags, 1, waitingTime));
	}
}
