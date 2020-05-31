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

import android.os.Message;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.web.WebConnection;
import org.catrobat.catroid.web.WebConnectionFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import androidx.annotation.IntDef;
import androidx.annotation.VisibleForTesting;

public class WebRequestAction extends Action implements WebConnection.WebRequestListener {
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

	@IntDef({UNKNOWN, PENDING, DENIED, GRANTED})
	private @interface PermissionStatus {}
	private static final int UNKNOWN = 0;
	private static final int PENDING = 1;
	private static final int DENIED = 2;
	private static final int GRANTED = 3;

	private @RequestStatus int requestStatus = NOT_SENT;
	private @PermissionStatus int permissionStatus = UNKNOWN;
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

	private boolean interpretUrl() {
		try {
			url = formula.interpretString(sprite);
			if (!url.startsWith("http://") && !url.startsWith("https://")) {
				url = "https://" + url;
			}
			return true;
		} catch (InterpretationException exception) {
			Log.d(getClass().getSimpleName(), "Couldn't interpret formula", exception);
			return false;
		}
	}

	private void askForPermission() {
		if (StageActivity.messageHandler == null) {
			denyPermission();
			return;
		}

		permissionStatus = PENDING;
		ArrayList<Object> params = new ArrayList<>();
		params.add(this);
		params.add(url);
		Message message = StageActivity.messageHandler.obtainMessage(StageActivity.REQUEST_PERMISSION, params);
		message.sendToTarget();
	}

	public void grantPermission() {
		permissionStatus = GRANTED;
	}

	public void denyPermission() {
		permissionStatus = DENIED;
	}

	@Override
	public boolean act(float delta) {
		if (userVariable == null) {
			return true;
		}

		if (url == null && !interpretUrl()) {
			return true;
		}

		if (permissionStatus == UNKNOWN) {
			if (ProjectManager.checkIfURLIsInWhitelist(url)) {
				grantPermission();
			} else {
				askForPermission();
			}
		}

		if (permissionStatus == PENDING) {
			return false;
		} else if (permissionStatus == DENIED) {
			userVariable.setValue(Integer.toString(Constants.ERROR_AUTHENTICATION_REQUIRED));
			return true;
		}

		if (requestStatus == NOT_SENT) {
			requestStatus = WAITING;

			webConnection = webConnectionFactory.createWebConnection(url, this);
			if (!StageActivity.stageListener.webConnectionHolder.addConnection(webConnection)) {
				userVariable.setValue(Integer.toString(Constants.ERROR_TOO_MANY_REQUESTS));
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
		permissionStatus = UNKNOWN;
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
