/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.physics.PhysicalCollision
import org.catrobat.catroid.physics.PhysicsBoundaryBox.BoundaryBoxIdentifier
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsWorld

class IfOnEdgeBouncePhysicsAction : TemporalAction() {
    enum class Side {
        TOP, BOTTOM, LEFT, RIGHT
    }

    companion object {
        const val THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE = 10.0f
        private const val COLLISION_OVERLAP_RANGE_FACTOR = 0.9f
        private const val OPPOSITE_DIRECTION = 180.0f
    }

    private val virtualScreenWidth = ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenWidth
    private val vsHeight = ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenHeight

    lateinit var sprite: Sprite
    lateinit var physicsWorld: PhysicsWorld

    private var boundaryBoxWidth = 0.0f
    private var boundaryBoxHeight = 0.0f
    private var boundaryBoxCenterX = 0.0f
    private var boundaryBoxCenterY = 0.0f

    override fun update(percent: Float) {
        val physicsObject = physicsWorld.getPhysicsObject(sprite)
        calculateBoundaryBoxDimensions(physicsObject)

        if (isBoundaryBoxCenterInLeftCollisionArea()) {
            performRepositioning(
                calculateLeftCollisionOffset(),
                isLeftVelocityHighEnoughToCollideAfterRepositioning(physicsObject),
                isLeftGravityPresent(),
                Side.LEFT
            )
        } else if (isBoundaryBoxCenterInRightCollisionArea()) {
            performRepositioning(
                -calculateRightCollisionOffset(),
                isRightVelocityHighEnoughToCollideAfterRepositioning(physicsObject),
                isRightGravityPresent(),
                Side.RIGHT
            )
        }

        if (isBoundaryBoxCenterInBottomCollisionArea()) {
            performRepositioning(
                calculateBottomCollisionOffset(),
                isBottomVelocityHighEnoughToCollideAfterRepositioning(physicsObject),
                isBottomGravityPresent(),
                Side.BOTTOM
            )
        } else if (isBoundaryBoxCenterInTopCollisionArea()) {
            performRepositioning(
                -calculateTopCollisionOffset(),
                isTopVelocityHighEnoughToCollideAfterRepositioning(physicsObject),
                isTopGravityPresent(),
                Side.TOP
            )
        }
        sprite.movedByStepsBrick = false
    }

    private fun performRepositioning(
        boundaryBoxLookOffset: Float,
        isVelocityHighEnoughToCollideAfterRepositioning: Boolean,
        correctGravityPresent: Boolean,
        side: Side
    ) {
        if (side == Side.LEFT || side == Side.RIGHT) {
            sprite.look.changeXInUserInterfaceDimensionUnit(boundaryBoxLookOffset)

            if (sprite.movedByStepsBrick) changeDirectionOnStepsTaken(side) else changeDirectionOnVelocityOrGravity(side)

            checkBounceActivation(
                correctGravityPresent,
                isVelocityHighEnoughToCollideAfterRepositioning,
                sprite,
                BoundaryBoxIdentifier.BBI_VERTICAL
            )
        } else {
            sprite.look.changeYInUserInterfaceDimensionUnit(boundaryBoxLookOffset)

            if (sprite.movedByStepsBrick) changeDirectionOnStepsTaken(side) else changeDirectionOnVelocityOrGravity(side)

            checkBounceActivation(
                correctGravityPresent,
                isVelocityHighEnoughToCollideAfterRepositioning,
                sprite,
                BoundaryBoxIdentifier.BBI_HORIZONTAL
            )
        }
    }

    private fun changeDirectionOnStepsTaken(side: Side) {
        val physicsObject = physicsWorld.getPhysicsObject(sprite)
        val realRotation = sprite.look.motionDirectionInUserInterfaceDimensionUnit

        if (side == Side.LEFT || side == Side.RIGHT) {
            sprite.look.motionDirectionInUserInterfaceDimensionUnit = -realRotation
            calculateBoundaryBoxDimensions(physicsObject)
            (sprite.look as PhysicsLook).updateFlippedByAction()
        } else if (side == Side.TOP || side == Side.BOTTOM) {
            sprite.look.motionDirectionInUserInterfaceDimensionUnit = OPPOSITE_DIRECTION -
                realRotation
            calculateBoundaryBoxDimensions(physicsObject)
        }

        when (side) {
            Side.LEFT ->
                sprite.look.changeXInUserInterfaceDimensionUnit(calculateLeftCollisionOffset())
            Side.RIGHT ->
                sprite.look.changeXInUserInterfaceDimensionUnit(
                    -calculateRightCollisionOffset()
                )
            Side.TOP ->
                sprite.look.changeYInUserInterfaceDimensionUnit(
                    -calculateTopCollisionOffset()
                )
            Side.BOTTOM ->
                sprite.look.changeYInUserInterfaceDimensionUnit(
                    calculateBottomCollisionOffset()
                )
            else -> throw IllegalArgumentException("invalid side")
        }

        PhysicalCollision.fireBounceOffEvent(sprite, null)
    }

    private fun changeDirectionOnVelocityOrGravity(side: Side) {
        val physicsObject = physicsWorld.getPhysicsObject(sprite)
        val realRotation = sprite.look.motionDirectionInUserInterfaceDimensionUnit

        if ((side == Side.LEFT || side == Side.RIGHT) &&
            sprite.look.rotationMode != Look.ROTATION_STYLE_ALL_AROUND) {
            sprite.look.motionDirectionInUserInterfaceDimensionUnit = -realRotation
            calculateBoundaryBoxDimensions(physicsObject)
            (sprite.look as PhysicsLook).updateFlippedByAction()
        } else if ((side == Side.TOP || side == Side.BOTTOM) &&
            sprite.look.rotationMode != Look.ROTATION_STYLE_ALL_AROUND) {
            sprite.look.motionDirectionInUserInterfaceDimensionUnit = OPPOSITE_DIRECTION -
                realRotation
            calculateBoundaryBoxDimensions(physicsObject)
        }
    }

    private fun checkBounceActivation(
        correctGravityPresent: Boolean,
        isVelocityHighEnoughToCollideAfterRepositioning: Boolean,
        sprite: Sprite?,
        boundaryBoxIdentifier: BoundaryBoxIdentifier
    ) {
        if (isVelocityHighEnoughToCollideAfterRepositioning || correctGravityPresent) {
            physicsWorld.setBounceOnce(sprite, boundaryBoxIdentifier)
        }
    }

    private fun calculateBoundaryBoxDimensions(physicsObject: PhysicsObject) {
        val boundaryBoxLowerEdge = Vector2()
        val boundaryBoxUpperEdge = Vector2()
        physicsObject.getBoundaryBox(boundaryBoxLowerEdge, boundaryBoxUpperEdge)
        boundaryBoxWidth = boundaryBoxUpperEdge.x - boundaryBoxLowerEdge.x
        boundaryBoxHeight = boundaryBoxUpperEdge.y - boundaryBoxLowerEdge.y
        boundaryBoxCenterX = boundaryBoxLowerEdge.x + boundaryBoxWidth / 2
        boundaryBoxCenterY = boundaryBoxLowerEdge.y + boundaryBoxHeight / 2
    }

    private fun isBoundaryBoxCenterInLeftCollisionArea(): Boolean {
        return calculateLeftCollisionAreaOuterBorder() < boundaryBoxCenterX &&
            boundaryBoxCenterX < calculateLeftCollisionAreaInnerBorder()
    }

    private fun isBoundaryBoxCenterInRightCollisionArea(): Boolean {
        return calculateRightCollisionAreaOuterBorder() > boundaryBoxCenterX &&
            boundaryBoxCenterX > calculateRightCollisionAreaInnerBorder()
    }

    private fun isBoundaryBoxCenterInBottomCollisionArea(): Boolean {
        return calculateBottomCollisionAreaOuterBorder() < boundaryBoxCenterY &&
            boundaryBoxCenterY < calculateBottomCollisionAreaInnerBorder()
    }

    private fun isBoundaryBoxCenterInTopCollisionArea(): Boolean {
        return calculateTopCollisionAreaOuterBorder() > boundaryBoxCenterY &&
            boundaryBoxCenterY > calculateTopCollisionAreaInnerBorder()
    }

    private fun calculateLeftCollisionAreaInnerBorder(): Float =
        -virtualScreenWidth / 2.0f + boundaryBoxWidth / 2.0f

    private fun calculateLeftCollisionAreaOuterBorder(): Float =
        calculateLeftCollisionAreaInnerBorder() + COLLISION_OVERLAP_RANGE_FACTOR * -boundaryBoxWidth

    private fun calculateRightCollisionAreaInnerBorder(): Float = virtualScreenWidth / 2.0f - boundaryBoxWidth / 2.0f

    private fun calculateRightCollisionAreaOuterBorder(): Float =
        calculateRightCollisionAreaInnerBorder() + COLLISION_OVERLAP_RANGE_FACTOR * boundaryBoxWidth

    private fun calculateBottomCollisionAreaInnerBorder(): Float =
        -vsHeight / 2.0f + boundaryBoxHeight / 2.0f

    private fun calculateBottomCollisionAreaOuterBorder(): Float =
        calculateBottomCollisionAreaInnerBorder() + COLLISION_OVERLAP_RANGE_FACTOR * -boundaryBoxHeight

    private fun calculateTopCollisionAreaInnerBorder(): Float = vsHeight / 2.0f - boundaryBoxHeight / 2.0f

    private fun calculateTopCollisionAreaOuterBorder(): Float =
        calculateTopCollisionAreaInnerBorder() + COLLISION_OVERLAP_RANGE_FACTOR * boundaryBoxHeight

    private fun isLeftVelocityHighEnoughToCollideAfterRepositioning(physicsObject: PhysicsObject):
        Boolean = physicsObject.velocity.x <= -Companion.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE

    private fun isRightVelocityHighEnoughToCollideAfterRepositioning(physicsObject: PhysicsObject):
        Boolean = physicsObject.velocity.x >= Companion.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE

    private fun isBottomVelocityHighEnoughToCollideAfterRepositioning(physicsObject: PhysicsObject):
        Boolean = physicsObject.velocity.y <= -Companion.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE

    private fun isTopVelocityHighEnoughToCollideAfterRepositioning(physicsObject: PhysicsObject):
        Boolean = physicsObject.velocity.y >= Companion.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE

    private fun isLeftGravityPresent(): Boolean = physicsWorld.gravity.x < 0

    private fun isRightGravityPresent(): Boolean = physicsWorld.gravity.x > 0

    private fun isBottomGravityPresent(): Boolean = physicsWorld.gravity.y < 0

    private fun isTopGravityPresent(): Boolean = physicsWorld.gravity.y > 0

    private fun calculateLeftCollisionOffset(): Float =
        Math.abs(boundaryBoxCenterX - boundaryBoxWidth / 2.0f + virtualScreenWidth / 2.0f)

    private fun calculateRightCollisionOffset(): Float =
        Math.abs(boundaryBoxCenterX + boundaryBoxWidth / 2.0f - virtualScreenWidth / 2.0f)

    private fun calculateBottomCollisionOffset(): Float =
        Math.abs(boundaryBoxCenterY - boundaryBoxHeight / 2.0f + vsHeight / 2.0f)

    private fun calculateTopCollisionOffset(): Float =
        Math.abs(boundaryBoxCenterY + boundaryBoxHeight / 2.0f - vsHeight / 2.0f)
}
