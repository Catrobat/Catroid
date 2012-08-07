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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;

public class DeleteCostumeDialog {

	private ScriptTabActivity scriptTabActivity;

	public DeleteCostumeDialog(ScriptTabActivity scriptTabActivity) {
		this.scriptTabActivity = scriptTabActivity;

	}

	public Dialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(scriptTabActivity);
		builder.setTitle(R.string.delete_costume_dialog);

		LayoutInflater inflater = (LayoutInflater) scriptTabActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_delete_costume, null);

		builder.setView(view);

		final Dialog deleteDialog = builder.create();
		deleteDialog.setCanceledOnTouchOutside(true);

		return deleteDialog;
	}

	public void handleOkButton() {

		ArrayList<CostumeData> costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		int position = scriptTabActivity.selectedPosition;

		StorageHandler.getInstance().deleteFile(costumeDataList.get(position).getAbsolutePath());
		costumeDataList.remove(position);
		((CostumeAdapter) ((CostumeActivity) scriptTabActivity.getCurrentActivity()).getListAdapter())
				.notifyDataSetChanged();

		scriptTabActivity.dismissDialog(ScriptTabActivity.DIALOG_DELETE_COSTUME);
	}

}
