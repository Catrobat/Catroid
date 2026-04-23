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

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class SetNfcTagBrick extends FormulaBrick implements
		BrickSpinner.OnItemSelectedListener<SetNfcTagBrick.NfcTypeOption> {

	private static final long serialVersionUID = 1L;

	private int nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_HTTPS;

	public SetNfcTagBrick() {
		addAllowedBrickField(BrickField.NFC_NDEF_MESSAGE, R.id.brick_set_nfc_tag_edit_text);
	}

	public SetNfcTagBrick(String messageString) {
		this(new Formula(messageString));
	}

	public SetNfcTagBrick(Formula messageFormula) {
		this();
		setFormulaWithBrickField(BrickField.NFC_NDEF_MESSAGE, messageFormula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_nfc_tag;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new NfcTypeOption(context.getString(R.string.tnf_mime_media), BrickValues.TNF_MIME_MEDIA));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_well_known_http), BrickValues.TNF_WELL_KNOWN_HTTP));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_well_known_https), BrickValues.TNF_WELL_KNOWN_HTTPS));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_well_known_sms), BrickValues.TNF_WELL_KNOWN_SMS));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_well_known_tel), BrickValues.TNF_WELL_KNOWN_TEL));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_well_known_mailto), BrickValues.TNF_WELL_KNOWN_MAILTO));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_external_type), BrickValues.TNF_EXTERNAL_TYPE));
		items.add(new NfcTypeOption(context.getString(R.string.tnf_empty), BrickValues.TNF_EMPTY));

		BrickSpinner<NfcTypeOption> spinner = new BrickSpinner<>(R.id.brick_set_nfc_tag_ndef_record_spinner,
				view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(nfcTagNdefType);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable NfcTypeOption item) {
		nfcTagNdefType = item.getNfcTagNdefType();
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(NFC_ADAPTER);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createSetNfcTagAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.NFC_NDEF_MESSAGE), nfcTagNdefType));
	}

	class NfcTypeOption implements Nameable {

		private String name;
		private int nfcTagNdefType;

		NfcTypeOption(String name, int nfcTagNdefType) {
			this.name = name;
			this.nfcTagNdefType = nfcTagNdefType;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		int getNfcTagNdefType() {
			return nfcTagNdefType;
		}
	}
}
