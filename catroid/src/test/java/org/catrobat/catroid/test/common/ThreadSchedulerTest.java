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

package org.catrobat.catroid.test.common;

import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.common.ThreadScheduler;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ThreadSchedulerTest {
	private ThreadScheduler scheduler;
	private ScriptSequenceAction sequenceActionMock;
	private Actor actor;

	@Before
	public void setUp() {
		actor = new Look(new Sprite());
		scheduler = new ThreadScheduler(actor);
		sequenceActionMock = mock(ScriptSequenceAction.class);
	}

	@Test
	public void startThreadTest() {
		when(sequenceActionMock.act(anyFloat())).thenReturn(false);

		scheduler.startThread(sequenceActionMock);
		scheduler.tick(1);

		assertEquals(1, actor.getActions().size);
		assertEquals(sequenceActionMock, actor.getActions().get(0));
	}

	@Test
	public void executeThreadOnceTest() {
		when(sequenceActionMock.act(anyFloat())).thenReturn(true);
		actor.addAction(sequenceActionMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(sequenceActionMock, times(1)).act(anyFloat());
	}

	@Test
	public void executeThreadContinuously() {
		when(sequenceActionMock.act(anyFloat())).thenReturn(false);
		actor.addAction(sequenceActionMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(sequenceActionMock, times(3)).act(anyFloat());
	}

	@Test
	public void executeMultipleThreadsWithSameScript() {
		Script scriptMock = mock(Script.class);
		ScriptSequenceAction action1 = createSequenceAction(scriptMock);
		ScriptSequenceAction action2 = createSequenceAction(scriptMock);
		scheduler.startThread(action1);
		scheduler.startThread(action2);

		scheduler.tick(1);

		// thread1 is thrown out when another thread with the same script is started
		verify(action1, times(0)).act(anyFloat());
		verify(action2, times(1)).act(anyFloat());
	}

	@Test
	public void stopRunningThreadTest() {
		Script scriptMock = mock(Script.class);
		ScriptSequenceAction sequenceAction = createSequenceAction(scriptMock);
		actor.addAction(sequenceAction);

		scheduler.tick(1);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(sequenceAction, times(2)).act(anyFloat());
	}

	@Test
	public void stopStartingThreadTest() {
		Script scriptMock = mock(Script.class);
		ScriptSequenceAction sequenceAction = createSequenceAction(scriptMock);

		scheduler.startThread(sequenceAction);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);

		verify(sequenceAction, times(1)).act(anyFloat());
	}

	@Test
	public void keepThreadRunningWhenOtherIsStoppedTest() {
		Script scriptMock = mock(Script.class);
		ScriptSequenceAction actionToBeStopped = createSequenceAction(scriptMock);
		ScriptSequenceAction actionThatKeepsRunning = createSequenceAction(mock(Script.class));
		actor.addAction(actionToBeStopped);
		actor.addAction(actionThatKeepsRunning);

		scheduler.tick(1);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(actionThatKeepsRunning, times(3)).act(anyFloat());
		verify(actionToBeStopped, times(2)).act(anyFloat());
	}

	@Test
	public void doNotExecuteThreadIfStateIsSuspended() {
		when(sequenceActionMock.act(anyFloat())).thenReturn(true);

		scheduler.startThread(sequenceActionMock);
		scheduler.setState(ThreadScheduler.SUSPENDED);
		scheduler.tick(1);

		verify(sequenceActionMock, times(0)).act(anyFloat());
		assertEquals(1, actor.getActions().size);
		assertEquals(sequenceActionMock, actor.getActions().get(0));
	}

	@Test
	public void executeThreadIfStateChangesToRunning() {
		when(sequenceActionMock.act(anyFloat())).thenReturn(false);

		scheduler.startThread(sequenceActionMock);
		scheduler.setState(ThreadScheduler.SUSPENDED);
		scheduler.tick(1);
		scheduler.setState(ThreadScheduler.RUNNING);
		scheduler.tick(1);

		verify(sequenceActionMock, times(1)).act(anyFloat());
	}

	private ScriptSequenceAction createSequenceAction(Script script) {
		ScriptSequenceAction sequenceActionMock = Mockito.mock(ScriptSequenceAction.class);
		when(sequenceActionMock.getScript()).thenReturn(script);
		when(sequenceActionMock.act(anyFloat())).thenReturn(false);
		return sequenceActionMock;
	}
}
