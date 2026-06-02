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

package org.catrobat.catroid.ui.aiassist

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.io.XstreamSerializer

enum class DiffStatus { ADDED, REMOVED, MODIFIED, UNCHANGED }

private data class DiffRow(val old: Brick?, val new: Brick?, val status: DiffStatus)

object AiTutorDiffScreen {
    const val TAG = "AiTutorDiffScreen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorDiffScreen(
    currentSprite: Sprite,
    newSpriteXml: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val context = LocalContext.current

    val background = colorResource(R.color.app_background)
    val barColor = colorResource(R.color.toolbar_background)
    val white = colorResource(R.color.solid_white)
    val accent = colorResource(R.color.accent)
    val actionColor = colorResource(R.color.action_button)

    val newSprite = remember(newSpriteXml) {
        XstreamSerializer.getInstance().getSpriteFromXmlString(newSpriteXml)
    }
    val rows = remember(currentSprite, newSprite) {
        if (newSprite == null) emptyList() else buildDiffRows(currentSprite, newSprite, context)
    }

    val added = rows.count { it.status == DiffStatus.ADDED }
    val removed = rows.count { it.status == DiffStatus.REMOVED }
    val modified = rows.count { it.status == DiffStatus.MODIFIED }

    Scaffold(
        containerColor = background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Review AI changes", color = white, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = barColor,
                    titleContentColor = white
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(barColor)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = white)
                ) { Text("Reject") }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = actionColor,
                        contentColor = white
                    )
                ) { Text("Accept", fontWeight = FontWeight.Bold) }
            }
        }
    ) { padding ->
        if (newSprite == null) {
            CenteredMessage(
                text = "Could not read the AI's response as a sprite.",
                color = white,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else if (rows.all { it.status == DiffStatus.UNCHANGED }) {
            CenteredMessage(
                text = "The AI returned the same sprite,\nnothing would change.",
                color = white,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item { SummaryAndLegend(added, removed, modified, white, accent) }
                item { ColumnHeader(white) }
                itemsIndexed(rows) { _, row ->
                    if (isScriptHeaderRow(row)) {
                        ScriptHeaderRow(row, accent)
                    } else {
                        BrickDiffRow(row, context, white, accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String, color: Color, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun SummaryAndLegend(
    added: Int,
    removed: Int,
    modified: Int,
    white: Color,
    accent: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$added added · $removed removed · $modified modified",
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendChip("Added", colorResource(R.color.brick_color_green), white)
            LegendChip("Removed", colorResource(R.color.brick_color_red), white)
            LegendChip("Modified", colorResource(R.color.brick_color_yellow), white)
        }
    }
}

@Composable
private fun LegendChip(label: String, color: Color, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = textColor, fontSize = 12.sp)
    }
}

@Composable
private fun ColumnHeader(white: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Text(
            "Before",
            color = white.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "After",
            color = white.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ScriptHeaderRow(row: DiffRow, accent: Color) {
    val brick = row.new ?: row.old
    val title = brick?.let { humanizeBrickName(it.javaClass.simpleName) } ?: "Script"
    val tint = statusColor(row.status)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(colorResource(R.color.button_background))
            .then(
                if (tint != null) Modifier.border(
                    2.dp,
                    tint,
                    RoundedCornerShape(6.dp)
                ) else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(title, color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun BrickDiffRow(row: DiffRow, context: Context, white: Color, accent: Color) {
    val tint = statusColor(row.status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(tint?.copy(alpha = 0.28f) ?: Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrickCell(row.old, context, white, accent, Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        BrickCell(row.new, context, white, accent, Modifier.weight(1f))
    }
}

@Composable
private fun BrickCell(
    brick: Brick?,
    context: Context,
    white: Color,
    accent: Color,
    modifier: Modifier
) {
    if (brick == null) {
        Text("—", color = white.copy(alpha = 0.35f), modifier = modifier)
        return
    }
    Column(modifier = modifier) {
        Text(
            text = humanizeBrickName(brick.javaClass.simpleName),
            color = white,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        val subtitle = brickSubtitle(brick, context)
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = accent,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun statusColor(status: DiffStatus): Color? = when (status) {
    DiffStatus.ADDED -> colorResource(R.color.brick_color_green)
    DiffStatus.REMOVED -> colorResource(R.color.brick_color_red)
    DiffStatus.MODIFIED -> colorResource(R.color.brick_color_yellow)
    DiffStatus.UNCHANGED -> null
}

private fun isScriptHeaderRow(row: DiffRow): Boolean =
    (row.new ?: row.old) is ScriptBrick

/**
 * Flattens both sprites to a single ordered brick list each (script heads, nested bricks, else/end
 * markers — exactly what the editor shows), aligns them with an LCS so unchanged bricks line up on the
 * same row, then merges a removed-then-added pair of the same brick type into a single MODIFIED row.
 */
private fun buildDiffRows(oldSprite: Sprite, newSprite: Sprite, context: Context): List<DiffRow> {
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
                AiTutorDiffScreen.TAG,
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
    if (brick is FormulaBrick) {
        val map = brick.formulaMap
        for (field in map.keys.sortedBy { it.toString() }) {
            sb.append('|').append(field.toString()).append('=')
                .append(formulaText(brick, field, context))
        }
    }
    return sb.toString()
}

private fun brickSubtitle(brick: Brick, context: Context): String? {
    if (brick !is FormulaBrick) return null
    val map = brick.formulaMap
    val parts = map.keys.sortedBy { it.toString() }.mapNotNull { field ->
        val value = formulaText(brick, field, context)
        if (value.isBlank()) null else "${humanizeField(field.toString())}: $value"
    }
    return if (parts.isEmpty()) null else parts.joinToString(", ")
}

private fun formulaText(brick: FormulaBrick, field: Brick.FormulaField, context: Context): String =
    try {
        brick.formulaMap[field]?.getTrimmedFormulaString(context).orEmpty()
    } catch (e: Exception) {
        Log.e(
            AiTutorDiffScreen.TAG,
            "Error getting formula text for ${brick.javaClass.simpleName} field $field",
            e
        )
        "<error>"
    }

private fun humanizeBrickName(simpleName: String): String {
    val base = simpleName.removeSuffix("Brick")
    if (base.isEmpty()) return simpleName
    return base
        .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
        .trim()
}

private fun humanizeField(fieldName: String): String =
    fieldName.split('_').joinToString(" ") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }
