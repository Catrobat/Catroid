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
import androidx.fragment.app.FragmentManager
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.utils.getProjectBitmap
import org.catrobat.paintroid.colorpicker.ColorPickerDialog
import org.catrobat.paintroid.colorpicker.OnColorPickedListener

class ShowColorPickerFormulaEditorStrategy : ShowFormulaEditorStrategy {
   override fun showFormulaEditorToEditFormula(
        view: View,
        callback: ShowFormulaEditorStrategy.Callback
    ) {
        if (isInCorrectFragment(view, callback)) {
            showSelectEditDialog(view, callback)
        } else {
            callback.showFormulaEditor(view)
        }
    }

    private fun isInCorrectFragment(
        view: View,
        callback: ShowFormulaEditorStrategy.Callback
    ): Boolean {
        val activity = UiUtils.getActivityFromView(view) ?: return false
        val supportFragmentManager = activity.supportFragmentManager
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is FormulaEditorFragment) {
            callback.showFormulaEditor(view)
        }
        return currentFragment is ScriptFragment || currentFragment is FormulaEditorFragment
    }

    private fun showSelectEditDialog(view: View, callback: ShowFormulaEditorStrategy.Callback) {
        AlertDialog.Builder(view.context)
            .setItems(
                R.array.brick_select_color_picker
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
            OPTION_PICK_COLOR -> {
                val activity = UiUtils.getActivityFromView(view) ?: return
                val fragmentManager = activity.supportFragmentManager
                if (fragmentManager.isStateSaved) {
                    return
                }
                showColorPicker(callback, fragmentManager)
            }
            OPTION_FORMULA_EDIT_BRICK -> callback.showFormulaEditor(view)
            else -> throw IllegalArgumentException()
        }
    }

    private fun showColorPicker(
        callback: ShowFormulaEditorStrategy.Callback,
        fragmentManager: FragmentManager
    ) {
        val currentColor = callback.value
        val dialog = ColorPickerDialog.newInstance(currentColor)
        val projectBitmap = ProjectManager.getInstance().getProjectBitmap()
        dialog.setBitmap(projectBitmap)
        dialog.addOnColorPickedListener(object : OnColorPickedListener {
            override fun colorChanged(color: Int) {
                callback.value = color
            }
        })
        dialog.show(fragmentManager, null)
    }

    companion object {
        private const val OPTION_PICK_COLOR = 0
        private const val OPTION_FORMULA_EDIT_BRICK = 1
    }
}