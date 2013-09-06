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
package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.BackPackSoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.SoundFragment.OnSoundInfoListChangedAfterNewListener;

import java.util.ArrayList;

public class BackPackSoundFragment extends BackPackActivityFragment implements SoundBaseAdapter.OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = BackPackSoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private ActionBar actionBar;

	private MediaPlayer mediaPlayer;
	private BackPackSoundAdapter adapter;
	private ArrayList<SoundInfo> soundInfoListBackPack;
	private SoundInfo selectedSoundInfoBackPack;

	private BackPackListManager backPackListManagerInstance;

	private ListView listView;

	private boolean isResultHandled = false;

	private OnSoundInfoListChangedAfterNewListener soundInfoListChangedAfterNewListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d("TAG", "BackPackSoundFragment created!");

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		backPackListManagerInstance = BackPackListManager.getInstance();

		BackPackSoundFragment backPackSoundFragment = backPackListManagerInstance.getBackPackSoundFragment();

		Activity bpSoundActivityFragmentActivity = backPackSoundFragment.getActivity();

		if (bpSoundActivityFragmentActivity == null) {
			Log.d("TAG", "Activity is null!");
		}

		if (getActivity() == null) {
			Log.d("TAG", "Activity is null!");
		}

		adapter = new BackPackSoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				backPackListManagerInstance.getSoundInfoArrayList(), false);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);
		adapter.setBackPackSoundActivity(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("TAG", "BackPackSoundFragment-->onCreateView()");
		View rootView = inflater.inflate(R.layout.sound_list, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.d("TAG", "BackPackSoundFragment-->onActivityCreated()");

		listView = getListView();
		registerForContextMenu(listView);

		if (savedInstanceState != null) {
			selectedSoundInfoBackPack = (SoundInfo) savedInstanceState
					.getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}
		soundInfoListBackPack = BackPackListManager.getInstance().getSoundInfoArrayList();

		adapter = new BackPackSoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				soundInfoListBackPack, false);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(true);
		menu.findItem(R.id.edit_in_pocket_paint).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, selectedSoundInfoBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
		initClickListener();
	}

	private void initClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedSoundPosition = position;
				return false;
			}
		});
	}

	public BackPackSoundAdapter getBackPackSoundAdapter() {
		return adapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnKeyListener#onKey(android.content.DialogInterface, int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader,
	 * java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundPlay(android.view.View)
	 */
	@Override
	public void onSoundPlay(View view) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundPause(android.view.View)
	 */
	@Override
	public void onSoundPause(View view) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundChecked()
	 */
	@Override
	public void onSoundChecked() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#getShowDetails()
	 */
	@Override
	public boolean getShowDetails() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#setShowDetails(boolean)
	 */
	@Override
	public void setShowDetails(boolean showDetails) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#setSelectMode(int)
	 */
	@Override
	public void setSelectMode(int selectMode) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#getSelectMode()
	 */
	@Override
	public int getSelectMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#startDeleteActionMode()
	 */
	@Override
	public void startDeleteActionMode() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.BackPackActivityFragment#showDeleteDialog()
	 */
	@Override
	protected void showDeleteDialog() {
		// TODO Auto-generated method stub

	}
}
