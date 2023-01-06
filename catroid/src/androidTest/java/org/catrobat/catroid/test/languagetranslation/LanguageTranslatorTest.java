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

package org.catrobat.catroid.test.languagetranslation;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModel;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.catrobat.catroid.content.actions.TranslateTextFromToAction;
import org.catrobat.catroid.languagetranslator.LanguageTranslator;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.DownloadLanguageModelDialog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LanguageTranslatorTest {

	private final String undefinedLanguageCode = "und";
	private final String defaultLanguageModel = "en";
	private Context mockedContext;
	private Task<String> mockedTask;
	private StageActivity mockedStageActivity;
	private LanguageIdentifier mockedLanguageIdentifier;
	private RemoteModelManager mockedModelManager;
	private TranslateTextFromToAction.TranslationResult mockedTranslationResult;

	@Before
	public void setUp() throws Exception {
		mockedContext = mock(Context.class);
		mockedTask = mock(Task.class);
		mockedStageActivity = mock(StageActivity.class);
		mockedLanguageIdentifier = mock(LanguageIdentifier.class);
		mockedModelManager = mock(RemoteModelManager.class);
		mockedTranslationResult = mock(TranslateTextFromToAction.TranslationResult.class);

		DownloadLanguageModelDialog mockedDownloadLanguageModelDialog = mock(DownloadLanguageModelDialog.class);
		when(mockedStageActivity.getDownloadLanguageModelDialog()).thenReturn(mockedDownloadLanguageModelDialog);
	}

	@Test
	public void testRemovePostFixFromLanguageCode() {
		LanguageTranslator languageTranslator = spy(new LanguageTranslator("Text",
				"en-US", "de-DE", mockedStageActivity));

		doNothing().when(languageTranslator).checkModelsOnDevice();
		languageTranslator.translate();

		assertEquals("en", languageTranslator.getSourceLanguage());
		assertEquals("de", languageTranslator.getTargetLanguage());

		verify(languageTranslator, times(1)).checkModelsOnDevice();
	}

	@Test
	public void testIdentifyLanguageOnFailureListener() {
		// In the corresponding implementation in the LanguageTranslator class, no parameters
		// that would trigger this Listener were found. Thus we reproduce here the expected
		// behaviour for handling this error.

		String text = "&$/&%%&$&%/$(%%/%";
		LanguageTranslator languageTranslator = new LanguageTranslator(text,
				undefinedLanguageCode, "en", mockedStageActivity);

		languageTranslator.setLanguageIdentifier(mockedLanguageIdentifier);
		when(mockedLanguageIdentifier.identifyLanguage(languageTranslator.getText())).thenReturn(mockedTask);

		String exceptionMessage = "Some exception";
		String result = "Language identification failed with exception: " + exceptionMessage;
		languageTranslator.registerTranslationListener(mockedTranslationResult);
		doNothing().when(mockedTranslationResult).onComplete(result);

		Exception mockedException = mock(Exception.class);
		when(mockedException.getMessage()).thenReturn(exceptionMessage);

		doAnswer(invocation -> {
			OnFailureListener callback = invocation.getArgument(0);
			callback.onFailure(mockedException);
			assertNotNull(mockedException);
			assertEquals(exceptionMessage, mockedException.getMessage());
			return null;
		}).when(mockedTask).addOnFailureListener(any(OnFailureListener.class));

		languageTranslator.identifyLanguage();

		verify(mockedLanguageIdentifier, times(1)).identifyLanguage(eq(text));
		verify(mockedTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));
		verify(mockedException, times(2)).getMessage();
	}

	@Test
	public void testIdentifyLanguageOnSuccessListenerLanguageIdentifiedWithUndInSourceLanguage() {
		String textToTranslate = "Text in some unidentified language";
		LanguageTranslator languageTranslator = spy(new LanguageTranslator(textToTranslate,
				undefinedLanguageCode, "target language", mockedStageActivity));

		languageTranslator.setLanguageIdentifier(mockedLanguageIdentifier);
		when(mockedLanguageIdentifier.identifyLanguage(languageTranslator.getText())).thenReturn(mockedTask);

		String response = "Some language code different from 'und'";
		doAnswer(invocation -> {
			OnSuccessListener<String> callback = invocation.getArgument(0);
			callback.onSuccess(response);
			assertNotNull("Response is not null", response);
			assertNotEquals(undefinedLanguageCode, response);
			assertEquals(response, languageTranslator.getSourceLanguage());
			assertTrue(languageTranslator.getIdentifiedLanguage().get());
			return null;
		}).when(mockedTask).addOnSuccessListener(any(OnSuccessListener.class));

		languageTranslator.translate();

		verify(mockedLanguageIdentifier, times(1)).identifyLanguage(eq(textToTranslate));
		verify(mockedTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
	}

	@Test
	public void testIdentifyLanguageOnCompleteListenerLanguageNotIdentifiedWithEmptySourceLanguage() {
		String text = "&$/&%%&$&%/$(%%/%";
		LanguageTranslator languageTranslator = new LanguageTranslator(text,
				"", "de", mockedStageActivity);

		languageTranslator.setLanguageIdentifier(mockedLanguageIdentifier);
		when(mockedLanguageIdentifier.identifyLanguage(languageTranslator.getText())).thenReturn(mockedTask);

		String message = "The source language couldn't be identified.";
		languageTranslator.registerTranslationListener(mockedTranslationResult);
		doNothing().when(mockedTranslationResult).onComplete(message);

		Task<String> mockedTaskResult = mock(Task.class);
		when(mockedTaskResult.isSuccessful()).thenReturn(true);

		String response = "und";
		doAnswer(invocation -> {
			OnSuccessListener<String> callback = invocation.getArgument(0);
			callback.onSuccess(response);
			assertNotNull("Response is not null", response);
			assertEquals(undefinedLanguageCode, response);
			assertEquals("", languageTranslator.getSourceLanguage());
			assertFalse(languageTranslator.getIdentifiedLanguage().get());
			return null;
		}).when(mockedTask).addOnSuccessListener(any(OnSuccessListener.class));

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedTaskResult);
			assertTrue(mockedTaskResult.isSuccessful());
			return null;
		}).when(mockedTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();

		verify(mockedLanguageIdentifier, times(1)).identifyLanguage(eq(text));
		verify(mockedTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
		verify(mockedTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedTaskResult, times(2)).isSuccessful();
		verify(mockedTranslationResult, times(1)).onComplete(eq(message));
	}

	@Test
	public void testLanguageIdentifierOnCompleteListenerLanguageIdentified() {
		String textToTranslate = "Some text in some language";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator(textToTranslate,
				undefinedLanguageCode, "target language", mockedStageActivity));

		languageTranslatorSpy.setLanguageIdentifier(mockedLanguageIdentifier);
		when(mockedLanguageIdentifier.identifyLanguage(languageTranslatorSpy.getText())).thenReturn(mockedTask);

		languageTranslatorSpy.setModelManager(mockedModelManager);
		Set<TranslateRemoteModel> mockedModelsOnDevice = mock(Set.class);

		Task<String> mockedTaskResult = mock(Task.class);
		when(mockedTaskResult.isSuccessful()).thenReturn(true);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		String response = "Some language code different from 'und'";
		doAnswer(invocation -> {
			OnSuccessListener<String> callback = invocation.getArgument(0);
			callback.onSuccess(response);
			assertNotNull("Response is not null", response);
			assertNotEquals(undefinedLanguageCode, response);
			assertEquals(response, languageTranslatorSpy.getSourceLanguage());
			assertTrue(languageTranslatorSpy.getIdentifiedLanguage().get());
			return null;
		}).when(mockedTask).addOnSuccessListener(any(OnSuccessListener.class));

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedTaskResult);
			assertTrue(mockedTaskResult.isSuccessful());
			return null;
		}).when(mockedTask).addOnCompleteListener(any(OnCompleteListener.class));

		doAnswer(invocation -> {
			OnSuccessListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onSuccess(mockedModelsOnDevice);
			assertNotNull(mockedModelsOnDevice);
			return null;
		}).when(mockedGetModelsTask).addOnSuccessListener(any(OnSuccessListener.class));

		languageTranslatorSpy.identifyLanguage();

		verify(mockedLanguageIdentifier, times(1)).identifyLanguage(eq(textToTranslate));
		verify(mockedTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
		verify(mockedTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedTaskResult, times(2)).isSuccessful();
		verify(languageTranslatorSpy, times(1)).checkModelsOnDevice();
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
	}

	@Test
	public void testLanguageIdentifierOnCompleteListenerLanguageUnidentified() {
		String textToTranslate = "ahdfkahdkfhald";
		LanguageTranslator languageTranslator = new LanguageTranslator(textToTranslate,
				undefinedLanguageCode, "target language", mockedStageActivity);

		languageTranslator.setLanguageIdentifier(mockedLanguageIdentifier);
		when(mockedLanguageIdentifier.identifyLanguage(languageTranslator.getText())).thenReturn(mockedTask);

		String result = "The source language couldn't be identified.";
		doNothing().when(mockedTranslationResult).onComplete(result);
		languageTranslator.registerTranslationListener(mockedTranslationResult);

		Task<String> mockedTaskResult = mock(Task.class);
		when(mockedTaskResult.isSuccessful()).thenReturn(true);

		String response = undefinedLanguageCode;
		doAnswer(invocation -> {
			OnSuccessListener<String> callback = invocation.getArgument(0);
			callback.onSuccess(response);
			assertNotNull("Response is not null", response);
			assertEquals(undefinedLanguageCode, response);
			assertEquals(undefinedLanguageCode, languageTranslator.getSourceLanguage());
			return null;
		}).when(mockedTask).addOnSuccessListener(any(OnSuccessListener.class));

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedTaskResult);
			assertTrue(mockedTaskResult.isSuccessful());
			assertFalse(languageTranslator.getIdentifiedLanguage().get());
			return null;
		}).when(mockedTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.identifyLanguage();

		verify(mockedLanguageIdentifier, times(1)).identifyLanguage(eq(textToTranslate));
		verify(mockedTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
		verify(mockedTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedTaskResult, times(2)).isSuccessful();
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnSuccessListenerDefaultLanguageModelAlwaysOnDevice() {
		LanguageTranslator languageTranslator = new LanguageTranslator("Some text in english",
				defaultLanguageModel, "de", mockedStageActivity);

		languageTranslator.setModelManager(mockedModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();
		TranslateRemoteModel mockedEnglishModel = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedEnglishModel);
		when(mockedEnglishModel.getLanguage()).thenReturn(defaultLanguageModel);

		doAnswer(invocation -> {
			OnSuccessListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onSuccess(modelsOnDevice);
			assertNotNull(modelsOnDevice);
			assertEquals(modelsOnDevice.size(), 1);

			TranslateRemoteModel translateRemoteModel = modelsOnDevice.iterator().next();
			assertEquals(translateRemoteModel.getLanguage(), defaultLanguageModel);
			return null;
		}).when(mockedGetModelsTask).addOnSuccessListener(any(OnSuccessListener.class));

		languageTranslator.translate();
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnFailureListener() {
		LanguageTranslator languageTranslator = new LanguageTranslator("Some text in english",
				defaultLanguageModel, "de", mockedStageActivity);

		languageTranslator.setModelManager(mockedModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Exception mockedException = mock(Exception.class);
		when(mockedException.getMessage()).thenReturn("Some exception");

		doAnswer(invocation -> {
			OnFailureListener callback = invocation.getArgument(0);
			callback.onFailure(mockedException);
			assertNotNull(mockedException);
			assertEquals("Some exception", mockedException.getMessage());
			return null;
		}).when(mockedGetModelsTask).addOnFailureListener(any(OnFailureListener.class));

		languageTranslator.translate();

		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
		verify(mockedException, times(2)).getMessage();
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnCompleteListenerTaskSuccessfulSameSourceAndTargetLanguage() {
		String text = "Text";
		LanguageTranslator languageTranslator = new LanguageTranslator(text,
				defaultLanguageModel, defaultLanguageModel, mockedStageActivity);

		languageTranslator.setModelManager(mockedModelManager);
		languageTranslator.registerTranslationListener(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();

		TranslateRemoteModel mockedTranslateRemoteModel = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModel);

		when(mockedResult.isSuccessful()).thenReturn(true);
		when(mockedResult.getResult()).thenReturn(modelsOnDevice);
		when(mockedTranslateRemoteModel.getLanguage()).thenReturn(defaultLanguageModel);

		doNothing().when(mockedTranslationResult).onComplete(text);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());

			Set<TranslateRemoteModel> result = mockedResult.getResult();
			assertFalse(result.isEmpty());
			assertEquals(result.size(), 1);

			TranslateRemoteModel translateRemoteModel = result.iterator().next();
			assertEquals(translateRemoteModel.getLanguage(), defaultLanguageModel);

			assertNotNull(languageTranslator.getModelsOnDevice());
			assertEquals(1, languageTranslator.getModelsOnDevice().size());
			assertEquals(defaultLanguageModel, languageTranslator.getModelsOnDevice().get(0));

			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();

		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedResult, times(3)).getResult();
		verify(mockedTranslationResult, times(1)).onComplete(eq(text));
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnCompleteListenerTaskSuccessfulTranslateText() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		String text = "Bienvenidos";
		LanguageTranslator languageTranslator = spy(new LanguageTranslator(text,
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslator.setModelManager(mockedModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();

		TranslateRemoteModel mockedTranslateRemoteModelEn = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModelEn);

		TranslateRemoteModel mockedTranslateRemoteModelEs = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModelEs);

		TranslateRemoteModel mockedTranslateRemoteModelDe = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModelDe);

		when(mockedResult.isSuccessful()).thenReturn(true);
		when(mockedResult.getResult()).thenReturn(modelsOnDevice);
		when(mockedTranslateRemoteModelEn.getLanguage()).thenReturn(defaultLanguageModel);
		when(mockedTranslateRemoteModelEs.getLanguage()).thenReturn(sourceLanguage);
		when(mockedTranslateRemoteModelDe.getLanguage()).thenReturn(targetLanguage);

		doNothing().when(languageTranslator).translateText();

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());

			Set<TranslateRemoteModel> result = mockedResult.getResult();
			assertFalse(result.isEmpty());
			assertEquals(result.size(), 3);

			assertNotNull(languageTranslator.getModelsOnDevice());
			assertEquals(3, languageTranslator.getModelsOnDevice().size());

			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();

		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedResult, times(3)).getResult();
		verify(languageTranslator, times(1)).translateText();
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnCompleteListenerTaskSuccessfulSetTranslationResult() {
		String targetLanguage = "de";
		LanguageTranslator languageTranslator = spy(new LanguageTranslator("Some text in english",
				defaultLanguageModel, targetLanguage, mockedStageActivity));

		languageTranslator.setModelManager(mockedModelManager);
		languageTranslator.registerTranslationListener(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();

		TranslateRemoteModel mockedTranslateRemoteModel = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModel);

		when(mockedResult.isSuccessful()).thenReturn(true);
		when(mockedResult.getResult()).thenReturn(modelsOnDevice);
		when(mockedTranslateRemoteModel.getLanguage()).thenReturn(defaultLanguageModel);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		doNothing().when(mockedStageActivity).manageDownloadLanguageModels();

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		Bundle extras = new Bundle();
		String key = "languageCode";
		extras.putString(key, targetLanguage);

		Notification notification = new Notification();
		notification.extras = extras;

		StatusBarNotification mockedStatusBarNotification = mock(StatusBarNotification.class);
		when(mockedStatusBarNotification.getNotification()).thenReturn(notification);
		when(mockedStatusBarNotification.getId()).thenReturn(1);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[1];
		statusBarNotifications[0] = mockedStatusBarNotification;
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		String translationResult = "Missing language model(s) to translate " + "'" + languageTranslator.getText() + "'";
		doNothing().when(mockedTranslationResult).onComplete(translationResult);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());

			Set<TranslateRemoteModel> result = mockedResult.getResult();
			assertFalse(result.isEmpty());
			assertEquals(result.size(), 1);

			TranslateRemoteModel translateRemoteModel = result.iterator().next();
			assertEquals(translateRemoteModel.getLanguage(), defaultLanguageModel);

			assertNotNull(languageTranslator.getModelsOnDevice());
			assertEquals(1, languageTranslator.getModelsOnDevice().size());
			assertEquals(defaultLanguageModel, languageTranslator.getModelsOnDevice().get(0));

			assertNotNull(languageTranslator.getModelsToDownload());
			assertNotNull(languageTranslator.getCanceledModels());
			assertEquals(0, languageTranslator.getModelsToDownload().size());
			assertEquals(0, languageTranslator.getCanceledModels().size());

			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();

		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedResult, times(3)).getResult();
		verify(languageTranslator, times(1)).showPopupDownloadDialog();
		verify(languageTranslator, times(1)).getActiveNotifications();
		verify(languageTranslator, times(1)).trackActiveNotifications();
		verify(languageTranslator, times(1)).setTranslationResult();
		verify(mockedTranslationResult, times(1)).onComplete(eq(translationResult));
		verify(mockedStageActivity, times(0)).manageDownloadLanguageModels();
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnCompleteListenerTaskSuccessfulShowPopupDownloadDialog() {
		String targetLanguage = "de";
		LanguageTranslator languageTranslator = spy(new LanguageTranslator("Some text in english",
				defaultLanguageModel, targetLanguage, mockedStageActivity));

		languageTranslator.setModelManager(mockedModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();
		TranslateRemoteModel mockedTranslateRemoteModel = mock(TranslateRemoteModel.class);
		modelsOnDevice.add(mockedTranslateRemoteModel);

		when(mockedResult.isSuccessful()).thenReturn(true);
		when(mockedResult.getResult()).thenReturn(modelsOnDevice);
		when(mockedTranslateRemoteModel.getLanguage()).thenReturn(defaultLanguageModel);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		doNothing().when(mockedStageActivity).manageDownloadLanguageModels();

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());

			Set<TranslateRemoteModel> result = mockedResult.getResult();
			assertFalse(result.isEmpty());
			assertEquals(result.size(), 1);

			TranslateRemoteModel translateRemoteModel = result.iterator().next();
			assertEquals(translateRemoteModel.getLanguage(), defaultLanguageModel);

			assertNotNull(languageTranslator.getModelsOnDevice());
			assertEquals(1, languageTranslator.getModelsOnDevice().size());
			assertEquals(defaultLanguageModel, languageTranslator.getModelsOnDevice().get(0));

			assertNotNull(languageTranslator.getModelsToDownload());
			assertNotNull(languageTranslator.getCanceledModels());
			assertEquals(1, languageTranslator.getModelsToDownload().size());
			assertEquals(1, languageTranslator.getCanceledModels().size());
			assertEquals(targetLanguage, languageTranslator.getModelsToDownload().get(0));
			assertEquals(targetLanguage, languageTranslator.getCanceledModels().entrySet().iterator().next().getKey());
			assertFalse(languageTranslator.getCanceledModels().entrySet().iterator().next().getValue());

			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();

		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedResult, times(3)).getResult();
		verify(languageTranslator, times(1)).showPopupDownloadDialog();
		verify(languageTranslator, times(1)).getActiveNotifications();
		verify(languageTranslator, times(1)).trackActiveNotifications();
		verify(mockedStageActivity, times(1)).manageDownloadLanguageModels();
	}

	@Test
	public void testRemoteModelManagerCheckModelsOnDeviceOnCompleteListenerTaskUnsuccessful() {
		LanguageTranslator languageTranslator = new LanguageTranslator("Some text in english",
				defaultLanguageModel, "de", mockedStageActivity);

		languageTranslator.setModelManager(mockedModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		when(mockedResult.isSuccessful()).thenReturn(false);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertFalse(mockedResult.isSuccessful());
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslator.translate();
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);
		verify(mockedGetModelsTask, times(1)).addOnCompleteListener(any(OnCompleteListener.class));
		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedResult, times(0)).getResult();
		verify(mockedStageActivity, times(0)).manageDownloadLanguageModels();
	}

	@Test
	public void testDownloadLanguageModelsLatter() {
		LanguageTranslator languageTranslator = spy(new LanguageTranslator("Some text in english",
				defaultLanguageModel, "de", mockedStageActivity));

		String result = "Missing language model(s) to translate " + "'" + languageTranslator.getText() + "'";
		doNothing().when(mockedTranslationResult).onComplete(result);

		languageTranslator.registerTranslationListener(mockedTranslationResult);
		languageTranslator.checkIfDownloadLanguageModels(false);

		verify(languageTranslator, times(1)).setTranslationResult();
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));
	}

	@Test
	public void testDownloadLanguageModelsNowOnSuccess() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageModels.add(targetLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Task<Void> mockedDownloadTask = mock(Task.class);

		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class)))
				.thenReturn(mockedDownloadTask);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class),
				any(NotificationManagerCompat.class));

		doAnswer(invocation -> {
			OnSuccessListener<Void> callback = invocation.getArgument(0);
			callback.onSuccess(any(Void.class));
			return null;
		}).when(mockedDownloadTask).addOnSuccessListener(any(OnSuccessListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(2)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));
		verify(languageTranslatorSpy, times(2)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(mockedDownloadTask, times(2)).addOnSuccessListener(any(OnSuccessListener.class));
	}

	@Test
	public void testDownloadLanguageModelsNowOnCancelListener() {
		// Google's ML Kit API doesn't seem to support canceling an ongoing download for a language
		// model, thus we only test here the OnCanceledListener setting a flag.

		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageModels.add(targetLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Task<Void> mockedDownloadTask = mock(Task.class);

		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class))).thenReturn(mockedDownloadTask);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		doAnswer(invocation -> {
			OnCanceledListener callback = invocation.getArgument(0);
			callback.onCanceled();
			assertTrue(languageTranslatorSpy.getTaskCanceled().get());
			return null;
		}).when(mockedDownloadTask).addOnCanceledListener(any(OnCanceledListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(2)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));
		verify(languageTranslatorSpy, times(2)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(mockedDownloadTask, times(2)).addOnCanceledListener(any(OnCanceledListener.class));
	}

	@Test
	public void testDownloadLanguageModelsNowOnFailureWhenCancelActionFromUser() {
		String sourceLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Hallo",
				sourceLanguage, defaultLanguageModel, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		DownloadManager mockedDownloadManager = mock(DownloadManager.class);
		languageTranslatorSpy.setDownloadManager(mockedDownloadManager);

		Cursor mockedCursor = mock(Cursor.class);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Map<String, Boolean> canceledModels = new ArrayMap<>();
		canceledModels.put(sourceLanguage, true);
		languageTranslatorSpy.setCanceledModels(canceledModels);

		Task<Void> mockedDownloadTask = mock(Task.class);

		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class)))
				.thenReturn(mockedDownloadTask);

		when(mockedDownloadManager.query(any())).thenReturn(mockedCursor);
		when(mockedCursor.getCount()).thenReturn(0);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();
		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterConnectivityChangeBroadcastReceiver();

		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class),
				any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).createCanceledDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).deleteNotificationOnCancelActionFromUser(any(String.class),
				any(NotificationManagerCompat.class));

		Task<Void> mockedOnCompleteResult = mock(Task.class);
		when(mockedOnCompleteResult.isSuccessful()).thenReturn(false);

		String exceptionMessage = "No existing model file";
		Exception mockedException = mock(Exception.class);
		when(mockedException.getMessage()).thenReturn(exceptionMessage);

		doAnswer(invocation -> {
			OnFailureListener callback = invocation.getArgument(0);
			callback.onFailure(mockedException);
			assertNotNull(mockedException);
			assertEquals(mockedException.getMessage(), exceptionMessage);
			return null;
		}).when(mockedDownloadTask).addOnFailureListener(any(OnFailureListener.class));

		doAnswer(invocation -> {
			OnCompleteListener<Void> callback = invocation.getArgument(0);
			callback.onComplete(mockedOnCompleteResult);
			assertNotNull(mockedOnCompleteResult);
			assertFalse(mockedOnCompleteResult.isSuccessful());
			return null;
		}).when(mockedDownloadTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		assertEquals(3, languageTranslatorSpy.getCanceledNotificationId().intValue());
		assertNotNull(languageTranslatorSpy.getCanceledModels());
		assertEquals(1, languageTranslatorSpy.getCanceledModels().size());

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(1)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));

		verify(languageTranslatorSpy, times(1)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(1)).createCanceledDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(1)).unregisterCancelDownloadBroadcastReceiver();
		verify(languageTranslatorSpy, times(1)).unregisterConnectivityChangeBroadcastReceiver();

		verify(mockedException, times(1)).getMessage();
	}

	@Test
	public void testDownloadLanguageModelsNowOnFailureAPIError() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageModels.add(targetLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Map<String, Boolean> canceledLanguages = new ArrayMap<>();
		canceledLanguages.put(sourceLanguage, false);
		canceledLanguages.put(targetLanguage, false);
		languageTranslatorSpy.setCanceledModels(canceledLanguages);

		Task<Void> mockedDownloadTask = mock(Task.class);

		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class))).thenReturn(mockedDownloadTask);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).checkModelsOnDevice();
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();

		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).updateNotificationOnDownloadError(any(String.class),
				any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).deleteNotificationOnDownloadCompleteOrError(any(String.class),
				any(NotificationManagerCompat.class));

		String exceptionMessage = "No existing model file";
		Exception mockedException = mock(Exception.class);
		when(mockedException.getMessage()).thenReturn(exceptionMessage);

		doAnswer(invocation -> {
			OnFailureListener callback = invocation.getArgument(0);
			callback.onFailure(mockedException);
			assertNotNull(mockedException);
			assertEquals(mockedException.getMessage(), exceptionMessage);
			return null;
		}).when(mockedDownloadTask).addOnFailureListener(any(OnFailureListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(2)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));

		verify(languageTranslatorSpy, times(2)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(2)).updateNotificationOnDownloadError(any(String.class),
				any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		verify(mockedException, times(4)).getMessage();
	}

	@Test
	public void testDownloadLanguageModelsNowOnCompleteSuccess() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageModels.add(targetLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Task<Void> mockedDownloadTask = mock(Task.class);

		mockedTranslationResult = mock(TranslateTextFromToAction.TranslationResult.class);
		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class))).thenReturn(mockedDownloadTask);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();
		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterConnectivityChangeBroadcastReceiver();

		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).updateNotificationOnDownloadComplete(any(String.class),
				any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		doNothing().when(languageTranslatorSpy).deleteNotificationOnDownloadCompleteOrError(any(String.class),
				any(NotificationManagerCompat.class));

		Task<Void> mockedOnCompleteResult = mock(Task.class);
		when(mockedOnCompleteResult.isSuccessful()).thenReturn(true);

		doAnswer(invocation -> {
			OnCompleteListener<Void> callback = invocation.getArgument(0);
			callback.onComplete(mockedOnCompleteResult);
			assertNotNull(mockedOnCompleteResult);
			assertTrue(mockedOnCompleteResult.isSuccessful());
			return null;
		}).when(mockedDownloadTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		assertEquals(0, languageTranslatorSpy.getDownloadCounter().get());

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(2)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedOnCompleteResult, times(4)).isSuccessful();
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));

		verify(languageTranslatorSpy, times(2)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(2)).updateNotificationOnDownloadComplete(any(String.class),
				any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(1)).unregisterCancelDownloadBroadcastReceiver();
		verify(languageTranslatorSpy, times(1)).unregisterConnectivityChangeBroadcastReceiver();
	}

	@Test
	public void testDownloadLanguageModelsNowOnCompleteFail() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		List<String> languageModels = new ArrayList<>();
		languageModels.add(sourceLanguage);
		languageModels.add(targetLanguage);
		languageTranslatorSpy.setModelsToDownload(languageModels);

		Task<Void> mockedDownloadTask = mock(Task.class);

		mockedTranslationResult = mock(TranslateTextFromToAction.TranslationResult.class);
		String result = "Missing language model(s) to translate " + "'" + languageTranslatorSpy.getText() + "'";
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResult);

		when(mockedStageActivity.getApplicationContext()).thenReturn(mockedContext);
		when(mockedModelManager.download(any(RemoteModel.class), any(DownloadConditions.class))).thenReturn(mockedDownloadTask);

		NotificationManager mockedNotificationManager = mock(NotificationManager.class);
		when(mockedContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockedNotificationManager);

		StatusBarNotification[] statusBarNotifications = new StatusBarNotification[0];
		when(mockedNotificationManager.getActiveNotifications()).thenReturn(statusBarNotifications);

		doNothing().when(mockedTranslationResult).onComplete(result);
		doNothing().when(languageTranslatorSpy).removePreviousDownloads();

		doNothing().when(languageTranslatorSpy).registerCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).registerConnectivityChangeBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterCancelDownloadBroadcastReceiver();
		doNothing().when(languageTranslatorSpy).unregisterConnectivityChangeBroadcastReceiver();

		doNothing().when(languageTranslatorSpy).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));

		Task<Void> mockedOnCompleteResult = mock(Task.class);
		when(mockedOnCompleteResult.isSuccessful()).thenReturn(false);

		doAnswer(invocation -> {
			OnCompleteListener<Void> callback = invocation.getArgument(0);
			callback.onComplete(mockedOnCompleteResult);
			assertNotNull(mockedOnCompleteResult);
			assertFalse(mockedOnCompleteResult.isSuccessful());
			return null;
		}).when(mockedDownloadTask).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslatorSpy.checkIfDownloadLanguageModels(true);

		verify(mockedStageActivity, times(2)).getApplicationContext();
		verify(mockedModelManager, times(2)).download(any(RemoteModel.class), any(DownloadConditions.class));
		verify(mockedOnCompleteResult, times(4)).isSuccessful();
		verify(mockedTranslationResult, times(1)).onComplete(eq(result));

		verify(languageTranslatorSpy, times(2)).createDownloadNotification(any(Context.class),
				any(String.class), any(Integer.class), any(NotificationCompat.Builder.class), any(NotificationManagerCompat.class));
		verify(languageTranslatorSpy, times(1)).unregisterCancelDownloadBroadcastReceiver();
		verify(languageTranslatorSpy, times(1)).unregisterConnectivityChangeBroadcastReceiver();
	}

	@Test
	public void testTranslateTextOnCompleteListenerSuccess() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		TranslatorOptions mockedTranslatorOptions = mock(TranslatorOptions.class);
		languageTranslatorSpy.setTranslatorOptions(mockedTranslatorOptions);

		Translator mockedTranslator = mock(Translator.class);
		languageTranslatorSpy.setTranslationModel(mockedTranslator);
		doNothing().when(mockedTranslator).close();

		Task<String> mockedTranslationResult = mock(Task.class);
		when(mockedTranslator.translate(languageTranslatorSpy.getText())).thenReturn(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		TranslateRemoteModel mockedFirstLanguage = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedSecondLanguage = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedThirdLanguage = mock(TranslateRemoteModel.class);
		Set<TranslateRemoteModel> result = new HashSet<>();
		result.add(mockedFirstLanguage);
		result.add(mockedSecondLanguage);
		result.add(mockedThirdLanguage);

		when(mockedFirstLanguage.getLanguage()).thenReturn(sourceLanguage);
		when(mockedSecondLanguage.getLanguage()).thenReturn(targetLanguage);
		when(mockedThirdLanguage.getLanguage()).thenReturn(defaultLanguageModel);
		when(mockedResult.getResult()).thenReturn(result);
		when(mockedResult.isSuccessful()).thenReturn(true);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		Task<String> mockedResultTextTranslation = mock(Task.class);
		when(mockedResultTextTranslation.isSuccessful()).thenReturn(true);

		String translation = "Herzlich Willkommen";
		when(mockedResultTextTranslation.getResult()).thenReturn(translation);

		TranslateTextFromToAction.TranslationResult mockedTranslationResultFromToAction =
				mock(TranslateTextFromToAction.TranslationResult.class);
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResultFromToAction);
		doNothing().when(mockedTranslationResultFromToAction).onComplete(translation);

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedResultTextTranslation);
			assertNotNull(mockedResultTextTranslation);
			assertTrue(mockedResultTextTranslation.isSuccessful());
			assertEquals(mockedResultTextTranslation.getResult(), translation);
			return null;
		}).when(mockedTranslationResult).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslatorSpy.translate();

		verify(languageTranslatorSpy, times(1)).translate();
		verify(mockedTranslator, times(1)).close();
		verify(mockedTranslator, times(1)).translate(languageTranslatorSpy.getText());
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);

		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedFirstLanguage, times(1)).getLanguage();
		verify(mockedSecondLanguage, times(1)).getLanguage();
		verify(mockedThirdLanguage, times(1)).getLanguage();

		verify(mockedResultTextTranslation, times(2)).isSuccessful();
		verify(mockedResultTextTranslation, times(2)).getResult();

		verify(mockedTranslationResultFromToAction, times(1)).onComplete(eq(translation));
	}

	@Test
	public void testTranslateTextOnCompleteListenerFail() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		TranslatorOptions mockedTranslatorOptions = mock(TranslatorOptions.class);
		languageTranslatorSpy.setTranslatorOptions(mockedTranslatorOptions);

		Translator mockedTranslator = mock(Translator.class);
		languageTranslatorSpy.setTranslationModel(mockedTranslator);
		doNothing().when(mockedTranslator).close();

		Task<String> mockedTranslationResult = mock(Task.class);
		when(mockedTranslator.translate(languageTranslatorSpy.getText())).thenReturn(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		TranslateRemoteModel mockedFirstLanguage = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedSecondLanguage = mock(TranslateRemoteModel.class);
		Set<TranslateRemoteModel> result = new HashSet<>();
		result.add(mockedFirstLanguage);
		result.add(mockedSecondLanguage);

		when(mockedFirstLanguage.getLanguage()).thenReturn(sourceLanguage);
		when(mockedSecondLanguage.getLanguage()).thenReturn(targetLanguage);
		when(mockedResult.getResult()).thenReturn(result);
		when(mockedResult.isSuccessful()).thenReturn(true);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		Task<String> mockedResultTextTranslation = mock(Task.class);
		when(mockedResultTextTranslation.isSuccessful()).thenReturn(false);

		Exception mockedException = mock(Exception.class);
		when(mockedException.getMessage()).thenReturn("Some exception");
		when(mockedResultTextTranslation.getException()).thenReturn(mockedException);

		String error = "Unable to complete translation because of "
				+ Objects.requireNonNull(mockedResultTextTranslation.getException(), "exception must not be null")
				.getMessage();

		TranslateTextFromToAction.TranslationResult mockedTranslationResultFromToAction =
				mock(TranslateTextFromToAction.TranslationResult.class);
		languageTranslatorSpy.registerTranslationListener(mockedTranslationResultFromToAction);
		doNothing().when(mockedTranslationResultFromToAction).onComplete(error);

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedResultTextTranslation);
			assertNotNull(mockedResultTextTranslation);
			assertFalse(mockedResultTextTranslation.isSuccessful());
			return null;
		}).when(mockedTranslationResult).addOnCompleteListener(any(OnCompleteListener.class));

		languageTranslatorSpy.translate();

		verify(languageTranslatorSpy, times(1)).translate();
		verify(mockedTranslator, times(1)).close();
		verify(mockedTranslator, times(1)).translate(languageTranslatorSpy.getText());
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);

		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedFirstLanguage, times(1)).getLanguage();
		verify(mockedSecondLanguage, times(1)).getLanguage();

		verify(mockedResultTextTranslation, times(2)).isSuccessful();
		verify(mockedTranslationResultFromToAction, times(1)).onComplete(error);
	}

	@Test
	public void testTranslateTextOnSuccessListener() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		TranslatorOptions mockedTranslatorOptions = mock(TranslatorOptions.class);
		languageTranslatorSpy.setTranslatorOptions(mockedTranslatorOptions);

		Translator mockedTranslator = mock(Translator.class);
		languageTranslatorSpy.setTranslationModel(mockedTranslator);
		doNothing().when(mockedTranslator).close();

		Task<String> mockedTranslationResult = mock(Task.class);
		when(mockedTranslator.translate(languageTranslatorSpy.getText())).thenReturn(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		TranslateRemoteModel mockedFirstLanguage = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedSecondLanguage = mock(TranslateRemoteModel.class);
		Set<TranslateRemoteModel> result = new HashSet<>();
		result.add(mockedFirstLanguage);
		result.add(mockedSecondLanguage);

		when(mockedFirstLanguage.getLanguage()).thenReturn(sourceLanguage);
		when(mockedSecondLanguage.getLanguage()).thenReturn(targetLanguage);
		when(mockedResult.getResult()).thenReturn(result);
		when(mockedResult.isSuccessful()).thenReturn(true);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		String translation = "Herzlich Willkommen";
		doAnswer(invocation -> {
			OnSuccessListener<String> callback = invocation.getArgument(0);
			callback.onSuccess(eq(translation));
			assertNotNull(translation);
			assertEquals(translation, "Herzlich Willkommen");
			return null;
		}).when(mockedTranslationResult).addOnSuccessListener(any(OnSuccessListener.class));

		languageTranslatorSpy.translate();

		verify(languageTranslatorSpy, times(1)).translate();
		verify(mockedTranslator, times(1)).translate(languageTranslatorSpy.getText());
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);

		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedFirstLanguage, times(1)).getLanguage();
		verify(mockedSecondLanguage, times(1)).getLanguage();
	}

	@Test
	public void testTranslateTextOnFailureListener() {
		String sourceLanguage = "es";
		String targetLanguage = "de";
		LanguageTranslator languageTranslatorSpy = spy(new LanguageTranslator("Bienvenidos",
				sourceLanguage, targetLanguage, mockedStageActivity));

		languageTranslatorSpy.setModelManager(mockedModelManager);

		TranslatorOptions mockedTranslatorOptions = mock(TranslatorOptions.class);
		languageTranslatorSpy.setTranslatorOptions(mockedTranslatorOptions);

		Translator mockedTranslator = mock(Translator.class);
		languageTranslatorSpy.setTranslationModel(mockedTranslator);
		doNothing().when(mockedTranslator).close();

		Task<String> mockedTranslationResult = mock(Task.class);
		when(mockedTranslator.translate(languageTranslatorSpy.getText())).thenReturn(mockedTranslationResult);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		when(mockedModelManager.getDownloadedModels(TranslateRemoteModel.class)).thenReturn(mockedGetModelsTask);

		Task<Set<TranslateRemoteModel>> mockedResult = mock(Task.class);
		TranslateRemoteModel mockedFirstLanguage = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedSecondLanguage = mock(TranslateRemoteModel.class);
		Set<TranslateRemoteModel> result = new HashSet<>();
		result.add(mockedFirstLanguage);
		result.add(mockedSecondLanguage);

		when(mockedFirstLanguage.getLanguage()).thenReturn(sourceLanguage);
		when(mockedSecondLanguage.getLanguage()).thenReturn(targetLanguage);
		when(mockedResult.getResult()).thenReturn(result);
		when(mockedResult.isSuccessful()).thenReturn(true);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedResult);
			assertNotNull(mockedResult);
			assertTrue(mockedResult.isSuccessful());
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		Exception exceptionResult = mock(Exception.class);
		String message = "Some exception";
		when(exceptionResult.getMessage()).thenReturn(message);
		doAnswer(invocation -> {
			OnFailureListener callback = invocation.getArgument(0);
			callback.onFailure(exceptionResult);
			assertNotNull(exceptionResult);
			assertEquals(exceptionResult.getMessage(), message);
			return null;
		}).when(mockedTranslationResult).addOnFailureListener(any(OnFailureListener.class));

		languageTranslatorSpy.translate();

		verify(languageTranslatorSpy, times(1)).translate();
		verify(mockedTranslator, times(1)).translate(languageTranslatorSpy.getText());
		verify(mockedModelManager, times(1)).getDownloadedModels(TranslateRemoteModel.class);

		verify(mockedResult, times(2)).isSuccessful();
		verify(mockedFirstLanguage, times(1)).getLanguage();
		verify(mockedSecondLanguage, times(1)).getLanguage();

		verify(exceptionResult, times(2)).getMessage();
	}
}
