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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.FileCache;
import org.catrobat.catroid.utils.WebImageLoader;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;

public class ScratchProjectAdapter extends ArrayAdapter<ScratchProjectData> {
    private static final String TAG = ScratchProjectAdapter.class.getSimpleName();
    private boolean showDetails;
    private int selectMode;
    private Set<Integer> checkedProjects = new TreeSet<Integer>();
    private OnScratchProjectEditListener onScratchProjectEditListener;
    private WebImageLoader webImageLoader;

    private static class ViewHolder {
        private RelativeLayout background;
        private CheckBox checkbox;
        private TextView projectName;
        private ImageView image;
        private TextView detailsText;
        private View projectDetails;
    }

    private static LayoutInflater inflater;

    public ScratchProjectAdapter(Context context, int resource, int textViewResourceId, List<ScratchProjectData> objects) {
        super(context, resource, textViewResourceId, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        showDetails = true;
        selectMode = ListView.CHOICE_MODE_NONE;
        webImageLoader = new WebImageLoader(new FileCache(context), Executors.newFixedThreadPool(5));
    }

    public void setOnScratchProjectEditListener(OnScratchProjectEditListener listener) {
        onScratchProjectEditListener = listener;
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
            projectView = inflater.inflate(R.layout.fragment_scratch_project_list_item, parent, false);
            holder = new ViewHolder();
            holder.background = (RelativeLayout) projectView.findViewById(R.id.scratch_projects_list_item_background);
            holder.checkbox = (CheckBox) projectView.findViewById(R.id.scratch_project_checkbox);
            holder.projectName = (TextView) projectView.findViewById(R.id.scratch_projects_list_item_title);
            holder.image = (ImageView) projectView.findViewById(R.id.scratch_projects_list_item_image);
            holder.detailsText = (TextView) projectView.findViewById(R.id.scratch_projects_list_item_details_text);
            holder.projectDetails = projectView.findViewById(R.id.scratch_projects_list_item_details);
            projectView.setTag(holder);
        } else {
            holder = (ViewHolder) projectView.getTag();
        }

        // ------------------------------------------------------------
        ScratchProjectData projectData = getItem(position);

        // set name of project:
        holder.projectName.setText(projectData.getTitle());

        // set size of project:
        holder.detailsText.setText(projectData.getContent().replace(" ... ", " - "));
        holder.detailsText.setSingleLine(false);

        // set project image (threaded):
        ScratchProjectData.HttpImage httpImageMetadata = projectData.getProjectThumbnail();
        if (httpImageMetadata != null) {
            webImageLoader.fetchAndShowImage(httpImageMetadata.getUrl(), holder.image);
        }

        if (showDetails) {
            holder.projectDetails.setVisibility(View.VISIBLE);
            holder.projectName.setSingleLine(true);
        } else {
            holder.projectDetails.setVisibility(View.GONE);
            holder.projectName.setSingleLine(false);
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

                if (onScratchProjectEditListener != null) {
                    onScratchProjectEditListener.onProjectChecked();
                }
            }
        });

        holder.background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (selectMode != ListView.CHOICE_MODE_NONE) {
                    return true;
                }
                return false;
            }
        });

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder clickedViewHolder = (ViewHolder) v.getTag();
                v.clearFocus();
                v.requestFocus();
                if (clickedViewHolder != null) {
                    Log.d(TAG, "Clicked on list view item: " + clickedViewHolder.projectName);
                } else {
                    Log.d(TAG, "Clicked on list view item");
                }

                // TODO: implement!
                if (selectMode != ListView.CHOICE_MODE_NONE) {
                    Log.d(TAG, "Toggling checkbox");
                    holder.checkbox.setChecked(!holder.checkbox.isChecked());
                } else if (onScratchProjectEditListener != null) {
                    Log.d(TAG, "Project edit listener not set");
                    onScratchProjectEditListener.onProjectEdit(position);
                }
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

    public interface OnScratchProjectEditListener {
        void onProjectChecked();

        void onProjectEdit(int position);
    }
}
