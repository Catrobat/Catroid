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

package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.SetTransparencyAction;
import org.catrobat.catroid.content.actions.SetVisibleAction;
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.content.actions.SetYAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PhysicsObjectStateTest {

	private Object physicsObjectStateHandler = null;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() throws Exception {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		physicsObjectStateHandler = Reflection.getPrivateField(sprite.look, "physicsObjectStateHandler");
	}

	@Test
	public void testVisibility() throws Exception {
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

	@Test
	public void testPos() throws Exception {
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

	@Test
	public void testGlideTo() throws Exception {
		allConditionsInactiveCheck();
		Action action = glideTo(new Formula(100), new Formula(100));
		hangupFixedActiveCheck();
		action.act(1.0f);
		allConditionsInactiveCheck();
	}

	@Test
	public void testPositionAndGlideTo() throws Exception {
		allConditionsInactiveCheck();
		Action action = glideTo(new Formula(10 + PhysicsWorld.activeArea.x), new Formula(100));
		hangupFixedActiveCheck();
		action.act(1.0f);
		hangupNonCollidingActiveCheck();
		setX(0.0f);
		allConditionsInactiveCheck();
	}

	@Test
	public void testVisibleAndPositionAndGlideTo() throws Exception {
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

	private void allConditionsInactiveCheck() throws Exception {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertFalse(hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertFalse(nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertFalse(fixed);
	}

	private void hangupNonCollidingActiveCheck() throws Exception {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue(hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertTrue(nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertFalse(fixed);
	}

	private void hangupFixedActiveCheck() throws Exception {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue(hangedUp);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertFalse(nonColliding);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertTrue(fixed);
	}

	private void hangupFixedNonCollidingActiveCheck() throws Exception {
		((PhysicsLook) sprite.look).updatePhysicsObjectState(true);
		boolean hangedUp = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp");
		assertTrue(hangedUp);

		boolean fixed = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "fixed");
		assertTrue(fixed);

		boolean nonColliding = (Boolean) Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding");
		assertTrue(nonColliding);
	}

	private void setX(float value) {
		SetXAction setXAction = (SetXAction) sprite.getActionFactory().createSetXAction(sprite,
				new SequenceAction(), new Formula(value));
		setXAction.act(1.0f);
		sprite.look.getX();
	}

	private void setY(float value) {
		SetYAction setYAction = (SetYAction) sprite.getActionFactory().createSetYAction(sprite,
				new SequenceAction(), new Formula(value));
		setYAction.act(1.0f);
		sprite.look.getY();
	}

	private void transparency(int percent) {
		SetTransparencyAction ghostEffectAction = (SetTransparencyAction) sprite.getActionFactory()
										.createSetTransparencyAction(sprite, new SequenceAction(),
												new Formula(percent));
		ghostEffectAction.act(1.0f);
	}

	private void show() {
		setVisible(true);
	}

	private void hide() {
		setVisible(false);
	}

	private void setVisible(boolean visible) {
		SetVisibleAction showAction = new SetVisibleAction();
		showAction.setSprite(sprite);
		showAction.setVisible(visible);
		showAction.act(1.0f);
	}

	private Action glideTo(Formula x, Formula y) {
		return sprite.getActionFactory().createGlideToPhysicsAction(sprite, (PhysicsLook) sprite.look,
				new SequenceAction(), x, y, 2.0f, 1.0f);
	}
}
