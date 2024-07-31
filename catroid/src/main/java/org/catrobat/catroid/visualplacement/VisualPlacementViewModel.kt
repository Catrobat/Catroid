/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.visualplacement

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.defaultprojectcreators.BitmapWithRotationInfo
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickWithVisualPlacement
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH
import org.catrobat.catroid.utils.AndroidCoordinates
import org.catrobat.catroid.utils.GameCoordinates
import org.catrobat.catroid.utils.ShowTextUtils
import org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED
import org.catrobat.catroid.utils.ShowTextUtils.convertColorToString
import org.catrobat.catroid.utils.UnscaledGameCoordinatesForBrick
import org.catrobat.catroid.visualplacement.model.DrawableSprite
import org.catrobat.catroid.visualplacement.model.Size
import org.catrobat.catroid.visualplacement.model.TextConfiguration
import org.koin.java.KoinJavaComponent.inject

open class VisualPlacementViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectManager by inject(ProjectManager::class.java)
    private val layoutComputer by inject(LayoutComputer::class.java)
    private val drawingUtils by inject(VisualPlacementDrawingUtils::class.java)

    val drawableSprites = MutableLiveData<List<DrawableSprite>>()
    val spriteToPlace = MutableLiveData<DrawableSprite>()
    val layoutSize: Size = layoutComputer.getLayoutSize()
    private val layoutRatio: Size = layoutComputer.getLayoutRatio()

    var imageWasMoved = false

    fun drawAllSprites() {
        val backgroundSprites = ArrayList<DrawableSprite>()

        val initialCoordinates = savedStateHandle.getCoordinates().toGameCoordinates(layoutRatio)

        for (sprite in projectManager.currentlyEditedScene.spriteList) {
            if (sprite.lookList.isEmpty()) continue

            val currentBrickHash = savedStateHandle[EXTRA_BRICK_HASH] ?: 0

            backgroundSprites.addAll(drawAllBackgroundText(sprite, currentBrickHash))

            if (sprite === projectManager.currentSprite && !savedStateHandle.contains(EXTRA_TEXT)) {
                spriteToPlace.value = getDrawableSprite(sprite, initialCoordinates)
            } else {
                val coordinates = UnscaledGameCoordinatesForBrick(
                    sprite.initialPosition.x.toFloat(),
                    sprite.initialPosition.y.toFloat()
                ).toGameCoordinates(layoutRatio)
                backgroundSprites.add(getDrawableSprite(sprite, coordinates))
            }
        }

        if (savedStateHandle.contains(EXTRA_TEXT)) {
            val config = TextConfiguration(savedStateHandle)
            spriteToPlace.value = getDrawableText(config, initialCoordinates)
        }

        drawableSprites.value = backgroundSprites
    }

    private fun drawAllBackgroundText(sprite: Sprite, currentBrickHash: Int): List<DrawableSprite> {
        val result = ArrayList<DrawableSprite>()
        val textBricks = sprite.scriptList.filter { !it.isCommentedOut && it is StartScript }
            .map {
                it.brickList
                    .filterIsInstance<UserVariableBrickWithVisualPlacement>()
                    .filter { brick -> brick.hashCode() != currentBrickHash }
                    .toList()
            }.flatten()

        textBricks.forEach {
            val text = it.userVariable.value.toString()
            val config = if (it is ShowTextColorSizeAlignmentBrick) {
                TextConfiguration(
                    text, it.color, it.alignmentSelection, it.sanitizeTextSize()
                )
            } else {
                TextConfiguration(
                    text = text,
                    color = convertColorToString(Color.BLACK),
                    alignment = ALIGNMENT_STYLE_CENTERED,
                    relativeSize = 1f
                )
            }

            val coordinates = UnscaledGameCoordinatesForBrick(
                it.coordinates.left.toFloat(),
                it.coordinates.right.toFloat()
            ).toGameCoordinates(layoutRatio)

            result.add(getDrawableText(config, coordinates))
        }
        return result
    }

    @VisibleForTesting
    fun getDrawableText(config: TextConfiguration, coordinates: GameCoordinates): DrawableSprite {
        val bitmap = ShowTextUtils.convertTextToBitmap(config)
        val rotatedScaledBitmap =
            drawingUtils.rotateAndScaleBitmap(BitmapWithRotationInfo(bitmap), layoutRatio)
        val drawable = BitmapDrawable(Resources.getSystem(), rotatedScaledBitmap)

        return DrawableSprite(drawable, coordinates, Size(1f, 1f))
    }

    @VisibleForTesting
    fun getDrawableSprite(currentSprite: Sprite, coordinates: GameCoordinates): DrawableSprite {
        val spriteAsBitmap = currentSprite.spriteBitmap
        val scaledBitmap = drawingUtils.rotateAndScaleBitmap(spriteAsBitmap, layoutRatio)
        val drawableSprite = BitmapDrawable(Resources.getSystem(), scaledBitmap)

        val scalingFactor = Size(currentSprite.look.scaleX, currentSprite.look.scaleY)
        return DrawableSprite(
            drawableSprite, coordinates, scalingFactor
        )
    }

    fun setCoordinates(screenTapCoordinates: AndroidCoordinates, imageView: ImageView) {
        val valueToUpdate = spriteToPlace.value
        val gameCoordinates = screenTapCoordinates.toGameCoordinates(layoutSize)
        valueToUpdate!!.coordinates = gameCoordinates
        spriteToPlace.value = valueToUpdate
        imageView.x = gameCoordinates.x
        imageView.y = gameCoordinates.y
        imageWasMoved = true
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun save(brickHash: Int): Bundle {
        val extras = Bundle()
        extras.putInt(EXTRA_BRICK_HASH, brickHash)
        extras.putCoordinates(
            spriteToPlace.value!!.coordinates.toUnscaledGameCoordinates(layoutRatio)
        )
        extras.putBoolean(CHANGED_COORDINATES, imageWasMoved)
        return extras
    }

    private fun Bundle.putCoordinates(coordinates: UnscaledGameCoordinatesForBrick) {
        this.putInt(EXTRA_X_COORDINATE, coordinates.x.toInt())
        this.putInt(EXTRA_Y_COORDINATE, coordinates.y.toInt())
    }

    private fun SavedStateHandle.getCoordinates() =
        UnscaledGameCoordinatesForBrick(
            this.get<Int>(EXTRA_X_COORDINATE)?.toFloat() ?: 0F,
            this.get<Int>(EXTRA_Y_COORDINATE)?.toFloat() ?: 0F,
        )

    companion object {
        const val EXTRA_TEXT = "TEXT"
        const val EXTRA_TEXT_COLOR = "TEXT_COLOR"
        const val EXTRA_TEXT_SIZE = "TEXT_SIZE"
        const val EXTRA_TEXT_ALIGNMENT = "TEXT_ALIGNMENT"
        const val EXTRA_X_COORDINATE = "xCoordinate"
        const val EXTRA_Y_COORDINATE = "yCoordinate"
        const val CHANGED_COORDINATES = "changedCoordinates"
    }
}
