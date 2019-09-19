package org.catrobat.catroid.dagger

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import org.catrobat.catroid.CatroidApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        CatroidModule::class,
        EagerSingletonsModule::class
    ]
)
interface AppComponent {
    fun initializeEagerSingletons(): Set<EagerSingleton>

    fun inject(app: CatroidApplication)
}
