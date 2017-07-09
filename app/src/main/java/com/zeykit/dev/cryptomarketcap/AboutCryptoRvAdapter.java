package com.zeykit.dev.cryptomarketcap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AboutCryptoRvAdapter extends RecyclerView.Adapter<AboutCryptoViewHolder> {

    private List<AboutCryptoAdapter> mAboutCryptoAdapter;

    public AboutCryptoRvAdapter(List<AboutCryptoAdapter> mAboutCryptoAdapter) {
        this.mAboutCryptoAdapter = mAboutCryptoAdapter;
    }

    @Override
    public void onBindViewHolder(AboutCryptoViewHolder viewHolder, int position) {
        final AboutCryptoAdapter aboutCryptoAdapter = mAboutCryptoAdapter.get(position);
        viewHolder.bind(aboutCryptoAdapter);
    }

    @Override
    public AboutCryptoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.about_list_row, parent, false);
        return new AboutCryptoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mAboutCryptoAdapter.size();
    }
}
