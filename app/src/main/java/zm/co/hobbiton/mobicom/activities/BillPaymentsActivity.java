package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BillPaymentsActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String EXTRA_ID = "service_id";
    public static final String EXTRA_LOGO = "logo";
    CardView mZescoCard, mWaterCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payments);
        MessageReceiver.bindListener(this);
        initializeViews();
    }

    private void initializeViews() {
        mZescoCard = findViewById(R.id.cardZesco);
        mZescoCard.setOnClickListener(this);
        mWaterCard = findViewById(R.id.cardWater);
        mWaterCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PayBillActivity.class);
        switch (view.getId()) {
            case R.id.cardZesco:
                intent.putExtra(EXTRA_LOGO, R.drawable.electricity);
                intent.putExtra(EXTRA_ID, 4);
                break;
            case R.id.cardWater:
                intent.putExtra(EXTRA_LOGO, R.drawable.lusaka_water);
                intent.putExtra(EXTRA_ID, 8);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void messageReceived(String message) {

    }
}