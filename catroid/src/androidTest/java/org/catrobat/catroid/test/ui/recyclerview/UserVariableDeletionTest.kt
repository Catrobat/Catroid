/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.test.ui.recyclerview

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.ArrayList

@RunWith(JUnit4::class)
class UserVariableDeletionTest {
    private var project: Project? = null
    private var dataListFragment: DataListFragment? = null
    private var mainScene: Scene? = null
    private var helperScene: Scene? = null
    private var mainSprite: Sprite? = null
    private var helperSprite: Sprite? = null
    private var spyDataListFragment: DataListFragment? = null
    private var changeVariableBrick: ChangeVariableBrick? = null

    private var localVariableInUse = UserVariable(LOCAL_VARIABLE_IN_USE)
    private var localVariableNotInUse = UserVariable(LOCAL_VARIABLE_IN_USE_NOT)
    private var localVariableInUseDropDown = UserVariable(LOCAL_VARIABLE_IN_USE_DROP_DOWN)
    private var globalVariableInUse = UserVariable(GLOBAL_VARIABLE_IN_USE)
    private var globalVariableNotInUse = UserVariable(GLOBAL_VARIABLE_IN_USE_NOT)

    @Before
    fun setUp() {
        dataListFragment = DataListFragment()
        spyDataListFragment = Mockito.spy<DataListFragment>(dataListFragment)
        Mockito.doNothing().`when`(spyDataListFragment)!!.showInUseAlert(any())
        Mockito.doNothing().`when`(spyDataListFragment)!!.showInDropDownDialog(any(), any())
        Mockito.doNothing().`when`(spyDataListFragment)!!.showSuccess(any())

        project = Project()
        project!!.name = PROJECT_NAME
        project!!.addUserVariable(globalVariableInUse)
        project!!.addUserVariable(globalVariableNotInUse)

        setUpCurrentScene()
        setUpHelperScene()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    private fun setUpCurrentScene() {
        val script: Script = StartScript()
        mainScene = Scene()

        mainSprite = Sprite()
        mainSprite!!.addUserVariable(localVariableInUse)
        mainSprite!!.addUserVariable(localVariableNotInUse)
        mainSprite!!.addUserVariable(localVariableInUseDropDown)

        val formulaElementChangeXByN = FormulaElement(
            FormulaElement.ElementType.USER_VARIABLE,
            LOCAL_VARIABLE_IN_USE,
            null
        )
        val formulaChangeXByN = Formula(formulaElementChangeXByN)
        val changeXByNBrick: FormulaBrick = ChangeXByNBrick(formulaChangeXByN)

        val formulaElementChangeVariable = FormulaElement(
            FormulaElement.ElementType.NUMBER,
            STRING_ONE,
            null
        )
        val formulaChangeVariable = Formula(formulaElementChangeVariable)
        changeVariableBrick = ChangeVariableBrick(formulaChangeVariable, localVariableInUseDropDown)

        script.addBrick(changeXByNBrick)
        script.addBrick(changeVariableBrick)
        mainSprite!!.addScript(script)
        mainScene!!.addSprite(mainSprite)
        project!!.addScene(mainScene)
    }

    private fun setUpHelperScene() {
        val script: Script = StartScript()
        helperScene = Scene()
        helperSprite = Sprite()

        val formulaElementSetTransparency = FormulaElement(
            FormulaElement.ElementType.USER_VARIABLE,
            GLOBAL_VARIABLE_IN_USE,
            null
        )
        val formulaSetTransparency = Formula(formulaElementSetTransparency)
        val setTransparency: FormulaBrick = ChangeXByNBrick(formulaSetTransparency)

        script.addBrick(setTransparency)
        helperSprite!!.addScript(script)
        helperScene!!.addSprite(helperSprite)
        project!!.addScene(helperScene)
    }

    private fun setUpProjectManager(scene: Scene?, sprite: Sprite?) {
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = scene
        ProjectManager.getInstance().currentSprite = sprite
        spyDataListFragment!!.initializeAdapter()
    }

    private fun getBrickList(sceneList: MutableList<Scene?>): List<Brick> {
        val brickFlatList: List<Brick> = ArrayList()

        for (scene in sceneList) {
            for (sprite in scene!!.spriteList) {
                for (script in sprite.scriptList) {
                    script.addToFlatList(brickFlatList)
                }
            }
        }
        return brickFlatList
    }

    @Test
    fun testDeleteGlobalVariable() {
        setUpProjectManager(mainScene, mainSprite)

        val selectedItems: MutableList<UserVariable> = ArrayList()
        selectedItems.add(globalVariableInUse)
        selectedItems.add(globalVariableNotInUse)

        spyDataListFragment!!.deleteItemsNotInUse(selectedItems)

        val userVariables = project!!.userVariables

        Assert.assertTrue(userVariables.contains(globalVariableInUse))
        Assert.assertFalse(userVariables.contains(globalVariableNotInUse))
    }

    @Test
    fun testDeleteLocalVariable() {
        setUpProjectManager(mainScene, mainSprite)

        val selectedItems: MutableList<UserVariable> = ArrayList()
        selectedItems.add(localVariableInUse)
        selectedItems.add(localVariableNotInUse)

        spyDataListFragment!!.deleteItemsNotInUse(selectedItems)

        val userVariables = mainSprite!!.userVariables

        Assert.assertTrue(userVariables.contains(localVariableInUse))
        Assert.assertFalse(userVariables.contains(localVariableNotInUse))
    }

    @Test
    fun testDeleteLocalVariableInDropDown() {
        setUpProjectManager(mainScene, mainSprite)

        Assert.assertSame(changeVariableBrick!!.userVariable, localVariableInUseDropDown)

        val selectedItems: MutableList<UserVariable> = ArrayList()
        selectedItems.add(localVariableInUseDropDown)

        spyDataListFragment!!.deleteItemsNotInUse(selectedItems)

        val sceneList: MutableList<Scene?> = ArrayList()
        sceneList.add(mainScene)

        val brickFlatList = getBrickList(sceneList)

        // User input form dialog: Yes, delete anyway!
        spyDataListFragment!!.deleteUserDataItemFromDropDown(
            localVariableInUseDropDown,
            brickFlatList
        )

        val userVariables = mainSprite!!.userVariables

        Assert.assertFalse(userVariables.contains(localVariableInUseDropDown))
        Assert.assertNull(changeVariableBrick!!.userVariable)
    }

    companion object {
        private const val STRING_ONE = "1"
        private const val PROJECT_NAME = "TestProject"
        private const val LOCAL_VARIABLE_IN_USE = "LocalVariableInUse"
        private const val LOCAL_VARIABLE_IN_USE_NOT = "LocalVariableNotInUse"
        private const val LOCAL_VARIABLE_IN_USE_DROP_DOWN = "LocalVariableInUseDropDown"
        private const val GLOBAL_VARIABLE_IN_USE = "GlobalVariableInUse"
        private const val GLOBAL_VARIABLE_IN_USE_NOT = "GlobalVariableNotInUse"
    }
}
