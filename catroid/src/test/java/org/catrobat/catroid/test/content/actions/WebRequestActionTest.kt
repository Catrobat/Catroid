/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.GdxNativesLoader
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkConstructor
import io.mockk.unmockkStatic
import io.mockk.verify
import okhttp3.Response
import okhttp3.ResponseBody
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WebAction
import org.catrobat.catroid.content.actions.WebRequestAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.test.mockutils.MockUtil
import org.catrobat.catroid.web.WebConnection
import org.catrobat.catroid.web.WebConnectionHolder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WebRequestActionTest {

    companion object {
        private const val TEST_URL = "https://catroid-test.catrob.at/pocketcode/"
        private const val TEST_USER_VARIABLE = "testUserVariable"
        private const val TEST_INPUT_VARIABLE = "testInputVariable"
        private const val RESPONSE_STRING = "Response"
    }

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var responseMock: Response

    @RelaxedMockK
    lateinit var responseBodyMock: ResponseBody

    private lateinit var testSprite: Sprite
    private lateinit var testSequence: SequenceAction
    private lateinit var userVariable: UserVariable

    @Before
    fun setUp() {
        mockkStatic(GdxNativesLoader::class)
        mockkConstructor(WebConnection::class)

        val context = MockUtil.getApplicationContextMock()
        val project = Project(context, "Project")
        ProjectManager.getInstance().currentProject = project

        StageActivity.stageListener = mockk<StageListener>()
        StageActivity.stageListener.webConnectionHolder = mockk<WebConnectionHolder>(relaxed = true)

        every { responseMock.body() } returns responseBodyMock
        every { responseBodyMock.string() } returns RESPONSE_STRING

        testSprite = Sprite("testSprite")
        testSequence = SequenceAction()
        userVariable = UserVariable(TEST_USER_VARIABLE)
    }

    @Test
    fun testUserVariableIsNull() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            null
        ) as WebRequestAction

        assertTrue(action.act(0f))
    }

    @Test
    fun testTooManyRequests() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            userVariable
        ) as WebRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns false

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_TOO_MANY_REQUESTS.toString(), userVariable.value)
    }

    @Test
    fun testRequestNotSuccessfullySent() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            userVariable
        ) as WebRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            action.onRequestError(Constants.ERROR_BAD_REQUEST.toString())
        }

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_BAD_REQUEST.toString(), userVariable.value)
    }

    @Test
    fun testSuccessfulResponse() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            userVariable
        ) as WebRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            action.onRequestSuccess(responseMock)
        }

        assertTrue(action.act(0f))
        assertEquals(RESPONSE_STRING, userVariable.value)
    }

    @Test
    fun testPermissionDenied() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            userVariable
        ) as WebRequestAction
        action.denyPermission()

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_AUTHENTICATION_REQUIRED.toString(), userVariable.value)
    }

    @Test
    fun testCancelledCallAndResendRequest() {
        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL),
            userVariable
        ) as WebRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        justRun { anyConstructed<WebConnection>().sendWebRequest() }

        assertFalse(action.act(0f))

        action.onCancelledCall()

        assertEquals(WebAction.RequestStatus.NOT_SENT, action.requestStatus)

        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            action.onRequestSuccess(responseMock)
        }

        assertTrue(action.act(0f))
        assertEquals(RESPONSE_STRING, userVariable.value)
        verify(exactly = 2) { anyConstructed<WebConnection>().sendWebRequest() }
        verify(exactly = 2) {
            StageActivity.stageListener.webConnectionHolder.addConnection(any<WebConnection>())
        }
    }

    @Test
    fun testSuccessfulResponseWithInputVariable() {
        val action = setupTestSuccessfulResponseWithInputVariable()

        assertTrue(action.act(0f))
    }

    @Test
    fun testInputVariableIsSetCorrectly() {
        val action = setupTestSuccessfulResponseWithInputVariable()

        assertTrue(action.act(0f))
        assertEquals(RESPONSE_STRING, userVariable.value)
    }

    @After
    fun tearDown() {
        StageActivity.stageListener.webConnectionHolder = null
        StageActivity.stageListener = null

        unmockkConstructor(WebConnection::class)
        unmockkStatic(GdxNativesLoader::class)
    }

    private fun setupTestSuccessfulResponseWithInputVariable(): WebRequestAction {
        val formulaSpy = spyk<Formula>(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.USER_VARIABLE,
                    TEST_INPUT_VARIABLE,
                    null
                )
            )
        )

        testSprite.addUserVariable(userVariable)
        testSprite.addUserVariable(UserVariable(TEST_INPUT_VARIABLE, TEST_URL))

        val action = testSprite.actionFactory.createWebRequestAction(
            testSprite,
            testSequence,
            formulaSpy,
            userVariable
        ) as WebRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { formulaSpy.interpretString(any<Scope>()) } answers {
            val scope = args[0] as Scope
            scope.sprite.getUserVariable(TEST_INPUT_VARIABLE).value.toString()
        }
        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            action.onRequestSuccess(responseMock)
        }

        return action
    }
}
