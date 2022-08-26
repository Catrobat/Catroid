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

package org.catrobat.catroid.stage

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.text.method.LinkMovementMethod
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.actions.AskAction
import org.catrobat.catroid.content.actions.WebAction
import java.net.URI
import java.util.ArrayList
import java.util.Collections

class BrickDialogManager(val stageActivity: StageActivity) :
    DialogInterface.OnKeyListener, DialogInterface.OnDismissListener {

    private val openDialogs = Collections.synchronizedList(ArrayList<Dialog>())

    enum class DialogType {
        ASK_DIALOG,
        WEB_ACCESS_DIALOG
    }

    fun dialogIsShowing() = openDialogs.isNotEmpty()

    fun dismissAllDialogs() {
        openDialogs.forEach { it.dismiss() }
        openDialogs.clear()
    }

    fun showDialog(type: DialogType, action: Action, content: String) {
        val dialog = when (type) {
            DialogType.ASK_DIALOG -> createAskDialog(action as AskAction, content)
            DialogType.WEB_ACCESS_DIALOG -> createWebAccessDialog(action as WebAction, content)
        }
        openDialog(dialog)
    }

    private fun openDialog(dialog: Dialog) {
        StageLifeCycleController.stagePause(stageActivity)
        openDialogs.add(dialog)
        dialog.show()
    }

    private fun createAskDialog(askAction: AskAction, question: String): Dialog {
        val editText = EditText(stageActivity)
        val askDialog = AlertDialog.Builder(ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
            .setView(editText)
            .setMessage(stageActivity.getString(R.string.brick_ask_dialog_hint))
            .setTitle(question)
            .setCancelable(false)
            .setOnKeyListener(this)
            .setOnDismissListener(this)
            .setPositiveButton(stageActivity.getString(R.string.brick_ask_dialog_submit)) { _, _ ->
                askAction.setAnswerText(editText.text.toString())
            }
            .create()

        editText.requestFocus()
        askDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return askDialog
    }

    private fun createWebAccessDialog(webAction: WebAction, url: String): Dialog {
        val view = LayoutInflater.from(stageActivity).inflate(R.layout.dialog_web_access, null)
        view.findViewById<TextView>(R.id.request_url).text = url

        view.findViewById<TextView>(R.id.request_warning).apply {
            text = HtmlCompat.fromHtml(
                stageActivity.getString(R.string.web_request_warning_message, Constants.WEB_REQUEST_WIKI_URL),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            movementMethod = LinkMovementMethod.getInstance()
        }

        return AlertDialog.Builder(ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
            .setTitle(stageActivity.getString(R.string.web_request_warning_title))
            .setCancelable(false)
            .setView(view)
            .setOnKeyListener(this)
            .setOnDismissListener(this)
            .setPositiveButton(stageActivity.getString(R.string.once)) { _, _ ->
                webAction.grantPermission()
            }
            .setNeutralButton(stageActivity.getString(R.string.always)) { dialog, _ ->
                openDialog(createTrustDomainDialog(webAction, url, dialog as Dialog))
            }
            .setNegativeButton(stageActivity.getString(R.string.deny)) { _, _ ->
                webAction.denyPermission()
            }
            .create()
    }

    private fun createTrustDomainDialog(webAction: WebAction, url: String, webAccessDialog: Dialog): Dialog {
        val domain = URI(url).host.removePrefix("www.")
        val view = LayoutInflater.from(stageActivity).inflate(R.layout.dialog_web_access, null)
        view.findViewById<TextView>(R.id.request_url).text = domain

        val warningMessage = StringBuilder()
            .append(stageActivity.getString(R.string.web_request_warning_message, Constants.WEB_REQUEST_WIKI_URL))
            .append("<br><br>")
            .append(stageActivity.getString(R.string.web_request_trust_domain_warning_message))

        if (!BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
            warningMessage.append(" ").append(stageActivity.getString(R.string.trusted_domains_edit_hint))
        }

        view.findViewById<TextView>(R.id.request_warning).apply {
            text = HtmlCompat.fromHtml(warningMessage.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            movementMethod = LinkMovementMethod.getInstance()
        }

        return AlertDialog.Builder(ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
            .setTitle(stageActivity.getString(R.string.web_request_trust_domain_warning_title))
            .setCancelable(false)
            .setView(view)
            .setOnKeyListener(this)
            .setOnDismissListener(this)
            .setPositiveButton(stageActivity.getString(R.string.always)) { _, _ ->
                TrustedDomainManager.addToUserTrustList(domain)
                webAction.grantPermission()
            }
            .setNeutralButton(stageActivity.getString(R.string.cancel)) { _, _ ->
                openDialog(webAccessDialog)
            }
            .create()
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent) =
        (keyCode == KeyEvent.KEYCODE_BACK).also {
            if (it) stageActivity.onBackPressed()
        }

    override fun onDismiss(dialog: DialogInterface) {
        openDialogs.remove(dialog as Dialog)
        StageLifeCycleController.stageResume(stageActivity)
    }
}
