/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.text.method.LinkMovementMethod
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog as CatroidAlertDialog
import androidx.core.text.HtmlCompat
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.actions.AskAction
import org.catrobat.catroid.content.actions.FaceNameTrainAction
import org.catrobat.catroid.content.actions.WebAction
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.InputWatcher
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile
import java.net.URI
import java.util.ArrayList
import java.util.Collections

class BrickDialogManager(val stageActivity: StageActivity) :
    DialogInterface.OnKeyListener, DialogInterface.OnDismissListener {

    private val openDialogs = Collections.synchronizedList(ArrayList<Dialog>())

    enum class DialogType {
        ASK_DIALOG,
        WEB_ACCESS_DIALOG,
        FACE_TRAIN_MENU,
        FACE_TRAIN_NEW_NAME,
        FACE_TRAIN_DELETE_CHOICE,
        FACE_TRAIN_DELETE_CONFIRM,
        FACE_TRAIN_PROGRESS
    }

    fun dialogIsShowing() = openDialogs.isNotEmpty()

    /** Set by [dismissAllDialogs]: the stage is being destroyed, nothing may resume it. */
    @Volatile
    private var closingStage = false

    fun dismissAllDialogs() {
        closingStage = true
        openDialogs.toList().forEach { it.dismiss() }
        openDialogs.clear()
    }

    fun showDialog(type: DialogType, action: Action, content: String) {
        if (closingStage || stageActivity.isFinishing || stageActivity.isDestroyed) {
            return
        }
        val dialog = when (type) {
            DialogType.ASK_DIALOG -> createAskDialog(action as AskAction, content)
            DialogType.WEB_ACCESS_DIALOG -> createWebAccessDialog(action as WebAction, content)
            DialogType.FACE_TRAIN_MENU -> createFaceTrainMenuDialog(action as FaceNameTrainAction)
            DialogType.FACE_TRAIN_NEW_NAME -> createFaceTrainNewNameDialog(action as FaceNameTrainAction)
            DialogType.FACE_TRAIN_DELETE_CHOICE -> createFaceTrainDeleteChoiceDialog(action as FaceNameTrainAction)
            DialogType.FACE_TRAIN_DELETE_CONFIRM ->
                createFaceTrainDeleteConfirmDialog(action as FaceNameTrainAction, content)
            DialogType.FACE_TRAIN_PROGRESS -> createFaceTrainProgressDialog(action as FaceNameTrainAction)
        }
        openDialog(dialog)
    }

    private fun openDialog(dialog: Dialog) {
        // Already paused while another dialog is open (e.g. the next face
        // training step opens before the previous one closes). Pausing again
        // would restart the timer's pause interval.
        if (!stageActivity.dialogIsShowing()) {
            StageLifeCycleController.stagePause(stageActivity)
        }
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

    // ---------------- Face name train ----------------

    /*
     * The face training listeners call the action straight away. The action opens
     * its next dialog synchronously (on the main thread), before AlertDialog
     * dismisses the current one, so the stage stays paused and dimmed without a
     * gap between the dialogs.
     */

    /**
     * The face training dialogs are built like Catroid's in-app dialogs: an
     * AppCompat AlertDialog on the Catroid theme (accent @color/accent), with the
     * user's accessibility profile applied as BaseActivity does. StageActivity is
     * not a BaseActivity, so its own theme carries neither. Created per dialog, so
     * a changed profile applies to the next one.
     */
    private fun faceTrainContext(): Context =
        ContextThemeWrapper(stageActivity, R.style.Catroid).also {
            AccessibilityProfile.fromCurrentPreferences(PreferenceManager.getDefaultSharedPreferences(stageActivity))
                .applyAccessibilityStyles(it.theme)
        }

    private fun faceTrainBuilder(title: String): CatroidAlertDialog.Builder =
        CatroidAlertDialog.Builder(faceTrainContext())
            .setTitle(title)
            .setCancelable(false)
            .setOnKeyListener(this)
            .setOnDismissListener(this)

    private fun createFaceTrainMenuDialog(action: FaceNameTrainAction): Dialog {
        val names = action.personNames()
        // A list leaves no room for a message, so the title says what tapping a name does.
        // Photos from an older face model cannot match; then the title asks for them again.
        val title = when {
            names.isEmpty() -> R.string.face_train_title
            action.needsRetraining() -> R.string.face_train_retrain
            else -> R.string.face_train_choose_name
        }
        val builder = faceTrainBuilder(stageActivity.getString(title))
            .setPositiveButton(stageActivity.getString(R.string.face_train_add_new_name)) { _, _ ->
                action.onAddNameChosen()
            }
            .setNegativeButton(stageActivity.getString(R.string.done)) { _, _ ->
                action.onDone()
            }
        if (names.isEmpty()) {
            builder.setMessage(stageActivity.getString(R.string.face_train_no_names))
        } else {
            builder.setItems(names.toTypedArray()) { _, which ->
                action.onPersonChosen(names[which])
            }
            builder.setNeutralButton(stageActivity.getString(R.string.face_train_delete)) { _, _ ->
                action.onDeleteChosen()
            }
        }
        return builder.create()
    }

    /**
     * Catroid's name dialog: TextInputDialog (dialog_text_input.xml, a Material
     * TextInputLayout with a hint) and the standard InputWatcher, which shows
     * empty, blank and duplicate names inline and disables Next until the name
     * is valid, as the "new variable" dialog does.
     */
    private fun createFaceTrainNewNameDialog(action: FaceNameTrainAction): Dialog {
        val builder = TextInputDialog.Builder(faceTrainContext())
        builder.setHint(stageActivity.getString(R.string.face_train_name_hint))
            .setTextWatcher(FaceNameWatcher().apply { setScope(action.personNames()) })
            .setPositiveButton(
                stageActivity.getString(R.string.next),
                TextInputDialog.OnClickListener { _, name -> action.onNewName(name.trim()) }
            )
        builder.setTitle(stageActivity.getString(R.string.face_train_add_new_name))
            .setMessage(stageActivity.getString(R.string.face_train_name_subtitle))
            .setNegativeButton(stageActivity.getString(R.string.cancel)) { _, _ ->
                action.onNewNameCancelled()
            }
            .setCancelable(false)
            .setOnKeyListener(this)
            .setOnDismissListener(this)
        return builder.create()
    }

    private fun createFaceTrainDeleteChoiceDialog(action: FaceNameTrainAction): Dialog {
        val names = action.personNames()
        return faceTrainBuilder(stageActivity.getString(R.string.face_train_delete_choose_title))
            .setItems(names.toTypedArray()) { _, which ->
                action.onDeleteTargetChosen(names[which])
            }
            .setNegativeButton(stageActivity.getString(R.string.cancel)) { _, _ ->
                action.onDeleteCancelled()
            }
            .create()
    }

    private fun createFaceTrainDeleteConfirmDialog(action: FaceNameTrainAction, name: String): Dialog {
        // Catroid's delete pattern: "Delete …?", "You can't undo this!", Delete / Cancel.
        return faceTrainBuilder(stageActivity.getString(R.string.face_train_delete_title, name))
            .setMessage(stageActivity.getString(R.string.dialog_confirm_delete))
            .setPositiveButton(stageActivity.getString(R.string.delete)) { _, _ ->
                action.onDeleteConfirmed(name)
            }
            .setNegativeButton(stageActivity.getString(R.string.cancel)) { _, _ ->
                action.onDeleteCancelled()
            }
            .create()
    }

    /** No buttons: the action closes it when training has finished. */
    private fun createFaceTrainProgressDialog(action: FaceNameTrainAction): Dialog {
        val context = faceTrainContext()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_face_train_progress, null)
        val bar = view.findViewById<ProgressBar>(R.id.face_train_progress_bar).apply {
            max = action.progressMax()
            progress = action.progressValue()
        }
        val dialog = faceTrainBuilder(stageActivity.getString(R.string.face_train_progress_title))
            .setMessage(action.progressText(stageActivity))
            .setView(view)
            .create()
        dialog.setOnShowListener { action.onProgressDialogShown(dialog, bar) }
        return dialog
    }

    /** Catroid's name checks, and no line break: the face database stores one name per line. */
    private class FaceNameWatcher : InputWatcher.TextWatcher() {
        override fun validateInput(input: String, context: Context): String? =
            if (Recognizer.hasLineBreak(input)) {
                context.getString(R.string.face_train_name_line_break)
            } else {
                super.validateInput(input, context)
            }
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent) =
        (keyCode == KeyEvent.KEYCODE_BACK).also {
            if (it) stageActivity.onBackPressed()
        }

    override fun onDismiss(dialog: DialogInterface?) {
        // Android passes the dialog through a weak reference, so it is null when
        // the dialog was collected before this notice ran. Only a dialog that
        // dismissAllDialogs() already let go of can be collected: there is
        // nothing left to remove or resume.
        if (dialog == null) {
            return
        }
        openDialogs.remove(dialog as Dialog)
        // Dismissed because the stage is closing: its listener may already be
        // gone, so resuming it (or opening the next step) would crash.
        if (closingStage || stageActivity.isFinishing || stageActivity.isDestroyed) {
            return
        }
        // The name list closes because the photo picker opens: the stage stays
        // paused behind it, and the picker's result opens the next dialog.
        if (FaceNameTrainAction.isPickerOpen()) {
            return
        }
        StageLifeCycleController.stageResume(stageActivity)
    }
}
