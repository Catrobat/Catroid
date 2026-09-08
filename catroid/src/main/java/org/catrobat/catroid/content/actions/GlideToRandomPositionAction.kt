/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Scope


class GlideToRandomPositionAction : TemporalAction() {
    lateinit var scope: Scope
    private var startXPosition: Float = 0f
    private var startYPosition: Float = 0f
    var randomXPosition: Float = 0f
    var randomYPosition: Float = 0f
    var currentXPosition: Float = 0f
    var currentYPosition: Float = 0f


    override fun begin() {
        super.begin()
        scope.sprite.isGliding = true
        startXPosition = scope.sprite.look.xInUserInterfaceDimensionUnit
        startYPosition = scope.sprite.look.yInUserInterfaceDimensionUnit
        randomXPosition = Math.random().toFloat() * (ScreenValues.currentScreenResolution.width +
            1) - (ScreenValues.currentScreenResolution.width / 2)
        randomYPosition = Math.random().toFloat() * (ScreenValues.currentScreenResolution.height +
            1) - (ScreenValues.currentScreenResolution.height / 2)
    }

    override fun update(percent: Float) {

        currentXPosition = startXPosition + (randomXPosition-startXPosition)*percent
        currentYPosition = startYPosition + (randomYPosition-startYPosition)*percent
       scope.sprite.look?.setPositionInUserInterfaceDimensionUnit(currentXPosition,currentYPosition)
    }
    override fun end(){
        super.end()
        scope.sprite.isGliding = false
    }

}
