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

import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.common.ProjectData
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import org.catrobat.catroid.R
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.databinding.FragmentProjectShowDetailsBinding
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.util.Date

class ProjectDetailsFragment : Fragment() {
    companion object {
        val TAG: String = ProjectDetailsFragment::class.java.simpleName
        const val SELECTED_PROJECT_KEY = "selectedProject"
    }
    private var _binding: FragmentProjectShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val projectManager: ProjectManager by inject()
    private lateinit var projectData: ProjectData
    private lateinit var project: Project

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectShowDetailsBinding
            .inflate(
                inflater,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        try {
            val fetchedProjectData = requireArguments().getSerializable(SELECTED_PROJECT_KEY) as
                ProjectData?
            fetchedProjectData ?: return
            projectData = fetchedProjectData
            project = XstreamSerializer.getInstance()
                .loadProject(projectData.directory, requireContext())
            projectManager.currentProject = project
        } catch (e: IOException) {
            onExceptionThrown(e)
        } catch (e: LoadingProjectException) {
            onExceptionThrown(e)
        }
        val thumbnailWidth =
            requireContext().resources.getDimensionPixelSize(R.dimen.project_thumbnail_width)
        val thumbnailHeight =
            requireContext().resources.getDimensionPixelSize(R.dimen.project_thumbnail_height)
        val screenshotLoader = ProjectAndSceneScreenshotLoader(thumbnailWidth, thumbnailHeight)
        val header = project.xmlHeader
        screenshotLoader.loadAndShowScreenshot(
            projectData.name,
            screenshotLoader.getScreenshotSceneName(project.directory), false,
            binding.image
        )
        val size = FileMetaDataExtractor
            .getSizeAsString(
                File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectData.name),
                requireContext()
            )
        val modeText = if (header.islandscapeMode()) {
            R.string.landscape
        } else {
            R.string.portrait
        }
        val screen =
            header.getVirtualScreenWidth().toString() + "x" + header.getVirtualScreenHeight()

        binding.apply {
            name.text = projectData.name
            authorValue.text = userHandle
            sizeValue.text = size
            lastAccessValue.text = lastAccess
            screenSizeValue.text = screen
            modeValue.text = getString(modeText)
            remixOfValue.text = remixOf
            descriptionValue.apply {
                text = header.description
                setOnClickListener {
                    handleDescriptionPressed()
                }
            }
            notesAndCreditsValue.apply {
                text = header.notesAndCredits
                setOnClickListener {
                    handleNotesAndCreditsPressed()
                }
            }
        }

        hideBottomBar(requireActivity())
    }

    private fun onExceptionThrown(e: Exception) {
        ToastUtil.showError(requireContext(), R.string.error_load_project)
        Log.e(TAG, Log.getStackTraceString(e))
        requireActivity().onBackPressed()
    }

    private fun handleDescriptionPressed() {
        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(R.string.description))
            .setText(project.description)
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { _, value ->
                    setDescription(value)
                })
        builder.setTitle(R.string.set_description)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun handleNotesAndCreditsPressed() {
        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(R.string.notes_and_credits_title))
            .setText(project.notesAndCredits)
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { _, value ->
                    setNotesAndCredits(value)
                })
        builder.setTitle(R.string.set_notes_and_credits)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
    }

    private val lastAccess: String
        get() {
            val lastModified = Date(projectData.lastUsed)
            var lastAccess: String
            if (DateUtils.isToday(lastModified.time)) {
                lastAccess = getString(R.string.details_date_today) + ": "
                lastAccess += DateFormat.getTimeFormat(requireContext()).format(lastModified)
            } else {
                lastAccess = DateFormat.getDateFormat(requireContext()).format(lastModified)
            }
            return lastAccess
        }

    private val userHandle: String
        get() {
            val userHandle = project.xmlHeader.userHandle
            return if (userHandle == null || userHandle == "") {
                getString(R.string.unknown)
            } else userHandle
        }

    private val remixOf: String
        get() {
            val remixOf = project.xmlHeader.remixParentsUrlString
            return if (remixOf == null || remixOf == "") {
                getString(R.string.nxt_no_sensor)
            } else remixOf
        }

    fun setDescription(description: String?) {
        project.description = description
        if (XstreamSerializer.getInstance().saveProject(project)) {
            binding.descriptionValue.text = description
        } else {
            ToastUtil.showError(requireContext(), R.string.error_set_description)
        }
    }

    private fun setNotesAndCredits(notesAndCredits: String?) {
        project.notesAndCredits = notesAndCredits
        if (XstreamSerializer.getInstance().saveProject(project)) {
            binding.notesAndCreditsValue.text = notesAndCredits
        } else {
            ToastUtil.showError(requireContext(), R.string.error_set_notes_and_credits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
