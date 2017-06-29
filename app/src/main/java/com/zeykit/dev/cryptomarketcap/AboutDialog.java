package com.zeykit.dev.cryptomarketcap;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutDialog extends DialogFragment {

    private TextView developerTextView;
    private TextView joinCommunityTextView;
    private TextView btcTextView;
    private TextView ethTextView;
    private TextView etcTextView;
    private TextView zecTextView;
    private TextView scTextView;
    private TextView checkForUpdateTextView;
    private Button gotItButton;

    private ClipboardManager clipboardManager;

    private View rootView;

    private interface CRYPTO_ADDR {
        String BTC = "1JB1RS9UeYjUgZV2rJCuwWXK4fVKuJDnxG";
        String ETH = "0xe4c26694bcb02b498e67e8059a174e3998d1cd2c";
        String ETC = "0x070beae2fd4ba17e354600938d853f71efc97f88";
        String ZEC = "t1LwjXxRFf8eNLcDRZS54A5ta3Q3tLbetnA";
        String SC = "b9a7625b96f1af146b24de8fd485d161432eb47f68938fcf4f041de9f440f19efebdeea3fbc7";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.about_layout, null);
        builder.setView(dialogView);

        init(dialogView);

        developerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeveloperProfile();
            }
        });

        joinCommunityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLaBaleine();
            }
        });

        btcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(CRYPTO_ADDR.BTC);
                String str = "BTC " + getString(R.string.copied_to_clipboard);
                showSnackBar(rootView, str, Snackbar.LENGTH_SHORT);
                openDonateBitcoin();
            }
        });

        ethTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(CRYPTO_ADDR.ETH);
                String str = "ETH " + getString(R.string.copied_to_clipboard);
                showSnackBar(rootView, str, Snackbar.LENGTH_SHORT);
                openDonateEth();
            }
        });

        checkForUpdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppPage();
            }
        });


        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Elements initialization
     * @param view View
     */
    private void init(View view) {
        developerTextView = (TextView) view.findViewById(R.id.developerTextView);
        joinCommunityTextView = (TextView) view.findViewById(R.id.joinCommunityTextView);
        btcTextView = (TextView) view.findViewById(R.id.btcTextView);
        ethTextView = (TextView) view.findViewById(R.id.ethTextView);
        checkForUpdateTextView = (TextView) view.findViewById(R.id.checkForUpdateTextView);
        gotItButton = (Button) view.findViewById(R.id.aboutGotItButton);

        rootView = getActivity().getWindow().getDecorView().findViewById(R.id.activity_main);

        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        String str = getString(R.string.current_version) +
                ": " + getString(R.string.app_version) +
                " (" + getString(R.string.check_for_updates) + ")";
        checkForUpdateTextView.setText(str);
    }

    /**
     * Open Zeykit-Dev Google Play developer page
     */
    private void openDeveloperProfile() {
        Uri uri = Uri.parse("market://dev?id=6806198870613114063");
        Intent openDeveloperProfile = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(openDeveloperProfile);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/dev?id=6806198870613114063")));
            } catch (ActivityNotFoundException f) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openDonateBitcoin() {
        Uri uri = Uri.parse("https://blockchain.info/address/1JB1RS9UeYjUgZV2rJCuwWXK4fVKuJDnxG");
        Intent openDonateBitcoin = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(openDonateBitcoin);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDonateEth() {
        Uri uri = Uri.parse("https://www.etherchain.org/account/0xA7753FbE95d1d99e2E112039B95896CCC3A7613F#txsent");
        Intent openDonateEth = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(openDonateEth);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open LaBaleine's website
     */
    private void openLaBaleine() {
        Uri uri = Uri.parse("http://www.labaleine.gg/");
        Intent openLaBaleine = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(openLaBaleine);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open the app page from Google Play
     */
    private void openAppPage() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getApplicationContext().getPackageName());
        Intent openAppPage = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(openAppPage);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException f) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display snackBar
     * @param v view
     * @param msg message
     * @param duration duration
     */
    private void showSnackBar(View v, String msg, int duration) {
        Snackbar snackbar = Snackbar.make(v, msg, duration);
        snackbar.show();
    }

    /**
     * Add data to clipboard
     * @param str data
     */
    private void copyToClipboard(String str) {
        ClipData dt = ClipData.newPlainText(null,
                str);
        clipboardManager.setPrimaryClip(dt);
    }
}
