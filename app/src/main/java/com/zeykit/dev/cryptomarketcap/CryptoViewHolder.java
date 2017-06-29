package com.zeykit.dev.cryptomarketcap;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CryptoViewHolder extends RecyclerView.ViewHolder {

    private TextView mRankTextView;
    private ImageView mIconImageView;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mPercentChangeTextView;

    public CryptoViewHolder(View v) {
        super(v);

        mRankTextView = (TextView) v.findViewById(R.id.rankTextView);
        mIconImageView = (ImageView) v.findViewById(R.id.iconImageView);
        mNameTextView = (TextView) v.findViewById(R.id.nameTextView);
        mPriceTextView = (TextView) v.findViewById(R.id.priceTextView);
        mPercentChangeTextView = (TextView) v.findViewById(R.id.percentChangeTextView);
    }

    public void bind(CryptoAdapter cryptoAdapter) {

        mRankTextView.setText(cryptoAdapter.getRank());
        mIconImageView.setImageDrawable(cryptoAdapter.getIcon());
        mNameTextView.setText(cryptoAdapter.getName());
        mPriceTextView.setText(cryptoAdapter.getPrice());
        mPercentChangeTextView.setText(cryptoAdapter.getPercentChange());

        String mPercentChange = cryptoAdapter.getPercentChange();
        if (mPercentChange.contains("-")) {
            mPercentChangeTextView.setTextColor(Color.parseColor("#ff3333"));
        } else if (mPercentChange.equals("0.0%")) {
            mPercentChangeTextView.setTextColor(Color.parseColor("#ffffff"));
        } else {
            String percentChange = "+" + cryptoAdapter.getPercentChange();
            mPercentChangeTextView.setText(percentChange);
            mPercentChangeTextView.setTextColor(Color.parseColor("#00ff55"));
        }

        String mCrypto = cryptoAdapter.getName();
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
        } else {
            mIconImageView.setImageDrawable(null);
        }
    }

        /*mPriceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), adapter.getPrice(), Toast.LENGTH_SHORT).show();
            }
        });*/

    /**
     * Change crypto's icon
     * @param drawable icon
     */
    private void setIcon(int drawable) {
        mIconImageView.setImageResource(drawable);
    }
}
