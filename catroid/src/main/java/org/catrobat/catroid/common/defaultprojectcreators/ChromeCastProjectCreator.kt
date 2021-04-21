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
package org.catrobat.catroid.common.defaultprojectcreators

import android.content.Context
import android.graphics.BitmapFactory
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.ImageEditing
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ChromeCastProjectCreator : ProjectCreator() {
    @Throws(IOException::class)
    override fun createDefaultProject(
        name: String,
        context: Context,
        landscapeMode: Boolean
    ): Project {
        val project = Project(context, name, true, true)
        if (project.directory.exists()) {
            throw IOException(
                "Cannot create new project at "
                    + project.directory.absolutePath
                    + ", directory already exists."
            )
        }
        XstreamSerializer.getInstance().saveProject(project)
        if (!project.directory.isDirectory) {
            throw FileNotFoundException("Cannot create project at " + project.directory.absolutePath)
        }
        val backgroundDrawableId = R.drawable.default_project_background_landscape
        val cloudDrawableId = R.drawable.default_project_clouds_landscape
        val screenshotDrawableId = R.drawable.default_project_screenshot_landscape
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, backgroundDrawableId, options)
        backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
            options.outWidth,
            options.outHeight,
            ScreenValues.CAST_SCREEN_WIDTH,
            ScreenValues.CAST_SCREEN_HEIGHT
        )
        val scene = project.defaultScene
        val imageDir = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
        val soundDir = File(scene.directory, Constants.SOUND_DIRECTORY_NAME)
        val imageFileName = "img" + Constants.DEFAULT_IMAGE_EXTENSION
        val soundFileName = "snd" + Constants.DEFAULT_SOUND_EXTENSION
        val backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            backgroundDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val cloudFile1 = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            cloudDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val cloudFile2 = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            cloudDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            R.drawable.default_project_bird_wing_up,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            R.drawable.default_project_bird_wing_down,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdLeftWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            R.drawable.default_project_bird_wing_up_left,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdLeftWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            R.drawable.default_project_bird_wing_down_left,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val tweetFile1 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            context.resources,
            R.raw.default_project_tweet_1,
            soundDir,
            soundFileName
        )
        val tweetFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            context.resources,
            R.raw.default_project_tweet_2,
            soundDir,
            soundFileName
        )
        ResourceImporter.createImageFileFromResourcesInDirectory(
            context.resources,
            screenshotDrawableId,
            scene.directory,
            Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, 1.0
        )
        val background = scene.spriteList[0]
        background.lookList
            .add(
                LookData(
                    context.getString(R.string.default_cast_project_background_name),
                    backgroundFile
                )
            )
        val cloud1 = Sprite(context.getString(R.string.default_cast_project_cloud_sprite_name1))
        val cloud2 = Sprite(context.getString(R.string.default_cast_project_cloud_sprite_name2))
        val bird = Sprite(context.getString(R.string.default_cast_project_sprites_bird_name))
        scene.addSprite(cloud1)
        scene.addSprite(cloud2)
        scene.addSprite(bird)
        cloud1.lookList
            .add(LookData(context.getString(R.string.default_cast_project_cloud_name), cloudFile1))
        cloud2.lookList
            .add(LookData(context.getString(R.string.default_cast_project_cloud_name), cloudFile2))
        bird.lookList
            .add(
                LookData(
                    context.getString(R.string.default_cast_project_sprites_bird_name_wing_up),
                    birdWingUpFile
                )
            )
        bird.lookList
            .add(
                LookData(
                    context.getString(R.string.default_cast_project_sprites_bird_name_wing_down),
                    birdWingDownFile
                )
            )
        bird.lookList
            .add(
                LookData(
                    context.getString(R.string.default_cast_project_sprites_bird_name_wing_up_left),
                    birdLeftWingUpFile
                )
            )
        bird.lookList
            .add(
                LookData(
                    context.getString(R.string.default_cast_project_sprites_bird_name_wing_down_left),
                    birdLeftWingDownFile
                )
            )
        bird.soundList
            .add(
                SoundInfo(
                    context.getString(R.string.default_cast_project_sprites_tweet_1),
                    tweetFile1
                )
            )
        bird.soundList
            .add(
                SoundInfo(
                    context.getString(R.string.default_cast_project_sprites_tweet_2),
                    tweetFile2
                )
            )
        var script: Script = StartScript()
        script.addBrick(PlaceAtBrick(Formula(0), Formula(0)))
        script.addBrick(
            GlideToBrick(
                Formula(-ScreenValues.CAST_SCREEN_WIDTH),
                Formula(0),
                Formula(5)
            )
        )
        script.addBrick(PlaceAtBrick(ScreenValues.CAST_SCREEN_WIDTH, 0))
        var loopBrick = ForeverBrick()
        loopBrick.addBrick(
            GlideToBrick(
                Formula(-ScreenValues.CAST_SCREEN_WIDTH),
                Formula(0),
                Formula(10)
            )
        )
        loopBrick.addBrick(PlaceAtBrick(ScreenValues.CAST_SCREEN_WIDTH, 0))
        script.addBrick(loopBrick)
        cloud1.addScript(script)
        script = StartScript()
        script.addBrick(PlaceAtBrick(Formula(ScreenValues.CAST_SCREEN_WIDTH), Formula(0)))
        loopBrick = ForeverBrick()
        loopBrick.addBrick(
            GlideToBrick(
                Formula(-ScreenValues.CAST_SCREEN_WIDTH),
                Formula(0),
                Formula(10)
            )
        )
        loopBrick.addBrick(PlaceAtBrick(Formula(ScreenValues.CAST_SCREEN_WIDTH), Formula(0)))
        script.addBrick(loopBrick)
        cloud2.addScript(script)
        script = StartScript()
        loopBrick = ForeverBrick()
        val minX = FormulaElement(FormulaElement.ElementType.NUMBER, "-640", null)
        val maxX = FormulaElement(FormulaElement.ElementType.NUMBER, "640", null)
        val minY = FormulaElement(FormulaElement.ElementType.NUMBER, "-360", null)
        val maxY = FormulaElement(FormulaElement.ElementType.NUMBER, "360", null)
        val birdX = FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_X.name, null)
        val birdY = FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_Y.name, null)
        var ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    Sensors.GAMEPAD_UP_PRESSED.name,
                    null
                )
            )
        )
        var innerIfBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.OPERATOR,
                    Operators.EQUAL.name,
                    null,
                    maxY,
                    birdY
                )
            )
        )
        innerIfBrick.addBrickToIfBranch(
            PlaceAtBrick(
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.SENSOR,
                        Sensors.OBJECT_X.name,
                        null
                    )
                ),
                Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "-360", null))
            )
        )
        ifBrick.addBrickToIfBranch(innerIfBrick)
        ifBrick.addBrickToIfBranch(ChangeYByNBrick(Formula(5)))
        script.addBrick(ifBrick)
        ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    Sensors.GAMEPAD_DOWN_PRESSED.name,
                    null
                )
            )
        )
        innerIfBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.OPERATOR,
                    Operators.EQUAL.name,
                    null,
                    minY,
                    birdY
                )
            )
        )
        innerIfBrick.addBrickToIfBranch(
            PlaceAtBrick(
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.SENSOR,
                        Sensors.OBJECT_X.name,
                        null
                    )
                ),
                Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "360", null))
            )
        )
        ifBrick.addBrickToIfBranch(innerIfBrick)
        ifBrick.addBrickToIfBranch(ChangeYByNBrick(Formula(-5)))
        script.addBrick(ifBrick)
        val directionVar =
            UserVariable(context.getString(R.string.default_cast_project_var_direction))
        project.addUserVariable(directionVar)
        ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    Sensors.GAMEPAD_LEFT_PRESSED.name,
                    null
                )
            )
        )
        ifBrick.addBrickToIfBranch(
            SetVariableBrick(
                Formula(
                    FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.TRUE.name, null)
                ), directionVar
            )
        )
        var setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[2]
        ifBrick.addBrickToIfBranch(setLookBrick)
        innerIfBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.OPERATOR,
                    Operators.EQUAL.name,
                    null,
                    minX,
                    birdX
                )
            )
        )
        innerIfBrick.addBrickToIfBranch(
            PlaceAtBrick(
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.SENSOR,
                        Sensors.OBJECT_X.name,
                        null
                    )
                ),
                Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "640", null))
            )
        )
        ifBrick.addBrickToIfBranch(innerIfBrick)
        ifBrick.addBrickToIfBranch(ChangeYByNBrick(Formula(-5)))
        script.addBrick(ifBrick)
        script.addBrick(loopBrick)
        bird.addScript(script)
        ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    Sensors.GAMEPAD_RIGHT_PRESSED.name,
                    null
                )
            )
        )
        ifBrick.addBrickToIfBranch(
            SetVariableBrick(
                Formula(
                    FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.TRUE.name, null)
                ), directionVar
            )
        )
        setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[0]
        ifBrick.addBrickToIfBranch(setLookBrick)
        innerIfBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.OPERATOR,
                    Operators.EQUAL.name,
                    null,
                    maxX,
                    birdX
                )
            )
        )
        innerIfBrick.addBrickToIfBranch(
            PlaceAtBrick(
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.SENSOR,
                        Sensors.OBJECT_X.name,
                        null
                    )
                ),
                Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "-640", null))
            )
        )
        ifBrick.addBrickToIfBranch(innerIfBrick)
        ifBrick.addBrickToIfBranch(ChangeYByNBrick(Formula(5)))
        script.addBrick(ifBrick)
        script.addBrick(loopBrick)
        bird.addScript(script)
        script = WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_A))
        ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(FormulaElement.ElementType.USER_VARIABLE, directionVar.name, null)
            )
        )
        setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[3]
        ifBrick.addBrickToIfBranch(setLookBrick)
        ifBrick.addBrickToIfBranch(WaitBrick(Formula(0.1)))
        setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[2]
        ifBrick.addBrickToIfBranch(setLookBrick)
        setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[0]
        ifBrick.addBrickToIfBranch(setLookBrick)
        ifBrick.addBrickToIfBranch(WaitBrick(Formula(0.1)))
        setLookBrick = SetLookBrick()
        setLookBrick.look = bird.lookList[1]
        ifBrick.addBrickToIfBranch(setLookBrick)
        script.addBrick(ifBrick)
        bird.addScript(script)
        script = WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_B))
        ifBrick = IfLogicBeginBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.USER_VARIABLE,
                    directionVar.name,
                    null
                )
            )
        )
        var playSoundBrick = PlaySoundBrick()
        playSoundBrick.sound = bird.soundList[0]
        ifBrick.addBrickToIfBranch(playSoundBrick)
        playSoundBrick = PlaySoundBrick()
        playSoundBrick.sound = bird.soundList[1]
        ifBrick.addBrickToElseBranch(playSoundBrick)
        script.addBrick(ifBrick)
        bird.addScript(script)
        XstreamSerializer.getInstance().saveProject(project)
        return project
    }

    init {
        defaultProjectNameResourceId = R.string.default_cast_project_name
    }
}