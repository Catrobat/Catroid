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

package org.catrobat.catroid.test.ui;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(Parameterized.class)
public class AddBrickFloatingBehaviorTest {

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"FirstAddedIsScriptBrick", 0, scriptBrick, 0},
				{"FirstAddedIsBrick", 0, brick, 0},
				{"SecondAddedIsScriptBrick", 1, scriptBrick, 1},
				{"SecondAddedIsBrick", 1, brick, 0},
				{"ThirdAddedIsScriptBrick", 2, scriptBrick, 1},
				{"ThirdAddedIsBrick", 2, brick, 1},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int alreadyAddedBricksCount;

	@Parameterized.Parameter(2)
	public Brick brickToAdd;

	@Parameterized.Parameter(3)
	public int expectedFloating;

	private ScriptFragment scriptFragmentMock;
	@Mock
	private Sprite spriteMock;
	@Mock
	private BrickAdapter brickAdapterMock;
	@Mock
	private BrickListView brickListViewMock;

	private static Brick brick = new GlideToBrick();
	private static ScriptBrick scriptBrick = new WhenStartedBrick();

	@Before
	public void setUp() throws Exception {
		scriptFragmentMock = Mockito.spy(ScriptFragment.class);
		MockitoAnnotations.initMocks(this);
		Mockito.when(brickAdapterMock.getCount()).thenReturn(alreadyAddedBricksCount);
		Mockito.doNothing().when(brickAdapterMock).addItem(anyInt(), any(Brick.class));
		Mockito.when(spriteMock.getScriptList()).thenReturn(Collections.singletonList(new StartScript()));
	}

	@Test
	public void testAddBrickFloatingBehaviour() {
		scriptFragmentMock.addBrick(brickToAdd, spriteMock, brickAdapterMock, brickListViewMock);
		Mockito.verify(brickListViewMock, Mockito.times(expectedFloating)).startMoving(brickToAdd);
	}
}
