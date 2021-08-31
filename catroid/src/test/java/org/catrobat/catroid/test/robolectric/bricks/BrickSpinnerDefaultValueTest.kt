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
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import android.app.Activity
import android.content.Context
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.Before
import org.catrobat.catroid.ui.SpriteActivity
import org.robolectric.Robolectric
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.bricks.Brick
import junit.framework.TestCase
import android.widget.Spinner
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.junit.After
import org.junit.Test
import org.robolectric.annotation.Config
import java.lang.Exception
import java.util.ArrayList

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BrickSpinnerDefaultValueTest(
    var name: String,
    var category: String,
    var brickClazz: Class<*>,
    var spinnerId: Int,
    var expected: String
) {
    private var categoryBricksFactory: CategoryBricksFactory? = null
    private var activity: Activity? = null
    private val speechAISettings: List<String> = ArrayList(
        listOf(
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
        )
    )

    @Before
    @kotlin.jvm.Throws(Exception::class)
    fun setUp() {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = activityController.get()
        createProject(activity)
        activityController.create().resume()

        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences((activity as SpriteActivity?)?.applicationContext)
            .edit()
        for (setting in speechAISettings) {
            sharedPreferencesEditor.putBoolean(setting, true)
        }
        sharedPreferencesEditor.commit()

        categoryBricksFactory = CategoryBricksFactory()
    }

    @After
    fun tearDown() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(activity?.applicationContext).edit()
        sharedPreferencesEditor.clear().commit()
    }

    fun createProject(context: Context?) {
        val project = Project(context, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()

        script.addBrick(SetXBrick())
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    private val brickFromCategoryBricksFactory: Brick?
        get() {
            var brickInAdapter: Brick? = null
            activity?.let {
                val categoryBricks = categoryBricksFactory?.getBricks(
                    category, false, it
                ) ?: listOf()
                for (brick in categoryBricks) {
                    if (brickClazz.isInstance(brick)) {
                        brickInAdapter = brick
                        break
                    }
                }
            }
            TestCase.assertNotNull(brickInAdapter)
            return brickInAdapter
        }

    @Test
    fun testDefaultSpinnerSelection() {
        val brick = brickFromCategoryBricksFactory
        val brickView = brick?.getView(activity)
        TestCase.assertNotNull(brickView)
        val brickSpinner = brickView?.findViewById<View>(spinnerId) as Spinner
        TestCase.assertNotNull(brickSpinner)
        TestCase.assertEquals(expected, (brickSpinner.selectedItem as Nameable).name)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            eventBricks() + soundBricks() + looksBricks() + dataBricks() + controlBricks()

        private fun eventBricks(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(
                "WhenBounceOffBrick - R.id.brick_when_bounce_off_spinner",
                "Event",
                WhenBounceOffBrick::class.java,
                R.id.brick_when_bounce_off_spinner,
                "\u0000any edge, actor, or object\u0000"
            ),
            arrayOf<Any>(
                "WhenBackgroundChangesBrick - R.id.brick_when_background_spinner",
                "Event",
                WhenBackgroundChangesBrick::class.java,
                R.id.brick_when_background_spinner,
                "new…"
            )
        )

        private fun soundBricks(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(
                "PlaySoundBrick - R.id.brick_play_sound_spinner",
                "Sound",
                PlaySoundBrick::class.java,
                R.id.brick_play_sound_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "PlaySoundAndWaitBrick - R.id.brick_play_sound_spinner",
                "Sound",
                PlaySoundAndWaitBrick::class.java,
                R.id.brick_play_sound_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "AskSpeechBrick - R.id.brick_ask_speech_spinner",
                "Sound",
                AskSpeechBrick::class.java,
                R.id.brick_ask_speech_spinner,
                "new…"
            )
        )

        private fun looksBricks(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(
                "SetLookBrick - R.id.brick_set_look_spinner",
                "Looks",
                SetLookBrick::class.java,
                R.id.brick_set_look_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "AskBrick - R.id.brick_ask_spinner",
                "Looks",
                AskBrick::class.java,
                R.id.brick_ask_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "WhenBackgroundChangesBrick - R.id.brick_when_background_spinner",
                "Looks",
                WhenBackgroundChangesBrick::class.java,
                R.id.brick_when_background_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "SetBackgroundBrick - R.id.brick_set_look_spinner",
                "Looks",
                SetBackgroundBrick::class.java,
                R.id.brick_set_background_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "SetBackgroundAndWaitBrick - R.id.brick_set_look_spinner",
                "Looks",
                SetBackgroundAndWaitBrick::class.java,
                R.id.brick_set_background_spinner,
                "new…"
            )
        )

        private fun dataBricks(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(
                "SetVariableBrick - R.id.set_variable_spinner",
                "Data",
                SetVariableBrick::class.java,
                R.id.set_variable_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "ChangeVariableBrick - R.id.change_variable_spinner",
                "Data",
                ChangeVariableBrick::class.java,
                R.id.change_variable_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "ShowTextBrick - R.id.show_variable_spinner",
                "Data",
                ShowTextBrick::class.java,
                R.id.show_variable_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "HideTextBrick - R.id.hide_variable_spinner",
                "Data",
                HideTextBrick::class.java,
                R.id.hide_variable_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "AddItemToUserListBrick - R.id.add_item_to_userlist_spinner",
                "Data",
                AddItemToUserListBrick::class.java,
                R.id.add_item_to_userlist_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "DeleteItemOfUserListBrick - R.id.delete_item_of_userlist_spinner",
                "Data",
                DeleteItemOfUserListBrick::class.java,
                R.id.delete_item_of_userlist_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "InsertItemIntoUserListBrick - R.id.insert_item_into_userlist_spinner",
                "Data",
                InsertItemIntoUserListBrick::class.java,
                R.id.insert_item_into_userlist_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "ReplaceItemInUserListBrick - R.id.replace_item_in_userlist_spinner",
                "Data",
                ReplaceItemInUserListBrick::class.java,
                R.id.replace_item_in_userlist_spinner,
                "new…"
            )
        )

        private fun controlBricks(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(
                "SceneTransitionBrick - R.id.brick_scene_transition_spinner",
                "Control",
                SceneTransitionBrick::class.java,
                R.id.brick_scene_transition_spinner,
                "new…"
            ),
            arrayOf<Any>(
                "SceneStartBrick - R.id.brick_scene_start_spinner",
                "Control",
                SceneStartBrick::class.java,
                R.id.brick_scene_start_spinner,
                "Scene"
            ),
            arrayOf<Any>(
                "CloneBrick - R.id.brick_clone_spinner",
                "Control",
                CloneBrick::class.java,
                R.id.brick_clone_spinner,
                "yourself"
            )
        )
    }
}
