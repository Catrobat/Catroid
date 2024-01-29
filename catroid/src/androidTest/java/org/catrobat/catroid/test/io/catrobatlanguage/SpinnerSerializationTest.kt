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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.NfcTagData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.ClearUserListBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils
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
import org.junit.runner.RunWith
import java.io.File

@Suppress("LargeClass")
@RunWith(AndroidJUnit4::class)
class SpinnerSerializationTest {
    companion object {
        val TEST_LANGUAGES: Array<String> = arrayOf("en-GB", "de", "hi")
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
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 0")
        device.executeShellCommand("settings put global transition_animation_scale 0")
        device.executeShellCommand("settings put global animator_duration_scale 0")

        createProject()
    }

    @After
    fun tearDown() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 1")
        device.executeShellCommand("settings put global transition_animation_scale 1")
        device.executeShellCommand("settings put global animator_duration_scale 1")

        baseActivityTestRule.finishActivity()
        setLanguage(TEST_LANGUAGES[0])
    }

    @Test
    fun testCloneBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = CloneBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testForItemInUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ForItemInUserListBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSceneTransitionBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SceneTransitionBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSceneStartBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SceneStartBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testStopScriptBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = StopScriptBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testGoToBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = GoToBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPointToBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PointToBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetRotationStyleBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetRotationStyleBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetPhysicsObjectTypeBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetPhysicsObjectTypeBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testFadeParticleEffectBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = FadeParticleEffectBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPlaySoundBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PlaySoundBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPlaySoundAndWaitBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PlaySoundAndWaitBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPlaySoundAtBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PlaySoundAtBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testStopSoundBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = StopSoundBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetInstrumentBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetInstrumentBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPlayDrumForBeatsBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PlayDrumForBeatsBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetLookBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetLookBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testAskSpeechBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = AskSpeechBrick("my question")
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testShowTextColorSizeAlignmentBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ShowTextColorSizeAlignmentBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetBackgroundBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetBackgroundBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetBackgroundAndWaitBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetBackgroundAndWaitBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testCameraBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = CameraBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testChooseCameraBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ChooseCameraBrick()
            startScript.addBrick(brick)
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
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testFlashBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = FlashBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.brick_flash_off) to "Turn (flashlight: (off));\n",
                getStringForResourceId(R.string.brick_flash_on) to "Turn (flashlight: (on));\n",
            )
            executeTest(
                R.id.brick_flash_spinner,
                brick,
                "Turn (flashlight: (on));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_flash_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testSetVariableBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = SetVariableBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "var2" to "Set (variable: (\"var2\"), value: (0));\n",
                "var3" to "Set (variable: (\"var3\"), value: (0));\n",
                "var1" to "Set (variable: (\"var1\"), value: (0));\n"
            )
            executeTest(
                R.id.set_variable_spinner,
                brick,
                "Set (variable: (\"var1\"), value: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testChangeVariableBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ChangeVariableBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "var2" to "Change (variable: (\"var2\"), value: (0));\n",
                "var3" to "Change (variable: (\"var3\"), value: (0));\n",
                "var1" to "Change (variable: (\"var1\"), value: (0));\n"
            )
            executeTest(
                R.id.change_variable_spinner,
                brick,
                "Change (variable: (\"var1\"), value: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testHideTextBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = HideTextBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.hide_variable_spinner,
                brick,
                "Hide (variable: (\"var1\"));\n",
                mapOf(
                    "var2" to "Hide (variable: (\"var2\"));\n",
                    "var3" to "Hide (variable: (\"var3\"));\n",
                    "var1" to "Hide (variable: (\"var1\"));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWriteVariableOnDeviceBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WriteVariableOnDeviceBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.write_variable_spinner,
                brick,
                "Write on device (variable: (\"var1\"));\n",
                mapOf(
                    "var2" to "Write on device (variable: (\"var2\"));\n",
                    "var3" to "Write on device (variable: (\"var3\"));\n",
                    "var1" to "Write on device (variable: (\"var1\"));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testReadVariableFromDeviceBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ReadVariableFromDeviceBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.read_variable_from_device_spinner,
                brick,
                "Read from device (variable: (\"var1\"));\n",
                mapOf(
                    "var2" to "Read from device (variable: (\"var2\"));\n",
                    "var3" to "Read from device (variable: (\"var3\"));\n",
                    "var1" to "Read from device (variable: (\"var1\"));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWriteVariableToFileBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WriteVariableToFileBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_write_variable_to_file_spinner,
                brick,
                "Write to file (variable: (\"var1\"), file: (0));\n",
                mapOf(
                    "var2" to "Write to file (variable: (\"var2\"), file: (0));\n",
                    "var3" to "Write to file (variable: (\"var3\"), file: (0));\n",
                    "var1" to "Write to file (variable: (\"var1\"), file: (0));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testReadVariableFromFileBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ReadVariableFromFileBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.brick_read_variable_from_file_spinner_variable,
                brick,
                "Read from file (variable: (\"var1\"), file: (0), action: (keep the file));\n",
                mapOf(
                    "var2" to "Read from file (variable: (\"var2\"), file: (0), action: (keep the file));\n",
                    "var3" to "Read from file (variable: (\"var3\"), file: (0), action: (keep the file));\n",
                    "var1" to "Read from file (variable: (\"var1\"), file: (0), action: (keep the file));\n"
                ),
                R.id.brick_read_variable_from_file_spinner_mode,
                mapOf(
                    getStringForResourceId(R.string.brick_read_variable_from_file_delete) to "Read from file (variable: (\"var1\"), file: (0), action: (delete the file));\n",
                    getStringForResourceId(R.string.brick_read_variable_from_file_keep) to "Read from file (variable: (\"var1\"), file: (0), action: (keep the file));\n",
                    getStringForResourceId(R.string.brick_read_variable_from_file_delete) to "Read from file (variable: (\"var1\"), file: (0), action: (delete the file));\n"
                )
            )
            checkSpinnerValueCount(brick, R.id.brick_read_variable_from_file_spinner_mode, 2)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testAddItemToUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = AddItemToUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Add (list: (*list2*), item: (0));\n",
                "list3" to "Add (list: (*list3*), item: (0));\n",
                "list1" to "Add (list: (*list1*), item: (0));\n"
            )
            executeTest(
                R.id.add_item_to_userlist_spinner,
                brick,
                "Add (list: (*list1*), item: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testDeleteItemOfUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = DeleteItemOfUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Delete item at (list: (*list2*), position: (0));\n",
                "list3" to "Delete item at (list: (*list3*), position: (0));\n",
                "list1" to "Delete item at (list: (*list1*), position: (0));\n"
            )
            executeTest(
                R.id.delete_item_of_userlist_spinner,
                brick,
                "Delete item at (list: (*list1*), position: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testClearUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ClearUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Delete all items (list: (*list2*));\n",
                "list3" to "Delete all items (list: (*list3*));\n",
                "list1" to "Delete all items (list: (*list1*));\n"
            )
            executeTest(
                R.id.clear_userlist_spinner,
                brick,
                "Delete all items (list: (*list1*));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testInsertItemIntoUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = InsertItemIntoUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Insert (list: (*list2*), position: (0), value: (0));\n",
                "list3" to "Insert (list: (*list3*), position: (0), value: (0));\n",
                "list1" to "Insert (list: (*list1*), position: (0), value: (0));\n"
            )
            executeTest(
                R.id.insert_item_into_userlist_spinner,
                brick,
                "Insert (list: (*list1*), position: (0), value: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testReplaceItemInUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ReplaceItemInUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Replace (list: (*list2*), position: (0), value: (0));\n",
                "list3" to "Replace (list: (*list3*), position: (0), value: (0));\n",
                "list1" to "Replace (list: (*list1*), position: (0), value: (0));\n"
            )
            executeTest(
                R.id.replace_item_in_userlist_spinner,
                brick,
                "Replace (list: (*list1*), position: (0), value: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWriteListOnDeviceBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WriteListOnDeviceBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.write_list_spinner,
                brick,
                "Write on device (list: (*list1*));\n",
                mapOf(
                    "list2" to "Write on device (list: (*list2*));\n",
                    "list3" to "Write on device (list: (*list3*));\n",
                    "list1" to "Write on device (list: (*list1*));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testReadListFromDeviceBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = ReadListFromDeviceBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            executeTest(
                R.id.read_list_from_device_spinner,
                brick,
                "Read from device (list: (*list1*));\n",
                mapOf(
                    "list2" to "Read from device (list: (*list2*));\n",
                    "list3" to "Read from device (list: (*list3*));\n",
                    "list1" to "Read from device (list: (*list1*));\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testStoreCSVIntoUserListBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = StoreCSVIntoUserListBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Store column of comma-separated values to list (list: (*list2*), csv: (0), column: (0));\n",
                "list3" to "Store column of comma-separated values to list (list: (*list3*), csv: (0), column: (0));\n",
                "list1" to "Store column of comma-separated values to list (list: (*list1*), csv: (0), column: (0));\n"
            )
            executeTest(
                R.id.brick_store_csv_into_userlist_spinner,
                brick,
                "Store column of comma-separated values to list (list: (*list1*), csv: (0), column: (0));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWebRequestBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WebRequestBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "var2" to "Send web request (url: (0), answer variable: (\"var2\"));\n",
                "var3" to "Send web request (url: (0), answer variable: (\"var3\"));\n",
                "var1" to "Send web request (url: (0), answer variable: (\"var1\"));\n"
            )
            executeTest(
                R.id.web_request_spinner,
                brick,
                "Send web request (url: (0), answer variable: (\"var1\"));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoNxtMotorTurnAngleBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoNxtMotorTurnAngleBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.nxt_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Turn NXT (motor: (B), degrees: (0));\n",
                keys[2] to "Turn NXT (motor: (C), degrees: (0));\n",
                keys[3] to "Turn NXT (motor: (B+C), degrees: (0));\n",
                keys[0] to "Turn NXT (motor: (A), degrees: (0));\n"
            )
            executeTest(
                R.id.lego_motor_turn_angle_spinner,
                brick,
                "Turn NXT (motor: (A), degrees: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.lego_motor_turn_angle_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoNxtMotorStopBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoNxtMotorStopBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.nxt_stop_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Stop NXT (motor: (B));\n",
                keys[2] to "Stop NXT (motor: (C));\n",
                keys[3] to "Stop NXT (motor: (B+C));\n",
                keys[4] to "Stop NXT (motor: (all));\n",
                keys[0] to "Stop NXT (motor: (A));\n"
            )
            executeTest(
                R.id.stop_motor_spinner,
                brick,
                "Stop NXT (motor: (A));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.stop_motor_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoNxtMotorMoveBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoNxtMotorMoveBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.nxt_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Set NXT (motor: (B), speed percentage: (0));\n",
                keys[2] to "Set NXT (motor: (C), speed percentage: (0));\n",
                keys[3] to "Set NXT (motor: (B+C), speed percentage: (0));\n",
                keys[0] to "Set NXT (motor: (A), speed percentage: (0));\n"
            )
            executeTest(
                R.id.lego_motor_action_spinner,
                brick,
                "Set NXT (motor: (A), speed percentage: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.lego_motor_action_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoEv3MotorTurnAngleBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoEv3MotorTurnAngleBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.ev3_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Turn EV3 (motor: (B), degrees: (0));\n",
                keys[2] to "Turn EV3 (motor: (C), degrees: (0));\n",
                keys[3] to "Turn EV3 (motor: (D), degrees: (0));\n",
                keys[4] to "Turn EV3 (motor: (B+C), degrees: (0));\n",
                keys[0] to "Turn EV3 (motor: (A), degrees: (0));\n"
            )
            executeTest(
                R.id.lego_ev3_motor_turn_angle_spinner,
                brick,
                "Turn EV3 (motor: (A), degrees: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.lego_ev3_motor_turn_angle_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoEv3MotorMoveBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoEv3MotorMoveBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.ev3_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Set EV3 (motor: (B), speed percentage: (0));\n",
                keys[2] to "Set EV3 (motor: (C), speed percentage: (0));\n",
                keys[3] to "Set EV3 (motor: (D), speed percentage: (0));\n",
                keys[4] to "Set EV3 (motor: (B+C), speed percentage: (0));\n",
                keys[0] to "Set EV3 (motor: (A), speed percentage: (0));\n"
            )
            executeTest(
                R.id.brick_ev3_motor_move_spinner,
                brick,
                "Set EV3 (motor: (A), speed percentage: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_ev3_motor_move_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoEv3MotorStopBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoEv3MotorStopBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.ev3_stop_motor_chooser)
            val mapOfValues = mapOf(
                keys[1] to "Stop EV3 (motor: (B));\n",
                keys[2] to "Stop EV3 (motor: (C));\n",
                keys[3] to "Stop EV3 (motor: (D));\n",
                keys[4] to "Stop EV3 (motor: (B+C));\n",
                keys[5] to "Stop EV3 (motor: (all));\n",
                keys[0] to "Stop EV3 (motor: (A));\n"
            )
            executeTest(
                R.id.ev3_stop_motor_spinner,
                brick,
                "Stop EV3 (motor: (A));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.ev3_stop_motor_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testLegoEv3SetLedBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = LegoEv3SetLedBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.ev3_led_status_chooser)
            val mapOfValues = mapOf(
                keys[0] to "Set EV3 LED (status: (off));\n",
                keys[2] to "Set EV3 LED (status: (red));\n",
                keys[3] to "Set EV3 LED (status: (orange));\n",
                keys[4] to "Set EV3 LED (status: (green flashing));\n",
                keys[5] to "Set EV3 LED (status: (red flashing));\n",
                keys[6] to "Set EV3 LED (status: (orange flashing));\n",
                keys[7] to "Set EV3 LED (status: (green pulse));\n",
                keys[8] to "Set EV3 LED (status: (red pulse));\n",
                keys[9] to "Set EV3 LED (status: (orange pulse));\n",
                keys[1] to "Set EV3 LED (status: (green));\n"
            )
            executeTest(
                R.id.brick_ev3_set_led_spinner,
                brick,
                "Set EV3 LED (status: (green));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_ev3_set_led_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testDronePlayLedAnimationBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = DronePlayLedAnimationBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_drone_play_led_animation_spinner)
            val mapOfValues = mapOf(
                keys[0] to "Play AR.Drone 2.0 (flash animation: (blink green red));\n",
                keys[2] to "Play AR.Drone 2.0 (flash animation: (blink red));\n",
                keys[3] to "Play AR.Drone 2.0 (flash animation: (blink orange));\n",
                keys[4] to "Play AR.Drone 2.0 (flash animation: (snake green red));\n",
                keys[5] to "Play AR.Drone 2.0 (flash animation: (fire));\n",
                keys[6] to "Play AR.Drone 2.0 (flash animation: (standard));\n",
                keys[7] to "Play AR.Drone 2.0 (flash animation: (red));\n",
                keys[8] to "Play AR.Drone 2.0 (flash animation: (green));\n",
                keys[9] to "Play AR.Drone 2.0 (flash animation: (red snake));\n",
                keys[10] to "Play AR.Drone 2.0 (flash animation: (blank));\n",
                keys[11] to "Play AR.Drone 2.0 (flash animation: (right missile));\n",
                keys[12] to "Play AR.Drone 2.0 (flash animation: (left missile));\n",
                keys[13] to "Play AR.Drone 2.0 (flash animation: (double missle));\n",
                keys[14] to "Play AR.Drone 2.0 (flash animation: (front left green others red));\n",
                keys[15] to "Play AR.Drone 2.0 (flash animation: (front right green others red));\n",
                keys[16] to "Play AR.Drone 2.0 (flash animation: (rear right green others red));\n",
                keys[17] to "Play AR.Drone 2.0 (flash animation: (rear left green others red));\n",
                keys[18] to "Play AR.Drone 2.0 (flash animation: (left green right red));\n",
                keys[19] to "Play AR.Drone 2.0 (flash animation: (left red right green));\n",
                keys[20] to "Play AR.Drone 2.0 (flash animation: (blink standard));\n",
                keys[1] to "Play AR.Drone 2.0 (flash animation: (blink green));\n"
            )
            executeTest(
                R.id.brick_drone_play_led_animation_spinner,
                brick,
                "Play AR.Drone 2.0 (flash animation: (blink green));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_drone_play_led_animation_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testJumpingSumoAnimationsBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = JumpingSumoAnimationsBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_jumping_sumo_select_animation_spinner)
            val mapOfValues = mapOf(
                keys[1] to "Start Jumping Sumo (animation: (tab));\n",
                keys[2] to "Start Jumping Sumo (animation: (slowshake));\n",
                keys[3] to "Start Jumping Sumo (animation: (metronome));\n",
                keys[4] to "Start Jumping Sumo (animation: (ondulation));\n",
                keys[5] to "Start Jumping Sumo (animation: (spinjump));\n",
                keys[6] to "Start Jumping Sumo (animation: (spiral));\n",
                keys[7] to "Start Jumping Sumo (animation: (slalom));\n",
                keys[0] to "Start Jumping Sumo (animation: (spin));\n"
            )
            executeTest(
                R.id.brick_jumping_sumo_animation_spinner,
                brick,
                "Start Jumping Sumo (animation: (spin));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_jumping_sumo_animation_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testJumpingSumoSoundBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = JumpingSumoSoundBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_jumping_sumo_select_sound_spinner)
            val mapOfValues = mapOf(
                keys[1] to "Play Jumping Sumo (sound: (robot), volume: (0));\n",
                keys[2] to "Play Jumping Sumo (sound: (insect), volume: (0));\n",
                keys[3] to "Play Jumping Sumo (sound: (monster), volume: (0));\n",
                keys[0] to "Play Jumping Sumo (sound: (normal), volume: (0));\n"
            )
            executeTest(
                R.id.brick_jumping_sumo_sound_spinner,
                brick,
                "Play Jumping Sumo (sound: (normal), volume: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_jumping_sumo_sound_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroMotorMoveForwardBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroMotorMoveForwardBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_select_motor_spinner)
            val mapOfValues = mapOf(
                keys[1] to "Move Phiro (motor: (right), direction: (forward), speed percentage: (0));\n",
                keys[2] to "Move Phiro (motor: (both), direction: (forward), speed percentage: (0));\n",
                keys[0] to "Move Phiro (motor: (left), direction: (forward), speed percentage: (0));\n"
            )
            executeTest(
                R.id.brick_phiro_motor_forward_action_spinner,
                brick,
                "Move Phiro (motor: (left), direction: (forward), speed percentage: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_motor_forward_action_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroMotorMoveBackwardBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroMotorMoveBackwardBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_select_motor_spinner)
            val mapOfValues = mapOf(
                keys[1] to "Move Phiro (motor: (right), direction: (backward), speed percentage: (0));\n",
                keys[2] to "Move Phiro (motor: (both), direction: (backward), speed percentage: (0));\n",
                keys[0] to "Move Phiro (motor: (left), direction: (backward), speed percentage: (0));\n"
            )
            executeTest(
                R.id.brick_phiro_motor_backward_action_spinner,
                brick,
                "Move Phiro (motor: (left), direction: (backward), speed percentage: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_motor_backward_action_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroMotorStopBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroMotorStopBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_stop_motor_spinner)
            val mapOfValues = mapOf(
                keys[0] to "Stop Phiro (motor: (left));\n",
                keys[1] to "Stop Phiro (motor: (right));\n",
                keys[2] to "Stop Phiro (motor: (both));\n"
            )
            executeTest(
                R.id.brick_phiro_stop_motor_spinner,
                brick,
                "Stop Phiro (motor: (both));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_stop_motor_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroPlayToneBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroPlayToneBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_select_tone_spinner)
            val mapOfValues = mapOf(
                keys[1] to "Play Phiro (tone: (re), seconds: (0));\n",
                keys[2] to "Play Phiro (tone: (mi), seconds: (0));\n",
                keys[3] to "Play Phiro (tone: (fa), seconds: (0));\n",
                keys[4] to "Play Phiro (tone: (so), seconds: (0));\n",
                keys[5] to "Play Phiro (tone: (la), seconds: (0));\n",
                keys[6] to "Play Phiro (tone: (ti), seconds: (0));\n",
                keys[0] to "Play Phiro (tone: (do), seconds: (0));\n"
            )
            executeTest(
                R.id.brick_phiro_select_tone_spinner,
                brick,
                "Play Phiro (tone: (do), seconds: (0));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_select_tone_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroRGBLightBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroRGBLightBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_select_light_spinner)
            val mapOfValues = mapOf(
                keys[0] to "Set Phiro (light: (left), color: (#000000));\n",
                keys[1] to "Set Phiro (light: (right), color: (#000000));\n",
                keys[2] to "Set Phiro (light: (both), color: (#000000));\n"
            )
            executeTest(
                R.id.brick_phiro_rgb_light_spinner,
                brick,
                "Set Phiro (light: (both), color: (#000000));\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_rgb_light_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testPhiroIfLogicBeginBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = PhiroIfLogicBeginBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val keys = getStringArrayForResourceId(R.array.brick_phiro_select_sensor_spinner)
            val mapOfValues = mapOf(
                keys[1] to "If (activated phiro: (front right sensor)) {\n} else {\n}\n",
                keys[2] to "If (activated phiro: (side left sensor)) {\n} else {\n}\n",
                keys[3] to "If (activated phiro: (side right sensor)) {\n} else {\n}\n",
                keys[4] to "If (activated phiro: (bottom left sensor)) {\n} else {\n}\n",
                keys[5] to "If (activated phiro: (bottom right sensor)) {\n} else {\n}\n",
                keys[0] to "If (activated phiro: (front left sensor)) {\n} else {\n}\n"
            )
            executeTest(
                R.id.brick_phiro_sensor_action_spinner,
                brick,
                "If (activated phiro: (front left sensor)) {\n} else {\n}\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_phiro_sensor_action_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testAssertUserListsBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = AssertUserListsBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "list2" to "Assert lists (actual: (*list2*), expected: (*list1*));\n",
                "list3" to "Assert lists (actual: (*list3*), expected: (*list1*));\n",
                "list1" to "Assert lists (actual: (*list1*), expected: (*list1*));\n"
            )
            val mapOfSecondValues = mapOf(
                "list2" to "Assert lists (actual: (*list1*), expected: (*list2*));\n",
                "list3" to "Assert lists (actual: (*list1*), expected: (*list3*));\n",
                "list1" to "Assert lists (actual: (*list1*), expected: (*list1*));\n"
            )
            executeTest(
                R.id.brick_assert_lists_actual,
                brick,
                "Assert lists (actual: (*list1*), expected: (*list1*));\n",
                mapOfValues,
                R.id.brick_assert_lists_expected,
                mapOfSecondValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWhenGamepadButtonBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WhenGamepadButtonBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                getStringForResourceId(R.string.cast_gamepad_B) to "When tapped (gamepad button: (B)) {\n}\n",
                getStringForResourceId(R.string.cast_gamepad_up) to "When tapped (gamepad button: (up)) {\n}\n",
                getStringForResourceId(R.string.cast_gamepad_down) to "When tapped (gamepad button: (down)) {\n}\n",
                getStringForResourceId(R.string.cast_gamepad_left) to "When tapped (gamepad button: (left)) {\n}\n",
                getStringForResourceId(R.string.cast_gamepad_right) to "When tapped (gamepad button: (right)) {\n}\n",
                getStringForResourceId(R.string.cast_gamepad_A) to "When tapped (gamepad button: (A)) {\n}\n"
            )
            executeTest(
                R.id.brick_when_gamepad_button_spinner,
                brick,
                "When tapped (gamepad button: (A)) {\n}\n",
                mapOfValues
            )
            checkSpinnerValueCount(brick, R.id.brick_when_gamepad_button_spinner, mapOfValues.size)
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWhenBounceOffBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WhenBounceOffBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()

            val spinner = baseActivityTestRule.activity.findViewById<Spinner>(R.id.brick_when_bounce_off_spinner)
            val option0 = spinner.adapter.getItem(0) as StringOption
            val mapOfValues = mapOf(
                projectManager.currentProject.defaultScene.spriteList[0].name to "When you bounce off (actor or object: ('${projectManager.currentProject.defaultScene.spriteList[0].name}')) {\n}\n",
                "testSprite1" to "When you bounce off (actor or object: ('testSprite1')) {\n}\n",
                "testSprite2" to "When you bounce off (actor or object: ('testSprite2')) {\n}\n",
                "testSprite3" to "When you bounce off (actor or object: ('testSprite3')) {\n}\n",
                option0.name to "When you bounce off (actor or object: (any edge, actor, or object)) {\n}\n"
            )
            executeTest(
                R.id.brick_when_bounce_off_spinner,
                brick,
                "When you bounce off (actor or object: (any edge, actor, or object)) {\n}\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWhenBackgroundChangesBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WhenBackgroundChangesBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "look2" to "When background changes to (look: ('look2')) {\n}\n",
                "look3" to "When background changes to (look: ('look3')) {\n}\n",
                "look1" to "When background changes to (look: ('look1')) {\n}\n"
            )
            executeTest(
                R.id.brick_when_background_spinner,
                brick,
                "When background changes to (look: ('look1')) {\n}\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWhenRaspiPinChangedBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WhenRaspiPinChangedBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "5" to "When Raspberry Pi pin changes to (pin: (5), position: (high)) {\n}\n",
                "7" to "When Raspberry Pi pin changes to (pin: (7), position: (high)) {\n}\n",
                "3" to "When Raspberry Pi pin changes to (pin: (3), position: (high)) {\n}\n"
            )
            executeTest(
                R.id.brick_raspi_when_pinspinner,
                brick,
                "When Raspberry Pi pin changes to (pin: (3), position: (high)) {\n}\n",
                mapOfValues,
                R.id.brick_raspi_when_valuespinner,
                mapOf(
                    getStringForResourceId(R.string.brick_raspi_released_text) to "When Raspberry Pi pin changes to (pin: (3), position: (low)) {\n}\n",
                    getStringForResourceId(R.string.brick_raspi_pressed_text) to "When Raspberry Pi pin changes to (pin: (3), position: (high)) {\n}\n"
                )
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testWhenNfcBrick() {
        val nfc1 = NfcTagData()
        nfc1.name = "nfc1"
        nfc1.nfcTagUid = "nfc1"
        projectManager.currentSprite.nfcTagList.add(nfc1)

        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = WhenNfcBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "nfc1" to "When NFC gets scanned (nfc tag: ('nfc1')) {\n}\n",
                getStringForResourceId(R.string.brick_when_nfc_default_all) to "When NFC gets scanned (nfc tag: (all)) {\n}\n"
            )
            executeTest(
                R.id.brick_when_nfc_spinner,
                brick,
                "When NFC gets scanned (nfc tag: (all)) {\n}\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testAskBrick() {
        for (languageCode in TEST_LANGUAGES) {
            setLanguage(languageCode)
            val brick = AskBrick()
            startScript.addBrick(brick)
            baseActivityTestRule.launchActivity()
            val mapOfValues = mapOf(
                "var2" to "Ask (question: (0), answer variable: (\"var2\"));\n",
                "var3" to "Ask (question: (0), answer variable: (\"var3\"));\n",
                "var1" to "Ask (question: (0), answer variable: (\"var1\"));\n"
            )
            executeTest(
                R.id.brick_ask_spinner,
                brick,
                "Ask (question: (0), answer variable: (\"var1\"));\n",
                mapOfValues
            )
            baseActivityTestRule.finishActivity()
            startScript.removeBrick(brick)
        }
    }

    @Test
    fun testParameterizedBrick() {
        setLanguage(TEST_LANGUAGES[0])
        val brick = ParameterizedBrick()
        startScript.addBrick(brick)
        baseActivityTestRule.launchActivity()

        val mapOfValues = mapOf(
            "list2" to "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (), value: (0), reference list: (*list2*)) {\n}\n",
            "list3" to "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (), value: (0), reference list: (*list3*)) {\n}\n",
            "list1" to "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (), value: (0), reference list: (*list1*)) {\n}\n"
        )
        executeTest(
            R.id.brick_param_expected_list,
            brick,
            "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (), value: (0), reference list: (*list1*)) {\n}\n",
            mapOfValues
        )

        var listOfLists = ""
        for (i in 1..3) {
            onView(withId(R.id.brick_param_list_of_list_text)).perform(click())
            onView(withText("list$i")).perform(click())
            onView(withId(R.id.confirm)).perform(click())

            val stringWithList = brick.serializeToCatrobatLanguage(0)

            if (listOfLists == "") {
                listOfLists = "*list$i*"
            } else {
                listOfLists += ", *list$i*"
            }

            Assert.assertEquals(
                "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: ($listOfLists), value: (0), reference list: (*list1*)) {\n}\n",
                stringWithList
            )
        }

        baseActivityTestRule.finishActivity()
        startScript.removeBrick(brick)
    }

    @Suppress("LongParameterList", "SwallowedException")
    private fun executeTest(
        @IdRes brickSpinnerId: Int,
        brick: Brick,
        defaultValue: String,
        expectedValues: Map<String, String>,
        @IdRes secondSpinerId: Int? = null,
        secondExpectedValues: Map<String, String>? = null
    ) {
        val initialValue = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals("Wrong default Value", defaultValue, initialValue)

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
            Assert.assertEquals("Wrong serialization for Key $key", value, newValue)
        }

        if (secondSpinerId != null && secondExpectedValues != null) {
            for ((key, value) in secondExpectedValues) {
                onView(withId(secondSpinerId)).perform(click())
                onView(withText(key)).perform(scrollTo(), click())

                val newValue = brick.serializeToCatrobatLanguage(0)
                Assert.assertEquals("Wrong serialization for Key $key", value, newValue)
            }
        }
    }

    private fun testIndentAndComment(brick: Brick, baseValue: String) {
        testDisabledBrick(brick, baseValue)
        testIndention(brick, baseValue)
    }

    private fun testDisabledBrick(brick: Brick, expectedOutput: String) {
        val disabledValue = "// " + expectedOutput.replace(Regex("\\n(?!\$)"), "\n// ")
        brick.isCommentedOut = true
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        brick.isCommentedOut = false
        Assert.assertEquals(disabledValue, actualOutput)
    }

    private fun testIndention(brick: Brick, expectedOutput: String) {
        val randomIndention = java.util.Random().nextInt(4) + 2
        val indention = CatrobatLanguageUtils.getIndention(randomIndention)
        val actualOutput = brick.serializeToCatrobatLanguage(randomIndention)
        val newOutput = indention + expectedOutput.replace(Regex("\\n(?!\$)"), "\n$indention")
        Assert.assertEquals("Wrong indention serialization", newOutput, actualOutput)
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
            val drumName = PickableDrum.getCatrobatLanguageStringByDrum(drum)
            if (spinnerItemName == defaultValue) {
                defaultCatLangValue = expectedValue.replace("{{DRUM}}", drumName)
                continue
            }
            items[spinnerItemName] = expectedValue.replace("{{DRUM}}", drumName)
        }

        items[defaultValue] = defaultCatLangValue

        return items
    }

    private fun getStringForResourceId(@StringRes resourceId: Int) = ApplicationProvider.getApplicationContext<Context>().getString(resourceId)

    private fun getStringArrayForResourceId(@ArrayRes resourceId: Int) = ApplicationProvider.getApplicationContext<Context>().resources.getStringArray(resourceId)

    private fun setLanguage(language: String) = SettingsFragment.setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), language)
}
