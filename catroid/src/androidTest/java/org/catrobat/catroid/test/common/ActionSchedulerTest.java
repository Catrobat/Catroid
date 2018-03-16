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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import junit.framework.Assert;

import org.catrobat.catroid.common.ActionScheduler;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.EventSequenceAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ActionSchedulerTest {
	private ActionScheduler scheduler;
	private Action actionMock;
	private Actor actor;

	@Before
	public void setUp() {
		actor = new Look(new Sprite());
		scheduler = new ActionScheduler(actor);
		actionMock = mock(Action.class);
		when(actionMock.act(anyFloat())).thenReturn(false);
	}

	@Test
	public void startActionTest() {
		when(actionMock.act(anyFloat())).thenReturn(false);

		scheduler.startAction(actionMock);
		scheduler.tick(1);

		Assert.assertEquals("Unexpected number of actions in scheduler", 1, actor.getActions().size);
		Assert.assertEquals("Invalid action in scheduler", actionMock, actor.getActions().get(0));
	}

	@Test
	public void executeActionOnceTest() {
		when(actionMock.act(anyFloat())).thenReturn(true);
		actor.addAction(actionMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(actionMock, times(1)).act(anyFloat());
	}

	@Test
	public void executeActionContinuouslyTest() {
		when(actionMock.act(anyFloat())).thenReturn(false);
		actor.addAction(actionMock);

		scheduler.tick(1);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(actionMock, times(3)).act(anyFloat());
	}

	@Test
	public void stopRunningActionTest() {
		Script scriptMock = mock(Script.class);
		EventSequenceAction eventSequenceActionMock = createEventSequenceActionWithScript(scriptMock);
		actor.addAction(eventSequenceActionMock);

		scheduler.tick(1);
		scheduler.stopActionsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(eventSequenceActionMock, times(2)).act(anyFloat());
	}

	@Test
	public void stopStartedActionTest() {
		Script scriptMock = mock(Script.class);
		EventSequenceAction eventSequenceAction = createEventSequenceActionWithScript(scriptMock);

		scheduler.startAction(eventSequenceAction);
		scheduler.stopActionsWithScript(scriptMock);
		scheduler.tick(1);

		verify(eventSequenceAction, times(0)).act(anyFloat());
	}

	@Test
	public void keepActionRunningWhenOtherIsStoppedTest() {
		Script scriptMock = mock(Script.class);
		EventSequenceAction actionToBeStopped = createEventSequenceActionWithScript(scriptMock);
		EventSequenceAction actionToKeepRunning = createEventSequenceActionWithScript(mock(Script.class));
		actor.addAction(actionToBeStopped);
		actor.addAction(actionToKeepRunning);

		scheduler.stopActionsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(actionToKeepRunning, times(2)).act(anyFloat());
	}

	@Test
	public void stopMultipleStartedActionsTest() {
		Script scriptMock = mock(Script.class);
		EventSequenceAction action1 = createEventSequenceActionWithScript(scriptMock);
		EventSequenceAction action2 = createEventSequenceActionWithScript(scriptMock);
		scheduler.startAction(action1);
		scheduler.startAction(action2);

		scheduler.stopActionsWithScript(scriptMock);
		scheduler.tick(1);

		verify(action1, times(0)).act(anyFloat());
		verify(action2, times(0)).act(anyFloat());
	}

	@Test
	public void stopMultipleRunningActionsTest() {
		Script scriptMock = mock(Script.class);
		EventSequenceAction action1 = createEventSequenceActionWithScript(scriptMock);
		EventSequenceAction action2 = createEventSequenceActionWithScript(scriptMock);
		actor.addAction(action1);
		actor.addAction(action2);

		scheduler.stopActionsWithScript(scriptMock);
		scheduler.tick(1);
		scheduler.tick(1);

		verify(action1, times(1)).act(anyFloat());
		verify(action2, times(1)).act(anyFloat());
	}

	private EventSequenceAction createEventSequenceActionWithScript(Script script) {
		EventSequenceAction eventSequenceActionMock = Mockito.mock(EventSequenceAction.class);
		when(eventSequenceActionMock.getScript()).thenReturn(script);
		when(eventSequenceActionMock.act(anyFloat())).thenReturn(false);
		return eventSequenceActionMock;
	}
}
