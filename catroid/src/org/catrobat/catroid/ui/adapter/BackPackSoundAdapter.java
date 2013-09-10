package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;

import java.util.ArrayList;

public class BackPackSoundAdapter extends SoundBaseAdapter implements ScriptActivityAdapterInterface {

	private BackPackSoundFragment backPackSoundFragment;

	public BackPackSoundAdapter(Context context, int resource, int textViewResourceId, ArrayList<SoundInfo> items,
			boolean showDetails, BackPackSoundFragment backPackSoundFragment) {

		super(context, resource, textViewResourceId, items, showDetails);

		this.backPackSoundFragment = backPackSoundFragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (this.backPackSoundFragment == null) {
			return convertView;
		}
		return this.backPackSoundFragment.getView(position, convertView);

	}
}
