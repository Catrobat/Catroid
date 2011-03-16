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

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
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
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;

public class AddBrickDialog extends Dialog {

    //	private Animation slideInAnimation;
    //	private Animation slideOutAnimation;
    //private LinearLayout layout;
    private ArrayList<Brick> prototypeBrickList;
	private ListView listView;
    private BrickAdapter adapter;

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
        //		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //		getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_toolbox);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        //		// initialize animations
        //		slideInAnimation = AnimationUtils.loadAnimation(scriptActivity, R.anim.toolbox_in);
        //		slideOutAnimation = AnimationUtils.loadAnimation(scriptActivity, R.anim.toolbox_out);
        //		slideOutAnimation.setAnimationListener(new AnimationListener() {
        //			public void onAnimationStart(Animation animation) {
        //                System.out.println("##############animation start");
        //			}
        //
        //			public void onAnimationRepeat(Animation animation) {
        //                System.out.println("##############animation rep");
        //			}
        //
        //			public void onAnimationEnd(Animation animation) {
        //                System.out.println("##############animation end");
        //				close();
        //			}
        //		});

        adapter = new BrickAdapter(scriptActivity, prototypeBrickList);
        adapter.isToolboxAdapter = true;
        //layout = (LinearLayout) findViewById(R.id.toolbox_layout);
		listView = (ListView) findViewById(R.id.toolboxListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("############################ clicking the item!!!");
                ProjectManager.getInstance().addBrick(getBrickClone(adapter.getItem(position)));
                dismiss();
            }
        });
	}

    //	@Override
    //	public void show() {
    //		super.show();
    //		layout.startAnimation(slideInAnimation);
    //	}

    //	@Override
    //	public void cancel() {
    //		layout.startAnimation(slideOutAnimation);
    //	}

    //	private void close() {
    //		super.cancel();
    //	}

    public Brick getBrickClone(Brick brick) {
        return brick.clone();
    }
}
