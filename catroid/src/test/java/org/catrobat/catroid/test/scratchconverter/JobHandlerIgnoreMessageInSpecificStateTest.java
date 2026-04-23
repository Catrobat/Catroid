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
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobAlreadyRunningMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFailedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFinishedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobOutputMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobProgressMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobReadyMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobRunningMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;

@RunWith(Parameterized.class)
public class JobHandlerIgnoreMessageInSpecificStateTest {

	private static final long JOB_ID = 1;
	private static final String JOB_TITLE = "My program";
	private static final String JOB_IMAGE_URL = "url";

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"JobReadyMessage READY", new JobReadyMessage(JOB_ID), Job.State.READY},
				{"JobReadyMessage RUNNING", new JobReadyMessage(JOB_ID), Job.State.RUNNING},
				{"JobAlreadyRunningMessage RUNNING", new JobAlreadyRunningMessage(JOB_ID, JOB_TITLE, JOB_IMAGE_URL), Job.State.RUNNING},
				{"JobAlreadyRunningMessage RUNNING", new JobAlreadyRunningMessage(JOB_ID, JOB_TITLE, JOB_IMAGE_URL), Job.State.RUNNING},
				{"JobProgressMessage SCHEDULED", new JobProgressMessage(JOB_ID, (short) 32), Job.State.SCHEDULED},
				{"JobProgressMessage READY", new JobProgressMessage(JOB_ID, (short) 32), Job.State.READY},
				{"JobOutputMessage SCHEDULED", new JobOutputMessage(JOB_ID, new String[] {"line1", "line2"}), Job.State.SCHEDULED},
				{"JobOutputMessage READY", new JobOutputMessage(JOB_ID, new String[] {"line1", "line2"}), Job.State.READY},
				{"JobFailedMessage READY", new JobFailedMessage(JOB_ID, "Error Message"), Job.State.READY},
				{"JobFinishedMessage READY", new JobFinishedMessage(JOB_ID, JOB_IMAGE_URL, new Date()), Job.State.READY},
				{"JobRunningMessage SCHEDULED", new JobRunningMessage(JOB_ID, JOB_TITLE, JOB_IMAGE_URL), Job.State.SCHEDULED},
				{"JobRunningMessage RUNNING", new JobRunningMessage(JOB_ID, JOB_TITLE, JOB_IMAGE_URL), Job.State.RUNNING},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public JobMessage message;

	@Parameterized.Parameter(2)
	public Job.State jobState;

	private JobHandler jobHandler;
	private Client.ConvertCallback convertCallbackMock;
	private Job jobSpy;

	@Before
	public void setUp() {
		convertCallbackMock = mock(Client.ConvertCallback.class);
		Job job = new Job(JOB_ID, "My program", null);
		job.setState(jobState);
		jobSpy = spy(job);
		jobHandler = new JobHandler(jobSpy, convertCallbackMock);
	}

	@Test
	public void testIgnoreMessage() {
		jobHandler.onJobMessage(message);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testJobUnchanged() {
		jobHandler.onJobMessage(message);
		verify(jobSpy, atLeast(0)).getJobID();
		verify(jobSpy, atLeast(0)).getState();
		verifyNoMoreInteractions(jobSpy);
	}
}
