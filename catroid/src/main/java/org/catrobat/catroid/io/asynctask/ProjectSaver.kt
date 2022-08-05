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
package org.catrobat.catroid.io.asynctask

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.content.LegoEV3Setting
import org.catrobat.catroid.content.LegoNXTSetting
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import java.lang.ref.WeakReference

class ProjectSaver(private val project: Project, context: Context) {
    private val weakContextReference: WeakReference<Context> = WeakReference(context)

    @JvmOverloads
    fun saveProjectAsync(
        onSaveProjectComplete: (Boolean) -> Unit = {},
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ) {
        val context = weakContextReference.get() ?: return
        scope.launch {
            val projectSaved = saveProjectSerial(project, context)

            withContext(Dispatchers.Main) {
                onSaveProjectComplete(projectSaved)
            }
        }
    }
}

fun saveProjectSerial(project: Project?, context: Context): Boolean {
    project ?: return false
    saveLegoNXTSettingsToProject(project, context)
    saveLegoEV3SettingsToProject(project, context)
    return XstreamSerializer.getInstance().saveProject(project)
}

private fun saveLegoNXTSettingsToProject(project: Project, context: Context) {
    if (!project.requiredResources.contains(Brick.BLUETOOTH_LEGO_NXT)) {
        project.settings.toTypedArray().filterIsInstance<LegoNXTSetting>().forEach { setting ->
            project.settings.remove(setting)
        }
        return
    }

    val sensorMapping = SettingsFragment.getLegoNXTSensorMapping(context)
    project.settings.filterIsInstance<LegoNXTSetting>().forEach { setting ->
        setting.updateMapping(sensorMapping)
        return
    }
    project.settings.add(LegoNXTSetting(sensorMapping))
}

private fun saveLegoEV3SettingsToProject(project: Project, context: Context) {
    if (!project.requiredResources.contains(Brick.BLUETOOTH_LEGO_EV3)) {
        project.settings.toTypedArray().filterIsInstance<LegoEV3Setting>().forEach { setting ->
            project.settings.remove(setting)
        }
        return
    }

    val sensorMapping = SettingsFragment.getLegoEV3SensorMapping(context)
    project.settings.filterIsInstance<LegoEV3Setting>().forEach { setting ->
        setting.updateMapping(sensorMapping)
        return
    }
    project.settings.add(LegoEV3Setting(sensorMapping))
}
