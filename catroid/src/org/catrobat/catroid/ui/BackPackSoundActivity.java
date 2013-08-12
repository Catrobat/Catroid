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
package org.catrobat.catroid.ui;

import java.util.ArrayList;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment.OnSoundInfoListChangedAfterNewListener;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class BackPackSoundActivity extends SherlockFragmentActivity implements OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = SoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private ActionBar actionBar;

	private MediaPlayer mediaPlayer;
	private SoundAdapter adapter;
	private ArrayList<SoundInfo> soundInfoList;
	private SoundInfo selectedSoundInfo;

	private ListView listView;

	private boolean isResultHandled = false;

	private OnSoundInfoListChangedAfterNewListener soundInfoListChangedAfterNewListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sound_list);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

		}

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.backpack);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSoundPlay(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSoundPause(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSoundChecked() {
		// TODO Auto-generated method stub

	}

	//	@SuppressLint("NewApi")
	//	@Override
	//	public void onCreate(Bundle savedInstanceState) {
	//
	//		Log.d("TAG", "BackPackSoundActivity --> onCreate()");
	//
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.sound_list);
	//
	//		android.app.ActionBar actionBar = this.getActionBar();
	//
	//		actionBar.setTitle("Backpack");
	//		actionBar.setHomeButtonEnabled(true);
	//		actionBar.setDisplayShowTitleEnabled(true);
	//
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//
	//		Log.d("TAG", "onOptionsItemSelected");
	//
	//		switch (item.getItemId()) {
	//			case android.R.id.home:
	//				Intent intent = new Intent(this, MainMenuActivity.class);
	//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//				startActivity(intent);
	//				return true;
	//			default:
	//				return super.onOptionsItemSelected(item);
	//
	//		}
	//
	//	}
	//
	//	public void onActivityCreated(Bundle savedInstanceState) {
	//		//super.onActivityCreated(savedInstanceState);
	//
	//		Log.d("TAG", "BackPackSoundActivity --> onActivityCreated");
	//
	//		//		listView = getListView();
	//		//		registerForContextMenu(listView);
	//		//
	//		//		if (savedInstanceState != null) {
	//		//			selectedSoundInfo = (SoundInfo) savedInstanceState
	//		//					.getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
	//		//		}
	//		//		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
	//		//
	//		//		adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item, soundInfoList, false);
	//		//		adapter.setOnSoundEditListener(this);
	//		//		setListAdapter(adapter);
	//		//		adapter.setSoundFragment(this);
	//		//
	//		//		Utils.loadProjectIfNeeded(getActivity());
	//		//		setHandleAddbutton();
	//
	//	}
	//
	//	public void onPrepareOptionMenu(Menu menu) {
	//		Log.d("TAG", "BackPackSoundAdapter-->onPrepareOptionsMenu");
	//		menu.findItem(R.id.delete).setVisible(true);
	//		menu.findItem(R.id.edit_in_pocket_paint).setVisible(false);
	//		super.onPrepareOptionsMenu(menu);
	//	}
	//
	//	@Override
	//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	//		super.onCreateContextMenu(menu, v, menuInfo);
	//
	//		if (SoundController.getInstance().isSoundPlaying(mediaPlayer)) {
	//			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
	//		}
	//		selectedSoundInfo = adapter.getItem(selectedSoundPosition);
	//		menu.setHeaderTitle(selectedSoundInfo.getTitle());
	//		adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);
	//
	//		//getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
	//		//menu.findItem(R.id.context_menu_copy).setVisible(true);
	//	}
	//
	//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	//		View rootView = inflater.inflate(R.layout.sound_list, null);
	//		return rootView;
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see android.content.DialogInterface.OnKeyListener#onKey(android.content.DialogInterface, int,
	//	 * android.view.KeyEvent)
	//	 */
	//	@Override
	//	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	//
	//		return false;
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	//	 */
	//	@Override
	//	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader,
	//	 * java.lang.Object)
	//	 */
	//	@Override
	//	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	//	 */
	//	@Override
	//	public void onLoaderReset(Loader<Cursor> arg0) {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundPlay(android.view.View)
	//	 */
	//	@Override
	//	public void onSoundPlay(View view) {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundPause(android.view.View)
	//	 */
	//	@Override
	//	public void onSoundPause(View view) {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener#onSoundChecked()
	//	 */
	//	@Override
	//	public void onSoundChecked() {
	//		// TODO Auto-generated method stub
	//
	//	}

	// old begins	
	/*
	 * public void setOnSoundInfoListChangedAfterNewListener(OnSoundInfoListChangedAfterNewListener listener) {
	 * soundInfoListChangedAfterNewListener = listener;
	 * }
	 * 
	 * @Override
	 * public void onCreate(Bundle savedInstanceState) {
	 * 
	 * Log.d("TAG", "BackPackActivity created!");
	 * 
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.sound_list);
	 * //setHasOptionsMenu(true);
	 * }
	 * 
	 * public View onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
	 * View rootView = inflater.inflate(R.layout.sound_list, null);
	 * return rootView;
	 * }
	 * 
	 * public void onActivityCreated(Bundle savedInstanceState) {
	 * //super.onActivityCreated(savedInstanceState);
	 * 
	 * // listView = getListView();
	 * // registerForContextMenu(listView);
	 * //
	 * // if (savedInstanceState != null) {
	 * // selectedSoundInfo = (SoundInfo) savedInstanceState
	 * // .getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
	 * // }
	 * // soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
	 * //
	 * // adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item, soundInfoList, false);
	 * // adapter.setOnSoundEditListener(this);
	 * // setListAdapter(adapter);
	 * // adapter.setBackPackSoundActivity(this);
	 * //
	 * // Utils.loadProjectIfNeeded(getActivity());
	 * // setHandleAddbutton();
	 * 
	 * }
	 * 
	 * public void onPrepareOptionsMenu(Menu menu) {
	 * menu.findItem(R.id.copy).setVisible(true);
	 * menu.findItem(R.id.edit_in_pocket_paint).setVisible(false);
	 * super.onPrepareOptionsMenu((android.view.Menu) menu);
	 * }
	 * 
	 * @Override
	 * public void onSaveInstanceState(Bundle outState) {
	 * outState.putSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, selectedSoundInfo);
	 * super.onSaveInstanceState(outState);
	 * }
	 * 
	 * @Override
	 * public void onStart() {
	 * super.onStart();
	 * mediaPlayer = new MediaPlayer();
	 * initClickListener();
	 * }
	 * 
	 * @Override
	 * public void onResume() {
	 * super.onResume();
	 * 
	 * // if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
	 * // return;
	 * // }
	 * //
	 * // SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
	 * // .getApplicationContext());
	 * //
	 * // setShowDetails(settings.getBoolean(SoundController.SHARED_PREFERENCE_NAME, false));
	 * }
	 * 
	 * @Override
	 * public void onPause() {
	 * super.onPause();
	 * 
	 * ProjectManager projectManager = ProjectManager.getInstance();
	 * if (projectManager.getCurrentProject() != null) {
	 * projectManager.saveProject();
	 * }
	 * SoundController.getInstance().stopSound(mediaPlayer, soundInfoList);
	 * adapter.notifyDataSetChanged();
	 * 
	 * SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	 * SharedPreferences.Editor editor = settings.edit();
	 * 
	 * editor.putBoolean(SoundController.SHARED_PREFERENCE_NAME, getShowDetails());
	 * editor.commit();
	 * 
	 * }
	 * 
	 * @Override
	 * public void onStop() {
	 * super.onStop();
	 * mediaPlayer.reset();
	 * mediaPlayer.release();
	 * mediaPlayer = null;
	 * }
	 * 
	 * @Override
	 * public void onSoundPlay(View view) {
	 * SoundController.getInstance().handlePlaySoundButton(view, soundInfoList, mediaPlayer, adapter);
	 * }
	 * 
	 * @Override
	 * public void onSoundPause(View view) {
	 * handlePauseSoundButton(view);
	 * }
	 * 
	 * @Override
	 * public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
	 * return SoundController.getInstance().onCreateLoader(id, arguments, this);
	 * }
	 * 
	 * @Override
	 * public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	 * // CopyAudioFilesTask task = new CopyAudioFilesTask();
	 * // String audioPath = SoundController.getInstance().onLoadFinished(loader, data, this);
	 * // if (!audioPath.isEmpty()) {
	 * // task.execute(audioPath);
	 * // getLoaderManager().destroyLoader(SoundController.ID_LOADER_MEDIA_IMAGE);
	 * // }
	 * //
	 * // this.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	 * //
	 * // isResultHandled = true;
	 * }
	 * 
	 * @Override
	 * public void onLoaderReset(Loader<Cursor> loader) {
	 * }
	 * 
	 * public void handlePauseSoundButton(View view) {
	 * final int position = (Integer) view.getTag();
	 * pauseSound(soundInfoList.get(position));
	 * adapter.notifyDataSetChanged();
	 * }
	 * 
	 * public void pauseSound(SoundInfo soundInfo) {
	 * mediaPlayer.pause();
	 * soundInfo.isPlaying = false;
	 * }
	 * 
	 * @Override
	 * public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	 * super.onCreateContextMenu(menu, v, menuInfo);
	 * 
	 * if (SoundController.getInstance().isSoundPlaying(mediaPlayer)) {
	 * SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
	 * }
	 * selectedSoundInfo = adapter.getItem(selectedSoundPosition);
	 * menu.setHeaderTitle(selectedSoundInfo.getTitle());
	 * adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);
	 * 
	 * //getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
	 * menu.findItem(R.id.context_menu_copy).setVisible(true);
	 * }
	 * 
	 * private void updateSoundAdapter(SoundInfo newSoundInfo) {
	 * 
	 * // if (soundInfoListChangedAfterNewListener != null) {
	 * // soundInfoListChangedAfterNewListener.onSoundInfoListChangedAfterNew(newSoundInfo);
	 * // }
	 * // //scroll down the list to the new item:
	 * // {
	 * // final ListView listView = getListView();
	 * // listView.post(new Runnable() {
	 * // @Override
	 * // public void run() {
	 * // listView.setSelection(listView.getCount() - 1);
	 * // }
	 * // });
	 * // }
	 * 
	 * }
	 * 
	 * public void setShowDetails(boolean showDetails) {
	 * // TODO CHANGE THIS!!! (was just a quick fix)
	 * if (adapter != null) {
	 * adapter.setShowDetails(showDetails);
	 * adapter.notifyDataSetChanged();
	 * }
	 * }
	 * 
	 * public boolean getShowDetails() {
	 * // TODO CHANGE THIS!!! (was just a quick fix)
	 * if (adapter != null) {
	 * return adapter.getShowDetails();
	 * } else {
	 * return false;
	 * }
	 * }
	 * 
	 * public void handleAddButton() {
	 * Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	 * intent.setType("audio/*");
	 * 
	 * startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
	 * SoundController.REQUEST_SELECT_MUSIC);
	 * }
	 * 
	 * private void initClickListener() {
	 * listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	 * 
	 * @Override
	 * public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	 * selectedSoundPosition = position;
	 * return false;
	 * }
	 * });
	 * }
	 * 
	 * @Override
	 * public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	 * // switch (keyCode) {
	 * // case KeyEvent.KEYCODE_BACK:
	 * // ScriptActivity scriptActivity = (ScriptActivity) getActivity();
	 * // if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()) {
	 * // SoundController.getInstance().switchToScriptFragment(this);
	 * //
	 * // return true;
	 * // }
	 * // default:
	 * // break;
	 * // }
	 * return false;
	 * }
	 * 
	 * public View getView(int position, View convertView) {
	 * SoundViewHolder holder;
	 * 
	 * if (convertView == null) {
	 * convertView = View.inflate(this, R.layout.fragment_sound_soundlist_item, null);
	 * 
	 * holder = new SoundViewHolder();
	 * holder.playButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_play_image_button);
	 * holder.pauseButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_pause_image_button);
	 * BottomBar.setButtonInvisible(this);
	 * 
	 * holder.playButton.setVisibility(Button.VISIBLE);
	 * holder.pauseButton.setVisibility(Button.GONE);
	 * 
	 * holder.soundFragmentButtonLayout = (LinearLayout) convertView
	 * .findViewById(R.id.fragment_sound_item_main_linear_layout);
	 * holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_sound_item_checkbox);
	 * holder.titleTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_title_text_view);
	 * holder.timeSeperatorTextView = (TextView) convertView
	 * .findViewById(R.id.fragment_sound_item_time_seperator_text_view);
	 * holder.timeDurationTextView = (TextView) convertView
	 * .findViewById(R.id.fragment_sound_item_duration_text_view);
	 * holder.soundFileSizePrefixTextView = (TextView) convertView
	 * .findViewById(R.id.fragment_sound_item_size_prefix_text_view);
	 * holder.soundFileSizeTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_size_text_view);
	 * 
	 * holder.timePlayedChronometer = (Chronometer) convertView
	 * .findViewById(R.id.fragment_sound_item_time_played_chronometer);
	 * 
	 * convertView.setTag(holder);
	 * } else {
	 * holder = (SoundViewHolder) convertView.getTag();
	 * }
	 * SoundController controller = SoundController.getInstance();
	 * controller.updateSoundLogic(position, holder);
	 * 
	 * return convertView;
	 * }
	 * 
	 * public interface OnSoundInfoListChangedAfterNewListener {
	 * 
	 * public void onSoundInfoListChangedAfterNew(SoundInfo soundInfo);
	 * 
	 * }
	 * 
	 * public void setSelectedSoundInfo(SoundInfo selectedSoundInfo) {
	 * this.selectedSoundInfo = selectedSoundInfo;
	 * }
	 * 
	 * public ArrayList<SoundInfo> getSoundInfoList() {
	 * return soundInfoList;
	 * }
	 * 
	 * 
	 * @Override
	 * public void onSoundChecked() {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 */

	// old ends	
}
