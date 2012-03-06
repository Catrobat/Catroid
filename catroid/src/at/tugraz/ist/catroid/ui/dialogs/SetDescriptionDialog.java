/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

//import android.content.DialogInterface;
//import android.content.DialogInterface.OnKeyListener;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import at.tugraz.ist.catroid.R;
//import at.tugraz.ist.catroid.ui.MyProjectsActivity;
//
//public class SetDescriptionDialog extends TextDialog {
//
//	public SetDescriptionDialog(MyProjectsActivity myProjectActivity) {
//		super(myProjectActivity, myProjectActivity.getString(R.string.description), null);
//		initKeyAndClickListener();
//
//		input.addTextChangedListener(new TextWatcher() {
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				buttonPositive.setEnabled(true);
//			}
//
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			public void afterTextChanged(Editable s) {
//			}
//		});
//	}
//
//	public void handleOkButton() {
//		String description = (input.getText().toString());
//
//		String currentProjectName = projectManager.getCurrentProject().getName();
//		String projectToChangeName = (((MyProjectsActivity) activity).projectToEdit.getName());
//
//		if (projectToChangeName.equalsIgnoreCase(currentProjectName)) {
//			setDescription(description);
//			((MyProjectsActivity) activity).initAdapter();
//			activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
//			return;
//		}
//
//		projectManager.loadProject(projectToChangeName, activity, false);
//		setDescription(description);
//		projectManager.loadProject(currentProjectName, activity, false);
//
//		((MyProjectsActivity) activity).initAdapter();
//		activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
//	}
//
//	private void setDescription(String description) {
//		projectManager.getCurrentProject().description = description;
//		projectManager.saveProject();
//	}
//
//	private void initKeyAndClickListener() {
//		dialog.setOnKeyListener(new OnKeyListener() {
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//					handleOkButton();
//					return true;
//				}
//				return false;
//			}
//		});
//
//		buttonPositive.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				handleOkButton();
//			}
//		});
//
//		buttonNegative.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
//			}
//		});
//	}
//}
