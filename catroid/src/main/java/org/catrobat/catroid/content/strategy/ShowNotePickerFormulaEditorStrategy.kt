/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.content.strategy

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import org.catrobat.catroid.R
import org.catrobat.catroid.pocketmusic.ui.NotePickerDialog
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment

class ShowNotePickerFormulaEditorStrategy : ShowFormulaEditorStrategy {
    override fun showFormulaEditorToEditFormula(
        view: View,
        callback: ShowFormulaEditorStrategy.Callback
    ) {
        if (isViewInScriptFragment(view)) {
            showSelectEditDialog(view, callback)
        } else {
            callback.showFormulaEditor(view)
        }
    }

    private fun isViewInScriptFragment(view: View): Boolean {
        val activity = UiUtils.getActivityFromView(view) ?: return false
        val supportFragmentManager = activity.supportFragmentManager
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return currentFragment is ScriptFragment
    }

    private fun showSelectEditDialog(view: View, callback: ShowFormulaEditorStrategy.Callback) {
        AlertDialog.Builder(view.context)
            .setItems(
                R.array.brick_select_note_picker
            ) { dialog: DialogInterface?, which: Int ->
                switchSelectEditDialogOption(
                    callback,
                    view,
                    which
                )
            }
            .show()
    }

    private fun switchSelectEditDialogOption(
        callback: ShowFormulaEditorStrategy.Callback,
        view: View,
        which: Int
    ) {
        when (which) {
            OPTION_PICK_NOTE -> {
                val activity = UiUtils.getActivityFromView(view) ?: return
                val fragmentManager = activity.supportFragmentManager
                if (fragmentManager.isStateSaved) {
                    return
                }
                showNotePicker(callback, fragmentManager)
            }
            OPTION_FORMULA_EDIT_BRICK -> callback.showFormulaEditor(view)
            else -> throw IllegalArgumentException()
        }
    }

    private fun showNotePicker(
        callback: ShowFormulaEditorStrategy.Callback,
        fragmentManager: FragmentManager
    ) {
        val currentNote = callback.value
        val dialog = NotePickerDialog.newInstance(currentNote)
        dialog.addOnNotePickedListener { value: Int -> callback.value = value }
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialogWithTitle)
        dialog.show(fragmentManager, null)
    }

    companion object {
        private const val OPTION_PICK_NOTE = 0
        private const val OPTION_FORMULA_EDIT_BRICK = 1
    }
}