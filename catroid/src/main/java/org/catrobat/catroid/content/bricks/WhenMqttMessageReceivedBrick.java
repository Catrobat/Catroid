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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.MqttScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.content.bricks.brickspinner.UserVariableBrickTextInputDialogBuilder;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.UiUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WhenMqttMessageReceivedBrick extends ScriptBrickBaseType
		implements BrickSpinner.OnItemSelectedListener<UserVariable> {

	private static final long serialVersionUID = 1L;

	private MqttScript script;

	private transient BrickSpinner<UserVariable> payloadSpinner;
	private transient BrickSpinner<UserVariable> topicSpinner;

	public WhenMqttMessageReceivedBrick() {
		this(new MqttScript());
	}

	public WhenMqttMessageReceivedBrick(MqttScript script) {
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenMqttMessageReceivedBrick clone = (WhenMqttMessageReceivedBrick) super.clone();
		clone.script = (MqttScript) script.clone();
		clone.script.setScriptBrick(clone);
		clone.payloadSpinner = null;
		clone.topicSpinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_mqtt_message_received;
	}

	public List<String> getBoundVariableNames() {
		List<String> names = new ArrayList<>();
		if (script.getPayloadVariable() != null) {
			names.add(script.getPayloadVariable().getName());
		}
		if (script.getTopicVariable() != null) {
			names.add(script.getTopicVariable().getName());
		}
		return names;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		setupTopicField();
		setupVariableSpinners(context);
		return view;
	}

	private void setupVariableSpinners(Context context) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		// Sits directly after "New…" so it is what the spinner falls back to when the
		// bound variable is missing, instead of silently binding the first real one.
		items.add(new StringOption(context.getString(R.string.brick_when_mqtt_variable_unset)));
		items.addAll(sprite.getUserVariables());
		items.addAll(ProjectManager.getInstance().getCurrentProject().getUserVariables());
		items.addAll(ProjectManager.getInstance().getCurrentProject().getMultiplayerVariables());

		payloadSpinner = new BrickSpinner<>(R.id.brick_when_mqtt_payload_spinner, view,
				new ArrayList<>(items));
		payloadSpinner.setOnItemSelectedListener(this);
		payloadSpinner.setSelection(script.getPayloadVariable());

		topicSpinner = new BrickSpinner<>(R.id.brick_when_mqtt_topic_spinner, view,
				new ArrayList<>(items));
		topicSpinner.setOnItemSelectedListener(this);
		topicSpinner.setSelection(script.getTopicVariable());
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable UserVariable item) {
		if (spinnerId == R.id.brick_when_mqtt_payload_spinner) {
			script.setPayloadVariable(item);
		} else if (spinnerId == R.id.brick_when_mqtt_topic_spinner) {
			script.setTopicVariable(item);
		}
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}
		BrickSpinner<UserVariable> spinner =
				spinnerId == R.id.brick_when_mqtt_payload_spinner ? payloadSpinner : topicSpinner;
		UserVariable selected = spinnerId == R.id.brick_when_mqtt_payload_spinner
				? script.getPayloadVariable() : script.getTopicVariable();

		new UserVariableBrickTextInputDialogBuilder(
				ProjectManager.getInstance().getCurrentProject(),
				ProjectManager.getInstance().getCurrentSprite(),
				selected, activity, spinner).show();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
		// The spinner offers no edit option, so this is never called.
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		onItemSelected(spinnerId, null);
	}

	private void setupTopicField() {
		EditText topicField = view.findViewById(R.id.brick_when_mqtt_topic_edit_text);
		topicField.setText(script.getTopic());
		topicField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// No action needed before text changes.
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// No action needed during text changes; topic is updated in afterTextChanged.
			}

			@Override
			public void afterTextChanged(Editable editable) {
				script.setTopic(editable.toString());
			}
		});
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(MQTT_CONNECTION);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		// A script brick starts its script; it contributes no action of its own.
	}
}
