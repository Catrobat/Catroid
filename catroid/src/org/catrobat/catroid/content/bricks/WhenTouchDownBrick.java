/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenTouchDownScript;

import java.util.List;

public class WhenTouchDownBrick extends BrickBaseType implements ScriptBrick {
	protected WhenTouchDownScript whenTouchDownScript;
	private transient View prototypeView;
	private static final long serialVersionUID = 1L;
	private transient AdapterView<?> adapterView;

	public WhenTouchDownBrick() {
		this.whenTouchDownScript = new WhenTouchDownScript();
	}

	public WhenTouchDownBrick(WhenTouchDownScript script) {
		this.whenTouchDownScript = script;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		WhenTouchDownBrick copyBrick = (WhenTouchDownBrick) clone();

		copyBrick.whenTouchDownScript = whenTouchDownScript;
		return copyBrick;
	}

	@Override
	public Script getScriptSafe() {
		if (whenTouchDownScript == null) {
			setWhenTouchDownScript(new WhenTouchDownScript());
		}
		return whenTouchDownScript;
	}

	@Override
	public Brick clone() {
		return new WhenTouchDownBrick(new WhenTouchDownScript());
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		if (whenTouchDownScript == null) {
			whenTouchDownScript = new WhenTouchDownScript();
		}
		final Brick brickInstance = this;
		view = View.inflate(context, R.layout.brick_screen_touched, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_when_screen_touched_checkbox);
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {

			View layout = view.findViewById(R.id.brick_when_screen_touched_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textWhenTouchLabel = (TextView) view.findViewById(R.id.brick_when_screen_touched_label);
			textWhenTouchLabel.setTextColor(textWhenTouchLabel.getTextColors().withAlpha(alphaValue));
			ColorStateList color = textWhenTouchLabel.getTextColors().withAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_screen_touched, null);

		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	public WhenTouchDownScript getWhenTouchDownScript() {
		return whenTouchDownScript;
	}

	public void setWhenTouchDownScript(WhenTouchDownScript whenTouchDownScript) {
		this.whenTouchDownScript = whenTouchDownScript;
	}
}
