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

import junit.framework.Assert
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.junit.Test
import java.util.Arrays

@RunWith(Parameterized::class)
class BricksDefaultFormulaFieldTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var brick: FormulaBrick? = null
    @JvmField
    @Parameterized.Parameter(2)
    var expectedDefaultBrickField: BrickField? = null
    @Test
    fun testEditFormula() {
        Assert.assertEquals(expectedDefaultBrickField, brick!!.defaultBrickField)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        ArduinoSendDigitalValueBrick::class.java.simpleName,
                        ArduinoSendDigitalValueBrick(),
                        BrickField.ARDUINO_DIGITAL_PIN_NUMBER
                    ), arrayOf(
                        ArduinoSendPWMValueBrick::class.java.simpleName,
                        ArduinoSendPWMValueBrick(),
                        BrickField.ARDUINO_ANALOG_PIN_NUMBER
                    ), arrayOf(
                        RaspiPwmBrick::class.java.simpleName,
                        RaspiPwmBrick(),
                        BrickField.RASPI_DIGITAL_PIN_NUMBER
                    ), arrayOf(
                        RaspiSendDigitalValueBrick::class.java.simpleName,
                        RaspiSendDigitalValueBrick(),
                        BrickField.RASPI_DIGITAL_PIN_NUMBER
                    ), arrayOf(
                        LegoEv3PlayToneBrick::class.java.simpleName,
                        LegoEv3PlayToneBrick(),
                        BrickField.LEGO_EV3_DURATION_IN_SECONDS
                    ), arrayOf(
                        LegoNxtPlayToneBrick::class.java.simpleName,
                        LegoNxtPlayToneBrick(),
                        BrickField.LEGO_NXT_DURATION_IN_SECONDS
                    ), arrayOf(
                        DroneMoveUpBrick::class.java.simpleName,
                        DroneMoveUpBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneMoveDownBrick::class.java.simpleName,
                        DroneMoveDownBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneMoveLeftBrick::class.java.simpleName,
                        DroneMoveLeftBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneMoveRightBrick::class.java.simpleName,
                        DroneMoveRightBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneMoveForwardBrick::class.java.simpleName,
                        DroneMoveForwardBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneMoveBackwardBrick::class.java.simpleName,
                        DroneMoveBackwardBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneTurnLeftBrick::class.java.simpleName,
                        DroneTurnLeftBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        DroneTurnRightBrick::class.java.simpleName,
                        DroneTurnRightBrick(),
                        BrickField.DRONE_TIME_TO_FLY_IN_SECONDS
                    ), arrayOf(
                        JumpingSumoMoveBackwardBrick::class.java.simpleName,
                        JumpingSumoMoveBackwardBrick(),
                        BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS
                    ), arrayOf(
                        JumpingSumoMoveForwardBrick::class.java.simpleName,
                        JumpingSumoMoveForwardBrick(),
                        BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS
                    ), arrayOf(
                        ShowTextBrick::class.java.simpleName, ShowTextBrick(), BrickField.X_POSITION
                    ), arrayOf(
                        ShowTextColorSizeAlignmentBrick::class.java.simpleName,
                        ShowTextColorSizeAlignmentBrick(),
                        BrickField.X_POSITION
                    ), arrayOf(
                        InsertItemIntoUserListBrick::class.java.simpleName,
                        InsertItemIntoUserListBrick(),
                        BrickField.INSERT_ITEM_INTO_USERLIST_VALUE
                    ), arrayOf(
                        ReplaceItemInUserListBrick::class.java.simpleName,
                        ReplaceItemInUserListBrick(),
                        BrickField.REPLACE_ITEM_IN_USERLIST_INDEX
                    ), arrayOf(
                        SayForBubbleBrick::class.java.simpleName,
                        SayForBubbleBrick(),
                        BrickField.STRING
                    ), arrayOf(
                        ThinkForBubbleBrick::class.java.simpleName,
                        ThinkForBubbleBrick(),
                        BrickField.STRING
                    ), arrayOf(
                        PhiroRGBLightBrick::class.java.simpleName,
                        PhiroRGBLightBrick(),
                        BrickField.PHIRO_LIGHT_RED
                    ), arrayOf(
                        SetPenColorBrick::class.java.simpleName,
                        SetPenColorBrick(),
                        BrickField.PEN_COLOR_RED
                    ), arrayOf(
                        PlaceAtBrick::class.java.simpleName, PlaceAtBrick(), BrickField.X_POSITION
                    ), arrayOf(
                        GlideToBrick::class.java.simpleName,
                        GlideToBrick(),
                        BrickField.DURATION_IN_SECONDS
                    ), arrayOf(
                        SetVelocityBrick::class.java.simpleName,
                        SetVelocityBrick(),
                        BrickField.PHYSICS_VELOCITY_X
                    ), arrayOf(
                        SetGravityBrick::class.java.simpleName,
                        SetGravityBrick(),
                        BrickField.PHYSICS_GRAVITY_X
                    )
                )
            )
        }
    }
}