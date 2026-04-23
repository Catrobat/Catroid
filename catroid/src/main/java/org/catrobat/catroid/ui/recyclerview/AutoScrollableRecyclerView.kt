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

package org.catrobat.catroid.ui.recyclerview

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import java.lang.ref.WeakReference

class AutoScrollableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private val scrollHandler by lazy {
        ScrollHandler(this)
    }

    var itemsCount: Int = 0

    private val delayMillis: Long

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AutoScrollableRecyclerView,
            0,
            0
        )
            .apply {
            try {
                delayMillis = getInt(
                    R.styleable.AutoScrollableRecyclerView_delay,
                    DELAY_BETWEEN_SCROLLS
                )
                    .toLong()
            } finally {
                recycle()
            }
        }
    }

    private fun createScroller(position: Int) = object : LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_END
        }

        override fun calculateTimeForScrolling(dx: Int) = DURATION_OF_SCROLL
    }.apply {
        targetPosition = position
    }

    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        when (e?.action) {
            MotionEvent.ACTION_UP -> resumeAutoScroll()
            MotionEvent.ACTION_DOWN -> pauseAutoScroll()
        }
        parent.requestDisallowInterceptTouchEvent(true)

        return super.dispatchTouchEvent(e)
    }

    private fun pauseAutoScroll() {
        scrollHandler.removeMessages(WHAT_SCROLL)
    }

    fun resumeAutoScroll() {
        scrollHandler.removeMessages(WHAT_SCROLL)
        scrollHandler.sendEmptyMessageDelayed(WHAT_SCROLL, delayMillis)
    }

    fun scrollNext() {
        (layoutManager as LinearLayoutManager).let { layoutManager ->
            var position = layoutManager.findLastVisibleItemPosition() + 1
            if (position >= itemsCount) {
                position = 0
            }
            layoutManager.startSmoothScroll(
                createScroller(position)
            )
        }
        scrollHandler.sendEmptyMessageDelayed(WHAT_SCROLL, delayMillis)
    }

    private class ScrollHandler(autoScrollableRecyclerView: AutoScrollableRecyclerView) :
        Handler() {
        private val autoScrollViewPager =
            WeakReference<AutoScrollableRecyclerView>(autoScrollableRecyclerView)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            autoScrollViewPager.get()?.scrollNext()
        }
    }

    companion object {
        private const val WHAT_SCROLL = 1
        private const val DELAY_BETWEEN_SCROLLS = 5000
        private const val DURATION_OF_SCROLL = 500
    }
}
