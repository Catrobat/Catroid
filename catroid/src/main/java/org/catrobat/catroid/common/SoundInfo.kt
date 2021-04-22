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
package org.catrobat.catroid.common

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import org.catrobat.catroid.io.StorageOperations
import java.io.File
import java.io.IOException
import java.io.Serializable

class SoundInfo : Cloneable, Nameable, Serializable {
    @XStreamAsAttribute
    private var name: String? = null

    @XStreamAsAttribute
    private var fileName: String? = null

    @Transient
    var file: File? = null
        private set
    var isMidiFile = false

    constructor() {}
    constructor(name: String?, file: File) {
        this.name = name
        this.file = file
        fileName = file.name
        isMidiFile = false
    }

    constructor(name: String?, file: File, midiFile: Boolean) {
        this.name = name
        this.file = file
        fileName = file.name
        isMidiFile = midiFile
    }

    override fun getName(): String {
        return name!!
    }

    override fun setName(name: String) {
        this.name = name
    }

    val xstreamFileName: String?
        get() {
            check(file == null) {
                ("This should be used only to deserialize the Object."
                    + " You should use @getFile() instead.")
            }
            return fileName
        }

    fun setFile(file: File) {
        this.file = file
        fileName = file.name
    }

    public override fun clone(): SoundInfo {
        return try {
            SoundInfo(name, StorageOperations.duplicateFile(file))
        } catch (e: IOException) {
            throw RuntimeException(TAG + ": Could not copy file: " + file!!.absolutePath)
        }
    }

    companion object {
        private const val serialVersionUID = 1L
        private val TAG = SoundInfo::class.java.simpleName
    }
}