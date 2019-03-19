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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.AskAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class AskActionTest {

	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String ASK_QUESTION = "What's your name";
	private static final String ASK_ANSWER = "Catrobat Pocket Cat";
	private Sprite testSprite;
	private Project project;
	private UserVariable userVariableForAnswer;

	@Before
	public void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		project = new Project(MockUtil.mockContextForProject(), "testProject");

		ProjectManager.getInstance().setCurrentProject(project);

		userVariableForAnswer = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariableForAnswer);
		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@Test
	public void testAskAndCheckAnswer() {
		AskAction action = (AskAction) testSprite.getActionFactory().createAskAction(testSprite, new Formula(ASK_QUESTION),
				userVariableForAnswer);
		action.act(1f);
		action.setAnswerText(ASK_ANSWER);

		assertEquals(ASK_ANSWER, userVariableForAnswer.getValue().toString());
	}
}
