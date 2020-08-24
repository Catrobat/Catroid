/* Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.io

import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.LegoEV3Setting
import org.catrobat.catroid.content.LegoNXTSetting
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.backwardcompatibility.BrickTreeBuilder
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.exceptions.CompatibilityProjectException
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.io.StorageOperations.duplicateFile
import org.catrobat.catroid.physics.PhysicsCollisionListener
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import java.io.File
import java.io.IOException
import java.util.ArrayList

@SuppressWarnings("MagicNumber", "SwallowedException", "ThrowsCount", "NestedBlockDepth")
class ProjectLoadAndUpdate {

    @Throws(ProjectException::class)
    fun loadProject(projectDir: File?, updateStringProvider: UpdateStringProvider) {
        val previousProject = ProjectManager.getInstance().currentProject

        val projectToLoad: Project = try {
            XstreamSerializer.getInstance().loadProject(
                projectDir,
                updateStringProvider.getDefaultSceneName()
            )
        } catch (e: IOException) {
            restorePreviousProject(previousProject)
            throw LoadingProjectException(updateStringProvider.getLoadErrorMessage())
        }
        ProjectManager.getInstance().currentProject = projectToLoad

        val projectCatrobatLanguageVersion: Float = projectToLoad.catrobatLanguageVersion
        when {
            projectCatrobatLanguageVersion > Constants.CURRENT_CATROBAT_LANGUAGE_VERSION -> {
                restorePreviousProject(previousProject)
                throw OutdatedVersionProjectException(updateStringProvider.getOutdatedErrorMessage())
            }
            projectCatrobatLanguageVersion < 0.9f && projectCatrobatLanguageVersion != 0.8f -> {
                restorePreviousProject(previousProject)
                throw CompatibilityProjectException(updateStringProvider.getCompatibilityErrorMessage())
            }
        }

        doLanguageVersionDependentUpdates(projectToLoad, projectCatrobatLanguageVersion)

        projectToLoad.catrobatLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION
        localizeBackgroundSprites(projectToLoad, updateStringProvider.getBackgroundString())
        initializeScripts(projectToLoad)
        loadLegoNXTSettingsFromProject(projectToLoad)
        loadLegoEV3SettingsFromProject(projectToLoad)

        val resourcesSet: ResourcesSet = projectToLoad.requiredResources
        if (resourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
            SettingsFragment.setPhiroSharedPreferenceEnabled(true)
        }
        if (resourcesSet.contains(Brick.JUMPING_SUMO)) {
            SettingsFragment.setJumpingSumoSharedPreferenceEnabled(true)
        }
        if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
            SettingsFragment.setArduinoSharedPreferenceEnabled(true)
        }
        ProjectManager.getInstance().currentlyPlayingScene = projectToLoad.defaultScene
        ProjectManager.getInstance().currentSprite = null
    }

    private fun doLanguageVersionDependentUpdates(project: Project, languageVersion: Float) {
        if (languageVersion <= 0.91f) {
            project.xmlHeader.setScreenMode(ScreenModes.STRETCH)
        }

        if (languageVersion <= 0.92f) {
            updateCollisionFormulasTo993(project)
        }

        if (languageVersion <= 0.993f) {
            updateSetPenColorFormulasTo994(project)
        }

        if (languageVersion <= 0.994f) {
            updateArduinoValuesTo995(project)
        }

        if (languageVersion <= 0.995f) {
            updateCollisionScriptsTo996(project)
        }

        if (languageVersion <= 0.999f) {
            makeShallowCopiesDeepAgain(project)
        }

        if (languageVersion <= 0.9993f) {
            updateScriptsToTreeStructure(project)
        }

        if (languageVersion <= 0.99992f) {
            removePermissionsFile(project)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun restorePreviousProject(previousProject: Project?) {
        ProjectManager.getInstance().currentProject = previousProject
        if (previousProject != null) {
            ProjectManager.getInstance().currentlyPlayingScene = previousProject.defaultScene
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateCollisionFormulasTo993(project: Project) {
        ProjectUpdater(project).updateScript { script ->
            val flatList: ArrayList<Brick?> = ArrayList<Brick?>()
            script.addToFlatList(flatList)
            for (brick in flatList) {
                if (brick is FormulaBrick) {
                    for (formula in brick.formulas) {
                        formula.updateCollisionFormulasToVersion()
                    }
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateSetPenColorFormulasTo994(project: Project) {
        ProjectUpdater(project).updateBrick { brick ->
            if (brick is SetPenColorBrick) {
                brick.replaceFormulaBrickField(
                    BrickField.PHIRO_LIGHT_RED,
                    BrickField.PEN_COLOR_RED)
                brick.replaceFormulaBrickField(
                    BrickField.PHIRO_LIGHT_GREEN,
                    BrickField.PEN_COLOR_GREEN)
                brick.replaceFormulaBrickField(
                    BrickField.PHIRO_LIGHT_BLUE,
                    BrickField.PEN_COLOR_BLUE)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateArduinoValuesTo995(project: Project) {
        ProjectUpdater(project).updateBrick { brick ->
            if (brick is ArduinoSendPWMValueBrick) {
                brick.updateArduinoValues994to995()
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateCollisionScriptsTo996(project: Project) {
        val projectUpdater = ProjectUpdater(project)

        projectUpdater.updateScript { script ->
            if (script is WhenBounceOffScript) {
                val spriteNames =
                    script.spriteToBounceOffName
                        .split(PhysicsCollisionListener.COLLISION_MESSAGE_CONNECTOR.toRegex())
                        .toTypedArray()
                var spriteToCollideWith = spriteNames[0]
                if (spriteNames[0] == projectUpdater.currentSprite?.name) {
                    spriteToCollideWith = spriteNames[1]
                }
                script.spriteToBounceOffName = spriteToCollideWith
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun makeShallowCopiesDeepAgain(project: Project) {
        for (scene in project.sceneList) {
            val fileNames: MutableList<String> =
                ArrayList()
            for (sprite in scene.spriteList) {
                copyShallowFiles<LookData>(fileNames, sprite.lookList)
                copyShallowFiles<SoundInfo>(fileNames, sprite.soundList)
            }
        }
    }

    private fun <T> copyShallowFiles(
        existingFiles: MutableList<String>,
        filesToCheck: MutableList<T>
    ) {
        val iterator = filesToCheck.iterator()
        while (iterator.hasNext()) {
            val objectData: T = iterator.next()

            val objectFile = when (objectData) {
                is LookData -> objectData.file
                is SoundInfo -> objectData.file
                else -> null
            }

            if (existingFiles.contains(objectFile?.name)) {
                try {
                    if (objectData is LookData) objectData.file = duplicateFile(objectData.file)
                    if (objectData is SoundInfo) objectData.file = duplicateFile(objectData.file)
                } catch (e: IOException) {
                    iterator.remove()
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateScriptsToTreeStructure(project: Project) {
        ProjectUpdater(project).updateScript { script ->
            val brickTreeBuilder = BrickTreeBuilder()
            brickTreeBuilder.convertBricks(script.brickList)
            script.brickList.clear()
            script.brickList.addAll(brickTreeBuilder.toList())
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun removePermissionsFile(project: Project) {
        val permissionsFile = File(project.directory, Constants.PERMISSIONS_FILE_NAME)
        if (permissionsFile.exists()) {
            permissionsFile.delete()
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun localizeBackgroundSprites(project: Project, localizedBackgroundName: String) {
        ProjectUpdater(project).updateScene { scene ->
            if (scene.spriteList.isNotEmpty()) {
                val background = scene.spriteList[0]
                background.name = localizedBackgroundName
                background.look.zIndex = 0
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun initializeScripts(project: Project) {
        val projectUpdater = ProjectUpdater(project)

        projectUpdater.updateScript { script ->
            script.setParents()
            if (script is WhenBounceOffScript) {
                script.updateSpriteToCollideWith(projectUpdater.currentScene)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun loadLegoNXTSettingsFromProject(project: Project) {
        for (setting in project.settings) {
            if (setting is LegoNXTSetting) {
                SettingsFragment.enableLegoMindstormsNXTBricks()
                SettingsFragment.setLegoMindstormsNXTSensorMapping(setting.sensorMapping)
                return
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun loadLegoEV3SettingsFromProject(project: Project) {
        for (setting in project.settings) {
            if (setting is LegoEV3Setting) {
                SettingsFragment.enableLegoMindstormsEV3Bricks()
                SettingsFragment.setLegoMindstormsEV3SensorMapping(setting.sensorMapping)
                return
            }
        }
    }

    interface UpdateStringProvider {
        fun getBackgroundString(): String
        fun getLoadErrorMessage(): String
        fun getOutdatedErrorMessage(): String
        fun getCompatibilityErrorMessage(): String
        fun getDefaultSceneName(): String
    }
}
