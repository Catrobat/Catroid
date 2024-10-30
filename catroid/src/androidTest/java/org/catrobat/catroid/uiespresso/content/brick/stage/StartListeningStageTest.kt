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

package org.catrobat.catroid.uiespresso.content.brick.stage

import android.Manifest
import android.speech.tts.TextToSpeech
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat.CatrobatLanguage
import org.catrobat.catroid.testsuites.annotations.Cat.SensorBox
import org.catrobat.catroid.testsuites.annotations.Cat.SettingsAndPermissions
import org.catrobat.catroid.testsuites.annotations.Level.Functional
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.Locale

class StartListeningStageTest {
    private lateinit var project: Project
    private lateinit var textToSpeech: TextToSpeech
    private val userVariableName = "var"
    private val text = "hello world coding is fun"

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        StageActivity::class.java,
        true,
        false
    )

    @get:Rule
    var runtimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp() {
        createProject(javaClass.simpleName)
        textToSpeech = TextToSpeech(ApplicationProvider.getApplicationContext(), TextToSpeech.OnInitListener
        { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        })
        baseActivityTestRule.launchActivity(null)
    }

    @Test
    @Category(
        CatrobatLanguage::class,
        Functional::class,
        SettingsAndPermissions::class,
        SensorBox::class
    )
    fun testStartListeningBrick() {
        // in case slow performance on test device
        onView(isRoot()).perform(CustomActions.wait(1200))

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        onView(isRoot()).perform(CustomActions.wait(5000))

        val userVariable = project.getUserVariable(userVariableName)
        assertEquals(text, userVariable.value.toString())
    }

    private fun createProject(projectName: String) {
        project = UiTestUtils.createDefaultTestProject(projectName)
        val startScript = UiTestUtils.getDefaultTestScript(project)
        val userVariable = UserVariable(userVariableName)
        startScript.addBrick(StartListeningBrick(userVariable))
        project.addUserVariable(userVariable)
    }
}
