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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@CatrobatLanguageBrick(command = "Jump Jumping Sumo")
public class JumpingSumoJumpLongBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private static final String TYPE_CATLANG_PARAMETER_NAME = "type";

	public JumpingSumoJumpLongBrick() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_jumping_sumo_jump_long;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(TYPE_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, "long");
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(TYPE_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}
}
