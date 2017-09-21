package com.zeykit.dev.cryptomarketcap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class MoreAboutCryptoDialog extends Activity {

    private final String mTAG = MoreAboutCryptoDialog.class.getName();

    private String chartAddress = "";
    private String chartLinesType = "Horizontal";

    LinearLayout moreAboutCryptoActivity;
    RecyclerView recyclerView;
    GraphView graphView;
    private List<AboutCryptoAdapter> cryptoAdapterList;
    private AboutCryptoRvAdapter adapter;

    private SpotsDialog progressDialog;

    static String selectedCrypto = "";
    private String[] cryptoName = selectedCrypto.split("\n");
    //private String[] cryptoSymbol1 = cryptoName[1].split(Pattern.quote("("));
    //private String[] cryptoSymbol2 = cryptoSymbol1[1].split(Pattern.quote(")"));

    Button mOneDayButton;
    Button mSevenDaysButton;
    Button mOneMonthButton;
    Button mSixMonthButton;
    Button mOneYearButton;
    Button mAllDataButton;

    private SharedPreferences sharedPreferences;

    private interface TAG {
        String NAME = "name";
        String SYMBOL = "symbol";
        String RANK = "rank";
        String PRICE = "price";
        String PRICE_USD = "price_usd";
        String PRICE_EUR = "price_eur";
        String PRICE_GBP = "price_gbp";
        String PRICE_BTC = "price_btc";
        String PRICE_CAD = "price_cad";
        String PRICE_JPY = "price_jpy";
        String PRICE_AUD = "price_aud";
        String PRICE_CHF = "price_chf";
        String PRICE_INR = "price_inr";
        String PRICE_BRL = "price_brl";
        String PRICE_PLN = "price_pln";
        String PRICE_CNY = "price_cny";
        String PRICE_SEK = "price_sek";
        String PRICE_NZD = "price_nzd";
        String PRICE_MXN = "price_mxn";
        String PRICE_SGD = "price_sgd";
        String PRICE_HKD = "price_hkd";
        String VOLUME_USD = "24h_volume_usd";
        String MARKET_CAP_USD = "market_cap_usd";
        String VOLUME_EUR = "24h_volume_eur";
        String MARKET_CAP_EUR = "market_cap_eur";
        String VOLUME_GBP = "24h_volume_gbp";
        String MARKET_CAP_GBP = "market_cap_gbp";
        String VOLUME_BTC = "24h_volume_btc";
        String MARKET_CAP_BTC = "market_cap_btc";
        String VOLUME_CAD = "24h_volume_cad";
        String MARKET_CAP_CAD = "market_cap_cad";
        String VOLUME_AUD = "24h_volume_aud";
        String MARKET_CAP_AUD = "market_cap_aud";
        String VOLUME_CHF = "24h_volume_chf";
        String MARKET_CAP_CHF = "market_cap_chf";
        String VOLUME_INR = "24h_volume_inr";
        String MARKET_CAP_INR = "market_cap_inr";
        String VOLUME_BRL = "24h_volume_brl";
        String MARKET_CAP_BRL = "market_cap_brl";
        String VOLUME_PLN = "24h_volume_pln";
        String MARKET_CAP_PLN = "market_cap_pln";
        String VOLUME_JPY = "24h_volume_jpy";
        String MARKET_CAP_JPY = "market_cap_jpy";
        String VOLUME_CNY = "24h_volume_cny";
        String MARKET_CAP_CNY = "market_cap_cny";
        String VOLUME_SEK = "24h_volume_sek";
        String MARKET_CAP_SEK = "market_cap_sek";
        String VOLUME_NZD = "24h_volume_nzd";
        String MARKET_CAP_NZD = "market_cap_nzd";
        String VOLUME_MXN = "24h_volume_mxn";
        String MARKET_CAP_MXN = "market_cap_mxn";
        String VOLUME_SGD = "24h_volume_sgd";
        String MARKET_CAP_SGD = "market_cap_sgd";
        String VOLUME_HKD = "24h_volume_hkd";
        String MARKET_CAP_HKD = "market_cap_hkd";
        String CIRCULATING_SUPPLY = "available_supply";
        String PERCENT_CHANGE_1H = "percent_change_1h";
        String PERCENT_CHANGE_24H = "percent_change_24h";
        String PERCENT_CHANGE_7D = "percent_change_7d";
        String MARKET_CAP = "market_cap";
        String USD = "USD";
        String EUR = "EUR";
        String GBP = "GBP";
        String CAD = "CAD";
        String JPY = "JPY";
        String AUD = "AUD";
        String CHF = "CHF";
        String INR = "INR";
        String BRL = "BRL";
        String PLN = "PLN";
        String CNY = "CNY";
        String SEK = "SEK";
        String NZD = "NZD";
        String MXN = "MXN";
        String SGD = "SGD";
        String HKD = "HKD";
    }

    private interface SYMBOL {
        String USD = "$";
        String EUR = "€";
        String GBP = "£";
        String BTC = "฿";
        String CAD = "C$";
        String JPY = "¥";
        String AUD = "A$";
        String CHF = "Fr";
        String INR = "₹";
        String BRL = "R$";
        String PLN = "zł";
        String CNY = "元";
        String SEK = "kr";
        String NZD = "NZ$";
        String MXN = "$";
        String SGD = "S$";
        String HKD = "HK$";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.more_about_crypto_layout);
        setFinishOnTouchOutside(false);

        if (cryptoName[0].contains(" "))
            cryptoName[0] = cryptoName[0].replace(" ", "-");

        if (cryptoName[0].contains("DubaiCoin"))
            cryptoName[0] = "dubaicoin-dbix";
        if (cryptoName[0].contains("Golem"))
            cryptoName[0] = "golem-network-tokens";
        if (cryptoName[0].contains("Stellar"))
            cryptoName[0] = "stellar";
        if (cryptoName[0].contains("Gnosis"))
            cryptoName[0] = "gnosis-gno";
        if (cryptoName[0].contains("Bytecoin"))
            cryptoName[0] = "bytecoin-bcn";
        if (cryptoName[0].contains("Peerplays"))
            cryptoName[0] = "peerplays-ppy";
        if (cryptoName[0].contains("iExec"))
            cryptoName[0] = "rlc";
        if (cryptoName[0].contains("LBRY"))
            cryptoName[0] = "library-credit";
        if (cryptoName[0].contains("I/O"))
            cryptoName[0] = "iocoin";
        if (cryptoName[0].contains("Metaverse"))
            cryptoName[0] = "metaverse";
        if (cryptoName[0].contains("WeTrust"))
            cryptoName[0] = "trust";
        if (cryptoName[0].contains("Cofound.it"))
            cryptoName[0] = "cofound-it";
        if (cryptoName[0].contains("Santiment"))
            cryptoName[0] = "santiment";
        if (cryptoName[0].contains("Matchpool"))
            cryptoName[0] = "guppy";
        if (cryptoName[0].contains("MUSE"))
            cryptoName[0] = "bitshares-music";
        if (cryptoName[0].contains("DAO.Casino"))
            cryptoName[0] = "dao-casino";
        if (cryptoName[0].contains("HEAT"))
            cryptoName[0] = "heat-ledger";
        if (cryptoName[0].contains("AdEx"))
            cryptoName[0] = "adx-net";

        init();
        setupRecyclerView();

        Button gotItBtn = findViewById(R.id.cryptoAboutGotItButton);
        gotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.justClosedDialog = true;

                if (!MainActivity.currentView.contains("MainActivity") && !getStoredPinnedCoins().equals(PinnedCoinsActivity.pinnedCoinsStr)) {
                    PinnedCoinsActivity.initialized = false;
                    Intent intent = new Intent(v.getContext(), PinnedCoinsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private String getStoredPinnedCoins() {
        return sharedPreferences.getString("pinned_coins", "");
    }

    @Override
    public void onStart() {
        super.onStart();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getChartHistoryPreference()) + getSelectedCrypto(selectedCrypto);
        chartLinesType = getChartLinesType();

        if (haveNetworkConnectionV2(getBaseContext())) {
            new JSONParse().execute();
        } else {
            finish();
        }
    }

    private void refreshGraph() {
        if (haveNetworkConnectionV2(this)) {
            if (!progressDialog.isShowing()) {
                progressDialog = new SpotsDialog(this, R.style.CustomProgressDialog);
                progressDialog.show();
            }

            graphView.removeAllSeries();
            new JSONGraph().execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init() {
        moreAboutCryptoActivity = findViewById(R.id.moreAboutCryptoActivity);
        recyclerView = findViewById(R.id.cryptoAboutRecyclerView);
        graphView = findViewById(R.id.graph);

        mOneDayButton = findViewById(R.id.oneDayChartBtn);
        mSevenDaysButton = findViewById(R.id.sevenDaysChartBtn);
        mOneMonthButton = findViewById(R.id.oneMonthChartBtn);
        mSixMonthButton = findViewById(R.id.sixMonthChartBtn);
        mOneYearButton = findViewById(R.id.oneYearChartBtn);
        mAllDataButton = findViewById(R.id.allDataChartBtn);

        graphView.getGridLabelRenderer().setTextSize(35);
        graphView.setTitleColor(getResources().getColor(R.color.colorTitle));

        mOneDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    Log.d(mTAG, "Pref: " + getChartHistoryLength(getChartHistoryPreference()));
                    if (!getChartHistoryLength(getChartHistoryPreference()).contains("history/1day")) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getString(R.string.one_day).toLowerCase()) + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.one_day).toLowerCase());
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });

        mSevenDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    if (!getChartHistoryLength(getChartHistoryPreference()).contains("history/7day")) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getString(R.string.seven_day).toLowerCase()) + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.seven_day).toLowerCase());
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });

        mOneMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    if (!getChartHistoryLength(getChartHistoryPreference()).contains("history/30day")) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getString(R.string.one_month).toLowerCase()) + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.one_month).toLowerCase());
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });

        mSixMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    if (!getChartHistoryLength(getChartHistoryPreference()).contains("history/180day")) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getString(R.string.six_month).toLowerCase()) + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.six_month).toLowerCase());
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });

        mOneYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    if (!getChartHistoryLength(getChartHistoryPreference()).contains("history/365day")) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getString(R.string.one_year).toLowerCase()) + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.one_year).toLowerCase());
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });

        mAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnectionV2(getBaseContext())) {
                    Log.d(mTAG, getChartHistoryPreference());
                    if (!getChartHistoryPreference().equals(getString(R.string.all_data_lower))) {
                        if (!progressDialog.isShowing()) {
                            progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                            progressDialog.show();
                        }

                        chartAddress = "http://www.coincap.io/history/" + getSelectedCrypto(selectedCrypto);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("chart_history_preference", getString(R.string.all_data_lower));
                        editor.apply();
                        new JSONGraph().execute();
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        cryptoAdapterList = new ArrayList<>();

        adapter = new AboutCryptoRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        boolean connectionEnabled = haveNetworkConnectionV2(getBaseContext());

        private AboutCryptoAdapter cryptoAdapter = new AboutCryptoAdapter();
        private JSONObject jObj;

        private String rank;
        private String name;
        private String symbol;
        private String price;
        private String marketCap;
        private String circulatingSupply;
        private String volume;
        private String percentChange1h;
        private String percentChange24h;
        private String percentChange7d;

        JSONArray jArray;
        StringBuffer buffer;

        private boolean errorFound = false;

        private String defaultCurrency = getDefaultCurrency();
        String apiAddress = "https://api.coinmarketcap.com/v1/ticker/" + cryptoName[0] + "/?convert=" + defaultCurrency;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (connectionEnabled) {
                progressDialog = new SpotsDialog(moreAboutCryptoActivity.getContext(), R.style.CustomProgressDialog);
                progressDialog.show();

                cryptoAdapterList.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection;
            BufferedReader reader;

            if (connectionEnabled) {
                try {
                    URL url = new URL(apiAddress);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    buffer = new StringBuffer();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String append = line + "\n";
                        buffer.append(append);
                    }

                    connection.disconnect();
                    reader.close();
                    stream.close();
                } catch (IOException e) {
                    errorFound = true;
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (errorFound) {
                Toast.makeText(getApplicationContext(), getString(R.string.coinmarketcap_error), Toast.LENGTH_SHORT).show();
            } else {
                if (connectionEnabled) {
                    try {
                        jArray = new JSONArray(buffer.toString());

                        for (int i = 0; i < jArray.length(); i++) {
                            jObj = jArray.getJSONObject(i);

                            rank = jObj.getString(TAG.RANK);
                            name = jObj.getString(TAG.NAME);
                            symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

                            DecimalFormat formatter = new DecimalFormat("0,000");

                            switch (defaultCurrency) {
                                case "USD":
                                    price = getString(R.string.price) + " : " + SYMBOL.USD + jObj.getString(TAG.PRICE_USD);

                                    String dUsdMarketCapStr = jObj.getString(TAG.MARKET_CAP_USD);
                                    Double dUsdMarketCap = null;
                                    if (!dUsdMarketCapStr.equals("null")) {
                                        dUsdMarketCap = Double.parseDouble(dUsdMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.USD + formatter.format(dUsdMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.USD + " ?";
                                    }

                                    String dUsdVolumeStr = jObj.getString(TAG.VOLUME_USD);
                                    Double dUsdVolume = null;
                                    if (!dUsdVolumeStr.equals("null")) {
                                        dUsdVolume = Double.parseDouble(dUsdVolumeStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.USD + formatter.format(dUsdVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.USD + " ?";
                                    }
                                    break;
                                case "EUR":
                                    price = getString(R.string.price) + " : " + SYMBOL.EUR + jObj.getString(TAG.PRICE_EUR);

                                    String dEurMarketCapStr = jObj.getString(TAG.MARKET_CAP_EUR);
                                    Double dEurMarketCap = null;
                                    if (!dEurMarketCapStr.equals("null")) {
                                        dEurMarketCap = Double.parseDouble(dEurMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.EUR + formatter.format(dEurMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.EUR + " ?";
                                    }

                                    String dEurVolumeStr = jObj.getString(TAG.VOLUME_EUR);
                                    Double dEurVolume = null;
                                    if (!dEurVolumeStr.equals("null")) {
                                        dEurVolume = Double.parseDouble(dEurVolumeStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.EUR + formatter.format(dEurVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.EUR + " ?";
                                    }
                                    break;
                                case "GBP":
                                    price = getString(R.string.price) + " : " + SYMBOL.GBP + jObj.getString(TAG.PRICE_GBP);

                                    String dGbpMarketCapStr = jObj.getString(TAG.MARKET_CAP_GBP);
                                    Double dGbpMarketCap = null;
                                    if (!dGbpMarketCapStr.equals("null")) {
                                        dGbpMarketCap = Double.parseDouble(dGbpMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.GBP + formatter.format(dGbpMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.GBP + " ?";
                                    }

                                    String dGbpVolumeStr = jObj.getString(TAG.VOLUME_GBP);
                                    Double dGbpVolume = null;
                                    if (!dGbpVolumeStr.equals("null")) {
                                        dGbpVolume = Double.parseDouble(dGbpVolumeStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.GBP + formatter.format(dGbpVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.GBP + " ?";
                                    }
                                    break;
                                case "BTC":
                                    price = getString(R.string.price) + " : " + SYMBOL.BTC + jObj.getString(TAG.PRICE_BTC);

                                    String dBtcMarketCapStr = jObj.getString(TAG.MARKET_CAP_BTC);
                                    Double dBtcMarketCap = null;
                                    if (!dBtcMarketCapStr.equals("null")) {
                                        dBtcMarketCap = Double.parseDouble(dBtcMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.BTC + formatter.format(dBtcMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.BTC + " ?";
                                    }

                                    String dBtcVolumeStr = jObj.getString(TAG.VOLUME_BTC);
                                    Double dBtcVolume = null;
                                    if (!dBtcVolumeStr.equals("null")) {
                                        dBtcVolume = Double.parseDouble(dBtcVolumeStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.BTC + formatter.format(dBtcVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.BTC + " ?";
                                    }
                                    break;
                                case "CAD":
                                    price = getString(R.string.price) + " : " + SYMBOL.CAD + jObj.getString(TAG.PRICE_CAD);

                                    String dCadMarketCapStr = jObj.getString(TAG.MARKET_CAP_CAD);
                                    Double dCadMarketCap = null;
                                    if (!dCadMarketCapStr.equals("null")) {
                                        dCadMarketCap = Double.parseDouble(dCadMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CAD + formatter.format(dCadMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CAD + " ?";
                                    }

                                    String dCadVolumeStr = jObj.getString(TAG.VOLUME_CAD);
                                    Double dCadVolume = null;
                                    if (!dCadVolumeStr.equals("null")) {
                                        dCadVolume = Double.parseDouble(dCadMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CAD + formatter.format(dCadVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CAD + " ?";
                                    }
                                    break;
                                case "JPY":
                                    price = getString(R.string.price) + " : " + SYMBOL.JPY + jObj.getString(TAG.PRICE_JPY);

                                    String dJpyMarketCapStr = jObj.getString(TAG.MARKET_CAP_JPY);
                                    Double dJpyMarketCap = null;
                                    if (!dJpyMarketCapStr.equals("null")) {
                                        dJpyMarketCap = Double.parseDouble(dJpyMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.JPY + formatter.format(dJpyMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.JPY + " ?";
                                    }

                                    String dJpyVolumeStr = jObj.getString(TAG.VOLUME_JPY);
                                    Double dJpyVolume = null;
                                    if (!dJpyVolumeStr.equals("null")) {
                                        dJpyVolume = Double.parseDouble(dJpyMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.JPY + formatter.format(dJpyVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.JPY + " ?";
                                    }
                                    break;
                                case "AUD":
                                    price = getString(R.string.price) + " : " + SYMBOL.AUD + jObj.getString(TAG.PRICE_AUD);

                                    String dAudMarketCapStr = jObj.getString(TAG.MARKET_CAP_AUD);
                                    Double dAudMarketCap = null;
                                    if (!dAudMarketCapStr.equals("null")) {
                                        dAudMarketCap = Double.parseDouble(dAudMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.AUD + formatter.format(dAudMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.AUD + " ?";
                                    }

                                    String dAudVolumeStr = jObj.getString(TAG.VOLUME_AUD);
                                    Double dAudVolume = null;
                                    if (!dAudVolumeStr.equals("null")) {
                                        dAudVolume = Double.parseDouble(dAudMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.AUD + formatter.format(dAudVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.AUD + " ?";
                                    }
                                    break;
                                case "CHF":
                                    price = getString(R.string.price) + " : " + SYMBOL.CHF + jObj.getString(TAG.PRICE_CHF);

                                    String dChfMarketCapStr = jObj.getString(TAG.MARKET_CAP_CHF);
                                    Double dChfMarketCap = null;
                                    if (!dChfMarketCapStr.equals("null")) {
                                        dChfMarketCap = Double.parseDouble(dChfMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CHF + formatter.format(dChfMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CHF + " ?";
                                    }

                                    String dChfVolumeStr = jObj.getString(TAG.VOLUME_CHF);
                                    Double dChfVolume = null;
                                    if (!dChfVolumeStr.equals("null")) {
                                        dChfVolume = Double.parseDouble(dChfMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CHF + formatter.format(dChfVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CHF + " ?";
                                    }
                                    break;
                                case "INR":
                                    price = getString(R.string.price) + " : " + SYMBOL.INR + jObj.getString(TAG.PRICE_INR);

                                    String dInrMarketCapStr = jObj.getString(TAG.MARKET_CAP_INR);
                                    Double dInrMarketCap = null;
                                    if (!dInrMarketCapStr.equals("null")) {
                                        dInrMarketCap = Double.parseDouble(dInrMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.INR + formatter.format(dInrMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.INR + " ?";
                                    }

                                    String dInrVolumeStr = jObj.getString(TAG.VOLUME_INR);
                                    Double dInrVolume = null;
                                    if (!dInrVolumeStr.equals("null")) {
                                        dInrVolume = Double.parseDouble(dInrMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.INR + formatter.format(dInrVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.INR + " ?";
                                    }
                                    break;
                                case "BRL":
                                    price = getString(R.string.price) + " : " + SYMBOL.BRL + jObj.getString(TAG.PRICE_BRL);

                                    String dBrlMarketCapStr = jObj.getString(TAG.MARKET_CAP_BRL);
                                    Double dBrlMarketCap = null;
                                    if (!dBrlMarketCapStr.equals("null")) {
                                        dBrlMarketCap = Double.parseDouble(dBrlMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.BRL + formatter.format(dBrlMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.BRL + " ?";
                                    }

                                    String dBrlVolumeStr = jObj.getString(TAG.VOLUME_BRL);
                                    Double dBrlVolume = null;
                                    if (!dBrlVolumeStr.equals("null")) {
                                        dBrlVolume = Double.parseDouble(dBrlMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.BRL + formatter.format(dBrlVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.BRL + " ?";
                                    }
                                    break;
                                case "PLN":
                                    price = getString(R.string.price) + " : " + SYMBOL.PLN + jObj.getString(TAG.PRICE_PLN);

                                    String dPlnMarketCapStr = jObj.getString(TAG.MARKET_CAP_PLN);
                                    Double dPlnMarketCap = null;
                                    if (!dPlnMarketCapStr.equals("null")) {
                                        dPlnMarketCap = Double.parseDouble(dPlnMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.PLN + formatter.format(dPlnMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.PLN + " ?";
                                    }

                                    String dPlnVolumeStr = jObj.getString(TAG.VOLUME_PLN);
                                    Double dPlnVolume = null;
                                    if (!dPlnVolumeStr.equals("null")) {
                                        dPlnVolume = Double.parseDouble(dPlnMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.PLN + formatter.format(dPlnVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.PLN + " ?";
                                    }
                                    break;
                                case "CNY":
                                    price = getString(R.string.price) + " : " + SYMBOL.CNY + jObj.getString(TAG.PRICE_CNY);

                                    String dCnyMarketCapStr = jObj.getString(TAG.MARKET_CAP_CNY);
                                    Double dCnyMarketCap = null;
                                    if (!dCnyMarketCapStr.equals("null")) {
                                        dCnyMarketCap = Double.parseDouble(dCnyMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CNY + formatter.format(dCnyMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.CNY + " ?";
                                    }

                                    String dCnyVolumeStr = jObj.getString(TAG.VOLUME_CNY);
                                    Double dCnyVolume = null;
                                    if (!dCnyVolumeStr.equals("null")) {
                                        dCnyVolume = Double.parseDouble(dCnyMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CNY + formatter.format(dCnyVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.CNY + " ?";
                                    }
                                    break;
                                case "SEK":
                                    price = getString(R.string.price) + " : " + SYMBOL.SEK + jObj.getString(TAG.PRICE_SEK);

                                    String dSekMarketCapStr = jObj.getString(TAG.MARKET_CAP_SEK);
                                    Double dSekMarketCap = null;
                                    if (!dSekMarketCapStr.equals("null")) {
                                        dSekMarketCap = Double.parseDouble(dSekMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.SEK + formatter.format(dSekMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.SEK + " ?";
                                    }

                                    String dSekVolumeStr = jObj.getString(TAG.VOLUME_SEK);
                                    Double dSekVolume = null;
                                    if (!dSekVolumeStr.equals("null")) {
                                        dSekVolume = Double.parseDouble(dSekMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.SEK + formatter.format(dSekVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.SEK + " ?";
                                    }
                                    break;
                                case "NZD":
                                    price = getString(R.string.price) + " : " + SYMBOL.NZD + jObj.getString(TAG.PRICE_NZD);

                                    String dNzdMarketCapStr = jObj.getString(TAG.MARKET_CAP_NZD);
                                    Double dNzdMarketCap = null;
                                    if (!dNzdMarketCapStr.equals("null")) {
                                        dNzdMarketCap = Double.parseDouble(dNzdMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.NZD + formatter.format(dNzdMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.NZD + " ?";
                                    }

                                    String dNzdVolumeStr = jObj.getString(TAG.VOLUME_NZD);
                                    Double dNzdVolume = null;
                                    if (!dNzdVolumeStr.equals("null")) {
                                        dNzdVolume = Double.parseDouble(dNzdMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.NZD + formatter.format(dNzdVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.NZD + " ?";
                                    }
                                    break;
                                case "MXN":
                                    price = getString(R.string.price) + " : " + SYMBOL.MXN + jObj.getString(TAG.PRICE_MXN);

                                    String dMxnMarketCapStr = jObj.getString(TAG.MARKET_CAP_MXN);
                                    Double dMxnMarketCap = null;
                                    if (!dMxnMarketCapStr.equals("null")) {
                                        dMxnMarketCap = Double.parseDouble(dMxnMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.MXN + formatter.format(dMxnMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.MXN + " ?";
                                    }

                                    String dMxnVolumeStr = jObj.getString(TAG.VOLUME_MXN);
                                    Double dMxnVolume = null;
                                    if (!dMxnVolumeStr.equals("null")) {
                                        dMxnVolume = Double.parseDouble(dMxnMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.MXN + formatter.format(dMxnVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.MXN + " ?";
                                    }
                                    break;
                                case "SGD":
                                    price = getString(R.string.price) + " : " + SYMBOL.SGD + jObj.getString(TAG.PRICE_SGD);

                                    String dSgdMarketCapStr = jObj.getString(TAG.MARKET_CAP_SGD);
                                    Double dSgdMarketCap = null;
                                    if (!dSgdMarketCapStr.equals("null")) {
                                        dSgdMarketCap = Double.parseDouble(dSgdMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.SGD + formatter.format(dSgdMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.SGD + " ?";
                                    }

                                    String dSgdVolumeStr = jObj.getString(TAG.VOLUME_SGD);
                                    Double dSgdVolume = null;
                                    if (!dSgdVolumeStr.equals("null")) {
                                        dSgdVolume = Double.parseDouble(dSgdMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.SGD + formatter.format(dSgdVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.SGD + " ?";
                                    }
                                    break;
                                case "HKD":
                                    price = getString(R.string.price) + " : " + SYMBOL.HKD + jObj.getString(TAG.PRICE_HKD);

                                    String dHkdMarketCapStr = jObj.getString(TAG.MARKET_CAP_HKD);
                                    Double dHkdMarketCap = null;
                                    if (!dHkdMarketCapStr.equals("null")) {
                                        dHkdMarketCap = Double.parseDouble(dHkdMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.HKD + formatter.format(dHkdMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.HKD + " ?";
                                    }

                                    String dHkdVolumeStr = jObj.getString(TAG.VOLUME_HKD);
                                    Double dHkdVolume = null;
                                    if (!dHkdVolumeStr.equals("null")) {
                                        dHkdVolume = Double.parseDouble(dHkdMarketCapStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.HKD + formatter.format(dHkdVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.HKD + " ?";
                                    }
                                    break;
                                default:
                                    price = getString(R.string.price) + " : $" + jObj.getString(TAG.PRICE_USD);

                                    String dDefaultMarketCapStr = jObj.getString(TAG.MARKET_CAP_USD);
                                    Double dDefaultMarketCap = null;
                                    if (!dDefaultMarketCapStr.equals("null")) {
                                        dDefaultMarketCap = Double.parseDouble(dDefaultMarketCapStr);
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.USD + formatter.format(dDefaultMarketCap);
                                    } else {
                                        marketCap = getString(R.string.market_cap) + " : " + SYMBOL.USD + " ?";
                                    }

                                    String dDefaultVolumeStr = jObj.getString(TAG.VOLUME_USD);
                                    Double dDefaultVolume = null;
                                    if (!dDefaultVolumeStr.equals("null")) {
                                        dDefaultVolume = Double.parseDouble(dDefaultVolumeStr);
                                        volume = getString(R.string.volume) + " : " + SYMBOL.USD + formatter.format(dDefaultVolume);
                                    } else {
                                        volume = getString(R.string.volume) + " : " + SYMBOL.USD + " ?";
                                    }
                                    break;
                            }

                            Double dCirculatingSupply = null;
                            String dCirculatingSupplyStr = jObj.getString(TAG.CIRCULATING_SUPPLY);
                            if (!dCirculatingSupplyStr.equals("null")) {
                                dCirculatingSupply = Double.parseDouble(jObj.getString(TAG.CIRCULATING_SUPPLY));
                                circulatingSupply = getString(R.string.circulating_supply) + " : " + formatter.format(dCirculatingSupply);
                            } else {
                                circulatingSupply = getString(R.string.circulating_supply) + " : ?";
                            }
                            percentChange1h = getString(R.string.percent_change_1h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_1H) + "%";
                            percentChange24h = getString(R.string.percent_change_24h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_24H) + "%";
                            percentChange7d = getString(R.string.percent_change_7d) + " : " + jObj.getString(TAG.PERCENT_CHANGE_7D) + "%";

                            cryptoAdapter = new AboutCryptoAdapter(rank, null, name + " " + symbol, price, marketCap, circulatingSupply,
                                    volume, percentChange1h, percentChange24h, percentChange7d);

                            cryptoAdapterList.add(i, cryptoAdapter);

                            adapter.notifyItemInserted(i);
                            adapter.notifyDataSetChanged();

                            new JSONGraph().execute();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getSelectedCrypto(String selectedCrypto) {
        String crypto = selectedCrypto;
        if (!crypto.equals("") && crypto.length() > 1) {
            String[] cryptoName = crypto.split(Pattern.quote("\n"));
            if (cryptoName[1] != null) {
                String[] cryptoSplit = cryptoName[1].split(Pattern.quote("("));
                String[] cryptoSplit2 = cryptoSplit[1].split(Pattern.quote(")"));

                return cryptoSplit2[0];
            }
        }

        return null;
    }

    public class JSONGraph extends AsyncTask<String, String, String> {

        private ArrayList<String> array = new ArrayList<>();
        //private ArrayList<String> marketCapList = new ArrayList<>();
        StringBuffer buffer;
        Date dMin = new Date();
        Date dMax = new Date();

        protected void onPreExecute() {
            graphView.removeAllSeries();

            switch (chartLinesType) {
                case "Horizontal":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
                    break;
                case "Vertical":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);
                    break;
                case "Horizontal and vertical":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
                    break;
                case "Horizontal et vertical":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
                    break;
                case "None":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
                    break;
                case "Aucune":
                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
                    break;
            }
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader;

            try {
                if (!chartAddress.contains("null")) {
                    URL url = new URL(chartAddress);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    buffer = new StringBuffer();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String append = line + "\n";
                        buffer.append(append);
                    }

                    connection.disconnect();
                    reader.close();
                    stream.close();

                    return buffer.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            Log.d(mTAG, result.length() + "");
            if (!result.contains("null") && result.length() > 41) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray priceArray = jObj.getJSONArray(TAG.PRICE);
                    //JSONArray marketCapArray = jObj.getJSONArray(TAG.MARKET_CAP);
                    //DataPoint[] marketCap = new DataPoint[marketCapArray.length()];

                    DataPoint[] values = new DataPoint[priceArray.length()];

                    for (int i = 0; i < priceArray.length(); i++) {
                        String value = priceArray.getString(i);
                        String[] split1 = value.split(Pattern.quote(","));
                        String[] split2 = split1[1].split(Pattern.quote("]"));

                        String timestamp[] = split1[0].split(Pattern.quote("["));
                        String mTimestamp = timestamp[1].substring(0, 10);

                        Timestamp stamp = new Timestamp(Long.valueOf(mTimestamp) * 1000);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm", Locale.getDefault());
                        Date d = new Date(stamp.getTime());

                        if (i == 0) {
                            dMin = d;
                        } else {
                            dMax = d;
                        }

                        Log.d(mTAG, format.format(d));

                        String point = "";

                        switch (getDefaultCurrency()) {
                            case "USD":
                                point = split2[0];
                                break;
                            case "EUR":
                                point = String.valueOf(convertUsdToEur(split2[0], MainActivity.convertEur));
                                break;
                            case "GBP":
                                point = String.valueOf(convertUsdToGbp(split2[0], MainActivity.convertGbp));
                                break;
                            case "BTC":
                                point = split2[0];
                                break;
                            case "CAD":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertCad));
                                break;
                            case "JPY":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertJpy));
                                break;
                            case "AUD":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertAud));
                                break;
                            case "CHF":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertChf));
                                break;
                            case "INR":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertInr));
                                break;
                            case "BRL":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertBrl));
                                break;
                            case "PLN":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertPln));
                                break;
                            case "CNY":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertCny));
                                break;
                            case "SEK":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertSek));
                                break;
                            case "NZD":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertNzd));
                                break;
                            case "MXN":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertMxn));
                                break;
                            case "SGD":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertSgd));
                                break;
                            case "HKD":
                                point = String.valueOf(convertUsdTo(split2[0], MainActivity.convertHkd));
                                break;
                            default:
                                point = split2[0];
                                break;
                        }

                        array.add(point);
                        DataPoint v = new DataPoint(d.getTime(), Double.parseDouble(point));
                        values[i] = v;
                    }

                    //for (int j = 0; j < marketCapArray.length(); j++) {
                    //    String mCapStr = marketCapArray.getString(j);
                    //    String[] mCapSplit1 = mCapStr.split(Pattern.quote(","));
                    //    String[] mCapSplit2 = mCapSplit1[1].split(Pattern.quote("]"));
                    //
                    //    Log.d(mTAG, mCapSplit2[0]);
                    //    marketCapList.add(mCapSplit2[0]);
                    //    DataPoint w = new DataPoint(j, Double.parseDouble(mCapSplit2[0]));
                    //    marketCap[j] = w;
                    //}
                    //float minMarketCapValue = getMinValue(marketCapList);
                    //float maxMarketCapValue = getMaxValue(marketCapList);
                    //LineGraphSeries<DataPoint> marketCapSeries = new LineGraphSeries<>(marketCap);

                    float minValue = getMinValue(array);
                    float maxValue = getMaxValue(array);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);

                    series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPoint) {

                            String data = dataPoint.toString();
                            String[] splitData1 = data.split(Pattern.quote("/"));
                            String[] splitData2 = splitData1[1].split(Pattern.quote("]"));
                            String priceAtThisPoint;
                            //String convertToEuro = String.valueOf(convertUsdToEur(splitData2[0], convertEur));
                            //String convertToGbp = String.valueOf(convertUsdToGbp(splitData2[0], convertGbp));

                            switch (getDefaultCurrency()) {
                                case "USD":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.USD + splitData2[0];
                                    break;
                                case "EUR":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.EUR + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.EUR + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.EUR + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.EUR + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    //priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.EUR + splitData2[0];
                                    break;
                                case "GBP":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.GBP + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.GBP + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.GBP + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.GBP + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    //priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.GBP + splitData2[0];
                                    break;
                                case "BTC":
                                    //priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.USD + splitData2[0];
                                    //int zero;
                                    String usdToBtc = String.valueOf(convertUsdToBtc(splitData2[0], MainActivity.btcPrice));
                                    if (usdToBtc.contains("E")) {
                                        /*String[] split1 = usdToBtc.split(Pattern.quote("E-"));
                                        Log.d(mTAG, split1[1]);
                                        zero = Integer.valueOf(split1[1]);
                                        Log.d(mTAG, "" + zero);*/
                                        priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.USD + splitData2[0];
                                    } else {
                                        priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.USD + splitData2[0] + " (" + SYMBOL.BTC + String.valueOf(convertUsdToBtc(splitData2[0], MainActivity.btcPrice)) + ")";
                                    }
                                    break;
                                case "CAD":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CAD + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CAD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CAD + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CAD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "JPY":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.JPY + splitData2[0];
                                    break;
                                case "AUD":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.AUD + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.AUD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.AUD + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.AUD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "CHF":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CHF + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CHF + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CHF + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CHF + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "INR":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.INR + splitData2[0];
                                    break;
                                case "BRL":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 8) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.BRL + splitData2[0].substring(0, 8);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.BRL + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.BRL + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.BRL + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "PLN":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 8) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.PLN + splitData2[0].substring(0, 8);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.PLN + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.PLN + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.PLN + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "CNY":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.CNY + splitData2[0];
                                    break;
                                case "SEK":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.SEK + splitData2[0];
                                    break;
                                case "NZD":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.NZD + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.NZD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.NZD + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.NZD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "MXN":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.MXN + splitData2[0];
                                    break;
                                case "SGD":
                                    if (splitData2[0].startsWith("0.")) {
                                        if (splitData2[0].length() >= 7) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.SGD + splitData2[0].substring(0, 7);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.SGD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    } else {
                                        if (splitData2[0].length() >= 6) {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.SGD + splitData2[0].substring(0, 6);
                                        } else {
                                            priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.SGD + splitData2[0].substring(0, splitData2[0].length());
                                        }
                                    }
                                    break;
                                case "HKD":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.HKD + splitData2[0];
                                    break;
                                default:
                                    priceAtThisPoint = getString(R.string.price_at_point) + " " + SYMBOL.USD + splitData2[0];
                                    break;
                            }

                            Toast.makeText(getApplicationContext(), priceAtThisPoint, Toast.LENGTH_SHORT).show();
                        }
                    });

                    series.setDrawBackground(true);
                    series.setColor(Color.parseColor("#80ff80"));
                    series.setBackgroundColor(Color.argb(20, 128, 255, 128));
                    series.setThickness(4);

                    graphView.getViewport().setYAxisBoundsManual(true);
                    graphView.getViewport().setMinY(minValue);
                    graphView.getViewport().setMaxY(maxValue);

                    graphView.getViewport().setXAxisBoundsManual(true);
                    graphView.getViewport().setMinX(dMin.getTime());
                    graphView.getViewport().setMaxX(dMax.getTime());

                    graphView.getViewport().setScalable(true);
                    graphView.getViewport().setScrollable(true);
                    graphView.getViewport().setScrollableY(true);
                    graphView.getViewport().setScalableY(true);

                    graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
                    graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);

                    SimpleDateFormat format;
                    switch (getChartHistoryPreference()) {
                        case "1 day":
                            format = new SimpleDateFormat("hh:mm", Locale.getDefault());
                            break;
                        case "1 jour":
                            format = new SimpleDateFormat("hh:mm", Locale.getDefault());
                            break;
                        case "7 day":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "7 jours":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "1 month":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "1 mois":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "6 month":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "6 mois":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "1 year":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "1 an":
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                        case "All data":
                            format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            break;
                        case "Toute les données":
                            format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            break;
                        default:
                            format = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            break;
                    }
                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(), format));

                    String legend = "";
                    String min = String.valueOf(minValue);
                    String max = String.valueOf(maxValue);

                    switch (getDefaultCurrency()) {
                        case "USD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            //legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue) + " (" + getChartHistoryPreference() + ")";
                            //Log.d(mTAG, "min: " + min.length());
                            break;
                        case "EUR":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.EUR + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.EUR + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.EUR + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.EUR + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.EUR + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.EUR + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.EUR + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.EUR + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            //legend = getString(R.string.min_price) + " " + SYMBOL.EUR + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.EUR + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "GBP":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.GBP + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.GBP + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.GBP + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.GBP + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.GBP + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.GBP + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.GBP + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.GBP + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "BTC":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "CAD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CAD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.CAD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CAD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CAD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CAD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.CAD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CAD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CAD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "JPY":
                            legend = getString(R.string.min_price) + " " + SYMBOL.JPY + String.valueOf(minValue) + ", " + getString(R.string.max_price) + " " + SYMBOL.JPY + String.valueOf(maxValue) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "AUD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.AUD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.AUD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.AUD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.AUD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.AUD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.AUD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.AUD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.AUD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "CHF":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CHF + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.CHF + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CHF + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CHF + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CHF + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.CHF + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CHF + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CHF + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "INR":
                            legend = getString(R.string.min_price) + " " + SYMBOL.INR + String.valueOf(minValue) + ", " + getString(R.string.max_price) + " " + SYMBOL.INR + String.valueOf(maxValue) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "BRL":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.BRL + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.BRL + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.BRL + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.BRL + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.BRL + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.BRL + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.BRL + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.BRL + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "PLN":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.PLN + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.PLN + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.PLN + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.PLN + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.PLN + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.PLN + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.PLN + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.PLN + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "CNY":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CNY + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.CNY + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CNY + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CNY + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CNY + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.CNY + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.CNY + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.CNY + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "SEK":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SEK + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.SEK + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SEK + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.SEK + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SEK + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.SEK + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SEK + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.SEK + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "NZD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.NZD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.NZD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.NZD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.NZD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.NZD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.NZD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.NZD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.NZD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "MXN":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.MXN + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.MXN + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.MXN + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.MXN + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.MXN + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.MXN + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.MXN + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.MXN + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "SGD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SGD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.SGD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SGD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.SGD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SGD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.SGD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.SGD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.SGD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        case "HKD":
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 9 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.HKD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.HKD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.HKD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.HKD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 8 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.HKD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.HKD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.HKD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.HKD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                        default:
                            if (min.startsWith("0.") || max.startsWith("0.")) {
                                if (min.length() >= 8 && max.length() >= 8) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 8) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 8) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            } else {
                                if (min.length() >= 7 && max.length() >= 7) {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, 7) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                                } else {
                                    legend = getString(R.string.min_price) + " " + SYMBOL.USD + String.valueOf(minValue).substring(0, min.length()) + ", " + getString(R.string.max_price) + " " + SYMBOL.USD + String.valueOf(maxValue).substring(0, max.length()) + " (" + getChartHistoryPreference() + ")";
                                }
                            }
                            break;
                    }

                    graphView.setTitle(legend);
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
                    graphView.addSeries(series);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LineGraphSeries<DataPoint> nullSeries = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0)
                });

                graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
                graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
                graphView.addSeries(nullSeries);
                Toast.makeText(getApplicationContext(), getString(R.string.no_chart), Toast.LENGTH_LONG).show();
            }

            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    //private float convertUsdToEur(float usdValue) {
    //    return usdValue * 0.85f;
    //}

    //private float convertUsdToEur(String usdValue) {
    //    return Float.parseFloat(usdValue) * 0.85f;
    //}

    //private float convertUsdToEur(float usdValue, float exchangeRate) {
    //    return usdValue * exchangeRate;
    //}

    //private float convertUsdToEur(String usdValue, float exchangeRate) {
    //    return Float.parseFloat(usdValue) * exchangeRate;
    //}

    private float convertUsdToEur(float usdValue, String exchangeRate) {
        return usdValue * Float.valueOf(exchangeRate);
    }

    private float convertUsdToEur(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    }

    private float convertUsdToBtc(float usdValue, String exchangeRate) {
        return usdValue / Float.valueOf(exchangeRate);
    }

    private float convertUsdToBtc(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) / Float.valueOf(exchangeRate);
    }

    private float convertUsdToBtc(float usdValue, float exchangeRate) {
        return usdValue / exchangeRate;
    }

    //private float convertUsdToGbp(float usdValue) {
    //    return usdValue * 0.775f;
    //}

    //private float convertUsdToGbp(String usdValue) {
    //   return Float.parseFloat(usdValue) * 0.775f;
    //}

    //private float convertUsdToGbp(float usdValue, float exchangeRate) {
    //    return usdValue * exchangeRate;
    //}

    private float convertUsdToGbp(float usdValue, String exchangeRate) {
        return usdValue * Float.valueOf(exchangeRate);
    }

    //private float convertUsdToGbp(String usdValue, float exchangeRate) {
    //    return Float.valueOf(usdValue) * exchangeRate;
    //}

    private float convertUsdToGbp(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
    }

    private String getChartLinesType() {
        return sharedPreferences.getString("chart_line_preference", "Horizontal");
    }

    private String getChartHistoryPreference() {
        return sharedPreferences.getString("chart_history_preference", "7 days");
    }

    private String getChartHistoryLength(String preference) {
        switch (preference) {
            case "1 day":
                return "history/1day/";
            case "1 jour":
                return "history/1day/";
            case "7 day":
                return "history/7day/";
            case "7 jours":
                return "history/7day/";
            case "1 month":
                return "history/30day/";
            case "1 mois":
                return "history/30day/";
            case "6 month":
                return "history/180day/";
            case "6 mois":
                return "history/180day/";
            case "1 year":
                return "history/365day/";
            case "1 an":
                return "history/365day/";
            case "All data":
                return "history/";
            case "Toute les données":
                return "history/";
            default:
                return "history/7day/";
        }
    }

    private boolean haveNetworkConnectionV2(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = conMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = conMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifi.isAvailable() && wifi.isConnected())
                || (mobile.isAvailable() && mobile.isConnected())) {
            Log.i("Is Net work?", "isNetWork:in 'isNetWork_if' is N/W Connected:"
                    + NetworkInfo.State.CONNECTED);
            return true;
        } else if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    private float getMaxValue(ArrayList<String> arrayList) {
        float[] floatArray = new float[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            floatArray[i] = Float.parseFloat(arrayList.get(i));
        }

        float max = 0;
        for (int i = 0; i < floatArray.length; i++) {
            if (floatArray[i] > max) {
                max = floatArray[i];
            }
        }

        return max;
    }

    private float getMinValue(ArrayList<String> arrayList) {
        float[] floatArray = new float[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            floatArray[i] = Float.parseFloat(arrayList.get(i));
        }

        float min = floatArray[0];
        for (int i = 0; i < floatArray.length; i++) {
            if (floatArray[i] < min) {
                min = floatArray[i];
            }
        }

        return min;
    }

    private float convertUsdTo(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    }

    private float convertUsdTo(float usdValue, float exchangeRate) {
        return usdValue * exchangeRate;
    }

    private float convertUsdTo(String usdValue, float exchangeRate) {
        return Float.valueOf(usdValue) * exchangeRate;
    }

    private float convertUsdTo(float usdValue, String exchangeRate) {
        return usdValue * Float.valueOf(exchangeRate);
    }
}
