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

import android.nfc.NdefMessage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.NUM_DETECTED_TAGS;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_ID;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.READ_TAG_MESSAGE;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST1;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST2;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableNotEqualsForTimeMs;
import static org.junit.Assert.assertEquals;

public class WhenNfcBrickStageTest {
	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	private NdefMessage ndefMessage1;
	private NdefMessage ndefMessage2;

	private NfcTagData firstTagData;
	private NfcTagData secondTagData;

	private UserVariable readTagId;
	private UserVariable readTagMessage;
	private UserVariable numDetectedTags;

	private static List<NfcTagData> tagDataList;

	private WhenNfcScript scriptUnderTest;

	private WhenNfcScript createProjectWithNfcAndSetVariable() {
		Project project = UiTestUtils.createProjectWithCustomScript("nfcStageTestProject", new WhenNfcScript());
		WhenNfcScript script = (WhenNfcScript) UiTestUtils.getDefaultTestScript(project);
		Sprite sprite = UiTestUtils.getDefaultTestSprite(project);

		numDetectedTags = new UserVariable(NUM_DETECTED_TAGS);
		readTagId = new UserVariable(READ_TAG_ID);
		readTagMessage = new UserVariable(READ_TAG_MESSAGE);

		project.addUserVariable(numDetectedTags);
		project.addUserVariable(readTagId);
		project.addUserVariable(readTagMessage);

		SetVariableBrick setVariableBrickId = new SetVariableBrick(Sensors.NFC_TAG_ID);
		setVariableBrickId.setUserVariable(readTagId);
		script.addBrick(setVariableBrickId);

		SetVariableBrick setVariableBrickMessage = new SetVariableBrick(Sensors.NFC_TAG_MESSAGE);
		setVariableBrickMessage.setUserVariable(readTagMessage);
		script.addBrick(setVariableBrickMessage);

		ChangeVariableBrick changeVariableBrickNumDetectedTags = new ChangeVariableBrick(new Formula(1),
				numDetectedTags);
		script.addBrick(changeVariableBrickNumDetectedTags);

		tagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
		tagDataList.add(firstTagData);
		tagDataList.add(secondTagData);

		assertEquals("Sprite is not set as current", sprite,
				ProjectManager.getInstance().getCurrentSprite());

		assertEquals("Sprite NFC tag list is not set", sprite.getNfcTagList(),
				ProjectManager.getInstance().getCurrentSprite().getNfcTagList());

		numDetectedTags.setValue(0.0);
		return script;
	}

	@Before
	public void setUp() throws Exception {
		ndefMessage1 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_1, BrickValues.TNF_MIME_MEDIA);
		ndefMessage2 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_2, BrickValues.TNF_MIME_MEDIA);

		firstTagData = new NfcTagData();
		firstTagData.setName(TAG_NAME_TEST1);
		firstTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.getBytes()));

		secondTagData = new NfcTagData();
		secondTagData.setName(TAG_NAME_TEST2);
		secondTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.getBytes()));

		scriptUnderTest = createProjectWithNfcAndSetVariable();
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testTriggerAll() throws InterpretationException {
		scriptUnderTest.setMatchAll(true);
		baseActivityTestRule.launchActivity(null);

		assertEquals("Read tag id does not match default value.", 0.0, readTagId.getValue());
		assertEquals("Read tag message does not match default value.", 0.0, readTagMessage.getValue());
		assertEquals("Tag count is not 0.", 0.0, numDetectedTags.getValue());

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());
		assertUserVariableEqualsWithTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.FIRST_TEST_TAG_UID), 2000);
		assertUserVariableEqualsWithTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_1, 2000);
		assertUserVariableEqualsWithTimeout(numDetectedTags, 1.0, 2000);

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.SECOND_TEST_TAG_ID, ndefMessage2, null, baseActivityTestRule.getActivity());
		assertUserVariableEqualsWithTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.SECOND_TEST_TAG_UID), 2000);
		assertUserVariableEqualsWithTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_2, 2000);
		assertUserVariableEqualsWithTimeout(numDetectedTags, 2.0, 2000);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testTriggerOne() throws InterpretationException {
		scriptUnderTest.setMatchAll(false);
		scriptUnderTest.setNfcTag(secondTagData);

		baseActivityTestRule.launchActivity(null);

		assertEquals("Read tag id does not match default value.", 0.0,
				readTagId.getValue());
		assertEquals("Read tag message does not match default value.", 0.0,
				readTagMessage.getValue());

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());
		assertUserVariableNotEqualsForTimeMs(numDetectedTags, 1.0, 2000);

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.SECOND_TEST_TAG_ID, ndefMessage2, null, baseActivityTestRule.getActivity());
		assertUserVariableEqualsWithTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.SECOND_TEST_TAG_UID), 2000);
		assertUserVariableEqualsWithTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_2, 2000);
		assertUserVariableEqualsWithTimeout(numDetectedTags, 1.0, 2000);
	}
}
