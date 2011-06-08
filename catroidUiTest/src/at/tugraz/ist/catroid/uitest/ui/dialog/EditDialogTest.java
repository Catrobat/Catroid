/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class EditDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;

	public EditDialogTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Utils.createTestProject();
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
		Utils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testIntegerDialog() {
		Utils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);

		int xPosition = 5;
		int yPosition = 7;

		int yPositionEditTextId = solo.getCurrentEditTexts().size() - 1;
		int xPositionEditTextId = yPositionEditTextId - 1;

		Utils.insertIntegerIntoEditText(solo, xPositionEditTextId, xPosition);
		solo.sendKey(Solo.ENTER);
		Utils.insertIntegerIntoEditText(solo, yPositionEditTextId, yPosition);
		solo.sendKey(Solo.ENTER);

		assertEquals("Wrong value in X-Position EditText", xPosition + "", solo.getEditText(xPositionEditTextId)
				.getText().toString());
		assertEquals("Wrong value in Y-Position EditText", yPosition + "", solo.getEditText(yPositionEditTextId)
				.getText().toString());
	}

	public void testDoubleDialog() {
		Utils.addNewBrickAndScrollDown(solo, R.string.brick_wait);

		double wait = 5.9;

		int waitEditTextId = solo.getCurrentEditTexts().size() - 1;
		Utils.insertDoubleIntoEditText(solo, waitEditTextId, wait);
		solo.sendKey(Solo.ENTER);

		assertEquals("Wrong value in WaitBrick EditText", wait + "", solo.getEditText(waitEditTextId).getText()
				.toString());
	}

	public void testEmptyEditDoubleDialog() {
		Utils.addNewBrickAndScrollDown(solo, R.string.brick_scale_costume);

		int editTextId = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnEditText(editTextId);
		solo.sleep(50);

		solo.clearEditText(0);
		assertTrue("Toast with warning was not found",
				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
		assertFalse(solo.getButton(getActivity().getString(R.string.ok)).isEnabled());

		solo.enterText(0, ".");
		assertTrue("Toast with warning was not found",
				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
		assertFalse(solo.getButton(0).isEnabled());
	}

	public void testEmptyEditIntegerDialog() {
		Utils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);

		int editTextId = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnEditText(editTextId);
		solo.sleep(50);

		solo.clearEditText(0);
		assertTrue("Toast with warning was not found",
				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
		assertFalse(solo.getButton(0).isEnabled());
	}
}
