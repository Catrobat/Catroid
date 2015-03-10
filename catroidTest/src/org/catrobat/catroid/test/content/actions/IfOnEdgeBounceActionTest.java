/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IfOnEdgeBounceActionTest extends InstrumentationTestCase {

	private Action ifOnEdgeBounceAction;
	private Sprite sprite;
	private static final float WIDTH = 100;
	private static final float HEIGHT = 100;

	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private static final float TOP_BORDER_POSITION = SCREEN_HEIGHT / 2f;
	private static final float BOTTOM_BORDER_POSITION = -TOP_BORDER_POSITION;
	private static final float RIGHT_BORDER_POSITION = SCREEN_WIDTH / 2f;
	private static final float LEFT_BORDER_POSITION = -RIGHT_BORDER_POSITION;

	private static final float BOUNCE_TOP_POSITION = TOP_BORDER_POSITION - (HEIGHT / 2f);
	private static final float BOUNCE_BOTTOM_POSITION = -BOUNCE_TOP_POSITION;
	private static final float BOUNCE_RIGHT_POSITION = RIGHT_BORDER_POSITION - (WIDTH / 2f);
	private static final float BOUNCE_LEFT_POSITION = -BOUNCE_RIGHT_POSITION;

	@Override
	public void setUp() throws Exception {
		sprite = new Sprite("Test");
		sprite.look.setWidth(WIDTH);
		sprite.look.setHeight(HEIGHT);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);

		ifOnEdgeBounceAction = ExtendedActions.ifOnEdgeBounce(sprite);

		Project project = new Project();
		project.getXmlHeader().virtualScreenWidth = SCREEN_WIDTH;
		project.getXmlHeader().virtualScreenHeight = SCREEN_HEIGHT;

		ProjectManager.getInstance().setProject(project);
	}

	public void testNoBounce() {
		setPositionAndDirection(0f, 0f, 90f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(0f, 0f, 90f);
	}

	public void testTopBounce() {
		// Bounce if -90 < direction < 90

		Map<Float, Float> expectedDirections = new HashMap<Float, Float>();
		expectedDirections.put(90f, 90f);
		expectedDirections.put(120f, 120f);
		expectedDirections.put(150f, 150f);
		expectedDirections.put(180f, 180f);
		expectedDirections.put(-150f, -150f);
		expectedDirections.put(-120f, -120f);
		expectedDirections.put(-90f, -90f);
		expectedDirections.put(-60f, -120f);
		expectedDirections.put(-30f, -150f);
		expectedDirections.put(0f, 180f);
		expectedDirections.put(30f, 150f);
		expectedDirections.put(60f, 120f);

		checkIfExpectedDirectionsContainsAllKeys(expectedDirections);
		for (Entry<Float, Float> entry : expectedDirections.entrySet()) {
			float direction = entry.getKey();
			float directionAfterBounce = entry.getValue();

			setPositionAndDirection(0, TOP_BORDER_POSITION, direction);
			executeIfOnEdgeBounceAction();
			checkPositionAndDirection(0, BOUNCE_TOP_POSITION, directionAfterBounce);
		}
	}

	public void testBottomBounce() {
		// Bounce if direction < -90 or direction > 90

		Map<Float, Float> expectedDirections = new HashMap<Float, Float>();
		expectedDirections.put(90f, 90f);
		expectedDirections.put(120f, 60f);
		expectedDirections.put(150f, 30f);
		expectedDirections.put(180f, 0f);
		expectedDirections.put(-150f, -30f);
		expectedDirections.put(-120f, -60f);
		expectedDirections.put(-90f, -90f);
		expectedDirections.put(-60f, -60f);
		expectedDirections.put(-30f, -30f);
		expectedDirections.put(0f, 0f);
		expectedDirections.put(30f, 30f);
		expectedDirections.put(60f, 60f);

		checkIfExpectedDirectionsContainsAllKeys(expectedDirections);
		for (Entry<Float, Float> entry : expectedDirections.entrySet()) {
			float direction = entry.getKey();
			float directionAfterBounce = entry.getValue();

			setPositionAndDirection(0, BOTTOM_BORDER_POSITION, direction);
			executeIfOnEdgeBounceAction();
			checkPositionAndDirection(0, BOUNCE_BOTTOM_POSITION, directionAfterBounce);
		}
	}

	public void testLeftBounce() {
		// Bounce if -180 < direction < 0

		Map<Float, Float> expectedDirections = new HashMap<Float, Float>();
		expectedDirections.put(90f, 90f);
		expectedDirections.put(120f, 120f);
		expectedDirections.put(150f, 150f);
		expectedDirections.put(180f, 180f);
		expectedDirections.put(-150f, 150f);
		expectedDirections.put(-120f, 120f);
		expectedDirections.put(-90f, 90f);
		expectedDirections.put(-60f, 60f);
		expectedDirections.put(-30f, 30f);
		expectedDirections.put(0f, 0f);
		expectedDirections.put(30f, 30f);
		expectedDirections.put(60f, 60f);

		checkIfExpectedDirectionsContainsAllKeys(expectedDirections);
		for (Entry<Float, Float> entry : expectedDirections.entrySet()) {
			float direction = entry.getKey();
			float directionAfterBounce = entry.getValue();

			setPositionAndDirection(LEFT_BORDER_POSITION, 0, direction);
			executeIfOnEdgeBounceAction();
			checkPositionAndDirection(BOUNCE_LEFT_POSITION, 0, directionAfterBounce);
		}
	}

	public void testRightBounce() {
		// Bounce if 0 < direction < 180

		Map<Float, Float> expectedDirections = new HashMap<Float, Float>();
		expectedDirections.put(90f, -90f);
		expectedDirections.put(120f, -120f);
		expectedDirections.put(150f, -150f);
		expectedDirections.put(180f, 180f);
		expectedDirections.put(-150f, -150f);
		expectedDirections.put(-120f, -120f);
		expectedDirections.put(-90f, -90f);
		expectedDirections.put(-60f, -60f);
		expectedDirections.put(-30f, -30f);
		expectedDirections.put(0f, 0f);
		expectedDirections.put(30f, -30f);
		expectedDirections.put(60f, -60f);

		checkIfExpectedDirectionsContainsAllKeys(expectedDirections);
		for (Entry<Float, Float> entry : expectedDirections.entrySet()) {
			float direction = entry.getKey();
			float directionAfterBounce = entry.getValue();

			setPositionAndDirection(RIGHT_BORDER_POSITION, 0, direction);
			executeIfOnEdgeBounceAction();
			checkPositionAndDirection(BOUNCE_RIGHT_POSITION, 0, directionAfterBounce);
		}
	}

	public void testUpLeftBounce() {
		setPositionAndDirection(LEFT_BORDER_POSITION, TOP_BORDER_POSITION, 135f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_LEFT_POSITION, BOUNCE_TOP_POSITION, 135f);

		setPositionAndDirection(LEFT_BORDER_POSITION, TOP_BORDER_POSITION, -45f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_LEFT_POSITION, BOUNCE_TOP_POSITION, 135f);
	}

	public void testUpRightBounce() {
		setPositionAndDirection(RIGHT_BORDER_POSITION, TOP_BORDER_POSITION, -135f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_RIGHT_POSITION, BOUNCE_TOP_POSITION, -135f);

		setPositionAndDirection(RIGHT_BORDER_POSITION, TOP_BORDER_POSITION, 45);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_RIGHT_POSITION, BOUNCE_TOP_POSITION, -135f);
	}

	public void testBottomLeftBounce() {
		setPositionAndDirection(LEFT_BORDER_POSITION, BOTTOM_BORDER_POSITION, 45f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_LEFT_POSITION, BOUNCE_BOTTOM_POSITION, 45f);

		setPositionAndDirection(LEFT_BORDER_POSITION, BOTTOM_BORDER_POSITION, -135f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_LEFT_POSITION, BOUNCE_BOTTOM_POSITION, 45f);
	}

	public void testBottomRightBounce() {
		setPositionAndDirection(RIGHT_BORDER_POSITION, BOTTOM_BORDER_POSITION, -45f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_RIGHT_POSITION, BOUNCE_BOTTOM_POSITION, -45f);

		setPositionAndDirection(RIGHT_BORDER_POSITION, BOTTOM_BORDER_POSITION, 135f);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_RIGHT_POSITION, BOUNCE_BOTTOM_POSITION, -45f);
	}

	private void checkIfExpectedDirectionsContainsAllKeys(Map<Float, Float> expectedDirections) {
		assertEquals("The expected directions count is wrong", 12, expectedDirections.size());

		assertTrue("A 90° direction is missing", expectedDirections.containsKey(90f));
		assertTrue("A 120° direction is missing", expectedDirections.containsKey(120f));
		assertTrue("A 150° direction is missing", expectedDirections.containsKey(150f));
		assertTrue("A 180° direction is missing", expectedDirections.containsKey(180f));
		assertTrue("A -150° direction is missing", expectedDirections.containsKey(-150f));
		assertTrue("A -120° direction is missing", expectedDirections.containsKey(-120f));
		assertTrue("A -90° direction is missing", expectedDirections.containsKey(-90f));
		assertTrue("A -60° direction is missing", expectedDirections.containsKey(-60f));
		assertTrue("A -30° direction is missing", expectedDirections.containsKey(-30f));
		assertTrue("A 0° direction is missing", expectedDirections.containsKey(0f));
		assertTrue("A 30° direction is missing", expectedDirections.containsKey(30f));
		assertTrue("A 60° direction is missing", expectedDirections.containsKey(60f));
	}

	private void setPositionAndDirection(float x, float y, float direction) {
		Look look = sprite.look;
		look.setPositionInUserInterfaceDimensionUnit(x, y);
		look.setDirectionInUserInterfaceDimensionUnit(direction);
	}

	private void executeIfOnEdgeBounceAction() {
		ifOnEdgeBounceAction.restart();
		ifOnEdgeBounceAction.act(1.0f);
	}

	private void checkPositionAndDirection(float expectedX, float expectedY, float expectedDirection) {
		Look look = sprite.look;
		assertEquals("Wrong x after bounce", expectedX, look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y after bounce", expectedY, look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction after bounce", expectedDirection, look.getDirectionInUserInterfaceDimensionUnit());
	}
}
