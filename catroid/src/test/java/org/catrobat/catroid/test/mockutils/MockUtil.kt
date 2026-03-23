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
package org.catrobat.catroid.test.mockutils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenValues
import org.mockito.ArgumentMatchers
import org.mockito.MockedStatic
import org.mockito.Mockito
import java.io.File
import java.io.IOException

object MockUtil {
    private var isInitialized = false
    private lateinit var fileDir: File
    private lateinit var cacheDir: File
    private const val FILE_DIR_NAME = "fileDir"
    private const val CACHE_DIR_NAME = "cacheDir"

    private lateinit var catroidApplicationMockedStatic: MockedStatic<CatroidApplication?>
    private lateinit var contextMock: Context
    private lateinit var packageManagerMock: PackageManager

    @JvmStatic
    fun initializeStaticsAndSingletons() {
        if (isInitialized) {
            return
        }

        try {
            catroidApplicationMockedStatic = Mockito.mockStatic(CatroidApplication::class.java)
            contextMock = Mockito.mock(Context::class.java)
            packageManagerMock = Mockito.mock(PackageManager::class.java)

            // App Context:
            // Lives until JVM shutdown without explicit close
            catroidApplicationMockedStatic.`when`<Context> { CatroidApplication.getAppContext() }
                .thenReturn(contextMock)

            // Context:
            Mockito.`when`(contextMock.packageManager).thenReturn(
                packageManagerMock
            )
            Mockito.`when`(contextMock.packageName).thenReturn("testStubPackage")
            Mockito.`when`(contextMock.getString(R.string.background)).thenReturn("Background")
            Mockito.`when`(
                contextMock.getString(
                    ArgumentMatchers.eq(R.string.look_request_http_error_message),
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any()
                )
            ).thenReturn("HTTP Error")
            val packageInfoStub = PackageInfo()
            packageInfoStub.versionName = "testStub"
            Mockito.`when`(
                packageManagerMock.getPackageInfo(
                    ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()
                )
            ).thenReturn(packageInfoStub)

            // ProjectManager:
            require(ProjectManager.getInstance() == null) {
                "ProjectManager instance should be null before initializing MockUtil"
            }
            ProjectManager(contextMock)

            // Constants and FlavoredConstants:
            val testResourcesDir = File("src/test/resources")
            fileDir = File(testResourcesDir, FILE_DIR_NAME)
            cacheDir = File(testResourcesDir, CACHE_DIR_NAME)
            fileDir.mkdirs()
            cacheDir.mkdirs()
            fileDir.deleteOnExit()
            cacheDir.deleteOnExit()
            Mockito.`when`(contextMock.filesDir).thenReturn(fileDir)
            Mockito.`when`(contextMock.cacheDir).thenReturn(cacheDir)

            Class.forName("org.catrobat.catroid.common.FlavoredConstants")
            Class.forName("org.catrobat.catroid.common.Constants")

            ScreenValues.setToDefaultScreenSize()

            isInitialized = true
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize MockUtil", e)
        }
    }

    @JvmStatic
    fun getApplicationContextMock(): Context {
        initializeStaticsAndSingletons()
        return contextMock
    }

    fun cleanUpFiles() {
        if (!isInitialized) {
            return
        }

        if (::fileDir.isInitialized && fileDir.exists()) {
            deleteDirectoryContent(fileDir)
        }
        if (::cacheDir.isInitialized && cacheDir.exists()) {
            deleteDirectoryContent(cacheDir)
        }
    }

    private fun deleteDirectoryContent(directory: File) {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectoryContent(file)
            }
            try {
                file.delete()
            } catch (e: SecurityException) {
                throw IOException("Failed to delete file: ${file.absolutePath}", e)
            }
        }
    }
}
