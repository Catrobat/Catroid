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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;import java.util.List;

public class IfOnEdgeBounceBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient View view;

	public IfOnEdgeBounceBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public IfOnEdgeBounceBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		IfOnEdgeBounceBrick copyBrick = (IfOnEdgeBounceBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
		}

		return view;
	}

	@Override
	public Brick clone() {
		return new IfOnEdgeBounceBrick(sprite);
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.ifOnEdgeBounce(sprite));
		return null;
	}

}
