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

package org.catrobat.catroid.ui.recyclerview.dialog

import android.app.Dialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SharedPreferenceKeys.NEW_SPRITE_VISUAL_PLACEMENT_KEY
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.merge.ImportProjectHelper
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM
import org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_VISUAL_PLACEMENT
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import java.io.File
import java.io.IOException

class NewSpriteDialogFragment(
    private val emptySprite: Boolean,
    private val lookDataName: String,
    private val lookFileName: String,
    private val contentResolver: ContentResolver?,
    private val uri: Uri?,
    private val currentFragment: Fragment,
    private val isObject: Boolean,
    private val importProjectHelper: ImportProjectHelper?
) : DialogFragment() {
    constructor(emptySprite: Boolean, lookDataName: String, currentFragment: Fragment) :
        this(emptySprite, lookDataName, "", null, null, currentFragment, false, null)

    private var visuallyPlaceSwitch: SwitchCompat? = null
    private var placeVisuallyTextView: TextView? = null
    private var isPlaceVisually = true
    private var sprite: Sprite? = null

    companion object {
        val TAG: String = NewSpriteDialogFragment::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = View.inflate(activity, R.layout.dialog_new_sprite, null)
        val currentScene = ProjectManager.getInstance().currentlyEditedScene

        visuallyPlaceSwitch = view.findViewById(R.id.place_visually_sprite_switch)
        placeVisuallyTextView = view.findViewById(R.id.place_visually_textView)

        if (emptySprite) {
            visuallyPlaceSwitch?.visibility = View.GONE
            placeVisuallyTextView?.text = getString(R.string.new_empty_sprite_note)
            isPlaceVisually = false
        } else {
            setupToggleButtonListener()
        }

        val builder = context?.let { TextInputDialog.Builder(it) }
        builder?.setHint(getString(R.string.sprite_name_label))
            ?.setText(lookDataName)
            ?.setTextWatcher(DuplicateInputTextWatcher(currentScene.spriteList))
            ?.setPositiveButton(getString(R.string.ok)) { _, textInput: String? ->
                handlePositiveButton(textInput, currentScene)
            }

        builder?.setTitle(R.string.new_sprite_dialog_title)
            ?.setNegativeButton(R.string.cancel) { _, _ ->
                handleNegativeButton()
            }

        return builder
            ?.setView(view)
            ?.setNegativeButton(R.string.cancel, null)
            ?.create() as AlertDialog
    }

    private fun handlePositiveButton(textInput: String?, currentScene: Scene) {
        if (isObject) {
            sprite = importProjectHelper?.addObjectDataToNewSprite(null)
            sprite?.rename(textInput)
            currentScene.addSprite(sprite)
        } else {
            sprite = Sprite(textInput)
            currentScene.addSprite(sprite)
            if (!emptySprite) {
                addLookDataToSprite(currentScene, textInput)
            }
        }

        if (currentFragment is SpriteListFragment) {
            currentFragment.notifyDataSetChanged()
        }

        if (isPlaceVisually) {
            startVisualPlacementActivity()
        }

        if (showCastDialog()) {
            CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(activity as? AppCompatActivity)
        }
    }

    private fun handleNegativeButton() {
        try {
            if (Constants.MEDIA_LIBRARY_CACHE_DIR.exists()) {
                StorageOperations.deleteDir(Constants.MEDIA_LIBRARY_CACHE_DIR)
            }
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
    }

    private fun showCastDialog(): Boolean = SettingsFragment.isCastSharedPreferenceEnabled(activity) &&
        ProjectManager.getInstance().currentProject.isCastProject &&
        !CastManager.getInstance().isConnected

    private fun addLookDataToSprite(currentScene: Scene?, textInput: String?) {
        try {
            val imageDirectory = File(
                currentScene?.directory,
                IMAGE_DIRECTORY_NAME
            )
            val file = StorageOperations.copyUriToDir(
                contentResolver, uri,
                imageDirectory,
                lookFileName
            )
            Utils.removeExifData(imageDirectory, lookFileName)
            val lookData = LookData(textInput, file)
            if (lookData.imageMimeType == null) {
                imgFormatNotSupportedDialog()
                currentScene?.removeSprite(sprite)
            } else {
                sprite?.lookList?.add(lookData)
                lookData.collisionInformation.calculate()
            }
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    private fun setupToggleButtonListener() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(NEW_SPRITE_VISUAL_PLACEMENT_KEY, true).apply {
                visuallyPlaceSwitch?.isChecked = this
                visualTextViewVisibility(this)
            }

        visuallyPlaceSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            visualTextViewVisibility(isChecked)
            PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(NEW_SPRITE_VISUAL_PLACEMENT_KEY, isPlaceVisually)
                .apply()
        }
    }

    private fun visualTextViewVisibility(isVisible: Boolean) {
        isPlaceVisually = isVisible
        placeVisuallyTextView?.visibility = if (isVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun startVisualPlacementActivity() {
        ProjectManager.getInstance().currentSprite = sprite
        val intent = Intent(requireContext(), VisualPlacementActivity()::class.java)
        intent.putExtra(EXTRA_X_TRANSFORM, BrickValues.X_POSITION)
        intent.putExtra(EXTRA_Y_TRANSFORM, BrickValues.Y_POSITION)
        activity?.startActivityForResult(intent, REQUEST_CODE_VISUAL_PLACEMENT)
    }

    private fun imgFormatNotSupportedDialog() {
        val alertDialogBuilder = context?.let {
            AlertDialog.Builder(it)
                .setMessage(getString(R.string.Image_format_not_supported))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, which: Int ->
                    dialog.cancel()
                }
        }
        val alertDialog = alertDialogBuilder?.create()
        alertDialog?.show()
    }
}
