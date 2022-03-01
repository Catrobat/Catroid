/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.content.actions;

import android.graphics.PointF;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Queue;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.stage.CameraPositioner;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PenDownActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float X_MOVEMENT = 100.0f;
	private static final float Y_MOVEMENT = 50.0f;
	private Formula xMovement = new Formula(X_MOVEMENT);
	private Formula yMovement = new Formula(Y_MOVEMENT);
	private Sprite sprite;
	private OrthographicCamera camera = Mockito.spy(new OrthographicCamera());
	private CameraPositioner cameraPositioner = new CameraPositioner(camera, 960.0f, 540.0f);
	private String projectName = "testProject";

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		Mockito.doNothing().when(camera).update();
		createTestProject();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createPenDownAction(null);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}

	@Test
	public void testSaveOnePositionChange() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createChangeXByNAction(sprite, new SequenceAction(), xMovement).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().removeFirst().x);
	}

	@Test
	public void testSimultaneousPositionChangeXY() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), xMovement,
				yMovement).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().first().x);
		assertEquals(Y_MOVEMENT, positions.first().removeFirst().y);
	}

	@Test
	public void testPositionChangeX() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), new Formula(0f),
				new Formula(0f)).act(1.0f);

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), xMovement,
				new Formula(0f)).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().first().x);
		assertEquals(0f, positions.first().removeFirst().y);
	}

	@Test
	public void testAfterBecomeFocusPoint() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		cameraPositioner.setHorizontalFlex(0f);
		cameraPositioner.setVerticalFlex(0f);
		cameraPositioner.setSpriteToFocusOn(sprite);

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), xMovement,
				yMovement).act(1.0f);
		cameraPositioner.updateCameraPositionForFocusedSprite();

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertEquals(X_MOVEMENT, positions.first().first().x);
		assertEquals(Y_MOVEMENT, positions.first().removeFirst().y);
		cameraPositioner.reset();
	}

	@Test
	public void testMultiplePenDownActions() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);
		sprite.getActionFactory().createPenDownAction(sprite).act(1.0f);

		Queue<Queue<PointF>> positions = sprite.penConfiguration.getPositions();
		assertEquals(0f, positions.first().removeFirst().x);
		assertTrue(positions.first().isEmpty());
	}

	private void createTestProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);
	}
}
