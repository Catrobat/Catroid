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

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.content.bricks.Brick
import org.junit.Before
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.stage.StageResourceHolder
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.content.bricks.CameraBrick
import android.Manifest.permission
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.test.content.bricks.BrickPermissionTest
import org.junit.Assert
import org.junit.Test
import java.util.Arrays

@RunWith(Parameterized::class)
class BrickPermissionTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null

    @Parameterized.Parameter(1)
    lateinit var bricks: Array<Brick>

    @Parameterized.Parameter(2)
    lateinit var expectedPermission: Array<String>
    var script: Script? = null
    @Before
    fun setUp() {
        val project = Project()
        val scene = Scene()
        project.addScene(scene)
        val sprite = Sprite()
        scene.addSprite(sprite)
        script = StartScript()
        sprite.addScript(script)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testGetProjectRuntimePermissionList() {
        script!!.brickList.addAll(Arrays.asList(*bricks))
        checkProjectRuntimePermissions()
    }

    @Test
    fun testRuntimePermissionInsideIf() {
        val ifBrick = IfLogicBeginBrick()
        for (brick in bricks) {
            ifBrick.addBrickToIfBranch(brick)
        }
        script!!.addBrick(ifBrick)
        checkProjectRuntimePermissions()
    }

    @Test
    fun testRuntimePermissionInsideElse() {
        val ifBrick = IfLogicBeginBrick()
        for (brick in bricks) {
            ifBrick.addBrickToElseBranch(brick)
        }
        script!!.addBrick(ifBrick)
        checkProjectRuntimePermissions()
    }

    @Test
    fun testRuntimePermissionInsideForever() {
        val foreverBrick = ForeverBrick()
        for (brick in bricks) {
            foreverBrick.addBrick(brick)
        }
        script!!.addBrick(foreverBrick)
        checkProjectRuntimePermissions()
    }

    @Test
    fun testRuntimePermissionInsideRepeat() {
        val repeatBrick = RepeatBrick()
        for (brick in bricks) {
            repeatBrick.addBrick(brick)
        }
        script!!.addBrick(repeatBrick)
        checkProjectRuntimePermissions()
    }

    @Test
    fun testDoubleNestedPermission() {
        val repeatBrick0 = RepeatBrick()
        val repeatBrick1 = RepeatBrick()
        for (brick in bricks) {
            repeatBrick1.addBrick(brick)
        }
        repeatBrick0.addBrick(repeatBrick1)
        script!!.addBrick(repeatBrick0)
        checkProjectRuntimePermissions()
    }

    private fun checkProjectRuntimePermissions() {
        val requestedString = StageResourceHolder.getProjectsRuntimePermissionList()
        Assert.assertTrue(requestedString.containsAll(Arrays.asList(*expectedPermission)))
        Assert.assertTrue(Arrays.asList(*expectedPermission).containsAll(requestedString))
    }

    companion object {
        private val brickWithGPS: Brick = SetVariableBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    "LONGITUDE",
                    null
                )
            ), UserVariable("x")
        )
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "CameraBrick",
                        arrayOf<Brick>(CameraBrick()),
                        arrayOf(permission.CAMERA)
                    ),
                    arrayOf(
                        "LegoNxtMotorMoveBrick",
                        arrayOf<Brick>(LegoNxtMotorMoveBrick()),
                        arrayOf(permission.BLUETOOTH_ADMIN, permission.BLUETOOTH)
                    ),
                    arrayOf(
                        "CameraBrick + LegoNxtMotorTurnAngleBrick",
                        arrayOf<Brick>(CameraBrick(), LegoNxtMotorTurnAngleBrick()),
                        arrayOf(permission.CAMERA, permission.BLUETOOTH_ADMIN, permission.BLUETOOTH)
                    ),
                    arrayOf(
                        "AskSpeechBrick",
                        arrayOf<Brick>(AskSpeechBrick()),
                        arrayOf(permission.RECORD_AUDIO)
                    ),
                    arrayOf(
                        "WhenGamepadButtonBrick",
                        arrayOf<Brick>(WhenGamepadButtonBrick(WhenGamepadButtonScript())),
                        arrayOf(
                            permission.CHANGE_WIFI_MULTICAST_STATE,
                            permission.CHANGE_WIFI_STATE,
                            permission.ACCESS_WIFI_STATE
                        )
                    ),
                    arrayOf(
                        "WhenNfcBrick",
                        arrayOf<Brick>(WhenNfcBrick()),
                        arrayOf(permission.NFC)
                    ),
                    arrayOf(
                        "WhenNfcBrick + GPS",
                        arrayOf(WhenNfcBrick(), brickWithGPS),
                        arrayOf(
                            permission.NFC,
                            permission.ACCESS_FINE_LOCATION,
                            permission.ACCESS_COARSE_LOCATION
                        )
                    ),
                    arrayOf(
                        "Brick With GPS Formula",
                        arrayOf(
                            brickWithGPS
                        ),
                        arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
                    )
                )
            )
        }
    }
}