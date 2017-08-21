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
package org.catrobat.catroid.uiespresso.stage;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ObjectVariableTest {
	private Sprite sprite;
	private ScriptEvaluationGateBrick lastBrickInScript;

	private static final double DELTA = 0.01d;

	private static final double SPRITE_X_POSITION = 30.0d;
	private static final double SPRITE_Y_POSITION = 50.0d;
	private static final double SPRITE_TRANSPARENCY = 0.8d;
	private static final double SPRITE_BRIGHTNESS = 0.7d;
	private static final double SPRITE_SIZE = 90.0d;
	private static final double SPRITE_DIRECTION = 42.0d;
	private static final int SPRITE_LAYER_CHANGE = 2;
	private static final int NUMBER_OF_SPRITES = 5;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("LookSensorValuesTest");
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testLookSensorValueInStage() throws InterpretationException {
		baseActivityTestRule.launchActivity(null);
		lastBrickInScript.waitUntilEvaluated(5000);

		assertEquals(SPRITE_X_POSITION, getSensorValue(Sensors.OBJECT_X), DELTA);
		assertEquals(SPRITE_Y_POSITION, getSensorValue(Sensors.OBJECT_Y), DELTA);
		assertEquals(SPRITE_TRANSPARENCY, getSensorValue(Sensors.OBJECT_TRANSPARENCY), DELTA);
		assertEquals(SPRITE_BRIGHTNESS, getSensorValue(Sensors.OBJECT_BRIGHTNESS), DELTA);
		assertEquals(SPRITE_SIZE, getSensorValue(Sensors.OBJECT_SIZE), DELTA);
		assertEquals(SPRITE_DIRECTION, getSensorValue(Sensors.OBJECT_ROTATION), DELTA);
		assertEquals(NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE, getSensorValue(Sensors.OBJECT_LAYER), DELTA);
	}

	public Double getSensorValue(Sensors sensor) throws InterpretationException {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula sensorFormula = new Formula(parseTree);
		return sensorFormula.interpretDouble(sprite);
	}

	private void createProject(String projectName) {
		Project project = new Project(null, projectName);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().addSprite(new SingleSprite("background"));
		ProjectManager.getInstance().addSprite(new SingleSprite("sprite1"));
		ProjectManager.getInstance().addSprite(new SingleSprite("sprite2"));
		ProjectManager.getInstance().addSprite(new SingleSprite("sprite3"));
		ProjectManager.getInstance().addSprite(new SingleSprite("sprite4"));

		sprite = new SingleSprite("sprite5");
		StartScript startScript = new StartScript();

		SetXBrick setXBrick = new SetXBrick((int) SPRITE_X_POSITION);
		startScript.addBrick(setXBrick);

		SetYBrick setYBrick = new SetYBrick((int) SPRITE_Y_POSITION);
		startScript.addBrick(setYBrick);

		SetTransparencyBrick setTransparencyBrick = new SetTransparencyBrick(SPRITE_TRANSPARENCY);
		startScript.addBrick(setTransparencyBrick);

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(SPRITE_BRIGHTNESS);
		startScript.addBrick(setBrightnessBrick);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(SPRITE_SIZE);
		startScript.addBrick(setSizeToBrick);

		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(SPRITE_DIRECTION);
		startScript.addBrick(pointInDirectionBrick);

		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(SPRITE_LAYER_CHANGE);
		startScript.addBrick(goNStepsBackBrick);

		sprite.addScript(startScript);

		ProjectManager.getInstance().addSprite(sprite);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(startScript);
	}
}
