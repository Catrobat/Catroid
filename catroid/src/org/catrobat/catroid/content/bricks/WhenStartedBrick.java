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
import org.catrobat.catroid.content.StartScript;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class WhenStartedBrick extends ScriptBrick {
	private static final long serialVersionUID = 1L;

	private Script script;
	private Sprite sprite;

	private transient View view;

	public WhenStartedBrick(Sprite sprite, Script script) {
		this.script = script;
		this.sprite = sprite;
	}

	public WhenStartedBrick() {

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
	public View getView(Context context, int brickId, final BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_when_started, null);
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_when_started, null);
	}

	@Override
	public Brick clone() {
		return new WhenStartedBrick(getSprite(), null);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (script == null) {
			script = new StartScript(sprite);
		}

		return script;
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		return null;

	}
}
