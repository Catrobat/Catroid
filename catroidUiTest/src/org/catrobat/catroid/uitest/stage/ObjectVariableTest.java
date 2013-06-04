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

import org.catrobat.catroid.R;
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
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class ObjectVariableTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private Sprite sprite;
	private static final double DELTA = 0.01d;

	private static final double SPRITE_X_POSITION_INITIAL = 0.0d;
	private static final double SPRITE_Y_POSITION_INITIAL = 0.0d;
	private static final double SPRITE_GHOSTEFFECT_INITIAL = 0.0d;
	private static final double SPRITE_BRIGHTNESS_INITIAL = 100.0d;
	private static final double SPRITE_SIZE_INITIAL = 100.0d;
	private static final double SPRITE_DIRECTION_INITIAL = 90.0d;
	private static final int NUMBER_OF_SPRITES_INITIAL = -1;

	private static final double SPRITE_X_POSITION = 30.0d;
	private static final double SPRITE_Y_POSITION = 50.0d;
	private static final double SPRITE_GHOSTEFFECT = 0.8d;
	private static final double SPRITE_BRIGHTNESS = 0.7d;
	private static final double SPRITE_SIZE = 90.0d;
	private static final double SPRITE_DIRECTION = 42.0d;
	private static final int SPRITE_LAYER_CHANGE = 2;
	private static final int NUMBER_OF_SPRITES = 5;

	public ObjectVariableTest() {
		super(MainMenuActivity.class);
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

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);
		solo.clickOnButton(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(1000);

		Formula lookXPositionFormula = getFormulaBySensor(Sensors.OBJECT_X);
		assertEquals("Variable shows false x position", SPRITE_X_POSITION,
				lookXPositionFormula.interpretDouble(sprite), DELTA);

		Formula lookYPositionFormula = getFormulaBySensor(Sensors.OBJECT_Y);
		assertEquals("Variable shows false y position", SPRITE_Y_POSITION,
				lookYPositionFormula.interpretDouble(sprite), DELTA);

		Formula lookAlphaValueFormula = getFormulaBySensor(Sensors.OBJECT_GHOSTEFFECT);
		assertEquals("Variable shows false ghosteffect", SPRITE_GHOSTEFFECT,
				lookAlphaValueFormula.interpretDouble(sprite), DELTA);

		Formula lookBrightnessFormula = getFormulaBySensor(Sensors.OBJECT_BRIGHTNESS);
		assertEquals("Variable shows false brightness", SPRITE_BRIGHTNESS,
				lookBrightnessFormula.interpretDouble(sprite), DELTA);

		Formula lookScaleFormula = getFormulaBySensor(Sensors.OBJECT_SIZE);
		assertEquals("Variable shows false size", SPRITE_SIZE, lookScaleFormula.interpretDouble(sprite), DELTA);

		Formula lookRotateFormula = getFormulaBySensor(Sensors.OBJECT_ROTATION);
		assertEquals("Variable shows false direction", SPRITE_DIRECTION, lookRotateFormula.interpretDouble(sprite),
				DELTA);

		Formula lookZPositionFormula = getFormulaBySensor(Sensors.OBJECT_LAYER);
		assertEquals("Variable shows false z index", NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE,
				lookZPositionFormula.interpretInteger(sprite), DELTA);

	}

	public void testLookSensorValueBeforeAndAfterStage() {

		Formula lookXPositionFormula = getFormulaBySensor(Sensors.OBJECT_X);
		assertEquals("Variable shows false x position before stage", SPRITE_X_POSITION_INITIAL,
				lookXPositionFormula.interpretDouble(sprite), DELTA);

		Formula lookYPositionFormula = getFormulaBySensor(Sensors.OBJECT_Y);
		assertEquals("Variable shows false y position before stage", SPRITE_Y_POSITION_INITIAL,
				lookYPositionFormula.interpretDouble(sprite), DELTA);

		Formula lookAlphaValueFormula = getFormulaBySensor(Sensors.OBJECT_GHOSTEFFECT);
		assertEquals("Variable shows false ghosteffect before stage", SPRITE_GHOSTEFFECT_INITIAL,
				lookAlphaValueFormula.interpretDouble(sprite), DELTA);

		Formula lookBrightnessFormula = getFormulaBySensor(Sensors.OBJECT_BRIGHTNESS);
		assertEquals("Variable shows false brightness before stage", SPRITE_BRIGHTNESS_INITIAL,
				lookBrightnessFormula.interpretDouble(sprite), DELTA);

		Formula lookScaleFormula = getFormulaBySensor(Sensors.OBJECT_SIZE);
		assertEquals("Variable shows false size before stage", SPRITE_SIZE_INITIAL,
				lookScaleFormula.interpretDouble(sprite), DELTA);

		Formula lookRotateFormula = getFormulaBySensor(Sensors.OBJECT_ROTATION);
		assertEquals("Variable shows false direction before stage", SPRITE_DIRECTION_INITIAL,
				lookRotateFormula.interpretDouble(sprite), DELTA);

		Formula lookZPositionFormula = getFormulaBySensor(Sensors.OBJECT_LAYER);
		assertEquals("Variable shows false z index before stage", NUMBER_OF_SPRITES_INITIAL,
				lookZPositionFormula.interpretInteger(sprite), DELTA);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);
		solo.clickOnButton(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(1000);

		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);

		lookXPositionFormula = getFormulaBySensor(Sensors.OBJECT_X);
		assertEquals("Variable shows false x position after Stage", SPRITE_X_POSITION,
				lookXPositionFormula.interpretDouble(sprite), DELTA);

		lookYPositionFormula = getFormulaBySensor(Sensors.OBJECT_Y);
		assertEquals("Variable shows false y position after Stage", SPRITE_Y_POSITION,
				lookYPositionFormula.interpretDouble(sprite), DELTA);

		lookAlphaValueFormula = getFormulaBySensor(Sensors.OBJECT_GHOSTEFFECT);
		assertEquals("Variable shows false ghosteffect after Stage", SPRITE_GHOSTEFFECT,
				lookAlphaValueFormula.interpretDouble(sprite), DELTA);

		lookBrightnessFormula = getFormulaBySensor(Sensors.OBJECT_BRIGHTNESS);
		assertEquals("Variable shows false brightness after Stage", SPRITE_BRIGHTNESS,
				lookBrightnessFormula.interpretDouble(sprite), DELTA);

		lookScaleFormula = getFormulaBySensor(Sensors.OBJECT_SIZE);
		assertEquals("Variable shows false size after Stage", SPRITE_SIZE, lookScaleFormula.interpretDouble(sprite),
				DELTA);

		lookRotateFormula = getFormulaBySensor(Sensors.OBJECT_ROTATION);
		assertEquals("Variable shows false direction after Stage", SPRITE_DIRECTION,
				lookRotateFormula.interpretDouble(sprite), DELTA);

		lookZPositionFormula = getFormulaBySensor(Sensors.OBJECT_LAYER);
		assertEquals("Variable shows false z index after Stage", NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE,
				lookZPositionFormula.interpretInteger(sprite), DELTA);

	}

	private void createProject() {
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

		spriteList.add(new Sprite("background"));
		spriteList.add(new Sprite("sprite1"));
		spriteList.add(new Sprite("sprite2"));
		spriteList.add(new Sprite("sprite3"));
		spriteList.add(new Sprite("sprite4"));

		sprite = new Sprite("sprite5");
		StartScript startScript = new StartScript(sprite);

		SetXBrick setXBrick = new SetXBrick(sprite, (int) SPRITE_X_POSITION);
		startScript.addBrick(setXBrick);
		sprite.addScript(startScript);

		SetYBrick setYBrick = new SetYBrick(sprite, (int) SPRITE_Y_POSITION);
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
