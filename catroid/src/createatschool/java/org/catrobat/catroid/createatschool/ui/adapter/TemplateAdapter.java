/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.TemplateContainer;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.createatschool.transfers.FetchTemplatesTask;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;

public class TemplateAdapter extends ArrayAdapter<TemplateData> implements
		FetchTemplatesTask.OnFetchTemplatesCompleteListener {

	private static LayoutInflater inflater;

	private final Context context;
	private OnTemplateEditListener onTemplateEditListener;
	private TemplateContainer templateContainer;

	public TemplateAdapter(Context context, int resource, int textViewResourceId, OnTemplateEditListener listener) {
		super(context, resource, textViewResourceId);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onTemplateEditListener = listener;
		fetchTemplates();
	}

	private void fetchTemplates() {
		if (isEmpty()) {
			FetchTemplatesTask task = new FetchTemplatesTask(context, this);
			task.execute();
		}
	}

	private void initTemplates() {
		if (templateContainer == null) {
			return;
		}

		addAll(templateContainer.getTemplateData());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.list_item_background);
			holder.templateName = (TextView) projectView.findViewById(R.id.list_item_text_view);
			holder.image = (ImageView) projectView.findViewById(R.id.list_item_image_view);
			projectView.setTag(holder);

			holder.templateName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.templateName.getTextSize() * TextSizeUtil.getModifier());
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		final TemplateData templateData = getItem(position);
		final String templateName;
		if (templateData != null) {
			templateName = templateData.getName();
			holder.templateName.setText(templateName);
		}
		setImage(templateData, holder);

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (templateData != null) {
					onTemplateEditListener.onTemplateEdit(templateData);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
		return projectView;
	}

	private void setImage(TemplateData templateData, ViewHolder holder) {
		Picasso.with(context).load(getBaseUrl() + templateData.getThumbnail()).into(holder.image);
	}

	public String getBaseUrl() {
		return !Strings.isNullOrEmpty(templateContainer.getBaseUrl())
				? templateContainer.getBaseUrl() : Constants.MAIN_URL_HTTPS + "/";
	}

	@Override
	public void onFetchTemplatesComplete(String templatesList) {
		Gson gson = new Gson();
		try {
			templateContainer = gson.fromJson(templatesList, TemplateContainer.class);
		} catch (JsonSyntaxException exception) {
			ToastUtil.showError(context, context.getString(R.string.error_fetching_templates));
		}

		initTemplates();
	}

	public interface OnTemplateEditListener {
		void onTemplateEdit(TemplateData templateData);
	}

	private static class ViewHolder {
		private RelativeLayout background;
		private TextView templateName;
		private ImageView image;
	}
}
