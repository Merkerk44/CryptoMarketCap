package com.zeykit.dev.cryptomarketcap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

public class PinnedCoinRvAdapter extends RecyclerView.Adapter<PinnedCoinViewHolder> implements ItemTouchHelperAdapter {

    private List<CryptoAdapter> mCryptoAdapter;

    public PinnedCoinRvAdapter(List<CryptoAdapter> mCryptoAdapter) {
        this.mCryptoAdapter = mCryptoAdapter;
    }

    @Override
    public void onBindViewHolder(final PinnedCoinViewHolder viewHolder, int position) {
        final CryptoAdapter cryptoAdapter = mCryptoAdapter.get(position);
        viewHolder.bind(cryptoAdapter);
    }

    @Override
    public PinnedCoinViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new PinnedCoinViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCryptoAdapter.size();
    }

    @Override
    public void onItemDismiss(int position) {
        mCryptoAdapter.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition > toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mCryptoAdapter, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mCryptoAdapter, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}
