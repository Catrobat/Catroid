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

import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.io.asynctask.renameProject
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.chip.ChipGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import org.catrobat.catroid.R
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import android.text.Editable
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.google.android.material.switchmaterial.SwitchMaterial
import org.catrobat.catroid.common.ScreenModes
import android.widget.CompoundButton
import android.widget.TextView
import org.catrobat.catroid.common.ProjectData
import android.content.DialogInterface
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectLoadTask
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.io.asynctask.ProjectSaver
import android.content.Intent
import org.catrobat.catroid.ui.ProjectUploadActivity
import androidx.annotation.RequiresApi
import android.provider.DocumentsContract
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import android.Manifest.permission
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.io.asynctask.ProjectExportTask
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.PROJECT_DIR
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class ProjectOptionsFragment : Fragment() {
    private var optionsView: View? = null
    private var project: Project? = null
    private var sceneName: String? = null
    private var nameInputLayout: TextInputLayout? = null
    private var descriptionInputLayout: TextInputLayout? = null
    private var notesAndCreditsInputLayout: TextInputLayout? = null
    private var tagsChipGroup: ChipGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        optionsView = inflater.inflate(R.layout.fragment_project_options, container, false)
        setHasOptionsMenu(true)
        val activity = activity as AppCompatActivity?
        activity?.supportActionBar?.setTitle(R.string.project_options)

        project = ProjectManager.getInstance().currentProject
        sceneName = ProjectManager.getInstance().currentlyEditedScene.name
        setupNameInputLayout()
        setupDescriptionInputLayout()
        setupNotesAndCreditsInputLayout()
        tagsChipGroup = optionsView?.findViewById(R.id.chip_group_tags)
        addTags()
        setupProjectAspectRatio()
        setupProjectUpload()
        setupProjectSaveExternal()
        setupProjectMoreDetails()
        setupProjectOptionDelete()
        hideBottomBar(getActivity())
        return optionsView
    }

    private fun setupNameInputLayout() {
        nameInputLayout = optionsView?.findViewById(R.id.project_options_name_layout)
        nameInputLayout?.editText?.let {
            it.setText(project?.name)
            it.addTextChangedListener(object : NewProjectNameTextWatcher<Nameable>() {
                override fun afterTextChanged(editable: Editable) {
                    nameInputLayout?.error = if (editable.toString() != project?.name) {
                        val error = validateInput(editable.toString(), getContext())
                        error
                    } else {
                        null
                    }
                }
            })
        }
    }

    private fun setupDescriptionInputLayout() {
        descriptionInputLayout = optionsView?.findViewById(R.id.project_options_description_layout)
        if (descriptionInputLayout != null) {
            val editText = descriptionInputLayout?.editText
            editText?.setText(project?.description)
        }
    }

    private fun setupNotesAndCreditsInputLayout() {
        notesAndCreditsInputLayout =
            optionsView?.findViewById(R.id.project_options_notes_and_credits_layout)
        notesAndCreditsInputLayout?.apply {
            val editText = editText
            editText?.setText(project?.notesAndCredits)
        }
    }

    private fun addTags() {
        tagsChipGroup?.removeAllViews()
        val tags = project?.tags
        val tagsLayout = optionsView?.findViewById<LinearLayout>(R.id.tags)
        if (tags?.size == 1 && tags[0].isEmpty()) {
            tagsLayout?.visibility = View.GONE
            return
        }
        tagsLayout?.visibility = View.VISIBLE
        if (tags != null) {
            for (tag in tags) {
                context?.let {
                    val chip = Chip(it)
                    chip.text = tag
                    chip.isClickable = false
                    tagsChipGroup?.addView(chip)
                }
            }
        }
    }

    private fun setupProjectAspectRatio() {
        val projectAspectRatio: SwitchMaterial =
            optionsView?.findViewById(R.id.project_options_aspect_ratio) ?: return
        projectAspectRatio.isChecked = project?.screenMode == ScreenModes.STRETCH
        projectAspectRatio.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            handleAspectRatioChecked(checked)
        }
    }

    private fun setupProjectUpload() {
        val projectUpload = optionsView?.findViewById<TextView>(R.id.project_options_upload)
        projectUpload?.setOnClickListener { projectUpload() }
    }

    private fun setupProjectSaveExternal() {
        val projectSaveExternal =
            optionsView?.findViewById<TextView>(R.id.project_options_save_external)
        projectSaveExternal?.setOnClickListener { exportProject() }
    }

    private fun setupProjectMoreDetails() {
        val projectMoreDetails =
            optionsView?.findViewById<TextView>(R.id.project_options_more_details)
        projectMoreDetails?.setOnClickListener { moreDetails() }
    }

    private fun setupProjectOptionDelete() {
        val projectOptionsDelete = optionsView?.findViewById<View>(R.id.project_options_delete)
        projectOptionsDelete?.setOnClickListener { handleDeleteButtonPressed() }
    }

    private fun handleAspectRatioChecked(checked: Boolean) {
        if (checked) {
            project?.screenMode = ScreenModes.STRETCH
        } else {
            project?.screenMode = ScreenModes.MAXIMIZE
        }
    }

    private fun handleDeleteButtonPressed() {
        project?.let { thisProject ->
            context?.let {
                val projectData = ProjectData(
                    thisProject.name,
                    thisProject.directory,
                    thisProject.catrobatLanguageVersion,
                    thisProject.hasScene()
                )
                AlertDialog.Builder(it)
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
        }
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
        context?.let {
            saveProjectSerial(project, it)
        }
    }

    override fun onResume() {
        super.onResume()
        ProjectManager.getInstance().currentProject = project
        nameInputLayout?.editText?.setText(project?.name)
        descriptionInputLayout?.editText?.setText(project?.description)
        notesAndCreditsInputLayout?.editText?.setText(project?.notesAndCredits)
        addTags()
        hideBottomBar(activity)
    }

    private fun setProjectName() {
        val name = nameInputLayout?.editText?.text.toString().trim { it <= ' ' }
        project ?: return
        XstreamSerializer.getInstance().saveProject(project)
        val projectDirectory = project?.directory ?: return
        val renamedDirectory = renameProject(projectDirectory, name)
        if (renamedDirectory == null) {
            Log.e(TAG, "Creating renamed directory failed!")
            return
        }
        ProjectLoadTask.task(renamedDirectory, activity?.applicationContext)
        project = ProjectManager.getInstance().currentProject
        ProjectManager.getInstance().currentlyEditedScene = project?.getSceneByName(sceneName)
    }

    fun saveDescription() {
        val description = descriptionInputLayout?.editText?.text.toString().trim { it <= ' ' }
        if (project?.description == null || project?.description != description) {
            project?.description = description
            if (!XstreamSerializer.getInstance().saveProject(project)) {
                ToastUtil.showError(activity, R.string.error_set_description)
            }
        }
    }

    fun saveCreditsAndNotes() {
        val notesAndCredits =
            notesAndCreditsInputLayout?.editText?.text.toString().trim { it <= ' ' }
        if (project?.notesAndCredits == null || project?.notesAndCredits != notesAndCredits) {
            project?.notesAndCredits = notesAndCredits
            if (!XstreamSerializer.getInstance().saveProject(project)) {
                ToastUtil.showError(activity, R.string.error_set_notes_and_credits)
            }
        }
    }

    fun projectUpload() {
        val currentProject = ProjectManager.getInstance().currentProject
        context?.apply {
            val projectSaver = ProjectSaver(currentProject, this)
            projectSaver.saveProjectAsync({ onSaveProjectComplete() })
            Utils.setLastUsedProjectName(this, currentProject.name)
        }
    }

    private fun onSaveProjectComplete() {
        val currentProject = ProjectManager.getInstance().currentProject
        val intent = Intent(context, ProjectUploadActivity::class.java)
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
        val title = context?.getString(R.string.export_project) ?: ""
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
        }.execute(activity)
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
        val context = context ?: return
        project?.apply {
            val notificationData = StatusBarNotificationManager(context)
                .createSaveProjectToExternalMemoryNotification(context, projectDestination, name)
            ProjectExportTask(directory, projectDestination, notificationData, context).execute()
        }
    }

    private fun moreDetails() {
        val fragment = ProjectDetailsFragment()
        val args = Bundle()
        project?.apply {
            val projectData = ProjectData(name, directory, catrobatLanguageVersion, hasScene())
            args.putSerializable(ProjectDetailsFragment.SELECTED_PROJECT_KEY, projectData)
        }
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment, ProjectDetailsFragment.TAG)
            ?.addToBackStack(ProjectDetailsFragment.TAG)?.commit()
    }

    private fun deleteProject(selectedProject: ProjectData) {
        try {
            StorageOperations.deleteDir(selectedProject.directory)
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
        ToastUtil.showSuccess(activity,
            resources.getQuantityString(R.plurals.deleted_projects, 1, 1)
        )
        project = null
        ProjectManager.getInstance().currentProject = project
        val activity: Activity? = activity
        activity?.onBackPressed()
    }

    companion object {
        @JvmStatic
        val TAG: String = ProjectOptionsFragment::class.java.simpleName

        private const val PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 802
        private const val REQUEST_EXPORT_PROJECT = 10
    }
}
