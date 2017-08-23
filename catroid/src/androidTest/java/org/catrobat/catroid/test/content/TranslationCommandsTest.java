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
package org.catrobat.catroid.test.content;

import android.content.res.Configuration;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.Translatable;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.commands.CommandFactory;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TranslationCommandsTest {

	private static final Integer TEST_SCENE = R.string.template_acceleration;
	private static final Integer TEST_SPRITE = R.string.template_active_coin1;
	private static final Integer TEST_USER_VARIABLE = R.string.template_correct;
	private static final Integer TEST_USER_LIST = R.string.template_wrong;
	private static final Integer TEST_LOOK = R.string.template_look_1_to_odd_out;
	private static final Integer TEST_BROADCAST = R.string.template_breakfast;
	private static final Integer TEST_NOTE = R.string.template_note_change_the_look_with_every_clone;

	private Project project;
	private Locale localeToRestore;

	@Before
	public void setUp() {
		project = new Project(getTargetContext(), "testProject" + System.currentTimeMillis(), false);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().saveProject(getTargetContext());

		Scene scene = StorageHandler.getInstance().createDefaultScene(getString(TEST_SCENE, "en"), false, false, getTargetContext());
		UtilFile.deleteDirectory(new File(Utils.buildScenePath(project.getName(), project.getDefaultScene().getName())));
		project.removeScene(project.getDefaultScene());
		project.addScene(scene);
		ProjectManager.getInstance().setCurrentScene(scene);
		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);

		localeToRestore = getTargetContext().getResources().getConfiguration().locale;
		SettingsActivity.updateLocale(getTargetContext(), Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testTranslationCommands() {
		List<Scene> scenes = new ArrayList<>();
		List<Sprite> sprites = new ArrayList<>();
		List<UserVariable> userVariables = new ArrayList<>();
		List<UserList> userLists = new ArrayList<>();
		List<LookData> looks = new ArrayList<>();
		List<Formula> userVariableFormulas = new ArrayList<>();
		List<Formula> userListFormulas = new ArrayList<>();
		List<Translatable> translatables = new ArrayList<>();

		scenes.add(project.getDefaultScene());
		CommandFactory.translateScenesCommand(scenes, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_SCENE, "de"), project.getDefaultScene().getName());

		Sprite sprite = new Sprite(getString(TEST_SPRITE, "en"));
		sprites.add(sprite);
		CommandFactory.translateSpritesCommand(sprites, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_SPRITE, "de"), sprite.getName());

		UserVariable variable = new UserVariable(getString(TEST_USER_VARIABLE, "en"));
		userVariables.add(variable);
		FormulaElement variableElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, getString(TEST_USER_VARIABLE, "en"), null);
		Formula variableFormula = new Formula(variableElement);
		userVariableFormulas.add(variableFormula);
		CommandFactory.translateUserVariablesCommand(userVariables, userVariableFormulas, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_USER_VARIABLE, "de"), variable.getName());
		assertEquals("String was not translated!", getString(TEST_USER_VARIABLE, "de"), variableFormula.getFormulaTree().getValue());

		UserList userList = new UserList(getString(TEST_USER_LIST, "en"));
		userLists.add(userList);
		FormulaElement listElement = new FormulaElement(FormulaElement.ElementType.USER_LIST, getString(TEST_USER_LIST, "en"), null);
		Formula listFormula = new Formula(listElement);
		userListFormulas.add(listFormula);
		CommandFactory.translateUserListsCommand(userLists, userListFormulas, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_USER_LIST, "de"), userList.getName());
		assertEquals("String was not translated!", getString(TEST_USER_LIST, "de"), listFormula.getFormulaTree().getValue());

		LookData look = new LookData(getString(TEST_LOOK, "en"), "testfile");
		looks.add(look);
		CommandFactory.translateLooksCommand(looks, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_LOOK, "de"), look.getLookName());

		NoteBrick note = new NoteBrick(getString(TEST_NOTE, "en"));
		BroadcastBrick broadcast = new BroadcastBrick(getString(TEST_BROADCAST, "en"));
		translatables.add(note);
		translatables.add(broadcast);
		CommandFactory.translateTranslatablesCommand(translatables, getTargetContext()).execute();
		assertEquals("String was not translated!", getString(TEST_NOTE, "de"), note.getFormulas().get(0).getFormulaTree().getValue());
		assertEquals("String was not translated!", getString(TEST_BROADCAST, "de"), broadcast.getBroadcastMessage());
	}

	@After
	public void tearDown() throws Exception {
		ProjectManager.getInstance().deleteCurrentProject(getTargetContext());
		SettingsActivity.updateLocale(getTargetContext(), localeToRestore.getLanguage(), localeToRestore.getCountry());
	}

	private String getString(int message, String languageCode) {
		Configuration configuration = getLanguageConfiguration(languageCode);
		return getTargetContext().createConfigurationContext(configuration).getResources().getString(message);
	}

	private Configuration getLanguageConfiguration(String languageCode) {
		Configuration configuration = new Configuration(getTargetContext().getResources().getConfiguration());
		configuration.setLocale(new Locale(languageCode));
		return configuration;
	}
}
