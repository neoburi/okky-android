package kr.okky.app.android.ui

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.ShareCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.ContextMenu
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.ImageView
import com.crashlytics.android.Crashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.race604.drawable.wave.WaveDrawable
import com.squareup.otto.Subscribe
import io.fabric.sdk.android.Fabric
import kr.okky.app.android.R
import kr.okky.app.android.global.*
import kr.okky.app.android.model.PushData
import kr.okky.app.android.model.SharedData
import kr.okky.app.android.utils.OkkyLog
import kr.okky.app.android.utils.OkkyUtils
import kr.okky.app.android.utils.Pref
import kr.okky.app.android.widget.BottomMenu
import kr.okky.app.android.widget.OkkyNaviDrawerMenu
import kr.okky.app.android.widget.OkkyWebView
import kr.okky.app.android.widget.WebViewWrapper
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity(), View.OnKeyListener, EasyPermissions.PermissionCallbacks,
        OkkyWebView.OnScrollChangeListener {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
    private var mWebWrapper: WebViewWrapper? = null
    private var mFinishCondition: Boolean = false
    private val mExitHandler = Handler()
    private var mBottomBar:BottomMenu? = null
    private var mShowKeyboard:Boolean = false
    private var mOkkyNavi: OkkyNaviDrawerMenu? = null
    /*private val mPushBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(BroadcastAction.PUSH_RECEIVE_ACTION.action == intent.action){
                val data:PushData? = intent.extras?.getParcelable(StoreKey.FCM_DATA.name)
                handlePushData(data)
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(MODE == Mode.DEV)
                .build())

        //Pref.init(this)
        OkkyUtils.checkDrawerMenuJsonInPref(baseContext)

        setContentView(R.layout.activity_main)
        findViews()
        initViews()
        attachEvents()
        handlePushData(intent, "onCreate()")
        registerFcmTopic()
        //traceBundleValues()//intent values trace.
        //determinesPushDataExist()
    }

    private fun traceBundleValues() {
        val bundle = intent.extras
        bundle?.let {
            bundle.keySet().iterator().forEach { k ->
                OkkyLog.err(TAG, "bundle key=$k, value=${bundle[k]}")
            }
        }
    }

    override fun findViews() {
        mSpinner = getView(R.id.progress)
        mBottomBar = getView(R.id.bottom_navi)
    }

    override fun initViews() {
        getView<ImageView>(R.id.okky_loading).setImageDrawable(
                WaveDrawable(this, R.drawable.img_bg_okky_logo).apply {
                    isIndeterminate = true
                }
        )

        mWebWrapper = WebViewWrapper(this).apply {
            initWebView(getView(R.id.web_view))
            loadUrl(getUrl())
            mSharedData = SharedData().parseIntent(intent)
            mPushData = intent.getParcelableExtra(StoreKey.FCM_DATA.name)
        }

        registerForContextMenu(mWebWrapper?.mWebView)

        val drawer = getView(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        mOkkyNavi = OkkyNaviDrawerMenu(this, getView(R.id.nav_view) as NavigationView)
        mOkkyNavi?.loadDrawerMenu()
    }

    override fun attachEvents() {
        mWebWrapper?.mWebView!!.setOnKeyListener(this)
        mWebWrapper?.mWebView!!.setOnClickListener { view -> toggleDrawer() }
        val wv = mWebWrapper?.mWebView as OkkyWebView
        wv.onScrollChangeListener = this

        val layout = getView<ConstraintLayout>(R.id.box_main)//for soft keyboard show/hide
//        val layout:ConstraintLayout? = null //crashlytics force test
        layout.viewTreeObserver!!.addOnGlobalLayoutListener {
            val r = Rect()
            layout.getWindowVisibleDisplayFrame(r)
            val screenHeight = layout.rootView.height

            val keypadHeight = screenHeight.minus(r.bottom).toFloat()
            mShowKeyboard = keypadHeight > screenHeight.times(0.15f)

            if(mShowKeyboard){
                mBottomBar?.visibility = View.GONE
            }
        }
    }

    override fun onScrollChange(v: WebView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val ydiff = scrollY - oldScrollY
        val height = Math.floor(((v.contentHeight * v.scale).toDouble())).toInt()
        val webViewHeight = v.measuredHeight
        when{
            ydiff > 0 -> {
                when{
                    (v.scrollY + webViewHeight >= height) -> {//scroll bottom reached
                        mBottomBar?.visibility = View.VISIBLE
                    }
                    ydiff > 20 -> {
                        mBottomBar?.visibility = View.GONE
                    }
                }
            }
            ydiff < -20 -> {
                if(!mShowKeyboard) {
                    mBottomBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        if (p2?.action == android.view.KeyEvent.ACTION_DOWN) {
            when (p1) {
                KeyEvent.KEYCODE_BACK -> {
                    checkCanGoBack()
                    return true
                }
            }
        }
        return false
    }

    private fun checkDrawerMenuFlagChanged(){
        if(Pref.getBooleanValue(StoreKey.DRAWER_MENU_CHANGED.name, false)){
            mOkkyNavi?.loadDrawerMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        BusProvider.eventBus.register(this)
        BusProvider.eventBus.post(BusEvent(BusEvent.Evt.BOTTOM_HISTORY))
        checkDrawerMenuFlagChanged()
        //mWebWrapper?.checkLoginStatus()
        //registerPushBroadcastReceiver()
    }

    override fun onPause() {
        super.onPause()
        BusProvider.eventBus.unregister(this)
        //unRegisterPushBroadcastReceiver()
    }

    override fun onStart() {
        super.onStart()
        //registerPushBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        //unRegisterPushBroadcastReceiver()
    }

    @Subscribe
    fun bottomMenuClick(menu:Menu){
        when(menu){
            Menu.HOME -> {
                mWebWrapper?.clearWebView()
                mWebWrapper?.loadUrl(getUrl())
            }
            Menu.BACKWARD -> mWebWrapper?.goBack()
            Menu.FORWARD -> mWebWrapper?.goForward()
            Menu.RELOAD -> mWebWrapper?.reload()
            Menu.GO_TOP -> mWebWrapper?.scrollTop()
            Menu.SHARE -> mWebWrapper?.shareThisPage()
            Menu.MORE -> toggleDrawer()
        }
    }

    @Subscribe
    fun accept(evt: BusEvent ){
        when(evt.event){
            BusEvent.Evt.BOTTOM_HISTORY -> controlBottomMenuHistory()
            BusEvent.Evt.BOTTOM_DISABLE -> disableBottomMenus()
        }
    }

    private fun disableBottomMenus(){
        mBottomBar?.disableAll()
    }

    private fun controlBottomMenuHistory(){
        mBottomBar?.enableAll()
        val isMain = mWebWrapper?.isMainPage()
        mWebWrapper?.mWebView?.let {
            mBottomBar?.setAvailable(it.canGoBack().and(!isMain!!), Menu.BACKWARD.ordinal)
            mBottomBar?.setAvailable(it.canGoForward(), Menu.FORWARD.ordinal)
        }
    }

    private fun checkCanGoBack() {
        when (isDrawerStateIsOpen()) {
            true -> {
                toggleDrawer()
            }
            else -> {
                when (mWebWrapper?.mWebView!!.canGoBack()) {
                    true -> {
                        when (mWebWrapper!!.isMainPage()) {
                            true -> finishNotify()
                            false -> mWebWrapper?.mWebView!!.goBack()
                        }
                    }
                    false -> finishNotify()
                }
            }
        }
    }

    private fun finishNotify(){
        when(mFinishCondition) {
            true -> finish()
            false -> let {
                toast(R.string.exit_msg)
                mFinishCondition = true
                mExitHandler.postDelayed({ mFinishCondition = false }, 2500)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mWebWrapper?.onActivityResult(requestCode, resultCode, data)
    }

    override fun requestAppPermission() {
        if (!hasAppPermission()) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.txt_permission_desc_1), 5001,
                    *PERMISSIONS)
        }
    }

    override fun hasAppPermission():Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return EasyPermissions.hasPermissions(this, *PERMISSIONS)
        }
        return true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (!hasAppPermission()) {
            toast(R.string.txt_permission_denied)
        }
    }

    fun loadUrl(url:String){
        mWebWrapper?.loadUrl(url)
    }

    fun launchEmailApp(path: String) {
        mWebWrapper?.launchEmailApp(path)
    }

    fun openSettings() {
        startActivity(Intent(baseContext, SettingsActivity::class.java))
    }

    fun toggleDrawer() {
        val drawer = getDrawerLayout()
        when(drawer.isDrawerOpen(Gravity.START)){
            true -> drawer.closeDrawer(Gravity.START)
            false -> drawer.openDrawer(Gravity.START)
        }
    }

    fun isDrawerStateIsOpen(): Boolean = getDrawerLayout().isDrawerOpen(Gravity.START)

    fun getDrawerLayout(): DrawerLayout = getView(R.id.drawer_layout)

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val webView = mWebWrapper?.mWebView
        val webViewHitTestResult = webView?.hitTestResult

        if (webViewHitTestResult?.type == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult?.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            val imageURL = webViewHitTestResult.extra
            menu!!.setHeaderTitle(getString(R.string.txt_context_menu_title))

            menu.add(0, 1, 0, getString(R.string.txt_context_menu_download))
                    .setOnMenuItemClickListener {
                        if (URLUtil.isValidUrl(imageURL)) {

                            val request = DownloadManager.Request(Uri.parse(imageURL))
                            request.allowScanningByMediaScanner()

                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            downloadManager.enqueue(request)

                            toast(R.string.txt_context_menu_download_start)
                        } else {
                            toast(R.string.txt_context_menu_download_fail)
                        }
                        false
                    }
            menu.add(1, 1, 1, getString(R.string.txt_context_menu_share))
                    .setOnMenuItemClickListener {
                        if (URLUtil.isValidUrl(imageURL)) {
                            ShareCompat.IntentBuilder.from(this)
                                    .setType("text/plain")
                                    .setChooserTitle(R.string.txt_selection)
                                    .setText(imageURL)
                                    .setSubject(getString(R.string.app_name))
                                    .startChooser()
                        }
                        false
                    }
        }
    }

    /*private fun registerPushBroadcastReceiver(){
        val filter = IntentFilter(BroadcastAction.PUSH_RECEIVE_ACTION.action)
        LocalBroadcastManager.getInstance(baseContext).registerReceiver(mPushBroadcastReceiver, filter)
    }

    private fun unRegisterPushBroadcastReceiver(){
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(mPushBroadcastReceiver)
    }

    private fun determinesPushDataExist(){
        val data:PushData? = intent.extras?.getParcelable(StoreKey.FCM_DATA.name)
        data?.let {
            handlePushData(it)
        }
    }*/

    private fun handlePushData(intent: Intent?, from: String) {
        val bundle = intent?.extras
        bundle?.let {
            bundle.keySet().iterator().forEach { k ->
                OkkyLog.err(TAG, "$from bundle key=$k, value=${bundle[k]}")
            }
            val shareText:String? = it.getString("android.intent.extra.TEXT")
            shareText?.let {
                mWebWrapper?.let { wrapper->
                    wrapper.mSharedData = SharedData().parseIntent(intent)
                    if (from != "onCreate()") {
                        wrapper.shareContent()
                    }
                }
                return
            }
            val pushData: PushData? = it.getParcelable(StoreKey.FCM_DATA.name) as? PushData

            pushData?.let { data ->
                when {
                    data.isAd() -> {
                        if (data.advertise?.url?.contains("okky.kr")!!) {
                            mWebWrapper?.loadUrl(data.advertise?.url!!)
                        } else {
                            openChrome(data.advertise?.url)
                        }
                    }
                    else -> {
                        mWebWrapper?.loadUrl(data.content?.url!!)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        handlePushData(intent, "onNewIntent()")
        super.onNewIntent(intent)
    }


    private fun openChrome(url: String?) {
        try {
            val uri = Uri.parse("googlechrome://navigate?url=$url")
            val itt = Intent(Intent.ACTION_VIEW, uri).also {
                if (it.resolveActivity(packageManager) == null) {
                    it.data = uri
                }
            }

            startActivity(itt)
        } catch (ex: ActivityNotFoundException) {
            mWebWrapper?.loadUrl(url!!)
        }
    }

    private fun registerFcmTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("okky-default")
                .addOnCompleteListener { task ->
                    /*var msg = "success"
                    if (!task.isSuccessful) {
                        msg = "fail"
                    }
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()*/
                }
    }


}
