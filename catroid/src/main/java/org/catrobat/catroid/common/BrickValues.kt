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
package org.catrobat.catroid.common

import com.badlogic.gdx.math.Vector2
import org.catrobat.catroid.content.PenColor
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsWorld

class BrickValues private constructor() {
    companion object {
        //constants Motions
        const val X_POSITION = 100
        const val Y_POSITION = 200
        const val CHANGE_X_BY = 10
        const val CHANGE_Y_BY = 10
        const val MOVE_STEPS = 10.0
        const val TURN_DEGREES = 15.0
        const val POINT_IN_DIRECTION = 90.0
        const val GLIDE_SECONDS = 1000
        const val GO_BACK = 1
        const val DURATION = 1

        //constants Physics
		@JvmField
		val PHYSIC_TYPE = PhysicsObject.Type.DYNAMIC
        const val PHYSIC_MASS = 1.0
        const val PHYSIC_BOUNCE_FACTOR = 0.8
        const val PHYSIC_FRICTION = 0.2
        @JvmField
		val PHYSIC_GRAVITY = PhysicsWorld.DEFAULT_GRAVITY
        @JvmField
		val PHYSIC_VELOCITY = Vector2()
        const val PHYSIC_TURN_DEGREES = TURN_DEGREES

        //constants Looks
        const val SET_SIZE_TO = 60.0
        const val RELATIVE_SIZE_IN_PERCENT = 120.0
        const val CHANGE_SIZE_BY = 10.0
        const val SET_TRANSPARENCY = 50.0
        const val CHANGE_TRANSPARENCY_EFFECT = 25.0
        const val SET_BRIGHTNESS_TO = 50.0
        const val CHANGE_BRITHNESS_BY = 25.0
        const val SET_COLOR_TO = 0.0
        const val CHANGE_COLOR_BY = 25.0
        const val VIBRATE_SECONDS = 1.0
        const val GO_TO_TOUCH_POSITION = 80
        const val GO_TO_RANDOM_POSITION = 81
        const val GO_TO_OTHER_SPRITE_POSITION = 82
        const val SET_LOOK_BY_INDEX = 1

        //constants Pen
        const val PEN_SIZE = 3.15
        val PEN_COLOR = PenColor(0F, 0F, 1F, 1F)

        //constants Sounds
        const val SET_VOLUME_TO = 60.0
        const val CHANGE_VOLUME_BY = -10.0

        //Constants Control
        const val WAIT = 1000
        const val REPEAT = 10
        const val IF_CONDITION = "1 < 2"
        const val NOTE = "add comment here…"
        const val STOP_THIS_SCRIPT = 0
        const val STOP_ALL_SCRIPTS = 1
        const val STOP_OTHER_SCRIPTS = 2
        const val FOR_LOOP_FROM = 1
        const val FOR_LOOP_TO = 10

        //Constants Lego
        const val LEGO_MOTOR = "A"
        const val LEGO_ANGLE = 180
        const val LEGO_SPEED = 100
        const val LEGO_DURATION = 1.0
        const val LEGO_FREQUENCY = 2
        const val LEGO_VOLUME = 100

        //Constants Drone
        const val DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000
        const val DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT = 20
        const val STRING_VALUE = "default"
        const val DRONE_ALTITUDE_MIN = 3
        const val DRONE_ALTITUDE_INDOOR = 5
        const val DRONE_ALTITUDE_OUTDOOR = 10
        const val DRONE_ALTITUDE_MAX = 100
        const val DRONE_VERTICAL_MIN = 200
        const val DRONE_VERTICAL_INDOOR = 700
        const val DRONE_VERTICAL_OUTDOOR = 1000
        const val DRONE_VERTICAL_MAX = 2000
        const val DRONE_ROTATION_MIN = 40
        const val DRONE_ROTATION_INDOOR = 100
        const val DRONE_ROTATION_OUTDOOR = 200
        const val DRONE_ROTATION_MAX = 350
        const val DRONE_TILT_MIN = 5
        const val DRONE_TILT_INDOOR = 12
        const val DRONE_TILT_OUTDOOR = 20
        const val DRONE_TILT_MAX = 30

        //Constants Jumping Sumo
        const val JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000
        const val JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT = 80
        const val JUMPING_SUMO_SOUND_BRICK_DEFAULT_VOLUME_PERCENT = 50
        const val JUMPING_SUMO_ROTATE_DEFAULT_DEGREE = 90

        //Constants Variables
        const val SET_VARIABLE = 1.0
        const val SHOW_VARIABLE_COLOR = "#FF0000"
        const val CHANGE_VARIABLE = 1.0

        //Constants Lists
        const val ADD_ITEM_TO_USERLIST = 1.0
        const val DELETE_ITEM_OF_USERLIST = 1
        const val INSERT_ITEM_INTO_USERLIST_INDEX = 1
        const val INSERT_ITEM_INTO_USERLIST_VALUE = 1.0
        const val REPLACE_ITEM_IN_USERLIST_INDEX = 1
        const val REPLACE_ITEM_IN_USERLIST_VALUE = 1.0
        const val STORE_CSV_INTO_USERLIST_COLUMN = 1

        //Constants Phiro
        const val PHIRO_SPEED = 100
        const val PHIRO_DURATION = 1
        const val PHIRO_VALUE_RED = 0
        const val PHIRO_VALUE_GREEN = 255
        const val PHIRO_VALUE_BLUE = 255
        const val PHIRO_IF_SENSOR_DEFAULT_VALUE = "Front Left Sensor"

        //Constants Arduino
        const val ARDUINO_PWM_INITIAL_PIN_VALUE = 255
        const val ARDUINO_PWM_INITIAL_PIN_NUMBER = 3
        const val ARDUINO_DIGITAL_INITIAL_PIN_VALUE = 1
        const val ARDUINO_DIGITAL_INITIAL_PIN_NUMBER = 13

        //Constants Raspi
        const val RASPI_DIGITAL_INITIAL_PIN_VALUE = 1
        const val RASPI_DIGITAL_INITIAL_PIN_NUMBER = 3
        @JvmField
		val RASPI_EVENTS = arrayOf("pressed", "released")
        const val RASPI_PWM_INITIAL_PERCENTAGE = 50.0
        const val RASPI_PWM_INITIAL_FREQUENCY = 100.0

        //Constants NFC
        const val TNF_MIME_MEDIA: Short = 0
        const val TNF_WELL_KNOWN_HTTP: Short = 1
        const val TNF_WELL_KNOWN_HTTPS: Short = 2
        const val TNF_WELL_KNOWN_SMS: Short = 3
        const val TNF_WELL_KNOWN_TEL: Short = 4
        const val TNF_WELL_KNOWN_MAILTO: Short = 5
        const val TNF_EXTERNAL_TYPE: Short = 6
        const val TNF_EMPTY: Short = 7
        const val NDEF_PREFIX_HTTP: Byte = 0x03
        const val NDEF_PREFIX_HTTPS: Byte = 0x04
        const val NDEF_PREFIX_TEL: Byte = 0x05
        const val NDEF_PREFIX_MAILTO: Byte = 0x06

        //Constants Embroidery
        const val STITCH_SIZE = 3.15f
        const val STITCH_LENGTH = 10
        const val ZIGZAG_STITCH_LENGTH = 2
        const val ZIGZAG_STITCH_WIDTH = 10
        const val THREAD_COLOR = "#ff0000"

        //Constants Device
        const val TOUCH_DURATION = 0.3
        const val TOUCH_X_START = -100
        const val TOUCH_Y_START = -200
        const val TOUCH_X_GOAL = 100
        const val TOUCH_Y_GOAL = 200

        //Constants Web
        const val OPEN_IN_BROWSER = "https://catrobat.org/"
        const val LOOK_REQUEST = "https://catrob.at/penguin"
        const val BACKGROUND_REQUEST = "https://catrob.at/HalloweenPortrait"
        const val BACKGROUND_REQUEST_LANDSCAPE = "https://catrob.at/HalloweenLandscape"
    }

    // Note: No constant default value for the "Send web request" brick, as it is localized in strings.xml
    init {
        throw AssertionError("No.")
    }
}