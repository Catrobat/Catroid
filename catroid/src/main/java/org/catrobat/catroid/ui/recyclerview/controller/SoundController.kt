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
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class SoundController {
    private val uniqueNameProvider = UniqueNameProvider()
    @Throws(IOException::class)
    fun copy(soundToCopy: SoundInfo?, dstScene: Scene?, dstSprite: Sprite?): SoundInfo {
        val name =
            uniqueNameProvider.getUniqueNameInNameables(soundToCopy?.name, dstSprite?.soundList)
        val dstDir = getSoundDir(dstScene)
        val file = StorageOperations.copyFileToDir(soundToCopy?.file, dstDir)
        return SoundInfo(name, file)
    }

    @Throws(IOException::class)
    fun findOrCopy(soundToCopy: SoundInfo?, dstScene: Scene?, dstSprite: Sprite?): SoundInfo {
        dstSprite?.soundList?.forEach {
            if (compareByChecksum(it.file, soundToCopy?.file)) {
                return it
            }
        }
        val copiedSound = copy(soundToCopy, dstScene, dstSprite)
        dstSprite?.soundList?.add(copiedSound)
        return copiedSound
    }

    @Throws(IOException::class)
    fun delete(soundToDelete: SoundInfo?) {
        StorageOperations.deleteFile(soundToDelete?.file)
    }

    @Throws(IOException::class)
    fun pack(soundToPack: SoundInfo?): SoundInfo {
        val name = uniqueNameProvider.getUniqueNameInNameables(
            soundToPack?.name, BackpackListManager.getInstance().backpackedSounds
        )
        val file = StorageOperations.copyFileToDir(
            soundToPack?.file,
            BackpackListManager.getInstance().backpackSoundDirectory
        )
        return SoundInfo(name, file)
    }

    @Throws(IOException::class)
    fun packForSprite(soundToPack: SoundInfo?, dstSprite: Sprite?): SoundInfo {
        dstSprite?.soundList?.forEach {
            if (compareByChecksum(it.file, soundToPack?.file)) {
                return it
            }
        }
        val file = StorageOperations.copyFileToDir(
            soundToPack?.file,
            BackpackListManager.getInstance().backpackSoundDirectory
        )
        val sound = SoundInfo(soundToPack?.name, file)
        dstSprite?.soundList?.add(sound)
        return sound
    }

    @Throws(IOException::class)
    fun unpack(soundToUnpack: SoundInfo?, dstScene: Scene?, dstSprite: Sprite?): SoundInfo {
        val name =
            uniqueNameProvider.getUniqueNameInNameables(soundToUnpack?.name, dstSprite?.soundList)
        val file = StorageOperations.copyFileToDir(soundToUnpack?.file, getSoundDir(dstScene))
        return SoundInfo(name, file)
    }

    @Throws(IOException::class)
    fun unpackForSprite(soundToUnpack: SoundInfo?, dstScene: Scene?, dstSprite: Sprite?): SoundInfo {
        dstSprite?.soundList?.forEach {
            if (compareByChecksum(it.file, soundToUnpack?.file)) {
                return it
            }
        }
        val soundInfo = unpack(soundToUnpack, dstScene, dstSprite)
        dstSprite?.soundList?.add(soundInfo)
        return soundInfo
    }

    private fun getSoundDir(scene: Scene?): File = File(scene?.directory, Constants.SOUND_DIRECTORY_NAME)

    private fun compareByChecksum(file1: File?, file2: File?): Boolean {
        // The backpack uses this method and because the backpack does not correctly
        // de-serialize SoundInfo references we can end up with a new SoundInfo that has a null file reference
        // although we actually need one that already exists. so the workaround here (prevents a lot of crashes) is
        // to just hope that copying bricks with references to files always happens after the SoundInfos are copied.
        if (file1 == null || file2 == null) {
            return true
        }
        val checksum1 = Utils.md5Checksum(file1)
        val checksum2 = Utils.md5Checksum(file2)
        return checksum1 == checksum2
    }
}
