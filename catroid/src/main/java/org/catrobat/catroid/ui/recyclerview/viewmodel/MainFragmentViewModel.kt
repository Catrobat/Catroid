/*
* Catroid: An on-device visual programming system for Android devices
* Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.sync.FeaturedProjectSyncWorker
import org.catrobat.catroid.sync.ProjectsCategoriesSyncWorker
import org.catrobat.catroid.ui.recyclerview.repository.FeaturedProjectsRepository
import org.catrobat.catroid.ui.recyclerview.repository.ProjectCategoriesRepository
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.catrobat.catroid.utils.combineWith
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainFragmentViewModel(
    private val workManager: WorkManager,
    private val featuredProjectsRepository: FeaturedProjectsRepository,
    private val projectCategoriesRepository: ProjectCategoriesRepository,
    private val connectionMonitor: NetworkConnectionMonitor
) : ViewModel() {
    private val projectList = MutableLiveData<List<ProjectData>>()

    fun getProjects(): LiveData<List<ProjectData>> = projectList

    private fun getProjectData(): List<ProjectData> {
        val myProjects = mutableListOf<ProjectData>()
        FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles()?.forEach { projectDir ->
            val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
            if (xmlFile.exists()) {
                val metaDataParser = ProjectMetaDataParser(xmlFile)
                try {
                    myProjects.add(metaDataParser.projectMetaData)
                } catch (e: IOException) {
                    Log.e(javaClass.simpleName, "Project not parsable", e)
                }
            }
        }
        return myProjects.sortedByDescending { it.lastUsed }
    }

    fun forceUpdate() {
        projectList.postValue(getProjectData())
    }

    init {
        update()
    }

    fun update() {
        workManager.enqueueUniquePeriodicWork(
            FEATURED_PROJECTS_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            createPeriodicWorkerRequestOf(FeaturedProjectSyncWorker::class.java)
        )

        workManager.enqueueUniquePeriodicWork(
            PROJECTS_CATEGORIES_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            createPeriodicWorkerRequestOf(ProjectsCategoriesSyncWorker::class.java)
        )
    }

    private val isLoadingData = MutableLiveData<Boolean>(true)

    fun isLoading(): LiveData<Boolean> = isLoadingData

    fun setIsLoading(loading: Boolean = false) {
        isLoadingData.postValue(loading)
    }

    fun connectionStatusAndFeaturedProjectsAndProjectCategoriesLiveData() =
        connectionMonitor.combineWith(
            featuredProjectsRepository.getFeaturedProjects().asLiveData(),
            projectCategoriesRepository.getProjectsCategories().asLiveData()
        )

    fun registerNetworkCallback() {
        connectionMonitor.registerDefaultNetworkCallback()
    }

    fun unregisterNetworkCallback() {
        connectionMonitor.unregisterDefaultNetworkCallback()
    }

    private fun createPeriodicWorkerRequestOf(workerClass: Class<out ListenableWorker?>):
        PeriodicWorkRequest {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        return PeriodicWorkRequest.Builder(
            workerClass,
            REPEATED_INTERVAL,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_DELAY,
                TimeUnit.SECONDS
            )
            .build()
    }

    companion object {
        private const val FEATURED_PROJECTS_WORK_NAME = "featured_projects_work"
        private const val PROJECTS_CATEGORIES_WORK_NAME = "projects_categories_work"
        private const val REPEATED_INTERVAL = 1L
        private const val BACKOFF_DELAY = 20L
    }
}
