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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.NUM_DETECTED_TAGS;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_ID;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_MESSAGE;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableContainsStringWithTimeout;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		createProjectWithNfcAndSetVariable();
		baseActivityTestRule.launchActivity();
	}

	private void createProjectWithNfcAndSetVariable() {
		Project project = UiTestUtils.createProjectWithCustomScript("whenNfcBrickHardwareTest",
				new WhenNfcScript());
		WhenNfcScript script = (WhenNfcScript) UiTestUtils.getDefaultTestScript(project);

		numDetectedTags = new UserVariable(NUM_DETECTED_TAGS);
		readTagId = new UserVariable(READ_TAG_ID);
		readTagMessage = new UserVariable(READ_TAG_MESSAGE);

		project.addUserVariable(numDetectedTags);
		project.addUserVariable(readTagId);
		project.addUserVariable(readTagMessage);

		ChangeVariableBrick changeVariableBrickNumDetectedTags = new ChangeVariableBrick(new Formula(1), numDetectedTags);
		script.addBrick(changeVariableBrickNumDetectedTags);

		SetVariableBrick setVariableBrickId = new SetVariableBrick(Sensors.NFC_TAG_ID);
		setVariableBrickId.setUserVariable(readTagId);
		script.addBrick(setVariableBrickId);

		SetVariableBrick setVariableBrickMessage = new SetVariableBrick(Sensors.NFC_TAG_MESSAGE);
		setVariableBrickMessage.setUserVariable(readTagMessage);
		script.addBrick(setVariableBrickMessage);

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
		assertUserVariableContainsStringWithTimeout(readTagId, tagID, waitingTime);
		assertUserVariableContainsStringWithTimeout(readTagMessage, catrobatUrl, waitingTime);
		assertUserVariableEqualsWithTimeout(numDetectedTags, 1, waitingTime);
	}
}
