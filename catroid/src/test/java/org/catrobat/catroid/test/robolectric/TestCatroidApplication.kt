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
import org.catrobat.catroid.dagger.CatroidModule
import org.catrobat.catroid.dagger.DaggerAppComponent
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestCatroidApplication : CatroidApplication(), TestLifecycleApplication {

    companion object {
        lateinit var projectManager: ProjectManager

        fun isProjectManagerInitialized(): Boolean = ::projectManager.isInitialized
    }

    override fun createApplicationComponents() {
        val testCatroidModule = TestCatroidModule(this)
        appComponents = DaggerAppComponent.builder()
            .catroidModule(testCatroidModule)
            .build()
    }

    override fun beforeTest(method: Method?) {}

    override fun prepareTest(test: Any?) {}

    override fun afterTest(method: Method?) {}
}

class TestCatroidModule(application: CatroidApplication) :
    CatroidModule(application = application) {

    override fun provideProjectManager(): ProjectManager {
        if (!TestCatroidApplication.isProjectManagerInitialized()) {
            TestCatroidApplication.projectManager = super.provideProjectManager()
        }
        return TestCatroidApplication.projectManager
    }
}
