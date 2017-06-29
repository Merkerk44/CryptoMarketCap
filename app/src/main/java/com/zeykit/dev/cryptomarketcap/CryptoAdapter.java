package com.zeykit.dev.cryptomarketcap;

import android.graphics.drawable.Drawable;

public class CryptoAdapter {

    private String _rank;
    private Drawable _icon;
    private String _name;
    private String _price;
    private String _percentChange;

    CryptoAdapter() {}

    CryptoAdapter(String rank, Drawable icon, String name, String price, String percentChange) {
        this._rank = rank;
        this._icon = icon;
        this._name = name;
        this._price = price;
        this._percentChange = percentChange;
    }

    public String getRank() {
        return _rank;
    }

    public void setRank(String rank) {
        this._rank = rank;
    }

    public Drawable getIcon() {
        return _icon;
    }

    public void setIcon(Drawable icon) {
        this._icon = icon;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getPrice() {
        return _price;
    }

    public void setPrice(String price) {
        this._price = price;
    }

    public String getPercentChange() {
        return _percentChange;
    }

    public void setPercentChange(String percentChange) {
        this._percentChange = percentChange;
    }
}
