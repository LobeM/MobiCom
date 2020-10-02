package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nbbse.mobiprint3.Printer;

public class CashPaymentsActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String EXTRA_ID = "service_id";
    public static final String EXTRA_LOGO = "logo";
    private CardView mAirtelMoneyCard, mMTNMomoCard, mZamtelKwachaCard;
    private Printer mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payments);
        MessageReceiver.bindListener(this);
        initializeViews();
        mPrinter = Printer.getInstance();
    }



    private void initializeViews() {
        mAirtelMoneyCard = findViewById(R.id.cardAirtelMoney);
        mAirtelMoneyCard.setOnClickListener(this);
        mMTNMomoCard = findViewById(R.id.cardMTNMomo);
        mMTNMomoCard.setOnClickListener(this);
        mZamtelKwachaCard = findViewById(R.id.cardZamtelKwacha);
        mZamtelKwachaCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PayCashActivity.class);
        switch (view.getId()) {
            case R.id.cardMTNMomo:
                intent.putExtra(EXTRA_ID, 51);
                intent.putExtra(EXTRA_LOGO, R.drawable.mtn_momo);
                break;
            case R.id.cardZamtelKwacha:
                intent.putExtra(EXTRA_ID, 52);
                intent.putExtra(EXTRA_LOGO, R.drawable.zamtel_kwacha);
                break;
            case R.id.cardAirtelMoney:
                intent.putExtra(EXTRA_ID, 50);
                intent.putExtra(EXTRA_LOGO, R.drawable.airtel_money);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void messageReceived(final String message) {
        new AlertDialog.Builder(this)
                .setTitle("Cash Payment")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Print receipt
                        mPrinter.printText("********************************");
                        mPrinter.printText("******** Hobbiton Tech *********");
                        mPrinter.printText("********************************");
                        mPrinter.printText("Cash Payment",2);
                        mPrinter.printText(message);
                        mPrinter.printEndLine();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setIcon(R.drawable.ic_monetization_white)
                .show();
    }
}