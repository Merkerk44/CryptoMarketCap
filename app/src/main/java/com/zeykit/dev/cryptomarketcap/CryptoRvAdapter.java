package com.zeykit.dev.cryptomarketcap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CryptoRvAdapter extends RecyclerView.Adapter<CryptoViewHolder> {

    private List<CryptoAdapter> mCryptoAdapter;

    public CryptoRvAdapter(List<CryptoAdapter> mCryptoAdapter) {
        this.mCryptoAdapter = mCryptoAdapter;
    }

    @Override
    public void onBindViewHolder(CryptoViewHolder viewHolder, int position) {
        final CryptoAdapter cryptoAdapter = mCryptoAdapter.get(position);
        viewHolder.bind(cryptoAdapter);
    }

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCryptoAdapter.size();
    }

    public void setFilter(List<CryptoAdapter> cryptoAdapters) {
        mCryptoAdapter = new ArrayList<>();
        mCryptoAdapter.addAll(cryptoAdapters);
        notifyDataSetChanged();
    }
}
