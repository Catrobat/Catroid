/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.util.Log;
import at.tugraz.ist.catroid.common.Consts;

/**
 * @author Peter Treitler
 */
public class FileCopyThread extends Thread {
	private File destinationFile;
	private File sourceFile;
	private ProgressDialog progressDialog;

	/**
	 * Creates a thread that copies a file to a given destination upon calling
	 * run().
	 * 
	 * @param destinationFile
	 *            path to the destination file
	 * @param sourceFile
	 *            path to the source file
	 * @param progressDialog
	 *            an open ProgressDialog that will be dismissed upon finishing
	 *            the thread. Can be null.
	 */
	public FileCopyThread(File destinationFile, File sourceFile, ProgressDialog progressDialog) {
		this.destinationFile = destinationFile;
		this.sourceFile = sourceFile;
		this.progressDialog = progressDialog;
	}

	@Override
	public void run() {
		FileInputStream fis;
		FileOutputStream fos;
		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(destinationFile);

			byte[] buf = new byte[Consts.BUFFER_8K];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		} catch (Exception e) {
			Log.e("FileCopyThread", "Error copying file \"" + sourceFile.getPath() + "\" to \""
					+ destinationFile.getPath() + "\".");
		}

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
