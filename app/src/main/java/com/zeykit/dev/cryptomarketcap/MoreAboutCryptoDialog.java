package com.zeykit.dev.cryptomarketcap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MoreAboutCryptoDialog extends FragmentActivity {

    static LinearLayout moreAboutCryptoActivity;
    RecyclerView recyclerView;
    private List<AboutCryptoAdapter> cryptoAdapterList;
    private AboutCryptoRvAdapter adapter;

    static String selectedCrypto = "";
    private String[] cryptoName = selectedCrypto.split("\n");
    private String apiAddress = "";

    private SharedPreferences sharedPreferences;

    private interface TAG {
        String NAME = "name";
        String SYMBOL = "symbol";
        String RANK = "rank";
        String PRICE_USD = "price_usd";
        String PRICE_EUR = "price_eur";
        String PRICE_GBP = "price_gbp";
        String PRICE_BTC = "price_btc";
        String VOLUME_USD = "24h_volume_usd";
        String MARKET_CAP_USD = "market_cap_usd";
        String VOLUME_EUR = "24h_volume_eur";
        String MARKET_CAP_EUR = "market_cap_eur";
        String VOLUME_GBP = "24h_volume_gbp";
        String MARKET_CAP_GBP = "market_cap_gbp";
        String VOLUME_BTC = "24h_volume_btc";
        String MARKET_CAP_BTC = "market_cap_btc";
        String CIRCULATING_SUPPLY = "available_supply";
        String PERCENT_CHANGE_1H = "percent_change_1h";
        String PERCENT_CHANGE_24H = "percent_change_24h";
        String PERCENT_CHANGE_7D = "percent_change_7d";
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

        init();
        setupRecyclerView();

        Button gotItBtn = (Button) findViewById(R.id.cryptoAboutGotItButton);
        gotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                if (MainActivity.currentView.contains("MainActivity")) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), PinnedCoinsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (haveNetworkConnectionV2())
            new JSONParse().execute();
        else
            showConnectionDialog();

    }

    private void init() {
        moreAboutCryptoActivity = (LinearLayout) findViewById(R.id.moreAboutCryptoActivity);
        recyclerView = (RecyclerView) findViewById(R.id.cryptoAboutRecyclerView);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        cryptoAdapterList = new ArrayList<>();

        adapter = new AboutCryptoRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);
    }

    private boolean haveNetworkConnection() {
        boolean wifiConnected = false;
        boolean dataConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected())
                    wifiConnected = true;
            } else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected())
                    dataConnected = true;
            }
        }
        return wifiConnected || dataConnected;
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        boolean connectionEnabled = haveNetworkConnectionV2();

        private SpotsDialog progressDialog;

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
                    String line = "";

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
                Toast.makeText(getApplicationContext(), "CoinMarketCap returning an error", Toast.LENGTH_SHORT).show();
            } else {
                if (connectionEnabled) {
                    try {
                        jArray = new JSONArray(buffer.toString());

                        for (int i = 0; i < jArray.length(); i++) {
                            jObj = jArray.getJSONObject(i);

                            rank = jObj.getString(TAG.RANK);
                            name = jObj.getString(TAG.NAME);
                            symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

                            switch (defaultCurrency) {
                                case "USD":
                                    price = getString(R.string.price) + " : $" + jObj.getString(TAG.PRICE_USD);
                                    marketCap = getString(R.string.market_cap) + " : $" + jObj.getString(TAG.MARKET_CAP_USD);
                                    volume = getString(R.string.volume) + " : $" + jObj.getString(TAG.VOLUME_USD);
                                    break;
                                case "EUR":
                                    price = getString(R.string.price) + " : €" + jObj.getString(TAG.PRICE_EUR);
                                    marketCap = getString(R.string.market_cap) + " : €" + jObj.getString(TAG.MARKET_CAP_EUR);
                                    volume = getString(R.string.volume) + " : €" + jObj.getString(TAG.VOLUME_EUR);
                                    break;
                                case "GBP":
                                    price = getString(R.string.price) + " : £" + jObj.getString(TAG.PRICE_GBP);
                                    marketCap = getString(R.string.market_cap) + " : £" + jObj.getString(TAG.MARKET_CAP_GBP);
                                    volume = getString(R.string.volume) + " : £" + jObj.getString(TAG.VOLUME_GBP);
                                    break;
                                case "BTC":
                                    price = getString(R.string.price) + " : ฿" + jObj.getString(TAG.PRICE_BTC);
                                    marketCap = getString(R.string.market_cap) + " : ฿" + jObj.getString(TAG.MARKET_CAP_BTC);
                                    volume = getString(R.string.volume) + " : ฿" + jObj.getString(TAG.VOLUME_BTC);
                                    break;
                            }

                            circulatingSupply = getString(R.string.circulating_supply) + " : $" + jObj.getString(TAG.CIRCULATING_SUPPLY);
                            percentChange1h = getString(R.string.percent_change_1h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_1H) + "%";
                            percentChange24h = getString(R.string.percent_change_24h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_24H) + "%";
                            percentChange7d = getString(R.string.percent_change_7d) + " : " + jObj.getString(TAG.PERCENT_CHANGE_7D) + "%";

                            cryptoAdapter = new AboutCryptoAdapter(rank, getDrawable(R.drawable.bitcoin_icon), name + " " + symbol, price, marketCap, circulatingSupply,
                                    volume, percentChange1h, percentChange24h, percentChange7d);

                            cryptoAdapterList.add(i, cryptoAdapter);

                            adapter.notifyItemInserted(i);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
    }

    private boolean haveNetworkConnectionV2() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mLte = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return mWifi.isConnected() || mLte.isConnected();
    }

    private void showConnectionDialog() {
        RequestConnectionDialog requestConnectionDialog = new RequestConnectionDialog();
        requestConnectionDialog.show(getSupportFragmentManager(), null);
    }
}
