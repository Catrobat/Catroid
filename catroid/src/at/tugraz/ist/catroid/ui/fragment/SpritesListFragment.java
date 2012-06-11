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
package at.tugraz.ist.catroid.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.SpriteAdapter;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class SpritesListFragment extends SherlockListFragment {

    private SpriteAdapter spriteAdapter;
    private ArrayList<Sprite> spriteList;
    private Sprite spriteToEdit;
    private CustomIconContextMenu iconContextMenu;

    private ActionBar actionBar;

    private static final int CONTEXT_MENU_ITEM_RENAME = 0; // or R.id.project_menu_rename
    private static final int CONTEXT_MENU_ITEM_DELETE = 1; // or R.id.project_menu_delete
    public static final int DIALOG_NEW_SPRITE = 0;
    public static final int DIALOG_RENAME_SPRITE = 1;
    private static final int DIALOG_CONTEXT_MENU = 2;
    
    private static final String STATE_SELECTED_SPRITE = "selected_sprite";

    private void initListeners() {
        spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
        spriteAdapter = new SpriteAdapter(getActivity(), 
        		R.layout.activity_project_spritelist_item, R.id.sprite_title, spriteList);

        setListAdapter(spriteAdapter);
        getListView().setTextFilterEnabled(true);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectManager.getInstance().setCurrentSprite(spriteAdapter.getItem(position));
                Intent intent = new Intent(getActivity(), ScriptTabActivity.class);
                startActivity(intent);
            }
        });
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                spriteToEdit = spriteList.get(position);

                // as long as background sprite is always the first one, we're
                // fine
                if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
                    return true;
                }

                getActivity().removeDialog(DIALOG_CONTEXT_MENU);
                getActivity().showDialog(DIALOG_CONTEXT_MENU);
                return true;
            }
        });
    }

    private void initCustomContextMenu() {
        Resources resources = getResources();
        iconContextMenu = new CustomIconContextMenu(getActivity(), DIALOG_CONTEXT_MENU);
        iconContextMenu.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
                CONTEXT_MENU_ITEM_RENAME);
        iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
                CONTEXT_MENU_ITEM_DELETE);

        iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
            @Override
            public void onClick(int menuId) {
                switch (menuId) {
                    case CONTEXT_MENU_ITEM_RENAME:
                        getActivity().showDialog(DIALOG_RENAME_SPRITE);
                        break;
                    case CONTEXT_MENU_ITEM_DELETE:
                        ProjectManager projectManager = ProjectManager.getInstance();
                        projectManager.getCurrentProject().getSpriteList().remove(spriteToEdit);
                        if (projectManager.getCurrentSprite() != null
                                && projectManager.getCurrentSprite().equals(spriteToEdit)) {
                            projectManager.setCurrentSprite(null);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_sprites_list, null);
    	return rootView;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	final Sprite savedSelectedSprite = spriteToEdit;
    	outState.putSerializable(STATE_SELECTED_SPRITE, savedSelectedSprite);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	String title = this.getResources().getString(R.string.project_name) + " "
                + ProjectManager.getInstance().getCurrentProject().getName();
    	actionBar = getSherlockActivity().getSupportActionBar();
    	actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        
        Utils.loadProjectIfNeeded(getActivity());
    }
    
    @Override
    public void onStart() {
        super.onStart();
        initListeners();
        initCustomContextMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.menu_current_project, menu);
      super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.checkForSdCard(getActivity())) {
            return;
        }
        spriteAdapter.notifyDataSetChanged();
    }

    public Sprite getSpriteToEdit() {
        return spriteToEdit;
    }

    @Override
    public void onPause() {
        super.onPause();
        ProjectManager projectManager = ProjectManager.getInstance();
        if (projectManager.getCurrentProject() != null) {
            projectManager.saveProject();
        }
    }

    public void notifySpriteAdapter() {
    	spriteAdapter.notifyDataSetChanged();
    }
    
    public void handleProjectActivityItemLongClick(View view) {}
}
