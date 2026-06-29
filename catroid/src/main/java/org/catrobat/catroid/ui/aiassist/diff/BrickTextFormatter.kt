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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.UserDataBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickInterface

// ── Brick titles ──

/** Human-readable title from the class name, e.g. "SetXBrick" -> "Set X". */
internal fun humanizeBrickName(simpleName: String): String {
    val base = simpleName.removeSuffix("Brick")
    if (base.isEmpty()) return simpleName
    return base
        .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
        .trim()
}

/**
 * The editor label for a script-header brick (e.g. "When scene starts", "When tapped").
 * A brick's layout resource name matches its label string resource name, so we resolve the layout
 * via [BrickBaseType.getViewResource] and look up the same-named string. Falls back to the
 * humanized class name for anything unrecognized.
 */
internal fun scriptHeaderTitle(brick: Brick, context: Context): String = try {
    val layoutId = (brick as? BrickBaseType)?.getViewResource()
        ?: return humanizeBrickName(brick.javaClass.simpleName)
    val entryName = context.resources.getResourceEntryName(layoutId)
    val stringId = context.resources.getIdentifier(entryName, "string", context.packageName)
    if (stringId != 0) context.getString(stringId) else humanizeBrickName(brick.javaClass.simpleName)
} catch (e: Exception) {
    Log.e(
        DIFF_TAG,
        "Error resolving script header title for ${brick.javaClass.simpleName}",
        e
    )
    humanizeBrickName(brick.javaClass.simpleName)
}

// ── Brick value text ──

/** The brick's value chunks joined into one plain-text subtitle, or null when it has no values. */
internal fun brickSubtitle(brick: Brick, context: Context): String? {
    val tokens = brickValueTokens(brick, context, null)
    return if (tokens.isEmpty()) null else tokens.joinToString(", ") { it.text }
}

/**
 * The new brick's value subtitle as an [AnnotatedString], with each [DiffToken] that changed from
 * [oldBrick] rendered in bold (the highlighted token).
 */
internal fun changedSubtitleAnnotated(
    oldBrick: Brick?,
    newBrick: Brick,
    context: Context
): AnnotatedString = buildAnnotatedString {
    brickValueTokens(newBrick, context, oldBrick).forEachIndexed { index, token ->
        if (index > 0) append(", ")
        if (token.changed) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(token.text) }
        } else {
            append(token.text)
        }
    }
}

/**
 * The displayable value chunks for a brick: the selected variable/list name(s) and the formula
 * field values. When [oldBrick] is given, each token is flagged [DiffToken.changed] if it differs.
 * A plain "set/change a variable" brick (one data name + one formula field) is merged into a single
 * "<name>: <value>" token so the name shows instead of the misleading "Variable" field label.
 */
internal fun brickValueTokens(brick: Brick, context: Context, oldBrick: Brick?): List<DiffToken> {
    val names = dataNames(brick)
    val oldNames = oldBrick?.let { dataNames(it) } ?: emptyList()
    val oldFormula = oldBrick as? FormulaBrick
    val fields =
        (brick as? FormulaBrick)?.formulaMap?.keys?.sortedBy { it.toString() } ?: emptyList()

    if (names.size == 1 && fields.size == 1 && brick is FormulaBrick) {
        val field = fields.first()
        val value = formulaText(brick, field, context)
        val oldValue = oldFormula?.takeIf { it.formulaMap.containsKey(field) }
            ?.let { formulaText(it, field, context) }
        val changed =
            oldBrick != null && (names.first() != oldNames.firstOrNull() || value != oldValue)
        return listOf(DiffToken("${names.first()}: $value", changed))
    }

    val tokens = mutableListOf<DiffToken>()
    names.forEachIndexed { index, name ->
        tokens.add(DiffToken(name, oldBrick != null && oldNames.getOrNull(index) != name))
    }
    if (brick is FormulaBrick) {
        for (field in fields) {
            val value = formulaText(brick, field, context)
            if (value.isBlank()) continue
            val oldValue = oldFormula?.takeIf { it.formulaMap.containsKey(field) }
                ?.let { formulaText(it, field, context) }
            tokens.add(
                DiffToken(
                    "${humanizeField(field.toString())}: $value",
                    oldBrick != null && oldValue != value
                )
            )
        }
    }
    return tokens
}

// ── Helpers ──

/** Names of the variables/lists a brick selects (stored outside the formula map). */
internal fun dataNames(brick: Brick): List<String> {
    val names = mutableListOf<String>()
    (brick as? UserVariableBrickInterface)?.userVariable?.name?.let(names::add)
    (brick as? UserListBrick)?.userList?.name?.let(names::add)
    (brick as? UserDataBrick)?.userDataMap?.values?.forEach { it?.name?.let(names::add) }
    return names.filter { it.isNotBlank() }
}

/** The editor's trimmed formula string for [field], or "<error>" if it cannot be read. */
internal fun formulaText(brick: FormulaBrick, field: Brick.FormulaField, context: Context): String =
    try {
        brick.formulaMap[field]?.getTrimmedFormulaString(context).orEmpty()
    } catch (e: Exception) {
        Log.e(
            DIFF_TAG,
            "Error getting formula text for ${brick.javaClass.simpleName} field $field",
            e
        )
        "<error>"
    }

/** Turns a formula-field enum name into a readable label, e.g. "X_POSITION" -> "X Position". */
private fun humanizeField(fieldName: String): String =
    fieldName.split('_').joinToString(" ") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }
