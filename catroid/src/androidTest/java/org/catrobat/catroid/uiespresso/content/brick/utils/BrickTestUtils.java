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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public final class BrickTestUtils {
	private BrickTestUtils() {
		throw new AssertionError();
	}

	public static Script createProjectAndGetStartScript(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
	}

	public static void createUserListFromDataFragment(String userListName, boolean forAllSprites) {
		onView(withId(R.id.data_user_variables_headline))
				.check(matches(isDisplayed()));
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(typeText(userListName), closeSoftKeyboard());

		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.perform(scrollTo(), click());

		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.check(matches(isChecked()));

		if (forAllSprites) {
			onView(withId(R.id.dialog_formula_editor_data_name_global_variable_radio_button))
					.perform(click());
		} else {
			onView(withId(R.id.dialog_formula_editor_data_name_local_variable_radio_button))
					.perform(click());
		}
		onView(withId(android.R.id.button1)).perform(click());
		onView(withText(userListName))
				.check(matches(isDisplayed()));
	}
}
