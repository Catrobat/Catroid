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

package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenMqttMessageReceivedBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.MqttEventId;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class MqttScript extends Script {

	private static final long serialVersionUID = 1L;

	private String topic;
	private UserVariable payloadVariable;
	private UserVariable topicVariable;

	public MqttScript() {
		this("");
	}

	public MqttScript(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public UserVariable getPayloadVariable() {
		return payloadVariable;
	}

	public void setPayloadVariable(UserVariable payloadVariable) {
		this.payloadVariable = payloadVariable;
	}

	public UserVariable getTopicVariable() {
		return topicVariable;
	}

	public void setTopicVariable(UserVariable topicVariable) {
		this.topicVariable = topicVariable;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (scriptBrick == null) {
			scriptBrick = new WhenMqttMessageReceivedBrick(this);
		}
		return scriptBrick;
	}

	@Override
	public void addRequiredResources(final Brick.ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(Brick.MQTT_CONNECTION);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		return new MqttEventId(topic);
	}
}
