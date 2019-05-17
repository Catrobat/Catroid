package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;


public class MergedProgramNameDialog extends DialogFragment {


	public MergedProgramNameDialog() {
		// Required empty public constructor
	}

	public interface MergeProgramNameDialogListener {
		void onFinishEditDialog(String inputText);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_merged_program_name, container, false);

		Button createButton = container.findViewById(R.id.bt_setMergedProjectName);
		final EditText projectNameInput = container.findViewById(R.id.et_mergedProjectName);

		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String projectName = projectNameInput.getText().toString();

				if (!projectName.isEmpty()) {
					if (!FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(projectName.toLowerCase())) {
						MergeProgramNameDialogListener listener = (MergeProgramNameDialogListener) getTargetFragment();
						listener.onFinishEditDialog(projectName);
						dismiss();
					} else {
						Toast.makeText(container.getContext(), "Project name already used", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(container.getContext(), "Enter Project name", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return view;
	}
}
