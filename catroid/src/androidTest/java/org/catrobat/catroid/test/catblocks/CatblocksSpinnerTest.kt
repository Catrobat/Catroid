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

package org.catrobat.catroid.test.catblocks

import android.content.Context
import android.webkit.WebView
import android.widget.Spinner
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.UpdateableSpinnerBrick
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerBrickUtils
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParser
import java.io.File

@RunWith(AndroidJUnit4::class)
class CatblocksSpinnerTest {
    companion object {
        private const val TIMEOUT: Long = (60 * 1000).toLong()
    }

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        CatblocksScriptFragment.testingMode = true
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
    }

    @After
    fun tearDown() {
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testControlSpinners() {
        executeTest(R.string.category_control)
    }

    @Test
    fun testMotionSpinners() {
        executeTest(R.string.category_motion)
    }

    @Test
    fun testSoundSpinners() {
        executeTest(R.string.category_sound)
    }

    @Test
    fun testLooksSpinners() {
        executeTest(R.string.category_looks)
    }

    @Test
    fun testPenSpinners() {
        executeTest(R.string.category_pen)
    }

    @Test
    fun testUserBricksSpinners() {
        executeTest(R.string.category_user_bricks)
    }

    @Test
    fun testCategoryDataSpinners() {
        executeTest(R.string.category_data)
    }

    @Test
    fun testDeviceSpinners() {
        executeTest(R.string.category_device)
    }

    @Test
    fun testLegoNxtSpinners() {
        executeTest(R.string.category_lego_nxt)
    }

    @Test
    fun testLegoEv3Spinners() {
        executeTest(R.string.category_lego_ev3)
    }

    @Test
    fun testArduinoSpinners() {
        executeTest(R.string.category_arduino)
    }

    @Test
    fun testDroneSpinners() {
        executeTest(R.string.category_drone)
    }

    @Test
    fun testJumpingSumoSpinners() {
        executeTest(R.string.category_jumping_sumo)
    }

    @Test
    fun testPhiroSpinners() {
        executeTest(R.string.category_phiro)
    }

    @Test
    fun testCastSpinners() {
        executeTest(R.string.category_cast)
    }

    @Test
    fun testRaspiSpinners() {
        executeTest(R.string.category_raspi)
    }

    @Test
    fun testEmbrioderySpinners() {
        executeTest(R.string.category_embroidery)
    }

    @Test
    fun testAssertionsSpinners() {
        executeTest(R.string.category_assertions)
    }

    @Test
    fun testEventSpinners() {
        executeTest(R.string.category_event)
    }

    private fun executeTest(categoryStringResource: Int) {
        val categoryName = ApplicationProvider.getApplicationContext<Context>().getString(categoryStringResource)
        createProject()
        val bricksToEdit = addBricksForTesting(categoryName)
        baseActivityTestRule.launchActivity()
        modifySpecialBricks(bricksToEdit)

        toggleCatblocksView()

        editBrickSpinners(bricksToEdit)
    }

    private fun editBrickSpinners(bricksToEdit: List<UpdateableSpinnerBrick>) {
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        waitForFinishedRendering(bricksToEdit, webViewUtils)
        val expectedValuesMap = changeCatblocksSpinnerSelection(bricksToEdit, webViewUtils)
        toggleCatblocksView()
        compareCatblocksSpinnersWithCatroidSpinners(bricksToEdit, expectedValuesMap)
    }

    private fun changeCatblocksSpinnerSelection(
        bricksToEdit: List<UpdateableSpinnerBrick>,
        webViewUtils: WebViewUtils
    ): MutableMap<String, String> {
        val expectedValuesMap = mutableMapOf<String, String>()
        for (spinnerBrick in bricksToEdit) {
            if (spinnerBrick !is BrickBaseType) {
                continue
            }
            val brickID = if (spinnerBrick is ScriptBrick) {
                (spinnerBrick as ScriptBrick).script.javaClass.simpleName + "-0"
            } else {
                spinnerBrick.javaClass.simpleName + "-0"
            }
            val allSpinnersIdsFromBrick = getAllSpinnerIdsFromBrick(spinnerBrick)
            for (spinnerId in allSpinnersIdsFromBrick) {
                val catblocksSpinnerId = "$brickID-spinner-$spinnerId"
                webViewUtils.waitForElement("#$catblocksSpinnerId")
                scrollToField(catblocksSpinnerId)
                webViewUtils.openBlocklySpinner(catblocksSpinnerId)
                webViewUtils.waitForElementVisible(".blocklyDropDownDiv")
                val spinnerItemToSelect = webViewUtils.getNotSelectedBlocklySpinnerItem()
                Assert.assertNotNull(spinnerItemToSelect)
                expectedValuesMap["${spinnerBrick.brickID}_$spinnerId"] = spinnerItemToSelect!!.text
                webViewUtils.selectBlocklySpinnerItem(spinnerItemToSelect!!.id)
                webViewUtils.waitForElementInvisible(".blocklyDropDownDiv")
            }
        }
        return expectedValuesMap
    }

    private fun compareCatblocksSpinnersWithCatroidSpinners(
        bricksToEdit: List<UpdateableSpinnerBrick>,
        expectedValuesMap: MutableMap<String, String>
    ) {
        val scriptFragment = baseActivityTestRule.activity.supportFragmentManager
            .findFragmentByTag(ScriptFragment.TAG) as ScriptFragment

        val regexWhitespaces = Regex("\\w|\\r|\\n")

        for (spinnerBrick in bricksToEdit) {
            if (spinnerBrick !is BrickBaseType) {
                continue
            }
            scriptFragment.focusBrick(spinnerBrick)
            val allSpinnersIdsFromBrick = getAllSpinnerIdsFromBrick(spinnerBrick)
            for (spinnerId in allSpinnersIdsFromBrick) {
                val expectedValue = expectedValuesMap["${spinnerBrick.brickID}_$spinnerId"]
                val spinnerViewId = SpinnerBrickUtils.getSpinnerIdByIdName(spinnerId)
                val spinner = spinnerBrick.view.findViewById<Spinner>(spinnerViewId)
                val selectedItem = spinner.selectedItem
                val expectedValueWithoutWhitespaces = regexWhitespaces.replace(expectedValue!!, "")
                val actualValue = if (selectedItem is Nameable) {
                    regexWhitespaces.replace(selectedItem.name, "")
                } else {
                    regexWhitespaces.replace(selectedItem.toString(), "")
                }
                Assert.assertEquals(expectedValueWithoutWhitespaces, actualValue)
            }
        }
    }

    private fun waitForFinishedRendering(
        bricksToEdit: List<UpdateableSpinnerBrick>,
        webViewUtils: WebViewUtils
    ) {
        if (bricksToEdit.any()) {
            val brick = bricksToEdit[0] as BrickBaseType
            val brickID = if (brick is ScriptBrick) {
                (brick as ScriptBrick).script.javaClass.simpleName + "-0"
            } else {
                brick.javaClass.simpleName + "-0"
            }
            webViewUtils.waitForElement("#$brickID")
        }
    }

    @Suppress("NestedBlockDepthComplexMethod", "NestedBlockDepth")
    private fun getAllSpinnerIdsFromBrick(brick: BrickBaseType): List<String> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val brickViewXmlParser = context.resources.getLayout(brick.viewResource)
        val spinnerIds = arrayListOf<String>()
        while (brickViewXmlParser.eventType != XmlPullParser.END_DOCUMENT) {
            if (brickViewXmlParser.eventType == XmlPullParser.START_TAG && brickViewXmlParser.name != null &&
                brickViewXmlParser.name.equals("Spinner")) {
                for (attributeIndex in 0 until brickViewXmlParser.attributeCount) {
                    val attributeName = brickViewXmlParser.getAttributeName(attributeIndex)
                    if (attributeName == "id") {
                        val idValue = brickViewXmlParser.getAttributeValue(attributeIndex)
                            .trim('@').toInt()
                        spinnerIds.add(context.resources.getResourceEntryName(idValue))
                    }
                }
            }
            brickViewXmlParser.next()
        }
        return spinnerIds
    }

    private fun toggleCatblocksView() {
        Espresso.openContextualActionModeOverflowMenu()
        Espresso.onView(ViewMatchers.withText(R.string.catblocks)).perform(ViewActions.click())
    }

    private fun modifySpecialBricks(allBricks: List<UpdateableSpinnerBrick>) {
        for (brick in allBricks) {
            if (brick is BroadcastMessageBrick) {
                val broadcastMessage = brick.javaClass.simpleName
                projectManager.currentProject.broadcastMessageContainer.broadcastMessages.add(broadcastMessage)
                brick.broadcastMessage = broadcastMessage
            }
        }
    }

    @Suppress("NestedBlockDepthComplexMethod")
    private fun addBricksForTesting(category: String): List<UpdateableSpinnerBrick> {
        val bricksFromCategory = CategoryBricksFactory().getBricks(category, false, ApplicationProvider.getApplicationContext())
        val script = StartScript()
        projectManager.currentSprite.addScript(script)
        val addedBricks = arrayListOf<UpdateableSpinnerBrick>()

        for (brick in bricksFromCategory) {
            if (brick is SetListeningLanguageBrick) {
                continue
            }

            if (!addedBricks.any { existingBrick -> existingBrick::class.simpleName == brick::class.simpleName } &&
                brick is UpdateableSpinnerBrick) {
                val clonedBrick = brick.clone()
                addedBricks.add(clonedBrick as UpdateableSpinnerBrick)

                if (brick !is ScriptBrick) {
                    script.addBrick(clonedBrick)
                    clonedBrick.parent = script.scriptBrick
                } else {
                    projectManager.currentSprite.addScript(clonedBrick.script)
                }
            }
        }

        return addedBricks
    }

    private fun scrollToField(domFieldID: String) {
        baseActivityTestRule.runOnUiThread {
            val webview = baseActivityTestRule.activity.findViewById<WebView>(R.id.catblocksWebView)
            webview!!.evaluateJavascript(
                "javascript:CatBlocks.scrollToInputField('$domFieldID');", null)
        }
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
    }
}
