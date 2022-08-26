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
package org.catrobat.catroid.content.bricks.brickspinner

import com.thoughtworks.xstream.annotations.XStreamAlias
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.io.XStreamFieldKeyOrder
import java.io.Serializable

/**
 * note that the @property languageTag has IETF representation
 * */
@XStreamAlias("languageObject")
@XStreamFieldKeyOrder(
    "languageName",
    "languageTag"
)
data class LanguageObject(var languageName: String, var languageTag: String) :
    Nameable, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun getName(): String = languageName

    override fun setName(name: String) {
        languageName = name
    }
}
