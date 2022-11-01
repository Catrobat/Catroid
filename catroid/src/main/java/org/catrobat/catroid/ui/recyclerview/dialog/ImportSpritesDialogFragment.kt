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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.merge.ImportSpriteData
import org.catrobat.catroid.merge.ImportUtils
import org.catrobat.catroid.merge.ImportVariablesManager
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.InputWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

class ImportSpritesDialogFragment(
    private val newSpritesData: List<ImportSpriteData>,
    private val currentFragment: Fragment
) : DialogFragment() {

    private var projectManager: ProjectManager = inject(ProjectManager::class.java).value
    val currentScene: Scene = projectManager.currentlyEditedScene
    val newSprites: ArrayList<Sprite> = newSpritesData.map { it.sprite } as ArrayList<Sprite>
    val sourceProject: Project = newSpritesData[0].sourceProject

    companion object {
        val TAG: String = ImportSpritesDialogFragment::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val view = View.inflate(activity, R.layout.dialog_new_sprites, null)
        val container = view.findViewById<LinearLayout>(R.id.new_sprites_container)
        val inputs: ArrayList<EditText?> = ArrayList()
        val inputWatcher: ArrayList<InputWatcher.TextWatcher> = ArrayList()
        val builder = AlertDialog.Builder(requireContext())

        newSpritesData.forEach { spriteData ->
            val inputLayout = TextInputLayout(requireContext())
            val editText = TextInputEditText(requireContext())
            if (spriteData.isGroup) {
                editText.hint = getString(R.string.new_group)
            } else {
                editText.hint = getString(R.string.sprite_name_label)
            }
            editText.setText(spriteData.lookDataName)
            inputLayout.addView(editText)
            container.addView(inputLayout)
            inputs.add(inputLayout.editText)
            val duplicateInputTextWatcher = DuplicateInputTextWatcher(currentScene.spriteList)
            duplicateInputTextWatcher.setContext(requireContext())
            duplicateInputTextWatcher.setInputLayout(inputLayout)
            editText.addTextChangedListener(duplicateInputTextWatcher)
            inputWatcher.add(duplicateInputTextWatcher)
        }

        return builder
            .setNegativeButton(R.string.cancel) { _, _ ->
                handleNegativeButton()
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                handlePositiveButton(inputs, inputWatcher, currentScene)
            }
            .setTitle(R.string.import_sprite_dialog_title)
            .setView(view)
            .create()
    }

    private fun handlePositiveButton(
        textInput: List<EditText?>,
        inputWatcher: List<InputWatcher.TextWatcher>,
        currentScene: Scene
    ) {
        if (inputWatcher.map { it.registeredError }.contains(true)) {
            ImportUtils(requireContext()).showRejectToast()
            return
        }

        for ((index, data) in newSpritesData.withIndex()) {
            addLocalSprite(data, textInput[index]?.text.toString(), currentScene)
            if (currentFragment is SpriteListFragment) {
                currentFragment.notifyDataSetChanged()
            }
        }

        ImportVariablesManager.importProjectVariables(sourceProject)
        if (showCastDialog()) {
            CastManager.getInstance()
                .openDeviceSelectorOrDisconnectDialog(activity as? AppCompatActivity)
        }
    }

    private fun addLocalSprite(data: ImportSpriteData, name: String, currentScene: Scene) {
        val sprite: Sprite?
        if (data.isGroup) {
            currentScene.addSprite(GroupSprite(data.sprite.name))
        } else {
            data.sprite.rename(name)
            sprite = Sprite(data.sprite, currentScene)
            currentScene.addSprite(sprite)
        }
    }

    private fun handleNegativeButton() {
        try {
            if (Constants.MEDIA_LIBRARY_CACHE_DIRECTORY.exists()) {
                StorageOperations.deleteDir(Constants.MEDIA_LIBRARY_CACHE_DIRECTORY)
            }
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
    }

    private fun showCastDialog(): Boolean =
        SettingsFragment.isCastSharedPreferenceEnabled(activity) &&
            projectManager.currentProject.isCastProject &&
            !CastManager.getInstance().isConnected
}
