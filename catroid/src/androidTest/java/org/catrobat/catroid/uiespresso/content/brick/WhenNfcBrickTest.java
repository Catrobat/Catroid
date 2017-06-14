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
import android.support.test.InstrumentationRegistry;
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
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiNFCTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.UserVariableTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.NFCTagDataNameMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.NFCTagListMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.util.UiNFCTestUtils.checkNfcSpinnerContains;
import static org.catrobat.catroid.uiespresso.util.UiNFCTestUtils.clickSpinnerValueOnBrick;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class WhenNfcBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private final NdefMessage ndefMessage1;
	private UserVariable readTagId;
	private UserVariable readTagMessage;
	private UserVariable numDetectedTags;
	private List<NfcTagData> tagDataList;
	private Script scriptUnderTest;
	private int nfcBrickPosition;
	private int setVariableIDPosition;
	private int setVariableMessagePosition;

	public WhenNfcBrickTest() throws InterpretationException {
		ndefMessage1 = NfcHandler.createMessage(UiNFCTestUtils.NFC_NDEF_STRING_1, BrickValues.TNF_MIME_MEDIA);
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

		firstTagData.setNfcTagName(UiTestUtils.getResourcesString(R.string.test_tag_name_1));
		firstTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.getBytes()));
		tagDataList.add(firstTagData);

		NfcTagData secondTagData = new NfcTagData();
		secondTagData.setNfcTagName(UiTestUtils.getResourcesString(R.string.test_tag_name_2));
		secondTagData.setNfcTagUid(NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.getBytes()));
		tagDataList.add(secondTagData);

		numDetectedTags.setValue(0);
		nfcBrickPosition = 0;
		setVariableIDPosition = 1;
		setVariableMessagePosition = 2;
		scriptUnderTest = script;
		return script;
	}

	@Before
	public void setUp() throws Exception {
		createProjectWithNfcAndSetVariable();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testBasicLayout() {
		onBrickAtPosition(nfcBrickPosition).checkShowsText(R.string.brick_when_nfc);

		onBrickAtPosition(setVariableIDPosition).onSpinner(R.id.set_variable_spinner)
				.checkShowsText(UiNFCTestUtils.READ_TAG_ID);

		onBrickAtPosition(setVariableMessagePosition).onSpinner(R.id.set_variable_spinner)
				.checkShowsText(UiNFCTestUtils.READ_TAG_MESSAGE);
	}

	@Test
	public void testAddNewTag() {
		List<String> spinnerValuesResourceIdsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_1,
				R.string.test_tag_name_2);
		List<String> spinnerValuesResourceIdsNotContained = Arrays.asList(
				UiNFCTestUtils.THIRD_TEST_TAG_ID,
				UiNFCTestUtils.FOURTH_TEST_TAG_ID);
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesResourceIdsContained, spinnerValuesResourceIdsNotContained);

		UiNFCTestUtils.gotoNfcFragment(nfcBrickPosition);
		NfcTagAdapter adapter = UiNFCTestUtils.getNfcTagAdapter(baseActivityTestRule.getActivity());
		adapter.setShowDetails(true);

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.THIRD_TEST_TAG_ID, null, null, baseActivityTestRule.getActivity());
		onView(withText(R.string.default_tag_name))
				.check(matches(isDisplayed()));
		onView(withText(NfcHandler.byteArrayToHex(UiNFCTestUtils.THIRD_TEST_TAG_ID.getBytes())))
				.check(matches(isDisplayed()));
		int numberOfTagsInList = adapter.getCount();
		assertEquals(3, numberOfTagsInList);
		pressBack();

		spinnerValuesResourceIdsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_1,
				R.string.test_tag_name_2,
				R.string.default_tag_name);
		spinnerValuesResourceIdsNotContained = Arrays.asList(UiNFCTestUtils.FOURTH_TEST_TAG_ID);
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesResourceIdsContained, spinnerValuesResourceIdsNotContained);

		UiNFCTestUtils.gotoNfcFragment(nfcBrickPosition);
		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FOURTH_TEST_TAG_ID, null, null, baseActivityTestRule.getActivity());
		onView(withText(NfcHandler.byteArrayToHex(UiNFCTestUtils.FOURTH_TEST_TAG_ID.getBytes())))
				.check(matches(isDisplayed()));
		numberOfTagsInList = adapter.getCount();
		assertEquals(4, numberOfTagsInList);

		pressBack();

		spinnerValuesResourceIdsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_1,
				R.string.test_tag_name_2,
				R.string.default_tag_name);
		spinnerValuesResourceIdsContained.add(UiTestUtils.getResourcesString(R.string.default_tag_name) + "_1");
		spinnerValuesResourceIdsNotContained = Arrays.asList();
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesResourceIdsContained, spinnerValuesResourceIdsNotContained);

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());
		onView(withText(R.string.default_tag_name))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testNfcSensorVariable() throws InterpretationException {
		UiNFCTestUtils.gotoNfcFragment(nfcBrickPosition);
		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FIRST_TEST_TAG_ID, ndefMessage1, null, baseActivityTestRule.getActivity());

		onView(withId(R.id.button_play))
				.perform(click());

		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagId, 0, 2000));
		Assert.assertTrue(UserVariableTestUtils.userVariableEqualsWithinTimeout(readTagMessage, "0.0", 2000));
	}

	@Test
	public void testSelectTagAndPlay() {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(R.string.brick_when_nfc_default_all);

		clickSpinnerValueOnBrick(R.id.brick_when_nfc_spinner, nfcBrickPosition, R.string.test_tag_name_1);

		onView(withId(R.id.button_play))
				.perform(click());

		assertEquals("Tag not set", ((WhenNfcBrick) scriptUnderTest.getScriptBrick()).getNfcTag().getNfcTagName(),
				tagDataList.get(0).getNfcTagName());
		pressBack();
		pressBack();

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(R.string.test_tag_name_1);

		clickSpinnerValueOnBrick(R.id.brick_when_nfc_spinner, nfcBrickPosition, R.string.test_tag_name_2);

		onView(withId(R.id.button_play))
				.perform(click());

		assertEquals("tag not set", ((WhenNfcBrick) scriptUnderTest.getScriptBrick()).getNfcTag().getNfcTagName(),
				tagDataList.get(1).getNfcTagName());
		pressBack();
		pressBack();

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkShowsText(R.string.test_tag_name_2);
	}

	@Test
	public void testSpinnerUpdatesDelete() {
		List<String> spinnerValuesResourceIdsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_1,
				R.string.test_tag_name_2);
		List<String> spinnerValuesResourceIdsNotContained = Arrays.asList();
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesResourceIdsContained, spinnerValuesResourceIdsNotContained);
		UiNFCTestUtils.gotoNfcFragment(nfcBrickPosition);
		contextMenuActionDelete(UiTestUtils.getResourcesString(R.string.test_tag_name_1));
		pressBack();
		spinnerValuesResourceIdsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_2);
		spinnerValuesResourceIdsNotContained = getStringsFromResourceIds(R.string.test_tag_name_1);
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesResourceIdsContained,
				spinnerValuesResourceIdsNotContained);
	}

	@Test
	public void testSpinnerUpdatesRename() {
		String renamedTag = "tag_renamed";
		List<String> spinnerValuesContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_1,
				R.string.test_tag_name_2);
		List<String> spinnerValuesNotContained = Arrays.asList();
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesContained, spinnerValuesNotContained);
		UiNFCTestUtils.gotoNfcFragment(nfcBrickPosition);
		contextMenuActionRename(UiTestUtils.getResourcesString(R.string.test_tag_name_1), renamedTag);
		pressBack();
		spinnerValuesContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.test_tag_name_2);
		spinnerValuesContained.add(renamedTag);
		spinnerValuesNotContained = getStringsFromResourceIds(R.string.test_tag_name_1);
		checkNfcSpinnerContains(nfcBrickPosition, spinnerValuesContained, spinnerValuesNotContained);
	}

	private void contextMenuActionDelete(String tagName) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());
		onData(allOf(instanceOf(NfcTagData.class), NFCTagDataNameMatchers.isNFCTagDataName(tagName)))
				.inAdapterView(NFCTagListMatchers.isNFCTagListView())
				.onChildView(withText(tagName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());
		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());
		onView(withText(tagName))
				.check(doesNotExist());
	}

	private void contextMenuActionRename(String tagName, String renameString) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withText(tagName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());
		onView(withText(R.string.rename_nfctag_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withId(R.id.edit_text), withText(tagName), isDisplayed()))
				.perform(replaceText(renameString));
		closeSoftKeyboard();
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());
		onView(withText(tagName))
				.check(doesNotExist());
	}

	private List<String> getStringsFromResourceIds(Integer... stringResourceIds) {
		List<String> stringList = new ArrayList<>();
		for (int resourceId : stringResourceIds) {
			stringList.add(UiTestUtils.getResourcesString(resourceId));
		}
		return stringList;
	}
}
