package at.tugraz.ist.catroid.download;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.web.ConnectionWrapper;

public class DownloadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download);
		
		String data = getIntent().getDataString();
		
		System.out.println("data: "+data);
		
//		try {
//			ConnectionWrapper.doHttpPostFileDownload(data, null, 
//					ConstructionSiteActivity.TMP_PATH+"/down.zip");
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
