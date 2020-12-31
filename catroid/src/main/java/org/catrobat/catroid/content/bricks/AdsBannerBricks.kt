/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.StringOption

class HideAdsBannerBrick : BrickBaseType() {
    override fun getViewResource() = R.layout.brick_hide_ads_banner

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(sprite.actionFactory.createHideAdsBannerAction())
    }
}

class ShowAdsBannerBrick : BrickBaseType(), BrickSpinner.OnItemSelectedListener<StringOption> {

    private val positionObjectList = mutableListOf<Nameable>()
    private val sizeObjectList = mutableListOf<Nameable>()

    private var position: StringOption? = null
    private var size: StringOption? = null

    @Transient
    private lateinit var positionSpinner: BrickSpinner<StringOption>

    @Transient
    private lateinit var sizeSpinner: BrickSpinner<StringOption>

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createShowAdsBannerAction(
                resolveEnumPosition(position!!),
                resolveEnumSize(size!!)
            )
        )
    }

    private fun resolveEnumPosition(position: StringOption): AdsBannerPositionEnum {
        val index = positionObjectList.indexOf(position)
        return AdsBannerPositionEnum.values()[index]
    }

    private fun resolveEnumSize(position: StringOption): AdsBannerSizeEnum {
        val index = sizeObjectList.indexOf(position)
        return AdsBannerSizeEnum.values()[index]
    }

    override fun getViewResource() = R.layout.brick_show_ads_banner

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int, string: String) {
        when (spinnerId) {
            R.id.brick_show_ads_banner_position ->
                position = StringOption(string)
            R.id.brick_ads_show_banner_size ->
                size = StringOption(string)
        }
    }

    override fun onItemSelected(spinnerId: Int?, item: StringOption?) = Unit

    override fun getView(context: Context?): View {
        super.getView(context)

        positionObjectList.ifEmpty {
            view.context.resources.getStringArray(R.array.brick_ads_banner_position)
                .forEach {
                    positionObjectList.add(StringOption(it))
                }
        }

        if (position == null) {
            position = positionObjectList[0] as StringOption
        }

        with(
            BrickSpinner<StringOption>(
                R.id.brick_show_ads_banner_position,
                view,
                positionObjectList
            )
        ) {
            positionSpinner = this
            setOnItemSelectedListener(this@ShowAdsBannerBrick)
            setSelection(position)
        }

        sizeObjectList.ifEmpty {
            view.context.resources.getStringArray(R.array.brick_ads_banner_size)
                .forEach {
                    sizeObjectList.add(StringOption(it))
                }
        }

        if (size == null) {
            size = sizeObjectList[0] as StringOption
        }

        with(
            BrickSpinner<StringOption>(
                R.id.brick_ads_show_banner_size,
                view,
                sizeObjectList
            )
        ) {
            sizeSpinner = this
            setOnItemSelectedListener(this@ShowAdsBannerBrick)
            setSelection(size)
        }

        return view
    }

    override fun addRequiredResources(requiredResourcesSet: Brick.ResourcesSet) {
        requiredResourcesSet.add(Brick.NETWORK_CONNECTION)
        super.addRequiredResources(requiredResourcesSet)
    }

    override fun onEditOptionSelected(spinnerId: Int?) = Unit
}

enum class AdsBannerPositionEnum {
    TOP, BOTTOM
}

enum class AdsBannerSizeEnum {
    BANNER, SMART_BANNER, LARGE_BANNER
}
