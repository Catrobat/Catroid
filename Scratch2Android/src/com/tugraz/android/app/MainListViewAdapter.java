package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class MainListViewAdapter extends BaseAdapter{
    private Context mCtx;
    private ArrayList<HashMap<String, String>> mList;
    private HashMap<Integer, Integer> mViewElementMap;
    private HashMap<Integer, String[]> mFromMap;
    private HashMap<Integer, int[]> mToMap;
    
	public MainListViewAdapter(Context context,
			ArrayList<HashMap<String, String>> data, HashMap<Integer, Integer> viewElementMap, HashMap<Integer, String[]> fromMap,
					HashMap<Integer, int[]> toMap) {
		mCtx = context;
		mList = data;
		mViewElementMap = viewElementMap;
		mFromMap = fromMap;
		mToMap = toMap;		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
