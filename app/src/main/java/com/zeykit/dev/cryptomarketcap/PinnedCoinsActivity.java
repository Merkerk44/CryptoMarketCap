package com.zeykit.dev.cryptomarketcap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PinnedCoinsActivity extends AppCompatActivity {

    final String _TAG = "CryptoMarketCap";

    ActionBar actionBar;
    LinearLayout pinnedCoinsLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private List<CryptoAdapter> cryptoAdapterList;
    private PinnedCoinRvAdapter adapter;

    private TinyDB tinyDB;

    private SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor sharedPreferencesEditor = null;

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
        String PERCENT_CHANGE_24H = "percent_change_24h";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pined_coins_layout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        init();
        setupActionBar();
        setupRecyclerView();

        if (keepScreenTurnedOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        tinyDB = new TinyDB(getApplicationContext());
        pinnedCoins = tinyDB.getListString("pinned_coins");

        //if (!orderManuallyChanged()) {
        Collections.reverse(pinnedCoins);
        //}


        if (!tinyDB.getString("pinned_coins").isEmpty()) {
            if (haveNetworkConnectionV2(getApplicationContext())) {
        //tinyDB.remove("pinned_coins");
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
                //cryptoAdapterList.clear();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pinned_coins_menu, menu);
        return true;
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
            //case android.R.id.home:
            //    onBackPressed();
            //    return true;
            case R.id.action_delete_all:
                if (!cryptoAdapterList.get(0).getName().contains(getString(R.string.no_pinned_coins))) {
                    showDeleteDialog();
                } else {
                    Snackbar.make(pinnedCoinsLayout, getString(R.string.pinned_coins_list_empty), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setMessage(getString(R.string.remove_dialog_message))
                .setTitle(getString(R.string.remove_dialog_title))
                .setPositiveButton(getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        removePinnedCoinsList();
                    }
                })
                .setNegativeButton(getString(R.string.no_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removePinnedCoinsList() {
        pinnedCoins.clear();
        cryptoAdapterList.clear();
        tinyDB.putListString("pinned_coins", pinnedCoins);

        CryptoAdapter cryptoAdapter = new CryptoAdapter("", null, getString(R.string.no_pinned_coins), "", "");
        cryptoAdapterList.add(cryptoAdapter);
        adapter.notifyDataSetChanged();

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("order_manually_changed", false);
        sharedPreferencesEditor.apply();

        Snackbar.make(pinnedCoinsLayout, getString(R.string.pinned_coins_list_removed), Snackbar.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cryptoAdapterList = new ArrayList<>();

        adapter = new PinnedCoinRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);

        /*final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (fromPosition > toPosition) {
                    Collections.swap(pinnedCoins, fromPosition, fromPosition + 1);
                } else {
                    Collections.swap(pinnedCoins, fromPosition, fromPosition - 1);
                }

                adapter.onItemMove(fromPosition, toPosition);

                tinyDB.putListString("pinned_coins", pinnedCoins);
                Log.d(_TAG, tinyDB.getListString("pinned_coins").toString());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
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

        private boolean notified;

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
            Log.d(_TAG, buffer.toString());

            try {
                jArray = new JSONArray(buffer.toString());

                for (int j = 0; j < jArray.length(); j++) {
                    jObj = jArray.getJSONObject(j);

                    rank = jObj.getString(TAG.RANK);
                    name = jObj.getString(TAG.NAME);
                    symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

                    price = SYMBOL.USD + jObj.getString(TAG.PRICE_USD);

                    switch (defaultCurrency) {
                        case "USD":
                            price = SYMBOL.USD + jObj.getString(TAG.PRICE_USD);
                            break;
                        case "EUR":
                            price = SYMBOL.EUR + jObj.getString(TAG.PRICE_EUR);
                            break;
                        case "GBP":
                            price = SYMBOL.GBP + jObj.getString(TAG.PRICE_GBP);
                            break;
                        case "BTC":
                            price = SYMBOL.BTC + jObj.getString(TAG.PRICE_BTC);
                            break;
                        case "CAD":
                            price = SYMBOL.CAD + jObj.getString(TAG.PRICE_CAD);
                            break;
                        case "JPY":
                            price = SYMBOL.JPY + jObj.getString(TAG.PRICE_JPY);
                            break;
                        case "AUD":
                            price = SYMBOL.AUD + jObj.getString(TAG.PRICE_AUD);
                            break;
                        case "CHF":
                            price = SYMBOL.CHF + jObj.getString(TAG.PRICE_CHF);
                            break;
                        case "INR":
                            price = SYMBOL.INR + jObj.getString(TAG.PRICE_INR);
                            break;
                        case "BRL":
                            price = SYMBOL.BRL + jObj.getString(TAG.PRICE_BRL);
                            break;
                        case "PLN":
                            price = SYMBOL.PLN + jObj.getString(TAG.PRICE_PLN);
                            break;
                        case "CNY":
                            price = SYMBOL.CNY + jObj.getString(TAG.PRICE_CNY);
                            break;
                        case "SEK":
                            price = SYMBOL.SEK + jObj.getString(TAG.PRICE_SEK);
                            break;
                        case "NZD":
                            price = SYMBOL.NZD + jObj.getString(TAG.PRICE_NZD);
                            break;
                        case "MXN":
                            price = SYMBOL.MXN + jObj.getString(TAG.PRICE_MXN);
                            break;
                        case "SGD":
                            price = SYMBOL.SGD + jObj.getString(TAG.PRICE_SGD);
                            break;
                        case "HKD":
                            price = SYMBOL.HKD + jObj.getString(TAG.PRICE_HKD);
                            break;
                        default:
                            price = SYMBOL.USD + jObj.getString(TAG.PRICE_USD);
                            break;
                    }

                    percentChange = jObj.getString(TAG.PERCENT_CHANGE_24H) + "%";

                    cryptoAdapter = new CryptoAdapter(rank, null, name + "\n" + symbol, price, percentChange);
                    cryptoAdapterList.add(j, cryptoAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                notified = true;
                this.notify();
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

                            Log.d(_TAG, pinnedCoin);

                            if (pinnedCoins.get(i).contains("DubaiCoin")) {
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
                            } else if (pinnedCoins.get(i).contains("DAO.Casino")) {
                                pinnedCoin = "dao-casino";
                            } else if (pinnedCoins.get(i).contains("HEAT")) {
                                pinnedCoin = "heat-ledger";
                            } else if (pinnedCoins.get(i).contains("AdEx")) {
                                pinnedCoin = "adx-net";
                            }
                            else if (pinnedCoins.get(i).contains(" ")) {
                                Log.d(_TAG, pinnedCoins.get(i));
                                pinnedCoin = pinnedCoins.get(i).replace(" ", "-");
                            }
                            else {
                                pinnedCoin = pinnedCoins.get(i);
                            }

                            Log.d(_TAG, i + " : " + pinnedCoin);

                            URL url = new URL(apiAddress + pinnedCoin + "/?convert=" + defaultCurrency);
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

                            onProgressUpdate(params);
                            synchronized (this) {
                                while (!notified) {
                                    try {
                                        this.wait();
                                    } catch (InterruptedException e) {}
                                }
                            }
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
            adapter.notifyDataSetChanged();

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

            if (!MainActivity.priceRetrieved) {
                new JSONPrice().execute();
                MainActivity.priceRetrieved = true;
            }
        }
    }

    private class JSONPrice extends AsyncTask<String, String, String> {

        private final String urlAddress = "http://api.fixer.io/latest?base=USD";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader;

            try {
                URL url = new URL(urlAddress);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
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

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONObject rates = jObj.getJSONObject("rates");

                    MainActivity.convertEur = rates.getString(TAG.EUR);
                    MainActivity.convertGbp = rates.getString(TAG.GBP);
                    MainActivity.convertCad = rates.getString(TAG.CAD);
                    MainActivity.convertJpy = rates.getString(TAG.JPY);
                    MainActivity.convertAud = rates.getString(TAG.AUD);
                    MainActivity.convertChf = rates.getString(TAG.CHF);
                    MainActivity.convertInr = rates.getString(TAG.INR);
                    MainActivity.convertBrl = rates.getString(TAG.BRL);
                    MainActivity.convertPln = rates.getString(TAG.PLN);
                    MainActivity.convertCny = rates.getString(TAG.CNY);
                    MainActivity.convertSek = rates.getString(TAG.SEK);
                    MainActivity.convertNzd = rates.getString(TAG.NZD);
                    MainActivity.convertMxn = rates.getString(TAG.MXN);
                    MainActivity.convertSgd = rates.getString(TAG.SGD);
                    MainActivity.convertHkd = rates.getString(TAG.HKD);

                    Log.d(_TAG, "Object: " + jObj.toString() + "\nRates: " + rates.toString() + "\nEur: " + MainActivity.convertEur + "\nGbp: " + MainActivity.convertGbp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void storePinnedCoinsPref() {
        pinnedCoinsStr = sharedPreferences.getString("pinned_coins", "");
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
    }

    private boolean orderManuallyChanged() {
        return sharedPreferences.getBoolean("order_manually_changed", false);
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

    private boolean keepScreenTurnedOn() {
        return sharedPreferences.getBoolean("always_on_screen", true);
    }

    //private float convertUsdTo(String usdValue, String exchangeRate) {
    //    return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    //}

    //private float convertUsdTo(float usdValue, float exchangeRate) {
    //    return usdValue * exchangeRate;
    //}

    //private float convertUsdTo(String usdValue, float exchangeRate) {
    //    return Float.valueOf(usdValue) * exchangeRate;
    //}

    //private float convertUsdTo(float usdValue, String exchangeRate) {
    //    return usdValue * Float.valueOf(exchangeRate);
    //}
}
