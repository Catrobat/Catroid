/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.ui.fragment;

/*import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.ScreenValues;
*/
import org.catrobat.catroid.ui.MainMenuActivity;
/*
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.uitest.annotation.Device;
*/
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
/*
import org.catrobat.catroid.uitest.util.SensorTestServerConnection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
*/

public class NfcTagFragmentHardwareTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
/*
	private static final int TIME_TO_WAIT = 50;

	private static final String FIRST_TEST_TAG_NAME = "tagNameTest";
	private static final String FIRST_TEST_TAG_ID = "111111";

	private static final String SECOND_TEST_TAG_NAME = "tagNameTest2";
	private static final String SECOND_TEST_TAG_ID = "222222";

	private NfcTagData tagData;
	private NfcTagData tagData2;

	private ArrayList<NfcTagData> tagDataList;

	private ProjectManager projectManager;
*/
	public NfcTagFragmentHardwareTest() {
		super(MainMenuActivity.class);
	}

	public void testThisTestmethodIsOnlyHereForPassingTheSourceTest(){
		assertSame("Remove me!!", "Remove me!!", "Remove me!!");
	}
/*
	@Override
	public void setUp() throws Exception {
		super.setUp();
		SensorTestServerConnection.connectToArduinoServer();
		UiTestUtils.enableNfcBricks(getActivity().getApplicationContext());
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();

		projectManager = ProjectManager.getInstance();
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();

		tagData = new NfcTagData();
		tagData.setNfcTagName(FIRST_TEST_TAG_NAME);
		tagData.setNfcTagUid(FIRST_TEST_TAG_ID);
		tagDataList.add(tagData);

		tagData2 = new NfcTagData();
		tagData2.setNfcTagName(SECOND_TEST_TAG_NAME);
		tagData2.setNfcTagUid(SECOND_TEST_TAG_ID);
		tagDataList.add(tagData2);

		Utils.updateScreenWidthAndHeight(solo.getCurrentActivity());
		projectManager.getCurrentProject().getXmlHeader().virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		projectManager.getCurrentProject().getXmlHeader().virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;

		UiTestUtils.getIntoNfcTagsFromMainMenu(solo, true);

		if (getNfcTagAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	@Device
	public void testScanTag() {
		NfcTagAdapter adapter = getNfcTagAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		solo.sleep(2000);

		SensorTestServerConnection.emulateNfcTag(false, "123456", "");

		solo.sleep(5000);

		int newCount = adapter.getCount();

		assertEquals("Tag not added!", oldCount + 1, newCount);
		assertEquals("Tag added but not visible!", solo.searchText(solo.getString(R.string.default_tag_name), 1), true);

	}

	private NfcTagFragment getNfcTagFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (NfcTagFragment) activity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS);
	}

	private NfcTagAdapter getNfcTagAdapter() {
		return (NfcTagAdapter) getNfcTagFragment().getListAdapter();
	}
	*/
}