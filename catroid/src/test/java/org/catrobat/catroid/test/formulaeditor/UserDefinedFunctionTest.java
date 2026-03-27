/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.assertEqualsTokenLists;

@RunWith(JUnit4.class)
public class UserDefinedFunctionTest {
	private Scope scope;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(MockUtil.mockContextForProject(), "Project");
		scope = new Scope(project, project.getDefaultScene().getBackgroundSprite(), new SequenceAction());
		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testParseAndConvertUserDefinedFunction() {
		String brickId = UUID.randomUUID().toString();
		List<InternToken> tokens = new LinkedList<>();
		tokens.add(new InternToken(InternTokenType.USER_DEFINED_FUNCTION, brickId));
		tokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		tokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		tokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		tokens.add(new InternToken(InternTokenType.NUMBER, "2"));
		tokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser parser = new InternFormulaParser(tokens);
		FormulaElement tree = parser.parseFormula(scope);

		assertNotNull(tree);
		assertEquals(ElementType.USER_DEFINED_FUNCTION, tree.getElementType());
		assertEquals(brickId, tree.getValue());

		List<InternToken> convertedTokens = tree.getInternTokenList();
		assertEqualsTokenLists(tokens, convertedTokens);
	}
}
