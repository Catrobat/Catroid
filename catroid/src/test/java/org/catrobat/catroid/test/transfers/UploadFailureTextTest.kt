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

package org.catrobat.catroid.test.transfers

import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.transfers.project.ProjectUpload
import org.catrobat.catroid.transfers.project.UploadFailureText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UploadFailureTextTest {

    @Test
    fun `a network error asks to check the connection, not the exception text`() {
        assertEquals(
            R.string.error_internet_connection,
            UploadFailureText.messageId(ProjectUpload.UPLOAD_NETWORK_ERROR, "Unable to resolve host share.catrobat.org")
        )
    }

    @Test
    fun `account states are explained`() {
        assertEquals(R.string.error_project_upload_verify_email, messageFor(403, "Email verification required."))
        assertEquals(R.string.error_project_upload_parental_consent, messageFor(403, "Parental consent pending."))
        assertEquals(R.string.error_project_upload_login_required, messageFor(401, null))
        assertEquals(R.string.error_project_upload_rate_limit, messageFor(429, null))
    }

    @Test
    fun `a server error says the upload is unavailable for now`() {
        assertEquals(R.string.progress_upload_dialog_failed, messageFor(503, "Service Unavailable"))
    }

    @Test
    fun `any other failure is the neutral upload error`() {
        assertEquals(R.string.error_project_upload, messageFor(ProjectUpload.UPLOAD_ZIP_ERROR, "Failed to zip"))
        assertEquals(R.string.error_project_upload, messageFor(404, null))
        assertEquals(R.string.error_project_upload, messageFor(403, "Something new."))
    }

    @Test
    fun `the notification offers a retry for network and server trouble only`() {
        val retry = Constants.STATUS_CODE_INTERNAL_SERVER_ERROR
        assertEquals(retry, UploadFailureText.notificationStatus(ProjectUpload.UPLOAD_NETWORK_ERROR))
        assertEquals(retry, UploadFailureText.notificationStatus(503))
        assertEquals(retry, UploadFailureText.notificationStatus(429))
        assertEquals(403, UploadFailureText.notificationStatus(403))
        assertEquals(422, UploadFailureText.notificationStatus(422))
    }

    private fun messageFor(code: Int, message: String?) = UploadFailureText.messageId(code, message)
}
