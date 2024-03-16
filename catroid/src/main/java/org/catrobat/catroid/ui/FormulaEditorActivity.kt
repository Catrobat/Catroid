/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.databinding.ActivityRecyclerBinding
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH
import org.catrobat.catroid.ui.fragment.AddBrickFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.SnackbarUtil

class FormulaEditorActivity : BaseActivity() {

    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var projectManager: ProjectManager
    private lateinit var currentProject: Project
    private lateinit var currentSprite: Sprite
    private lateinit var generatedVariableName: String

    private var setUndoMenuItemVisibility: Boolean = false
    private var formulaHasChanged: Boolean = false
    private var userVariableChanged: Boolean = false
    private var editedFormulaName: String = ""

    companion object {
        const val FORMULA_HAS_CHANGED = "formulaHasChanged"
        const val FORMULA_MAP = "formulaMap"
        const val SET_UNDO_MENU_ITEM_VISIBILITY = "setUndoMenuItemVisibility"
        const val BRICK_HASH_DEFAULT_VALUE = -1

        val TAG: String = FormulaEditorFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isFinishing) {
            return
        }

        projectManager = ProjectManager.getInstance()
        currentProject = projectManager.currentProject

        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadFragment()
    }

    private fun loadFragment() {
        val fragment = FormulaEditorFragment()
        fragment.arguments = makeBundleForFormulaEditorFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, FormulaEditorFragment.TAG)
            .commit()

        BottomBar.hideBottomBar(this)
    }

    private fun makeBundleForFormulaEditorFragment(): Bundle {
        val bundle = Bundle()

        bundle.putSerializable(
            FormulaEditorFragment.SHOW_CUSTOM_VIEW,
            intent.getSerializableExtra(FormulaEditorFragment.SHOW_CUSTOM_VIEW)
        )
        bundle.putSerializable(
            FormulaEditorFragment.FORMULA_BRICK_BUNDLE_ARGUMENT,
            intent.getSerializableExtra(FormulaEditorFragment.FORMULA_BRICK_BUNDLE_ARGUMENT)
        )
        bundle.putSerializable(
            FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT,
            intent.getSerializableExtra(FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT)
        )
        bundle.putSerializable(
            FormulaEditorFragment.BRICK_FIELD_TO_TEXT_VIEW_ID_MAP,
            intent.getSerializableExtra(FormulaEditorFragment.BRICK_FIELD_TO_TEXT_VIEW_ID_MAP)
        )
        bundle.putSerializable(
            FormulaEditorFragment.CURRENT_BRICK_INTERN_FORMULA,
            intent.getSerializableExtra(FormulaEditorFragment.CURRENT_BRICK_INTERN_FORMULA)
        )
        bundle.putSerializable(
            FormulaEditorFragment.FORMULA_MAP_BUNDLE_ARGUMENT,
            intent.getSerializableExtra(FormulaEditorFragment.FORMULA_MAP_BUNDLE_ARGUMENT)
        )
        bundle.putInt(
            EXTRA_BRICK_HASH,
            intent.getIntExtra(EXTRA_BRICK_HASH, BRICK_HASH_DEFAULT_VALUE)
        )

        return bundle
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_script_activity, menu)
        optionsMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        saveProject()

        val fragment = getCurrentFragment()

        if (fragment is FormulaEditorFragment) {
            fragment.exitFormulaEditorFragment()
            if (formulaHasChanged || userVariableChanged || setUndoMenuItemVisibility) {
                setResult(RESULT_OK, makeIntentForResult(fragment))
            }
            finish()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            if (fragment is BrickCategoryFragment) {
                SnackbarUtil.showHintSnackbar(this, R.string.hint_scripts)
            }
            if (fragment is AddBrickFragment) {
                SnackbarUtil.showHintSnackbar(this, R.string.hint_category)
            }
            supportFragmentManager.popBackStack()
            return
        }

        finish()
    }

    private fun makeIntentForResult(fragment: FormulaEditorFragment): Intent {
        val extras = Bundle()

        extras.putInt(
            EXTRA_BRICK_HASH,
            intent.getIntExtra(EXTRA_BRICK_HASH, BRICK_HASH_DEFAULT_VALUE)
        )
        extras.putBoolean(FORMULA_HAS_CHANGED, formulaHasChanged)

        val showUndoMenuItemVisibility =
            if (userVariableChanged) true else setUndoMenuItemVisibility
        extras.putBoolean(SET_UNDO_MENU_ITEM_VISIBILITY, showUndoMenuItemVisibility)

        extras.putSerializable(FORMULA_MAP, fragment.formulaBrick.formulaMap)

        return Intent().putExtras(extras)
    }

    private fun saveProject() {
        currentProject = ProjectManager.getInstance().currentProject
        val projectSaver = ProjectSaver(currentProject, applicationContext)
        projectSaver.saveProjectAsync()
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleAddButton(vie: View?) {
        if (getCurrentFragment() is DataListFragment) {
            handleAddUserDataButton()
            return
        }
    }

    private fun handleAddUserDataButton() {
        val view = View.inflate(this, R.layout.dialog_new_user_data, null)

        val makeListCheckBox = view.findViewById<CheckBox>(R.id.make_list)
        makeListCheckBox.visibility = View.VISIBLE

        val multiplayerRadioButton = view.findViewById<RadioButton>(R.id.multiplayer)
        if (SettingsFragment.isMultiplayerVariablesPreferenceEnabled(applicationContext)) {
            multiplayerRadioButton.visibility = View.VISIBLE
            multiplayerRadioButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                makeListCheckBox.isEnabled = !isChecked
            }
        }

        val addToProjectUserDataRadioButton = view.findViewById<RadioButton>(R.id.global)

        val variables: MutableList<UserData<*>?> = ArrayList()

        val projectManager = ProjectManager.getInstance()
        currentSprite = projectManager.currentSprite
        currentProject = projectManager.currentProject

        variables.addAll(currentProject.userVariables)
        variables.addAll(currentProject.multiplayerVariables)
        variables.addAll(currentSprite.userVariables)

        val lists: MutableList<UserData<*>?> = ArrayList()
        lists.addAll(currentProject.userLists)
        lists.addAll(currentSprite.userLists)

        val textWatcher: DuplicateInputTextWatcher<UserData<*>?> =
            DuplicateInputTextWatcher<UserData<*>?>(variables)

        val builder = TextInputDialog.Builder(this)

        val uniqueVariableNameProvider =
            builder.createUniqueNameProvider(R.string.default_variable_name)

        val uniqueListNameProvider = builder.createUniqueNameProvider(R.string.default_list_name)
        generatedVariableName = uniqueVariableNameProvider.getUniqueName(
            getString(R.string.default_variable_name),
            null
        )

        builder.setTextWatcher(textWatcher)
            .setText(generatedVariableName)
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String? ->
                val addToProjectUserData = addToProjectUserDataRadioButton.isChecked
                val addToMultiplayerData = multiplayerRadioButton.isChecked
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(
                        SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY,
                        false
                    ).apply()
                if (makeListCheckBox.isChecked) {
                    val userList = UserList(textInput)
                    if (addToProjectUserData) {
                        currentProject.addUserList(userList)
                    } else {
                        currentSprite.addUserList(userList)
                    }
                } else {
                    val userVariable = UserVariable(textInput)
                    if (addToMultiplayerData) {
                        currentProject.addMultiplayerVariable(userVariable)
                    } else if (addToProjectUserData) {
                        currentProject.addUserVariable(userVariable)
                    } else {
                        currentSprite.addUserVariable(userVariable)
                    }
                }
                if (getCurrentFragment() is DataListFragment) {
                    (getCurrentFragment() as DataListFragment?)!!.notifyDataSetChanged()
                    (getCurrentFragment() as DataListFragment?)!!.indexAndSort()
                }
            }

        val alertDialog = builder.setTitle(R.string.formula_editor_variable_dialog_title)
            .setView(view)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        makeListCheckBox.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            val textInputEditText =
                alertDialog.findViewById<TextInputEditText>(R.id.input_edit_text)
            val currentName = textInputEditText!!.text.toString()
            if (checked) {
                alertDialog.setTitle(getString(R.string.formula_editor_list_dialog_title))
                textWatcher.setOriginalScope(lists)
                if (currentName == generatedVariableName) {
                    generatedVariableName = uniqueListNameProvider.getUniqueName(
                        getString(R.string.default_list_name),
                        null
                    )
                    textInputEditText.setText(generatedVariableName)
                }
            } else {
                alertDialog.setTitle(getString(R.string.formula_editor_variable_dialog_title))
                textWatcher.setOriginalScope(variables)
                if (currentName == generatedVariableName) {
                    generatedVariableName = uniqueVariableNameProvider.getUniqueName(
                        getString(R.string.default_variable_name),
                        null
                    )
                    textInputEditText.setText(generatedVariableName)
                }
            }
            multiplayerRadioButton.isEnabled = !checked
        }
        alertDialog.show()
    }

    private fun getCurrentFragment(): Fragment? =
        supportFragmentManager.findFragmentById(R.id.fragment_container)

    fun setUndoMenuItemVisibility(value: Boolean) {
        setUndoMenuItemVisibility = value
    }

    fun setFormulaHasChanged(value: Boolean) {
        formulaHasChanged = value
    }

    fun setUserVariableHasChanged(value: Boolean) {
        userVariableChanged = value
    }

    fun setEditedFormulaName(name: String) {
        editedFormulaName = name
    }
}
