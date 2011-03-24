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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;

public class AddBrickDialog extends Dialog {

    private ArrayList<Brick> prototypeBrickList;
    private ListView listView;
    private BrickAdapter adapter;

    private void setupBrickPrototypes(Sprite sprite) {
        prototypeBrickList = new ArrayList<Brick>();
        prototypeBrickList.add(new PlaySoundBrick(""));
        prototypeBrickList.add(new WaitBrick(1000));
        prototypeBrickList.add(new HideBrick(sprite));
        prototypeBrickList.add(new ShowBrick(sprite));
        prototypeBrickList.add(new PlaceAtBrick(sprite, 100, 200));
        prototypeBrickList.add(new SetCostumeBrick(sprite));
        prototypeBrickList.add(new ScaleCostumeBrick(sprite, 100));
        prototypeBrickList.add(new GoNStepsBackBrick(sprite, 1));
        prototypeBrickList.add(new ComeToFrontBrick(sprite, null));
        prototypeBrickList.add(new IfTouchedBrick(sprite, new Script()));
    }

    public AddBrickDialog(ScriptActivity scriptActivity, Sprite sprite) {
        super(scriptActivity);
        setupBrickPrototypes(sprite);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_toolbox);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        adapter = new BrickAdapter(scriptActivity, prototypeBrickList);
        adapter.isToolboxAdapter = true;

        listView = (ListView) findViewById(R.id.toolboxListView);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectManager.getInstance().addBrick(getBrickClone(adapter.getItem(position)));
                dismiss();
            }
        });
    }

    public Brick getBrickClone(Brick brick) {
        return brick.clone();
    }
}
