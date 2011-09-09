/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

public class LoopEndBrick extends Brick {
	public static final int FOREVER = -1;
	public static final long LOOP_DELAY = 2000;
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private LoopBeginBrick loopBeginBrick;
	private int timesToRepeat;

	public LoopEndBrick(Sprite sprite, LoopBeginBrick loopStartingBrick) {
		super.brickBehavior = Brick.IS_LOOP_END_BRICK;
		this.sprite = sprite;
		this.loopBeginBrick = loopStartingBrick;
	}

	@Override
	public void execute() {

		if (timesToRepeat > 0) {

			long loopBeginTime = loopBeginBrick.getBeginLoopTime() / 1000000;
			long loopEndTime = System.nanoTime() / 1000000;
			long waitForNextLoop = (LOOP_DELAY - (loopEndTime - loopBeginTime));
			//Log.i("bt", loopBeginTime + " " + loopEndTime + " time to wait til next loop: " + waitForNextLoop);
			if (waitForNextLoop > 0) {
				try {
					Thread.sleep(waitForNextLoop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			loopBeginBrick.setBeginLoopTime(System.nanoTime());
		}

		if (timesToRepeat == FOREVER) {
			Script script = getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopBeginBrick));
		} else if (timesToRepeat > 0) {
			Script script = getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopBeginBrick));
			timesToRepeat--;
		}
	}

	private Script getScript() {
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
	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_loop_end, null);
	}

	@Override
	public Brick clone() {
		return new LoopEndBrick(getSprite(), getLoopBeginBrick());
	}

	@Override
	public View getPrototypeView(Context context) {
		return null;
	}
}