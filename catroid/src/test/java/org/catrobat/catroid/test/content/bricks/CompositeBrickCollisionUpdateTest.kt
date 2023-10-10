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

import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.Brick.FormulaField
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.test.PowerMockUtil.Companion.mockStaticAppContextAndInitializeStaticSingletons
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import java.util.Arrays

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(
    CatroidApplication::class
)
class CompositeBrickCollisionUpdateTest {
    private var formulaBrick: FormulaBrick? = null
    private var sprite: Sprite? = null
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var compositeBrickClass: Class<CompositeBrick>? = null
    @Before
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun setUp() {
        mockStaticAppContextAndInitializeStaticSingletons()
        val project = Project()
        val scene = Scene()
        sprite = Sprite(VARIABLE_NAME)
        val script: Script = WhenScript()
        val compositeBrick = compositeBrickClass!!.newInstance()
        formulaBrick = SetXBrick()
        project.addScene(scene)
        scene.addSprite(sprite)
        sprite!!.addScript(script)
        script.addBrick(compositeBrick)
        compositeBrick.nestedBricks.add(formulaBrick)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = scene
    }

    @Test
    fun testRenameSprite() {
        val newFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.COLLISION_FORMULA,
                VARIABLE_NAME, null
            )
        )
        val map = formulaBrick!!.formulaMap
        map.forEach { (k: FormulaField?, v: Formula?) ->
            formulaBrick!!.setFormulaWithBrickField(
                k,
                newFormula
            )
        }
        sprite!!.rename(NEW_VARIABLE_NAME)
        map.forEach { (k: FormulaField?, v: Formula) ->
            Assert.assertEquals(
                v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
                REPLACED_VARIABLE
            )
        }
    }

    @Test
    fun testRenameSpriteNoChange() {
        val newFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.COLLISION_FORMULA,
                DIFFERENT_VARIABLE_NAME, null
            )
        )
        val map = formulaBrick!!.formulaMap
        map.forEach { (k: FormulaField?, v: Formula?) ->
            formulaBrick!!.setFormulaWithBrickField(
                k,
                newFormula
            )
        }
        sprite!!.rename(NEW_VARIABLE_NAME)
        map.forEach { (k: FormulaField?, v: Formula) ->
            Assert.assertEquals(
                v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
                NO_CHANGE_VARIABLE
            )
        }
    }

    companion object {
        private const val VARIABLE_NAME = "Test"
        private const val DIFFERENT_VARIABLE_NAME = "Abcd"
        private const val NEW_VARIABLE_NAME = "NewName"
        private const val REPLACED_VARIABLE = "null(" + NEW_VARIABLE_NAME + ") "
        private const val NO_CHANGE_VARIABLE = "null(" + DIFFERENT_VARIABLE_NAME + ") "
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        IfThenLogicBeginBrick::class.java.simpleName,
                        IfThenLogicBeginBrick::class.java
                    ), arrayOf(
                        ForeverBrick::class.java.simpleName, ForeverBrick::class.java
                    ), arrayOf(
                        RepeatBrick::class.java.simpleName, RepeatBrick::class.java
                    ), arrayOf(
                        RepeatUntilBrick::class.java.simpleName, RepeatUntilBrick::class.java
                    )
                )
            )
        }
    }
}
