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

package org.catrobat.catroid.ui.fragment

import android.Manifest.permission
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.databinding.FragmentProjectOptionsBinding
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectExportTask
import org.catrobat.catroid.io.asynctask.ProjectLoadTask

import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.ProjectUploadActivity
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.utils.setVisibleOrGone
import org.koin.android.ext.android.inject
import java.io.IOException

class ProjectOptionsFragment : Fragment() {

    private var _binding: FragmentProjectOptionsBinding? = null
    private val binding get() = _binding!!

    private val projectManager: ProjectManager by inject()
    private lateinit var currentProject: Project
    private lateinit var sceneName: String
    private var deleted = false

    companion object {
        val TAG = ProjectOptionsFragment::class.java.simpleName
        const val PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 802
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity)
            .supportActionBar?.setTitle(R.string.project_options)

        currentProject = projectManager.currentProject
        sceneName = projectManager.currentlyEditedScene.name

        handleChangeProjectName()
        handleChangeProjectDescription()
        handleChangeProjectNotesAndCredits()
        handleProjectTags()
        handleProjectAspectRatio()
        handleProjectUpload()

        binding.projectOptionsSaveExternal.setOnClickListener {
            exportProject()
        }

        binding.projectOptionsMoreDetails.setOnClickListener {
            moreDetails()
        }

        binding.projectOptionsDelete.setOnClickListener {
            handleDeleteButtonPressed()
        }

        BottomBar.hideBottomBar(requireActivity())
    }

    private fun handleDeleteButtonPressed() {
        val projectData = ProjectData(
            currentProject.name,
            currentProject.directory,
            currentProject.catrobatLanguageVersion,
            currentProject.hasScene()
        )
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getQuantityString(R.plurals.delete_projects, 1))
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ -> deleteProject(projectData) }
            .setNegativeButton(R.string.no, null)
            .setCancelable(false)
            .show()
    }

    private fun handleProjectUpload() {
        binding.projectOptionsUpload.setOnClickListener {
            ProjectSaver(currentProject, requireContext())
                .saveProjectAsync({ onSaveProjectComplete() })
            Utils.setLastUsedProjectName(requireContext(), currentProject.name)
        }
    }

    private fun onSaveProjectComplete() {
        Intent(
            requireActivity(),
            ProjectUploadActivity::class.java
        ).apply {
            putExtra(
                ProjectUploadActivity.PROJECT_DIR,
                currentProject.directory
            )
        }.let { intent -> startActivity(intent) }
    }

    private fun handleProjectAspectRatio() {
        binding.projectOptionsAspectRatio.apply {
            isChecked = currentProject.screenMode == ScreenModes.STRETCH
            setOnCheckedChangeListener { _, checked: Boolean ->
                val screenMode = if (checked) {
                    ScreenModes.STRETCH
                } else {
                    ScreenModes.MAXIMIZE
                }
                currentProject.screenMode = screenMode
            }
        }
    }

    private fun handleChangeProjectName() {
        binding.projectOptionsNameLayout.editText?.apply {
            setText(currentProject.name)
            addTextChangedListener(object : NewProjectNameTextWatcher<Nameable>() {
                override fun afterTextChanged(s: Editable?) {
                    val error = if (s.toString() != currentProject.name) {
                        validateInput(s.toString(), getContext())
                    } else {
                        null
                    }
                    binding.projectOptionsNameLayout.error = error
                    setProjectName()
                }
            })
        }
    }

    private fun handleChangeProjectDescription() {
        binding.projectOptionsDescriptionLayout
            .editText?.apply {
                setText(currentProject.description)
                doAfterTextChanged {
                    saveDescription()
                }
            }
    }

    private fun handleChangeProjectNotesAndCredits() {
        binding.projectOptionsNotesAndCreditsLayout
            .editText?.apply {
                setText(currentProject.notesAndCredits)
                doAfterTextChanged {
                    saveCreditsAndNotes()
                }
            }
    }

    private fun handleProjectTags() {
        binding.chipGroupTags.removeAllViews()
        val tags = projectManager.currentProject.tags
        val tagsLayout = binding.tags
        if (tags.size == 1 && tags[0].isEmpty()) {
            tagsLayout.setVisibleOrGone(false)
            return
        }
        tagsLayout.setVisibleOrGone(true)
        for (tag in tags) {
            val chip = Chip(requireContext())
            chip.text = tag
            chip.isClickable = false
            binding.chipGroupTags.addView(chip)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        projectManager.currentProject = currentProject
        binding.apply {
            projectOptionsNameLayout.editText?.setText(currentProject.name)
            projectOptionsDescriptionLayout.editText?.setText(currentProject.description)
            projectOptionsNotesAndCreditsLayout.editText?.setText(currentProject.notesAndCredits)
        }

        handleProjectTags()
        BottomBar.hideBottomBar(requireActivity())
    }

    override fun onPause() {
        saveProject()
        super.onPause()
    }

    private fun saveProject() {
        if (!deleted) {
            saveProjectSerial(currentProject, requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProjectName() {
        val name = binding.projectOptionsNameLayout.editText?.text.toString().trim()
        val sceneName = projectManager.currentlyEditedScene.name
        if (currentProject.name != name) {
            XstreamSerializer.getInstance().saveProject(currentProject)
            val renamedDirectory = renameProject(currentProject.directory, name)
            if (renamedDirectory == null) {
                Log.e(TAG, "Creating renamed directory failed!")
                return
            }
            ProjectLoadTask.task(renamedDirectory, requireActivity().applicationContext)
            currentProject = projectManager.currentProject
            projectManager.currentlyEditedScene = currentProject.getSceneByName(sceneName)
        }
    }

    private fun saveDescription() {
        val description = binding.projectOptionsDescriptionLayout.editText?.text.toString().trim()

        if (currentProject.description == null || currentProject.description != description) {
            currentProject.description = description
            if (!XstreamSerializer.getInstance().saveProject(currentProject)) {
                ToastUtil.showError(requireContext(), R.string.error_set_description)
            }
        }
    }

    private fun saveCreditsAndNotes() {
        val notesAndCredits = binding.projectOptionsNotesAndCreditsLayout
            .editText?.text.toString().trim()

        if (currentProject.notesAndCredits == null ||
            currentProject.notesAndCredits != notesAndCredits
        ) {
            currentProject.notesAndCredits = notesAndCredits
            if (!XstreamSerializer.getInstance().saveProject(currentProject)) {
                ToastUtil.showError(requireContext(), R.string.error_set_notes_and_credits)
            }
        }
    }

    private fun deleteProject(selectedProject: ProjectData) {
        try {
            StorageOperations.deleteDir(selectedProject.directory)
            deleted = true
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        ToastUtil.showSuccess(
            requireContext(),
            resources.getQuantityString(
                R.plurals.deleted_projects,
                1,
                1
            )
        )

        findNavController().popBackStack()
    }

    private fun exportProject() {
        saveProject()
        object : RequiresPermissionTask(
            PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE,
            listOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                val notificationData = StatusBarNotificationManager(requireContext())
                    .createSaveProjectToExternalMemoryNotification(
                        requireContext().applicationContext,
                        currentProject.name
                    )

                ProjectExportTask(
                    currentProject.directory,
                    notificationData,
                    requireContext().applicationContext
                )
                    .execute()
            }
        }.execute(requireActivity())
    }

    private fun moreDetails() {
        val projectData = ProjectData(
            currentProject.name,
            currentProject.directory,
            currentProject.catrobatLanguageVersion,
            currentProject.hasScene()
        )

        val direction =
            ProjectOptionsFragmentDirections.navigateToProjectDetailsFragment(projectData)
        findNavController().navigate(direction)
    }
}
