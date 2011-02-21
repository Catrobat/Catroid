package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.utils.ImageContainer;

public class ProgrammAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	private ListView mMainListView;
	//private ImageContainer mImageContainer;
	private ArrayList<HashMap<String, String>> mBrickList;
	//private EditTextDialog mEditTextDialog;
	private Script script;
	//private LayoutInflater inflater;

	public ProgrammAdapter(Context context, Script script, ListView listview, ImageContainer imageContainer) {
		this.script = script;
		
		this.context = context;
		mMainListView = listview;
		//mImageContainer = imageContainer;
		//inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//mEditTextDialog = null;

	}
	
	public void setContent(Script script) {
		this.script = script;
		notifyDataSetChanged();
	}
	
	
/*
	private int mPositionForAnimation = -1;

	// the shake animation is set for the context menu
	public void setAnimationOnPosition(int position) {
		mPositionForAnimation = position;
	}

	private void tryStopAnimationOnView(View view) {
		try {
			if (view.getAnimation() != null) {
				view.getAnimation().setDuration(0);
				view.setAnimation(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private View organizeViewHandling(String type, int typeId, View convertView, int position, String brickId) {
		View view = null;
		if (convertView != null) {
			view = convertView;
			tryStopAnimationOnView(view);
		} else {
			view = inflater.inflate(typeId, null);

		}

		if (mPositionForAnimation >= 0 && mPositionForAnimation == position) {
			Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
			view.startAnimation(shake);
		}

		return view;
	}
*/
	@Override
	public boolean hasStableIds() {
		return false;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//if(convertView != null)
		//	return convertView;
		return script.getBrickList().get(position).getView(context, convertView, this);
/*
		final HashMap<String, String> brick = mBrickList.get(position);
		final String type = mBrickList.get(position).get(BrickDefine.BRICK_TYPE);
		final String value = mBrickList.get(position).get(BrickDefine.BRICK_VALUE);
		final String value1 = mBrickList.get(position).get(BrickDefine.BRICK_VALUE_1);
		final String brickId = mBrickList.get(position).get(BrickDefine.BRICK_ID);

		if (type != null)
			switch (Integer.valueOf(type).intValue()) {

			case (BrickDefine.SET_BACKGROUND): {
				View view = organizeViewHandling(type, R.layout.construction_brick_set_background, convertView, position, brickId);
				ImageView imageView = (ImageView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_set_background_image_view_tag));
				imageView.setOnClickListener(this);
				if (!value1.equals("")) {
					imageView.setBackgroundDrawable(null);
					imageView.setImageBitmap(mImageContainer.getImage(value1));
				} else {
					imageView.setImageBitmap(null);
					imageView.setBackgroundResource(R.drawable.landscape);
				}
				return view;
			}

			case (BrickDefine.PLAY_SOUND): {
				View view = organizeViewHandling(type, R.layout.construction_brick_play_sound, convertView, position, brickId);
				Spinner spinner = (Spinner) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_play_sound_spinner_tag));

				final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getSoundContent(), R.layout.sound_spinner,
						new String[] { MediaFileLoader.SOUND_NAME }, new int[] { R.id.SoundSpinnerTextView });
				spinner.setAdapter(adapter);
				// workaround for audio files that are only in the project
				// folder but not somewhere else on the sd card
				if (getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_NAME)) == -1) {
					ArrayList<HashMap<String, String>> soundContent = mMediaFileLoader.getSoundContent();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(MediaFileLoader.SOUND_NAME, brick.get(BrickDefine.BRICK_NAME));
					map.put(MediaFileLoader.SOUND_PATH, Utils.concatPaths(ConstructionSiteActivity.ROOT_SOUNDS, brick.get(BrickDefine.BRICK_VALUE)));
					soundContent.add(map);
					final SimpleAdapter newAdapter = new SimpleAdapter(mCtx, soundContent, R.layout.sound_spinner, new String[] { MediaFileLoader.SOUND_NAME },
							new int[] { R.id.SoundSpinnerTextView });
					spinner.setAdapter(newAdapter);

				}
				try {
					spinner.setSelection(getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_NAME)));
				} catch (Exception e) {
					e.printStackTrace();
				}

				spinner.setOnItemSelectedListener(this);
				return view;
			}

			case (BrickDefine.WAIT): {
				View view = organizeViewHandling(type, R.layout.construction_brick_wait, convertView, position, brickId);
				EditText eText = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_wait_edit_text_tag));
				// eText.setOnKeyListener(this);
				eText.setText(value);
				eText.setOnClickListener(this);
				return view;
			}

			case (BrickDefine.HIDE): {
				View view = organizeViewHandling(type, R.layout.construction_brick_simple_text_view, convertView, position, brickId);
				TextView tView = (TextView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_hide));
				tView.setText(R.string.hide_main_adapter);
				return view;
			}

			case (BrickDefine.SHOW): {
				View view = organizeViewHandling(type, R.layout.construction_brick_simple_text_view, convertView, position, brickId);
				TextView tView = (TextView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_hide));
				tView.setText(R.string.show_main_adapter);
				return view;
			}

			case (BrickDefine.GO_TO): {
				View view = organizeViewHandling(type, R.layout.construction_brick_goto, convertView, position, brickId);
				EditText eTextX = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_go_to_x_tag));
				// eTextX.setOnKeyListener(this);
				eTextX.setText(value);
				eTextX.setOnClickListener(this);

				EditText eTextY = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_go_to_y_tag));
				// eTextY.setOnKeyListener(this);
				eTextY.setText(value1);
				eTextY.setOnClickListener(this);
				return view;
			}

			case (BrickDefine.SET_COSTUME): {
				View view = organizeViewHandling(type, R.layout.construction_brick_set_costume, convertView, position, brickId);
				ImageView imageView = (ImageView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_set_costume_image_view_tag));
				imageView.setOnClickListener(this);
				if (!value1.equals("")) {
					imageView.setBackgroundDrawable(null);
					imageView.setImageBitmap(mImageContainer.getImage(value1));
				} else {
					imageView.setImageBitmap(null);
					imageView.setBackgroundResource(R.drawable.icon);
				}
				return view;
			}

			case (BrickDefine.SCALE_COSTUME): {
				View view = organizeViewHandling(type, R.layout.construction_brick_scale_costume, convertView, position, brickId);
				EditText eText = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_scale_costume_edit_text_tag));
				eText.setText(value);
				eText.setOnClickListener(this);
				return view;
			}

			case (BrickDefine.COME_TO_FRONT): {
				View view = organizeViewHandling(type, R.layout.construction_brick_come_to_front, convertView, position, brickId);
				TextView tView = (TextView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_come_to_front));
				tView.setText(R.string.come_to_front_main_adapter);
				return view;
			}

			case (BrickDefine.GO_BACK): {
				View view = organizeViewHandling(type, R.layout.construction_brick_go_back, convertView, position, brickId);
				EditText eText = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_go_back_edit_text_tag));
				eText.setText(value);
				eText.setOnClickListener(this);
				return view;
			}

			case (BrickDefine.TOUCHED): {
				View view = organizeViewHandling(type, R.layout.construction_brick_touched, convertView, position, brickId);
				TextView tView = (TextView) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_touched));
				tView.setText(R.string.touched_main_adapter);
				return view;
			}
			}
		return null;
		*/
	}

//	public int getIndexFromElementSound(SimpleAdapter adapter, String element) {
//		ArrayList<HashMap<String, String>> arrayList = mMediaFileLoader.getSoundContent();
//		for (int i = 0; i < adapter.getCount(); i++) {
//			String value = arrayList.get(i).get(MediaFileLoader.SOUND_NAME);
//			if (value.equals((element))) {
//				return i;
//			}
//		}
//		return -1;
//	}

	public int getCount() {
		return script.getBrickList().size();
	}
	
	public Object getItem(int arg0) {
		return script.getBrickList().get(arg0);
	}

	public void onClick(View v) {
		/*
		String tag = (String) v.getTag();

		if (context.getString(R.string.constructional_brick_set_background_image_view_tag).equals(tag)) {
			mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView) v);
		} else if (context.getString(R.string.constructional_brick_set_costume_image_view_tag).equals(tag)) {
			mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView) v);
		} else if (context.getString(R.string.constructional_brick_wait_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mEditTextDialog.show(mBrickList.get(brickPosition), (EditText) v);
		} else if (context.getString(R.string.constructional_brick_scale_costume_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mEditTextDialog.show(mBrickList.get(brickPosition), (EditText) v);
		} else if (context.getString(R.string.constructional_brick_go_to_x_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mEditTextDialog.show(mBrickList.get(brickPosition), (EditText) v);
		} else if (context.getString(R.string.constructional_brick_go_to_y_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mEditTextDialog.show(mBrickList.get(brickPosition), (EditText) v);
		} else if (context.getString(R.string.constructional_brick_go_back_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mEditTextDialog.show(mBrickList.get(brickPosition), (EditText) v);
		}
		*/
	}
//
//	private void deleteSound(String soundName) {
//		if (soundName == null || soundName.length() == 0) {
//			Log.i("ConstructionSiteListViewAdapter", "No sound file to delete.");
//		} else {
//			String soundsPath = ConstructionSiteActivity.ROOT_SOUNDS;
//			String soundFilePath = Utils.concatPaths(soundsPath, soundName);
//			if (Utils.deleteFile(soundFilePath)) {
//				Log.i("ConstructionSiteListViewAdapter", "Successfully deleted sound file \"" + soundFilePath + "\".");
//			} else {
//				Log.w("ConstructionSiteListViewAdapter", "Error! Could not delete sound file \"" + soundFilePath + "\".");
//			}
//		}
//	}

//	public void onItemSelected(AdapterView<?> spinner, View v, int position, long id) {
//		String tag = (String) spinner.getTag();
//		if (context.getString(R.string.constructional_brick_play_sound_spinner_tag).equals(tag)) {
//			int brickPosition = mMainListView.getPositionForView(spinner);
//			@SuppressWarnings("unchecked")
//			HashMap<String, String> map = (HashMap<String, String>) spinner.getAdapter().getItem(position);
//
//			Log.i("ConstructionSiteListViewAdapter", "Brick value: " + mBrickList.get(brickPosition).get(BrickDefine.BRICK_VALUE));
//			Log.i("ConstructionSiteListViewAdapter", "map sound name: " + map.get(MediaFileLoader.SOUND_NAME));
//			Log.i("ConstructionSiteListViewAdapter", "map sound path: " + map.get(MediaFileLoader.SOUND_PATH));
//
//			if (!mBrickList.get(brickPosition).get(BrickDefine.BRICK_NAME).equals(map.get(MediaFileLoader.SOUND_NAME))) {
//				String soundName = mBrickList.get(brickPosition).get(BrickDefine.BRICK_VALUE);
//				deleteSound(soundName);
//
//				String newPath = ConstructionSiteActivity.ROOT_SOUNDS;
//				String uniqueName = Calendar.getInstance().getTimeInMillis() + map.get(MediaFileLoader.SOUND_NAME)
//						+ map.get(MediaFileLoader.SOUND_PATH).substring(map.get(MediaFileLoader.SOUND_PATH).length() - 4);
//				newPath = Utils.concatPaths(newPath, uniqueName);
//
//				if (Utils.copyFile(map.get(MediaFileLoader.SOUND_PATH), newPath, context, true)) {
//					mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, uniqueName);
//					mBrickList.get(brickPosition).put(BrickDefine.BRICK_NAME, map.get(MediaFileLoader.SOUND_NAME));
//				} else
//					Log.e("ConstructionSiteViewAdapter", "Copy Sound File Error");
//			}
//
//		}
//	}

	public void notifyDataSetChanged(ArrayList<HashMap<String, String>> data) {
		mBrickList = data;
		notifyDataSetChanged();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		String tag = v.getTag().toString();

		if (context.getString(R.string.constructional_brick_go_to_x_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText) v).getText().toString());
			return false;
		} else if (context.getString(R.string.constructional_brick_go_to_y_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE_1, ((EditText) v).getText().toString());
			return false;
		} else if (context.getString(R.string.constructional_brick_wait_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText) v).getText().toString());
			return false;
		} else if (context.getString(R.string.constructional_brick_scale_costume_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText) v).getText().toString());
			return false;
		} else if (context.getString(R.string.constructional_brick_go_back_edit_text_tag).equals(tag)) {
			int brickPosition = mMainListView.getPositionForView((EditText) v);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText) v).getText().toString());
			return false;
		}
		return false;
	}

	public long getItemId(int position) {
		return position;
	}

}
