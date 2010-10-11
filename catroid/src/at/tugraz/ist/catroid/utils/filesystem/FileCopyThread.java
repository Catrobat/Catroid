/**
 * 
 */
package at.tugraz.ist.catroid.utils.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.util.Log;

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

			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		} catch (Exception e) {
			Log.e("FileCopyThread", "Error copying file \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".");
		}
		
		if(progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}
}
