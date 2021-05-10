/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.content.bricks.brickspinner

import android.content.Context
import org.catrobat.catroid.common.Nameable
import android.widget.AdapterView
import android.widget.Spinner
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner.BrickSpinnerAdapter
import org.catrobat.catroid.content.bricks.brickspinner.NewOption
import org.catrobat.catroid.content.bricks.brickspinner.EditOption
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.content.bricks.Brick
import androidx.fragment.app.FragmentActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.R
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.view.View.OnTouchListener
import androidx.annotation.VisibleForTesting

class BrickSpinner<T : Nameable?>(private val spinnerid: Int, parent: View, items: List<Nameable>) :
    AdapterView.OnItemSelectedListener {
    private val spinner: Spinner
    private val adapter: BrickSpinnerAdapter
    private var previousItem: T? = null
    private var onItemSelectedListener: OnItemSelectedListener<T>? = null
    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener<T>?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val item = adapter.getItem(position)
        if (onItemSelectedListener == null || item == null) {
            return
        }
        if (item.javaClass == NewOption::class.java || item.javaClass == EditOption::class.java) {
            return
        }
        onSelectionChanged(view, item)
        if (item.javaClass == StringOption::class.java) {
            onItemSelectedListener!!.onStringOptionSelected(spinnerid, item.name)
            return
        }
        onItemSelectedListener!!.onItemSelected(spinnerid, item as T)
    }

    private fun onSelectionChanged(view: View, item: Nameable) {
        if (previousItem != null && previousItem !== item) {
            showUndo(view)
        }
        previousItem = item as T
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    fun add(item: T) {
        adapter.add(item)
    }

    val items: List<T>
        get() = adapter.items as List<T>

    fun setSelection(position: Int) {
        spinner.setSelection(position)
    }

    fun setSelection(itemName: String?) {
        spinner.onItemSelectedListener = null
        var position = adapter.getPosition(itemName)
        position = consolidateSpinnerSelection(position)
        spinner.setSelection(position)
        onSelectionSet(adapter.getItem(position))
        spinner.onItemSelectedListener = this
    }

    fun setSelection(item: T?) {
        spinner.onItemSelectedListener = null
        var position = adapter.getPosition(item)
        position = consolidateSpinnerSelection(position)
        if (position < adapter.count) {
            spinner.setSelection(position)
            onSelectionSet(adapter.getItem(position))
            spinner.onItemSelectedListener = this
        }
    }

    val selection: Any
        get() = spinner.selectedItem

    private fun consolidateSpinnerSelection(position: Int): Int {
        var position = position
        if (position == -1) {
            position = if (adapter.containsNewOption()) {
                if (adapter.containsEditOption()) {
                    if (adapter.count > 2) 2 else 0
                } else {
                    if (adapter.count > 1) 1 else 0
                }
            } else {
                0
            }
        }
        return position
    }

    private fun onSelectionSet(selectedItem: Nameable?) {
        if (onItemSelectedListener != null) {
            if (selectedItem?.javaClass == NewOption::class.java || selectedItem?.javaClass ==
                EditOption::class.java) {
                onItemSelectedListener!!.onItemSelected(spinnerid, null)
                return
            }
            if (selectedItem?.javaClass == StringOption::class.java) {
                onItemSelectedListener!!.onStringOptionSelected(spinnerid, selectedItem!!.name)
                return
            }
            onItemSelectedListener!!.onItemSelected(spinnerid, selectedItem as T?)
        }
    }

    private fun showUndo(view: View) {
        val scriptFragment = getScriptFragment(view)
        if (scriptFragment!!.copyProjectForUndoOption()) {
            scriptFragment.showUndo(true)
            if (onItemSelectedListener is Brick) {
                scriptFragment.setUndoBrickPosition(onItemSelectedListener as Brick?)
            }
        }
    }

    private fun getScriptFragment(view: View?): ScriptFragment? {
        var activity: FragmentActivity? = null
        if (view != null) {
            activity = UiUtils.getActivityFromView(view)
        }
        if (activity == null) {
            return null
        }
        val currentFragment =
            activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
        return if (currentFragment is ScriptFragment) {
            currentFragment
        } else null
    }

    @VisibleForTesting
    inner class BrickSpinnerAdapter internal constructor(
        context: Context,
        resource: Int,
        val items: List<Nameable>
    ) : ArrayAdapter<Nameable?>(context, resource, items) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
            }
            val item = getItem(position)
            (convertView as TextView?)!!.text = item!!.name
            convertView!!.setOnTouchListener { view, event ->
                if (event.actionIndex == MotionEvent.ACTION_DOWN) {
                    if (item.javaClass == NewOption::class.java) {
                        onItemSelectedListener!!.onNewOptionSelected(spinnerid)
                    } else if (item.javaClass == EditOption::class.java) {
                        onItemSelectedListener!!.onEditOptionSelected(spinnerid)
                    }
                }
                false
            }
            return convertView
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_spinner_item, parent, false)
            }
            val item = getItem(position)
            (convertView as TextView?)!!.text = item!!.name
            return convertView!!
        }

        fun getPosition(itemName: String?): Int {
            for (position in 0 until count) {
                if (getItem(position)!!.name == itemName) {
                    return position
                }
            }
            return -1
        }

        fun containsNewOption(): Boolean {
            for (item in items) {
                if (item is NewOption) {
                    return true
                }
            }
            return false
        }

        fun containsEditOption(): Boolean {
            for (item in items) {
                if (item is EditOption) {
                    return true
                }
            }
            return false
        }
    }

    interface OnItemSelectedListener<T> {
        fun onNewOptionSelected(spinnerId: Int?)
        fun onEditOptionSelected(spinnerId: Int?)
        fun onStringOptionSelected(spinnerId: Int?, string: String?)
        fun onItemSelected(spinnerId: Int?, item: T?)
    }

    init {
        adapter = BrickSpinnerAdapter(parent.context, android.R.layout.simple_spinner_item, items)
        spinner = parent.findViewById(spinnerid)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = this
    }
}