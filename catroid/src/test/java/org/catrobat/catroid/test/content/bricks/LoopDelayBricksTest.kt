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

import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.utils.LoopUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class, ParameterizedBrick::class)
@PowerMockRunnerDelegate(Parameterized::class)
internal class LoopDelayBricksTest(private val brick: Brick?) {

    private val REPEAT_TIMES = 3
    private lateinit var script: Script
    private lateinit var repeatBrickInner: RepeatBrick
    private lateinit var repeatBrickOuter: RepeatBrick
    private lateinit var conditionBrick: IfThenLogicBeginBrick

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<out Any?>> {
            return listOf(
                arrayOf(PlaceAtBrick(0, 0)),
                arrayOf(SetXBrick(0)),
                arrayOf(SetYBrick(0)),
                arrayOf(ChangeXByNBrick(0)),
                arrayOf(ChangeYByNBrick(0)),
                arrayOf(GoToBrick()),
                arrayOf(IfOnEdgeBounceBrick()),
                arrayOf(MoveNStepsBrick(0.0)),
                arrayOf(TurnLeftBrick(0.0)),
                arrayOf(TurnRightBrick(0.0)),
                arrayOf(PointInDirectionBrick(0.0)),
                arrayOf(PointToBrick(Sprite())),
                arrayOf(SetLookBrick()),
                arrayOf(SetLookByIndexBrick(0)),
                arrayOf(NextLookBrick()),
                arrayOf(PreviousLookBrick()),
                arrayOf(SetSizeToBrick(0.0)),
                arrayOf(ChangeSizeByNBrick(0.0)),
                arrayOf(SetTransparencyBrick(0.0)),
                arrayOf(ChangeTransparencyByNBrick(0.0)),
                arrayOf(SetBrightnessBrick(0.0)),
                arrayOf(ChangeBrightnessByNBrick(0.0)),
                arrayOf(SetColorBrick(0.0)),
                arrayOf(ChangeColorByNBrick(0.0)),
                arrayOf(SetBackgroundBrick()),
                arrayOf(SetBackgroundByIndexBrick(0)),
                arrayOf(SetVolumeToBrick(0.0)),
                arrayOf(ChangeVolumeByNBrick(0.0)),
                arrayOf(SetTempoBrick(0))
            )
        }
    }

    @Before
    fun setUp() {
        repeatBrickInner = RepeatBrick(Formula(REPEAT_TIMES))
        repeatBrickOuter = RepeatBrick(Formula(REPEAT_TIMES))
        conditionBrick = IfThenLogicBeginBrick()
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }
    }

    @Test
    fun testLoopDelayOnly() {
        script = StartScript()
        repeatBrickInner.addBrick(brick)
        assert(LoopUtil.checkLoopBrickForLoopDelay(repeatBrickInner, script))
    }

    @Test
    fun testLoopDelayWithInnerLoop() {
        script = StartScript()
        repeatBrickOuter.addBrick(repeatBrickInner)
        repeatBrickInner.addBrick(brick)
        assert(LoopUtil.checkLoopBrickForLoopDelay(repeatBrickOuter, script))
    }

    @Test
    fun testLoopDelayWithInnerCondition() {
        script = StartScript()
        repeatBrickOuter.addBrick(conditionBrick)
        conditionBrick.addBrick(brick)
        assert(LoopUtil.checkLoopBrickForLoopDelay(repeatBrickOuter, script))
    }

    @Test
    fun testLoopDelayInUserDefinedBrickWithoutScreenRefreshingOnly() {
        script = UserDefinedScript()
        (script as UserDefinedScript).screenRefresh = false
        repeatBrickInner.addBrick(brick)
        assert(!LoopUtil.checkLoopBrickForLoopDelay(repeatBrickInner, script))
    }

    @Test
    fun testLoopDelayInUserDefinedBrickWithScreenRefreshingOnly() {
        script = UserDefinedScript()
        (script as UserDefinedScript).screenRefresh = true
        repeatBrickInner.addBrick(brick)
        assert(LoopUtil.checkLoopBrickForLoopDelay(repeatBrickInner, script))
    }
}
