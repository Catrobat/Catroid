/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.EventThread;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SpriteTest {

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private Project project;
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = new SingleSprite("testSprite");
		project = new Project(InstrumentationRegistry.getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.getDefaultScene().addSprite(sprite);
		sprite.addUserVariable(new UserVariable(LOCAL_VARIABLE_NAME));
		sprite.getUserVariable(LOCAL_VARIABLE_NAME).setValue(LOCAL_VARIABLE_VALUE);

		UserVariable globalVariable = new UserVariable(GLOBAL_VARIABLE_NAME, GLOBAL_VARIABLE_VALUE);
		project.addUserVariable(globalVariable);

		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testAddScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		assertEquals(1, sprite.getNumberOfScripts());

		sprite.addScript(0, secondScript);
		assertEquals(2, sprite.getNumberOfScripts());

		assertEquals(1, sprite.getScriptIndex(firstScript));
		assertEquals(0, sprite.getScriptIndex(secondScript));

		sprite.removeAllScripts();
		assertEquals(0, sprite.getNumberOfScripts());
	}

	@Test
	public void testGetScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals(firstScript, sprite.getScript(0));
		assertEquals(secondScript, sprite.getScript(1));
	}

	@Test
	public void testRemoveAllScripts() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeAllScripts();

		assertEquals(0, sprite.getNumberOfScripts());
	}

	@Test
	public void testRemoveScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeScript(firstScript);

		assertEquals(1, sprite.getNumberOfScripts());
		assertEquals(secondScript, sprite.getScript(0));
	}

	@Test
	public void testGetScriptIndex() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals(0, sprite.getScriptIndex(firstScript));
		assertEquals(1, sprite.getScriptIndex(secondScript));
	}

	@Test
	public void testSpriteCloneWithLocalVariable() throws IOException {
		Script script = new StartScript();
		Brick brick = new ChangeBrightnessByNBrick(new Formula(new FormulaElement(ElementType.USER_VARIABLE,
				LOCAL_VARIABLE_NAME, null)));

		script.addBrick(brick);
		sprite.addScript(script);
		Sprite clonedSprite = new SpriteController().copy(sprite, project, project.getDefaultScene());

		UserVariable clonedVariable = clonedSprite.getUserVariable(LOCAL_VARIABLE_NAME);

		assertNotNull(clonedVariable);
		assertEquals(LOCAL_VARIABLE_NAME, clonedVariable.getName());
		assertEquals(LOCAL_VARIABLE_VALUE, clonedVariable.getValue());

		List<UserVariable> userVariableList = clonedSprite.getUserVariables();

		Set<String> hashSet = new HashSet<>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue(hashSet.add(userVariable.getName()));
		}
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
		Sprite sprite2 = new SingleSprite("testSprite2");
		Script secondScript = new StartScript();
		Brick textBrick = new ShowTextBrick(10, 10);
		secondScript.addBrick(textBrick);
		sprite2.addScript(secondScript);
		sprite2.addUserVariable(new UserVariable(variableName));
		UserVariable userVariable = sprite2.getUserVariable(variableName);
		userVariable.setValue(LOCAL_VARIABLE_VALUE);
		userVariable.setVisible(false);
		ProjectManager.getInstance().setCurrentlyPlayingScene(secondScene);

		EventThread thread = (EventThread) ActionFactory.createEventThread(new StartScript());
		thread.addAction(sprite2.getActionFactory().createShowVariableAction(sprite2, new Formula(10),
				new Formula(10), userVariable));
		secondScript.run(sprite2, thread);

		userVariable = sprite2.getUserVariable(variableName);
		assertFalse(userVariable.getVisible());

		thread.act(1f);

		userVariable = sprite2.getUserVariable(variableName);
		assertTrue(userVariable.getVisible());
	}
}
