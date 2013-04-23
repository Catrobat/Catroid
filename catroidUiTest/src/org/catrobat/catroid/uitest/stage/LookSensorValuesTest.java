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
package org.catrobat.catroid.uitest.stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class LookSensorValuesTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Solo solo;
	private Sprite sprite;
	private Sprite sprite2;
	private static final int SPRITE_X_POSITION = 30;
	private static final int SPRITE_Y_POSITION = 50;
	private static final float SPRITE_GHOSTEFFECT = 0.8F;
	private static final float SPRITE_BRIGHTNESS = 0.7F;
	private static final int SPRITE_SIZE = 90;
	private static final float SPRITE_DIRECTION = 42.0F;
	private static final int SPRITE_LAYER_CHANGE = 2;
	private static final int NUMBER_OF_SPRITES = 5;
	private static final float DELTA = 0.01f;

	public LookSensorValuesTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public Formula getFormulaBySensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		return new Formula(parseTree);

	}

	public void testLookSensorValueInStage() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(2000);
		assertEquals("Variable shows false x position", SPRITE_X_POSITION,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());

		Formula lookXPositionFormula = getFormulaBySensor(Sensors.LOOK_X);
		assertEquals("Variable shows false x position", SPRITE_X_POSITION, lookXPositionFormula.interpretFloat(sprite),
				DELTA);

		Formula lookYPositionFormula = getFormulaBySensor(Sensors.LOOK_Y);
		assertEquals("Variable shows false x position", SPRITE_Y_POSITION, lookYPositionFormula.interpretFloat(sprite),
				DELTA);

		Formula lookAlphaValueFormula = getFormulaBySensor(Sensors.LOOK_GHOSTEFFECT);
		assertEquals("Variable shows false ghosteffect", SPRITE_GHOSTEFFECT,
				lookAlphaValueFormula.interpretFloat(sprite), DELTA);

		Formula lookBrightnessFormula = getFormulaBySensor(Sensors.LOOK_BRIGHTNESS);
		assertEquals("Variable shows false brightness", SPRITE_BRIGHTNESS,
				lookBrightnessFormula.interpretFloat(sprite), DELTA);

		Formula lookScaleFormula = getFormulaBySensor(Sensors.LOOK_SIZE);
		assertEquals("Variable shows false size", SPRITE_SIZE, lookScaleFormula.interpretFloat(sprite), DELTA);

		Formula lookRotateFormula = getFormulaBySensor(Sensors.LOOK_ROTATION);
		assertEquals("Variable shows false direction", SPRITE_DIRECTION, lookRotateFormula.interpretFloat(sprite),
				DELTA);

		Formula lookZPositionFormula = getFormulaBySensor(Sensors.LOOK_LAYER);
		assertEquals("Variable shows false z index", NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE,
				lookZPositionFormula.interpretInteger(sprite));

	}

	private void createProject() {
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

		sprite2 = new Sprite("sprite0");
		spriteList.add(sprite2);
		sprite2 = new Sprite("sprite1");
		spriteList.add(sprite2);
		sprite2 = new Sprite("sprite2");
		spriteList.add(sprite2);
		sprite2 = new Sprite("sprite3");
		spriteList.add(sprite2);
		sprite2 = new Sprite("sprite4");
		spriteList.add(sprite2);

		sprite = new Sprite("sprite5");
		StartScript startScript = new StartScript(sprite);

		SetXBrick setXBrick = new SetXBrick(sprite, SPRITE_X_POSITION);
		startScript.addBrick(setXBrick);
		sprite.addScript(startScript);

		SetYBrick setYBrick = new SetYBrick(sprite, SPRITE_Y_POSITION);
		startScript.addBrick(setYBrick);
		sprite.addScript(startScript);

		SetGhostEffectBrick setGhostEffectBrick = new SetGhostEffectBrick(sprite, SPRITE_GHOSTEFFECT);
		startScript.addBrick(setGhostEffectBrick);
		sprite.addScript(startScript);

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, SPRITE_BRIGHTNESS);
		startScript.addBrick(setBrightnessBrick);
		sprite.addScript(startScript);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, SPRITE_SIZE);
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);

		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, SPRITE_DIRECTION);
		startScript.addBrick(pointInDirectionBrick);
		sprite.addScript(startScript);

		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(sprite, SPRITE_LAYER_CHANGE);
		startScript.addBrick(goNStepsBackBrick);
		sprite.addScript(startScript);

		spriteList.add(sprite);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, null);
	}

}
