package org.catrobat.catroid.dagger

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

annotation class ActivityScope
