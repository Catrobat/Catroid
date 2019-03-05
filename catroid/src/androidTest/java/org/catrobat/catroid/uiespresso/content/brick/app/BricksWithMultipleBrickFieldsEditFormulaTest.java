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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class BricksWithMultipleBrickFieldsEditFormulaTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	private int arduinoDigitalPinNumber = 13;
	private int arduinoDigitalPinValue = 1;
	private int arduinoPwdPinNumber = 3;
	private int arduinoPwdPinValue = 255;
	private float raspiPwmFrequency = 50;
	private float raspiPwmPercentage = 100;
	private int raspiPwmPinNumber = 3;
	private int raspiPinValue = 1;
	private double legoDuration = 1.01;
	private double legoFrequency = 2.02;
	private double legoVolume = 100;
	private int droneDuration = 1;
	private int dronePower = 20;
	private int xCoordinate = 100;
	private int yCoordinate = 200;
	private double relativeTextSize = 100;
	private double droneDurationFormulaEditor = droneDuration / 1000.0;

	@Before
	public void setUp() throws Exception {
		createProject("editFormulaTest");
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testEditFormula() {
		checkFormulaEditorStartWithFirstBrickField(1, arduinoDigitalPinNumber + " ");
		checkFormulaEditorStartWithFirstBrickField(2, arduinoPwdPinNumber + " ");

		checkFormulaEditorStartWithFirstBrickField(3, raspiPwmPinNumber + " ");
		checkFormulaEditorStartWithFirstBrickField(4, raspiPwmPinNumber + " ");

		checkFormulaEditorStartWithFirstBrickField(5, legoDuration + " ");
		checkFormulaEditorStartWithFirstBrickField(6, legoDuration + " ");

		checkFormulaEditorStartWithFirstBrickField(7, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(8, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(9, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(10, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(11, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(12, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(13, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(14, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(15, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(16, droneDurationFormulaEditor + " ");

		checkFormulaEditorStartWithFirstBrickField(17, xCoordinate + " ");
		checkFormulaEditorStartWithFirstBrickField(18, xCoordinate + " ");
		checkFormulaEditorStartWithFirstBrickField(19, 1 + " ");
		checkFormulaEditorStartWithFirstBrickField(20, 1 + " ");

		checkFormulaEditorStartWithFirstBrickField(21, "'Servus' ");
		checkFormulaEditorStartWithFirstBrickField(22, "'Serwaas' ");
		checkFormulaEditorStartWithFirstBrickField(23, "'red' ");
		checkFormulaEditorStartWithFirstBrickField(24, "'r' ");

		checkFormulaEditorStartWithFirstBrickField(25, xCoordinate + " ");
		checkFormulaEditorStartWithFirstBrickField(26, droneDurationFormulaEditor + " ");
		checkFormulaEditorStartWithFirstBrickField(27, 1 + " ");
		checkFormulaEditorStartWithFirstBrickField(28, 5 + " ");
	}

	private void checkFormulaEditorStartWithFirstBrickField(int brickPosition, String shownText) {
		onBrickAtPosition(brickPosition)
				.performEditFormula();
		onFormulaEditor()
				.checkShows(shownText);
		onFormulaEditor()
				.performCloseAndSave();
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite1 = new Sprite("testSprite");
		Script sprite1StartScript = new StartScript();
		sprite1.addScript(sprite1StartScript);

		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);

		sprite1StartScript.addBrick(new ArduinoSendDigitalValueBrick(arduinoDigitalPinNumber, arduinoDigitalPinValue));
		sprite1StartScript.addBrick(new ArduinoSendPWMValueBrick(arduinoPwdPinNumber, arduinoPwdPinValue));

		sprite1StartScript.addBrick(new RaspiPwmBrick(raspiPwmPinNumber, raspiPwmFrequency, raspiPwmPercentage));
		sprite1StartScript.addBrick(new RaspiSendDigitalValueBrick(raspiPwmPinNumber, raspiPinValue));

		sprite1StartScript.addBrick(new LegoEv3PlayToneBrick(legoFrequency, legoDuration, legoVolume));
		sprite1StartScript.addBrick(new LegoNxtPlayToneBrick(legoFrequency, legoDuration));

		sprite1StartScript.addBrick(new DroneMoveUpBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneMoveDownBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneMoveLeftBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneMoveRightBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneMoveForwardBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneMoveBackwardBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneTurnLeftBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new DroneTurnRightBrick(droneDuration, dronePower));

		sprite1StartScript.addBrick(new JumpingSumoMoveBackwardBrick(droneDuration, dronePower));
		sprite1StartScript.addBrick(new JumpingSumoMoveForwardBrick(droneDuration, dronePower));

		sprite1StartScript.addBrick(new ShowTextBrick(xCoordinate, yCoordinate));
		sprite1StartScript.addBrick(new ShowTextColorSizeAlignmentBrick(xCoordinate, yCoordinate, relativeTextSize,
				"#FF00FF"));
		sprite1StartScript.addBrick(new InsertItemIntoUserListBrick(new Formula(1), new Formula(1)));
		sprite1StartScript.addBrick(new ReplaceItemInUserListBrick(new Formula(1), new Formula(1)));

		sprite1StartScript.addBrick(new SayForBubbleBrick("Servus", droneDuration));
		sprite1StartScript.addBrick(new ThinkForBubbleBrick("Serwaas", droneDuration));
		sprite1StartScript.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.RIGHT, new Formula("red"), new Formula(0), new Formula(255)));
		sprite1StartScript.addBrick(new SetPenColorBrick(new Formula("r"), new Formula(0), new Formula(255)));

		sprite1StartScript.addBrick(new PlaceAtBrick(xCoordinate, yCoordinate));
		sprite1StartScript.addBrick(new GlideToBrick(xCoordinate, yCoordinate, droneDuration));
		sprite1StartScript.addBrick(new SetVelocityBrick(new Formula(1), new Formula(3)));
		sprite1StartScript.addBrick(new SetGravityBrick(new Formula(5), new Formula(8)));
	}
}
