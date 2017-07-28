package com.zeykit.dev.cryptomarketcap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kobakei.ratethisapp.RateThisApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // TODO Open crypto's website

    private final String _TAG = "CryptoMarketCap";

    static String currentView = "";           // Used to get if user come from MainActivity or PinnedCoinsActivity
    static String mRunningActivity;           // Used to get current running activity

    private boolean isRunning = false;        // Used to check if app is running (to prevent DialogProgress error)
    private boolean canRefresh = false;       // Used with autoRefresh function
    private boolean isFirstLaunch = true;     // If is first launch, app will retrieve data
    static boolean justClosedDialog = false;  // Check if user has closed MoreAboutCryptoDialog. If true, app don't retrieve data
    static boolean needToBeRefreshed = false; // Used to refresh when user has left the settings panel

    static LinearLayout activityMain;
    private Toolbar mToolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences = null;

    private List<CryptoAdapter> cryptoAdapterList;
    private CryptoRvAdapter adapter;

    private interface TAG {
        String NAME = "name";
        String SYMBOL = "symbol";
        String PRICE_USD = "price_usd";
        String PRICE_EUR = "price_eur";
        String PRICE_GBP = "price_gbp";
        String PRICE_BTC = "price_btc";
        String PERCENT_CHANGE_24H = "percent_change_24h";
        String TOTAL_MARKET_CAP_USD = "total_market_cap_usd";
        String TOTAL_MARKET_CAP_EUR = "total_market_cap_eur";
        String TOTAL_MARKET_CAP_GBP = "total_market_cap_gbp";
        String TOTAL_MARKET_CAP_BTC = "total_market_cap_btc";
    }

    private interface PERMISSIONS {
        int REQUEST_NETWORK_STATE = 0x1;
        int REQUEST_INTERNET = 0x2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setupToolbar();
        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cryptoAdapterList.clear();
                new JSONParse().execute();
            }
        });
    }

    /**
     * Elements initialization
     */
    private void init() {
        activityMain = (LinearLayout) findViewById(R.id.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    /**
     * Settings up the toolBar
     */
    private void setupToolbar() {
        ViewCompat.setElevation(mToolbar, 10);
        setSupportActionBar(mToolbar);
    }

    /**
     * Settings up the recyclerView
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cryptoAdapterList = new ArrayList<>();

        adapter = new CryptoRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        isRunning = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean connectionEnabled = haveNetworkConnectionV2(getApplicationContext());

        mRunningActivity = this.getClass().getSimpleName();

        // Check for permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                showNetworkStateDialog();
                Log.d(_TAG, "User have already denied the network state permission");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.ACCESS_NETWORK_STATE },
                        PERMISSIONS.REQUEST_NETWORK_STATE);
                Log.d(_TAG, "Requesting for network state permission");
            }
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.INTERNET)) {
                    showInternetDialog();
                    Log.d(_TAG, "User have already denied the Internet permission");
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[] { Manifest.permission.INTERNET },
                            PERMISSIONS.REQUEST_INTERNET);
                    Log.d(_TAG, "Requesting for Internet permission");
                }
            } else {
                if (connectionEnabled) {
                    if (isFirstLaunch && !justClosedDialog) {
                        new JSONParse().execute();
                        new JSONGlobal().execute();
                        isFirstLaunch = false;
                    }
                } else {
                    showConnectionDialog();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS.REQUEST_NETWORK_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(_TAG, "Network state permission granted");
                } else {
                    showNetworkStateDialog();
                    Log.d(_TAG, "Network state permission denied");
                }
            }
            case PERMISSIONS.REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(_TAG, "Internet permission granted");
                } else {
                    showInternetDialog();
                    Log.d(_TAG, "Internet permission denied");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(_TAG, autoRefreshEnabled() + " > " + autoRefreshDelay() + "s");

        // Check for user preferences
        if (autoRefreshEnabled()) {
            handler.removeCallbacks(autoRefresh);
            handler.post(autoRefresh);
        } else {
            handler.removeCallbacks(autoRefresh);
        }

        if (justClosedDialog) {
            Gson gson = new Gson();
            Type listOfObjects = new TypeToken<List<CryptoAdapter>>() {
            }.getType();
            String json = sharedPreferences.getString("stored_rv_data", "");
            List<CryptoAdapter> retrieveStoredData = gson.fromJson(json, listOfObjects);

            cryptoAdapterList.clear();
            adapter.notifyDataSetChanged();
            cryptoAdapterList.addAll(retrieveStoredData);
            adapter.notifyDataSetChanged();

            isRunning = true;
            Log.d(_TAG, "ArrayList set");
        }

        if (needToBeRefreshed) {
            needToBeRefreshed = false;
            new JSONParse().execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Type listOfObjects = new TypeToken<List<CryptoAdapter>>(){}.getType();
        Gson gson = new Gson();
        String strObj = gson.toJson(cryptoAdapterList, listOfObjects);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("stored_rv_data");
        editor.apply();
        editor.putString("stored_rv_data", strObj);
        editor.apply();

        Log.d(_TAG, sharedPreferences.getString("stored_rv_data", strObj));

        isRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        // Setting up the searchView style
        searchView.setQueryHint(Html.fromHtml("<font color = #373839>" + getResources().getString(R.string.search_hint) + "</font>"));
        SearchView.SearchAutoComplete textArea = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textArea.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        adapter.setFilter(cryptoAdapterList);
                        return true;
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pinned_coins:
                Intent pinnedIntent = new Intent(getApplicationContext(), PinnedCoinsActivity.class);
                startActivity(pinnedIntent);
                break;
            case R.id.action_about:
                /*AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), null);*/
                Intent aboutIntent = new Intent(getApplicationContext(), AboutLayoutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Auto refresh method
     */
    Handler handler = new Handler();
    Runnable autoRefresh = new Runnable() {
        @Override
        public void run() {
            String autoRefreshDelay = autoRefreshDelay() + "000";
            int autoRefreshDelayToInt = Integer.parseInt(autoRefreshDelay);
            handler.postDelayed(autoRefresh, autoRefreshDelayToInt);

            if (isRunning && !justClosedDialog) {
                if (autoRefreshEnabled() && canRefresh) {
                    Log.d(_TAG, "Refreshing...");
                    new JSONParse().execute();
                } else {
                    canRefresh = true;
                }
            }
        }
    };

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<CryptoAdapter> filteredCryptoList = filter(cryptoAdapterList, newText);
        adapter.setFilter(filteredCryptoList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Filtering recyclerView data
     * with the user's search
     * @param cryptoAdapterList list to filter
     * @param query user's search
     * @return filtered list
     */
    private List<CryptoAdapter> filter(List<CryptoAdapter> cryptoAdapterList, String query) {
        query = query.toLowerCase();
        final List<CryptoAdapter> filteredCryptoList = new ArrayList<>();
        for (CryptoAdapter crypto : cryptoAdapterList) {
            final String text = crypto.getName().toLowerCase();
            if (text.contains(query))
                filteredCryptoList.add(crypto);
        }
        return filteredCryptoList;
    }

    private class JSONGlobal extends AsyncTask<String, String, String> {
        private String marketCap = "";
        private final String urlAddress = "https://api.coinmarketcap.com/v1/global/?convert=" + getDefaultCurrency();

        HttpURLConnection connection;
        BufferedReader reader;
        StringBuffer buffer;

        @Override
        protected String doInBackground(String... params) {

            if (haveNetworkConnectionV2(getApplicationContext()) && isRunning) {
                try {
                    URL url = new URL(urlAddress);
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
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (haveNetworkConnectionV2(getApplicationContext()) && isRunning) {
                try {
                    JSONObject jObj = new JSONObject(buffer.toString().trim());
                    String _marketCap = "";
                    char c0 = '0';
                    char c1 = '1';
                    char c2 = '2';

                    String currency = getDefaultCurrency();
                    switch (currency) {
                        case "USD":
                            _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_USD);
                            c0 = _marketCap.charAt(0);
                            c1 = _marketCap.charAt(2);
                            c2 = _marketCap.charAt(3);
                            marketCap = getString(R.string.total_market_cap) + " : $" + String.valueOf(c0) +
                                    String.valueOf(c1) +
                                    "." +
                                    String.valueOf(c2) +
                                    " Md";
                            break;
                        case "EUR":
                            _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_EUR);
                            c0 = _marketCap.charAt(0);
                            c1 = _marketCap.charAt(2);
                            c2 = _marketCap.charAt(3);
                            marketCap = getString(R.string.total_market_cap) + " : €" + String.valueOf(c0) +
                                    String.valueOf(c1) +
                                    "." +
                                    String.valueOf(c2) +
                                    " Md";
                            break;
                        case "GBP":
                            _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_GBP);
                            c0 = _marketCap.charAt(0);
                            c1 = _marketCap.charAt(2);
                            c2 = _marketCap.charAt(3);
                            marketCap = getString(R.string.total_market_cap) + " : £" + String.valueOf(c0) +
                                    String.valueOf(c1) +
                                    "." +
                                    String.valueOf(c2) +
                                    " Md";
                            break;
                        case "BTC":
                            _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_BTC);
                            c0 = _marketCap.charAt(0);
                            c1 = _marketCap.charAt(2);
                            c2 = _marketCap.charAt(3);
                            marketCap = getString(R.string.total_market_cap) + " : ฿" + String.valueOf(c0) +
                                    String.valueOf(c1) +
                                    "." +
                                    String.valueOf(c2) +
                                    " M";
                            break;
                    }

                    Log.d(_TAG, _marketCap);
                    Snackbar.make(activityMain, marketCap, Snackbar.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        boolean connectionEnabled = haveNetworkConnectionV2(getApplicationContext());

        private String defaultCurrency = getDefaultCurrency();
        private final String urlAddress = "https://api.coinmarketcap.com/v1/ticker/?convert=" + defaultCurrency + "&limit=" + arraySizeToDisplay();

        //private ProgressDialog progressDialog;
        private SpotsDialog progressDialog;

        private CryptoAdapter cryptoAdapter = new CryptoAdapter();
        private JSONObject jObj;

        int _rank;
        private String rank;
        private String name;
        private String symbol;
        private String price;
        private String percentChange;

        JSONArray jArray;
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);

            if (connectionEnabled) {
                progressDialog = new SpotsDialog(activityMain.getContext(), R.style.CustomProgressDialog);

                if (isRunning)
                    progressDialog.show();

                cryptoAdapterList.clear();
                adapter.notifyDataSetChanged();
            }
        }

        /**
         * Enable connection to CoinMarketCap.com API
         * and retrieve data
         * @param params params
         * @return null
         */
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader;

            if (connectionEnabled && isRunning) {
                try {
                    URL url = new URL(urlAddress);
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
                    e.printStackTrace();
                }
            }

            return null;
        }

        /**
         * Handling retrieved data
         * and setup the recycler view
         * @param result result
         */
        @Override
        protected void onPostExecute(String result) {
            if (connectionEnabled && isRunning) {
                try {
                    jArray = new JSONArray(buffer.toString());

                    for (int i = 0; i < jArray.length(); i++) {
                        jObj = jArray.getJSONObject(i);

                        _rank = (i + 1);
                        rank = String.valueOf(_rank);
                        name = jObj.getString(TAG.NAME);
                        symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

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

                        cryptoAdapterList.add(i, cryptoAdapter);

                        adapter.notifyItemInserted(i);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            if (progressDialog != null)
                progressDialog.dismiss();

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);

            RateThisApp.onCreate(activityMain.getContext());
            RateThisApp.Config config = new RateThisApp.Config(7, 10);
            config.setTitle(R.string.rta_title);
            config.setMessage(R.string.rta_message);
            config.setYesButtonText(R.string.rta_yes);
            config.setNoButtonText(R.string.rta_no);
            config.setCancelButtonText(R.string.rta_cancel);
            RateThisApp.init(config);
            RateThisApp.showRateDialogIfNeeded(activityMain.getContext(), R.style.AlertDialog);
        }
    }

    /**
     * @return if user have network connection
     */
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

    /**
     * Show the 'network state dialog' for requesting
     * to user to enable the Network State permission
     */
    private void showNetworkStateDialog() {
        NetworkStateDialog networkStateDialog = new NetworkStateDialog();
        networkStateDialog.show(getSupportFragmentManager(), null);
    }

    /**
     * Show the 'internet dialog' for requesting to
     * user to enable the Internet permission
     */
    private void showInternetDialog() {
        InternetDialog internetDialog = new InternetDialog();
        internetDialog.show(getSupportFragmentManager(), null);
    }

    /**
     * Show the 'connection dialog' for requesting to
     * user to enable Wi-FI or data mobile connection
     */
    private void showConnectionDialog() {
        RequestConnectionDialog requestConnectionDialog = new RequestConnectionDialog();
        requestConnectionDialog.show(getSupportFragmentManager(), null);
    }

    /**
     * @return if user want to enable the auto refresh function
     */
    private boolean autoRefreshEnabled() {
        return sharedPreferences.getBoolean("auto_refresh_switch", true);
    }

    /**
     * @return refresh delay set by user
     */
    private String autoRefreshDelay() {
        return sharedPreferences.getString("auto_refresh_delay", "60");
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
    }

    private String arraySizeToDisplay() {
        return sharedPreferences.getString("array_size", "100");
    }
}
