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

package org.catrobat.catroid.test.physics.actions.conditional;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.actions.HideAction;
import org.catrobat.catroid.content.actions.SetGhostEffectAction;
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.content.actions.SetYAction;
import org.catrobat.catroid.content.actions.ShowAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.actions.GlideToPhysicsAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class HangUpTest extends PhysicsBaseTest {

	PhysicsObject physicsObject = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsObject = physicsWorld.getPhysicsObject(sprite);
	}

	@Override
	protected void tearDown() throws Exception {
		physicsObject = null;
		super.tearDown();
	}

	public void testVisibility() {
		activeCheck();
		gostEffect(100);
		inactiveCheck();
		gostEffect(0);
		activeCheck();
		hide();
		inactiveCheck();
		show();
		activeCheck();

		gostEffect(100);
		inactiveCheck();
		show();
		inactiveCheck();
		hide();
		inactiveCheck();
		gostEffect(0);
		inactiveCheck();
		show();
		activeCheck();

		hide();
		inactiveCheck();
		gostEffect(100);
		inactiveCheck();
		show();
		inactiveCheck();
		gostEffect(0);
		activeCheck();
	}

	public void testPos() {
		activeCheck();
		setX(10 + PhysicsWorld.activeArea.x);
		inactiveCheck();
		setX(0.0f);
		activeCheck();
		setX(-10 - PhysicsWorld.activeArea.x);
		inactiveCheck();
		setX(0.0f);
		activeCheck();
		setY(-10 - PhysicsWorld.activeArea.y);
		inactiveCheck();
		setY(0);
		activeCheck();
		setY(10 + PhysicsWorld.activeArea.y);
		inactiveCheck();
		setY(0);
		activeCheck();
	}

	public void testGlideTo() {
		activeCheck();
		Action action = glideTo(new Formula(100), new Formula(100));
		inactiveCheck();
		action.act(1.0f);
		activeCheck();
		action = glideTo(new Formula(10 + PhysicsWorld.activeArea.x), new Formula(100));
		inactiveCheck();
		action.act(1.0f);
		inactiveCheck();
		setX(0.0f);
		activeCheck();
	}



	// HELPER ----------------------------------------------------------------------------------------------------------

	private void activeCheck() {
		assertTrue("Unexpected physicsObject-status: should be active", ((PhysicsLook) sprite.look).isActive());
	}

	private void inactiveCheck() {
		assertFalse("Unexpected physicsObject-status: should be inactive", ((PhysicsLook) sprite.look).isActive());
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

	private void gostEffect(int percent) {
		SetGhostEffectAction ghostEffectAction = new SetGhostEffectAction();
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
		glideToPhysicsAction.setPhysicsLook((PhysicsLook)sprite.look);
		glideToPhysicsAction.setPosition(x, y);
		glideToPhysicsAction.setDuration(2.0f);
		glideToPhysicsAction.act(1.0f);
		return glideToPhysicsAction;
	}
}
