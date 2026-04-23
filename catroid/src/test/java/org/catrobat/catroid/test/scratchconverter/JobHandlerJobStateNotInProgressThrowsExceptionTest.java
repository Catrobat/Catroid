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

package org.catrobat.catroid.test.scratchconverter;

import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.JobHandler;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class JobHandlerJobStateNotInProgressThrowsExceptionTest {

	private static final long JOB_ID = 1;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		ArrayList<Object[]> parameters = new ArrayList<>();
		for (Job.State state : Job.State.values()) {
			if (!state.isInProgress()) {
				parameters.add(new Object[] {state.name(), state});
			}
		}
		return parameters;
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Job.State jobState;

	private Job jobSpy;
	private JobHandler jobHandler;

	@Before
	public void setUp() {
		Job job = new Job(JOB_ID, "My program", null);
		job.setState(jobState);
		jobSpy = spy(job);
		Client.ConvertCallback convertCallbackMock = mock(Client.ConvertCallback.class);
		jobHandler = new JobHandler(jobSpy, convertCallbackMock);
	}

	@Test
	public void testReceivedJobMessageWithWrongJobID() {
		JobMessage message = mock(JobMessage.class);
		when(message.getJobID()).thenReturn(JOB_ID);

		exception.expect(IllegalStateException.class);
		try {
			jobHandler.onJobMessage(message);
		} finally {
			verify(jobSpy, atLeast(0)).getState();
			verify(jobSpy, atLeast(0)).getJobID();
			verifyNoMoreInteractions(jobSpy);
		}
	}
}
