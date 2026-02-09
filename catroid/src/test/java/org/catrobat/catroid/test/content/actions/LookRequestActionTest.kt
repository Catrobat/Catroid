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

import android.os.Handler
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
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.LookRequestAction
import org.catrobat.catroid.content.actions.WebAction
import org.catrobat.catroid.formulaeditor.Formula
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
import java.io.InputStream

@RunWith(JUnit4::class)
class LookRequestActionTest {

    companion object {
        private const val TEST_URL = "https://catroid-test.catrob.at/pocketcode/"
    }

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var responseMock: Response

    @RelaxedMockK
    lateinit var responseBodyMock: ResponseBody

    @RelaxedMockK
    lateinit var lookData1Mock: LookData

    @RelaxedMockK
    lateinit var lookData2Mock: LookData

    @RelaxedMockK
    lateinit var responseStreamMock: InputStream

    private lateinit var testSprite: Sprite
    private lateinit var testSequence: SequenceAction

    @Before
    fun setUp() {
        mockkStatic(GdxNativesLoader::class)
        mockkConstructor(WebConnection::class)

        val context = MockUtil.getApplicationContextMock()
        val project = Project(context, "Project")
        ProjectManager.getInstance().currentProject = project

        StageActivity.stageListener = mockk<StageListener>()
        StageActivity.stageListener.webConnectionHolder = mockk<WebConnectionHolder>(relaxed = true)
        StageActivity.messageHandler = mockk<Handler>(relaxed = true)

        every { responseMock.body() } returns responseBodyMock
        every { responseBodyMock.byteStream() } returns responseStreamMock

        testSprite = Sprite("testSprite")
        testSequence = SequenceAction()
        testSprite.look.lookData = lookData1Mock
    }

    @Test(expected = NullPointerException::class)
    fun testSpriteIsNull() {
        val action = testSprite.actionFactory.createLookRequestAction(
            null, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction

        assertTrue(action.act(0f))
        assertEquals(lookData1Mock, testSprite.look.lookData)
    }

    @Test
    fun testTooManyRequests() {
        val action = testSprite.actionFactory.createLookRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns false

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_TOO_MANY_REQUESTS.toString(), action.errorCode)
        assertEquals(lookData1Mock, testSprite.look.lookData)
    }

    @Test
    fun testRequestNotSuccessfullySent() {
        val action = testSprite.actionFactory.createLookRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction
        action.grantPermission()

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            action.onRequestError(Constants.ERROR_BAD_REQUEST.toString())
        }

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_BAD_REQUEST.toString(), action.errorCode)
        assertEquals(lookData1Mock, testSprite.look.lookData)
    }

    @Test
    fun testSuccessfulResponse() {
        val actionSpy = spyk<LookRequestAction>()
        actionSpy.apply {
            scope = Scope(Project(), testSprite, testSequence)
            formula = Formula(TEST_URL)
            requestStatus = WebAction.RequestStatus.NOT_SENT
            grantPermission()
        }

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { actionSpy.getLookFromResponse() } returns lookData2Mock
        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            actionSpy.onRequestSuccess(responseMock)
        }

        assertTrue(actionSpy.act(0f))
        assertEquals(responseStreamMock, actionSpy.response)
        assertEquals(lookData2Mock, testSprite.look.lookData)
    }

    @Test
    fun testPermissionDenied() {
        val action = testSprite.actionFactory.createLookRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction
        action.denyPermission()

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_AUTHENTICATION_REQUIRED.toString(), action.errorCode)
        assertEquals(lookData1Mock, testSprite.look.lookData)
    }

    @Test
    fun testCancelledCallAndResendRequest() {
        val actionSpy = spyk<LookRequestAction>()
        actionSpy.apply {
            scope = Scope(Project(), testSprite, testSequence)
            formula = Formula(TEST_URL)
            requestStatus = WebAction.RequestStatus.NOT_SENT
            grantPermission()
        }

        every {
            StageActivity.stageListener.webConnectionHolder
                .addConnection(any<WebConnection>())
        } returns true
        every { actionSpy.getLookFromResponse() } returns lookData2Mock
        justRun { anyConstructed<WebConnection>().sendWebRequest() }

        assertFalse(actionSpy.act(0f))

        actionSpy.onCancelledCall()

        assertEquals(WebAction.RequestStatus.NOT_SENT, actionSpy.requestStatus)

        every { anyConstructed<WebConnection>().sendWebRequest() } answers {
            actionSpy.onRequestSuccess(responseMock)
        }

        assertTrue(actionSpy.act(0f))
        assertEquals(responseStreamMock, actionSpy.response)
        assertEquals(lookData2Mock, testSprite.look.lookData)
        verify(exactly = 2) { anyConstructed<WebConnection>().sendWebRequest() }
        verify(exactly = 2) {
            StageActivity.stageListener.webConnectionHolder.addConnection(any<WebConnection>())
        }
    }

    @After
    fun tearDown() {
        StageActivity.stageListener.webConnectionHolder = null
        StageActivity.stageListener = null

        unmockkConstructor(WebConnection::class)
        unmockkStatic(GdxNativesLoader::class)
    }
}
