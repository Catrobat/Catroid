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

package org.catrobat.catroid.uiespresso.content.brick.app

import android.content.Context
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.SENSOR
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.Sensors.FACE_DETECTED
import org.catrobat.catroid.formulaeditor.Sensors.FACE_SIZE
import org.catrobat.catroid.formulaeditor.Sensors.FACE_X
import org.catrobat.catroid.formulaeditor.Sensors.FACE_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_ANKLE_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_ANKLE_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EAR_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EAR_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_ELBOW_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_ELBOW_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_CENTER_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_CENTER_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_INNER_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_INNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_OUTER_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_EYE_OUTER_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_FOOT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_FOOT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_HEEL_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_HEEL_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_HIP_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_HIP_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_KNEE_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_KNEE_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_PINKY_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_PINKY_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_SHOULDER_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_SHOULDER_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_THUMB_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_THUMB_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_WRIST_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_WRIST_Y
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_LEFT_CORNER_X
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_LEFT_CORNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_RIGHT_CORNER_X
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_RIGHT_CORNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.NOSE_X
import org.catrobat.catroid.formulaeditor.Sensors.NOSE_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_ANKLE_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_ANKLE_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EAR_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EAR_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_ELBOW_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_ELBOW_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_CENTER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_CENTER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_INNER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_INNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_OUTER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_OUTER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_FOOT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_FOOT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_HEEL_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_HEEL_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_HIP_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_HIP_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_KNEE_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_KNEE_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_PINKY_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_PINKY_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_SHOULDER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_SHOULDER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_THUMB_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_THUMB_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_WRIST_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_WRIST_Y
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_DETECTED
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_SIZE
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_X
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_Y
import org.catrobat.catroid.formulaeditor.Sensors.SPEECH_RECOGNITION_LANGUAGE
import org.catrobat.catroid.formulaeditor.Sensors.TEXT_FROM_CAMERA
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class LoadProjectAIExtensionSettingsTest(
    private val name: String,
    private val setting: String,
    private val brick: Brick
) {
    private var initialSettings = mutableMapOf<String, Boolean>()
    val applicationContext: Context = ApplicationProvider.getApplicationContext<Context>()
    var bufferedPrivacyPolicyPreferenceSetting = 0

    private lateinit var script: Script
    private val projectName = "projectName"

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<MainMenuActivity> = BaseActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            *speechRecognitionList,
            *speechSynthetizationList,
            *faceDetectionList,
            *poseDetectionList,
            *textRecognitionList
        )

        private var speechRecognitionLanguage = FormulaElement(SENSOR, SPEECH_RECOGNITION_LANGUAGE.name, null)

        private var faceDetected = FormulaElement(SENSOR, FACE_DETECTED.name, null)
        private var faceSize = FormulaElement(SENSOR, FACE_SIZE.name, null)
        private var faceXPosition = FormulaElement(SENSOR, FACE_X.name, null)
        private var faceYPosition = FormulaElement(SENSOR, FACE_Y.name, null)
        private var secondFaceDetected = FormulaElement(SENSOR, SECOND_FACE_DETECTED.name, null)
        private var secondFaceSize = FormulaElement(SENSOR, SECOND_FACE_SIZE.name, null)
        private var secondFaceXPosition = FormulaElement(SENSOR, SECOND_FACE_X.name, null)
        private var secondFaceYPosition = FormulaElement(SENSOR, SECOND_FACE_Y.name, null)

        private var noseX = FormulaElement(SENSOR, NOSE_X.name, null)
        private var noseY = FormulaElement(SENSOR, NOSE_Y.name, null)
        private var leftEyeInnerX = FormulaElement(SENSOR, LEFT_EYE_INNER_X.name, null)
        private var leftEyeInnerY = FormulaElement(SENSOR, LEFT_EYE_INNER_Y.name, null)
        private var leftEyeCenterX = FormulaElement(SENSOR, LEFT_EYE_CENTER_X.name, null)
        private var leftEyeCenterY = FormulaElement(SENSOR, LEFT_EYE_CENTER_Y.name, null)
        private var leftEyeOuterX = FormulaElement(SENSOR, LEFT_EYE_OUTER_X.name, null)
        private var leftEyeOuterY = FormulaElement(SENSOR, LEFT_EYE_OUTER_Y.name, null)
        private var rightEyeInnerX = FormulaElement(SENSOR, RIGHT_EYE_INNER_X.name, null)
        private var rightEyeInnerY = FormulaElement(SENSOR, RIGHT_EYE_INNER_Y.name, null)
        private var rightEyeCenterX = FormulaElement(SENSOR, RIGHT_EYE_CENTER_X.name, null)
        private var rightEyeCenterY = FormulaElement(SENSOR, RIGHT_EYE_CENTER_Y.name, null)
        private var rightEyeOuterX = FormulaElement(SENSOR, RIGHT_EYE_OUTER_X.name, null)
        private var rightEyeOuterY = FormulaElement(SENSOR, RIGHT_EYE_OUTER_Y.name, null)
        private var leftEarX = FormulaElement(SENSOR, LEFT_EAR_X.name, null)
        private var leftEarY = FormulaElement(SENSOR, LEFT_EAR_Y.name, null)
        private var rightEarX = FormulaElement(SENSOR, RIGHT_EAR_X.name, null)
        private var rightEarY = FormulaElement(SENSOR, RIGHT_EAR_Y.name, null)
        private var mouthLeftCornerX = FormulaElement(SENSOR, MOUTH_LEFT_CORNER_X.name, null)
        private var mouthLeftCornerY = FormulaElement(SENSOR, MOUTH_LEFT_CORNER_Y.name, null)
        private var mouthRightCornerX = FormulaElement(SENSOR, MOUTH_RIGHT_CORNER_X.name, null)
        private var mouthRightCornerY = FormulaElement(SENSOR, MOUTH_RIGHT_CORNER_Y.name, null)
        private var leftShoulderX = FormulaElement(SENSOR, LEFT_SHOULDER_X.name, null)
        private var leftShoulderY = FormulaElement(SENSOR, LEFT_SHOULDER_Y.name, null)
        private var rightShoulderX = FormulaElement(SENSOR, RIGHT_SHOULDER_X.name, null)
        private var rightShoulderY = FormulaElement(SENSOR, RIGHT_SHOULDER_Y.name, null)
        private var leftElbowX = FormulaElement(SENSOR, LEFT_ELBOW_X.name, null)
        private var leftElbowY = FormulaElement(SENSOR, LEFT_ELBOW_Y.name, null)
        private var rightElbowX = FormulaElement(SENSOR, RIGHT_ELBOW_X.name, null)
        private var rightElbowY = FormulaElement(SENSOR, RIGHT_ELBOW_Y.name, null)
        private var leftWristX = FormulaElement(SENSOR, LEFT_WRIST_X.name, null)
        private var leftWristY = FormulaElement(SENSOR, LEFT_WRIST_Y.name, null)
        private var rightWristX = FormulaElement(SENSOR, RIGHT_WRIST_X.name, null)
        private var rightWristY = FormulaElement(SENSOR, RIGHT_WRIST_Y.name, null)
        private var leftPinkyKnuckleX = FormulaElement(SENSOR, LEFT_PINKY_X.name, null)
        private var leftPinkyKnuckleY = FormulaElement(SENSOR, LEFT_PINKY_Y.name, null)
        private var rightPinkyKnuckleX = FormulaElement(SENSOR, RIGHT_PINKY_X.name, null)
        private var rightPinkyKnuckleY = FormulaElement(SENSOR, RIGHT_PINKY_Y.name, null)
        private var leftIndexKnuckleX = FormulaElement(SENSOR, LEFT_INDEX_X.name, null)
        private var leftIndexKnuckleY = FormulaElement(SENSOR, LEFT_INDEX_Y.name, null)
        private var rightIndexKnuckleX = FormulaElement(SENSOR, RIGHT_INDEX_X.name, null)
        private var rightIndexKnuckleY = FormulaElement(SENSOR, RIGHT_INDEX_Y.name, null)
        private var leftThumbKnuckleX = FormulaElement(SENSOR, LEFT_THUMB_X.name, null)
        private var leftThumbKnuckleY = FormulaElement(SENSOR, LEFT_THUMB_Y.name, null)
        private var rightThumbKnuckleX = FormulaElement(SENSOR, RIGHT_THUMB_X.name, null)
        private var rightThumbKnuckleY = FormulaElement(SENSOR, RIGHT_THUMB_Y.name, null)
        private var leftHipX = FormulaElement(SENSOR, LEFT_HIP_X.name, null)
        private var leftHipY = FormulaElement(SENSOR, LEFT_HIP_Y.name, null)
        private var rightHipX = FormulaElement(SENSOR, RIGHT_HIP_X.name, null)
        private var rightHipY = FormulaElement(SENSOR, RIGHT_HIP_Y.name, null)
        private var leftKneeX = FormulaElement(SENSOR, LEFT_KNEE_X.name, null)
        private var leftKneeY = FormulaElement(SENSOR, LEFT_KNEE_Y.name, null)
        private var rightKneeX = FormulaElement(SENSOR, RIGHT_KNEE_X.name, null)
        private var rightKneeY = FormulaElement(SENSOR, RIGHT_KNEE_Y.name, null)
        private var leftAnkleX = FormulaElement(SENSOR, LEFT_ANKLE_X.name, null)
        private var leftAnkleY = FormulaElement(SENSOR, LEFT_ANKLE_Y.name, null)
        private var rightAnkleX = FormulaElement(SENSOR, RIGHT_ANKLE_X.name, null)
        private var rightAnkleY = FormulaElement(SENSOR, RIGHT_ANKLE_Y.name, null)
        private var leftHeelX = FormulaElement(SENSOR, LEFT_HEEL_X.name, null)
        private var leftHeelY = FormulaElement(SENSOR, LEFT_HEEL_Y.name, null)
        private var rightHeelX = FormulaElement(SENSOR, RIGHT_HEEL_X.name, null)
        private var rightHeelY = FormulaElement(SENSOR, RIGHT_HEEL_Y.name, null)
        private var leftFootIndexX = FormulaElement(SENSOR, LEFT_FOOT_INDEX_X.name, null)
        private var leftFootIndexY = FormulaElement(SENSOR, LEFT_FOOT_INDEX_Y.name, null)
        private var rightFootIndexX = FormulaElement(SENSOR, RIGHT_FOOT_INDEX_X.name, null)
        private var rightFootIndexY = FormulaElement(SENSOR, RIGHT_FOOT_INDEX_Y.name, null)

        private var textFromCamera = FormulaElement(SENSOR, TEXT_FROM_CAMERA.name, null)
        private var textBlocksNumber = FormulaElement(SENSOR, Sensors.TEXT_BLOCKS_NUMBER.name, null)
        private var textBlockX = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_X.name, null)
        private var textBlockY = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_Y.name, null)
        private var textBlockSize = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_SIZE.name, null)
        private var textBlockFromCamera = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_FROM_CAMERA.name, null)
        private var textBlockLanguageFromCamera = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_LANGUAGE_FROM_CAMERA.name, null)

        private val speechRecognitionList = arrayOf(
            arrayOf(
                "Speech Recognition AskSpeechBrick", SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                AskSpeechBrick()
            ),
            arrayOf(
                "Speech Recognition StartListeningBrick",
                SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                StartListeningBrick()
            ),
            arrayOf(
                "Speech Recognition SetListeningLanguageBrick",
                SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                SetListeningLanguageBrick()
            ),
            arrayOf(
                "Speech Recognition speechRecognitionLanguage",
                SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(speechRecognitionLanguage))
            )
        )

        private val speechSynthetizationList = arrayOf(
            arrayOf(
                "Speech Synthetization SpeakBrick", SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                SpeakBrick()
            ),
            arrayOf(
                "Speech Synthetization SpeakAndWaitBrick",
                SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                SpeakAndWaitBrick()
            )
        )

        private val faceDetectionList = arrayOf(
            arrayOf(
                "Face Detection faceDetected", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceDetected))
            ),
            arrayOf(
                "Face Detection faceSize", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceSize))
            ),
            arrayOf(
                "Face Detection faceXPosition", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceXPosition))
            ),
            arrayOf(
                "Face Detection faceYPosition", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceYPosition))
            ),
            arrayOf(
                "Face Detection secondFaceDetected",
                SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceDetected))
            ),
            arrayOf(
                "Face Detection secondFaceSize", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceSize))
            ),
            arrayOf(
                "Face Detection secondFaceXPosition",
                SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceXPosition))
            ),
            arrayOf(
                "Face Detection secondFaceYPosition",
                SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceYPosition))
            )
        )

        private val poseDetectionList = arrayOf(
            arrayOf(
                "Pose Detection noseX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(noseX))
            ),
            arrayOf(
                "Pose Detection noseY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(noseY))
            ),
            arrayOf(
                "Pose Detection leftEyeInnerX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeInnerX))
            ),
            arrayOf(
                "Pose Detection leftEyeInnerY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeInnerY))
            ),
            arrayOf(
                "Pose Detection leftEyeCenterX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeCenterX))
            ),
            arrayOf(
                "Pose Detection leftEyeCenterY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeCenterY))
            ),
            arrayOf(
                "Pose Detection leftEyeOuterX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeOuterX))
            ),
            arrayOf(
                "Pose Detection leftEyeOuterY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEyeOuterY))
            ),
            arrayOf(
                "Pose Detection rightEyeInnerX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeInnerX))
            ),
            arrayOf(
                "Pose Detection rightEyeInnerY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeInnerY))
            ),
            arrayOf(
                "Pose Detection rightEyeCenterX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeCenterX))
            ),
            arrayOf(
                "Pose Detection rightEyeCenterY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeCenterY))
            ),
            arrayOf(
                "Pose Detection rightEyeOuterX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeOuterX))
            ),
            arrayOf(
                "Pose Detection rightEyeOuterY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEyeOuterY))
            ),
            arrayOf(
                "Pose Detection leftEarX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEarX))
            ),
            arrayOf(
                "Pose Detection leftEarY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftEarY))
            ),
            arrayOf(
                "Pose Detection rightEarX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEarX))
            ),
            arrayOf(
                "Pose Detection rightEarY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightEarY))
            ),
            arrayOf(
                "Pose Detection mouthLeftCornerX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(mouthLeftCornerX))
            ),
            arrayOf(
                "Pose Detection mouthLeftCornerY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(mouthLeftCornerY))
            ),
            arrayOf(
                "Pose Detection mouthRightCornerX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(mouthRightCornerX))
            ),
            arrayOf(
                "Pose Detection mouthRightCornerY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(mouthRightCornerY))
            ),
            arrayOf(
                "Pose Detection leftShoulderX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftShoulderX))
            ),
            arrayOf(
                "Pose Detection leftShoulderY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftShoulderY))
            ),
            arrayOf(
                "Pose Detection rightShoulderX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightShoulderX))
            ),
            arrayOf(
                "Pose Detection rightShoulderY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightShoulderY))
            ),
            arrayOf(
                "Pose Detection leftElbowX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftElbowX))
            ),
            arrayOf(
                "Pose Detection leftElbowY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftElbowY))
            ),
            arrayOf(
                "Pose Detection rightElbowX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightElbowX))
            ),
            arrayOf(
                "Pose Detection rightElbowY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightElbowY))
            ),
            arrayOf(
                "Pose Detection leftWristX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftWristX))
            ),
            arrayOf(
                "Pose Detection leftWristY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftWristY))
            ),
            arrayOf(
                "Pose Detection rightWristX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightWristX))
            ),
            arrayOf(
                "Pose Detection rightWristY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightWristY))
            ),
            arrayOf(
                "Pose Detection leftPinkyKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftPinkyKnuckleX))
            ),
            arrayOf(
                "Pose Detection leftPinkyKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftPinkyKnuckleY))
            ),
            arrayOf(
                "Pose Detection rightPinkyKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightPinkyKnuckleX))
            ),
            arrayOf(
                "Pose Detection rightPinkyKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightPinkyKnuckleY))
            ),
            arrayOf(
                "Pose Detection leftIndexKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftIndexKnuckleX))
            ),
            arrayOf(
                "Pose Detection leftIndexKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftIndexKnuckleY))
            ),
            arrayOf(
                "Pose Detection rightIndexKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightIndexKnuckleX))
            ),
            arrayOf(
                "Pose Detection rightIndexKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightIndexKnuckleY))
            ),
            arrayOf(
                "Pose Detection leftThumbKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftThumbKnuckleX))
            ),
            arrayOf(
                "Pose Detection leftThumbKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftThumbKnuckleY))
            ),
            arrayOf(
                "Pose Detection rightThumbKnuckleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightThumbKnuckleX))
            ),
            arrayOf(
                "Pose Detection rightThumbKnuckleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightThumbKnuckleY))
            ),
            arrayOf(
                "Pose Detection leftHipX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftHipX))
            ),
            arrayOf(
                "Pose Detection leftHipY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftHipY))
            ),
            arrayOf(
                "Pose Detection rightHipX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightHipX))
            ),
            arrayOf(
                "Pose Detection rightHipY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightHipY))
            ),
            arrayOf(
                "Pose Detection leftKneeX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftKneeX))
            ),
            arrayOf(
                "Pose Detection leftKneeY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftKneeY))
            ),
            arrayOf(
                "Pose Detection rightKneeX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightKneeX))
            ),
            arrayOf(
                "Pose Detection rightKneeY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightKneeY))
            ),
            arrayOf(
                "Pose Detection leftAnkleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftAnkleX))
            ),
            arrayOf(
                "Pose Detection leftAnkleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftAnkleY))
            ),
            arrayOf(
                "Pose Detection rightAnkleX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightAnkleX))
            ),
            arrayOf(
                "Pose Detection rightAnkleY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightAnkleY))
            ),
            arrayOf(
                "Pose Detection leftHeelX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftHeelX))
            ),
            arrayOf(
                "Pose Detection leftHeelY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftHeelY))
            ),
            arrayOf(
                "Pose Detection rightHeelX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightHeelX))
            ),
            arrayOf(
                "Pose Detection rightHeelY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightHeelY))
            ),
            arrayOf(
                "Pose Detection leftFootIndexX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftFootIndexX))
            ),
            arrayOf(
                "Pose Detection leftFootIndexY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(leftFootIndexY))
            ),
            arrayOf(
                "Pose Detection rightFootIndexX", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightFootIndexX))
            ),
            arrayOf(
                "Pose Detection rightFootIndexY", SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(rightFootIndexY))
            )
        )

        private val textRecognitionList = arrayOf(
            arrayOf(
                "Text Recognition textFromCameraSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textFromCamera))
            ),
            arrayOf(
                "Text Recognition textBlocksNumberSensor",
                SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlocksNumber))
            ),
            arrayOf(
                "Text Recognition textBlockXSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockX))
            ),
            arrayOf(
                "Text Recognition textBlockYSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockY))
            ),
            arrayOf(
                "Text Recognition textBlockSizeSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockSize))
            ),
            arrayOf(
                "Text Recognition textBlockFromCameraSensor",
                SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockFromCamera))
            ),
            arrayOf(
                "Text Recognition textBlockLanguageFromCameraSensor",
                SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockLanguageFromCamera))
            )
        )
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        saveInitialSettings()

        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit().putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            ).commit()

        allAIExtensionSettings.forEach { setting -> setSettingToBoolean(setting, false) }

        script = UiTestUtils.createProjectAndGetStartScript(projectName)
        baseActivityTestRule.launchActivity(null)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        restoreInitialSettings()
    }

    @Test
    fun testSettingsBeforeAndAfterLoadProject() {
        script.addBrick(brick)

        assertFalse(getSetting(setting))
        onView(ViewMatchers.withText(applicationContext.getString(R.string.main_menu_programs))).perform(
            click()
        )
        onView(ViewMatchers.withText(projectName)).perform(click())
        assertTrue(getSetting(setting))
    }

    private fun getSetting(setting: String): Boolean {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        return sharedPreferences.getBoolean(setting, false)
    }

    private fun saveInitialSettings() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        allAIExtensionSettings.forEach { setting ->
            initialSettings[setting] = sharedPreferences.getBoolean(setting, false)
        }
        bufferedPrivacyPolicyPreferenceSetting =
            sharedPreferences.getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
    }

    private fun restoreInitialSettings() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        allAIExtensionSettings.forEach { setting ->
            sharedPreferencesEditor.putBoolean(
                setting,
                initialSettings.getOrDefault(setting, false)
            )
        }
        sharedPreferencesEditor.putInt(
            SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
            bufferedPrivacyPolicyPreferenceSetting
        )

        sharedPreferencesEditor.commit()
    }

    private val allAIExtensionSettings: List<String> = listOf(
        SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
        SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
        SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
        SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
        SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
    )

    private fun setSettingToBoolean(setting: String, value: Boolean) {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        sharedPreferencesEditor.putBoolean(setting, value)
        sharedPreferencesEditor.commit()
    }
}
