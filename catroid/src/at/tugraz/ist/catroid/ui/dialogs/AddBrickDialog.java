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
package at.tugraz.ist.catroid.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.gui.Brick;
import at.tugraz.ist.catroid.content.brick.gui.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.gui.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.brick.gui.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.adapter.AddBrickAdapter;

public class AddBrickDialog extends Dialog {

	private Animation slideInAnimation;
	private Animation slideOutAnimation;
	private AddBrickAdapter adapter;
	private LinearLayout layout;

	private List<Brick> prototypeBrickList;
	private ListView listView;

	private void setupBrickPrototypes() {
		prototypeBrickList = new ArrayList<Brick>();
		prototypeBrickList.add(new PlaySoundBrick(""));
		prototypeBrickList.add(new WaitBrick(1000));
		prototypeBrickList.add(new HideBrick(null));
		prototypeBrickList.add(new ShowBrick(null));
		prototypeBrickList.add(new PlaceAtBrick(null, 100, 200));
//		brickList.add(new SetCostumeBrick(null));
		prototypeBrickList.add(new GoNStepsBackBrick(null, 1));
		prototypeBrickList.add(new ComeToFrontBrick(null, null));
		prototypeBrickList.add(new IfTouchedBrick(null, new Script()));
	}

    public AddBrickDialog(ScriptActivity scriptActivity) {
		super(scriptActivity);
		setupBrickPrototypes();

		// adjust window
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
		setContentView(R.layout.dialog_toolbox);

		// initialize animations
		slideInAnimation = AnimationUtils.loadAnimation(scriptActivity, R.anim.toolbox_in);
		slideOutAnimation = AnimationUtils.loadAnimation(scriptActivity, R.anim.toolbox_out);
		slideOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				close();
			}
		});

		layout = (LinearLayout) findViewById(R.id.toolbox_layout);
		listView = (ListView) findViewById(R.id.toolboxListView);
		listView.setAdapter(new AddBrickAdapter(scriptActivity, prototypeBrickList));
		listView.setOnItemClickListener(scriptActivity);

	}

	@Override
	public void show() {
		super.show();
		layout.startAnimation(slideInAnimation);
	}

	@Override
	public void cancel() {
		layout.startAnimation(slideOutAnimation);

	}

	private void close() {
		super.cancel();
	}

	public Brick getBrickClone(View v) {
		return adapter.getItem(listView.getPositionForView(v)).clone();
	}

}
