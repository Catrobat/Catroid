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

package org.catrobat.catroid.koin

import android.app.Application
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

val componentsModules = module(createdAtStart = true, override = false) {
    single { ProjectManager(androidContext()) }
}

/**
 * examples for inject view models in java & kotlin
 * https://github.com/InsertKoinIO/koin/blob/master/koin-projects/docs/reference/koin-android/viewmodel.md
 */
val viewModelModules = module {
    viewModel { ProjectsViewModel() }
}

val myModules = listOf(componentsModules, viewModelModules)

fun start(application: Application, modules: List<Module>) {
    startKoin {
        androidContext(application.applicationContext)
        androidLogger(Level.ERROR)
        modules(modules)
    }
}
