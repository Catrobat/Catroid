/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class EditDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public EditDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testIntegerDialog() {
		solo.clickLongOnText(getActivity().getString(R.string.brick_when_started));
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(1000);

		UiTestUtils.addNewBrick(solo, R.string.brick_place_at);
		solo.clickOnText(getActivity().getString(R.string.brick_when_started));

		int xPosition = 5;
		int yPosition = 7;

		int yPositionEditTextId = 1;
		int xPositionEditTextId = 0;

		UiTestUtils.insertIntegerIntoEditText(solo, xPositionEditTextId, xPosition);
		solo.sleep(300);
		solo.clickOnButton(0);
		solo.sleep(300);
		UiTestUtils.insertIntegerIntoEditText(solo, yPositionEditTextId, yPosition);
		solo.sleep(300);
		solo.clickOnButton(0);
		solo.sleep(300);

		assertEquals("Wrong value in X-Position EditText", xPosition + "", solo.getEditText(xPositionEditTextId)
				.getText().toString());
		assertEquals("Wrong value in Y-Position EditText", yPosition + "", solo.getEditText(yPositionEditTextId)
				.getText().toString());
	}

	public void testDoubleDialog() {
		UiTestUtils.addNewBrick(solo, R.string.brick_wait);

		double wait = 5.9;

		UiTestUtils.insertDoubleIntoEditText(solo, 0, wait);
		solo.sendKey(Solo.ENTER);

		assertEquals("Wrong value in WaitBrick EditText", wait + "", solo.getEditText(0).getText().toString());
	}

	//Don't need these test anymore
	//	
	//	public void testEmptyEditDoubleDialog() {
	//		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_size_to);
	//
	//		int editTextId = solo.getCurrentEditTexts().size() - 1;
	//
	//		solo.clickOnEditText(editTextId);
	//		solo.sleep(50);
	//
	//		solo.clearEditText(0);
	//		assertTrue("Toast with warning was not found",
	//				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
	//		assertFalse("OK button was not disabled upon deleting text field contents",
	//				solo.getButton(getActivity().getString(R.string.ok)).isEnabled());
	//
	//		solo.enterText(0, ".");
	//		assertTrue("Toast with warning was not found",
	//				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
	//		assertFalse("OK button was not disabled upon entering invalid text", solo.getButton(0).isEnabled());
	//	}

	//	public void testEmptyEditIntegerDialog() {
	//		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);
	//
	//		int editTextId = solo.getCurrentEditTexts().size() - 1;
	//
	//		solo.clickOnEditText(editTextId);
	//		solo.sleep(50);
	//
	//		solo.clearEditText(0);
	//
	//		solo.enterText(0, "3.5");
	//		solo.sleep(300);
	//
	//		solo.clickOnButton(0);
	//
	//		assertTrue("Toast with warning was not found",
	//				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
	//		assertFalse("OK button was not disabled upon deleting text field contents", solo.getButton(0).isEnabled());
	//	}
}
