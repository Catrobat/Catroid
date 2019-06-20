package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.OrientationDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.utils.Utils;

public class MergeProjectDialogFragment extends DialogFragment {

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_merge_project_name, null);

		final TextInputDialog.TextWatcher textWatcher = new TextInputDialog.TextWatcher() {
			@Nullable
			@Override
			public String validateInput(String input, Context context) {
				String error = null;
				if (input.isEmpty()) {
					return context.getString(R.string.name_empty);
				}

				input = input.trim();
				if (input.isEmpty()) {
					error = context.getString(R.string.name_consists_of_spaces_only);
				} else if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(input)) {
					error = context.getString(R.string.name_already_exists);
				}

				return error;
			}
		};

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this.getContext())
				.setHint(getString(R.string.project_name_label))
				.setTextWatcher(textWatcher)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					showNextDialog(textInput, true);
				});

		return builder
				.setTitle(R.string.new_merge_project_dialog_title)
				.setView(view)
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	void showNextDialog(String projectName, boolean createEmptyProject) {

	}
}