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

package org.catrobat.catroid.ui.recyclerview.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;

public class ScratchJobVH extends ViewHolder {

	public RelativeLayout background;
	public ProgressBar progressBar;
	public ImageView image;
	public RelativeLayout details;
	public TextView progress;
	public RelativeLayout progressLayout;
	public TextView title;
	public TextView status;

	public ScratchJobVH(View view) {
		super(view);
		background = (RelativeLayout) view.findViewById(R.id.scratch_job_list_item_background);
		image = (ImageView) view.findViewById(R.id.scratch_project_image_view);
		title = (TextView) view.findViewById(R.id.scratch_job_list_item_title);
		image = (ImageView) view.findViewById(R.id.scratch_job_list_item_image);
		status = (TextView) view.findViewById(R.id.scratch_job_list_item_status);
		progressLayout = (RelativeLayout) view.findViewById(R.id.scratch_job_list_item_progress_layout);
		progressBar = (ProgressBar) view.findViewById(R.id.scratch_job_list_item_progress_bar);
		progress = (TextView) view.findViewById(R.id.scratch_job_list_item_progress_text);
		details = (RelativeLayout) view.findViewById(R.id.scratch_job_details);
	}
}
