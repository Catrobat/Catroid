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
package org.catrobat.catroid.ui.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.TemplateContainer;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.dependencies.DaggerWebComponent;
import org.catrobat.catroid.dependencies.WebComponent;
import org.catrobat.catroid.dependencies.WebModule;
import org.catrobat.catroid.dependencies.WebRequestModule;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.FetchTemplatesRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TemplateAdapter extends ArrayAdapter<TemplateData> {

	private static final String TAG = TemplateAdapter.class.getSimpleName();
	private static LayoutInflater inflater;

	private final Activity activity;
	private OnTemplateEditListener onTemplateEditListener;
	private TemplateContainer templateContainer;
	private ProgressDialog progressDialog;

	@Inject
	FetchTemplatesRequest fetchTemplatesRequest;

	private WebComponent webComponent;

	public TemplateAdapter(Activity activity, int resource, int textViewResourceId, OnTemplateEditListener listener) {
		super(activity, resource, textViewResourceId);
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onTemplateEditListener = listener;

		if (activity.getIntent().hasExtra(Constants.FETCH_TEMPLATES_FROM_SERVER)) {
			webComponent = DaggerWebComponent.builder()
					.webModule(new WebModule())
					.webRequestModule(new WebRequestModule())
					.build();

			webComponent.inject(this);

			fetchTemplates();
		}
	}

	private void fetchTemplates() {
		if (isEmpty()) {
			String title = activity.getString(R.string.please_wait);
			String progressMessage = activity.getString(R.string.fetching_templates);
			progressDialog = ProgressDialog.show(activity, title, progressMessage);

			fetchTemplatesRequest.fetchTemplates(FetchTemplatesRequest.ENDPOINT)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Consumer<TemplateContainer>() {
						@Override
						public void accept(TemplateContainer fetchedTemplateContainer) throws Exception {
							templateContainer = fetchedTemplateContainer;
							initTemplates();
							progressDialog.dismiss();
						}
					}, new Consumer<Throwable>() {
						@Override
						public void accept(@NonNull Throwable throwable) throws Exception {
							showFailure();
							progressDialog.dismiss();
							Log.e(TAG, "could not fetch templates list: " + throwable.getMessage());
						}
					});
		}
	}

	private void initTemplates() {
		if (templateContainer != null) {
			addAll(templateContainer.getTemplateData());
		}
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
		Picasso.with(activity).load(getBaseUrl() + templateData.getThumbnail()).into(holder.image);
	}

	public String getBaseUrl() {
		return !Strings.isNullOrEmpty(templateContainer.getBaseUrl())
				? templateContainer.getBaseUrl() : Constants.MAIN_URL_HTTPS + "/";
	}

	private void showFailure() {
		int errorMessage = !Utils.isNetworkAvailable(activity) ? R.string.error_internet_connection : R.string.error_fetching_templates;
		ToastUtil.showError(activity, errorMessage);
	}

	public void setWebComponent(WebComponent webComponent) {
		this.webComponent = webComponent;
		webComponent.inject(this);
		fetchTemplates();
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
