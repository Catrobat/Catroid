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

package org.catrobat.catroid.test.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Translations are edited by hand and by scripts now, and lint ignores missing and extra
 * translations, so nothing else notices a string pasted into the middle of another one.
 */
@RunWith(JUnit4::class)
class TranslationFilesTest {

    private val resDir = File("src/main/res")
    private val english = strings(File(resDir, "values/strings.xml"))
    private val locales = resDir.listFiles { dir -> dir.name.startsWith("values-") }!!
        .map { File(it, "strings.xml") }
        .filter { it.isFile }

    @Test
    fun everyLocaleIsFound() {
        assertTrue(locales.size > 50)
    }

    @Test
    fun noStringIsNestedInsideAnother() {
        val nested = locales.flatMap { file ->
            stringElements(file).filter { it.getElementsByTagName("string").length > 0 }
                .map { "${file.parentFile.name}: ${it.getAttribute("name")}" }
        }
        assertEquals(emptyList<String>(), nested)
    }

    @Test
    fun placeholdersMatchEnglish() {
        val mismatches = locales.flatMap { file ->
            strings(file).filter { (key, text) -> key in english && placeholders(text) != placeholders(english.getValue(key)) }
                .map { "${file.parentFile.name}: ${it.key}" }
        }
        assertEquals(emptyList<String>(), mismatches)
    }

    private fun placeholders(text: String) = PLACEHOLDER.findAll(text.replace("%%", "")).map { it.value }.sorted().toList()

    private fun strings(file: File) = stringElements(file).associate { it.getAttribute("name") to it.textContent }

    private fun stringElements(file: File): List<Element> {
        val root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).documentElement
        val children = root.childNodes
        return (0 until children.length).map { children.item(it) }
            .filterIsInstance<Element>()
            .filter { it.tagName == "string" }
    }

    private companion object {
        val PLACEHOLDER = Regex("%(\\d+\\$)?[sdf]")
    }
}
