/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatrobatProject;
import org.catrobat.catroid.ui.adapter.ProjectAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectLoaderFromAPI extends AppCompatActivity {

	private TextView textView;
	private RecyclerView rv ;
	private List<CatrobatProject> projectList;
	private ProjectAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list_view);


		textView = findViewById(R.id.empty_view);
		rv = findViewById(R.id.recycler_view);
		rv.setVisibility(View.VISIBLE);
		textView.setVisibility(View.GONE);
		findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

		Intent intent = getIntent();
		String Path =  intent.getStringExtra("path");

		getData(Path);

	}

	public void getData(String Path) {

		OkHttpClient client = new OkHttpClient();
		String base_URL = "https://share.catrob.at/";
		String url = base_URL + Path;
		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				call.cancel();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				projectList = new ArrayList<>();
				String response_string = response.body().string();
				Log.d("projectloader", "onResponse: " + response_string);
				JSONArray jsonArray = new JSONArray();
				try {
					jsonArray = new JSONObject(response_string).getJSONArray(
							"CatrobatProjects");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				for(int i=0; i<jsonArray.length(); i++){
					try {
						Log.d("projectloader", "onResponse: d" + jsonArray.getJSONObject(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}

					CatrobatProject catrobatProject = null;
					try {
						JSONObject obj = jsonArray.getJSONObject(i);
						catrobatProject = new CatrobatProject(obj.getInt(
								"ProjectId"), obj.getString("ProjectName"), obj.getString(
										"Author"), obj.getString("Description"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					projectList.add(catrobatProject);
				}


				ProjectLoaderFromAPI.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter = new ProjectAdapter(rv.getContext(), projectList);
						rv.setAdapter(adapter);
						findViewById(R.id.progress_bar).setVisibility(View.GONE);
					}
				});
			}
		});
	}
}