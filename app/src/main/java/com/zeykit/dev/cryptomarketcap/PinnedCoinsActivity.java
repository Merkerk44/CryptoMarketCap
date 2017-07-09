package com.zeykit.dev.cryptomarketcap;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

public class PinnedCoinsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private LinearLayout pinnedCoinsLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pined_coins_layout);

        init();
        setupActionBar();
        setupRecyclerView();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.pinnedRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pinnedSwipeRefreshLayout);
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
