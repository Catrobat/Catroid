package org.catrobat.catroid.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.catrobat.catroid.ui.MainMenuActivity

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivityInjector(): MainMenuActivity
}
