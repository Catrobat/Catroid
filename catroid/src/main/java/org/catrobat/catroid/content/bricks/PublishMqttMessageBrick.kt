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

package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.formulaeditor.Formula

class PublishMqttMessageBrick() : FormulaBrick() {

    private var qos = 0
    private var retained = false

    init {
        addAllowedBrickField(BrickField.MQTT_MESSAGE, R.id.brick_publish_mqtt_message_edit_text)
        addAllowedBrickField(BrickField.MQTT_TOPIC, R.id.brick_publish_mqtt_topic_edit_text)
    }

    constructor(message: String, topic: String) : this(Formula(message), Formula(topic))

    constructor(message: Formula, topic: Formula) : this() {
        setFormulaWithBrickField(BrickField.MQTT_MESSAGE, message)
        setFormulaWithBrickField(BrickField.MQTT_TOPIC, topic)
    }

    override fun getViewResource(): Int = R.layout.brick_publish_mqtt_message

    override fun getDefaultBrickField(): BrickField = BrickField.MQTT_MESSAGE

    override fun getView(context: Context): View {
        super.getView(context)
        setupQosSpinner(context)
        setupRetainedCheckBox()
        return view
    }

    private fun setupQosSpinner(context: Context) {
        val spinner = view.findViewById<Spinner>(R.id.brick_publish_mqtt_qos_spinner)
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            QOS_LEVELS.map { it.toString() }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(qos.coerceIn(QOS_LEVELS.first(), QOS_LEVELS.last()), true)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                qos = QOS_LEVELS[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupRetainedCheckBox() {
        val checkBox = view.findViewById<CheckBox>(R.id.brick_publish_mqtt_retained_checkbox)
        checkBox.isChecked = retained
        checkBox.setOnCheckedChangeListener { _, isChecked -> retained = isChecked }
    }

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        requiredResourcesSet.add(Brick.MQTT_CONNECTION)
        super.addRequiredResources(requiredResourcesSet)
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createPublishMqttMessageAction(
                sprite,
                sequence,
                getFormulaWithBrickField(BrickField.MQTT_TOPIC),
                getFormulaWithBrickField(BrickField.MQTT_MESSAGE),
                qos,
                retained
            )
        )
    }

    companion object {
        private val QOS_LEVELS = listOf(0, 1, 2)
    }
}
