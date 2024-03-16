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
package org.catrobat.catroid.formulaeditor

import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.SensorAccelerationX
import org.catrobat.catroid.formulaeditor.sensor.SensorAccelerationY
import org.catrobat.catroid.formulaeditor.sensor.SensorAccelerationZ
import org.catrobat.catroid.formulaeditor.sensor.SensorAltitude
import org.catrobat.catroid.formulaeditor.sensor.SensorCompassDirection
import org.catrobat.catroid.formulaeditor.sensor.SensorDateDay
import org.catrobat.catroid.formulaeditor.sensor.SensorDateMonth
import org.catrobat.catroid.formulaeditor.sensor.SensorDateWeekday
import org.catrobat.catroid.formulaeditor.sensor.SensorDateYear
import org.catrobat.catroid.formulaeditor.sensor.SensorDefaultSensor
import org.catrobat.catroid.formulaeditor.sensor.SensorEV3Sensor1
import org.catrobat.catroid.formulaeditor.sensor.SensorEV3Sensor2
import org.catrobat.catroid.formulaeditor.sensor.SensorEV3Sensor3
import org.catrobat.catroid.formulaeditor.sensor.SensorEV3Sensor4
import org.catrobat.catroid.formulaeditor.sensor.SensorFaceDetected
import org.catrobat.catroid.formulaeditor.sensor.SensorFaceSize
import org.catrobat.catroid.formulaeditor.sensor.SensorFaceX
import org.catrobat.catroid.formulaeditor.sensor.SensorFaceY
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerTouched
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerX
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerY
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadAPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadBPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadDOWNPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadLEFTPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadRIGHTPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorGamepadUPPressed
import org.catrobat.catroid.formulaeditor.sensor.SensorHeadTopX
import org.catrobat.catroid.formulaeditor.sensor.SensorHeadTopY
import org.catrobat.catroid.formulaeditor.sensor.SensorLastFingerIndex
import org.catrobat.catroid.formulaeditor.sensor.SensorLatitude
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftAnkleX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftAnkleY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEarX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEarY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftElbowX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftElbowY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeCenterX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeCenterY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeInnerX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeInnerY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeOuterX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftEyeOuterY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftFootIndexX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftFootIndexY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftHeelX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftHeelY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftHipX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftHipY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftIndexX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftIndexY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftKneeX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftKneeY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftPinkyX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftPinkyY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftShoulderX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftShoulderY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftThumbX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftThumbY
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftWristX
import org.catrobat.catroid.formulaeditor.sensor.SensorLeftWristY
import org.catrobat.catroid.formulaeditor.sensor.SensorLocationAccuracy
import org.catrobat.catroid.formulaeditor.sensor.SensorLongitude
import org.catrobat.catroid.formulaeditor.sensor.SensorLoudnessSensor
import org.catrobat.catroid.formulaeditor.sensor.SensorMouthLeftCornerX
import org.catrobat.catroid.formulaeditor.sensor.SensorMouthLeftCornerY
import org.catrobat.catroid.formulaeditor.sensor.SensorMouthRightCornerX
import org.catrobat.catroid.formulaeditor.sensor.SensorMouthRightCornerY
import org.catrobat.catroid.formulaeditor.sensor.SensorNFCTagId
import org.catrobat.catroid.formulaeditor.sensor.SensorNFCTagMessage
import org.catrobat.catroid.formulaeditor.sensor.SensorNXTSensor1
import org.catrobat.catroid.formulaeditor.sensor.SensorNXTSensor2
import org.catrobat.catroid.formulaeditor.sensor.SensorNXTSensor3
import org.catrobat.catroid.formulaeditor.sensor.SensorNXTSensor4
import org.catrobat.catroid.formulaeditor.sensor.SensorNeckX
import org.catrobat.catroid.formulaeditor.sensor.SensorNeckY
import org.catrobat.catroid.formulaeditor.sensor.SensorNoseX
import org.catrobat.catroid.formulaeditor.sensor.SensorNoseY
import org.catrobat.catroid.formulaeditor.sensor.SensorNumberCurrentTouches
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectAngularVelocity
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectBackgroundName
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectBackgroundNumber
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectBrightness
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectCollidesWithEdge
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectCollidesWithFinger
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectColor
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectDistanceTo
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectLayer
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectLookDirection
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectLookName
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectLookNumber
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectMotionDirection
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectNumberOfLooks
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectSize
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectTransparency
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectX
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectXVelocity
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectY
import org.catrobat.catroid.formulaeditor.sensor.SensorObjectYVelocity
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroBottomLeft
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroBottomRight
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroFrontLeft
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroFrontRight
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroSideLeft
import org.catrobat.catroid.formulaeditor.sensor.SensorPhiroSideRight
import org.catrobat.catroid.formulaeditor.sensor.SensorRightAnkleX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightAnkleY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEarX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEarY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightElbowX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightElbowY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeCenterX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeCenterY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeInnerX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeInnerY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeOuterX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightEyeOuterY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightFootIndexX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightFootIndexY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightHeelX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightHeelY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightHipX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightHipY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightIndexX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightIndexY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightKneeX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightKneeY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightPinkyX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightPinkyY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightShoulderX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightShoulderY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightThumbX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightThumbY
import org.catrobat.catroid.formulaeditor.sensor.SensorRightWristX
import org.catrobat.catroid.formulaeditor.sensor.SensorRightWristY
import org.catrobat.catroid.formulaeditor.sensor.SensorSecondFaceDetected
import org.catrobat.catroid.formulaeditor.sensor.SensorSecondFaceSize
import org.catrobat.catroid.formulaeditor.sensor.SensorSecondFaceX
import org.catrobat.catroid.formulaeditor.sensor.SensorSecondFaceY
import org.catrobat.catroid.formulaeditor.sensor.SensorSpeechRecognition
import org.catrobat.catroid.formulaeditor.sensor.SensorStageHeight
import org.catrobat.catroid.formulaeditor.sensor.SensorStageWidth
import org.catrobat.catroid.formulaeditor.sensor.SensorTextBlocksNumber
import org.catrobat.catroid.formulaeditor.sensor.SensorTextFormCamera
import org.catrobat.catroid.formulaeditor.sensor.SensorTimeHour
import org.catrobat.catroid.formulaeditor.sensor.SensorTimeMinute
import org.catrobat.catroid.formulaeditor.sensor.SensorTimeSecond
import org.catrobat.catroid.formulaeditor.sensor.SensorTimer
import org.catrobat.catroid.formulaeditor.sensor.SensorUserLanguage
import org.catrobat.catroid.formulaeditor.sensor.SensorXInclination
import org.catrobat.catroid.formulaeditor.sensor.SensorYInclination
import org.catrobat.catroid.utils.EnumUtils
import java.util.EnumSet

enum class Sensors(val getSensor: () -> Sensor) {
    X_ACCELERATION({ SensorAccelerationX.getInstance() }),
    Y_ACCELERATION({ SensorAccelerationY.getInstance() }),
    Z_ACCELERATION({ SensorAccelerationZ.getInstance() }),
    COMPASS_DIRECTION({ SensorCompassDirection.getInstance() }),
    X_INCLINATION({ SensorXInclination.getInstance() }),
    Y_INCLINATION({ SensorYInclination.getInstance() }),
    LOUDNESS({ SensorLoudnessSensor.getInstance() }),
    LATITUDE({ SensorLatitude.getInstance() }),
    LONGITUDE({ SensorLongitude.getInstance() }),
    LOCATION_ACCURACY({ SensorLocationAccuracy.getInstance() }),
    ALTITUDE({ SensorAltitude.getInstance() }),
    USER_LANGUAGE({ SensorUserLanguage.getInstance() }),
    TIMER({ SensorTimer.getInstance() }),
    DATE_YEAR({ SensorDateYear.getInstance() }),
    DATE_MONTH({ SensorDateMonth.getInstance() }),
    DATE_DAY({ SensorDateDay.getInstance() }),
    DATE_WEEKDAY({ SensorDateWeekday.getInstance() }),
    TIME_HOUR({ SensorTimeHour.getInstance() }),
    TIME_MINUTE({ SensorTimeMinute.getInstance() }),
    TIME_SECOND({ SensorTimeSecond.getInstance() }),
    FACE_DETECTED({ SensorFaceDetected.getInstance() }),
    FACE_SIZE({ SensorFaceSize.getInstance() }),
    FACE_X({ SensorFaceX.getInstance() }),
    FACE_Y({ SensorFaceY.getInstance() }),
    SECOND_FACE_DETECTED({ SensorSecondFaceDetected.getInstance() }),
    SECOND_FACE_SIZE({ SensorSecondFaceSize.getInstance() }),
    SECOND_FACE_X({ SensorSecondFaceX.getInstance() }),
    SECOND_FACE_Y({ SensorSecondFaceY.getInstance() }),
    HEAD_TOP_X({ SensorHeadTopX.getInstance() }),
    HEAD_TOP_Y({ SensorHeadTopY.getInstance() }),
    NECK_X({ SensorNeckX.getInstance() }),
    NECK_Y({ SensorNeckY.getInstance() }),
    NOSE_X({ SensorNoseX.getInstance() }),
    NOSE_Y({ SensorNoseY.getInstance() }),
    LEFT_EYE_INNER_X({ SensorLeftEyeInnerX.getInstance() }),
    LEFT_EYE_INNER_Y({ SensorLeftEyeInnerY.getInstance() }),
    LEFT_EYE_CENTER_X({ SensorLeftEyeCenterX.getInstance() }),
    LEFT_EYE_CENTER_Y({ SensorLeftEyeCenterY.getInstance() }),
    LEFT_EYE_OUTER_X({ SensorLeftEyeOuterX.getInstance() }),
    LEFT_EYE_OUTER_Y({ SensorLeftEyeOuterY.getInstance() }),
    RIGHT_EYE_INNER_X({ SensorRightEyeInnerX.getInstance() }),
    RIGHT_EYE_INNER_Y({ SensorRightEyeInnerY.getInstance() }),
    RIGHT_EYE_CENTER_X({ SensorRightEyeCenterX.getInstance() }),
    RIGHT_EYE_CENTER_Y({ SensorRightEyeCenterY.getInstance() }),
    RIGHT_EYE_OUTER_X({ SensorRightEyeOuterX.getInstance() }),
    RIGHT_EYE_OUTER_Y({ SensorRightEyeOuterY.getInstance() }),
    LEFT_EAR_X({ SensorLeftEarX.getInstance() }),
    LEFT_EAR_Y({ SensorLeftEarY.getInstance() }),
    RIGHT_EAR_X({ SensorRightEarX.getInstance() }),
    RIGHT_EAR_Y({ SensorRightEarY.getInstance() }),
    MOUTH_LEFT_CORNER_X({ SensorMouthLeftCornerX.getInstance() }),
    MOUTH_LEFT_CORNER_Y({ SensorMouthLeftCornerY.getInstance() }),
    MOUTH_RIGHT_CORNER_X({ SensorMouthRightCornerX.getInstance() }),
    MOUTH_RIGHT_CORNER_Y({ SensorMouthRightCornerY.getInstance() }),
    LEFT_SHOULDER_X({ SensorLeftShoulderX.getInstance() }),
    LEFT_SHOULDER_Y({ SensorLeftShoulderY.getInstance() }),
    RIGHT_SHOULDER_X({ SensorRightShoulderX.getInstance() }),
    RIGHT_SHOULDER_Y({ SensorRightShoulderY.getInstance() }),
    LEFT_ELBOW_X({ SensorLeftElbowX.getInstance() }),
    LEFT_ELBOW_Y({ SensorLeftElbowY.getInstance() }),
    RIGHT_ELBOW_X({ SensorRightElbowX.getInstance() }),
    RIGHT_ELBOW_Y({ SensorRightElbowY.getInstance() }),
    LEFT_WRIST_X({ SensorLeftWristX.getInstance() }),
    LEFT_WRIST_Y({ SensorLeftWristY.getInstance() }),
    RIGHT_WRIST_X({ SensorRightWristX.getInstance() }),
    RIGHT_WRIST_Y({ SensorRightWristY.getInstance() }),
    LEFT_PINKY_X({ SensorLeftPinkyX.getInstance() }),
    LEFT_PINKY_Y({ SensorLeftPinkyY.getInstance() }),
    RIGHT_PINKY_X({ SensorRightPinkyX.getInstance() }),
    RIGHT_PINKY_Y({ SensorRightPinkyY.getInstance() }),
    LEFT_INDEX_X({ SensorLeftIndexX.getInstance() }),
    LEFT_INDEX_Y({ SensorLeftIndexY.getInstance() }),
    RIGHT_INDEX_X({ SensorRightIndexX.getInstance() }),
    RIGHT_INDEX_Y({ SensorRightIndexY.getInstance() }),
    LEFT_THUMB_X({ SensorLeftThumbX.getInstance() }),
    LEFT_THUMB_Y({ SensorLeftThumbY.getInstance() }),
    RIGHT_THUMB_X({ SensorRightThumbX.getInstance() }),
    RIGHT_THUMB_Y({ SensorRightThumbY.getInstance() }),
    LEFT_HIP_X({ SensorLeftHipX.getInstance() }),
    LEFT_HIP_Y({ SensorLeftHipY.getInstance() }),
    RIGHT_HIP_X({ SensorRightHipX.getInstance() }),
    RIGHT_HIP_Y({ SensorRightHipY.getInstance() }),
    LEFT_KNEE_X({ SensorLeftKneeX.getInstance() }),
    LEFT_KNEE_Y({ SensorLeftKneeY.getInstance() }),
    RIGHT_KNEE_X({ SensorRightKneeX.getInstance() }),
    RIGHT_KNEE_Y({ SensorRightKneeY.getInstance() }),
    LEFT_ANKLE_X({ SensorLeftAnkleX.getInstance() }),
    LEFT_ANKLE_Y({ SensorLeftAnkleY.getInstance() }),
    RIGHT_ANKLE_X({ SensorRightAnkleX.getInstance() }),
    RIGHT_ANKLE_Y({ SensorRightAnkleY.getInstance() }),
    LEFT_HEEL_X({ SensorLeftHeelX.getInstance() }),
    LEFT_HEEL_Y({ SensorLeftHeelY.getInstance() }),
    RIGHT_HEEL_X({ SensorRightHeelX.getInstance() }),
    RIGHT_HEEL_Y({ SensorRightHeelY.getInstance() }),
    LEFT_FOOT_INDEX_X({ SensorLeftFootIndexX.getInstance() }),
    LEFT_FOOT_INDEX_Y({ SensorLeftFootIndexY.getInstance() }),
    RIGHT_FOOT_INDEX_X({ SensorRightFootIndexX.getInstance() }),
    RIGHT_FOOT_INDEX_Y({ SensorRightFootIndexY.getInstance() }),
    OBJECT_X({ SensorObjectX.getInstance() }),
    OBJECT_Y({ SensorObjectY.getInstance() }),
    OBJECT_TRANSPARENCY({ SensorObjectTransparency.getInstance() }),
    OBJECT_BRIGHTNESS({ SensorObjectBrightness.getInstance() }),
    OBJECT_COLOR({ SensorObjectColor.getInstance() }),
    OBJECT_SIZE({ SensorObjectSize.getInstance() }),
    MOTION_DIRECTION({ SensorObjectMotionDirection.getInstance() }),
    LOOK_DIRECTION({ SensorObjectLookDirection.getInstance() }),
    OBJECT_LAYER({ SensorObjectLayer.getInstance() }),
    OBJECT_DISTANCE_TO({ SensorObjectDistanceTo.getInstance() }),
    NXT_SENSOR_1({ SensorNXTSensor1.getInstance() }),
    NXT_SENSOR_2({ SensorNXTSensor2.getInstance() }),
    NXT_SENSOR_3({ SensorNXTSensor3.getInstance() }),
    NXT_SENSOR_4({ SensorNXTSensor4.getInstance() }),
    EV3_SENSOR_1({ SensorEV3Sensor1.getInstance() }),
    EV3_SENSOR_2({ SensorEV3Sensor2.getInstance() }),
    EV3_SENSOR_3({ SensorEV3Sensor3.getInstance() }),
    EV3_SENSOR_4({ SensorEV3Sensor4.getInstance() }),
    PHIRO_FRONT_LEFT({ SensorPhiroFrontLeft.getInstance() }),
    PHIRO_FRONT_RIGHT({ SensorPhiroFrontRight.getInstance() }),
    PHIRO_SIDE_LEFT({ SensorPhiroSideLeft.getInstance() }),
    PHIRO_SIDE_RIGHT({ SensorPhiroSideRight.getInstance() }),
    PHIRO_BOTTOM_LEFT({ SensorPhiroBottomLeft.getInstance() }),
    PHIRO_BOTTOM_RIGHT({ SensorPhiroBottomRight.getInstance() }),
    DRONE_BATTERY_STATUS({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_EMERGENCY_STATE({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_FLYING({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_INITIALIZED({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_USB_ACTIVE({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_USB_REMAINING_TIME({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_CAMERA_READY({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_RECORD_READY({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_RECORDING({ SensorDefaultSensor.getInstance() }), // no visible usage
    DRONE_NUM_FRAMES({ SensorDefaultSensor.getInstance() }), // no visible usage
    COLLIDES_WITH_EDGE({ SensorObjectCollidesWithEdge.getInstance() }),
    COLLIDES_WITH_FINGER({ SensorObjectCollidesWithFinger.getInstance() }),
    OBJECT_X_VELOCITY({ SensorObjectXVelocity.getInstance() }),
    OBJECT_Y_VELOCITY({ SensorObjectYVelocity.getInstance() }),
    OBJECT_ANGULAR_VELOCITY({ SensorObjectAngularVelocity.getInstance() }),
    LAST_FINGER_INDEX({ SensorLastFingerIndex.getInstance() }),
    FINGER_X({ SensorFingerX.getInstance() }),
    FINGER_Y({ SensorFingerY.getInstance() }),
    FINGER_TOUCHED({ SensorFingerTouched.getInstance() }),
    NUMBER_CURRENT_TOUCHES({ SensorNumberCurrentTouches.getInstance() }),
    OBJECT_LOOK_NUMBER({ SensorObjectLookNumber.getInstance() }),
    OBJECT_LOOK_NAME({ SensorObjectLookName.getInstance() }),
    OBJECT_NUMBER_OF_LOOKS({ SensorObjectNumberOfLooks.getInstance() }),
    OBJECT_BACKGROUND_NUMBER({ SensorObjectBackgroundNumber.getInstance() }),
    OBJECT_BACKGROUND_NAME({ SensorObjectBackgroundName.getInstance() }),
    NFC_TAG_ID({ SensorNFCTagId.getInstance() }),
    NFC_TAG_MESSAGE({ SensorNFCTagMessage.getInstance() }),
    GAMEPAD_A_PRESSED({ SensorGamepadAPressed.getInstance() }),
    GAMEPAD_B_PRESSED({ SensorGamepadBPressed.getInstance() }),
    GAMEPAD_UP_PRESSED({ SensorGamepadUPPressed.getInstance() }),
    GAMEPAD_DOWN_PRESSED({ SensorGamepadDOWNPressed.getInstance() }),
    GAMEPAD_LEFT_PRESSED({ SensorGamepadLEFTPressed.getInstance() }),
    GAMEPAD_RIGHT_PRESSED({ SensorGamepadRIGHTPressed.getInstance() }),
    TEXT_FROM_CAMERA({ SensorTextFormCamera.getInstance() }),
    TEXT_BLOCKS_NUMBER({ SensorTextBlocksNumber.getInstance() }),
    TEXT_BLOCK_X({ SensorDefaultSensor.getInstance() }), // interpreted as Function
    TEXT_BLOCK_Y({ SensorDefaultSensor.getInstance() }), // interpreted as Function
    TEXT_BLOCK_SIZE({ SensorDefaultSensor.getInstance() }), // interpreted as Function
    TEXT_BLOCK_FROM_CAMERA({ SensorDefaultSensor.getInstance() }), // interpreted as Function
    TEXT_BLOCK_LANGUAGE_FROM_CAMERA({ SensorDefaultSensor.getInstance() }), // interpreted as function
    SPEECH_RECOGNITION_LANGUAGE({ SensorSpeechRecognition.getInstance() }),
    STAGE_WIDTH({ SensorStageWidth.getInstance() }),
    STAGE_HEIGHT({ SensorStageHeight.getInstance() }),
    DEFAULT_SENSOR({ SensorDefaultSensor.getInstance() });

    companion object {
        val TAG: String = Sensors::class.java.simpleName
        private val BOOLEAN: EnumSet<Sensors> = EnumSet.of(
            FACE_DETECTED,
            SECOND_FACE_DETECTED,
            DRONE_FLYING,
            DRONE_INITIALIZED,
            DRONE_USB_ACTIVE,
            DRONE_CAMERA_READY,
            GAMEPAD_RIGHT_PRESSED,
            GAMEPAD_LEFT_PRESSED,
            GAMEPAD_DOWN_PRESSED,
            GAMEPAD_UP_PRESSED,
            GAMEPAD_B_PRESSED,
            GAMEPAD_A_PRESSED,
            FINGER_TOUCHED,
            COLLIDES_WITH_FINGER,
            COLLIDES_WITH_EDGE,
            DRONE_RECORDING,
            DRONE_RECORD_READY
        )

        @JvmStatic
        fun isSensor(value: String?): Boolean = EnumUtils.isValidEnum(Sensors::class.java, value)

        @JvmStatic
        fun isBoolean(sensor: Sensors): Boolean = BOOLEAN.contains(sensor)

        @JvmStatic
        fun getSensorByValue(value: String?): Sensors {
            val sensor: Sensors? = EnumUtils.getEnum(Sensors::class.java, value)
            return sensor ?: DEFAULT_SENSOR
        }
    }
}
