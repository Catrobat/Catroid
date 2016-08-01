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
package org.catrobat.catroid.createatschool.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.createatschool.common.TemplateConstants;

public class TemplateAdapter extends ArrayAdapter<TemplateData> {
	private final Context context;
	private OnTemplateEditListener onTemplateEditListener;
	private static LayoutInflater inflater;

	public TemplateAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initTemplates();
	}

	private void initTemplates() {
		add(new TemplateData(TemplateConstants.TEMPLATE_ACTION_NAME, TemplateConstants.TEMPLATE_ACTION_IMAGE_NAME,
				TemplateConstants.TEMPLATE_ACTION_LANDSCAPE_FILENAME, TemplateConstants.TEMPLATE_ACTION_PORTRAIT_FILENAME));

		add(new TemplateData(TemplateConstants.TEMPLATE_ADVENTURE_NAME, TemplateConstants.TEMPLATE_ADVENTURE_IMAGE_NAME,
				TemplateConstants.TEMPLATE_ADVENTURE_LANDSCAPE_FILENAME, TemplateConstants.TEMPLATE_ADVENTURE_PORTRAIT_FILENAME));

		add(new TemplateData(TemplateConstants.TEMPLATE_PUZZLE_NAME, TemplateConstants.TEMPLATE_PUZZLE_IMAGE_NAME,
				TemplateConstants.TEMPLATE_PUZZLE_LANDSCAPE_FILENAME, TemplateConstants.TEMPLATE_PUZZLE_PORTRAIT_FILENAME));

		add(new TemplateData(TemplateConstants.TEMPLATE_QUIZ_NAME, TemplateConstants.TEMPLATE_QUIZ_IMAGE_NAME,
				TemplateConstants.TEMPLATE_QUIZ_LANDSCAPE_FILENAME, TemplateConstants.TEMPLATE_QUIZ_PORTRAIT_FILENAME));
	}

	public void setOnTemplateEditListener(OnTemplateEditListener listener) {
		onTemplateEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.activity_my_projects_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.my_projects_activity_item_background);
			holder.templateName = (TextView) projectView.findViewById(R.id.my_projects_activity_project_title);
			holder.image = (ImageView) projectView.findViewById(R.id.my_projects_activity_project_image);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		final TemplateData templateData = getItem(position);
		final String templateName;
		if (templateData != null) {
			templateName = templateData.templateName;
			holder.templateName.setText(templateName);
		}
		setImage(templateData, holder);

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (onTemplateEditListener != null && templateData != null) {
					onTemplateEditListener.onTemplateEdit(templateData);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
		return projectView;
	}

	private void setImage(TemplateData templateData, ViewHolder holder) {
		Picasso.with(context).load(templateData.templateResourceId).into(holder.image);
	}

	public interface OnTemplateEditListener {
		void onTemplateEdit(TemplateData templateData);
	}

	public static class ViewHolder {
		private RelativeLayout background;
		private TextView templateName;
		private ImageView image;
	}
}
