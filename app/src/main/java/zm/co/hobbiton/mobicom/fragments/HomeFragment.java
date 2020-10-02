package zm.co.hobbiton.mobicom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.activities.CashPaymentsActivity;
import zm.co.hobbiton.mobicom.activities.TvPaymentsActivity;
import zm.co.hobbiton.mobicom.activities.AirtimeActivity;
import zm.co.hobbiton.mobicom.activities.BillPaymentsActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private CardView mAirtimeCard, mBillPaymentsCard, mCashPaymentsCard, mDFSCard, mMomoAgencyCard, mAgencyBankingCard, mPayTvCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        mAirtimeCard = view.findViewById(R.id.cardAirtime);
        mAirtimeCard.setOnClickListener(this);
        mBillPaymentsCard = view.findViewById(R.id.cardBillPayments);
        mBillPaymentsCard.setOnClickListener(this);
        mCashPaymentsCard = view.findViewById(R.id.cardCashPayments);
        mCashPaymentsCard.setOnClickListener(this);
        mDFSCard = view.findViewById(R.id.cardDFS);
        mDFSCard.setOnClickListener(this);
        mMomoAgencyCard = view.findViewById(R.id.cardMomoAgency);
        mMomoAgencyCard.setOnClickListener(this);
        mAgencyBankingCard = view.findViewById(R.id.cardAgencyBanking);
        mAgencyBankingCard.setOnClickListener(this);
        mPayTvCard = view.findViewById(R.id.cardPayTv);
        mPayTvCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.cardAirtime:
                intent = new Intent(getContext(), AirtimeActivity.class);
                startActivity(intent);
                break;
            case R.id.cardBillPayments:
                intent = new Intent(getContext(), BillPaymentsActivity.class);
                startActivity(intent);
                break;
            case R.id.cardPayTv:
                intent = new Intent(getContext(), TvPaymentsActivity.class);
                startActivity(intent);
                break;
            case R.id.cardCashPayments:
                intent = new Intent(getContext(), CashPaymentsActivity.class);
                startActivity(intent);
                break;
            case R.id.cardDFS:
                Toast.makeText(getContext(), "DFS", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cardMomoAgency:
                Toast.makeText(getContext(), "MoMo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cardAgencyBanking:
                Toast.makeText(getContext(), "Agency", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}