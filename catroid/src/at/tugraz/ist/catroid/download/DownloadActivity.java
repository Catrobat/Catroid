package at.tugraz.ist.catroid.download;

import java.net.URLDecoder;

import android.app.Activity;
import android.os.Bundle;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;

public class DownloadActivity extends Activity {
	private static final String PROJECTNAME_TAG = "fname=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download);
		
		String zipUrl = getIntent().getDataString();
		
		System.out.println("data: "+zipUrl);
		if(zipUrl == null || zipUrl.length() <= 0)
			return;
		
		String projectName = getProjectName(zipUrl);
		
		new ProjectDownloadTask(this, zipUrl, projectName,
					ConstructionSiteActivity.TMP_PATH+"/down.zip").execute();  
		
		
	}
	
	private String getProjectName(String zipUrl) {
		int projectNameIndex = zipUrl.lastIndexOf(PROJECTNAME_TAG)+PROJECTNAME_TAG.length();
		String projectName =  zipUrl.substring(projectNameIndex);
		projectName = URLDecoder.decode(projectName);
		
		return projectName;
	}
	
}
