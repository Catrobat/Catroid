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
package org.catrobat.catroid.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.EditTextImeOverride;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProjectAdapter extends ArrayAdapter<ProjectData> implements EditTextImeOverride.EditTextImeBackListener {
	private boolean showDetails;
	private int selectMode;
	private Set<Integer> checkedProjects = new TreeSet<Integer>();
	private OnProjectEditListener onProjectEditListener;

	public static class ViewHolder {
		private RelativeLayout background;
		private CheckBox checkbox;
		private TextView projectName;
		private ImageView image;
		private TextView size;
		private TextView dateChanged;
		private View projectDetails;
		private ImageButton showOverview;
		private View projectOverview;
		private ProgressBar projectProgressBar;
		private Project project = null;
	}

	private static LayoutInflater inflater;
	private ProjectAndSceneScreenshotLoader screenshotLoader;

	public ProjectAdapter(Context context, int resource, int textViewResourceId, List<ProjectData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screenshotLoader = new ProjectAndSceneScreenshotLoader(context);
		showDetails = false;
		selectMode = ListView.CHOICE_MODE_NONE;
	}

	public void setOnProjectEditListener(OnProjectEditListener listener) {
		onProjectEditListener = listener;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	public Set<Integer> getCheckedProjects() {
		return checkedProjects;
	}

	public int getAmountOfCheckedProjects() {
		return checkedProjects.size();
	}

	public void addCheckedProject(int position) {
		checkedProjects.add(position);
	}

	public void clearCheckedProjects() {
		checkedProjects.clear();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.activity_my_projects_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.my_projects_activity_item_background);
			holder.checkbox = (CheckBox) projectView.findViewById(R.id.project_checkbox);
			holder.projectName = (TextView) projectView.findViewById(R.id.my_projects_activity_project_title);
			holder.image = (ImageView) projectView.findViewById(R.id.my_projects_activity_project_image);
			holder.size = (TextView) projectView.findViewById(R.id.my_projects_activity_size_of_project_2);
			holder.dateChanged = (TextView) projectView.findViewById(R.id.my_projects_activity_project_changed_2);
			holder.projectDetails = projectView.findViewById(R.id.my_projects_activity_list_item_details);
			holder.showOverview = (ImageButton) projectView.findViewById(R.id.my_projects_activity_show_overview);
			holder.projectOverview = projectView.findViewById(R.id.my_projects_activity_list_item_overview);
			holder.projectProgressBar = (ProgressBar) projectView.findViewById(R.id.my_projects_activity_list_item_progress_bar);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		// ------------------------------------------------------------
		ProjectData projectData = getItem(position);
		final String projectName = projectData.projectName;
		String sceneName = StorageHandler.getInstance().getFirstSceneName(projectName);

		//set name of project:
		holder.projectName.setText(projectName);

		// set size of project:
		String size = UtilFile.getSizeAsString(new File(Utils.buildProjectPath(projectName)));
		holder.size.setText(size);
		((TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_size_content)).setText(size);

		//set last changed:
		Date projectLastModificationDate = new Date(projectData.lastUsed);
		Date now = new Date();
		Date yesterday = new Date(now.getTime() - DateUtils.DAY_IN_MILLIS);
		DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		String projectLastModificationDateString = "";

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now);

		Calendar yesterdayCalendar = Calendar.getInstance();
		yesterdayCalendar.setTime(yesterday);

		Calendar projectLastModificationDateCalendar = Calendar.getInstance();
		projectLastModificationDateCalendar.setTime(projectLastModificationDate);

		if (mediumDateFormat.format(projectLastModificationDate).equals(mediumDateFormat.format(now))) {
			projectLastModificationDateString = getContext().getString(R.string.details_date_today) + " "
					+ shortTimeFormat.format(projectLastModificationDate);
		} else if (mediumDateFormat.format(projectLastModificationDate).equals(mediumDateFormat.format(yesterday))) {
			projectLastModificationDateString = getContext().getString(R.string.details_date_yesterday);
		} else {
			projectLastModificationDateString = mediumDateFormat.format(projectLastModificationDate);
		}

		holder.dateChanged.setText(projectLastModificationDateString);
		((TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_last_modified_content)).setText(projectLastModificationDateString);

		//set project image (threaded):
		screenshotLoader.loadAndShowScreenshot(projectName, sceneName, false, holder.image);

		if (!showDetails) {
			holder.projectDetails.setVisibility(View.GONE);
			holder.projectName.setSingleLine(true);
			holder.showOverview.setVisibility(View.GONE);
		} else {
			holder.projectDetails.setVisibility(View.VISIBLE);
			holder.projectName.setSingleLine(false);
			holder.showOverview.setVisibility(View.VISIBLE);
		}

		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedProjects();
					}
					checkedProjects.add(position);
				} else {
					checkedProjects.remove(position);
				}
				notifyDataSetChanged();

				if (onProjectEditListener != null) {
					onProjectEditListener.onProjectChecked();
				}
			}
		});

		holder.background.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					return true;
				}
				return false;
			}
		});

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				} else if (onProjectEditListener != null) {
					onProjectEditListener.onProjectEdit(position);
				}
			}
		});

		holder.showOverview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.projectOverview.getVisibility() == View.GONE) {
					holder.showOverview.setImageResource(R.drawable.project_list_arrow_up);
					holder.projectDetails.setVisibility(View.GONE);
					holder.projectName.setSingleLine(true);
					setProjectOverview(projectName, holder);
				} else {
					holder.showOverview.setImageResource(R.drawable.project_list_arrow_down);
					holder.projectOverview.setVisibility(View.GONE);
					if (showDetails) {
						holder.projectDetails.setVisibility(View.VISIBLE);
						holder.projectName.setSingleLine(false);
					}
				}
			}
		});

		final EditTextImeOverride editDescription = (EditTextImeOverride) holder.projectOverview.findViewById(R.id
				.my_projects_activity_description_edit);
		editDescription.setOnEditTextImeBackListener(this, holder, editDescription);
		holder.projectOverview.findViewById(R.id.my_projects_activity_edit_description_button).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						editDescription.setVisibility(View.VISIBLE);
						holder.projectOverview.findViewById(R.id.my_projects_activity_description_content).setVisibility(View.GONE);
						editDescription.requestFocus();
						InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						manager.showSoftInput(editDescription, InputMethodManager.SHOW_IMPLICIT);
						editDescription.setSelection(editDescription.getText().length());
					}
		});

		if (checkedProjects.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
			clearCheckedProjects();
		}

		return projectView;
	}

	@Override
	public void onImeBack(ViewHolder holder, EditTextImeOverride editText) {
		if (holder.project != null) {
			holder.project.setDescription(editText.getText().toString());
			StorageHandler.getInstance().saveProject(holder.project);
			((TextView) holder.projectOverview.findViewById(R.id
					.my_projects_activity_description_content)).setText(editText.getText());
		}
		editText.setVisibility(View.GONE);
		holder.projectOverview.findViewById(R.id.my_projects_activity_description_content).setVisibility(View.VISIBLE);
	}

	public interface OnProjectEditListener {
		void onProjectChecked();

		void onProjectEdit(int position);
	}

	private void setProjectOverview(final String projectName, final ViewHolder holder) {
		if (holder.project != null) {
			holder.projectOverview.setVisibility(View.VISIBLE);
			return;
		}

		holder.projectProgressBar.setVisibility(View.VISIBLE);

		final TextView authorView = (TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_author_content);
		final TextView screenSizeView = (TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_screen_size_content);
		final TextView modeView = (TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_mode_content);
		final TextView remixView = (TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_remix_content);
		final TextView descriptionView = (TextView) holder.projectOverview.findViewById(R.id.my_projects_activity_description_content);
		final EditText descriptionEditView = (EditText) holder.projectOverview.findViewById(R.id.my_projects_activity_description_edit);

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				final Project finalProject = StorageHandler.getInstance().loadProject(projectName);
				((Activity) getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (finalProject != null) {
							XmlHeader header = finalProject.getXmlHeader();
							screenSizeView.setText(header.getVirtualScreenWidth() + "x" + header.getVirtualScreenHeight());
							if (header.islandscapeMode()) {
								modeView.setText(getContext().getString(R.string.landscape));
							} else {
								modeView.setText(getContext().getString(R.string.portrait));
							}
							descriptionView.setText(header.getDescription());
							descriptionEditView.setText(header.getDescription());
							holder.project = finalProject;

							String text = header.getUserHandle().trim().equals("") ? getContext().getString(R.string.unknown) : header.getUserHandle();
							authorView.setText(text);
							text = header.getRemixOf().trim().equals("") ? getContext().getString(R.string.nxt_no_sensor) : header.getRemixOf();
							remixView.setText(text);
						}
						holder.projectProgressBar.setVisibility(View.GONE);
						holder.projectOverview.setVisibility(View.VISIBLE);
					}
				});
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}
}
