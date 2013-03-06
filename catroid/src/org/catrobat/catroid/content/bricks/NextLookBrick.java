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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class NextLookBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient View view;

	public NextLookBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NextLookBrick() {

	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_next_look, null);
		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_next_look_text_view);
			textView.setText(R.string.brick_next_background);
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new NextLookBrick(sprite);
	}

	@Override
	public int getRequiredResources() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_next_look, null);
		}

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_next_look_text_view);
			textView.setText(R.string.brick_next_background);
		}

		return view;
	}

	@Override
<<<<<<< HEAD
	public void onClick(View view) {

=======
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.nextLook(sprite));
		return null;
>>>>>>> refs/remotes/origin/master
	}
}
