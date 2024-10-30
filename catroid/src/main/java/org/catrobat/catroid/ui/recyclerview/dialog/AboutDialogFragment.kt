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
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.databinding.DialogAboutBinding
import org.catrobat.catroid.utils.Utils

private const val SDK_VERSION = 24

class AboutDialogFragment : DialogFragment() {
    private var _binding: DialogAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        _binding = DialogAboutBinding.inflate(LayoutInflater.from(activity))

        val developerUrlView = binding.dialogAboutTextViewUrl
        developerUrlView.movementMethod = LinkMovementMethod.getInstance()
        val developerUrl = getString(
            R.string.about_link_template, Constants.ABOUT_POCKETCODE_LICENSE_URL,
            getString(R.string.dialog_about_license_link_text)
        )
        developerUrlView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(developerUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(developerUrl)
        }

        val aboutCatrobatView = binding.dialogAboutTextCatrobatUrl
        aboutCatrobatView.movementMethod = LinkMovementMethod.getInstance()
        val aboutCatrobatUrl = getString(
            R.string.about_link_template, Constants.CATROBAT_ABOUT_URL,
            getString(R.string.dialog_about_catrobat_link_text)
        )
        aboutCatrobatView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(aboutCatrobatUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(aboutCatrobatUrl)
        }

        val aboutVersionNameTextView = binding.dialogAboutTextViewCatrobatVersionName
        val versionCode =
            if (BuildConfig.FLAVOR == "pocketCodeBeta") "-" + BuildConfig.VERSION_CODE else ""
        val versionName =
            getString(R.string.app_name) + versionCode + " " + getString(R.string.dialog_about_version) + " " + getString(
                R.string.android_version_prefix
            ) + Utils.getVersionName(
                activity
            )
        aboutVersionNameTextView.text = versionName

        val aboutCatrobatVersionTextView = binding.dialogAboutTextViewCatrobatVersionName
        val catrobatVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION
        val catrobatVersionName =
            getString(R.string.dialog_about_catrobat_language_version) + ": " + catrobatVersion
        aboutCatrobatVersionTextView.text = catrobatVersionName

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.dialog_about_title)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = AboutDialogFragment::class.java.simpleName
    }
}
