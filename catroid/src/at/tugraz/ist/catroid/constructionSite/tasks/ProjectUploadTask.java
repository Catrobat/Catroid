package at.tugraz.ist.catroid.constructionSite.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class ProjectUploadTask extends AsyncTask<Void, Void, Boolean> {
	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String FILE_UPLOAD_URL = "http://www.catroid.org/catroid/upload/upload.http";
	
	private Context mContext;
	private String mProjectPath;
	private String mZipFile;
	private ProgressDialog mProgressdialog;
	private String mProjectName;
	private String resultString;
	
	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}
	
	public ProjectUploadTask(Context context, String projectName, String projectPath, String zipFile) {
		mContext = context;
		mProjectPath = projectPath;
		mZipFile = zipFile;
		mProjectName = projectName;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mContext == null)
			return;
		String title = mContext.getString(R.string.please_wait);
		String message = mContext.getString(R.string.loading);
		mProgressdialog = ProgressDialog.show(mContext, title,
				message);
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			System.out.println("___________asdfasfasfd: "+mProjectPath);
			File dirPath = new File(mProjectPath);
			String[] pathes = dirPath.list(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					if(filename.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING) || filename.equalsIgnoreCase("images")
							|| filename.equalsIgnoreCase("sounds"))
						return true;
					return false;
				}
			});
			System.out.println("asdfasfasfd");
			if(pathes == null)
				return false;
			
			
			for(int i=0;i<pathes.length;++i) {
				pathes[i] = dirPath +"/"+ pathes[i];
			}	
			
			File file = new File(mZipFile);
	    	if(!file.exists()) {
	    		file.getParentFile().mkdirs();
	    		file.createNewFile();
	    	}
	    	System.out.println("file: "+mZipFile);
			if (!UtilZip.writeToZipFile(pathes, mZipFile)) {
				file.delete();
				return false;
			}
			System.out.println("file: after");
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put(PROJECT_NAME_TAG, mProjectName);
			resultString = createConnection().doHttpPostFileUpload(FILE_UPLOAD_URL, hm, FILE_UPLOAD_TAG, mZipFile);
			
			//file.delete();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WebconnectionException e) {
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
			showDialog(R.string.error_project_upload); 
			return;
		}
		
		showDialog(R.string.success_project_upload);
		
	}
	
	private void showDialog(int messageId) {
		if(mContext == null)
			return;
		new Builder(mContext)
			.setMessage(messageId)
			.setPositiveButton("OK", null)
			.show();
	}
	
	public String getResultString() {
		return resultString;
	}

	
}
