package com.zeykit.dev.cryptomarketcap;

import android.graphics.drawable.Drawable;

public class AboutCryptoAdapter {

    private String _rank;
    private Drawable _icon;
    private String _name;
    private String _price;
    private String _percentChange1h;
    private String _percentChange24h;
    private String _percentChange7d;
    private String _marketCap;
    private String _circulatingSupply;
    private String _volume;

    AboutCryptoAdapter() {}

    AboutCryptoAdapter(String rank, Drawable icon, String name, String price,
                       String marketCap, String circulatingSupply, String volume,
                       String percentChange1h, String percentChange24h, String percentChange7d) {
        this._rank = rank;
        this._icon = icon;
        this._name = name;
        this._price = price;
        this._marketCap = marketCap;
        this._circulatingSupply = circulatingSupply;
        this._volume = volume;
        this._percentChange1h = percentChange1h;
        this._percentChange24h = percentChange24h;
        this._percentChange7d = percentChange7d;
    }

    public String getRank() {return _rank;}
    public void setRank(String rank) {this._rank = rank;}
    public Drawable getIcon() {return _icon;}
    public void setIcon(Drawable icon) {this._icon = icon;}
    public String getName() {return _name;}
    public void setName(String name) {this._name = name;}
    public String getPrice() {return _price;}
    public void setPrice(String price) {this._price = price;}
    public String getMarketCap() {return _marketCap;}
    public void setMarketCap(String marketCap) {this._marketCap = marketCap;}
    public String getCirculatingSupply() {return _circulatingSupply;}
    public void setCirculatingSupply(String circulatingSupply) {this._circulatingSupply = circulatingSupply;}
    public String getVolume() {return _volume;}
    public void setVolume(String volume) {this._volume = volume;}
    public String getPercentChange1h() {return _percentChange1h;}
    public void setPercentChange1h(String percentChange1h) {this._percentChange1h = percentChange1h;}
    public String getPercentChange24h() {return _percentChange24h;}
    public void setPercentChange24h(String percentChange24h) {this._percentChange24h = percentChange24h;}
    public String getPercentChange7d() {return _percentChange7d;}
    public void setPercentChange7d(String percentChange7d) {this._percentChange7d = percentChange7d;}
}
