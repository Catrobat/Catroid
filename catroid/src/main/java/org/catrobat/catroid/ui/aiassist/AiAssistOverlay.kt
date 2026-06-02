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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import org.catrobat.aitutor.ui.public.AiTutorView
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer

private sealed class Stage {
    /** Showing the library's AiTutorView. [outputContext] carries a prior error to feed back to the AI. */
    data class Tutor(
        val outputContext: String?,
        val modifiedSpriteXml: String? = null
    ) : Stage()

    /** Showing the side-by-side diff for a valid sprite. */
    data class Diff(val xml: String) : Stage()

    /** Showing the validation-error dialog for an unrenderable sprite. */
    data class Error(val xml: String, val reason: String) : Stage()
}

/**
 * Hosts the full AI Assist flow as a single overlay: AiTutorView → (on paste) validate →
 * either the full-screen diff preview or a compact error dialog.
 */
@Composable
private fun AiAssistFlow(
    spriteXml: String?,
    currentSprite: Sprite,
    callbacks: AiAssistOverlayCallbacks
) {
    val context = LocalContext.current
    var stage by remember { mutableStateOf<Stage>(Stage.Tutor(null)) }

    val tutorStage = stage as? Stage.Tutor
    AiTutorView(
        show =  tutorStage != null,
        onDismissRequest = { if (stage is Stage.Tutor) callbacks.close() },
        codeContext = if (tutorStage?.modifiedSpriteXml != null) {
            "The AI previously suggested the following sprite, but it couldn't be applied: " +
                "\n\n${tutorStage.modifiedSpriteXml}\n\n" +
                "The original sprite before modification was:\n\n$spriteXml\n\n" +
                "Please fix the issues and return only a valid sprite XML."
        } else {
            spriteXml
        },
        outputContext = tutorStage?.outputContext,
        onClipboardPaste = { pastedText ->
            val result = try {
                val sprite = XstreamSerializer.getInstance().getSpriteFromXmlStringOrThrow(pastedText)
                if (sprite == null) {
                    AiTutorSpriteValidator.Result.Invalid("The pasted text is not a valid Pocket Code sprite.")
                } else {
                    AiTutorSpriteValidator.validate(sprite, context)
                }
            } catch (e: Exception) {
                AiTutorSpriteValidator.Result.Invalid(
                    "Couldn't read the sprite XML: ${e.message ?: e.javaClass.simpleName}"
                )
            }
            stage = if (result is AiTutorSpriteValidator.Result.Invalid) {
                Stage.Error(pastedText, result.reason)
            } else {
                Stage.Diff(pastedText)
            }
        }
    )

    when (val current = stage) {
        is Stage.Tutor -> Unit // already handled by AiTutorView's show parameter

        is Stage.Diff -> AiTutorDiffScreen(
            currentSprite = currentSprite,
            newSpriteXml = current.xml,
            onAccept = {
                callbacks.applySprite(current.xml)
                callbacks.close()
            },
            onReject = { callbacks.close() }
        )

        is Stage.Error -> AiTutorErrorDialog(
            technicalReason = current.reason,
            onBack = { callbacks.close() },
            onAskAgain = {
                // Re-open the tutor, feeding the validation error back to the AI as output context.
                stage = Stage.Tutor(
                    outputContext =
                        "The previous response could not be applied to Pocket Code. " +
                            "Please fix it and return only a valid sprite. Reason: ${
                                current
                                    .reason
                            }",
                    modifiedSpriteXml = current.xml
                )
            }
        )
    }
}

/** Java-facing bridge so `SpriteActivity` can drive the Compose overlay. */
object AiAssistOverlayHelper {
    @JvmStatic
    fun show(
        composeView: ComposeView,
        spriteXml: String?,
        currentSprite: Sprite,
        callbacks: AiAssistOverlayCallbacks
    ) {
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            AiAssistFlow(spriteXml, currentSprite, callbacks)
        }
    }
}
