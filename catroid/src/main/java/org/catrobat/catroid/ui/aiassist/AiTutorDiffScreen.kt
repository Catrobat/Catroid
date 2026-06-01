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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorDiffScreen(
    currentSprite: Sprite,
    newSpriteXml: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val newSprite = remember(newSpriteXml) {
        XstreamSerializer.getInstance().getSpriteFromXmlString(newSpriteXml)
    }
    val diffs = remember(currentSprite, newSprite) {
        newSprite?.let { diffSprites(currentSprite, it) } ?: emptyList()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("AI Tutor Preview") }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Reject") }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) { Text("Accept") }
            }
        }
    ) { padding ->
        if (newSprite == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Could not parse AI response.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                diffs.forEach { scriptDiff ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = scriptDiff.oldScript?.javaClass?.simpleName ?: "(removed)",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = scriptDiff.newScript?.javaClass?.simpleName ?: "(added)",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Divider()
                    }
                    items(scriptDiff.brickDiffs) { brickDiff ->
                        val bgColor = when (brickDiff.status) {
                            DiffStatus.ADDED -> addedBg
                            DiffStatus.REMOVED -> removedBg
                            DiffStatus.MODIFIED -> modifiedBg
                            DiffStatus.UNCHANGED -> Color.Transparent
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = brickDiff.oldBrick?.javaClass?.simpleName ?: "",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = brickDiff.newBrick?.javaClass?.simpleName ?: "",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

object AiTutorPreviewHelper {
    @JvmStatic
    fun showPreview(
        composeView: ComposeView,
        currentSprite: Sprite,
        newSpriteXml: String,
        onAccept: Runnable,
        onReject: Runnable
    ) {
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            AiTutorDiffScreen(
                currentSprite = currentSprite,
                newSpriteXml = newSpriteXml,
                onAccept = { onAccept.run() },
                onReject = { onReject.run() }
            )
        }
    }
}

private val addedBg = Color(0xFF4CAF50).copy(alpha = 0.25f)
private val removedBg = Color(0xFFF44336).copy(alpha = 0.25f)
private val modifiedBg = Color(0xFFFF9800).copy(alpha = 0.25f)

private fun diffSprites(oldSprite: Sprite, newSprite: Sprite): List<ScriptDiff> {
    val oldScripts = oldSprite.scriptList
    val newScripts = newSprite.scriptList
    val maxLen = maxOf(oldScripts.size, newScripts.size)
    return (0 until maxLen).map { i ->
        val old = oldScripts.getOrNull(i)
        val new = newScripts.getOrNull(i)
        ScriptDiff(old, new, diffScripts(old, new))
    }
}

private fun diffScripts(oldScript: Script?, newScript: Script?): List<BrickDiff> {
    val oldBricks = oldScript?.brickList ?: emptyList()
    val newBricks = newScript?.brickList ?: emptyList()
    val maxLen = maxOf(oldBricks.size, newBricks.size)
    return (0 until maxLen).map { i ->
        val old = oldBricks.getOrNull(i)
        val new = newBricks.getOrNull(i)
        val status = when {
            old == null -> DiffStatus.ADDED
            new == null -> DiffStatus.REMOVED
            old.javaClass == new.javaClass -> DiffStatus.UNCHANGED
            else -> DiffStatus.MODIFIED
        }
        BrickDiff(old, new, status)
    }
}