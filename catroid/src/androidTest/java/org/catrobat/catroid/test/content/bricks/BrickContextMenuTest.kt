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

import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick
import org.catrobat.catroid.content.bricks.PenDownBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment.getContextMenuItems
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class BrickContextMenuTest(
    private val brick: Brick,
    private val expectedEditFormula: Boolean,
    private val expectedHighlight: Boolean
) {

    private val contextMenuItems: MutableList<Int> = ArrayList()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(AssertUserListsBrick(), false, false),
            arrayOf(AssertEqualsBrick(), true, false),
            arrayOf(UserDefinedReceiverBrick(), false, false),
            arrayOf(UserDefinedBrick(), false, false),
            arrayOf(UserDefinedBrick(listOf(UserDefinedBrickInput(""))), true, false),
            arrayOf(UserDefinedBrick(listOf(UserDefinedBrickLabel(""))), false, false),
            arrayOf(ForItemInUserListBrick(), false, true),
            arrayOf(RepeatUntilBrick(), true, true),
            arrayOf(WhenStartedBrick(), false, false),
            arrayOf(SetXBrick(), true, false),
            arrayOf(SetVariableBrick(), true, false),
            arrayOf(PenDownBrick(), false, false),
            arrayOf(SetVolumeToBrick(), true, false),
            arrayOf(SetInstrumentBrick(), false, false),
            arrayOf(FadeParticleEffectBrick(), false, false),
            arrayOf(ParticleEffectAdditivityBrick(), false, false),
            arrayOf(FlashBrick(), false, false),
            arrayOf(ReadVariableFromDeviceBrick(), false, false),
            arrayOf(EmptyEventBrick(), false, false),
            arrayOf(WhenBounceOffBrick(), false, false),
            arrayOf(WhenBrick(), false, false),
            arrayOf(WhenClonedBrick(), false, false),
            arrayOf(WhenGamepadButtonBrick(), false, false),
            arrayOf(WhenNfcBrick(), false, false),
            arrayOf(WhenRaspiPinChangedBrick(), false, false),
            arrayOf(WhenTouchDownBrick(), false, false)
            )
    }

    @Before
    fun setUp() {
        contextMenuItems.addAll(getContextMenuItems(brick))
    }

    @After
    fun tearDown() {
        contextMenuItems.clear()
    }

    @Test
    fun testEditFormula() {
        assertEquals(contextMenuItems.contains(R.string.brick_context_dialog_formula_edit_brick), expectedEditFormula)
    }

    @Test
    fun testHighlightBrickParts() {
        assertEquals(contextMenuItems.contains(R.string.brick_context_dialog_highlight_brick_parts), expectedHighlight)
    }

    @Test
    fun testExpectedDelete() {
        val showsDelete: Boolean = when (brick) {
            is UserDefinedReceiverBrick -> contextMenuItems.contains(R.string.brick_context_dialog_delete_definition)
            is ScriptBrick -> contextMenuItems.contains(R.string.brick_context_dialog_delete_script)
            else -> contextMenuItems.contains(R.string.brick_context_dialog_delete_brick)
        }
        assertTrue(showsDelete)
    }

    @Test
    fun testExpectedCommentOut() {
        val showsCommentOut: Boolean = when (brick) {
            is UserDefinedReceiverBrick, is EmptyEventBrick -> !contextMenuItems.contains(R.string.brick_context_dialog_comment_out_script)
            is ScriptBrick -> contextMenuItems.contains(R.string.brick_context_dialog_comment_out_script)
            else -> contextMenuItems.contains(R.string.brick_context_dialog_comment_out)
        }
        assertTrue(showsCommentOut)
    }

    @Test
    fun testExpectedCommentIn() {
        brick.isCommentedOut = true
        contextMenuItems.addAll(getContextMenuItems(brick))
        val showsCommentIn: Boolean = when (brick) {
            is UserDefinedReceiverBrick, is EmptyEventBrick -> !contextMenuItems.contains(R.string.brick_context_dialog_comment_in)
            is ScriptBrick -> contextMenuItems.contains(R.string.brick_context_dialog_comment_in_script)
            else -> contextMenuItems.contains(R.string.brick_context_dialog_comment_in)
        }
        assertTrue(showsCommentIn)
    }

    @Test
    fun testExpectedCopy() {
        val showsCopy: Boolean = when (brick) {
            is UserDefinedReceiverBrick -> !contextMenuItems.contains(R.string.brick_context_dialog_copy_script)
            is ScriptBrick -> contextMenuItems.contains(R.string.brick_context_dialog_copy_script)
            else -> contextMenuItems.contains(R.string.brick_context_dialog_copy_brick)
        }
        assertTrue(showsCopy)
    }

    @Test
    fun testExpectedHelp() {
        assertTrue(contextMenuItems.contains(R.string.brick_context_dialog_help))
    }

    @Test
    fun testExpectedAddToBackpack() {
        val showsAddToBackpack: Boolean = when (brick) {
            is UserDefinedReceiverBrick, is ScriptBrick -> contextMenuItems.contains(R.string.backpack_add)
            else -> !contextMenuItems.contains(R.string.backpack_add)
        }
        assertTrue(showsAddToBackpack)
    }
}
