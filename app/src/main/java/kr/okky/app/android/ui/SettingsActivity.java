package kr.okky.app.android.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kr.okky.app.android.R;
import kr.okky.app.android.model.NaviMenu;

public class SettingsActivity extends BaseActivity {
    private ViewPager mPager;
    private List<NaviMenu> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        initViews();
    }

    @Override
    public void findViews() {
        mPager = getView(R.id.viewPager);
    }

    @Override
    public void initViews() {
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.settings);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void attachEvents() {

    }

    @Override
    public void performClick(@NotNull View view) {

    }

    @Override
    public void setInitialData() {

    }

    @Override
    public void assignData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }
}
