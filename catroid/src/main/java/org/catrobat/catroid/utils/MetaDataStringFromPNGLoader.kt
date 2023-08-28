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
package org.catrobat.catroid.utils

import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.chunks.ChunkHelper
import ar.com.hjg.pngj.chunks.PngChunkTextVar
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.utils.MetaDataStringFromPNGLoader.MetaDataStringFromPNGParameter
import java.io.File

class MetaDataStringFromPNGLoader(resolver: FileHandleResolver?) :
    SynchronousAssetLoader<String, MetaDataStringFromPNGParameter>(resolver) {
    override fun load(
        am: AssetManager,
        fileName: String,
        file: FileHandle,
        param: MetaDataStringFromPNGParameter
    ): String {
        val image = File(param.absolutePath)
        val pngr = PngReader(image)
        pngr.readSkippingAllRows()
        var metaData = ""
        for (c in pngr.chunksList.chunks) {
            if (!ChunkHelper.isText(c)) {
                continue
            }
            val ct = c as PngChunkTextVar
            val k = ct.key
            val ctValue = ct.getVal()
            if (param.key == k) {
                metaData = ctValue
            }
        }
        pngr.close()
        return metaData
    }

    override fun resolve(fileName: String?): FileHandle = FileHandle(fileName)

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        param: MetaDataStringFromPNGParameter
    ): Array<AssetDescriptor<*>>? = null

    class MetaDataStringFromPNGParameter : AssetLoaderParameters<String?>() {
        @JvmField var key: String? = null
        @JvmField var absolutePath: String? = null
    }

    companion object {
        const val ID_POSTFIX = "MetaDataId"
    }
}
