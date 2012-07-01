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

import android.app.Activity;
import android.content.Intent;
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
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.SpriteAdapter;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class SpritesListFragment extends SherlockListFragment {
	
    private SpriteAdapter spriteAdapter;
    private ArrayList<Sprite> spriteList;

    private ActionBar actionBar;
    private OnSpriteToEditSelectedListener onSpriteToEditSelectedListener;

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
                Sprite spriteToEdit = spriteList.get(position);

                // as long as background sprite is always the first one, we're fine
                if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
                    return true;
                }
                
                if (onSpriteToEditSelectedListener != null) {
                	onSpriteToEditSelectedListener.onSpriteToEditSelected(spriteToEdit);
                }
                
                getActivity().removeDialog(ProjectActivity.DIALOG_CONTEXT_MENU);
                getActivity().showDialog(ProjectActivity.DIALOG_CONTEXT_MENU);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
    	try {
    		onSpriteToEditSelectedListener = (OnSpriteToEditSelectedListener) activity;
    	} catch (ClassCastException ex) {
    		throw new IllegalStateException(activity.getClass().getSimpleName()
                    + " does not implement SpriteListFragment's OnSpriteToEditListener interface.", ex);
    	}
    	
    	super.onAttach(activity);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_sprites_list, null);
    	return rootView;
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
    
    public interface OnSpriteToEditSelectedListener {
    	
    	public void onSpriteToEditSelected(Sprite sprite);
    	
    }
}
