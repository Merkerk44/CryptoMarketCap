package com.zeykit.dev.cryptomarketcap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    }



    public void bind(AboutCryptoAdapter aboutCryptoAdapter) {

        mRankTextView.setText(aboutCryptoAdapter.getRank());
        mIconImageView.setImageDrawable(aboutCryptoAdapter.getIcon());
        mNameTextView.setText(aboutCryptoAdapter.getName());
        mPriceTextView.setText(aboutCryptoAdapter.getPrice());
        mMarketCap.setText(aboutCryptoAdapter.getMarketCap());
        mCirculatingSupply.setText(aboutCryptoAdapter.getCirculatingSupply());
        mVolume.setText(aboutCryptoAdapter.getVolume());
        mPercentChange1h.setText(aboutCryptoAdapter.getPercentChange1h());
        mPercentChange24h.setText(aboutCryptoAdapter.getPercentChange24h());
        mPercentChange7d.setText(aboutCryptoAdapter.getPercentChange7d());

        if (!isNoPanicModeEnable()) {
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
        } else if (mCrypto.contains("(ETH)")) {
            setIcon(R.drawable.eth_icon);
        } else if (mCrypto.contains("(XRP)")) {
            setIcon(R.drawable.ripple_icon);
        } else if (mCrypto.contains("(LTC)")) {
            setIcon(R.drawable.litecoin_icon);
        } else if (mCrypto.contains("(ETC)")) {
            setIcon(R.drawable.etc_icon);
        } else if (mCrypto.contains("(XEM)")) {
            setIcon(R.drawable.nem_icon);
        } else if (mCrypto.contains("(DASH)")) {
            setIcon(R.drawable.dash_icon);
        } else if (mCrypto.contains("(MIOTA)")) {
            setIcon(R.drawable.iota_icon);
        } else if (mCrypto.contains("(BTS)")) {
            setIcon(R.drawable.bitshares_icon);
        } else if (mCrypto.contains("(XMR)")) {
            setIcon(R.drawable.monero_icon);
        } else if (mCrypto.contains("(STRAT)")) {
            setIcon(R.drawable.stratis_icon);
        } else if (mCrypto.contains("(ZEC)")) {
            setIcon(R.drawable.zcash_icon);
        } else if (mCrypto.contains("(SC)")) {
            setIcon(R.drawable.siacoin_icon);
        } else if (mCrypto.contains("(WAVES)")) {
            setIcon(R.drawable.waves_icon);
        } else if (mCrypto.contains("(GNT)")) {
            setIcon(R.drawable.golem_icon);
        } else if (mCrypto.contains("(ICN)")) {
            setIcon(R.drawable.iconomi_icon);
        } else if (mCrypto.contains("(BCN)")) {
            setIcon(R.drawable.bytecoin_icon);
        } else if (mCrypto.contains("(ANS)")) {
            setIcon(R.drawable.antshares_icon);
        } else if (mCrypto.contains("(STEEM)")) {
            setIcon(R.drawable.steem_icon);
        } else if (mCrypto.contains("(XLM)")) {
            setIcon(R.drawable.stellar_icon);
        } else if (mCrypto.contains("(BCC)")) {
            setIcon(R.drawable.bitconnect_icon);
        } else if (mCrypto.contains("(DOGE)")) {
            setIcon(R.drawable.dogecoin_icon);
        } else if (mCrypto.contains("(LSK)")) {
            setIcon(R.drawable.lisk_icon);
        } else if (mCrypto.contains("(REP)")) {
            setIcon(R.drawable.augur_icon);
        } else if (mCrypto.contains("(GAME)")) {
            setIcon(R.drawable.gamecredits_icon);
        } else if (mCrypto.contains("(ARDR)")) {
            setIcon(R.drawable.ardor_icon);
        } else if (mCrypto.contains("(FCT)")) {
            setIcon(R.drawable.factom_icon);
        } else if (mCrypto.contains("(DGB)")) {
            setIcon(R.drawable.digibyte_icon);
        } else if (mCrypto.contains("(GNO)")) {
            setIcon(R.drawable.gnosis_icon);
        } else if (mCrypto.contains("(MAID)")) {
            setIcon(R.drawable.maidsafecoin_icon);
        } else if (mCrypto.contains("(DCR)")) {
            setIcon(R.drawable.decred_icon);
        } else if (mCrypto.contains("(VERI)")) {
            setIcon(R.drawable.veritaseum_icon);
        } else if (mCrypto.contains("(KMD)")) {
            setIcon(R.drawable.komodo_icon);
        } else if (mCrypto.contains("(BAT)")) {
            setIcon(R.drawable.basicattentiontoken_icon);
        } else if (mCrypto.contains("(GBYTE)")) {
            setIcon(R.drawable.byteball_icon);
        } else if (mCrypto.contains("(DGD)")) {
            setIcon(R.drawable.digixdao_icon);
        } else if (mCrypto.contains("(1ST)")) {
            setIcon(R.drawable.firstblood_icon);
        } else if (mCrypto.contains("(NXT)")) {
            setIcon(R.drawable.nxt_icon);
        } else if (mCrypto.contains("(USDT)")) {
            setIcon(R.drawable.tether_icon);
        } else if (mCrypto.contains("(MGO)")) {
            setIcon(R.drawable.mobilego_icon);
        } else if (mCrypto.contains("(SNGLS)")) {
            setIcon(R.drawable.singulardtv_icon);
        } else if (mCrypto.contains("(SYS)")) {
            setIcon(R.drawable.syscoin_icon);
        } else if (mCrypto.contains("(BTCD)")) {
            setIcon(R.drawable.bitcoindark_icon);
        } else if (mCrypto.contains("(ANT)")) {
            setIcon(R.drawable.aragon_icon);
        } else if (mCrypto.contains("(PIVX)")) {
            setIcon(R.drawable.pivx_icon);
        } else if (mCrypto.contains("(ROUND)")) {
            setIcon(R.drawable.round_icon);
        } else if (mCrypto.contains("(EMC)")) {
            setIcon(R.drawable.emercoin_icon);
        } else if (mCrypto.contains("(UBQ)")) {
            setIcon(R.drawable.ubiq_icon);
        } else if (mCrypto.contains("(LKK)")) {
            setIcon(R.drawable.lykke_icon);
        } else if (mCrypto.contains("(LBC)")) {
            setIcon(R.drawable.lbry_icon);
        } else if (mCrypto.contains("(MCAP)")) {
            setIcon(R.drawable.mcap_icon);
        } else if (mCrypto.contains("(ARK)")) {
            setIcon(R.drawable.ark_icon);
        } else if (mCrypto.contains("(PPY)")) {
            setIcon(R.drawable.peerplays_icon);
        } else if (mCrypto.contains("(XAS)")) {
            setIcon(R.drawable.asch_icon);
        } else if (mCrypto.contains("(PPC)")) {
            setIcon(R.drawable.peercoin_icon);
        } else if (mCrypto.contains("(RLC)")) {
            setIcon(R.drawable.iexec_icon);
        } else if (mCrypto.contains("(QRL)")) {
            setIcon(R.drawable.quantum_icon);
        } else if (mCrypto.contains("(AMP)")) {
            setIcon(R.drawable.synereo_icon);
        } else if (mCrypto.contains("(SJCX)")) {
            setIcon(R.drawable.storjcoin_icon);
        } else if (mCrypto.contains("(NMR)")) {
            setIcon(R.drawable.numeraire_icon);
        } else if (mCrypto.contains("(MLN)")) {
            setIcon(R.drawable.melon_icon);
        } else if (mCrypto.contains("(RDD)")) {
            setIcon(R.drawable.reddcoin_icon);
        } else if (mCrypto.contains("(WINGS)")) {
            setIcon(R.drawable.wings_icon);
        } else if (mCrypto.contains("(NMC)")) {
            setIcon(R.drawable.namecoin_icon);
        } else if (mCrypto.contains("(BAY)")) {
            setIcon(R.drawable.bitbay_icon);
        } else if (mCrypto.contains("(XCP)")) {
            setIcon(R.drawable.counterparty_icon);
        } else if (mCrypto.contains("(NXS)")) {
            setIcon(R.drawable.nexus_icon);
        } else if (mCrypto.contains("(LEO)")) {
            setIcon(R.drawable.leocoin_icon);
        } else if (mCrypto.contains("(MYST)")) {
            setIcon(R.drawable.mysterium_icon);
        } else if (mCrypto.contains("(XVG)")) {
            setIcon(R.drawable.verge_icon);
        } else if (mCrypto.contains("(CLOAK)")) {
            setIcon(R.drawable.cloakcoin_icon);
        } else if (mCrypto.contains("(OMNI)")) {
            setIcon(R.drawable.omni_icon);
        } else if (mCrypto.contains("(XEL)")) {
            setIcon(R.drawable.elastic_icon);
        } else if (mCrypto.contains("(BLK)")) {
            setIcon(R.drawable.blackcoin_icon);
        } else if (mCrypto.contains("(EDG)")) {
            setIcon(R.drawable.edgeless_icon);
        } else if (mCrypto.contains("(NLG)")) {
            setIcon(R.drawable.gulden_icon);
        } else if (mCrypto.contains("(XAUR)")) {
            setIcon(R.drawable.xaurum_icon);
        } else if (mCrypto.contains("(MONA)")) {
            setIcon(R.drawable.monacoin_icon);
        } else if (mCrypto.contains("(XZC)")) {
            setIcon(R.drawable.zcoin_icon);
        } else if (mCrypto.contains("(VTC)")) {
            setIcon(R.drawable.vertcoin_icon);
        } else if (mCrypto.contains("(EOS)")) {
            setIcon(R.drawable.eos_icon);
        } else if (mCrypto.contains("(SNT)")) {
            setIcon(R.drawable.status_icon);
        } else if (mCrypto.contains("(BNT)")) {
            setIcon(R.drawable.bancor_icon);
        } else if (mCrypto.contains("(PAY)")) {
            setIcon(R.drawable.tenx_icon);
        } else if (mCrypto.contains("(DCT)")) {
            setIcon(R.drawable.decent_icon);
        } else if (mCrypto.contains("(MTL)")) {
            setIcon(R.drawable.metal_icon);
        } else if (mCrypto.contains("(FUN)")) {
            setIcon(R.drawable.funfair_icon);
        } else if (mCrypto.contains("(SOAR)")) {
            setIcon(R.drawable.soarcoin_icon);
        } else if (mCrypto.contains("(DICE)")) {
            setIcon(R.drawable.etheroll_icon);
        } else if (mCrypto.contains("(DBIX)")) {
            setIcon(R.drawable.dubaicoin_icon);
        } else if (mCrypto.contains("(EDR)")) {
            setIcon(R.drawable.edinar_icon);
        } else if (mCrypto.contains("(YBC)")) {
            setIcon(R.drawable.ybcoin_icon);
        } else if (mCrypto.contains("(SKY)")) {
            setIcon(R.drawable.skycoin_icon);
        } else if (mCrypto.contains("(QAU)")) {
            setIcon(R.drawable.quantum_icon);
        } else if (mCrypto.contains("(HMQ)")) {
            setIcon(R.drawable.humaniq_icon);
        } else if (mCrypto.contains("(STORJ)")) {
            setIcon(R.drawable.storj_icon);
        } else if (mCrypto.contains("(ADT)")) {
            setIcon(R.drawable.adtoken_icon);
        } else if (mCrypto.contains("(POT)")) {
            setIcon(R.drawable.potcoin_icon);
        } else if (mCrypto.contains("(BLOCK)")) {
            setIcon(R.drawable.blocknet_icon);
        } else if (mCrypto.contains("(VSL)")) {
            setIcon(R.drawable.vslice_icon);
        } else if (mCrypto.contains("(OBITS)")) {
            setIcon(R.drawable.obits_icon);
        } else if (mCrypto.contains("(SIB)")) {
            setIcon(R.drawable.sibcoin_icon);
        } else if (mCrypto.contains("(SLS)")) {
            setIcon(R.drawable.salus_icon);
        } else if (mCrypto.contains("(SWT)")) {
            setIcon(R.drawable.swarmcity_icon);
        } else if (mCrypto.contains("(UNY)")) {
            setIcon(R.drawable.unityingot_icon);
        } else if (mCrypto.contains("(IOC)")) {
            setIcon(R.drawable.iocoin_icon);
        } else if (mCrypto.contains("(BURST)")) {
            setIcon(R.drawable.burst_icon);
        } else if (mCrypto.contains("(NVC)")) {
            setIcon(R.drawable.novacoin_icon);
        } else if (mCrypto.contains("(TKN)")) {
            setIcon(R.drawable.tokencard_icon);
        } else if (mCrypto.contains("(VIA)")) {
            setIcon(R.drawable.viacoin_icon);
        } else {
            mIconImageView.setImageDrawable(null);
        }
    }

    private void setIcon(int drawable) {
        mIconImageView.setImageResource(drawable);
    }

    private boolean isNoPanicModeEnable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("no_panic_switch", false);
    }
}
