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

package org.catrobat.catroid.ui.settingsfragments

import android.content.DialogInterface
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceFragment
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.stage.gamepad.GamepadFaceButtonLayout
import org.catrobat.catroid.stage.gamepad.reportedGamepadButton
import org.catrobat.catroid.utils.ToastUtil

/**
 * The submenu of the gamepad extension: the extension itself, the swap of A/B and X/Y with its
 * detection, and a hint at the KeyLinker app, which some controllers can be set up with.
 */
class GamepadSettingsFragment : PreferenceFragment() {

    private var detectionDialog: AlertDialog? = null

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title = preferenceScreen.title
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SettingsFragment.setToChosenLanguage(activity)
        addPreferencesFromResource(R.xml.gamepad_preferences)
        findPreference(DETECT_FACE_BUTTONS_KEY)?.setOnPreferenceClickListener {
            showDetectionDialog()
            true
        }
    }

    override fun onDestroyView() {
        detectionDialog?.dismiss()
        detectionDialog = null
        super.onDestroyView()
    }

    /**
     * Asks for a press of the button labeled A. While the dialog is open, it consumes every
     * controller button, so that a reported B does not act as Back and no button clicks a
     * dialog button.
     */
    private fun showDetectionDialog() {
        val context = activity ?: return
        detectionDialog = AlertDialog.Builder(context)
            .setTitle(R.string.preference_title_gamepad_detect_face_buttons)
            .setMessage(R.string.dialog_gamepad_detect_face_buttons)
            .setNegativeButton(R.string.cancel, null)
            .setOnKeyListener { dialog, _, event -> onDetectionKey(dialog, event) }
            .show()
    }

    /** @return true if the event is consumed, false if the dialog should handle it */
    private fun onDetectionKey(dialog: DialogInterface, event: KeyEvent): Boolean {
        if (reportedGamepadButton(event) == null) {
            return false
        }
        GamepadFaceButtonLayout.detectedBy(event)?.let { layout ->
            dialog.dismiss()
            applyDetectedLayout(layout)
        }
        return true
    }

    private fun applyDetectedLayout(layout: GamepadFaceButtonLayout) {
        val swapPreference = findPreference(SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS)
        (swapPreference as? CheckBoxPreference)?.isChecked = layout.swapsFaceButtons
        activity?.let { context -> ToastUtil.showSuccess(context, layout.detectedMessage) }
    }

    companion object {
        @JvmField
        val TAG: String = GamepadSettingsFragment::class.java.simpleName

        const val DETECT_FACE_BUTTONS_KEY = "setting_gamepad_detect_face_buttons"
        const val KEYLINKER_HINT_KEY = "setting_gamepad_keylinker_hint"
    }
}
