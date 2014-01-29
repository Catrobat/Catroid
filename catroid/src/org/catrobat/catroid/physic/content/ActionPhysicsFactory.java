/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physic.content;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.conditional.GlideToPhysicAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsObject.Type;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.physic.content.actions.IfOnEdgeBouncePhysicAction;
import org.catrobat.catroid.physic.content.actions.SetBounceFactorAction;
import org.catrobat.catroid.physic.content.actions.SetFrictionAction;
import org.catrobat.catroid.physic.content.actions.SetGravityAction;
import org.catrobat.catroid.physic.content.actions.SetMassAction;
import org.catrobat.catroid.physic.content.actions.SetPhysicsObjectTypeAction;
import org.catrobat.catroid.physic.content.actions.SetVelocityAction;
import org.catrobat.catroid.physic.content.actions.TurnLeftSpeedAction;
import org.catrobat.catroid.physic.content.actions.TurnRightSpeedAction;

public class ActionPhysicsFactory extends ActionFactory {

	private PhysicsObject getPhysicObject(Sprite sprite) {
		return getPhysicWorld().getPhysicObject(sprite);
	}

	private PhysicsWorld getPhysicWorld() {
		return ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
	}

	// OVERRIDE
	@Override
	public Action createIfOnEdgeBounceAction(Sprite sprite) {
		IfOnEdgeBouncePhysicAction action = Actions.action(IfOnEdgeBouncePhysicAction.class);
		action.setSprite(sprite);
		action.setPhysicWorld(getPhysicWorld());
		return action;
	}

	public Action createGlideToAction(Sprite sprite) {
		GlideToPhysicAction action = Actions.action(GlideToPhysicAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		return action;
	}

	// PHYSICS
	@Override
	public Action createSetBounceFactorAction(Sprite sprite, Formula bounceFactor) {
		SetBounceFactorAction action = Actions.action(SetBounceFactorAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setBounceFactor(bounceFactor);
		return action;
	}

	@Override
	public Action createSetFrictionAction(Sprite sprite, Formula friction) {
		SetFrictionAction action = Actions.action(SetFrictionAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setFriction(friction);
		return action;
	}

	@Override
	public Action createSetGravityAction(Sprite sprite, Formula gravityX, Formula gravityY) {
		SetGravityAction action = Actions.action(SetGravityAction.class);
		action.setSprite(sprite);
		action.setPhysicWorld(getPhysicWorld());
		action.setGravity(gravityX, gravityY);
		return action;
	}

	@Override
	public Action createSetMassAction(Sprite sprite, Formula mass) {
		SetMassAction action = Actions.action(SetMassAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setMass(mass);
		return action;
	}

	@Override
	public Action createSetPhysicObjectTypeAction(Sprite sprite, Type type) {
		SetPhysicsObjectTypeAction action = Actions.action(SetPhysicsObjectTypeAction.class);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setType(type);
		return action;
	}

	@Override
	public Action createSetVelocityAction(Sprite sprite, Formula velocityX, Formula velocityY) {
		SetVelocityAction action = Actions.action(SetVelocityAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setVelocity(velocityX, velocityY);
		return action;
	}

	@Override
	public Action createTurnLeftSpeedAction(Sprite sprite, Formula speed) {
		TurnLeftSpeedAction action = Actions.action(TurnLeftSpeedAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setSpeed(speed);
		return action;
	}

	@Override
	public Action createTurnRightSpeedAction(Sprite sprite, Formula speed) {
		TurnRightSpeedAction action = Actions.action(TurnRightSpeedAction.class);
		action.setSprite(sprite);
		action.setPhysicObject(getPhysicObject(sprite));
		action.setSpeed(speed);
		return action;
	}
}
