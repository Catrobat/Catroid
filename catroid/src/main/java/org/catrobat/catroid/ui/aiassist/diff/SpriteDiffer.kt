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

package org.catrobat.catroid.ui.aiassist.diff

import android.content.Context
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ScriptBrick

internal fun isScriptHeaderRow(row: DiffRow): Boolean =
    (row.new ?: row.old) is ScriptBrick

/**
 * Flattens both sprites to a single ordered brick list each (script heads, nested bricks, else/end
 * markers — exactly what the editor shows), aligns them with an LCS so unchanged bricks line up on the
 * same row, then merges a removed-then-added pair of the same brick type into a single MODIFIED row.
 */
internal fun buildDiffRows(oldSprite: Sprite, newSprite: Sprite, context: Context): List<DiffRow> {
    val oldFlat = flatten(oldSprite)
    val newFlat = flatten(newSprite)
    val signaturesOld = oldFlat.map { brickSignature(it, context) }
    val signaturesNew = newFlat.map { brickSignature(it, context) }

    // lcsLengths[oldIndex][newIndex] = length of the longest common run of signatures starting at
    // those positions. Filled bottom-up so the forward walk below can read matches ahead of it.
    val oldCount = oldFlat.size
    val newCount = newFlat.size
    val lcsLengths = Array(oldCount + 1) { IntArray(newCount + 1) }
    for (oldIndex in oldCount - 1 downTo 0) {
        for (newIndex in newCount - 1 downTo 0) {
            lcsLengths[oldIndex][newIndex] =
                if (signaturesOld[oldIndex] == signaturesNew[newIndex]) {
                    lcsLengths[oldIndex + 1][newIndex + 1] + 1
                } else {
                    maxOf(lcsLengths[oldIndex + 1][newIndex], lcsLengths[oldIndex][newIndex + 1])
                }
        }
    }

    // Walk both lists in order: matching signatures pair up as UNCHANGED; otherwise advance the side
    // whose skip keeps the most matches, emitting REMOVED (old) or ADDED (new).
    val aligned = mutableListOf<DiffRow>()
    var oldIndex = 0
    var newIndex = 0
    while (oldIndex < oldCount && newIndex < newCount) {
        val droppingOldKeepsMoreMatches =
            lcsLengths[oldIndex + 1][newIndex] >= lcsLengths[oldIndex][newIndex + 1]
        when {
            signaturesOld[oldIndex] == signaturesNew[newIndex] -> {
                aligned.add(DiffRow(oldFlat[oldIndex], newFlat[newIndex], DiffStatus.UNCHANGED))
                oldIndex++
                newIndex++
            }

            droppingOldKeepsMoreMatches -> {
                aligned.add(DiffRow(oldFlat[oldIndex], null, DiffStatus.REMOVED))
                oldIndex++
            }

            else -> {
                aligned.add(DiffRow(null, newFlat[newIndex], DiffStatus.ADDED))
                newIndex++
            }
        }
    }
    // Leftover tail on either side: all removals or all additions.
    while (oldIndex < oldCount) aligned.add(DiffRow(oldFlat[oldIndex++], null, DiffStatus.REMOVED))
    while (newIndex < newCount) aligned.add(DiffRow(null, newFlat[newIndex++], DiffStatus.ADDED))

    return reconcileChangeBlocks(aligned)
}

/**
 * Pairs in-place edits as MODIFIED. The LCS emits all removes of a change region before all adds, so a
 * removed brick and its matching added brick (same class, changed value) are usually not adjacent. We
 * therefore reconcile per **change block** — a maximal run of consecutive non-UNCHANGED rows (UNCHANGED
 * rows are anchors that also keep script boundaries intact) — pairing each removed brick with the first
 * unused added brick of the same class.
 */
private fun reconcileChangeBlocks(rows: List<DiffRow>): List<DiffRow> {
    val out = mutableListOf<DiffRow>()
    var k = 0
    while (k < rows.size) {
        if (rows[k].status == DiffStatus.UNCHANGED) {
            out.add(rows[k])
            k++
            continue
        }
        val block = mutableListOf<DiffRow>()
        while (k < rows.size && rows[k].status != DiffStatus.UNCHANGED) {
            block.add(rows[k])
            k++
        }
        out.addAll(reconcileBlock(block))
    }
    return out
}

/**
 * Within one change block, pairs each removed brick with the first unused added brick of the same
 * class into a MODIFIED row (in original order); unmatched removes and the leftover adds stay as-is.
 */
private fun reconcileBlock(block: List<DiffRow>): List<DiffRow> {
    val removed = block.filter { it.status == DiffStatus.REMOVED }
    val added = block.filter { it.status == DiffStatus.ADDED }
    val usedAdded = BooleanArray(added.size)
    val result = mutableListOf<DiffRow>()

    // Removed bricks first (in original order): MODIFIED if a same-class added brick is still available.
    for (r in removed) {
        val matchIdx = added.indices.firstOrNull { a ->
            !usedAdded[a] && added[a].new?.javaClass == r.old?.javaClass
        }
        if (matchIdx != null) {
            usedAdded[matchIdx] = true
            result.add(DiffRow(r.old, added[matchIdx].new, DiffStatus.MODIFIED))
        } else {
            result.add(r)
        }
    }
    // Leftover purely-added bricks, in original order.
    for (a in added.indices) {
        if (!usedAdded[a]) {
            result.add(added[a])
        }
    }
    return result
}

private fun flatten(sprite: Sprite): List<Brick> {
    val list = mutableListOf<Brick>()
    for (script in sprite.scriptList) {
        script.addToFlatList(list)
    }
    return list
}

/**
 * A brick's equality key for the LCS: its class name plus the selected data names and each formula
 * field's text, so two bricks compare equal only when they would look identical in the editor.
 */
private fun brickSignature(brick: Brick, context: Context): String {
    val sb = StringBuilder(brick.javaClass.simpleName)
    // Include the selected variable/list names so changing only the data selection is a real change.
    for (name in dataNames(brick)) {
        sb.append("|data=").append(name)
    }
    if (brick is FormulaBrick) {
        val map = brick.formulaMap
        for (field in map.keys.sortedBy { it.toString() }) {
            sb.append('|').append(field.toString()).append('=')
                .append(formulaText(brick, field, context))
        }
    }
    return sb.toString()
}
