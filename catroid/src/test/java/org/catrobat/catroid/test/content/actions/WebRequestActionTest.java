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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.WebRequestAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.web.WebConnection;
import org.catrobat.catroid.web.WebConnectionFactory;
import org.catrobat.catroid.web.WebConnectionHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class WebRequestActionTest {

	private static final Double ERROR_TOO_MANY_REQUESTS = 429d;
	private static final Double ERROR_BAD_REQUEST = 400d;
	private static final String TEST_URL = "https://catroid-test.catrob.at/pocketcode/";
	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String TEST_INPUTVARIABLE = "testInputvariable";
	private static final String RESPONSE_STRING = "Response";

	private Sprite testSprite;
	private UserVariable userVariable;
	private WebConnectionFactory webConnectionFactory;
	private WebConnection webConnection;

	@Before
	public void setUp() throws Exception {
		mockStatic(GdxNativesLoader.class);
		testSprite = new SingleSprite("testSprite");
		userVariable = new UserVariable(TEST_USERVARIABLE);

		StageActivity.stageListener = mock(StageListener.class);
		StageActivity.stageListener.webConnectionHolder = mock(WebConnectionHolder.class);
		webConnectionFactory = mock(WebConnectionFactory.class);
		webConnection = mock(WebConnection.class);
	}

	@After
	public void tearDown() throws Exception {
		StageActivity.stageListener.webConnectionHolder = null;
		StageActivity.stageListener = null;
	}

	@Test
	public void testUserVariableIsNull() {
		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				new Formula(TEST_URL),
				null
		);
		action.setWebConnectionFactory(webConnectionFactory);
		when(webConnectionFactory.createWebConnection(anyString(), any())).thenReturn(webConnection);
		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(false);

		assertTrue(action.act(0f));
	}

	@Test
	public void testTooManyRequests() {
		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				new Formula(TEST_URL),
				userVariable
		);
		action.setWebConnectionFactory(webConnectionFactory);
		when(webConnectionFactory.createWebConnection(anyString(), any())).thenReturn(webConnection);
		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(false);

		assertTrue(action.act(0f));
		assertEquals(ERROR_TOO_MANY_REQUESTS, userVariable.getValue());
	}

	@Test
	public void testRequestNotSuccessfullySend() {
		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				new Formula(TEST_URL),
				userVariable
		);
		action.setWebConnectionFactory(webConnectionFactory);
		when(webConnectionFactory.createWebConnection(anyString(), any())).thenReturn(webConnection);
		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(true);

		doAnswer(invocation -> {
			action.onRequestFinished(ERROR_BAD_REQUEST.toString());
			return null;
		}).when(webConnection).sendWebRequest();

		assertTrue(action.act(0f));
		assertEquals(ERROR_BAD_REQUEST.toString(), userVariable.getValue());
	}

	@Test
	public void testSuccessfulResponse() {
		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				new Formula(TEST_URL),
				userVariable
		);
		action.setWebConnectionFactory(webConnectionFactory);
		when(webConnectionFactory.createWebConnection(anyString(), any())).thenReturn(webConnection);
		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(true);

		doAnswer(invocation -> {
			action.onRequestFinished(RESPONSE_STRING);
			return null;
		}).when(webConnection).sendWebRequest();

		assertTrue(action.act(0f));
		assertEquals(RESPONSE_STRING, userVariable.getValue());
	}

	@Test
	public void testCancelledCallAndResendRequest() {
		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				new Formula(TEST_URL),
				userVariable
		);
		action.setWebConnectionFactory(webConnectionFactory);
		when(webConnectionFactory.createWebConnection(anyString(), any())).thenReturn(webConnection);
		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(true);

		assertFalse(action.act(0f));

		action.onCancelledCall();
		assertEquals(WebRequestAction.NOT_SENT, action.getRequestStatus());

		doAnswer(invocation -> {
			action.onRequestFinished(RESPONSE_STRING);
			return null;
		}).when(webConnection).sendWebRequest();

		assertTrue(action.act(0f));
		assertEquals(RESPONSE_STRING, userVariable.getValue());
		verify(webConnectionFactory, times(2)).createWebConnection(anyString(), any());
		verify(StageActivity.stageListener.webConnectionHolder, times(2)).addConnection(any());
		verify(webConnection, times(2)).sendWebRequest();
	}

	private WebRequestAction setupTestSuccessfulResponseWithInputVariable() throws InterpretationException {
		UserVariable inputVariable = new UserVariable(TEST_INPUTVARIABLE, TEST_URL);
		FormulaElement formulaElement = new FormulaElement(
				FormulaElement.ElementType.USER_VARIABLE, inputVariable.getName(), null);

		Formula formula = Mockito.spy(new Formula(formulaElement));

		testSprite.addUserVariable(userVariable);
		testSprite.addUserVariable(inputVariable);

		WebRequestAction action = (WebRequestAction) testSprite.getActionFactory().createWebRequestAction(
				testSprite,
				formula,
				userVariable
		);
		action.setWebConnectionFactory(webConnectionFactory);

		when(StageActivity.stageListener.webConnectionHolder.addConnection(any())).thenReturn(true);

		doAnswer(invocation -> {
			Sprite sprite = invocation.getArgument(0);
			return String.valueOf(sprite.getUserVariable(TEST_INPUTVARIABLE).getValue());
		}).when(formula).interpretString(any(Sprite.class));

		doAnswer(invocation -> webConnection).when(webConnectionFactory).createWebConnection(anyString(), any());

		doAnswer(invocation -> {
			action.onRequestFinished(RESPONSE_STRING);
			return null;
		}).when(webConnection).sendWebRequest();

		return action;
	}

	@Test
	public void testSuccessfulResponseWithInputVariable() throws InterpretationException {
		WebRequestAction action = setupTestSuccessfulResponseWithInputVariable();
		assertTrue(action.act(0f));
	}

	@Test
	public void testInputVariableIsSetCorrectly() throws InterpretationException {
		WebRequestAction action = setupTestSuccessfulResponseWithInputVariable();
		action.act(0f);
		assertEquals(RESPONSE_STRING, userVariable.getValue());
	}
}
