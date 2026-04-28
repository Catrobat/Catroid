/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.retrofit

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * Qualifier for Map fields that may arrive as [] (empty JSON array) instead of {}
 * from the Catroweb PHP API. PHP's json_encode returns [] for empty associative arrays
 * and {"key":"value"} for non-empty ones.
 */
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class FlexibleMap

class FlexibleMapAdapter : JsonAdapter<Map<String, String>?>() {

    override fun fromJson(reader: JsonReader): Map<String, String>? {
        return when (reader.peek()) {
            JsonReader.Token.BEGIN_ARRAY -> {
                reader.skipValue()
                emptyMap()
            }
            JsonReader.Token.BEGIN_OBJECT -> {
                val result = mutableMapOf<String, String>()
                reader.beginObject()
                while (reader.hasNext()) {
                    result[reader.nextName()] = reader.nextString()
                }
                reader.endObject()
                result
            }
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Map<String, String>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        for ((k, v) in value) {
            writer.name(k).value(v)
        }
        writer.endObject()
    }

    companion object {
        val FACTORY = object : Factory {
            override fun create(
                type: Type,
                annotations: Set<Annotation>,
                moshi: Moshi
            ): JsonAdapter<*>? {
                Types.nextAnnotations(annotations, FlexibleMap::class.java) ?: return null
                return FlexibleMapAdapter()
            }
        }
    }
}
