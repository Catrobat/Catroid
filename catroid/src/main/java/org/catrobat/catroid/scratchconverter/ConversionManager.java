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

package org.catrobat.catroid.scratchconverter;

import android.app.Activity;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.scratchconverter.Client.ConnectAuthCallback;
import org.catrobat.catroid.scratchconverter.Client.ConvertCallback;
import org.catrobat.catroid.scratchconverter.Client.DownloadCallback;
import org.catrobat.catroid.ui.scratchconverter.BaseInfoViewListener;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;

public interface ConversionManager extends ConnectAuthCallback, ConvertCallback, DownloadCallback {
	void setCurrentActivity(Activity activity);
	void connectAndAuthenticate();
	void shutdown();
	void convertProgram(final long jobID, final String title, final WebImage image, final boolean force);
	void addBaseInfoViewListener(BaseInfoViewListener baseInfoViewListener);
	boolean removeBaseInfoViewListener(BaseInfoViewListener baseInfoViewListener);
	void addGlobalJobViewListener(JobViewListener jobViewListener);
	boolean removeGlobalJobViewListener(JobViewListener jobViewListener);
	void addJobViewListener(long jobID, JobViewListener jobViewListener);
	boolean removeJobViewListener(long jobID, JobViewListener jobViewListener);
	void addGlobalDownloadCallback(DownloadCallback callback);
	boolean removeGlobalDownloadCallback(Client.DownloadCallback callback);
	boolean isJobInProgress(long jobID);
	boolean isJobDownloading(long jobID);
	int getNumberOfJobsInProgress();
}
