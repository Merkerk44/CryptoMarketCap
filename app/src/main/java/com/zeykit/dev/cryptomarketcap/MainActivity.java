package com.zeykit.dev.cryptomarketcap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    private final String _TAG = "CryptoMarketCap";

    static String currentView = "";           // Used to get if user come from MainActivity or PinnedCoinsActivity
    static String mRunningActivity;           // Used to get current running activity

    private boolean isRunning = false;        // Used to check if app is running (to prevent DialogProgress error)
    private boolean canRefresh = false;       // Used with autoRefresh function
    boolean canShowChangeLog;                 // Check if change-log have already been displayed
    boolean isFirstLaunch = true;             // If is first launch, app will retrieve data
    private int refreshCount = 0;
    static boolean justClosedDialog = false;  // Check if user has closed MoreAboutCryptoDialog. If true, app don't retrieve data
    static boolean needToBeRefreshed = false; // Used to refresh when user has left the settings panel
    static boolean priceRetrieved = false;    // Check if prices from Api Fixer are retrieved

    LinearLayout activityMain;
    private Toolbar mToolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences = null;
    SpotsDialog progressDialog;

    private List<CryptoAdapter> cryptoAdapterList;
    private CryptoRvAdapter adapter;

    static String convertEur = "0.86";
    static String convertGbp = "0.78";
    static String convertCad = "1.2481";
    static String convertJpy = "108.62";
    static String convertAud = "1.2602";
    static String convertChf = "0.95854";
    static String convertInr = "63.784";
    static String convertBrl = "3.0892";
    static String convertPln = "3.5269";
    static String convertCny = "6.5752";
    static String convertSek = "7.94";
    static String convertNzd = "1.3556";
    static String convertMxn = "17.752";
    static String convertSgd = "1.3432";
    static String convertHkd = "7.8011";

    private interface TAG {
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
        String TOTAL_MARKET_CAP_USD = "total_market_cap_usd";
        String TOTAL_MARKET_CAP_EUR = "total_market_cap_eur";
        String TOTAL_MARKET_CAP_GBP = "total_market_cap_gbp";
        String TOTAL_MARKET_CAP_BTC = "total_market_cap_btc";
        String TOTAL_MARKET_CAP_CAD = "total_market_cap_cad";
        String TOTAL_MARKET_CAP_JPY = "total_market_cap_jpy";
        String TOTAL_MARKET_CAP_AUD = "total_market_cap_aud";
        String TOTAL_MARKET_CAP_CHF = "total_market_cap_chf";
        String TOTAL_MARKET_CAP_INR = "total_market_cap_inr";
        String TOTAL_MARKET_CAP_BRL = "total_market_cap_brl";
        String TOTAL_MARKET_CAP_PLN = "total_market_cap_pln";
        String TOTAL_MARKET_CAP_CNY = "total_market_cap_cny";
        String TOTAL_MARKET_CAP_SEK = "total_market_cap_sek";
        String TOTAL_MARKET_CAP_NZD = "total_market_cap_nzd";
        String TOTAL_MARKET_CAP_MXN = "total_market_cap_mxn";
        String TOTAL_MARKET_CAP_SGD = "total_market_cap_sgd";
        String TOTAL_MARKET_CAP_HKD = "total_market_cap_hkd";
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

    private interface PERMISSIONS {
        int REQUEST_NETWORK_STATE = 0x1;
        int REQUEST_INTERNET = 0x2;
    }

    static String btcPrice;

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

    private void rateMyApp() {
        RateThisApp.onCreate(activityMain.getContext());
        RateThisApp.Config config = new RateThisApp.Config(7, 10);
        config.setTitle(R.string.rta_title);
        config.setMessage(R.string.rta_message);
        config.setYesButtonText(R.string.rta_yes);
        config.setNoButtonText(R.string.rta_no);
        config.setCancelButtonText(R.string.rta_cancel);
        config.setCancelMode(RateThisApp.Config.CANCEL_MODE_BACK_KEY);
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(activityMain.getContext(), R.style.AlertDialog);
    }

    /**
     * Elements initialization
     */
    private void init() {
        activityMain = findViewById(R.id.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressDialog = new SpotsDialog(activityMain.getContext(), R.style.CustomProgressDialog);
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
        canShowChangeLog = sharedPreferences.getBoolean("can_display_change_log_5", true);

        if (canShowChangeLog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setMessage(getString(R.string.change_log_content))
                    .setTitle(getString(R.string.change_log_title))
                    .setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("can_display_change_log_5", false);
                            editor.apply();
                        }
                    })
                    .setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if (keepScreenTurnedOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

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
                        new JSONGlobal().execute();
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
            new JSONGlobal().execute();
        }
    }

    private void storeData() {
        Type listOfObjects = new TypeToken<List<CryptoAdapter>>(){}.getType();
        Gson gson = new Gson();
        String strObj = gson.toJson(cryptoAdapterList, listOfObjects);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("stored_rv_data");
        editor.apply();
        editor.putString("stored_rv_data", strObj);
        editor.apply();

        Log.d(_TAG, sharedPreferences.getString("stored_rv_data", strObj));
    }

    @Override
    public void onPause() {
        super.onPause();
        storeData();
        isRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();

        // Setting up the searchView style
        searchView.setQueryHint(fromHtml("<font color = #373839>" + getResources().getString(R.string.search_hint) + "</font>"));
        SearchView.SearchAutoComplete textArea = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textArea.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
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

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pinned_coins:
                Intent pinnedIntent = new Intent(getApplicationContext(), PinnedCoinsActivity.class);
                startActivity(pinnedIntent);
                break;
            case R.id.action_about:
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

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String append = line + "\n";
                        buffer.append(append);
                    }

                    connection.disconnect();
                    reader.close();
                    stream.close();

                    return buffer.toString().trim();

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (haveNetworkConnectionV2(getApplicationContext()) && isRunning) {
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        String _marketCap;
                        char c0, c1, c2, c3, c4;

                        String currency = getDefaultCurrency();
                        switch (currency) {
                            case "USD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_USD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.USD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.USD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "EUR":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_EUR);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.EUR + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.EUR + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "GBP":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_GBP);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.GBP + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.GBP + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "BTC":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_BTC);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.BTC + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.BTC + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "CAD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_CAD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CAD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CAD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "JPY":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_JPY);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);
                                c4 = _marketCap.charAt(5);

                                marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.JPY + String.valueOf(c0) +
                                        String.valueOf(c1) +
                                        String.valueOf(c2) +
                                        String.valueOf(c3) +
                                        String.valueOf(c4) +
                                        " " + getString(R.string.billion);
                                break;
                            case "AUD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_AUD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.AUD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.AUD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "CHF":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_CHF);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CHF + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CHF + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "INR":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_INR);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);
                                c4 = _marketCap.charAt(5);

                                marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.INR + String.valueOf(c0) +
                                        String.valueOf(c1) +
                                        String.valueOf(c2) +
                                        String.valueOf(c3) +
                                        String.valueOf(c4) +
                                        " " + getString(R.string.billion);
                                break;
                            case "BRL":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_BRL);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.BRL + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.BRL + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "PLN":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_PLN);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.PLN + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.PLN + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "CNY":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_CNY);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CNY + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.CNY + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "SEK":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_SEK);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.SEK + String.valueOf(c0) +
                                        String.valueOf(c1) +
                                        String.valueOf(c2) +
                                        String.valueOf(c3) +
                                        " " + getString(R.string.billion);
                                break;
                            case "NZD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_NZD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.NZD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.NZD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "MXN":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_MXN);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.MXN + String.valueOf(c0) +
                                        String.valueOf(c1) +
                                        String.valueOf(c2) +
                                        String.valueOf(c3) +
                                        " " + getString(R.string.billion);
                                break;
                            case "SGD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_SGD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.SGD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.SGD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                            case "HKD":
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_HKD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.HKD + String.valueOf(c0) +
                                        String.valueOf(c1) +
                                        String.valueOf(c2) +
                                        String.valueOf(c3) +
                                        " " + getString(R.string.billion);
                                break;
                            default:
                                _marketCap = jObj.getString(TAG.TOTAL_MARKET_CAP_USD);
                                c0 = _marketCap.charAt(0);
                                c1 = _marketCap.charAt(2);
                                c2 = _marketCap.charAt(3);
                                c3 = _marketCap.charAt(4);

                                if (_marketCap.contains("E11")) {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.USD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            String.valueOf(c2) +
                                            "." + String.valueOf(c3) +
                                            " " + getString(R.string.billion);
                                } else {
                                    marketCap = getString(R.string.total_market_cap) + " : " + SYMBOL.USD + String.valueOf(c0) +
                                            String.valueOf(c1) +
                                            "." +
                                            String.valueOf(c2) +
                                            " " + getString(R.string.billion);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Snackbar.make(activityMain, marketCap, Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

                new JSONParse().execute();
            }
        }
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        boolean connectionEnabled = haveNetworkConnectionV2(getApplicationContext());

        private String defaultCurrency = getDefaultCurrency();
        private final String urlAddress = "https://api.coinmarketcap.com/v1/ticker/?convert=" + defaultCurrency + "&limit=" + arraySizeToDisplay();

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
                if (!progressDialog.isShowing())
                    progressDialog.show();

                if (!cryptoAdapterList.isEmpty()) {
                    cryptoAdapterList.clear();
                    adapter.notifyDataSetChanged();
                }
                refreshCount++;
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

            if (connectionEnabled) {
                if (isRunning) {
                    try {
                        URL url = new URL(urlAddress);
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

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
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
                if (result != null) {
                    try {
                        jArray = new JSONArray(result);

                        for (int i = 0; i < jArray.length(); i++) {
                            jObj = jArray.getJSONObject(i);

                            _rank = (i + 1);
                            rank = String.valueOf(_rank);
                            name = jObj.getString(TAG.NAME);
                            symbol = "(" + jObj.getString(TAG.SYMBOL) + ")";

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

                            cryptoAdapterList.add(i, cryptoAdapter);

                            adapter.notifyDataSetChanged();

                            if (i == 0) {
                                btcPrice = jObj.getString(TAG.PRICE_USD);
                                Log.d(_TAG, btcPrice);
                            }
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.something_goes_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);

            if (refreshCount > 1) {
                rateMyApp();
            }

            if (!priceRetrieved) {
                new JSONPrice().execute();
                priceRetrieved = true;
            }

            isFirstLaunch = false;
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

                    convertEur = rates.getString(TAG.EUR);
                    convertGbp = rates.getString(TAG.GBP);
                    convertCad = rates.getString(TAG.CAD);
                    convertJpy = rates.getString(TAG.JPY);
                    convertAud = rates.getString(TAG.AUD);
                    convertChf = rates.getString(TAG.CHF);
                    convertInr = rates.getString(TAG.INR);
                    convertBrl = rates.getString(TAG.BRL);
                    convertPln = rates.getString(TAG.PLN);
                    convertCny = rates.getString(TAG.CNY);
                    convertSek = rates.getString(TAG.SEK);
                    convertNzd = rates.getString(TAG.NZD);
                    convertMxn = rates.getString(TAG.MXN);
                    convertSgd = rates.getString(TAG.SGD);
                    convertHkd = rates.getString(TAG.HKD);

                    Log.d(_TAG, "Object: " + jObj.toString() + "\nRates: " + rates.toString() + "\nEur: " + convertEur + "\nGbp: " + convertGbp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
        return sharedPreferences.getString("array_size", "200");
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
