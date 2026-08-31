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

package org.catrobat.catroid.ui.shortcut

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.pm.ShortcutManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File

/**
 * Transparent trampoline activity that validates the project on disk and launches
 * StageActivity. Exported so the home launcher can start it via pinned shortcuts.
 * Excluded from the recents list.
 *
 * Uses plain [Activity] (not AppCompatActivity) so that
 * [android:theme="@android:style/Theme.Translucent.NoTitleBar"] works without
 * requiring an AppCompat theme.
 */
class ShortcutTrampolineActivity : Activity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectName = intent?.getStringExtra(EXTRA_PROJECT_NAME)
        if (projectName.isNullOrBlank()) {
            Log.e(TAG, "No project name in shortcut intent")
            finish()
            return
        }

        scope.launch {
            val projectDir = withContext(Dispatchers.IO) {
                resolveProjectDirectory(projectName)
            }

            if (projectDir == null) {
                disableShortcutAndFinish(projectName)
                return@launch
            }

            val codeXml = File(projectDir, Constants.CODE_XML_FILE_NAME)
            val isAccessible = withContext(Dispatchers.IO) {
                codeXml.exists() && codeXml.canRead()
            }

            if (!isAccessible) {
                Toast.makeText(
                    this@ShortcutTrampolineActivity,
                    R.string.project_busy_try_again,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            val projectManager = ProjectManager.getInstance()
            if (projectManager == null) {
                Log.e(TAG, "ProjectManager not initialized")
                Toast.makeText(
                    this@ShortcutTrampolineActivity,
                    R.string.project_busy_try_again,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            val appContext = applicationContext
            val loaded = withContext(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    projectManager.loadProject(projectDir, appContext)
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load project: ${e.message}", e)
                    false
                }
            }

            if (!loaded) {
                Toast.makeText(
                    this@ShortcutTrampolineActivity,
                    R.string.project_busy_try_again,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            val project = projectManager.currentProject
            if (project != null) {
                projectManager.currentlyEditedScene = project.defaultScene
                projectManager.setCurrentlyPlayingScene(project.defaultScene)
                projectManager.startScene = project.defaultScene
            }

            val stageIntent = Intent(
                this@ShortcutTrampolineActivity,
                StageActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(StageActivity.EXTRA_IS_FROM_SHORTCUT, true)
            }
            startActivity(stageIntent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun resolveProjectDirectory(projectName: String): File? {
        val encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, encodedName)
        return if (projectDir.exists() && projectDir.isDirectory) projectDir else null
    }

    private fun disableShortcutAndFinish(projectName: String) {
        val encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
        try {
            ShortcutManagerCompat.disableShortcuts(
                this,
                listOf(encodedName),
                getString(R.string.shortcut_project_not_found)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not disable shortcut: ${e.message}")
        }
        Toast.makeText(this, R.string.shortcut_project_not_found, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        private const val TAG = "ShortcutTrampoline"
        const val EXTRA_PROJECT_NAME = "shortcut_project_name"
    }
}
