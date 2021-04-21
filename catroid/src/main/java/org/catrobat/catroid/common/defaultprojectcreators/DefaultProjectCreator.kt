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
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.ImageEditing
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

open class DefaultProjectCreator : ProjectCreator() {
    @Throws(IOException::class)
    override fun createDefaultProject(
        name: String?,
        context: Context?,
        landscapeMode: Boolean
    ): Project? {
        val project = Project(context, name, landscapeMode)
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
        val backgroundDrawableId: Int
        val cloudDrawableId: Int
        val screenshotDrawableId: Int
        if (landscapeMode) {
            backgroundDrawableId = R.drawable.default_project_background_landscape
            cloudDrawableId = R.drawable.default_project_clouds_landscape
            screenshotDrawableId = R.drawable.default_project_screenshot_landscape
        } else {
            backgroundDrawableId = R.drawable.default_project_background_portrait
            cloudDrawableId = R.drawable.default_project_clouds_portrait
            screenshotDrawableId = R.drawable.default_project_screenshot
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context?.resources, backgroundDrawableId, options)
        backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
            options.outWidth,
            options.outHeight,
            ScreenValues.screenWidth,
            ScreenValues.screenHeight
        )
        val scene = project.defaultScene
        val imageDir = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
        val soundDir = File(scene.directory, Constants.SOUND_DIRECTORY_NAME)
        val imageFileName = "img" + Constants.DEFAULT_IMAGE_EXTENSION
        val soundFileName = "snd" + Constants.DEFAULT_SOUND_EXTENSION
        val backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            backgroundDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val cloudFile1 = ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            cloudDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val cloudFile2 = ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            cloudDrawableId,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            R.drawable.default_project_bird_wing_up,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val birdWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            R.drawable.default_project_bird_wing_down,
            imageDir,
            imageFileName,
            backgroundImageScaleFactor
        )
        val tweetFile1 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            context?.resources,
            R.raw.default_project_tweet_1,
            soundDir,
            soundFileName
        )
        val tweetFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            context?.resources,
            R.raw.default_project_tweet_2,
            soundDir,
            soundFileName
        )
        ResourceImporter.createImageFileFromResourcesInDirectory(
            context?.resources,
            screenshotDrawableId,
            scene.directory,
            Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, 1.0
        )
        val background = scene.spriteList[0]
        background.lookList
            .add(
                LookData(
                    context?.getString(R.string.default_project_background_name),
                    backgroundFile
                )
            )
        val cloud1 = Sprite(context?.getString(R.string.default_project_cloud_sprite_name_1))
        val cloud2 = Sprite(context?.getString(R.string.default_project_cloud_sprite_name_2))
        val bird = Sprite(context?.getString(R.string.default_project_sprites_animal_name))
        scene.addSprite(cloud1)
        scene.addSprite(cloud2)
        scene.addSprite(bird)
        cloud1.lookList
            .add(LookData(context?.getString(R.string.default_project_cloud_name), cloudFile1))
        cloud2.lookList
            .add(LookData(context?.getString(R.string.default_project_cloud_name), cloudFile2))
        bird.lookList
            .add(
                LookData(
                    context?.getString(R.string.default_project_sprites_animal_wings_up),
                    birdWingUpFile
                )
            )
        bird.lookList
            .add(
                LookData(
                    context?.getString(R.string.default_project_sprites_animal_wings_down),
                    birdWingDownFile
                )
            )
        bird.soundList
            .add(SoundInfo(context?.getString(R.string.default_project_sprites_tweet_1),
                           tweetFile1))
        bird.soundList
            .add(SoundInfo(context?.getString(R.string.default_project_sprites_tweet_2),
                           tweetFile2))
        var script: Script = StartScript()
        script.addBrick(PlaceAtBrick(Formula(0), Formula(0)))
        script.addBrick(GlideToBrick(Formula(-ScreenValues.screenWidth), Formula(0), Formula(5)))
        script.addBrick(PlaceAtBrick(Formula(ScreenValues.screenWidth), Formula(0)))
        var loopBrick = ForeverBrick()
        loopBrick.addBrick(
            GlideToBrick(
                Formula(-ScreenValues.screenWidth),
                Formula(0),
                Formula(10)
            )
        )
        loopBrick.addBrick(PlaceAtBrick(Formula(ScreenValues.screenWidth), Formula(0)))
        script.addBrick(loopBrick)
        cloud1.addScript(script)
        script = StartScript()
        script.addBrick(PlaceAtBrick(Formula(ScreenValues.screenWidth), Formula(0)))
        loopBrick = ForeverBrick()
        loopBrick.addBrick(
            GlideToBrick(
                Formula(-ScreenValues.screenWidth),
                Formula(0),
                Formula(10)
            )
        )
        loopBrick.addBrick(PlaceAtBrick(Formula(ScreenValues.screenWidth), Formula(0)))
        script.addBrick(loopBrick)
        cloud2.addScript(script)
        script = StartScript()
        loopBrick = ForeverBrick()
        val randomElement1 = FormulaElement(
            FormulaElement.ElementType.FUNCTION,
            Functions.RAND.toString(), null
        )
        val randomElement1LeftChild = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.MINUS.toString(),
            randomElement1
        )
        randomElement1LeftChild.setRightChild(
            FormulaElement(
                FormulaElement.ElementType.NUMBER,
                "300",
                randomElement1LeftChild
            )
        )
        randomElement1.setLeftChild(randomElement1LeftChild)
        randomElement1.setRightChild(
            FormulaElement(
                FormulaElement.ElementType.NUMBER,
                "300",
                randomElement1
            )
        )
        val randomGlide1 = Formula(randomElement1)
        val randomElement2 = FormulaElement(
            FormulaElement.ElementType.FUNCTION,
            Functions.RAND.toString(),
            null
        )
        val randomElement2LeftChild = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.MINUS.toString(),
            randomElement2
        )
        randomElement2LeftChild.setRightChild(
            FormulaElement(
                FormulaElement.ElementType.NUMBER,
                "200",
                randomElement2LeftChild
            )
        )
        randomElement2.setLeftChild(randomElement2LeftChild)
        randomElement2.setRightChild(
            FormulaElement(
                FormulaElement.ElementType.NUMBER,
                "200",
                randomElement2
            )
        )
        val randomGlide2 = Formula(randomElement2)
        val glideToBrick = GlideToBrick(randomGlide1, randomGlide2, Formula(1))
        loopBrick.addBrick(glideToBrick)
        script.addBrick(loopBrick)
        bird.addScript(script)
        script = StartScript()
        loopBrick = ForeverBrick()
        loopBrick.addBrick(NextLookBrick())
        loopBrick.addBrick(WaitBrick(Formula(0.2)))
        script.addBrick(loopBrick)
        bird.addScript(script)
        script = WhenScript()
        val playSoundBrick = PlaySoundBrick()
        playSoundBrick.sound = bird.soundList[0]
        script.addBrick(playSoundBrick)
        bird.addScript(script)
        XstreamSerializer.getInstance().saveProject(project)
        return project
    }

    init {
        defaultProjectNameID = R.string.default_project_name
    }
}