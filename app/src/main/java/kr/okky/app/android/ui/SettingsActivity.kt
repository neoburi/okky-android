package kr.okky.app.android.ui


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import kr.okky.app.android.R
import kr.okky.app.android.global.BusEvent
import kr.okky.app.android.global.BusProvider
import kr.okky.app.android.ui.frag.SettingsEnvFragment
import kr.okky.app.android.ui.frag.SettingsInfoFragment
import kr.okky.app.android.ui.frag.SettingsMenuFragment

class SettingsActivity : BaseActivity() {
    private var mPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViews()
        initViews()
    }

    override fun findViews() {
        mPager = getView(R.id.viewPager)
        mPager?.adapter = SettingsAdapter()
    }

    override fun initViews() {
        val ab = supportActionBar
        ab!!.setTitle(R.string.settings)
        ab.setDisplayHomeAsUpEnabled(true)
        val tab = getView<TabLayout>(R.id.tabs)
        tab.setupWithViewPager(mPager)
    }

    override fun attachEvents() {

    }

    override fun performClick(view: View) {

    }

    override fun setInitialData() {

    }

    override fun assignData() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun finish() {
        super.finish()
        BusProvider.post(BusEvent.Evt.DRAWER_RELOAD)
    }

    private inner class SettingsAdapter internal constructor() : FragmentPagerAdapter(supportFragmentManager) {
        val titles = arrayOf(
                getString(R.string.settings_menu),
                getString(R.string.settings_env),
                getString(R.string.settings_info)
        )
        private val mList = arrayListOf(
                SettingsMenuFragment(), SettingsEnvFragment(), SettingsInfoFragment()
        )

        override fun getItem(position: Int): Fragment? {
            return mList[position]
        }

        override fun getCount(): Int {
            return titles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}