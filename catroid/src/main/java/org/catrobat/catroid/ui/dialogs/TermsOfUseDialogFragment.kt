/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.databinding.DialogTermsOfUseBinding

private const val SDK_VERSION = 24

class TermsOfUseDialogFragment : DialogFragment() {
    private var _binding: DialogTermsOfUseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        _binding = DialogTermsOfUseBinding.inflate(LayoutInflater.from(activity))

        val termsOfUseTextView = binding.dialogTermsOfUseTextViewInfo
        val termsOfUseUrlTextView = binding.dialogTermsOfUseTextViewUrl
        termsOfUseUrlTextView.movementMethod = LinkMovementMethod.getInstance()

        val termsOfUseUrlStringText = getString(R.string.dialog_terms_of_use_link_text)
        val termsOfUseDialogBuilder = AlertDialog.Builder(
            requireActivity()
        )
            .setView(binding.root)
            .setTitle(R.string.dialog_terms_of_use_title)

        termsOfUseDialogBuilder.setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }

        termsOfUseTextView.setText(R.string.dialog_terms_of_use_info)

        val termsOfUseUrl = getString(
            R.string.terms_of_use_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
            termsOfUseUrlStringText
        )
        termsOfUseUrlTextView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(termsOfUseUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(termsOfUseUrl)
        }

        val termsOfUseDialog = termsOfUseDialogBuilder.create()
        termsOfUseDialog.setCanceledOnTouchOutside(true)

        return termsOfUseDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = TermsOfUseDialogFragment::class.java.simpleName
    }
}
