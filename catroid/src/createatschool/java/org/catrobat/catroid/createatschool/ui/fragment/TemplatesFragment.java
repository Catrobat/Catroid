/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.createatschool.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.createatschool.ui.adapter.TemplateAdapter;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.utils.SnackBarUtil;

public class TemplatesFragment extends ListFragment implements TemplateAdapter.OnTemplateEditListener {

	public static final String TAG = TemplatesFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_TEMPLATE_DATA = "template_data";

	private ProjectData projectToEdit;
	private TemplateAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		initAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_projects_list, container);
		SnackBarUtil.showHintSnackBar(getActivity(), R.string.hint_templates);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			projectToEdit = (ProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_TEMPLATE_DATA);
		}

		initAdapter();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_TEMPLATE_DATA, projectToEdit);
		super.onSaveInstanceState(outState);
	}

	private void initAdapter() {
		adapter = new TemplateAdapter(getActivity(), R.layout.activity_my_projects_list_item,
				R.id.my_projects_activity_project_title);
		setListAdapter(adapter);
		initListener();
	}

	private void initListener() {
		adapter.setOnTemplateEditListener(this);
	}

	@Override
	public void onTemplateEdit(TemplateData templateData) {
		showNewProjectDialog(templateData);
	}

	private void showNewProjectDialog(TemplateData templateData) {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.setOpenedFromTemplatesList(true);
		dialog.setTemplateData(templateData);
		dialog.show(getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}
}
