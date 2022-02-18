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

package org.catrobat.catroid.ui

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CAMERA_CACHE_DIR
import org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION
import org.catrobat.catroid.common.Constants.EXTRA_PICTURE_PATH_POCKET_PAINT
import org.catrobat.catroid.common.Constants.POCKET_PAINT_CACHE_DIR
import org.catrobat.catroid.common.Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME
import org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.WebViewActivity.INTENT_PARAMETER_URL
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import java.io.File
import java.io.IOException

interface ImportLauncher {

    fun startActivityForResult(requestCode: Int)
}

class ImportFromPocketPaintLauncher(private val activity: Activity) : ImportLauncher {

    private val pocketPaintImageFileName = TMP_IMAGE_FILE_NAME + DEFAULT_IMAGE_EXTENSION

    fun getPocketPaintCacheUri(): Uri {
        return FileProvider.getUriForFile(
            activity,
            activity.applicationContext.packageName + ".fileProvider",
            File(POCKET_PAINT_CACHE_DIR, pocketPaintImageFileName)
        )
    }

    private fun createEmptyImageFile(): File {
        POCKET_PAINT_CACHE_DIR.mkdirs()
        if (!POCKET_PAINT_CACHE_DIR.isDirectory) {
            throw IOException("Cannot create ${POCKET_PAINT_CACHE_DIR.absolutePath}.")
        }

        val currentProject = ProjectManager.getInstance().currentProject
        val bitmap = Bitmap.createBitmap(
            currentProject.xmlHeader.virtualScreenWidth,
            currentProject.xmlHeader.virtualScreenHeight, Bitmap.Config.ARGB_8888
        )
        return StorageOperations.compressBitmapToPng(bitmap, File(POCKET_PAINT_CACHE_DIR, pocketPaintImageFileName))
    }

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent("android.intent.action.MAIN")
            .setComponent(ComponentName(activity, POCKET_PAINT_INTENT_ACTIVITY_NAME))

        val bundle = Bundle()

        bundle.putString(EXTRA_PICTURE_PATH_POCKET_PAINT, createEmptyImageFile().absolutePath)
        intent.putExtras(bundle)

        intent.addCategory("android.intent.category.LAUNCHER")
        activity.startActivityForResult(intent, requestCode)
    }
}

class ImportFormMediaLibraryLauncher(
    private val activity: AppCompatActivity,
    private val url: String
) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(activity, WebViewActivity::class.java)
        intent.putExtra(INTENT_PARAMETER_URL, url)
        activity.startActivityForResult(intent, requestCode)
    }
}

class ImportFromFileLauncher(
    private val activity: AppCompatActivity,
    private val type: String,
    private val title: String
) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        activity.startActivityForResult(Intent.createChooser(intent, title), requestCode)
    }
}

class ImportFromCameraLauncher(private val activity: AppCompatActivity) : ImportLauncher {

    private val cameraImageFileName = "$TMP_IMAGE_FILE_NAME.jpg"

    fun getCacheCameraUri(): Uri {
        return FileProvider.getUriForFile(
            activity,
            activity.applicationContext.packageName + ".fileProvider",
            File(CAMERA_CACHE_DIR, cameraImageFileName)
        )
    }

    fun createCameraCacheDir() {
        CAMERA_CACHE_DIR.mkdirs()
        if (!CAMERA_CACHE_DIR.isDirectory) {
            throw IOException("Cannot create ${CAMERA_CACHE_DIR.absolutePath}.")
        }
    }

    override fun startActivityForResult(requestCode: Int) {
        object : RequiresPermissionTask(
            REQUEST_PERMISSIONS_CAMERA_LAUNCHER, listOf(CAMERA),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                createCameraCacheDir()
                val intent = Intent(ACTION_IMAGE_CAPTURE)
                intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheCameraUri())

                val chooser = Intent.createChooser(intent, activity.getString(R.string.select_look_from_camera))

                try {
                    activity.startActivityForResult(chooser, requestCode)
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, "Could not find camera.")
                }
            }
        }.execute(activity)
    }

    companion object {
        @JvmStatic
        val REQUEST_PERMISSIONS_CAMERA_LAUNCHER = 301
    }
}

class ImportFromLocalProjectListLauncher(
    private val activity: AppCompatActivity,
    private val title: String
) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(activity, ProjectListActivity::class.java)
        intent.putExtra(ProjectListActivity.IMPORT_LOCAL_INTENT, title)
        activity.startActivityForResult(intent, requestCode)
    }
}
