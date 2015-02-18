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
package org.catrobat.catroid.test.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;

public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	public MainMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@UiThreadTest
	public void testShouldNotCrashIfProjectIsNull() {
		ProjectManager.getInstance().setProject(null);
		getInstrumentation().callActivityOnPostCreate(getActivity(), null);
		assertTrue("Test failed!", true);
	}

	public void testContinueButtonIsBiggerThanOthers() {
		float continueButtonHeight = getActivity().findViewById(R.id.main_menu_button_continue).getHeight();
		float newButtonHeight = getActivity().findViewById(R.id.main_menu_button_new).getHeight();
		float programsButtonHeight = getActivity().findViewById(R.id.main_menu_button_programs).getHeight();
		float helpButtonHeight = getActivity().findViewById(R.id.main_menu_button_help).getHeight();
		float communityButtonHeight = getActivity().findViewById(R.id.main_menu_button_web).getHeight();
		float uploadButtonHeight = getActivity().findViewById(R.id.main_menu_button_upload).getHeight();

		final String message = "Button heights are not in the correct relation to each other!";
		assertEquals(message, 1.5, continueButtonHeight / newButtonHeight, 0.05);
		assertEquals(message, 1.5, continueButtonHeight / programsButtonHeight, 0.05);
		assertEquals(message, 1.5, continueButtonHeight / helpButtonHeight, 0.05);
		assertEquals(message, 1.5, continueButtonHeight / communityButtonHeight, 0.05);
		assertEquals(message, 1.5, continueButtonHeight / uploadButtonHeight, 0.05);
	}
}
