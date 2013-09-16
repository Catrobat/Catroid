/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.interaction;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class UserBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo = null;

	public UserBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProjectWithUserBrick();

		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testUserBrickEditInstanceScriptChangesOtherInstanceScript() throws InterruptedException {
		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);

		solo.sleep(1000);
		// click on position x brick-heights above/below the place where the brick currently is
		int[] location = UiTestUtils.dragFloatingBrick(solo, -1);
		assertTrue("was not able to find the brick we just added: first user brick", location != null);
		solo.sleep(4000);

		Script currentScript = UiTestUtils.getProjectManager().getCurrentScript();
		int indexOfUserBrickInScript = currentScript.containsBrickOfTypeReturnsFirstIndex(UserBrick.class);
		assertTrue("current script should contain a User Brick after we tried to add one.",
				indexOfUserBrickInScript != -1);

		UserBrick userBrick = (UserBrick) currentScript.getBrick(indexOfUserBrickInScript);
		assertTrue("we should be able to cast the brick we found to a User Brick.", userBrick != null);

		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		// add a new brick to the internal script of the user brick
		UiTestUtils.addNewBrick(solo, R.string.brick_change_y_by);

		// place it
		location = UiTestUtils.dragFloatingBrick(solo, 1);
		assertTrue("was not able to find the brick we just added: brick inside user brick", location != null);
		solo.sleep(1000);

		// go back to normal script activity
		solo.goBack();
		solo.sleep(2000);
		solo.goBack();
		solo.sleep(2000);

		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);

		location = UiTestUtils.dragFloatingBrick(solo, 1);
		assertTrue("was not able to find the brick we just added: second user brick", location != null);

		solo.sleep(2000);

		// click on the location the brick was just dragged to.
		solo.clickLongOnScreen(location[0], location[1], 10);

		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, false, solo);

		String brickAddedToUserBrickScriptName = solo.getCurrentActivity().getString(R.string.brick_change_y_by);
		assertTrue("was not able to find the script we added to the other instance",
				solo.searchText(brickAddedToUserBrickScriptName));
	}
}
