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
package org.catrobat.catroid.test.content;

import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.stage.OnUtteranceCompletedListenerContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@SuppressWarnings("deprecation")
@RunWith(AndroidJUnit4.class)
public class OnUtteranceCompletedListenerContainerTest {

	private List<String> onUtteranceCompletedIds;
	private OnUtteranceCompletedListenerContainer container;

	private final File existingFile = new FileMock(true);
	private final File nonExistingFile = new FileMock(false);

	private final String utteranceId1 = "hash1";
	private final String utteranceId2 = "hash2";

	@Before
	public void setUp() throws Exception {
		container = new OnUtteranceCompletedListenerContainer();
		onUtteranceCompletedIds = new ArrayList<String>();
	}

	@Test
	public void testExistingSpeechFile() {
		final OnUtteranceCompletedListener listener = new OnUtteranceCompletedListenerMock();

		boolean returnValue = container.addOnUtteranceCompletedListener(existingFile, listener, utteranceId1);
		assertFalse(returnValue);
		assertTrue(onUtteranceCompletedIds.contains(utteranceId1));
	}

	@Test
	public void testNonExistingSpeechFile() {
		final OnUtteranceCompletedListener listener = new OnUtteranceCompletedListenerMock();

		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener, utteranceId1);
		assertTrue(returnValue);

		container.onUtteranceCompleted(utteranceId1);
		assertTrue(onUtteranceCompletedIds.contains(utteranceId1));
	}

	@Test
	public void testSpeechFilesWithSameHashValue() {
		final OnUtteranceCompletedListener listener1 = new OnUtteranceCompletedListenerMock();

		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener1, utteranceId1);
		assertTrue(returnValue);

		final OnUtteranceCompletedListener listener2 = new OnUtteranceCompletedListenerMock();
		returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener2, utteranceId1);
		assertFalse(returnValue);

		container.onUtteranceCompleted(utteranceId1);

		assertTrue(onUtteranceCompletedIds.contains(utteranceId1));
		assertEquals(2, onUtteranceCompletedIds.size());
	}

	@Test
	public void testNormalBehavior() {
		final OnUtteranceCompletedListener listener1 = new OnUtteranceCompletedListenerMock();
		final OnUtteranceCompletedListener listener2 = new OnUtteranceCompletedListenerMock();
		final OnUtteranceCompletedListener listener3 = new OnUtteranceCompletedListenerMock();

		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener1, utteranceId1);
		assertTrue(returnValue);

		returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener2, utteranceId2);
		assertTrue(returnValue);

		container.onUtteranceCompleted(utteranceId1);
		assertTrue(onUtteranceCompletedIds.contains(utteranceId1));

		returnValue = container.addOnUtteranceCompletedListener(existingFile, listener3, utteranceId1);
		assertFalse(returnValue);
		assertEquals(onUtteranceCompletedIds.get(1), utteranceId1);

		container.onUtteranceCompleted(utteranceId2);
		assertTrue(onUtteranceCompletedIds.contains(utteranceId2));
	}

	private class OnUtteranceCompletedListenerMock implements OnUtteranceCompletedListener {
		public void onUtteranceCompleted(String utteranceId) {
			onUtteranceCompletedIds.add(utteranceId);
		}
	}

	private class FileMock extends File {
		private static final long serialVersionUID = 1L;
		private boolean exists;

		FileMock(boolean exists) {
			super("");
			this.exists = exists;
		}

		@Override
		public boolean exists() {
			return exists;
		}
	}
}
