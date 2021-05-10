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
package org.catrobat.catroid.content.bricks.brickspinner

import android.app.Dialog
import android.content.DialogInterface
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment

class UserVariableBrickTextInputDialogBuilder(
    project: Project, sprite: Sprite, currentUserVariable: UserVariable?,
    activity: AppCompatActivity, spinner: BrickSpinner<UserVariable>
) : TextInputDialog.Builder(activity) {
    init {
        val dialogView = View.inflate(activity, R.layout.dialog_new_user_data, null)
        val multiplayerRadioButton = dialogView.findViewById<RadioButton>(R.id.multiplayer)
        if (SettingsFragment.isMultiplayerVariablesPreferenceEnabled(activity.applicationContext)) {
            multiplayerRadioButton.visibility = View.VISIBLE
        }
        setView(dialogView)
        setHint(activity.getString(R.string.data_label))
            .setTextWatcher(DuplicateInputTextWatcher(spinner.items))
            .setPositiveButton(
                activity.getString(R.string.ok),
                TextInputDialog.OnClickListener { dialog: DialogInterface, textInput: String? ->
                    val userVariable = UserVariable(textInput)
                    val addToProjectVariablesRadioButton =
                        (dialog as Dialog).findViewById<RadioButton>(R.id.global)
                    val addToProjectVariables = addToProjectVariablesRadioButton.isChecked
                    val addToMultiplayerVariables = multiplayerRadioButton.isChecked
                    if (addToProjectVariables) {
                        project.addUserVariable(userVariable)
                    } else if (addToMultiplayerVariables) {
                        project.addMultiplayerVariable(userVariable)
                    } else {
                        sprite.addUserVariable(userVariable)
                    }
                    spinner.add(userVariable)
                    spinner.setSelection(userVariable)
                    val parentFragment = activity
                        .supportFragmentManager.findFragmentByTag(ScriptFragment.TAG) as ScriptFragment?
                    parentFragment?.notifyDataSetChanged()
                })
        setTitle(R.string.formula_editor_variable_dialog_title)
        val uniqueNameProvider = createUniqueNameProvider(R.string.default_variable_name)
        setText(
            uniqueNameProvider.getUniqueName(
                activity.getString(R.string.default_variable_name),
                null
            )
        )
        setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int ->
            spinner.setSelection(
                currentUserVariable
            )
        }
        setOnCancelListener { dialog: DialogInterface? -> spinner.setSelection(currentUserVariable) }
    }
}