/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.stage;

import android.util.Log;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
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
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjectVariableTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Sprite sprite;
	private static final double DELTA = 0.01d;

	private static final double SPRITE_X_POSITION_INITIAL = 0.0d;
	private static final double SPRITE_Y_POSITION_INITIAL = 0.0d;
	private static final double SPRITE_TRANSPARENCY_INITIAL = 0.0d;
	private static final double SPRITE_BRIGHTNESS_INITIAL = 100.0d;
	private static final double SPRITE_SIZE_INITIAL = 100.0d;
	private static final double SPRITE_DIRECTION_INITIAL = 90.0d;
	private static final int NUMBER_OF_SPRITES_INITIAL = -1;

	private static final double SPRITE_X_POSITION = 30.0d;
	private static final double SPRITE_Y_POSITION = 50.0d;
	private static final double SPRITE_TRANSPARENCY = 0.8d;
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
		createProject();
		UiTestUtils.prepareStageForTest();
	}

	public Double interpretSensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula sensorFormula = new Formula(parseTree);
		try {
			return sensorFormula.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for Sensor failed.", interpretationException);
		}
		return Double.NaN;
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

		solo.sleep(2000);

		assertEquals("Variable shows false x position", SPRITE_X_POSITION, interpretSensor(Sensors.OBJECT_X), DELTA);
		assertEquals("Variable shows false y position", SPRITE_Y_POSITION, interpretSensor(Sensors.OBJECT_Y), DELTA);
		assertEquals("Variable shows false transparency", SPRITE_TRANSPARENCY, interpretSensor(Sensors.OBJECT_TRANSPARENCY), DELTA);
		assertEquals("Variable shows false brightness", SPRITE_BRIGHTNESS, interpretSensor(Sensors.OBJECT_BRIGHTNESS), DELTA);
		assertEquals("Variable shows false size", SPRITE_SIZE, interpretSensor(Sensors.OBJECT_SIZE), DELTA);
		assertEquals("Variable shows false direction", SPRITE_DIRECTION, interpretSensor(Sensors.OBJECT_ROTATION), DELTA);
		assertEquals("Variable shows false z index", NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE, interpretSensor(Sensors.OBJECT_LAYER), DELTA);
	}

	public void testLookSensorValueBeforeAndAfterStage() {

		assertEquals("Variable shows false x position before stage", SPRITE_X_POSITION_INITIAL,
				interpretSensor(Sensors.OBJECT_X), DELTA);
		assertEquals("Variable shows false y position before stage", SPRITE_Y_POSITION_INITIAL,
				interpretSensor(Sensors.OBJECT_Y), DELTA);
		assertEquals("Variable shows false transparency before stage", SPRITE_TRANSPARENCY_INITIAL,
				interpretSensor(Sensors.OBJECT_TRANSPARENCY), DELTA);
		assertEquals("Variable shows false brightness before stage", SPRITE_BRIGHTNESS_INITIAL,
				interpretSensor(Sensors.OBJECT_BRIGHTNESS), DELTA);
		assertEquals("Variable shows false size before stage", SPRITE_SIZE_INITIAL,
				interpretSensor(Sensors.OBJECT_SIZE), DELTA);
		assertEquals("Variable shows false direction before stage", SPRITE_DIRECTION_INITIAL,
				interpretSensor(Sensors.OBJECT_ROTATION), DELTA);
		assertEquals("Variable shows false z index before stage", NUMBER_OF_SPRITES_INITIAL,
				interpretSensor(Sensors.OBJECT_LAYER), DELTA);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);
		solo.clickOnButton(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		solo.sleep(1000);

		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);

		assertEquals("Variable shows false x position after Stage", SPRITE_X_POSITION,
				interpretSensor(Sensors.OBJECT_X), DELTA);
		assertEquals("Variable shows false y position after Stage", SPRITE_Y_POSITION,
				interpretSensor(Sensors.OBJECT_Y), DELTA);
		assertEquals("Variable shows false transparency after Stage", SPRITE_TRANSPARENCY,
				interpretSensor(Sensors.OBJECT_TRANSPARENCY), DELTA);
		assertEquals("Variable shows false brightness after Stage", SPRITE_BRIGHTNESS,
				interpretSensor(Sensors.OBJECT_BRIGHTNESS), DELTA);
		assertEquals("Variable shows false size after Stage", SPRITE_SIZE,
				interpretSensor(Sensors.OBJECT_SIZE), DELTA);
		assertEquals("Variable shows false direction after Stage", SPRITE_DIRECTION,
				interpretSensor(Sensors.OBJECT_ROTATION), DELTA);
		assertEquals("Variable shows false z index after Stage", NUMBER_OF_SPRITES - SPRITE_LAYER_CHANGE,
				interpretSensor(Sensors.OBJECT_LAYER), DELTA);
	}

	private void createProject() {
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

		spriteList.add(new Sprite("background"));
		spriteList.add(new Sprite("sprite1"));
		spriteList.add(new Sprite("sprite2"));
		spriteList.add(new Sprite("sprite3"));
		spriteList.add(new Sprite("sprite4"));

		sprite = new Sprite("sprite5");
		StartScript startScript = new StartScript();

		SetXBrick setXBrick = new SetXBrick((int) SPRITE_X_POSITION);
		startScript.addBrick(setXBrick);
		sprite.addScript(startScript);

		SetYBrick setYBrick = new SetYBrick((int) SPRITE_Y_POSITION);
		startScript.addBrick(setYBrick);
		sprite.addScript(startScript);

		SetTransparencyBrick setTransparencyBrick = new SetTransparencyBrick(SPRITE_TRANSPARENCY);
		startScript.addBrick(setTransparencyBrick);
		sprite.addScript(startScript);

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(SPRITE_BRIGHTNESS);
		startScript.addBrick(setBrightnessBrick);
		sprite.addScript(startScript);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(SPRITE_SIZE);
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);

		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(SPRITE_DIRECTION);
		startScript.addBrick(pointInDirectionBrick);
		sprite.addScript(startScript);

		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(SPRITE_LAYER_CHANGE);
		startScript.addBrick(goNStepsBackBrick);
		sprite.addScript(startScript);

		spriteList.add(sprite);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, null);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
