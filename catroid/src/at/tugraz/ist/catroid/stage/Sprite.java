package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Pair;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;

public class Sprite implements Comparable<Sprite> {
	public DrawObject mDrawObject;
	public LinkedList<Script> mScriptList;
	public String mName;

	public Sprite(Pair<String, ArrayList<HashMap<String, String>>> blockList) {
		mName = blockList.first;
		mDrawObject = new DrawObject();
		mScriptList = new LinkedList<Script>();
		if (blockList.second != null)
			generateScripts(blockList.second);
		markTouchScripts();
		int helper = 2;
	}

	void generateScripts(ArrayList<HashMap<String, String>> blockList) {
		for (int i = 0; i < blockList.size(); i++) {

			if (blockList.get(i).get(BrickDefine.BRICK_TYPE) == String
					.valueOf(BrickDefine.TOUCHED)
					&& i > 0) {
				ArrayList<HashMap<String, String>> frontBlockList = getSublist(
						blockList, 0, i);
				ArrayList<HashMap<String, String>> backBlockList = getSublist(
						blockList, i, blockList.size());

				mScriptList.add(new Script(mDrawObject, frontBlockList));
				generateScripts(backBlockList);
				return;
			}

		}
		mScriptList.add(new Script(mDrawObject, blockList));

	}

	public void start() {
		for (int i = 0; i < mScriptList.size(); i++) {
			mScriptList.get(i).start();
		}
	}

	public void pause() {
		for (int i = 0; i < mScriptList.size(); i++) {
			mScriptList.get(i).pause();
		}
	}

	public void unPause() {
		for (int i = 0; i < mScriptList.size(); i++) {
			mScriptList.get(i).endPause();
		}
	}

	public int compareTo(Sprite sprite) {
		return this.mDrawObject.getZOrder() - sprite.mDrawObject.getZOrder();
	}

	private void markTouchScripts() {
		for (int i = 0; i < mScriptList.size(); i++) {
			if (mScriptList.get(i).mScriptData.get(0).get(
					BrickDefine.BRICK_TYPE) == String
					.valueOf(BrickDefine.TOUCHED)) {
				mScriptList.get(i).mIsTouchScript = true;
			}
		}
	}

	private ArrayList<HashMap<String, String>> getSublist(
			ArrayList<HashMap<String, String>> list, int startIndex,
			int endIndex) {
		ArrayList<HashMap<String, String>> subList = new ArrayList<HashMap<String, String>>();
		for (int i = startIndex; i < endIndex; i++) {
			subList.add(list.get(i));
		}
		return subList;
	}
}
