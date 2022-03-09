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
package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.NewOption
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface
import org.koin.java.KoinJavaComponent.inject

class StopSoundBrick : BrickBaseType(),
    BrickSpinner.OnItemSelectedListener<SoundInfo>, NewItemInterface<SoundInfo> {

    var sound: SoundInfo? = null

    @Transient
    private lateinit var spinner: BrickSpinner<SoundInfo>

    override fun getViewResource() = R.layout.brick_stop_sound

    override fun getView(context: Context): View {
        super.getView(context)

        val items = mutableListOf<Nameable>(NewOption(context.getString(R.string.new_option)))
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        items.addAll(projectManager.currentSprite.soundList)
        with(BrickSpinner<SoundInfo>(R.id.brick_stop_sound_spinner, view, items)) {
            spinner = this
            setOnItemSelectedListener(this@StopSoundBrick)
            setSelection(sound)
        }
        return view
    }

    override fun onNewOptionSelected(spinnerId: Int) {
        (UiUtils.getActivityFromView(view) as? SpriteActivity)?.apply {
            registerOnNewSoundListener(this@StopSoundBrick)
            handleAddSoundButton()
        }
    }

    override fun onEditOptionSelected(spinnerId: Int) = Unit

    override fun addItem(item: SoundInfo) {
        spinner.add(item)
        spinner.setSelection(item)
    }

    override fun onStringOptionSelected(spinnerId: Int, string: String) = Unit

    override fun onItemSelected(spinnerId: Int, item: SoundInfo?) {
        sound = item
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(sprite.actionFactory.createStopSoundAction(sprite, sound))
    }
}
