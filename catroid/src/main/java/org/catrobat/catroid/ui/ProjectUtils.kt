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

@file:JvmName("ProjectUtils")

/**
 * suspicious bricks as defined in CATROID-681
 *
 * StartListeningBrick and WebRequestBrick or BackgroundRequestBrick or LookRequestBrick
 * */

package org.catrobat.catroid.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.koin.java.KoinJavaComponent

/**
 * extension boolean function for List<Brick> data type.
 * check if the list contains suspicious bricks
 * */
private fun List<Brick>.containsSuspiciousBricks(): Boolean {
    val startListeningBrickExists = any { brick ->
        brick is StartListeningBrick
    }
    val backgroundRequestOrWebRequestBrickExists = any { brick ->
        brick is WebRequestBrick || brick is BackgroundRequestBrick || brick is LookRequestBrick
    }
    return startListeningBrickExists and backgroundRequestOrWebRequestBrickExists
}

/**
 * extension function for Sprite data type.
 * get list of all bricks and its nested bricks if exists
 * */
public fun Sprite.getListAllBricks(): List<Brick> {
    val bricks = arrayListOf<Brick>()
    allBricks.forEach { brick ->
        bricks.add(brick)
        when (brick) {
            is ForeverBrick ->
                bricks.addAll(brick.nestedBricks)

            is IfLogicBeginBrick -> {
                bricks.addAll(brick.nestedBricks)
                bricks.addAll(brick.secondaryNestedBricks)
            }
            is IfThenLogicBeginBrick ->
                bricks.addAll(brick.nestedBricks)

            is RepeatBrick ->
                bricks.addAll(brick.nestedBricks)

            is RepeatUntilBrick ->
                bricks.addAll(brick.nestedBricks)

            is ForVariableFromToBrick ->
                bricks.addAll(brick.nestedBricks)

            is ForItemInUserListBrick ->
                bricks.addAll(brick.nestedBricks)

            is ParameterizedBrick ->
                bricks.addAll(brick.nestedBricks)

            is PhiroIfLogicBeginBrick -> {
                bricks.addAll(brick.nestedBricks)
                bricks.addAll(brick.secondaryNestedBricks)
            }
        }
    }
    return bricks
}

/**
 * extension boolean function for Project data type.
 * check if the project contains suspicious bricks
 * */
private fun Project.shouldDisplaySuspiciousBricksWarning(): Boolean {
    val brickList = arrayListOf<Brick>()
    sceneList.forEach { scene ->
        brickList.run {
            scene.spriteList.forEach { sprite ->
                addAll(sprite.getListAllBricks())
            }
        }
    }
    return brickList.containsSuspiciousBricks()
}

fun showWarningForSuspiciousBricksOnce(context: Context) {
    // used for not showing the dialog again
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_approved_list_file_key),
        MODE_PRIVATE
    )
    val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
    val currentProject = projectManager.currentProject
    val projectUrl = currentProject?.xmlHeader?.remixParentsUrlString ?: return
    // if project has an url => is a downloaded project
    val isDownloadedProject = projectUrl.isNotBlank()
    // since the projectUrl is kinda unique, ues it as key for the shared preference, if it's null
    // that means the dialog hasn't been displayed yet
    val showForFirstTime = sharedPreferences.getString(projectUrl, null).isNullOrBlank()

    if (isDownloadedProject && currentProject.shouldDisplaySuspiciousBricksWarning() && showForFirstTime) {
        AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.warning))
            .setMessage(context.resources.getString(R.string.security_warning_dialog_msg))
            .setCancelable(false)
            .setPositiveButton(context.resources.getString(R.string.ok)) { dialog, _ ->
                sharedPreferences
                    .edit()
                    .putString(projectUrl, projectUrl)
                    .apply()
                dialog.dismiss()
            }
            .show()
    } else if (isDownloadedProject) {
        // add it anyway to avoid showing this dialog for downloaded projects, but have
        // suspicious bricks added by the user afterwards..
        sharedPreferences
            .edit()
            .putString(projectUrl, projectUrl)
            .apply()
    }
}
