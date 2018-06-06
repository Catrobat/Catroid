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

package org.catrobat.catroid.test.common;

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.common.ThreadScheduler;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.EventThread;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ThreadSchedulerTest {
	private ThreadScheduler scheduler;
	private EventThread threadMock;
	private Actor actor;

	@Before
	public void setUp() {
		actor = new Look(new Sprite());
		scheduler = new ThreadScheduler(actor);
		threadMock = mock(EventThread.class);
		when(threadMock.act(anyFloat())).thenReturn(false);
	}

	@Test
	public void startThreadTest() {
		when(threadMock.act(anyFloat())).thenReturn(false);

		scheduler.startThread(threadMock);
		scheduler.tick(1);

		assertEquals(1, actor.getActions().size);
		assertEquals(threadMock, actor.getActions().get(0));
	}

	@Test
	public void executeThreadOnceTest() {
		when(threadMock.act(anyFloat())).thenReturn(true);
		actor.addAction(threadMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(threadMock, times(1)).act(anyFloat());
	}

	@Test
	public void executeThreadContinuously() {
		when(threadMock.act(anyFloat())).thenReturn(false);
		actor.addAction(threadMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(threadMock, times(3)).act(anyFloat());
	}

	@Test
	public void executeMultipleThreadsWithSameScript() {
		Script scriptMock = mock(Script.class);
		EventThread thread1 = createEventThread(scriptMock);
		EventThread thread2 = createEventThread(scriptMock);
		scheduler.startThread(thread1);
		scheduler.startThread(thread2);

		scheduler.tick(1);

		// thread1 is thrown out when another thread with the same script is started
		verify(thread1, times(0)).act(anyFloat());
		verify(thread2, times(1)).act(anyFloat());
	}

	@Test
	public void stopRunningThreadTest() {
		Script scriptMock = mock(Script.class);
		EventThread thread = createEventThread(scriptMock);
		actor.addAction(thread);

		scheduler.tick(1);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(thread, times(2)).act(anyFloat());
	}

	@Test
	public void stopStartingThreadTest() {
		Script scriptMock = mock(Script.class);
		EventThread thread = createEventThread(scriptMock);

		scheduler.startThread(thread);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);

		verify(thread, times(1)).act(anyFloat());
	}

	@Test
	public void keepThreadRunningWhenOtherIsStoppedTest() {
		Script scriptMock = mock(Script.class);
		EventThread threadToBeStopped = createEventThread(scriptMock);
		EventThread threadThatKeepsRunning = createEventThread(mock(Script.class));
		actor.addAction(threadToBeStopped);
		actor.addAction(threadThatKeepsRunning);

		scheduler.tick(1);
		scheduler.stopThreadsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(threadThatKeepsRunning, times(3)).act(anyFloat());
		verify(threadToBeStopped, times(2)).act(anyFloat());
	}

	private EventThread createEventThread(Script script) {
		EventThread threadMock = Mockito.mock(EventThread.class);
		when(threadMock.getScript()).thenReturn(script);
		when(threadMock.act(anyFloat())).thenReturn(false);
		return threadMock;
	}
}
