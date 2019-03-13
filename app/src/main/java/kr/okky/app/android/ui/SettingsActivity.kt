package kr.okky.app.android.ui


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import kr.okky.app.android.R
import kr.okky.app.android.ui.frag.SettingsInfoFragment
import kr.okky.app.android.ui.frag.SettingsMenuFragment
import kr.okky.app.android.ui.frag.SettingsPushFragment

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
        supportActionBar!!.also {
            it.setTitle(R.string.settings)
            it.setDisplayHomeAsUpEnabled(true)
        }
        getView<TabLayout>(R.id.tabs).also {
            it.setupWithViewPager(mPager)
        }
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

    private inner class SettingsAdapter internal constructor() : FragmentPagerAdapter(supportFragmentManager) {
        val titles = arrayOf(
                getString(R.string.settings_menu),
                getString(R.string.settings_push),
                getString(R.string.settings_info)
        )
        private val mItems = arrayListOf(
                SettingsMenuFragment(),
                SettingsPushFragment(),
                SettingsInfoFragment()
        )

        override fun getItem(position: Int): Fragment? = mItems[position]

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}
