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
package org.catrobat.catroid.ui.recyclerview.controller

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class LookController {
    private val uniqueNameProvider = UniqueNameProvider()

    @Throws(IOException::class)
    fun copy(lookToCopy: LookData?, dstScene: Scene?, dstSprite: Sprite?): LookData {
        val name =
            uniqueNameProvider.getUniqueNameInNameables(lookToCopy?.name, dstSprite?.lookList)
        val dstDir = dstScene?.let { getImageDir(it) }
        val file = StorageOperations.copyFileToDir(lookToCopy?.file, dstDir)
        return LookData(name, file)
    }

    @Throws(IOException::class)
    fun findOrCopy(lookToCopy: LookData?, dstScene: Scene?, dstSprite: Sprite?): LookData {
        val existingCopy =
            dstSprite?.lookList?.firstOrNull { it.file.checksumEquals(lookToCopy?.file) }
        if (existingCopy != null) return existingCopy

        val copiedLook = copy(lookToCopy, dstScene, dstSprite)
        dstSprite?.lookList?.add(copiedLook)
        return copiedLook
    }

    @Throws(IOException::class)
    fun delete(lookToDelete: LookData?) {
        if (lookToDelete?.file?.exists() == true) {
            StorageOperations.deleteFile(lookToDelete.file)
        }
    }

    @Throws(IOException::class)
    fun pack(lookToPack: LookData?): LookData {
        val name = uniqueNameProvider.getUniqueNameInNameables(
            lookToPack?.name, BackpackListManager.getInstance().backpackedLooks
        )
        val file = StorageOperations.copyFileToDir(
            lookToPack?.file,
            BackpackListManager.getInstance().backpackImageDirectory
        )
        return LookData(name, file)
    }

    @Throws(IOException::class)
    fun packForSprite(lookToPack: LookData?, dstSprite: Sprite?): LookData {
        val existingCopy =
            dstSprite?.lookList?.firstOrNull { it.file.checksumEquals(lookToPack?.file) }
        if (existingCopy != null) return existingCopy

        val file = StorageOperations.copyFileToDir(
            lookToPack?.file,
            BackpackListManager.getInstance().backpackImageDirectory
        )
        val look = LookData(lookToPack?.name, file)
        dstSprite?.lookList?.add(look)
        return look
    }

    @Throws(IOException::class)
    fun unpack(lookToUnpack: LookData?, dstScene: Scene?, dstSprite: Sprite?): LookData {
        val name =
            uniqueNameProvider.getUniqueNameInNameables(lookToUnpack?.name, dstSprite?.lookList)
        val file =
            StorageOperations.copyFileToDir(lookToUnpack?.file, dstScene?.let { getImageDir(it) })
        return LookData(name, file)
    }

    @Throws(IOException::class)
    fun unpackForSprite(lookToUnpack: LookData?, dstScene: Scene?, dstSprite: Sprite?): LookData {
        val existingCopy =
            dstSprite?.lookList?.firstOrNull { it.file.checksumEquals(lookToUnpack?.file) }
        if (existingCopy != null) return existingCopy

        val lookData = unpack(lookToUnpack, dstScene, dstSprite)
        dstSprite?.lookList?.add(lookData)
        return lookData
    }

    private fun getImageDir(scene: Scene?): File =
        File(scene?.directory, Constants.IMAGE_DIRECTORY_NAME)

    private fun File.checksumEquals(file: File?): Boolean {
        // The backpack uses this method and because the backpack does not correctly
        // de-serialize LookData references we can end up with a new LookData that has a null file reference
        // although we actually need one that already exists. so the workaround here (prevents a lot of crashes) is
        // to just hope that copying bricks with references to files always happens after the LookDatas are copied.
        if (file == null) {
            return true
        }
        val checksum1 = Utils.md5Checksum(this)
        val checksum2 = Utils.md5Checksum(file)
        return checksum1 == checksum2
    }
}
