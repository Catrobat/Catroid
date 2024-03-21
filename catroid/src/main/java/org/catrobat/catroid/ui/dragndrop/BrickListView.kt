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
package org.catrobat.catroid.ui.dragndrop

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ListAdapter
import android.widget.ListView
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.content.bricks.Brick
import java.util.ArrayList

private const val SMOOTH_SCROLL_BY = 15
private const val ANIMATION_DURATION = 250
private const val TRANSLUCENT_BLACK_ALPHA = 128
private const val OBJECT_ANIMATOR_VALUE = 255
private const val ANIMATION_REPEAT_COUNT = 5
private const val UPPER_SCROLL_BOUND_DIVISOR = 8
private const val LOWER_SCROLL_BOUND_DIVISOR = 48
private const val Y_TRANSLATION_CONSTANT = 10

class BrickListView : ListView {
    private var upperScrollBound = 0
    private var lowerScrollBound = 0
    private var hoveringDrawables: MutableList<BitmapDrawable?> = ArrayList()
    private var viewBounds: MutableList<Rect> = ArrayList()
    private var currentPositionOfHoveringBrick = 0
    private var bricksToMove: MutableList<Brick> = ArrayList()
    private var relativePositionOfMovingBricks: MutableList<Int> = ArrayList()
    private var motionEventId = -1
    private var downY = 0f
    private var offsetToCenter = 0
    private var invalidateHoveringItem = false
    private var brickAdapterInterface: BrickAdapterInterface? = null
    private val translucentBlack = Color.argb(TRANSLUCENT_BLACK_ALPHA, 0, 0, 0)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes)
    constructor(context: Context?, attributes: AttributeSet?, defStyle: Int) : super(
        context,
        attributes,
        defStyle
    )

    val brickPositionsToHighlight: MutableList<Int> = ArrayList()

    val isCurrentlyMoving: Boolean
        get() = hoveringDrawables.isNotEmpty()

    val isCurrentlyHighlighted: Boolean
        get() = brickPositionsToHighlight.isNotEmpty()

    fun highlightMovingItem() {
        for (drawable in hoveringDrawables) {
            val animator = ObjectAnimator.ofInt(drawable!!, "alpha", OBJECT_ANIMATOR_VALUE, 0)
            animator.duration = ANIMATION_DURATION.toLong()
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ANIMATION_REPEAT_COUNT
            animator.start()
            animator.addUpdateListener { invalidate() }
        }
    }

    fun cancelHighlighting() {
        brickPositionsToHighlight.clear()
        invalidate()
    }

    fun highlightControlStructureBricks(positions: Collection<Int>) {
        cancelHighlighting()
        brickPositionsToHighlight.addAll(positions)
        invalidate()
    }

    fun startMoving(bricksToMove: List<Brick?>) {
        cancelMove()
        val flatList: MutableList<Brick> = ArrayList()

        var lastPosition = 0
        for (brick in bricksToMove) {
            if (brick in flatList) {
                continue
            }
            val brickFlatList: MutableList<Brick> = ArrayList()
            brick?.addToFlatList(brickFlatList)
            if (brick != brickFlatList[0]) {
                return
            }
            if (this.relativePositionOfMovingBricks.isEmpty()) {
                relativePositionOfMovingBricks.add(0)
            } else {
                relativePositionOfMovingBricks.add(
                    brickAdapterInterface!!.getPosition(brickFlatList[0])
                        - lastPosition
                )
            }
            lastPosition = brickAdapterInterface!!.getPosition(brickFlatList[0])
            this.bricksToMove.add(brickFlatList[0])
            brickFlatList.removeAt(0)
            flatList.addAll(brickFlatList)
        }

        upperScrollBound = height / UPPER_SCROLL_BOUND_DIVISOR
        lowerScrollBound = height / LOWER_SCROLL_BOUND_DIVISOR
        currentPositionOfHoveringBrick =
            brickAdapterInterface!!.getPosition(this.bricksToMove.get(0))
        invalidateHoveringItem = true

        for (i in this.bricksToMove.indices) {
            prepareHoveringItem(getChildAtVisiblePosition(currentPositionOfHoveringBrick + i))
            brickAdapterInterface?.setItemVisible(currentPositionOfHoveringBrick + i, false)
        }

        if (!brickAdapterInterface!!.removeItems(flatList)) {
            invalidateViews()
        }
    }

    fun stopMoving() {
        var pos = currentPositionOfHoveringBrick
        for (i in bricksToMove.indices) {
            pos += relativePositionOfMovingBricks[i]
            brickAdapterInterface?.moveItemTo(
                pos,
                bricksToMove[i]
            )
            pos = brickAdapterInterface!!.getPosition(bricksToMove[i])
        }
        cancelMove()
    }

    fun cancelMove() {
        brickAdapterInterface?.setAllPositionsVisible()
        motionEventId = -1
        bricksToMove.clear()
        relativePositionOfMovingBricks.clear()
        hoveringDrawables.clear()
        viewBounds.clear()
        hoveringDrawables.clear()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (hoveringDrawables.isEmpty()) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> stopMoving()
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
                motionEventId = event.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                val dY = event.y - downY
                downY += dY
                downY -= offsetToCenter.toFloat()
                var offsetHeight = 0
                for (i in viewBounds.indices) {
                    viewBounds[i].offsetTo(viewBounds[i].left, downY.toInt() + offsetHeight)
                    hoveringDrawables[i]?.bounds = viewBounds[i]
                    offsetHeight += viewBounds[i].height()
                }
                invalidate()
                swapListItems()
                scrollWhileDragging()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex =
                    event.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                if (event.getPointerId(pointerIndex) == motionEventId) {
                    stopMoving()
                }
            }
        }
        return true
    }

    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (bricksToMove.isNotEmpty() || brickPositionsToHighlight.isNotEmpty()) {
            canvas.drawColor(translucentBlack)
        }
        if (invalidateHoveringItem) {
            hoveringDrawables.clear()
            viewBounds.clear()
            for (i in bricksToMove.indices) {
                val childAtVisiblePosition = getChildAtVisiblePosition(currentPositionOfHoveringBrick + i)
                if (childAtVisiblePosition != null) {
                    invalidateHoveringItem = false
                    prepareHoveringItem(childAtVisiblePosition)
                }
            }
        }

        for (drawable in hoveringDrawables) {
            drawable?.draw(canvas)
        }

        for (pos in brickPositionsToHighlight) {
            if (pos in firstVisiblePosition..lastVisiblePosition) {
                drawHighlightedItem(getChildAtVisiblePosition(pos), canvas)
            }
        }
    }

    @VisibleForTesting
    fun drawHighlightedItem(view: View?, canvas: Canvas?) {
        if (view == null) {
            return
        }
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))

        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(view.left, view.top, view.right, view.bottom)
        drawable.draw(canvas!!)
    }

    private fun prepareHoveringItem(view: View?) {
        if (view == null) {
            return
        }
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))

        viewBounds.add(Rect(view.left, view.top, view.right, view.bottom))
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.bounds = viewBounds.last()
        hoveringDrawables.add(drawable)
        setOffsetToCenter()
    }

    private fun setOffsetToCenter() {
        var viewBoundHeight = 0
        for (viewBound in viewBounds) {
            viewBoundHeight += viewBound.height()
        }
        offsetToCenter = viewBoundHeight / 2
    }

    @Suppress("ComplexMethod")
    private fun swapListItems() {
        val itemPositionAbove = currentPositionOfHoveringBrick - 1
        val itemPositionBelow = currentPositionOfHoveringBrick + bricksToMove.size
        val itemBelow: View? =
            if (isPositionValid(itemPositionBelow)) getChildAtVisiblePosition(itemPositionBelow) else null
        val itemAbove: View? =
            if (isPositionValid(itemPositionAbove)) getChildAtVisiblePosition(itemPositionAbove) else null

        var downYAdd = 0
        for (viewBound in viewBounds) {
            downYAdd += viewBound.height()
        }
        downYAdd -= viewBounds.last().height()

        val isAbove = itemBelow != null && downY + downYAdd > itemBelow.y
        val isBelow = itemAbove != null && downY < itemAbove.y

        if (isAbove || isBelow) {
            var viewBoundsHeight = 0
            for (viewBound in viewBounds) {
                viewBoundsHeight += viewBound.height()
            }
            val translationY =
                if (isAbove) Y_TRANSLATION_CONSTANT - viewBoundsHeight else viewBoundsHeight -
                    Y_TRANSLATION_CONSTANT

            if (isAbove) {
                var currentSwapPos = itemPositionBelow
                for (i in bricksToMove.lastIndex downTo 0) {
                    if (brickAdapterInterface?.onItemMove(
                            currentPositionOfHoveringBrick + i,
                            currentSwapPos
                        ) == false) {
                            return
                        }
                    currentSwapPos -= 1
                }
                brickAdapterInterface?.setItemVisible(currentSwapPos, true)
                currentPositionOfHoveringBrick += 1
            } else {
                var currentSwapPos = itemPositionAbove
                for (i in bricksToMove.indices) {
                    if (brickAdapterInterface?.onItemMove(
                            currentPositionOfHoveringBrick + i,
                            currentSwapPos
                        ) == false) {
                        return
                    }
                    currentSwapPos += 1
                }
                brickAdapterInterface?.setItemVisible(currentSwapPos, true)
                currentPositionOfHoveringBrick -= 1
            }

            for (i in bricksToMove.indices) {
                brickAdapterInterface?.setItemVisible(currentPositionOfHoveringBrick + i, false)
            }

            val viewToSwapWith = if (isAbove) itemBelow else itemAbove
            startAnimationToSwap(viewToSwapWith, translationY)
        }
    }

    private fun startAnimationToSwap(viewToSwapWith: View?, translationY: Int) {
        val animator = ObjectAnimator.ofFloat(viewToSwapWith, TRANSLATION_Y, translationY.toFloat())
        animator.duration = ANIMATION_DURATION.toLong()
        animator.start()

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) = Unit
            override fun onAnimationEnd(animation: Animator) {
                invalidateViews()
            }

            override fun onAnimationCancel(animation: Animator) = Unit
            override fun onAnimationRepeat(animation: Animator) = Unit
        })
    }

    private fun scrollWhileDragging() {
        if (downY > lowerScrollBound) {
            smoothScrollBy(SMOOTH_SCROLL_BY, 0)
        } else if (downY < upperScrollBound) {
            smoothScrollBy(-SMOOTH_SCROLL_BY, 0)
        }
    }

    private fun getChildAtVisiblePosition(positionInAdapter: Int): View? =
        getChildAt(positionInAdapter - firstVisiblePosition)

    private fun isPositionValid(position: Int): Boolean = position in 0 until count

    override fun setAdapter(adapter: ListAdapter) {
        require(adapter is BrickAdapterInterface) { "Adapter has to implement the BrickListView.AdapterInterface." }
        super.setAdapter(adapter)
        brickAdapterInterface = adapter
    }
}
