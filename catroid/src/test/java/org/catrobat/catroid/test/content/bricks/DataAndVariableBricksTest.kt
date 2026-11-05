/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ClearUserListBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

class DataAndVariableBricksTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var actionFactory: ActionFactory
    private lateinit var sequence: ScriptSequenceAction
    private lateinit var scope: Scope

    @Before
    fun setUp() {
        project = Project(MockUtil.mockContextForProject(), "Project")
        val scene = Scene("Currently playing scene", project)
        sprite = Sprite("Sprite")

        scene.addSprite(sprite)
        project.addScene(scene)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = Scene()
        ProjectManager.getInstance().currentlyPlayingScene = scene

        actionFactory = Mockito.mock(ActionFactory::class.java)
        sprite.actionFactory = actionFactory
        sequence = Mockito.mock(ScriptSequenceAction::class.java)
        scope = Scope(project, sprite, sequence)
    }

    // --- SetVariableBrick ---

    @Test
    fun testSetVariableBrickDefaultConstructorAndGetViewResource() {
        val brick = SetVariableBrick()
        assertEquals(R.layout.brick_set_variable, brick.viewResource)
    }

    @Test
    fun testSetVariableBrickConstructorsAndAction() {
        val userVariable = UserVariable("score")
        val customFormula = Formula(42.0)
        val brick = SetVariableBrick(customFormula, userVariable)

        assertEquals(userVariable, brick.userVariable)
        assertEquals(customFormula, brick.getFormulaWithBrickField(Brick.BrickField.VARIABLE))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetVariableAction(
            eq(sprite),
            eq(sequence),
            eq(customFormula),
            eq(userVariable)
        )

        val brickDouble = SetVariableBrick(99.5)
        assertEquals(99.5, brickDouble.getFormulaWithBrickField(Brick.BrickField.VARIABLE).interpretDouble(scope), 0.001)

        val brickSensor = SetVariableBrick(Sensors.X_ACCELERATION)
        assertNotNull(brickSensor.getFormulaWithBrickField(Brick.BrickField.VARIABLE))
    }

    // --- ChangeVariableBrick ---

    @Test
    fun testChangeVariableBrickDefaultConstructorAndGetViewResource() {
        val brick = ChangeVariableBrick()
        assertEquals(R.layout.brick_change_variable_by, brick.viewResource)
    }

    @Test
    fun testChangeVariableBrickConstructorsAndAction() {
        val userVariable = UserVariable("counter")
        val customFormula = Formula(1.0)
        val brick = ChangeVariableBrick(customFormula, userVariable)

        assertEquals(userVariable, brick.userVariable)
        assertEquals(customFormula, brick.getFormulaWithBrickField(Brick.BrickField.VARIABLE_CHANGE))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createChangeVariableAction(
            eq(sprite),
            eq(sequence),
            eq(customFormula),
            eq(userVariable)
        )

        val brickDouble = ChangeVariableBrick(5.0)
        assertEquals(5.0, brickDouble.getFormulaWithBrickField(Brick.BrickField.VARIABLE_CHANGE).interpretDouble(scope), 0.001)

        val brickSingleFormula = ChangeVariableBrick(customFormula)
        assertEquals(customFormula, brickSingleFormula.getFormulaWithBrickField(Brick.BrickField.VARIABLE_CHANGE))
    }

    // --- AddItemToUserListBrick ---

    @Test
    fun testAddItemToUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = AddItemToUserListBrick()
        assertEquals(R.layout.brick_add_item_to_userlist, brick.viewResource)
    }

    @Test
    fun testAddItemToUserListBrickConstructorsAndAction() {
        val userList = UserList("myList")
        val customFormula = Formula("Apple")
        val brick = AddItemToUserListBrick(customFormula)
        brick.userList = userList

        assertEquals(customFormula, brick.getFormulaWithBrickField(Brick.BrickField.LIST_ADD_ITEM))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createAddItemToUserListAction(
            eq(sprite),
            eq(sequence),
            eq(customFormula),
            eq(userList)
        )

        val brickDouble = AddItemToUserListBrick(10.0)
        assertEquals(10.0, brickDouble.getFormulaWithBrickField(Brick.BrickField.LIST_ADD_ITEM).interpretDouble(scope), 0.001)
    }

    // --- DeleteItemOfUserListBrick ---

    @Test
    fun testDeleteItemOfUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = DeleteItemOfUserListBrick()
        assertEquals(R.layout.brick_delete_item_of_userlist, brick.viewResource)
    }

    @Test
    fun testDeleteItemOfUserListBrickConstructorsAndAction() {
        val userList = UserList("myList")
        val customFormula = Formula(2)
        val brick = DeleteItemOfUserListBrick(customFormula, userList)

        assertEquals(userList, brick.userList)
        assertEquals(customFormula, brick.getFormulaWithBrickField(Brick.BrickField.LIST_DELETE_ITEM))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createDeleteItemOfUserListAction(
            eq(sprite),
            eq(sequence),
            eq(customFormula),
            eq(userList)
        )

        val brickInt = DeleteItemOfUserListBrick(1)
        assertEquals(1, brickInt.getFormulaWithBrickField(Brick.BrickField.LIST_DELETE_ITEM).interpretInteger(scope))

        val brickSingleFormula = DeleteItemOfUserListBrick(customFormula)
        assertEquals(customFormula, brickSingleFormula.getFormulaWithBrickField(Brick.BrickField.LIST_DELETE_ITEM))
    }

    // --- InsertItemIntoUserListBrick ---

    @Test
    fun testInsertItemIntoUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = InsertItemIntoUserListBrick()
        assertEquals(R.layout.brick_insert_item_into_userlist, brick.viewResource)
        assertEquals(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_VALUE, brick.defaultBrickField)
    }

    @Test
    fun testInsertItemIntoUserListBrickConstructorsAndAction() {
        val userList = UserList("myList")
        val valueFormula = Formula("Banana")
        val indexFormula = Formula(1)
        val brick = InsertItemIntoUserListBrick(valueFormula, indexFormula, userList)

        assertEquals(userList, brick.userList)
        assertEquals(valueFormula, brick.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_VALUE))
        assertEquals(indexFormula, brick.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_INDEX))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createInsertItemIntoUserListAction(
            eq(sprite),
            eq(sequence),
            eq(indexFormula),
            eq(valueFormula),
            eq(userList)
        )

        val brickValues = InsertItemIntoUserListBrick(25.0, 3)
        assertEquals(25.0, brickValues.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_VALUE).interpretDouble(scope), 0.001)
        assertEquals(3, brickValues.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_INDEX).interpretInteger(scope))

        val brickTwoFormulas = InsertItemIntoUserListBrick(valueFormula, indexFormula)
        assertEquals(valueFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_VALUE))
        assertEquals(indexFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.INSERT_ITEM_INTO_USERLIST_INDEX))
    }

    // --- ReplaceItemInUserListBrick ---

    @Test
    fun testReplaceItemInUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = ReplaceItemInUserListBrick()
        assertEquals(R.layout.brick_replace_item_in_userlist, brick.viewResource)
        assertEquals(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_INDEX, brick.defaultBrickField)
    }

    @Test
    fun testReplaceItemInUserListBrickConstructorsAndAction() {
        val userList = UserList("myList")
        val valueFormula = Formula("Orange")
        val indexFormula = Formula(2)
        val brick = ReplaceItemInUserListBrick(valueFormula, indexFormula, userList)

        assertEquals(userList, brick.userList)
        assertEquals(valueFormula, brick.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_VALUE))
        assertEquals(indexFormula, brick.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_INDEX))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createReplaceItemInUserListAction(
            eq(sprite),
            eq(sequence),
            eq(indexFormula),
            eq(valueFormula),
            eq(userList)
        )

        val brickValues = ReplaceItemInUserListBrick(50.0, 4)
        assertEquals(50.0, brickValues.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_VALUE).interpretDouble(scope), 0.001)
        assertEquals(4, brickValues.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_INDEX).interpretInteger(scope))

        val brickTwoFormulas = ReplaceItemInUserListBrick(valueFormula, indexFormula)
        assertEquals(valueFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_VALUE))
        assertEquals(indexFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.REPLACE_ITEM_IN_USERLIST_INDEX))
    }

    // --- ClearUserListBrick ---

    @Test
    fun testClearUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = ClearUserListBrick()
        assertEquals(R.layout.brick_clear_userlist, brick.viewResource)
    }

    @Test
    fun testClearUserListBrickCreatesAction() {
        val userList = UserList("myList")
        val brick = ClearUserListBrick()
        brick.userList = userList

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createClearUserListAction(eq(userList))
    }

    // --- StoreCSVIntoUserListBrick ---

    @Test
    fun testStoreCSVIntoUserListBrickDefaultConstructorAndGetViewResource() {
        val brick = StoreCSVIntoUserListBrick()
        assertEquals(R.layout.brick_store_csv_into_userlist, brick.viewResource)
        assertEquals(Brick.BrickField.STORE_CSV_INTO_USERLIST_COLUMN, brick.defaultBrickField)
    }

    @Test
    fun testStoreCSVIntoUserListBrickConstructorsAndAction() {
        val userList = UserList("csvList")
        val columnFormula = Formula(1)
        val csvFormula = Formula("a,b,c")
        val brick = StoreCSVIntoUserListBrick(columnFormula, csvFormula, userList)

        assertEquals(userList, brick.userList)
        assertEquals(columnFormula, brick.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_COLUMN))
        assertEquals(csvFormula, brick.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_CSV))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStoreCSVIntoUserListAction(
            eq(sprite),
            eq(sequence),
            eq(columnFormula),
            eq(csvFormula),
            eq(userList)
        )

        val brickValues = StoreCSVIntoUserListBrick(2, "x,y,z")
        assertEquals(2, brickValues.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_COLUMN).interpretInteger(scope))
        assertEquals("x,y,z", brickValues.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_CSV).interpretString(scope))

        val brickTwoFormulas = StoreCSVIntoUserListBrick(columnFormula, csvFormula)
        assertEquals(columnFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_COLUMN))
        assertEquals(csvFormula, brickTwoFormulas.getFormulaWithBrickField(Brick.BrickField.STORE_CSV_INTO_USERLIST_CSV))
    }

    // --- AskBrick ---

    @Test
    fun testAskBrickDefaultConstructorAndGetViewResource() {
        val brick = AskBrick()
        assertEquals(R.layout.brick_ask, brick.viewResource)
    }

    @Test
    fun testAskBrickConstructorsAndAction() {
        val answerVariable = UserVariable("answer")
        val questionFormula = Formula("What is your name?")
        val brick = AskBrick(questionFormula, answerVariable)

        assertEquals(answerVariable, brick.userVariable)
        assertEquals(questionFormula, brick.getFormulaWithBrickField(Brick.BrickField.ASK_QUESTION))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createAskAction(
            eq(sprite),
            eq(sequence),
            eq(questionFormula),
            eq(answerVariable)
        )

        val brickString = AskBrick("How old are you?")
        assertEquals("How old are you?", brickString.getFormulaWithBrickField(Brick.BrickField.ASK_QUESTION).interpretString(scope))

        val brickSingleFormula = AskBrick(questionFormula)
        assertEquals(questionFormula, brickSingleFormula.getFormulaWithBrickField(Brick.BrickField.ASK_QUESTION))
    }

    // --- AskSpeechBrick ---

    @Test
    fun testAskSpeechBrickDefaultConstructorAndGetViewResource() {
        val brick = AskSpeechBrick()
        assertEquals(R.layout.brick_ask_speech, brick.viewResource)

        val requiredResources = Brick.ResourcesSet()
        brick.addRequiredResources(requiredResources)
        assertTrue(requiredResources.contains(Brick.MICROPHONE))
        assertTrue(requiredResources.contains(Brick.SPEECH_RECOGNITION))
    }

    @Test
    fun testAskSpeechBrickConstructorsAndAction() {
        val answerVariable = UserVariable("speechAnswer")
        val questionFormula = Formula("Say something")
        val brick = AskSpeechBrick(questionFormula)
        brick.userVariable = answerVariable

        assertEquals(answerVariable, brick.userVariable)
        assertEquals(questionFormula, brick.getFormulaWithBrickField(Brick.BrickField.ASK_SPEECH_QUESTION))

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createAskSpeechAction(
            eq(sprite),
            eq(sequence),
            eq(questionFormula),
            eq(answerVariable)
        )

        val brickString = AskSpeechBrick("Speak now")
        assertEquals("Speak now", brickString.getFormulaWithBrickField(Brick.BrickField.ASK_SPEECH_QUESTION).interpretString(scope))
    }
}
