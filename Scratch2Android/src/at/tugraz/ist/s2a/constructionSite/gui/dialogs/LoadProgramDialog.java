package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;

public class LoadProgramDialog extends Dialog{

	private Context mCtx;
	private ListView mListView;
	private ContentManager mContentManager;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mAdapterFileList;
	
	public LoadProgramDialog(Context context, ContentManager contentmanager) {
		super(context);
		mCtx = context;
		mContentManager = contentmanager;
		mAdapterFileList = new ArrayList<String>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_load_program_layout);
		
		
    	//TODO fix static path
    	File sdFile = new File("/sdcard/");
    	File[] sdFileList = sdFile.listFiles();
    	
    	
    	searchForFile(sdFile);
    	
//    	for(int i=0; i<sdFileList.length; i++)
//    	{
//    		if(sdFileList[i].isFile()){
//    			if(sdFileList[i].getName().contains(".spf")){
//    				adapterFileList.add(sdFileList[i].getName());
//    			}
//    		}
//    		else{
//    			File fileList = new File(sdFileList[i].toString());
//    	    	File[] sdFileList = sdFile.listFiles();
//    		}
//    	}
    	mAdapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1, mAdapterFileList);
    	
		mListView = (ListView)findViewById(R.id.loadfilelist);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mContentManager.loadContent(mAdapter.getItem(arg2));
				dismiss();
			}
		});

    	
		super.onCreate(savedInstanceState);
	}
	
	public void searchForFile(File file){
		File[] sdFileList = file.listFiles();
		for(int i=0; i<sdFileList.length; i++)
    	{
    		if(sdFileList[i].isFile()){
    			if(sdFileList[i].getName().contains(".spf")){
    				mAdapterFileList.add(sdFileList[i].getName());
    			}
    		}
    		else{
    			searchForFile(sdFileList[i]);
    		}
    	}
	}
	
	

}
