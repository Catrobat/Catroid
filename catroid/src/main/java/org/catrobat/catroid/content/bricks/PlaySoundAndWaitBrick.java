/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;

@CatrobatLanguageBrick(command = "Start sound and wait")
public class PlaySoundAndWaitBrick extends PlaySoundBrick implements CatrobatLanguageAttributes {

	private static final long serialVersionUID = 1L;

	public PlaySoundAndWaitBrick() {
	}

	@Override
	protected void onViewCreated(View prototypeView) {
		((TextView) view.findViewById(R.id.brick_play_sound_text_view))
				.setText(R.string.brick_play_sound_and_wait);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (sound == null || sound.getFile() == null || !sprite.getSoundList().contains(sound)) {
			return;
		}
		sequence.addAction(sprite.getActionFactory().createPlaySoundAction(sprite, sound));
		sequence.addAction(sprite.getActionFactory().createWaitForSoundAction(sprite, sequence,
				new Formula(getDurationOfSound()), sound.getFile().getAbsolutePath()));
	}

	private float getDurationOfSound() {
		float duration;
		MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
		metadataRetriever.setDataSource(sound.getFile().getAbsolutePath());
		duration = Integer.parseInt(metadataRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000.0f;
		return duration;
	}

	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageParameterizedCall(indentionLevel, false).toString();
	}

	@Override
	public void appendCatrobatLanguageArguments(StringBuilder brickBuilder) {
		String soundName = "";
		if (sound != null) {
			soundName = CatrobatLanguageUtils.formatSoundName(sound.getName());
		}

		brickBuilder.append("sound: (")
				.append(soundName)
				.append(')');
	}

	@Override
	protected Collection<String> getRequiredArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredArgumentNames());
		requiredArguments.add("sound");
		return requiredArguments;
	}
}
