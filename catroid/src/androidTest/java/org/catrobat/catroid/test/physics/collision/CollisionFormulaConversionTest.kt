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
package org.catrobat.catroid.test.physics.collision

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.utils.ScreenValueHandler
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class CollisionFormulaConversionTest {
    private var projectManager: ProjectManager? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        ScreenValueHandler.updateScreenWidthAndHeight(InstrumentationRegistry.getInstrumentation().context)
        projectManager = ProjectManager.getInstance()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        projectManager!!.currentProject = null
        TestUtils.deleteProjects(COLLISION_TEST_PROJECT)
        TestUtils.removeFromPreferences(
            InstrumentationRegistry.getInstrumentation().context,
            Constants.PREF_PROJECTNAME_KEY
        )
    }

    @Test
    @Throws(IOException::class)
    fun testFormulaUpdated() {
        val firstSpriteName = "a"
        val secondSpriteName = "b"
        val thirdSpriteName = "ab"
        val collisionTag =
            CatroidApplication.getAppContext().getString(R.string.formula_editor_function_collision)
        val project = createProjectWithOldCollisionFormulas(
            COLLISION_TEST_PROJECT,
            ApplicationProvider.getApplicationContext(),
            firstSpriteName, secondSpriteName, thirdSpriteName, collisionTag
        )
        ProjectManager.updateCollisionFormulasTo993(project)
        val sprite1 = project.defaultScene.getSprite(firstSpriteName)
        val brick = sprite1.getScript(0).getBrick(0)
        Assert.assertThat(
            brick, Matchers.`is`(
                Matchers.instanceOf(
                    FormulaBrick::class.java
                )
            )
        )
        val formulaBrick = brick as FormulaBrick
        val newFormula =
            formulaBrick.formulas[0].getTrimmedFormulaString(ApplicationProvider.getApplicationContext())
        val expected = "$collisionTag($thirdSpriteName) "
        junit.framework.Assert.assertEquals(expected, newFormula)
        TestUtils.deleteProjects()
    }

    @Test
    @Throws(IOException::class)
    fun testFormulaUpdatedWithLanguageConversion() {
        val firstSpriteName = "sprite1"
        val secondSpriteName = "sprite2"
        val thirdSpriteName = "sprite3"
        val res = CatroidApplication.getAppContext().resources
        val conf = res.configuration
        val savedLocale = conf.locale
        conf.locale = Locale.US
        res.updateConfiguration(conf, null)
        var collisionTag = res.getString(R.string.formula_editor_function_collision)
        conf.locale = savedLocale
        res.updateConfiguration(conf, null)
        collisionTag =
            CatroidApplication.getAppContext().getString(R.string.formula_editor_function_collision)
        val project = createProjectWithOldCollisionFormulas(
            COLLISION_TEST_PROJECT,
            ApplicationProvider.getApplicationContext(),
            firstSpriteName, secondSpriteName, thirdSpriteName, collisionTag
        )
        ProjectManager.updateCollisionFormulasTo993(project)
        val sprite1 = project.defaultScene.getSprite(firstSpriteName)
        val brick = sprite1.getScript(0).getBrick(0)
        Assert.assertThat(
            brick, Matchers.`is`(
                Matchers.instanceOf(
                    FormulaBrick::class.java
                )
            )
        )
        val formulaBrick = brick as FormulaBrick
        val newFormula =
            formulaBrick.formulas[0].getTrimmedFormulaString(ApplicationProvider.getApplicationContext())
        val expected = "$collisionTag($thirdSpriteName) "
        junit.framework.Assert.assertEquals(expected, newFormula)
        TestUtils.deleteProjects()
    }

    private fun createProjectWithOldCollisionFormulas(
        name: String, context: Context, firstSprite: String,
        secondSprite: String, thirdSprite: String, collisionTag: String
    ): Project {
        val project = Project(context, name)
        project.catrobatLanguageVersion = 0.992
        val sprite1 = Sprite(firstSprite)
        val sprite2 = Sprite(secondSprite)
        val sprite3 = Sprite(thirdSprite)
        val firstScript: Script = StartScript()
        val formulaElement = FormulaElement(
            FormulaElement.ElementType.COLLISION_FORMULA,
            "$firstSprite $collisionTag $thirdSprite", null
        )
        val formula1 = Formula(formulaElement)
        val ifBrick = IfLogicBeginBrick(formula1)
        firstScript.addBrick(ifBrick)
        sprite1.addScript(firstScript)
        project.defaultScene.addSprite(sprite1)
        project.defaultScene.addSprite(sprite2)
        project.defaultScene.addSprite(sprite3)
        val projectManager = ProjectManager.getInstance()
        projectManager.currentProject = project
        return project
    }

    companion object {
        private const val COLLISION_TEST_PROJECT = "COLLISION_TEST_PROJECT"
    }
}
