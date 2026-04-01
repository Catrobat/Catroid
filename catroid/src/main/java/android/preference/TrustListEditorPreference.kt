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

package android.preference

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat.startActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.common.Constants

class TrustListEditorPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {
    private val neutralButtonText = context.getString(R.string.brick_context_dialog_help)

    init {
        setDialogTitle(R.string.preference_screen_web_access_title)
        setPositiveButtonText(R.string.ok)
        setNegativeButtonText(R.string.cancel)
    }

    override fun showDialog(state: Bundle?) {
        val mBuilder = AlertDialog.Builder(context)
            .setTitle(dialogTitle)
            .setIcon(dialogIcon)
            .setPositiveButton(positiveButtonText) { _, _ ->
                text = editText.text.toString()
                TrustedDomainManager.setUserTrustList(text)
            }
            .setNeutralButton(neutralButtonText, null)
            .setNegativeButton(negativeButtonText, this)
            .setOnDismissListener(this)

        LayoutInflater.from(mBuilder.context).inflate(dialogLayoutResource, null)?.run {
            onBindDialogView(this)
            mBuilder.setView(this)
        } ?: run {
            mBuilder.setMessage(dialogMessage)
        }
        onPrepareDialogBuilder(mBuilder)

        mBuilder.create().apply {
            state?.run { onRestoreInstanceState(this) }
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            editText.requestFocus()
            setOnShowListener {
                getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEB_REQUEST_WIKI_URL))
                    startActivity(context, intent, null)
                }
            }
            show()
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        text = TrustedDomainManager.getUserTrustList()
        editText.setText(text)
        editText.setSelection(text.length)
        val oldParent = editText.parent
        if (oldParent !== view) {
            (oldParent as? ViewGroup)?.removeView(editText)
            onAddEditTextToDialogView(view, editText)
        }
    }
}
