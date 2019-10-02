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

package org.catrobat.catroid

import dagger.Component
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.dagger.ActivityBindingModule
import org.catrobat.catroid.dagger.ActivityScope
import org.catrobat.catroid.dagger.AppComponent
import org.catrobat.catroid.dagger.CatroidModule
import org.catrobat.catroid.dagger.EagerSingletonsModule
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.uiespresso.ui.activity.ProjectUploadRatingDialogTest
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        UiTestActivityBindingModule::class,
        CatroidModule::class,
        EagerSingletonsModule::class
    ]
)
interface UiTestAppComponent : AppComponent

@Module(includes = [ActivityBindingModule::class])
abstract class UiTestActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeProjectUploadTestActivityInjector(): ProjectUploadRatingDialogTest.ProjectUploadTestActivity
}

class UiTestCatroidModule(application: CatroidApplication) :
    CatroidModule(application = application) {

    override fun provideProjectManager(
        xstreamSerializer: XstreamSerializer,
        defaultProjectHandler: DefaultProjectHandler
    ): ProjectManager {
        val projectManager = super.provideProjectManager(xstreamSerializer, defaultProjectHandler)
        UiTestCatroidApplication.projectManager = projectManager
        return projectManager
    }
}
