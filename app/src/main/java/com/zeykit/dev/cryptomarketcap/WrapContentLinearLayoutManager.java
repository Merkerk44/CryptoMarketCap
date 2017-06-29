package com.zeykit.dev.cryptomarketcap;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {

    /**
     * java.lang.IndexOutOfBoundsException
     * in RecyclerView fix
     */

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reserveLayout) {
        super(context, orientation, reserveLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
