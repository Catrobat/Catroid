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

package org.catrobat.catroid.test.robolectric

import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.dagger.CatroidModule
import org.catrobat.catroid.dagger.DaggerAppComponent
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.DownloadUtil
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestCatroidApplication : CatroidApplication(), TestLifecycleApplication {

    companion object {
        lateinit var projectManager: ProjectManager
        fun isProjectManagerInitialized(): Boolean = ::projectManager.isInitialized

        lateinit var defaultProjectHandler: DefaultProjectHandler
        fun isDefaultProjectHandlerInitialized(): Boolean = ::defaultProjectHandler.isInitialized

        lateinit var downloadUtil: DownloadUtil
        fun isDownloadUtilInitialized(): Boolean = ::downloadUtil.isInitialized

        lateinit var xstreamSerializer: XstreamSerializer
        fun isXstreamSerializerInitialized(): Boolean = ::xstreamSerializer.isInitialized

        lateinit var statusBarNotificationManager: StatusBarNotificationManager
        fun isStatusBarNotificationManagerInitialized(): Boolean =
            ::statusBarNotificationManager.isInitialized
    }

    override fun createApplicationComponents() {
        val testCatroidModule = TestCatroidModule(this)
        appComponents = DaggerAppComponent.builder()
            .catroidModule(testCatroidModule)
            .build()
    }

    override fun beforeTest(method: Method?) = Unit

    override fun prepareTest(test: Any?) = Unit

    override fun afterTest(method: Method?) = Unit
}

class TestCatroidModule(application: CatroidApplication) :
    CatroidModule(application = application) {

    override fun provideProjectManager(
        xstreamSerializer: XstreamSerializer,
        defaultProjectHandler: DefaultProjectHandler
    ): ProjectManager {
        if (!TestCatroidApplication.isProjectManagerInitialized()) {
            TestCatroidApplication.projectManager =
                super.provideProjectManager(xstreamSerializer, defaultProjectHandler)
        }
        return TestCatroidApplication.projectManager
    }

    override fun provideDefaultProjectHandler(xstreamSerializer: XstreamSerializer): DefaultProjectHandler {
        if (!TestCatroidApplication.isDefaultProjectHandlerInitialized()) {
            TestCatroidApplication.defaultProjectHandler =
                super.provideDefaultProjectHandler(xstreamSerializer)
        }
        return TestCatroidApplication.defaultProjectHandler
    }

    override fun provideDownloadUtil(statusBarNotificationManager: StatusBarNotificationManager):
        DownloadUtil {
        if (!TestCatroidApplication.isDownloadUtilInitialized()) {
            TestCatroidApplication.downloadUtil =
                super.provideDownloadUtil(statusBarNotificationManager)
        }
        return TestCatroidApplication.downloadUtil
    }

    override fun provideStatusBarNotificationManager(): StatusBarNotificationManager {
        if (!TestCatroidApplication.isStatusBarNotificationManagerInitialized()) {
            TestCatroidApplication.statusBarNotificationManager =
                super.provideStatusBarNotificationManager()
        }
        return TestCatroidApplication.statusBarNotificationManager
    }

    override fun provideXstreamSerializer(): XstreamSerializer {
        if (!TestCatroidApplication.isXstreamSerializerInitialized()) {
            TestCatroidApplication.xstreamSerializer = super.provideXstreamSerializer()
        }
        return TestCatroidApplication.xstreamSerializer
    }
}
