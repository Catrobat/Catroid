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
package org.catrobat.catroid.physics.content

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GlideToPhysicsAction
import org.catrobat.catroid.content.actions.IfOnEdgeBouncePhysicsAction
import org.catrobat.catroid.content.actions.SetBounceFactorAction
import org.catrobat.catroid.content.actions.SetFrictionAction
import org.catrobat.catroid.content.actions.SetGravityAction
import org.catrobat.catroid.content.actions.SetMassAction
import org.catrobat.catroid.content.actions.SetPhysicsObjectTypeAction
import org.catrobat.catroid.content.actions.SetVelocityAction
import org.catrobat.catroid.content.actions.TurnLeftSpeedAction
import org.catrobat.catroid.content.actions.TurnRightSpeedAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsWorld

class ActionPhysicsFactory : ActionFactory() {
    private val physicsWorld: PhysicsWorld
        get() = ProjectManager.getInstance().currentlyPlayingScene.physicsWorld

    private fun getPhysicsObject(sprite: Sprite): PhysicsObject = physicsWorld.getPhysicsObject(sprite)

    override fun createIfOnEdgeBounceAction(sprite: Sprite): Action {
        val action = Actions.action(IfOnEdgeBouncePhysicsAction::class.java)
        action.sprite = sprite
        action.physicsWorld = physicsWorld
        return action
    }

    override fun createGlideToAction(sprite: Sprite, sequence: SequenceAction, x: Formula?, y: Formula?, duration: Formula?): Action {
        val action = Actions.action(GlideToPhysicsAction::class.java)
        action.setPosition(x, y)
        action.setDuration(duration)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsLook(sprite.look as PhysicsLook)
        return action
    }

    override fun createSetBounceFactorAction(sprite: Sprite, sequence: SequenceAction, bounceFactor: Formula?): Action {
        val action = Actions.action(SetBounceFactorAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setBounceFactor(bounceFactor)
        return action
    }

    override fun createSetFrictionAction(sprite: Sprite, sequence: SequenceAction, friction: Formula?): Action {
        val action = Actions.action(SetFrictionAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setFriction(friction)
        return action
    }

    override fun createSetGravityAction(sprite: Sprite, sequence: SequenceAction, gravityX: Formula?, gravityY: Formula?): Action {
        val action = Actions.action(SetGravityAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsWorld(physicsWorld)
        action.setGravity(gravityX, gravityY)
        return action
    }

    override fun createSetMassAction(sprite: Sprite, sequence: SequenceAction, mass: Formula?): Action {
        val action = Actions.action(SetMassAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setMass(mass)
        return action
    }

    override fun createSetPhysicsObjectTypeAction(sprite: Sprite, type: PhysicsObject.Type): Action {
        val action = Actions.action(SetPhysicsObjectTypeAction::class.java)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setType(type)
        return action
    }

    override fun createSetVelocityAction(sprite: Sprite, sequence: SequenceAction, velocityX: Formula?, velocityY: Formula?): Action {
        val action = Actions.action(SetVelocityAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setVelocity(velocityX, velocityY)
        return action
    }

    override fun createTurnLeftSpeedAction(sprite: Sprite, sequence: SequenceAction, speed: Formula?): Action {
        val action = Actions.action(TurnLeftSpeedAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setSpeed(speed)
        return action
    }

    override fun createTurnRightSpeedAction(sprite: Sprite, sequence: SequenceAction, speed: Formula?): Action {
        val action = Actions.action(TurnRightSpeedAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.setScope(scope)
        action.setPhysicsObject(getPhysicsObject(sprite))
        action.setSpeed(speed)
        return action
    }
}
