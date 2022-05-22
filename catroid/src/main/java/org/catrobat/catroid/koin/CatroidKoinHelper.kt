/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import androidx.room.Room
import androidx.work.WorkManager
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.db.AppDatabase
import org.catrobat.catroid.db.DatabaseMigrations
import org.catrobat.catroid.retrofit.CatroidWebServer
//import org.catrobat.catroid.stage.HmsSpeechRecognitionHolder
import org.catrobat.catroid.stage.SpeechRecognitionHolder
import org.catrobat.catroid.stage.SpeechRecognitionHolderFactory
import org.catrobat.catroid.sync.DefaultFeaturedProjectSync
import org.catrobat.catroid.sync.DefaultProjectsCategoriesSync
import org.catrobat.catroid.sync.FeaturedProjectsSync
import org.catrobat.catroid.sync.ProjectsCategoriesSync
import org.catrobat.catroid.ui.recyclerview.adapter.CategoriesAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.FeaturedProjectsAdapter
import org.catrobat.catroid.ui.recyclerview.repository.LocalHashVersionRepository
import org.catrobat.catroid.ui.recyclerview.repository.DefaultLocalHashVersionRepository
import org.catrobat.catroid.ui.recyclerview.repository.DefaultFeaturedProjectsRepository
import org.catrobat.catroid.ui.recyclerview.repository.DefaultProjectCategoriesRepository
import org.catrobat.catroid.ui.recyclerview.repository.FeaturedProjectsRepository
import org.catrobat.catroid.ui.recyclerview.repository.ProjectCategoriesRepository
import org.catrobat.catroid.ui.recyclerview.viewmodel.MainFragmentViewModel
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

val componentsModules = module(createdAtStart = true, override = false) {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
            .addMigrations(DatabaseMigrations.MIGRATION_1_2)
            .build()
    }
    single {
        CatroidWebServer.getWebService("https://share.catrob.at/api/")
    }
    factory { WorkManager.getInstance(androidContext()) }
    single { ProjectManager(androidContext()) }
    single { NetworkConnectionMonitor(androidContext()) }
    factory { HuaweiApiAvailability.getInstance() }
    factory { GoogleApiAvailability.getInstance() }
    factory { MobileServiceAvailability(get(), get()) }

    single {
        DefaultFeaturedProjectSync(get(), get(), get()) as FeaturedProjectsSync
    }

    single {
        DefaultProjectsCategoriesSync(get(), get(), get()) as ProjectsCategoriesSync
    }
}

/**
 * examples for inject view models in java & kotlin
 * https://github.com/InsertKoinIO/koin/blob/master/koin-projects/docs/reference/koin-android/viewmodel.md
 */
val viewModelModules = module {
    viewModel { MainFragmentViewModel(get(), get(), get(), get()) }
}

val repositoryModules = module {
    single {
        DefaultLocalHashVersionRepository(androidContext()) as LocalHashVersionRepository
    }

    single {
        DefaultFeaturedProjectsRepository(get()) as FeaturedProjectsRepository
    }

    single {
        DefaultProjectCategoriesRepository(get()) as ProjectCategoriesRepository
    }
}

val adapterModules = module {
    single {
        FeaturedProjectsAdapter()
    }

    single {
        CategoriesAdapter()
    }
}

val speechModules = module {
    single { SpeechRecognitionHolder() }
    /*single { HmsSpeechRecognitionHolder() }
    single {
        SpeechRecognitionHolderFactory(
            get<SpeechRecognitionHolder>(),
            get<HmsSpeechRecognitionHolder>(),
            get()
        )
    }*/
}

val myModules = listOf(
    componentsModules, viewModelModules, repositoryModules, adapterModules, speechModules
)

fun start(application: Application, modules: List<Module>) {
    startKoin {
        androidContext(application.applicationContext)
        androidLogger(Level.ERROR)
        modules(modules)
    }
}
