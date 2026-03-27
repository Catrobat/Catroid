/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.PlotArcAction;
import org.catrobat.catroid.content.bricks.PlotArcBrick;
import org.catrobat.catroid.embroidery.DSTPatternManager;
import org.catrobat.catroid.embroidery.SimpleRunningStitch;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class EmbroideryArcActionTest {

	private static final int STITCH_LENGTH = 10;
	private static final int RADIUS = 100;

	private Sprite sprite;
	private DSTPatternManager embroideryPatternManager;

	@Before
	public void setUp() {
		Group parentGroup = new Group();
		sprite = new Sprite("sprite");
		parentGroup.addActor(sprite.look);

		embroideryPatternManager = new DSTPatternManager();
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager;
	}

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testHalfArcProducesStitchesOffHorizontalAxis() {
		ArrayList<StitchPoint> stitchPoints = executeArc(180f);

		assertTrue("Expected embroidery output for a 180 degree arc", embroideryPatternManager.validPatternExists());
		assertTrue("Expected multiple stitches for a 180 degree arc", stitchPoints.size() > 2);
		assertTrue("Expected at least one stitch with a vertical offset", containsPointOffHorizontalAxis(stitchPoints));
	}

	@Test
	public void testFullArcProducesEmbroideryOutput() {
		ArrayList<StitchPoint> stitchPoints = executeArc(360f);

		assertTrue("Expected embroidery output for a 360 degree arc", embroideryPatternManager.validPatternExists());
		assertFalse("Expected a non-empty stitch list for a 360 degree arc", stitchPoints.isEmpty());
		assertTrue("Expected the full arc to contain stitches away from the horizontal axis",
				containsPointOffHorizontalAxis(stitchPoints));
	}

	private ArrayList<StitchPoint> executeArc(float degrees) {
		sprite.runningStitch.activateStitching(sprite, new SimpleRunningStitch(sprite, STITCH_LENGTH));

		PlotArcAction plotArcAction = new PlotArcAction();
		plotArcAction.setScope(new Scope(new Project(), sprite, new SequenceAction()));
		plotArcAction.setDirection(PlotArcBrick.Directions.RIGHT);
		plotArcAction.setRadius(new Formula(RADIUS));
		plotArcAction.setDegrees(new Formula(degrees));
		plotArcAction.begin();
		plotArcAction.update(1f);

		return embroideryPatternManager.getEmbroideryPatternList();
	}

	private boolean containsPointOffHorizontalAxis(ArrayList<StitchPoint> stitchPoints) {
		for (StitchPoint stitchPoint : stitchPoints) {
			if (Math.abs(stitchPoint.getY()) > 0.1f) {
				return true;
			}
		}
		return false;
	}
}
