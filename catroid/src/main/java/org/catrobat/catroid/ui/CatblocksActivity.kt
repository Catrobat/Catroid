package org.catrobat.catroid.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Locale

class CatblocksActivity : BaseActivity() {

    companion object {
        const val SERIALIZED_PROJECT = "CurrentProjectSerialized";
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val currentProjectPath = intent.getStringExtra(SERIALIZED_PROJECT)
        val currentProjectFile = File(currentProjectPath)

        val fis = FileInputStream(currentProjectFile)
        val isr = InputStreamReader(fis)
        val xmlCurrentProject = isr.readText()

        isr.close()
        fis.close()

        currentProjectFile.delete()

        WebView.setWebContentsDebuggingEnabled(true)

        val catblocksWebview = WebView(this)
        catblocksWebview.settings.javaScriptEnabled = true
        WebView.setWebContentsDebuggingEnabled(true);
        setContentView(catblocksWebview)

        val assetLoader: WebViewAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()

        catblocksWebview.addJavascriptInterface(JSInterface(xmlCurrentProject), "Android");

        catblocksWebview.webViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
        catblocksWebview.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html");
    }

    class JSInterface(projectXML : String) {

        private val _projectXML = projectXML

        @JavascriptInterface
        fun getCurrentProject():String {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n$_projectXML"
        }

        @JavascriptInterface
        fun getCurrentLanguage():String {
            return Locale.getDefault().toString().replace("_", "-");
        }

        @JavascriptInterface
        fun isRTL():Boolean {
            val directionality = Character.getDirectionality(Locale.getDefault().displayName[0])
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
        }
    }
}