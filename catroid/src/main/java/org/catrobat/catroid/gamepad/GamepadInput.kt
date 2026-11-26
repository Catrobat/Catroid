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
 * boundary, when the stage calls [applyPendingInput] on the GL thread. That call applies the queued
 * changes in order. For each change it updates the sources that hold each button, publishes an
 * immutable snapshot for [isPressed], marks the change as applied for [isApplied], and reports a
 * button that changed from released to pressed to the [ButtonPressedListener].
 *
 * A button is pressed while any [GamepadInputSource] holds it. Holding a button, repeated presses
 * from the same source, presses from further sources, and releases report nothing. A press and a
 * release within the same frame still report the press once.
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
     * Applies the changes that were queued before this call, in order, and reports each button that
     * changes from released to pressed to [onPressed]. When [onPressed] is called, [isPressed]
     * already returns true for the reported button. Changes queued during this call, for example by
     * [onPressed], are applied by the next call.
     *
     * Call it only from a single thread: on the stage once per frame from the GL thread, in the
     * formula editor's Compute dialog from the main thread.
     */
    fun applyPendingInput(onPressed: ButtonPressedListener) {
        val frameBoundary = lastIssuedSequence.get()
        generateSequence { pollChangeUpTo(frameBoundary) }
            .forEach { change -> applyChange(change)?.let(onPressed::onButtonPressed) }
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

    private fun pollChangeUpTo(frameBoundary: Long): InputChange? {
        val nextChange = pendingChanges.peek() ?: return null
        return if (nextChange.sequence <= frameBoundary) pendingChanges.poll() else null
    }

    /** Applies [change] and returns its button if the change pressed a released button. */
    private fun applyChange(change: InputChange): GamepadButton? {
        val newlyPressedButton = when (change) {
            is ButtonStateChange -> applyButtonStateChange(change)
            is ReleaseAll -> {
                releaseAllSources()
                null
            }
        }
        publishPressedButtons()
        lastAppliedSequence = change.sequence
        return newlyPressedButton
    }

    private fun applyButtonStateChange(change: ButtonStateChange): GamepadButton? {
        val sources = heldSources.getValue(change.button)
        val wasPressed = sources.isNotEmpty()
        if (change.pressed) {
            sources.add(change.source)
        } else {
            sources.remove(change.source)
        }
        return change.button.takeIf { !wasPressed && sources.isNotEmpty() }
    }

    private fun releaseAllSources() {
        heldSources.values.forEach { it.clear() }
    }

    private fun publishPressedButtons() {
        pressedButtons = heldSources.filterValues { it.isNotEmpty() }.keys.toSet()
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
