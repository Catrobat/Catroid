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
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.catrobat.catroid.R

class RejectImportDialogFragment(
    private val conflicts: List<String>?,
    private val errorType: Int = CONFLICT_VARIABLE
) : DialogFragment() {
    companion object {
        val TAG: String = RejectImportDialogFragment::class.java.simpleName
        const val DISPLAYED_CONFLICT_VARIABLE: Int = 3
        const val GAP_WIDTH: Int = 15
        const val CONFLICT_VARIABLE = -10
        const val CONFLICT_PROJECT_NAME = -11
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.dialog_import_rejected, null)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setPositiveButton(requireContext().getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        setImportErrorText(errorType, view)

        return builder
            .setTitle(R.string.warning)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun addConflicts(view: View) {
        val conflictField = view.findViewById<TextView>(R.id.conflicting_variables)
        val numberOfIterations = minOf(conflicts!!.size, DISPLAYED_CONFLICT_VARIABLE)
        val content = SpannableStringBuilder()

        for (iterator in conflicts.withIndex().take(numberOfIterations)) {
            val contentStart = content.length

            if (iterator.index < numberOfIterations - 1) {
                content.append(iterator.value + System.lineSeparator())
            } else {
                content.append(iterator.value)
            }

            content.setSpan(
                BulletSpan(GAP_WIDTH), contentStart, content.length, 0
            )
        }
        conflictField?.text = content
        conflictField.visibility = View.VISIBLE
    }

    private fun setImportErrorText(
        errorType: Int,
        view: View
    ) {
        val resolveView = view.findViewById<TextView>(R.id.import_conflicting_variables_try_again)
        val importConflictReasonView = view.findViewById<TextView>(R.id.import_conflicting_variables_reason)

        when (errorType) {
            CONFLICT_VARIABLE -> {
                importConflictReasonView.setText(R.string.import_conflicting_variables_reason)
                resolveView.setText(R.string.import_conflicting_variables_try_again)
                addConflicts(view)
            }
            CONFLICT_PROJECT_NAME -> {
                importConflictReasonView.setText(R.string.import_unresolvable_project_name_reason)
                resolveView.setText(R.string.import_unresolvable_project_name_try_again)
                view.findViewById<TextView>(R.id.conflicting_variables)?.visibility = View.GONE
            }
        }
    }
}
