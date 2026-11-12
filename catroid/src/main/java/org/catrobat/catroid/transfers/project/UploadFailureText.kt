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

package org.catrobat.catroid.transfers.project

import android.content.Context
import androidx.annotation.StringRes
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants

/** What a child is told when an upload fails, in the upload dialog and in the notification. */
object UploadFailureText {

    private const val API_ERROR_VERIFICATION = "verification"
    private const val API_ERROR_SUSPENDED = "suspended"
    private const val API_ERROR_CONSENT = "consent"
    private const val API_ERROR_REGISTRATION = "registration"
    private const val FIRST_SERVER_ERROR = 500
    private const val LAST_SERVER_ERROR = 599
    private val HTTP_SERVER_ERRORS = FIRST_SERVER_ERROR..LAST_SERVER_ERROR

    @JvmStatic
    fun of(context: Context, errorCode: Int, errorMessage: String?): String = when {
        // The server explains what it refused, e.g. a project above its size limit, in the
        // language the app asked for.
        errorCode == Constants.ERROR_UNPROCESSABLE_ENTITY && !errorMessage.isNullOrBlank() &&
            errorMessage != ProjectUpload.UPLOAD_FAILED_MESSAGE -> errorMessage
        else -> context.getString(messageId(errorCode, errorMessage))
    }

    @StringRes
    fun messageId(errorCode: Int, errorMessage: String?): Int = when {
        errorCode == ProjectUpload.UPLOAD_NETWORK_ERROR -> R.string.error_internet_connection
        errorCode == Constants.ERROR_TOO_MANY_REQUESTS -> R.string.error_project_upload_rate_limit
        errorCode == Constants.ERROR_UNAUTHORIZED -> R.string.error_project_upload_login_required
        errorCode == Constants.ERROR_FORBIDDEN && errorMessage != null -> accountStateMessageId(errorMessage)
        // "Currently unavailable" only when the server itself is in trouble.
        errorCode in HTTP_SERVER_ERRORS -> R.string.progress_upload_dialog_failed
        else -> R.string.error_project_upload
    }

    /**
     * Whether the notification offers to retry. The share platform answers with HTTP statuses, not
     * the old app-level codes the notification switches on, so they are translated here.
     */
    @JvmStatic
    fun notificationStatus(errorCode: Int): Int = when {
        isWorthRetrying(errorCode) -> Constants.STATUS_CODE_INTERNAL_SERVER_ERROR
        else -> errorCode
    }

    private fun isWorthRetrying(errorCode: Int) =
        errorCode == ProjectUpload.UPLOAD_NETWORK_ERROR ||
            errorCode == Constants.ERROR_TOO_MANY_REQUESTS ||
            errorCode in HTTP_SERVER_ERRORS

    /**
     * The API rejects writes for accounts that are not usable yet with HTTP 403. Those are not
     * outages: the user has to act on them. Catroweb's AccountStateEventListener sends these
     * messages in English whatever the Accept-Language, and with no machine-readable code, so the
     * message is all there is to match on.
     */
    @StringRes
    private fun accountStateMessageId(errorMessage: String): Int = when {
        errorMessage.contains(API_ERROR_VERIFICATION, ignoreCase = true) ->
            R.string.error_project_upload_verify_email
        errorMessage.contains(API_ERROR_SUSPENDED, ignoreCase = true) ->
            R.string.error_project_upload_account_suspended
        errorMessage.contains(API_ERROR_CONSENT, ignoreCase = true) ->
            R.string.error_project_upload_parental_consent
        errorMessage.contains(API_ERROR_REGISTRATION, ignoreCase = true) ->
            R.string.error_project_upload_complete_registration
        else -> R.string.error_project_upload
    }
}
