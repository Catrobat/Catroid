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
package org.catrobat.catroid.content

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.content.BrightnessContrastHueShaderLoader.ShaderParameter

class BrightnessContrastHueShaderLoader(resolver: FileHandleResolver?) :
    SynchronousAssetLoader<BrightnessContrastHueShaderProgram, ShaderParameter>(resolver) {
    override fun load(
        am: AssetManager,
        fileName: String,
        file: FileHandle,
        param: ShaderParameter
    ): BrightnessContrastHueShaderProgram {
        val shader = BrightnessContrastHueShaderProgram()
        shader.setBrightness(param.brightness)
        shader.setHue(param.hue)
        return shader
    }

    override fun getDependencies(fileName: String, file: FileHandle, param: ShaderParameter): Array<AssetDescriptor<*>>? =
        null

    class ShaderParameter : AssetLoaderParameters<BrightnessContrastHueShaderProgram?>() {
        @JvmField
        var brightness: Float = 0f
        @JvmField
        var hue: Float = 0f
    }

    companion object {
        const val ID_POSTFIX = "ShaderLoaderId"
    }
}
