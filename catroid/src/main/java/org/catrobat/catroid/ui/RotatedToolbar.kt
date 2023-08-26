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

package org.catrobat.catroid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import org.catrobat.catroid.R

class RotatedToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val closeButton: ImageButton
    private val titleTextView: TextView
    private val acceptButton: ImageButton

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.rotated_toolbar, this, true)
        closeButton = view.findViewById(R.id.rotated_toolbar_buttonClose)
        acceptButton = view.findViewById(R.id.rotated_toolbar_buttonAccept)
        titleTextView = view.findViewById(R.id.rotated_toolbar_title)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RotatedToolbar)
            val titleText = typedArray.getString(R.styleable.RotatedToolbar_title)
            titleTextView.text = titleText
            typedArray.recycle()
        }
    }

    fun setTitle(title: String) {
       titleTextView.text = title
    }

    fun setAcceptButtonOnClickListener(l: OnClickListener) {
        acceptButton.setOnClickListener(l)
    }

    fun setCloseButtonOnClickListener(l: OnClickListener) {
        closeButton.setOnClickListener(l)
    }
}