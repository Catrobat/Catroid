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

package org.catrobat.catroid.uiespresso.content.brick;

import android.nfc.NdefMessage;

import junit.framework.Assert;

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
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UserVariableTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST1;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST2;

public class WhenNfcBrickStageTest {
	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

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
		Project project = new Project(null, "nfcStageTestProject");

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

		ChangeVariableBrick changeVariableBrickNumDetectedTags = new ChangeVariableBrick(new Formula(1),
				numDetectedTags);
		script.addBrick(changeVariableBrickNumDetectedTags);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		tagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
		tagDataList.add(firstTagData);
		tagDataList.add(secondTagData);

		Assert.assertEquals("Sprite is not set as current", sprite,
				ProjectManager.getInstance().getCurrentSprite());

		Assert.assertEquals("Sprite NFC tag list is not set", sprite.getNfcTagList(),
				ProjectManager.getInstance().getCurrentSprite().getNfcTagList());

		numDetectedTags.setValue(0.0);
		return script;
	}

	@Before
	public void setUp() throws Exception {
		ndefMessage1 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_1, BrickValues.TNF_MIME_MEDIA);
		ndefMessage2 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_2, BrickValues.TNF_MIME_MEDIA);

		firstTagData = new NfcTagData();
		firstTagData.setNfcTagName(TAG_NAME_TEST1);
		firstTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.getBytes()));

		secondTagData = new NfcTagData();
		secondTagData.setNfcTagName(TAG_NAME_TEST2);
		secondTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.getBytes()));

		scriptUnderTest = createProjectWithNfcAndSetVariable();
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testTriggerAll() throws InterpretationException {
		scriptUnderTest.setMatchAll(true);
		baseActivityTestRule.launchActivity(null);

		Assert.assertEquals("Read tag id does not match default value.", 0.0, readTagId.getValue());
		Assert.assertEquals("Read tag message does not match default value.", 0.0, readTagMessage.getValue());
		Assert.assertEquals("Tag count is not 0.", 0.0, numDetectedTags.getValue());

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.FIRST_TEST_TAG_UID), 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_1, 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(numDetectedTags, 1.0, 2000));

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.SECOND_TEST_TAG_ID, ndefMessage2, null, baseActivityTestRule.getActivity());
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.SECOND_TEST_TAG_UID), 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_2, 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(numDetectedTags, 2.0, 2000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testTriggerOne() throws InterpretationException {
		scriptUnderTest.setMatchAll(false);
		scriptUnderTest.setNfcTag(secondTagData);

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue("Read tag id does not match default value.", readTagId.getValue().equals(0.0));
		Assert.assertTrue("Read tag message does not match default value.", readTagMessage.getValue().equals(0.0));

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());
		Assert.assertTrue(UserVariableTestUtils.userVariableDoesNotEqualWithinTimeout(numDetectedTags, 1.0, 2000));

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.SECOND_TEST_TAG_ID, ndefMessage2, null, baseActivityTestRule.getActivity());
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagId,
				Double.parseDouble(UiNFCTestUtils.SECOND_TEST_TAG_UID), 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagMessage,
				UiNFCTestUtils.NFC_NDEF_STRING_2, 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(numDetectedTags, 1.0, 2000));
	}
}
