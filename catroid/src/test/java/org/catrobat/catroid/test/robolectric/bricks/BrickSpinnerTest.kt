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
import org.catrobat.catroid.ui.SpriteActivity
import android.widget.Spinner
import org.junit.Before
import org.robolectric.Robolectric
import org.catrobat.catroid.R
import org.hamcrest.CoreMatchers
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.common.Nameable
import android.app.Activity
import android.os.Build
import android.view.View
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.SetNfcTagBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.io.File
import java.lang.Exception
import java.util.ArrayList

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BrickSpinnerTest(
    private val name: String,
    private val brick: Brick,
    @field:IdRes @param:IdRes private val spinnerId: Int,
    private val expectedSelection: String,
    private val expectedContent: List<String>
) {
    private var activity: SpriteActivity? = null
    var brickSpinner: Spinner? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = activityController.get()
        createProject(activity)
        activityController.create().resume()

        val scriptFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
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
        assertEquals(expectedSelection, (brickSpinner?.selectedItem as Nameable).name)
    }

    @Test
    fun spinnerContentTest() {
        val spinnerContent: MutableList<String> = ArrayList()
        for (index in 0 until (brickSpinner?.adapter?.count ?: 0)) {
            spinnerContent.add((brickSpinner?.adapter?.getItem(index) as Nameable).name)
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

        val sprite2 = Sprite("otherTestSprite")
        project.defaultScene.addSprite(sprite2)

        val scene2 = Scene("Scene 2", project)
        project.addScene(scene2)

        val soundInfo = SoundInfo()
        soundInfo.file = Mockito.mock(File::class.java)
        soundInfo.name = "someSound"
        val soundInfoList = sprite.soundList
        soundInfoList.add(soundInfo)

        val lookData = LookData()
        lookData.file = Mockito.mock(File::class.java)
        lookData.name = "someLook"
        val lookDataList = sprite.lookList
        lookDataList.add(lookData)

        val backgroundLookData = LookData()
        backgroundLookData.file = Mockito.mock(File::class.java)
        backgroundLookData.name = "someBackground"

        val backgroundLookDataList = project.defaultScene.backgroundSprite.lookList
        backgroundLookDataList.add(backgroundLookData)

        project.addUserVariable(UserVariable("someVariable"))
        project.addUserList(UserList("someList"))

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            dataChunk1() + dataChunk2() + dataChunk3() + dataChunk4()

        private fun dataChunk1(): Collection<Array<Any>> = listOf(
            arrayOf(
                SceneTransitionBrick::class.java.simpleName,
                SceneTransitionBrick(""),
                R.id.brick_scene_transition_spinner,
                "Scene 2",
                listOf("new…", "Scene 2")
            ), arrayOf(
                SceneStartBrick::class.java.simpleName,
                SceneStartBrick(""),
                R.id.brick_scene_start_spinner,
                "Scene",
                listOf("new…", "Scene", "Scene 2")
            ), arrayOf(
                CloneBrick::class.java.simpleName,
                CloneBrick(),
                R.id.brick_clone_spinner,
                "yourself",
                listOf("yourself", "otherTestSprite")
            ), arrayOf(
                SetNfcTagBrick::class.java.simpleName,
                SetNfcTagBrick(),
                R.id.brick_set_nfc_tag_ndef_record_spinner,
                "HTTPS",
                listOf(
                    "Text",
                    "HTTP",
                    "HTTPS",
                    "SMS",
                    "Phone number",
                    "E-Mail",
                    "External type",
                    "Empty"
                )
            ), arrayOf(
                GoToBrick::class.java.simpleName,
                GoToBrick(),
                R.id.brick_go_to_spinner,
                "touch position",
                listOf("touch position", "random position", "otherTestSprite")
            ), arrayOf(
                PointToBrick::class.java.simpleName,
                PointToBrick(),
                R.id.brick_point_to_spinner,
                "otherTestSprite",
                listOf("new…", "otherTestSprite")
            ), arrayOf(
                SetRotationStyleBrick::class.java.simpleName,
                SetRotationStyleBrick(),
                R.id.brick_set_rotation_style_spinner,
                "left-right only",
                listOf("left-right only", "all-around", "don't rotate")
            )
        )

        private fun dataChunk2(): Collection<Array<Any>> = listOf(
            arrayOf(
                PlaySoundBrick::class.java.simpleName,
                PlaySoundBrick(),
                R.id.brick_play_sound_spinner,
                "someSound",
                listOf("new…", "someSound")
            ), arrayOf(
                PlaySoundAndWaitBrick::class.java.simpleName,
                PlaySoundAndWaitBrick(),
                R.id.brick_play_sound_spinner,
                "someSound",
                listOf("new…", "someSound")
            ), arrayOf(
                AskSpeechBrick::class.java.simpleName,
                AskSpeechBrick(),
                R.id.brick_ask_speech_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            ), arrayOf(
                SetLookBrick::class.java.simpleName,
                SetLookBrick(),
                R.id.brick_set_look_spinner,
                "someLook",
                listOf("new…", "someLook")
            ), arrayOf(
                SetBackgroundBrick::class.java.simpleName,
                SetBackgroundBrick(),
                R.id.brick_set_background_spinner,
                "someBackground",
                listOf("new…", "someBackground")
            ), arrayOf(
                SetBackgroundAndWaitBrick::class.java.simpleName,
                SetBackgroundAndWaitBrick(),
                R.id.brick_set_background_spinner,
                "someBackground",
                listOf("new…", "someBackground")
            ), arrayOf(
                SetVariableBrick::class.java.simpleName,
                SetVariableBrick(),
                R.id.set_variable_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            )
        )

        private fun dataChunk3(): Collection<Array<Any>> = listOf(
            arrayOf(
                ChangeVariableBrick::class.java.simpleName,
                ChangeVariableBrick(),
                R.id.change_variable_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            ), arrayOf(
                ShowTextBrick::class.java.simpleName,
                ShowTextBrick(),
                R.id.show_variable_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            ), arrayOf(
                ShowTextColorSizeAlignmentBrick::class.java.simpleName + " variable",
                ShowTextColorSizeAlignmentBrick(),
                R.id.show_variable_color_size_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            ), arrayOf(
                ShowTextColorSizeAlignmentBrick::class.java.simpleName + " alignment",
                ShowTextColorSizeAlignmentBrick(),
                R.id.brick_show_variable_color_size_align_spinner,
                "centered",
                listOf("left", "centered", "right")
            ), arrayOf(
                HideTextBrick::class.java.simpleName,
                HideTextBrick(),
                R.id.hide_variable_spinner,
                "someVariable",
                listOf("new…", "someVariable")
            ), arrayOf(
                AddItemToUserListBrick::class.java.simpleName,
                AddItemToUserListBrick(),
                R.id.add_item_to_userlist_spinner,
                "someList",
                listOf("new…", "someList")
            ), arrayOf(
                DeleteItemOfUserListBrick::class.java.simpleName,
                DeleteItemOfUserListBrick(),
                R.id.delete_item_of_userlist_spinner,
                "someList",
                listOf("new…", "someList")
            ), arrayOf(
                InsertItemIntoUserListBrick::class.java.simpleName,
                InsertItemIntoUserListBrick(),
                R.id.insert_item_into_userlist_spinner,
                "someList",
                listOf("new…", "someList")
            ), arrayOf(
                ReplaceItemInUserListBrick::class.java.simpleName,
                ReplaceItemInUserListBrick(),
                R.id.replace_item_in_userlist_spinner,
                "someList",
                listOf("new…", "someList")
            ), arrayOf(
                WhenBackgroundChangesBrick::class.java.simpleName,
                WhenBackgroundChangesBrick(),
                R.id.brick_when_background_spinner,
                "someBackground",
                listOf("new…", "someBackground")
            )
        )

        private fun dataChunk4(): Collection<Array<Any>> = listOf(
            arrayOf(
                WhenBounceOffBrick::class.java.simpleName,
                WhenBounceOffBrick(WhenBounceOffScript(null)),
                R.id.brick_when_bounce_off_spinner,
                "\u0000any edge, actor, or object\u0000",
                listOf(
                    "\u0000any edge, actor, or object\u0000",
                    "Background",
                    "testSprite",
                    "otherTestSprite"
                )
            ), arrayOf(
                WhenNfcBrick::class.java.simpleName,
                WhenNfcBrick(),
                R.id.brick_when_nfc_spinner,
                "all",
                listOf("Edit list of NFC tags", "all")
            ), arrayOf(
                WhenGamepadButtonBrick::class.java.simpleName,
                WhenGamepadButtonBrick(WhenGamepadButtonScript("")),
                R.id.brick_when_gamepad_button_spinner,
                "A",
                listOf("A", "B", "up", "down", "left", "right")
            )
        )
    }
}
