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

package org.catrobat.catroid.utils

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
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
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

object LoopUtil {
    private val loopDelayBricks: List<KClass<out BrickBaseType>> = listOf(
        PlaceAtBrick::class, SetXBrick::class, SetYBrick::class, ChangeXByNBrick::class,
        ChangeYByNBrick::class, GoToBrick::class, IfOnEdgeBounceBrick::class,
        MoveNStepsBrick::class, TurnLeftBrick::class, TurnRightBrick::class,
        PointInDirectionBrick::class, PointToBrick::class, SetLookBrick::class,
        SetLookByIndexBrick::class, NextLookBrick::class, PreviousLookBrick::class,
        SetSizeToBrick::class, ChangeSizeByNBrick::class, SetTransparencyBrick::class,
        ChangeTransparencyByNBrick::class, SetBrightnessBrick::class,
        ChangeBrightnessByNBrick::class, SetColorBrick::class, ChangeColorByNBrick::class,
        SetBackgroundBrick::class, SetBackgroundByIndexBrick::class,
        SetVolumeToBrick::class, ChangeVolumeByNBrick::class, SetTempoBrick::class)

    @JvmStatic
    fun checkLoopBrickForLoopDelay(loopBrick: CompositeBrick, script: Script): Boolean {
        val allNestedBricks: List<Brick> = ArrayList()
        loopBrick.addToFlatList(allNestedBricks)

        if (script is UserDefinedScript && !script.screenRefresh) {
            return false
        }
        for (brick in allNestedBricks.filter { b -> !b.isCommentedOut }) {
            if (loopDelayBricks.contains(brick::class)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isAnyStitchRunning(): Boolean {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.currentProject ?: return false
        projectManager.currentProject.spriteListWithClones?.forEach {
            if (it.runningStitch.isRunning) {
                return true
            }
        }
        return false
    }
}
