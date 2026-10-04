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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.asynctask.ProjectRenamer
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.ToastUtil
import java.io.File

@Suppress("LongParameterList")
object ShortcutDialogHelper {

    private const val MAX_DIALOG_WIDTH_DP = 345
    private const val DIALOG_WIDTH_SCREEN_RATIO = 0.86

    var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    private fun applyDialogDimensions(dialog: AlertDialog, context: Context) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val maxDialogWidth = (MAX_DIALOG_WIDTH_DP * displayMetrics.density).toInt()
        val dialogWidth =
            (screenWidth * DIALOG_WIDTH_SCREEN_RATIO).toInt().coerceAtMost(maxDialogWidth)
        dialog.window?.setLayout(dialogWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun showPinShortcutDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
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
            onProjectRenamed = onProjectRenamed
        )
    }

    private fun createAndShowPinDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        projectName: String,
        icon: Bitmap?,
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
            handlePinAction(
                context,
                dialog,
                projectName,
                icon,
                nameEdit,
                renameCheckbox,
                onProjectRenamed
            )
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

    private fun handlePinAction(
        context: Context,
        dialog: AlertDialog,
        projectName: String,
        icon: Bitmap?,
        nameEdit: EditText,
        renameCheckbox: CheckBox,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)?
    ) {
        val editedName = nameEdit.text?.toString()?.trim()
        val labelToUse = if (!editedName.isNullOrBlank()) editedName else projectName
        val shouldRenameProject = renameCheckbox.isChecked && labelToUse != projectName

        if (shouldRenameProject) {
            renameProjectAndPin(context, dialog, projectName, labelToUse, icon, onProjectRenamed)
        } else {
            dialog.dismiss()
            ShortcutHelper.pinProject(context, projectName, icon, shortcutLabel = labelToUse)
        }
    }

    private fun renameProjectAndPin(
        context: Context,
        dialog: AlertDialog,
        projectName: String,
        newName: String,
        icon: Bitmap?,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)?
    ) {
        val destinationDir = File(
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(newName)
        )
        if (destinationDir.exists()) {
            ToastUtil.showError(context, R.string.name_already_exists)
            return
        }

        val projectDir = File(
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
        )

        dialog.dismiss()

        ProjectRenamer(projectDir, newName).renameProjectAsync(
            onRenameProjectComplete = { success ->
                if (success) {
                    onProjectRenameSuccess(
                        context,
                        projectName,
                        newName,
                        icon,
                        onProjectRenamed
                    )
                } else {
                    ToastUtil.showError(
                        context,
                        R.string.error_rename_incompatible_project
                    )
                }
            }
        )
    }

    private fun onProjectRenameSuccess(
        context: Context,
        projectName: String,
        newName: String,
        icon: Bitmap?,
        onProjectRenamed: ((oldName: String, newName: String) -> Unit)?
    ) {
        val currentProject = ProjectManager.getInstance()?.currentProject
        if (currentProject != null && currentProject.name == projectName) {
            relocateOpenProject(currentProject, newName)
        }
        CoroutineScope(mainDispatcher).launch {
            // Migrate existing shortcuts first so pinning reuses their ID instead of
            // publishing a second shortcut for the same project.
            ShortcutHelper.updateShortcutOnRename(context, projectName, newName)
            ShortcutHelper.pinProject(context, newName, icon, shortcutLabel = newName)
            onProjectRenamed?.invoke(projectName, newName)
        }
    }

    /**
     * Points the loaded project at its renamed directory. The directory and all look/sound
     * file references are updated in memory, so unsaved edits are preserved and later saves
     * (e.g. in onPause) are written to the new folder instead of recreating the old one.
     */
    @VisibleForTesting
    fun relocateOpenProject(project: Project, newName: String) {
        val oldDirectory = project.directory
        val newDirectory = File(
            oldDirectory.parentFile,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(newName)
        )
        project.name = newName
        project.directory = newDirectory

        project.sceneList.forEach { scene ->
            scene.spriteList.forEach { sprite ->
                sprite.lookList.forEach { look ->
                    look.file = relocateFile(look.file, oldDirectory, newDirectory)
                }
                sprite.soundList.forEach { sound ->
                    sound.file = relocateFile(sound.file, oldDirectory, newDirectory)
                }
            }
        }
    }

    private fun relocateFile(file: File?, oldDirectory: File, newDirectory: File): File? {
        file ?: return null
        val oldPath = oldDirectory.absolutePath + File.separator
        val filePath = file.absolutePath
        if (!filePath.startsWith(oldPath)) return file
        return File(newDirectory, filePath.removePrefix(oldPath))
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
