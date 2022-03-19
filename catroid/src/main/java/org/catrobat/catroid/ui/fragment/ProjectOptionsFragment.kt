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
package org.catrobat.catroid.ui.fragment

import android.Manifest.permission
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.databinding.FragmentProjectOptionsBinding
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectExportTask
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.PROJECT_DIR
import org.catrobat.catroid.ui.ProjectUploadActivity
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException

class ProjectOptionsFragment : Fragment() {

    private val projectManager: ProjectManager by inject()
    private var _binding: FragmentProjectOptionsBinding? = null
    private val binding get() = _binding!!
    private var project: Project? = null
    private var sceneName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.project_options)

        project = projectManager.currentProject
        sceneName = projectManager.currentlyEditedScene.name

        setupNameInputLayout()
        setupDescriptionInputLayout()
        setupNotesAndCreditsInputLayout()
        addTags()
        setupProjectAspectRatio()
        setupProjectUpload()
        setupProjectSaveExternal()
        setupProjectMoreDetails()
        setupProjectOptionDelete()

        hideBottomBar(requireActivity())
    }

    private fun setupNameInputLayout() {
        binding.projectOptionsNameLayout.editText?.apply {
            setText(project?.name)
            addTextChangedListener(object : NewProjectNameTextWatcher<Nameable>() {
                override fun afterTextChanged(s: Editable?) {
                    val error = if (s.toString() != project!!.name) {
                        validateInput(s.toString(), getContext())
                    } else {
                        null
                    }
                    binding.projectOptionsNameLayout.error = error
                }
            })
        }
    }

    private fun setupDescriptionInputLayout() {
        binding.projectOptionsDescriptionLayout.editText?.setText(project?.description)
    }

    private fun setupNotesAndCreditsInputLayout() {
        binding.projectOptionsNotesAndCreditsLayout.editText?.setText(project?.notesAndCredits)
    }

    private fun addTags() {
        binding.chipGroupTags.removeAllViews()
        val tags = project!!.tags

        if (tags.size == 1 && tags[0].isEmpty()) {
            binding.tags.visibility = View.GONE
            return
        }
        binding.tags.visibility = View.VISIBLE
        for (tag in tags) {
            val chip = Chip(context)
            chip.text = tag
            chip.isClickable = false
            binding.chipGroupTags.addView(chip)
        }
    }

    private fun setupProjectAspectRatio() {
        binding.projectOptionsAspectRatio.apply {
            isChecked = project?.screenMode == ScreenModes.STRETCH
            setOnCheckedChangeListener { _, isChecked ->
                handleAspectRatioChecked(isChecked)
            }
        }
    }

    private fun setupProjectUpload() {
        binding.projectOptionsUpload.setOnClickListener {
            projectUpload()
        }
    }

    private fun setupProjectSaveExternal() {
        binding.projectOptionsSaveExternal.setOnClickListener {
            exportProject()
        }
    }

    private fun setupProjectMoreDetails() {
        binding.projectOptionsMoreDetails.setOnClickListener {
            moreDetails()
        }
    }

    private fun setupProjectOptionDelete() {
        binding.projectOptionsDelete.setOnClickListener {
            handleDeleteButtonPressed()
        }
    }

    private fun handleAspectRatioChecked(checked: Boolean) {
        project?.screenMode = if (checked) {
            ScreenModes.STRETCH
        } else {
            ScreenModes.MAXIMIZE
        }
    }

    private fun handleDeleteButtonPressed() {
        project ?: return

        val projectData = ProjectData(
            project!!.name,
            project!!.directory,
            project!!.catrobatLanguageVersion,
            project!!.hasScene()
        )
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getQuantityString(R.plurals.delete_projects, 1))
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                deleteProject(
                    projectData
                )
            }
            .setNegativeButton(R.string.no, null)
            .setCancelable(false)
            .show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        saveProject()
        super.onPause()
    }

    private fun saveProject() {
        project ?: return
        setProjectName()
        saveDescription()
        saveCreditsAndNotes()
        saveProjectSerial(project, requireContext())
    }

    override fun onResume() {
        super.onResume()

        projectManager.currentProject = project
        binding.projectOptionsNameLayout.editText?.setText(project?.name)
        setupDescriptionInputLayout()
        setupNotesAndCreditsInputLayout()

        addTags()
        hideBottomBar(requireActivity())
    }

    private fun setProjectName() {
        val name = binding.projectOptionsNameLayout.editText?.text.toString().trim()
        project ?: return

        if (project!!.name != name) {
            XstreamSerializer.getInstance().saveProject(project)
            val renamedDirectory = renameProject(project!!.directory, name)
            if (renamedDirectory == null) {
                Log.e(TAG, "Creating renamed directory failed!")
                return
            }
            loadProject(renamedDirectory, requireContext().applicationContext)
            project = projectManager.currentProject
            projectManager.currentlyEditedScene = project!!.getSceneByName(sceneName)
        }
    }

    fun saveDescription() {
        val description = binding.projectOptionsDescriptionLayout.editText?.text.toString().trim()
        if (project?.description == null || project?.description != description) {
            project?.description = description
            if (!XstreamSerializer.getInstance().saveProject(project)) {
                ToastUtil.showError(activity, R.string.error_set_description)
            }
        }
    }

    fun saveCreditsAndNotes() {
        val notesAndCredits = binding.projectOptionsNotesAndCreditsLayout.editText
            ?.text.toString().trim()
        if (project?.notesAndCredits == null || project?.notesAndCredits != notesAndCredits) {
            project?.notesAndCredits = notesAndCredits
            if (!XstreamSerializer.getInstance().saveProject(project)) {
                ToastUtil.showError(requireContext(), R.string.error_set_notes_and_credits)
            }
        }
    }

    fun projectUpload() {
        val currentProject = projectManager.currentProject
        ProjectSaver(currentProject, requireContext())
            .saveProjectAsync({ onSaveProjectComplete() })
        Utils.setLastUsedProjectName(requireContext(), currentProject.name)
    }

    private fun onSaveProjectComplete() {
        val currentProject = projectManager.currentProject

        if (Utils.isDefaultProject(currentProject, activity)) {
            binding.root.apply {
                Snackbar.make(binding.root, R.string.error_upload_default_project, Snackbar.LENGTH_LONG).show()
            }
            return
        }

        val intent = Intent(requireContext(), ProjectUploadActivity::class.java)
        intent.putExtra(PROJECT_DIR, currentProject.directory)

        startActivity(intent)
    }

    private fun exportProject() {
        saveProject()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportUsingSystemFilePicker()
        } else {
            exportToExternalMemory()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun exportUsingSystemFilePicker() {
        val fileName = project?.name + Constants.CATROBAT_EXTENSION
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        intent.type = "*/*"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        val title = requireContext().getString(R.string.export_project)
        startActivityForResult(Intent.createChooser(intent, title), REQUEST_EXPORT_PROJECT)
    }

    private fun exportToExternalMemory() {
        object : RequiresPermissionTask(
            PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE,
            listOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                val fileName = project?.name + Constants.CATROBAT_EXTENSION
                val projectZip = File(Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, fileName)
                Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.mkdirs()
                if (!Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.isDirectory) {
                    return
                }
                if (projectZip.exists()) {
                    projectZip.delete()
                }
                val projectDestination = Uri.fromFile(projectZip)
                startAsyncProjectExport(projectDestination)
            }
        }.execute(requireActivity())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        if (requestCode == REQUEST_EXPORT_PROJECT && resultCode == Activity.RESULT_OK) {
            val projectDestination = data.data ?: return
            startAsyncProjectExport(projectDestination)
        }
    }

    private fun startAsyncProjectExport(projectDestination: Uri) {
        project?.let {
            val notificationData = StatusBarNotificationManager(requireContext())
                .createSaveProjectToExternalMemoryNotification(
                    requireContext(),
                    projectDestination,
                    it.name
                )
            ProjectExportTask(it.directory, projectDestination, notificationData, requireContext())
                .execute()
        }
    }

    private fun moreDetails() {
        val fragment = ProjectDetailsFragment()
        val args = Bundle()
        project?.let {
            val projectData = ProjectData(
                it.name,
                it.directory,
                it.catrobatLanguageVersion,
                it.hasScene()
            )
            args.putSerializable(ProjectDetailsFragment.SELECTED_PROJECT_KEY, projectData)
        }
        fragment.arguments = args
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, ProjectDetailsFragment.TAG)
            .addToBackStack(ProjectDetailsFragment.TAG).commit()
    }

    private fun deleteProject(selectedProject: ProjectData) {
        try {
            StorageOperations.deleteDir(selectedProject.directory)
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
        ToastUtil.showSuccess(
            requireContext(),
            resources.getQuantityString(R.plurals.deleted_projects, 1, 1)
        )
        project = null
        projectManager.currentProject = project
        requireActivity().onBackPressed()
    }

    companion object {
        val TAG: String = ProjectOptionsFragment::class.java.simpleName

        private const val PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 802
        private const val REQUEST_EXPORT_PROJECT = 10
    }
}
