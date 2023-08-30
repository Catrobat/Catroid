/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.io.catrobatlanguage

import android.content.Context
import android.widget.AdapterView
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class SpinnerSerializationTest {
    companion object {
        val TEST_LANGUAGES: Array<String> = arrayOf("en-GB", "de")
    }
    private lateinit var startScript: StartScript

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testCloneBrick() {
        val brick = CloneBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()

            val yourselfString = getStringForResourceId(R.string.brick_clone_this)
            executeTest(
                R.id.brick_clone_spinner,
                brick,
                "Create clone of (actor or object: (yourself));\n",
                mapOf(
                    "testSprite1" to "Create clone of (actor or object: ('testSprite1'));\n",
                    "testSprite2" to "Create clone of (actor or object: ('testSprite2'));\n",
                    "testSprite3" to "Create clone of (actor or object: ('testSprite3'));\n",
                    yourselfString to "Create clone of (actor or object: (yourself));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testForItemInUserListBrick() {
        val brick = ForItemInUserListBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.for_item_in_userlist_list_spinner,
                brick,
                "For each value in list (value: (\"var1\"), list: (*list1*)) {\n}\n",
                mapOf(
                    "list2" to "For each value in list (value: (\"var1\"), list: (*list2*)) {\n}\n",
                    "list3" to "For each value in list (value: (\"var1\"), list: (*list3*)) {\n}\n",
                    "list1" to "For each value in list (value: (\"var1\"), list: (*list1*)) {\n}\n"
                ),
                R.id.for_item_in_userlist_variable_spinner,
                mapOf(
                    "var2" to "For each value in list (value: (\"var2\"), list: (*list1*)) {\n}\n",
                    "var3" to "For each value in list (value: (\"var3\"), list: (*list1*)) {\n}\n",
                    "var1" to "For each value in list (value: (\"var1\"), list: (*list1*)) {\n}\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSceneTransitionBrick() {
        val brick = SceneTransitionBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_scene_transition_spinner,
                brick,
                "Continue (scene: ('s1'));\n",
                mapOf(
                    "s2" to "Continue (scene: ('s2'));\n",
                    "s3" to "Continue (scene: ('s3'));\n",
                    "s1" to "Continue (scene: ('s1'));\n",
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSceneStartBrick() {
        val brick = SceneStartBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_scene_start_spinner,
                brick,
                "Start (scene: ('${projectManager.currentProject.defaultScene.name}'));\n",
                mapOf(
                    "s1" to "Start (scene: ('s1'));\n",
                    "s2" to "Start (scene: ('s2'));\n",
                    "s3" to "Start (scene: ('s3'));\n",
                    projectManager.currentProject.defaultScene.name to "Start (scene: ('${projectManager.currentProject.defaultScene.name}'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testStopScriptBrick() {
        val brick = StopScriptBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.brick_stop_all_scripts) to "Stop (script: (all scripts));\n",
                getStringForResourceId(R.string.brick_stop_other_scripts) to "Stop (script: (other scripts of this actor or object));\n",
                getStringForResourceId(R.string.brick_stop_this_script) to "Stop (script: (this script));\n",
            )
            executeTest(
                R.id.brick_stop_script_spinner,
                brick,
                "Stop (script: (this script));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_stop_script_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testGoToBrick() {
        val brick = GoToBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_go_to_spinner,
                brick,
                "Go to (target: (touch position));\n",
                mapOf(
                    getStringForResourceId(R.string.brick_go_to_random_position) to "Go to (target: (random position));\n",
                    "testSprite3" to "Go to (target: ('testSprite3'));\n",
                    getStringForResourceId(R.string.brick_go_to_touch_position) to "Go to (target: (touch position));\n",
                )
            )

            val size = projectManager.currentProject.defaultScene.spriteList.size
            checkSpinnerValueCount(brick, R.id.brick_go_to_spinner, size)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testPointToBrick() {
        val brick = PointToBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_point_to_spinner,
                brick,
                "Point towards (actor or object: ('testSprite1'));\n",
                mapOf(
                    "testSprite2" to "Point towards (actor or object: ('testSprite2'));\n",
                    "testSprite3" to "Point towards (actor or object: ('testSprite3'));\n",
                    "testSprite1" to "Point towards (actor or object: ('testSprite1'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSetRotationStyleBrick() {
        val brick = SetRotationStyleBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.brick_set_rotation_style_normal) to "Set (rotation style: (all-around));\n",
                getStringForResourceId(R.string.brick_set_rotation_style_no) to "Set (rotation style: (don't rotate));\n",
                getStringForResourceId(R.string.brick_set_rotation_style_lr) to "Set (rotation style: (left-right only));\n",
            )
            executeTest(
                R.id.brick_set_rotation_style_spinner,
                brick,
                "Set (rotation style: (left-right only));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_set_rotation_style_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSetPhysicsObjectTypeBrick() {
        val brick = SetPhysicsObjectTypeBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val spinnerValues = getStringArrayForResourceId(R.array.physics_object_types)
            executeTest(
                R.id.brick_set_physics_object_type_spinner,
                brick,
                "Set (motion type: (not moving or bouncing under gravity (default)));\n",
                mapOf(
                    spinnerValues[0] to "Set (motion type: (moving and bouncing under gravity));\n",
                    spinnerValues[1] to "Set (motion type: (not moving under gravity, but others bounce off you under gravity));\n",
                    spinnerValues[2] to "Set (motion type: (not moving or bouncing under gravity (default)));\n",
                )
            )
            checkSpinnerValueCount(brick, R.id.brick_set_physics_object_type_spinner, 3)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testFadeParticleEffectBrick() {
        val brick = FadeParticleEffectBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_fade_particle_effect_spinner,
                brick,
                "Fade particle (effect: (in));\n",
                mapOf(
                    getStringForResourceId(R.string.particle_effects_fade_out) to "Fade particle (effect: (out));\n",
                    getStringForResourceId(R.string.particle_effects_fade_in) to "Fade particle (effect: (in));\n",
                )
            )
            checkSpinnerValueCount(brick, R.id.brick_fade_particle_effect_spinner, 2)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testPlaySoundBrick() {
        val brick = PlaySoundBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_play_sound_spinner,
                brick,
                "Start (sound: ('sound1'));\n",
                mapOf(
                    "sound2" to "Start (sound: ('sound2'));\n",
                    "sound3" to "Start (sound: ('sound3'));\n",
                    "sound1" to "Start (sound: ('sound1'));\n",
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testPlaySoundAndWaitBrick() {
        val brick = PlaySoundAndWaitBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_play_sound_spinner,
                brick,
                "Start sound and wait (sound: ('sound1'));\n",
                mapOf(
                    "sound2" to "Start sound and wait (sound: ('sound2'));\n",
                    "sound3" to "Start sound and wait (sound: ('sound3'));\n",
                    "sound1" to "Start sound and wait (sound: ('sound1'));\n",
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testPlaySoundAtBrick() {
        val brick = PlaySoundAtBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_play_sound_at_spinner,
                brick,
                "Start sound and skip seconds (sound: ('sound1'), seconds: (0));\n",
                mapOf(
                    "sound2" to "Start sound and skip seconds (sound: ('sound2'), seconds: (0));\n",
                    "sound3" to "Start sound and skip seconds (sound: ('sound3'), seconds: (0));\n",
                    "sound1" to "Start sound and skip seconds (sound: ('sound1'), seconds: (0));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testStopSoundBrick() {
        val brick = StopSoundBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_stop_sound_spinner,
                brick,
                "Stop (sound: ('sound1'));\n",
                mapOf(
                    "sound2" to "Stop (sound: ('sound2'));\n",
                    "sound3" to "Stop (sound: ('sound3'));\n",
                    "sound1" to "Stop (sound: ('sound1'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSetInstrumentBrick() {
        val brick = SetInstrumentBrick()
        startScript.addBrick(brick)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfInstruments = getAllInstruments(getStringForResourceId(R.string.piano), "Set (instrument: ({{INSTRUMENT}}));\n")
            executeTest(
                R.id.set_instrument_spinner,
                brick,
                "Set (instrument: (piano));\n",
                mapOfInstruments
            )
            checkSpinnerValueCount(brick, R.id.set_instrument_spinner, mapOfInstruments.size)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testPlayDrumForBeatsBrick() {
        val brick = PlayDrumForBeatsBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfDrums = getAllDrums(getStringForResourceId(R.string.snare_drum), "Play (drum: ({{DRUM}}), number of beats: (0));\n")
            executeTest(
                R.id.play_drum_for_beats_spinner,
                brick,
                "Play (drum: (snare drum), number of beats: (0));\n",
                mapOfDrums
            )
            checkSpinnerValueCount(brick, R.id.play_drum_for_beats_spinner, mapOfDrums.size)
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSetLookBrick() {
        val brick = SetLookBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_set_look_spinner,
                brick,
                "Switch to (look: ('spritelook1'));\n",
                mapOf(
                    "spritelook2" to "Switch to (look: ('spritelook2'));\n",
                    "spritelook3" to "Switch to (look: ('spritelook3'));\n",
                    "spritelook1" to "Switch to (look: ('spritelook1'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testAskSpeechBrick() {
        val brick = AskSpeechBrick("my question")
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_ask_speech_spinner,
                brick,
                "Ask question and store written answer to variable (question: ('my question'), variable: (\"var1\"));\n",
                mapOf(
                    "var2" to "Ask question and store written answer to variable (question: ('my question'), variable: (\"var2\"));\n",
                    "var3" to "Ask question and store written answer to variable (question: ('my question'), variable: (\"var3\"));\n",
                    "var1" to "Ask question and store written answer to variable (question: ('my question'), variable: (\"var1\"));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testShowTextColorSizeAlignmentBrick() {
        val brick = ShowTextColorSizeAlignmentBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_show_variable_color_size_align_spinner,
                brick,
                "Show (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (centered));\n",
                mapOf(
                    getStringForResourceId(R.string.brick_show_variable_aligned_left) to "Show (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (left));\n",
                    getStringForResourceId(R.string.brick_show_variable_aligned_right) to "Show (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (right));\n",
                    getStringForResourceId(R.string.brick_show_variable_aligned_centered) to "Show (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (centered));\n"
                ),
                R.id.show_variable_color_size_spinner,
                mapOf(
                    "var2" to "Show (variable: (\"var2\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (centered));\n",
                    "var3" to "Show (variable: (\"var3\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (centered));\n",
                    "var1" to "Show (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (#000000), alignment: (centered));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }

    }

    @Test
    fun testSetBackgroundBrick() {
        val brick = SetBackgroundBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_set_background_spinner,
                brick,
                "Set background to (look: ('look1'));\n",
                mapOf(
                    "look2" to "Set background to (look: ('look2'));\n",
                    "look3" to "Set background to (look: ('look3'));\n",
                    "look1" to "Set background to (look: ('look1'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testSetBackgroundAndWaitBrick() {
        val brick = SetBackgroundAndWaitBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_set_background_spinner,
                brick,
                "Set background and wait (look: ('look1'));\n",
                mapOf(
                    "look2" to "Set background and wait (look: ('look2'));\n",
                    "look3" to "Set background and wait (look: ('look3'));\n",
                    "look1" to "Set background and wait (look: ('look1'));\n"
                )
            )
            baseActivityTestRule.finishActivity()
        }
    }

    @Test
    fun testCameraBrick() {
        val brick = CameraBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.video_brick_camera_off) to "Turn (camera: (off));\n",
                getStringForResourceId(R.string.video_brick_camera_on) to "Turn (camera: (on));\n",
            )
            executeTest(
                R.id.brick_video_spinner,
                brick,
                "Turn (camera: (on));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_video_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
        }

    }

    @Test
    fun testChooseCameraBrick() {
        val brick = ChooseCameraBrick()
        startScript.addBrick(brick)
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.choose_camera_back) to "Use (camera: (rear));\n",
                getStringForResourceId(R.string.choose_camera_front) to "Use (camera: (front));\n",
            )
            executeTest(
                R.id.brick_choose_camera_spinner,
                brick,
                "Use (camera: (front));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_choose_camera_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
        }
    }
//
//    @Test
//    fun testFlashBrick() {
//        executeTest(
//            R.id.brick_flash_spinner,
//            FlashBrick(),
//            "Turn (flashlight: (on));\n",
//            mapOf(
//                "off" to "Turn (flashlight: (off));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testSetVariableBrick() {
//        // TODO: right spinner?
//        executeTest(
//            R.id.set_variable_spinner,
//            SetVariableBrick(),
//            "Set (variable: (\"var1\"), value: (0));\n",
//            mapOf(
//                "var2" to "Set (variable: (\"var2\"), value: (0));\n",
//                "var3" to "Set (variable: (\"var3\"), value: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testChangeVariableBrick() {
//        executeTest(
//            R.id.change_variable_spinner,
//            ChangeVariableBrick(),
//            "Change (variable: (\"var1\"), value: (0));\n",
//            mapOf(
//                "var2" to "Change (variable: (\"var2\"), value: (0));\n",
//                "var3" to "Change (variable: (\"var3\"), value: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testHideTextBrick() {
//        executeTest(
//            R.id.hide_variable_spinner,
//            HideTextBrick(),
//            "Hide (variable: (\"var1\"));\n",
//            mapOf(
//                "var2" to "Hide (variable: (\"var2\"));\n",
//                "var3" to "Hide (variable: (\"var3\"));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWriteVariableOnDeviceBrick() {
//        executeTest(
//            R.id.write_variable_spinner,
//            WriteVariableOnDeviceBrick(),
//            "Write on device (variable: (\"var1\"));\n",
//            mapOf(
//                "var2" to "Write on device (variable: (\"var2\"));\n",
//                "var3" to "Write on device (variable: (\"var3\"));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testReadVariableFromDeviceBrick() {
//        executeTest(
//            R.id.read_variable_from_device_spinner,
//            ReadVariableFromDeviceBrick(),
//            "Read from device (variable: (\"var1\"));\n",
//            mapOf(
//                "var2" to "Read from device (variable: (\"var2\"));\n",
//                "var3" to "Read from device (variable: (\"var3\"));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWriteVariableToFileBrick() {
//        executeTest(
//            R.id.brick_write_variable_to_file_spinner,
//            WriteVariableToFileBrick(),
//            "Write to file (variable: (\"var1\"), file: (0));\n",
//            mapOf(
//                "var2" to "Write to file (variable: (\"var2\"), file: (0));\n",
//                "var3" to "Write to file (variable: (\"var3\"), file: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testReadVariableFromFileBrick() {
//        executeTest(
//            R.id.brick_read_variable_from_file_spinner_variable,
//            ReadVariableFromFileBrick(),
//            "Read from file (variable: (\"var1\"), file: (0), action: (keep the file));\n",
//            mapOf(
//                "var2" to "Read from file (variable: (\"var2\"), file: (0), action: (keep the file));\n",
//                "var3" to "Read from file (variable: (\"var3\"), file: (0), action: (keep the file));\n"
//            ),
//            R.id.brick_read_variable_from_file_spinner_mode,
//            mapOf(
//                "delete the file" to "Read from file (variable: (\"var3\"), file: (0), action: (delete the file));\n",
//                "keep the file" to "Read from file (variable: (\"var3\"), file: (0), action: (keep the file));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testAddItemToUserListBrick() {
//        executeTest(
//            R.id.add_item_to_userlist_spinner,
//            ForItemInUserListBrick(),
//            "Add (list: (*list1*), item: (0));\n",
//            mapOf(
//                "list2" to "Add (list: (*list2*), item: (0));\n",
//                "list3" to "Add (list: (*list3*), item: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testDeleteItemOfUserListBrick() {
//        executeTest(
//            R.id.delete_item_of_userlist_spinner,
//            DeleteItemOfUserListBrick(),
//            "Delete item at (list: (*list1*), position: (0));\n",
//            mapOf(
//                "list2" to "Delete item at (list: (*list2*), position: (0));\n",
//                "list3" to "Delete item at (list: (*list3*), position: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testClearUserListBrick() {
//        executeTest(
//            R.id.clear_userlist_spinner,
//            ClearUserListBrick(),
//            "Delete all items (list: (*list1*));\n",
//            mapOf(
//                "list2" to "Delete all items (list: (*list2*));\n",
//                "list3" to "Delete all items (list: (*list3*));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testInsertItemIntoUserListBrick() {
//        executeTest(
//            R.id.insert_item_into_userlist_spinner,
//            InsertItemIntoUserListBrick(),
//            "Insert (list: (*list1*), position: (0), value: (0));\n",
//            mapOf(
//                "list2" to "Insert (list: (*list2*), position: (0), value: (0));\n",
//                "list3" to "Insert (list: (*list3*), position: (0), value: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testReplaceItemInUserListBrick() {
//        executeTest(
//            R.id.replace_item_in_userlist_spinner,
//            ReplaceItemInUserListBrick(),
//            "Replace (list: (*list1*), position: (0), value: (0));\n",
//            mapOf(
//                "list2" to "Replace (list: (*list2*), position: (0), value: (0));\n",
//                "list3" to "Replace (list: (*list3*), position: (0), value: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWriteListOnDeviceBrick() {
//        executeTest(
//            R.id.write_list_spinner,
//            WriteListOnDeviceBrick(),
//            "Write on device (list: (*list1*));\n",
//            mapOf(
//                "list2" to "Write on device (list: (*list2*));\n",
//                "list3" to "Write on device (list: (*list3*));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testReadListFromDeviceBrick() {
//        executeTest(
//            R.id.read_list_from_device_spinner,
//            ReadListFromDeviceBrick(),
//            "Read from device (list: (*list1*));\n",
//            mapOf(
//                "list2" to "Read from device (list: (*list2*));\n",
//                "list3" to "Read from device (list: (*list3*));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testStoreCSVIntoUserListBrick() {
//        executeTest(
//            R.id.brick_store_csv_into_userlist_spinner,
//            ReadListFromDeviceBrick(),
//            "Store column of comma-separated values to list (list: (*list1*), csv: (0), column: (0));\n",
//            mapOf(
//                "list2" to "Store CSV into (list: (*list2*), csv: (0), column: (0));\n",
//                "list3" to "Store CSV into (list: (*list3*), csv: (0), column: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWebRequestBrick() {
//        executeTest(
//            R.id.web_request_spinner,
//            WebRequestBrick(),
//            "Send web request (url: (0), answer variable: (\"var1\"));\n",
//            mapOf(
//                "var2" to "Send web request (url: (0), answer variable: (\"var2\"));\n",
//                "var3" to "Send web request (url: (0), answer variable: (\"var3\"));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoNxtMotorTurnAngleBrick() {
//        executeTest(
//            R.id.lego_motor_turn_angle_spinner,
//            LegoNxtMotorTurnAngleBrick(),
//            "Turn NXT (motor: (A), degrees: (0));\n",
//            mapOf(
//                "B" to "Turn NXT (motor: (B), degrees: (0));\n",
//                "C" to "Turn NXT (motor: (C), degrees: (0));\n",
//                "B+C" to "Turn NXT (motor: (B+C), degrees: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoNxtMotorStopBrick() {
//        executeTest(
//            R.id.stop_motor_spinner,
//            LegoNxtMotorStopBrick(),
//            "Stop NXT (motor: (A));\n",
//            mapOf(
//                "B" to "Stop NXT (motor: (B));\n",
//                "C" to "Stop NXT (motor: (C));\n",
//                "B+C" to "Stop NXT (motor: (B+C));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoNxtMotorMoveBrick() {
//        executeTest(
//            R.id.lego_motor_action_spinner,
//            LegoNxtMotorMoveBrick(),
//            "Move NXT (motor: (A), speed percentage: (0));\n",
//            mapOf(
//                "B" to "Move NXT (motor: (B), speed percentage: (0));\n",
//                "C" to "Move NXT (motor: (C), speed percentage: (0));\n",
//                "B+C" to "Move NXT (motor: (B+C), speed percentage: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoEv3MotorTurnAngleBrick() {
//        executeTest(
//            R.id.lego_ev3_motor_turn_angle_spinner,
//            LegoEv3MotorTurnAngleBrick(),
//            "Turn EV3 (motor: (A), degrees: (0));\n",
//            mapOf(
//                "B" to "Turn EV3 (motor: (B), degrees: (0));\n",
//                "C" to "Turn EV3 (motor: (C), degrees: (0));\n",
//                "B+C" to "Turn EV3 (motor: (B+C), degrees: (0));\n",
//                "All" to "Turn EV3 (motor: (All), degrees: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoEv3MotorMoveBrick() {
//        executeTest(
//            R.id.brick_ev3_motor_move_spinner,
//            LegoEv3MotorMoveBrick(),
//            "Set EV3 (motor: (A), speed percentage: (0));\n",
//            mapOf(
//                "B" to "Set EV3 (motor: (B), speed percentage: (0));\n",
//                "C" to "Set EV3 (motor: (C), speed percentage: (0));\n",
//                "B+C" to "Set EV3 (motor: (B+C), speed percentage: (0));\n",
//                "All" to "Set EV3 (motor: (All), speed percentage: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoEv3MotorStopBrick() {
//        executeTest(
//            R.id.ev3_stop_motor_spinner,
//            LegoEv3MotorStopBrick(),
//            "Stop EV3 (motor: (A));\n",
//            mapOf(
//                "B" to "Stop EV3 (motor: (B));\n",
//                "C" to "Stop EV3 (motor: (C));\n",
//                "B+C" to "Stop EV3 (motor: (B+C));\n",
//                "All" to "Stop EV3 (motor: (All));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testLegoEv3SetLedBrick() {
//        executeTest(
//            R.id.brick_ev3_set_led_spinner,
//            LegoEv3SetLedBrick(),
//            "Set EV3 (status: (green));\n",
//            mapOf(
//                "red" to "Set EV3 (status: (red));\n",
//                "orange" to "Set EV3 (status: (orange));\n",
//                "green" to "Set EV3 (status: (green));\n",
//                "red flashing" to "Set EV3 (status: (red flashing));\n",
//                "orange flashing" to "Set EV3 (status: (orange flashing));\n",
//                "green flashing" to "Set EV3 (status: (green flashing));\n",
//                "red pulse" to "Set EV3 (status: (red pulse));\n",
//                "orange pulse" to "Set EV3 (status: (orange pulse));\n",
//                "green pulse" to "Set EV3 (status: (green pulse));\n",
//                "off" to "Set EV3 (status: (off));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testDronePlayLedAnimationBrick() {
//        executeTest(
//            R.id.brick_drone_play_led_animation_spinner,
//            DronePlayLedAnimationBrick(),
//            "Play AR.Drone 2.0 (flash animation: (blink green red));\n",
//            mapOf(
//                "blink green" to "Play AR.Drone 2.0 (flash animation: (blink green));\n",
//                "blink red" to "Play AR.Drone 2.0 (flash animation: (blink red));\n",
//                "blink orange" to "Play AR.Drone 2.0 (flash animation: (blink orange));\n",
//                "snake green red" to "Play AR.Drone 2.0 (flash animation: (snake green red));\n",
//                "fire" to "Play AR.Drone 2.0 (flash animation: (fire));\n",
//                "standard" to "Play AR.Drone 2.0 (flash animation: (standard));\n",
//                "red" to "Play AR.Drone 2.0 (flash animation: (red));\n",
//                "green" to "Play AR.Drone 2.0 (flash animation: (green));\n",
//                "red snake" to "Play AR.Drone 2.0 (flash animation: (red snake));\n",
//                "blank" to "Play AR.Drone 2.0 (flash animation: (blank));\n",
//                "right missile" to "Play AR.Drone 2.0 (flash animation: (right missile));\n",
//                "left missile" to "Play AR.Drone 2.0 (flash animation: (left missile));\n",
//                "double missle" to "Play AR.Drone 2.0 (flash animation: (double missle));\n",
//                "front left green others red" to "Play AR.Drone 2.0 (flash animation: (front left green others red));\n",
//                "front right green others red" to "Play AR.Drone 2.0 (flash animation: (front right green others red));\n",
//                "rear right green others red" to "Play AR.Drone 2.0 (flash animation: (rear right green others red));\n",
//                "rear left green others red" to "Play AR.Drone 2.0 (flash animation: (rear left green others red));\n",
//                "left green right red" to "Play AR.Drone 2.0 (flash animation: (left green right red));\n",
//                "left red right green" to "Play AR.Drone 2.0 (flash animation: (left red right green));\n",
//                "blink standard" to "Play AR.Drone 2.0 (flash animation: (blink standard));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testJumpingSumoAnimationsBrick() {
//        executeTest(
//            R.id.brick_jumping_sumo_animation_spinner,
//            JumpingSumoAnimationsBrick(),
//            "Play Jumping Sumo (animation: (spin));\n",
//            mapOf(
//                "tab" to "Play Jumping Sumo (animation: (tab));\n",
//                "slowshake" to "Play Jumping Sumo (animation: (slowshake));\n",
//                "metronome" to "Play Jumping Sumo (animation: (metronome));\n",
//                "ondulation" to "Play Jumping Sumo (animation: (ondulation));\n",
//                "spinjump" to "Play Jumping Sumo (animation: (spinjump));\n",
//                "spiral" to "Play Jumping Sumo (animation: (spiral));\n",
//                "slalom" to "Play Jumping Sumo (animation: (slalom));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testJumpingSumoSoundBrick() {
//        executeTest(
//            R.id.brick_jumping_sumo_sound_spinner,
//            JumpingSumoSoundBrick(),
//            "Play Jumping Sumo (sound: (default), volume: (0));\n",
//            mapOf(
//                "robot" to "Play Jumping Sumo (sound: (robot), volume: (0));\n",
//                "insect" to "Play Jumping Sumo (sound: (insect), volume: (0));\n",
//                "monster" to "Play Jumping Sumo (sound: (monster), volume: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroMotorMoveForwardBrick() {
//        executeTest(
//            R.id.brick_phiro_motor_forward_action_spinner,
//            PhiroMotorMoveForwardBrick(),
//            "Move Phiro (motor: (left), direction: (forward), speed percentage: (0));\n",
//            mapOf(
//                "right" to "Move Phiro (motor: (right), direction: (forward), speed percentage: (0));\n",
//                "both" to "Move Phiro (motor: (both), direction: (forward), speed percentage: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroMotorMoveBackwardBrick() {
//        executeTest(
//            R.id.brick_phiro_motor_forward_action_spinner,
//            PhiroMotorMoveBackwardBrick(),
//            "Move Phiro (motor: (left), direction: (backward), speed percentage: (0));\n",
//            mapOf(
//                "right" to "Move Phiro (motor: (right), direction: (backward), speed percentage: (0));\n",
//                "both" to "Move Phiro (motor: (both), direction: (backward), speed percentage: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroMotorStopBrick() {
//        executeTest(
//            R.id.brick_phiro_stop_motor_spinner,
//            PhiroMotorStopBrick(),
//            "Stop Phiro (motor: (both));\n",
//            mapOf(
//                "right" to "Stop Phiro (motor: (right));\n",
//                "left" to "Stop Phiro (motor: (left));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroPlayToneBrick() {
//        executeTest(
//            R.id.brick_phiro_select_tone_spinner,
//            PhiroPlayToneBrick(),
//            "Play Phiro (tone: (do), seconds: (0));\n",
//            mapOf(
//                "Re" to "Play Phiro (tone: (re), seconds: (0));\n",
//                "Mi" to "Play Phiro (tone: (mi), seconds: (0));\n",
//                "Fa" to "Play Phiro (tone: (fa), seconds: (0));\n",
//                "So" to "Play Phiro (tone: (so), seconds: (0));\n",
//                "La" to "Play Phiro (tone: (la), seconds: (0));\n",
//                "Ti" to "Play Phiro (tone: (ti), seconds: (0));\n",
//                "Do" to "Play Phiro (tone: (do), seconds: (0));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroRGBLightBrick() {
//        executeTest(
//            R.id.brick_phiro_rgb_light_spinner,
//            PhiroRGBLightBrick(),
//            "Set Phiro (light: (both), color: (#000000));\n",
//            mapOf(
//                "Left" to "Set Phiro (light: (left), color: (#000000));\n",
//                "Right" to "Set Phiro (light: (right), color: (#000000));\n",
//                "Both" to "Set Phiro (light: (both), color: (#000000));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testPhiroIfLogicBeginBrick() {
//        executeTest(
//            R.id.brick_phiro_sensor_action_spinner,
//            PhiroIfLogicBeginBrick(),
//            "If (activated phiro: (front left sensor)) {\n} else {\n}\n",
//            mapOf(
//                "front right sensor" to "If (activated phiro: (front right sensor)) {\n} else {\n}\n",
//                "side left sensor" to "If (activated phiro: (side left sensor)) {\n} else {\n}\n",
//                "side right sensor" to "If (activated phiro: (side right sensor)) {\n} else {\n}\n",
//                "bottom left sensor" to "If (activated phiro: (bottom left sensor)) {\n} else {\n}\n",
//                "bottom right sensor" to "If (activated phiro: (bottom right sensor)) {\n} else {\n}\n"
//            )
//        )
//    }
//
//    @Test
//    fun testAssertUserListsBrick() {
//        executeTest(
//            R.id.brick_assert_lists_actual,
//            AssertUserListsBrick(),
//            "Assert that (list: (*list1*)) is equal to (list: (*list1*));\n",
//            mapOf(
//                "list2" to "Assert that (list: (*list2*)) is equal to (list: (*list1*));\n",
//                "list3" to "Assert that (list: (*list3*)) is equal to (list: (*list1*));\n"
//            ),
//            R.id.brick_assert_lists_expected,
//            mapOf(
//                "list2" to "Assert that (list: (*list3*)) is equal to (list: (*list2*));\n",
//                "list3" to "Assert that (list: (*list3*)) is equal to (list: (*list3*));\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWhenGamepadButtonBrick() {
//        executeTest(
//            R.id.brick_when_gamepad_button_spinner,
//            WhenGamepadButtonBrick(),
//            "When tapped (gamepad button: (A)) {\n}\n",
//            mapOf(
//                "B" to "When tapped (gamepad button: (B)) {\n}\n",
//                "up" to "When tapped (gamepad button: (up)) {\n}\n",
//                "down" to "When tapped (gamepad button: (down)) {\n}\n",
//                "left" to "When tapped (gamepad button: (left)) {\n}\n",
//                "right" to "When tapped (gamepad button: (right)) {\n}\n",
//                "center" to "When tapped (gamepad button: (center)) {\n}\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWhenBounceOffBrick() {
//        executeTest(
//            R.id.brick_when_bounce_off_spinner,
//            WhenBounceOffBrick(),
//            "When you bounce off (actor or object: ('testSprite')) {\n}\n",
//            mapOf(
//                "testSprite1" to "When you bounce off (actor or object: ('testSprite1')) {\n}\n",
//                "testSprite2" to "When you bounce off (actor or object: ('testSprite2')) {\n}\n",
//                "testSprite3" to "When you bounce off (actor or object: ('testSprite3')) {\n}\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWhenBackgroundChangesBrick() {
//        executeTest(
//            R.id.brick_when_background_spinner,
//            WhenBackgroundChangesBrick(),
//            "When background changes to (look: ('look1')) {\n}\n",
//            mapOf(
//                "look2" to "When background changes to (look: ('look2')) {\n}\n",
//                "look3" to "When background changes to (look: ('look3')) {\n}\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWhenRaspiPinChangedBrick() {
//        executeTest(
//            R.id.brick_raspi_when_pinspinner,
//            WhenRaspiPinChangedBrick(),
//            "When Raspberry Pi pin changes to (pin: (3), position: (high)) {\n}\n",
//            mapOf(
//                "5" to "When Raspberry Pi pin changes to (pin: (5), position: (high)) {\n}\n",
//                "7" to "When Raspberry Pi pin changes to (pin: (7), position: (high)) {\n}\n"
//            ),
//            R.id.brick_raspi_when_valuespinner,
//            mapOf(
//                "low" to "When Raspberry Pi pin changes to (pin: (7), position: (low)) {\n}\n",
//                "high" to "When Raspberry Pi pin changes to (pin: (7), position: (high)) {\n}\n"
//            )
//        )
//    }
//
//    @Test
//    fun testWhenNfcBrick() {
//        // TODO: add NFC tags to test project
//        executeTest(
//            R.id.brick_when_nfc_spinner,
//            WhenNfcBrick(),
//            "When NFC gets scanned (nfc tag: (all)) {\n}\n",
//            mapOf()
//        )
//    }
//
//    @Test
//    fun testAskBrick() {
//        executeTest(
//            R.id.brick_ask_spinner,
//            AskBrick(),
//            "Ask (text: (''), and store in: (\"var1\"));\n",
//            mapOf(
//                "var2" to "Ask (text: (''), and store in: (\"var2\"));\n",
//                "var3" to "Ask (text: (''), and store in: (\"var3\"));\n"
//            )
//        )
//    }

    private fun executeTest(
        @IdRes brickSpinnerId: Int,
        brick: Brick,
        defaultValue: String,
        expectedValues: Map<String, String>,
        @IdRes secondSpinerId: Int? = null,
        secondExpectedValues: Map<String, String>? = null
    ) {
        val initialValue = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals(defaultValue, initialValue)

        testIndentAndComment(brick, defaultValue)

        for ((key, value) in expectedValues) {
            onView(withId(brickSpinnerId)).perform(click())
            try {
                onView(withText(key)).perform(click())
            } catch (e: NoMatchingViewException) {
                onData(anything())
                    .inAdapterView(CoreMatchers.allOf(ViewMatchers.isAssignableFrom(AdapterView::class.java), isDisplayed()))
                    .atPosition(0)
                    .perform(click())
                onView(withId(brickSpinnerId)).perform(click())
                onView(withText(key)).perform(click())
            }

            val newValue = brick.serializeToCatrobatLanguage(0)
            Assert.assertEquals(value, newValue)
        }

        if (secondSpinerId != null && secondExpectedValues != null) {
            for ((key, value) in secondExpectedValues) {
                onView(withId(secondSpinerId)).perform(click())
                onView(withText(key)).perform(scrollTo(), click())

                val newValue = brick.serializeToCatrobatLanguage(0)
                Assert.assertEquals(value, newValue)
            }
        }
    }

    private fun testIndentAndComment(brick: Brick, baseValue: String) {
        testDisabledBrick(brick, baseValue)
        testIndention(brick, baseValue)
    }

    private fun testDisabledBrick(brick: Brick, expectedOutput: String) {
        val trimmedBaseValue = expectedOutput.substring(0, expectedOutput.length - 1)
        brick.isCommentedOut = true
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        brick.isCommentedOut = false
        val newOutput = "/* $trimmedBaseValue */\n"
        Assert.assertEquals(newOutput, actualOutput)
    }

    private fun testIndention(brick: Brick, expectedOutput: String) {
        val randomIndention = java.util.Random().nextInt(4) + 2
        val indention = CatrobatLanguageUtils.getIndention(randomIndention)
        val actualOutput = brick.serializeToCatrobatLanguage(randomIndention)
        val newOutput = indention + expectedOutput.replace(Regex("\\n(?!\$)"), "\n$indention")
        Assert.assertEquals(newOutput, actualOutput)
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        val sprite1 = Sprite("testSprite1")
        val sprite2 = Sprite("testSprite2")
        val sprite3 = Sprite("testSprite3")

        project.sceneList.add(Scene("s1", project))
        project.sceneList.add(Scene("s2", project))
        project.sceneList.add(Scene("s3", project))

        project.defaultScene.addSprite(sprite)
        project.defaultScene.addSprite(sprite1)
        project.defaultScene.addSprite(sprite2)
        project.defaultScene.addSprite(sprite3)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite

        project.defaultScene.backgroundSprite.lookList.add(LookData("look1", File("look1.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look2", File("look2.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look3", File("look3.jpg")))

        sprite.lookList.add(LookData("spritelook1", File("look1.jpg")))
        sprite.lookList.add(LookData("spritelook2", File("look2.jpg")))
        sprite.lookList.add(LookData("spritelook3", File("look3.jpg")))

        sprite.soundList.add(SoundInfo("sound1", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound2", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound3", File("sound3.mp3")))

        projectManager.currentProject.userVariables.add(UserVariable("var1"))
        projectManager.currentProject.userVariables.add(UserVariable("var2"))
        projectManager.currentProject.userVariables.add(UserVariable("var3"))

        projectManager.currentProject.userLists.add(UserList("list1"))
        projectManager.currentProject.userLists.add(UserList("list2"))
        projectManager.currentProject.userLists.add(UserList("list3"))

        projectManager.currentProject.broadcastMessageContainer.addBroadcastMessage("Broadcast1")

        val script = StartScript()
        projectManager.currentSprite.addScript(script)
        startScript = script
    }

    private fun checkSpinnerValueCount(brick: Brick, @IdRes brickSpinnerId: Int, expectedCount: Int) {
        val brickSpinner = brick.getView(baseActivityTestRule.activity).findViewById<Spinner>(brickSpinnerId)
        val itemCount = brickSpinner.adapter.count
        Assert.assertEquals(expectedCount, itemCount)
    }

    private fun getAllInstruments(defaultValue: String, expectedValue: String): Map<String, String> {
        val items: MutableMap<String, String> = mutableMapOf()

        var defaultCatLangValue = ""
        for (instrument in PickableMusicalInstrument.values()) {
            val spinnerItemName = getStringForResourceId(instrument.nameStringId)
            val instrumentName = instrument.catrobatLanguageString
            if (spinnerItemName == defaultValue) {
                defaultCatLangValue = expectedValue.replace("{{INSTRUMENT}}", instrumentName)
                continue
            }
            items[spinnerItemName] = expectedValue.replace("{{INSTRUMENT}}", instrumentName)
        }

        items[defaultValue] = defaultCatLangValue

        return items
    }

    private fun getAllDrums(defaultValue: String, expectedValue: String): Map<String, String> {
        val items: MutableMap<String, String> = mutableMapOf()

        var defaultCatLangValue = ""
        for (drum in PickableDrum.values()) {
            val spinnerItemName = getStringForResourceId(drum.nameStringId)
            val drumName = drum.catrobatLanguageString
            if (spinnerItemName == defaultValue) {
                defaultCatLangValue = expectedValue.replace("{{DRUM}}", drumName)
                continue
            }
            items[spinnerItemName] = expectedValue.replace("{{DRUM}}", drumName)
        }

        items[defaultValue] = defaultCatLangValue

        return items
    }

    private fun getStringForResourceId(@StringRes resourceId: Int): String {
        return ApplicationProvider.getApplicationContext<Context>().getString(resourceId)
    }

    private fun getStringArrayForResourceId(@ArrayRes resourceId: Int): Array<String> {
        return ApplicationProvider.getApplicationContext<Context>().resources.getStringArray(resourceId)
    }

    private fun setLanguage(language: String) {
        SettingsFragment.setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), language)
    }
}

