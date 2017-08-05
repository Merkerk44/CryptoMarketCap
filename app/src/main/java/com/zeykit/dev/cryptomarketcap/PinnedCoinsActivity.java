package com.zeykit.dev.cryptomarketcap;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

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
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PinnedCoinsActivity extends AppCompatActivity {

    private final String _TAG = "CryptoMarketCap";

    private ActionBar actionBar;
    static LinearLayout pinnedCoinsLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private List<CryptoAdapter> cryptoAdapterList;
    private CryptoRvAdapter adapter;

    private TinyDB tinyDB;

    private SharedPreferences sharedPreferences = null;

    static boolean initialized = false;

    static String pinnedCoinsStr = "";

    private ArrayList<String> pinnedCoins;

    private interface TAG {
        String RANK = "rank";
        String NAME = "name";
        String SYMBOL = "symbol";
        String PRICE_USD = "price_usd";
        String PRICE_EUR = "price_eur";
        String PRICE_GBP = "price_gbp";
        String PRICE_BTC = "price_btc";
        String PERCENT_CHANGE_24H = "percent_change_24h";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pined_coins_layout);

        init();
        setupActionBar();
        setupRecyclerView();

        /*tinyDB = new TinyDB(getApplicationContext());
        if (tinyDB.getString("pinned_coins").isEmpty()) {
            CryptoAdapter cryptoAdapter = new CryptoAdapter("", null, getString(R.string.no_pinned_coins), "", "");
            cryptoAdapterList.add(cryptoAdapter);

            adapter.notifyDataSetChanged();
        }*/

        tinyDB = new TinyDB(getApplicationContext());
        pinnedCoins = tinyDB.getListString("pinned_coins");
        Collections.reverse(pinnedCoins);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!tinyDB.getString("pinned_coins").isEmpty()) {
            if (haveNetworkConnectionV2(getApplicationContext())) {
                if (!initialized) {
                    new JSONParse().execute();
                    initialized = true;
                }
            } else {
                showConnectionDialog();
            }
        } else {
            CryptoAdapter cryptoAdapter = new CryptoAdapter("", null, getString(R.string.no_pinned_coins), "", "");
            cryptoAdapterList.add(cryptoAdapter);
            adapter.notifyDataSetChanged();
        }

        Log.d("CryptoMarketCap", sharedPreferences.getString("pinned_coins", ""));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cryptoAdapterList.clear();
                new JSONParse().execute();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity.mRunningActivity = this.getClass().getSimpleName();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        initialized = false;
    }

    private void init() {
        recyclerView = findViewById(R.id.pinnedRecyclerView);
        swipeRefreshLayout = findViewById(R.id.pinnedSwipeRefreshLayout);
        pinnedCoinsLayout = findViewById(R.id.pinned_coins_layout);
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cryptoAdapterList = new ArrayList<>();

        adapter = new CryptoRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        private String pinnedCoin = "";
        private String apiAddress = "https://api.coinmarketcap.com/v1/ticker/";

        private SpotsDialog progressDialog;

        private CryptoAdapter cryptoAdapter = new CryptoAdapter();
        private JSONObject jObj;
        private JSONArray jArray;
        private StringBuffer buffer;

        private String rank;
        private String name;
        private String symbol;
        private String price;
        private String percentChange;

        private String defaultCurrency = getDefaultCurrency();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);

            if (haveNetworkConnectionV2(getApplicationContext())) {
                progressDialog = new SpotsDialog(pinnedCoinsLayout.getContext(), R.style.CustomProgressDialog);
                progressDialog.show();

                cryptoAdapterList.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(String... value) {
            super.onProgressUpdate(value);

            try {
                jArray = new JSONArray(buffer.toString());

                for (int j = 0; j < jArray.length(); j++) {
                    jObj = jArray.getJSONObject(j);

                    rank = jObj.getString(TAG.RANK);
                    name = jObj.getString(TAG.NAME);
                    symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

                    price = "$" + jObj.getString(TAG.PRICE_USD);

                    switch (defaultCurrency) {
                        case "USD":
                            price = "$" + jObj.getString(TAG.PRICE_USD);
                            break;
                        case "EUR":
                            price = "€" + jObj.getString(TAG.PRICE_EUR);
                            break;
                        case "GBP":
                            price = "£" + jObj.getString(TAG.PRICE_GBP);
                            break;
                        case "BTC":
                            price = "฿" + jObj.getString(TAG.PRICE_BTC);
                            break;
                    }

                    percentChange = jObj.getString(TAG.PERCENT_CHANGE_24H) + "%";

                    cryptoAdapter = new CryptoAdapter(rank, null, name + "\n" + symbol, price, percentChange);
                    cryptoAdapterList.add(j, cryptoAdapter);

                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader;

            if (haveNetworkConnectionV2(getApplicationContext())) {
                try {
                    if (!pinnedCoins.isEmpty()) {
                        for (int i = 0; i < pinnedCoins.size(); i++) {

                            if (pinnedCoins.get(i).contains(" ")) {
                                pinnedCoin = pinnedCoins.get(i).replace(" ", "-");
                            } else if (pinnedCoins.get(i).contains("DubaiCoin")) {
                                pinnedCoin = "dubaicoin-dbix";
                            } else if (pinnedCoins.get(i).contains("Golem")) {
                                pinnedCoin = "golem-network-tokens";
                            } else if (pinnedCoins.get(i).contains("Stellar")) {
                                pinnedCoin = "stellar";
                            } else if (pinnedCoins.get(i).contains("Gnosis")) {
                                pinnedCoin = "gnosis-gno";
                            } else if (pinnedCoins.get(i).contains("Bytecoin")) {
                                pinnedCoin = "bytecoin-bcn";
                            } else if (pinnedCoins.get(i).contains("Peerplays")) {
                                pinnedCoin = "peerplays-ppy";
                            } else if (pinnedCoins.get(i).contains("iExec")) {
                                pinnedCoin = "rlc";
                            } else if (pinnedCoins.get(i).contains("LBRY")) {
                                pinnedCoin = "library-credit";
                            } else if (pinnedCoins.get(i).contains("I/O")) {
                                pinnedCoin = "iocoin";
                            } else if (pinnedCoins.get(i).contains("Metaverse")) {
                                pinnedCoin = "metaverse";
                            } else if (pinnedCoins.get(i).contains("WeTrust")) {
                                pinnedCoin = "trust";
                            } else if (pinnedCoins.get(i).contains("Cofound.it")) {
                                pinnedCoin = "cofound-it";
                            } else if (pinnedCoins.get(i).contains("Santiment")) {
                                pinnedCoin = "santiment";
                            } else if (pinnedCoins.get(i).contains("Matchpool")) {
                                pinnedCoin = "guppy";
                            }
                            else {
                                pinnedCoin = pinnedCoins.get(i);
                            }

                            URL url = new URL(apiAddress + pinnedCoin + "/?convert=" + defaultCurrency);
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

                            publishProgress(params);
                            Log.d("CryptoMarketCap", url.toString());

                            connection.disconnect();
                            reader.close();
                            stream.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                progressDialog = null;
            }

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);

            storePinnedCoinsPref();
        }
    }

    private void storePinnedCoinsPref() {
        pinnedCoinsStr = sharedPreferences.getString("pinned_coins", "");
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
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

    private void showConnectionDialog() {
        RequestConnectionDialog requestConnectionDialog = new RequestConnectionDialog();
        requestConnectionDialog.show(getSupportFragmentManager(), null);
    }
}
