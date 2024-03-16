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
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.databinding.DialogPrivacyAwarenessDisclaimerBinding

private const val SDK_VERSION = 24

class PrivacyAwarenessDisclaimerDialogFragment : DialogFragment() {
    private var _binding: DialogPrivacyAwarenessDisclaimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        _binding = DialogPrivacyAwarenessDisclaimerBinding.inflate(LayoutInflater.from(activity))

        val privacyAwarenessUrlView = binding.dialogPrivacyAwarenessTextViewUrl
        privacyAwarenessUrlView.movementMethod = LinkMovementMethod.getInstance()
        val privacyAwarenessUrl = getString(
            R.string.privacy_awareness_link_template, Constants.PRIVACY_AWARENESS_WIKI_URL,
            getString(R.string.dialog_privacy_awareness_link_text)
        )
        privacyAwarenessUrlView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(privacyAwarenessUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(privacyAwarenessUrl)
        }

        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.disclaimer_privacy_awareness_title)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .create()
        builder.setCanceledOnTouchOutside(false)
        return builder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = PrivacyAwarenessDialogFragment::class.java.simpleName
    }
}
