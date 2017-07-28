package com.zeykit.dev.cryptomarketcap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AboutCryptoViewHolder extends RecyclerView.ViewHolder {

    private TextView mRankTextView;
    private ImageView mIconImageView;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mMarketCap;
    private TextView mCirculatingSupply;
    private TextView mVolume;
    private TextView mPercentChange1h;
    private TextView mPercentChange24h;
    private TextView mPercentChange7d;
    private ImageView mPinImageView;

    private TinyDB tinyDB;
    private ArrayList<String> getList;
    private String cryptoWebsite = "noUrl";

    Context context;

    public AboutCryptoViewHolder(View v) {
        super(v);

        context = v.getContext();

        mRankTextView = (TextView) v.findViewById(R.id.cryptoAboutRank);
        mIconImageView = (ImageView) v.findViewById(R.id.cryptoAboutIcon);
        mNameTextView = (TextView) v.findViewById(R.id.cryptoAboutName);
        mPriceTextView = (TextView) v.findViewById(R.id.cryptoAboutPrice);
        mMarketCap = (TextView) v.findViewById(R.id.cryptoAboutMarketCap);
        mCirculatingSupply = (TextView) v.findViewById(R.id.cryptoAboutCirculatingSupply);
        mVolume = (TextView) v.findViewById(R.id.cryptoAboutVolume);
        mPercentChange1h = (TextView) v.findViewById(R.id.cryptoAboutPercentChange1h);
        mPercentChange24h = (TextView) v.findViewById(R.id.cryptoAboutPercentChange24h);
        mPercentChange7d = (TextView) v.findViewById(R.id.cryptoAboutPercentChange7d);
        mPinImageView = (ImageView) v.findViewById(R.id.cryptoAboutPinIcon);

        tinyDB = new TinyDB(context);
        getList = tinyDB.getListString("pinned_coins");

        String selectedCrypto = MoreAboutCryptoDialog.selectedCrypto;
        String[] selectedCryptoSplit = selectedCrypto.split("\n");

        if (!getList.contains(selectedCryptoSplit[0])) {
            mPinImageView.setImageResource(R.drawable.ic_pinned);
        } else {
            mPinImageView.setImageResource(R.drawable.ic_unpin);
        }

        mPinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCrypto = MoreAboutCryptoDialog.selectedCrypto;
                String[] selectedCryptoSplit = selectedCrypto.split("\n");

                if (!getList.contains(selectedCryptoSplit[0])) {
                    getList.add(selectedCryptoSplit[0]);
                    tinyDB.putListString("pinned_coins", getList);

                    mPinImageView.setImageResource(R.drawable.ic_unpin);

                    String pinned = selectedCryptoSplit[0] + " " + context.getString(R.string.pinned);
                    Snackbar.make(MainActivity.activityMain, pinned, Snackbar.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < getList.size(); i++) {
                        if (getList.get(i).contains(selectedCryptoSplit[0])) {
                            getList.remove(i);
                            tinyDB.putListString("pinned_coins", getList);

                            mPinImageView.setImageResource(R.drawable.ic_pinned);

                            String unpin = selectedCryptoSplit[0] + " " + context.getString(R.string.unpin);

                            if (MainActivity.currentView.contains("MainActivity")) {
                                Snackbar.make(MainActivity.activityMain, unpin, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(PinnedCoinsActivity.pinnedCoinsLayout, unpin, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        mIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cryptoWebsite.equals("noUrl")) {
                    openCryptoWebsite(context, cryptoWebsite);
                }
            }
        });
    }

    private void openCryptoWebsite(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent openWebsite = new Intent(Intent.ACTION_VIEW, uri);

        try {
            context.startActivity(openWebsite);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private String getDefaultCurrency() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("currency_list_preference", "USD");
    }

    public void bind(AboutCryptoAdapter aboutCryptoAdapter) {

        int maxLength;
        if (!getDefaultCurrency().equals("BTC")) {
            if (context.getString(R.string.price).length() == 5) {
                maxLength = context.getString(R.string.price).length() + 12;
            } else {
                maxLength = context.getString(R.string.price).length() + 11;
            }
        } else {
            maxLength = context.getString(R.string.price).length() + 14;
        }
        Log.d("CryptoMarketCap", String.valueOf(context.getString(R.string.price).length()));
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        mPriceTextView.setFilters(fArray);

        String marketCap = aboutCryptoAdapter.getMarketCap();
        String volume = aboutCryptoAdapter.getVolume();
        String circulating = aboutCryptoAdapter.getCirculatingSupply();

        String mcSplitDot[] = marketCap.split(Pattern.quote("."));
        String mc0 = mcSplitDot[0];

        String volumeSplitDot[] = volume.split(Pattern.quote("."));
        String volume0 = volumeSplitDot[0];

        String circulatingSplitDot[] = circulating.split(Pattern.quote("."));
        String circulating0 = circulatingSplitDot[0];

        mRankTextView.setText(aboutCryptoAdapter.getRank());
        mIconImageView.setImageDrawable(aboutCryptoAdapter.getIcon());
        mNameTextView.setText(aboutCryptoAdapter.getName());
        mPriceTextView.setText(aboutCryptoAdapter.getPrice());
        mMarketCap.setText(mc0);
        mCirculatingSupply.setText(circulating0);
        mVolume.setText(volume0);
        mPercentChange1h.setText(aboutCryptoAdapter.getPercentChange1h());
        mPercentChange24h.setText(aboutCryptoAdapter.getPercentChange24h());
        mPercentChange7d.setText(aboutCryptoAdapter.getPercentChange7d());

        if (!isNoPanicModeEnabled()) {
            String mPercentChange1hStr = aboutCryptoAdapter.getPercentChange1h();
            String[] mPercentChange1hSplit = mPercentChange1hStr.split(": ");
            if (mPercentChange1hStr.contains("-")) {
                mPercentChange1hStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_1h) + " :</font> <font color=#ff3333>" + mPercentChange1hSplit[1] + "</font>";
                mPercentChange1h.setText(Html.fromHtml(mPercentChange1hStr));
            } else if (!mPercentChange1hStr.equals("0.0%")) {
                mPercentChange1hStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_1h) + " :</font> <font color=#00ff55>+" + mPercentChange1hSplit[1] + "</font>";
                mPercentChange1h.setText(Html.fromHtml(mPercentChange1hStr));
            }

            String mPercentChange24hStr = aboutCryptoAdapter.getPercentChange24h();
            String[] mPercentChange24hSplit = mPercentChange24hStr.split(": ");
            if (mPercentChange24hStr.contains("-")) {
                mPercentChange24hStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_24h) + " :</font> <font color=#ff3333>" + mPercentChange24hSplit[1] + "</font>";
                mPercentChange24h.setText(Html.fromHtml(mPercentChange24hStr));
            } else if (!mPercentChange24hStr.equals("0.0%")) {
                mPercentChange24hStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_24h) + " :</font> <font color=#00ff55>+" + mPercentChange24hSplit[1] + "</font>";
                mPercentChange24h.setText(Html.fromHtml(mPercentChange24hStr));
            }

            String mPercentChange7dStr = aboutCryptoAdapter.getPercentChange7d();
            String[] mPercentChange7dSplit = mPercentChange7dStr.split(": ");
            if (mPercentChange7dStr.contains("-")) {
                mPercentChange7dStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_7d) + " :</font> <font color=#ff3333>" + mPercentChange7dSplit[1] + "</font>";
                mPercentChange7d.setText(Html.fromHtml(mPercentChange7dStr));
            } else if (!mPercentChange7dStr.equals("0.0%")) {
                mPercentChange7dStr = "<font color=#A9A9A9>" + context.getString(R.string.percent_change_7d) + " :</font> <font color=#00ff55>+" + mPercentChange7dSplit[1] + "</font>";
                mPercentChange7d.setText(Html.fromHtml(mPercentChange7dStr));
            }
        }

        String mCrypto = aboutCryptoAdapter.getName();
        if (mCrypto.contains("(BTC)")) {
            setIcon(R.drawable.bitcoin_icon);
            cryptoWebsite = "https://bitcoin.org/";
        } else if (mCrypto.contains("(ETH)")) {
            setIcon(R.drawable.eth_icon);
            cryptoWebsite = "https://www.ethereum.org/";
        } else if (mCrypto.contains("(XRP)")) {
            setIcon(R.drawable.ripple_icon);
            cryptoWebsite = "https://ripple.com/";
        } else if (mCrypto.contains("(LTC)")) {
            setIcon(R.drawable.litecoin_icon);
            cryptoWebsite = "https://litecoin.com/";
        } else if (mCrypto.contains("(ETC)")) {
            setIcon(R.drawable.etc_icon);
            cryptoWebsite = "https://ethereumclassic.github.io/";
        } else if (mCrypto.contains("(XEM)")) {
            setIcon(R.drawable.nem_icon);
            cryptoWebsite = "http://nem.io/";
        } else if (mCrypto.contains("(DASH)")) {
            setIcon(R.drawable.dash_icon);
            cryptoWebsite = "https://www.dash.org/";
        } else if (mCrypto.contains("(MIOTA)")) {
            setIcon(R.drawable.iota_icon);
            cryptoWebsite = "https://iota.org/";
        } else if (mCrypto.contains("(BTS)")) {
            setIcon(R.drawable.bitshares_icon);
            cryptoWebsite = "https://bitshares.org/";
        } else if (mCrypto.contains("(XMR)")) {
            setIcon(R.drawable.monero_icon);
            cryptoWebsite = "http://www.monero.cc/";
        } else if (mCrypto.contains("(STRAT)")) {
            setIcon(R.drawable.stratis_icon);
            cryptoWebsite = "http://stratisplatform.com/";
        } else if (mCrypto.contains("(ZEC)")) {
            setIcon(R.drawable.zcash_icon);
            cryptoWebsite = "https://z.cash/";
        } else if (mCrypto.contains("(SC)")) {
            setIcon(R.drawable.siacoin_icon);
            cryptoWebsite = "http://sia.tech/";
        } else if (mCrypto.contains("(WAVES)")) {
            setIcon(R.drawable.waves_icon);
            cryptoWebsite = "https://wavesplatform.com/";
        } else if (mCrypto.contains("(GNT)")) {
            setIcon(R.drawable.golem_icon);
            cryptoWebsite = "https://golem.network/";
        } else if (mCrypto.contains("(ICN)")) {
            setIcon(R.drawable.iconomi_icon);
            cryptoWebsite = "https://www.iconomi.net/";
        } else if (mCrypto.contains("(BCN)")) {
            setIcon(R.drawable.bytecoin_icon);
            cryptoWebsite = "https://bytecoin.org/";
        } else if (mCrypto.contains("(ANS)")) {
            setIcon(R.drawable.antshares_icon);
            cryptoWebsite = "https://neo.org/";
        } else if (mCrypto.contains("(STEEM)")) {
            setIcon(R.drawable.steem_icon);
            cryptoWebsite = "https://steem.io/";
        } else if (mCrypto.contains("(XLM)")) {
            setIcon(R.drawable.stellar_icon);
            cryptoWebsite = "https://www.stellar.org/";
        } else if (mCrypto.contains("(BCC)")) {
            setIcon(R.drawable.bitconnect_icon);
            cryptoWebsite = "https://bitconnectcoin.co/";
        } else if (mCrypto.contains("(DOGE)")) {
            setIcon(R.drawable.dogecoin_icon);
            cryptoWebsite = "http://dogecoin.com/";
        } else if (mCrypto.contains("(LSK)")) {
            setIcon(R.drawable.lisk_icon);
            cryptoWebsite = "https://lisk.io/";
        } else if (mCrypto.contains("(REP)")) {
            setIcon(R.drawable.augur_icon);
            cryptoWebsite = "http://www.augur.net/";
        } else if (mCrypto.contains("(GAME)")) {
            setIcon(R.drawable.gamecredits_icon);
            cryptoWebsite = "http://gamecredits.com/";
        } else if (mCrypto.contains("(ARDR)")) {
            setIcon(R.drawable.ardor_icon);
            cryptoWebsite = "https://www.ardorplatform.org/";
        } else if (mCrypto.contains("(FCT)")) {
            setIcon(R.drawable.factom_icon);
            cryptoWebsite = "http://factom.org/";
        } else if (mCrypto.contains("(DGB)")) {
            setIcon(R.drawable.digibyte_icon);
            cryptoWebsite = "http://www.digibyte.co/";
        } else if (mCrypto.contains("(GNO)")) {
            setIcon(R.drawable.gnosis_icon);
            cryptoWebsite = "https://gnosis.pm/";
        } else if (mCrypto.contains("(MAID)")) {
            setIcon(R.drawable.maidsafecoin_icon);
            cryptoWebsite = "http://maidsafe.net/";
        } else if (mCrypto.contains("(DCR)")) {
            setIcon(R.drawable.decred_icon);
            cryptoWebsite = "https://www.decred.org/";
        } else if (mCrypto.contains("(VERI)")) {
            setIcon(R.drawable.veritaseum_icon);
            cryptoWebsite = "http://veritas.veritaseum.com/";
        } else if (mCrypto.contains("(KMD)")) {
            setIcon(R.drawable.komodo_icon);
            cryptoWebsite = "https://komodoplatform.com/";
        } else if (mCrypto.contains("(BAT)")) {
            setIcon(R.drawable.basicattentiontoken_icon);
            cryptoWebsite = "https://basicattentiontoken.org/";
        } else if (mCrypto.contains("(GBYTE)")) {
            setIcon(R.drawable.byteball_icon);
            cryptoWebsite = "https://byteball.org/";
        } else if (mCrypto.contains("(DGD)")) {
            setIcon(R.drawable.digixdao_icon);
            cryptoWebsite = "https://digix.io/";
        } else if (mCrypto.contains("(1ST)")) {
            setIcon(R.drawable.firstblood_icon);
            cryptoWebsite = "https://firstblood.io/";
        } else if (mCrypto.contains("(NXT)")) {
            setIcon(R.drawable.nxt_icon);
            cryptoWebsite = "https://nxt.org/";
        } else if (mCrypto.contains("(USDT)")) {
            setIcon(R.drawable.tether_icon);
            cryptoWebsite = "https://tether.to/";
        } else if (mCrypto.contains("(MGO)")) {
            setIcon(R.drawable.mobilego_icon);
            cryptoWebsite = "https://mobilego.io/";
        } else if (mCrypto.contains("(SNGLS)")) {
            setIcon(R.drawable.singulardtv_icon);
            cryptoWebsite = "https://singulardtv.com/";
        } else if (mCrypto.contains("(SYS)")) {
            setIcon(R.drawable.syscoin_icon);
            cryptoWebsite = "http://syscoin.org/";
        } else if (mCrypto.contains("(BTCD)")) {
            setIcon(R.drawable.bitcoindark_icon);
            cryptoWebsite = "http://bitcoindark.com/";
        } else if (mCrypto.contains("(ANT)")) {
            setIcon(R.drawable.aragon_icon);
            cryptoWebsite = "https://aragon.one/";
        } else if (mCrypto.contains("(PIVX)")) {
            setIcon(R.drawable.pivx_icon);
            cryptoWebsite = "http://www.pivx.org/";
        } else if (mCrypto.contains("(ROUND)")) {
            setIcon(R.drawable.round_icon);
            cryptoWebsite = "http://roundcoin.org/";
        } else if (mCrypto.contains("(EMC)")) {
            setIcon(R.drawable.emercoin_icon);
            cryptoWebsite = "http://emercoin.com/";
        } else if (mCrypto.contains("(UBQ)")) {
            setIcon(R.drawable.ubiq_icon);
            cryptoWebsite = "http://ubiqsmart.com/";
        } else if (mCrypto.contains("(LKK)")) {
            setIcon(R.drawable.lykke_icon);
            cryptoWebsite = "https://lykke.com/";
        } else if (mCrypto.contains("(LBC)")) {
            setIcon(R.drawable.lbry_icon);
            cryptoWebsite = "https://lbry.io/";
        } else if (mCrypto.contains("(MCAP)")) {
            setIcon(R.drawable.mcap_icon);
            cryptoWebsite = "https://bitcoingrowthfund.com/mcap";
        } else if (mCrypto.contains("(ARK)")) {
            setIcon(R.drawable.ark_icon);
            cryptoWebsite = "http://ark.io/";
        } else if (mCrypto.contains("(PPY)")) {
            setIcon(R.drawable.peerplays_icon);
            cryptoWebsite = "http://www.peerplays.com/";
        } else if (mCrypto.contains("(XAS)")) {
            setIcon(R.drawable.asch_icon);
            cryptoWebsite = "https://www.asch.so/";
        } else if (mCrypto.contains("(PPC)")) {
            setIcon(R.drawable.peercoin_icon);
            cryptoWebsite = "http://www.peercoin.net/";
        } else if (mCrypto.contains("(RLC)")) {
            setIcon(R.drawable.iexec_icon);
            cryptoWebsite = "http://iex.ec/";
        } else if (mCrypto.contains("(QRL)")) {
            setIcon(R.drawable.quantum_icon);
            cryptoWebsite = "https://theqrl.org/";
        } else if (mCrypto.contains("(AMP)")) {
            setIcon(R.drawable.synereo_icon);
            cryptoWebsite = "http://www.synereo.com/";
        } else if (mCrypto.contains("(SJCX)")) {
            setIcon(R.drawable.storjcoin_icon);
            cryptoWebsite = "http://storj.io/";
        } else if (mCrypto.contains("(NMR)")) {
            setIcon(R.drawable.numeraire_icon);
            cryptoWebsite = "https://numer.ai/";
        } else if (mCrypto.contains("(MLN)")) {
            setIcon(R.drawable.melon_icon);
            cryptoWebsite = "https://melonport.com/";
        } else if (mCrypto.contains("(RDD)")) {
            setIcon(R.drawable.reddcoin_icon);
            cryptoWebsite = "http://www.reddcoin.com/";
        } else if (mCrypto.contains("(WINGS)")) {
            setIcon(R.drawable.wings_icon);
            cryptoWebsite = "https://wings.ai/";
        } else if (mCrypto.contains("(NMC)")) {
            setIcon(R.drawable.namecoin_icon);
            cryptoWebsite = "https://www.namecoin.org/";
        } else if (mCrypto.contains("(BAY)")) {
            setIcon(R.drawable.bitbay_icon);
            cryptoWebsite = "http://bitbay.market/";
        } else if (mCrypto.contains("(XCP)")) {
            setIcon(R.drawable.counterparty_icon);
            cryptoWebsite = "http://counterparty.io/";
        } else if (mCrypto.contains("(NXS)")) {
            setIcon(R.drawable.nexus_icon);
            cryptoWebsite = "http://www.nexusearth.com/";
        } else if (mCrypto.contains("(LEO)")) {
            setIcon(R.drawable.leocoin_icon);
            cryptoWebsite = "http://www.leocoin.org/";
        } else if (mCrypto.contains("(MYST)")) {
            setIcon(R.drawable.mysterium_icon);
            cryptoWebsite = "https://mysterium.network/";
        } else if (mCrypto.contains("(XVG)")) {
            setIcon(R.drawable.verge_icon);
            cryptoWebsite = "http://vergecurrency.com/";
        } else if (mCrypto.contains("(CLOAK)")) {
            setIcon(R.drawable.cloakcoin_icon);
            cryptoWebsite = "https://www.cloakcoin.com/";
        } else if (mCrypto.contains("(OMNI)")) {
            setIcon(R.drawable.omni_icon);
            cryptoWebsite = "http://www.omnilayer.org/";
        } else if (mCrypto.contains("(XEL)")) {
            setIcon(R.drawable.elastic_icon);
            cryptoWebsite = "https://www.elastic.pw/";
        } else if (mCrypto.contains("(BLK)")) {
            setIcon(R.drawable.blackcoin_icon);
            cryptoWebsite = "http://www.blackcoin.co/";
        } else if (mCrypto.contains("(EDG)")) {
            setIcon(R.drawable.edgeless_icon);
            cryptoWebsite = "https://edgeless.io/";
        } else if (mCrypto.contains("(NLG)")) {
            setIcon(R.drawable.gulden_icon);
            cryptoWebsite = "https://gulden.com/";
        } else if (mCrypto.contains("(XAUR)")) {
            setIcon(R.drawable.xaurum_icon);
            cryptoWebsite = "http://www.xaurum.org/";
        } else if (mCrypto.contains("(MONA)")) {
            setIcon(R.drawable.monacoin_icon);
            cryptoWebsite = "http://monacoin.org/";
        } else if (mCrypto.contains("(XZC)")) {
            setIcon(R.drawable.zcoin_icon);
            cryptoWebsite = "https://zcoin.io/";
        } else if (mCrypto.contains("(VTC)")) {
            setIcon(R.drawable.vertcoin_icon);
            cryptoWebsite = "http://vertcoin.org/";
        } else if (mCrypto.contains("(EOS)")) {
            setIcon(R.drawable.eos_icon);
            cryptoWebsite = "https://eos.io/";
        } else if (mCrypto.contains("(SNT)")) {
            setIcon(R.drawable.status_icon);
            cryptoWebsite = "http://status.im/";
        } else if (mCrypto.contains("(BNT)")) {
            setIcon(R.drawable.bancor_icon);
            cryptoWebsite = "https://bancor.network/";
        } else if (mCrypto.contains("(PAY)")) {
            setIcon(R.drawable.tenx_icon);
            cryptoWebsite = "https://www.tenx.tech/";
        } else if (mCrypto.contains("(DCT)")) {
            setIcon(R.drawable.decent_icon);
            cryptoWebsite = "https://decent.ch/";
        } else if (mCrypto.contains("(MTL)")) {
            setIcon(R.drawable.metal_icon);
            cryptoWebsite = "https://www.metalpay.com/";
        } else if (mCrypto.contains("(FUN)")) {
            setIcon(R.drawable.funfair_icon);
            cryptoWebsite = "https://funfair.io/";
        } else if (mCrypto.contains("(SOAR)")) {
            setIcon(R.drawable.soarcoin_icon);
            cryptoWebsite = "http://soarlabs.org/";
        } else if (mCrypto.contains("(DICE)")) {
            setIcon(R.drawable.etheroll_icon);
            cryptoWebsite = "https://etheroll.com/";
        } else if (mCrypto.contains("(DBIX)")) {
            setIcon(R.drawable.dubaicoin_icon);
            cryptoWebsite = "http://www.arabianchain.org/";
        } else if (mCrypto.contains("(EDR)")) {
            setIcon(R.drawable.edinar_icon);
            cryptoWebsite = "https://edinarcoin.com/";
        } else if (mCrypto.contains("(YBC)")) {
            setIcon(R.drawable.ybcoin_icon);
            cryptoWebsite = "http://www.ybcoin.com/";
        } else if (mCrypto.contains("(SKY)")) {
            setIcon(R.drawable.skycoin_icon);
            cryptoWebsite = "http://skycoin.net/";
        } else if (mCrypto.contains("(QAU)")) {
            setIcon(R.drawable.quantum_icon);
            cryptoWebsite = "http://www.quantumproject.org/";
        } else if (mCrypto.contains("(HMQ)")) {
            setIcon(R.drawable.humaniq_icon);
            cryptoWebsite = "https://humaniq.co/";
        } else if (mCrypto.contains("(STORJ)")) {
            setIcon(R.drawable.storj_icon);
            cryptoWebsite = "https://storj.io/";
        } else if (mCrypto.contains("(ADT)")) {
            setIcon(R.drawable.adtoken_icon);
            cryptoWebsite = "https://adtoken.com/";
        } else if (mCrypto.contains("(POT)")) {
            setIcon(R.drawable.potcoin_icon);
            cryptoWebsite = "http://www.potcoin.com/";
        } else if (mCrypto.contains("(BLOCK)")) {
            setIcon(R.drawable.blocknet_icon);
            cryptoWebsite = "http://blocknet.co/";
        } else if (mCrypto.contains("(VSL)")) {
            setIcon(R.drawable.vslice_icon);
            cryptoWebsite = "http://www.vslice.io/";
        } else if (mCrypto.contains("(OBITS)")) {
            setIcon(R.drawable.obits_icon);
            cryptoWebsite = "http://www.obits.io/";
        } else if (mCrypto.contains("(SIB)")) {
            setIcon(R.drawable.sibcoin_icon);
            cryptoWebsite = "http://sibcoin.org/";
        } else if (mCrypto.contains("(SLS)")) {
            setIcon(R.drawable.salus_icon);
            cryptoWebsite = "http://saluscoin.info/";
        } else if (mCrypto.contains("(SWT)")) {
            setIcon(R.drawable.swarmcity_icon);
            cryptoWebsite = "http://swarm.city/";
        } else if (mCrypto.contains("(UNY)")) {
            setIcon(R.drawable.unityingot_icon);
            cryptoWebsite = "https://unityingot.com/";
        } else if (mCrypto.contains("(IOC)")) {
            setIcon(R.drawable.iocoin_icon);
            cryptoWebsite = "http://iocoin.io/";
        } else if (mCrypto.contains("(BURST)")) {
            setIcon(R.drawable.burst_icon);
            cryptoWebsite = "http://www.burst-team.us/";
        } else if (mCrypto.contains("(NVC)")) {
            setIcon(R.drawable.novacoin_icon);
            cryptoWebsite = "http://novacoin.org/";
        } else if (mCrypto.contains("(TKN)")) {
            setIcon(R.drawable.tokencard_icon);
            cryptoWebsite = "http://tokencard.io/";
        } else if (mCrypto.contains("(VIA)")) {
            setIcon(R.drawable.viacoin_icon);
            cryptoWebsite = "http://viacoin.org/";
        } else if (mCrypto.contains("(QTUM)")) {
            setIcon(R.drawable.qtum_icon);
            cryptoWebsite = "https://qtum.org/";
        } else if (mCrypto.contains("(CVC)")) {
            setIcon(R.drawable.civic_icon);
        } else if (mCrypto.contains("(BDL)")) {
            setIcon(R.drawable.bitdeal_icon);
            cryptoWebsite = "https://bitdeal.co.in/";
        } else if (mCrypto.contains("(PPT)")) {
            setIcon(R.drawable.populous_icon);
            cryptoWebsite = "http://populous.co/";
        } else if (mCrypto.contains("(OMG)")) {
            setIcon(R.drawable.omisego_icon);
            cryptoWebsite = "https://omg.omise.co/";
        } else if (mCrypto.contains("(PART)")) {
            setIcon(R.drawable.particl_icon);
            cryptoWebsite = "http://particl.io/";
        } else if (mCrypto.contains("(PLR)")) {
            setIcon(R.drawable.pillar_icon);
            cryptoWebsite = "https://pillarproject.io/";
        } else if (mCrypto.contains("(XRL)")) {
            setIcon(R.drawable.rialto_icon);
            cryptoWebsite = "https://www.rialto.ai/";
        } else if (mCrypto.contains("(ION)")) {
            setIcon(R.drawable.ion_icon);
            cryptoWebsite = "https://ionomy.com/";
        } else if (mCrypto.contains("(GOLOS)")) {
            setIcon(R.drawable.golos_icon);
            cryptoWebsite = "https://golos.io/";
        } else if (mCrypto.contains("(TAAS)")) {
            setIcon(R.drawable.taas_icon);
            cryptoWebsite = "https://taas.fund/";
        } else {
            mIconImageView.setImageDrawable(null);
        }
    }

    private void setIcon(int drawable) {
        mIconImageView.setImageResource(drawable);
    }

    private boolean isNoPanicModeEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("no_panic_switch", false);
    }
}
