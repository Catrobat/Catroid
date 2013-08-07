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

import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SoundViewHolder {
	public ImageButton playButton;
	public ImageButton pauseButton;
	public LinearLayout soundFragmentButtonLayout;
	public CheckBox checkbox;
	public TextView titleTextView;
	public TextView timeSeperatorTextView;
	public TextView timeDurationTextView;
	public TextView soundFileSizePrefixTextView;
	public TextView soundFileSizeTextView;
	public Chronometer timePlayedChronometer;

	public ImageButton getPlayButton() {
		return playButton;
	}

	public ImageButton getPauseButton() {
		return pauseButton;
	}

	public LinearLayout getSoundFragmentButtonLayout() {
		return soundFragmentButtonLayout;
	}

	public CheckBox getCheckbox() {
		return checkbox;
	}

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public TextView getTimeSeperatorTextView() {
		return timeSeperatorTextView;
	}

	public TextView getTimeDurationTextView() {
		return timeDurationTextView;
	}

	public TextView getSoundFileSizePrefixTextView() {
		return soundFileSizePrefixTextView;
	}

	public TextView getSoundFileSizeTextView() {
		return soundFileSizeTextView;
	}

	public Chronometer getTimePlayedChronometer() {
		return timePlayedChronometer;
	}

}
