/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.ui.recyclerview.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.SignInButton
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.ui.OnBoardingWelcomeActivity
import org.catrobat.catroid.utils.setVisibleOrGone

class OnBoardingWelcomeFragment(pos: Int) : Fragment() {
    private var position = pos

    enum class OnBoardingSteps {
        WELCOME,
        COMPLETE_CONTROL,
        JOIN_COMMUNITY,
        BEGIN
    }

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var normalButton: Button
    private lateinit var tutorialButton: Button
    private lateinit var googleSignInButton: SignInButton
    private lateinit var loggedInLabelView: TextView
    private lateinit var loggedInUserView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (position) {
            OnBoardingSteps.WELCOME.ordinal -> inflater.inflate(
                R.layout.fragment_onboarding_step_welcome,
                container, false
            )
            OnBoardingSteps.COMPLETE_CONTROL.ordinal -> inflater.inflate(
                R.layout.fragment_onboarding_step_complete_control,
                container,
                false
            )
            OnBoardingSteps.JOIN_COMMUNITY.ordinal -> inflater.inflate(
                R.layout.fragment_onboarding_step_join_community,
                container,
                false
            )
            OnBoardingSteps.BEGIN.ordinal -> inflater.inflate(
                R.layout.fragment_onboarding_step_begin,
                container,
                false
            )
            else -> inflater.inflate(R.layout.fragment_onboarding_step_welcome, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (position) {
            OnBoardingSteps.WELCOME.ordinal -> setupWelcomeStep(view)
            OnBoardingSteps.JOIN_COMMUNITY.ordinal -> setupJoinCommunityStep(view)
            OnBoardingSteps.BEGIN.ordinal -> setupBeginStep(view)
        }
    }

    fun onSuccessfulLogin(username: String) {
        loginButton.setVisibleOrGone(false)
        registerButton.setVisibleOrGone(false)
        googleSignInButton.setVisibleOrGone(false)

        loggedInLabelView.setVisibleOrGone(true)
        loggedInUserView.text = username
        loggedInUserView.setVisibleOrGone(true)
    }

    private fun setupWelcomeStep(view: View) {
        val websiteLinkView = view.findViewById<TextView>(R.id.website_link)
        websiteLinkView.movementMethod = LinkMovementMethod.getInstance()

        val catrobatUrl = getString(
            R.string.about_link_template, Constants.CATROBAT_ABOUT_URL,
            getString(R.string.website)
        )
        websiteLinkView.text = HtmlCompat.fromHtml(
            getString(R.string.click_visit_app) + " " + catrobatUrl,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setupJoinCommunityStep(view: View) {
        val onBoardingWelcomeActivity = requireActivity() as OnBoardingWelcomeActivity

        loginButton = view.findViewById(R.id.login)
        registerButton = view.findViewById(R.id.register)
        googleSignInButton = view.findViewById(R.id.sign_in_google_login_button)
        loggedInLabelView = view.findViewById(R.id.loggedInLabel)
        loggedInUserView = view.findViewById(R.id.loggedInUser)

        loginButton.setOnClickListener(View.OnClickListener { onBoardingWelcomeActivity.onLoginClicked() })
        registerButton.setOnClickListener(View.OnClickListener { onBoardingWelcomeActivity.onRegisterClicked() })
        googleSignInButton.setOnClickListener(View.OnClickListener { onBoardingWelcomeActivity.onSignInWithGoogleClicked() })

        loginButton.setVisibleOrGone(true)
        registerButton.setVisibleOrGone(true)
        googleSignInButton.setVisibleOrGone(true)

        loggedInLabelView.setVisibleOrGone(false)
        loggedInUserView.setVisibleOrGone(false)
    }

    private fun setupBeginStep(view: View) {
        val onBoardingWelcomeActivity = requireActivity() as OnBoardingWelcomeActivity

        normalButton = view.findViewById(R.id.normal)
        tutorialButton = view.findViewById(R.id.tutorial)

        normalButton.setOnClickListener(View.OnClickListener { onBoardingWelcomeActivity.onNormalModeClicked() })
        tutorialButton.setOnClickListener(View.OnClickListener { onBoardingWelcomeActivity.onTutorialModeClicked() })
    }

    companion object {
        val TAG = OnBoardingWelcomeFragmentContainer::class.java.simpleName
    }
}
