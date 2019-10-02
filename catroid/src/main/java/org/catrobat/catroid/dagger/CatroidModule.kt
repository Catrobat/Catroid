package org.catrobat.catroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.DownloadUtil
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import javax.inject.Singleton

@Module
open class CatroidModule(private val application: CatroidApplication) {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    open fun provideProjectManager(
        xstreamSerializer: XstreamSerializer,
        defaultProjectHandler: DefaultProjectHandler
    ):
        ProjectManager =
        ProjectManager(application, xstreamSerializer, defaultProjectHandler)

    @Provides
    @Singleton
    open fun provideDefaultProjectHandler(xstreamSerializer: XstreamSerializer): DefaultProjectHandler =
        DefaultProjectHandler(application, xstreamSerializer)

    @Provides
    @Singleton
    open fun provideDownloadUtil(statusBarNotificationManager: StatusBarNotificationManager):
        DownloadUtil = DownloadUtil(statusBarNotificationManager)

    @Provides
    @Singleton
    open fun provideStatusBarNotificationManager(): StatusBarNotificationManager =
        StatusBarNotificationManager(application)

    @Provides
    @Singleton
    open fun provideXstreamSerializer(): XstreamSerializer =
        XstreamSerializer()
}
