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

package org.catrobat.catroid.utils

import org.catrobat.catroid.common.Constants
import java.io.File
import java.io.IOException

object ProjectIdUtils {
    val UUID_REGEX = Regex("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
    val URL_TAG_REGEX = Regex("<url>(.*?)</url>")

    fun extractUuidFromString(input: String): String? = UUID_REGEX.find(input)?.value

    /**
     * The id to send when uploading the project in [projectDir], so the server updates the user's
     * existing project instead of creating a second one. Null for a new project.
     *
     * Sending someone else's id is harmless: Catroweb only updates a project the uploading user
     * owns, and otherwise falls back to the user's project of the same name or creates a new one.
     */
    fun readServerProjectId(projectDir: File): String? {
        val idFile = File(projectDir, Constants.SERVER_PROJECT_ID_FILE_NAME)
        return try {
            if (idFile.isFile) {
                idFile.readText().trim().takeIf { UUID_REGEX.matches(it) }
            } else {
                // Projects from before the id file carry their id only in the share URL.
                val urlContent = URL_TAG_REGEX.find(codeXml(projectDir).readText())?.groupValues?.get(1)
                urlContent?.let { extractUuidFromString(it) }
            }
        } catch (_: IOException) {
            null
        }
    }

    /** Remembers [projectId] after the user's own upload, in the id file and the share URL. */
    fun saveServerProjectId(projectDir: File, projectId: String): Boolean {
        if (!UUID_REGEX.matches(projectId)) {
            return false
        }
        return try {
            File(projectDir, Constants.SERVER_PROJECT_ID_FILE_NAME).writeText(projectId)
            writeShareUrl(projectDir, Constants.SHARE_PROJECT_URL + projectId)
        } catch (_: IOException) {
            false
        }
    }

    /** A copy is a new project: an empty id keeps its upload from replacing the original. */
    fun markAsNewProject(projectDir: File) {
        File(projectDir, Constants.SERVER_PROJECT_ID_FILE_NAME).writeText("")
    }

    private fun writeShareUrl(projectDir: File, shareUrl: String): Boolean {
        val codeXml = codeXml(projectDir)
        val content = codeXml.readText()
        val replacement = "<url>$shareUrl</url>"
        val updatedContent = when {
            URL_TAG_REGEX.containsMatchIn(content) -> content.replace(URL_TAG_REGEX) { replacement }
            content.contains("<url/>") -> content.replace("<url/>", replacement)
            content.contains("<url />") -> content.replace("<url />", replacement)
            else -> return true
        }
        // Written next to it and swapped, so a process death mid-write cannot cut code.xml off.
        val tmpXml = File(projectDir, "upload_" + Constants.CODE_XML_FILE_NAME)
        tmpXml.writeText(updatedContent)
        if (tmpXml.renameTo(codeXml)) {
            return true
        }
        tmpXml.delete()
        return false
    }

    private fun codeXml(projectDir: File) = File(projectDir, Constants.CODE_XML_FILE_NAME)
}
