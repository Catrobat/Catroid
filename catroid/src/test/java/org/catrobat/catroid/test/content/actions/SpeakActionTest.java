/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.Context;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.stage.SpeechSynthesizer;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.PowerMockUtil;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({File.class, SpeakAction.class, Constants.class, FlavoredConstants.class, CatroidApplication.class})
public class SpeakActionTest {

	private Sprite sprite;
	private static final String SPEAK = "hello world!";
	private FormulaElement helloStringFormulaElement;
	private FormulaElement worldStringFormulaElement;
	private Formula textNumber;
	private Formula textFunction;
	private Formula textString;
	private Scope scope;
	private ActionFactory factory = new ActionFactory();
	private TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		Context contextMock = PowerMockUtil.mockStaticAppContextAndInitializeStaticSingletons();
		temporaryFolder.create();
		File temporaryCacheFolder = temporaryFolder.newFolder("SpeakTest");
		Mockito.when(contextMock.getCacheDir()).thenAnswer(invocation -> temporaryCacheFolder);

		sprite = new Sprite("testSprite");
		textNumber = new Formula(888.88);
		helloStringFormulaElement = new FormulaElement(FormulaElement.ElementType.STRING, "hello ", null);
		worldStringFormulaElement = new FormulaElement(FormulaElement.ElementType.STRING, "world!", null);
		textFunction = new Formula(new FormulaElement(FormulaElement.ElementType.FUNCTION,
			Functions.JOIN.name(), null, helloStringFormulaElement, worldStringFormulaElement));
		textString = new Formula(SPEAK);
		scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, new SequenceAction());

		Project project = new Project(MockUtil.mockContextForProject(), "Project");
		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testRequirements() {
		SpeakBrick speakBrick = new SpeakBrick(new Formula(""));
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		speakBrick.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.TEXT_TO_SPEECH));
	}

	@Test
	public void testBrickWithStringFormula() throws Exception {
		SpeakBrick speakBrick = new SpeakBrick(textString);
		SpeakAction action = new SpeakAction();
		SpeechSynthesizer synthesizer = new SpeechSynthesizer(scope, textString);

		action.setSpeechSynthesizer(synthesizer);
		action.initialize();

		assertEquals(textString, speakBrick.getFormulaWithBrickField(Brick.BrickField.SPEAK));
		assertEquals(SPEAK, String.valueOf(Reflection.getPrivateField(synthesizer, "interpretedText")));
	}

	@Test
	public void testBrickWithFunctionFormula() throws Exception {
		SpeakBrick speakBrick = new SpeakBrick(textFunction);
		SpeakAction action = new SpeakAction();
		SpeechSynthesizer synthesizer = new SpeechSynthesizer(scope, textString);

		action.setSpeechSynthesizer(synthesizer);
		action.initialize();

		assertEquals(textFunction, speakBrick.getFormulaWithBrickField(Brick.BrickField.SPEAK));
		assertEquals(SPEAK, String.valueOf(Reflection.getPrivateField(synthesizer, "interpretedText")));
	}

	@Test
	public void testNullFormula() throws Exception {
		SpeakAction action = new SpeakAction();
		SpeechSynthesizer synthesizer = new SpeechSynthesizer(scope, null);

		action.setSpeechSynthesizer(synthesizer);
		action.initialize();

		assertEquals("", String.valueOf(Reflection.getPrivateField(synthesizer, "interpretedText")));
	}

	@Test
	public void testNotANumberFormula() throws Exception {
		SpeakAction action = new SpeakAction();
		SpeechSynthesizer synthesizer = new SpeechSynthesizer(scope, new Formula(Double.NaN));

		action.setSpeechSynthesizer(synthesizer);
		action.initialize();

		assertEquals("", String.valueOf(Reflection.getPrivateField(synthesizer, "interpretedText")));
	}

	@Test
	public void testSynthesizeInitialization() {
		SpeakAction action = (SpeakAction) factory.createSpeakAction(sprite, new SequenceAction(), textNumber);
		assertNotNull(action.getSpeechSynthesizer());
	}
}
