package com.zeykit.dev.cryptomarketcap;

import android.graphics.drawable.Drawable;

public class AboutLayoutAdapter {

    private Drawable _icon;
    private String _text;

    AboutLayoutAdapter() {}

    AboutLayoutAdapter(Drawable icon, String text) {
        this._icon = icon;
        this._text = text;
    }

    public Drawable getIcon() {
        return _icon;
    }

    public void setIcon(Drawable icon) {
        this._icon = icon;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        this._text = text;
    }
}
