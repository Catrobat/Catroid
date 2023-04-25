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

package org.catrobat.catroid.test.formulaeditor;

import android.content.Context;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Spy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class RecognizeFormulaInTextTest {
	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"NoFormula", "This is just text", false},
				{"WithFormula", "sine(20)", true},
				{"WithCompoundFormula", "random value from to( modulo (20,4), 7)", true},
				{"FormulaWithoutParams", "inclination x", true},
				{"FormulaWithUserVariable", "variable + 4", true},
				{"FormulaWithUserList", "list", true},
				{"FormulaWithUserDefinedBrickInput", "userDefinedBrickInput + 3", true}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public String string;

	@Parameterized.Parameter(2)
	public boolean expectedResult;

	Context context;
	Project project;
	Sprite sprite;
	UserVariable userVariable = new UserVariable("variable");
	UserList userList = new UserList("list", Arrays.asList(new Object[]{"a", "b", "c"}));

	@Spy
	private FormulaEditorFragment formulaEditorFragmentMock;
	@Spy
	private FormulaBrick formulaBrick;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		UserDefinedScript userDefinedScript = new UserDefinedScript();
		UserDefinedBrick userDefinedBrick = new UserDefinedBrick(Collections.singletonList(new UserDefinedBrickInput("userDefinedBrickInput")));
		userDefinedScript.setScriptBrick(new UserDefinedReceiverBrick(userDefinedBrick));

		doReturn(formulaBrick).when(formulaEditorFragmentMock).getFormulaBrick();
		doReturn(userDefinedScript).when(formulaBrick).getScript();

		context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		project = new Project(context, RecognizeFormulaInTextTest.class.getSimpleName());
		sprite = new Sprite();

		Scene scene = new Scene();
		scene.addSprite(sprite);
		sprite.addUserVariable(userVariable);
		sprite.addUserList(userList);
		project.addScene(scene);
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(RecognizeFormulaInTextTest.class.getSimpleName());
	}

	@Test
	public void testEditFormula() {
		assertEquals(formulaEditorFragmentMock.recognizedFormulaInText(string, context, project, sprite), expectedResult);
	}
}
