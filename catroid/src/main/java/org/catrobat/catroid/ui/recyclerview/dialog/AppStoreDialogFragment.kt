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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants

class AppStoreDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = AppStoreDialogFragment::class.java.simpleName

        enum class Extension {
            LEGO_NXT_EV3,
            PHIRO,
            EMBROIDERY
        }

        @JvmStatic
        fun newInstance(extension: Extension): AppStoreDialogFragment {
            val dialog = AppStoreDialogFragment()
            dialog.extension = extension
            return dialog
        }
    }

    private lateinit var extension: Extension

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.dialog_google_play, null)

        val text = view.findViewById<TextView>(R.id.dialog_google_play_text)
        text.text = when (extension) {
            Extension.LEGO_NXT_EV3 -> getString(R.string.preference_lego_dialog_text)
            Extension.EMBROIDERY -> getString(R.string.preference_embroidery_dialog_text)
            Extension.PHIRO -> getString(R.string.preference_phiro_dialog_text)
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.preference_dialog_use_full_features))
            .setView(view)
            .setNegativeButton(R.string.cancel_button_text, null)

        val buttonText = if (android.os.Build.BRAND != Constants.DEVICE_BRAND_HUAWEI) {
            getString(R.string.preference_dialog_google_play)
        } else {
            getString(R.string.preference_dialog_appgallery)
        }

        builder.setPositiveButton(buttonText) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getCorrectUrl())
            startActivity(intent)
        }

        return builder.create()
    }

    private fun getCorrectUrl(): String {
        return if (android.os.Build.BRAND != Constants.DEVICE_BRAND_HUAWEI) {
            when (extension) {
                Extension.LEGO_NXT_EV3 -> Constants.PREFRENCE_PLAYSTORE_MINDSTORMS_URL
                Extension.EMBROIDERY -> Constants.PREFRENCE_PLAYSTORE_EMBROIDERY_URL
                Extension.PHIRO -> Constants.PREFRENCE_PLAYSTORE_PHIRO_URL
            }
        } else {
            when (extension) {
                Extension.LEGO_NXT_EV3 -> Constants.PREFRENCE_APPGALLERY_MINDSTORMS_URL
                Extension.EMBROIDERY -> Constants.PREFRENCE_APPGALLERY_EMBROIDERY_URL
                Extension.PHIRO -> Constants.PREFRENCE_APPGALLERY_PHIRO_URL
            }
        }
    }
}
