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

enum class Sensors {
    X_ACCELERATION {
        override fun getSensor(): Sensor = SensorAccelerationX.getInstance()
    },
    Y_ACCELERATION {
        override fun getSensor(): Sensor = SensorAccelerationY.getInstance()
    },
    Z_ACCELERATION {
        override fun getSensor(): Sensor = SensorAccelerationZ.getInstance()
    },
    COMPASS_DIRECTION {
        override fun getSensor(): Sensor = SensorCompassDirection.getInstance()
    },
    X_INCLINATION {
        override fun getSensor(): Sensor = SensorXInclination.getInstance()
    },
    Y_INCLINATION {
        override fun getSensor(): Sensor = SensorYInclination.getInstance()
    },
    LOUDNESS {
        override fun getSensor(): Sensor = SensorLoudnessSensor.getInstance()
    },
    LATITUDE {
        override fun getSensor(): Sensor = SensorLatitude.getInstance()
    },
    LONGITUDE {
        override fun getSensor(): Sensor = SensorLongitude.getInstance()
    },
    LOCATION_ACCURACY {
        override fun getSensor(): Sensor = SensorLocationAccuracy.getInstance()
    },
    ALTITUDE {
        override fun getSensor(): Sensor = SensorAltitude.getInstance()
    },
    USER_LANGUAGE {
        override fun getSensor(): Sensor = SensorUserLanguage.getInstance()
    },
    TIMER {
        override fun getSensor(): Sensor = SensorTimer.getInstance()
    },
    DATE_YEAR {
        override fun getSensor(): Sensor = SensorDateYear.getInstance()
    },
    DATE_MONTH {
        override fun getSensor(): Sensor = SensorDateMonth.getInstance()
    },
    DATE_DAY {
        override fun getSensor(): Sensor = SensorDateDay.getInstance()
    },
    DATE_WEEKDAY {
        override fun getSensor(): Sensor = SensorDateWeekday.getInstance()
    },
    TIME_HOUR {
        override fun getSensor(): Sensor = SensorTimeHour.getInstance()
    },
    TIME_MINUTE {
        override fun getSensor(): Sensor = SensorTimeMinute.getInstance()
    },
    TIME_SECOND {
        override fun getSensor(): Sensor = SensorTimeSecond.getInstance()
    },
    FACE_DETECTED {
        override fun getSensor(): Sensor = SensorFaceDetected.getInstance()
    },
    FACE_SIZE {
        override fun getSensor(): Sensor = SensorFaceSize.getInstance()
    },
    FACE_X {
        override fun getSensor(): Sensor = SensorFaceX.getInstance()
    },
    FACE_Y {
        override fun getSensor(): Sensor = SensorFaceY.getInstance()
    },
    SECOND_FACE_DETECTED {
        override fun getSensor(): Sensor = SensorSecondFaceDetected
            .getInstance()
    },
    SECOND_FACE_SIZE {
        override fun getSensor(): Sensor = SensorSecondFaceSize.getInstance()
    },
    SECOND_FACE_X {
        override fun getSensor(): Sensor = SensorSecondFaceX.getInstance()
    },
    SECOND_FACE_Y {
        override fun getSensor(): Sensor = SensorSecondFaceY.getInstance()
    },
    HEAD_TOP_X {
        override fun getSensor(): Sensor = SensorHeadTopX.getInstance()
    },
    HEAD_TOP_Y {
        override fun getSensor(): Sensor = SensorHeadTopY.getInstance()
    },
    NECK_X {
        override fun getSensor(): Sensor = SensorNeckX.getInstance()
    },
    NECK_Y {
        override fun getSensor(): Sensor = SensorNeckY.getInstance()
    },
    NOSE_X {
        override fun getSensor(): Sensor = SensorNoseX.getInstance()
    },
    NOSE_Y {
        override fun getSensor(): Sensor = SensorNoseY.getInstance()
    },
    LEFT_EYE_INNER_X {
        override fun getSensor(): Sensor = SensorLeftEyeInnerX.getInstance()
    },
    LEFT_EYE_INNER_Y {
        override fun getSensor(): Sensor = SensorLeftEyeInnerY.getInstance()
    },
    LEFT_EYE_CENTER_X {
        override fun getSensor(): Sensor = SensorLeftEyeCenterX.getInstance()
    },
    LEFT_EYE_CENTER_Y {
        override fun getSensor(): Sensor = SensorLeftEyeCenterY.getInstance()
    },
    LEFT_EYE_OUTER_X {
        override fun getSensor(): Sensor = SensorLeftEyeOuterX.getInstance()
    },
    LEFT_EYE_OUTER_Y {
        override fun getSensor(): Sensor = SensorLeftEyeOuterY.getInstance()
    },
    RIGHT_EYE_INNER_X {
        override fun getSensor(): Sensor = SensorRightEyeInnerX.getInstance()
    },
    RIGHT_EYE_INNER_Y {
        override fun getSensor(): Sensor = SensorRightEyeInnerY.getInstance()
    },
    RIGHT_EYE_CENTER_X {
        override fun getSensor(): Sensor = SensorRightEyeCenterX.getInstance()
    },
    RIGHT_EYE_CENTER_Y {
        override fun getSensor(): Sensor = SensorRightEyeCenterY.getInstance()
    },
    RIGHT_EYE_OUTER_X {
        override fun getSensor(): Sensor = SensorRightEyeOuterX.getInstance()
    },
    RIGHT_EYE_OUTER_Y {
        override fun getSensor(): Sensor = SensorRightEyeOuterY.getInstance()
    },
    LEFT_EAR_X {
        override fun getSensor(): Sensor = SensorLeftEarX.getInstance()
    },
    LEFT_EAR_Y {
        override fun getSensor(): Sensor = SensorLeftEarY.getInstance()
    },
    RIGHT_EAR_X {
        override fun getSensor(): Sensor = SensorRightEarX.getInstance()
    },
    RIGHT_EAR_Y {
        override fun getSensor(): Sensor = SensorRightEarY.getInstance()
    },
    MOUTH_LEFT_CORNER_X {
        override fun getSensor(): Sensor = SensorMouthLeftCornerX.getInstance()
    },
    MOUTH_LEFT_CORNER_Y {
        override fun getSensor(): Sensor = SensorMouthLeftCornerY.getInstance()
    },
    MOUTH_RIGHT_CORNER_X {
        override fun getSensor(): Sensor = SensorMouthRightCornerX.getInstance()
    },
    MOUTH_RIGHT_CORNER_Y {
        override fun getSensor(): Sensor = SensorMouthRightCornerY.getInstance()
    },
    LEFT_SHOULDER_X {
        override fun getSensor(): Sensor = SensorLeftShoulderX.getInstance()
    },
    LEFT_SHOULDER_Y {
        override fun getSensor(): Sensor = SensorLeftShoulderY.getInstance()
    },
    RIGHT_SHOULDER_X {
        override fun getSensor(): Sensor = SensorRightShoulderX.getInstance()
    },
    RIGHT_SHOULDER_Y {
        override fun getSensor(): Sensor = SensorRightShoulderY.getInstance()
    },
    LEFT_ELBOW_X {
        override fun getSensor(): Sensor = SensorLeftElbowX.getInstance()
    },
    LEFT_ELBOW_Y {
        override fun getSensor(): Sensor = SensorLeftElbowY.getInstance()
    },
    RIGHT_ELBOW_X {
        override fun getSensor(): Sensor = SensorRightElbowX.getInstance()
    },
    RIGHT_ELBOW_Y {
        override fun getSensor(): Sensor = SensorRightElbowY.getInstance()
    },
    LEFT_WRIST_X {
        override fun getSensor(): Sensor = SensorLeftWristX.getInstance()
    },
    LEFT_WRIST_Y {
        override fun getSensor(): Sensor = SensorLeftWristY.getInstance()
    },
    RIGHT_WRIST_X {
        override fun getSensor(): Sensor = SensorRightWristX.getInstance()
    },
    RIGHT_WRIST_Y {
        override fun getSensor(): Sensor = SensorRightWristY.getInstance()
    },
    LEFT_PINKY_X {
        override fun getSensor(): Sensor = SensorLeftPinkyX.getInstance()
    },
    LEFT_PINKY_Y {
        override fun getSensor(): Sensor = SensorLeftPinkyY.getInstance()
    },
    RIGHT_PINKY_X {
        override fun getSensor(): Sensor = SensorRightPinkyX.getInstance()
    },
    RIGHT_PINKY_Y {
        override fun getSensor(): Sensor = SensorRightPinkyY.getInstance()
    },
    LEFT_INDEX_X {
        override fun getSensor(): Sensor = SensorLeftIndexX.getInstance()
    },
    LEFT_INDEX_Y {
        override fun getSensor(): Sensor = SensorLeftIndexY.getInstance()
    },
    RIGHT_INDEX_X {
        override fun getSensor(): Sensor = SensorRightIndexX.getInstance()
    },
    RIGHT_INDEX_Y {
        override fun getSensor(): Sensor = SensorRightIndexY.getInstance()
    },
    LEFT_THUMB_X {
        override fun getSensor(): Sensor = SensorLeftThumbX.getInstance()
    },
    LEFT_THUMB_Y {
        override fun getSensor(): Sensor = SensorLeftThumbY.getInstance()
    },
    RIGHT_THUMB_X {
        override fun getSensor(): Sensor = SensorRightThumbX.getInstance()
    },
    RIGHT_THUMB_Y {
        override fun getSensor(): Sensor = SensorRightThumbY.getInstance()
    },
    LEFT_HIP_X {
        override fun getSensor(): Sensor = SensorLeftHipX.getInstance()
    },
    LEFT_HIP_Y {
        override fun getSensor(): Sensor = SensorLeftHipY.getInstance()
    },
    RIGHT_HIP_X {
        override fun getSensor(): Sensor = SensorRightHipX.getInstance()
    },
    RIGHT_HIP_Y {
        override fun getSensor(): Sensor = SensorRightHipY.getInstance()
    },
    LEFT_KNEE_X {
        override fun getSensor(): Sensor = SensorLeftKneeX.getInstance()
    },
    LEFT_KNEE_Y {
        override fun getSensor(): Sensor = SensorLeftKneeY.getInstance()
    },
    RIGHT_KNEE_X {
        override fun getSensor(): Sensor = SensorRightKneeX.getInstance()
    },
    RIGHT_KNEE_Y {
        override fun getSensor(): Sensor = SensorRightKneeY.getInstance()
    },
    LEFT_ANKLE_X {
        override fun getSensor(): Sensor = SensorLeftAnkleX.getInstance()
    },
    LEFT_ANKLE_Y {
        override fun getSensor(): Sensor = SensorLeftAnkleY.getInstance()
    },
    RIGHT_ANKLE_X {
        override fun getSensor(): Sensor = SensorRightAnkleX.getInstance()
    },
    RIGHT_ANKLE_Y {
        override fun getSensor(): Sensor = SensorRightAnkleY.getInstance()
    },
    LEFT_HEEL_X {
        override fun getSensor(): Sensor = SensorLeftHeelX.getInstance()
    },
    LEFT_HEEL_Y {
        override fun getSensor(): Sensor = SensorLeftHeelY.getInstance()
    },
    RIGHT_HEEL_X {
        override fun getSensor(): Sensor = SensorRightHeelX.getInstance()
    },
    RIGHT_HEEL_Y {
        override fun getSensor(): Sensor = SensorRightHeelY.getInstance()
    },
    LEFT_FOOT_INDEX_X {
        override fun getSensor(): Sensor = SensorLeftFootIndexX.getInstance()
    },
    LEFT_FOOT_INDEX_Y {
        override fun getSensor(): Sensor = SensorLeftFootIndexY.getInstance()
    },
    RIGHT_FOOT_INDEX_X {
        override fun getSensor(): Sensor = SensorRightFootIndexX.getInstance()
    },
    RIGHT_FOOT_INDEX_Y {
        override fun getSensor(): Sensor = SensorRightFootIndexY.getInstance()
    },
    OBJECT_X {
        override fun getSensor(): Sensor = SensorObjectX.getInstance()
    },
    OBJECT_Y {
        override fun getSensor(): Sensor = SensorObjectY.getInstance()
    },
    OBJECT_TRANSPARENCY {
        override fun getSensor(): Sensor = SensorObjectTransparency.getInstance()
    },
    OBJECT_BRIGHTNESS {
        override fun getSensor(): Sensor = SensorObjectBrightness.getInstance()
    },
    OBJECT_COLOR {
        override fun getSensor(): Sensor = SensorObjectColor.getInstance()
    },
    OBJECT_SIZE {
        override fun getSensor(): Sensor = SensorObjectSize.getInstance()
    },
    MOTION_DIRECTION {
        override fun getSensor(): Sensor = SensorObjectMotionDirection.getInstance()
    },
    LOOK_DIRECTION {
        override fun getSensor(): Sensor = SensorObjectLookDirection.getInstance()
    },
    OBJECT_LAYER {
        override fun getSensor(): Sensor = SensorObjectLayer.getInstance()
    },
    OBJECT_DISTANCE_TO {
        override fun getSensor(): Sensor = SensorObjectDistanceTo.getInstance()
    },
    NXT_SENSOR_1 {
        override fun getSensor(): Sensor = SensorNXTSensor1.getInstance()
    },
    NXT_SENSOR_2 {
        override fun getSensor(): Sensor = SensorNXTSensor2.getInstance()
    },
    NXT_SENSOR_3 {
        override fun getSensor(): Sensor = SensorNXTSensor3.getInstance()
    },
    NXT_SENSOR_4 {
        override fun getSensor(): Sensor = SensorNXTSensor4.getInstance()
    },
    EV3_SENSOR_1 {
        override fun getSensor(): Sensor = SensorEV3Sensor1.getInstance()
    },
    EV3_SENSOR_2 {
        override fun getSensor(): Sensor = SensorEV3Sensor2.getInstance()
    },
    EV3_SENSOR_3 {
        override fun getSensor(): Sensor = SensorEV3Sensor3.getInstance()
    },
    EV3_SENSOR_4 {
        override fun getSensor(): Sensor = SensorEV3Sensor4.getInstance()
    },
    PHIRO_FRONT_LEFT {
        override fun getSensor(): Sensor = SensorPhiroFrontLeft.getInstance()
    },
    PHIRO_FRONT_RIGHT {
        override fun getSensor(): Sensor = SensorPhiroFrontRight.getInstance()
    },
    PHIRO_SIDE_LEFT {
        override fun getSensor(): Sensor = SensorPhiroSideLeft.getInstance()
    },
    PHIRO_SIDE_RIGHT {
        override fun getSensor(): Sensor = SensorPhiroSideRight.getInstance()
    },
    PHIRO_BOTTOM_LEFT {
        override fun getSensor(): Sensor = SensorPhiroBottomLeft.getInstance()
    },
    PHIRO_BOTTOM_RIGHT {
        override fun getSensor(): Sensor = SensorPhiroBottomRight.getInstance()
    },
    DRONE_BATTERY_STATUS, // no visible usage
    DRONE_EMERGENCY_STATE, // no visible usage
    DRONE_FLYING, // no visible usage
    DRONE_INITIALIZED, // no visible usage
    DRONE_USB_ACTIVE, // no visible usage
    DRONE_USB_REMAINING_TIME, // no visible usage
    DRONE_CAMERA_READY, // no visible usage
    DRONE_RECORD_READY, // no visible usage
    DRONE_RECORDING, // no visible usage
    DRONE_NUM_FRAMES, // no visible usage
    COLLIDES_WITH_EDGE {
        override fun getSensor(): Sensor = SensorObjectCollidesWithEdge
            .getInstance()
    },
    COLLIDES_WITH_FINGER {
        override fun getSensor(): Sensor = SensorObjectCollidesWithFinger
            .getInstance()
    },
    OBJECT_X_VELOCITY {
        override fun getSensor(): Sensor = SensorObjectXVelocity.getInstance()
    },
    OBJECT_Y_VELOCITY {
        override fun getSensor(): Sensor = SensorObjectYVelocity.getInstance()
    },
    OBJECT_ANGULAR_VELOCITY {
        override fun getSensor(): Sensor = SensorObjectAngularVelocity
            .getInstance()
    },
    LAST_FINGER_INDEX {
        override fun getSensor(): Sensor = SensorLastFingerIndex.getInstance()
    },
    FINGER_X {
        override fun getSensor(): Sensor = SensorFingerX.getInstance()
    },
    FINGER_Y {
        override fun getSensor(): Sensor = SensorFingerY.getInstance()
    },
    FINGER_TOUCHED {
        override fun getSensor(): Sensor = SensorFingerTouched.getInstance()
    },
    NUMBER_CURRENT_TOUCHES {
        override fun getSensor(): Sensor = SensorNumberCurrentTouches
            .getInstance()
    },
    OBJECT_LOOK_NUMBER {
        override fun getSensor(): Sensor = SensorObjectLookNumber.getInstance()
    },
    OBJECT_LOOK_NAME {
        override fun getSensor(): Sensor = SensorObjectLookName.getInstance()
    },
    OBJECT_NUMBER_OF_LOOKS {
        override fun getSensor(): Sensor = SensorObjectNumberOfLooks
            .getInstance()
    },
    OBJECT_BACKGROUND_NUMBER {
        override fun getSensor(): Sensor = SensorObjectBackgroundNumber
            .getInstance()
    },
    OBJECT_BACKGROUND_NAME {
        override fun getSensor(): Sensor = SensorObjectBackgroundName
            .getInstance()
    },
    NFC_TAG_ID {
        override fun getSensor(): Sensor = SensorNFCTagId.getInstance()
    },
    NFC_TAG_MESSAGE {
        override fun getSensor(): Sensor = SensorNFCTagMessage.getInstance()
    },
    GAMEPAD_A_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadAPressed.getInstance()
    },
    GAMEPAD_B_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadBPressed.getInstance()
    },
    GAMEPAD_UP_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadUPPressed.getInstance()
    },
    GAMEPAD_DOWN_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadDOWNPressed
            .getInstance()
    },
    GAMEPAD_LEFT_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadLEFTPressed
            .getInstance()
    },
    GAMEPAD_RIGHT_PRESSED {
        override fun getSensor(): Sensor = SensorGamepadRIGHTPressed
            .getInstance()
    },
    TEXT_FROM_CAMERA {
        override fun getSensor(): Sensor = SensorTextFormCamera.getInstance()
    },
    TEXT_BLOCKS_NUMBER {
        override fun getSensor(): Sensor = SensorTextBlocksNumber.getInstance()
    },
    TEXT_BLOCK_X, // interpreted as Function
    TEXT_BLOCK_Y, // interpreted as Function
    TEXT_BLOCK_SIZE, // interpreted as Function
    TEXT_BLOCK_FROM_CAMERA, // interpreted as Function
    TEXT_BLOCK_LANGUAGE_FROM_CAMERA, // interpreted as function
    SPEECH_RECOGNITION_LANGUAGE {
        override fun getSensor(): Sensor = SensorSpeechRecognition
            .getInstance()
    },
    STAGE_WIDTH {
        override fun getSensor(): Sensor = SensorStageWidth.getInstance()
    },
    STAGE_HEIGHT {
        override fun getSensor(): Sensor = SensorStageHeight.getInstance()
    },
    DEFAULT_SENSOR;

    open fun getSensor(): Sensor = SensorDefaultSensor.getInstance()

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
