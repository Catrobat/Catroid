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

package org.catrobat.catroid.ui.dialogs.regexassistant;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.web.WebpageUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

public class RegularExpressionAssistantDialog {

	static List<RegularExpressionFeature> listOfFeatures;
	List<String> namesOfFeatures;
	Context context;
	FragmentManager fragmentManager;

	public RegularExpressionAssistantDialog(Context context, FragmentManager fragmentManager) {
		this.context = context;
		this.fragmentManager = fragmentManager;
		createListOfFeatures();
	}

	public void createAssistant() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.formula_editor_dialog_regular_expression_assistant_title);
		builder.setNegativeButton(R.string.help, (dialog, id) -> WebpageUtils.openWikiPage(context));
		builder.setPositiveButton(R.string.cancel, null);

		buildListOfFeatures(builder);

		AlertDialog dialog = builder.create();

		dialog.show();
	}

	private void buildListOfFeatures(AlertDialog.Builder builder) {
		namesOfFeatures = new ArrayList<>();
		for (RegularExpressionFeature rf : listOfFeatures) {
			namesOfFeatures.add(context.getString(rf.getTitleId()));
		}

		builder.setItems(namesOfFeatures.toArray(new String[0]),
				(dialog, indexInList) -> listOfFeatures.get(indexInList).openDialog(context));
	}

	private void createListOfFeatures() {
		listOfFeatures = new ArrayList<>();
		listOfFeatures.add(new HtmlExtractorDialog(fragmentManager));
		listOfFeatures.add(new JsonExtractorDialog(fragmentManager));
	}
}
