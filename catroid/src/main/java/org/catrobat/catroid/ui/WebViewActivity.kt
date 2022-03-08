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
package org.catrobat.catroid.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_HORIZONTAL
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.SharedPreferences
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.CookieManager
import android.webkit.URLUtil.guessFileName
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.AUTHENTICATION_COOKIE_NAME
import org.catrobat.catroid.common.Constants.BASE_APP_URL_HTTPS
import org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION
import org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION
import org.catrobat.catroid.common.Constants.FLAVOR_DEFAULT
import org.catrobat.catroid.common.Constants.MAIN_URL_HTTPS
import org.catrobat.catroid.common.Constants.MEDIA_LIBRARY_CACHE_DIR
import org.catrobat.catroid.common.Constants.NO_TOKEN
import org.catrobat.catroid.common.Constants.PLATFORM_DEFAULT
import org.catrobat.catroid.common.Constants.REFRESH_TOKEN
import org.catrobat.catroid.common.Constants.REFRESH_TOKEN_COOKIE_NAME
import org.catrobat.catroid.common.Constants.TOKEN
import org.catrobat.catroid.common.Constants.WHATSAPP_URI
import org.catrobat.catroid.common.FlavoredConstants.BASE_URL_HTTPS
import org.catrobat.catroid.common.FlavoredConstants.CATROBAT_HELP_URL
import org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BASE_URL
import org.catrobat.catroid.transfers.TokenTask
import org.catrobat.catroid.ui.MainMenuActivity.surveyCampaign
import org.catrobat.catroid.utils.MediaDownloader
import org.catrobat.catroid.utils.ProjectDownloadUtil
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.Utils.checkIsNetworkAvailableAndShowErrorMessage
import org.catrobat.catroid.utils.Utils.setLoginCookies
import org.catrobat.catroid.web.GlobalProjectDownloadQueue.queue
import org.catrobat.catroid.web.ProjectDownloader
import org.catrobat.catroid.web.ProjectDownloader.Companion.getProjectNameFromUrl
import org.catrobat.catroid.web.ServerAuthenticationConstants.DEPRECATED_TOKEN_LENGTH
import org.koin.android.ext.android.inject
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var allowGoBack = false
    private var forceOpenInApp = false
    private lateinit var progressDialog: ProgressDialog
    private var webViewLoadingDialog: ProgressDialog? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val tokenTask: TokenTask by inject()
    private var resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent.getStringExtra(INTENT_PARAMETER_URL) ?: BASE_URL_HTTPS

        forceOpenInApp = intent.getBooleanExtra(INTENT_FORCE_OPEN_IN_APP, false)

        webView = findViewById(R.id.webView)
        webView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.app_background, null))
        webView.webViewClient = MyWebViewClient()
        webView.settings.javaScriptEnabled = true
        val language = CURRENT_CATROBAT_LANGUAGE_VERSION.toString()
        val version = Utils.getVersionName(applicationContext)
        val buildType = if (BuildConfig.FLAVOR == "pocketCodeBeta") "debug" else BuildConfig.BUILD_TYPE
        webView.settings.userAgentString = "Catrobat/$language $FLAVOR_DEFAULT/$version Platform/$PLATFORM_DEFAULT BuildType/$buildType"

        sharedPreferences = getDefaultSharedPreferences(this)

        setCookies(url)

        webView.setDownloadListener { downloadUrl: String, _: String?, contentDisposition: String, mimetype: String?, _: Long ->
            startDownload(downloadUrl, contentDisposition, mimetype)
        }
    }

    private fun setCookies(url: String) {
        val token = sharedPreferences.getString(TOKEN, NO_TOKEN).orEmpty()
        when (token.length) {
            DEPRECATED_TOKEN_LENGTH -> checkDeprecatedToken(token, url)
            else -> {
                setLoginCookies(url, sharedPreferences, CookieManager.getInstance())
                webView.loadUrl(url)
            }
        }
    }

    private fun startDownload(downloadUrl: String, contentDisposition: String, mimetype: String?) {
        if (getExtensionFromContentDisposition(contentDisposition).contains(CATROBAT_EXTENSION) && !downloadUrl.contains(LIBRARY_BASE_URL)) {
            ProjectDownloader(queue, downloadUrl, ProjectDownloadUtil).download(this)
        } else if (downloadUrl.contains(LIBRARY_BASE_URL)) {
            val fileName = guessFileName(downloadUrl, contentDisposition, mimetype)

            MEDIA_LIBRARY_CACHE_DIR.mkdirs()
            if (!MEDIA_LIBRARY_CACHE_DIR.isDirectory) {
                Log.e(TAG, "Cannot create $MEDIA_LIBRARY_CACHE_DIR")
                return
            }

            val file = File(MEDIA_LIBRARY_CACHE_DIR, fileName)
            resultIntent.putExtra(MEDIA_FILE_PATH, file.absolutePath)
            MediaDownloader(this).startDownload(this, downloadUrl, fileName, file.absolutePath)
        } else {
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
            val projectName = getProjectNameFromUrl(downloadUrl)
            request.run {
                setTitle(getString(R.string.notification_download_title_pending) + " " + projectName)
                setDescription(getString(R.string.notification_download_pending))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, projectName + ANDROID_APPLICATION_EXTENSION)
                setMimeType(mimetype)
            }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK && webView.canGoBack()) {
            allowGoBack = false
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView, urlClient: String, favicon: Bitmap?) {
            if (webViewLoadingDialog == null && !allowGoBack) {
                webViewLoadingDialog = ProgressDialog(view.context, R.style.WebViewLoadingCircle)
                webViewLoadingDialog?.run {
                    setCancelable(true)
                    setCanceledOnTouchOutside(false)
                    setProgressStyle(android.R.style.Widget_ProgressBar_Small)
                    show()
                }
            } else if (allowGoBack && (urlClient == BASE_URL_HTTPS || urlClient == BASE_APP_URL_HTTPS)) {
                allowGoBack = false
                onBackPressed()
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            allowGoBack = true
            webViewLoadingDialog?.let {
                webViewLoadingDialog?.dismiss()
                webViewLoadingDialog = null
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            url?.let {
                if (url.startsWith(WHATSAPP_URI)) {
                    if (isWhatsappInstalled) {
                        val uri = Uri.parse(url)
                        val intent = Intent(ACTION_VIEW, uri)
                        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    } else {
                        ToastUtil.showError(baseContext, R.string.error_no_whatsapp)
                    }
                    return true
                } else if (!forceOpenInApp && checkIfWebViewVisitExternalWebsite(url)) {
                    val uri = Uri.parse(url)
                    val intent = Intent(ACTION_VIEW, uri)
                    startActivity(intent)
                    return true
                }
            }

            return false
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            if (checkIsNetworkAvailableAndShowErrorMessage(this@WebViewActivity)) {
                ToastUtil.showError(baseContext, R.string.error_unknown_error)
            }

            when (errorCode) {
                ERROR_CONNECT, ERROR_FILE_NOT_FOUND, ERROR_HOST_LOOKUP, ERROR_TIMEOUT, ERROR_PROXY_AUTHENTICATION, ERROR_UNKNOWN -> {
                    setContentView(R.layout.activity_network_error)
                    setSupportActionBar(findViewById(R.id.toolbar))
                    supportActionBar?.setIcon(R.drawable.pc_toolbar_icon)
                    supportActionBar?.setTitle(R.string.app_name)
                }
                else -> Log.e(TAG, "couldn't connect to the server! info: $description : $errorCode")
            }
        }

        private fun checkIfWebViewVisitExternalWebsite(url: String): Boolean =
            (!url.contains(MAIN_URL_HTTPS) || url.contains(CATROBAT_HELP_URL)) && !url.contains(LIBRARY_BASE_URL)
    }

    fun createProgressDialog(mediaName: String) {
        progressDialog = ProgressDialog(this)

        progressDialog.run {
            setTitle(getString(R.string.notification_download_title_pending) + mediaName)
            setMessage(getString(R.string.notification_download_pending))
            setProgressStyle(STYLE_HORIZONTAL)
            progress = 0
            max = PROGRESS_DIALOG_MAX
            setProgressNumberFormat(null)
        }

        if (!isFinishing) {
            progressDialog.show()
        }
    }

    fun updateProgressDialog(progress: Long) {
        when (progress) {
            PROGRESS_DIALOG_MAX.toLong() -> {
                if (progressDialog.isShowing && !isFinishing) {
                    progressDialog.progress = progressDialog.max
                    setResult(RESULT_OK, resultIntent)
                    progressDialog.dismiss()
                }
                finish()
            }
            else -> progressDialog.progress = progress.toInt()
        }
    }

    fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    fun getResultIntent(): Intent = resultIntent

    fun setResultIntent(intent: Intent) {
        resultIntent = intent
    }

    private fun getExtensionFromContentDisposition(contentDisposition: String): String {
        val extensionIndex = contentDisposition.lastIndexOf('.')
        val extension = contentDisposition.substring(extensionIndex)
        return extension.substring(0, extension.length - 1)
    }

    private val isWhatsappInstalled: Boolean
        get() {
            return try {
                packageManager.getPackageInfo(PACKAGE_NAME_WHATSAPP, GET_ACTIVITIES)
                true
            } catch (_: NameNotFoundException) {
                false
            }
        }

    override fun onDestroy() {
        webView.setDownloadListener(null)
        webView.destroy()

        val token = getCookie(BASE_URL_HTTPS, AUTHENTICATION_COOKIE_NAME)
        val refreshToken = getCookie(BASE_URL_HTTPS, REFRESH_TOKEN_COOKIE_NAME)

        sharedPreferences
            .edit()
            .putString(TOKEN, token)
            .putString(REFRESH_TOKEN, refreshToken)
            .apply()

        surveyCampaign?.let {
            surveyCampaign.showSurvey(applicationContext)
        }

        super.onDestroy()
    }

    private fun checkDeprecatedToken(token: String, url: String) {
        tokenTask.getUpgradeTokenResponse().observe(this, Observer { upgradeResponse ->
            upgradeResponse?.let {
                sharedPreferences.edit()
                    .putString(TOKEN, upgradeResponse.token)
                    .putString(REFRESH_TOKEN, upgradeResponse.refresh_token)
                    .apply()
                setLoginCookies(url, sharedPreferences, CookieManager.getInstance())
            }

            webView.loadUrl(url)
        })

        tokenTask.upgradeToken(token)
    }

    private fun getCookie(url: String, cookieName: String): String {
        val cookies = CookieManager.getInstance().getCookie(url)
        val cookiesList = cookies.split(";")
        for (cookie in cookiesList) {
            if (cookie.contains(cookieName)) {
                return cookie.split("=")[1]
            }
        }

        return NO_TOKEN
    }

    companion object {
        private val TAG = WebViewActivity::class.java.simpleName
        const val INTENT_PARAMETER_URL = "url"
        const val INTENT_FORCE_OPEN_IN_APP = "openInApp"
        const val ANDROID_APPLICATION_EXTENSION = ".apk"
        const val MEDIA_FILE_PATH = "media_file_path"
        private const val PACKAGE_NAME_WHATSAPP = "com.whatsapp"
        private const val PROGRESS_DIALOG_MAX = 100
    }
}
