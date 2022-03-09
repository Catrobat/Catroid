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

package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBounceBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.SetGravityBrick;
import org.catrobat.catroid.content.bricks.SetMassBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class CloneBrickWithFormulaTest {

	private static final Integer BRICK_FORMULA_VALUE = 0;
	private static final String BRICK_INVALID_FORMULA_VALUE = "1";
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"SetBounceBrick", new SetBounceBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_BOUNCE_FACTOR},
				{"SetFrictionBrick", new SetFrictionBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_FRICTION},
				{"SetGravityBrickX", new SetGravityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_INVALID_FORMULA_VALUE)), Brick.BrickField.PHYSICS_GRAVITY_X},
				{"SetGravityBrickY", new SetGravityBrick(new Formula(BRICK_INVALID_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_GRAVITY_Y},
				{"SetMassBrick", new SetMassBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_MASS},
				{"SetVelocityBrickX", new SetVelocityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_INVALID_FORMULA_VALUE)), Brick.BrickField.PHYSICS_VELOCITY_X},
				{"SetVelocityBrickY", new SetVelocityBrick(new Formula(BRICK_INVALID_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_VELOCITY_Y},
				{"TurnRightSpeedBrick", new TurnRightSpeedBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_TURN_RIGHT_SPEED},
				{"TurnLeftSpeedBrick", new TurnLeftSpeedBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_TURN_LEFT_SPEED},
				{"ChangeBrightnessByNBrick", new ChangeBrightnessByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.BRIGHTNESS_CHANGE},
				{"ChangeTransparencyByNBrick", new ChangeTransparencyByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TRANSPARENCY_CHANGE},
				{"ChangeSizeByNBrick", new ChangeSizeByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.SIZE_CHANGE},
				{"ChangeVariableBrick", new ChangeVariableBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.VARIABLE_CHANGE},
				{"ChangeVolumeByNBrick", new ChangeVolumeByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.VOLUME_CHANGE},
				{"ChangeXByNBrick", new ChangeXByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.X_POSITION_CHANGE},
				{"ChangeYByNBrick", new ChangeYByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.Y_POSITION_CHANGE},
				{"GoNStepsBackBrick", new GoNStepsBackBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.STEPS},
				{"IfLogicBeginBrick", new IfLogicBeginBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.IF_CONDITION},
				{"LegoNxtMotorMoveBrick", new LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A, new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.LEGO_NXT_SPEED},
				{"LegoNxtMotorTurnAngleBrick", new LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.LEGO_NXT_DEGREES},
				{"LegoNxtPlayToneBrick Frequency", new LegoNxtPlayToneBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.LEGO_NXT_FREQUENCY},
				{"LegoNxtPlayToneBrick Duration", new LegoNxtPlayToneBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.LEGO_NXT_DURATION_IN_SECONDS},
				{"MoveNStepsBrick", new MoveNStepsBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.STEPS},
				{"RepeatBrick", new RepeatBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TIMES_TO_REPEAT},
				{"SetBrightnessBrick", new SetBrightnessBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.BRIGHTNESS},
				{"SetTransparencyBrick", new SetTransparencyBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TRANSPARENCY},
				{"SetColorBrick", new SetColorBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.COLOR},
				{"ChangeColorByNBrick", new ChangeColorByNBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.COLOR_CHANGE},
				{"SetSizeToBrick", new SetSizeToBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.SIZE},
				{"SetVariableBrick", new SetVariableBrick(BRICK_FORMULA_VALUE), Brick.BrickField.VARIABLE},
				{"SetVolumeToBrick", new SetVolumeToBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.VOLUME},
				{"SetXBrick", new SetXBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.X_POSITION},
				{"SetYBrick", new SetYBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.Y_POSITION},
				{"TurnLeftBrick", new TurnLeftBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TURN_LEFT_DEGREES},
				{"TurnRightBrick", new TurnRightBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TURN_RIGHT_DEGREES},
				{"VibrationBrick", new VibrationBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.VIBRATE_DURATION_IN_SECONDS},
				{"WaitBrick", new WaitBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.TIME_TO_WAIT_IN_SECONDS},
				{"PlaceAtBrick X", new PlaceAtBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.X_POSITION},
				{"PlaceAtBrick Y", new PlaceAtBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.Y_POSITION},
				{"GlideToBrick X", new GlideToBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.X_DESTINATION},
				{"GlideToBrick Y", new GlideToBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.Y_DESTINATION},
				{"GlideToBrick Duration", new GlideToBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE), Brick.BrickField.DURATION_IN_SECONDS},
				{"NoteBrick", new NoteBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.NOTE},
				{"SpeakBrick", new SpeakBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.SPEAK},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public FormulaBrick brick;

	@Parameterized.Parameter(2)
	public Brick.BrickField brickField;

	private Sprite sprite = new Sprite("testSprite");
	private Formula brickFormula;
	private Formula cloneBrickFormula;
	private Scope scope;

	@Before
	public void setUp() throws CloneNotSupportedException {
		initializeStaticSingletonMethods(dependencyModules);
		FormulaBrick cloneBrick = (FormulaBrick) brick.clone();
		brickFormula = brick.getFormulaWithBrickField(brickField);
		cloneBrickFormula = cloneBrick.getFormulaWithBrickField(brickField);
		scope = new Scope(projectManager.getValue().getCurrentProject(), sprite, new SequenceAction());
	}

	@Test
	public void testChangeBrickField() throws InterpretationException {
		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotEquals(brickFormula.interpretInteger(scope),
				cloneBrickFormula.interpretInteger(scope));
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testBrickFieldValidValue() throws InterpretationException {
		assertEquals(BRICK_FORMULA_VALUE, brickFormula.interpretInteger(scope));
	}

	@Test
	public void testBrickFieldEquals() throws InterpretationException {
		assertEquals(brickFormula.interpretInteger(scope), cloneBrickFormula.interpretInteger(scope));
	}
}
