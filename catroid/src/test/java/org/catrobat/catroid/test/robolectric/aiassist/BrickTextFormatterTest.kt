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

package org.catrobat.catroid.test.robolectric.aiassist

import android.os.Build
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.aiassist.diff.brickEditorLabel
import org.catrobat.catroid.ui.aiassist.diff.brickPhraseTokens
import org.catrobat.catroid.ui.aiassist.diff.dataNames
import org.catrobat.catroid.ui.aiassist.diff.scriptHeaderTitle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BrickTextFormatterTest {

    private lateinit var activity: SpriteActivity

    @Before
    fun setUp() {
        val controller = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = controller.get()
        val project = Project(activity, javaClass.simpleName)
        val hostSprite = Sprite("hostSprite")
        project.defaultScene.addSprite(hostSprite)
        ProjectManager.getInstance().apply {
            currentProject = project
            currentSprite = hostSprite
            currentlyEditedScene = project.defaultScene
        }
        controller.create().resume()
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    // ── dataNames ──

    @Test
    fun dataNamesReturnsSelectedVariableName() {
        val brick = SetVariableBrick(Formula(5.0), UserVariable("score"))
        assertEquals(listOf("score"), dataNames(brick))
    }

    @Test
    fun dataNamesIsEmptyForBrickWithoutData() {
        assertTrue(dataNames(SetXBrick(5)).isEmpty())
    }

    @Test
    fun dataNamesIncludesSelectedSpinnerName() {
        val brick = SetLookBrick().apply { look = LookData().apply { name = "look1" } }
        assertEquals(listOf("look1"), dataNames(brick))
    }

    // ── brickEditorLabel ──

    @Test
    fun editorLabelRendersMultiPartBrickAsEditorPhrase() {
        assertEquals("If 0 is true then", brickEditorLabel(IfLogicBeginBrick(), activity))
    }

    @Test
    fun editorLabelShowsFieldValueInline() {
        assertEquals("Set x to 0", brickEditorLabel(SetXBrick(0), activity))
    }

    @Test
    fun editorLabelLabelsEachFieldByItsLayoutPosition() {
        assertEquals("Place at x: 0 y: 500", brickEditorLabel(PlaceAtBrick(0, 500), activity))
    }

    @Test
    fun editorLabelIncludesRuntimeUnitLabel() {
        assertEquals("Wait 5 seconds", brickEditorLabel(WaitBrick(Formula(5.0)), activity))
    }

    @Test
    fun editorLabelInlinesSpinnerSelectionAndValue() {
        val brick = SetVariableBrick(Formula(0.0), UserVariable("playerY"))
        assertEquals("Set variable playerY to 0", brickEditorLabel(brick, activity))
    }

    @Test
    fun editorLabelInlinesSpinnerOnlySelection() {
        val brick = SetLookBrick().apply { look = LookData().apply { name = "look1" } }
        assertEquals("Switch to look look1", brickEditorLabel(brick, activity))
    }

    // ── brickPhraseTokens ──

    @Test
    fun phraseTokensFlagTheChangedValueAsDynamic() {
        val tokens = brickPhraseTokens(SetXBrick(100), activity, SetXBrick(0))

        val value = tokens.single { it.dynamic }
        assertEquals("100", value.text)
        assertTrue(value.changed)
        assertTrue(tokens.any { !it.dynamic && it.text == "Set x to" && !it.changed })
    }

    @Test
    fun phraseTokensDoNotFlagAnUnchangedValue() {
        val tokens = brickPhraseTokens(SetXBrick(100), activity, SetXBrick(100))
        assertTrue(tokens.none { it.changed })
    }

    // ── scriptHeaderTitle ──

    @Test
    fun scriptHeaderTitleUsesTheRealEditorLabel() {
        assertEquals("When scene starts", scriptHeaderTitle(WhenStartedBrick(), activity))
    }
}
