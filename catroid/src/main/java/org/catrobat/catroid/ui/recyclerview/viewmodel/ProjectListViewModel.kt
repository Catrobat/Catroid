package org.catrobat.catroid.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.content.backwardcompatibility.ProjectRepository
import org.catrobat.catroid.content.backwardcompatibility.ProjectRepository.SortBy

class ProjectListViewModel(val projectRepository: ProjectRepository) : ViewModel() {

    private val _projects = MutableLiveData<List<ProjectData>>()
    val projects: LiveData<List<ProjectData>> get() = _projects

    /**
     * 載入專案資料並更新 LiveData
     */
    fun loadProjects(sortBy: SortBy) {
        viewModelScope.launch {
            _projects.postValue(projectRepository.fetchProjectData(sortBy))

        }
    }
}
