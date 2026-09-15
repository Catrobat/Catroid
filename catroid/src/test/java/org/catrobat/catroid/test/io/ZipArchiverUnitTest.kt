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

package org.catrobat.catroid.test.io

import org.catrobat.catroid.io.ZipArchiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RunWith(JUnit4::class)
class ZipArchiverUnitTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    private val zipArchiver = ZipArchiver()

    @Test
    fun testZipAndUnzipNormalFiles() {
        val srcDir = tempFolder.newFolder("src")
        val file1 = File(srcDir, "test1.txt").apply { writeText("Hello Catroid") }
        val subDir = File(srcDir, "sub").apply { mkdir() }
        val file2 = File(subDir, "test2.txt").apply { writeText("Nested Content") }

        val archive = File(tempFolder.root, "archive.zip")
        zipArchiver.zip(archive, arrayOf(file1, subDir))

        assertTrue(archive.exists())

        val dstDir = tempFolder.newFolder("dst")
        zipArchiver.unzip(archive, dstDir)

        val unzippedFile1 = File(dstDir, "test1.txt")
        val unzippedFile2 = File(dstDir, "sub/test2.txt")

        assertTrue(unzippedFile1.exists())
        assertEquals("Hello Catroid", unzippedFile1.readText())
        assertTrue(unzippedFile2.exists())
        assertEquals("Nested Content", unzippedFile2.readText())
    }

    @Test
    fun testZipSlipPathTraversalEntriesAreIgnored() {
        val dstDir = tempFolder.newFolder("extract_dest")
        val outsideVictim = File(tempFolder.root, "victim.txt")

        val byteOutput = ByteArrayOutputStream()
        ZipOutputStream(byteOutput).use { zos ->
            // Valid entry
            zos.putNextEntry(ZipEntry("valid.txt"))
            zos.write("Valid content".toByteArray())
            zos.closeEntry()

            // Path traversal entries
            zos.putNextEntry(ZipEntry("../victim.txt"))
            zos.write("Malicious payload".toByteArray())
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("nested/../../victim2.txt"))
            zos.write("Malicious payload 2".toByteArray())
            zos.closeEntry()
        }

        zipArchiver.unzip(ByteArrayInputStream(byteOutput.toByteArray()), dstDir)

        assertTrue(File(dstDir, "valid.txt").exists())
        assertEquals("Valid content", File(dstDir, "valid.txt").readText())

        assertFalse("Zip Slip file was created outside destination directory!", outsideVictim.exists())
        assertFalse("Zip Slip nested traversal file was created!", File(tempFolder.root, "victim2.txt").exists())
    }

    @Test
    fun testUnzipCreatesDeeplyNestedDirectories() {
        val dstDir = tempFolder.newFolder("nested_dest")

        val byteOutput = ByteArrayOutputStream()
        ZipOutputStream(byteOutput).use { zos ->
            zos.putNextEntry(ZipEntry("level1/level2/level3/deep.txt"))
            zos.write("Deep content".toByteArray())
            zos.closeEntry()
        }

        zipArchiver.unzip(ByteArrayInputStream(byteOutput.toByteArray()), dstDir)

        val deepFile = File(dstDir, "level1/level2/level3/deep.txt")
        assertTrue(deepFile.exists())
        assertEquals("Deep content", deepFile.readText())
    }
}
