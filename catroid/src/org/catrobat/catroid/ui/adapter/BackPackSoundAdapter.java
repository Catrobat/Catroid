package org.catrobat.catroid.ui.adapter;

import android.content.Context;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;

import java.util.ArrayList;

public class BackPackSoundAdapter extends SoundBaseAdapter implements ScriptActivityAdapterInterface {

	private BackPackSoundFragment backPackSoundActivity;

	public BackPackSoundAdapter(Context context, int resource, int textViewResourceId, ArrayList<SoundInfo> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items, showDetails);
	}

	public BackPackSoundFragment getBackPackSoundActivity() {
		return this.backPackSoundActivity;
	}

	public void setBackPackSoundActivity(BackPackSoundFragment backPackSoundActivity) {
		this.backPackSoundActivity = backPackSoundActivity;
	}
}
