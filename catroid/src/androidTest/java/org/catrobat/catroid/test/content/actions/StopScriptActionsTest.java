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

package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class StopScriptActionsTest extends InstrumentationTestCase {

	private Project project;

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		this.createProject();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testStopScript() {
		Sprite sprite = new Sprite("sprite");
		StartScript script = new StartScript();

		sprite.look.setX(1);

		script.addBrick(new SetXBrick(20));
		script.addBrick(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));
		script.addBrick(new SetXBrick(50));
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		for (int i = 0; i < 100; i++) {
			sprite.look.act(1.0f);
		}

		assertEquals("Script didn't stop", 20.0f, sprite.look.getX());
	}

	public void testStopCurrentScript() {
		String variableName = "testVariable";
		project.getDefaultScene().getDataContainer().addProjectUserVariable(variableName);
		UserVariable userVariable = project.getDefaultScene().getDataContainer().getUserVariable(variableName, null);
		Sprite sprite = new Sprite("sprite");

		Script script = new StartScript();
		script.addBrick(new SetVariableBrick(new Formula(10), userVariable));
		script.addBrick(new WaitBrick(500));
		script.addBrick(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));
		script.addBrick(new SetVariableBrick(new Formula(20), userVariable));

		ForeverBrick foreverBrick = new ForeverBrick();
		LoopEndBrick endBrick = new LoopEndBrick(foreverBrick);
		foreverBrick.setLoopEndBrick(endBrick);

		Script script2 = new StartScript();
		script2.addBrick(foreverBrick);
		script2.addBrick(new SetVariableBrick(new Formula(50), userVariable));
		script2.addBrick(endBrick);

		sprite.addScript(script);
		sprite.addScript(script2);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script2);

		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		for (int i = 0; i < 50; i++) {
			sprite.look.act(10.0f);
		}

		userVariable = project.getDefaultScene().getDataContainer().getUserVariable(variableName, null);

		assertEquals("Script didn't stop", 50.0, userVariable.getValue());
	}

	public void testStopOtherScripts() {
		String varName = "testVar";
		project.getDefaultScene().getDataContainer().addProjectUserVariable(varName);
		UserVariable userVar = project.getDefaultScene().getDataContainer().getUserVariable(varName, null);

		Sprite sprite = new Sprite("sprite");
		Script script = new StartScript();

		script.addBrick(new SetVariableBrick(new Formula(1), userVar));
		script.addBrick(new WaitBrick(1000));
		script.addBrick(new StopScriptBrick(BrickValues.STOP_OTHER_SCRIPTS));
		script.addBrick(new SetVariableBrick(new Formula(2), userVar));

		ForeverBrick foreverBrick = new ForeverBrick();
		LoopEndBrick endBrick = new LoopEndBrick(foreverBrick);
		foreverBrick.setLoopEndBrick(endBrick);

		Script script2 = new StartScript();
		script2.addBrick(foreverBrick);
		script2.addBrick(new SetVariableBrick(new Formula(50), userVar));
		script2.addBrick(endBrick);

		sprite.addScript(script2);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script2);

		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		for (int i = 0; i < 100; i++) {
			sprite.look.act(1.0f);
		}

		userVar = project.getDefaultScene().getDataContainer().getUserVariable(varName, null);

		assertEquals("Script didn't stop", 2.0, userVar.getValue());
	}

	private void createProject() throws IOException {
		this.project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
