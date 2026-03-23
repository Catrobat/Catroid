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
package org.catrobat.catroid.test.transfers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.web.ProjectDownloader
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

@RunWith(JUnit4::class)
class ProjectDownloaderTest {

    companion object {
        private const val URL =
            "https://share.catrob.at/pocketcode/download/71489.catrobat?fname=Pet%20Simulator"
        private const val PROJECT_NAME = "Pet Simulator"
    }

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var activityMock: AppCompatActivity

    @RelaxedMockK
    lateinit var queueMock: ProjectDownloader.ProjectDownloadQueue

    lateinit var downloaderSpy: ProjectDownloader

    @Before
    fun setUp() {
        downloaderSpy = spyk(ProjectDownloader(queueMock, URL, null))

        mockkStatic(ToastUtil::class)
        mockkStatic(URLDecoder::class)
        mockkStatic(ReplaceExistingProjectDialogFragment::class)
    }

    @Test
    fun testShowNotificationIfDecodeProjectNameFailed() {
        every { URLDecoder.decode(any<String>(), any<String>()) } throws
            UnsupportedEncodingException()

        downloaderSpy.download(activityMock)

        verify(exactly = 1) {
            ToastUtil.showError(
                activityMock,
                R.string.error_could_not_decode_project_name_from_url
            )
        }
        verify { queueMock wasNot Called }
        verify(exactly = 0) { downloaderSpy.startService(any<String>(), any<Context>()) }
    }

    @Test
    fun testShowDialogIfProjectAlreadyExists() {
        val dialogMock = mockk<ReplaceExistingProjectDialogFragment>(relaxed = true)
        every {
            ReplaceExistingProjectDialogFragment.newInstance(
                any<String>(),
                any<ProjectDownloader>()
            )
        } returns dialogMock

        every { ReplaceExistingProjectDialogFragment.projectExistsInDirectory(PROJECT_NAME) } returns true

        val transactionMock = mockk<FragmentManager>(relaxed = true)
        every { activityMock.supportFragmentManager } returns transactionMock

        downloaderSpy.download(activityMock)

        verify(exactly = 1) { dialogMock.show(transactionMock, any<String>()) }
        verify(exactly = 0) {
            downloaderSpy.downloadOverwriteExistingProject(
                any<Context>(),
                any<String>()
            )
        }
        verify(exactly = 0) {
            downloaderSpy.startService(
                any<String>(),
                any<Context>()
            )
        }
    }

    @Test
    fun testDownloadOverwriteExistingProjectProjectNotInDownloadQueue() {
        justRun { downloaderSpy.startService(PROJECT_NAME, any<Context>()) }

        downloaderSpy.downloadOverwriteExistingProject(activityMock, PROJECT_NAME)

        verify(exactly = 1) { downloaderSpy.startService(PROJECT_NAME, activityMock) }
        verify(exactly = 1) { queueMock.enqueue(PROJECT_NAME) }
        verify(exactly = 1) { queueMock.alreadyInQueue(PROJECT_NAME) }
        confirmVerified(queueMock)
    }

    @Test
    fun testDownloadOverwriteExistingProjectProjectInDownloadQueue() {
        justRun { downloaderSpy.startService(PROJECT_NAME, any<Context>()) }
        every { queueMock.alreadyInQueue(PROJECT_NAME) } returns true

        val errorMessage = "test error"
        every {
            activityMock.getString(
                R.string.error_project_already_in_queue,
                any<String>()
            )
        } returns errorMessage

        downloaderSpy.downloadOverwriteExistingProject(activityMock, PROJECT_NAME)

        verify(exactly = 1) { queueMock.alreadyInQueue(PROJECT_NAME) }
        verify(exactly = 1) { ToastUtil.showError(activityMock, errorMessage) }
        confirmVerified(queueMock)
    }

    @After
    fun tearDown() {
        unmockkStatic(ToastUtil::class)
        unmockkStatic(URLDecoder::class)
        unmockkStatic(ReplaceExistingProjectDialogFragment::class)
    }
}
