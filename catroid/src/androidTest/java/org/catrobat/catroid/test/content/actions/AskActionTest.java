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

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.AskAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class AskActionTest extends AndroidTestCase {

	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String ASK_QUESTION = "What's your name";
	private static final String ASK_ANSWER = "Catrobat Pocket Cat";
	private Sprite testSprite;
	private Project project;
	private UserVariable userVariableForAnswer;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().getCurrentScene().getDataContainer().addProjectUserVariable(TEST_USERVARIABLE);
		userVariableForAnswer = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.getUserVariable(TEST_USERVARIABLE, null);
		super.setUp();
	}

	public void testAskAndCheckAnswer() {
		AskAction action = (AskAction) testSprite.getActionFactory().createAskAction(testSprite, new Formula(ASK_QUESTION),
				userVariableForAnswer);
		action.act(1f);
		action.setAnswerText(ASK_ANSWER);

		assertEquals("answer incorrect", ASK_ANSWER, userVariableForAnswer.getValue().toString());
	}
}
