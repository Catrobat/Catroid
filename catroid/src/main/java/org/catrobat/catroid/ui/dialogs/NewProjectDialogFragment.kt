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

package org.catrobat.catroid.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.databinding.DialogNewProjectBinding
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment.projectExistsInDirectory
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isCastSharedPreferenceEnabled
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException

class NewProjectDialogFragment : DialogFragment() {
    private val projectManager: ProjectManager by inject()
    private var _binding: DialogNewProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.new_project_title)

        binding.toolbar.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.toolbar.toolbar.setNavigationOnClickListener { dismiss() }

        if (isCastSharedPreferenceEnabled(activity)) {
            binding.castRadioButton.visibility = VISIBLE
        }

        val uniqueNameProvider: UniqueNameProvider = object : UniqueNameProvider() {
            override fun isUnique(newName: String) = !projectExistsInDirectory(newName)
        }

        binding.input.hint = getString(R.string.project_name_label)
        binding.inputEditText.apply {
            setText(uniqueNameProvider.getUniqueName(getString(R.string.default_project_name), null))
            addTextChangedListener(object : NewProjectNameTextWatcher<Nameable>() {
                override fun afterTextChanged(s: Editable?) {
                    binding.input.error = validateInput(s.toString(), getContext())
                    activity?.invalidateOptionsMenu()
                }
            })
            requestFocus()
        }
    }

    fun createProject() {
        val projectName = binding.inputEditText.text.toString().trim()
        var landscapeMode = false
        var projectCreatorType = DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DEFAULT
        var castProject = false

        when (binding.radioGroup.checkedRadioButtonId) {
            R.id.landscape_radio_button -> landscapeMode = true
            R.id.cast_radio_button -> {
                castProject = true
                projectCreatorType = DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_CAST
            }
        }

        try {
            when (binding.exampleProjectSwitch.isChecked) {
                true -> projectManager.createNewExampleProject(projectName, projectCreatorType, landscapeMode)
                false -> projectManager.createNewEmptyProject(projectName, landscapeMode, castProject)
            }

            activity?.startActivity(Intent(activity, ProjectActivity::class.java))
        } catch (_: IOException) {
            ToastUtil.showError(activity, R.string.error_new_project)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_confirm, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }

        val confirm = menu.findItem(R.id.confirm)
        if (binding.input.error == null) {
            confirm.setIcon(R.drawable.ic_done)
            confirm.isEnabled = true
        } else {
            confirm.setIcon(R.drawable.ic_done_disabled)
            confirm.isEnabled = false
        }

        confirm.isVisible = true

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.confirm -> {
                dismiss()
                createProject()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        val TAG: String = NewProjectDialogFragment::class.java.simpleName
    }
}
