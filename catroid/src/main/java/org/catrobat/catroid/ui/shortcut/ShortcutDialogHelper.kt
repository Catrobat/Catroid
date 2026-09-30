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

package org.catrobat.catroid.ui.shortcut

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import org.catrobat.catroid.R

object ShortcutDialogHelper {

    fun showPinShortcutDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
        isPermissionGranted: Boolean = ShortcutHelper.isShortcutPermissionGranted(context),
        onSettingsClicked: (() -> Unit)? = null
    ) {
        if (ShortcutHelper.isXiaomiDevice() && !isPermissionGranted) {
            showShortcutPermissionDialog(context, layoutInflater, projectName, icon, onSettingsClicked)
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_shortcut_pin, null)

        val iconView = dialogView.findViewById<ImageView>(R.id.shortcut_dialog_icon)
        val nameView = dialogView.findViewById<TextView>(R.id.shortcut_dialog_project_name)
        val pinButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_pin_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_cancel_button)
        val miuiContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_miui_container)

        if (icon != null) {
            iconView.setImageBitmap(icon)
        } else {
            iconView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        nameView.text = projectName
        miuiContainer.visibility = View.GONE
        pinButton.visibility = View.VISIBLE

        val dialog = AlertDialog.Builder(context, R.style.ShortcutPinDialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        pinButton.setOnClickListener {
            dialog.dismiss()
            ShortcutHelper.pinProject(context, projectName, icon)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showShortcutPermissionDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
        onSettingsClicked: (() -> Unit)? = null
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_shortcut_pin, null)

        val iconView = dialogView.findViewById<ImageView>(R.id.shortcut_dialog_icon)
        val nameView = dialogView.findViewById<TextView>(R.id.shortcut_dialog_project_name)
        val pinButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_pin_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_cancel_button)
        val miuiContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_miui_container)
        val settingsButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_miui_settings_button)
        val miuiCancelButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_miui_cancel_button)

        if (icon != null) {
            iconView.setImageBitmap(icon)
        } else {
            iconView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        nameView.text = projectName
        miuiContainer.visibility = View.VISIBLE
        pinButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        val dialog = AlertDialog.Builder(context, R.style.ShortcutPinDialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        settingsButton.setOnClickListener {
            dialog.dismiss()
            onSettingsClicked?.invoke()
            ShortcutHelper.openMiuiPermissionEditor(context)
        }

        miuiCancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
