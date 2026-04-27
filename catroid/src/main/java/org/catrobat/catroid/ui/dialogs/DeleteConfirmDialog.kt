/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.catrobat.catroid.R
import androidx.core.graphics.drawable.toDrawable

fun Fragment.showDeleteConfirmationDialog(
    target: DeleteTarget,
    itemCount: Int,
    onConfirmDelete: () -> Unit
) {
    if (!isAdded) return

    val dialogView = LayoutInflater.from(requireContext())
        .inflate(R.layout.dialog_confirm_delete, null, false)

    val titleTv = dialogView.findViewById<TextView>(R.id.deleteDialogTitle)
    val messageTv = dialogView.findViewById<TextView>(R.id.deleteDialogMessage)
    val closeBtn = dialogView.findViewById<ImageButton>(R.id.deleteDialogClose)
    val cancelTv = dialogView.findViewById<TextView>(R.id.deleteDialogCancel)
    val deleteTv = dialogView.findViewById<TextView>(R.id.deleteDialogConfirm)

    titleTv.setText(target.titleRes)
    messageTv.text = resources.getQuantityString(
        target.messagePluralsRes,
        itemCount,
        itemCount
    )

    val dialog = AlertDialog.Builder(requireContext())
        .setView(dialogView)
        .setCancelable(false)
        .create()

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }

    closeBtn.setOnClickListener { dismiss() }
    cancelTv.setOnClickListener { dismiss() }
    deleteTv.setOnClickListener {
        dismiss()
        onConfirmDelete()
    }

    dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    dialog.show()
}
