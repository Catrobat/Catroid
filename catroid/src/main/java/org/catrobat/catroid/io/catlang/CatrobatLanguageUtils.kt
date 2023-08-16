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

package org.catrobat.catroid.io.catlang

class CatrobatLanguageUtils {
    companion object {
        fun getIndention (level: Int): String {
            return " ".repeat(level * 2);
        }

        fun formatSoundName (soundName: String): String {
            return "'$soundName'"
        }

        fun formatActorOrObject (actorOrObjectName: String): String {
            return "'$actorOrObjectName'"
        }

        fun formatLook (lookName: String): String {
            return "'$lookName'"
        }

        fun formatNFCTag (nfcTag: String): String {
            return "'$nfcTag'"
        }

        fun formatVariable (variableName: String): String {
            return "\"$variableName\""
        }

        fun formatList (listName: String): String {
            return "*$listName*"
        }

        fun formatString (string: String): String {
            return "'$string'"
        }

        fun formatHexColorString (hexColorString: String): String {
            val trimmedString = hexColorString.replace(Regex("^'"), "").replace(Regex("'$"), "").toLowerCase()
            if (trimmedString == "0") {
                return "000000"
            }

            if (Regex("^([a-f0-9]{6}|[a-f0-9]{3})$").matches(trimmedString)) {
                return "#$trimmedString"
            }

            return hexColorString
        }
    }
}