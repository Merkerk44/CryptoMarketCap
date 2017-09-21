package com.zeykit.dev.cryptomarketcap;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class AboutLayoutActivity extends AppCompatActivity {

    LinearLayout aboutActivityLayout;
    private RecyclerView recyclerView;
    List<AboutLayoutAdapter> adapterList;
    AboutLayoutRvAdapter adapter;
    ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout_v2);

        init();
        setupActionBar();
        setupRecyclerView();
    }

    private void init() {
        aboutActivityLayout = findViewById(R.id.aboutLayout);
        recyclerView = findViewById(R.id.aboutRecyclerView);
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        adapterList = new ArrayList<>();

        AboutLayoutAdapter mAdapter;
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_code), getString(R.string.developed_by_zeykit_dev));
        adapterList.add(0, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_labaleine), getString(R.string.join_community));
        adapterList.add(1, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.bitcoin_icon), getString(R.string.btc));
        adapterList.add(2, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.eth_icon), getString(R.string.eth));
        adapterList.add(3, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ubiq_logo), getString(R.string.ubiq));
        adapterList.add(4, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.zcash_icon), getString(R.string.zec));
        adapterList.add(5, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_share), getString(R.string.share));
        adapterList.add(6, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_rate), getString(R.string.rate));
        adapterList.add(7, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_bug), getString(R.string.report_a_bug));
        adapterList.add(8, mAdapter);
        mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_github), getString(R.string.github));
        adapterList.add(9, mAdapter);
        //mAdapter = new AboutLayoutAdapter(getResources().getDrawable(R.drawable.ic_how_to_use), getString(R.string.how_to_use));
        //adapterList.add(9, mAdapter);

        adapter = new AboutLayoutRvAdapter(adapterList);
        recyclerView.setAdapter(adapter);
    }
}
