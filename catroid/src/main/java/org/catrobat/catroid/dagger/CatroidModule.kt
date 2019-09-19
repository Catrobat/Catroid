package org.catrobat.catroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import javax.inject.Singleton

@Module
open class CatroidModule(private val application: CatroidApplication) {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    open fun provideProjectManager(): ProjectManager = ProjectManager(application)
}
