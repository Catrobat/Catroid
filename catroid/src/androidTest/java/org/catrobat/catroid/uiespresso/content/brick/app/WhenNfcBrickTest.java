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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.adapter.NfcTagBaseAdapter;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST1;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.TAG_NAME_TEST2;
import static org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils.onNfcBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class WhenNfcBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private int nfcBrickPosition;
	private int setVariableIDPosition;
	private int setVariableMessagePosition;

	@Before
	public void setUp() throws Exception {
		createProjectWithNfcAndSetVariable();
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.SettingsAndPermissions.class})
	@Test
	public void testBasicLayout() {
		onBrickAtPosition(nfcBrickPosition).checkShowsText(R.string.brick_when_nfc);

		onBrickAtPosition(setVariableIDPosition).onSpinner(R.id.set_variable_spinner)
				.checkShowsText(UiNFCTestUtils.READ_TAG_ID);

		onBrickAtPosition(setVariableMessagePosition).onSpinner(R.id.set_variable_spinner)
				.checkShowsText(UiNFCTestUtils.READ_TAG_MESSAGE);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.SettingsAndPermissions.class})
	@Test
	@Flaky
	public void testAddNewTag() {
		List<String> spinnerValuesStringsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag);
		spinnerValuesStringsContained.addAll(Arrays.asList(TAG_NAME_TEST1, TAG_NAME_TEST2));

		List<String> spinnerValuesStringsNotContained = Arrays.asList(
				UiNFCTestUtils.THIRD_TEST_TAG_ID,
				UiNFCTestUtils.FOURTH_TEST_TAG_ID);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesStringsContained)
				.checkTagNamesNotDisplayed(spinnerValuesStringsNotContained);

		gotoNfcFragment(nfcBrickPosition);
		NfcTagAdapter adapter = getNfcTagAdapter(baseActivityTestRule.getActivity());
		adapter.setShowDetails(true);

		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.THIRD_TEST_TAG_ID, null, null, baseActivityTestRule.getActivity());
		onView(withText(R.string.default_tag_name))
				.check(matches(isDisplayed()));
		onView(withText(NfcHandler.byteArrayToHex(UiNFCTestUtils.THIRD_TEST_TAG_ID.getBytes())))
				.check(matches(isDisplayed()));
		int numberOfTagsInList = adapter.getCount();
		assertEquals(3, numberOfTagsInList);
		pressBack();

		spinnerValuesStringsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.default_tag_name);
		spinnerValuesStringsContained.addAll(Arrays.asList(TAG_NAME_TEST1, TAG_NAME_TEST2));

		spinnerValuesStringsNotContained = Arrays.asList(UiNFCTestUtils.FOURTH_TEST_TAG_ID);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesStringsContained)
				.checkTagNamesNotDisplayed(spinnerValuesStringsNotContained);

		gotoNfcFragment(nfcBrickPosition);
		UiNFCTestUtils.fakeNfcTag(UiNFCTestUtils.FOURTH_TEST_TAG_ID, null, null, baseActivityTestRule.getActivity());
		onView(withText(NfcHandler.byteArrayToHex(UiNFCTestUtils.FOURTH_TEST_TAG_ID.getBytes())))
				.check(matches(isDisplayed()));
		numberOfTagsInList = adapter.getCount();
		assertEquals(4, numberOfTagsInList);

		pressBack();

		spinnerValuesStringsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag,
				R.string.default_tag_name);
		spinnerValuesStringsContained.addAll(Arrays.asList(TAG_NAME_TEST1, TAG_NAME_TEST2));

		spinnerValuesStringsContained.add(UiTestUtils.getResourcesString(R.string.default_tag_name) + "_1");
		spinnerValuesStringsNotContained = Arrays.asList();

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesStringsContained)
				.checkTagNamesNotDisplayed(spinnerValuesStringsNotContained);

		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());
		onView(withText(R.string.default_tag_name))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Detailed.class, Cat.SettingsAndPermissions.class})
	@Test
	@Flaky
	public void testSpinnerUpdatesDelete() {
		List<String> spinnerValuesStringsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag);
		spinnerValuesStringsContained.addAll(Arrays.asList(TAG_NAME_TEST1, TAG_NAME_TEST2));

		List<String> spinnerValuesStringsNotContained = Arrays.asList();

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesStringsContained)
				.checkTagNamesNotDisplayed(spinnerValuesStringsNotContained);

		gotoNfcFragment(nfcBrickPosition);
		contextMenuActionDelete(TAG_NAME_TEST1);
		pressBack();
		spinnerValuesStringsContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag);
		spinnerValuesStringsContained.add(TAG_NAME_TEST2);
		spinnerValuesStringsNotContained = Arrays.asList(TAG_NAME_TEST1);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesStringsContained)
				.checkTagNamesNotDisplayed(spinnerValuesStringsNotContained);
	}

	@Category({Cat.AppUi.class, Level.Detailed.class, Cat.SettingsAndPermissions.class})
	@Test
	@Flaky
	public void testSpinnerUpdatesRename() {
		String renamedTag = "tag_renamed";
		List<String> spinnerValuesContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag);
		spinnerValuesContained.addAll(Arrays.asList(TAG_NAME_TEST1, TAG_NAME_TEST2));

		List<String> spinnerValuesNotContained = Arrays.asList();

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesContained)
				.checkTagNamesNotDisplayed(spinnerValuesNotContained);

		gotoNfcFragment(nfcBrickPosition);
		contextMenuActionRename(TAG_NAME_TEST1, renamedTag);
		pressBack();
		spinnerValuesContained = getStringsFromResourceIds(
				R.string.brick_when_nfc_default_all,
				R.string.new_nfc_tag);
		spinnerValuesContained.addAll(Arrays.asList(renamedTag, TAG_NAME_TEST2));
		spinnerValuesContained.add(renamedTag);
		spinnerValuesNotContained = Arrays.asList(TAG_NAME_TEST1);

		onNfcBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.checkTagNamesAvailable(spinnerValuesContained)
				.checkTagNamesNotDisplayed(spinnerValuesNotContained);
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

	private static final class NFCTagListMatchers {
		private NFCTagListMatchers() {
			throw new AssertionError();
		}

		public static Matcher<View> isNFCTagListView() {
			return new TypeSafeMatcher<View>() {
				@Override
				public void describeTo(Description description) {
					description.appendText("NFCTagListView");
				}

				@Override
				protected boolean matchesSafely(View view) {
					return view instanceof ListView && ((ListView) view).getAdapter() instanceof NfcTagBaseAdapter;
				}
			};
		}
	}

	private static final class NFCTagDataNameMatchers {
		private NFCTagDataNameMatchers() {
			throw new AssertionError();
		}

		public static Matcher<NfcTagData> isNFCTagDataName(final String expectedTagName) {
			return new TypeSafeMatcher<NfcTagData>() {
				@Override
				protected boolean matchesSafely(NfcTagData nfcTagData) {
					String nfcTagName = nfcTagData.getNfcTagName();
					return nfcTagName.equals(expectedTagName);
				}

				@Override
				public void describeTo(Description description) {
					description.appendText("NFCTagDataName");
				}
			};
		}
	}

	private void gotoNfcFragment(int nfcBrickPosition) {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());
		onView(withText(R.string.new_nfc_tag))
				.perform(click());
	}

	private NfcTagAdapter getNfcTagAdapter(Activity callingActivity) {
		ScriptActivity activity = (ScriptActivity) callingActivity;
		return (NfcTagAdapter) (activity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS))
				.getListAdapter();
	}

	private void createProjectWithNfcAndSetVariable() {
		Project project = new Project(null, "nfcTestProject");

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();

		UserVariable readTagId = dataContainer.addProjectUserVariable(UiNFCTestUtils.READ_TAG_ID);
		UserVariable readTagMessage = dataContainer.addProjectUserVariable(UiNFCTestUtils.READ_TAG_MESSAGE);
		UserVariable numDetectedTags = dataContainer.addProjectUserVariable(UiNFCTestUtils.NUM_DETECTED_TAGS);

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

		List<NfcTagData> tagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
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
		setVariableIDPosition = 1;
		setVariableMessagePosition = 2;
	}
}
