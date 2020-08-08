/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.physics.PhysicsBoundaryBox.BoundaryBoxIdentifier
import org.catrobat.catroid.physics.PhysicsWorld

class IfOnEdgeBouncePhysicsAction : TemporalAction() {
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    private fun performVerticalRepositioning(
        bbLookOffsetX: Float,
        velocityHighEnoughToCollideAfterRepositioning: Boolean,
        correctGravityPresent: Boolean
    ) {
        sprite!!.look.xInUserInterfaceDimensionUnit =
            sprite!!.look.xInUserInterfaceDimensionUnit + bbLookOffsetX
        checkBounceActivation(
            correctGravityPresent,
            velocityHighEnoughToCollideAfterRepositioning,
            sprite,
            BoundaryBoxIdentifier.BBI_VERTICAL
        )
    }

    private fun performHorizontalRepositioning(
        bbLookOffsetY: Float,
        velocityHighEnoughToCollideAfterRepositioning: Boolean,
        correctGravityPresent: Boolean
    ) {
        sprite!!.look.yInUserInterfaceDimensionUnit =
            sprite!!.look.yInUserInterfaceDimensionUnit + bbLookOffsetY
        checkBounceActivation(
            correctGravityPresent,
            velocityHighEnoughToCollideAfterRepositioning,
            sprite,
            BoundaryBoxIdentifier.BBI_HORIZONTAL
        )
    }

    private fun checkBounceActivation(
        correctGravityPresent: Boolean,
        velocityHighEnoughToCollideAfterRepositioning: Boolean,
        sprite: Sprite?,
        boundaryBoxIdentifier: BoundaryBoxIdentifier
    ) {
        if (velocityHighEnoughToCollideAfterRepositioning || correctGravityPresent) {
            physicsWorld!!.setBounceOnce(sprite, boundaryBoxIdentifier)
        }
    }

    override fun update(percent: Float) {
        // AABB ... AXIS-ALIGNED-BOUNDING-BOX
        val bbLowerEdge = Vector2()
        val bbUpperEdge = Vector2()
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.getBoundaryBox(bbLowerEdge, bbUpperEdge)
        val bbWidth = bbUpperEdge.x - bbLowerEdge.x
        val bbHeight = bbUpperEdge.y - bbLowerEdge.y
        val bbCenterX = bbLowerEdge.x + bbWidth / 2
        val bbCenterY = bbLowerEdge.y + bbHeight / 2
        val vsWidth =
            ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenWidth
        val vsHeight =
            ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenHeight
        val leftCollisionAreaInnerBorder = -vsWidth / 2.0f + bbWidth / 2.0f
        val leftCollisionAreaOuterBorder =
            leftCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * -bbWidth
        val leftVelocityHighEnoughToCollideAfterRepositioning =
            physicsObject.velocity.x <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE
        val leftGravityPresent = physicsWorld!!.gravity.x < 0
        val rightCollisionAreaInnerBorder = vsWidth / 2.0f - bbWidth / 2.0f
        val rightCollisionAreaOuterBorder =
            rightCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbWidth
        val rightVelocityHighEnoughToCollideAfterRepositioning =
            physicsObject.velocity.x >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE
        val rightGravityPresent = physicsWorld!!.gravity.x > 0
        if (leftCollisionAreaOuterBorder < bbCenterX && bbCenterX < leftCollisionAreaInnerBorder) {
            val bbLookOffsetX =
                Math.abs(bbCenterX - bbWidth / 2.0f + vsWidth / 2.0f)
            performVerticalRepositioning(
                bbLookOffsetX,
                leftVelocityHighEnoughToCollideAfterRepositioning,
                leftGravityPresent
            )
        } else if (rightCollisionAreaOuterBorder > bbCenterX && bbCenterX > rightCollisionAreaInnerBorder) {
            val bbLookOffsetX =
                Math.abs(bbCenterX + bbWidth / 2.0f - vsWidth / 2.0f)
            performVerticalRepositioning(
                -bbLookOffsetX,
                rightVelocityHighEnoughToCollideAfterRepositioning,
                rightGravityPresent
            )
        }
        val bottomCollisionAreaInnerBorder = -vsHeight / 2.0f + bbHeight / 2.0f
        val bottomCollisionAreaOuterBorder =
            bottomCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * -bbHeight
        val bottomVelocityHighEnoughToCollideAfterRepositioning =
            physicsObject.velocity.y <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE
        val bottomGravityPresent = physicsWorld!!.gravity.y < 0
        val topCollisionAreaInnerBorder = vsHeight / 2.0f - bbHeight / 2.0f
        val topCollisionAreaOuterBorder =
            topCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbHeight
        val topVelocityHighEnoughToCollideAfterRepositioning =
            physicsObject.velocity.y >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE
        val topGravityPresent = physicsWorld!!.gravity.y > 0
        if (bottomCollisionAreaOuterBorder < bbCenterY && bbCenterY < bottomCollisionAreaInnerBorder) {
            val bbLookOffsetY =
                Math.abs(bbCenterY - bbHeight / 2.0f + vsHeight / 2.0f)
            performHorizontalRepositioning(
                bbLookOffsetY,
                bottomVelocityHighEnoughToCollideAfterRepositioning,
                bottomGravityPresent
            )
        } else if (topCollisionAreaOuterBorder > bbCenterY && bbCenterY > topCollisionAreaInnerBorder) {
            val bbLookOffsetY =
                Math.abs(bbCenterY + bbHeight / 2.0f - vsHeight / 2.0f)
            performHorizontalRepositioning(
                -bbLookOffsetY,
                topVelocityHighEnoughToCollideAfterRepositioning,
                topGravityPresent
            )
        }
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }

    fun setPhysicsWorld(physicsWorld: PhysicsWorld?) {
        this.physicsWorld = physicsWorld
    }

    companion object {
        const val THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE = 10.0f
        private const val COLLISION_OVERLAP_RANGE_FACTOR = 0.9f
    }
}