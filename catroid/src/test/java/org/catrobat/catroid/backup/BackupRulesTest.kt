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

package org.catrobat.catroid.backup

import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Projects and trained faces live in the files folder and move with the user
 * to a new phone. The settings and the web view hold the login, so a rule
 * that names any other folder would copy secrets. Without an include rule
 * Android copies everything, so each section must name the files folder.
 */
class BackupRulesTest {

    @Test
    fun everyBuildAllowsBackups() {
        listOf(MAIN_MANIFEST, DEBUG_MANIFEST).forEach { manifest ->
            assertEquals(manifest, "true", application(manifest).getAttribute("android:allowBackup"))
        }
    }

    @Test
    fun theManifestNamesTheRulesForEveryAndroidVersion() {
        val application = application(MAIN_MANIFEST)

        assertEquals("@xml/data_extraction_rules", application.getAttribute("android:dataExtractionRules"))
        assertEquals("@xml/backup_rules", application.getAttribute("android:fullBackupContent"))
    }

    @Test
    fun android12AndNewerCopyOnlyTheFilesFolder() {
        val rules = parse(DATA_EXTRACTION_RULES).documentElement

        listOf("cloud-backup", "device-transfer").forEach { section ->
            val element = rules.getElementsByTagName(section).item(0) as? Element
            assertEquals(section, setOf("file"), element?.let(::includedDomains))
        }
    }

    @Test
    fun android11AndOlderCopyOnlyTheFilesFolder() {
        assertEquals(setOf("file"), includedDomains(parse(BACKUP_RULES).documentElement))
    }

    @Test
    fun theAnalyticsIdStaysOnThePhone() {
        val dataExtractionRules = parse(DATA_EXTRACTION_RULES).documentElement
        val sections = listOf("cloud-backup", "device-transfer")
            .mapNotNull { dataExtractionRules.getElementsByTagName(it).item(0) as? Element }

        assertEquals(2, sections.size)
        (sections + parse(BACKUP_RULES).documentElement).forEach { rules ->
            assertEquals(setOf(ANALYTICS_ID_FILE), excludedPaths(rules))
        }
    }

    private fun application(manifest: String): Element =
        parse(manifest).getElementsByTagName("application").item(0) as Element

    private fun includedDomains(rules: Element): Set<String> {
        val includes = rules.getElementsByTagName("include")
        return (0 until includes.length)
            .mapNotNull { includes.item(it) as? Element }
            .map { it.getAttribute("domain") }
            .toSet()
    }

    private fun excludedPaths(rules: Element): Set<String> {
        val excludes = rules.getElementsByTagName("exclude")
        return (0 until excludes.length)
            .mapNotNull { excludes.item(it) as? Element }
            .filter { it.getAttribute("domain") == "file" }
            .map { it.getAttribute("path") }
            .toSet()
    }

    private fun parse(path: String): Document =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(path))

    private companion object {
        const val MAIN_MANIFEST = "src/main/AndroidManifest.xml"
        const val DEBUG_MANIFEST = "src/debug/AndroidManifest.xml"
        const val DATA_EXTRACTION_RULES = "src/main/res/xml/data_extraction_rules.xml"
        const val BACKUP_RULES = "src/main/res/xml/backup_rules.xml"
        const val ANALYTICS_ID_FILE = "gaClientId"
    }
}
