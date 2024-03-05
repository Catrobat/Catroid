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
package org.catrobat.catroid.test.content.sprite;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.updateLocale;

@RunWith(AndroidJUnit4.class)
public class SpriteTest {

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private Project project;
	private Sprite sprite;
	private Locale locale;

	private AndroidStringProvider androidStringProvider =
			new AndroidStringProvider(ApplicationProvider.getApplicationContext());

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.getDefaultScene().addSprite(sprite);
		sprite.addUserVariable(new UserVariable(LOCAL_VARIABLE_NAME));
		sprite.getUserVariable(LOCAL_VARIABLE_NAME).setValue(LOCAL_VARIABLE_VALUE);

		UserVariable globalVariable = new UserVariable(GLOBAL_VARIABLE_NAME, GLOBAL_VARIABLE_VALUE);
		project.addUserVariable(globalVariable);

		ProjectManager.getInstance().setCurrentProject(project);
		locale = ApplicationProvider.getApplicationContext()
				.getResources().getConfiguration().locale;
	}

	@After
	public void tearDown() {
		updateLocale(ApplicationProvider.getApplicationContext(), locale);
	}

	@Test
	public void testUserVariableVisibilityOfLocalVariablesInDifferentScenes() {
		String variableName = "sceneTestVariable";

		Script script = new StartScript();
		Brick firstBrick = new ChangeBrightnessByNBrick(0);
		script.addBrick(firstBrick);
		sprite.addScript(script);

		Scene secondScene = new Scene("scene 2", project);
		secondScene.addSprite(new Sprite("Background"));
		Sprite sprite2 = new Sprite("testSprite2");
		Script secondScript = new StartScript();
		Brick textBrick = new ShowTextBrick(10, 10);
		secondScript.addBrick(textBrick);
		sprite2.addScript(secondScript);
		sprite2.addUserVariable(new UserVariable(variableName));
		UserVariable userVariable = sprite2.getUserVariable(variableName);
		userVariable.setValue(LOCAL_VARIABLE_VALUE);
		userVariable.setVisible(false);
		ProjectManager.getInstance().setCurrentlyPlayingScene(secondScene);

		ScriptSequenceAction thread = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(new StartScript());
		thread.addAction(sprite2.getActionFactory().createShowVariableAction(sprite2,
				new SequenceAction(), new Formula(10),
				new Formula(10), userVariable, androidStringProvider));
		secondScript.run(sprite2, thread);

		userVariable = sprite2.getUserVariable(variableName);
		assertFalse(userVariable.getVisible());

		thread.act(1f);

		userVariable = sprite2.getUserVariable(variableName);
		assertTrue(userVariable.getVisible());
	}

	@Test
	public void spriteEqualBackgroundTest() {
		sprite.setName("Hintergrund");
		updateLocale(ApplicationProvider.getApplicationContext(), new Locale("de"));
		project.checkIfSpriteNameEqualBackground(ApplicationProvider.getApplicationContext());
		Assert.assertNotEquals(sprite.getName(), "Hintergrund");
		Assert.assertEquals(sprite.getName(), "Hintergrund (1)");
	}
}
