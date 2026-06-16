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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick

@Composable
internal fun BrickDiffDialog(
    row: DiffRow,
    context: Context,
    white: Color,
    accent: Color,
    actionColor: Color,
    onDismiss: () -> Unit
) {
    val brick = row.new ?: row.old ?: return
    val tint = statusColor(row.status)
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.button_background)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)) {
                Text(
                    text = humanizeBrickName(brick.javaClass.simpleName),
                    color = white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = statusLabel(row.status),
                    color = tint ?: accent,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))

                DialogSection(
                    "Before",
                    row.old,
                    null,
                    "Not in the original",
                    context,
                    white,
                    accent
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward_vector),
                        contentDescription = null,
                        tint = white.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(90f)
                    )
                }
                DialogSection("After", row.new, row.old, "Removed", context, white, accent)

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = actionColor,
                        contentColor = white
                    )
                ) { Text("Close", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun DialogSection(
    label: String,
    brick: Brick?,
    oldForDiff: Brick?,
    emptyText: String,
    context: Context,
    white: Color,
    accent: Color
) {
    Text(label, color = white.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.app_background))
            .padding(12.dp)
    ) {
        if (brick == null) {
            Text(emptyText, color = white.copy(alpha = 0.5f), fontSize = 14.sp)
        } else {
            Text(
                text = humanizeBrickName(brick.javaClass.simpleName),
                color = white,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            for (token in brickValueTokens(brick, context, oldForDiff)) {
                Text(
                    text = token.text,
                    color = accent,
                    fontWeight = if (token.changed) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
