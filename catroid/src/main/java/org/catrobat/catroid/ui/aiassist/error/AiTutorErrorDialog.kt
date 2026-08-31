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

package org.catrobat.catroid.ui.aiassist.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R

@Composable
fun AiTutorErrorDialog(
    technicalReason: String,
    onBack: () -> Unit,
    onAskAgain: () -> Unit
) {
    val white = colorResource(R.color.solid_white)
    val accent = colorResource(R.color.accent)
    val errorColor = colorResource(R.color.brick_color_red)
    val buttonBackgroundColor = colorResource(R.color.button_background)
    val actionButtonColor = colorResource(R.color.action_button)

    AlertDialog(
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onBack,
        containerColor = buttonBackgroundColor,
        titleContentColor = white,
        textContentColor = white,
        title = { Text("This change couldn't be applied", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "A brick in the AI's response is missing or malformed, so it can't be added to " +
                        "your project. You can ask the AI to fix it and paste the corrected sprite again."
                )
                if (BuildConfig.DEBUG) {
                    Spacer(Modifier.height(12.dp))
                    Text("Details: $technicalReason", color = errorColor)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAskAgain,
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionButtonColor,
                    contentColor = white
                )
            ) { Text("Ask AI again", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onBack) { Text("Back", color = accent) }
        }
    )
}