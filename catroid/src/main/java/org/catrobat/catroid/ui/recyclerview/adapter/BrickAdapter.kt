/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.EndBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ListSelectorBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.ui.dragndrop.BrickAdapterInterface
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.ViewStateManager
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import java.util.ArrayList
import java.util.Collections

class BrickAdapter(private val sprite: Sprite) :
    BaseAdapter(),
    BrickAdapterInterface,
    AdapterView.OnItemClickListener,
    OnItemLongClickListener {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, ALL, SCRIPTS_ONLY, CONNECTED_ONLY, COLLAPSE_EXPAND)
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
    private val squashedBricks = mutableSetOf<Brick>()

    init {
        updateItems(sprite)
    }

    companion object {
        const val DISABLED_BRICK_ALPHA = .8f
        const val NONE = 0
        const val ALL = 1
        const val SCRIPTS_ONLY = 2
        const val CONNECTED_ONLY = 3
        const val COLLAPSE_EXPAND = 4
        const val COLLAPSE_INDICATOR_TAG = "collapse_indicator"
        const val SQUASHED_HEIGHT_DP = 16

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
        filterCollapsedCompositeBricks()
        notifyDataSetChanged()
    }

    private fun filterCollapsedCompositeBricks() {
        val filteredItems = ArrayList<Brick>()
        squashedBricks.clear()
        
        var skipDepth = 0
        var skipEndBricks = 0
        var squashedRemaining = 0

        for (item in items) {
            if (item is ScriptBrick) {
                filteredItems.add(item)
                squashedRemaining = if (item.script.isCollapsed) 3 else 0
                skipDepth = 0
                continue
            }
            
            if (squashedRemaining > 0 && skipDepth == 0) {
                filteredItems.add(item)
                squashedBricks.add(item)
                squashedRemaining--
                continue
            }

            if (skipDepth > 0) {
                if (item is CompositeBrick) {
                    skipDepth++
                    skipEndBricks++
                }
                if (item is EndBrick) {
                    if (skipEndBricks > 0) {
                        skipEndBricks--
                    } else {
                        skipDepth--
                        if (skipDepth == 0) {
                            filteredItems.add(item)
                            continue
                        }
                    }
                }
                
                if (squashedRemaining > 0) {
                    filteredItems.add(item)
                    squashedBricks.add(item)
                    squashedRemaining--
                }
                continue
            }

            filteredItems.add(item)
            if (item is CompositeBrick && item is BrickBaseType && (item as BrickBaseType).isCollapsed) {
                skipDepth = 1
                skipEndBricks = 0
                squashedRemaining = 3
            }
        }

        items.clear()
        items.addAll(filteredItems)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]
        val itemView = item.getView(parent.context)

        itemView.visibility =
            if (viewStateManager.isVisible(position)) View.VISIBLE else View.INVISIBLE
        itemView.alpha = if (viewStateManager.isEnabled(position)) 1F else DISABLED_BRICK_ALPHA

        var brickViewContainer = (itemView as ViewGroup).getChildAt(1)
        if (item is UserDefinedReceiverBrick) {
            brickViewContainer = (itemView.getChildAt(1) as ViewGroup).getChildAt(0)
        }

        val background = brickViewContainer.background
        if (item.isCommentedOut || item is EmptyEventBrick) {
            colorAsCommentedOut(background)
        } else {
            background.clearColorFilter()
        }
        if (checkBoxMode != NONE) {
            checkBoxClickListener(item, itemView, position)
        } else {
            if (item is FormulaBrick) {
                item.setClickListeners()
            } else if (item is ListSelectorBrick) {
                item.setClickListeners()
            }
        }
        item.checkBox.isChecked = selectionManager.isPositionSelected(position)
        item.checkBox.isEnabled = viewStateManager.isEnabled(position)

        addCollapseIndicator(itemView, item)

        return itemView
    }

    private fun addCollapseIndicator(itemView: View, item: Brick) {
        val brickViewContainer = itemView.findViewWithTag<View>("brick_view_container")
            ?: (itemView as? ViewGroup)?.let { if (it.childCount > 1) it.getChildAt(1) else it.getChildAt(0) }
            ?: return

        val isCollapsed = (item is ScriptBrick && item.script.isCollapsed) ||
            (item is CompositeBrick && item is BrickBaseType && (item as BrickBaseType).isCollapsed)
        val isSquashed = squashedBricks.contains(item)

        val existingIndicator = itemView.findViewWithTag<View>(COLLAPSE_INDICATOR_TAG)
        if (existingIndicator != null) {
            (existingIndicator.parent as? ViewGroup)?.removeView(existingIndicator)
        }

        if (isSquashed) {
            val density = itemView.resources.displayMetrics.density
            val squashedHeight = (SQUASHED_HEIGHT_DP * density).toInt()
            itemView.layoutParams = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                squashedHeight
            )
            if (itemView is ViewGroup) {
                itemView.clipChildren = true
                // Hide checkbox
                if (itemView.childCount > 0) {
                    itemView.getChildAt(0).visibility = View.GONE
                }
                // Show colored brick layout but hide its internal text/icons
                brickViewContainer.visibility = View.VISIBLE
                if (brickViewContainer is ViewGroup) {
                    for (i in 0 until brickViewContainer.childCount) {
                        brickViewContainer.getChildAt(i).visibility = View.GONE
                    }
                }
            }
            return
        } else {
            // Reset to normal state
            itemView.layoutParams = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
            )
            if (itemView is ViewGroup) {
                itemView.clipChildren = false
                if (itemView.childCount > 0) {
                    itemView.getChildAt(0).visibility = View.VISIBLE
                }
                brickViewContainer.visibility = View.VISIBLE
                if (brickViewContainer is ViewGroup) {
                    for (i in 0 until brickViewContainer.childCount) {
                        val child = brickViewContainer.getChildAt(i)
                        if (child.tag != COLLAPSE_INDICATOR_TAG) {
                            child.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        val canCollapse = item is ScriptBrick || item is CompositeBrick
        if (!canCollapse) return

        val containerParent = brickViewContainer.parent as? ViewGroup ?: return
        val containerIndex = containerParent.indexOfChild(brickViewContainer)

        val indicatorIcon = ImageView(itemView.context).apply {
            tag = COLLAPSE_INDICATOR_TAG
            setImageResource(if (isCollapsed) R.drawable.ic_collapse else R.drawable.ic_expand)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            val density = resources.displayMetrics.density
            layoutParams = FrameLayout.LayoutParams(
                (32 * density).toInt(),
                (32 * density).toInt()
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                marginEnd = (8 * density).toInt()
            }
            setColorFilter(Color.WHITE)
            alpha = if (isCollapsed) 0.8f else 0.5f

            setOnClickListener {
                if (item is ScriptBrick) {
                    item.script.isCollapsed = !item.script.isCollapsed
                } else if (item is CompositeBrick && item is BrickBaseType) {
                    (item as BrickBaseType).isCollapsed = !(item as BrickBaseType).isCollapsed
                }
                updateItems(sprite)
            }
        }

        val wrapper: FrameLayout
        if (containerParent is FrameLayout && containerParent.tag == "collapse_wrapper") {
            wrapper = containerParent
        } else {
            wrapper = FrameLayout(itemView.context).apply {
                tag = "collapse_wrapper"
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }
            containerParent.removeView(brickViewContainer)
            wrapper.addView(brickViewContainer)
            containerParent.addView(wrapper, containerIndex)
        }

        if (wrapper.findViewWithTag<View>(COLLAPSE_INDICATOR_TAG) == null) {
            wrapper.addView(indicatorIcon)
        }
    }

    private fun checkBoxClickListener(item: Brick, itemView: ViewGroup, position: Int) {
        item.checkBox.setOnClickListener { onCheckBoxClick(position) }
        if (viewStateManager.isEnabled(position)) {
            itemView.setOnClickListener { onCheckBoxClick(position) }
        } else {
            itemView.setOnClickListener(null)
        }
        when (checkBoxMode) {
            NONE -> handleCheckBoxModeNone(item)
            CONNECTED_ONLY -> handleCheckBoxModeConnectedOnly(item, itemView, position)
            ALL -> handleCheckBoxModeAll(item)
            SCRIPTS_ONLY -> handleCheckBoxModeScriptsOnly(item)
            COLLAPSE_EXPAND -> handleCheckBoxModeAll(item)
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

    fun selectAllCollapsedScripts() {
        for (i in items.indices) {
            val item = items[i]
            if (item is ScriptBrick) {
                val script = item.script
                if (script != null && script.isCollapsed) {
                    selectionManager.setSelectionTo(true, i)
                }
            } else if (item is CompositeBrick && item is BrickBaseType) {
                if ((item as BrickBaseType).isCollapsed) {
                    selectionManager.setSelectionTo(true, i)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun toggleCollapseExpand(selectedItems: List<Brick>) {
        for (brick in selectedItems) {
            if (brick is ScriptBrick) {
                val script = brick.script
                if (script != null) {
                    script.isCollapsed = !script.isCollapsed
                }
            } else if (brick is CompositeBrick && brick is BrickBaseType) {
                (brick as BrickBaseType).isCollapsed = !(brick as BrickBaseType).isCollapsed
            }
        }
        updateItemsFromCurrentScripts()
    }

    private fun handleCheckBoxModeCollapseExpand(item: Brick) {
        item.checkBox.visibility = View.VISIBLE
        item.disableSpinners()
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
        if (source !is EndBrick && source.allParts.contains(items[targetPosition])) {
            return false
        }
        Collections.swap(items, sourcePosition, targetPosition)
        return true
    }

    private fun getParentBrickInDragAndDropList(
        brickAboveTarget: Brick,
        enclosureBrick: Brick
    ): Pair<Brick, Int>? {

        if (brickAboveTarget == enclosureBrick) {
            return brickAboveTarget to 0
        }

        var brickInEnclosure = brickAboveTarget
        while (brickInEnclosure.parent !== null &&
            brickInEnclosure.parent !in enclosureBrick.allParts &&
            brickInEnclosure !in enclosureBrick.dragAndDropTargetList
        ) {

            brickInEnclosure = brickInEnclosure.parent
        }

        if (brickInEnclosure.parent !== enclosureBrick &&
            brickInEnclosure !in enclosureBrick.dragAndDropTargetList
        ) {
            return null
        }

        return brickInEnclosure to
            enclosureBrick.dragAndDropTargetList.indexOf(brickInEnclosure) + 1
    }

    private fun moveEndIntoExtendedSection(
        position: Int,
        endBrick: Brick,
        brickAboveTargetPosition: Brick
    ): Boolean {
        var tmpParent = brickAboveTargetPosition
        val firstPart = endBrick.parent
        while (tmpParent.parent != null) {
            if (tmpParent is CompositeBrick) {
                moveItemTo(position, firstPart)
                return true
            }
            tmpParent = tmpParent.parent
            if (tmpParent !is CompositeBrick || tmpParent == firstPart.parent) {
                break
            }
        }
        return false
    }

    private fun moveEndTo(position: Int, endBrick: Brick, brickAboveTargetPosition: Brick) {
        if (endBrick.script !== brickAboveTargetPosition.script) {
            return
        }

        var startBrick = endBrick.parent
        var enclosure = (startBrick as CompositeBrick).nestedBricks
        if (startBrick.hasSecondaryList()) {
            enclosure = startBrick.secondaryNestedBricks
            startBrick = startBrick.allParts[1]
        }

        val (parentOfBrickAboveTargetPosition, destinationPosition) =
            getParentBrickInDragAndDropList(brickAboveTargetPosition, startBrick)
                ?: (null to 0)

        val indexStartBrick = getPosition(startBrick)
        if (getPosition(brickAboveTargetPosition) + 1 < indexStartBrick) {
            return
        }

        if (parentOfBrickAboveTargetPosition == null) {
            if (moveEndIntoExtendedSection(position, endBrick, brickAboveTargetPosition)) {
                return
            }

            var (outsideParent, outPosition) =
                getParentBrickInDragAndDropList(
                    brickAboveTargetPosition,
                    endBrick.parent.parent
                )
                    ?: return
            outsideParent = outsideParent.parent

            val startIndex =
                outsideParent.dragAndDropTargetList.indexOf(endBrick.parent) + 1
            for (i in startIndex until outPosition) {
                val brick = outsideParent.dragAndDropTargetList.removeAt(startIndex)
                brick.parent = startBrick
                enclosure.add(brick)
            }
        } else {
            val parentOfCompBrick = endBrick.parent.parent
            val positionInList = parentOfCompBrick.dragAndDropTargetList.indexOf(endBrick.parent)
            for (index in (destinationPosition until enclosure.size).withIndex()) {
                val brick = enclosure.removeAt(destinationPosition)
                brick.parent = parentOfCompBrick
                parentOfCompBrick.dragAndDropTargetList.add(positionInList + index.index + 1, brick)
            }
        }
    }

    override fun moveItemTo(position: Int, itemToMove: Brick?) {
        val brickAboveTargetPosition = getBrickAbovePosition(position)

        if (itemToMove is ScriptBrick) {
            moveScript(itemToMove, brickAboveTargetPosition)
        } else if (itemToMove is EndBrick) {
            moveEndTo(position, itemToMove, brickAboveTargetPosition)
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
