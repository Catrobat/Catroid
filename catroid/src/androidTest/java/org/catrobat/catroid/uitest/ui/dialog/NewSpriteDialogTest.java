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
package org.catrobat.catroid.uitest.ui.dialog;

import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog.ActionAfterFinished;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.io.IOException;

public class NewSpriteDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testingproject = UiTestUtils.PROJECTNAME1;
	private String testingsprite = "testingsprite";

	private File lookFile;

	public NewSpriteDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		lookFile = UiTestUtils.setUpLookFile(solo, getActivity());
		UiTestUtils.createTestProject(testingproject);
	}

	@Override
	protected void tearDown() throws Exception {
		lookFile.delete();
		super.tearDown();
	}

	public void testNewSpriteDialogStep1() throws Exception {
	}

	public void testNewSpriteDialogStep2() throws NameNotFoundException, IOException {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String spriteName = "spriteError";
		UiTestUtils.addNewSprite(solo, spriteName, lookFile, null);
		assertTrue("Sprite not successfully added", ProjectManager.getInstance().spriteExists(spriteName));

		//Add sprite which already exists
		UiTestUtils.showAndFilloutNewSpriteDialogWithoutClickingOk(solo, spriteName, Uri.fromFile(lookFile),
				ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT, null);
		solo.clickOnButton(solo.getString(R.string.ok));

		String errorMessageText = solo.getString(R.string.spritename_already_exists);
		String buttonCloseText = solo.getString(R.string.close);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(errorMessageText));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);

		String okButtonText = solo.getString(R.string.ok);
		boolean okButtonEnabled = solo.getButton(okButtonText).isEnabled();
		assertTrue("'" + okButtonText + "' button is deactivated", okButtonEnabled);

		int spriteEditTextId = solo.getCurrentViews(EditText.class).size() - 1;
		assertTrue("Not in NewSpriteDialog", solo.searchText(solo.getString(R.string.new_sprite_dialog_title)));

		UiTestUtils.enterText(solo, spriteEditTextId, testingsprite);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Current Activity is not ScriptActivity", ScriptActivity.class);
	}
}
