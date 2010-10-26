package at.tugraz.ist.catroid.download;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.UtilZip;

public class DownloadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download);
		
		String data = getIntent().getDataString();
		
		System.out.println("data: "+data);
		
//		new ProjectDownloadTask(this, "http://www.url.com", ConstructionSiteActivity.DEFAULT_ROOT+"/downloadedProject",
//					ConstructionSiteActivity.TMP_PATH).execute();
//		
		
	}
}
