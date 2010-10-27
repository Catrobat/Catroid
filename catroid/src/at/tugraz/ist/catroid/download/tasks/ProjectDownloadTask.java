package at.tugraz.ist.catroid.download.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.UtilZip;

public class ProjectDownloadTask extends AsyncTask<Void, Void, Boolean> {
	private Context mContext;
	private String mDestProjectPath;
	private String mZipFile;
	private String mUrl;
	private ProgressDialog mProgressdialog;
	
	public ProjectDownloadTask(Context context, String url, String destProjectPath, String zipFile) {
		mContext = context;
		mDestProjectPath = destProjectPath;
		mZipFile = zipFile;
		mUrl = url;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String title = mContext.getString(R.string.please_wait);
		String message = mContext.getString(R.string.loading);
		mProgressdialog = ProgressDialog.show(mContext, title,
				message);
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			ConnectionWrapper.doHttpPostFileDownload(mUrl, null, mZipFile);
				
			return UtilZip.unZipFile(mZipFile, mDestProjectPath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;	
		
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	
		if(mProgressdialog != null && mProgressdialog.isShowing())
			mProgressdialog.dismiss();
		
		if(!result) {
			Toast.makeText(mContext, R.string.error_project_upload, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Toast.makeText(mContext, R.string.success_project_upload, Toast.LENGTH_SHORT).show();
		
	}

}
