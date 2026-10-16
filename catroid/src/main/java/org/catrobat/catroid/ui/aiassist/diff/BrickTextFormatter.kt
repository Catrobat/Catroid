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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.UserDataBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickInterface
import java.lang.reflect.Modifier

// ── Brick titles ──

/**
 * The editor label for a script-header brick (e.g. "When scene starts", "When tapped").
 * A brick's layout resource name matches its label string resource name, so we resolve the layout
 * via [BrickBaseType.getViewResource] and look up the same-named string. Falls back to the
 * humanized class name for anything unrecognized.
 */
internal fun scriptHeaderTitle(brick: Brick, context: Context): String = try {
    val layoutId = (brick as? BrickBaseType)?.getViewResource()
        ?: throw IllegalArgumentException("Brick ${brick.javaClass.simpleName} is not a BrickBaseType")
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

/**
 * The full editor phrase for a brick as ordered [DiffToken]s: the static label words plus, inline at
 * their real position, each formula field's value and each spinner selection's name. When [oldBrick] is
 * given, every value/name token is flagged [DiffToken.changed] if it differs (for old→new diffing) and
 * carries `dynamic = true` so the UI can style it like an input field.
 *
 * Static words (including runtime-only labels such as Wait's "seconds") come from the inflated
 * [Brick.getView]; values come from the model ([formulaText]) and selections from [dataNames], so they
 * stay correct even when the brick belongs to a parsed (non-current) sprite and even when the raw-layout
 * fallback is used because [Brick.getView] failed.
 *
 * In [inspectionMode] (a Compose `@Preview`/layoutlib render) no real view is inflated at all — the
 * preview renderer cannot load the themed font assets, so inflation crashes. There we derive the phrase
 * from the model only via [modelOnlyPhraseTokens]; the same model-only path is the deep fallback when
 * inflation fails on a real device.
 */
internal fun brickPhraseTokens(
    brick: Brick,
    context: Context,
    oldBrick: Brick?,
    inspectionMode: Boolean = false
): List<DiffToken> {
    if (inspectionMode) return modelOnlyPhraseTokens(brick, context, oldBrick)

    val formulaBrick = brick as? FormulaBrick
    val fieldIds = formulaBrick?.brickFieldToTextViewIdMap?.values?.toSet() ?: emptySet()
    val names = dataNames(brick)
    val oldNames = oldBrick?.let { dataNames(it) } ?: emptyList()
    val oldFormula = oldBrick as? FormulaBrick

    val root = try {
        brick.getView(context)
    } catch (e: Exception) {
        Log.e(DIFF_TAG, "Error inflating view for ${brick.javaClass.simpleName}", e)
        rawLayout(brick, context)
    } ?: return modelOnlyPhraseTokens(brick, context, oldBrick)

    val tokens = mutableListOf<DiffToken>()
    var spinnerIndex = 0
    fun walk(node: View) {
        if (node.visibility != View.VISIBLE) return
        when {
            // A spinner shows a selection; take its name from the model, never the (sprite-dependent) view.
            node is Spinner -> {
                addNameToken(tokens, names, oldNames, oldBrick, spinnerIndex)
                spinnerIndex++
            }

            node is TextView && node.id != View.NO_ID && node.id in fieldIds -> {
                val field = formulaBrick?.getBrickFieldFromTextViewId(node.id) ?: return
                val value = formulaText(formulaBrick, field, context).trim()
                if (value.isBlank()) return
                val oldValue = oldFormula?.takeIf { it.formulaMap.containsKey(field) }
                    ?.let { formulaText(it, field, context).trim() }
                tokens.add(DiffToken(value, oldBrick != null && value != oldValue, dynamic = true))
            }

            node is TextView -> node.text?.toString()?.trim()?.takeIf { it.isNotBlank() }
                ?.let { tokens.add(DiffToken(it, changed = false, dynamic = false)) }

            node is ViewGroup -> for (i in 0 until node.childCount) walk(node.getChildAt(i))
        }
    }
    walk(root)
    // Selections whose widget isn't a Spinner (rare) are never dropped: append them after the walk.
    while (spinnerIndex < names.size) {
        addNameToken(tokens, names, oldNames, oldBrick, spinnerIndex)
        spinnerIndex++
    }
    return tokens
}

/** Adds the [index]-th selected name (if any) as a dynamic token, flagged changed vs [oldBrick]. */
private fun addNameToken(
    tokens: MutableList<DiffToken>,
    names: List<String>,
    oldNames: List<String>,
    oldBrick: Brick?,
    index: Int
) {
    val name = names.getOrNull(index) ?: return
    tokens.add(
        DiffToken(
            name,
            oldBrick != null && oldNames.getOrNull(index) != name,
            dynamic = true
        )
    )
}

/**
 * The brick phrase derived from the model alone, never inflating a view: a humanized class-name label
 * followed by each formula field's value and each selected name. Used in Compose preview/inspection mode
 * (where view inflation crashes on missing font assets) and as the deep fallback when inflation fails.
 * Less precise than the inflated phrase (static words come from the class name, not the real layout), but
 * never crashes and stays correct for parsed sprites.
 */
private fun modelOnlyPhraseTokens(brick: Brick, context: Context, oldBrick: Brick?): List<DiffToken> {
    val tokens = mutableListOf(
        DiffToken(humanizeBrickName(brick.javaClass.simpleName), changed = false, dynamic = false)
    )
    val formulaBrick = brick as? FormulaBrick
    val oldFormula = oldBrick as? FormulaBrick
    formulaBrick?.formulaMap?.forEach { (field, _) ->
        val value = formulaText(formulaBrick, field, context).trim()
        if (value.isNotBlank()) {
            val oldValue = oldFormula?.takeIf { it.formulaMap.containsKey(field) }
                ?.let { formulaText(it, field, context).trim() }
            tokens.add(DiffToken(value, oldBrick != null && value != oldValue, dynamic = true))
        }
    }
    val names = dataNames(brick)
    val oldNames = oldBrick?.let { dataNames(it) } ?: emptyList()
    names.indices.forEach { addNameToken(tokens, names, oldNames, oldBrick, it) }
    return tokens
}

/** Inflates only the brick's layout (no [Brick.getView] population) as a fallback view source. */
private fun rawLayout(brick: Brick, context: Context): View? = try {
    (brick as? BrickBaseType)?.getViewResource()
        ?.let { LayoutInflater.from(context).inflate(it, null, false) }
} catch (e: Exception) {
    Log.e(DIFF_TAG, "Error inflating layout for ${brick.javaClass.simpleName}", e)
    null
}

/** The brick's editor phrase as a plain string, e.g. "Place at x: 0 y: 500". */
internal fun brickEditorLabel(brick: Brick, context: Context, inspectionMode: Boolean = false): String {
    val label = brickPhraseTokens(brick, context, null, inspectionMode)
        .joinToString(" ") { it.text }.trim()
    return label.ifBlank { humanizeBrickName(brick.javaClass.simpleName) }
}

/** Human-readable title from the class name, e.g. "SetXBrick" -> "Set X". */
internal fun humanizeBrickName(simpleName: String): String {
    val base = simpleName.removeSuffix("Brick")
    if (base.isEmpty()) return simpleName
    return base
        .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
        .trim()
}

// ── Helpers ──

/**
 * Names of the variables/lists/looks/sounds a brick selects (stored outside the formula map). The
 * explicit cases cover the variable/list/userdata bricks; the reflective sweep adds any other
 * [Nameable] selection (a look, sound, scene, instrument, …) held in a spinner-backed brick field, so
 * those bricks surface their value too. A [LinkedHashSet] de-duplicates the userVariable/userList
 * overlap between the two passes.
 */
internal fun dataNames(brick: Brick): List<String> {
    val names = LinkedHashSet<String>()
    (brick as? UserVariableBrickInterface)?.userVariable?.name?.let(names::add)
    (brick as? UserListBrick)?.userList?.name?.let(names::add)
    (brick as? UserDataBrick)?.userDataMap?.values?.forEach { it?.name?.let(names::add) }
    nameableFieldNames(brick).forEach(names::add)
    return names.filter { it.isNotBlank() }
}

/**
 * The names of every non-static instance field value that is a [Nameable], walking up the brick's
 * class hierarchy. Matched by type (not field name) so it is ProGuard-safe; per-field reads are
 * guarded so an inaccessible field never breaks the whole sweep.
 */
private fun nameableFieldNames(brick: Brick): List<String> {
    val names = mutableListOf<String>()
    var cls: Class<*>? = brick.javaClass
    while (cls != null && cls != Any::class.java) {
        for (field in cls.declaredFields) {
            if (Modifier.isStatic(field.modifiers)) continue
            try {
                field.isAccessible = true
                (field.get(brick) as? Nameable)?.name?.let(names::add)
            } catch (e: Exception) {
                Log.e(
                    DIFF_TAG,
                    "Error reading field ${field.name} of ${brick.javaClass.simpleName}",
                    e
                )
            }
        }
        cls = cls.superclass
    }
    return names
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
