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
import kr.okky.app.android.BuildConfig
import kr.okky.app.android.R
import kr.okky.app.android.global.*
import kr.okky.app.android.model.SharedData
import kr.okky.app.android.ui.BaseActivity
import kr.okky.app.android.utils.OkkyLog
import kr.okky.app.android.utils.OkkyUtils
import java.io.File
import java.io.IOException
import kotlin.collections.HashMap


class WebViewWrapper constructor(val mActivity: BaseActivity){
    var mWebView:WebView? = null
    private var mCallback: ValueCallback<Array<Uri>>? = null
    private var mImagePath: String? = null
    private var mPreviousUrl:String? = null
    private var mCurrentUrl:String? = null
    private var mClearHistories:Boolean = false
    private val mErrorCodes = arrayOf(WebViewClient.ERROR_AUTHENTICATION, WebViewClient.ERROR_UNSUPPORTED_SCHEME)
    private val header = HashMap<String, String>()
    private var mPageTitle: String? = null
    var mSharedData:SharedData? = null
    private var mJsBridge: OkkyBridge? = null
    init {
        header["Okky.App"] = "${mActivity.packageName}, v${OkkyUtils.getVersionName(mActivity.baseContext)}"
    }

    fun initWebView(webView:WebView){
        mWebView = webView

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWebView?.settings?.safeBrowsingEnabled = false
        }

        mWebView?.settings?.run {
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            /*databaseEnabled = true
            domStorageEnabled = true*/
            setSupportMultipleWindows(false)
            mediaPlaybackRequiresUserGesture = true

            allowFileAccess = true
            pluginState = WebSettings.PluginState.ON

            loadWithOverviewMode = true
            useWideViewPort = true

            javaScriptEnabled = true
            setAppCacheEnabled(false)

            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE

        }
        mJsBridge = OkkyBridge()
        mWebView?.let {
            it.refreshDrawableState()
            it.setInitialScale(1)
            it.webViewClient = OkkyWebViewClient()
            it.webChromeClient = OkkyChromeClient()
            it.addJavascriptInterface(mJsBridge!!, "okkyAndroidBridge")
        }

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        CookieManager.getInstance().setAcceptCookie(true)

    }

    fun loadUrl(url: String) {
        mCurrentUrl = url
        mWebView?.loadUrl(url, header)
    }

    fun isMainPage():Boolean{
        mCurrentUrl?.let {
            return it.matches("^(https://okky.kr)/?$".toRegex())
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
            OkkyLog.log("load url=$url")
            when {
                url!!.startsWith(UrlCompareValue.INTENT.value()) -> {
                    //executeApp(url)
                }
                url.startsWith(UrlCompareValue.MARKET.value()) -> {
                    moveToMarket(url)
                }
                url.startsWith(UrlCompareValue.EMAIL.value()) -> {
                    launchEmailApp(url.replaceFirst(UrlCompareValue.EMAIL.value(), ""))
                }
                url.startsWith(UrlCompareValue.HTTP.value()) -> {
                    var loadTarget: String = url
                    when {
                        url.contains(UrlCompareValue.GOOGLE_OAUTH.value()) -> {
                            mActivity.toast(R.string.txt_not_support)
                            return true
                        }
                        url.contains(UrlCompareValue.LOGIN.value()) -> {
                            setupUrl()
                            loadTarget = getLoginUrl(mPreviousUrl)
                        }
                        url.contains(UrlCompareValue.LOGIN_FAIL.value()) -> {
                            loadTarget = getLoginUrl(getLoginUrl("/"))
                            mActivity.toast(R.string.txt_invalid_id_or_pwd)
                        }
                    }
                    view?.loadUrl(loadTarget, header)
                    mCurrentUrl = url
                    /*return true*/
                }
            }
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            //val cookies = CookieManager.getInstance().getCookie(url)
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
            if(mSharedData?.hasContent()!!){
                mJsBridge?.shareContent()
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

    fun setPaddingBottom() {
        mWebView?.post { mWebView?.loadUrl("javascript:(function(){ document.body.style.paddingBottom = '20px'})();") }
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

            mCallback = filePathCallback

            var imageInent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (imageInent?.resolveActivity(mActivity.packageManager) != null) {
                var imageFile: File? = null
                try {
                    imageFile = createImageFile()
                    imageInent.putExtra("_PHOTO_PATH_", mImagePath)
                }catch (e:IOException){
                    OkkyLog.err("image file creation fail.", e)
                }
                imageFile?.let {
                    mImagePath = "file:".plus(imageFile.absolutePath)
                    imageInent?.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
                }?:kotlin.run {
                    imageInent = null
                }

                val content = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }

                val intentArray: Array<Intent?> =
                        imageInent?.let {
                            arrayOf(imageInent)
                        }?:arrayOfNulls(0)


                val chooser = Intent(Intent.ACTION_CHOOSER).apply {
                    putExtra(Intent.EXTRA_INTENT, content)
                    putExtra(Intent.EXTRA_TITLE, mActivity.getString(R.string.txt_selection))
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                }
                mActivity.startActivityForResult(chooser, 20001)
            }
            return true
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            mPageTitle = title
            super.onReceivedTitle(view, title)
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
                mImagePath?.let {
                    results = arrayOf(Uri.parse(mImagePath))
                }
            }
        }

        mCallback?.onReceiveValue(results)
        mCallback = null
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        return File.createTempFile(
                createFilenameWithTimestamp("JPEG_", "_"),
                FileExt.JPEG.value(),
                getPublicDirectory(Environment.DIRECTORY_PICTURES)
        )
    }

    internal inner class OkkyBridge {
        @JavascriptInterface
        fun testFunc() {

        }

        @JavascriptInterface
        fun shareContent(){
            mWebView?.loadUrl("javascript:shareContent('${mSharedData?.encodedSubject()}', '${mSharedData?.encodedText()}'")
            mSharedData?.clear()
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
                .setType(MimeType.TEXT_PLAIN.value())
                .setChooserTitle(R.string.txt_selection)
                .setText(mWebView?.url)
                .setSubject(mActivity.getString(R.string.app_name))
                .startChooser()
    }

    fun launchEmailApp(emailTo:String?){
        ShareCompat.IntentBuilder.from(mActivity)
                .setType(MimeType.TEXT_HTML.value())
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
            val path = marketUrl?.replace(UrlCompareValue.MARKET.value(), "")
            mActivity.startActivity(
                    Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/$path")))
        }

    }
}