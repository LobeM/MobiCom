package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.activities.PayTvActivity;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TvPaymentsActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String EXTRA_ID = "service_id";
    public static final String EXTRA_LOGO = "logo";
    CardView mDstvCard, mGotvCard, mTopstarCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_payments);
        MessageReceiver.bindListener(this);
        initializeViews();
    }

    private void initializeViews() {
        mDstvCard = findViewById(R.id.cardDstv);
        mDstvCard.setOnClickListener(this);
        mGotvCard = findViewById(R.id.cardGotv);
        mGotvCard.setOnClickListener(this);
        mTopstarCard = findViewById(R.id.cardTopStar);
        mTopstarCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PayTvActivity.class);
        switch (view.getId()) {
            case R.id.cardDstv:
                intent.putExtra(EXTRA_ID, 5);
                intent.putExtra(EXTRA_LOGO, R.drawable.dstv);
                break;
            case R.id.cardGotv:
                intent.putExtra(EXTRA_ID, 6);
                intent.putExtra(EXTRA_LOGO, R.drawable.gotv);
                break;
            case R.id.cardTopStar:
                intent.putExtra(EXTRA_ID, 7);
                intent.putExtra(EXTRA_LOGO, R.drawable.topstar);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void messageReceived(String message) {

    }
}