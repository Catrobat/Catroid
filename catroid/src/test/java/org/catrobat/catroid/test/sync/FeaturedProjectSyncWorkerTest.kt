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

package org.catrobat.catroid.test.sync

import android.os.Build
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.sync.FeaturedProjectSyncWorker
import org.catrobat.catroid.sync.FeaturedProjectsSync
import org.catrobat.catroid.web.WebConnectionException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], instrumentedPackages = [])
class FeaturedProjectSyncWorkerTest {

    private val featuredProjectsSync = mockk<FeaturedProjectsSync>(relaxed = true)
    private val context get() = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(module {
                single<FeaturedProjectsSync> { featuredProjectsSync }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun buildWorker(runAttemptCount: Int = 0) =
        TestListenableWorkerBuilder<FeaturedProjectSyncWorker>(context)
            .setRunAttemptCount(runAttemptCount)
            .build()

    private fun givenSyncFails() {
        coEvery { featuredProjectsSync.sync(any()) } throws
            WebConnectionException(WebConnectionException.ERROR_NETWORK, "I/O Exception")
    }

    @Test
    fun testSyncSuccessReturnsSuccess() = runBlocking {
        assertEquals(ListenableWorker.Result.success(), buildWorker().doWork())
        coVerify(exactly = 1) { featuredProjectsSync.sync(any()) }
    }

    @Test
    fun testWebConnectionExceptionOnFirstAttemptReturnsRetry() = runBlocking {
        givenSyncFails()

        assertEquals(ListenableWorker.Result.retry(), buildWorker(runAttemptCount = 0).doWork())
    }

    @Test
    fun testWebConnectionExceptionOnSecondAttemptReturnsRetry() = runBlocking {
        givenSyncFails()

        assertEquals(ListenableWorker.Result.retry(), buildWorker(runAttemptCount = 1).doWork())
    }

    @Test
    fun testWebConnectionExceptionOnLastAttemptReturnsFailure() = runBlocking {
        givenSyncFails()

        assertEquals(ListenableWorker.Result.failure(), buildWorker(runAttemptCount = 2).doWork())
    }
}
