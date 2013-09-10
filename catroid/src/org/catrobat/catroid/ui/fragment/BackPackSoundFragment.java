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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.adapter.BackPackSoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.SoundFragment.OnSoundInfoListChangedAfterNewListener;

public class BackPackSoundFragment extends BackPackActivityFragment implements SoundBaseAdapter.OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = BackPackSoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private ActionBar actionBar;

	private MediaPlayer mediaPlayer;
	private BackPackSoundAdapter adapter;
	private SoundInfo selectedSoundInfoBackPack;

	private ListView listView;

	private boolean isResultHandled = false;

	private OnSoundInfoListChangedAfterNewListener soundInfoListChangedAfterNewListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d("TAG", "BackPackSoundFragment created!");

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		adapter = new BackPackSoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				R.id.fragment_sound_item_title_text_view, BackPackListManager.getInstance().getSoundInfoArrayList(),
				false);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		menu.findItem(R.id.edit_in_pocket_paint).setVisible(false);

		BottomBar.hideBottomBar(getActivity());
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

	public View getView(int position, View convertView) {

		Log.d("TAG", "@BackPackSoundFragment-->getView()");

		SoundViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(getActivity(), R.layout.fragment_sound_soundlist_item, null);

			holder = new SoundViewHolder();
			holder.playButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_play_image_button);
			holder.pauseButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_pause_image_button);

			holder.playButton.setVisibility(Button.VISIBLE);
			holder.pauseButton.setVisibility(Button.GONE);

			holder.soundFragmentButtonLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_sound_item_main_linear_layout);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_sound_item_checkbox);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_title_text_view);
			holder.timeSeperatorTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_time_seperator_text_view);
			holder.timeDurationTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_duration_text_view);
			holder.soundFileSizePrefixTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_size_prefix_text_view);
			holder.soundFileSizeTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_size_text_view);

			holder.timePlayedChronometer = (Chronometer) convertView
					.findViewById(R.id.fragment_sound_item_time_played_chronometer);

			convertView.setTag(holder);
		} else {
			holder = (SoundViewHolder) convertView.getTag();
		}
		SoundController controller = SoundController.getInstance();
		Log.v("Adapter *********", controller.toString());
		controller.updateSoundLogic(position, holder, adapter);

		return convertView;
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public void onSoundPlay(View view) {

	}

	@Override
	public void onSoundPause(View view) {

	}

	@Override
	public void onSoundChecked() {

	}

	@Override
	public boolean getShowDetails() {
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {

	}

	@Override
	public void setSelectMode(int selectMode) {

	}

	@Override
	public int getSelectMode() {
		return 0;
	}

	@Override
	public void startDeleteActionMode() {

	}

	@Override
	protected void showDeleteDialog() {

	}

	public BackPackSoundAdapter getAdapter() {
		return adapter;
	}
}
