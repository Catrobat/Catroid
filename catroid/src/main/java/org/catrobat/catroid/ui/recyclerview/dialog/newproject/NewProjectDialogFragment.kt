/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.ui.recyclerview.dialog.newproject

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.databinding.DialogNewProjectBinding
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException

class NewProjectDialogFragment : DialogFragment() {

    private val projectManager: ProjectManager by inject()
    private lateinit var binding: DialogNewProjectBinding
    private lateinit var adapter: FrameSizeAdapter
    private val uniqueNameProvider = NewProjectUniqueNameProvider()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewProjectBinding.inflate(LayoutInflater.from(context))

        adapter = FrameSizeAdapter(
            requireContext(), isOrientationLandscape(), isUnitCm()
        )

        binding.frameSizeSpinner.adapter = adapter

        binding.radioGroup.setOnCheckedChangeListener { _, _ -> updateAdapter() }
        binding.unitRadioGroup.setOnCheckedChangeListener { _, _ -> updateAdapter() }

        if (SettingsFragment.isCastSharedPreferenceEnabled(activity)) {
            binding.castRadioButton.visibility = View.VISIBLE
        }

        val uniqueName = uniqueNameProvider.getUniqueName(
            getString(R.string.default_project_name), null
        )

        return TextInputDialog.Builder(requireContext())
            .setHint(getString(R.string.project_name_label))
            .setText(uniqueName)
            .setTextWatcher(NewProjectNameTextWatcher<Nameable>())
            .setPositiveButton(getString(R.string.ok),
                TextInputDialog.OnClickListener { _: DialogInterface?, textInput: String? ->
                    createProject(textInput)
                })
            .setNegativeButton(R.string.cancel, null)
            .setView(binding.root)
            .create()
    }

    private fun isOrientationLandscape(): Boolean =
        binding.radioGroup.checkedRadioButtonId == R.id.landscape_mode_radio_button

    private fun isOrientationCast(): Boolean =
        binding.radioGroup.checkedRadioButtonId == R.id.cast_radio_button

    private fun isUnitCm(): Boolean =
        binding.unitRadioGroup.checkedRadioButtonId == R.id.cm_radio_button

    private fun updateAdapter() {
        adapter.update(
            isOrientationLandscape(), isUnitCm()
        )
    }

    fun createProject(
        projectName: String?
    ) {
        val isExampleProject = binding.exampleProjectSwitch.isChecked
        val landscape = isOrientationLandscape()
        val castProject = isOrientationCast()

        val frameSize = binding.frameSizeSpinner.selectedItem as FrameSize
        val height = frameSize.getHeight(landscape, FrameSizeUnit.PIXEL)
        val width = frameSize.getWidth(landscape, FrameSizeUnit.PIXEL)

        try {
            if (isExampleProject) {
                val projectCreatorType =
                    if (castProject) ProjectCreatorType.PROJECT_CREATOR_CAST else ProjectCreatorType.PROJECT_CREATOR_DEFAULT

                projectManager.createNewExampleProject(
                    projectName, projectCreatorType, landscape, height, width
                )
            } else {
                projectManager.createNewEmptyProject(
                    projectName, landscape, castProject, height, width
                )
            }
            requireActivity().startActivity(Intent(activity, ProjectActivity::class.java))
        } catch (e: IOException) {
            ToastUtil.showError(activity, R.string.error_new_project)
            Log.e(TAG, "Failed to create project.", e)
        }
    }

    companion object {
        val TAG: String = NewProjectDialogFragment::class.java.simpleName
    }
}
