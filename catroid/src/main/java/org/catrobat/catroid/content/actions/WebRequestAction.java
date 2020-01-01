/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import androidx.annotation.IntDef;
import androidx.annotation.VisibleForTesting;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.web.WebConnection;
import org.catrobat.catroid.web.WebConnectionFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class WebRequestAction extends Action implements WebConnection.WebRequestListener {

	private static final Double ERROR_TOO_MANY_REQUESTS = 429d;

	private Sprite sprite;
	private Formula formula;
	private String url;
	private UserVariable userVariable;
	private WebConnectionFactory webConnectionFactory;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NOT_SENT, WAITING, FINISHED})
	private @interface RequestStatus {}
	public static final int NOT_SENT = 0;
	public static final int WAITING = 1;
	public static final int FINISHED = 2;

	private @RequestStatus int requestStatus = NOT_SENT;
	private WebConnection webConnection = null;
	private String response = null;

	public WebRequestAction() {
	}

	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setWebConnectionFactory(WebConnectionFactory webConnectionFactory) {
		this.webConnectionFactory = webConnectionFactory;
	}

	public void interpretUrl() {
		try {
			url = formula.interpretString(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Couldn't interpret formula", interpretationException);
		}
	}

	@Override
	public boolean act(float delta) {
		if (userVariable == null) {
			return true;
		}

		if (requestStatus == NOT_SENT) {
			requestStatus = WAITING;
			webConnection = webConnectionFactory.createWebConnection(url, this);
			if (!StageActivity.stageListener.webConnectionHolder.addConnection(webConnection)) {
				userVariable.setValue(ERROR_TOO_MANY_REQUESTS);
				return true;
			}

			webConnection.sendWebRequest();
		}

		if (requestStatus == FINISHED) {
			userVariable.setValue(response);
			StageActivity.stageListener.webConnectionHolder.removeConnection(webConnection);
			return true;
		}

		return false;
	}

	@Override
	public void restart() {
		response = null;
		StageActivity.stageListener.webConnectionHolder.removeConnection(webConnection);
		webConnection = null;
		requestStatus = NOT_SENT;
	}

	@Override
	public void onRequestFinished(String responseString) {
		requestStatus = FINISHED;
		response = responseString;
	}

	@Override
	public void onCancelledCall() {
		response = null;
		webConnection = null;
		requestStatus = NOT_SENT;
	}

	@VisibleForTesting
	public @RequestStatus int getRequestStatus() {
		return requestStatus;
	}
}
