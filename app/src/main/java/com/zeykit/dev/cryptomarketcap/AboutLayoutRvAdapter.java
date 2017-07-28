package com.zeykit.dev.cryptomarketcap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AboutLayoutRvAdapter extends RecyclerView.Adapter<AboutLayoutViewHolder> {

    private List<AboutLayoutAdapter> mAboutLayoutAdapter;

    public AboutLayoutRvAdapter(List<AboutLayoutAdapter> mAboutLayoutAdapter) {
        this.mAboutLayoutAdapter = mAboutLayoutAdapter;
    }

    @Override
    public void onBindViewHolder(AboutLayoutViewHolder viewHolder, int position) {
        final AboutLayoutAdapter aboutLayoutAdapter = mAboutLayoutAdapter.get(position);
        viewHolder.bind(aboutLayoutAdapter);
    }

    @Override
    public AboutLayoutViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.about_layout_row, parent, false);
        return new AboutLayoutViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mAboutLayoutAdapter.size();
    }
}
