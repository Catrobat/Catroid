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

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpriteTest extends AndroidTestCase {

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private Project project;
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new SingleSprite("testSprite");
		project = new Project(getContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer()
				.getUserVariable(sprite, LOCAL_VARIABLE_NAME).setValue(LOCAL_VARIABLE_VALUE);

		project.getDefaultScene().getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer()
				.getUserVariable(null, GLOBAL_VARIABLE_NAME).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);
	}

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

	public void testGetScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals(firstScript, sprite.getScript(0));
		assertEquals(secondScript, sprite.getScript(1));
	}

	public void testRemoveAllScripts() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeAllScripts();

		assertEquals(0, sprite.getNumberOfScripts());
	}

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

	public void testGetScriptIndex() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals(0, sprite.getScriptIndex(firstScript));
		assertEquals(1, sprite.getScriptIndex(secondScript));
	}

	public void testSpriteCloneWithLocalVariable() {
		Script script = new StartScript();
		Brick brick = new ChangeBrightnessByNBrick(new Formula(new FormulaElement(ElementType.USER_VARIABLE,
				LOCAL_VARIABLE_NAME, null)));

		script.addBrick(brick);
		sprite.addScript(script);
		Sprite clonedSprite = sprite.clone();

		UserVariable clonedVariable = project.getDefaultScene().getDataContainer()
				.getUserVariable(clonedSprite, LOCAL_VARIABLE_NAME);

		assertNotNull(clonedVariable);
		assertEquals(LOCAL_VARIABLE_NAME, clonedVariable.getName());
		assertEquals(LOCAL_VARIABLE_VALUE, clonedVariable.getValue());

		List<UserVariable> userVariableList = project.getDefaultScene().getDataContainer()
				.getOrCreateVariableListForSprite(clonedSprite);

		Set<String> hashSet = new HashSet<>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue(hashSet.add(userVariable.getName()));
		}
	}

	public void testUserVariableVisibilityOfLocalVariablesInDifferentScenes() {
		String variableName = "sceneTestVariable";

		Script script = new StartScript();
		Brick firstBrick = new ChangeBrightnessByNBrick(0);
		script.addBrick(firstBrick);
		sprite.addScript(script);

		Scene secondScene = new Scene(getContext(), "scene 2", project);
		Sprite sprite2 = new SingleSprite("testSprite2");
		Script secondScript = new StartScript();
		Brick textBrick = new ShowTextBrick(10, 10);
		secondScript.addBrick(textBrick);
		sprite2.addScript(secondScript);
		secondScene.getDataContainer().addSpriteUserVariableToSprite(sprite2, variableName);
		UserVariable userVariable = secondScene.getDataContainer().getUserVariable(sprite2, variableName);
		userVariable.setValue(LOCAL_VARIABLE_VALUE);
		userVariable.setVisible(false);
		ProjectManager.getInstance().setSceneToPlay(secondScene);

		SequenceAction sequence = new SequenceAction();
		sequence.addAction(sprite2.getActionFactory().createShowVariableAction(sprite2, new Formula(10),
				new Formula(10), userVariable));
		secondScript.run(sprite2, sequence);

		DataContainer dataContainer = ProjectManager.getInstance().getSceneToPlay().getDataContainer();
		userVariable = dataContainer.getUserVariable(sprite2, variableName);
		assertFalse(userVariable.getVisible());

		sequence.act(1f);

		userVariable = dataContainer.getUserVariable(sprite2, variableName);
		assertTrue(userVariable.getVisible());
	}
}
