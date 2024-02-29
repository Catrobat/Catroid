/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.ui.recyclerview.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatImageView
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.ComeToFrontBrick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteLookBrick
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick
import org.catrobat.catroid.content.bricks.DroneFlipBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.EditLookBrick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.EndBrick
import org.catrobat.catroid.content.bricks.ExitStageBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick
import org.catrobat.catroid.content.bricks.ListSelectorBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick
import org.catrobat.catroid.content.bricks.PauseForBeatsBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.SetTextBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.ui.BrickLayout
import org.catrobat.catroid.ui.dragndrop.BrickAdapterInterface
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.ViewStateManager
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import java.util.ArrayList
import java.util.Collections
import kotlin.reflect.KClass

class BrickAdapter(private val sprite: Sprite) :
    BaseAdapter(),
    BrickAdapterInterface,
    AdapterView.OnItemClickListener,
    OnItemLongClickListener {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, ALL, SCRIPTS_ONLY, CONNECTED_ONLY)
    internal annotation class CheckBoxMode

    @CheckBoxMode
    private var checkBoxMode = NONE

    private var scripts: MutableList<Script> = ArrayList()
    private var firstConnectedItem = -1
    private var lastConnectedItem = -1

    private val selectionManager = MultiSelectionManager()
    private val viewStateManager = ViewStateManager()

    private var onItemClickListener: OnBrickClickListener? = null
    private var selectionListener: SelectionListener? = null

    val items: MutableList<Brick> = ArrayList()

    init {
        updateItems(sprite)
    }

    companion object {
        const val DISABLED_BRICK_ALPHA = .8f
        const val NONE = 0
        const val ALL = 1
        const val SCRIPTS_ONLY = 2
        const val CONNECTED_ONLY = 3

        @JvmStatic
        fun colorAsCommentedOut(background: Drawable) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            background.mutate()
            background.colorFilter = filter
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnBrickClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectionListener(selectionListener: SelectionListener?) {
        this.selectionListener = selectionListener
    }

    fun setCheckBoxMode(checkBoxMode: Int) {
        this.checkBoxMode = checkBoxMode
        notifyDataSetChanged()
    }

    fun updateItems(sprite: Sprite?) {
        sprite?.scriptList?.let { scripts = it }
        updateItemsFromCurrentScripts()
    }

    private fun updateItemsFromCurrentScripts() {
        items.clear()
        sprite.removeAllEmptyScriptBricks()
        for (script in scripts) {
            script.setParents()
            script.addToFlatList(items)
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]
        val itemView = if (item.isCollapsed && checkBoxMode == NONE) {
            getCollapsedItemView(item, parent)
        } else {
            item.getView(parent.context)
        }

        itemView.visibility =
            if (viewStateManager.isVisible(position)) View.VISIBLE else View.INVISIBLE
        itemView.alpha = if (viewStateManager.isEnabled(position)) 1F else DISABLED_BRICK_ALPHA

        val background = getBackground(item, itemView)
        if (item.isCommentedOut || item is EmptyEventBrick) {
            colorAsCommentedOut(background)
        } else {
            background.clearColorFilter()
        }

        checkBoxClickListener(item, (itemView as ViewGroup), position)
        item.checkBox.isChecked = selectionManager.isPositionSelected(position)
        item.checkBox.isEnabled = viewStateManager.isEnabled(position)
        return itemView
    }

    private fun getBackground(item: Brick, itemView: View): Drawable {
        if (item.isCollapsed) {
            return (itemView as ViewGroup).getChildAt(0).background
        }
        if (item is UserDefinedReceiverBrick) {
            return ((itemView as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(0).background
        }
        return (itemView as ViewGroup).getChildAt(1).background
    }

    fun isInstanceOfAny(obj: Any, classes: List<KClass<out Any>>): Boolean {
        return classes.any { it.isInstance(obj) }
    }

    private fun getCollapsedItemView(item: Brick, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.collapsed_control_brick, null)
        val eventBrickList = listOf(
            WhenStartedBrick::class, WhenBrick::class,
            WhenTouchDownBrick::class, BroadcastReceiverBrick::class,
            WhenConditionBrick::class, WhenBackgroundChangesBrick::class,
            WhenClonedBrick::class
        )

        val controlBrickList = listOf(
            CompositeBrick::class, WaitBrick::class,
            WaitUntilBrick::class, SceneStartBrick::class,
            CloneBrick::class, BroadcastBrick::class,
            BroadcastWaitBrick::class, SceneTransitionBrick::class,
            StopScriptBrick::class, ExitStageBrick::class,
            DeleteThisCloneBrick::class, EndBrick::class,
            WaitTillIdleBrick::class
        )

        val lookBrickList = listOf(
            ParticleEffectAdditivityBrick::class, AskBrick::class,
            BackgroundRequestBrick::class,
            ChangeBrightnessByNBrick::class, ChangeColorByNBrick::class,
            ChangeSizeByNBrick::class, ChangeTransparencyByNBrick::class,
            ChooseCameraBrick::class, ClearGraphicEffectBrick::class,
            CopyLookBrick::class, DeleteLookBrick::class,
            DroneSwitchCameraBrick::class, EditLookBrick::class,
            FadeParticleEffectBrick::class, FlashBrick::class,
            HideBrick::class, LookRequestBrick::class,
            NextLookBrick::class, PaintNewLookBrick::class,
            PhiroRGBLightBrick::class, PreviousLookBrick::class,
            SayBubbleBrick::class, SayForBubbleBrick::class,
            SetBackgroundBrick::class, SetBackgroundByIndexBrick::class,
            SetBackgroundByIndexAndWaitBrick::class,
            SetBrightnessBrick::class, SetCameraFocusPointBrick::class,
            SetColorBrick::class, SetLookBrick::class,
            SetLookByIndexBrick::class, SetParticleColorBrick::class,
            SetSizeToBrick::class, SetTransparencyBrick::class,
            ShowBrick::class, ThinkBubbleBrick::class,
            ThinkForBubbleBrick::class, CameraBrick::class
        )

        val soundBrickList = listOf(
            AskSpeechBrick::class, ChangeTempoByNBrick::class,
            ChangeVolumeByNBrick::class, JumpingSumoNoSoundBrick::class,
            JumpingSumoSoundBrick::class, PauseForBeatsBrick::class,
            PhiroPlayToneBrick::class, PlayDrumForBeatsBrick::class,
            PlayNoteForBeatsBrick::class, PlaySoundBrick::class,
            PlaySoundAtBrick::class, SetInstrumentBrick::class,
            SetListeningLanguageBrick::class, SetTempoBrick::class,
            SetVolumeToBrick::class, SpeakBrick::class,
            SpeakAndWaitBrick::class, StartListeningBrick::class,
            StopAllSoundsBrick::class, StopSoundBrick::class
        )

        val motionBrickList =
            listOf(
                ChangeXByNBrick::class, ChangeYByNBrick::class, DroneEmergencyBrick::class,
                DroneFlipBrick::class, DroneMoveBackwardBrick::class,
                DroneMoveDownBrick::class, DroneMoveForwardBrick::class,
                DroneMoveLeftBrick::class, DroneMoveRightBrick::class,
                DroneMoveUpBrick::class, DronePlayLedAnimationBrick::class,
                DroneTakeOffLandBrick::class, DroneTurnLeftBrick::class,
                DroneTurnRightBrick::class, GlideToBrick::class, GoNStepsBackBrick::class,
                GoToBrick::class, ComeToFrontBrick::class, IfOnEdgeBounceBrick::class,
                JumpingSumoAnimationsBrick::class, JumpingSumoJumpHighBrick::class,
                JumpingSumoJumpLongBrick::class, JumpingSumoMoveBackwardBrick::class,
                JumpingSumoMoveForwardBrick::class, JumpingSumoRotateLeftBrick::class,
                JumpingSumoRotateRightBrick::class, JumpingSumoTakingPictureBrick::class,
                JumpingSumoTurnBrick::class, MoveNStepsBrick::class, SetBounceBrick::class,
                SetFrictionBrick::class, SetGravityBrick::class, SetMassBrick::class,
                SetPhysicsObjectTypeBrick::class, SetVelocityBrick::class,
                TurnLeftSpeedBrick::class, TurnRightSpeedBrick::class, PlaceAtBrick::class,
                PointInDirectionBrick::class, PointToBrick::class,
                SetRotationStyleBrick::class, SetTextBrick::class, SetXBrick::class,
                SetYBrick::class, TurnLeftBrick::class, TurnRightBrick::class,
                VibrationBrick::class, WhenBounceOffBrick::class
            )

        if (isInstanceOfAny(item, eventBrickList)) {
            return inflater.inflate(R.layout.collapsed_event_brick, null)
        }
        if (isInstanceOfAny(item, lookBrickList)) {
            return inflater.inflate(R.layout.collapsed_looks_brick, null)
        }
        if (isInstanceOfAny(item, soundBrickList)) {
            return inflater.inflate(R.layout.collapsed_sound_brick, null)
        }
        if (isInstanceOfAny(item, motionBrickList)) {
            return inflater.inflate(R.layout.collapsed_motion_brick, null)
        }
        if (isInstanceOfAny(item, controlBrickList)) {
            return inflater.inflate(R.layout.collapsed_control_brick, null)
        }
        throw NullPointerException("No collapsed view found")
    }

    private fun checkBoxClickListener(item: Brick, itemView: ViewGroup, position: Int) {
        item.checkBox.setOnClickListener { onCheckBoxClick(position) }
        when (checkBoxMode) {
            NONE -> handleCheckBoxModeNone(item)
            CONNECTED_ONLY -> handleCheckBoxModeConnectedOnly(item, itemView, position)
            ALL -> handleCheckBoxModeAll(item)
            SCRIPTS_ONLY -> handleCheckBoxModeScriptsOnly(item)
        }
    }

    private fun handleCheckBoxModeScriptsOnly(item: Brick) {
        val isScriptBrick = item is ScriptBrick
        item.checkBox.visibility = if (isScriptBrick) View.VISIBLE else View.INVISIBLE
        item.disableSpinners()
    }

    private fun handleCheckBoxModeAll(item: Brick) {
        item.checkBox.visibility = View.VISIBLE
        item.disableSpinners()
    }

    private fun handleCheckBoxModeNone(item: Brick) {
        item.checkBox.visibility = View.GONE
        if (item is FormulaBrick) {
            item.setClickListeners()
        } else if (item is ListSelectorBrick) {
            item.setClickListeners()
        }
    }

    private fun handleCheckBoxModeConnectedOnly(item: Brick, itemView: ViewGroup, position: Int) {
        if (item is UserDefinedReceiverBrick) {
            viewStateManager.setEnabled(false, position)
            itemView.alpha = DISABLED_BRICK_ALPHA
        }
        item.checkBox.visibility = View.VISIBLE
        item.disableSpinners()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (checkBoxMode == NONE) {
            val item = items[position]
            onItemClickListener?.onBrickClick(item, position)
        }
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        if (checkBoxMode == NONE) {
            val item = items[position]
            onItemClickListener?.onBrickLongClick(item, position)
            return true
        }
        return false
    }

    private fun onCheckBoxClick(position: Int) {
        val selected = !selectionManager.isPositionSelected(position)
        setSelectionTo(selected, position)
        selectionListener?.onSelectionChanged(selectionManager.selectedPositions.size)
        notifyDataSetChanged()
    }

    private fun setSelectionTo(selected: Boolean, position: Int) {
        val item = items[position]

        val flatItems: List<Brick> = ArrayList()
        item.addToFlatList(flatItems)

        val scriptSelected = item is ScriptBrick
        var adapterPosition = -1

        if (selected && noConnectedItemsSelected()) {
            firstConnectedItem = position - 1
            lastConnectedItem = position + 1
        }

        for (i in flatItems.indices) {
            adapterPosition = items.indexOf(flatItems[i])
            selectionManager.setSelectionTo(selected, adapterPosition)
            if (i > 0) {
                viewStateManager.setEnabled(!selected, adapterPosition)
            }
        }

        if (checkBoxMode == CONNECTED_ONLY) {
            val firstFlatListPosition = items.indexOf(flatItems[0])
            updateConnectedItems(
                position,
                firstFlatListPosition,
                adapterPosition,
                selected,
                scriptSelected
            )
        }
    }

    private fun updateConnectedItems(
        selectedPosition: Int,
        firstFlatListPosition: Int,
        lastFlatListPosition: Int,
        selected: Boolean,
        scriptSelected: Boolean
    ) {
        if (selected) {
            if (lastFlatListPosition >= lastConnectedItem) {
                lastConnectedItem = lastFlatListPosition + 1
            }
            if (firstFlatListPosition <= firstConnectedItem) {
                firstConnectedItem = firstFlatListPosition - 1
            }
        } else {
            if (selectedPosition == firstConnectedItem + 1) {
                firstConnectedItem = firstFlatListPosition
            }
            if (selectedPosition == lastConnectedItem - 1) {
                lastConnectedItem = firstFlatListPosition
            }
            if (selectionManager.selectedPositions.isEmpty()) {
                clearConnectedItems()
            }
        }
        for (item in items) {
            val brickPosition = items.indexOf(item)
            viewStateManager.setEnabled(
                selectableForCopy(brickPosition, scriptSelected),
                brickPosition
            )
        }
    }

    private fun selectableForCopy(brickPosition: Int, scriptSelected: Boolean): Boolean =
        noConnectedItemsSelected() || isItemWithinConnectedRange(
            brickPosition,
            scriptSelected
        ) && !isItemOfNewScript(brickPosition, scriptSelected)

    private fun isItemWithinConnectedRange(brickPosition: Int, scriptSelected: Boolean): Boolean {
        return brickPosition >= firstConnectedItem && brickPosition <= firstConnectedItem + 1 ||
            brickPosition <= lastConnectedItem && brickPosition >= lastConnectedItem - 1 && !scriptSelected
    }

    private fun isItemOfNewScript(brickPosition: Int, scriptSelected: Boolean): Boolean {
        return lastConnectedItem == brickPosition && items[brickPosition] is ScriptBrick ||
            scriptSelected && brickPosition <= firstConnectedItem
    }

    private fun noConnectedItemsSelected(): Boolean =
        firstConnectedItem == -1 && lastConnectedItem == -1

    private fun clearConnectedItems() {
        firstConnectedItem = -1
        lastConnectedItem = -1
    }

    val selectedItems: List<Brick>
        get() {
            val selectedItems: MutableList<Brick> = ArrayList()
            for (position in selectionManager.selectedPositions) {
                selectedItems.add(items[position])
            }
            return selectedItems
        }

    fun clearSelection() {
        selectionManager.clearSelection()
        viewStateManager.clearDisabledPositions()
        clearConnectedItems()
        notifyDataSetChanged()
    }

    override fun setItemVisible(position: Int, visible: Boolean) {
        viewStateManager.setVisible(position, visible)
    }

    override fun setAllPositionsVisible() {
        viewStateManager.setAllPositionsVisible()
    }

    fun selectAllCommentedOutBricks() {
        for (i in items.indices) {
            setSelectionTo(items[i].isCommentedOut, i)
        }
        notifyDataSetChanged()
    }

    fun selectAllCollapsedBricks() {
        for (i in items.indices) {
            setSelectionTo(items[i].isCollapsed, i)
        }
        notifyDataSetChanged()
    }

    fun addItem(position: Int, item: Brick?) {
        item?.let { items.add(position, it) }
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Brick = items[position]

    fun findByHash(hashCode: Int): Brick? {
        for (item in items) {
            if (item.hashCode() == hashCode) {
                return item
            }
        }
        return null
    }

    override fun removeItems(items: List<Brick>): Boolean {
        if (this.items.removeAll(items)) {
            notifyDataSetChanged()
            return true
        }
        return false
    }

    override fun getPosition(brick: Brick?): Int = items.indexOf(brick)

    override fun onItemMove(sourcePosition: Int, targetPosition: Int): Boolean {
        val source = items[sourcePosition]
        if (source !is ScriptBrick && targetPosition == 0) {
            return false
        }
        if (source.allParts.contains(items[targetPosition])) {
            return false
        }
        Collections.swap(items, sourcePosition, targetPosition)
        return true
    }

    override fun moveItemTo(position: Int, itemToMove: Brick?) {
        val brickAboveTargetPosition = getBrickAbovePosition(position)

        if (itemToMove is ScriptBrick) {
            moveScript(itemToMove, brickAboveTargetPosition)
        } else {
            for (script in scripts) {
                script.removeBrick(itemToMove)
            }
            val destinationPosition = brickAboveTargetPosition.positionInDragAndDropTargetList + 1
            val destinationList = brickAboveTargetPosition.dragAndDropTargetList

            if (destinationPosition < destinationList.size) {
                destinationList.add(destinationPosition, itemToMove)
            } else {
                destinationList.add(itemToMove)
            }
        }
        updateItemsFromCurrentScripts()
    }

    private fun moveScript(itemToMove: ScriptBrick, brickAboveTargetPosition: Brick) {
        val scriptToMove = itemToMove.script
        val scriptAtTargetPosition = brickAboveTargetPosition.script
        val bricksInScriptToMove = scriptToMove.brickList
        val bricksInScriptAtTargetPosition = scriptAtTargetPosition.brickList

        val divideScriptAtPositionAndAddBricksToMovingScript =
            bricksInScriptToMove.isEmpty() && bricksInScriptAtTargetPosition.isNotEmpty()

        if (divideScriptAtPositionAndAddBricksToMovingScript) {
            val positionToDivideScriptAt = brickAboveTargetPosition.positionInScript + 1
            val bricksToMove: MutableList<Brick> = ArrayList()

            for (i in positionToDivideScriptAt until bricksInScriptAtTargetPosition.size) {
                bricksToMove.add(bricksInScriptAtTargetPosition[i])
            }

            bricksInScriptToMove.addAll(bricksToMove)
            bricksInScriptAtTargetPosition.removeAll(bricksToMove)
        }

        scripts.remove(scriptToMove)
        val destinationPosition = scripts.indexOf(scriptAtTargetPosition) + 1

        if (destinationPosition == scripts.size) {
            scripts.add(scriptToMove)
        } else {
            scripts.add(destinationPosition, scriptToMove)
        }
    }

    private fun getBrickAbovePosition(position: Int): Brick {
        var position = position
        if (position > 0) {
            position--
        }
        return items[position]
    }

    override fun getCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    interface SelectionListener {
        fun onSelectionChanged(selectedItemCnt: Int)
    }

    interface OnBrickClickListener {
        fun onBrickClick(item: Brick, position: Int)
        fun onBrickLongClick(item: Brick, position: Int): Boolean
    }
}
