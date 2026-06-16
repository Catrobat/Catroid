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

package org.catrobat.catroid.ui.aiassist.validation

import android.content.Context
import android.util.Log
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FormulaBrick

/**
 * Validates a sprite produced by the AI Tutor before it is applied to the project.
 *
 * Deserialization via [org.catrobat.catroid.io.XstreamSerializer.getSpriteFromXmlString] only proves
 * the XML *parses* — it does not prove the sprite is *renderable*. An AI can emit XML that deserializes
 * fine but is semantically incomplete (e.g. a brick missing a required formula), which then crashes when
 * the brick list renders. This validator reuses the exact code paths the UI runs so it catches precisely
 * those cases.
 *
 * Must be called on the main thread: the render dry-run inflates views via [Brick.getView].
 */
object AiTutorSpriteValidator {

    sealed class Result {
        object Valid : Result()
        data class Invalid(val reason: String) : Result()
    }

    const val TAG = "AiTutorSpriteValidator"

    @JvmStatic
    fun validate(sprite: Sprite, context: Context): Result {
        // 1. Reconstruct parent references exactly like ProjectManager.initializeScripts() does.
        try {
            for (script in sprite.scriptList) {
                script.setParents()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Error setting parent references for sprite ${sprite.name}: ${t.message}", t)
            return Result.Invalid("setParents failed: ${t.message}")
        }

        for (script in sprite.scriptList) {
            val flat = mutableListOf<Brick>()
            try {
                script.addToFlatList(flat)
            } catch (t: Throwable) {
                Log.e(
                    TAG,
                    "Error flattening script ${script.javaClass.simpleName} in sprite ${sprite.name}: ${t.message}",
                    t
                )
                return Result.Invalid("Malformed script structure: ${t.message}")
            }

            // 2. Explicit formula-completeness check — the dominant crash cause, with a specific
            // reason.
            // FormulaBrick.getView() iterates brickFieldToTextViewIdMap and looks each field up in
            // formulaMap; a field present in the former but missing from the latter throws at render time.
            for (brick in flat) {
                if (brick is FormulaBrick) {
                    for (field in brick.brickFieldToTextViewIdMap.keys) {
                        if (brick.formulaMap[field] == null) {
                            return Result.Invalid(
                                "${brick.javaClass.simpleName} is missing a required value ($field)"
                            )
                        }
                    }
                }
            }

            // 3. Render dry-run — catch-all safety net. Mirrors BrickAdapter: getView() on every
            // brick.
            for (brick in flat) {
                try {
                    brick.getView(context)
                } catch (t: Throwable) {
                    Log.e(
                        TAG,
                        "Error rendering brick ${brick.javaClass.simpleName} in sprite ${sprite.name}: ${t.message}",
                        t
                    )
                    return Result.Invalid("${brick.javaClass.simpleName} failed to render: ${t.message}")
                }
            }
        }

        return Result.Valid
    }
}
