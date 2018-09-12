/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.support.v7.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.*
import org.catrobat.catroid.ui.WebViewActivity.INTENT_PARAMETER_URL
import android.provider.MediaStore
import org.catrobat.catroid.common.Constants.FILE_PROVIDER_AUTHORITY
import android.support.v4.content.FileProvider
import java.io.File


interface ImportLauncher {

    fun startActivityForResult(requestCode: Int)
}

class ImportFromPocketPaintLauncher(private val activity: AppCompatActivity) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent("android.intent.action.MAIN")
                .setComponent(ComponentName(activity, POCKET_PAINT_INTENT_ACTIVITY_NAME))

        val bundle = Bundle()
        bundle.putString(EXTRA_PICTURE_PATH_POCKET_PAINT, "")
        bundle.putString(EXTRA_PICTURE_NAME_POCKET_PAINT, activity.getString(R.string.default_look_name))
        intent.putExtras(bundle)
        intent.addCategory("android.intent.category.LAUNCHER")

        activity.startActivityForResult(intent, requestCode)
    }
}

class ImportFormMediaLibraryLauncher(private val activity: AppCompatActivity, private val url: String) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(activity, WebViewActivity::class.java)
        intent.putExtra(INTENT_PARAMETER_URL, url)
        activity.startActivityForResult(intent, requestCode)
    }
}

class ImportFromFileLauncher(private val activity: AppCompatActivity, private val type: String, private val title: String) : ImportLauncher {

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        activity.startActivityForResult(Intent.createChooser(intent, title), requestCode)
    }
}

class ImportFromCameraLauncher(private val activity: AppCompatActivity) : ImportLauncher {

    fun getCacheCameraUri(): Uri {
        val childName = activity.getString(R.string.default_look_name)
        val cacheDir = File(activity.cacheDir.absolutePath + "/cameraCache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val pictureFile = File(cacheDir, "$childName.jpg")
        return FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY, pictureFile)
    }

    override fun startActivityForResult(requestCode: Int) {
        val intent = Intent(ACTION_IMAGE_CAPTURE)
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val uri = getCacheCameraUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val chooser = Intent.createChooser(intent, activity.getString(R.string.select_look_from_camera))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(chooser, requestCode)
        }
    }
}