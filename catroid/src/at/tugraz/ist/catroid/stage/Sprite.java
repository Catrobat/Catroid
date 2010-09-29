package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Pair;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;

public class Sprite {
	public DrawObject mDrawObject;
	public LinkedList<Script> mScriptList;
	public String mName;

	public Sprite(Pair<String, ArrayList<HashMap<String, String>>> blockList) {
		mName = blockList.first;
		generateScripts(blockList.second);
		mDrawObject = new DrawObject();
	}

	
	void generateScripts(ArrayList<HashMap<String, String>> blockList) {
		for (int i = 0; i < blockList.size(); i++) {

			if (blockList.get(i).get(BrickDefine.BRICK_TYPE) == String
					.valueOf(BrickDefine.TOUCHED)
					&& i > 0) {
				ArrayList<HashMap<String, String>> frontBlockList = (ArrayList<HashMap<String, String>>) blockList
						.subList(0, i);
				ArrayList<HashMap<String, String>> backBlockList = (ArrayList<HashMap<String, String>>) blockList
						.subList(i, blockList.size());

				mScriptList.add(new Script(mDrawObject, frontBlockList));
				generateScripts(backBlockList);
				return;
			}

		}
		mScriptList.add(new Script(mDrawObject, blockList));
	}
}
