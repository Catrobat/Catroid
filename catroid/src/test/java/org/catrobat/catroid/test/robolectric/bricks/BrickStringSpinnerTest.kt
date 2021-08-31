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
package org.catrobat.catroid.test.robolectric.bricks

import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.catrobat.catroid.content.bricks.Brick
import androidx.annotation.IdRes
import android.widget.Spinner
import org.junit.Before
import org.catrobat.catroid.ui.SpriteActivity
import org.robolectric.Robolectric
import org.catrobat.catroid.R
import org.hamcrest.CoreMatchers
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import android.app.Activity
import android.os.Build
import android.view.View
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import org.robolectric.annotation.Config
import java.lang.Exception

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BrickStringSpinnerTest(
    private val name: String,
    private val brick: Brick,
    @field:IdRes @param:IdRes private val spinnerId: Int,
    private val expectedSelection: String,
    private val expectedContent: List<String>
) {
    private var brickSpinner: Spinner? = null
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        val activity = activityController.get()
        createProject(activity)
        activityController.create().resume()

        val scriptFragment =
            activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
        assertNotNull(scriptFragment)
        assertThat(
            scriptFragment, CoreMatchers.`is`(CoreMatchers.instanceOf(ScriptFragment::class.java))
        )
        val brickView = brick.getView(activity)
        assertNotNull(brickView)
        brickSpinner = brickView.findViewById<View>(spinnerId) as Spinner
        assertNotNull(brickSpinner)
    }

    @Test
    fun spinnerDefaultSelectionTest() {
        assertEquals(expectedSelection, brickSpinner?.selectedItem as String)
    }

    @Test
    fun spinnerContentTest() {
        val spinnerContent: MutableList<String> = ArrayList()
        brickSpinner?.adapter?.let {
            for (index in 0 until it.count) {
                spinnerContent.add(it.getItem(index) as String)
            }
        }
        assertEquals(expectedContent, spinnerContent)
    }

    fun createProject(activity: Activity?) {
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        script.addBrick(brick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        projectManager.currentlyEditedScene = project.defaultScene
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> = miscData() + legoData() + droneAndJumpingData() + phiroData()

        private fun miscData(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    StopScriptBrick::class.java.simpleName,
                    StopScriptBrick(),
                    R.id.brick_stop_script_spinner,
                    "this script",
                    listOf("this script", "all scripts", "other scripts of this actor or object")
                ), arrayOf(
                    SetPhysicsObjectTypeBrick::class.java.simpleName,
                    SetPhysicsObjectTypeBrick(),
                    R.id.brick_set_physics_object_type_spinner,
                    "not moving or bouncing under gravity (default)",
                    listOf(
                        "moving and bouncing under gravity",
                        "not moving under gravity, but others bounce off you under gravity",
                        "not moving or bouncing under gravity (default)"
                    )
                ), arrayOf(
                    CameraBrick::class.java.simpleName,
                    CameraBrick(),
                    R.id.brick_video_spinner,
                    "on",
                    listOf("off", "on")
                ), arrayOf(
                    ChooseCameraBrick::class.java.simpleName,
                    ChooseCameraBrick(),
                    R.id.brick_choose_camera_spinner,
                    "front",
                    listOf("rear", "front")
                ), arrayOf(
                    FlashBrick::class.java.simpleName,
                    FlashBrick(),
                    R.id.brick_flash_spinner,
                    "on",
                    listOf("off", "on")
                )
            )
        }

        private fun legoData(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    LegoNxtMotorTurnAngleBrick::class.java.simpleName,
                    LegoNxtMotorTurnAngleBrick(),
                    R.id.lego_motor_turn_angle_spinner,
                    "A",
                    listOf("A", "B", "C", "B+C")
                ), arrayOf(
                    LegoNxtMotorStopBrick::class.java.simpleName,
                    LegoNxtMotorStopBrick(),
                    R.id.stop_motor_spinner,
                    "A",
                    listOf("A", "B", "C", "B+C", "All")
                ), arrayOf(
                    LegoNxtMotorMoveBrick::class.java.simpleName,
                    LegoNxtMotorMoveBrick(),
                    R.id.lego_motor_action_spinner,
                    "A",
                    listOf("A", "B", "C", "B+C")
                ), arrayOf(
                    LegoEv3MotorTurnAngleBrick::class.java.simpleName,
                    LegoEv3MotorTurnAngleBrick(),
                    R.id.lego_ev3_motor_turn_angle_spinner,
                    "A",
                    listOf("A", "B", "C", "D", "B+C")
                ), arrayOf(
                    LegoEv3MotorMoveBrick::class.java.simpleName,
                    LegoEv3MotorMoveBrick(),
                    R.id.brick_ev3_motor_move_spinner,
                    "A",
                    listOf("A", "B", "C", "D", "B+C")
                ), arrayOf(
                    LegoEv3MotorStopBrick::class.java.simpleName,
                    LegoEv3MotorStopBrick(),
                    R.id.ev3_stop_motor_spinner,
                    "A",
                    listOf("A", "B", "C", "D", "B+C", "All")
                ), arrayOf(
                    LegoEv3SetLedBrick::class.java.simpleName,
                    LegoEv3SetLedBrick(),
                    R.id.brick_ev3_set_led_spinner,
                    "Green",
                    listOf(
                        "Off", "Green", "Red", "Orange", "Green flashing", "Red flashing",
                        "Orange flashing", "Green pulse", "Red pulse", "Orange pulse"
                    )
                )
            )
        }

        private fun droneAndJumpingData(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    DronePlayLedAnimationBrick::class.java.simpleName,
                    DronePlayLedAnimationBrick(),
                    R.id.brick_drone_play_led_animation_spinner,
                    "Blink green",
                    listOf(
                        "Blink green red", "Blink green", "Blink red", "Blink orange",
                        "Snake green red", "Fire", "Standard", "Red", "Green", "Red snake",
                        "Blank", "Right missile", "Left missile", "Double missile",
                        "Front left green others red", "Front right green others red",
                        "Rear right green others red", "Rear left green others red",
                        "Left green right red", "Left red right green", "Blink standard"
                    )
                ), arrayOf(
                    JumpingSumoAnimationsBrick::class.java.simpleName,
                    JumpingSumoAnimationsBrick(),
                    R.id.brick_jumping_sumo_animation_spinner,
                    "Spin",
                    listOf(
                        "Spin", "Tab", "Slowshake", "Metronome", "Ondulation", "Spinjump",
                        "Spiral", "Slalom"
                    )
                ), arrayOf(
                    JumpingSumoSoundBrick::class.java.simpleName,
                    JumpingSumoSoundBrick(),
                    R.id.brick_jumping_sumo_sound_spinner,
                    "Normal",
                    listOf("Normal", "Robot", "Insect", "Monster")
                )
            )
        }

        private fun phiroData(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    PhiroMotorMoveForwardBrick::class.java.simpleName,
                    PhiroMotorMoveForwardBrick(),
                    R.id.brick_phiro_motor_forward_action_spinner,
                    "Left",
                    listOf("Left", "Right", "Both")
                ), arrayOf(
                    PhiroMotorMoveBackwardBrick::class.java.simpleName,
                    PhiroMotorMoveBackwardBrick(),
                    R.id.brick_phiro_motor_backward_action_spinner,
                    "Left",
                    listOf("Left", "Right", "Both")
                ), arrayOf(
                    PhiroMotorStopBrick::class.java.simpleName,
                    PhiroMotorStopBrick(),
                    R.id.brick_phiro_stop_motor_spinner,
                    "Both",
                    listOf("Left", "Right", "Both")
                ), arrayOf(
                    PhiroPlayToneBrick::class.java.simpleName,
                    PhiroPlayToneBrick(),
                    R.id.brick_phiro_select_tone_spinner,
                    "Do",
                    listOf("Do", "Re", "Mi", "Fa", "So", "La", "Ti")
                ), arrayOf(
                    PhiroRGBLightBrick::class.java.simpleName,
                    PhiroRGBLightBrick(),
                    R.id.brick_phiro_rgb_light_spinner,
                    "Both",
                    listOf("Left", "Right", "Both")
                ), arrayOf(
                    PhiroIfLogicBeginBrick::class.java.simpleName,
                    PhiroIfLogicBeginBrick(),
                    R.id.brick_phiro_sensor_action_spinner,
                    "Front Left Sensor",
                    listOf(
                        "Front Left Sensor", "Front Right Sensor", "Side Left Sensor",
                        "Side Right Sensor", "Bottom Left Sensor", "Bottom Right Sensor"
                    )
                )
            )
        }
    }
}
