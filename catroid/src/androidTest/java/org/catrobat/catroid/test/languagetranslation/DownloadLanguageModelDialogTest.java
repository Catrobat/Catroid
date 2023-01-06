/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.languagetranslation;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.TranslateTextFromToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class DownloadLanguageModelDialogTest {

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
		onView(isRoot()).perform(CustomActions.wait(2000));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void firstTestDownloadLanguageModelsDialogClickOnMaybeLater() {
		onView(withText("MAYBE LATER")).check(matches(isDisplayed()));
		onView(withId(R.id.stage_dialog_download_language_model_button_maybe_later))
				.check(matches(isClickable()));

		onView(withId(R.id.stage_dialog_download_language_model))
				.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withText(getText())).check(matches(isDisplayed()));

		onView(withId(R.id.stage_dialog_download_language_model_button_maybe_later))
				.perform(click());
		onView(withText("MAYBE LATER")).check(doesNotExist());

		onView(withText(getText())).check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void secondTestDownloadLanguageModelsDialogClickOnDownloadNow() {
		onView(withText("DOWNLOAD NOW")).check(matches(isDisplayed()));
		onView(withId(R.id.stage_dialog_download_language_model_button_download_now))
				.check(matches(isClickable()));

		onView(withId(R.id.stage_dialog_download_language_model))
				.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withText(getText())).check(matches(isDisplayed()));

		onView(withId(R.id.stage_dialog_download_language_model_button_download_now))
				.perform(click());
		onView(withText("DOWNLOAD NOW")).check(doesNotExist());

		onView(withText(getText())).check(doesNotExist());
	}

	private void createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), "DownloadLanguageModelsTest");
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();
		sprite.addScript(startScript);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		UserVariable userVariable = new UserVariable("Translation");
		project.addUserVariable(userVariable);
		userVariable.setValue(0.0);

		TranslateTextFromToBrick translateTextFromToBrick = new TranslateTextFromToBrick(
				new Formula("Bienvenidos"), new Formula("es"), new Formula("de"), userVariable);
		startScript.addBrick(translateTextFromToBrick);
	}

	private String getText() {
		return "A translation brick from 'es' to language 'de' is being executed. "
				+ "To enable this feature, language models must be downloaded from the internet, "
				+ "which may incur additional costs. The size of the additional model or models is "
				+ "60 MB. Do you want to download it/them now?";
	}
}
