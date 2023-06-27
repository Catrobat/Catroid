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

package org.catrobat.catroid.uiespresso.content.brick.app

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class CatblocksEditFieldsTest {

    companion object {
        private const val TIMEOUT: Long = (60 * 1000).toLong()
        private const val MAX_TRIES = 5
    }

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
    }

    @After
    fun tearDown() {
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun editControlFormulaFields() {
        executeTest(R.string.category_control)
    }

    @Test
    fun editMotionFields() {
        executeTest(R.string.category_motion)
    }

    @Test
    fun editSoundFields() {
        executeTest(R.string.category_sound)
    }

    @Test
    fun editLooksFields() {
        executeTest(R.string.category_looks)
    }

    @Test
    fun editPenFields() {
        executeTest(R.string.category_pen)
    }

    @Test
    fun editUserBricksFields() {
        executeTest(R.string.category_user_bricks)
    }

    @Test
    fun editCategoryDataFields() {
        executeTest(R.string.category_data)
    }

    @Test
    fun editDeviceFields() {
        executeTest(R.string.category_device)
    }

    @Test
    fun editLegoNxtFields() {
        executeTest(R.string.category_lego_nxt)
    }

    @Test
    fun editLegoEv3Fields() {
        executeTest(R.string.category_lego_ev3)
    }

    @Test
    fun editArduinoFields() {
        executeTest(R.string.category_arduino)
    }

    @Test
    fun editDroneFields() {
        executeTest(R.string.category_drone)
    }

    @Test
    fun editJumpingSumoFields() {
        executeTest(R.string.category_jumping_sumo)
    }

    @Test
    fun editPhiroFields() {
        executeTest(R.string.category_phiro)
    }

    @Test
    fun editCastFields() {
        executeTest(R.string.category_cast)
    }

    @Test
    fun editRaspiFields() {
        executeTest(R.string.category_raspi)
    }

    @Test
    fun editEmbrioderyFields() {
        executeTest(R.string.category_embroidery)
    }

    @Test
    fun editAssertionsFields() {
        executeTest(R.string.category_assertions)
    }

    @Test
    fun editEventFields() {
        executeTest(R.string.category_event)
    }

    private fun executeTest(categoryStringResource: Int) {
        val categoryName = ApplicationProvider.getApplicationContext<Context>().getString(categoryStringResource)
        createProject()
        val bricksToEdit = addBricksForTesting(categoryName)
        baseActivityTestRule.launchActivity()
        toggleCatblocksView()
        editBrickFormulas(bricksToEdit)
    }

    private fun toggleCatblocksView() {
        Espresso.openContextualActionModeOverflowMenu()
        Espresso.onView(withText(R.string.catblocks)).perform(ViewActions.click())
    }

    @Suppress("ComplexMethod", "NestedBlockDepthComplexMethod", "NestedBlockDepth")
    private fun editBrickFormulas(bricksToEdit: List<FormulaBrick>) {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        Assert.assertNotNull(uiDevice.wait(Until.findObject(By.clazz(WebView::class.java)), TIMEOUT))

        for (brickToEdit in bricksToEdit) {
            val brickID = if (brickToEdit is ScriptBrick) {
                (brickToEdit as ScriptBrick).script.javaClass.simpleName + "-0"
            } else {
                brickToEdit.javaClass.simpleName + "-0"
            }
            for (fieldID in brickToEdit.formulaMap.keys) {

                val catblocksFieldID = "$brickID-field-$fieldID"
                scrollToField(catblocksFieldID)
                val formulaField = uiDevice.findObject(
                    UiSelector().resourceId(catblocksFieldID)
                )
                println("Wait for field exist $catblocksFieldID")
                Assert.assertTrue(formulaField.waitForExists(TIMEOUT))

                Web.onWebView()
                    .withElement(DriverAtoms.findElement(Locator.ID, catblocksFieldID))
                    .perform(DriverAtoms.webClick())

                Thread.sleep(200)

                val numberToEnter = Random.nextInt(0, 10)
                var tryCounter = 0
                while (tryCounter < MAX_TRIES) {
                    try {
                        onFormulaEditor().performEnterNumber(numberToEnter)
                        break
                    } catch (t: Throwable) {
                        tryCounter++
                        if (tryCounter >= MAX_TRIES) {
                            throw t
                        }
                        Thread.sleep(1000)
                    }
                }

                Thread.sleep(200)

                pressBack()

                Thread.sleep(200)

                val catroidFieldContent = brickToEdit.formulaMap[fieldID]!!
                    .getTrimmedFormulaString(ApplicationProvider.getApplicationContext<Context>())
                    .trim().replace(" ", "")

                Assert.assertEquals(numberToEnter.toString(), catroidFieldContent)

                tryCounter = 0
                while (tryCounter < MAX_TRIES) {
                    try {
                        Web.onWebView()
                            .withElement(DriverAtoms.findElement(Locator.CSS_SELECTOR, "#$catblocksFieldID .blocklyText"))
                            .check(webMatches(getText(), containsString("$numberToEnter")))
                        break
                    } catch (t: Throwable) {
                        tryCounter++
                        if (tryCounter >= MAX_TRIES) {
                            throw t
                        }
                        Thread.sleep(1000)
                    }
                }
            }
        }
    }

    @Suppress("ComplexMethod")
    private fun addBricksForTesting(category: String): List<FormulaBrick> {
        val bricksFromCategory = CategoryBricksFactory().getBricks(category, false, ApplicationProvider.getApplicationContext())

        val script = StartScript()
        projectManager.currentSprite.addScript(script)
        val addedBricks = arrayListOf<FormulaBrick>()

        for (brick in bricksFromCategory) {
            if (!addedBricks.any { existingBrick -> existingBrick::class.simpleName == brick::class.simpleName } &&
                brick is FormulaBrick) {
                val clonedBrick = brick.clone()
                addedBricks.add(clonedBrick as FormulaBrick)

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
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }
}
