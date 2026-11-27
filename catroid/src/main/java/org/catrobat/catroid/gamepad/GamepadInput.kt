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

package org.catrobat.catroid.gamepad

import java.util.EnumSet
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * The platform-neutral button state of a single gamepad.
 *
 * Producers on any thread report changes with [setButtonState] and [releaseAll]. These calls only
 * queue the change and return its sequence number. Nothing becomes visible until the next frame
 * boundary, when the stage calls [applyPendingFrameInput] on the GL thread. That call applies the
 * queued changes in order. For each change it updates the sources that hold each button, publishes
 * an immutable snapshot for [isPressed], marks the change as applied for [isApplied], and reports a
 * button that changed from released to pressed to the [ButtonPressedListener].
 *
 * A button is pressed while any [GamepadInputSource] holds it. Holding a button, repeated presses
 * from the same source, presses from further sources, and releases report nothing. On the stage,
 * each button changes at most once per frame, so every press is reported and stays visible for at
 * least one frame, even if it was released before the frame.
 *
 * The analog sticks are no events, so their positions take effect at once: [setStickPosition] is
 * visible to [stickPosition] right away, and the latest position wins.
 */
class GamepadInput {

    /** Is told about each button that changes from released to pressed. */
    fun interface ButtonPressedListener {
        fun onButtonPressed(button: GamepadButton)
    }

    /** Keeps the queue in sequence order, so [isApplied] can compare against the last applied change. */
    private val issueLock = Any()
    private val lastIssuedSequence = AtomicLong(NO_SEQUENCE)
    private val pendingChanges = ConcurrentLinkedQueue<InputChange>()

    @Volatile
    private var lastAppliedSequence = NO_SEQUENCE

    /** Replaced as a whole after every applied change and never modified afterwards. */
    @Volatile
    private var pressedButtons: Set<GamepadButton> = emptySet()

    /** Replaced as a whole on every change; a stick without an entry is centered. */
    @Volatile
    private var stickPositions: Map<GamepadStick, GamepadStickPosition> = emptyMap()

    /** Only used by the thread that calls [applyPendingInput] and [reset]. */
    private val heldSources: Map<GamepadButton, MutableSet<GamepadInputSource>> =
        GamepadButton.entries.associateWith { EnumSet.noneOf(GamepadInputSource::class.java) }

    /**
     * Queues that [source] holds [button] down if [pressed] is true, or no longer holds it
     * otherwise. Thread-safe.
     *
     * @return the sequence number of the queued change, see [isApplied]
     */
    fun setButtonState(button: GamepadButton, source: GamepadInputSource, pressed: Boolean): Long =
        enqueue { sequence -> ButtonStateChange(sequence, button, source, pressed) }

    /**
     * Queues the release of every button from every source, for example when the stage loses focus
     * or the controller is removed. The release reports nothing. Thread-safe.
     *
     * @return the sequence number of the queued change, see [isApplied]
     */
    fun releaseAll(): Long {
        centerSticks()
        return enqueue { sequence -> ReleaseAll(sequence) }
    }

    /**
     * Sets the position of [stick] at once, each axis limited to the range of
     * [GamepadStickPosition]. Thread-safe.
     */
    fun setStickPosition(stick: GamepadStick, x: Double, y: Double) {
        synchronized(issueLock) {
            stickPositions = stickPositions + (stick to GamepadStickPosition.limited(x, y))
        }
    }

    /** Returns the latest position of [stick], centered if none was set. Thread-safe. */
    fun stickPosition(stick: GamepadStick): GamepadStickPosition =
        stickPositions[stick] ?: GamepadStickPosition.CENTER

    /**
     * Applies all changes that were queued before this call, in order, and reports each button that
     * changes from released to pressed to [onPressed]. When [onPressed] is called, [isPressed]
     * already returns true for the reported button. Changes queued during this call, for example by
     * [onPressed], are applied by the next call.
     *
     * Call it only from a single thread, for example in the formula editor's Compute dialog from the
     * main thread. The stage uses [applyPendingFrameInput] instead.
     */
    fun applyPendingInput(onPressed: ButtonPressedListener) =
        applyPendingChanges(onPressed) { true }

    /**
     * Applies the queued changes like [applyPendingInput] for one frame of the stage, where the
     * scripts must see every change:
     * - Each button changes at most once per frame. So each press starts the scripts of its button,
     *   and the sensors and the scripts see each press for at least one frame.
     * - Scripts started twice in one frame start only once. So only one press per frame starts the
     *   scripts for any button, which are the presses for which [startsAnyButtonScripts] returns
     *   true.
     *
     * A change that would break these rules waits for the next frame, and the changes after it wait
     * too, so the order stays the same.
     *
     * Call it only from the GL thread of the stage, once per frame.
     */
    fun applyPendingFrameInput(
        startsAnyButtonScripts: (GamepadButton) -> Boolean,
        onPressed: ButtonPressedListener
    ) {
        val frame = FrameChanges(startsAnyButtonScripts)
        applyPendingChanges(onPressed, frame::admit)
    }

    /** Returns whether [button] was pressed after the last applied change. Thread-safe. */
    fun isPressed(button: GamepadButton): Boolean = button in pressedButtons

    /**
     * Returns whether the change with the given [sequence] number has been applied or discarded by
     * [reset]. Thread-safe.
     */
    fun isApplied(sequence: Long): Boolean = sequence <= lastAppliedSequence

    /**
     * Discards all queued changes and releases every button immediately, without reporting
     * anything, for example when the stage restarts. Afterwards every sequence number issued so far
     * counts as applied.
     *
     * Call it only from the thread that calls [applyPendingInput].
     */
    fun reset() {
        synchronized(issueLock) {
            pendingChanges.clear()
            heldSources.values.forEach { it.clear() }
            pressedButtons = emptySet()
            lastAppliedSequence = lastIssuedSequence.get()
        }
        centerSticks()
    }

    private fun centerSticks() {
        synchronized(issueLock) {
            stickPositions = emptyMap()
        }
    }

    private fun enqueue(createChange: (sequence: Long) -> InputChange): Long =
        synchronized(issueLock) {
            val sequence = lastIssuedSequence.incrementAndGet()
            pendingChanges.add(createChange(sequence))
            sequence
        }

    /**
     * Applies the changes queued before this call in order, as long as [mayApplyNow] accepts the
     * next one.
     */
    private fun applyPendingChanges(
        onPressed: ButtonPressedListener,
        mayApplyNow: (InputChange) -> Boolean
    ) {
        val frameBoundary = lastIssuedSequence.get()
        while (true) {
            val change = peekChangeUpTo(frameBoundary) ?: return
            if (!mayApplyNow(change)) {
                return
            }
            val pressedButton = buttonPressedBy(change)
            pendingChanges.poll()
            applyChange(change)
            pressedButton?.let(onPressed::onButtonPressed)
        }
    }

    private fun peekChangeUpTo(frameBoundary: Long): InputChange? =
        pendingChanges.peek()?.takeIf { it.sequence <= frameBoundary }

    /** Returns the button of [change] if applying it would press a released button. */
    private fun buttonPressedBy(change: InputChange): GamepadButton? =
        (change as? ButtonStateChange)
            ?.takeIf { it.pressed && heldSources.getValue(it.button).isEmpty() }
            ?.button

    /** Returns the buttons that applying [change] would press or release. */
    private fun buttonsChangedBy(change: InputChange): Set<GamepadButton> =
        when (change) {
            is ButtonStateChange -> setOfNotNull(change.button.takeIf { changesPressedState(change) })
            is ReleaseAll -> pressedButtons
        }

    private fun changesPressedState(change: ButtonStateChange): Boolean {
        val sources = heldSources.getValue(change.button)
        return if (change.pressed) {
            sources.isEmpty()
        } else {
            sources.size == 1 && change.source in sources
        }
    }

    private fun applyChange(change: InputChange) {
        when (change) {
            is ButtonStateChange -> applyButtonStateChange(change)
            is ReleaseAll -> releaseAllSources()
        }
        publishPressedButtons()
        lastAppliedSequence = change.sequence
    }

    private fun applyButtonStateChange(change: ButtonStateChange) {
        val sources = heldSources.getValue(change.button)
        if (change.pressed) {
            sources.add(change.source)
        } else {
            sources.remove(change.source)
        }
    }

    private fun releaseAllSources() {
        heldSources.values.forEach { it.clear() }
    }

    private fun publishPressedButtons() {
        pressedButtons = heldSources.filterValues { it.isNotEmpty() }.keys.toSet()
    }

    /** The changes that one stage frame has applied so far, see [applyPendingFrameInput]. */
    private inner class FrameChanges(
        private val startsAnyButtonScripts: (GamepadButton) -> Boolean
    ) {
        private val changedButtons = EnumSet.noneOf(GamepadButton::class.java)
        private var anyButtonScriptsStarted = false

        /** Returns whether this frame may still apply [change], and if so, counts it as applied. */
        fun admit(change: InputChange): Boolean {
            val buttons = buttonsChangedBy(change)
            val startsAnyScripts = buttonPressedBy(change)?.let(startsAnyButtonScripts) == true
            if (buttons.any { it in changedButtons } || startsAnyScripts && anyButtonScriptsStarted) {
                return false
            }
            changedButtons.addAll(buttons)
            anyButtonScriptsStarted = anyButtonScriptsStarted || startsAnyScripts
            return true
        }
    }

    private sealed interface InputChange {
        val sequence: Long
    }

    private data class ButtonStateChange(
        override val sequence: Long,
        val button: GamepadButton,
        val source: GamepadInputSource,
        val pressed: Boolean
    ) : InputChange

    private data class ReleaseAll(override val sequence: Long) : InputChange

    private companion object {
        const val NO_SEQUENCE = 0L
    }
}
