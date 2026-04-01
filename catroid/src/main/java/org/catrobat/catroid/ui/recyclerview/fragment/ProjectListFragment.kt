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
package org.catrobat.catroid.ui.recyclerview.fragment

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.PluralsRes
import androidx.annotation.RequiresApi
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectCopier
import org.catrobat.catroid.io.asynctask.ProjectLoader
import org.catrobat.catroid.io.asynctask.ProjectLoader.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.ProjectRenamer
import org.catrobat.catroid.io.asynctask.ProjectUnZipperAndImporter
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.filepicker.FilePickerActivity
import org.catrobat.catroid.ui.fragment.ProjectOptionsFragment
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException

@SuppressLint("NotifyDataSetChanged")
class ProjectListFragment : RecyclerViewFragment<ProjectData?>(), ProjectLoadListener {
    private var filesForUnzipAndImportTask: ArrayList<File>? = null
    private var hasUnzipAndImportTaskFinished = false

    private val projectManager: ProjectManager by inject()

    override fun onActivityCreated(savedInstance: Bundle?) {
        super.onActivityCreated(savedInstance)
        filesForUnzipAndImportTask = ArrayList()
        hasUnzipAndImportTaskFinished = true
        if (arguments != null) {
            importProject(requireArguments().getParcelable("intent"))
        }
        if (requireActivity().intent?.hasExtra(ProjectListActivity.IMPORT_LOCAL_INTENT) == true) {
            adapter.showSettings = false
            actionModeType = IMPORT_LOCAL
        }
    }

    private fun onImportProjectFinished(success: Boolean) {
        setAdapterItems(adapter.projectsSorted)
        if (!success) {
            ToastUtil.showError(requireContext(), R.string.error_import_project)
        } else {
            ToastUtil.showSuccess(
                requireContext(),
                resources.getQuantityString(
                    R.plurals.imported_projects,
                    filesForUnzipAndImportTask?.size ?: 0,
                    filesForUnzipAndImportTask?.size ?: 0
                )
            )
        }
        filesForUnzipAndImportTask?.clear()
        setShowProgressBar(false)
    }

    private fun onRenameFinished(success: Boolean) {
        if (success) {
            if (hasUnzipAndImportTaskFinished) {
                ToastUtil.showSuccess(
                    requireContext(),
                    getString(R.string.renamed_project)
                )
                filesForUnzipAndImportTask?.clear()
            }
            setAdapterItems(adapter.projectsSorted)
        } else {
            ToastUtil.showError(requireContext(), R.string.error_rename_incompatible_project)
        }
        setShowProgressBar(false)
    }

    override fun onResume() {
        if (actionModeType != IMPORT_LOCAL) {
            projectManager.currentProject = null
        }

        setAdapterItems(adapter.projectsSorted)
        checkForEmptyList()
        BottomBar.showBottomBar(requireActivity())
        super.onResume()
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY
        adapter = ProjectAdapter(itemList)
        onAdapterReady()
    }

    private val itemList: List<ProjectData>
        get() {
            val items: MutableList<ProjectData> = ArrayList()
            getLocalProjectList(items)
            items.sortWith(Comparator { project1: ProjectData, project2: ProjectData ->
                project2.lastUsed.compareTo(project1.lastUsed)
            })
            return items
        }

    private val sortedItemList: List<ProjectData>
        get() {
            val items: MutableList<ProjectData> = ArrayList()
            getLocalProjectList(items)
            items.sortWith(Comparator { project1: ProjectData, project2: ProjectData ->
                project1.name.compareTo(
                    project2.name
                )
            })
            return items
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.import_project -> showImportChooser()
            R.id.sort_projects -> sortProjects()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun sortProjects() {
        adapter.projectsSorted = !adapter.projectsSorted
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .edit()
            .putBoolean(
                SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY,
                adapter.projectsSorted
            )
            .apply()
        setAdapterItems(adapter.projectsSorted)
    }

    private fun showImportChooser() {
        setShowProgressBar(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            importUsingSystemFilePicker()
        } else {
            importUsingFilePickerActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun importUsingSystemFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }
        val title = requireContext().resources.getString(R.string.import_project)
        startActivityForResult(Intent.createChooser(intent, title), REQUEST_IMPORT_PROJECT)
    }

    private fun importUsingFilePickerActivity() {
        object : RequiresPermissionTask(
            PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
            listOf(permission.READ_EXTERNAL_STORAGE),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                startActivityForResult(
                    Intent(requireContext(), FilePickerActivity::class.java),
                    REQUEST_IMPORT_PROJECT
                )
            }
        }.execute(requireActivity())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMPORT_PROJECT && resultCode == RESULT_OK) {
            importProject(data)
        }
    }

    private fun importProject(data: Intent?) {
        if (data == null) {
            onImportError()
            return
        }
        var uris: ArrayList<Uri> = ArrayList()
        if (data.data == null && !data.hasExtra(Intent.EXTRA_STREAM) && data.clipData == null) {
            onImportError()
            return
        }
        if (data.hasExtra(Intent.EXTRA_STREAM)) {
            uris = data.extras?.get(Intent.EXTRA_STREAM) as ArrayList<Uri>
        } else {
            extractAllUris(data, uris)
        }
        try {
            importProjectUris(uris)
        } catch (e: IOException) {
            Log.e(TAG, "Cannot resolve project to import.", e)
        }
    }

    private fun onImportError() {
        setShowProgressBar(false)
        ToastUtil.showError(requireContext(), R.string.error_import_project)
    }

    private fun extractAllUris(data: Intent, uris: ArrayList<Uri>) {
        val singleUri = data.data
        if (singleUri != null) {
            uris.add(singleUri)
        } else {
            val clipData = data.clipData
            if (clipData != null) {
                val itemCount = clipData.itemCount
                for (idx in 0 until itemCount) {
                    uris.add(clipData.getItemAt(idx).uri)
                }
            }
        }
    }

    private fun importProjectUris(uris: ArrayList<Uri>) {
        prepareFilesForImport(uris)
        filesForUnzipAndImportTask?.apply {
            if (isNotEmpty()) {
                val filesToUnzipAndImport = filesForUnzipAndImportTask?.toTypedArray() ?: arrayOf()
                ProjectUnZipperAndImporter({ success: Boolean -> onImportProjectFinished(success) })
                    .unZipAndImportAsync(filesToUnzipAndImport)
            }
        }
    }

    private fun prepareFilesForImport(urisToImport: ArrayList<Uri>) {
        for (uri in urisToImport) {
            val contentResolver = requireActivity().contentResolver
            var fileName = StorageOperations.resolveFileName(contentResolver, uri)
            if (!fileName.contains(Constants.CATROBAT_EXTENSION)) {
                ToastUtil.showError(requireContext(), R.string.only_select_catrobat_files)
                continue
            }
            fileName = fileName.replace(Constants.CATROBAT_EXTENSION, Constants.ZIP_EXTENSION)
            copyFileContentToCacheFile(uri, fileName)
        }
    }

    private fun copyFileContentToCacheFile(uri: Uri, fileName: String) {
        val projectFile = StorageOperations.copyUriToDir(
            requireActivity().contentResolver, uri,
            Constants.CACHE_DIRECTORY, fileName
        )
        filesForUnzipAndImportTask?.add(projectFile)
        hasUnzipAndImportTaskFinished = false
    }

    override fun prepareActionMode(@ActionModeType type: Int) {
        if (type == COPY) {
            adapter.selectionMode = RVAdapter.MULTIPLE
        } else if (type == MERGE) {
            adapter.selectionMode = RVAdapter.PAIRS
        }
        super.prepareActionMode(type)
    }

    override fun packItems(selectedItems: MutableList<ProjectData?>?) {
        throw IllegalStateException("$TAG: Projects cannot be backpacked")
    }

    override fun isBackpackEmpty(): Boolean = true

    override fun switchToBackpack() {
        throw IllegalStateException("$TAG: Projects cannot be backpacked")
    }

    override fun copyItems(selectedItems: MutableList<ProjectData?>?) {
        finishActionMode()
        setShowProgressBar(true)
        selectedItems ?: return
        val usedProjectNames = ArrayList(adapter.items)
        for (projectData in selectedItems) {
            projectData ?: continue
            val name = uniqueNameProvider.getUniqueNameInNameables(projectData.name, usedProjectNames)
            usedProjectNames.add(ProjectData(name, null, 0.0, false))
            val projectCopier = ProjectCopier(projectData.directory, name)
            projectCopier.copyProjectAsync({ success: Boolean -> onCopyProjectComplete(success) })
        }
    }

    @PluralsRes
    override fun getDeleteAlertTitleId(): Int = R.plurals.delete_projects

    override fun deleteItems(selectedItems: MutableList<ProjectData?>?) {
        setShowProgressBar(true)
        selectedItems ?: return
        for (item in selectedItems) {
            item ?: continue
            try {
                projectManager.deleteDownloadedProjectInformation(item.name)
                StorageOperations.deleteDir(item.directory)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            adapter.remove(item)
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

    override fun getRenameDialogTitle(): Int = R.string.rename_project

    override fun getRenameDialogHint(): Int = R.string.project_name_label

    override fun renameItem(item: ProjectData?, name: String?) {
        finishActionMode()
        item ?: return
        name ?: return
        if (name != item.name) {
            setShowProgressBar(true)
            ProjectRenamer(item.directory, name)
                .renameProjectAsync({ success: Boolean -> onRenameFinished(success) })
        }
    }

    override fun onLoadFinished(success: Boolean) {
        if (success) {
            val intent = Intent(requireContext(), ProjectActivity::class.java)
            intent.putExtra(
                ProjectActivity.EXTRA_FRAGMENT_POSITION,
                ProjectActivity.FRAGMENT_SCENES
            )
            startActivity(intent)
        } else {
            setShowProgressBar(false)
            ToastUtil.showError(requireContext(), R.string.error_load_project)
        }
    }

    private fun onCopyProjectComplete(success: Boolean) {
        if (success) {
            setAdapterItems(adapter.projectsSorted)
        } else {
            ToastUtil.showError(requireContext(), R.string.error_copy_project)
        }
        setShowProgressBar(false)
    }

    override fun onItemClick(item: ProjectData?, selectionManager: MultiSelectionManager?) {
        when (actionModeType) {
            RENAME -> {
                super.onItemClick(item, null)
                return
            }
            NONE -> {
                setShowProgressBar(true)
                val directoryFile = item?.directory ?: return
                ProjectLoader(directoryFile, requireContext()).setListener(this).loadProjectAsync()
            }
            IMPORT_LOCAL -> {
                val intent = Intent()
                intent.putExtra(
                    ProjectListActivity.IMPORT_LOCAL_INTENT,
                    item?.directory?.absoluteFile?.absolutePath
                )
                requireActivity().setResult(RESULT_OK, intent)
                requireActivity().finish()
            }
            else -> super.onItemClick(item, selectionManager)
        }
    }

    override fun onItemLongClick(item: ProjectData?, holder: CheckableViewHolder?) {
        onItemClick(item, null)
    }

    override fun onSettingsClick(item: ProjectData?, view: View?) {
        val itemList: MutableList<ProjectData?> = ArrayList()
        itemList.add(item)
        val hiddenMenuOptionIds = intArrayOf(
            R.id.new_group, R.id.new_scene, R.id.show_details,
            R.id.from_library, R.id.from_local, R.id.edit
        )
        val popupMenu = UiUtils.createSettingsPopUpMenu(
            view, requireContext(),
            R.menu.menu_project_activity, hiddenMenuOptionIds
        )
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.copy -> copyItems(itemList)
                R.id.rename -> showRenameDialog(item)
                R.id.delete -> deleteItems(itemList)
                R.id.project_options -> showProjectOptionsFragment(item)
            }
            true
        }
        popupMenu.show()
    }

    private fun showProjectOptionsFragment(item: ProjectData?) {
        item ?: return
        try {
            val project = XstreamSerializer.getInstance().loadProject(
                item.directory,
                requireContext()
            )
            projectManager.currentProject = project
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container, ProjectOptionsFragment(), ProjectOptionsFragment.TAG
                )
                .addToBackStack(ProjectOptionsFragment.TAG)
                .commit()
        } catch (exception: IOException) {
            ToastUtil.showError(requireContext(), R.string.error_load_project)
            Log.e(TAG, Log.getStackTraceString(exception))
        } catch (exception: LoadingProjectException) {
            ToastUtil.showError(requireContext(), R.string.error_load_project)
            Log.e(TAG, Log.getStackTraceString(exception))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        adapter.projectsSorted = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getBoolean(SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY, false)
        menu.findItem(R.id.sort_projects)
            .setTitle(
                if (adapter.projectsSorted) {
                    R.string.unsort_projects
                } else {
                    R.string.sort_projects
                }
            )
    }

    private fun setAdapterItems(sortProjects: Boolean) {
        if (sortProjects) {
            adapter.setItems(sortedItemList)
        } else {
            adapter.setItems(itemList)
        }
        adapter.notifyDataSetChanged()
    }

    interface ProjectImportFinishedListener {
        fun notifyActivityFinished(success: Boolean)
    }

    companion object {
        @JvmStatic
        val TAG: String = ProjectListFragment::class.java.simpleName
        private const val PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801
        private const val REQUEST_IMPORT_PROJECT = 7

        @JvmStatic
        fun getLocalProjectList(items: MutableList<ProjectData>) {
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles()?.forEach { projectDir ->
                val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
                if (!xmlFile.exists()) {
                    return@forEach
                }
                val metaDataParser = ProjectMetaDataParser(xmlFile)
                try {
                    items.add(metaDataParser.projectMetaData)
                } catch (exception: IOException) {
                    Log.e(TAG, "Could no parse local project.", exception)
                }
            }
        }
    }
}
