/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.projecthandler;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.LookInfo;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.data.SoundInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.data.brick.PlaceAtBrick;
import org.catrobat.catroid.data.brick.SetLookBrick;
import org.catrobat.catroid.data.brick.SetXBrick;
import org.catrobat.catroid.data.brick.WhenStartedBrick;
import org.catrobat.catroid.formula.Formula;
import org.catrobat.catroid.storage.StorageManager;

import java.io.IOException;

public final class ProjectCreator {

	public static final String TAG = ProjectCreator.class.getSimpleName();

	private ProjectCreator() {
	}

	public static ProjectInfo createDefaultProject(String name, Context context) throws IOException {
		ProjectInfo project = new ProjectInfo(name);

		SceneInfo scene0 = new SceneInfo("Scene 0", project.getDirectoryInfo());
		SceneInfo scene1 = new SceneInfo("Scene 1", project.getDirectoryInfo());

		SpriteInfo background = new SpriteInfo("Background", project.getDirectoryInfo());
		SpriteInfo bird = new SpriteInfo("Bird", project.getDirectoryInfo());
		SpriteInfo cloud0 = new SpriteInfo("Clouds 1", project.getDirectoryInfo());
		SpriteInfo cloud1 = new SpriteInfo("Clouds 2", project.getDirectoryInfo());

		LookInfo look0 = new LookInfo("Background", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_background_portrait, project.getDirectoryInfo(), context));
		LookInfo look1 = new LookInfo("Bird wings up", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_bird_wing_up, project.getDirectoryInfo(), context));
		LookInfo look2 = new LookInfo("Bird wings down", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_bird_wing_down, project.getDirectoryInfo(), context));
		LookInfo look3 = new LookInfo("Cloud 1", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_clouds_portrait, project.getDirectoryInfo(), context));
		LookInfo look4 = new LookInfo("Cloud 2", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_clouds_portrait, project.getDirectoryInfo(), context));

		bird.addBrick(new WhenStartedBrick());
		bird.addBrick(new SetXBrick(new Formula(100)));
		bird.addBrick(new SetLookBrick(look1));
		bird.addBrick(new PlaceAtBrick(new Formula(50), new Formula(60)));

		background.addLook(look0);

		bird.addLook(look1);
		bird.addLook(look2);

		bird.addSound(new SoundInfo("Tweet 1", StorageManager.saveSoundResourceToSDCard(R.raw.default_project_tweet_1,
				project.getDirectoryInfo(), context)));
		bird.addSound(new SoundInfo("Tweet 2", StorageManager.saveSoundResourceToSDCard(R.raw.default_project_tweet_2,
				project.getDirectoryInfo(), context)));

		cloud0.addLook(look3);
		cloud1.addLook(look4);

		scene0.addSprite(background);
		scene0.addSprite(bird);
		scene0.addSprite(cloud0);
		scene0.addSprite(cloud1);

		project.addScene(scene0);
		project.addScene(scene1);

		ProjectHolder.getInstance().serialize(project);

		return project;
	}
}
