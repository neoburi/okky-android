package kr.okky.app.android.widget

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ShareCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.webkit.*
import kr.okky.app.android.R
import kr.okky.app.android.global.BusEvent
import kr.okky.app.android.global.BusProvider
import kr.okky.app.android.global.getLoginUrl
import kr.okky.app.android.global.getUrl
import kr.okky.app.android.ui.BaseActivity
import kr.okky.app.android.utils.OkkyLog
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class WebViewWrapper constructor(val mActivity: BaseActivity){
    var mWebView:WebView? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null
    private var mPreviousUrl:String? = null
    private var mCurrentUrl:String? = null
    private var mClearHistories:Boolean = false
    private val mErrorCodes = arrayOf(WebViewClient.ERROR_AUTHENTICATION, WebViewClient.ERROR_UNSUPPORTED_SCHEME)

    fun initWebView(webView:WebView){
        mWebView = webView

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWebView?.settings?.safeBrowsingEnabled = false
        }

        mWebView?.settings?.run {
            //userAgentString = "okky.android"
            setSupportZoom(true)

            databaseEnabled = true
            domStorageEnabled = true
            setSupportMultipleWindows(false)
            mediaPlaybackRequiresUserGesture = true

            allowFileAccess = true
            pluginState = WebSettings.PluginState.ON

            loadWithOverviewMode = true
            useWideViewPort = true

            javaScriptEnabled = true
            setAppCacheEnabled(false)

            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE

        }

        mWebView?.let {
            it.refreshDrawableState()
            it.setInitialScale(1)
            it.webViewClient = OkkyWebViewClient()
            it.webChromeClient = OkkyChromeClient()
            it.addJavascriptInterface(OkkyBridge(), "okkyBridge")
        }

        CookieManager.getInstance().setAcceptCookie(true)

    }

    fun loadUrl(url: String) {
        mWebView?.loadUrl(url)
    }

    fun isMainPage():Boolean{
        mCurrentUrl?.let {
            return it == getUrl().plus("/")
        }
        return false
    }

    private fun setupUrl(){
        mPreviousUrl = mCurrentUrl
    }

    internal inner class OkkyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            return loadOverrideUrl(view, url)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

            return loadOverrideUrl(view, request?.url.toString())
        }

        private fun loadOverrideUrl(view:WebView?, url:String?):Boolean{
            when {
                url!!.startsWith("intent:") -> {
                    //executeApp(url)
                }
                url.startsWith("market:") -> {
                    moveToMarket(url)
                }
                url.startsWith("mailto:") -> {
                    launchEmailApp(url.replaceFirst("mailto:", ""))
                }
                //url.contains("facebook.com") -> launchFacebook(url)
                url.startsWith("http") -> {
                    when {
                        url.contains("/login/authAjax") -> {
                            setupUrl()
                            view?.loadUrl(getLoginUrl(mPreviousUrl))
                        }
                        url.contains("/login/authfail?ajax=true") -> {
                            view?.loadUrl(getLoginUrl("/"))//if login fail, redirect to main(/).
                            mActivity.toast(R.string.txt_invalid_id_or_pwd)
                        }
                        else -> view?.loadUrl(url)
                    }
                    mCurrentUrl = url
                    /*return true*/
                }
            }

            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            val cookies = CookieManager.getInstance().getCookie(url)
            //OkkyLog.log("url onPageStarted:$url , cookies:$cookies")
            BusProvider.eventBus.post(BusEvent(BusEvent.Evt.BOTTOM_DISABLE))
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if(mClearHistories){
                mWebView?.let {
                    it.clearHistory()
                    mClearHistories = false
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
            onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            if (mErrorCodes.contains(errorCode)) {
                clearWebView()
                loadUrl(getUrl())
            }
        }
    }

    fun clearWebView(){
        mClearHistories = true
    }

    internal inner class OkkyChromeClient : WebChromeClient() {

        override fun onJsAlert(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
            AlertDialog.Builder(mActivity).setTitle("").setMessage(message)
                    .setPositiveButton(android.R.string.ok){ dialog, which -> result.confirm()}
                    .setCancelable(false).create().show()
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            AlertDialog.Builder(mActivity).setTitle("").setMessage(message)
                    .setPositiveButton(android.R.string.ok) { dialog, which -> result.confirm() }
                    .setNegativeButton(android.R.string.cancel) { dialog, which -> result.cancel() }.create().show()
            return true
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            (newProgress in 1..99).let {
                when(it){
                    true -> {
                        if(mActivity.getView<View>(R.id.ic_loading_bg).visibility == View.GONE){
                            mActivity.showSpinner(R.id.progress)
                        }
                    }
                    false -> {
                        mActivity.closeSpinner(R.id.progress)
                        mActivity.hideBgLogo()
                        BusProvider.eventBus.post(BusEvent(BusEvent.Evt.BOTTOM_HISTORY))
                    }
                }
            }
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            callback.invoke(origin, true, false)
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources)
        }

        override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?): Boolean {

            if(!mActivity.hasAppPermission()){
                mActivity.requestAppPermission()
                return false
            }

            mFilePathCallback = filePathCallback

            var takePictureInent:Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(takePictureInent?.resolveActivity(mActivity.packageManager) != null){
                var photoFile:File? = null
                try {
                    photoFile = createImageFile()
                    takePictureInent.putExtra("_PHOTO_PATH_", mCameraPhotoPath)
                }catch (e:IOException){
                    OkkyLog.err("image file creation fail.", e)
                }
                photoFile?.let {
                    mCameraPhotoPath = "file:".plus(photoFile.absolutePath)
                    takePictureInent?.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                }?:kotlin.run {
                    takePictureInent = null
                }

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }

                val intentArray: Array<Intent?> =
                        takePictureInent?.let {
                            arrayOf(takePictureInent)
                        }?:arrayOfNulls(0)


                val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
                    putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    putExtra(Intent.EXTRA_TITLE, mActivity.getString(R.string.txt_selection))
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                }
                mActivity.startActivityForResult(chooserIntent, 20001)
            }
            return true
        }
    }

    fun onActivityResult(reqCode:Int, resultCode:Int, data:Intent?){
        var results: Array<Uri>? = null

        (resultCode == Activity.RESULT_OK).let {
            data?.let {
                it.dataString?.let {
                    results = arrayOf(Uri.parse(it))
                }
            }?:kotlin.run {
                mCameraPhotoPath?.let {
                    results = arrayOf(Uri.parse(mCameraPhotoPath))
                }
            }
        }

        mFilePathCallback?.onReceiveValue(results)
        mFilePathCallback = null
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )
    }

    internal inner class OkkyBridge {
        @JavascriptInterface
        fun testFunc() {

        }
    }

    fun goBack() = (mWebView!!.canGoBack()).let{
            mWebView?.goBack()
        }

    fun goForward() = (mWebView!!.canGoForward()).let{
            mWebView?.goForward()
        }

    fun reload() = mWebView?.reload()

    fun scrollTop() = mWebView?.let{
        it.post { mWebView?.scrollTo(0, 0) }
    }

    fun getCurrentUrl(): String? = mCurrentUrl

    fun shareThisPage(){
        ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setChooserTitle(R.string.txt_selection)
                .setText(mWebView?.url)
                .setSubject(mActivity.getString(R.string.app_name))
                .startChooser()
    }

    fun launchEmailApp(emailTo:String?){
        ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/html")
                .setChooserTitle(R.string.txt_selection)
                .addEmailTo(emailTo)
                .startChooser()
    }

    fun moveToMarket(marketUrl: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        try {
            intent.data = Uri.parse(marketUrl!!)
            mActivity.startActivity(intent)
        } catch (ee: ActivityNotFoundException) {
            val path = marketUrl?.replace("market://", "")
            mActivity.startActivity(
                    Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/$path")))
        }

    }
}