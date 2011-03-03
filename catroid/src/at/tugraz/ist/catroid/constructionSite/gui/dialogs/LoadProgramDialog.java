package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class LoadProgramDialog extends Dialog {

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

		File sdFile = new File(ConstructionSiteActivity.DEFAULT_ROOT);
		searchForFile(sdFile);

		mAdapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1, mAdapterFileList);

		mListView = (ListView) findViewById(R.id.loadfilelist);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				File file = new File(mAdapter.getItem(arg2));
				ConstructionSiteActivity.setRoot(file.getParent(), file.getName());
				if(!mContentManager.loadContent(file.getName())){
				    //TODO: error message
				}

				((Activity) mCtx).setTitle(file.getName());
				dismiss();
			}
		});

		super.onCreate(savedInstanceState);
	}

	public void searchForFile(File file) {
		File[] sdFileList = file.listFiles();
		int length = 0;
		if (sdFileList != null) {
			length = sdFileList.length;
		}
		for (int i = 0; i < length; i++) {
			if (sdFileList[i].isFile()) {
				if (sdFileList[i].getName().contains(ConstructionSiteActivity.DEFAULT_FILE_ENDING)) {
					mAdapterFileList.add(sdFileList[i].getAbsolutePath());
				}
			} else {
				searchForFile(sdFileList[i]);
			}
		}
	}
}
