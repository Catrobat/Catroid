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
package org.catrobat.catroid.uitest.ui.dialog;

import android.view.View;
import android.widget.Button;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

import java.util.ArrayList;

public class TermsOfUseDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public TermsOfUseDialogTest() {
		super(MainMenuActivity.class);
	}

	public void testTermsOfUseDialog() {
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_terms_of_use));
		assertTrue("TermsOfUseDialog title not found", solo.searchText(solo.getString(R.string.dialog_terms_of_use_title)));
		assertTrue("TermsOfUseDialog text not found", solo.searchText(solo
				.getString(R.string.dialog_terms_of_use_info)));
		assertTrue("TermsOfUseDialog linktext not found",
				solo.searchText(solo.getString(R.string.dialog_terms_of_use_link_text)));

		Button termsOfUseDialogButton = (Button) solo.getView(android.R.id.button2);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);

		ArrayList<View> currentViews = solo.getCurrentViews();
		assertFalse("Not returned to MainMenuActivity", currentViews.contains(termsOfUseDialogButton));
	}
}
