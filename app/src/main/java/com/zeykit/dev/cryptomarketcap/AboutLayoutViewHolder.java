package com.zeykit.dev.cryptomarketcap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mIcon;
    private TextView mText;
    static int position;

    Context context;
    private ClipboardManager clipboardManager;
    private View rootView;

    private interface CRYPTO_ADDR {
        String BTC = "1JB1RS9UeYjUgZV2rJCuwWXK4fVKuJDnxG";
        String ETH = "0xDD353AF332EB94148949a3725871649B1a491147";
        String ETC = "0x070beae2fd4ba17e354600938d853f71efc97f88";
        String ZEC = "t1LwjXxRFf8eNLcDRZS54A5ta3Q3tLbetnA";
        String SC = "b9a7625b96f1af146b24de8fd485d161432eb47f68938fcf4f041de9f440f19efebdeea3fbc7";
    }

    public AboutLayoutViewHolder(View view) {
        super(view);

        context = view.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(R.id.aboutLayout);
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        mIcon = (ImageView) view.findViewById(R.id.aboutIcon);
        mText = (TextView) view.findViewById(R.id.aboutText);

        mText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        position = getAdapterPosition();
        switch (position) {
            case 0:
                openDeveloperProfile();
                break;
            case 1:
                openLaBaleine();
                break;
            case 2:
                copyToClipboard(CRYPTO_ADDR.BTC);
                String str0 = "BTC " + context.getString(R.string.copied_to_clipboard) + ". " + context.getString(R.string.thanks_for_support);
                showSnackBar(rootView, str0, Snackbar.LENGTH_SHORT);
                //openDonateBitcoin();
                break;
            case 3:
                copyToClipboard(CRYPTO_ADDR.ETH);
                String str1 = "ETH " + context.getString(R.string.copied_to_clipboard) + ". " + context.getString(R.string.thanks_for_support);
                showSnackBar(rootView, str1, Snackbar.LENGTH_SHORT);
                //openDonateEth();
                break;
            case 4:
                copyToClipboard(CRYPTO_ADDR.ZEC);
                String str2 = "ZEC " + context.getString(R.string.copied_to_clipboard) + ". " + context.getString(R.string.thanks_for_support);
                showSnackBar(rootView, str2, Snackbar.LENGTH_SHORT);
                break;
            case 5:
                String str3 = context.getString(R.string.share_content)
                                                    + " : https://play.google.com/store/apps/details?id=" + context.getApplicationContext().getPackageName();
                shareTheApp(str3);
                break;
            case 6:
                openAppPage();
                break;
            case 7:
                reportBug();
                break;
            default:
                break;
        }
    }

    public void bind(AboutLayoutAdapter adapter) {
        mIcon.setImageDrawable(adapter.getIcon());
        mText.setText(adapter.getText());
    }

    /**
     * Open Zeykit-Dev Google Play developer page
     */
    private void openDeveloperProfile() {
        Uri uri = Uri.parse("market://dev?id=6806198870613114063");
        Intent openDeveloperProfile = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openDeveloperProfile);
        } catch (ActivityNotFoundException e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/dev?id=6806198870613114063")));
            } catch (ActivityNotFoundException f) {
                Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openDonateBitcoin() {
        Uri uri = Uri.parse("https://blockchain.info/address/1JB1RS9UeYjUgZV2rJCuwWXK4fVKuJDnxG");
        Intent openDonateBitcoin = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openDonateBitcoin);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDonateEth() {
        Uri uri = Uri.parse("https://www.etherchain.org/account/0xDD353AF332EB94148949a3725871649B1a491147#txsent");
        Intent openDonateEth = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openDonateEth);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open LaBaleine's website
     */
    private void openLaBaleine() {
        Uri uri = Uri.parse("http://www.labaleine.gg/");
        Intent openLaBaleine = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openLaBaleine);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open the app page from Google Play
     */
    private void openAppPage() {
        Uri uri = Uri.parse("market://details?id=" + context.getApplicationContext().getPackageName());
        Intent openAppPage = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openAppPage);
        } catch (ActivityNotFoundException e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + context.getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException f) {
                Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
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

    private void reportBug() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: zeykit.dev@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_a_bug)
                                                    + " : " + context.getString(R.string.app_name)
                                                    + " v" + context.getString(R.string.app_version));
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.report_a_bug)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.mailing_app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareTheApp(String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);

        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.sharing_app_not_found), Toast.LENGTH_SHORT).show();
        }
    }
}
