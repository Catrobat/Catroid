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

import android.widget.Spinner
import androidx.annotation.IdRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
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
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class SpinnerSerializationTest {
    private lateinit var startScript: StartScript;

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
        executeTest(
            R.id.brick_clone_spinner,
            CloneBrick(),
            "Create clone of (actor or object: (yourself));\n",
            mapOf(
                "testSprite1" to "Create clone of (actor or object: ('testSprite1'));\n",
                "testSprite2" to "Create clone of (actor or object: ('testSprite2'));\n",
                "testSprite3" to "Create clone of (actor or object: ('testSprite3'));\n"
            )
        )
    }

    @Test
    fun testForItemInUserListBrick() {
        executeTest(
            R.id.for_item_in_userlist_list_spinner,
            ForItemInUserListBrick(),
            "For each value in list (value: (\"var1\"), list: (*list1*)) {\n}\n",
            mapOf(
                "list2" to "For each value in list (value: (\"var1\"), list: (*list2*)) {\n}\n",
                "list3" to "For each value in list (value: (\"var1\"), list: (*list3*)) {\n}\n"
            ),
            R.id.for_item_in_userlist_variable_spinner,
            mapOf(
                "var2" to "For each value in list (value: (\"var2\"), list: (*list3*)) {\n}\n",
                "var3" to "For each value in list (value: (\"var3\"), list: (*list3*)) {\n}\n"
            )
        )
    }

    @Test
    fun testSceneTransitionBrick() {
        executeTest(
            R.id.brick_scene_transition_spinner,
            SceneTransitionBrick(),
            "Continue (scene: ('s1'));\n",
            mapOf(
                "s2" to "Continue (scene: ('s2'));\n",
                "s3" to "Continue (scene: ('s3'));\n",
            )
        )
    }

    @Test
    fun testSceneStartBrick() {
        executeTest(
            R.id.brick_scene_start_spinner,
            SceneStartBrick(),
            "Start (scene: ('Scene'));\n",
            mapOf(
                "s2" to "Start (scene: ('s2'));\n",
                "s3" to "Start (scene: ('s3'));\n",
            )
        )
    }

    @Test
    fun testStopScriptBrick() {
        val brick = StopScriptBrick()
        executeTest(
            R.id.brick_stop_script_spinner,
            brick,
            "Stop (script: (this script));\n",
            mapOf(
                "all scripts" to "Stop (script: (all scripts));\n",
                "other scripts of this actor or object" to "Stop (script: (other scripts of this actor or object));\n",
            )
        )
        checkSpinnerCount(brick, R.id.brick_stop_script_spinner, 3)
    }

    @Test
    fun testGoToBrick() {
        executeTest(
            R.id.brick_go_to_spinner,
            GoToBrick(),
            "Go to (target: (touch position));\n",
            mapOf(
                "random position" to "Go to (target: (random position));\n",
                "testSprite3" to "Go to (target: ('testSprite3'));\n",
            )
        )
    }

    @Test
    fun testPointToBrick() {
        executeTest(
            R.id.brick_point_to_spinner,
            PointToBrick(),
            "Point towards (actor or object: ('testSprite1'));\n",
            mapOf(
                "testSprite2" to "Point towards (actor or object: ('testSprite2'));\n",
                "testSprite3" to "Point towards (actor or object: ('testSprite3'));\n"
            )
        )
    }

    @Test
    fun testSetRotationStyleBrick() {
        val brick = SetRotationStyleBrick()
        executeTest(
            R.id.brick_set_rotation_style_spinner,
            brick,
            "Set (rotation style: (left-right only));\n",
            mapOf(
                "all-around" to "Set (rotation style: (all-around));\n",
                "don't rotate" to "Set (rotation style: (don't rotate));\n",
            )
        )
        checkSpinnerCount(brick, R.id.brick_set_rotation_style_spinner, 3)
    }

    @Test
    fun testSetPhysicsObjectTypeBrick() {
        executeTest(
            R.id.brick_set_physics_object_type_spinner,
            SetPhysicsObjectTypeBrick(),
            "Set (motion type: (not moving or bouncing under gravity (default)));\n",
            mapOf(
                "moving and bouncing under gravity" to "Set (motion type: (moving and bouncing under gravity));\n",
                "not moving under gravity, but others bounce off you under gravity" to "Set (motion type: (not moving under gravity, but others bounce off you under gravity));\n",
            )
        )
    }

    @Test
    fun testFadeParticleEffectBrick() {
        val brick = FadeParticleEffectBrick()
        executeTest(
            R.id.brick_fade_particle_effect_spinner,
            brick,
            "Set particle (effect: (in));\n",
            mapOf(
                "out" to "Set particle (effect: (out));\n",
            )
        )
        checkSpinnerCount(brick, R.id.brick_fade_particle_effect_spinner, 2)
    }

    @Test
    fun testPlaySoundBrick() {
        executeTest(
            R.id.brick_play_sound_spinner,
            PlaySoundBrick(),
            "Start (sound: ('sound1'));\n",
            mapOf(
                "sound2" to "Start (sound: ('sound2'));\n",
                "sound3" to "Start (sound: ('sound3'));\n",
            )
        )
    }

    @Test
    fun testPlaySoundAndWaitBrick() {
        // TODO: right spinner id?
        executeTest(
            R.id.brick_play_sound_spinner,
            PlaySoundAndWaitBrick(),
            "Start sound and skip seconds (sound: ('sound1'), seconds: (0));\n",
            mapOf(
                "sound2" to "Start sound and skip seconds (sound: ('sound2'), seconds: (0));\n",
                "sound3" to "Start sound and skip seconds (sound: ('sound3'), seconds: (0));\n"
            )
        )
    }

    @Test
    fun testPlaySoundAtBrick() {
        executeTest(
            R.id.brick_play_sound_at_spinner,
            PlaySoundAtBrick(),
            "Start sound and skip seconds (sound: ('sound1'), seconds: (0));\n",
            mapOf(
                "sound2" to "Start sound and skip seconds (sound: ('sound2'), seconds: (0));\n",
                "sound3" to "Start sound and skip seconds (sound: ('sound3'), seconds: (0));\n"
            )
        )
    }

    @Test
    fun testStopSoundBrick() {
        executeTest(
            R.id.brick_stop_sound_spinner,
            StopSoundBrick(),
            "Stop (sound: ('sound1'));\n",
            mapOf(
                "sound2" to "Stop (sound: ('sound2'));\n",
                "sound3" to "Stop (sound: ('sound3'));\n"
            )
        )
    }

    @Test
    fun testSetInstrumentBrick() {
        executeTest(
            R.id.set_instrument_spinner,
            SetInstrumentBrick(),
            "Set (instrument: (piano));\n",
            getAllInstruments("piano", "Set (instrument: ({{INSTRUMENT}}));\n")
        )
    }

    @Test
    fun testPlayDrumForBeatsBrick() {
        executeTest(
            R.id.play_drum_for_beats_spinner,
            PlayDrumForBeatsBrick(),
            "Play (drum: (snare drum), number of beats: (0));\n",
            mapOf(
                "bass drum" to "Play (drum: (bass drum), number of beats: (0));\n",
                "side stick" to "Play (drum: (side stick), number of beats: (0));\n",
                "crash cymbal" to "Play (drum: (crash cymbal), number of beats: (0));\n",
                "open hi-hat" to "Play (drum: (open hi-hat), number of beats: (0));\n",
                "closed hi-hat" to "Play (drum: (closed hi-hat), number of beats: (0));\n",
                "tambourine" to "Play (drum: (tambourine), number of beats: (0));\n",
                "hand clap" to "Play (drum: (hand clap), number of beats: (0));\n",
                "claves" to "Play (drum: (claves), number of beats: (0));\n",
                "wood block" to "Play (drum: (wood block), number of beats: (0));\n",
                "cowbell" to "Play (drum: (cowbell), number of beats: (0));\n",
                "triangle" to "Play (drum: (triangle), number of beats: (0));\n",
                "bongo" to "Play (drum: (bongo), number of beats: (0));\n",
                "conga" to "Play (drum: (conga), number of beats: (0));\n",
                "cabasa" to "Play (drum: (cabasa), number of beats: (0));\n",
                "guiro" to "Play (drum: (guiro), number of beats: (0));\n",
                "vibraslap" to "Play (drum: (vibraslap), number of beats: (0));\n",
                "open cuica" to "Play (drum: (open cuica), number of beats: (0));\n",
            )
        )
    }

    @Test
    fun testSetLookBrick() {
        executeTest(
            R.id.brick_set_look_spinner,
            SetLookBrick(),
            "Switch to (look: ('spritelook1'));\n",
            mapOf(
                "spritelook2" to "Switch to (look: ('spritelook2'));\n",
                "spritelook3" to "Switch to (look: ('spritelook3'));\n"
            )
        )
    }

    @Test
    fun testAskSpeechBrick() {
        executeTest(
            R.id.brick_ask_speech_spinner,
            AskSpeechBrick("my question"),
            "Ask question and store written answer to variable (question: ('my question'), variable: (\"var1\"));\n",
            mapOf(
                "var2" to "Ask question and store written answer to variable (question: ('my question'), variable: (\"var2\"));\n",
                "var3" to "Ask question and store written answer to variable (question: ('my question'), variable: (\"var3\"));\n"
            )
        )
    }

    @Test
    fun testShowTextColorSizeAlignmentBrick() {
        executeTest(
            R.id.brick_show_variable_color_size_align_spinner,
            ShowTextColorSizeAlignmentBrick(),
            "Show variable (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (0), alignment: (left));\n",
            mapOf(
                "center" to "Show variable (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (0), alignment: (center));\n",
                "right" to "Show variable (variable: (\"var1\"), x: (0), y: (0), size: (0), color: (0), alignment: (right));\n"
            )
        )
    }

    @Test
    fun testSetBackgroundBrick() {
        executeTest(
            R.id.brick_set_background_spinner,
            SetBackgroundBrick(),
            "Set background to (look: ('look1'));\n",
            mapOf(
                "look2" to "Set background to (look: ('look2'));\n",
                "look3" to "Set background to (look: ('look3'));\n"
            )
        )
    }

    @Test
    fun testSetBackgroundAndWaitBrick() {
        // TODO: spinner id?
        executeTest(
            R.id.brick_set_background_spinner,
            SetBackgroundAndWaitBrick(),
            "Set background and wait (look: ('look1'));\n",
            mapOf(
                "look2" to "Set background and wait (look: ('look2'));\n",
                "look3" to "Set background and wait (look: ('look3'));\n"
            )
        )
    }

    @Test
    fun testCameraBrick() {
        executeTest(
            R.id.brick_video_spinner,
            CameraBrick(),
            "Turn (camera: (on));\n",
            mapOf(
                "off" to "Turn (camera: (off));\n"
            )
        )
    }

    @Test
    fun testChooseCameraBrick() {
        executeTest(
            R.id.brick_choose_camera_spinner,
            CameraBrick(),
            "Use (camera: (rear));\n",
            mapOf(
                "front" to "Use (camera: (front));\n"
            )
        )
    }

    @Test
    fun testFlashBrick() {
        executeTest(
            R.id.brick_flash_spinner,
            FlashBrick(),
            "Turn (flashlight: (on));\n",
            mapOf(
                "off" to "Turn (flashlight: (off));\n"
            )
        )
    }

    @Test
    fun testSetVariableBrick() {
        // TODO: right spinner?
        executeTest(
            R.id.set_variable_spinner,
            SetVariableBrick(),
            "Set (variable: (\"var1\"), value: (0));\n",
            mapOf(
                "var2" to "Set (variable: (\"var2\"), value: (0));\n",
                "var3" to "Set (variable: (\"var3\"), value: (0));\n"
            )
        )
    }

    @Test
    fun testChangeVariableBrick() {
        executeTest(
            R.id.change_variable_spinner,
            ChangeVariableBrick(),
            "Change (variable: (\"var1\"), value: (0));\n",
            mapOf(
                "var2" to "Change (variable: (\"var2\"), value: (0));\n",
                "var3" to "Change (variable: (\"var3\"), value: (0));\n"
            )
        )
    }

    @Test
    fun testHideTextBrick() {
        executeTest(
            R.id.hide_variable_spinner,
            HideTextBrick(),
            "Hide (variable: (\"var1\"));\n",
            mapOf(
                "var2" to "Hide (variable: (\"var2\"));\n",
                "var3" to "Hide (variable: (\"var3\"));\n"
            )
        )
    }

    @Test
    fun testWriteVariableOnDeviceBrick() {
        executeTest(
            R.id.write_variable_spinner,
            WriteVariableOnDeviceBrick(),
            "Write on device (variable: (\"var1\"));\n",
            mapOf(
                "var2" to "Write on device (variable: (\"var2\"));\n",
                "var3" to "Write on device (variable: (\"var3\"));\n"
            )
        )
    }

    @Test
    fun testReadVariableFromDeviceBrick() {
        executeTest(
            R.id.read_variable_from_device_spinner,
            ReadVariableFromDeviceBrick(),
            "Read from device (variable: (\"var1\"));\n",
            mapOf(
                "var2" to "Read from device (variable: (\"var2\"));\n",
                "var3" to "Read from device (variable: (\"var3\"));\n"
            )
        )
    }

    @Test
    fun testWriteVariableToFileBrick() {
        executeTest(
            R.id.brick_write_variable_to_file_spinner,
            WriteVariableToFileBrick(),
            "Write variable (variable: (\"var1\"), path: (\"path\"));\n",
            mapOf(
                "var2" to "Write variable (variable: (\"var2\"), path: (\"path\"));\n",
                "var3" to "Write variable (variable: (\"var3\"), path: (\"path\"));\n"
            )
        )
    }

    @Test
    fun testReadVariableFromFileBrick() {
        executeTest(
            R.id.brick_read_variable_from_file_spinner_variable,
            ReadVariableFromFileBrick(),
            "Read from file (variable: (\"var1\"), file: (0), action: (keep the file));\n",
            mapOf(
                "var2" to "Read from file (variable: (\"var2\"), file: (0), action: (keep the file));\n",
                "var3" to "Read from file (variable: (\"var3\"), file: (0), action: (keep the file));\n"
            ),
            R.id.brick_read_variable_from_file_spinner_mode,
            mapOf(
                "delete the file" to "Read from file (variable: (\"var3\"), file: (0), action: (delete the file));\n",
                "keep the file" to "Read from file (variable: (\"var3\"), file: (0), action: (keep the file));\n"
            )
        )
    }

    @Test
    fun testAddItemToUserListBrick() {
        executeTest(
            R.id.add_item_to_userlist_spinner,
            ForItemInUserListBrick(),
            "Add (list: (*list1*), item: (0));\n",
            mapOf(
                "list2" to "Add (list: (*list2*), item: (0));\n",
                "list3" to "Add (list: (*list3*), item: (0));\n"
            )
        )
    }

    @Test
    fun testDeleteItemOfUserListBrick() {
        executeTest(
            R.id.delete_item_of_userlist_spinner,
            DeleteItemOfUserListBrick(),
            "Delete item at (list: (*list1*), position: (0));\n",
            mapOf(
                "list2" to "Delete item at (list: (*list2*), position: (0));\n",
                "list3" to "Delete item at (list: (*list3*), position: (0));\n"
            )
        )
    }

    @Test
    fun testClearUserListBrick() {
        executeTest(
            R.id.clear_userlist_spinner,
            ClearUserListBrick(),
            "Delete all items (list: (*list1*));\n",
            mapOf(
                "list2" to "Delete all items (list: (*list2*));\n",
                "list3" to "Delete all items (list: (*list3*));\n"
            )
        )
    }

    @Test
    fun testInsertItemIntoUserListBrick() {
        executeTest(
            R.id.insert_item_into_userlist_spinner,
            InsertItemIntoUserListBrick(),
            "Insert (list: (*list1*), position: (0), value: (0));\n",
            mapOf(
                "list2" to "Insert (list: (*list2*), position: (0), value: (0));\n",
                "list3" to "Insert (list: (*list3*), position: (0), value: (0));\n"
            )
        )
    }

    @Test
    fun testReplaceItemInUserListBrick() {
        executeTest(
            R.id.replace_item_in_userlist_spinner,
            ReplaceItemInUserListBrick(),
            "Replace (list: (*list1*), position: (0), value: (0));\n",
            mapOf(
                "list2" to "Replace (list: (*list2*), position: (0), value: (0));\n",
                "list3" to "Replace (list: (*list3*), position: (0), value: (0));\n"
            )
        )
    }

    @Test
    fun testWriteListOnDeviceBrick() {
        executeTest(
            R.id.write_list_spinner,
            WriteListOnDeviceBrick(),
            "Write on device (list: (*list1*));\n",
            mapOf(
                "list2" to "Write on device (list: (*list2*));\n",
                "list3" to "Write on device (list: (*list3*));\n"
            )
        )
    }

    @Test
    fun testReadListFromDeviceBrick() {
        executeTest(
            R.id.read_list_from_device_spinner,
            ReadListFromDeviceBrick(),
            "Read from device (list: (*list1*));\n",
            mapOf(
                "list2" to "Read from device (list: (*list2*));\n",
                "list3" to "Read from device (list: (*list3*));\n"
            )
        )
    }

    @Test
    fun testStoreCSVIntoUserListBrick() {
        executeTest(
            R.id.brick_store_csv_into_userlist_spinner,
            ReadListFromDeviceBrick(),
            "Store column of comma-separated values to list (list: (*list1*), csv: (0), column: (0));\n",
            mapOf(
                "list2" to "Store CSV into (list: (*list2*), csv: (0), column: (0));\n",
                "list3" to "Store CSV into (list: (*list3*), csv: (0), column: (0));\n"
            )
        )
    }

    @Test
    fun testWebRequestBrick() {
        executeTest(
            R.id.web_request_spinner,
            WebRequestBrick(),
            "Send web request (url: (0), answer variable: (\"var1\"));\n",
            mapOf(
                "var2" to "Send web request (url: (0), answer variable: (\"var2\"));\n",
                "var3" to "Send web request (url: (0), answer variable: (\"var3\"));\n"
            )
        )
    }

    @Test
    fun testLegoNxtMotorTurnAngleBrick() {
        executeTest(
            R.id.lego_motor_turn_angle_spinner,
            LegoNxtMotorTurnAngleBrick(),
            "Turn NXT (motor: (A), degrees: (0));\n",
            mapOf(
                "B" to "Turn NXT (motor: (B), degrees: (0));\n",
                "C" to "Turn NXT (motor: (C), degrees: (0));\n",
                "B+C" to "Turn NXT (motor: (B+C), degrees: (0));\n"
            )
        )
    }

    @Test
    fun testLegoNxtMotorStopBrick() {
        executeTest(
            R.id.stop_motor_spinner,
            LegoNxtMotorStopBrick(),
            "Stop NXT (motor: (A));\n",
            mapOf(
                "B" to "Stop NXT (motor: (B));\n",
                "C" to "Stop NXT (motor: (C));\n",
                "B+C" to "Stop NXT (motor: (B+C));\n"
            )
        )
    }

    @Test
    fun testLegoNxtMotorMoveBrick() {
        executeTest(
            R.id.lego_motor_action_spinner,
            LegoNxtMotorMoveBrick(),
            "Move NXT (motor: (A), speed percentage: (0));\n",
            mapOf(
                "B" to "Move NXT (motor: (B), speed percentage: (0));\n",
                "C" to "Move NXT (motor: (C), speed percentage: (0));\n",
                "B+C" to "Move NXT (motor: (B+C), speed percentage: (0));\n"
            )
        )
    }

    @Test
    fun testLegoEv3MotorTurnAngleBrick() {
        executeTest(
            R.id.lego_ev3_motor_turn_angle_spinner,
            LegoEv3MotorTurnAngleBrick(),
            "Turn EV3 (motor: (A), degrees: (0));\n",
            mapOf(
                "B" to "Turn EV3 (motor: (B), degrees: (0));\n",
                "C" to "Turn EV3 (motor: (C), degrees: (0));\n",
                "B+C" to "Turn EV3 (motor: (B+C), degrees: (0));\n",
                "All" to "Turn EV3 (motor: (All), degrees: (0));\n"
            )
        )
    }

    @Test
    fun testLegoEv3MotorMoveBrick() {
        executeTest(
            R.id.brick_ev3_motor_move_spinner,
            LegoEv3MotorMoveBrick(),
            "Set EV3 (motor: (A), speed percentage: (0));\n",
            mapOf(
                "B" to "Set EV3 (motor: (B), speed percentage: (0));\n",
                "C" to "Set EV3 (motor: (C), speed percentage: (0));\n",
                "B+C" to "Set EV3 (motor: (B+C), speed percentage: (0));\n",
                "All" to "Set EV3 (motor: (All), speed percentage: (0));\n"
            )
        )
    }

    @Test
    fun testLegoEv3MotorStopBrick() {
        executeTest(
            R.id.ev3_stop_motor_spinner,
            LegoEv3MotorStopBrick(),
            "Stop EV3 (motor: (A));\n",
            mapOf(
                "B" to "Stop EV3 (motor: (B));\n",
                "C" to "Stop EV3 (motor: (C));\n",
                "B+C" to "Stop EV3 (motor: (B+C));\n",
                "All" to "Stop EV3 (motor: (All));\n"
            )
        )
    }

    @Test
    fun testLegoEv3SetLedBrick() {
        executeTest(
            R.id.brick_ev3_set_led_spinner,
            LegoEv3SetLedBrick(),
            "Set EV3 (status: (green));\n",
            mapOf(
                "red" to "Set EV3 (status: (red));\n",
                "orange" to "Set EV3 (status: (orange));\n",
                "green" to "Set EV3 (status: (green));\n",
                "red flashing" to "Set EV3 (status: (red flashing));\n",
                "orange flashing" to "Set EV3 (status: (orange flashing));\n",
                "green flashing" to "Set EV3 (status: (green flashing));\n",
                "red pulse" to "Set EV3 (status: (red pulse));\n",
                "orange pulse" to "Set EV3 (status: (orange pulse));\n",
                "green pulse" to "Set EV3 (status: (green pulse));\n",
                "off" to "Set EV3 (status: (off));\n"
            )
        )
    }

    @Test
    fun testDronePlayLedAnimationBrick() {
        executeTest(
            R.id.brick_drone_play_led_animation_spinner,
            DronePlayLedAnimationBrick(),
            "Play AR.Drone 2.0 (flash animation: (blink green red));\n",
            mapOf(
                "blink green" to "Play AR.Drone 2.0 (flash animation: (blink green));\n",
                "blink red" to "Play AR.Drone 2.0 (flash animation: (blink red));\n",
                "blink orange" to "Play AR.Drone 2.0 (flash animation: (blink orange));\n",
                "snake green red" to "Play AR.Drone 2.0 (flash animation: (snake green red));\n",
                "fire" to "Play AR.Drone 2.0 (flash animation: (fire));\n",
                "standard" to "Play AR.Drone 2.0 (flash animation: (standard));\n",
                "red" to "Play AR.Drone 2.0 (flash animation: (red));\n",
                "green" to "Play AR.Drone 2.0 (flash animation: (green));\n",
                "red snake" to "Play AR.Drone 2.0 (flash animation: (red snake));\n",
                "blank" to "Play AR.Drone 2.0 (flash animation: (blank));\n",
                "right missile" to "Play AR.Drone 2.0 (flash animation: (right missile));\n",
                "left missile" to "Play AR.Drone 2.0 (flash animation: (left missile));\n",
                "double missle" to "Play AR.Drone 2.0 (flash animation: (double missle));\n",
                "front left green others red" to "Play AR.Drone 2.0 (flash animation: (front left green others red));\n",
                "front right green others red" to "Play AR.Drone 2.0 (flash animation: (front right green others red));\n",
                "rear right green others red" to "Play AR.Drone 2.0 (flash animation: (rear right green others red));\n",
                "rear left green others red" to "Play AR.Drone 2.0 (flash animation: (rear left green others red));\n",
                "left green right red" to "Play AR.Drone 2.0 (flash animation: (left green right red));\n",
                "left red right green" to "Play AR.Drone 2.0 (flash animation: (left red right green));\n",
                "blink standard" to "Play AR.Drone 2.0 (flash animation: (blink standard));\n"
            )
        )
    }

    @Test
    fun testJumpingSumoAnimationsBrick() {
        executeTest(
            R.id.brick_jumping_sumo_animation_spinner,
            JumpingSumoAnimationsBrick(),
            "Play Jumping Sumo (animation: (spin));\n",
            mapOf(
                "tab" to "Play Jumping Sumo (animation: (tab));\n",
                "slowshake" to "Play Jumping Sumo (animation: (slowshake));\n",
                "metronome" to "Play Jumping Sumo (animation: (metronome));\n",
                "ondulation" to "Play Jumping Sumo (animation: (ondulation));\n",
                "spinjump" to "Play Jumping Sumo (animation: (spinjump));\n",
                "spiral" to "Play Jumping Sumo (animation: (spiral));\n",
                "slalom" to "Play Jumping Sumo (animation: (slalom));\n"
            )
        )
    }

    @Test
    fun testJumpingSumoSoundBrick() {
        executeTest(
            R.id.brick_jumping_sumo_sound_spinner,
            JumpingSumoSoundBrick(),
            "Play Jumping Sumo (sound: (default), volume: (0));\n",
            mapOf(
                "robot" to "Play Jumping Sumo (sound: (robot), volume: (0));\n",
                "insect" to "Play Jumping Sumo (sound: (insect), volume: (0));\n",
                "monster" to "Play Jumping Sumo (sound: (monster), volume: (0));\n"
            )
        )
    }

    @Test
    fun testPhiroMotorMoveForwardBrick() {
        executeTest(
            R.id.brick_phiro_motor_forward_action_spinner,
            PhiroMotorMoveForwardBrick(),
            "Move Phiro (motor: (left), direction: (forward), speed percentage: (0));\n",
            mapOf(
                "right" to "Move Phiro (motor: (right), direction: (forward), speed percentage: (0));\n",
                "both" to "Move Phiro (motor: (both), direction: (forward), speed percentage: (0));\n"
            )
        )
    }

    @Test
    fun testPhiroMotorMoveBackwardBrick() {
        executeTest(
            R.id.brick_phiro_motor_forward_action_spinner,
            PhiroMotorMoveBackwardBrick(),
            "Move Phiro (motor: (left), direction: (backward), speed percentage: (0));\n",
            mapOf(
                "right" to "Move Phiro (motor: (right), direction: (backward), speed percentage: (0));\n",
                "both" to "Move Phiro (motor: (both), direction: (backward), speed percentage: (0));\n"
            )
        )
    }

    @Test
    fun testPhiroMotorStopBrick() {
        executeTest(
            R.id.brick_phiro_stop_motor_spinner,
            PhiroMotorStopBrick(),
            "Stop Phiro (motor: (both));\n",
            mapOf(
                "right" to "Stop Phiro (motor: (right));\n",
                "left" to "Stop Phiro (motor: (left));\n"
            )
        )
    }

    @Test
    fun testPhiroPlayToneBrick() {
        executeTest(
            R.id.brick_phiro_select_tone_spinner,
            PhiroPlayToneBrick(),
            "Play Phiro (tone: (do), seconds: (0));\n",
            mapOf(
                "re" to "Play Phiro (tone: (re), seconds: (0));\n",
                "mi" to "Play Phiro (tone: (mi), seconds: (0));\n",
                "fa" to "Play Phiro (tone: (fa), seconds: (0));\n",
                "so" to "Play Phiro (tone: (so), seconds: (0));\n",
                "la" to "Play Phiro (tone: (la), seconds: (0));\n",
                "ti" to "Play Phiro (tone: (ti), seconds: (0));\n",
                "do" to "Play Phiro (tone: (do), seconds: (0));\n"
            )
        )
    }

    @Test
    fun testPhiroRGBLightBrick() {
        executeTest(
            R.id.brick_phiro_rgb_light_spinner,
            PhiroRGBLightBrick(),
            "Set Phiro (light: (both), color: (0));\n",
            mapOf(
                "left" to "Set Phiro (light: (left), color: (0));\n",
                "right" to "Set Phiro (light: (right), color: (0));\n",
                "both" to "Set Phiro (light: (both), color: (0));\n"
            )
        )
    }

    @Test
    fun testPhiroIfLogicBeginBrick() {
        executeTest(
            R.id.brick_phiro_sensor_action_spinner,
            PhiroIfLogicBeginBrick(),
            "If (activated phiro: (front left sensor)) {\n} else {\n}\n",
            mapOf(
                "front right sensor" to "If (activated phiro: (front right sensor)) {\n} else {\n}\n",
                "side left sensor" to "If (activated phiro: (side left sensor)) {\n} else {\n}\n",
                "side right sensor" to "If (activated phiro: (side right sensor)) {\n} else {\n}\n",
                "bottom left sensor" to "If (activated phiro: (bottom left sensor)) {\n} else {\n}\n",
                "bottom right sensor" to "If (activated phiro: (bottom right sensor)) {\n} else {\n}\n"
            )
        )
    }

    @Test
    fun testAssertUserListsBrick() {
        executeTest(
            R.id.brick_assert_lists_actual,
            AssertUserListsBrick(),
            "Assert that (list: (*list1*)) is equal to (list: (*list1*));\n",
            mapOf(
                "list2" to "Assert that (list: (*list2*)) is equal to (list: (*list1*));\n",
                "list3" to "Assert that (list: (*list3*)) is equal to (list: (*list1*));\n"
            ),
            R.id.brick_assert_lists_expected,
            mapOf(
                "list2" to "Assert that (list: (*list3*)) is equal to (list: (*list2*));\n",
                "list3" to "Assert that (list: (*list3*)) is equal to (list: (*list3*));\n"
            )
        )
    }

    @Test
    fun testWhenGamepadButtonBrick() {
        executeTest(
            R.id.brick_when_gamepad_button_spinner,
            WhenGamepadButtonBrick(),
            "When tapped (gamepad button: (A)) {\n}\n",
            mapOf(
                "B" to "When tapped (gamepad button: (B)) {\n}\n",
                "up" to "When tapped (gamepad button: (up)) {\n}\n",
                "down" to "When tapped (gamepad button: (down)) {\n}\n",
                "left" to "When tapped (gamepad button: (left)) {\n}\n",
                "right" to "When tapped (gamepad button: (right)) {\n}\n",
                "center" to "When tapped (gamepad button: (center)) {\n}\n"
            )
        )
    }

    @Test
    fun testWhenBounceOffBrick() {
        executeTest(
            R.id.brick_when_bounce_off_spinner,
            WhenBounceOffBrick(),
            "When you bounce off (actor or object: ('testSprite')) {\n}\n",
            mapOf(
                "testSprite1" to "When you bounce off (actor or object: ('testSprite1')) {\n}\n",
                "testSprite2" to "When you bounce off (actor or object: ('testSprite2')) {\n}\n",
                "testSprite3" to "When you bounce off (actor or object: ('testSprite3')) {\n}\n"
            )
        )
    }

    @Test
    fun testWhenBackgroundChangesBrick() {
        executeTest(
            R.id.brick_when_background_spinner,
            WhenBackgroundChangesBrick(),
            "When background changes to (look: ('look1')) {\n}\n",
            mapOf(
                "look2" to "When background changes to (look: ('look2')) {\n}\n",
                "look3" to "When background changes to (look: ('look3')) {\n}\n"
            )
        )
    }

    @Test
    fun testWhenRaspiPinChangedBrick() {
        executeTest(
            R.id.brick_raspi_when_pinspinner,
            WhenRaspiPinChangedBrick(),
            "When Raspberry Pi pin changes to (pin: (3), position: (high)) {\n}\n",
            mapOf(
                "5" to "When Raspberry Pi pin changes to (pin: (5), position: (high)) {\n}\n",
                "7" to "When Raspberry Pi pin changes to (pin: (7), position: (high)) {\n}\n"
            ),
            R.id.brick_raspi_when_valuespinner,
            mapOf(
                "low" to "When Raspberry Pi pin changes to (pin: (7), position: (low)) {\n}\n",
                "high" to "When Raspberry Pi pin changes to (pin: (7), position: (high)) {\n}\n"
            )
        )
    }

    @Test
    fun testWhenNfcBrick() {
        // TODO: add NFC tags to test project
        executeTest(
            R.id.brick_when_nfc_spinner,
            WhenNfcBrick(),
            "When NFC gets scanned (nfc tag: (all)) {\n}\n",
            mapOf()
        )
    }

    private fun executeTest(
        @IdRes brickSpinnerId: Int,
        brick: Brick,
        defaultValue: String,
        expectedValues: Map<String, String>,
        @IdRes secondSpinerId: Int? = null,
        secondExpectedValues: Map<String, String>? = null
    ) {
        startScript.addBrick(brick)
        baseActivityTestRule.launchActivity()
//        Thread.sleep(99999)
        val initialValue = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals(defaultValue, initialValue)

        testIndentAndComment(brick, defaultValue)

        for ((key, value) in expectedValues) {
            onView(withId(brickSpinnerId)).perform(click())
            onView(withText(key)).perform(click())

            val newValue = brick.serializeToCatrobatLanguage(0)
            Assert.assertEquals(value, newValue)
        }

        if (secondSpinerId != null && secondExpectedValues != null) {
            for ((key, value) in secondExpectedValues) {
                onView(withId(secondSpinerId)).perform(click())
                onView(withText(key)).perform(click())

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

    private fun checkSpinnerCount(brick: Brick, brickSpinnerId: Int, expectedCount: Int) {
        val brickSpinner = brick.getView(baseActivityTestRule.activity).findViewById<Spinner>(brickSpinnerId)
        val itemCount = brickSpinner.adapter.count
        Assert.assertEquals(expectedCount, itemCount)
    }

    private fun getAllInstruments(defaultValue: String, expectedValue: String): Map<String, String> {
        val items: MutableMap<String, String> = mutableMapOf()

        for (instrument in PickableMusicalInstrument.values()) {
            val instrumentName = instrument.name.toLowerCase().replace("_", " ")
            if (instrumentName == defaultValue) {
                continue
            }
            items[instrumentName] = expectedValue.replace("{{INSTRUMENT}}", instrumentName)
        }

        return items
    }
}
