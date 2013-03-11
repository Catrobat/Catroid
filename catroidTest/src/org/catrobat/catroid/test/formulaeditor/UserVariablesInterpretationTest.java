/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.formulaeditor;

import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import android.test.AndroidTestCase;

public class UserVariablesInterpretationTest extends AndroidTestCase {

	private static final double USER_VARIABLE_VALUE = 5d;
	private static final String USER_VARIABLE_NAME = "userVariable1";
	private static final double DELTA = 0.001d;
	private Sprite testSprite;
	private Project project;
	private Sprite firstSprite;
	private StartScript startScript1;
	private ChangeSizeByNBrick changeBrick;

	@Override
	protected void setUp() {
		testSprite = new Sprite("sprite");
		this.project = new Project(null, "testProject");
		firstSprite = new Sprite("zwoosh");
		startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(USER_VARIABLE_NAME, USER_VARIABLE_VALUE);
	}

	public void testUservariables() {

		Formula formula = getUservariableByName(USER_VARIABLE_NAME);
		assertEquals("Formula interpretation is not as expected", USER_VARIABLE_VALUE,
				formula.interpretFloat(testSprite), DELTA);
	}

	public void testNotExistingUservariable() {

		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, "NOT_EXISTING_USER_VARIABLE"));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNull("Invalid user variable parsed:   NOT_EXISTING_USER_VARIABLE)", parseTree);
		int errorTokenIndex = internParser.getErrorTokenIndex();
		assertEquals("Error Token Index is not as expected", 0, errorTokenIndex);

	}

	private Formula getUservariableByName(String userVariableName) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, userVariableName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		return new Formula(parseTree);
	}
}
