/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public abstract class DroneBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	@Override
	public abstract Brick clone();

	@Override
	public abstract Brick copyBrickForSprite(Sprite sprite, Script script);

	@Override
	public abstract List<SequenceAction> addActionToSequence(SequenceAction sequence);

	@Override
	public abstract View getView(Context context, int brickId, BaseAdapter baseAdapter);

	@Override
	public abstract View getPrototypeView(Context context);

	@Override
	public abstract View getViewWithAlpha(int alphaValue);

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}
}
