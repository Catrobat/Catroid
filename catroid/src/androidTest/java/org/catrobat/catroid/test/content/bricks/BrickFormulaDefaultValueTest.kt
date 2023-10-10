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
package org.catrobat.catroid.test.content.bricks

import android.content.Context
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.bricks.Brick
import junit.framework.TestCase
import org.catrobat.catroid.content.bricks.Brick.FormulaField
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.junit.Test
import java.lang.Exception
import java.util.Arrays

@RunWith(Parameterized::class)
class BrickFormulaDefaultValueTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var category: String? = null
    @JvmField
    @Parameterized.Parameter(2)
    var brickClazz: Class<*>? = null
    @JvmField
    @Parameterized.Parameter(3)
    var formulaTextFieldId = 0
    @JvmField
    @Parameterized.Parameter(4)
    var expected: String? = null
    private var categoryBricksFactory: CategoryBricksFactory? = null
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(ApplicationProvider.getApplicationContext())
        categoryBricksFactory = CategoryBricksFactory()
    }

    fun createProject(context: Context?) {
        val project = Project(context, javaClass.simpleName)
        sprite = Sprite("testSprite")
        val script: Script = StartScript()
        script.addBrick(SetXBrick())
        sprite!!.addScript(script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    @Test
    fun testBrickCategory() {
        val categoryBricks = categoryBricksFactory!!.getBricks(
            category!!, false,
            ApplicationProvider.getApplicationContext()
        )
        var brickInAdapter: Brick? = null
        for (brick in categoryBricks) {
            if (brickClazz!!.isInstance(brick)) {
                brickInAdapter = brick
                break
            }
        }
        TestCase.assertNotNull(brickInAdapter)
        val brickField =
            (brickInAdapter as FormulaBrick?)!!.brickFieldToTextViewIdMap.inverse()[formulaTextFieldId]
        val actual = brickInAdapter!!.getFormulaWithBrickField(brickField)
            .getTrimmedFormulaString(ApplicationProvider.getApplicationContext())
        Assert.assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "PhiroMotorMoveBackwardBrick - R.id.brick_phiro_motor_backward_action_speed_edit_text",
                        "Phiro",
                        PhiroMotorMoveBackwardBrick::class.java,
                        R.id.brick_phiro_motor_backward_action_speed_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "PhiroPlayToneBrick - R.id.brick_phiro_play_tone_duration_edit_text",
                        "Phiro",
                        PhiroPlayToneBrick::class.java,
                        R.id.brick_phiro_play_tone_duration_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "PhiroRGBLightBrick - R.id.brick_phiro_rgb_led_action_green_edit_text",
                        "Phiro",
                        PhiroRGBLightBrick::class.java,
                        R.id.brick_phiro_rgb_led_action_green_edit_text,
                        "255 "
                    ),
                    arrayOf(
                        "PhiroRGBLightBrick - R.id.brick_phiro_rgb_led_action_red_edit_text",
                        "Phiro",
                        PhiroRGBLightBrick::class.java,
                        R.id.brick_phiro_rgb_led_action_red_edit_text,
                        "0 "
                    ),
                    arrayOf(
                        "PhiroRGBLightBrick - R.id.brick_phiro_rgb_led_action_blue_edit_text",
                        "Phiro",
                        PhiroRGBLightBrick::class.java,
                        R.id.brick_phiro_rgb_led_action_blue_edit_text,
                        "255 "
                    ),
                    arrayOf(
                        "ArduinoSendDigitalValueBrick - R.id.brick_arduino_set_digital_pin_edit_text",
                        "Arduino",
                        ArduinoSendDigitalValueBrick::class.java,
                        R.id.brick_arduino_set_digital_pin_edit_text,
                        "13 "
                    ),
                    arrayOf(
                        "ArduinoSendDigitalValueBrick - R.id.brick_arduino_set_digital_value_edit_text",
                        "Arduino",
                        ArduinoSendDigitalValueBrick::class.java,
                        R.id.brick_arduino_set_digital_value_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "ArduinoSendPWMValueBrick - R.id.brick_arduino_set_analog_pin_edit_text",
                        "Arduino",
                        ArduinoSendPWMValueBrick::class.java,
                        R.id.brick_arduino_set_analog_pin_edit_text,
                        "3 "
                    ),
                    arrayOf(
                        "ArduinoSendPWMValueBrick - R.id.brick_arduino_set_analog_value_edit_text",
                        "Arduino",
                        ArduinoSendPWMValueBrick::class.java,
                        R.id.brick_arduino_set_analog_value_edit_text,
                        "255 "
                    ),
                    arrayOf(
                        "RaspiIfLogicBeginBrick - R.id.brick_if_begin_edit_text",
                        "Raspberry Pi",
                        RaspiIfLogicBeginBrick::class.java,
                        R.id.brick_if_begin_edit_text,
                        "3 "
                    ),
                    arrayOf(
                        "RaspiSendDigitalValueBrick - R.id.brick_raspi_set_digital_pin_edit_text",
                        "Raspberry Pi",
                        RaspiSendDigitalValueBrick::class.java,
                        R.id.brick_raspi_set_digital_pin_edit_text,
                        "3 "
                    ),
                    arrayOf(
                        "RaspiSendDigitalValueBrick - R.id.brick_raspi_set_digital_value_edit_text",
                        "Raspberry Pi",
                        RaspiSendDigitalValueBrick::class.java,
                        R.id.brick_raspi_set_digital_value_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "RaspiPwmBrick - R.id.brick_raspi_pwm_pin_edit_text",
                        "Raspberry Pi",
                        RaspiPwmBrick::class.java,
                        R.id.brick_raspi_pwm_pin_edit_text,
                        "3 "
                    ),
                    arrayOf(
                        "RaspiPwmBrick - R.id.brick_raspi_pwm_frequency_edit_text",
                        "Raspberry Pi",
                        RaspiPwmBrick::class.java,
                        R.id.brick_raspi_pwm_frequency_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "RaspiPwmBrick - R.id.brick_raspi_pwm_percentage_edit_text",
                        "Raspberry Pi",
                        RaspiPwmBrick::class.java,
                        R.id.brick_raspi_pwm_percentage_edit_text,
                        "50 "
                    ),
                    arrayOf(
                        "SetXBrick - R.id.brick_set_x_edit_text",
                        "Motion",
                        SetXBrick::class.java,
                        R.id.brick_set_x_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "SetYBrick - R.id.brick_set_y_edit_text",
                        "Motion",
                        SetYBrick::class.java,
                        R.id.brick_set_y_edit_text,
                        "200 "
                    ),
                    arrayOf(
                        "ChangeXByNBrick - R.id.brick_change_x_edit_text",
                        "Motion",
                        ChangeXByNBrick::class.java,
                        R.id.brick_change_x_edit_text,
                        "10 "
                    ),
                    arrayOf(
                        "ChangeYByNBrick - R.id.brick_change_y_edit_text",
                        "Motion",
                        ChangeYByNBrick::class.java,
                        R.id.brick_change_y_edit_text,
                        "10 "
                    ),
                    arrayOf(
                        "MoveNStepsBrick - R.id.brick_move_n_steps_edit_text",
                        "Motion",
                        MoveNStepsBrick::class.java,
                        R.id.brick_move_n_steps_edit_text,
                        "10 "
                    ),
                    arrayOf(
                        "TurnLeftBrick - R.id.brick_turn_left_edit_text",
                        "Motion",
                        TurnLeftBrick::class.java,
                        R.id.brick_turn_left_edit_text,
                        "15 "
                    ),
                    arrayOf(
                        "TurnRightBrick - R.id.brick_turn_right_edit_text",
                        "Motion",
                        TurnRightBrick::class.java,
                        R.id.brick_turn_right_edit_text,
                        "15 "
                    ),
                    arrayOf(
                        "PointInDirectionBrick - R.id.brick_point_in_direction_edit_text",
                        "Motion",
                        PointInDirectionBrick::class.java,
                        R.id.brick_point_in_direction_edit_text,
                        "90 "
                    ),
                    arrayOf(
                        "GlideToBrick - R.id.brick_glide_to_edit_text_x",
                        "Motion",
                        GlideToBrick::class.java,
                        R.id.brick_glide_to_edit_text_x,
                        "100 "
                    ),
                    arrayOf(
                        "GlideToBrick - R.id.brick_glide_to_edit_text_y",
                        "Motion",
                        GlideToBrick::class.java,
                        R.id.brick_glide_to_edit_text_y,
                        "200 "
                    ),
                    arrayOf(
                        "GlideToBrick - R.id.brick_glide_to_edit_text_duration",
                        "Motion",
                        GlideToBrick::class.java,
                        R.id.brick_glide_to_edit_text_duration,
                        "1 "
                    ),
                    arrayOf(
                        "GoNStepsBackBrick - R.id.brick_go_back_edit_text",
                        "Motion",
                        GoNStepsBackBrick::class.java,
                        R.id.brick_go_back_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "VibrationBrick - R.id.brick_vibration_edit_text",
                        "Motion",
                        VibrationBrick::class.java,
                        R.id.brick_vibration_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "TurnLeftBrick - R.id.brick_turn_left_edit_text",
                        "Motion",
                        TurnLeftBrick::class.java,
                        R.id.brick_turn_left_edit_text,
                        "15 "
                    ),
                    arrayOf(
                        "TurnRightBrick - R.id.brick_turn_right_edit_text",
                        "Motion",
                        TurnRightBrick::class.java,
                        R.id.brick_turn_right_edit_text,
                        "15 "
                    ),
                    arrayOf(
                        "SetGravityBrick - R.id.brick_set_gravity_edit_text_x",
                        "Motion",
                        SetGravityBrick::class.java,
                        R.id.brick_set_gravity_edit_text_x,
                        "0 "
                    ),
                    arrayOf(
                        "SetGravityBrick, R.id.brick_set_gravity_edit_text_y",
                        "Motion",
                        SetGravityBrick::class.java,
                        R.id.brick_set_gravity_edit_text_y,
                        "- 10 "
                    ),
                    arrayOf(
                        "SetMassBrick - R.id.brick_set_mass_edit_text",
                        "Motion",
                        SetMassBrick::class.java,
                        R.id.brick_set_mass_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "SetBounceBrick - R.id.brick_set_bounce_factor_edit_text",
                        "Motion",
                        SetBounceBrick::class.java,
                        R.id.brick_set_bounce_factor_edit_text,
                        "80 "
                    ),
                    arrayOf(
                        "SetFrictionBrick - R.id.brick_set_friction_edit_text",
                        "Motion",
                        SetFrictionBrick::class.java,
                        R.id.brick_set_friction_edit_text,
                        "20 "
                    ),
                    arrayOf(
                        "SetSizeToBrick - R.id.brick_set_size_to_edit_text",
                        "Looks",
                        SetSizeToBrick::class.java,
                        R.id.brick_set_size_to_edit_text,
                        "60 "
                    ),
                    arrayOf(
                        "ChangeSizeByNBrick - R.id.brick_change_size_by_edit_text",
                        "Looks",
                        ChangeSizeByNBrick::class.java,
                        R.id.brick_change_size_by_edit_text,
                        "10 "
                    ),
                    arrayOf(
                        "AskBrick - R.id.brick_ask_question_edit_text",
                        "Looks",
                        AskBrick::class.java,
                        R.id.brick_ask_question_edit_text,
                        "'What's your name?' "
                    ),
                    arrayOf(
                        "SayBubbleBrick - R.id.brick_bubble_edit_text",
                        "Looks",
                        SayBubbleBrick::class.java,
                        R.id.brick_bubble_edit_text,
                        "'Hello!' "
                    ),
                    arrayOf(
                        "SayForBubbleBrick - R.id.brick_for_bubble_edit_text_text",
                        "Looks",
                        SayForBubbleBrick::class.java,
                        R.id.brick_for_bubble_edit_text_text,
                        "'Hello!' "
                    ),
                    arrayOf(
                        "ThinkBubbleBrick - R.id.brick_bubble_edit_text",
                        "Looks",
                        ThinkBubbleBrick::class.java,
                        R.id.brick_bubble_edit_text,
                        "'Hello!' "
                    ),
                    arrayOf(
                        "ThinkForBubbleBrick - R.id.brick_for_bubble_edit_text_text",
                        "Looks",
                        ThinkForBubbleBrick::class.java,
                        R.id.brick_for_bubble_edit_text_text,
                        "'Hello!' "
                    ),
                    arrayOf(
                        "SetTransparencyBrick - R.id.brick_set_transparency_to_edit_text",
                        "Looks",
                        SetTransparencyBrick::class.java,
                        R.id.brick_set_transparency_to_edit_text,
                        "50 "
                    ),
                    arrayOf(
                        "ChangeTransparencyByNBrick - R.id.brick_change_transparency_edit_text",
                        "Looks",
                        ChangeTransparencyByNBrick::class.java,
                        R.id.brick_change_transparency_edit_text,
                        "25 "
                    ),
                    arrayOf(
                        "SetBrightnessBrick - R.id.brick_set_brightness_edit_text",
                        "Looks",
                        SetBrightnessBrick::class.java,
                        R.id.brick_set_brightness_edit_text,
                        "50 "
                    ),
                    arrayOf(
                        "ChangeBrightnessByNBrick - R.id.brick_change_brightness_edit_text",
                        "Looks",
                        ChangeBrightnessByNBrick::class.java,
                        R.id.brick_change_brightness_edit_text,
                        "25 "
                    ),
                    arrayOf(
                        "SetColorBrick - R.id.brick_set_color_edit_text",
                        "Looks",
                        SetColorBrick::class.java,
                        R.id.brick_set_color_edit_text,
                        "0 "
                    ),
                    arrayOf(
                        "ChangeColorByNBrick - R.id.brick_change_color_by_edit_text",
                        "Looks",
                        ChangeColorByNBrick::class.java,
                        R.id.brick_change_color_by_edit_text,
                        "25 "
                    ),
                    arrayOf(
                        "SetVariableBrick - R.id.brick_set_variable_edit_text",
                        "Data",
                        SetVariableBrick::class.java,
                        R.id.brick_set_variable_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "ChangeVariableBrick - R.id.brick_change_variable_edit_text",
                        "Data",
                        ChangeVariableBrick::class.java,
                        R.id.brick_change_variable_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "ShowTextBrick - R.id.brick_show_variable_edit_text_x",
                        "Data",
                        ShowTextBrick::class.java,
                        R.id.brick_show_variable_edit_text_x,
                        "100 "
                    ),
                    arrayOf(
                        "ShowTextBrick - R.id.brick_show_variable_edit_text_y",
                        "Data",
                        ShowTextBrick::class.java,
                        R.id.brick_show_variable_edit_text_y,
                        "200 "
                    ),
                    arrayOf(
                        "AddItemToUserListBrick - R.id.brick_add_item_to_userlist_edit_text",
                        "Data",
                        AddItemToUserListBrick::class.java,
                        R.id.brick_add_item_to_userlist_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "DeleteItemOfUserListBrick - R.id.brick_delete_item_of_userlist_edit_text",
                        "Data",
                        DeleteItemOfUserListBrick::class.java,
                        R.id.brick_delete_item_of_userlist_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "LegoNxtMotorTurnAngleBrick - R.id.motor_turn_angle_edit_text",
                        "Lego NXT",
                        LegoNxtMotorTurnAngleBrick::class.java,
                        R.id.motor_turn_angle_edit_text,
                        "180 "
                    ),
                    arrayOf(
                        "LegoNxtMotorMoveBrick - R.id.motor_action_speed_edit_text",
                        "Lego NXT",
                        LegoNxtMotorMoveBrick::class.java,
                        R.id.motor_action_speed_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "LegoNxtPlayToneBrick - R.id.nxt_tone_freq_edit_text",
                        "Lego NXT",
                        LegoNxtPlayToneBrick::class.java,
                        R.id.nxt_tone_freq_edit_text,
                        "2 "
                    ),
                    arrayOf(
                        "LegoNxtPlayToneBrick - R.id.nxt_tone_duration_edit_text",
                        "Lego NXT",
                        LegoNxtPlayToneBrick::class.java,
                        R.id.nxt_tone_duration_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "LegoEv3MotorTurnAngleBrick - R.id.ev3_motor_turn_angle_edit_text",
                        "Lego EV3",
                        LegoEv3MotorTurnAngleBrick::class.java,
                        R.id.ev3_motor_turn_angle_edit_text,
                        "180 "
                    ),
                    arrayOf(
                        "LegoEv3MotorMoveBrick - R.id.ev3_motor_move_speed_edit_text",
                        "Lego EV3",
                        LegoEv3MotorMoveBrick::class.java,
                        R.id.ev3_motor_move_speed_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "LegoEv3PlayToneBrick - R.id.brick_ev3_tone_freq_edit_text",
                        "Lego EV3",
                        LegoEv3PlayToneBrick::class.java,
                        R.id.brick_ev3_tone_freq_edit_text,
                        "2 "
                    ),
                    arrayOf(
                        "LegoEv3PlayToneBrick - R.id.brick_ev3_tone_duration_edit_text",
                        "Lego EV3",
                        LegoEv3PlayToneBrick::class.java,
                        R.id.brick_ev3_tone_duration_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "LegoEv3PlayToneBrick - R.id.brick_ev3_tone_volume_edit_text",
                        "Lego EV3",
                        LegoEv3PlayToneBrick::class.java,
                        R.id.brick_ev3_tone_volume_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "DroneMoveUpBrick - R.id.brick_drone_move_up_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveUpBrick::class.java,
                        R.id.brick_drone_move_up_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveUpBrick - R.id.brick_drone_move_up_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveUpBrick::class.java,
                        R.id.brick_drone_move_up_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneMoveDownBrick - R.id.brick_drone_move_down_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveDownBrick::class.java,
                        R.id.brick_drone_move_down_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveDownBrick - R.id.brick_drone_move_down_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveDownBrick::class.java,
                        R.id.brick_drone_move_down_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneMoveLeftBrick - R.id.brick_drone_move_left_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveLeftBrick::class.java,
                        R.id.brick_drone_move_left_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveLeftBrick - R.id.brick_drone_move_left_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveLeftBrick::class.java,
                        R.id.brick_drone_move_left_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneMoveRightBrick - R.id.brick_drone_move_right_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveRightBrick::class.java,
                        R.id.brick_drone_move_right_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveRightBrick - R.id.brick_drone_move_right_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveRightBrick::class.java,
                        R.id.brick_drone_move_right_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneMoveForwardBrick - R.id.brick_drone_move_forward_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveForwardBrick::class.java,
                        R.id.brick_drone_move_forward_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveForwardBrick - R.id.brick_drone_move_forward_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveForwardBrick::class.java,
                        R.id.brick_drone_move_forward_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneMoveBackwardBrick - R.id.brick_drone_move_backward_edit_text_second",
                        "AR.Drone 2.0",
                        DroneMoveBackwardBrick::class.java,
                        R.id.brick_drone_move_backward_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneMoveBackwardBrick - R.id.brick_drone_move_backward_edit_text_power",
                        "AR.Drone 2.0",
                        DroneMoveBackwardBrick::class.java,
                        R.id.brick_drone_move_backward_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneTurnLeftBrick - R.id.brick_drone_turn_left_edit_text_second",
                        "AR.Drone 2.0",
                        DroneTurnLeftBrick::class.java,
                        R.id.brick_drone_turn_left_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneTurnLeftBrick - R.id.brick_drone_turn_left_edit_text_power",
                        "AR.Drone 2.0",
                        DroneTurnLeftBrick::class.java,
                        R.id.brick_drone_turn_left_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "DroneTurnRightBrick - R.id.brick_drone_turn_right_edit_text_second",
                        "AR.Drone 2.0",
                        DroneTurnRightBrick::class.java,
                        R.id.brick_drone_turn_right_edit_text_second,
                        "1 "
                    ),
                    arrayOf(
                        "DroneTurnRightBrick - R.id.brick_drone_turn_right_edit_text_power",
                        "AR.Drone 2.0",
                        DroneTurnRightBrick::class.java,
                        R.id.brick_drone_turn_right_edit_text_power,
                        "20 "
                    ),
                    arrayOf(
                        "PhiroMotorMoveForwardBrick - R.id.brick_phiro_motor_forward_action_speed_edit_text",
                        "Phiro",
                        PhiroMotorMoveForwardBrick::class.java,
                        R.id.brick_phiro_motor_forward_action_speed_edit_text,
                        "100 "
                    ),
                    arrayOf(
                        "WhenConditionBrick - R.id.brick_when_condition_edit_text",
                        "Event",
                        WhenConditionBrick::class.java,
                        R.id.brick_when_condition_edit_text,
                        "1 < 2 "
                    ),
                    arrayOf(
                        "IfLogicBeginBrick - R.id.brick_if_begin_edit_text",
                        "Control",
                        IfLogicBeginBrick::class.java,
                        R.id.brick_if_begin_edit_text,
                        "1 < 2 "
                    ),
                    arrayOf(
                        "IfThenLogicBeginBrick - R.id.brick_if_begin_edit_text",
                        "Control",
                        IfThenLogicBeginBrick::class.java,
                        R.id.brick_if_begin_edit_text,
                        "1 < 2 "
                    ),
                    arrayOf(
                        "WaitBrick - R.id.brick_wait_edit_text",
                        "Control",
                        WaitBrick::class.java,
                        R.id.brick_wait_edit_text,
                        "1 "
                    ),
                    arrayOf(
                        "NoteBrick - R.id.brick_wait_edit_text",
                        "Control",
                        NoteBrick::class.java,
                        R.id.brick_note_edit_text,
                        "'add comment here…' "
                    ),
                    arrayOf(
                        "WaitUntilBrick - R.id.brick_wait_until_edit_text",
                        "Control",
                        WaitUntilBrick::class.java,
                        R.id.brick_wait_until_edit_text,
                        "1 < 2 "
                    ),
                    arrayOf(
                        "RepeatUntilBrick - R.id.brick_repeat_until_edit_text",
                        "Control",
                        RepeatUntilBrick::class.java,
                        R.id.brick_repeat_until_edit_text,
                        "1 < 2 "
                    ),
                    arrayOf(
                        "RepeatUntilBrick - R.id.brick_repeat_edit_text",
                        "Control",
                        RepeatBrick::class.java,
                        R.id.brick_repeat_edit_text,
                        "10 "
                    ),
                    arrayOf(
                        "ZigZagStitchBrick - R.id.brick_zigzag_stitch_edit_text_density",
                        "Embroidery",
                        ZigZagStitchBrick::class.java,
                        R.id.brick_zigzag_stitch_edit_text_length,
                        "2 "
                    ),
                    arrayOf(
                        "ZigZagStitchBrick - R.id.brick_zigzag_stitch_edit_text_height",
                        "Embroidery",
                        ZigZagStitchBrick::class.java,
                        R.id.brick_zigzag_stitch_edit_text_width,
                        "10 "
                    ),
                    arrayOf(
                        "RunningStitchBrick - R.id.brick_running_stitch_edit_text_steps",
                        "Embroidery",
                        RunningStitchBrick::class.java,
                        R.id.brick_running_stitch_edit_text_length,
                        "10 "
                    ),
                    arrayOf(
                        "RunningStitchBrick - R.id.brick_running_stitch_edit_text_steps",
                        "Embroidery",
                        TripleStitchBrick::class.java,
                        R.id.brick_triple_stitch_edit_text_steps,
                        "10 "
                    ),
                    arrayOf(
                        "WriteEmbroideryToFileBrick - R.id.brick_write_embroidery_to_file_edit_text",
                        "Embroidery",
                        WriteEmbroideryToFileBrick::class.java,
                        R.id.brick_write_embroidery_to_file_edit_text,
                        "'embroidery.dst' "
                    )
                )
            )
        }
    }
}