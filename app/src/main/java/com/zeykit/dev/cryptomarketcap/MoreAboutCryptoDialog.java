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
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class MoreAboutCryptoDialog extends Activity {

    private final String mTAG = MoreAboutCryptoDialog.class.getName();

    static LinearLayout moreAboutCryptoActivity;
    RecyclerView recyclerView;
    GraphView graphView;
    private List<AboutCryptoAdapter> cryptoAdapterList;
    private AboutCryptoRvAdapter adapter;

    private SpotsDialog progressDialog;

    static String selectedCrypto = "";
    private String[] cryptoName = selectedCrypto.split("\n");
    private String[] cryptoSymbol1 = cryptoName[1].split(Pattern.quote("("));
    private String[] cryptoSymbol2 = cryptoSymbol1[1].split(Pattern.quote(")"));

    String convertEur = "0.86";
    String convertGbp = "0.78";

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
        String USD = "USD";
        String EUR = "EUR";
        String GBP = "GBP";
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

        if (haveNetworkConnectionV2(getApplicationContext())) {
            new JSONParse().execute();
        } else {
            finish();
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

        graphView.getGridLabelRenderer().setTextSize(35);

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        graphView.setTitleColor(getResources().getColor(R.color.colorTitle));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        cryptoAdapterList = new ArrayList<>();

        adapter = new AboutCryptoRvAdapter(cryptoAdapterList);
        recyclerView.setAdapter(adapter);
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        boolean connectionEnabled = haveNetworkConnectionV2(getApplicationContext());

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

                            DecimalFormat formatter = new DecimalFormat("#,###");

                            switch (defaultCurrency) {
                                case "USD":
                                    price = getString(R.string.price) + " : $" + jObj.getString(TAG.PRICE_USD);
                                    Double dUsdMarketCap = Double.parseDouble(jObj.getString(TAG.MARKET_CAP_USD));
                                    marketCap = getString(R.string.market_cap) + " : $" + formatter.format(dUsdMarketCap);
                                    Double dUsdVolume = Double.parseDouble(jObj.getString(TAG.VOLUME_USD));
                                    volume = getString(R.string.volume) + " : $" + formatter.format(dUsdVolume);
                                    break;
                                case "EUR":
                                    price = getString(R.string.price) + " : €" + jObj.getString(TAG.PRICE_EUR);
                                    Double dEurMarketCap = Double.parseDouble(jObj.getString(TAG.MARKET_CAP_EUR));
                                    marketCap = getString(R.string.market_cap) + " : €" + formatter.format(dEurMarketCap);
                                    Double dEurVolume = Double.parseDouble(jObj.getString(TAG.VOLUME_EUR));
                                    volume = getString(R.string.volume) + " : €" + formatter.format(dEurVolume);
                                    break;
                                case "GBP":
                                    price = getString(R.string.price) + " : £" + jObj.getString(TAG.PRICE_GBP);
                                    Double dGbpMarketCap = Double.parseDouble(jObj.getString(TAG.MARKET_CAP_GBP));
                                    marketCap = getString(R.string.market_cap) + " : £" + formatter.format(dGbpMarketCap);
                                    Double dGbpVolume = Double.parseDouble(jObj.getString(TAG.VOLUME_GBP));
                                    volume = getString(R.string.volume) + " : £" + formatter.format(dGbpVolume);
                                    break;
                                case "BTC":
                                    price = getString(R.string.price) + " : ฿" + jObj.getString(TAG.PRICE_BTC);
                                    Double dBtcMarketCap = Double.parseDouble(jObj.getString(TAG.MARKET_CAP_BTC));
                                    marketCap = getString(R.string.market_cap) + " : ฿" + formatter.format(dBtcMarketCap);
                                    Double dBtcVolume = Double.parseDouble(jObj.getString(TAG.VOLUME_BTC));
                                    volume = getString(R.string.volume) + " : ฿" + formatter.format(dBtcVolume);
                                    break;
                            }

                            Double dCirculatingSupply = Double.parseDouble(jObj.getString(TAG.CIRCULATING_SUPPLY));
                            circulatingSupply = getString(R.string.circulating_supply) + " : " + formatter.format(dCirculatingSupply);
                            percentChange1h = getString(R.string.percent_change_1h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_1H) + "%";
                            percentChange24h = getString(R.string.percent_change_24h) + " : " + jObj.getString(TAG.PERCENT_CHANGE_24H) + "%";
                            percentChange7d = getString(R.string.percent_change_7d) + " : " + jObj.getString(TAG.PERCENT_CHANGE_7D) + "%";

                            cryptoAdapter = new AboutCryptoAdapter(rank, null, name + " " + symbol, price, marketCap, circulatingSupply,
                                    volume, percentChange1h, percentChange24h, percentChange7d);

                            cryptoAdapterList.add(i, cryptoAdapter);

                            adapter.notifyItemInserted(i);
                            adapter.notifyDataSetChanged();

                            new JSONPrice().execute();
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

    private class JSONGraph extends AsyncTask<String, String, String> {

        private final String chartAddress = "http://www.coincap.io/" + getChartHistoryLength(getChartHistoryPreference()) + getSelectedCrypto(selectedCrypto);
        private ArrayList<String> array = new ArrayList<>();
        StringBuffer buffer;

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
                    String line = "";

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

                    DataPoint[] values = new DataPoint[priceArray.length()];

                    for (int i = 0; i < priceArray.length(); i++) {
                        String value = priceArray.getString(i);
                        String[] split1 = value.split(",");
                        String[] split2 = split1[1].split("]");

                        array.add(split2[0]);

                        DataPoint v = new DataPoint(i, Double.parseDouble(split2[0]));
                        values[i] = v;
                    }

                    float minValue = getMinValue(array);
                    float maxValue = getMaxValue(array);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);

                    series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPoint) {

                            String data = dataPoint.toString();
                            String[] splitData1 = data.split(Pattern.quote("/"));
                            String[] splitData2 = splitData1[1].split(Pattern.quote("]"));
                            String priceAtThisPoint = "";

                            switch (getDefaultCurrency()) {
                                case "USD":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " $" + splitData2[0];
                                    break;
                                case "EUR":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " $" + splitData2[0] + " (€" + String.valueOf(convertUsdToEur(splitData2[0], convertEur)).substring(0, 7) + ")";
                                    break;
                                case "GBP":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " $" + splitData2[0] + " (£" + String.valueOf(convertUsdToGbp(splitData2[0], convertGbp)).substring(0, 7) + ")";
                                    break;
                                case "BTC":
                                    priceAtThisPoint = getString(R.string.price_at_point) + " $" + splitData2[0];
                                    break;
                                default:
                                    priceAtThisPoint = getString(R.string.price_at_point) + " $" + splitData2[0];
                                    break;
                            }

                            Toast.makeText(getApplicationContext(), priceAtThisPoint, Toast.LENGTH_SHORT).show();
                        }
                    });

                    series.setDrawBackground(true);
                    series.setColor(Color.parseColor("#007acc"));
                    series.setBackgroundColor(Color.argb(30, 0, 138, 230));

                    graphView.getViewport().setYAxisBoundsManual(true);
                    graphView.getViewport().setMinY(minValue);
                    graphView.getViewport().setMaxY(maxValue);

                    graphView.getViewport().setXAxisBoundsManual(true);
                    graphView.getViewport().setMinX(0);
                    graphView.getViewport().setMaxX(array.size());

                    graphView.getViewport().setScalable(true);
                    graphView.getViewport().setScrollable(true);
                    graphView.getViewport().setScrollableY(true);
                    graphView.getViewport().setScalableY(true);

                    graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                    graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);

                    String legend = "";

                    switch (getDefaultCurrency()) {
                        case "USD":
                            legend = getString(R.string.min_price) + " $" + String.valueOf(minValue).substring(0, 6) + ", " + getString(R.string.max_price) + " $" + String.valueOf(maxValue).substring(0, 6) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "EUR":
                            legend = getString(R.string.min_price) + " €" + String.valueOf(convertUsdToEur(minValue, convertEur)).substring(0, 7) + ", " + getString(R.string.max_price) + " €" + String.valueOf(convertUsdToEur(maxValue, convertEur)).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "GBP":
                            legend = getString(R.string.min_price) + " £" + String.valueOf(convertUsdToGbp(minValue, convertGbp)).substring(0, 7) + ", " + getString(R.string.max_price) + " £" + String.valueOf(convertUsdToGbp(maxValue, convertGbp)).substring(0, 7) + " (" + getChartHistoryPreference() + ")";
                            break;
                        case "BTC":
                            legend = getString(R.string.min_price) + " $" + String.valueOf(minValue).substring(0, 6) + ", " + getString(R.string.max_price) + " $" + String.valueOf(maxValue).substring(0, 6) + " (" + getChartHistoryPreference() + ")";
                            break;
                        default:
                            legend = getString(R.string.min_price) + " $" + String.valueOf(minValue).substring(0, 6) + ", " + getString(R.string.max_price) + " $" + String.valueOf(maxValue).substring(0, 6) + " (" + getChartHistoryPreference() + ")";
                            break;
                    }

                    graphView.setTitle(legend);

                    graphView.addSeries(series);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LineGraphSeries<DataPoint> nullSeries = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0)
                });

                graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
                graphView.addSeries(nullSeries);
                Toast.makeText(getApplicationContext(), getString(R.string.no_chart), Toast.LENGTH_LONG).show();
            }

            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    private class JSONPrice extends AsyncTask<String, String, String> {

        private final String urlAddress = "http://api.fixer.io/latest?base=USD";
        StringBuffer buffer;

        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader;

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

                    Log.d(mTAG, "Object: " + jObj.toString() + "\nRates: " + rates.toString() + "\nEur: " + convertEur + "\nGbp: " + convertGbp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            new JSONGraph().execute();
        }
    }

    private float convertUsdToEur(float usdValue) {
        return usdValue * 0.85f;
    }

    private float convertUsdToEur(String usdValue) {
        return Float.parseFloat(usdValue) * 0.85f;
    }

    private float convertUsdToEur(float usdValue, float exchangeRate) {
        return usdValue * exchangeRate;
    }

    private float convertUsdToEur(String usdValue, float exchangeRate) {
        return Float.parseFloat(usdValue) * exchangeRate;
    }

    private float convertUsdToEur(float usdValue, String exchangeRate) {
        return usdValue * Float.valueOf(exchangeRate);
    }

    private float convertUsdToEur(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    }

    private float convertUsdToGbp(float usdValue) {
        return usdValue * 0.775f;
    }

    private float convertUsdToGbp(String usdValue) {
        return Float.parseFloat(usdValue) * 0.775f;
    }

    private float convertUsdToGbp(float usdValue, float exchangeRate) {
        return usdValue * exchangeRate;
    }

    private float convertUsdToGbp(float usdValue, String exchangeRate) {
        return usdValue * Float.valueOf(exchangeRate);
    }

    private float convertUsdToGbp(String usdValue, float exchangeRate) {
        return Float.valueOf(usdValue) * exchangeRate;
    }

    private float convertUsdToGbp(String usdValue, String exchangeRate) {
        return Float.valueOf(usdValue) * Float.valueOf(exchangeRate);
    }

    private String getDefaultCurrency() {
        return sharedPreferences.getString("currency_list_preference", "USD");
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
            case "30 days":
                return "history/30day/";
            case "30 jours":
                return "history/30day/";
            case "180 days":
                return "history/180day/";
            case "180 jours":
                return "history/180day/";
            case "365 days":
                return "history/365day/";
            case "365 jours":
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
}
