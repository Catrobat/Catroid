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

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.images.WebImage
import java.io.Serializable
import java.util.ArrayList
import java.util.Date

class ScratchProgramData : Serializable, Parcelable {
    var id: Long
        private set
    var title: String?
        private set
    var owner: String?
        private set
    var image: WebImage?
    var instructions: String?
    var notesAndCredits: String?
    var views: Int
    var favorites: Int
    var loves: Int
    var createdDate: Date?
    var modifiedDate: Date?
    var sharedDate: Date?
    private var tags: MutableList<String>
    var visibilityState: ScratchVisibilityState?
    private var remixes: MutableList<ScratchProgramData> = ArrayList()

    constructor(id: Long, title: String?, owner: String?, image: WebImage?) {
        this.id = id
        this.title = title
        this.owner = owner
        this.image = image
        instructions = null
        notesAndCredits = null
        views = 0
        favorites = 0
        loves = 0
        createdDate = null
        modifiedDate = null
        sharedDate = null
        tags = ArrayList()
        visibilityState = null
        remixes = ArrayList()
    }

    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        title = `in`.readString()
        owner = `in`.readString()
        image = `in`.readParcelable(WebImage::class.java.classLoader)
        instructions = `in`.readString()
        notesAndCredits = `in`.readString()
        views = `in`.readInt()
        favorites = `in`.readInt()
        loves = `in`.readInt()
        val createdDateTime = `in`.readLong()
        createdDate = if (createdDateTime == 0L) null else Date(createdDateTime)
        val modifiedDateTime = `in`.readLong()
        modifiedDate = if (modifiedDateTime == 0L) null else Date(modifiedDateTime)
        val sharedDateTime = `in`.readLong()
        sharedDate = if (sharedDateTime == 0L) null else Date(sharedDateTime)
        tags = ArrayList()
        `in`.readStringList(tags)
        visibilityState = `in`.readParcelable(ScratchVisibilityState::class.java.classLoader)
        remixes = ArrayList()
        `in`.readTypedList(remixes as List<ScratchProgramData?>, CREATOR)
    }

    fun addRemixProgram(remixProgramData: ScratchProgramData) {
        remixes.add(remixProgramData)
    }

    fun getTags(): List<String> {
        return tags
    }

    fun addTag(tagName: String) {
        tags.add(tagName)
    }

    fun getRemixes(): List<ScratchProgramData> {
        return remixes
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(owner)
        dest.writeParcelable(image, flags)
        dest.writeString(instructions)
        dest.writeString(notesAndCredits)
        dest.writeInt(views)
        dest.writeInt(favorites)
        dest.writeInt(loves)
        dest.writeLong(if (createdDate != null) createdDate!!.time else 0)
        dest.writeLong(if (modifiedDate != null) modifiedDate!!.time else 0)
        dest.writeLong(if (sharedDate != null) sharedDate!!.time else 0)
        dest.writeStringList(tags)
        dest.writeParcelable(visibilityState, flags)
        dest.writeTypedList(remixes)
    }

    companion object {
        private const val serialVersionUID = 1L
        @JvmField val CREATOR: Parcelable.Creator<ScratchProgramData?> =
            object : Parcelable.Creator<ScratchProgramData?> {
                override fun createFromParcel(source: Parcel): ScratchProgramData? {
                    return ScratchProgramData(source)
                }

                override fun newArray(size: Int): Array<ScratchProgramData?> {
                    return arrayOfNulls(size)
                }
            }
    }
}