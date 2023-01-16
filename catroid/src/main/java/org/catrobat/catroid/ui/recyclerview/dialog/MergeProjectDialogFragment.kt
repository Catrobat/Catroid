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

package org.catrobat.catroid.ui.recyclerview.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

class MergeProjectDialogFragment(
    private val sourceProject1: Project,
    private val sourceProject2: Project,
    val adapter: ExtendedRVAdapter<*>
) : DialogFragment() {

    private var projectManager: ProjectManager = inject(ProjectManager::class.java).value

    companion object {
        var TAG = MergeProjectDialogFragment::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.dialog_new_project, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.visibility = View.GONE
        val exampleProjectSwitch: SwitchMaterial = view.findViewById(R.id.example_project_switch)
        exampleProjectSwitch.visibility = View.GONE

        if (SettingsFragment.isCastSharedPreferenceEnabled(activity)) {
            view.findViewById<View>(R.id.cast_radio_button).visibility = View.VISIBLE
        }
        val uniqueNameProvider: UniqueNameProvider = object : UniqueNameProvider() {
            override fun isUnique(newName: String): Boolean {
                return !ReplaceExistingProjectDialogFragment.projectExistsInDirectory(newName)
            }
        }
        val builder = TextInputDialog.Builder(requireContext())
            .setHint(getString(R.string.project_name_label))
            .setText(
                uniqueNameProvider.getUniqueName(
                    getString(R.string.default_project_name),
                    null
                )
            )
            .setTextWatcher(NewProjectNameTextWatcher<Nameable>())
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String? ->
                if (textInput != null) {
                    createMergedProject(textInput)
                }
            }
        return builder
            .setView(view)
            .setTitle(R.string.new_merge_project_dialog_title)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    fun createMergedProject(projectName: String) {
        try {
            val mergedProject = Project(
                projectName, sourceProject1,
                sourceProject2
            )
            projectManager.currentProject = mergedProject
        } catch (e: IOException) {
            Log.e(TAG, "Error merging projects", e)
        }
        requireActivity().startActivity(Intent(activity, ProjectActivity::class.java))
    }
}
