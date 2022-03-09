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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.GdxNativesLoader
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
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.web.WebConnection
import org.catrobat.catroid.web.WebConnectionHolder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.whenNew
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.InputStream
import java.util.Collections

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class, WebAction::class)
class LookRequestActionTest {
    private lateinit var testSprite: Sprite
    private lateinit var testSequence: SequenceAction
    private lateinit var lookData1: LookData
    private lateinit var lookData2: LookData
    private lateinit var webConnection: WebConnection
    private lateinit var response: Response
    private lateinit var responseStream: InputStream

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    companion object {
        private const val TEST_URL = "https://catroid-test.catrob.at/pocketcode/"
    }

    @Before
    fun setUp() {
        PowerMockito.mockStatic(GdxNativesLoader::class.java)

        testSprite = Sprite("testSprite")
        testSequence = SequenceAction()
        lookData1 = mock(LookData::class.java)
        testSprite.look.lookData = lookData1
        lookData2 = mock(LookData::class.java)
        webConnection = mock(WebConnection::class.java)
        response = mock(Response::class.java)
        responseStream = mock(InputStream::class.java)

        StageActivity.stageListener = mock(StageListener::class.java)
        StageActivity.stageListener.webConnectionHolder = mock(WebConnectionHolder::class.java)

        whenNew(WebConnection::class.java).withAnyArguments().thenReturn(webConnection)
        val responseBody = mock(ResponseBody::class.java)
        doReturn(responseBody).`when`(response).body()
        doReturn(responseStream).`when`(responseBody).byteStream()

        val context = MockUtil.mockContextForProject(dependencyModules)
        val project = Project(context, "Project")
        projectManager.currentProject = project
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
        StageActivity.stageListener.webConnectionHolder = null
        StageActivity.stageListener = null
    }

    @Test(expected = NullPointerException::class)
    fun testSpriteIsNull() {
        val action = testSprite.actionFactory.createLookRequestAction(
            null, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction

        assertTrue(action.act(0f))
        assertEquals(lookData1, testSprite.look.lookData)
    }

    @Test
    fun testTooManyRequests() {
        val action = testSprite.actionFactory.createLookRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction
        action.grantPermission()

        doReturn(false).`when`(StageActivity.stageListener.webConnectionHolder)
            .addConnection(webConnection)

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_TOO_MANY_REQUESTS.toString(), action.errorCode)
        assertEquals(lookData1, testSprite.look.lookData)
    }

    @Test
    fun testRequestNotSuccessfullySent() {
        val action = testSprite.actionFactory.createLookRequestAction(
            testSprite, testSequence,
            Formula(TEST_URL)
        ) as LookRequestAction
        action.grantPermission()

        doReturn(true).`when`(StageActivity.stageListener.webConnectionHolder)
            .addConnection(webConnection)

        doAnswer {
            action.onRequestError(Constants.ERROR_BAD_REQUEST.toString())
        }.`when`(webConnection).sendWebRequest()

        assertTrue(action.act(0f))
        assertEquals(Constants.ERROR_BAD_REQUEST.toString(), action.errorCode)
        assertEquals(lookData1, testSprite.look.lookData)
    }

    @Test
    fun testSuccessfulResponse() {
        val action = mock(LookRequestAction::class.java, Mockito.CALLS_REAL_METHODS)
        action.apply {
            scope = Scope(Project(), testSprite, testSequence)
            formula = Formula(TEST_URL)
            requestStatus = WebAction.RequestStatus.NOT_SENT
            grantPermission()
        }

        doReturn(lookData2).`when`(action).getLookFromResponse()
        doReturn(true).`when`(StageActivity.stageListener.webConnectionHolder)
            .addConnection(webConnection)

        doAnswer {
            action.onRequestSuccess(response)
        }.`when`(webConnection).sendWebRequest()

        assertTrue(action.act(0f))
        assertEquals(responseStream, action.response)
        assertEquals(lookData2, testSprite.look.lookData)
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
        assertEquals(lookData1, testSprite.look.lookData)
    }

    @Test
    fun testCancelledCallAndResendRequest() {
        val action = mock(LookRequestAction::class.java, Mockito.CALLS_REAL_METHODS)
        action.apply {
            scope = Scope(Project(), testSprite, testSequence)
            formula = Formula(TEST_URL)
            requestStatus = WebAction.RequestStatus.NOT_SENT
            grantPermission()
        }

        doReturn(true).`when`(StageActivity.stageListener.webConnectionHolder)
            .addConnection(webConnection)
        doReturn(lookData2).`when`(action).getLookFromResponse()

        assertFalse(action.act(0f))
        action.onCancelledCall()
        assertEquals(WebAction.RequestStatus.NOT_SENT, action.requestStatus)

        doAnswer {
            action.onRequestSuccess(response)
        }.`when`(webConnection).sendWebRequest()

        assertTrue(action.act(0f))

        PowerMockito.verifyNew(WebConnection::class.java, times(2))
            .withArguments(any(), anyString())
        Mockito.verify(StageActivity.stageListener.webConnectionHolder, times(2))
            .addConnection(webConnection)
        Mockito.verify(webConnection, times(2)).sendWebRequest()

        assertEquals(responseStream, action.response)
        assertEquals(lookData2, testSprite.look.lookData)
    }
}
