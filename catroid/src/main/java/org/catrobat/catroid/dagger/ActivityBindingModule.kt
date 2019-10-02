package org.catrobat.catroid.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.ProjectUploadActivity
import org.catrobat.catroid.ui.WebViewActivity

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivityInjector(): MainMenuActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeProjectUploadActivityInjector(): ProjectUploadActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeWebViewActivityInjector(): WebViewActivity
}
