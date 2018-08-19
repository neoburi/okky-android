package kr.okky.app.android

import android.app.DownloadManager
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
import android.view.*
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Toast
import com.squareup.otto.Subscribe
import kr.okky.app.android.global.*
import kr.okky.app.android.global.Menu
import kr.okky.app.android.widget.BottomMenu
import kr.okky.app.android.widget.OkkyWebView
import kr.okky.app.android.widget.WebViewWrapper
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity(), View.OnKeyListener, EasyPermissions.PermissionCallbacks,
        OkkyWebView.OnScrollChangeListener, NavigationView.OnNavigationItemSelectedListener {
    private var mWebWrapper: WebViewWrapper? = null
    private var mFinishCondition: Boolean = false
    private val mExitHandler = Handler()
    private var mBottomBar:BottomMenu? = null
    private var mDrawerMenuMap = HashMap<Int, DrawerMenu>()
    private var mShowKeyboard:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        initViews()
        attachEvents()
    }

    override fun findViews() {
        mSpinner = getView(R.id.progress)
        mBottomBar = getView(R.id.bottom_navi)
    }

    override fun initViews() {
        mWebWrapper = WebViewWrapper(MainActivity@ this)
        mWebWrapper?.initWebView(getView(R.id.web_view))
        mWebWrapper?.loadUrl(getUrl())

        registerForContextMenu(mWebWrapper?.mWebView)//register context menu for long click download

        initDrawerMenus()
        val drawer = getView(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = getView(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.isVerticalScrollBarEnabled = false
    }

    private fun initDrawerMenus(){
        mDrawerMenuMap.clear()
        DrawerMenu.values().forEach {
            val resId = loadResourceId(baseContext, "id", "nav_menu_".plus(it.ordinal))
            mDrawerMenuMap[resId] = it
        }
    }

    override fun attachEvents() {
        mWebWrapper?.mWebView!!.setOnKeyListener(MainActivity@this)
        mWebWrapper?.mWebView!!.setOnClickListener { view -> toggleDrawer() }
        val wv = mWebWrapper?.mWebView as OkkyWebView
        wv.onScrollChangeListener = MainActivity@this

        val layout = getView<ConstraintLayout>(R.id.box_main)//for soft keyboard show/hide
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
        when{
            ydiff > 20 -> mBottomBar?.visibility = View.GONE
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

    override fun onResume() {
        BusProvider.instance().register(MainActivity@this)
        BusProvider.instance().post(BusEvent(BusEvent.Evt.BOTTOM_HISTORY))
        super.onResume()
    }

    override fun onPause() {
        BusProvider.instance().unregister(MainActivity@this)
        super.onPause()
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
        mWebWrapper?.mWebView?.let {
            mBottomBar?.setAvailable(it.canGoBack(), Menu.BACKWARD.ordinal)
            mBottomBar?.setAvailable(it.canGoForward(), Menu.FORWARD.ordinal)
        }
    }

    private fun checkCanGoBack() {
        when(mWebWrapper?.mWebView!!.canGoBack()){
            true -> {
                when(mWebWrapper!!.isMainPage()){
                    true -> finishNotify()
                    false -> mWebWrapper?.mWebView!!.goBack()
                }
            }
            false -> finishNotify()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        toggleDrawer()
        val menu = mDrawerMenuMap[item.itemId]

        when(menu){
            DrawerMenu.BUG_REPORT ->{
                loadUrl(menu.path())
            }
            DrawerMenu.CONTACT ->{
                mWebWrapper?.launchEmailApp(menu.path())
            }
            else ->{
                loadUrl(getUrl().plus(menu?.path()))
            }
        }
        return true
    }

    private fun toggleDrawer(){
        val drawer = getView(R.id.drawer_layout) as DrawerLayout
        when(drawer.isDrawerOpen(Gravity.START)){
            true -> drawer.closeDrawer(Gravity.START)
            false -> drawer.openDrawer(Gravity.START)
        }
    }

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
                            ShareCompat.IntentBuilder.from(MainActivity@this)
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
}
