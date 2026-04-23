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

package org.catrobat.catroid.test.content.controller;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.ScriptBrickBaseType;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BrickControllerTest {

	private Sprite sprite;
	private List<Script> scripts;
	private BrickController brickController;

	@Before
	public void setUp() {
		sprite = mock(Sprite.class);

		scripts = new ArrayList<>();
		when(sprite.getScriptList()).thenReturn(scripts);

		brickController = new BrickController();
	}

	@Test
	public void testCopyFlatListFromSingleScript() throws Exception {
		Brick brickToCopy = spy(new BrickStub());
		Brick copiedBrick = mock(Brick.class);
		when(brickToCopy.clone()).thenReturn(copiedBrick);

		Script script = new ScriptStub();
		scripts.add(script);

		script.addBrick(brickToCopy);

		setParents(scripts);

		brickController
				.copy(Collections.singletonList(brickToCopy), sprite);

		verify(brickToCopy).clone();

		assertEquals(2, script.getBrickList().size());

		assertEquals(0, script.getBrickList().indexOf(brickToCopy));

		assertEquals(1, script.getBrickList().indexOf(copiedBrick));
	}

	@Test
	public void testCopyNestedListFormSingleScript() throws Exception {
		Brick brickBeforeCompositeBrick = spy(new BrickStub());
		Brick copiedBrickBrickBeforeCompositeBrick = mock(Brick.class);
		when(brickBeforeCompositeBrick.clone()).thenReturn(copiedBrickBrickBeforeCompositeBrick);

		CompositeBrick compositeBrick = spy(new CompositeBrickStub());

		Brick brickInCompositeBrick = spy(new BrickStub());
		Brick copiedBrickBrickInCompositeBrick = mock(Brick.class);
		compositeBrick.getNestedBricks().add(brickInCompositeBrick);

		CompositeBrick copiedCompositeBrick = new CompositeBrickStub();
		copiedCompositeBrick.getNestedBricks().add(copiedBrickBrickInCompositeBrick);

		when(compositeBrick.clone()).thenReturn(copiedCompositeBrick);

		Brick brickAfterCompositeBrick = spy(new BrickStub());
		Brick copiedBrickBrickAfterCompositeBrick = mock(Brick.class);
		when(brickAfterCompositeBrick.clone()).thenReturn(copiedBrickBrickAfterCompositeBrick);

		Script script = new ScriptStub();
		scripts.add(script);

		script.addBrick(brickBeforeCompositeBrick);
		script.addBrick(compositeBrick);
		script.addBrick(brickAfterCompositeBrick);

		setParents(scripts);

		List<Brick> bricksToCopy = new ArrayList<>();
		bricksToCopy.add(brickBeforeCompositeBrick);
		bricksToCopy.add(compositeBrick);
		bricksToCopy.add(brickAfterCompositeBrick);

		brickController
				.copy(bricksToCopy, sprite);

		verify(brickBeforeCompositeBrick).clone();
		verify(compositeBrick).clone();
		verify(brickAfterCompositeBrick).clone();

		assertEquals(6, script.getBrickList().size());

		assertEquals(0, script.getBrickList().indexOf(brickBeforeCompositeBrick));
		assertEquals(1, script.getBrickList().indexOf(compositeBrick));
		assertTrue(compositeBrick.getNestedBricks().contains(brickInCompositeBrick));
		assertEquals(2, script.getBrickList().indexOf(brickAfterCompositeBrick));

		assertEquals(3, script.getBrickList().indexOf(copiedBrickBrickBeforeCompositeBrick));
		assertEquals(4, script.getBrickList().indexOf(copiedCompositeBrick));
		assertTrue(copiedCompositeBrick.getNestedBricks().contains(copiedBrickBrickInCompositeBrick));
		assertEquals(5, script.getBrickList().indexOf(copiedBrickBrickAfterCompositeBrick));
	}

	@Test
	public void testCopyFromMultipleScripts() throws Exception {
		Brick brickToCopyInFirstScript = spy(new BrickStub());
		Brick copiedBrickInFirstScript = mock(Brick.class);
		when(brickToCopyInFirstScript.clone()).thenReturn(copiedBrickInFirstScript);

		Brick brickToCopyInSecondScript = spy(new BrickStub());
		Brick copiedBrickInSecondScript = mock(Brick.class);
		when(brickToCopyInSecondScript.clone()).thenReturn(copiedBrickInSecondScript);

		Script firstScript = new ScriptStub();
		scripts.add(firstScript);

		Script secondScript = new ScriptStub();
		scripts.add(secondScript);

		firstScript.addBrick(brickToCopyInFirstScript);
		secondScript.addBrick(brickToCopyInSecondScript);

		setParents(scripts);

		List<Brick> bricksToCopy = new ArrayList<>();
		bricksToCopy.add(brickToCopyInFirstScript);
		bricksToCopy.add(brickToCopyInSecondScript);

		brickController
				.copy(bricksToCopy, sprite);

		verify(brickToCopyInFirstScript).clone();

		assertEquals(2, firstScript.getBrickList().size());

		assertEquals(0, firstScript.getBrickList().indexOf(brickToCopyInFirstScript));

		assertEquals(1, firstScript.getBrickList().indexOf(copiedBrickInFirstScript));

		verify(brickToCopyInSecondScript).clone();

		assertEquals(2, secondScript.getBrickList().size());

		assertEquals(0, secondScript.getBrickList().indexOf(brickToCopyInSecondScript));

		assertEquals(1, secondScript.getBrickList().indexOf(copiedBrickInSecondScript));
	}

	private static class ScriptStub extends Script {

		private ScriptBrick scriptBrick = new ScriptBrickBaseType() {

			@Override
			public Script getScript() {
				return ScriptStub.this;
			}

			@Override
			public int getViewResource() {
				return 0;
			}

			@Override
			public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
			}
		};

		@Override
		public EventId createEventId(Sprite sprite) {
			return null;
		}

		@Override
		public ScriptBrick getScriptBrick() {
			return scriptBrick;
		}
	}

	private static class BrickStub extends BrickBaseType {

		@Override
		public int getViewResource() {
			return 0;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}

	private static class CompositeBrickStub extends BrickBaseType implements CompositeBrick {

		private List<Brick> nestedBricks = new ArrayList<>();

		@Override
		public int getViewResource() {
			return 0;
		}

		@Override
		public boolean hasSecondaryList() {
			return false;
		}

		@Override
		public List<Brick> getNestedBricks() {
			return nestedBricks;
		}

		@Override
		public List<Brick> getSecondaryNestedBricks() {
			return null;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}

	private void setParents(List<Script> scripts) {
		for (Script script : scripts) {
			script.setParents();
		}
	}
}
