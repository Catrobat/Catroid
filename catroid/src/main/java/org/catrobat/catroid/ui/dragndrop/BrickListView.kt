/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
    private var hoveringDrawable: BitmapDrawable? = null
    private val viewBounds = Rect()
    private var currentPositionOfHoveringBrick = 0
    private var brickToMove: Brick? = null
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
        get() = hoveringDrawable != null

    val isCurrentlyHighlighted: Boolean
        get() = brickPositionsToHighlight.isNotEmpty()

    fun highlightMovingItem() {
        val animator = ObjectAnimator.ofInt(hoveringDrawable!!, "alpha", OBJECT_ANIMATOR_VALUE, 0)
        animator.duration = ANIMATION_DURATION.toLong()
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ANIMATION_REPEAT_COUNT
        animator.start()
        animator.addUpdateListener { invalidate() }
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

    fun startMoving(brickToMove: Brick?) {
        cancelMove()
        val flatList: MutableList<Brick> = ArrayList()
        brickToMove?.addToFlatList(flatList)
        if (brickToMove !== flatList[0]) {
            return
        }
        this.brickToMove = flatList[0]
        flatList.removeAt(0)

        upperScrollBound = height / UPPER_SCROLL_BOUND_DIVISOR
        lowerScrollBound = height / LOWER_SCROLL_BOUND_DIVISOR
        currentPositionOfHoveringBrick = brickAdapterInterface!!.getPosition(this.brickToMove)
        invalidateHoveringItem = true

        prepareHoveringItem(getChildAtVisiblePosition(currentPositionOfHoveringBrick))
        brickAdapterInterface?.setItemVisible(currentPositionOfHoveringBrick, false)

        if (!brickAdapterInterface!!.removeItems(flatList)) {
            invalidateViews()
        }
    }

    fun stopMoving() {
        brickAdapterInterface?.moveItemTo(currentPositionOfHoveringBrick, brickToMove)
        cancelMove()
    }

    fun cancelMove() {
        brickAdapterInterface?.setAllPositionsVisible()
        brickToMove = null
        hoveringDrawable = null
        motionEventId = -1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (hoveringDrawable == null) {
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
                viewBounds.offsetTo(viewBounds.left, downY.toInt())
                hoveringDrawable?.bounds = viewBounds
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
        if (brickToMove != null || brickPositionsToHighlight.isNotEmpty()) {
            canvas.drawColor(translucentBlack)
        }
        if (invalidateHoveringItem) {
            val childAtVisiblePosition = getChildAtVisiblePosition(currentPositionOfHoveringBrick)
            if (childAtVisiblePosition != null) {
                invalidateHoveringItem = false
                prepareHoveringItem(childAtVisiblePosition)
            }
        }

        hoveringDrawable?.draw(canvas)

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

        viewBounds[view.left, view.top, view.right] = view.bottom
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.bounds = viewBounds
        hoveringDrawable = drawable
        setOffsetToCenter(viewBounds)
    }

    private fun setOffsetToCenter(viewBounds: Rect) {
        offsetToCenter = viewBounds.height() / 2
    }

    @Suppress("ComplexMethod")
    private fun swapListItems() {
        val itemPositionAbove = currentPositionOfHoveringBrick - 1
        val itemPositionBelow = currentPositionOfHoveringBrick + 1
        val itemBelow: View? = if (isPositionValid(itemPositionBelow)) getChildAtVisiblePosition(itemPositionBelow) else null
        val itemAbove: View? = if (isPositionValid(itemPositionAbove)) getChildAtVisiblePosition(itemPositionAbove) else null

        val isAbove = itemBelow != null && downY > itemBelow.y
        val isBelow = itemAbove != null && downY < itemAbove.y

        if (isAbove || isBelow) {
            val swapWith = if (isAbove) itemPositionBelow else itemPositionAbove
            val translationY = if (isAbove) Y_TRANSLATION_CONSTANT - viewBounds.height() else viewBounds.height() - Y_TRANSLATION_CONSTANT

            if (brickAdapterInterface?.onItemMove(currentPositionOfHoveringBrick, swapWith) == true) {
                brickAdapterInterface?.setItemVisible(currentPositionOfHoveringBrick, true)
                currentPositionOfHoveringBrick = swapWith
                brickAdapterInterface?.setItemVisible(currentPositionOfHoveringBrick, false)

                val viewToSwapWith = if (isAbove) itemBelow else itemAbove
                startAnimationToSwap(viewToSwapWith, translationY)
            }
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
