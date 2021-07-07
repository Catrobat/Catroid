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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.Manifest.permission
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.PluralsRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY
import org.catrobat.catroid.common.SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectCopyTask
import org.catrobat.catroid.io.asynctask.ProjectImportTask
import org.catrobat.catroid.io.asynctask.ProjectImportTask.ProjectImportListener
import org.catrobat.catroid.io.asynctask.ProjectLoadTask
import org.catrobat.catroid.io.asynctask.ProjectRenamer
import org.catrobat.catroid.io.asynctask.ProjectUnZipperAndImporter
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.filepicker.FilePickerActivity
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.util.ArrayList

class ProjectListFragment : RecyclerViewFragment<ProjectData>(),
    ProjectLoadTask.ProjectLoadListener,
    ProjectCopyTask.ProjectCopyListener {

    val args by navArgs<ProjectListFragmentArgs>()
    private val projectManager: ProjectManager by inject()

    companion object {
        val TAG = ProjectListFragment::class.simpleName
        private const val PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801
        private const val REQUEST_IMPORT_PROJECT = 7

        @JvmStatic
        fun getLocalProjectList(): MutableList<ProjectData> {
            val items = mutableListOf<ProjectData>()
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles()?.forEach { projectDir ->
                val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
                if (xmlFile.exists()) {
                    val metaDataParser = ProjectMetaDataParser(xmlFile)
                    try {
                        items.add(metaDataParser.projectMetaData)
                    } catch (e: IOException) {
                        Log.e(TAG, "Well, that's awkward.", e)
                    }
                }
            }
            return items
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.intent?.let {
            importProject(it)
        }
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SHOW_DETAILS_PROJECTS_PREFERENCE_KEY
        adapter = ProjectAdapter(getItemList())
        onAdapterReady()
    }

    private fun getItemList(): List<ProjectData> {
        return getLocalProjectList()
            .sortedBy { it.lastUsed }
            .toMutableList()
    }

    private fun getSortedItemList(): List<ProjectData> {
        return getLocalProjectList()
            .sortedBy { it.name }
            .toMutableList()
    }

    override fun packItems(selectedItems: MutableList<ProjectData>?) {
        throw IllegalStateException("$TAG +: Projects cannot be backpacked")
    }

    override fun isBackpackEmpty() = true

    override fun switchToBackpack() {
        throw IllegalStateException("$TAG +: Projects cannot be backpacked")
    }

    override fun copyItems(selectedItems: MutableList<ProjectData>) {
        finishActionMode()
        setShowProgressBar(true)

        val usedProjectNames = ArrayList<Nameable>(adapter.items)

        selectedItems.forEach { projectData ->
            val name = uniqueNameProvider.getUniqueNameInNameables(
                projectData.name,
                usedProjectNames
            )
            usedProjectNames.add(ProjectData(name, null, 0.0, false))
            ProjectCopyTask(projectData.directory, name)
                .setListener(this)
                .execute()
        }
    }

    @PluralsRes
    override fun getDeleteAlertTitleId() = R.plurals.delete_projects

    override fun deleteItems(selectedItems: MutableList<ProjectData>) {
        setShowProgressBar(true)
        selectedItems.forEach { projectData ->
            try {
                projectManager.deleteDownloadedProjectInformation(projectData.name)
                StorageOperations.deleteDir(projectData.directory)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }

            adapter.remove(projectData)
        }

        ToastUtil.showSuccess(
            requireContext(), resources.getQuantityString(
                R.plurals.deleted_projects,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()

        setAdapterItems(adapter.projectsSorted)

        checkForEmptyList()
    }

    fun checkForEmptyList() {
        if (adapter.items.isEmpty()) {
            setShowProgressBar(true)
            if (projectManager.initializeDefaultProject()) {
                setAdapterItems(adapter.projectsSorted)
                setShowProgressBar(false)
            } else {
                ToastUtil.showError(requireContext(), R.string.wtf_error)
                requireActivity().finish()
            }
        }
    }

    override fun prepareActionMode(type: Int) {
        if (type == COPY) {
            adapter.selectionMode = RVAdapter.MULTIPLE
        } else if (type == MERGE) {
            adapter.selectionMode = RVAdapter.PAIRS
        }
        super.prepareActionMode(type)
    }

    override fun getRenameDialogTitle() = R.string.rename_project

    override fun getRenameDialogHint() = R.string.project_name_label

    override fun renameItem(item: ProjectData, newName: String) {
        finishActionMode()

        if (newName != item.name) {
            setShowProgressBar(true)
            ProjectRenamer(item.directory, newName)
                .renameProjectAsync(this::onRenameFinished)
        }
    }

    override fun onLoadFinished(success: Boolean) {
        if (success) {
            Intent(requireActivity(), ProjectActivity::class.java).apply {
                putExtra(
                    ProjectActivity.EXTRA_FRAGMENT_POSITION,
                    ProjectActivity.FRAGMENT_SCENES
                )
            }.let {
                startActivity(it)
            }
        } else {
            setShowProgressBar(false)
            ToastUtil.showError(requireContext(), R.string.error_load_project)
        }
    }

    override fun onCopyFinished(success: Boolean) {
        if (success) {
            setAdapterItems(adapter.projectsSorted)
        } else {
            ToastUtil.showError(requireContext(), R.string.error_copy_project)
        }
        setShowProgressBar(false)
    }

    private fun onRenameFinished(success: Boolean) {
        if (success) {
            setAdapterItems(adapter.projectsSorted)
        } else {
            ToastUtil.showError(requireContext(), R.string.error_rename_incompatible_project)
        }

        setShowProgressBar(false)
    }

    override fun onResume() {
        (requireActivity() as AppCompatActivity)
            .supportActionBar?.setTitle(R.string.project_list_title)
        projectManager.currentProject = null

        setAdapterItems(adapter.projectsSorted)
        checkForEmptyList()

        BottomBar.showBottomBar(requireActivity())
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        adapter.projectsSorted = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getBoolean(SORT_PROJECTS_PREFERENCE_KEY, false)

        val title = if (adapter.projectsSorted) {
            R.string.unsort_projects
        } else {
            R.string.sort_projects
        }

        menu.findItem(R.id.sort_projects)
            .setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.import_project -> showImportChooser()
            R.id.sort_projects -> {
                adapter.projectsSorted = !adapter.projectsSorted
                PreferenceManager.getDefaultSharedPreferences(requireActivity())
                    .edit()
                    .putBoolean(SORT_PROJECTS_PREFERENCE_KEY, adapter.projectsSorted)
                    .apply()
                setAdapterItems(adapter.projectsSorted)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun setAdapterItems(sortProjects: Boolean) {
        if (sortProjects) {
            adapter.setItems(getSortedItemList())
        } else {
            adapter.setItems(getItemList())
        }
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(item: ProjectData) {
        if (actionModeType == RENAME) {
            super.onItemClick(item)
            return
        }

        if (actionModeType == NONE) {
            setShowProgressBar(true)
            ProjectLoadTask(item.directory, requireContext())
                .setListener(this)
                .execute()
        }
    }

    override fun onItemLongClick(item: ProjectData, holder: CheckableVH?) {
        val items = arrayOf<CharSequence>(
            getString(R.string.copy),
            getString(R.string.project_options)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(item.name)
            .setItems(
                items
            ) { dialog: DialogInterface, which: Int ->
                when (which) {
                    0 -> copyItems(ArrayList(listOf(item)))
                    1 -> {
                        try {
                            val project = XstreamSerializer.getInstance()
                                .loadProject(item.directory, requireContext())
                            projectManager.currentProject = project
                        } catch (e: IOException) {
                            ToastUtil.showError(requireContext(), R.string.error_load_project)
                            Log.e(TAG, Log.getStackTraceString(e))
                        } catch (e: LoadingProjectException) {
                            ToastUtil.showError(requireContext(), R.string.error_load_project)
                            Log.e(TAG, Log.getStackTraceString(e))
                        }

                        val direction = ProjectListFragmentDirections
                            .navigateToProjectOptionsFragment()
                        findNavController().navigate(direction)
                    }
                    else -> dialog.dismiss()
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMPORT_PROJECT && resultCode == Activity.RESULT_OK) {
            importProject(data)
        }
    }

    private fun showImportChooser() {
        setShowProgressBar(true)

        val requiresPermissionTask = object : RequiresPermissionTask(
            PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
            listOf(permission.READ_EXTERNAL_STORAGE),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                startActivityForResult(
                    Intent(requireContext(), FilePickerActivity::class.java), REQUEST_IMPORT_PROJECT
                )
            }
        }
        requiresPermissionTask.execute(requireActivity())
    }

    private fun onImportFinished(success: Boolean) {
        setAdapterItems(adapter.projectsSorted)
        if (!success) {
            ToastUtil.showError(requireContext(), R.string.error_import_project)
        }
        setShowProgressBar(false)
    }

    private val projectImportListener = ProjectImportListener { success ->
        setAdapterItems(adapter.projectsSorted)
        if (!success) {
            ToastUtil.showError(requireContext(), R.string.error_import_project)
        }
        setShowProgressBar(false)
    }

    private fun importProject(data: Intent?) {
        if (data == null || data.data == null) {
            setShowProgressBar(false)
            ToastUtil.showError(requireContext(), R.string.error_import_project)
            return
        }

        try {
            val cacheFile = File(Constants.CACHE_DIR, Constants.CACHED_PROJECT_ZIP_FILE_NAME)
            if (cacheFile.exists()) {
                cacheFile.delete()
            }
            val src = File(data.data?.path!!)
            if (src.isDirectory) {
                ProjectImportTask()
                    .setListener(projectImportListener)
                    .execute(src)
            } else {
                val projectFile = StorageOperations
                    .copyUriToDir(
                        requireContext().contentResolver,
                        data.data,
                        Constants.CACHE_DIR,
                        Constants.CACHED_PROJECT_ZIP_FILE_NAME
                    )
                ProjectUnZipperAndImporter({ success: Boolean -> onImportFinished(success) })
                    .unZipAndImportAsync(arrayOf(projectFile))
            }
            setShowProgressBar(true)
        } catch (e: IOException) {
            Log.e(TAG, "Cannot resolve project to import.", e)
        }
    }
}
