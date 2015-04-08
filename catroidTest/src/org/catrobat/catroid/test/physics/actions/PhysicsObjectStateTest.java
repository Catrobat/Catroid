/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.actions.HideAction;
import org.catrobat.catroid.content.actions.SetTransparencyAction;
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.content.actions.SetYAction;
import org.catrobat.catroid.content.actions.ShowAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.actions.GlideToPhysicsAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.Reflection;

public class PhysicsObjectStateTest extends PhysicsBaseTest {

	PhysicsObject physicsObject = null;
	Object physicsObjectStateHandler = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObjectStateHandler = Reflection.getPrivateField(sprite.look, "physicsObjectStateHandler");
	}

	@Override
	protected void tearDown() throws Exception {
		physicsObject = null;
		super.tearDown();
	}

	public void testVisibility() {
		allConditionsInactiveCheck();
		transparency(100);
		hangupNonCollidingActiveCheck();
		transparency(0);
		allConditionsInactiveCheck();
		hide();
		hangupNonCollidingActiveCheck();
		show();
		allConditionsInactiveCheck();

		transparency(100);
		hangupNonCollidingActiveCheck();
		show();
		hangupNonCollidingActiveCheck();
		hide();
		hangupNonCollidingActiveCheck();
		transparency(0);
		hangupNonCollidingActiveCheck();
		show();
		allConditionsInactiveCheck();

		hide();
		hangupNonCollidingActiveCheck();
		transparency(100);
		hangupNonCollidingActiveCheck();
		show();
		hangupNonCollidingActiveCheck();
		transparency(0);
		allConditionsInactiveCheck();
	}

	public void testPos() {
		allConditionsInactiveCheck();
		setX(10 + PhysicsWorld.activeArea.x);
		hangupNonCollidingActiveCheck();
		setX(0.0f);
		allConditionsInactiveCheck();
		setX(-10 - PhysicsWorld.activeArea.x);
		hangupNonCollidingActiveCheck();
		setX(0.0f);
		allConditionsInactiveCheck();
		setY(-10 - PhysicsWorld.activeArea.y);
		hangupNonCollidingActiveCheck();
		setY(0);
		allConditionsInactiveCheck();
		setY(10 + PhysicsWorld.activeArea.y);
		hangupNonCollidingActiveCheck();
		setY(0);
		allConditionsInactiveCheck();
	}

	public void testGlideTo() {
		allConditionsInactiveCheck();
		Action action = glideTo(new Formula(100), new Formula(100));
		hangupFixedActiveCheck();
		action.act(1.0f);
		allConditionsInactiveCheck();
	}

	public void testPositionAndGlideTo() {
		allConditionsInactiveCheck();
		Action action = glideTo(new Formula(10 + PhysicsWorld.activeArea.x), new Formula(100));
		hangupFixedActiveCheck();
		action.act(1.0f);
		hangupNonCollidingActiveCheck();
		setX(0.0f);
		allConditionsInactiveCheck();
	}

	public void testVisibleAndPositionAndGlideTo() {
		allConditionsInactiveCheck();
		Action action = glideTo(new Formula(10 + PhysicsWorld.activeArea.x), new Formula(100));
		hangupFixedActiveCheck();
		hide();
		hangupFixedNonCollidingActiveCheck();
		show();
		hangupFixedActiveCheck();
		hide();
		action.act(1.0f);
		hangupNonCollidingActiveCheck();
		setX(0.0f);
		hangupNonCollidingActiveCheck();
		show();
		allConditionsInactiveCheck();

		action = glideTo(new Formula(100), new Formula(10 + PhysicsWorld.activeArea.y));
		hangupFixedActiveCheck();
		show();
		hangupFixedActiveCheck();
		hide();
		hangupFixedNonCollidingActiveCheck();
		show();
		hangupFixedActiveCheck();
		action.act(1.0f);
		hangupNonCollidingActiveCheck();
		setY(0.0f);
		allConditionsInactiveCheck();
	}


	// HELPER ----------------------------------------------------------------------------------------------------------

	private void allConditionsInactiveCheck() {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertFalse("Unexpected physicsObject-status: hangedUp should be inactive", hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertFalse("Unexpected physicsObject-status: non colliding should be inactive", nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertFalse("Unexpected physicsObject-status: fixed should be inactive", fixed);
	}

	private void hangupNonCollidingActiveCheck() {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue("Unexpected physicsObject-status: hangup should be active", hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertTrue("Unexpected physicsObject-status: non colliding should be active", nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertFalse("Unexpected physicsObject-status: fixed should be inactive", fixed);
	}

	private void hangupFixedActiveCheck() {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue("Unexpected physicsObject-status: hangup should be active", hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertFalse("Unexpected physicsObject-status: non colliding should be active", nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertTrue("Unexpected physicsObject-status: fixed should be active", fixed);
	}

	private void hangupFixedNonCollidingActiveCheck() {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue("Unexpected physicsObject-status: hangup should be active", hangedUp);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertTrue("Unexpected physicsObject-status: fixed should be active", fixed);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertTrue("Unexpected physicsObject-status: non colliding should be active", nonColliding);
	}

	private void setX(float value) {
		SetXAction setXAction = new SetXAction();
		setXAction.setSprite(sprite);
		setXAction.setX(new Formula(value));
		setXAction.act(1.0f);
		sprite.look.getX();
	}

	private void setY(float value) {
		SetYAction setYAction = new SetYAction();
		setYAction.setSprite(sprite);
		setYAction.setY(new Formula(value));
		setYAction.act(1.0f);
		sprite.look.getY();
	}

	private void transparency(int percent) {
		SetTransparencyAction ghostEffectAction = new SetTransparencyAction();
		ghostEffectAction.setSprite(sprite);
		ghostEffectAction.setTransparency(new Formula(percent));
		ghostEffectAction.act(1.0f);
	}

	private void show() {
		ShowAction showAction = new ShowAction();
		showAction.setSprite(sprite);
		showAction.act(1.0f);
	}

	private void hide() {
		HideAction hideAction = new HideAction();
		hideAction.setSprite(sprite);
		hideAction.act(1.0f);
	}

	private Action glideTo(Formula x, Formula y) {
		GlideToPhysicsAction glideToPhysicsAction = new GlideToPhysicsAction();
		glideToPhysicsAction.setSprite(sprite);
		glideToPhysicsAction.setPhysicsLook((PhysicsLook) sprite.look);
		glideToPhysicsAction.setPosition(x, y);
		glideToPhysicsAction.setDuration(2.0f);
		glideToPhysicsAction.act(1.0f);
		return glideToPhysicsAction;
	}
}
