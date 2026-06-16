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
import android.util.Log
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

    val n = oldFlat.size
    val m = newFlat.size
    val lcs = Array(n + 1) { IntArray(m + 1) }
    for (i in n - 1 downTo 0) {
        for (j in m - 1 downTo 0) {
            lcs[i][j] = if (signaturesOld[i] == signaturesNew[j]) {
                lcs[i + 1][j + 1] + 1
            } else {
                maxOf(lcs[i + 1][j], lcs[i][j + 1])
            }
        }
    }

    val aligned = mutableListOf<DiffRow>()
    var i = 0
    var j = 0
    while (i < n && j < m) {
        when {
            signaturesOld[i] == signaturesNew[j] -> {
                aligned.add(DiffRow(oldFlat[i], newFlat[j], DiffStatus.UNCHANGED)); i++; j++
            }

            lcs[i + 1][j] >= lcs[i][j + 1] -> {
                aligned.add(DiffRow(oldFlat[i], null, DiffStatus.REMOVED)); i++
            }

            else -> {
                aligned.add(DiffRow(null, newFlat[j], DiffStatus.ADDED)); j++
            }
        }
    }
    while (i < n) aligned.add(DiffRow(oldFlat[i++], null, DiffStatus.REMOVED))
    while (j < m) aligned.add(DiffRow(null, newFlat[j++], DiffStatus.ADDED))

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
        try {
            script.addToFlatList(list)
        } catch (e: Exception) {
            Log.e(
                DIFF_TAG,
                "Error flattening script ${script.javaClass.simpleName} in sprite ${sprite.name}: ${e.message}",
                e
            )
            // Defensive: a malformed script shouldn't crash the preview.
        }
    }
    return list
}

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
