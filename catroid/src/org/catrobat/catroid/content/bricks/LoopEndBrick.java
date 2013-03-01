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

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class LoopEndBrick extends NestingBrick implements AllowedAfterDeadEndBrick {
	static final int FOREVER = -1;
	private static final int LOOP_DELAY = 20;
	private static final int MILLION = 1000 * 1000;
	private static final long serialVersionUID = 1L;
	private static final String TAG = LoopEndBrick.class.getSimpleName();
	private Sprite sprite;
	private LoopBeginBrick loopBeginBrick;
	private transient int timesToRepeat;
	private transient CheckBox checkbox;
	private transient View view;
	private transient boolean checked;

	public LoopEndBrick(Sprite sprite, LoopBeginBrick loopStartingBrick) {
		this.sprite = sprite;
		this.loopBeginBrick = loopStartingBrick;
		loopStartingBrick.setLoopEndBrick(this);
	}

	public LoopEndBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		loopBeginBrick.setBeginLoopTime(System.nanoTime());

		if (timesToRepeat == FOREVER) {
			Script script = getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopBeginBrick));
		} else if (--timesToRepeat > 0) {
			Script script = getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopBeginBrick));
		}

		long loopBeginTime = loopBeginBrick.getBeginLoopTime() / MILLION;
		long loopEndTime = System.nanoTime() / MILLION;
		long waitForNextLoop = (LOOP_DELAY - (loopEndTime - loopBeginTime));
		if (waitForNextLoop > 0) {
			try {
				Thread.sleep(waitForNextLoop);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected Script getScript() {
		for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
			Script script = sprite.getScript(i);
			if (script.getBrickList().contains(this)) {
				return script;
			}
		}
		return null;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	public void setTimesToRepeat(int timesToRepeat) {
		this.timesToRepeat = timesToRepeat;
	}

	public LoopBeginBrick getLoopBeginBrick() {
		return loopBeginBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.brick_loop_end, null);
			checkbox = (CheckBox) view.findViewById(R.id.brick_loop_end_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}
		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_loop_end_layout);
		if (layout == null) {
			layout = (LinearLayout) view.findViewById(R.id.brick_loop_end_no_puzzle_layout);
		}
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new LoopEndBrick(getSprite(), getLoopBeginBrick());
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_loop_end, null);
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == loopBeginBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isInitialized() {
		if (loopBeginBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		loopBeginBrick = new ForeverBrick(sprite);
		Log.w(TAG, "Not supposed to create the LoopBeginBrick!");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(this);
		nestingBrickList.add(loopBeginBrick);

		return nestingBrickList;
	}

	@Override
	public View getNoPuzzleView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_loop_end_no_puzzle, null);
	}

	@Override
	public void setCheckboxVisibility(int visibility) {

		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}
}
