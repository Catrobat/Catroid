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

package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.TranslateTextFromToAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.languagetranslator.LanguageTranslator;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.DownloadLanguageModelDialog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TranslateTextFromToActionTest {

	private final String userVariableName = "Translation";
	private final String text = "Bienvenidos";
	private final String sourceLanguage = "es";
	private final String targetLanguage = "de";
	private final String translation = "Herzlich Willkommen";
	private final Object defaultValue = 0d;
	private Sprite testSprite;
	private UserVariable userVariable;
	private LanguageTranslator languageTranslator;
	private Task<Set<TranslateRemoteModel>> mockedTaskResult;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		createProject();
		setUpMocks();
	}

	@Test
	public void testSetFieldsWithStringFormulasAndInitialDefaultUserVariableValue() {
		testSprite.getActionFactory().createTranslateTextFromToAction(testSprite,
				new SequenceAction(), new Formula(text), new Formula(sourceLanguage),
				new Formula(targetLanguage), userVariable);

		assertEquals(userVariableName, userVariable.getName());
		assertEquals(defaultValue, userVariable.getValue());
	}

	@Test
	public void testTranslationResultIsSetInUserVariable() {
		ActionFactory actionFactory = testSprite.getActionFactory();
		TranslateTextFromToAction translateTextFromToAction =
				(TranslateTextFromToAction) actionFactory.createTranslateTextFromToAction(testSprite,
						new SequenceAction(), new Formula(text), new Formula(sourceLanguage),
						new Formula(targetLanguage), userVariable);

		translateTextFromToAction.setLanguageTranslator(languageTranslator);
		translateTextFromToAction.act(1.0f);

		assertEquals(translation, userVariable.getValue());
	}

	@Test
	public void testNullSprite() {
		exception.expect(NullPointerException.class);
		testSprite.getActionFactory().createTranslateTextFromToAction(null,
				new SequenceAction(), new Formula(text), new Formula(sourceLanguage),
				new Formula(targetLanguage), userVariable).act(1.0f);
	}

	private void createProject() {
		testSprite = new Sprite("testSprite");
		userVariable = new UserVariable(userVariableName);
		Project project = new Project(ApplicationProvider.getApplicationContext(), "testProject");
		project.addUserVariable(userVariable);
		ProjectManager.getInstance().setCurrentProject(project);
	}

	private void setUpMocks() {
		StageActivity mockedStageActivity = mock(StageActivity.class);
		languageTranslator = new LanguageTranslator(text, sourceLanguage, targetLanguage, mockedStageActivity);

		RemoteModelManager mockedRemoteModelManager = mock(RemoteModelManager.class);
		languageTranslator.setModelManager(mockedRemoteModelManager);

		Task<Set<TranslateRemoteModel>> mockedGetModelsTask = mock(Task.class);
		DownloadLanguageModelDialog mockedDownloadLanguageModelDialog = mock(DownloadLanguageModelDialog.class);

		when(mockedStageActivity.getDownloadLanguageModelDialog())
				.thenReturn(mockedDownloadLanguageModelDialog);
		when(mockedRemoteModelManager.getDownloadedModels(TranslateRemoteModel.class))
				.thenReturn(mockedGetModelsTask);

		TranslateRemoteModel mockedLanguageModelEn = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedLanguageModelEs = mock(TranslateRemoteModel.class);
		TranslateRemoteModel mockedLanguageModelDe = mock(TranslateRemoteModel.class);

		Set<TranslateRemoteModel> modelsOnDevice = new HashSet<>();

		modelsOnDevice.add(mockedLanguageModelEn);
		modelsOnDevice.add(mockedLanguageModelEs);
		modelsOnDevice.add(mockedLanguageModelDe);

		mockedTaskResult = mock(Task.class);
		when(mockedTaskResult.isSuccessful()).thenReturn(true);
		when(mockedTaskResult.getResult()).thenReturn(modelsOnDevice);

		when(mockedLanguageModelEn.getLanguage()).thenReturn("en");
		when(mockedLanguageModelEs.getLanguage()).thenReturn(sourceLanguage);
		when(mockedLanguageModelDe.getLanguage()).thenReturn(targetLanguage);

		TranslatorOptions mockedTranslatorOptions = mock(TranslatorOptions.class);
		languageTranslator.setTranslatorOptions(mockedTranslatorOptions);

		Translator mockedTranslationModel = mock(Translator.class);
		doNothing().when(mockedTranslationModel).close();
		languageTranslator.setTranslationModel(mockedTranslationModel);

		doAnswer(invocation -> {
			OnCompleteListener<Set<TranslateRemoteModel>> callback = invocation.getArgument(0);
			callback.onComplete(mockedTaskResult);
			return null;
		}).when(mockedGetModelsTask).addOnCompleteListener(any(OnCompleteListener.class));

		Task<String> mockedTranslateTask = mock(Task.class);
		when(mockedTranslationModel.translate(text)).thenReturn(mockedTranslateTask);

		Task<String> mockedTaskTranslation = mock(Task.class);
		when(mockedTaskTranslation.isSuccessful()).thenReturn(true);
		when(mockedTaskTranslation.getResult()).thenReturn(translation);

		doAnswer(invocation -> {
			OnCompleteListener<String> callback = invocation.getArgument(0);
			callback.onComplete(mockedTaskTranslation);
			return null;
		}).when(mockedTranslateTask).addOnCompleteListener(any(OnCompleteListener.class));
	}
}
