package at.tugraz.ist.catroid.download.tasks;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ConnectionWrapper;

public class ProjectDownloadTask extends AsyncTask<Void, Void, Boolean> implements OnClickListener {
	private Activity mActivity;
	private String mProjectName;
	private String mZipFile;
	private String mUrl;
	private ProgressDialog mProgressdialog;
	private boolean result;
	
	public ProjectDownloadTask(Activity activity, String url, String projectName, String zipFile) {
		
		mActivity = activity;
		mProjectName = projectName;
		mZipFile = zipFile;
		mUrl = url;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mActivity == null)
			return;
		String title = mActivity.getString(R.string.please_wait);
		String message = mActivity.getString(R.string.loading);
		mProgressdialog = ProgressDialog.show(mActivity, title,
				message);
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			ConnectionWrapper.doHttpPostFileDownload(mUrl, null, mZipFile);
				
			result = UtilZip.unZipFile(mZipFile, ConstructionSiteActivity.DEFAULT_ROOT + "/"+mProjectName+"/");  
			return result;
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
			//Toast.makeText(mActivity, R.string.error_project_download, Toast.LENGTH_SHORT).show();
			showDialog( R.string.error_project_download);
			return;
		}
		
		if(mActivity == null)
			return;
		Toast.makeText(mActivity, R.string.success_project_download, Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(mActivity, ConstructionSiteActivity.class);
		intent.putExtra(ConstructionSiteActivity.INTENT_EXTRA_ROOT, ConstructionSiteActivity.DEFAULT_ROOT + "/"+mProjectName+"/");
		intent.putExtra(ConstructionSiteActivity.INTENT_EXTRA_SPF_FILE_NAME, mProjectName+ConstructionSiteActivity.DEFAULT_FILE_ENDING);
		mActivity.startActivity(intent);
		
	}
	
	private void showDialog(int messageId) {
		if(mActivity == null)
			return;
		new Builder(mActivity)
			.setMessage(messageId)
			.setPositiveButton("OK", null)
			.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if(!result) 
			mActivity.finish();
		
	}

}
