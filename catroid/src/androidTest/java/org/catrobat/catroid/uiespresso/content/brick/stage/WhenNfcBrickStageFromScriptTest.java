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

import android.nfc.NdefMessage;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UserVariableTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST1;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST2;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.onNfcBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class WhenNfcBrickStageFromScriptTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private NdefMessage ndefMessage1;
	private UserVariable readTagId;
	private UserVariable readTagMessage;
	private UserVariable numDetectedTags;
	private List<NfcTagData> tagDataList;
	private Script scriptUnderTest;
	private int nfcBrickPosition;

	@Before
	public void setUp() throws Exception {
		ndefMessage1 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_1, BrickValues.TNF_MIME_MEDIA);
		createProjectWithNfcAndSetVariable();
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testNfcSensorVariable() throws InterpretationException {
		gotoNfcFragment(nfcBrickPosition);
		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());

		onView(withId(R.id.button_play))
				.perform(click());

		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagId, 0, 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagMessage, "0.0", 2000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testSelectTagAndPlay() {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(R.string.brick_when_nfc_default_all);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.performSelect(TAG_NAME_TEST1);

		onView(withId(R.id.button_play))
				.perform(click());

		assertEquals("Tag not set", ((WhenNfcBrick) scriptUnderTest.getScriptBrick()).getNfcTag().getNfcTagName(),
				tagDataList.get(0).getNfcTagName());
		pressBack();
		pressBack();

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(TAG_NAME_TEST1);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.performSelect(TAG_NAME_TEST2);

		onView(withId(R.id.button_play))
				.perform(click());

		assertEquals("tag not set", ((WhenNfcBrick) scriptUnderTest.getScriptBrick()).getNfcTag().getNfcTagName(),
				tagDataList.get(1).getNfcTagName());
		pressBack();
		pressBack();

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(TAG_NAME_TEST2);
	}

	private void gotoNfcFragment(int nfcBrickPosition) {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());
		onView(withText(R.string.new_nfc_tag))
				.perform(click());
	}

	private Script createProjectWithNfcAndSetVariable() {
		Project project = new Project(null, "nfcTestProject");

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();

		readTagId = dataContainer.addProjectUserVariable(UiNFCTestUtils.READ_TAG_ID);
		readTagMessage = dataContainer.addProjectUserVariable(UiNFCTestUtils.READ_TAG_MESSAGE);
		numDetectedTags = dataContainer.addProjectUserVariable(UiNFCTestUtils.NUM_DETECTED_TAGS);

		Sprite sprite = new Sprite("testSprite");
		WhenNfcScript script = new WhenNfcScript();

		SetVariableBrick setVariableBrickId = new SetVariableBrick(Sensors.NFC_TAG_ID);
		setVariableBrickId.setUserVariable(readTagId);
		script.addBrick(setVariableBrickId);

		SetVariableBrick setVariableBrickMessage = new SetVariableBrick(Sensors.NFC_TAG_MESSAGE);
		setVariableBrickMessage.setUserVariable(readTagMessage);
		script.addBrick(setVariableBrickMessage);

		ChangeVariableBrick changeVariableBrickNumDetectedTags = new ChangeVariableBrick(new Formula(1), numDetectedTags);
		script.addBrick(changeVariableBrickNumDetectedTags);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		tagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
		NfcTagData firstTagData = new NfcTagData();

		firstTagData.setNfcTagName(TAG_NAME_TEST1);
		firstTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.getBytes()));
		tagDataList.add(firstTagData);

		NfcTagData secondTagData = new NfcTagData();
		secondTagData.setNfcTagName(TAG_NAME_TEST2);
		secondTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.getBytes()));
		tagDataList.add(secondTagData);

		numDetectedTags.setValue(0);
		nfcBrickPosition = 0;
		scriptUnderTest = script;
		return script;
	}
}
