package org.catrobat.catroid.ui.adapter;

import android.content.Context;

import org.catrobat.catroid.common.SoundInfo;

import java.util.ArrayList;

public class BackPackSoundAdapter extends SoundBaseAdapter implements ScriptActivityAdapterInterface {

	public BackPackSoundAdapter(Context context, int resource, int textViewResourceId, ArrayList<SoundInfo> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items, showDetails);
	}
}
