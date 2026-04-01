/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.pocketmusic.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NotePickerDialog extends AppCompatDialogFragment implements NotePickerView.OnNoteChangedListener {
	private static final String CURRENT_NOTE = "CurrentNote";
	private static final String INITIAL_NOTE = "InitialNote";
	private static final String SHOW_ACTION_BAR = "ShowActionBar";

	public List<OnNotePickedListener> onNotePickedListener = new ArrayList<>();

	private MidiNotePlayer midiDriver;

	private NotePickerView notePickerView;
	private Button buttonApply;
	private Button buttonCancel;
	private int noteToApply;

	public static NotePickerDialog newInstance(int initialNote) {
		return newInstance(initialNote, false);
	}

	public static NotePickerDialog newInstance(int initialNote, boolean showActionBar) {
		NotePickerDialog dialog = new NotePickerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(INITIAL_NOTE, initialNote);
		bundle.putInt(CURRENT_NOTE, initialNote);
		bundle.putBoolean(SHOW_ACTION_BAR, showActionBar);
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		midiDriver = new MidiNotePlayer();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_notepicker, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		midiDriver = new MidiNotePlayer();
		if (!MidiNotePlayer.isInitialized()) {
			midiDriver.start();
		} else {
			midiDriver.setInstrument((byte) 0, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		}

		notePickerView = view.findViewById(R.id.note_picker_view);
		notePickerView.setOnNoteChangedListener(this);

		buttonApply = view.findViewById(R.id.note_picker_button_ok);
		buttonCancel = view.findViewById(R.id.note_picker_button_cancel);

		buttonApply.setOnClickListener(v -> {
			updateNoteChange(noteToApply);
			dismiss();
		});

		buttonCancel.setOnClickListener(v -> dismiss());

		if (savedInstanceState != null) {
			notePickerView.setSelectedNote(savedInstanceState.getInt(CURRENT_NOTE,
					TrackRowView.getMidiValueForRow(0)));
			notePickerView.setInitialNote(savedInstanceState.getInt(INITIAL_NOTE,
					TrackRowView.getMidiValueForRow(0)));
		} else {
			Bundle arguments = getArguments();
			notePickerView.setSelectedNote(arguments.getInt(CURRENT_NOTE,
					TrackRowView.getMidiValueForRow(0)));
			notePickerView.setInitialNote(arguments.getInt(INITIAL_NOTE,
					TrackRowView.getMidiValueForRow(0)));
		}

		noteToApply = notePickerView.getInitialNote();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(R.string.note_picker_title);
		return dialog;
	}

	@Override
	public void dismiss() {
		if (getShowsDialog()) {
			super.dismiss();
		} else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	public void addOnNotePickedListener(NotePickerDialog.OnNotePickedListener listener) {
		onNotePickedListener.add(listener);
	}

	public interface OnNotePickedListener {
		void noteChanged(int color);
	}

	@Override
	public void noteChanged(int note) {
		noteToApply = note;
	}

	private void updateNoteChange(int note) {
		for (NotePickerDialog.OnNotePickedListener listener : onNotePickedListener) {
			listener.noteChanged(note);
		}
	}
}

