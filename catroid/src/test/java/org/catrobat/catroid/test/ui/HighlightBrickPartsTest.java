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

package org.catrobat.catroid.test.ui;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class HighlightBrickPartsTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{2, Arrays.asList(2, 5, 6)},
				{5, Arrays.asList(2, 5, 6)},
				{6, Arrays.asList(2, 5, 6)},
				{1, Arrays.asList(1, 7)},
				{7, Arrays.asList(1, 7)},
				{3, Arrays.asList(3, 4)},
				{4, Arrays.asList(3, 4)}
		});
	}

	@Parameterized.Parameter
	public @IdRes int brickPositionToHighlight;

	@Parameterized.Parameter(1)
	public @IdRes List<Integer> brickPositions;

	private BrickAdapter brickAdapter;
	private BrickListView brickListView;

	@Before
	public void setUp() {
		Context context = Mockito.mock(Context.class).getApplicationContext();
		createProject();

		brickAdapter = new BrickAdapter(ProjectManager.getInstance().getCurrentSprite());
		brickListView = Mockito.spy(new BrickListView(context));
		brickListView.setAdapter(brickAdapter);
		View view = Mockito.mock(View.class);

		Mockito.doNothing().when(brickListView).drawItem(Mockito.any(View.class), Mockito.anyBoolean());
		Mockito.doNothing().when(brickListView).invalidate();
		Mockito.doNothing().when(brickListView).invalidateViews();
		Mockito.doReturn(view).when(brickListView).getChildAt(Mockito.anyInt());
	}

	private void createProject() {
		String projectName = "highlightBrickPartsTest";
		Project project = new Project(MockUtil.mockContextForProject(), projectName);

		Sprite sprite = new SingleSprite("sprite");
		project.getDefaultScene().addSprite(sprite);
		Script script = new StartScript();
		script.addBrick(new ForeverBrick());
		script.addBrick(new IfLogicBeginBrick());
		script.addBrick(new IfThenLogicBeginBrick());
		script.addBrick(new IfThenLogicEndBrick(null));
		script.addBrick(new IfLogicElseBrick(null));
		script.addBrick(new IfLogicEndBrick(null, null));
		script.addBrick(new LoopEndBrick());
		sprite.addScript(script);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		new BrickController().setControlBrickReferences(script.getBrickList());
	}

	@Test
	public void testHighlightBrickParts() {
		ControlStructureBrick highlightBrick = (ControlStructureBrick) brickAdapter.getItem(brickPositionToHighlight);
		List<Integer> positions = brickAdapter.getPositionsOfItems(highlightBrick.getAllParts());
		assertEquals(brickPositions, positions);

		brickListView.highlightControlStructureBricks(positions);
		Mockito.verify(brickListView, Mockito.times(positions.size())).drawItem(Mockito.any(View.class),
				Mockito.eq(false));
		assertEquals(positions, brickListView.getBrickPositionsToHighlight());

		assertTrue(brickListView.isCurrentlyHighlighted());

		brickListView.cancelHighlighting();
		assertEquals(0, brickListView.getBrickPositionsToHighlight().size());

		assertFalse(brickListView.isCurrentlyHighlighted());
	}
}
