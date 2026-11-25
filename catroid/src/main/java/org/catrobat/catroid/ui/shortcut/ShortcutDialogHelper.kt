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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.ui.ProjectRenameHelper
import org.catrobat.catroid.ui.shortcut.ShortcutHelper.PinResult
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.io.IOException

@Suppress("LongParameterList")
object ShortcutDialogHelper {

    private const val MAX_DIALOG_WIDTH_DP = 345
    private const val DIALOG_WIDTH_SCREEN_RATIO = 0.86

    @VisibleForTesting
    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private fun applyDialogDimensions(dialog: AlertDialog, context: Context) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val maxDialogWidth = (MAX_DIALOG_WIDTH_DP * displayMetrics.density).toInt()
        val dialogWidth =
            (screenWidth * DIALOG_WIDTH_SCREEN_RATIO).toInt().coerceAtMost(maxDialogWidth)
        dialog.window?.setLayout(dialogWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Shows the dialog to pin a project to the home screen.
     *
     * @param coroutineScope scope of the calling screen (for example its `lifecycleScope`).
     *        A rename started from the dialog reports back through it, so nothing is shown
     *        after the screen is gone.
     */
    fun showPinShortcutDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
        coroutineScope: CoroutineScope,
        isPermissionGranted: Boolean = ShortcutHelper.isShortcutPermissionGranted(context),
        onSettingsClicked: (() -> Unit)? = null,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)? = null
    ) {
        if (ShortcutHelper.isXiaomiDevice() && !isPermissionGranted) {
            showShortcutPermissionDialog(
                context,
                layoutInflater,
                projectName,
                icon,
                onSettingsClicked
            )
            return
        }

        createAndShowPinDialog(
            context = context,
            layoutInflater = layoutInflater,
            projectName = projectName,
            icon = icon,
            coroutineScope = coroutineScope,
            onProjectRenamed = onProjectRenamed
        )
    }

    private fun createAndShowPinDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
        coroutineScope: CoroutineScope,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)?
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_shortcut_pin, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
        val renameCheckbox =
            dialogView.findViewById<CheckBox>(R.id.shortcut_dialog_rename_project_checkbox)
        val pinButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_pin_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.shortcut_dialog_cancel_button)

        setupDialogViews(dialogView, projectName, icon, renameCheckbox)

        val dialog = AlertDialog.Builder(context, R.style.ShortcutPinDialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        pinButton.setOnClickListener {
            val editedName = nameEdit.text?.toString()?.trim()
            val labelToUse = if (!editedName.isNullOrBlank()) editedName else projectName
            if (renameCheckbox.isChecked && labelToUse != projectName) {
                renameProjectAndPin(
                    context, dialog, coroutineScope, projectName, labelToUse, icon,
                    onProjectRenamed
                )
            } else {
                dialog.dismiss()
                pinAndReport(context, projectName, icon, labelToUse)
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        applyDialogDimensions(dialog, context)
    }

    private fun setupDialogViews(
        dialogView: View,
        projectName: String,
        icon: Bitmap?,
        renameCheckbox: CheckBox
    ) {
        val iconView = dialogView.findViewById<ImageView>(R.id.shortcut_dialog_icon)
        val nameEdit = dialogView.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
        val renameContainer =
            dialogView.findViewById<View>(R.id.shortcut_dialog_rename_project_container)
        val renameLabel =
            dialogView.findViewById<TextView>(R.id.shortcut_dialog_rename_project_label)
        val buttonContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_button_container)
        val miuiContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_miui_container)

        if (icon != null) {
            iconView.setImageBitmap(icon)
        } else {
            iconView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        nameEdit.setText(projectName)
        nameEdit.setSelection(projectName.length)
        miuiContainer.visibility = View.GONE
        buttonContainer.visibility = View.VISIBLE

        val toggleCheckbox = View.OnClickListener {
            renameCheckbox.isChecked = !renameCheckbox.isChecked
        }
        renameLabel.setOnClickListener(toggleCheckbox)
        renameContainer.setOnClickListener(toggleCheckbox)
    }

    /**
     * Pins the project and tells the user when nothing new was added to the home screen.
     * When the pin was requested, the launcher shows its own confirmation.
     */
    private fun pinAndReport(context: Context, projectName: String, icon: Bitmap?, label: String) {
        when (ShortcutHelper.pinProject(context, projectName, icon, shortcutLabel = label)) {
            PinResult.REQUESTED -> Unit
            PinResult.ALREADY_PINNED ->
                ToastUtil.showSuccess(context, R.string.shortcut_already_pinned)
            PinResult.FAILED -> ToastUtil.showError(context, R.string.shortcut_pin_failed)
        }
    }

    private fun renameProjectAndPin(
        context: Context,
        dialog: AlertDialog,
        coroutineScope: CoroutineScope,
        projectName: String,
        newName: String,
        icon: Bitmap?,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)?
    ) {
        val rootDirectory = FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val projectDirectory = File(
            rootDirectory,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
        )
        val destinationDirectory = File(
            rootDirectory,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(newName)
        )
        if (destinationDirectory.exists() &&
            !isSameDirectory(projectDirectory, destinationDirectory)
        ) {
            ToastUtil.showError(context, R.string.name_already_exists)
            return
        }

        setDialogBusy(dialog, busy = true)
        coroutineScope.launch {
            val renamedDirectory = ProjectRenameHelper.renameProjectAsync(
                context, projectDirectory, projectName, newName, ioDispatcher
            )
            if (renamedDirectory == null) {
                setDialogBusy(dialog, busy = false)
                ToastUtil.showError(context, R.string.error_rename_project)
                return@launch
            }
            dialog.dismiss()
            onProjectRenamed?.invoke(projectName, newName)
            pinAndReport(context, newName, icon, newName)
        }
    }

    /** Keeps the dialog open but inactive while the project is renamed. */
    private fun setDialogBusy(dialog: AlertDialog, busy: Boolean) {
        dialog.setCancelable(!busy)
        dialog.findViewById<Button>(R.id.shortcut_dialog_pin_button)?.isEnabled = !busy
        dialog.findViewById<Button>(R.id.shortcut_dialog_cancel_button)?.isEnabled = !busy
        dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)?.isEnabled = !busy
    }

    /**
     * True if both files are the same folder on disk. On case-insensitive storage, a
     * case-only rename such as "game" -> "Game" targets the folder that already exists,
     * which must not count as a name conflict.
     */
    private fun isSameDirectory(projectDirectory: File, destinationDirectory: File): Boolean {
        if (projectDirectory.name == destinationDirectory.name) return true
        if (!projectDirectory.name.equals(destinationDirectory.name, ignoreCase = true)) {
            return false
        }
        return try {
            projectDirectory.canonicalPath == destinationDirectory.canonicalPath
        } catch (_: IOException) {
            true
        } catch (_: SecurityException) {
            true
        }
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
        val nameContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_name_container)
        val renameContainer =
            dialogView.findViewById<View>(R.id.shortcut_dialog_rename_project_container)
        val buttonContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_button_container)
        val miuiContainer = dialogView.findViewById<View>(R.id.shortcut_dialog_miui_container)
        val settingsButton =
            dialogView.findViewById<Button>(R.id.shortcut_dialog_miui_settings_button)
        val miuiCancelButton =
            dialogView.findViewById<Button>(R.id.shortcut_dialog_miui_cancel_button)

        if (icon != null) {
            iconView.setImageBitmap(icon)
        } else {
            iconView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        nameContainer.visibility = View.GONE
        renameContainer.visibility = View.GONE
        buttonContainer.visibility = View.GONE
        miuiContainer.visibility = View.VISIBLE

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
        applyDialogDimensions(dialog, context)
    }
}
