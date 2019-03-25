/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import org.catrobat.catroid.stage.OnUtteranceCompletedListenerContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@RunWith(JUnit4.class)
public class OnUtteranceCompletedListenerContainerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private OnUtteranceCompletedListenerContainer container;

	private final String utteranceId1 = "hash1";
	private final String utteranceId2 = "hash2";

	@Mock
	private File nonExistingFile;
	@Mock
	private File existingFile;

	@Before
	public void setUp() throws Exception {
		container = new OnUtteranceCompletedListenerContainer();

		when(nonExistingFile.exists()).thenReturn(false);
		when(existingFile.exists()).thenReturn(true);
	}

	@Test
	public void testExistingSpeechFile() {
		OnUtteranceCompletedListener listener = Mockito.mock(OnUtteranceCompletedListener.class);

		boolean returnValue = container.addOnUtteranceCompletedListener(existingFile, listener, utteranceId1);
		assertFalse(returnValue);
		verify(listener, times(1)).onUtteranceCompleted(utteranceId1);
	}

	@Test
	public void testNonExistingSpeechFile() {
		OnUtteranceCompletedListener listener = Mockito.mock(OnUtteranceCompletedListener.class);

		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener, utteranceId1);
		assertTrue(returnValue);

		container.onUtteranceCompleted(utteranceId1);
		verify(listener, times(1)).onUtteranceCompleted(utteranceId1);
	}

	@Test
	public void testSpeechFilesWithSameHashValue() {
		OnUtteranceCompletedListener listener1 = Mockito.mock(OnUtteranceCompletedListener.class);
		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener1, utteranceId1);
		assertTrue(returnValue);

		OnUtteranceCompletedListener listener2 = Mockito.mock(OnUtteranceCompletedListener.class);
		returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener2, utteranceId1);
		assertFalse(returnValue);

		container.onUtteranceCompleted(utteranceId1);

		verify(listener1, times(1)).onUtteranceCompleted(utteranceId1);
		verify(listener2, times(1)).onUtteranceCompleted(utteranceId1);
	}

	@Test
	public void testNormalBehavior() {
		OnUtteranceCompletedListener listener1 = Mockito.mock(OnUtteranceCompletedListener.class);
		OnUtteranceCompletedListener listener2 = Mockito.mock(OnUtteranceCompletedListener.class);
		OnUtteranceCompletedListener listener3 = Mockito.mock(OnUtteranceCompletedListener.class);

		boolean returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener1, utteranceId1);
		assertTrue(returnValue);

		returnValue = container.addOnUtteranceCompletedListener(nonExistingFile, listener2, utteranceId2);
		assertTrue(returnValue);

		container.onUtteranceCompleted(utteranceId1);
		verify(listener1, times(1)).onUtteranceCompleted(utteranceId1);

		returnValue = container.addOnUtteranceCompletedListener(existingFile, listener3, utteranceId1);
		assertFalse(returnValue);
		verify(listener3, times(1)).onUtteranceCompleted(utteranceId1);

		container.onUtteranceCompleted(utteranceId2);
		verify(listener2, times(1)).onUtteranceCompleted(utteranceId2);
	}
}
