/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.view.View;
import android.view.View.OnClickListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.List;

public class UserBrick extends BrickBaseType implements OnClickListener {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("definitionBrick")
	private UserScriptDefinitionBrick definitionBrick;

	@XStreamAlias("userBrickParameters")
	@SuppressWarnings("unused")
	private List<UserBrickParameter> userBrickParameters = new ArrayList<>();

	public UserBrick() {
		this.definitionBrick = new UserScriptDefinitionBrick();
	}

	public UserBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		definitionBrick.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user;
	}

	@Override
	public void onClick(View eventOrigin) {
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}
}
