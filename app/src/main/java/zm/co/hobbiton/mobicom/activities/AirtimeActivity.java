package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AirtimeActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String EXTRA_ID = "service_id";
    public static final String EXTRA_LOGO = "logo";
    CardView mAirtelCard, mMTNCard, mZamtelCard, mLiquidCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);
        MessageReceiver.bindListener(this);
        initializeViews();
    }

    private void initializeViews() {
        mAirtelCard = findViewById(R.id.cardAirtel);
        mAirtelCard.setOnClickListener(this);
        mMTNCard = findViewById(R.id.cardMTN);
        mMTNCard.setOnClickListener(this);
        mZamtelCard = findViewById(R.id.cardZamtel);
        mZamtelCard.setOnClickListener(this);
        mLiquidCard = findViewById(R.id.cardLiquid);
        mLiquidCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, BuyAirtimeActivity.class);
        switch (view.getId()) {
            case R.id.cardMTN:
                intent.putExtra(EXTRA_ID, 2);
                intent.putExtra(EXTRA_LOGO, R.drawable.mtn);
                break;
            case R.id.cardZamtel:
                intent.putExtra(EXTRA_ID, 3);
                intent.putExtra(EXTRA_LOGO, R.drawable.zamtel);
                break;
            case R.id.cardAirtel:
                intent.putExtra(EXTRA_ID, 1);
                intent.putExtra(EXTRA_LOGO, R.drawable.airtel);
                break;
            case R.id.cardLiquid:
                intent.putExtra("serviceID", 0);
                intent.putExtra("logo", R.drawable.liquid);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void messageReceived(String message) {

    }
}