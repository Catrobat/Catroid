package org.catrobat.catroid.content.actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.TimeUtils
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import com.badlogic.gdx.graphics.Color as GdxColor

class FaceObjectSensorDataAction : Action() {
    private var scope: Scope? = null
    var questionFormula: Formula? = null
    var answerVariable: UserVariable? = null

    // UI actors/resources
    private var bubbleGroup: Group? = null
    private var labelMain: Label? = null
    private var labelShadow: Label? = null
    private val ownedTextures = mutableListOf<Texture>() // dispose on reset

    private var startMs = 0L
    private val timeoutMs = 5000L
    private var lastShownText: String = ""
    private var confettiBurstFired = false

    // ---------- Kid-friendly styling ----------
    private val FONT_SCALE = 7.5f
    private val TEXT_COLOR = GdxColor(0.10f, 0.16f, 0.24f, 1f)           // deep navy
    private val SHADOW_COLOR = GdxColor(0f, 0f, 0f, 0.35f)               // text shadow
    private val SHADOW_OFFSET = 6f

    // Soft pastels for bubble
    private val BUBBLE_FILL = GdxColor(1.00f, 0.96f, 0.88f, 0.98f)       // warm cream
    private val BUBBLE_BORDER = GdxColor(0.61f, 0.46f, 0.98f, 1f)        // lavender border
    private val BUBBLE_SHADOW = GdxColor(0f, 0f, 0f, 0.18f)              // soft drop shadow

    private val PAD_X = 48f
    private val PAD_Y = 32f
    private val BORDER_THICKNESS_PX = 6
    private val RADIUS_PX = 42
    private val TAIL_W_PX = 66
    private val TAIL_H_PX = 48
    private val SHADOW_OFFSET_PX = 10

    private val MAX_WIDTH_RATIO = 0.82f

    private val CONFETTI_COLORS = arrayOf(
        GdxColor(1.00f, 0.66f, 0.42f, 1f), // peach
        GdxColor(0.40f, 0.78f, 1.00f, 1f), // sky
        GdxColor(0.53f, 0.88f, 0.52f, 1f), // green
        GdxColor(0.89f, 0.69f, 1.00f, 1f), // purple
        GdxColor(1.00f, 0.84f, 0.55f, 1f)  // orange
    )

    // ---------- Lifecycle ----------
    override fun act(delta: Float): Boolean {
        val stage: Stage = actor?.stage ?: return true

        if (startMs == 0L) {
            startMs = TimeUtils.millis()
            confettiBurstFired = false
            buildBubble(stage, "Loading…")
            recenter(stage)

            // Playful entrance + gentle wiggle
            bubbleGroup?.setScale(0.9f)
            bubbleGroup?.addAction(
                Actions.sequence(
                    Actions.scaleTo(1.06f, 1.06f, 0.18f),
                    Actions.scaleTo(1.0f, 1.0f, 0.12f),
                    Actions.forever(
                        Actions.sequence(
                            Actions.rotateBy(1.5f, 0.8f),
                            Actions.rotateBy(-1.5f, 0.8f)
                        )
                    )
                )
            )
        }

        val txt = try { questionFormula?.interpretString(scope).orEmpty() } catch (_: Exception) { "" }
        val ready = txt.isNotBlank()
        val timedOut = TimeUtils.timeSinceMillis(startMs) >= timeoutMs
        val toShow = when {
            ready    -> txt
            timedOut -> "(empty)"
            else     -> "Loading…"
        }

        if (toShow != lastShownText) {
            buildBubble(stage, toShow)
            recenter(stage)
            lastShownText = toShow

            // Fire a one-time confetti burst when content first becomes ready
            if (ready && !confettiBurstFired) {
                spawnConfettiBurst(stage, bubbleGroup!!)
                confettiBurstFired = true
                // happy pulse
                bubbleGroup?.addAction(
                    Actions.sequence(
                        Actions.scaleTo(1.08f, 1.08f, 0.12f),
                        Actions.scaleTo(1.00f, 1.00f, 0.10f)
                    )
                )
            }
        } else {
            recenter(stage)
        }

        if (ready || timedOut) {
            try { answerVariable?.value = txt } catch (_: Throwable) {}
            try { answerVariable?.javaClass?.getMethod("setValue", Any::class.java)?.invoke(answerVariable, txt) } catch (_: Throwable) {}
            return true
        }
        return false
    }

    fun setScope(scope: Scope) { this.scope = scope }
    fun setFormula(formula: Formula) { this.questionFormula = formula }

    override fun reset() {
        bubbleGroup?.remove(); bubbleGroup = null
        labelMain = null; labelShadow = null
        ownedTextures.forEach { try { it.dispose() } catch (_: Throwable) {} }
        ownedTextures.clear()
        startMs = 0L
        lastShownText = ""
        confettiBurstFired = false
        questionFormula = null
        scope = null
    }

    // ---------- Build/Update Bubble ----------
    private fun buildBubble(stage: Stage, text: String) {
        bubbleGroup?.remove()

        val font = BitmapFont().apply { data.setScale(FONT_SCALE) }

        labelMain = Label(text, Label.LabelStyle(font, TEXT_COLOR)).apply {
            setAlignment(Align.center)
            setWrap(true)
            width = stage.viewport.worldWidth * MAX_WIDTH_RATIO
            height = prefHeight
        }
        labelShadow = Label(text, Label.LabelStyle(font, SHADOW_COLOR)).apply {
            setAlignment(Align.center)
            setWrap(true)
            width = labelMain!!.width
            height = labelMain!!.prefHeight
        }

        val bodyW = labelMain!!.width + PAD_X * 2f
        val bodyH = labelMain!!.height + PAD_Y * 2f
        val ppu = pxPerWorld(stage)

        // Bubble texture with soft shadow
        val bubbleTex = makeSpeechBubbleTextureWithShadow(
            (bodyW * ppu).toInt().coerceAtLeast(64),
            (bodyH * ppu).toInt().coerceAtLeast(64),
            RADIUS_PX, TAIL_W_PX, TAIL_H_PX,
            BUBBLE_FILL, BUBBLE_BORDER, BUBBLE_SHADOW,
            BORDER_THICKNESS_PX, SHADOW_OFFSET_PX
        )
        ownedTextures += bubbleTex

        val bubbleImg = Image(TextureRegionDrawable(TextureRegion(bubbleTex))).apply {
            val tailWorld = TAIL_H_PX / ppu
            setSize(bodyW, bodyH + tailWorld + (SHADOW_OFFSET_PX / ppu))
        }

        // Cute buddy sticker (smiley) overlapping corner
        val stickerTex = makeBuddyStickerTexture(120, 120)
        ownedTextures += stickerTex
        val sticker = Image(TextureRegionDrawable(TextureRegion(stickerTex))).apply {
            val s = 64f / ppu
            setSize(s, s)
            setOrigin(Align.center)
            // place near top-left of bubble
            setPosition(-s * 0.35f, bubbleImg.height - s * 0.72f)
            addAction(Actions.forever(Actions.sequence(
                Actions.rotateBy(6f, 0.6f),
                Actions.rotateBy(-6f, 0.6f)
            )))
        }

        // Build group
        bubbleGroup = Group().apply {
            setSize(bubbleImg.width, bubbleImg.height)
            addActor(bubbleImg)

            val tailWorld = TAIL_H_PX / ppu
            val stack = Stack().apply {
                setSize(labelMain!!.width, labelMain!!.height)
                setPosition(
                    PAD_X,
                    tailWorld + (bodyH - labelMain!!.height) / 2f
                )
                // label shadow slight offset
                val shadowContainer = Container(labelShadow).apply {
                    padTop(-SHADOW_OFFSET)
                    padLeft(SHADOW_OFFSET)
                    setSize(labelShadow!!.width, labelShadow!!.height)
                }
                addActor(shadowContainer)
                addActor(labelMain)
            }
            addActor(stack)
            addActor(sticker)

            // Gentle float animation on the whole group (already rotating lightly)
            addAction(Actions.forever(Actions.sequence(
                Actions.moveBy(0f, 6f / ppu, 0.9f),
                Actions.moveBy(0f, -6f / ppu, 0.9f)
            )))

            // perimeter confetti (static drifting)
            addConfettiAround(this, ppu)
        }

        stage.addActor(bubbleGroup)
        bubbleGroup!!.toFront()
    }

    private fun recenter(stage: Stage) {
        val g = bubbleGroup ?: return
        val center = Vector2(
            stage.viewport.screenWidth / 2f,
            stage.viewport.screenHeight / 2f
        )
        stage.screenToStageCoordinates(center)
        g.setPosition(center.x, center.y, Align.center)
    }

    // ---------- Confetti helpers ----------
    private fun addConfettiAround(group: Group, ppu: Float) {
        while (group.children.size > 3) group.children.removeIndex(group.children.size - 1)

        val w = group.width
        val h = group.height
        val rPx = 12
        val rWorld = rPx / ppu

        repeat(8) { i ->
            val color = CONFETTI_COLORS[i % CONFETTI_COLORS.size]
            val tex = if (i % 2 == 0) makeCircleTexture(rPx, color) else makeStarTexture(18, color)
            ownedTextures += tex
            val img = Image(TextureRegionDrawable(TextureRegion(tex))).apply {
                val size = if (i % 2 == 0) rWorld * 2 else rWorld * 2.2f
                setSize(size, size)
                val angle = MathUtils.random(0f, MathUtils.PI2)
                val rx = (w * 0.56f) * MathUtils.cos(angle)
                val ry = (h * 0.56f) * MathUtils.sin(angle)
                setPosition(w / 2f + rx - width / 2f, h / 2f + ry - height / 2f)
                color.a = 0.92f
                addAction(Actions.forever(Actions.sequence(
                    Actions.rotateBy(MathUtils.random(-45f, 45f), MathUtils.random(0.5f, 0.9f)),
                    Actions.rotateBy(MathUtils.random(-45f, 45f), MathUtils.random(0.5f, 0.9f))
                )))
            }
            group.addActor(img)
        }
    }

    private fun spawnConfettiBurst(stage: Stage, anchor: Group) {
        val ppu = pxPerWorld(stage)
        val burstGroup = Group()
        burstGroup.setSize(anchor.width, anchor.height)
        burstGroup.setPosition(anchor.x, anchor.y)
        stage.addActor(burstGroup)
        burstGroup.toFront()

        val count = 16
        repeat(count) {
            val color = CONFETTI_COLORS[it % CONFETTI_COLORS.size]
            val star = makeStarTexture(20, color)
            ownedTextures += star
            val img = Image(TextureRegionDrawable(TextureRegion(star))).apply {
                val s = 22f / ppu
                setSize(s, s)
                setPosition(anchor.width / 2f - s / 2f, anchor.height / 2f - s / 2f)
                rotation = MathUtils.random(0f, 360f)
                addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(
                            MathUtils.random(-120f, 120f) / ppu,
                            MathUtils.random(-80f, 140f) / ppu,
                            0.7f
                        ),
                        Actions.rotateBy(MathUtils.random(-540f, 540f), 0.7f),
                        Actions.fadeOut(0.7f)
                    ),
                    Actions.removeActor()
                ))
            }
            burstGroup.addActor(img)
        }
        // remove container after burst completes
        burstGroup.addAction(Actions.sequence(Actions.delay(0.8f), Actions.removeActor()))
    }

    // ---------- Drawing helpers ----------
    private fun pxPerWorld(stage: Stage): Float =
        Gdx.graphics.width / stage.viewport.worldWidth

    private fun makeCircleTexture(radiusPx: Int, color: GdxColor): Texture {
        val d = radiusPx * 2 + 2
        val pm = Pixmap(d, d, Pixmap.Format.RGBA8888)
        pm.setColor(0f, 0f, 0f, 0f); pm.fill()
        pm.setColor(color); pm.fillCircle(d / 2, d / 2, radiusPx)
        val tex = Texture(pm); pm.dispose()
        return tex
    }

    private fun makeStarTexture(sizePx: Int, color: GdxColor): Texture {
        val pm = Pixmap(sizePx, sizePx, Pixmap.Format.RGBA8888)
        pm.setColor(0f, 0f, 0f, 0f); pm.fill()
        pm.setColor(color)
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val rOuter = sizePx * 0.48f
        val rInner = sizePx * 0.20f
        val points = mutableListOf<Float>()
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) rOuter else rInner
            val a = MathUtils.PI / 2 + i * (MathUtils.PI2 / 10f)
            points += (cx + MathUtils.cos(a) * r)
            points += (cy + MathUtils.sin(a) * r)
        }
        // Fill star as triangle fan
        for (i in 1 until 9 step 1) {
            pm.fillTriangle(
                cx.toInt(), cy.toInt(),
                points[(i * 2).toInt()].toInt(), points[(i * 2) + 1].toInt(),
                points[((i + 1) * 2).toInt()].toInt(), points[((i + 1) * 2) + 1].toInt()
            )
        }
        val tex = Texture(pm); pm.dispose()
        return tex
    }

    private fun makeBuddyStickerTexture(w: Int, h: Int): Texture {
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        pm.setColor(0f, 0f, 0f, 0f); pm.fill()

        // face circle
        pm.setColor(1.00f, 0.91f, 0.60f, 1f) // friendly yellow
        pm.fillCircle(w / 2, h / 2, (w * 0.45f).toInt())

        // outline
        pm.setColor(0.95f, 0.72f, 0.20f, 1f)
        pm.drawCircle(w / 2, h / 2, (w * 0.45f).toInt())

        // eyes
        pm.setColor(0.12f, 0.18f, 0.28f, 1f)
        val ex = (w * 0.18f).toInt()
        val ey = (h * 0.08f).toInt()
        pm.fillCircle(w / 2 - ex, h / 2 + ey, (w * 0.05f).toInt())
        pm.fillCircle(w / 2 + ex, h / 2 + ey, (w * 0.05f).toInt())

        // smile
        pm.setColor(0.12f, 0.18f, 0.28f, 1f)
        val sx = (w * 0.22f).toInt()
        val sy = (h * 0.10f).toInt()
        for (x in -sx..sx) {
            val y = (MathUtils.sin((x / sx.toFloat()) * MathUtils.PI) * sy * 0.5f).toInt()
            pm.drawPixel(w / 2 + x, h / 2 - sy + y)
        }

        val tex = Texture(pm); pm.dispose()
        return tex
    }

    private fun makeSpeechBubbleTextureWithShadow(
        bodyWpx: Int,
        bodyHpx: Int,
        radiusPx: Int,
        tailWpx: Int,
        tailHpx: Int,
        fill: GdxColor,
        border: GdxColor,
        shadow: GdxColor,
        strokePx: Int,
        shadowOffsetPx: Int
    ): Texture {
        val totalH = bodyHpx + tailHpx + shadowOffsetPx
        val pm = Pixmap(bodyWpx + strokePx * 2 + shadowOffsetPx, totalH + strokePx * 2, Pixmap.Format.RGBA8888)
        pm.setColor(0f, 0f, 0f, 0f); pm.fill()

        fun fillRoundedRect(x: Int, y: Int, w: Int, h: Int, r: Int) {
            pm.fillRectangle(x + r, y, w - 2 * r, h)
            pm.fillRectangle(x, y + r, w, h - 2 * r)
            pm.fillCircle(x + r, y + r, r)
            pm.fillCircle(x + w - r - 1, y + r, r)
            pm.fillCircle(x + r, y + h - r - 1, r)
            pm.fillCircle(x + w - r - 1, y + h - r - 1, r)
        }

        // Shadow (draw first, under everything)
        pm.setColor(shadow)
        fillRoundedRect(
            strokePx / 2 + shadowOffsetPx,
            strokePx / 2 + tailHpx + shadowOffsetPx,
            bodyWpx + strokePx,
            bodyHpx + strokePx,
            radiusPx
        )
        val tailSX = bodyWpx - tailWpx - strokePx + shadowOffsetPx
        val tailSY = strokePx / 2 + shadowOffsetPx
        pm.fillTriangle(
            tailSX,              tailSY + tailHpx,
            tailSX + tailWpx,    tailSY + tailHpx,
            tailSX + tailWpx/2,  tailSY
        )

        // Border
        pm.setColor(border)
        fillRoundedRect(strokePx / 2, strokePx / 2 + tailHpx, bodyWpx + strokePx, bodyHpx + strokePx, radiusPx)
        val tailBX = bodyWpx - tailWpx - strokePx
        val tailBY = strokePx / 2
        pm.fillTriangle(
            tailBX,              tailBY + tailHpx,
            tailBX + tailWpx,    tailBY + tailHpx,
            tailBX + tailWpx/2,  tailBY
        )

        // Fill (inner)
        pm.setColor(fill)
        fillRoundedRect(strokePx, strokePx + tailHpx, bodyWpx, bodyHpx, Math.max(radiusPx - strokePx, 8))
        val tailFX = tailBX + strokePx
        val tailFY = tailBY + strokePx
        pm.fillTriangle(
            tailFX,                            tailFY + tailHpx - strokePx,
            tailFX + tailWpx - 2*strokePx,     tailFY + tailHpx - strokePx,
            tailFX + (tailWpx/2 - strokePx),   tailFY
        )

        val tex = Texture(pm)
        pm.dispose()
        return tex
    }
}
