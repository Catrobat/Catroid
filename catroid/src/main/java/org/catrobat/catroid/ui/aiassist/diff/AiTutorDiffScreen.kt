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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer

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

    var selected by remember { mutableStateOf<DiffRow?>(null) }

    val added = rows.count { it.status == DiffStatus.ADDED }
    val removed = rows.count { it.status == DiffStatus.REMOVED }
    val modified = rows.count { it.status == DiffStatus.MODIFIED }

    Scaffold(
        containerColor = background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Review AI changes",
                        color = white,
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { SummaryAndLegend(added, removed, modified, white, accent) }
                itemsIndexed(rows) { _, row ->
                    if (isScriptHeaderRow(row)) {
                        Spacer(Modifier.height(8.dp))
                        ScriptHeaderRow(row, context, accent)
                    } else {
                        BrickDiffRow(row, context, white, accent) { selected = row }
                    }
                }
            }
        }
    }

    selected?.let { row ->
        BrickDiffDialog(row, context, white, accent, actionColor) { selected = null }
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendChip(DiffStatus.ADDED, white)
            LegendChip(DiffStatus.REMOVED, white)
            LegendChip(DiffStatus.MODIFIED, white)
            LegendChip(DiffStatus.UNCHANGED, white)
        }
    }
}

@Composable
private fun LegendChip(status: DiffStatus, textColor: Color) {
    val color = statusColor(status)
    val iconRes = statusIcon(status)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .then(
                    if (color != null) {
                        Modifier.background(color)
                    } else {
                        Modifier.border(
                            1.dp,
                            colorResource(R.color.button_background),
                            RoundedCornerShape(4.dp)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null, // the label text already names the status
                    tint = textColor,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        Text(statusLabel(status), color = textColor, fontSize = 12.sp)
    }
}

@Composable
private fun ScriptHeaderRow(row: DiffRow, context: Context, accent: Color) {
    val brick = row.new ?: row.old
    val title = brick?.let { scriptHeaderTitle(it, context) } ?: "Script"
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            statusIcon(row.status)?.let { iconRes ->
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = statusLabel(row.status),
                    tint = tint ?: accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(title, color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BrickDiffRow(
    row: DiffRow,
    context: Context,
    white: Color,
    accent: Color,
    onClick: () -> Unit
) {
    when (row.status) {
        DiffStatus.UNCHANGED -> UnchangedDiffRow(row, context, white, accent, onClick)
        DiffStatus.MODIFIED -> statusColor(row.status)?.let {
            ModifiedDiffRow(row, context, it, white, accent, onClick)
        }
        // ADDED / REMOVED
        else -> statusColor(row.status)?.let {
            SingleDiffRow(row, context, it, white, accent, onClick)
        }
    }
}

/**
 * GitHub-style row: a 10% status tint over the page + a solid left stripe + a leading status icon,
 * shared by every changed row.
 */
@Composable
private fun StatusContainer(
    tint: Color,
    iconRes: Int?,
    iconDesc: String?,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .background(tint.copy(alpha = ROW_TINT_ALPHA)) // 10% status tint, GitHub diff style
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Solid status stripe, flush to the rounded-left edge.
        Box(
            modifier = Modifier
                .width(STRIPE_WIDTH)
                .fillMaxHeight()
                .background(tint)
        )
        Spacer(Modifier.width(8.dp))
        if (iconRes != null) {
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = iconDesc,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
        }
        content()
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun SingleDiffRow(
    row: DiffRow,
    context: Context,
    tint: Color,
    white: Color,
    accent: Color,
    onClick: () -> Unit
) {
    val brick = row.new ?: row.old ?: return
    StatusContainer(tint, statusIcon(row.status), statusLabel(row.status), onClick) {
        BrickContent(brick, context, white, accent, Modifier.weight(1f))
    }
}

@Composable
private fun ModifiedDiffRow(
    row: DiffRow,
    context: Context,
    tint: Color,
    white: Color,
    accent: Color,
    onClick: () -> Unit
) {
    val newBrick = row.new ?: return
    StatusContainer(tint, statusIcon(row.status), statusLabel(row.status), onClick) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = humanizeBrickName(newBrick.javaClass.simpleName),
                color = white,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            val newSubtitle = brickSubtitle(newBrick, context)
            if (newSubtitle != null) {
                ModifiedValues(
                    oldText = row.old?.let { brickSubtitle(it, context) }.orEmpty(),
                    newText = changedSubtitleAnnotated(row.old, newBrick, context),
                    oldColor = white.copy(alpha = 0.45f), // faded old value
                    newColor = accent, // crisp new value, changed token bold
                    arrowColor = white.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ModifiedValues(
    oldText: String,
    newText: AnnotatedString,
    oldColor: Color,
    newColor: Color,
    arrowColor: Color
) {
    val measurer = rememberTextMeasurer()
    val style = TextStyle(fontSize = 12.sp)
    val density = LocalDensity.current
    val arrowSpace = with(density) { (14.dp + 16.dp).roundToPx() } // icon + 8.dp padding each side

    val oldWidth =
        remember(oldText) { measurer.measure(AnnotatedString(oldText), style).size.width }
    val newWidth = remember(newText) { measurer.measure(newText, style).size.width }

    var rowWidth by remember { mutableIntStateOf(0) }
    val half = (rowWidth - arrowSpace) / 2
    val sideBySide = rowWidth == 0 || (oldWidth <= half && newWidth <= half)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { rowWidth = it.width }) {
        if (sideBySide) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = oldText,
                    color = oldColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward_vector),
                    contentDescription = null,
                    tint = arrowColor,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(14.dp)
                )
                Text(
                    text = newText,
                    color = newColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = oldText,
                    color = oldColor,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward_vector),
                    contentDescription = null,
                    tint = arrowColor,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(14.dp)
                        .rotate(90f) // → becomes ↓ when stacked
                )
                Text(
                    text = newText,
                    color = newColor,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun UnchangedDiffRow(
    row: DiffRow,
    context: Context,
    white: Color,
    accent: Color,
    onClick: () -> Unit
) {
    val brick = row.new ?: row.old ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, colorResource(R.color.button_background), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = CONTENT_INDENT, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrickContent(brick, context, white, accent, Modifier.weight(1f))
    }
}

@Composable
private fun BrickContent(
    brick: Brick,
    context: Context,
    labelColor: Color,
    valueColor: Color,
    modifier: Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = humanizeBrickName(brick.javaClass.simpleName),
            color = labelColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        val subtitle = brickSubtitle(brick, context)
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = valueColor,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun spriteOf(name: String, vararg scripts: Script): Sprite = Sprite(name).apply {
    scripts.forEach { addScript(it) }
}

private fun startScript(vararg bricks: Brick): Script =
    StartScript().apply { bricks.forEach { addBrick(it) } }

private fun whenScript(vararg bricks: Brick): Script =
    WhenScript().apply { bricks.forEach { addBrick(it) } }

@Preview(name = "Changes", showBackground = true)
@Composable
private fun AiTutorDiffScreenPreview() {
    val longOld = Formula("playerStartingHorizontalPositionBeforeOffset")
    val longNew = Formula("playerComputedHorizontalPositionAfterApplyingOffsetAndClamp")
    val current = spriteOf(
        "current",
        // SetX 0→100 (short modified), Wait unchanged, SetY 10→200 (short modified), ChangeX removed.
        startScript(SetXBrick(0), WaitBrick(1000), SetYBrick(10), ChangeXByNBrick(10)),
        // SetX long→long (long modified, stacks), Hide unchanged, SetY 50 added.
        whenScript(SetXBrick(longOld), HideBrick())
    )
    val proposed = spriteOf(
        "proposed",
        startScript(SetXBrick(100), WaitBrick(1000), SetYBrick(200)),
        whenScript(SetXBrick(longNew), HideBrick(), SetYBrick(50))
    )
    AiTutorDiffScreen(
        currentSprite = current,
        newSpriteXml = XstreamSerializer.getInstance().getXmlAsStringFromSprite(proposed),
        onAccept = {},
        onReject = {}
    )
}

@Preview(name = "No changes", showBackground = true)
@Composable
private fun AiTutorDiffScreenNoChangesPreview() {
    val sprite = spriteOf("sprite", startScript(SetXBrick(100), WaitBrick(1000)))
    AiTutorDiffScreen(
        currentSprite = sprite,
        newSpriteXml = XstreamSerializer.getInstance().getXmlAsStringFromSprite(sprite),
        onAccept = {},
        onReject = {}
    )
}
